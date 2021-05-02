/*	                                                                                                   */
/*	      Initialize JDBC driver                                                                       */
/*	                                                                                                   */
/* When        What                                                                                    */
/* 040823      Init DBBridge for MySQL                                                                 */
/* 050119      Add disconnect()                                                                        */
/* 050404      Add Authentication                                                                      */
/* 190401      Refefactor the package                                                                  */
/* 190504      Add relaxAutoCommit=true in JDBC connection string to avoid except caused by autocommit */
package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import com.solar.utility.*;

public class mysqlDBBridge extends DBBridge{

  public static void main(String argv[]) {
    mysqlDBBridge db = new mysqlDBBridge();
    db.initConnection();
  }
  public mysqlDBBridge() {
    super();
  }

  public int initConnection() {

    if ( mysql_connect()==0) {
      b_connect=true;
      dumpQuery("select curdate() as \"Server Time\"");

      return 0;
    }
    else {
      b_connect=false;
      return -1;
    }
  }

  private int mysql_connect() {
/* assume connected with user :myadmin pass:myadmin */

//    String connstr="jdbc:oracle:thin:eric/abc123@182.19.8.4:1521:orastart";
//    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/test?user=monty&password=greatsqldb");
    String connstr = "jdbc:mysql://"+connect_props.get("host").toString()+":"+connect_props.get("port")+"/"+connect_props.get("database")+"?relaxAutoCommit=true&user=myadmin&password=myadmin";

/*
    String connstr="jdbc:oracle:thin:"+connect_props.get("username").toString()+"/"+
                   connect_props.get("password").toString()+"@"+
                   connect_props.get("host").toString()+":"+connect_props.get("port")+":"+
                   connect_props.get("sid");
*/
    MyLogger.logger.info("Connecting using "+connstr);
    try {
      Class.forName("com.mysql.jdbc.Driver");
   //   DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      super.con =  DriverManager.getConnection(connstr);
      b_connect=true;
    } catch (Exception e) {
            MyLogger.logger.error("SQL Exception:\n"+e.getMessage());
            b_connect=false;
            return -1;
    } /* end try */
    System.out.println("Connected to MySQL !");
    dumpQuery("select curdate() as \"Server Time\"");
    if (userSignIn())
      return 0;
    else
      return -1;
  } /* end ora_connect */

  private boolean userSignIn() {
    System.out.println("Performing application level authentication...");
    String sql="select access_lvl from security_user where username = ? and password = ?";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean grantaccess=false;
    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1,userInfo.getUserName());
      pstmt.setString(2,userInfo.getUserPass());
      rs = pstmt.executeQuery();
      while (rs.next()) {
          grantaccess=true;
          userInfo.setAccessLevel(rs.getInt(1));
      }
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    if (!grantaccess) {
      System.out.println("** ");
      System.out.println("** User authentication failed !");
      System.out.println("** ");

    }
    return grantaccess;
  }

  public mm_query createMMQuery() {
    return new mysql_mmquery(this);
  }
  public void disconnect() {
    super.reset();
    try {
      super.con.close();
    } catch(Exception e) {
      MyLogger.logger.error("SQL Exception:\n"+e.getMessage());
      b_connect = false;
    }
  }
}
