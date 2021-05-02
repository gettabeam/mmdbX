package com.solar.utility;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import java.util.Properties;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class AppLogin extends Login {
  JLabel jLabel4 = new JLabel();
  JTextField jTxt_username = new JTextField();
  JLabel jLabel5 = new JLabel();
  JButton jBut_OK = new JButton();
  JButton jBut_CANCEL = new JButton();
  JLabel jLb_title = new JLabel();
  JPasswordField jTxt_passwd = new JPasswordField();
  LoginPrompt parent = null;

  public AppLogin(LoginPrompt parent,Properties loginparms) {
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
    this.setLayout(null);
    this.setBorder(BorderFactory.createEtchedBorder());
    this.setPreferredSize(new Dimension(386, 212));
    jLabel4.setText("Username");
    jLabel4.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel4.setBounds(new Rectangle(31, 76, 59, 20));
    jTxt_username.setText("scott");
    jTxt_username.setBounds(new Rectangle(95, 75, 127, 26));
    jLabel5.setBounds(new Rectangle(31, 102, 59, 20));
    jLabel5.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel5.setText("Password");
    jBut_OK.setBackground(new Color(112, 138, 230));
    jBut_OK.setText("Connect");
    jBut_OK.setBounds(new Rectangle(267, 100, 91, 21));
    jBut_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_OK_actionPerformed(e);
      }
    });
    jBut_CANCEL.setBounds(new Rectangle(268, 125, 91, 23));
    jBut_CANCEL.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_CANCEL_actionPerformed(e);
      }
    });
    jBut_CANCEL.setBackground(Color.lightGray);
    jBut_CANCEL.setText("Cancel");
    jLb_title.setFont(new java.awt.Font("DialogInput", 3, 20));
    jLb_title.setText("Application Login");
    jLb_title.setBounds(new Rectangle(18, 13, 215, 17));
    jTxt_passwd.setText("jPasswordField1");
    jTxt_passwd.setBounds(new Rectangle(96, 104, 127, 27));
    this.add(jLb_title, null);
    this.add(jTxt_username, null);
    this.add(jLabel4, null);
    this.add(jLabel5, null);
    this.add(jTxt_passwd, null);
    this.add(jBut_OK, null);
    this.add(jBut_CANCEL, null);
  }

  void jBut_OK_actionPerformed(ActionEvent e) {
    loginparms.put("PRESS","OK");
    loginparms.put("LOGIN","APP");
  }

  void jBut_CANCEL_actionPerformed(ActionEvent e) {
    loginparms.put("PRESS","CANCEL");
    loginparms.put("LOGIN","APP");
  }

  void notifyParent() {
    parent.receiveResponse();
  }

  public Rectangle getPanelSize() {
    return this.getBounds();
  }


}