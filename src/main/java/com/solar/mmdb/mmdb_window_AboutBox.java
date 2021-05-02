package com.solar.mmdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class mmdb_window_AboutBox extends JDialog implements ActionListener {

  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  JPanel insetsPanel1 = new JPanel();
  JButton button1 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  BorderLayout borderLayout2 = new BorderLayout();
  String product = "Multimedia Database";
  String version = "1.0";
  String copyright = "Copyright (c) 2001";
  String comments = "";
  JLabel jLabel1 = new JLabel();
  JTextArea jTextArea_version_info = new JTextArea();
  public mmdb_window_AboutBox(Frame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    pack();
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
    //imageLabel.setIcon(new ImageIcon(mmdb_window_AboutBox.class.getResource("[Your Image]")));
    this.setTitle("About");
    setResizable(false);
    panel1.setLayout(borderLayout1);
    panel2.setLayout(borderLayout2);
    button1.setText("Ok");
    button1.addActionListener(this);
    jLabel1.setFont(new java.awt.Font("Arial Black", 0, 30));
    jLabel1.setText("GigaBase");
    jTextArea_version_info.setEnabled(false);
    jTextArea_version_info.setFont(new java.awt.Font("Arial", 0, 12));
    jTextArea_version_info.setDisabledTextColor(SystemColor.desktop);
    jTextArea_version_info.setEditable(false);
    jTextArea_version_info.setText(com.solar.mmdb.version.packagesInfo);
    this.getContentPane().add(panel1, null);
    insetsPanel1.add(button1, null);
    panel1.add(insetsPanel1, BorderLayout.SOUTH);
    panel1.add(panel2, BorderLayout.CENTER);
    panel2.add(jTextArea_version_info, BorderLayout.CENTER);
    panel1.add(jLabel1, BorderLayout.NORTH);
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }
  /**Close the dialog*/
  void cancel() {
    dispose();
  }
  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == button1) {
      cancel();
    }
  }
}
