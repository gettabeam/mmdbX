/*
Usage:

DBBridge dbconnect = new DBBridge();
dbconnect.connectDB();
mq = new mm_query(dbconnect);
mq.mm_init();
imgicon=mq.retrievePrevImg();
imgicon=mq.retrieveCurImg();
imgicon=mq.retrieveNextImg();

when    what
040822  Refactor the code
041205  relocated hasPreview()
041220
051115
070529  Add getDBBridge()
*/

package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.Vector;
import java.io.*;
import javax.swing.*;
import java.math.BigDecimal;
//import oracle.jdbc.driver.*;
//import oracle.sql.*;
import java.util.*;

import com.solar.utility.*;
import com.solar.imgproc.*;



public abstract class mm_query {
  public static String DEFAULT_TREE="defaulttree";
  public static final int FILENOTFOUND=-1;
  public static final int DUPFILEFOUND=-2;
  public static final int DBNOTCONNECTED=-3;
  public static final int _DefaultThumbnailSize=500;
  public static final int NATURAL_ORDER=1;
  public static final int FREQUENCY_ORDER=2;
  public static final int CUSTOM_ORDER=3;
  long _last_tree_refresh_time = 0;
  protected transient Vector processStatusListeners;
  PrintWriter out = null;
  UserProfileBean userInfo = null;
  mmTree _tree = null;
  
  MergeSort  CatgCodeSort = new MergeSort() {
	

    public int compareElementsAt(int a, int b) {
      Vector v1 = (Vector)toSort[a];
      Vector v2 = (Vector)toSort[b];
      boolean b1 = ((Boolean)v1.get(3)).booleanValue();
      boolean b2 = ((Boolean)v2.get(3)).booleanValue();
      if (b1==b2)
        return ((String)v1.get(0)).compareTo((String)v2.get(0));
      if (b1==false && b2==true)
        return -1;
      else
        return 1;

    }
  };


  DBBridge db = null;
  Connection con = null;
  int imgidx[]=null;
  int imgidx_iterator=0;
  Vector duplist=null;

  Hashtable cache_catgcode = new Hashtable(200);
  Hashtable cache_catgdesc = new Hashtable(200);

  FileDigest filedigest = new FileDigest();
  DataSecure ds = new DataSecure();
  private String passwd = "iameric";

//-------------------------------------------------
//	Default Public Constructor
//
//-------------------------------------------------
  public mm_query() {
  	/* sub-classes need this constructor !! */
  	super();
  }

  public mm_query(DBBridge db) {
    this.db = db;
    if (!db.isConnected()) return;
    con = db.getConnection();
  }

  public abstract int addMMData(String filepath,String desc, String[] catg,String catg2[],String encpasswd);
  public abstract int addMMData(byte[] buf,String filepath,String desc, String[] catg,String catg2[],String encpasswd);
  public abstract int addDataCache(int mmkey, byte[] indata, int size,String encrypt_passwd);
  protected abstract int updateMMData(int mmkey,String filename) ;
  public abstract int populateImgIdx(int grp_from,int grp_to);
  public abstract void printDBInfo();
  public abstract Vector getAllCatgGrp(String passwd);
  public abstract byte[] readMMData(int ikey,String s);
  public abstract byte[] readMMData(int ikey,String s,String ss);
//  public abstract boolean hasPreview(int mmkey,int size);
  protected abstract int getGroupItems(byte[] code, Vector v) ;

  protected abstract int getNextMMKey();
  public abstract boolean updateCatgRelation(int parentkey, Vector childgrp) ;
  public abstract boolean updateCatgRelation2(int x, Vector a, Vector b) ;
  public abstract int addCatgKey(String catgapp,String catgcode,String catgdesc, int seclvl,String password)  throws Exception ;
  public abstract void updateCatgKey(int catgkey,String catgapp,String catgcode, String catgdesc, int seclvl,String password) throws Exception ;
  public abstract Vector getFileGroups(int fkey,String encrypt_passwd) ;
  public abstract boolean updateFileGroups(int fkey, Vector oldgrp, Vector newgrp);
  public abstract int registerMM(String filename,int filestatus,int filesize, byte[] filesig) throws Exception;
  public abstract boolean isMMRegistered(int filesize, byte[] digest) throws Exception;
  public abstract void printBenchMark();
  public abstract mmdata readMMObj(int itemkey,String passwd,String preview);
  public abstract Vector getFileCategory(int mmkey,String encrypt_passwd);
  public int mm_init() {
    int i=0;
    int mmkey=0;
    if (!db.isConnected()) return -1;
//    System.out.println("Number of image loaded="+populateImgIdx(0,1000));
//    System.out.println("Number of image in array="+imgidx.length);
    return i;
  }

//-------------------------------------------------
//	get Next Image
//
//-------------------------------------------------

  public byte[] readMMData(int key) {
    return readMMData(key,null);
  }
  protected ImageIcon returnImageIcon() {
      return retrieveMMData(imgidx[imgidx_iterator]);
  }

  public DBBridge getDBBridge() {
	  return db;
  }
  public String getCharSet() {
	    return "UTF-8";
	  }
  public ImageIcon retrieveNextImg() {
    imgidx_iterator++;
    if (imgidx_iterator>=imgidx.length)
      imgidx_iterator=0;
    return returnImageIcon();
  }

  public ImageIcon retrievePrevImg() {
    imgidx_iterator--;
    if (imgidx_iterator<0)
      imgidx_iterator=imgidx.length-1;
    return returnImageIcon();
  }
  public ImageIcon retrieveCurImg() {
  	return returnImageIcon();
  }
//-------------------------------------------------
//	get Next Image in byte[]
//
//-------------------------------------------------
  public byte[] retrieveNextImgByte() {

    imgidx_iterator++;
    if (imgidx_iterator>=imgidx.length)
      imgidx_iterator=0;
    return retrieveMMByteData(imgidx[imgidx_iterator]);
  }

  public byte[] retrievePrevImgByte() {
    imgidx_iterator--;
    if (imgidx_iterator<0)
      imgidx_iterator=imgidx.length-1;
    return retrieveMMByteData(imgidx[imgidx_iterator]);
  }

  public byte[] retrieveCurImgByte() {
    return retrieveMMByteData(imgidx[imgidx_iterator]);
  }

//-------------------------------------------------
//	Retrieve MM data
//
//-------------------------------------------------
  public ImageIcon  retrieveMMData(int mmkey) {

    return new ImageIcon(this.retrieveMMByteData(mmkey));
  }

  public int addMMData(String filename,String description,String catgcode) {
    String catgcodes[] = new String[1];
    catgcodes[0]=catgcode;
    return addMMData(filename,description,catgcodes);
  }

  public int addMMData(String filename,String description,String[] catgcodes) {
    return addMMData(filename,description,catgcodes,null,"");
  }

  public int addMMData(String filename,String description,String[] catgcodes,String passwd) {
    return addMMData(filename,description,catgcodes,null,passwd);
  }

  public byte[] retrieveMMByteData(int mmkey) {
    return readMMData(mmkey);
  }

  public int importFile(String filepath,String desc,String catgcode) {
    int mmkey=0;
    mmkey=addMMData(filepath,desc,catgcode);
    return mmkey;
  }

  public int importFile(String filepath,int catg) {
    return importFile(filepath,"New File",String.valueOf(catg));
  }

  public int populateImgIdx(int grp) {
    /* for backward compatibale purpose */
    return populateImgIdx(grp,grp);
  }

  public Vector populateImgIdx(String catgcode) {
    String[] catgs = new String[1];
    catgs[0] = catgcode;
    return populateImgIdx(catgs);
  }

  public Vector getAllCatgGrp() {
    return getAllCatgGrp("");
  }
//
// eRic@20030119: fast decrypt using cache
//
  protected void removeCache_catgcode(String encrypt_passwd,int catgkey) {
    String key = String.valueOf(catgkey)+":"+encrypt_passwd;
    cache_catgcode.remove(key);
  }

  protected void removeCache_catgdesc(String encrypt_passwd,int catgkey) {
    String key = String.valueOf(catgkey)+":"+encrypt_passwd;
    cache_catgdesc.remove(key);
  }

  protected String fastDecrypt_catgcode(String encrypt_passwd,int catgkey,byte[] desc) {
    return fastDecrypt(cache_catgcode,encrypt_passwd,catgkey,desc);
  }

  protected String fastDecrypt_catgdesc(String encrypt_passwd,int catgkey,byte[] desc) {
    return fastDecrypt(cache_catgdesc,encrypt_passwd,catgkey,desc);
  }

  protected String fastDecrypt(Hashtable cache,String encrypt_passwd,int catgkey,byte[] desc) {
 //   System.out.println("fastDecrypt: this.getCharSet() = "+this.getCharSet());
    String key = String.valueOf(catgkey)+":"+encrypt_passwd;
    String sdesc=null;
    try {
      sdesc = new String(ds.decryptWithPBE(encrypt_passwd, desc), this.getCharSet());
    } catch (Exception e) {
      sdesc = new String(ds.decryptWithPBE(encrypt_passwd, desc));
      MyLogger.logger.error("fastDecrypt: Error in encoding byte arrays using "+this.getCharSet()+"!");
      e.printStackTrace();
    }
//    System.out.println("fastDecrypt: this.getCharSet() = "+this.getCharSet()+" sdesc="+sdesc);
    return sdesc;
  }

/*
  protected String fastDecrypt(Hashtable cache,String encrypt_passwd,int catgkey,byte[] desc) {
    out.println("this.getCharSet() = "+this.getCharSet());
    String key = String.valueOf(catgkey)+":"+encrypt_passwd;
    String sdesc=null;
    Object code=cache.get(key);

    if (code == null) {
      try {
        sdesc = new String(ds.decryptWithPBE(encrypt_passwd, desc), this.getCharSet());
      } catch (Exception e) {
        sdesc = new String(ds.decryptWithPBE(encrypt_passwd, desc));
      }
      cache.put(key,sdesc);
    }
    else {
      sdesc = String.valueOf(code);
    }
    return sdesc;
  }
*/
  public Vector populateImgIdx(String[] catgcodes) {
    Vector v = new Vector();
    for (int i=0;i<catgcodes.length;i++) {

      populateImgIdx(catgcodes[i],v);
      MyLogger.logger.debug("No. of records in "+catgcodes[i]+" = "+v.size());
    }
    imgidx_iterator=0;
    if (v.size()>0) {
      imgidx=new int[v.size()];
      for (int i=0;i<v.size();i++)
        imgidx[i] = Integer.parseInt((String)v.get(i));
    }
    else
      imgidx=null;
    return v;
  }
  /*
     eRic@010814: Added handling for encrypted description
  */

  protected int populateImgIdx(String catgcode,Vector v) {
    int count=0;
    count= getGroupItems(catgcode.getBytes(),v);
    byte[] ciphertext=ds.encryptWithPBE(this.passwd,catgcode.getBytes());
    count+=getGroupItems(ciphertext,v);
    return count;
  }

  public Vector getImgIdx(int[] catgkeys) {
    Vector v = new Vector();
    for (int i=0;i<catgkeys.length;i++)
      populateImgIdx(catgkeys[i],v);

    /* remove duplicated keys */
    Vector vv = new Vector();
    HashSet hs = new HashSet();
    Object o = null;
    for (int i=0;i<v.size();i++) {
      o=v.get(i);
      if (!hs.contains(o)) {
        hs.add(o);
        vv.add(o);
      }
    }
    return vv;
  }

  protected int populateImgIdx(int catgkey,Vector v) {
    return getGroupItems(catgkey,v);
  }

  protected int populateVector(String sql,Vector v) {
    if (v==null)
      return -1;
    int imgcount=0;
    int ii=0;
    try {
      Statement stmt=con.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
    //  System.out.println(sql);
      while(rs.next()) {
        v.add(rs.getString(1));
      }
      rs.close();
      imgcount=v.size();
      stmt.close();
    } catch (Exception e) {
      imgcount=0;
      return 0;
    }
    return imgcount;
  }

  public int chkMMExist(String filename,int status,boolean register) {

    FileDigest fd = new FileDigest();
    byte[] digest = fd.getDigest(filename);
    int filesize= fd.getFileSize();
    int mmkey=0;
    try {

      if (!isMMRegistered(filesize,digest) && register) {
        con.setAutoCommit(false);
        mmkey = registerMM(new File(filename).getName(),status,filesize,digest);
        con.commit();
        con.setAutoCommit(true);
        return 0;
      }
//      System.out.println("checkMMExists:"+noofmatch+" mmkey="+mmkey);
      return 1;
    } catch (Exception e) {
      MyLogger.logger.error("** Error checking duplicated file ");
      e.printStackTrace();
      return -1;
    }
  } /* end of checkMMExist() */

  public int register(String path,boolean register) {
    File f = new File(path);
    File outputfile = new File(f.getAbsolutePath()+File.separator+"movedup.bat");
    try {
      FileOutputStream fos = new FileOutputStream(outputfile);
      out = new PrintWriter(fos);
      int nooffile=registerFilesInDir(path,0,register);
      MyLogger.logger.info("*** "+nooffile+" scanned!");
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }



  public int registerFilesInDir(String path, int status, boolean register) {
    Vector vdir=new Vector();
    File files[] = null;
    int totalfile=0;
    try {
      File dir = new File(path);
      if (dir.isDirectory()) {
        MyLogger.logger.debug("*** processing directory "+path);
        files=dir.listFiles(new mm_filefilter());
      }
      else if (dir.isFile()) {
        files = new File[1];
        files[0] = new File(path);
        totalfile=1;
      }
      if (files==null || files.length<1)
        return 0;
      else {
        for (int i=0;i<files.length;i++) {
          if (files[i].isFile()) {
            totalfile++;
            if (chkMMExist(files[i].getPath(),0,register)>0) {
              out.println("move "+files[i]+" dup");
              String s="-"+files[i].getAbsoluteFile();
              fireDataChanged(new processStatusEvent(s));
            }
            else {
              String s="+"+files[i].getAbsoluteFile();
              fireDataChanged(new processStatusEvent(s));
            }
          } /* end if */
          else if (files[i].isDirectory()) {
            vdir.add(files[i].getAbsolutePath());
          }
        } /* end for */
          /* recursively processing underlying directories */
        for (int j=0;j<vdir.size();j++) {
          totalfile+=registerFilesInDir((String)vdir.get(j),0,register);
        } /* end for */
      } /* end else */
      return totalfile;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public boolean hasPreview(int mmkey,int size) {
//20041205:default size=300, force check size of 300
    size=_DefaultThumbnailSize;

    int count=-1;
    if (!db.isConnected()) return false;
    Statement stmt =null;
    try {
     // con.setAutoCommit(true);
      stmt=con.createStatement();
      String sql="select count(*) from mm_datacache where mm_key = "+String.valueOf(mmkey) +" and mm_cache="+size;
      ResultSet rs = stmt.executeQuery(sql);
      rs.next();
      count=rs.getInt(1);
      rs.close();
      stmt.close();
    } catch (Exception e) {

    }
    if (count>0) 
    	return true;
    else {
      //deleteDataCache(mmkey);	
      return false;
    }
  }
  public boolean regenDataCache(int mmkey) {
	  try {
		MyLogger.logger.debug("DataCache Status:"+hasPreview(mmkey,_DefaultThumbnailSize));  
	    int i=deleteDataCache(mmkey);
	    MyLogger.logger.debug("regenDataCache deleteDataCache()="+i);
	    con.setAutoCommit(true);
		MyLogger.logger.debug("DataCache Status:"+hasPreview(mmkey,_DefaultThumbnailSize));  
	  } catch (Exception e) {
		  MyLogger.logger.error(e.getMessage());  
		  e.printStackTrace();
		  return false;
	  }
	  return true;
  }
  
  public  int deleteDataCache(int mmkey) {
	String sql = "delete from mm_datacache where mm_key = " + mmkey  ;
	return db.sqlExec(sql);
  }  
  
  public synchronized void removeprocessStatusListener(processStatusListener l) {
    if (processStatusListeners != null && processStatusListeners.contains(l)) {
      Vector v = (Vector) processStatusListeners.clone();
      v.removeElement(l);
      processStatusListeners = v;
    }
  }
  public synchronized void addprocessStatusListener(processStatusListener l) {
    Vector v = processStatusListeners == null ? new Vector(2) : (Vector) processStatusListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      processStatusListeners = v;
    }
  }
  protected void fireDataChanged(processStatusEvent e) {
    if (processStatusListeners != null) {
      Vector listeners = processStatusListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((processStatusListener) listeners.elementAt(i)).dataChanged(e);
      }
    }
  }
  
  
  public mmTree getTree() {
    
	if (_tree ==null) {
		_tree = new mmTree();
	} else {
	  if (System.currentTimeMillis()-this._last_tree_refresh_time<1000*60)
	    return _tree;
	}  
	long t0=System.currentTimeMillis();
    String select_criteria_1=null;
/*
    if (magickey==null || magickey.trim().equals(""))
      select_criteria_1=" = 0 ";
    else
      select_criteria_1=" > 0 ";
*/
    select_criteria_1=" >= 0 ";
    String sql=
    "select a.catg_key,a.catg_code,a.catg_desc,a.catg_seclvl, b.catg_key,b.catg_code,b.catg_desc,b.catg_seclvl "+
    "from catg_relation r,catg_grp a, catg_grp b "+
    "where r.catg_key = a.catg_key and "+
    "r.child_key = b.catg_key and "+
    "r.catg_app = a.catg_app and "+
    "r.catg_app = b.catg_app and "+
    "a.catg_seclvl "+select_criteria_1 + " and "+
    "b.catg_seclvl "+select_criteria_1 +
    " union "+
    "select a.catg_key,a.catg_code,a.catg_desc,a.catg_seclvl, "+
    "       a.catg_key,a.catg_code,a.catg_desc,a.catg_seclvl "+
    "from "+
    "catg_grp a "+
    "where not exists  (select 1 from catg_relation r where a.catg_app = r.catg_app and "+
    "r.child_key = a.catg_key) and "+
    "a.catg_seclvl "+select_criteria_1;

    Statement stmt=null;
    ResultSet rs = null;
    try {
      stmt = con.createStatement();
      rs=stmt.executeQuery(sql);
      System.out.println("gentree1>"+(System.currentTimeMillis()-t0));
      _tree.reset();
      int item=0;
      int root_cnt=0;
      while (rs.next()) {
    	item++;
        int pkey = rs.getInt(1);
        byte[] pcode=rs.getBytes(2);
        byte[] pdesc=rs.getBytes(3);
        int psec= rs.getInt(4);
        int ckey = rs.getInt(5);
        byte[] ccode=rs.getBytes(6);
        byte[] cdesc = rs.getBytes(7);
        int csec = rs.getInt(8);
        if (pkey==ckey)
        	root_cnt++;
     //   System.out.println("debug>itemkey="+ckey+", parent key="+pkey);
  
             
        try {
        	
        
        	mmNode pnode = null;
        	TreeNodeBasic _p= _tree.getItem(pkey);
        
        	if (_p==null) {
        		pnode = new mmNode(pkey);
        		pnode.setProperties(pcode,pdesc,psec);
        		_p=pnode;
        	} 
        
        mmNode cnode = null;
        TreeNodeBasic _c = _tree.getItem(ckey);
        
        if (_c==null) {
          cnode = new mmNode(ckey);
          cnode.setProperties(ccode,cdesc,csec);
          _c=cnode;
        }
        
        _tree.addRelation(_p,_c);
        } catch (Exception e) {
        	e.printStackTrace();
        	
        }

      }
      rs.close();
      stmt.close();
     System.out.println("gentree2>"+(System.currentTimeMillis()-t0));      
     MyLogger.logger.debug("mm_query.getTree()>Item count in result set :"+item+" Root Count="+root_cnt);

    } catch (Exception e) {
      return null;
    }

    _tree.initRoots();
    _last_tree_refresh_time=System.currentTimeMillis();
    return _tree;

  }

  
  protected int getGroupItems(int catgkey, Vector v) {
	    String sql = "select b.item_key from catg_grpitem b "+
	                 "where b.catg_app = 'MM' and "+
	                 "b.catg_key=? order by 1";
	    int count=0;
	    try {
	      PreparedStatement pstmt = con.prepareStatement(sql);
	      pstmt.setInt(1,catgkey);
	      ResultSet rs = pstmt.executeQuery();
	      while(rs.next()) {
	        count++;
	        v.add(new Integer(rs.getInt(1)));
//	        System.out.print(count);
	      }
	      rs.close();
	      pstmt.close();
	      return count;
	    } catch (Exception e) {
	      e.printStackTrace();
	      return 0;
	    }
	  }
  
  public Vector getCatgTreeDetails(int rootkey,String encrypt_passwd) {
	  if (encrypt_passwd==null)
		  encrypt_passwd="";
	  this.getTree();
	  Vector v = new Vector();
	  Vector result= new Vector();
	  TreeNodeBasic root=_tree.getItem(rootkey);
	  if (root==null) {
		  MyLogger.logger.error("cannot load mmkey = "+rootkey);
		  return v;
	  }
	  v=_tree.getChildPairs(root);
	  for (int i=0;i<v.size();i++) {
		  Object obj[] = (Object[])v.get(i);
		  mmNode _p = (mmNode)obj[0];
		  mmNode node = (mmNode)obj[1];
		  try {
		  Vector rec = new Vector();
		  if (encrypt_passwd!=null && !encrypt_passwd.trim().equals("") ) {
		//	  System.out.println("##decrypt!");
			  if (node.getSecLvl()==0) {
				  throw new Exception("Skip");
			  }
			  rec.add(new String(ds.decryptWithPBE(encrypt_passwd,node.getCode()),this.getCharSet()));
			  rec.add(new String(ds.decryptWithPBE(encrypt_passwd,node.getDesc()),this.getCharSet()));
		  } else {
	//		  System.out.println("no err");
              if (node.getSecLvl()>0) {
            	  throw new Exception("Skip");
              }
			  rec.add(new String(node.getCode(),this.getCharSet()));
			  rec.add(new String(node.getDesc(),this.getCharSet()));
		  }
		  
		  rec.add(new Integer(node.getItemKey()));
	      if (encrypt_passwd==null || encrypt_passwd.equals(""))
	        rec.add(new Boolean(true));
	      else
	        rec.add(new Boolean(false));
	      
		  rec.add(new Integer(_p.getItemKey()));
		  rec.add(new Boolean(false));
		  result.add(rec);
		  } catch (Exception e) {
		//	  System.out.println("Error Encoding code/desc");
		//	  e.printStackTrace();
		  }
	//	  System.out.println("debug("+i+")>>>"+node.getItemKey()+","+_p.getItemKey());
	  }
	  return result;
  }
  
  private Vector getChildDesc(int parentkey, String encrypt_passwd,Vector result) {	  
	    String sql_filter = "";
	    int count = 0;
	    int seclvl = 0;
	    if (encrypt_passwd == null || encrypt_passwd.trim().equals("")) {
	      seclvl = 0;
	      sql_filter = "=0";
	    }
	    else {
	      seclvl = 1;
	      sql_filter = ">0";
	    }

	    String sql = "select b.item_key,c.mm_name,c.mm_desc,c.mm_size from catg_grpitem b,mm_datastore c " +
	                 "where b.catg_app = 'MM' and c.mm_seclvl " + sql_filter +
	                 " and b.catg_key = " + parentkey + " and " +
	                 " b.item_key = c.mm_key ";

	    ResultSet rs = null;
	    try {
	      Statement stmt = con.createStatement();
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        count++;
	        Object data[] = new Object[4];
	        data[0] = rs.getString(1);
	        if (seclvl == 0) {
	          data[1] = (byte[]) rs.getObject(2);
	          data[2] = (byte[]) rs.getObject(3);
	        }
	        else {
	          data[1] = ds.decryptWithPBE(encrypt_passwd, (byte[]) rs.getObject(2));
	          data[2] = ds.decryptWithPBE(encrypt_passwd, (byte[]) rs.getObject(3));
	        }
	        data[3] = rs.getString(3);
	        result.add(data);
	      }
	      rs.close();
	      stmt.close();
	    } catch (SQLException e) {
	      e.printStackTrace(); 
	    }
	    return result;
	  }
  
  public Vector getCatgTreeImgDesc(int parentkey, String encrypt_passwd) {
	    return getCatgTreeImgDesc(parentkey,encrypt_passwd,mm_query.NATURAL_ORDER);
	  }
	  
	  public Vector getCatgTreeImgDesc(int parentkey, String encrypt_passwd, int order) {	  
		  MyLogger.logger.debug("##getCatgTreeImgDesc:"+parentkey);
		this.getTree();
		Vector _grp = new Vector();
		Vector result = new Vector();
		TreeNodeBasic root=_tree.getItem(parentkey);
		Vector _tmp=_tree.getChilds(root);
		if (_tmp==null)
		  _tmp = new Vector();
		_tmp.add(root);
		MyLogger.logger.debug("getcatgTreeImgDesc>>"+_tmp.size());
		for (int i=0;i<_tmp.size();i++) {
	      TreeNodeBasic _node = (TreeNodeBasic)_tmp.get(i);
		  this.getChildDesc(_node.getItemKey(),encrypt_passwd,result);
		  MyLogger.logger.debug("getCatgTreeIdx>>>"+_node.getItemKey());
		}
		return result;
	  }
	  
	  public Vector getCatgTreeImgIdx(int parentkey) {
		    return getCatgTreeImgIdx(parentkey,mm_query.NATURAL_ORDER);
		  }
		  public Vector getCatgTreeImgIdx(int itemkey, int order) {
			  MyLogger.logger.debug("##getCatgTreeImgIdx("+itemkey+")");  
			  this.getTree();
			  Vector _grp = new Vector();
			  Vector result = new Vector();
			  TreeNodeBasic root=_tree.getItem(itemkey);
			  Vector _tmp=_tree.getChilds(root);
			  if (_tmp==null)
				  _tmp = new Vector();
			  _tmp.add(root);
			  MyLogger.logger.debug("getcatgTreeIdx>>"+_tmp.size());
			   for (int i=0;i<_tmp.size();i++) {
					 
					 TreeNodeBasic _node = (TreeNodeBasic)_tmp.get(i);
					 this.getGroupItems(_node.getItemKey(),result);
					 MyLogger.logger.debug("getCatgTreeIdx>>>"+_node.getItemKey());
					 
			   }
			  
			  return result;
		    
		  }
		  public Vector getCatgTreeRoot() {
		      String sql = "select distinct a.catg_key from catg_grp a where not exists (select 1 from catg_relation where child_key = a.catg_key)";

		    return db.execSQLRetVector(sql);
		  }

		  public Vector getChildGrp(int parentkey,String encrypt_passwd) {
		    Vector v = new Vector();
		    String ss="";
		    if (encrypt_passwd == null || encrypt_passwd.trim().equals(""))
		       ss= " and a.catg_seclvl = 0 ";
		    else
		         ss=  " and a.catg_seclvl > 0 ";

		    String sql ="select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,0,0 from catg_grp a "+
		                "where a.catg_app = 'MM' and "+
		                "not exists (select 1 from catg_relation where catg_app = 'MM' and "+
		                "catg_key = "+String.valueOf(parentkey)+" and child_key = a.catg_key) and "+
		                " a.catg_key <> "+String.valueOf(parentkey) +ss+" union "+
		                "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,0,1 from catg_grp a,catg_relation b "+
		                "where a.catg_app = 'MM' and a.catg_app = b.catg_app and b.child_key = a.catg_key and "+
		                "b.catg_key = "+String.valueOf(parentkey) +ss;

		    ResultSet rs=null;
		    try {
		    Statement stmt = con.createStatement();
		    rs = stmt.executeQuery(sql);
		    String sraw=null;
		    String sdesc = null;
		    String ssec = null;
		    while (rs.next()) {
		      byte[] raw = (byte[])rs.getObject(1);
		      byte[] desc=(byte[])rs.getObject(2);
		      int catgkey = rs.getInt(3);
		      int seclvl = rs.getInt(4);
		      int total = rs.getInt(5);
		      int selected = rs.getInt(6);

		      if (seclvl==0) {
		        sraw=new String(raw,this.getCharSet());
		        sdesc = new String(desc,this.getCharSet());
		      }
		      else {
		        if (encrypt_passwd != null && !encrypt_passwd.trim().equalsIgnoreCase("")) {
		          try {
		              sraw = fastDecrypt_catgcode(encrypt_passwd,catgkey,raw);
		              sdesc = fastDecrypt_catgdesc(encrypt_passwd,catgkey,desc);

		          } catch (Exception e) {
		            sraw=null;
		            sdesc=null;
		          }
		        }
		      }
		      if (sraw!=null) {
		        Vector rowvector = new Vector();
		        rowvector.add(sraw);
		        rowvector.add(sdesc);
		        rowvector.add(new Integer(catgkey));
		        if (seclvl == 0)
		          rowvector.add(new Boolean(false));
		        else
		          rowvector.add(new Boolean(true));
		        rowvector.add(new Integer(total));
		        if (selected == 0)
		          rowvector.add(new Boolean(false));
		        else
		          rowvector.add(new Boolean(true));
		        v.add(rowvector);
		      }
		    }
		    rs.close();
		    stmt.close();

		    Object[] or = v.toArray();
		    CatgCodeSort.sort(or);
		    v.clear();
		    for (int i=0;i<or.length;i++)
		      v.add(or[i]);

		    return v;
		    } catch (Exception e) {
		      e.printStackTrace();
		      return null;
		    }
		  }
		  
		  public Vector getCatgGrpDetails(String encrypt_passwd) {
			  MyLogger.logger.debug("getCatgGrpDetails> acclvl="+userInfo.getAccessLevel());
			    Vector v = new Vector();
			    int sec_lvl = 0;
			    if (encrypt_passwd == null || encrypt_passwd.trim().equals("")) {
			      sec_lvl = 0;
			    }
			    else {
			      sec_lvl = 1;
			    }
			    String sql = "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl, count(*) from catg_grp a, catg_grpitem b where " +
			        "a.catg_app = 'MM' and a.catg_key = b.catg_key and a.catg_seclvl = " +
			        sec_lvl + " and a.access_lvl <=" + userInfo.getAccessLevel() +
			        " group by a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl" +
			        " union " +
			        "select a.catg_code,a.catg_desc,a.catg_key,a.catg_seclvl,0 from catg_grp a " +
			        "where a.catg_app = 'MM' and a.catg_seclvl = " + sec_lvl +
			        " and a.access_lvl <=" + userInfo.getAccessLevel() + " and " +
			        "not exists (select 1 from catg_grpitem where catg_key = a.catg_key and catg_app = a.catg_app)";

			 //   System.out.println(sql);
			    ResultSet rs = null;
			    try {
			      Statement stmt = con.createStatement();
			      rs = stmt.executeQuery(sql);
			      String sraw = null;
			      String sdesc = null;
			      String ssec = null;
			      while (rs.next()) {
			        byte[] raw = (byte[]) rs.getObject(1);
			        byte[] desc = (byte[]) rs.getObject(2);
			        int catgkey = rs.getInt(3);
			        int seclvl = rs.getInt(4);
			        int total = rs.getInt(5);

	    	//  System.out.println("## Processing result..."+catgkey);

			        if (seclvl == 0) {
			          sraw = new String(raw, this.getCharSet());
			          sdesc = new String(desc, this.getCharSet());
			        }
			        else {
			          if (encrypt_passwd != null &&
			              !encrypt_passwd.trim().equalsIgnoreCase("")) {
			            try {
			              sraw = fastDecrypt_catgcode(encrypt_passwd, catgkey, raw);
			              sdesc = fastDecrypt_catgdesc(encrypt_passwd, catgkey, desc);
			            }
			            catch (Exception e) {
			              e.printStackTrace();
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
			          rowvector.add(new Integer(total));
			          rowvector.add(new Boolean(false));
			          v.add(rowvector);
			        }
			      } // end while
			      rs.close();
			      stmt.close();
			      Object[] or = v.toArray();
			      CatgCodeSort.sort(or);
			      v.clear();
			      for (int i = 0; i < or.length; i++) {
			        v.add(or[i]);
			      }
			      MyLogger.logger.debug("No. of grp="+v.size());
			      return v;
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      return null;
			    }
			  }

   public void forceTreeRefresh() {
     _last_tree_refresh_time = 0;
   }

   public synchronized int updateViewCnt(int mmkey) {
	    String sql =
	        "update mm_datastore set view_cnt = 0 where view_cnt is null and mm_key = " +
	        mmkey;
	    if (db.sqlExec(sql) != -1) {
	      sql = "update mm_datastore set view_cnt = view_cnt+1 where mm_key =" +
	          mmkey;
	      return db.sqlExec(sql);
	    }
	    return -1;
	  }
   
}





