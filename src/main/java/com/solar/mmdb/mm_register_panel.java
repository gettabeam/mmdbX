/*
  Last Update
  2002-04-01
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

import com.solar.mmquery.*;
import com.solar.utility.*;
import javax.swing.border.*;
/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class mm_register_panel extends JPanel {
  DirBrowser dirBrowser1 = new DirBrowser();
  JButton jBut_start = new JButton();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  int scancount=0;
  int dupcount=0;
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JTextArea jTextArea1 = new JTextArea();
  TitledBorder titledBorder1;
  JPanel jPanel2 = new JPanel();
  JCheckBox jCheckBox1 = new JCheckBox();
  JButton jButtonLogon = new JButton();
  DBBridge db = null;
  class register_thread extends Thread {
    mm_query mr=null;
    String path=null;
    JTextArea jt=null;
    boolean reg_flag=false;
    public register_thread(mm_query r,boolean register) {
      mr = r;
      reg_flag=register;
    }
    public void setPath(String s) {
      path = s;
    }
    public void setJTextArea(JTextArea j) {
      jt=j;
    }

    public void run() {
      try {
        if (mr!=null) {
          mr.register(path,reg_flag);
          jt.setText("** FINISHED **");
        }
      } catch (Exception e) {
        if (jt!=null)
         jt.setText(e.getMessage());
      }
    }
  }

  public mm_register_panel() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    this.setLayout(null);
    this.setMaximumSize(new Dimension(542, 303));
    this.setMinimumSize(new Dimension(542, 303));
    this.setPreferredSize(new Dimension(542, 303));
    dirBrowser1.setBorder(BorderFactory.createEtchedBorder());
    dirBrowser1.setBounds(new Rectangle(11, 5, 243, 289));
    dirBrowser1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent e) {
        button_action();
      }
    });
    jBut_start.setBackground(new Color(210, 223, 255));
    jBut_start.setToolTipText("");
    jBut_start.setMargin(new Insets(2, 2, 2, 2));
    jBut_start.setText("Register !");
    jBut_start.setBounds(new Rectangle(280, 50, 81, 29));
    jBut_start.setEnabled(false);
    jBut_start.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_start_actionPerformed(e);
      }
    });
    jLabel2.setText("Files Scanned");
    jLabel2.setBounds(new Rectangle(285, 231, 127, 19));
    jLabel3.setBackground(Color.white);
    jLabel3.setBorder(BorderFactory.createEtchedBorder());
    jLabel3.setOpaque(true);
    jLabel3.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel3.setText("0");
    jLabel3.setBounds(new Rectangle(386, 231, 57, 19));
    jLabel4.setText("Duplicated");
    jLabel4.setBounds(new Rectangle(285, 251, 63, 19));
    jLabel5.setBounds(new Rectangle(385, 253, 57, 19));
    jLabel5.setText("0");
    jLabel5.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel5.setOpaque(true);
    jLabel5.setBackground(Color.white);
    jLabel5.setBorder(BorderFactory.createEtchedBorder());
    jTextArea1.setLineWrap(true);
    jTextArea1.setBorder(titledBorder1);
    jTextArea1.setToolTipText("");
    jTextArea1.setBackground(new Color(171, 177, 184));
    jTextArea1.setBounds(new Rectangle(280, 107, 245, 119));
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.setBounds(new Rectangle(265, 6, 268, 287));
    jCheckBox1.setText("Register Files");
    jCheckBox1.setBounds(new Rectangle(281, 78, 147, 25));
    jButtonLogon.setBackground(new Color(212, 208, 255));
    jButtonLogon.setBounds(new Rectangle(280, 19, 79, 27));
    jButtonLogon.setMargin(new Insets(2, 2, 2, 2));
    jButtonLogon.setMnemonic('0');
    jButtonLogon.setText("Connect DB");
    jButtonLogon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButtonLogon_actionPerformed(e);
      }
    });
    this.add(dirBrowser1, null);
    this.add(jCheckBox1, null);
    this.add(jBut_start, null);
    this.add(jButtonLogon, null);
    this.add(jLabel4, null);
    this.add(jLabel2, null);
    this.add(jLabel5, null);
    this.add(jLabel3, null);
    this.add(jTextArea1, null);
    this.add(jPanel2, null);
  }
  private void button_action() {
    System.out.println("BUTTON CLICKED");
  }

  void jBut_start_actionPerformed(ActionEvent e) {
    scancount=0;
    dupcount=0;
    int mmkey,i=0;
    if (db ==null ||!db.isConnected()) {
      com.solar.utility.MsgBox.show("ERROR","Not connected!");
      return;
    }
    jTextArea1.setText("Registerring ...");
    mm_query mr= null;
    mr = db.createMMQuery();
    mr.addprocessStatusListener(new processStatusListener() {
      public void dataChanged(com.solar.mmquery.processStatusEvent e) {
        System.out.println(e);
        if (e.getObj() ==null)
          System.out.println("sourceobj of processStatusEvent = null!");
        refreshScreen(e);
      }
    });
    register_thread rt = new register_thread(mr,jCheckBox1.isSelected());
    rt.setPath(dirBrowser1.getGetInputDir());
    rt.setJTextArea(jTextArea1);
//    mr.register(dirBrowser1.getGetInputDir());
    rt.start();
  }

  protected void refreshScreen(com.solar.mmquery.processStatusEvent e) {
    scancount++;
    jTextArea1.setText(e.toString());

    if (e.toString().substring(0,1).equals("-")) {
      dupcount++;
      jLabel5.setText(String.valueOf(dupcount));
    }
    jLabel3.setText(String.valueOf(scancount));
  }

  void jButtonLogon_actionPerformed(ActionEvent e) {
    Properties login = new Properties();
    LoginPrompt lp = new LoginPrompt(null,"Oracle Database Login",true,"ORACLE",login);
    lp.pack();
    lp.setLocation(200,200);
    lp.show();
    if (login.getProperty("LOGIN").equals("ORACLE"))
      db = new oraDBBridge();
    else if (login.getProperty("LOGIN").equals("MySQL"))
      db = new mysqlDBBridge();
    else {
      System.out.println("Database not supported !");
      return ;
    }

    db.setLoginParms(login)  ;
    if (db.connectDB()==-1) {
      com.solar.utility.MsgBox.show("ERROR","Login Failed or database not available!");
      jTextArea1.setText("Login Failed or database not available!");
      jBut_start.setEnabled(false);
      return ;
    }
    jBut_start.setEnabled(true);
    int i=db.sqlExecINT("select count(*) from mm_register");
    jTextArea1.setText(String.valueOf(i)+ " files registered.");
  }
}