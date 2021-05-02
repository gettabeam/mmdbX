/*	                                  */
/*      Initialize JDBC driver            */
/*	                                  */
/* When        What                       */
/* 010612      Init DBBridge for MSSQL    */
/*	                                  */


package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.Vector;
public class mssqlDBBridge extends DBBridge{

  public mssqlDBBridge() {
    super();
  }


/*	                                         */
/*	                                         */
/*	      Initialize JDBC driver             */
/*	                                         */
/*	                                         */


  public int initConnection() {
    System.out.println("connectDB: connecting to "+connect_props.get("datasource").toString());
    System.out.println("connectDB: using driver "+connect_props.get("jdbcdriver").toString());
    System.out.println("connectDB: "+connect_props.get("username").toString()+"("+connect_props.get("password").toString()+")");
    b_connect = true;
    int rc = 0;

    try {
      Class.forName(connect_props.get("jdbcdriver").toString());
    } catch (Exception e) {
      b_connect=false;
      System.out.println("JDBC driver Exception:"+e.getMessage());
    }
    if (!b_connect)  return 0;
    try {
      con = DriverManager.getConnection(
            connect_props.get("datasource").toString(),
            connect_props.get("username").toString(),
            connect_props.get("password").toString());
    } catch (SQLException e) {
      sqlerrtext=e.getMessage();
      System.out.println("Driver Manager Exception:"+sqlerrtext);
      b_connect = false;
      rc = -1;
      return rc;
    }
    if (!b_connect)
      return rc;
    System.out.println("connectDB: successfully connected to database");
    dumpQuery("select  suser_name() \"username\",getdate() \"time\",db_name() \"database\"");
    return rc;
  }

  public mm_query createMMQuery () {
//    return new msql_mmquery(this);
    return null;
  }
  public void disconnect() {
    super.reset();
    try {
      super.con.close();
    } catch(Exception e) {
      System.out.println("SQL Exception:\n"+e.getMessage());
      b_connect = false;
    }
  }
}
