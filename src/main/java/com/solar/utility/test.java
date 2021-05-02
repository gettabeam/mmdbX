package com.solar.utility;
import javax.swing.JFrame;
import java.awt.Rectangle;
import java.util.Properties;
import java.awt.LayoutManager;
import java.awt.*;
import javax.swing.JDialog;
import java.io.*;
/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class test {
  public static void main(String argv[]) {
/*
    JFrame jf = new JFrame();
    Properties loginparms = new Properties();
//    AppLogin al = new AppLogin();
    jf.getContentPane().setLayout(new BorderLayout());
//    jf.getContentPane().add(al);
    jf.setBounds(new Rectangle(400,300));
    LoginPrompt lp = new LoginPrompt(jf,"Login",true,"ORACLE",loginparms);
    jf.show();
    lp.show();
    System.out.println("*****************");
    loginparms.list(System.out);
    System.exit(0);
*/
    try {
      FileOutputStream fos = new FileOutputStream(new File("c:\\aaa"));
      java.io.DataOutputStream dos = new DataOutputStream(fos);
//      dos.writeUTF("���100�E\u5186 ERIC");
      dos.write("���100�E\u5186 ERIC".getBytes("UTF8"));
      dos.flush();
      dos.close();
      fos.close();

    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }

  }
}
