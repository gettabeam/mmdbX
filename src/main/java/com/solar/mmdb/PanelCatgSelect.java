package com.solar.mmdb;

import java.awt.*;
//import com.borland.dbswing.*;
import javax.swing.*;
//import com.borland.jbcl.layout.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;

/**

  2002-06-11  Use CatgMaint for group selection
  2002-06-12  Use CatgMaint for group selection
  2003-01-12  Bugfix
 */

public class PanelCatgSelect extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JButton jBut_Connect = new JButton();
 // BoxLayout2 boxLayout21 = new BoxLayout2();
  BorderLayout boxLayout21 = new BorderLayout();
  JList jList1 = new JList();
  JButton jBut_Add2DB = new JButton();
  JPanel jPanel2 = new JPanel();
  UploadFileSelectionPanel parent =null;
//  JPanel parent = null;
  TitledBorder titledBorder1;
  Vector selected_grp = new Vector();
  TitledBorder titledBorder2;
  TitledBorder titledBorder3;
  JLabel jLabel7 = new JLabel();
  JTextField jTxtEncPasswd = new JTextField();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList jList_selectedgrp = new JList();

  /* define custom sort class */
  com.solar.utility.MergeSort  grpMS = new com.solar.utility.MergeSort() {
    public int compareElementsAt(int a, int b) {
       String A=(String)toSort[a];
       String B=(String)toSort[b];
       return (A.toUpperCase().compareTo(B.toUpperCase()));
    }
  };
  JButton jBut_catgselect = new JButton();
  JTextField jTextField1 = new JTextField();

  public PanelCatgSelect() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  void jbInit() throws Exception {
    Object data[][] = {{"MM","1"},{"CATG1","2"},{"CATG2","3"}};
    String[] dbs = {"**","ORACLE","MSSQL","SYBASE"};
    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    titledBorder3 = new TitledBorder("");
    this.setLayout(borderLayout1);
    jBut_Connect.setBackground(Color.lightGray);
    jBut_Connect.setFont(new java.awt.Font("Dialog", 1, 12));
    jBut_Connect.setForeground(SystemColor.activeCaption);
    jBut_Connect.setText("Login");
    jBut_Connect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_Connect_actionPerformed(e);
      }
    });
    jPanel1.setLayout(boxLayout21);
    jBut_Add2DB.setBackground(new Color(200, 200, 200));
    jBut_Add2DB.setEnabled(false);
    jBut_Add2DB.setForeground(Color.blue);
    jBut_Add2DB.setText("ADD");
    jBut_Add2DB.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_Add2DB_actionPerformed(e);
      }
    });
    this.setMinimumSize(new Dimension(70, 57));
    this.setPreferredSize(new Dimension(70, 57));
    jPanel2.setLayout(null);

    jLabel7.setBounds(new Rectangle(9, 214, 69, 19));
    jLabel7.setHorizontalAlignment(SwingConstants.LEFT);
    jLabel7.setForeground(new Color(85, 0, 0));
    jLabel7.setToolTipText("");
    jLabel7.setText("Magic Word");
    jTxtEncPasswd.setEnabled(false);
    jTxtEncPasswd.setBounds(new Rectangle(83, 214, 124, 23));
    jTxtEncPasswd.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTxtEncPasswd_actionPerformed(e);
      }
    });
    jScrollPane2.setBounds(new Rectangle(10, 9, 198, 195));
    jScrollPane2.setBorder(BorderFactory.createLoweredBevelBorder());
    jBut_catgselect.setEnabled(false);
    jBut_catgselect.setForeground(SystemColor.activeCaption);
    jBut_catgselect.setToolTipText("");
    jBut_catgselect.setMargin(new Insets(1, 1, 1, 1));
    jBut_catgselect.setText("Select Category");
    jBut_catgselect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_catgselect_actionPerformed(e);
      }
    });
    jPanel1.setMaximumSize(new Dimension(300, 27));
    jPanel1.setMinimumSize(new Dimension(300, 27));
    jPanel1.setPreferredSize(new Dimension(300, 27));
    jTextField1.setText("jTextField1");
    jTextField1.setBounds(new Rectangle(82, 240, 126, 23));
    jPanel1.add(jBut_Connect, null);
    jPanel1.add(jBut_catgselect, null);
    jPanel1.add(jList1, null);
    jPanel1.add(jBut_Add2DB, null);
    this.add(jPanel2, BorderLayout.CENTER);
    jPanel2.add(jScrollPane2, null);
    jPanel2.add(jTxtEncPasswd, null);
    jPanel2.add(jLabel7, null);
    jPanel2.add(jTextField1, null);
    this.add(jPanel1, BorderLayout.SOUTH);
    jScrollPane2.getViewport().add(jList_selectedgrp, null);
  }

//    void setParent(JPanel fc) {
  void setParent(UploadFileSelectionPanel fc) {
    parent = fc;
  }

  void jBut_Add2DB_actionPerformed(ActionEvent e) {

    if (jList_selectedgrp.getModel().getSize()> 0)
      parent.addToDatabase();

  }

  void jBut_Connect_actionPerformed(ActionEvent e) {
    if (parent.logindb()==0) {
      jBut_Add2DB.setEnabled(true);
      jBut_catgselect.setEnabled(true);
    }
  }

  public Vector getCatgCodes() {
    return selected_grp;
  }

  public String getEncryptPasswd() {
    return jTxtEncPasswd.getText();
  }

  void jTxtEncPasswd_actionPerformed(ActionEvent e) {
  }

  void setSelectedGroup(Vector v, String magicword) {
    selected_grp.clear();  //2002-01-12:clear buffer
    for (int i=0; i<v.size();i++)
      selected_grp.add((String)v.get(i));
    jTxtEncPasswd.setText(magicword);
    jList_selectedgrp.removeAll();
    jList_selectedgrp.setListData(selected_grp);
  }

  void jBut_catgselect_actionPerformed(ActionEvent e) {
    parent.selectCatgGrp();
  }
}
