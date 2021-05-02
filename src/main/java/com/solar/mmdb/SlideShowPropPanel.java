package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class SlideShowPropPanel extends JPanel {
  JLabel jLabel1 = new JLabel();
  JTextField jText_col = new JTextField();
  JTextField jText_row = new JTextField();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextField jText_height = new JTextField();
  JButton jButton_preset1 = new JButton();
  JButton jButton_preset2 = new JButton();
  JButton jButton_preset3 = new JButton();
  JButton jButton1 = new JButton();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel6 = new JLabel();
  Properties sprop = null;
  JFrame parent = null;
  JCheckBox jCheckBox1 = new JCheckBox();
  public SlideShowPropPanel(Properties pp) {
    this.sprop = pp;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    jLabel1.setText("Grid Dimension");
    jLabel1.setBounds(new Rectangle(11, 32, 100, 17));
    this.setLayout(null);
    jText_col.setText("3");
    jText_col.setBounds(new Rectangle(203, 30, 49, 21));
    jText_row.setText("10");
    jText_row.setBounds(new Rectangle(130, 30, 48, 21));
    jLabel2.setToolTipText("");
    jLabel2.setText("X");
    jLabel2.setBounds(new Rectangle(185, 34, 17, 17));
    jLabel3.setText("Grid Size");
    jLabel3.setBounds(new Rectangle(13, 62, 94, 17));
    jText_height.setText("500");
    jText_height.setBounds(new Rectangle(130, 61, 49, 21));
    jButton_preset1.setMargin(new Insets(0, 0, 0, 0));
    jButton_preset1.setText("Thumbnail");
    jButton_preset1.setBounds(new Rectangle(11, 95, 89, 20));
    jButton_preset1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_preset1_actionPerformed(e);
      }
    });
    jButton_preset2.setBounds(new Rectangle(11, 115, 89, 20));
    jButton_preset2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_preset2_actionPerformed(e);
      }
    });
    jButton_preset2.setText("Medium Frame");
    jButton_preset2.setMargin(new Insets(0, 0, 0, 0));
    jButton_preset3.setMargin(new Insets(0, 0, 0, 0));
    jButton_preset3.setText("Slide Show");
    jButton_preset3.setBounds(new Rectangle(11, 134, 89, 20));
    jButton_preset3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton_preset3_actionPerformed(e);
      }
    });
    jButton1.setText("OK");
    jButton1.setBounds(new Rectangle(161, 128, 79, 27));
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jLabel5.setText("row");
    jLabel5.setBounds(new Rectangle(132, 12, 41, 17));
    jLabel6.setBounds(new Rectangle(202, 13, 41, 17));
    jLabel6.setText("column");
    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Loop");
    jCheckBox1.setBounds(new Rectangle(130, 93, 90, 25));
    this.add(jLabel1, null);
    this.add(jText_row, null);
    this.add(jText_col, null);
    this.add(jLabel3, null);
    this.add(jText_height, null);
    this.add(jLabel2, null);
    this.add(jButton_preset1, null);
    this.add(jButton_preset2, null);
    this.add(jButton_preset3, null);
    this.add(jLabel5, null);
    this.add(jLabel6, null);
    this.add(jButton1, null);
    this.add(jCheckBox1, null);
  }
  void setParent(JFrame jf) {
    parent = jf;
  }
  void jButton_preset1_actionPerformed(ActionEvent e) {
  /* Thumbnail */
    jText_row.setText("10");
    jText_col.setText("3");
    jText_height.setText("350");
    //jText_width.setText("350");
  }

  void jButton_preset2_actionPerformed(ActionEvent e) {
    jText_row.setText("5");
    jText_col.setText("2");
    jText_height.setText("600");
 //   jText_width.setText("600");

  }

  void jButton_preset3_actionPerformed(ActionEvent e) {
    jText_row.setText("5");
    jText_col.setText("1");
    jText_height.setText("950");
 //   jText_width.setText("950");

  }

  void jButton1_actionPerformed(ActionEvent e) {
    sprop.setProperty("row",jText_row.getText());
    sprop.setProperty("col",jText_col.getText());
    sprop.setProperty("height",jText_height.getText());
    sprop.setProperty("width",jText_height.getText());
    if (jCheckBox1.isSelected())
      sprop.setProperty("loop","1");
    else
      sprop.setProperty("loop","0");
    if (parent!=null)
      parent.setVisible(false);
  }
}