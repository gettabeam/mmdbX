package com.solar.utility;

import java.awt.*;
import javax.swing.*;
import java.util.*;
//import com.solar.mmquery.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class base_datawindow extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTable1 = new JTable();
  BaseTableModel datastore = null;
  Vector columns = null;
  private transient Vector processStatusListeners;

  public base_datawindow() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    this.add(jScrollPane1, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTable1, null);
    this.setPreferredSize(new Dimension(400,300));
    this.setMinimumSize(new Dimension(400,300));

  }

  void setDataSouce(BaseTableModel ds) {
    this.datastore = ds;
    if (datastore !=null)
      jTable1.setModel(datastore);
  }

  void setColumns(Vector cols) {
    columns = cols;
    if (datastore !=null)
      datastore.setColumns(cols);
  }

/*
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
*/
}
