package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.util.*;

import com.solar.mmquery.*;
import com.solar.utility.*;
import java.awt.event.*;

/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class CatgSelectPanel extends JPanel {
  JFrame parentframe = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTable_catg = new JTable();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JTextField jTxt_magicword = new JTextField();
  JButton jButton1 = new JButton();
//  DefaultTableModel catgdataModel=null;
  CatgCodeTableModel catgdataModel= new CatgCodeTableModel();
  ora_mmquery mq = null;
  Vector columns = new Vector();
  JButton jBut_query = new JButton();
  private transient Vector processStatusListeners;

  public static void main(String argv[]) {
    oraDBBridge db = new oraDBBridge();

    Properties login = new Properties();
    login.put("username","eric");
    login.put("password","abc123");
    login.put("host","blackgate");
    login.put("port","1521");
    login.put("sid","orastart");
    db.setLoginParms(login);
    db.initConnection();
    ora_mmquery mq= new ora_mmquery(db);

    JFrame jf = new JFrame();
    CatgSelectPanel cs = new CatgSelectPanel(mq);
    jf.getContentPane().add(cs);
    jf.pack();
    jf.show();
  }

  class CatgCodeTableModel extends BaseTableModel {
    public CatgCodeTableModel() {
    }
    public boolean isCellEditable(int row, int col) {
      if (col==5)
        return true;
      else
        return false;
    }
  }

  public void setParent(JFrame jf ) {
    parentframe = jf;
  }

  public CatgSelectPanel() {
  }

  public CatgSelectPanel(ora_mmquery mq) {
    this.mq = mq;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setMMQuery(ora_mmquery mq) {
    this.mq = mq;
  }

  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jLabel1.setText("Magic Word");
    jTxt_magicword.setMinimumSize(new Dimension(80, 23));
    jTxt_magicword.setPreferredSize(new Dimension(80, 23));
    jTxt_magicword.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTxt_magicword_actionPerformed(e);
      }
    });
    this.setMinimumSize(new Dimension(450,300));
    this.setPreferredSize(new Dimension(450,300));
    jButton1.setToolTipText("");
    jButton1.setText("Cast !");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jTable_catg.setBackground(SystemColor.info);
    jTable_catg.setBorder(BorderFactory.createEtchedBorder());
    jTable_catg.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    jBut_query.setText("List Categories");
    jBut_query.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_query_actionPerformed(e);
      }
    });
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jTxt_magicword, null);
    jPanel1.add(jBut_query, null);
    jPanel1.add(jButton1, null);
    columns.add("catg_code");
    columns.add("description");
    columns.add("catg_key");
    columns.add("seclvl");
    columns.add("total");
    columns.add("select");

    catgdataModel.setColumns(columns);
    jTable_catg.setModel(catgdataModel);
    TableColumn tc=jTable_catg.getColumn("description");
    tc.setPreferredWidth(250);
    tc=jTable_catg.getColumn("catg_code");
    tc.setPreferredWidth(200);
    jScrollPane1.getViewport().add(jTable_catg, null);

    this.setPreferredSize(new Dimension(600,400));
    this.setMinimumSize(new Dimension(600,400));

  }
  public String getMagicWord() {
    return jTxt_magicword.getText();
  }

  public void queryResult(String passwd) {
    Vector v= mq.getCatgGrpDetails(passwd);
    catgdataModel.setDataVector(v);
  }

  void jButton1_actionPerformed(ActionEvent e) {
    this.fireDataChanged(new processStatusEvent((Object)catgdataModel));
    if (parentframe!=null)
      parentframe.setVisible(false);
  }

  void jBut_query_actionPerformed(ActionEvent e) {
    queryResult(jTxt_magicword.getText());
  }

  public BaseTableModel getDataModel() {
    return (BaseTableModel)catgdataModel;
  }

  public synchronized void addprocessStatusListener(processStatusListener l) {
    Vector v = processStatusListeners == null ? new Vector(2) : (Vector) processStatusListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      processStatusListeners = v;
    }
  }

  protected void fireDataChanged(processStatusEvent e) {
    if (processStatusListeners != null) {
      Vector listeners = processStatusListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((processStatusListener) listeners.elementAt(i)).dataChanged(e);
      }
    }
  }

  void jTxt_magicword_actionPerformed(ActionEvent e) {

  }/* end of registerMM() */

}