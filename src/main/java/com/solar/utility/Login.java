package com.solar.utility;

import java.awt.*;
import javax.swing.JPanel;
import javax.swing.JDialog;
import java.util.Properties;
/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class Login extends JPanel {
  Properties loginparms = new Properties();
  public Login(Properties loginparms) {
    this.loginparms = loginparms;
  }
  public Login() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  public Rectangle getPanelSize() {return null;}

  public Properties getLoginParms() {
    return loginparms;
  }
  void notifyParent() {}
  private void jbInit() throws Exception {
    this.setMinimumSize(new Dimension(200, 100));
    this.setPreferredSize(new Dimension(400, 300));
  }
}