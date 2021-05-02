/*
  Last Update
  2002-04-01
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class UploadFileFrame extends JFrame{
  UploadFileSelectionPanel uploadFileSelectionPanel1 = new UploadFileSelectionPanel();

  public UploadFileFrame() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    this.setResizable(true);
    this.setTitle("Load File");
    this.getContentPane().add(uploadFileSelectionPanel1, BorderLayout.CENTER);

  }

    protected void processWindowEvent(WindowEvent e) {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
   //     upfileselectpanel1.close();
  //      com.solar.utility.MsgBox.show("INFO","Close Window");
        this.dispose();
      }
    }

}
