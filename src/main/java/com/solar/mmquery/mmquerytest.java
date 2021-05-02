package com.solar.mmquery;

/**
 * Title:        mmquery
 * Description:  mmquery lib
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */
import java.util.Properties;
import java.util.Vector;

public class mmquerytest {
  public static void main(String argv[]) {
    DBBridge db = null;
    db = new oraDBBridge();
    Properties login = new Properties();
    login.put("username","sun99");
    login.put("password","sun99");
    login.put("host","blackgate");
    login.put("port","1521");
    login.put("sid","orastart");
    db.setLoginParms(login);
    System.out.println(db.connectDB());
    ora_mmquery mq = new ora_mmquery(db);
    Vector v = mq.populateImgIdx("NEWIMAGES");
    System.out.println("No. of files in NEWIMAGES  = "+v.size());
    mq.importFile("E:\\ARMOREDCORE2_07.JPG","PC GAME CG","GAME");
    mq.importFile("E:\\ARMOREDCORE2_02.JPG","PC GAME CG","IMAGE");
    mq.importFile("E:\\ARMOREDCORE2_03.JPG","PC GAME CG","GAME");

    db = new mssqlDBBridge();
    login.put("username","sun99web");
    login.put("password","sun99web");
    login.put("jdbcdriver","sun.jdbc.odbc.JdbcOdbcDriver");
    login.put("datasource","jdbc:odbc:mmtest");
    db.setLoginParms(login);
    System.out.println(db.connectDB());
  }
}