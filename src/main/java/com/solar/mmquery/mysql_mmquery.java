/*
 Usage:

 DBBridge dbconnect = new DBBridge();
 dbconnect.connectDB();
 mq = new mysql_mmquery(dbconnect);
 mq.mm_init();
 imgicon=mq.retrievePrevImg();
 imgicon=mq.retrieveCurImg();
 imgicon=mq.retrieveNextImg();
 */
/*
 *                                         
 * When   What                             
 * 040825  add method addDataCache         
 * 040829                                  
 * 041201                                  
 * 050318  add ordering by view_cnt        
 * 050331  fix bug with encryption         
 * 050425  add getCatgTreeImgDesc() method 
 * 060206  Replace BIG5 by UTF8 support    
 * 060212  Bug fix duplicated catg code when adding new files 
 * 060323  Add signature validation after inserting record to db 
 * 070315  Add benchmarking info
 * 110423  addDataCache() - now will delete and recreate.             
 */
package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import javax.crypto.*;


import com.solar.utility.*;
import com.solar.imgproc.*;

public class mysql_mmquery     extends mm_query {
    static long _totalWriteTime=0;
    static long _totalWriteByte=0;
    static long _totalReadTime=0;
    static long _totalReadByte=0;
    
//-------------------------------------------------
//	Default Public Constructor
//
//-------------------------------------------------

//-------------------------------------------------
//	Testing routine 1
//
//-------------------------------------------------
  public static void main(String argv[]) {
    DBBridge db = new mysqlDBBridge();
    db.initConnection();
    mysql_mmquery mq = new mysql_mmquery(db);
    mq.mm_test();
  }

  public mysql_mmquery(DBBridge db) {
    this.db = db;
    userInfo = db.getUserInfo();
    if (!db.isConnected()) {
      return;
    }
    con = db.getConnection();
  }

  public void printDBInfo() {
    MyLogger.logger.info("MySQL version of mmquery");
    MyLogger.logger.info("Last update :2012-02-12 ");
  }



  public int mm_test() {
    printDBInfo();
    int i = 0;
    int mmkey = 0;
    if (!db.isConnected()) {
      return -1;
    }
    mm_init();
    String catgcode[] = { "jap"};
    mmkey = this.addMMData("d:\\IMG_4617.jpg", "testfile", catgcode);
    mmkey = this.addMMData("d:\\IMG_4618.jpg", "testfile", catgcode);
    mmkey = this.addMMData("d:\\IMG_4622.jpg", "testfile", catgcode);

//    mmkey=addMMKey(0);
    MyLogger.logger.debug("New mmkey = " + mmkey);
    if (mmkey > 0) {
//    i=(mmkey,"c:\\DVD_ID4.jpg");
      ImageIcon imgicon = retrieveMMData(mmkey);
      MyLogger.logger.debug("Image Height = " + imgicon.getIconHeight());
      MyLogger.logger.debug("Image Width = " + imgicon.getIconWidth());
    }
    return i;
  }

  public int addImageFile(int mmcatg, String filename) {
    return importFile(filename, mmcatg);
  }

  public Vector getDupList() {
    return duplist;
  }

//-------------------------------------------------
//	Add new MM object
//  using result set instead of output parameter
//-------------------------------------------------

  public int addMMKey(int mm_catg) {
    int mm_key = getNextMMKey();
    return mm_key;
  }

//-------------------------------------------------
//	Add MM data
//
//-------------------------------------------------
  public int updateMMData(int mmkey, String filename) {
    if (!db.isConnected()) {
      return -1;
    }
    PreparedStatement pstmt_update = null;
    Blob blob = null;
    int len = 0;
    String sql = "update mm_datastore set mm_data =?,mm_name=?,mm_size=?,create_dte = curdate() where mm_key = " +
        Integer.toString(mmkey);
    try {
      File f = new File(filename);
      FileInputStream fis = new FileInputStream(f);
      pstmt_update = con.prepareStatement(sql);
      len = (int) f.length();
      pstmt_update.setBinaryStream(1, fis, (int) f.length());
      pstmt_update.setString(2, f.getName()); // size field is obsoleted
      pstmt_update.setInt(3, (int) f.length());
      pstmt_update.executeUpdate();
      MyLogger.logger.debug("Successfully added " + f.getName() + " to a new record");
    }
    catch (SQLException e) {
      MyLogger.logger.error("JDBC:" + e.getMessage() + "\n" + e.getSQLState());
    }
    catch (FileNotFoundException e) {
      MyLogger.logger.error("File not found" + e.getMessage());
    }
    return 1;
  }

//-------------------------------------------------
//	populate Image index
//
//-------------------------------------------------
 
  public Vector getAllCatgGrp(String encrypt_passwd) {
    Vector v = new Vector();
    String sql = null;
    if (encrypt_passwd == null || encrypt_passwd.trim().equals("")) {
      sql = "select catg_code,catg_seclvl from catg_grp where catg_app = 'MM' and catg_seclvl=0 and access_lvl <=" +
          userInfo.getAccessLevel();
    }
    else {
      sql = "select catg_code,catg_seclvl from catg_grp where catg_app = 'MM' and catg_seclvl > 0 and access_lvl <= " +
          userInfo.getAccessLevel();

    }
    ResultSet rs = null;
    try {
      Statement stmt = con.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        byte[] raw = (byte[]) rs.getObject(1);
        int seclvl = rs.getInt(2);
        if (seclvl == 0) {
          v.add(new String(raw, this.getCharSet()));
        }
        else {
          if (encrypt_passwd != null &&
              !encrypt_passwd.trim().equalsIgnoreCase("")) {
            byte[] cleartext = ds.decryptWithPBE(encrypt_passwd, raw);
            if (cleartext != null) {
              v.add(new String(cleartext, this.getCharSet()));
            }
          }
        }
      }
      rs.close();
      stmt.close();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    catch (java.io.UnsupportedEncodingException e1) {
      e1.printStackTrace();
    }
    return v;
  }

  protected int getGroupItems(int catgkey, Vector v) {
    return getGroupItems(catgkey, v, mm_query.NATURAL_ORDER);
  }

  protected int getGroupItems(int catgkey, Vector v, int order) {
    String order_clause = "";
    if (order == mm_query.NATURAL_ORDER) {
      order_clause = "order by b.item_key";
    }
    if (order == mm_query.FREQUENCY_ORDER) {
      order_clause = "order by a.view_cnt,b.item_key";
    }
    String sql =
        "select b.item_key from catg_grpitem b, mm_datastore a,catg_grp c " +
        "where b.catg_app = 'MM' and b.catg_key = c.catg_key and c.access_lvl <= ? and " +
        "b.catg_key= ? and b.item_key = a.mm_key " +
        order_clause; /* 20050318 */
    MyLogger.logger.debug(sql);
    int count = 0;
    int key = -1;
    try {
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, userInfo.getAccessLevel());
      pstmt.setInt(2, catgkey);
      ResultSet rs = pstmt.executeQuery();
      while (rs.next()) {
        count++;
        v.add(new Integer(rs.getInt(1)));
        //System.out.print(count);
      }
      rs.close();
      pstmt.close();
      return count;
    }
    catch (SQLException e) {
      e.printStackTrace();
      return 0;
    }
  }

  protected int getGroupItems(byte[] code, Vector v) {


    return 0;
  }

  protected int getNextKey(String keyname) {
    String sql = "update sys_tbl set keyvalue = keyvalue +1 where keyname= \"" +
        keyname + "\"";
    if (db.sqlExec(sql) == 1) {
      sql = "select keyvalue from sys_tbl where keyname = \"" + keyname + "\"";
      return db.sqlExecINT(sql);
    }
    return -1;
  }

  protected int getNextMMKey() {
    return getNextKey("seq_mmkey");
  }

  protected int getNextRegKey() throws Exception {
    int key = 0;
    key = getNextKey("mm_regkey");
    //System.out.println("key=" + key);
    if (key == -1) {
      throw new Exception("Error getting new register key");
    }
    return key;
  }

  protected int getNextCatgKey() {
    return getNextKey("seq_catgkey");
  }


  public int addDataCache(int mmkey, byte[] indata, int size,
                                       String encrypt_passwd) {
    int seclvl = 0;
    if (encrypt_passwd != null && !encrypt_passwd.trim().equals("")) {
      seclvl = 1;

    }
    if (con == null) {
      return -1;
    }
    if (indata == null || indata.length == 0) {
        MyLogger.logger.error("addDataCache: invalid inbound data.");
    	return -1;
    }
    if (size <= 0) {
      MyLogger.logger.error("addDataCache: invalid thumbnail size (" + size + ").");
      return -1;
    }

    PreparedStatement pstmt_update = null;
    int len = 0;
    int catg = 0;
    String sql = null;
    ImgProc imgproc = new ImgProc();
    byte[] data = null;

    try {
    imgproc.loadImage(indata);
    imgproc.scaleToPixel(size);
    data = imgproc.toByteArray();
    } catch (Exception e) {
    	MyLogger.logger.error("addDataCache:" +e.getMessage());
    	return -1;
    } 
    /* check if file already exists */
    try {
      if (seclvl == 1) {
        data = ds.encryptWithPBE(encrypt_passwd, data);
      }
      con.setAutoCommit(false);
      Statement stmt = con.createStatement();
      stmt.execute("delete from mm_datacache where mm_key = "+mmkey);
      PreparedStatement pstmt0 = con.prepareStatement(
          "insert into mm_datacache(mm_cache,mm_key,mm_data) values (?,?,?)");
      pstmt0.setInt(1, size);
      pstmt0.setInt(2, mmkey);
      pstmt0.setBytes(3, data);
      pstmt0.executeUpdate();

      con.commit();
      pstmt0.close();
      stmt.close();
      con.setAutoCommit(true);
      MyLogger.logger.debug("addDataCache: ADDED "+mmkey+", thumb size:"+data.length+","+size+", org size:"+indata.length+", sec="+seclvl);
      //System.out.println("** Data cache UPDATED.");
      return mmkey;
    }
    catch (SQLException e) {
      MyLogger.logger.error("addMMData :Exception !" + e.getMessage());
      e.printStackTrace();
      return -1;
    } catch (Exception e) {
        MyLogger.logger.error("addMMData :Exception !" + e.getMessage());
        e.printStackTrace();
        return -1;
    }
  }

  protected byte[] getEncryptByteArray(byte[] indata, Cipher cipher) throws
      Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream(indata.length);
    CipherOutputStream cos = new CipherOutputStream(baos, cipher);
    cos.write(indata, 0, indata.length);
    cos.flush();
    byte[] encrypted_data = baos.toByteArray();
    return encrypted_data;
  }


  private int getExistingKey (int filesize, byte[] md5) throws Exception {
	  int mmkey=-1;
      String sql = "select ifnull(max(mm_key),0) from mm_datastore where mm_size = ? and mm_sig = ?";
      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, filesize);
      pstmt.setObject(2, md5);
      ResultSet rs = pstmt.executeQuery();
      rs.next();
      mmkey = rs.getInt(1);
      rs.close();
      pstmt.close();
      return mmkey;
  }
  
  private void addItem2Groups(int mmkey, String catgcodes[], String encrypt_passwd) throws Exception{
  	  int catgKey=-1;
	  if (catgcodes != null) {
    	  for (int i = 0; i < catgcodes.length; i++) {
            catgKey=getCatgKey("MM", catgcodes[i],"new " + catgcodes[i], (encrypt_passwd.equals("")?0:1), encrypt_passwd);
          	if (catgKey<0) {
          		catgKey = addCatgKey("MM", catgcodes[i],"new " + catgcodes[i], (encrypt_passwd.equals("")?0:1),encrypt_passwd);
          	} 
            if (db.sqlExecINT("select count(*) from catg_grpitem where catg_app = 'MM' and catg_key = "+catgKey+" and item_key = "+mmkey)==0) {
              if(db.sqlExec("insert into catg_grpitem (catg_app,catg_key,item_key,last_upd_by,last_upd_dtm) values ('MM'," + String.valueOf(catgKey) + "," + String.valueOf(mmkey) +  ",\"sa\",curdate())")==-1) {
              	MyLogger.logger.debug("Error insert new row into catg_grpitem!"+db.sqlerrtext);
               	throw new Exception("Error insert new row into catg_grpitem!");
              }
            }  
          }
       }	  
  }
  /* last update: 2011-05-31 */  
  public int addMMData(byte[] inputdata, String filename,
                                    String description, String[] catgcodes,
                                    String encrypted_catgcodes[],
                                    String encrypt_passwd) {
    boolean isEncrypted = false;
    boolean isNew = true;
    boolean bException = false;

    int mmkey = -1;
    String sql = null;

    FileDigest fd = null;
    File f = null;
    
    String _importBatchNo=null;
    String _importBase = null;
 
    int _importBaseKey=-1;
    int _importBatchKey=-1;
    
    String[] catgGroups = null;
    
	if (encrypt_passwd != null && !encrypt_passwd.trim().equals("")) {
      isEncrypted = true;
    } else
    	encrypt_passwd="";
	
     _importBatchNo = "$$" + com.solar.utility.SysDateTime.getDate();
     _importBase = "$$IMPORT";
  
    
    if (catgcodes != null && catgcodes.length > 0)
    	catgGroups=catgcodes ; 

    if (encrypted_catgcodes != null && encrypted_catgcodes.length > 0) 
    	catgGroups=encrypted_catgcodes ;
    

    if (inputdata == null || inputdata.length <= 0) {
      MyLogger.logger.warn("dataMMData : input buffer is empty !");
    }

    if (con == null) {
    	MyLogger.logger.warn("addMMData: DB not connected !");
      return -1;
    }
    MyLogger.logger.debug("addMMData-->" + inputdata.length);

    int len = 0;
    int catg = 0;
    fd = new FileDigest();
    byte[] digest = fd.getDigest(inputdata);
    int filesize = inputdata.length;
    //  try {
    f = new File(filename);
    if (f.exists()) {
      int _s = (int) f.length();
      if (_s != filesize) {
    	  MyLogger.logger.debug("--> Data Size Mismatch !!! Size of Byte[] = " +filesize + " Size of file = " + f.length());
      }
      filename = f.getName();
    }
    f = null;

    if (description == null) {
      description = "";
    }
    
    long _t0=System.currentTimeMillis();    
    
    try {
      con.setAutoCommit(false);
      mmkey = getExistingKey(filesize,digest);
      if (mmkey>0)
    	  isNew=false;

      if (isNew) {
          PreparedStatement stmtInsert = con.prepareStatement("insert into mm_datastore(mm_key,mm_name,mm_desc,mm_sig,mm_seclvl,create_dte,mm_size) values (?,?,?,null,?,curdate(),?)");
    	  mmkey = getNextMMKey();
          if (mmkey < 0) {
      	    MyLogger.logger.error("Unable to get next mmkey!");
            throw new Exception("Unable to get next mmkey !");
          }
          stmtInsert.setInt(1, mmkey);
          if (!isEncrypted) {
        	stmtInsert.setObject(2, filename.getBytes(this.getCharSet()));
        	stmtInsert.setObject(3, filename.getBytes(this.getCharSet()));
          }
          else {
        	stmtInsert.setObject(2, ds.encryptWithPBE(encrypt_passwd, filename.getBytes(this.getCharSet())));
        	stmtInsert.setObject(3, ds.encryptWithPBE(encrypt_passwd, filename.getBytes(this.getCharSet())));
          }
          stmtInsert.setInt(4, (isEncrypted?1:0));
          stmtInsert.setInt(5, inputdata.length);
          stmtInsert.executeUpdate();

          PreparedStatement stmtUpdate = con.prepareStatement("update mm_datastore set mm_data =?,mm_sig = ? where mm_key = " +Integer.toString(mmkey));
          byte[] encrypted_data = null;
          if (isEncrypted) {
            encrypted_data = ds.encryptWithPBE(encrypt_passwd, inputdata);
            byte[] decrypted_data = null;
            decrypted_data = ds.decryptWithPBE(encrypt_passwd, encrypted_data);
            MyLogger.logger.debug("check size=" + decrypted_data.length);
          }
          else {
            encrypted_data = inputdata;
          }
          stmtUpdate.setBytes(1, encrypted_data);
          stmtUpdate.setBytes(2, digest);
          stmtUpdate.executeUpdate();
          _totalWriteByte+=encrypted_data.length;
          _totalWriteTime+=System.currentTimeMillis()-_t0;
          
          stmtUpdate.close();
          stmtInsert.close();
      } 
      
      addItem2Groups(mmkey,catgGroups,encrypt_passwd);
      if (isNew) {
    	String[] importBatchGrp = new String[1];
    	importBatchGrp[0]=_importBatchNo;
    	addItem2Groups(mmkey,importBatchGrp,encrypt_passwd);
      }
      _importBaseKey = getCatgKey("MM",_importBase,null,(isEncrypted?1:0),encrypt_passwd );
      
      if (_importBaseKey == -1) {
     	  _importBaseKey = addCatgKey("MM", _importBase,"Import Base", (encrypt_passwd.equals("")?0:1),encrypt_passwd);
      }
      
      _importBatchKey = getCatgKey("MM",_importBatchNo,null,(isEncrypted?1:0),encrypt_passwd );
     
      if (_importBaseKey>0 && _importBatchKey>0) {
    	if (db.sqlExecINT("select count(*) from catg_relation where catg_app='MM' and catg_key = "+_importBaseKey+" and child_key = "+_importBatchKey)==0) { 
          if (db.sqlExec("insert into catg_relation values ('MM'," + _importBaseKey + "," + _importBatchKey + ")")==-1) {
        	  MyLogger.logger.debug("Error insert new row into catg_relation. "+db.sqlerrtext);
        	  throw new Exception("Error inserting new row into catg_relation. "+db.sqlerrtext);
          }
        }
      }

      //20060323:Validate data
      if (isNew) { 
        sql = "select mm_data from mm_datastore where mm_key = " + Integer.toString(mmkey);
        Statement stmtChk = con.createStatement();
        ResultSet rsChk = stmtChk.executeQuery(sql);
        byte[] dataToChk = null;
        while (rsChk.next()) {
          dataToChk = rsChk.getBytes(1);
        }
        rsChk.close();
        stmtChk.close();
        byte[] tmpSig = null;
        if (isEncrypted) {
          tmpSig = fd.getDigest(ds.decryptWithPBE(encrypt_passwd, dataToChk));
        }
        else {
          tmpSig = fd.getDigest(dataToChk);
        }
        if (tmpSig.length != digest.length) {
          throw new Exception("Signature length mismatch after reading from database ! Insert operation aborted !");
        }
        for (int i = 0; i < tmpSig.length; i++) {
          if (tmpSig[i] != digest[i]) {
            throw new Exception("Signature mismatch after reading from database ! Insert operation abort !");
          }
        }
      } //end updating mm_datastore
      

      con.commit();
      MyLogger.logger.debug("addMMData: Successfully added " + filename + " to a new record");
      this.printBenchMark();
    }
    catch (SQLException e) {
      MyLogger.logger.error("addMMData: SQLException:" + e.getMessage() + "\n" + e.getSQLState());
      e.printStackTrace();
      bException = true;
      mmkey = -1;
    }
    catch (Exception e) {
      MyLogger.logger.error("addMMData :Exception !" + e.getMessage());
      e.printStackTrace();
      bException = true;
      mmkey = -1;
    }
    finally {
      try {
        if (bException) {
          con.rollback();
          MyLogger.logger.error("addMMData: ROLLBACK");
        }
        con.setAutoCommit(true);
        MyLogger.logger.info("set autocommit = true");
      }
      catch (Exception e) {
    	  MyLogger.logger.error("addMMData :Error commit/rollback transaction");
    	  MyLogger.logger.error(e.getMessage());
      }
    }
    if (!isNew)
      return -2;  // file already exist
    else
      return mmkey;
  }

  public int addMMData(String filename, String description, String[] catgcodes,
                       String encrypted_catgcodes[], String encrypt_passwd) {
    int filesize = 0;
    int i = 0;
    int totalsize = 0;
    File f = null;
    FileInputStream fis = null;
    try {
      f = new File(filename);
      fis = new FileInputStream(f);
      filesize = (int) f.length();
      byte buf[] = new byte[filesize];
      MyLogger.logger.debug("Original size:" + filesize);
      i = fis.read(buf, 0, filesize);
      fis.close();

      return addMMData(buf, filename, description, catgcodes,
                       encrypted_catgcodes, encrypt_passwd);
    }
    catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  public int getCatgKey(String catgapp, String catgcode,
                        String catgdesc, int seclvl, String password) throws
      Exception {
    try {
    	MyLogger.logger.debug("getting catgkey ...");
      int catgkey = -1;
      if (seclvl == 0) {
        PreparedStatement pstmt = con.prepareStatement("select catg_key from catg_grp where catg_app  = 'MM' and catg_code = ? and catg_seclvl = 0 and access_lvl <= ?");
        pstmt.setObject(1, catgcode.getBytes(this.getCharSet()));
        pstmt.setInt(2, userInfo.getAccessLevel());
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
          catgkey = rs.getInt(1);
        }
        rs.close();
        pstmt.close();
      }
      else {
        PreparedStatement pstmt = con.prepareStatement("select catg_key from catg_grp where catg_app = 'MM' and catg_code = ? and catg_seclvl > 0 and access_lvl <= ?");
        byte[] ciphertext = ds.encryptWithPBE(password,catgcode.getBytes(this.getCharSet()));
        pstmt.setObject(1, ciphertext);
        pstmt.setInt(2, userInfo.getAccessLevel());
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
          catgkey = rs.getInt(1);
        }
        rs.close();
        pstmt.close();
      }

      return catgkey;
    }
    catch (Exception e) {
      e.printStackTrace();
      throw new Exception();
    }
  }



  public Vector getDupMMKey(String filename, int len) {
    int imgcount = 0;
    imgidx_iterator = -1;
    int ii = 0;
    Vector v = new Vector();
    String sql = "select mm_key from mm..mm_datastore where mm_filename= '" +
        filename + "' and datalength(mm_data) = " + String.valueOf(len);
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = null;
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        v.add(String.valueOf(rs.getInt(1)));
      }
    }
    catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return v;
  }

//-------------------------------------------------
//	populate Image index
//
//-------------------------------------------------
  public int populateImgIdx(int grp_from, int grp_to) {
    int imgcount = 0;
    imgidx_iterator = -1;
    int ii = 0;
    String sql = "select count(*) from mm..mm_datastore where mm_catg between " +
        String.valueOf(grp_from) + " and " + String.valueOf(grp_to) +
        " and mm_data is not null";
    imgcount = db.sqlExecINT(sql);
    imgidx = new int[imgcount];
    sql = "select mm_key from mm..mm_datastore where mm_catg between " +
        String.valueOf(grp_from) + " and " + String.valueOf(grp_to) +
        " and mm_data is not null order by mm_key ";
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = null;
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        imgidx[ii++] = rs.getInt(1);
      }
    }
    catch (SQLException e) {
      MyLogger.logger.error(e.getMessage());
    }

    return ii;
  }

//-------------------------------------------------
//	Retrieve MM binary
//
//-------------------------------------------------
  public byte[] readMMData(int mmkey, String encrypt_passwd) {
    return readMMData(mmkey, encrypt_passwd, null);
  }

  public byte[] readMMData(int mmkey, String encrypt_passwd, String preview) {
    mmdata out = readMMObj(mmkey, encrypt_passwd, preview);
    String s = null;
    try {
      s = new String(out.getName(), this.getCharSet());
    }
    catch (Exception e) {
      s = new String(out.getName());
    }
    MyLogger.logger.debug("Filename=" + s+" size="+out.getSize());

    return out.getData();
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
      MyLogger.logger.error(e.getMessage());
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

  public mmdata readMMObj(int mmkey, String encrypt_passwd, String preview) {
    if (!checkPrivilege(mmkey)) {
    	MyLogger.logger.warn("** Insufficient privilege to read mm_key = " + mmkey + " !");
      return null;
    }
    mmdata out = new mmdata();
    int seclvl = 0;
    if (encrypt_passwd != null && !encrypt_passwd.trim().equals("")) {
      seclvl = 1;
    }
    boolean _preview = true;

    int iconsize = 0;
//20041201 if preview size is larger than pre-rendered icon size, the image will be re-rendered.
    if (preview == null || preview.trim().equals("")) {
      _preview = false;
    }
    else {
      try {
        iconsize = Integer.parseInt(preview);
        if (iconsize > _DefaultThumbnailSize) {
          _preview = false;
        }
        else {
          if (iconsize!=-1)
            iconsize = _DefaultThumbnailSize;
        }
      }
      catch (Exception e) {
        iconsize = _DefaultThumbnailSize;
      }
    }
    if (iconsize==-1) 
    	_preview = false;
    else if (_preview && !hasPreview(mmkey, _DefaultThumbnailSize)) {
      _preview = false;

    }

    if (con == null) {
      return null;
    }

    byte b[] = null;
    if (!db.isConnected()) {
      return null;
    }
    long _t0=System.currentTimeMillis();

    PreparedStatement pstmt_update = null;
    int rowsec = 0;
    String sql = null;
    if (iconsize==-1) {
        sql = "select mm_name,length(mm_data),null,mm_seclvl,mm_desc,create_dte from mm_datastore where mm_key = " +
        Integer.toString(mmkey);
    }
    else if (!_preview) {
      sql = "select mm_name,length(mm_data),mm_data,mm_seclvl,mm_desc,create_dte from mm_datastore where mm_key = " +
          Integer.toString(mmkey);
    }
    else {
      sql = "select a.mm_name,length(b.mm_data),b.mm_data,a.mm_seclvl,a.mm_desc,a.create_dte from mm_datastore a,mm_datacache b where a.mm_key = b.mm_key and a.mm_key = " +  Integer.toString(mmkey) +" and b.mm_cache="+String.valueOf(_DefaultThumbnailSize);
    }
    try {
    //  System.out.println(sql);
      pstmt_update = con.prepareStatement(sql);
      ResultSet rs = pstmt_update.executeQuery();
      byte[] mm_desc = null;
      byte[] mm_file = null;
      int len = 0;
      while (rs.next()) {
        mm_file = rs.getBytes(1);
        len = rs.getInt(2);
        if (iconsize!=-1)
          b = rs.getBytes(3);
        rowsec = rs.getInt(4);
        mm_desc = rs.getBytes(5);
        java.sql.Date crdate = rs.getDate(6);
        out.setSize(len);

        if (rowsec > 0) {
          if (b!=null)	
            b = ds.decryptWithPBE(encrypt_passwd, b);
          out.setEncrypted(true);
          out.setDesc(ds.decryptWithPBE(encrypt_passwd, mm_desc));
          out.setName(ds.decryptWithPBE(encrypt_passwd, mm_file));
        }
        else {
          out.setEncrypted(false);
          out.setDesc(mm_desc);
          out.setName(mm_file);
        }
        out.setData(b);
        
        _totalReadTime+=System.currentTimeMillis()-_t0;
        _totalReadByte+=len;
        

      }
      /* end while(next()) */
      if (len <= 10) {
        deleteDataCache(mmkey);
      }
      this.printBenchMark();
    }
    catch (SQLException e) {
      MyLogger.logger.error("JDBC:" + e.getMessage() + "\n" + e.getSQLState());
      return null;
    }
    catch (Exception e) {
      MyLogger.logger.error("IO Exception" + e.getMessage());
      e.printStackTrace();
      return null;
    }
    if (b!=null && !hasPreview(mmkey, _DefaultThumbnailSize)) {
      addDataCache(mmkey, b, _DefaultThumbnailSize, encrypt_passwd);
      // 050318
    }
    //updateViewCnt(mmkey);
    out.setCategory(getFileCategory(mmkey,encrypt_passwd));
    return out;
  }




  public int addCatgKey(String catgapp, String catgcode, String catgdesc, int seclvl, String password) throws   Exception {
	  
    int newkey = 0;
    newkey = this.getNextCatgKey();
// add column :access_lvl
    String sql = "insert into catg_grp (catg_app,catg_key,catg_code,catg_desc,catg_seclvl,last_upd_by,last_upd_dtm,access_lvl)" +
        " values (?,?,?,?,?,'sys',curdate(),1)";
      MyLogger.logger.debug("Adding new key mm catg key for catcode :" + catgcode);

      PreparedStatement pstmt = con.prepareStatement(sql);
      pstmt.setString(1, "MM");
      pstmt.setInt(2, newkey);
      if (seclvl == 0) {
        pstmt.setObject(3, catgcode.getBytes(this.getCharSet()));
        pstmt.setObject(4, catgdesc.getBytes(this.getCharSet()));
      }
      else {
        pstmt.setObject(3,
                        ds.encryptWithPBE(password,
                                          catgcode.getBytes(this.getCharSet())));
        pstmt.setObject(4,
                        ds.encryptWithPBE(password,
                                          catgdesc.getBytes(this.getCharSet())));
      }
      pstmt.setInt(5, seclvl);
      pstmt.executeUpdate();
      pstmt.close();
      this.forceTreeRefresh();
      return newkey;
  }

  public void updateCatgKey(int catgkey, String catgapp, String catgcode,
                            String catgdesc, int seclvl, String password) throws
      Exception {
    String sql = "update catg_grp set catg_code = ?, catg_desc = ?, last_upd_by = 'sys', last_upd_dtm = curdate() " +
        "where catg_app = ? and catg_key = ? and catg_seclvl = ?";

    int newkey = 0;
      PreparedStatement pstmt = con.prepareStatement(sql);
      if (seclvl == 0) {
        pstmt.setObject(1, catgcode.getBytes(this.getCharSet()));
        pstmt.setObject(2, catgdesc.getBytes(this.getCharSet()));
      }
      else {
        pstmt.setObject(1,
                        ds.encryptWithPBE(password,
                                          catgcode.getBytes(this.getCharSet())));
        pstmt.setObject(2,
                        ds.encryptWithPBE(password,
                                          catgdesc.getBytes(this.getCharSet())));
        // update cache
        removeCache_catgcode(password, catgkey);
        removeCache_catgdesc(password, catgkey);
      }
      pstmt.setString(3, "MM");
      pstmt.setInt(4, catgkey);
      pstmt.setInt(5, seclvl);
      pstmt.executeUpdate();
      pstmt.close();
      this.forceTreeRefresh();
    
  }
  private Vector getFileGroupsByType(int fkey, String encrypt_passwd,int type) {
	    String _enc = ">0";
	    if (encrypt_passwd == null || encrypt_passwd.trim().equals("")) {
	      _enc = "=0";

	    }
	    long t0 = System.currentTimeMillis();
	    PreparedStatement pstmt = null;
	    Vector v = new Vector();
	    String sql = "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,0 from catg_grp a where  " +
	        "a.catg_seclvl " + _enc + " and a.access_lvl <= " +
	        userInfo.getAccessLevel() + " and " +
	        "not exists " +
	        "(select 1 from catg_grpitem where item_key = ? and catg_key = a.catg_key and catg_app = a.catg_app) " +
	        "union " +
	        "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,1 from catg_grp a, catg_grpitem b " +
	        " where " +
	        "a.catg_app = b.catg_app and " +
	        "a.catg_key = b.catg_key and " +
	        "a.catg_seclvl " + _enc + " and " +
	        "b.item_key = ? order by 2,1,3,4  ";
        if (type==2) {
        	sql = 
	        "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,1 from catg_grp a, catg_grpitem b " +
	        " where " +
	        "a.catg_app = b.catg_app and " +
	        "a.catg_key = b.catg_key and " +
	        "a.catg_seclvl " + _enc + " and b.item_key = ? and " +  // two inparms as type 1 sql
	        "b.item_key = ? order by 2,1,3,4  ";       	
        }
	    ResultSet rs = null;
	    try {
	      pstmt = con.prepareStatement(sql);
	      pstmt.setInt(1, fkey);
	      pstmt.setInt(2, fkey);
	      rs = pstmt.executeQuery();
	      String sraw = null;
	      String sdesc = null;
	      String ssec = null;
	      while (rs.next()) {
	        byte[] raw = (byte[]) rs.getObject(1);
	        byte[] desc = (byte[]) rs.getObject(2);
	        int catgkey = rs.getInt(3);
	        int seclvl = rs.getInt(4);
	        int member = rs.getInt(5);

	        if (seclvl == 0) {
	          sraw = new String(raw, this.getCharSet());
	          sdesc = new String(desc, this.getCharSet());
	        }
	        else {
	          if (encrypt_passwd != null &&
	              !encrypt_passwd.trim().equalsIgnoreCase("")) {
	            try {
//	            sraw=new String(ds.decryptWithPBE(encrypt_passwd,raw));
//	            sdesc=new String(ds.decryptWithPBE(encrypt_passwd,desc));
	              sraw = fastDecrypt_catgcode(encrypt_passwd, catgkey, raw);
	              sdesc = fastDecrypt_catgdesc(encrypt_passwd, catgkey, desc);

	            }
	            catch (Exception e) {
	              sraw = null;
	              sdesc = null;
	            }
	          }
	        }
	        if (sraw != null) {
	          Vector rowvector = new Vector();
	          rowvector.add(sraw);
	          rowvector.add(sdesc);
	          rowvector.add(new Integer(catgkey));
	          if (seclvl == 0) {
	            rowvector.add(new Boolean(false));
	          }
	          else {
	            rowvector.add(new Boolean(true));
	          }
	          if (member == 0) {
	            rowvector.add(new Boolean(false));
	          }
	          else {
	            rowvector.add(new Boolean(true));
	          }
	          v.add(rowvector);
	        }
	      }
	      rs.close();
//	    pstmt_getfilegroup.close();

	      long t1 = System.currentTimeMillis() - t0;
	      MyLogger.logger.info("Time taken for getFileGroup: " + t1);

	      return v;
	    }
	    catch (Exception e) {
	    	MyLogger.logger.error(e.getMessage());
	      e.printStackTrace();
	      return null;
	    }
	  }
  public Vector getFileGroups(int fkey, String encrypt_passwd) {
	  // get all file groups
     return getFileGroupsByType(fkey,encrypt_passwd,1);
  }
  
  public Vector getFileCategory(int fkey,String encrypt_passwd) {
	  // get only associated file groups
	  return getFileGroupsByType(fkey,encrypt_passwd,2);
  }

  public boolean updateFileGroups(int fkey, Vector oldgrp, Vector newgrp) {
    try {
      con.setAutoCommit(false);
      Statement stmt = con.createStatement();

      for (int i=0;i<oldgrp.size();i++) {
      //    System.out.println(oldgrp.get(i));
          Integer I = (Integer)oldgrp.get(i);
          String ckey=I.toString();
          String sql="delete from catg_grpitem where catg_app = 'MM' and catg_key = "+ckey+" and item_key = "+fkey ;
          stmt.executeUpdate(sql);
        }
//      String sql =
//          "delete from catg_grpitem where catg_app = 'MM' and item_key = " +    fkey;
//      stmt.executeUpdate(sql);

      for (int j = 0; j < newgrp.size(); j++) {
        System.out.println(newgrp.get(j));
        Integer J = (Integer) newgrp.get(j);
        String ckey = J.toString();
        String _sql="select count(*) from catg_grpitem where catg_app='MM' and catg_key="+ckey+" and item_key="+fkey;
        ResultSet _rs=stmt.executeQuery(_sql);
        _rs.next();
        int _cnt=_rs.getInt(1);
        _rs.close();
        if (_cnt>0) {
        	MyLogger.logger.info("File already exist in this group !");
        } else {
          String sql = "insert into catg_grpitem values ('MM'," + ckey + "," + fkey +",'sys',curdate(),0,0)";
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

  public int registerMM(String filename, int filestatus, int filesize,
                        byte[] filesig) throws Exception {
    int regkey = 0;
    String sql = "insert into mm_register (mm_key,mm_name,mm_size,mm_sig,mm_status,create_dte) values (?,?,?,?,?,curdate())";
    regkey = this.getNextRegKey();
    PreparedStatement pstmt = con.prepareStatement(sql);
    pstmt.setInt(1, regkey);
    pstmt.setBytes(2, filename.getBytes(this.getCharSet()));
    pstmt.setInt(3, filesize);
    pstmt.setBytes(4, filesig);
    pstmt.setInt(5, filestatus);
    pstmt.executeUpdate();
    return regkey;
  }

  public boolean isMMRegistered(int filesize, byte[] digest) throws Exception {
    PreparedStatement pstmt = con.prepareStatement(
        "select count(*) from mm_register where mm_size=? and mm_sig = ?");
    pstmt.setInt(1, filesize);
    pstmt.setBytes(2, digest);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    int noofmatch = rs.getInt(1);
    rs.close();
    pstmt.close();

    if (noofmatch <= 0) {
      return false;
    }
    else {
      return true;
    }
  }

  /* end of isMMRegistered */



  public void getTreeRelation() {
//those not belongs to any group will be add to the root tree in the union statement below
	  
    String sql = "  select a.catg_key,a.catg_code,a.catg_desc,a.catg_seclvl, " +
        "  b.catg_key,b.catg_code,b.catg_desc,b.catg_seclvl " +
        "  from catg_relation r,catg_grp a,catg_grp b " +
        "  where r.catg_key = a.catg_key and r.child_key = b.catg_key " +
        "  and r.catg_app = a.catg_app and r.catg_app = b.catg_app  " +
        "  union " +
        "  select c.catg_key,c.catg_code,c.catg_desc,c.catg_seclvl, " +
        "  c.catg_key,c.catg_code,c.catg_desc,c.catg_seclvl " +
        "  from catg_grp c where " +
        "  not exists (select 1 from catg_relation where child_key = c.catg_key) ";

    Statement stmt;
    ResultSet rs;
    try {
      
      stmt = con.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next()) {

      }
    }
    catch (Exception e) {
       MyLogger.logger.error(e.getMessage());
       MyLogger.logger.error("sql>"+sql);
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
	      this.forceTreeRefresh();	      
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
  
  public boolean updateCatgRelation(int parentkey, Vector childgrp) {
	childgrp = VectorHelper.removeDuplicates(childgrp);  
    try {
    	
      con.setAutoCommit(false);
      Statement stmt = con.createStatement();
      String sql =
          "delete from catg_relation where catg_app = 'MM' and catg_key = " +
          parentkey;
      stmt.executeUpdate(sql);
      for (int j = 0; j < childgrp.size(); j++) {
        Integer J = (Integer) childgrp.get(j);
        String ckey = J.toString();
        sql = "insert into catg_relation values ('MM'," + parentkey + "," +
            ckey + ")";
        stmt.executeUpdate(sql);
      }
      con.commit();
      stmt.close();
      con.setAutoCommit(true);
      this.forceTreeRefresh();
      return true;
    }
    catch (Exception e) {
      MyLogger.logger.error(e.getMessage());
      try {
        con.rollback();
        con.setAutoCommit(true);
      }
      catch (Exception ee) {      MyLogger.logger.error(ee.getMessage());}
      e.printStackTrace();
      return false;
    }
  }



  public static String version() {
    return "MYSQL_MMQUERY:2019-05-04";
  }

}
