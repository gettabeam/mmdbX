package com.solar.utility;

import java.awt.*;
import javax.swing.*;
import java.util.Properties;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class MySQLLogin extends Login {
  JLabel jLabel1 = new JLabel();
  JTextField jTxt_host = new JTextField();
  JLabel jLabel2 = new JLabel();
  JTextField jTxt_port = new JTextField();
  JLabel jLabel3 = new JLabel();
  JTextField jTxt_db = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField jTxt_username = new JTextField();
  JLabel jLabel5 = new JLabel();
  JButton jBut_OK = new JButton();
  JButton jBut_CANCEL = new JButton();
  JLabel jLabel6 = new JLabel();
  JPasswordField jTxt_passwd = new JPasswordField();
  LoginPrompt parent=null;


  public MySQLLogin(LoginPrompt parent,Properties loginparms) {
    this.loginparms = loginparms;
    this.parent = parent;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel1.setText("Host Name");
    jLabel1.setBounds(new Rectangle(14, 52, 69, 20));
    this.setLayout(null);
    jTxt_host.setText("localhost");
    jTxt_host.setBounds(new Rectangle(92, 49, 127, 26));
    jTxt_host.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTxt_host_actionPerformed(e);
      }
    });
  //  this.setBorder(BorderFactory.createEtchedBorder());
    jLabel2.setBounds(new Rectangle(222, 51, 46, 20));
    jLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel2.setText("Port");
    jTxt_port.setBounds(new Rectangle(279, 49, 73, 26));
    jTxt_port.setToolTipText("");
    jTxt_port.setText("3306");
    jLabel3.setBounds(new Rectangle(28, 81, 59, 20));
    jLabel3.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel3.setText("Database");
    jTxt_db.setBounds(new Rectangle(92, 78, 127, 26));
    jTxt_db.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTxt_db_actionPerformed(e);
      }
    });
    jTxt_db.setToolTipText("");
    jTxt_db.setText("test");
    jLabel4.setText("Username");
    jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel4.setBounds(new Rectangle(29, 125, 59, 20));
    jTxt_username.setText("");
    jTxt_username.setBounds(new Rectangle(93, 124, 127, 26));
    jLabel5.setBounds(new Rectangle(29, 151, 59, 20));
    jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel5.setText("Password");
    jBut_OK.setBackground(Color.orange);
    jBut_OK.setText("Connect");
    jBut_OK.setBounds(new Rectangle(267, 105, 91, 37));
    jBut_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_OK_actionPerformed(e);
      }
    });
    jBut_CANCEL.setBounds(new Rectangle(266, 147, 91, 37));
    jBut_CANCEL.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_CANCEL_actionPerformed(e);
      }
    });
    jBut_CANCEL.setBackground(Color.lightGray);
    jBut_CANCEL.setText("Cancel");
    jLabel6.setFont(new java.awt.Font("DialogInput", 3, 20));
    jLabel6.setForeground(Color.blue);
    jLabel6.setText("MySQL Connect");
    jLabel6.setBounds(new Rectangle(18, 13, 215, 17));
    jTxt_passwd.setText("jPasswordField1");
    jTxt_passwd.setBounds(new Rectangle(94, 153, 127, 27));
    this.setBorder(BorderFactory.createEtchedBorder());
    this.setMaximumSize(new Dimension(386, 212));
    this.setMinimumSize(new Dimension(386, 212));
    this.setPreferredSize(new Dimension(386, 212));
    this.add(jTxt_host, null);
    this.add(jTxt_port, null);
    this.add(jLabel1, null);
    this.add(jTxt_username, null);
    this.add(jTxt_db, null);
    this.add(jLabel3, null);
    this.add(jLabel4, null);
    this.add(jLabel2, null);
    this.add(jBut_CANCEL, null);
    this.add(jLabel6, null);
    this.add(jTxt_passwd, null);
    this.add(jLabel5, null);
    this.add(jBut_OK, null);
  }

  void jBut_OK_actionPerformed(ActionEvent e) {
    loginparms.put("PRESS","OK");
//    loginparms.put("PRESS","CANCEL");
    loginparms.put("username",jTxt_username.getText());
    loginparms.put("password",String.valueOf(jTxt_passwd.getPassword()));
    loginparms.put("host",jTxt_host.getText());
    loginparms.put("port",jTxt_port.getText());
    loginparms.put("database",jTxt_db.getText());
    loginparms.put("LOGIN","MySQL");
    Rectangle rr = this.getBounds();
    notifyParent();
  }
  void jBut_CANCEL_actionPerformed(ActionEvent e) {
    loginparms.put("PRESS","CANCEL");

  }

  void notifyParent() {
    parent.receiveResponse();
  }
  public Rectangle getPanelSize() {
    return this.getBounds();
  }

  void jTxt_host_actionPerformed(ActionEvent e) {

  }

  void jTxt_db_actionPerformed(ActionEvent e) {

  }

}
