package com.solar.utility;

import java.awt.*;
import javax.swing.*;
import java.util.Properties;
import java.awt.event.*;

//
//
// WHen         What
// 040824       Add MySQL Login prompt

public class LoginPrompt extends JDialog {
  JPanel jpLoginOption = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  Login loginpanel = null;
  Properties loginparms = null;
  FlowLayout flowLayout1 = new FlowLayout();
  JRadioButton jrb_db1 = new JRadioButton();
  JRadioButton jrb_db2 = new JRadioButton();
  JRadioButton jrb_db3 = new JRadioButton();
  Component cp = null;
  public LoginPrompt(Frame frame, String title, boolean modal,String logintype,Properties loginparms) {

    super(frame, title, modal);
    this.loginparms = loginparms;
/*
    if (logintype.equals("ORACLE"))
      loginpanel = new OracleLogin(this,loginparms);
    else if (logintype.equals("APP"))
      loginpanel = new AppLogin(this,loginparms);
    else if (logintype.equals("MySQL"))
      loginpanel = new MySQLLogin(this,loginparms);
*/
    initPanel(logintype,false);

    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }

  }

  public LoginPrompt() {
  }
  void jbInit() throws Exception {
//    panel1.setLayout(borderLayout1);
    getContentPane().setLayout(borderLayout1);
    jpLoginOption.setLayout(flowLayout1);
    jrb_db1.setText("Oracle");
    jrb_db1.addActionListener(new LoginPrompt_jrb_db1_actionAdapter(this));
    jrb_db2.setText("MySQL");
    jrb_db2.addActionListener(new LoginPrompt_jrb_db2_actionAdapter(this));
    jrb_db3.setToolTipText("Microsoft SQL Server");
    jrb_db3.setText("MSSQL");
    jrb_db3.addActionListener(new LoginPrompt_jrb_db3_actionAdapter(this));

    getContentPane().add(loginpanel,BorderLayout.CENTER);
    getContentPane().add(jpLoginOption, BorderLayout.NORTH);
    jpLoginOption.add(jrb_db1, null);
    jpLoginOption.add(jrb_db2, null);
    jpLoginOption.add(jrb_db3, null);
//    getContentPane().add(loginpanel);
//    setBounds(loginpanel.getPanelSize());
//    Rectangle rr = loginpanel.getPanelSize();
    this.setSize(loginpanel.getPreferredSize());
  }

  public void receiveResponse() {
    loginparms.list(System.out);
    if (loginparms.get("PRESS").toString().equals("OK"))
      this.hide();
    if (loginparms.get("PRESS").toString().equals("CANCEL"))
      this.hide();

  }

  private void initPanel(String dbtype,boolean updpanel) {
    if (loginpanel != null) {
      getContentPane().remove(loginpanel);
      System.out.println("remove");
    }
    if (dbtype.equals("ORACLE")) {
      jrb_db1.setSelected(true);
      jrb_db2.setSelected(false);
      jrb_db3.setSelected(false);
      loginpanel = new OracleLogin(this,this.loginparms);
      System.out.println("selected oracle");

    } else if (dbtype.equals("MySQL")) {
      jrb_db1.setSelected(false);
      jrb_db2.setSelected(true);
      jrb_db3.setSelected(false);
      loginpanel = new MySQLLogin(this,this.loginparms);
      System.out.println("selected mysql");

    } else {
      jrb_db1.setSelected(false);
      jrb_db2.setSelected(false);
      jrb_db3.setSelected(true);
      loginpanel = new AppLogin(this,this.loginparms);
      System.out.println("selected mssql");

    }
    if (updpanel) {
      getContentPane().add(loginpanel,BorderLayout.CENTER);
      pack();
    }
  }

  void jrb_db1_actionPerformed(ActionEvent e) {
    initPanel("ORACLE",true);
  }

  void jrb_db2_actionPerformed(ActionEvent e) {
    initPanel("MySQL",true);
  }

  void jrb_db3_actionPerformed(ActionEvent e) {
    initPanel("APP",true);
  }
}

class LoginPrompt_jrb_db1_actionAdapter implements java.awt.event.ActionListener {
  LoginPrompt adaptee;

  LoginPrompt_jrb_db1_actionAdapter(LoginPrompt adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jrb_db1_actionPerformed(e);
  }
}

class LoginPrompt_jrb_db2_actionAdapter implements java.awt.event.ActionListener {
  LoginPrompt adaptee;

  LoginPrompt_jrb_db2_actionAdapter(LoginPrompt adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jrb_db2_actionPerformed(e);
  }
}

class LoginPrompt_jrb_db3_actionAdapter implements java.awt.event.ActionListener {
  LoginPrompt adaptee;

  LoginPrompt_jrb_db3_actionAdapter(LoginPrompt adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jrb_db3_actionPerformed(e);
  }
}
