package com.solar.mmdb;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class InfoFrame extends JFrame implements Runnable{
  Thread t = null;
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea jTextArea1 = new JTextArea();
  JLabel jLab_ind = new JLabel();
  JLabel jLabel1 = new JLabel();
  TitledBorder titledBorder1;
  public InfoFrame() {
    super("Progress Status");
    try {
      jbInit();
  //    jProgressBar1.
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    t = new Thread(this);
    titledBorder1 = new TitledBorder("");
    this.getContentPane().setLayout(null);
    this.setSize(330,280);
    this.getContentPane().setBackground(Color.lightGray);
    jScrollPane1.setBounds(new Rectangle(10, 43, 300, 193));
    jLab_ind.setBackground(Color.white);
    jLab_ind.setForeground(Color.red);
    jLab_ind.setBorder(BorderFactory.createEtchedBorder());
    jLab_ind.setToolTipText("");
    jLab_ind.setText("Processing ...");
    jLab_ind.setBounds(new Rectangle(11, 5, 93, 17));
    jLabel1.setBackground(SystemColor.info);
    jLabel1.setForeground(UIManager.getColor("ComboBox.selectionBackground"));
    jLabel1.setBorder(BorderFactory.createLoweredBevelBorder());
    jLabel1.setOpaque(true);
    jLabel1.setBounds(new Rectangle(10, 23, 300, 19));
    this.getContentPane().add(jScrollPane1, null);
    this.getContentPane().add(jLab_ind, null);
    this.getContentPane().add(jLabel1, null);
    jScrollPane1.getViewport().add(jTextArea1, null);
//    t.start();
  }
  public void busy() {this.getContentPane().setBackground(Color.orange);}
  public void done() {this.getContentPane().setBackground(Color.gray);}

  public void print(String s) {

    this.getContentPane().setBackground(new Color(200,200,220));
    jTextArea1.setText(s+"\n"+jTextArea1.getText());
    jLabel1.setText(s);
  }

  public void go() {
    if (t!=null)
      t.start();
  }

  public void run() {
    boolean b=true;
    while (true) {
      try {
      t.yield();
      t.sleep(300);
      b=!b;
      if (b) {
        jLab_ind.setText("Processing...");
        jLab_ind.setBackground(Color.red);
      }
      else {
        jLab_ind.setText("");
        jLab_ind.setBackground(Color.yellow);
      }
      } catch (Exception e) {}
    }
  }
}