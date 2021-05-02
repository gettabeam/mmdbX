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

import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;



import com.solar.utility.*;

/**
 * Title:        mmquery
 * Description:  mmquery lib
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class jdbcworkshop {
  Connection con=null;

  public static void main(String[] argv) {
    jdbcworkshop js = new jdbcworkshop();
    js.ora_ws02();
/*
    DataSecure ds = new DataSecure();
    byte[] ciphertext=ds.encryptWithPBE("iamericabc12333333","HELLO WORLD".getBytes());
    byte[] cleartext=ds.decryptWithPBE("iamericabc12333333",ciphertext);
    String s = new String(cleartext);
    String es = new String(ciphertext);
    for (int i=0;i<cleartext.length;i++)
      System.out.print(cleartext[i]+",");
    System.out.println("Length="+cleartext.length);

    for (int i=0;i<ciphertext.length;i++)
      System.out.print(ciphertext[i]+",");
    System.out.println("Length="+ciphertext.length);

    System.out.println("Encrypted data="+es);
    System.out.println("Decrypted data="+s);
*/
    System.exit(0);
  }

  public jdbcworkshop() {
  }

  public boolean ora_connect() {
    DBBridge db = new oraDBBridge();
    Properties login = new Properties();
    login.put("username","eric");
    login.put("password","abc123");
    login.put("host","pbrct01");
    login.put("port","1521");
    login.put("sid","orastart");
    db.setLoginParms(login);
    db.initConnection();
    if (db.isConnected()) {
      System.out.println("Connected !!");
      con = db.getConnection();
      return true;
    }
    else {
      System.out.println("Unable to connect !");
      System.exit(0);
    }
    return false;
  }

  public void ora_ws01() {
    DataSecure ds = new DataSecure();

    ora_connect();
    try {
    byte[] ciphertext=ds.encryptWithPBE("iamericabc12333333","GAMECUBE".getBytes());

    System.out.println("testing query");
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("select * from xx");
    PreparedStatement pstmt = con.prepareStatement("insert into xx (key,data) values (?,?)");
    int rowcount=0;
    while (rs.next()) {
      String key=rs.getString(1);
      byte[] rawdata=(byte[])rs.getObject(2);
      byte[] cleartext=ds.decryptWithPBE("iamericabc12333333",rawdata);
      String text = new String(cleartext);
      System.out.println(">>"+key+","+rawdata.length+","+text);
      System.out.println();
    }
    rs.close();
    stmt.close();

    pstmt.setObject(1,"GB");
    pstmt.setObject(2,ciphertext);
    pstmt.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void ora_ws02() {
    DataSecure ds = new DataSecure();

    ora_connect();
    try {
    byte[] ciphertext=ds.encryptWithPBE("iamericabc12333333","SONY PLAYSTATION 2".getBytes());

    PreparedStatement pstmt = con.prepareStatement("select key,data from xx where data = ?");
    pstmt.setObject(1,ciphertext);
    ResultSet rs = pstmt.executeQuery();
    int rowcount=0;
    while (rs.next()) {
      String key=rs.getString(1);
      byte[] rawdata=(byte[])rs.getObject(2);
      byte[] cleartext=ds.decryptWithPBE("iamericabc12333333",rawdata);
      String text = new String(cleartext);
      System.out.println(">>"+key+","+rawdata.length+","+text);
      System.out.println();
    }
    rs.close();
    pstmt.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}