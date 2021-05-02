package com.solar.utility;

import java.awt.*;
import javax.swing.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class base_MaintWindow extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  base_datawindow base_datawindow1 = new base_datawindow();
  JPanel jPanel1 = new JPanel();

  public base_MaintWindow() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.add(base_datawindow1, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.SOUTH);
  }
}