package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.io.*;
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

public class DirBrowser extends JPanel implements Serializable {
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  JList jList_File = new JList();
  JButton jButton1 = new JButton();
  private java.util.Vector getFileList;
  private String setDir;
  private java.io.File getSelectedFile;
  private transient Vector actionListeners;
  private String getInputDir;
  private transient Vector mouseListeners;

  public DirBrowser() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
    jLabel1.setText("DIR");
    jLabel1.setBounds(new Rectangle(7, 6, 38, 19));
    this.setLayout(null);
    jTextField1.setText("c:\\");
    jTextField1.setBounds(new Rectangle(39, 4, 154, 23));
    jTextField1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTextField1_actionPerformed(e);
      }
    });
    jScrollPane1.setBounds(new Rectangle(7, 31, 223, 253));
    jButton1.setMargin(new Insets(2, 2, 2, 2));
    jButton1.setText("OK");
    jButton1.setBounds(new Rectangle(199, 6, 32, 22));
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jList_File.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jList_File_mouseClicked(e);
      }
    });
    this.setMinimumSize(new Dimension(100, 150));
    this.setPreferredSize(new Dimension(100, 150));
    this.add(jLabel1, null);
    this.add(jTextField1, null);
    this.add(jScrollPane1, null);
    this.add(jButton1, null);
    jScrollPane1.getViewport().add(jList_File, null);
  }

  void refreshFileList(String dir) {
     File ff = new File(dir);
     File files[] = ff.listFiles(new com.solar.mmquery.mm_filefilter());
     String[] filenames = null;
     if (ff.getParentFile() == null) {
       filenames = new String[files.length];
     }
     else {
       filenames = new String[files.length+1];
       filenames[0] = ff.getParentFile().toString();
     }


     for (int i=0;i<files.length;i++) {
       int j=0;
       if (filenames.length>files.length)
         j=i+1;
       else
         j=i;
       if (files[i].isDirectory())
         filenames[j] = files[i].toString()+File.separator;
       else if (!files[i].isFile())
         filenames[j] = files[i].toString()+" ??";
       else
         filenames[j] = files[i].toString();
     }


     com.solar.utility.MergeSort  fileMS = new com.solar.utility.MergeSort() {
       public int compareElementsAt(int a, int b) {
         String A=(String)toSort[a];
         String B=(String)toSort[b];
	 return (A.toUpperCase().compareTo(B.toUpperCase()));
       }
     };
    fileMS.sort(filenames);
    jList_File.setListData(filenames);
  }

  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }
  public java.util.Vector getGetFileList() {
    return getFileList;
  }
  public void setSetDir(String newSetDir) {
    setDir = newSetDir;
  }
  public java.io.File getGetSelectedFile() {
    return getSelectedFile;
  }

  void jTextField1_actionPerformed(ActionEvent e) {

  }

  void jButton1_actionPerformed(ActionEvent e) {
     this.refreshFileList(jTextField1.getText());
     this.fireActionPerformed(e);
  }

  void jList_File_mouseClicked(MouseEvent e) {
    String file = String.valueOf(jList_File.getSelectedValue());
    File f = new File(file);
    if (e.getClickCount()>1) {
      if (f.isDirectory()) {
        refreshFileList(file);
        jTextField1.setText(file);
      }
      else
        displayFile(f);
    }
    else
      displayFile(f);
  }

  protected void displayFile(File f) {
    try {
      System.out.println("Display File >" + f.getCanonicalPath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public synchronized void removeActionListener(ActionListener l) {
    if (actionListeners != null && actionListeners.contains(l)) {
      Vector v = (Vector) actionListeners.clone();
      v.removeElement(l);
      actionListeners = v;
    }
  }

  public synchronized void addActionListener(ActionListener l) {

    Vector v = actionListeners == null ? new Vector(2) : (Vector) actionListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      actionListeners = v;
    }

  }
  protected void fireActionPerformed(ActionEvent e) {
    if (actionListeners != null) {
      Vector listeners = actionListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((ActionListener) listeners.elementAt(i)).actionPerformed(e);
      }
    }
  }
  public void actionPerformed(ActionEvent e) {

  }
  public String getGetInputDir() {
    getInputDir=jTextField1.getText();
    return getInputDir;
  }
  public synchronized void removeMouseListener(MouseListener l) {
    super.removeMouseListener(l);
    if (mouseListeners != null && mouseListeners.contains(l)) {
      Vector v = (Vector) mouseListeners.clone();
      v.removeElement(l);
      mouseListeners = v;
    }
  }
  public synchronized void addMouseListener(MouseListener l) {
    super.addMouseListener(l);
    Vector v = mouseListeners == null ? new Vector(2) : (Vector) mouseListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      mouseListeners = v;
    }
  }
  protected void fireMouseClicked(MouseEvent e) {
    if (mouseListeners != null) {
      Vector listeners = mouseListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((MouseListener) listeners.elementAt(i)).mouseClicked(e);
      }
    }
  }
  protected void fireMousePressed(MouseEvent e) {
    if (mouseListeners != null) {
      Vector listeners = mouseListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((MouseListener) listeners.elementAt(i)).mousePressed(e);
      }
    }
  }
  protected void fireMouseReleased(MouseEvent e) {
    if (mouseListeners != null) {
      Vector listeners = mouseListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((MouseListener) listeners.elementAt(i)).mouseReleased(e);
      }
    }
  }
  protected void fireMouseEntered(MouseEvent e) {
    if (mouseListeners != null) {
      Vector listeners = mouseListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((MouseListener) listeners.elementAt(i)).mouseEntered(e);
      }
    }
  }
  protected void fireMouseExited(MouseEvent e) {
    if (mouseListeners != null) {
      Vector listeners = mouseListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((MouseListener) listeners.elementAt(i)).mouseExited(e);
      }
    }
  }
}
