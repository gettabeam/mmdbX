package com.solar.utility;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class MsgBox{
    static JPanel jPanel1 = new JPanel();
    static  BorderLayout borderLayout1 = new BorderLayout();
    static JLabel jLabel1 = new JLabel();
    static JButton jBut_OK = new JButton();
    static JDialog jf = new JDialog();

    public static void main(String argv[]) {
       MsgBox.show("test","thisi s a test this is a tset this is a test this is a test");
    }

  public MsgBox() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void show(String title,String msg) {
    try {
      jbInit();

 //     jf.setSize(new Dimension(300, 100));

      jf.setTitle(title);
      jLabel1.setText(msg);
      jf.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = jf.getSize();
      if (frameSize.height > screenSize.height) {
        frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width) {
        frameSize.width = screenSize.width;
      }
      jf.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
//      jf.setSize(200,80);
      jf.setModal(true);

      jf.show();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private static void jbInit() throws Exception {

    jPanel1.setLayout(borderLayout1);
    jLabel1.setText("TEXT");
    jLabel1.setFont(new java.awt.Font("Arial", 1, 20));
    jLabel1.setForeground(new Color(158, 0, 0));
    jLabel1.setMaximumSize(new Dimension(800, 500));
    jLabel1.setMinimumSize(new Dimension(300, 100));
    jLabel1.setPreferredSize(new Dimension(400, 100));
    jLabel1.setToolTipText("");
    jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);
    jBut_OK.setText("OK");
    jBut_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jf.hide();
//        jBut_OK_actionPerformed(e);
      }
    });
    jf.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jLabel1, BorderLayout.CENTER);
    jPanel1.add(jBut_OK, BorderLayout.SOUTH);
  }

  void jBut_OK_actionPerformed(ActionEvent e) {
    jf.hide();
  }
}