package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;

public class FileCategoryEditor extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JSplitPane jSplitPane1 = new JSplitPane();
  FilePropPanel filePropPanel1 = new FilePropPanel();
  JScrollPane jScrollPane1 = new JScrollPane();

  public FileCategoryEditor() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jSplitPane1.setDoubleBuffered(true);
    jScrollPane1.setAutoscrolls(false);
    jScrollPane1.setDoubleBuffered(true);
    jScrollPane1.setMaximumSize(new Dimension(100, 100));
    jScrollPane1.setMinimumSize(new Dimension(100, 100));
    jScrollPane1.setPreferredSize(new Dimension(100, 100));
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(filePropPanel1, JSplitPane.LEFT);
    jSplitPane1.add(jScrollPane1, JSplitPane.RIGHT);
  }
  public void addPreviewPanel(JComponent jj) {
//    jSplitPane1.add(jj, JSplitPane.RIGHT);
   jScrollPane1.setViewportView(jj);

  }
  public FilePropPanel getFilePropPanel() {
    return filePropPanel1;
  }
}