
/*
 *  when    what                         
 * 010619  Add to package com.solar.mmquery  
 * 050119  Add disconnect(),reset()     
 * 070529  Add Logger
 *                    
*/

package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import java.io.FileInputStream;
import com.solar.utility.*;

public abstract class DBBridge {
  Properties connect_props = null;
  String s_status;
  Connection con=null;
  Statement stmt=null;
  UserProfileBean userInfo = null;
  boolean b_connect = false;
  PreparedStatement stmt_negolog=null;
  int ref_no=-1;
  String sqlerrtext=null;
  int sqlcode=0;

  public DBBridge() {
    super();
  }
  protected void reset() {
    connect_props = null;
    s_status = null;
    stmt=null;
    b_connect=false;
    stmt_negolog=null;
    ref_no=-1;
    sqlerrtext=null;
    sqlcode=0;
    userInfo = null;
  }
  public boolean isConnected() {
    return b_connect;
  }

  public int getSqlCode() {
    return sqlcode;
  }

  public Connection getConnection() {
    return con;
  }

  public abstract void disconnect() ;
/*	                                         */
/*	                                         */
/*	      Initialize JDBC driver             */
/*	                                         */
/*	                                         */
  public int connectDB() {
    return initConnection();
  }

  public boolean setLoginParms(Properties connect_props) {
    userInfo = new UserProfileBean();
    userInfo.setUserName(connect_props.getProperty("username"));
    userInfo.setUserPass(connect_props.getProperty("password"));

    this.connect_props = connect_props;
    return true;
  }

  public boolean setLoginParms(String inifile) {
    userInfo = new UserProfileBean();
    connect_props = new Properties();
    try {
      connect_props.load(new FileInputStream(inifile));
    } catch (Exception e) {
      MyLogger.logger.error("Unable to load ini file "+inifile);
      return false;
    }
    userInfo.setUserName(connect_props.getProperty("username"));
    userInfo.setUserPass(connect_props.getProperty("password"));

    return true;
  }
  public abstract int initConnection() ;
  public abstract mm_query createMMQuery();

//-------------------------------------------
// execute an adhoc SQL statement
//-------------------------------------------
  public int sqlExec(String sql) {
    if (!b_connect) return -1;
    int rc = -1;
    try {
      stmt=con.createStatement();
      MyLogger.logger.debug("sqlExecUpdate:"+sql);
      rc= stmt.executeUpdate(sql);
      stmt.close();
      return rc;
    } catch (SQLException e) {
      sqlerrtext=new String(e.getMessage());
      sqlerrtext=sqlerrtext+"\n"+sql;
      MyLogger.logger.error(sqlerrtext);
      rc = -1;
    }
    return rc;
  }

//-------------------------------------------
// execute an adhoc SQL statement
// and return an INTEGER
//-------------------------------------------
public int sqlExecINT(String sql) {
  ResultSet rs=null;
  sqlerrtext=null;
  sqlcode=-1;
  if (!b_connect) return -1;
    int rc = -1;
    try {
      stmt=con.createStatement();
      MyLogger.logger.debug("sqlExecINT:"+sql);
      rs=stmt.executeQuery(sql);
      while(rs.next()) {
        rc= rs.getInt(1);
      }
      rs.close();
      stmt.close();
      return rc;
    } catch (SQLException e) {
      sqlerrtext=new String(e.getMessage());
      sqlerrtext=sqlerrtext+"\n"+sql;
      MyLogger.logger.error(sqlerrtext);
      sqlcode = -1;
    }
    return sqlcode;
  }

//-------------------------------------------
// execute an adhoc SQL statement
// and return a vector of OBJECT
//-------------------------------------------
public Vector execSQLRetVector(String sql) {
  ResultSet rs=null;
  sqlerrtext=null;
  sqlcode=0;
  Vector v=null;
  if (!b_connect) return null;
  int rc = 1;
  try {
    stmt=con.createStatement();
//    System.out.println("sqlExec:"+sql);
    rs=stmt.executeQuery(sql);
    while(rs.next()) {
      if (v==null)
        v = new Vector();
      v.add(rs.getString(1));
    }
    rs.close();
    stmt.close();
    return v;
  } catch (SQLException e) {
    sqlerrtext=new String(e.getMessage());
    sqlerrtext="sqlExec:"+sqlerrtext+"\n"+sql;
    MyLogger.logger.error(sqlerrtext);
    e.printStackTrace();
    sqlcode = -1;
  }
  return null;
}



//-------------------------------------------
// execute an adhoc SQL statement
// and dump result to standard output
//-------------------------------------------
  public void dumpQuery(String sql) {
    try {
      Statement stmt=con.createStatement();
      ResultSet rs=stmt.executeQuery(sql);
      ResultSetMetaData meta=rs.getMetaData();
      int	 noofcols=meta.getColumnCount();
      String[]	 colname = new  String[noofcols];
      for (int i=0;i<noofcols;i++) {
        colname[i]=meta.getColumnLabel(i+1);
        MyLogger.logger.info(colname[i]+", ");
      }
      
      String record[]=new String[noofcols];
      while (rs.next()){
        for (int i=0;i<noofcols;i++)
          record[i]=rs.getString(i+1);

        for (int i=0;i<noofcols;i++)
        	MyLogger.logger.info(record[i]+", ");

      }
      rs.close();
      stmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

public String getErrText() {
  return sqlerrtext;
}

   public UserProfileBean getUserInfo() {
     return userInfo;
   }
}
