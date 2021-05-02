/*
  Last Update
  2002-03-23
  2004-09-24

  File property Panel
  Purpose: 1. Change catg group of file
           2. Allow filtering
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import javax.swing.table.*;
import com.solar.utility.*;
import com.solar.mmquery.*;
import java.awt.event.*;

public class FilePropPanel extends JPanel {
  int mmkey=0;
  String password=null;
  Vector voldgrp = new Vector();
  Vector vnewgrp = new Vector();
  Vector vmmkey = null;

  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTb_filegroup = new JTable();
  JButton jbut_update = new JButton();
  Vector columns = new Vector();
  FileGroupTableModel filegroupmodel = new FileGroupTableModel();
  mm_query mq = null;
  JButton jbut_filter = new JButton();
  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  JTextField jTextFilter = new JTextField();
  JLabel jLabel2 = new JLabel();
  String catggrg_filter = "";
  Vector filteredgrp = new Vector();
  JLabel jLabel3 = new JLabel();
  class FileGroupTableModel extends BaseTableModel {

    public FileGroupTableModel() {
    }
    public boolean isCellEditable(int row, int col) {
      if (col==4)
        return true;
      else
        return false;
    }
  }


  public FilePropPanel() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jbut_update.setText("Update");
    jbut_update.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbut_update_actionPerformed(e);
      }
    });
    jbut_filter.setText("Filter");
    jbut_filter.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbut_filter_actionPerformed(e);
      }
    });
    jLabel1.setForeground(SystemColor.activeCaption);
    jLabel1.setToolTipText("");
    jLabel1.setText("File Groups Setting");
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder());
    jScrollPane1.setPreferredSize(new Dimension(454, 500));
    jPanel1.setLayout(borderLayout2);
    jTextFilter.setMinimumSize(new Dimension(100, 23));
    jTextFilter.setPreferredSize(new Dimension(100, 23));
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setText("Filter");
    jLabel3.setBorder(BorderFactory.createEtchedBorder());
    jLabel3.setMaximumSize(new Dimension(61, 27));
    jLabel3.setMinimumSize(new Dimension(61, 27));
    jLabel3.setPreferredSize(new Dimension(61, 27));
    jLabel3.setToolTipText("");
    this.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1, BorderLayout.NORTH);
    jPanel1.add(jScrollPane1,  BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTb_filegroup,null);
    this.add(jPanel2, BorderLayout.SOUTH);
    jPanel2.add(jbut_update, null);
    jPanel2.add(jLabel2, null);
    jPanel2.add(jTextFilter, null);
    jPanel2.add(jbut_filter, null);
    jPanel2.add(jLabel3, null);
    /* set up JTable */
    columns.add("catg_code");
    columns.add("description");
    columns.add("catg_key");
    columns.add("seclvl");
    columns.add("select");
    filegroupmodel.setColumns(columns);
    jTb_filegroup.setBackground(SystemColor.info);
    jTb_filegroup.setBorder(BorderFactory.createEtchedBorder());
//    jTb_filegroup.setAutoResizeMode(JTable.AUTO_RESIZE_ON);
    jTb_filegroup.setModel(filegroupmodel);

    TableColumn tc=jTb_filegroup.getColumn("description");
    tc.setPreferredWidth(150);
    tc=jTb_filegroup.getColumn("catg_code");
    tc.setPreferredWidth(100);
    tc=jTb_filegroup.getColumn("catg_key");
    tc.setPreferredWidth(20);
    tc=jTb_filegroup.getColumn("seclvl");
    tc.setPreferredWidth(10);
    tc=jTb_filegroup.getColumn("select");
    tc.setPreferredWidth(10);

  }

  public void setMMQuery(mm_query mq) {
    this.mq = mq;
  }
  public void setFileInfo(String s) {
    jLabel1.setText(s);
  }

  public void setMMKeyVector(Vector vin) {
    vmmkey=vin;
  }
  public void queryResult(int mmkey,String passwd) {
    this.mmkey=mmkey;
    this.password=passwd;
    voldgrp.clear();
    vnewgrp.clear();
    Vector v= mq.getFileGroups(mmkey,passwd);
    filteredgrp.clear();
    for (int i=0;i<v.size();i++) {
      Vector vv = (Vector)v.get(i);
      Boolean b=(Boolean)vv.get(4);
      String catgcode=(String)vv.get(0);

      if (catgcode.indexOf(jTextFilter.getText().trim())==-1) {
        if (!b.booleanValue())
          filteredgrp.add(vv);
      }
      if (b.booleanValue()) {
        voldgrp.add(vv.get(2));
        vnewgrp.add(vv.get(2));
      }
    }
    v.removeAll(filteredgrp);
    filegroupmodel.setDataVector(v);
  }

  void jbut_filter_actionPerformed(ActionEvent e) {
    this.queryResult(this.mmkey,this.password);
  }

  void jbut_update_actionPerformed(ActionEvent e) {
    vnewgrp.clear();
/* find marked catg in window */
    for (int i=0;i<filegroupmodel.getRowCount();i++) {
      Boolean b = (Boolean)filegroupmodel.getValueAt(i,4);
      if (b.booleanValue()) {
        vnewgrp.add(filegroupmodel.getValueAt(i,2));
      }
    }
/* find marked catg in filtered list */
/*
    for (int i=0;i<filteredgrp.size();i++) {
      Vector vv=(Vector)filteredgrp.get(i);
      Boolean b = (Boolean)vv.get(4);
      if (b.booleanValue()) {
        vnewgrp.add(vv.get(2));
      }
    }
*/
    int key=0;
    int errcnt=0;
    int proccnt=0;
    if (vmmkey.size()>0 && voldgrp !=null && vnewgrp !=null) {
      for (int i=0;i<vmmkey.size();i++) {
        key=((Integer)vmmkey.get(i)).intValue();
        if (!mq.updateFileGroups(key,voldgrp,vnewgrp))
          errcnt++;
        else
          proccnt++;
       }
    }
    jLabel1.setText("Processed:"+proccnt+" Error:"+errcnt);
  }


}