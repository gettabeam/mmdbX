/*
Usage:

DBBridge dbconnect = new DBBridge();
dbconnect.connectDB();
mq = new ora_mmquery(dbconnect);
mq.mm_init();
imgicon=mq.retrievePrevImg();
imgicon=mq.retrieveCurImg();cac
imgicon=mq.retrieveNextImg();
*/
/*

 When    What
----------------------------------------------
 010611  added new function addCagtgKey()
 010619  Added to package com.solar.mmquery
 010814  Replace CHAR with RAW
 020616  add updateCatgRelation()
 020620  add getCatgTreeDetails()
 020623
 020624
 030119  Add cache for decrypted catgcode and catgdesc
 030907  Change PopulateImgIdx ordering
 040818  Support for data cache
 040822  Refactor the code
 040908  Refactor the code
 041201
 041205
 041220  Restrict selection to either not encrypted or encrypted.
 041230
 050205  getCatgTreeImgIdx, return vector of Integer instead of String
 050826  Set read buffer to 2M
 050906  update String encoding charset
 050912  rewrite addCatgKey() function
 051115
 051117  Added addMMData(byte[]..) method
 051230  buf fix addMMdata
 060205  Replace BIG5 by UTF-8 Support
 070314  Cannot use setBinaryStream to insert BLOB directly !
 070315  Add benchmark information
 070529  Add Logger
*/
package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import java.math.BigDecimal;
import oracle.jdbc.driver.*;
import oracle.sql.*;
import java.util.*;
import javax.crypto.*;

import com.solar.utility.*;
import com.solar.imgproc.*;

public class ora_mmquery extends mm_query{

    static long _totalWriteTime=0;
    static long _totalWriteByte=0;
    static long _totalReadTime=0;
    static long _totalReadByte=0;
    

//-------------------------------------------------
//	MAIN
//-------------------------------------------------
  public static void main(String argv[]) {
    int mmkey,i=0;
    DBBridge db = new oraDBBridge();
    Properties login = new Properties();
    login.put("username","mmdb2");
    login.put("password","mmdbx2");
    login.put("host","160.19.22.132");
  //  login.put("host","vmdatasrv");

    
    login.put("port","1521");
    login.put("sid","orastart");
    db.setLoginParms(login);
    db.initConnection();
    ora_mmquery mq= new ora_mmquery(db);
    String _tree_id=DEFAULT_TREE;
    
 //   System.out.println("Root Size="+mq.getTree().roots.size());
 //   System.out.println("Tree Size="+mq.getTree().getAllChilds().size());
 //   System.out.println("Item size="+mq.getTree().getAllItems().size());
    
//    Vector v=mq.getCatgTreeDetails(460,_tree_id);
//    System.out.println("Tree size="+v.size());
    
    Vector v1=mq.getCatgTreeDetails(165,"123456");
 //   System.out.println("Tree size="+v1.size());
 //   System.out.println("getCatgTreeImgIdx>>"+mq.getCatgTreeImgIdx(1081,0).size());
    

    // test insert file
    /*
    if (argv.length==3) {
      mmkey=mq.importFile(argv[0],argv[1],argv[2]);
      mq.printImageInfo(mmkey);
    }
    else {
      mq.printDBInfo();
      String[] ss = {"MMITEM","ZETA","MM","SHINGETA"};
      Vector v = mq.populateImgIdx(ss);
      System.out.print("No of files in NEWIMAGES:");
      System.out.println(v.size());
    }
    String code[] = {"TXT"};
    String enccode[] = {"ENCTXT"};
    mmkey=mq.addMMData("d:\\pohac.txt","CG",code,enccode,"iameric");
    byte buf[] = mq.readMMData(mmkey,"iameric");
    try {
      FileOutputStream fos = new FileOutputStream("d:\\output");
      fos.write(buf);
      fos.flush();
      fos.close();
    } catch (Exception e) { e.printStackTrace(); }
    */
    
    System.exit(0);
  }
//-------------------------------------------------
//	Default Public Constructor
//-------------------------------------------------

  public ora_mmquery() {
  //  System.out.println("ora_mmquery:default constructor");
    this.db = null;
  }

  public ora_mmquery(DBBridge db) {
  //  System.out.println("ora_mmquery:constructor ora_mmquery(DBBridge)");
    this.db = db;
    if (!db.isConnected()) {
      MyLogger.logger.warn("No connection established !");
      return;
    }
    userInfo = db.getUserInfo();
    userInfo.setAccessLevel(1000);
    con = db.getConnection();
  }

//-------------------------------------------------
//  Dump Info
//-------------------------------------------------
  public void printImageInfo(int mmkey) {
    byte buf[]=null;
    if (mmkey>0) {
      buf=readMMData(mmkey);
      ImageIcon imgicon= new ImageIcon(buf);
      MyLogger.logger.info("Height="+imgicon.getIconHeight()+ " Width="+imgicon.getIconWidth()+" Size="+buf.length+" mmkey="+mmkey);
    }
  }
  public void printDBInfo() {
    String sql=null;
    MyLogger.logger.info("DATABASE:ORACLE");
    sql="select mm_key,mm_name,dbms_lob.getLength(mm_data) \"File size\",to_char(create_dte,'yyyy-mm-dd HH24:MM') \"date\" from mm_datastore";
    db.dumpQuery(sql);
    sql="select count(*) \"Count\",sum(dbms_lob.getLength(mm_data)) \"Total Size\" from mm_datastore";
    db.dumpQuery(sql);
  }
  
  private boolean checkPrivilege(int mmkey) {
	    String sql = "select count(*) from catg_grp a, catg_grpitem b where a.catg_key = b.catg_key and b.item_key = ? and a.access_lvl <= ?";
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    int count = 0;
	    try {
	      pstmt = con.prepareStatement(sql);
	      pstmt.setInt(1, mmkey);
	      pstmt.setInt(2, userInfo.getAccessLevel());
	      rs = pstmt.executeQuery();
	      while (rs.next()) {
	        count = rs.getInt(1);
	      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      return false;
	    }

	    if (count == 0) {
	      return false;
	    }
	    else {
	      return true;
	    }
	  }
//-------------------------------------------------
//	Add MM data
//-------------------------------------------------

  protected int getNextMMKey() {
    String sql="select seq_mmkey.nextval from dual";
    Vector v=db.execSQLRetVector(sql);
    if (v==null||v.size()==0) /* there is error */
      return -1;

    int mmkey=Integer.parseInt((String)v.get(0));
    return mmkey;
  }

  public int addMMData(String filename,String description,String[] catgcodes,String encrypted_catgcodes[], String encrypt_passwd) {
    int filesize=0;
    int i=0;
    File f=null;
    FileInputStream fis =null;
    try {
      f = new File(filename);
      fis = new FileInputStream(f);
      filesize=(int) f.length();
      byte buf[] = new byte[filesize];
      MyLogger.logger.debug("File " +filename+" Size:"+ filesize);
      i = fis.read(buf, 0, filesize);
      fis.close();

      return addMMData(buf,filename,description,catgcodes,encrypted_catgcodes,encrypt_passwd);
    } catch (Exception e) {
    	MyLogger.logger.error(e.getMessage());
      e.printStackTrace();
      return -1;
    }
  }
//2007-03-15
  public synchronized int addMMData(byte[] buf,String filename,String description,String[] catgcodes,String encrypted_catgcodes[], String encrypt_passwd) {
	MyLogger.logger.info("AddMMData with $IMPORT catg! eee (2007-03-15)");
	long t0 = System.currentTimeMillis();
    int seclvl=0;
    if (encrypt_passwd!=null && !encrypt_passwd.trim().equals("")) seclvl=1;

    String _importBatchNo = "$$" + com.solar.utility.SysDateTime.getDate();
    String _importBase = "$$IMPORT";

    if (catgcodes != null && catgcodes.length > 0) {
      int _l = catgcodes.length;
      String[] _catg = new String[_l + 2];
      _catg[1] = _importBatchNo;
      _catg[0] = _importBase;

      for (int i = 2; i < _l + 2; i++) {
        _catg[i] = catgcodes[i-2];
      }
      catgcodes = _catg;
    }

    if (encrypted_catgcodes != null && encrypted_catgcodes.length > 0) {
      int _l = encrypted_catgcodes.length;
      String[] _catg = new String[_l + 2];

      _catg[1] = _importBatchNo;
      _catg[0] = _importBase;

      for (int i = 2; i < _l + 2; i++) {
        _catg[i] = encrypted_catgcodes[i-2];
      }
      encrypted_catgcodes = _catg;
    }


    if (con == null) return -1;
    PreparedStatement pstmt_update=null;
    BLOB blob=null;
    int len=0;
    int mmkey=0;
    int catg=0;
    String sql=null;
    if (buf==null || buf.length==0)
      return -1;

/* check if file already exists */
    FileDigest fd = new FileDigest();
    byte[] digest = fd.getDigest(buf);
    int filesize= buf.length;
    try {
      File f = new File(filename);
      filename = f.getName();
    }catch (Exception e) {
    	MyLogger.logger.info("addMMdata():"+filename+" not exists !");
    }

    try {
    	MyLogger.logger.info("Check for duplication ...");
      sql = "select count(*) from mm_datastore where mm_size = ? and mm_sig = ?";
      OraclePreparedStatement pstmt = (OraclePreparedStatement)con.prepareStatement(sql);
      pstmt.setInt(1,filesize);
      pstmt.setObject(2,digest);
      ResultSet rs= pstmt.executeQuery();
      rs.next();
      int noofmatch=rs.getInt(1);
      rs.close();
      pstmt.close();
      if ( noofmatch> 0)  {
    	  MyLogger.logger.info("File ["+filename+"] already exists !");
        return -2;
      }

    } catch (Exception e) {
    	MyLogger.logger.error("** Error checking duplicated file ");
      e.printStackTrace();
      return -1;
    }

    if (description==null)
      description="";
    try {
      //System.out.println("Start inserting data ...");
    	
      con.setAutoCommit(false);
      mmkey=getNextMMKey();
      if (mmkey<0) {
    	  MyLogger.logger.error("Unable to get next mmkey!");
        throw new Exception("Unable to get Next mmkey");
      }
      MyLogger.logger.info("Created new mmkey :"+mmkey+" ...");
      Statement stmt=con.createStatement();
      
      OraclePreparedStatement pstmt0 = (OraclePreparedStatement)con.prepareStatement("insert into mm_datastore(mm_key,mm_data,mm_name,mm_desc,mm_size,mm_sig,mm_seclvl,create_dte,view_cnt) values (?,empty_blob(),?,?,?,?,?,SYSDATE,0)");
      oracle.sql.NUMBER ora_mmkey = new oracle.sql.NUMBER(mmkey);
      pstmt0.setNUMBER(1,ora_mmkey);
  
      byte[] encrypted_data =null;
      if (seclvl==0)
    	  encrypted_data = buf;
      else
    	  encrypted_data = ds.encryptWithPBE(encrypt_passwd,buf);


      oracle.sql.RAW sig = new oracle.sql.RAW(digest);  
      if (seclvl == 0) {
        pstmt0.setObject(2,filename.getBytes(this.getCharSet()));
        pstmt0.setObject(3,filename.getBytes(this.getCharSet()));
      }
      else {
        pstmt0.setObject(2,ds.encryptWithPBE(encrypt_passwd,filename.getBytes(this.getCharSet())));
        pstmt0.setObject(3,ds.encryptWithPBE(encrypt_passwd,filename.getBytes(this.getCharSet())));
      }
      pstmt0.setInt(4,buf.length);
      pstmt0.setRAW(5,sig);
      pstmt0.setInt(6,seclvl);

      pstmt0.executeUpdate();

      // Write BLOB - Recommended by Oracle
      String _selectBlobSql="select mm_data from mm_datastore where mm_key ="+mmkey;
      oracleWriteBLOB(con,_selectBlobSql,encrypted_data);


      int _importBaseKey=-1;
      int _importBatchKey=-1;


      if (catgcodes != null)
        for (int i=0;i<catgcodes.length;i++) {
          catg=getCatgKey("MM",catgcodes[i],"New MM category "+catgcodes[i],0,"");
          if (catg<0) {
            catg=addCatgKey("MM",catgcodes[i],"New MM category "+catgcodes[i],0,"");
          }

          if (catgcodes[i].equals(_importBase))
            _importBaseKey=catg;
          if (catgcodes[i].equals(_importBatchNo))
            _importBatchKey=catg;

          if (!catgcodes[i].equals(_importBase)) {
            sql="insert into catg_grpitem (catg_app,catg_key,item_key,last_upd_by,last_upd_dtm) values ('MM',"+String.valueOf(catg)+","+String.valueOf(mmkey)+",USER,SYSDATE)";
            stmt.executeUpdate(sql);
          }
        }

      if (encrypted_catgcodes != null)
       for (int i=0;i<encrypted_catgcodes.length;i++) {
          catg=getCatgKey("MM",encrypted_catgcodes[i],"New MM category "+encrypted_catgcodes[i],1,encrypt_passwd);
          if (catg<0) {
            catg=addCatgKey("MM",encrypted_catgcodes[i],"New MM category "+encrypted_catgcodes[i],1,encrypt_passwd);
          }

          if (encrypted_catgcodes[i].equals(_importBase))
            _importBaseKey=catg;
          if (encrypted_catgcodes[i].equals(_importBatchNo))
            _importBatchKey=catg;

          if (!encrypted_catgcodes[i].equals(_importBase)) {
            sql="insert into catg_grpitem (catg_app,catg_key,item_key,last_upd_by,last_upd_dtm) values ('MM',"+String.valueOf(catg)+","+String.valueOf(mmkey)+",USER,SYSDATE)";
            stmt.executeUpdate(sql);
          }
        }


      if (_importBaseKey>0 && _importBatchKey>0) {
        sql="select count(*) from catg_relation where catg_app='MM' and catg_key = "+_importBaseKey+" and child_key = "+_importBatchKey;

        ResultSet _rs = stmt.executeQuery(sql);
        _rs.next();
        int _cnt = _rs.getInt(1);
        _rs.close();
        if (_cnt==0) {
          sql = "insert into catg_relation values ('MM'," + _importBaseKey +
              "," + _importBatchKey + ")";
          stmt.executeUpdate(sql);
        }
      }
      stmt.close();
      pstmt0.close();
      con.commit();
      con.setAutoCommit(true);
      _totalWriteTime+=System.currentTimeMillis()-t0;
      _totalWriteByte+=encrypted_data.length;
      this.printBenchMark();
      return mmkey;
    } catch (SQLException e) {
    	MyLogger.logger.error("addMMData>>>FATAL ERROR >>>> Failed to add files into mmdb ...");
    	MyLogger.logger.error("addMMData>>>mm_key="+mmkey);
    	MyLogger.logger.error("addMMData>>>JDBC:"+e.getMessage()+"\n"+e.getSQLState());
      e.printStackTrace();
      try {
        con.rollback();
        con.setAutoCommit(true);
      } catch (SQLException e1) {
    	  MyLogger.logger.error("addMMData>>>JDBC:"+e.getMessage()+"\nFailed to ROLLBACK SQL statements");
        e.printStackTrace();
      }
      return -1;
    } catch (FileNotFoundException e) {
    	MyLogger.logger.error("addMMData: File not found"+e.getMessage());
      e.printStackTrace();
      return -1;
    } catch (Exception e) {
    	MyLogger.logger.error("addMMData :Exception !"+e.getMessage());
      e.printStackTrace();
      return -1;
    }
}
  public void printBenchMark() {
	  if (_totalWriteTime >0.0) {
	    double _write = ((double)_totalWriteByte / 1000.0)/((double)(_totalWriteTime )/1000.0) ;
	    MyLogger.logger.info("Write Speed==>"+_write+" KB/sec");
	  }  
	    
	  if (_totalReadTime >0.0) {
		    double _write = ((double)_totalReadByte / 1000.0)/((double)(_totalReadTime )/1000.0) ;
		    MyLogger.logger.info("Read Speed==>"+_write+" KB/sec");
		  }  
  }
  private void oracleWriteBLOB(Connection con,String _selectBlobSql,byte[] data) throws Exception{
      // Write BLOB subroutine recommended by Oracle
      System.out.println("oracleWriteBLOB,"+data.length);
      Statement _stmtUpdBlob = con.createStatement();
      ResultSet _rsGetBlob = _stmtUpdBlob.executeQuery(_selectBlobSql);
      _rsGetBlob.next();
      BLOB blob = ((OracleResultSet)_rsGetBlob).getBLOB(1);
      OutputStream _blobOs = blob.getBinaryOutputStream();
      ByteArrayInputStream _bais= new ByteArrayInputStream(data);
      int _bufSize=blob.getBufferSize();
      byte[] _writeBuffer = new byte[_bufSize];
      int length=-1;
      while ((length=_bais.read(_writeBuffer))!=-1)
        _blobOs.write(_writeBuffer,0,length);
      _bais.close();
      _blobOs.close();
      _rsGetBlob.close();
      _stmtUpdBlob.close();
  }
  public synchronized int addDataCache_000(int mmkey, byte[] indata, int size,String encrypt_passwd) {
	    int seclvl=0;
	    if (encrypt_passwd!=null && !encrypt_passwd.trim().equals("")) seclvl=1;

	    if (con == null) return -1;
	    if (indata == null || indata.length==0)  return -1;
	    if (size<=0) {
	      System.out.println("addDataCache: invalid thumbnail size ("+size+").");
	      return -1;
	    }
	    PreparedStatement pstmt_update=null;
	    Statement stmt=null;
	    OraclePreparedStatement pstmt0=null;
	    BLOB blob=null;
	    int len=0;
	    int catg=0;
	    String sql=null;
	    ImgProc imgproc = new ImgProc();
	    byte[] data =null;
	    imgproc.loadImage(indata);
	    imgproc.scaleToPixel(size);
	    data=imgproc.toByteArray();

	/* check if file already exists */
	    try {
	      con.setAutoCommit(false);
	      stmt=con.createStatement();
	      pstmt0 = (OraclePreparedStatement)con.prepareStatement("insert into mm_datacache(mm_cache,mm_key,mm_data) values (?,?,'01')");
	      pstmt0.setInt(1,size);
	      pstmt0.setInt(2,mmkey);
	      pstmt0.executeUpdate();
	      sql="select mm_data from mm_datacache where mm_key = "+String.valueOf(mmkey)+" and mm_cache="+size;
	      ResultSet rs = stmt.executeQuery(sql);
	      rs.next();
	      blob = ((OracleResultSet)rs).getBLOB (1);
	      OutputStream os = blob.getBinaryOutputStream();
	      /* Encrypting blob */
	      Cipher cipher = null;
	      if (seclvl==1)
	        cipher = ds.pbeEncryptCipher(encrypt_passwd);
	      else
	        cipher = new NullCipher();

	      CipherOutputStream cos = new CipherOutputStream(os,cipher);
	      cos.write(data);
	      cos.flush();
	      cos.close();

	      con.commit();

	      rs.close();
	      pstmt0.close();
	      stmt.close();

	      con.setAutoCommit(true);

	      System.out.println("ora_mmquery.addDataCache :data cache updated successfully");
	      return mmkey;
	    } catch (Exception e) {
	      System.out.println("ora_mmquery.addDataCache :Exception !"+e.getMessage());
	      e.printStackTrace();
	      try {
	        if (pstmt0!=null)
	          pstmt0.close();
	        if (stmt!=null)
	          stmt.close();
	      } catch (Exception ee) {

	      }

	      return -1;
	    }
	}

  public synchronized int addDataCache(int mmkey, byte[] indata, int thumbnail_size,String encrypt_passwd) {
	 System.out.println("addDataCache(Begin):"+mmkey+","+indata.length+","+thumbnail_size);
    int seclvl=0;
    if (encrypt_passwd!=null && !encrypt_passwd.trim().equals("")) seclvl=1;

    if (con == null) return -1;
    if (indata == null || indata.length==0)  return -1;
    if (thumbnail_size<=0) {
    	MyLogger.logger.error("addDataCache: invalid thumbnail size ("+thumbnail_size+").");
      return -1;
    }
    PreparedStatement pstmt_update=null;
    Statement stmt=null;
    OraclePreparedStatement pstmt0=null;
    BLOB blob=null;
    int len=0;
    int catg=0;
    String sql=null;
    ImgProc imgproc = new ImgProc();
    byte[] data =null;
    try {
        imgproc.loadImage(indata);
        imgproc.scaleToPixel(thumbnail_size);
        data = imgproc.toByteArray();
        } catch (OutOfMemoryError e) {
        	System.out.println("addDataCache:" +e.getMessage());
        	return -1;
    }    
    byte[] encrypted_data=null;
    
    if (seclvl==1) {
    	encrypted_data = ds.encryptWithPBE(encrypt_passwd,data);
    } else
    	encrypted_data=data;

   // ByteArrayInputStream _bais = new ByteArrayInputStream(encrypted_data);
   

/* check if file already exists */
    try {
      con.setAutoCommit(false);
      pstmt0 = (OraclePreparedStatement)con.prepareStatement("insert into mm_datacache(mm_cache,mm_key,mm_data) values (?,?,empty_blob())");
      pstmt0.setInt(1,thumbnail_size);
      pstmt0.setInt(2,mmkey);
//      pstmt0.setBinaryStream(3,_bais,encrypted_data.length);
      pstmt0.executeUpdate();
      String _selectBlobSql="select mm_data from mm_datacache where mm_key ="+mmkey+" and mm_cache="+thumbnail_size+ " for update";
      oracleWriteBLOB(con,_selectBlobSql,encrypted_data);
      
      pstmt0.close();
      con.commit();

   //   con.setAutoCommit(true);

      //System.out.println("ora_mmquery.addDataCache :data cache updated successfully");
      return mmkey;
    } catch (Exception e) {
    	System.out.println("addDataCache():Failed to insert into mm_datacache for mmkey="+mmkey);
    	System.out.println("addDataCache():Exception !"+e.getMessage());
      e.printStackTrace();
      try {
    	  con.rollback();
   // 	  con.setAutoCommit(true);
      } catch (Exception ee) {
    	  System.out.println("addDataCache():Failed to ROLLBACK TRANSACTION.");
         ee.printStackTrace();
      } finally {
    	  try {
    	  con.setAutoCommit(true);
    	  } catch (Exception eee) {
    		  MyLogger.logger.fatal("failed to close transaction !");
    	  }
    	  
  		 System.out.println("addDataCache(End):"+mmkey+","+indata.length+","+thumbnail_size);
      }
      return -1;
    }
}


//----------------------------------
//  Get exiting category key or
//  create new category key based
//  on inputted catecode
//----------------------------------
protected int getNextCatgKey() {
  int key= getNextKey("seq_catgkey");
  MyLogger.logger.error("New CatgKey :"+key);
  return key;
}

protected int getNextKey(String keyname) {
  String sql = "select seq_catgkey.nextval from dual";
  int nextkey=-1;
  try {
    OracleStatement stmt = (OracleStatement) con.createStatement();
    OracleResultSet rs = (OracleResultSet)stmt.executeQuery(sql);
    while(rs.next()) {
      nextkey= rs.getInt(1);
    }
    rs.close();
    stmt.close();
    return nextkey;

  }catch (Exception e) {
	  MyLogger.logger.error("ERROR!! ora_mmquery.getNextKey() ["+keyname+"] failed !");
    e.printStackTrace();
     return -1;
  }
}

  public int addCatgKey(String catgapp,String catgcode,
                      String catgdesc, int seclvl,String password)
                     throws Exception {
 int newkey=0;
 newkey=this.getNextCatgKey();
// add column :access_lvl
 String sql="insert into catg_grp (catg_app,catg_key,catg_code,catg_desc,catg_seclvl,last_upd_by,last_upd_dtm,access_lvl) "+
   " values (?,?,?,?,?,'sys',SYSDATE,1)";
 try {
	 MyLogger.logger.info("Adding new key mm catg key for catcode :"+catgcode);
  // System.out.println("--->"+sql);
   OraclePreparedStatement pstmt=(OraclePreparedStatement)con.prepareStatement(sql);
   pstmt.setString(1,"MM");
   pstmt.setInt(2,newkey);
   if (seclvl==0) {
     pstmt.setObject(3,catgcode.getBytes(this.getCharSet()));
     pstmt.setObject(4,catgdesc.getBytes(this.getCharSet()));
   }
   else {
     pstmt.setObject(3,ds.encryptWithPBE(password,catgcode.getBytes(this.getCharSet())));
     pstmt.setObject(4,ds.encryptWithPBE(password,catgdesc.getBytes(this.getCharSet())));
   }
   pstmt.setInt(5,seclvl);
   pstmt.executeUpdate();
   pstmt.close();
   this.forceTreeRefresh();
   return newkey;
 } catch (Exception e) {
	 MyLogger.logger.error("SQL>"+sql);
	 MyLogger.logger.error(e.getMessage());
   e.printStackTrace();
   throw new Exception();
}
}

 
//----------------------------------
//  Get exiting category key or
//  create new category key based
//  on inputted catecode
//----------------------------------
  public void updateCatgKey(int catgkey,String catgapp,String catgcode,
                        String catgdesc, int seclvl,String password)
                       throws Exception {
   int newkey=0;
   try {
     MyLogger.logger.info("ora_mmquery: Update category >"+catgcode+","+catgdesc+","+seclvl);
     OracleConnection ccc = (OracleConnection)con;
     OracleCallableStatement cs = (OracleCallableStatement)ccc.prepareCall("{ call p_u_catgkey(?,?,?,?,?,?,?) }");
     cs.setString(1,"MM");
     cs.setString(2,"xxx");  //obsoleted
     cs.setString(3,"xxx");  //obsoleted
     if (seclvl==0) {
       cs.setObject(4,catgcode.getBytes(this.getCharSet()));
       cs.setObject(5,catgdesc.getBytes(this.getCharSet()));
     }
     else {
       cs.setObject(4,ds.encryptWithPBE(password,catgcode.getBytes(this.getCharSet())));
       cs.setObject(5,ds.encryptWithPBE(password,catgdesc.getBytes(this.getCharSet())));
   // update cache
       removeCache_catgcode(password,catgkey);
       removeCache_catgdesc(password,catgkey);
     }
     cs.setInt(6,seclvl);
     cs.setInt(7,catgkey);
     cs.executeUpdate();
     cs.close();
     this.forceTreeRefresh();
   } catch (Exception e) {
     e.printStackTrace();
     throw new Exception();
  }
 }

  public int getCatgKey(String catgapp,String catgcode,
                        String catgdesc, int seclvl,String password)
                       throws Exception {
    try {
    	MyLogger.logger.info("getting catgkey ...");
      int catgkey=-1;
      if (seclvl == 0 ) {
        PreparedStatement pstmt=con.prepareStatement("select catg_key from catg_grp where catg_app  = 'MM' and catg_code = ? and catg_seclvl = 0");
        pstmt.setObject(1,catgcode.getBytes(this.getCharSet()));
        ResultSet rs=pstmt.executeQuery();
        while(rs.next()) {
          catgkey=rs.getInt(1);
        }
        rs.close();
        pstmt.close();
     }
     else {
       PreparedStatement pstmt =con.prepareStatement("select catg_key from catg_grp where catg_app = 'MM' and catg_code = ? and catg_seclvl > 0");
       byte[] ciphertext=ds.encryptWithPBE(password,catgcode.getBytes(this.getCharSet()));
       pstmt.setObject(1,ciphertext);
       ResultSet rs=pstmt.executeQuery();
       while(rs.next())
         catgkey=rs.getInt(1);
       rs.close();
       pstmt.close();
     }

     return catgkey;
   } catch (Exception e) {
     e.printStackTrace();
     throw new Exception();
   }
 }

    protected int  updateMMData(int mmkey,String filename) {
      return -1;

  }
/********************************************/
/* READ MM Data from DB                     */
/********************************************/


// read from mm_datastore
    public byte[] readMMData(int mmkey,String encrypt_passwd) {
      return readMMData(mmkey,encrypt_passwd,null);
    }

    public byte[] readMMData(int mmkey,String encrypt_passwd,String preview) {
    	System.out.println("readMMData() version 1");
      boolean _preview = false;
      boolean _cached = false;
      int iconsize=0;
      long _t0=System.currentTimeMillis();
    //20041201 if preview size is larger than pre-rendered icon size, the image will be re-rendered.
      if (preview==null || preview.trim().equals(""))
        _preview = false;
      else {
        _preview = true;
        try {
          iconsize=Integer.parseInt(preview) ;
          if (iconsize > _DefaultThumbnailSize)
            _preview = false;
          else {
            iconsize = _DefaultThumbnailSize;
          }
        }
        catch (Exception e) {
          iconsize=_DefaultThumbnailSize;
        }
      }
      _cached=hasPreview(mmkey,_DefaultThumbnailSize);

      if (_preview & !_cached) {
          _preview = false;
      }

//System.out.println("ora_mmquery.readMMData("+mmkey+","+encrypt_passwd+","+preview+"): iconsize="+iconsize+" cached="+_cached+" _preview="+_preview);
      if (!db.isConnected()) return null;

      OraclePreparedStatement pstmt_update=null;
      BLOB blob=null;
      oracle.sql.RAW sig = null;
      OracleStatement stmt = null;
      int len=0;
      int rowseclvl=0;
      byte buf[] = null;
      byte name[] = null;
      byte desc[] = null;
      String sql=null;
      try {
        con.setAutoCommit(true);
        stmt=(OracleStatement)con.createStatement();

        if (_preview)
          sql="select dbms_lob.getlength(a.mm_data),a.mm_data,b.mm_sig,b.mm_seclvl,mm_name,mm_desc from mm_datacache a,mm_datastore b where a.mm_key = b.mm_key and a.mm_key = "+String.valueOf(mmkey)+ " and a.mm_cache="+_DefaultThumbnailSize;
        else
          sql="select dbms_lob.getlength(mm_data),mm_data,mm_sig,mm_seclvl,mm_name,mm_desc from mm_datastore where mm_key = "+String.valueOf(mmkey);

        OracleResultSet rs = (OracleResultSet)stmt.executeQuery(sql);
        rs.next();
        int ilen=rs.getInt(1);
        blob = ((OracleResultSet)rs).getBLOB (2);
        sig = ((OracleResultSet)rs).getRAW(3);
        rowseclvl = rs.getInt(4);
        name = ((OracleResultSet)rs).getRAW(5).getBytes();
        desc = ((OracleResultSet)rs).getRAW(6).getBytes();

        InputStream is = blob.getBinaryStream();
        MyLogger.logger.info("Length of File :"+ilen);
        buf = new byte[ilen];
        int i=0;
        i=is.read(buf);
        is.close();
        rs.close();
        stmt.close();
        if (rowseclvl>0) {
          buf = ds.decryptWithPBE(encrypt_passwd, buf);
          name = ds.decryptWithPBE(encrypt_passwd,name);
          desc = ds.decryptWithPBE(encrypt_passwd,desc);
        }
        
        _totalReadByte+=ilen;
        _totalReadTime+=System.currentTimeMillis()-_t0;
        
        if (!_cached) {
          MyLogger.logger.debug("create data cache for "+mmkey);
          addDataCache(mmkey,buf,_DefaultThumbnailSize,encrypt_passwd);

          byte[] computesig = filedigest.getDigest(buf);
          /* Update signature */
          if (sig==null) {
        	  MyLogger.logger.info("Signature is null, rebuilding ...");
            String updsql="update mm_datastore set mm_size=dbms_lob.getLength(mm_data), "+
                          "mm_sig= ? where mm_key = ?";
            OraclePreparedStatement pstmt = (OraclePreparedStatement)con.prepareStatement(updsql);
            pstmt.setObject(1,computesig);
            pstmt.setInt(2,mmkey);
            pstmt.executeQuery();
            pstmt.close();
          }
          else {
            byte[] dbsig = sig.getBytes();
            if (dbsig==null)
            	MyLogger.logger.info("sig.getBytes() is null");
            else
            	MyLogger.logger.info("sig.getBytes().length = "+dbsig.length);
            boolean bsame=true;
            for (int ii=0;ii<computesig.length;ii++)
              if (computesig[ii] != dbsig[ii]) {
                bsame = false;
                break;
              }
            if (!bsame)
            	MyLogger.logger.info("*** Signature changed !!");
            else
            	MyLogger.logger.info("*** SAME signature !");
           }
        } // end !cached
        if (ilen<10) {
          deleteDataCache(mmkey);
        }
        else {
         // updateViewCnt(mmkey);
        }  
        this.printBenchMark(); 
      }	catch (SQLException e) {
    	  MyLogger.logger.error("JDBC:"+e.getMessage()+"\n"+e.getSQLState());
        e.printStackTrace();
      } catch (FileNotFoundException e) {
    	  MyLogger.logger.error("File not found"+e.getMessage());
        e.printStackTrace();
      } catch (Exception e) {
    	  MyLogger.logger.error("Exception:"+e.getMessage());
        e.printStackTrace();
      }
      return buf;
    }

    /* the active function for read MMdata */ 
    /* keep this function single thread */
    public synchronized mmdata readMMObj(int mmkey,String encrypt_passwd,String preview) {
    	System.out.println("Read readMMObj Version 2 >"+mmkey);
        boolean _preview = false;
        boolean _cached = false;
        int iconsize=0;
        mmdata _out = new mmdata();
        long _t0=System.currentTimeMillis();
      //20041201 if preview size is larger than pre-rendered icon size, the image will be re-rendered.
        if (preview==null || preview.trim().equals(""))
          _preview = false;
        else {
          _preview = true;
          try {
            iconsize=Integer.parseInt(preview) ;
            if (iconsize > _DefaultThumbnailSize)
              _preview = false;
            else {
              iconsize = _DefaultThumbnailSize;
            }
          }
          catch (Exception e) {
            iconsize=_DefaultThumbnailSize;
          }
        }
        _cached=hasPreview(mmkey,iconsize);

        if (_preview & !_cached) {
            _preview = false;
        }

//  System.out.println("ora_mmquery.readMMData("+mmkey+","+encrypt_passwd+","+preview+"): iconsize="+iconsize+" cached="+_cached+" _preview="+_preview);
        if (!db.isConnected()) return null;

        OraclePreparedStatement pstmt_update=null;
        BLOB blob=null;
        oracle.sql.RAW sig = null;
        OracleStatement stmt = null;
        int len=0;
        int rowseclvl=0;
        byte buf[] = null;
        byte name[] = null;
        byte desc[] = null;
        String sql=null;
        try {
          con.setAutoCommit(true);
          stmt=(OracleStatement)con.createStatement();

          if (_preview)
            sql="select dbms_lob.getlength(a.mm_data),a.mm_data,b.mm_sig,b.mm_seclvl,mm_name,mm_desc from mm_datacache a,mm_datastore b where a.mm_key = b.mm_key and a.mm_key = "+String.valueOf(mmkey)+" and a.mm_cache="+iconsize;
          else
            sql="select dbms_lob.getlength(mm_data),mm_data,mm_sig,mm_seclvl,mm_name,mm_desc from mm_datastore where mm_key = "+String.valueOf(mmkey);

          OracleResultSet rs = (OracleResultSet)stmt.executeQuery(sql);
          rs.next();
          int ilen=rs.getInt(1);
          blob = ((OracleResultSet)rs).getBLOB (2);
          sig = ((OracleResultSet)rs).getRAW(3);
          rowseclvl = rs.getInt(4);
          name = ((OracleResultSet)rs).getRAW(5).getBytes();
          desc = ((OracleResultSet)rs).getRAW(6).getBytes();

          InputStream is = blob.getBinaryStream();
          MyLogger.logger.debug("Length of File :"+ilen);
          buf = new byte[ilen];
          int i=0;
          i=is.read(buf);
          is.close();
          rs.close();
          stmt.close();
          if (rowseclvl>0) {
            buf = ds.decryptWithPBE(encrypt_passwd, buf);
            name = ds.decryptWithPBE(encrypt_passwd,name);
            desc = ds.decryptWithPBE(encrypt_passwd,desc);
         }
          _out.setData(buf);
          _out.setName(name);
          _out.setDesc(desc);
         
          _totalReadByte+=ilen;
          _totalReadTime+=System.currentTimeMillis()-_t0;
          
          _out.setCategory(this.getFileCategory(mmkey, encrypt_passwd));
          
          if (!_cached) {
       //     MyLogger.logger.info("create data cache for "+mmkey);
            addDataCache(mmkey,buf,_DefaultThumbnailSize,encrypt_passwd);

            byte[] computesig = filedigest.getDigest(buf);
            /* Update signature */
            if (sig==null) {
          	  MyLogger.logger.info("Signature is null, rebuilding ...");
              String updsql="update mm_datastore set mm_size=dbms_lob.getLength(mm_data), "+
                            "mm_sig= ? where mm_key = ?";
              OraclePreparedStatement pstmt = (OraclePreparedStatement)con.prepareStatement(updsql);
              pstmt.setObject(1,computesig);
              pstmt.setInt(2,mmkey);
              pstmt.executeQuery();
              pstmt.close();
            }
            else {
              byte[] dbsig = sig.getBytes();
              if (dbsig==null)
              	MyLogger.logger.info("sig.getBytes() is null");
              else
              	MyLogger.logger.info("sig.getBytes().length = "+dbsig.length);
              boolean bsame=true;
              for (int ii=0;ii<computesig.length;ii++)
                if (computesig[ii] != dbsig[ii]) {
                  bsame = false;
                  break;
                }
              if (!bsame)
              	MyLogger.logger.info("*** Signature changed !!");
              else
              	MyLogger.logger.info("*** SAME signature !");
             }
          } // end !cached
          if (ilen<10) {
            deleteDataCache(mmkey);
          }
     //     else
     //       updateViewCnt(mmkey);
      //    this.printBenchMark(); 
        }	catch (SQLException e) {
      	  MyLogger.logger.error("JDBC:"+e.getMessage()+"\n"+e.getSQLState());
          e.printStackTrace();
        } catch (FileNotFoundException e) {
      	  MyLogger.logger.error("File not found"+e.getMessage());
          e.printStackTrace();
        } catch (Exception e) {
      	  MyLogger.logger.error("Exception:"+e.getMessage());
          e.printStackTrace();
        }
        return _out;
      }


  public int populateImgIdx(int frm_catg,int to_catg) {
  /* for backward compatibale purpose */
    int imgcount=0;
    imgidx_iterator=-1;
    int ii=0;
    Vector v=new Vector();
    String sql = "select item_key from catg_grpitem where catg_app='MM' and catg_key = "+String.valueOf(frm_catg)+" order by itemkey";
  //  System.out.println(sql);
    try {
      Statement stmt=con.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while(rs.next()) {
 	v.add(new Integer(rs.getInt(1)));
      }
      rs.close();
      stmt.close();
      imgcount=v.size();
    } catch (Exception e) {
  	  MyLogger.logger.error("Exception:"+e.getMessage());

      imgcount=0;
      return 0;
    }
    imgidx=new int[imgcount];
    for (int i=0;i<imgcount;i++) {
      imgidx[i] = ((Integer)v.get(i)).intValue();
    }
    return imgcount;
  }


  


  public boolean updateFileGroups(int fkey, Vector oldgrp, Vector newgrp) {
    try {
      con.setAutoCommit(false);
      Statement stmt = con.createStatement();
 
      for (int i=0;i<oldgrp.size();i++) {
        //System.out.println(oldgrp.get(i));
        Integer I = (Integer)oldgrp.get(i);
        String ckey=I.toString();
        String sql="delete from catg_grpitem where catg_app = 'MM' and catg_key = "+ckey+" and item_key = "+fkey ;
        stmt.executeUpdate(sql);
      }
  
  //    String sql="delete from catg_grpitem where catg_app = 'MM' and item_key = "+fkey ;
  //    stmt.executeUpdate(sql);

      for (int j=0;j<newgrp.size();j++) {
        //System.out.println(newgrp.get(j));
        Integer J=(Integer)newgrp.get(j);
        String ckey=J.toString();
        String _sql="select count(*) from catg_grpitem where catg_app='MM' and catg_key="+ckey+" and item_key="+fkey;
        ResultSet _rs=stmt.executeQuery(_sql);
        _rs.next();
        int _cnt=_rs.getInt(1);
        _rs.close();
        if (_cnt>0) {
        	MyLogger.logger.info("Already exist in this group !");
        } else {
          String sql="insert into catg_grpitem values ('MM',"+ckey+","+fkey+",USER,SYSDATE)";
          stmt.executeUpdate(sql);
        }
      }
      con.commit();
      stmt.close();
      con.setAutoCommit(true);
      return true;
    } catch (Exception e) {
      try {
      	  MyLogger.logger.fatal(e.getMessage());

      con.rollback();
      con.setAutoCommit(true);
      } catch (Exception ee) {
      	  MyLogger.logger.fatal(ee.getMessage());

      }
      e.printStackTrace();
      return false;
    }
  }
  
  private Vector getFileGroupsByType(int fkey,String encrypt_passwd,int _type) {
	    OraclePreparedStatement pstmt_getfilegroup = null;

	    String _sql_filter=null;
	    if (encrypt_passwd ==null || encrypt_passwd.trim().equals(""))
	      _sql_filter="=0 ";
	    else
	      _sql_filter=">0 ";

	    Vector v = new Vector();
	    
	    /* show all file groups, the last col indicates if the file associated with the cate_key*/
	    String sql= "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,0 from catg_grp a where a.catg_seclvl"+_sql_filter+" and "+
	                "not exists " +
	                "(select 1 from catg_grpitem where item_key = ? and catg_key = a.catg_key and catg_app = a.catg_app) " +
	                "union "+
	                "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,1 from catg_grp a, catg_grpitem b "+
	                " where "+
	                "a.catg_app = b.catg_app and " +
	                "a.catg_key = b.catg_key and " +
	                "a.catg_seclvl "+_sql_filter+" and "+
	                "b.item_key = ? order by 2,1,3,4  ";

	    if (_type==2) {  // only show file groups which the fkey belongs to 
		    sql= 
            "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,1 from catg_grp a, catg_grpitem b "+
            " where "+
            "a.catg_app = b.catg_app and " +
            "a.catg_key = b.catg_key and " +
            "a.catg_seclvl "+_sql_filter+" and b.item_key = ? and "+  //two inparms as type 1 sql
            "b.item_key = ? order by 2,1,3,4  ";	    	
	    }
	    
	    OracleResultSet rs=null;
	    try {
	    pstmt_getfilegroup = (OraclePreparedStatement)con.prepareStatement(sql);
	    pstmt_getfilegroup.setInt(1,fkey);
	    pstmt_getfilegroup.setInt(2,fkey);
	    rs = (OracleResultSet)pstmt_getfilegroup.executeQuery();
	    String sraw=null;
	    String sdesc = null;
	    String ssec = null;
	    while (rs.next()) {
	      byte[] raw = (byte[])rs.getObject(1);
	      byte[] desc=(byte[])rs.getObject(2);
	      int catgkey = rs.getInt(3);

	      int seclvl = rs.getInt(4);
	      int member = rs.getInt(5);

	      //System.out.println(catgkey+","+seclvl);

	      if (seclvl==0) {
	        sraw=new String(raw,this.getCharSet());
	        sdesc = new String(desc,this.getCharSet());
	      }
	      else {
	        if (encrypt_passwd != null && !encrypt_passwd.trim().equals("")) {
	          try {
//	            sraw=new String(ds.decryptWithPBE(encrypt_passwd,raw));
//	            sdesc=new String(ds.decryptWithPBE(encrypt_passwd,desc));
	              sraw = fastDecrypt_catgcode(encrypt_passwd,catgkey,raw);
	              sdesc = fastDecrypt_catgdesc(encrypt_passwd,catgkey,desc);

	          } catch (Exception e) {
	          	  MyLogger.logger.error("Exception:"+e.getMessage());

	            sraw=null;
	            sdesc=null;
	          }
	        }
	      }
	      if (sraw!=null) {
	  //System.out.println("plain]"+sraw+","+sdesc);

	        Vector rowvector = new Vector();
	        rowvector.add(sraw);
	        rowvector.add(sdesc);
	        rowvector.add(new Integer(catgkey));
	        if (seclvl == 0)
	          rowvector.add(new Boolean(false));
	        else
	          rowvector.add(new Boolean(true));
	        if (member == 0)
	          rowvector.add(new Boolean(false));
	        else
	          rowvector.add(new Boolean(true));
	        v.add(rowvector);
	      }
	    }
	    rs.close();
	    pstmt_getfilegroup.close();


	    return v;
	    } catch (Exception e) {
	      MyLogger.logger.error(e.getMessage());
	      e.printStackTrace();
	      return null;
	    }
	  }
  
  public Vector getFileGroups(int fkey,String encrypt_passwd) {
	 // get all file groups with indication showing whether the file belongs to the group
     return getFileGroupsByType(fkey,encrypt_passwd,1);
  }
  
  public Vector getFileCategory(int fkey,String encrypt_passwd) {
	  // get file groups which the file associated with
	     return getFileGroupsByType(fkey,encrypt_passwd,2);
	  }

  public Vector getAllCatgGrp(String encrypt_passwd) {
    Vector v = new Vector();
    String sql =null;
    if (encrypt_passwd == null || encrypt_passwd.trim().equals(""))
       sql = "select catg_code,catg_seclvl from catg_grp where catg_app = 'MM' and catg_seclvl=0 ";
    else
       sql = "select catg_code,catg_seclvl from catg_grp where catg_app = 'MM' and catg_seclvl > 0 " ;

    ResultSet rs=null;
    try {
    Statement stmt = con.createStatement();
    rs = stmt.executeQuery(sql);
    while (rs.next()) {
      byte[] raw = (byte[])rs.getObject(1);
      int seclvl = rs.getInt(2);
      if (seclvl==0)
        v.add(new String(raw,this.getCharSet()));
      else {
        if (encrypt_passwd != null && !encrypt_passwd.trim().equalsIgnoreCase("")) {
          byte[] cleartext=ds.decryptWithPBE(encrypt_passwd,raw);
          if (cleartext !=null)
            v.add(new String(cleartext,this.getCharSet()));
        }
      }
    }
    rs.close();
    stmt.close();
    } catch (Exception e) {
    	  MyLogger.logger.error(e.getMessage());

      e.printStackTrace();
    }
    return v;
  }

  public boolean updateCatgRelation2(int catkey, Vector delchilds, Vector addchilds) {
	    try {
	      con.setAutoCommit(false);
	      Statement stmt = con.createStatement();

	      for (int i=0;i<delchilds.size();i++) {
	      //    System.out.println(oldgrp.get(i));
	          Integer I = (Integer)delchilds.get(i);
	          String delkey=I.toString();
	          String sql="delete from catg_relation where catg_app = 'MM' and catg_key = "+catkey+" and child_key = "+delkey ;
	          stmt.executeUpdate(sql);
	        }
//	      String sql =
//	          "delete from catg_grpitem where catg_app = 'MM' and item_key = " +    fkey;
//	      stmt.executeUpdate(sql);

	      for (int j = 0; j < addchilds.size(); j++) {
	        System.out.println(addchilds.get(j));
	        Integer J = (Integer) addchilds.get(j);
	        String addkey = J.toString();
	        String _sql="select count(*) from catg_relation where catg_app='MM' and catg_key="+catkey+" and child_key="+addkey;
	        ResultSet _rs=stmt.executeQuery(_sql);
	        _rs.next();
	        int _cnt=_rs.getInt(1);
	        _rs.close();
	        if (_cnt==0) {
	          String sql = "insert into catg_relation values ('MM'," + catkey + "," + addkey +")";
	          stmt.executeUpdate(sql);
	        }
	      }
	      con.commit();
	      stmt.close();
	      con.setAutoCommit(true);
	      return true;
	    }
	    catch (Exception e) {
	      try {
	        con.rollback();
	        con.setAutoCommit(true);
	      }
	      catch (Exception ee) {      MyLogger.logger.error(ee.getMessage());
	      }
	      MyLogger.logger.error(e.getMessage());
	      
	      e.printStackTrace();
	      return false;
	    }
	  }
  // 2002-06-16
  public boolean updateCatgRelation(int parentkey, Vector childgrp) {
    try {
      con.setAutoCommit(false);
      Statement stmt = con.createStatement();
      String sql="delete from catg_relation where catg_app = 'MM' and catg_key = "+parentkey;
      stmt.executeUpdate(sql);
      for (int j=0;j<childgrp.size();j++) {
        Integer J=(Integer)childgrp.get(j);
        String ckey=J.toString();
        sql="insert into catg_relation values ('MM',"+parentkey+","+ckey+")";
        stmt.executeUpdate(sql);
      }
      con.commit();
      stmt.close();
      con.setAutoCommit(true);
      this.forceTreeRefresh();
      return true;
    } catch (Exception e) {
  	  MyLogger.logger.error(e.getMessage());

      try {
      con.rollback();
      con.setAutoCommit(true);
      } catch (Exception ee) {
    	  MyLogger.logger.fatal(ee.getMessage());

      }
      e.printStackTrace();
      return false;
    }
  }

  protected int getGroupItems(byte[] code, Vector v ) {
    return getGroupItems(code,v,mm_query.NATURAL_ORDER);
  }
  protected int getGroupItems(byte[] code, Vector v,int order) {
    String order_clause="";
    if (order==mm_query.NATURAL_ORDER) {
      order_clause="order by b.item_key";
    }
    if (order==mm_query.FREQUENCY_ORDER) {
      order_clause="order by c.view_cnt,c.mm_key";
    }
    String sql = "select b.item_key from catg_grp a,catg_grpitem b,mm_datastore c "+
                 "where a.catg_app = 'MM' and "+
                 "a.catg_app = b.catg_app and "+
                 "a.catg_key = b.catg_key and "+
                 "b.item_key = c.mm_key and "+
                 "a.catg_code=? "+order_clause;
    int count=0;
    try {
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.setObject(1,code);
      ResultSet rs = pstmt.executeQuery();
      while(rs.next()) {
        count++;
        v.add(new Integer(rs.getInt(1)));
        //System.out.print(count);
      }
      rs.close();
      pstmt.close();
      return count;
    } catch (Exception e) {
  	  MyLogger.logger.error(e.getMessage());

      e.printStackTrace();
      return 0;
    }
  }
  // 2002-06-18
  /*                                                 */
  /* get All img_key based on the inputted parentkey */
  /* to be used by mmdbitem.jsp                      */
  /* replace getImgIdx(int)                          */
  /*                                                 */

  
 



  public int registerMM(String filename,int filestatus,int filesize, byte[] filesig) throws Exception{
    int mmkey=0;
    CallableStatement cstmt = con.prepareCall("{call p_i_mm_register(?,?,?,?,?)}");
    cstmt.setObject(1,filename.getBytes(this.getCharSet()));
    cstmt.setInt(2,filesize);
    cstmt.setObject(3,filesig);
    cstmt.setInt(4,filestatus);
    cstmt.registerOutParameter(5,java.sql.Types.NUMERIC);
    cstmt.executeUpdate();
    mmkey=cstmt.getInt(5);
    cstmt.close();
    return mmkey;
  }

  public boolean isMMRegistered(int filesize, byte[] digest) throws Exception {
    PreparedStatement pstmt = con.prepareStatement("select f_chk_mm_register(?,?) from dual");
    pstmt.setInt(1,filesize);
    pstmt.setObject(2,digest);
    ResultSet rs= pstmt.executeQuery();
    rs.next();
    int noofmatch=rs.getInt(1);
    rs.close();
    pstmt.close();
    if (noofmatch<=0)
      return false;
    else
      return true;
  } /* end of isMMRegistered */



  
  

  public static String version() {
    return "ORA_MMQUERY:2009-06-21";
  }

}
