/*	                                         */
/*	      Initialize JDBC driver             */
/*	                                         */
/* When        What                              */
/* 010612      Init DBBridge for Oracle          */
/* 050119      Add disconnect()                  */
/* 070529      Add Logger                        */

package com.solar.mmquery;

import java.net.*;
import java.sql.*;
import java.awt.*;
import java.util.Vector;
import com.solar.utility.*;

public class oraDBBridge extends DBBridge{

	public oraDBBridge() {
		super();
	}

	public int initConnection() {

	  if ( ora_connect()==0) {
	  	b_connect=true;
	    super.dumpQuery("select sysdate from dual");
	  	return 0;
	  }
	  else {
	  	b_connect=false;
	  	return -1;
	  }
	}

  private int ora_connect() {
//    String connstr="jdbc:oracle:thin:eric/abc123@182.19.8.4:1521:orastart";
    String connstr="jdbc:oracle:thin:"+connect_props.get("username").toString()+"/"+
                   connect_props.get("password").toString()+"@"+
                   connect_props.get("host").toString()+":"+connect_props.get("port")+":"+
                   connect_props.get("sid");
   // System.out.println("Connecting using "+connstr);
    try {
      DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
      super.con =  DriverManager.getConnection(connstr);
      b_connect=true;
    } catch (SQLException e) {
	    MyLogger.logger.error("SQL Exception:\n"+e.getMessage());
	    b_connect=false;
	    return -1;
    } /* end try */
  	return 0;
  } /* end ora_connect */

  public mm_query createMMQuery() {
    return new ora_mmquery(this);
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
