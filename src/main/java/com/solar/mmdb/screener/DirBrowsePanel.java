package com.solar.mmdb.screener;

import com.solar.mmquery.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.table.*;
import javax.swing.event.*;
import com.solar.utility.*;
/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */


// eRic 20060412 moved from eric.utility to eric.mmdb
//      20060402 renamed from DirBrowser to DirBrowsePanel
public class DirBrowsePanel extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JSplitPane jSplitPane1 = new JSplitPane();
  JLabel jLabel1 = new JLabel();
  JTextField jTextField1 = new JTextField();
  JButton jButton1 = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTable1 = new JTable();
  Vector vdir=new Vector();
  Vector vfile = new Vector();
  DirListDataModel _dirListModel = null;
  DirListDataModel _fileListModel = null;
  JPanel jPanel2 = new JPanel();
  JScrollPane jScrollPane2 = new JScrollPane();
  JTable jTable2 = new JTable();
  BorderLayout borderLayout2 = new BorderLayout();
  static int _loadFileThreadCnt=0;
  private transient Vector processStatusListeners;

  boolean _bFileSelect = false;
  class DirListDataModel extends BaseTableModel {
    public DirListDataModel() {
    }
    public boolean isCellEditable(int row, int col) {
      if (col>0)
        return true;
      else
        return false;
    }
  }


  class LoadFileThread extends Thread {
    String _file=null;

    public LoadFileThread(String _filePath) {
      _file = _filePath;
    }
    public void run() {
      long t0=System.currentTimeMillis();
      File f = new File(_file);
      int filesize=(int)f.length();
      byte[] buf = new byte[filesize];
      try {
//        sleep(10000);
        FileInputStream fis = new FileInputStream(f);
        fis.read(buf,0,filesize);
      } catch (Exception e) {
        System.out.println("LoadFileThread Error !"+e.getMessage());
      }
      System.out.println("Time Take to load file "+_file+" >>"+(System.currentTimeMillis()-t0));
    }
  }
  public static void main(String[] argv) {
    JFrame jf = new JFrame();
    jf.setSize(250,300);
    jf.setLocation(200,200);
    DirBrowsePanel dirb = new DirBrowsePanel(true);
    jf.getContentPane().add(dirb);
    jf.show();
  }

  public DirBrowsePanel() {
//    DirBrowser(true);
  }
  public DirBrowsePanel(boolean fileselect) {
    _bFileSelect=fileselect;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    this.setLayout(borderLayout1);
    jLabel1.setText("Directory");
    jTextField1.setMinimumSize(new Dimension(150, 21));
    jTextField1.setPreferredSize(new Dimension(150, 21));
    jTextField1.setToolTipText("");
    jTextField1.setText("c:\\");
    jButton1.setMargin(new Insets(0, 0, 0, 0));
    jButton1.setText("OK");
    jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
    jSplitPane1.setDividerSize(7);
    jTable1.setBackground(new Color(217, 230, 255));
    jTable2.setBackground(new Color(230, 230, 240));

    jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jTable2_mouseClicked(e);
      }
    });
    jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jTable1_mouseClicked(e);
      }
    });
    jTable2.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        jTable2_keyPressed(e);
      }
      public void keyReleased(KeyEvent e) {
        jTable2_keyReleased(e);
      }
    });

    jPanel2.setLayout(new BorderLayout());
    this.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jTextField1, null);
    jPanel1.add(jButton1, null);
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
    jSplitPane1.add(jPanel2, JSplitPane.RIGHT);

    jPanel2.add(jScrollPane2, BorderLayout.CENTER);
    jScrollPane1.getViewport().add(jTable1, null);
    jScrollPane2.getViewport().add(jTable2,null);
    jSplitPane1.setDividerLocation(130);

    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    _fileListModel = new DirListDataModel();
    _dirListModel = new DirListDataModel();

    Vector columns = new Vector();
    columns.add("path");
    _dirListModel.setColumns(columns);
    jTable1.setModel(_dirListModel);

    columns = new Vector();
    columns.add("File");
    if (_bFileSelect)
      columns.add("Select");
    _fileListModel.setColumns(columns);
    jTable2.setModel(_fileListModel);
    if (_bFileSelect) {
      TableColumn tc=jTable2.getColumn("Select");
      tc.setPreferredWidth(10);
      tc=jTable2.getColumn("File");
      tc.setPreferredWidth(200);
    }
  }

   void refreshFileList(String dir) {
     File ff = new File(dir);
     File files[] = ff.listFiles(new com.solar.mmquery.mm_filefilter());
     String[] filenames = null;
     String parentdir=null;
     filenames = new String[files.length];
     if (ff.getParentFile() == null)
       parentdir=dir;
     else
       parentdir=ff.getParent().toString();

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
    vdir.removeAllElements();
    vfile.removeAllElements();
    Vector vv = new Vector();
    vv.add(parentdir);
    vdir.add(vv);
    for (int i=0;i<filenames.length;i++) {
      Vector v = new Vector();
      v.add(filenames[i]);
      if (filenames[i].lastIndexOf(File.separator)==filenames[i].length()-1)
        vdir.add(v);
      else {
        if (_bFileSelect) {
          v.add(Boolean.FALSE);
        }
        vfile.add(v);
      }
    }
    _dirListModel.setDataVector(vdir);
    _fileListModel.setDataVector(vfile);
  }

  void jButton1_actionPerformed( ActionEvent e) {
    refreshFileList(jTextField1.getText());
  }

  void jTable1_mouseClicked(MouseEvent e) {
    if (e.getClickCount()>=2) {
      String s=(String)jTable1.getValueAt(jTable1.getSelectedRow(),0);
      if (s!=null) {
        jTextField1.setText(s);
        refreshFileList(s);
      }
    }
    e.consume();
  }

  void jTable2_mouseClicked(MouseEvent e) {
    String s=(String)jTable2.getValueAt(jTable2.getSelectedRow(),0);
    this.fireDataChanged(new processStatusEvent(s));
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

  public synchronized void addprocessStatusListener(processStatusListener l) {
    Vector v = processStatusListeners == null ? new Vector(2) : (Vector) processStatusListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      processStatusListeners = v;
    }
  }

  void jTable2_keyPressed(KeyEvent e) {
  }

  void jTable2_keyReleased(KeyEvent e) {

     if (e.getKeyCode()==e.VK_DOWN || e.getKeyCode()==e.VK_UP) {
       String s=(String)jTable2.getValueAt(jTable2.getSelectedRow(),0);
       this.fireDataChanged(new processStatusEvent(s));
     }
  }

  public void fileList_MarkFile() {
  }
  public void fileList_Scroll2NextRow(int dir) {
    long t0=System.currentTimeMillis();
    if (jTable2.getRowCount()==0)
      return;
    int i=jTable2.getSelectedRow();
    i+=dir;
    if (i>=jTable2.getRowCount())
      i=0;
    if (i<0)
      i=jTable2.getRowCount()-1;
    jTable2.setRowSelectionInterval(i,i);
    String _nextFile=(String)jTable2.getValueAt((i+1>=jTable2.getRowCount()?0:i+1),0);

    String s=(String)jTable2.getValueAt(jTable2.getSelectedRow(),0);
    this.fireDataChanged(new processStatusEvent(s));
//    LoadFileThread _lft = new LoadFileThread(_nextFile);
//    _lft.start();
    System.out.println("Total time to scroll an image "+(System.currentTimeMillis()-t0));
  }

}
