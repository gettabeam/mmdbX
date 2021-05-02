package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;

/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class OptionDialog extends JDialog {
  JPanel panel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel jPanel1 = new JPanel();
  JOptionPane jOptionPane1 = new JOptionPane();
  JList jListCatg = new JList();

  public OptionDialog(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    try {
      jbInit();
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public OptionDialog() {
    this(null, "", false);
  }
  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
    this.setModal(true);
    getContentPane().add(panel1);
    panel1.add(jTabbedPane1, BorderLayout.CENTER);
    jTabbedPane1.add(jPanel1, "jPanel1");
    jTabbedPane1.add(jOptionPane1, "jOptionPane1");
    jOptionPane1.add(jListCatg, null);
  }
}
