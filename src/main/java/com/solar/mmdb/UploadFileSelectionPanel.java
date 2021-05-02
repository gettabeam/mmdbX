/*
  Purpose: Add files to database

  Amendment History
  -----------------
  2002-03-25
  2002-06-11   Use CatgMaint for group selection.
  2002-06-12   Use CatgMaint for group selection.
  2004-11-26
  2004-12-16
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.*;
import javax.swing.border.*;

import com.solar.utility.*;
import com.solar.mmquery.*;
import com.solar.imgproc.*;

public class UploadFileSelectionPanel extends JPanel {
  com.solar.utility.Login dblogin = null;
  com.solar.mmquery.DBBridge dbbridge = null;
  mm_query mq = null;
  Properties loginparms = new Properties();
  int [] imgkey = null;
  int imgkey_iterator=0;
  InfoFrame info = new InfoFrame();

  Vector selectedFiles = new Vector();
  Vector selected_list = new Vector();
  JPanel jPanelSelectBase = new JPanel();
  JButton jBut_OK = new JButton();
  JTextField jTextField1 = new JTextField();
  JPanel jPanelInputDir = new JPanel();
  JPanel jPanelDIR = new JPanel();
  JList jListSelected = new JList();
  JScrollPane jScrollPane2 = new JScrollPane();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  BorderLayout borderLayout1 = new BorderLayout();
  JList jList_Dir = new JList();
  JPanel jPanelImport = new JPanel();
  JPanel jPanelSelected = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  BorderLayout borderLayout3 = new BorderLayout();
//  BoxLayout2 boxLayout21 = new BoxLayout2();
  BorderLayout boxLayout21 = new BorderLayout();
  JPanel jPanelPreview = new JPanel();
  BorderLayout borderLayout4 = new BorderLayout();
  ScrollPanePanel scrollPanePanel1 = new ScrollPanePanel();
  JLabel jLab_count = new JLabel();
  JPanel jPanelSelectedInfo = new JPanel();
  JLabel jLab_imginfo = new JLabel();
  JLabel jLabel1 = new JLabel();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout6 = new BorderLayout();
 // BoxLayout2 boxLayout22 = new BoxLayout2();
  BorderLayout boxLayout22 = new BorderLayout();
  TitledBorder titledBorder1;
  PanelCatgSelect panelCatgSelect2 = new PanelCatgSelect();
  JPanel jPanel1 = new JPanel();
  JButton jBut_select = new JButton();
  JPanel jPanel2 = new JPanel();
  JButton jBut_remove = new JButton();
  JButton jBut_removeall = new JButton();
  GridLayout gridLayout1 = new GridLayout();
  JButton jBut_selectAll = new JButton();
  FlowLayout flowLayout1 = new FlowLayout();

  // use CatgMaint for catg group selection
   JFrame cspFrame = null;
   CatgMaint csp =null;
  class TNthread extends Thread {
    UploadFileSelectionPanel fc = null;
    int dir=0;
    TNthread(UploadFileSelectionPanel fc) {
      this.fc = fc;
    }
    public void run() {
      fc.insertDatabase();
    }
  }  /* end class TNthread */

  public UploadFileSelectionPanel() {
    try {
      jbInit();
      info.go();
      info.show();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    System.out.println("init UploadFileSelectionPanel");
    panelCatgSelect2.setParent(this);
    titledBorder1 = new TitledBorder("");
    this.setLayout(borderLayout6);
    jPanelSelectBase.setLayout(borderLayout5);
    jBut_OK.setBackground(new Color(131, 138, 255));
    jBut_OK.setMargin(new Insets(0, 10, 0, 10));
    jBut_OK.setText("OK");
    jBut_OK.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_OK_actionPerformed(e);
      }
    });
    jTextField1.setText("c:\\");
    jPanelInputDir.setLayout(boxLayout21);
    jPanelDIR.setLayout(borderLayout2);
    jListSelected.setBackground(new Color(207, 217, 255));
    jListSelected.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jListSelected_mouseClicked(e);
      }
    });
    jScrollPane2.setFont(new java.awt.Font("Dialog", 0, 10));
    jScrollPane2.setBorder(BorderFactory.createEtchedBorder());
    jScrollPane1.getViewport().setBackground(Color.yellow);
    jScrollPane1.setFont(new java.awt.Font("Dialog", 0, 10));
    jScrollPane1.setForeground(Color.blue);
    jScrollPane1.setBorder(BorderFactory.createEtchedBorder());
    jScrollPane1.setDoubleBuffered(true);
    jScrollPane1.setPreferredSize(new Dimension(260, 132));
    jList_Dir.setBackground(UIManager.getColor("ToolTip.background"));
    jList_Dir.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        jList_Dir_mouseClicked(e);
      }
    });
    jPanelImport.setLayout(borderLayout1);
    jPanelSelected.setLayout(borderLayout3);
    jTabbedPane1.setBackground(SystemColor.desktop);
    jTabbedPane1.setForeground(Color.yellow);
    jTabbedPane1.setBorder(BorderFactory.createEtchedBorder());
    jTabbedPane1.setMaximumSize(new Dimension(32767, 200));
    jPanelInputDir.setBackground(Color.orange);
    jPanelInputDir.setBorder(BorderFactory.createEtchedBorder());
    jPanelPreview.setLayout(borderLayout4);
    jLab_count.setBackground(Color.pink);
    jLab_count.setForeground(new Color(0, 105, 177));
    jLab_count.setBorder(titledBorder1);
    jLab_count.setMaximumSize(new Dimension(30, 19));
    jLab_count.setMinimumSize(new Dimension(30, 19));
    jLab_count.setPreferredSize(new Dimension(30, 19));
    jLab_count.setHorizontalAlignment(SwingConstants.CENTER);
    jLab_count.setHorizontalTextPosition(SwingConstants.CENTER);
    jLab_count.setText("0");
    jPanelSelectedInfo.setFont(new java.awt.Font("Dialog", 0, 10));
    jPanelSelectedInfo.setBorder(BorderFactory.createEtchedBorder());
    jPanelSelectedInfo.setLayout(boxLayout22);
    jLab_imginfo.setForeground(new Color(56, 39, 105));
    jLab_imginfo.setText("Image:");
    jLabel1.setForeground(new Color(92, 33, 177));
    jLabel1.setToolTipText("");
    jLabel1.setHorizontalAlignment(SwingConstants.TRAILING);
    jLabel1.setText("Selected :");
    jPanelSelectBase.setMaximumSize(new Dimension(270, 99999));
    scrollPanePanel1.setBackground(SystemColor.desktop);
    scrollPanePanel1.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel1.setLayout(flowLayout1);
    jBut_select.setBackground(new Color(164, 192, 192));
    jBut_select.setToolTipText("");
    jBut_select.setMargin(new Insets(0, 0, 0, 0));
    jBut_select.setText("Select");
    jBut_select.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_select_actionPerformed(e);
      }
    });
    jBut_remove.setBackground(Color.orange);
    jBut_remove.setMargin(new Insets(0, 0, 0, 0));
    jBut_remove.setMnemonic('0');
    jBut_remove.setText("Delete");
    jBut_remove.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_remove_actionPerformed(e);
      }
    });
    jBut_removeall.setBackground(Color.red);
    jBut_removeall.setForeground(Color.yellow);
    jBut_removeall.setMargin(new Insets(0, 0, 0, 0));
    jBut_removeall.setText("Remove All");
    jBut_removeall.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_removeall_actionPerformed(e);
      }
    });
    jPanel2.setLayout(gridLayout1);
    jBut_selectAll.setBackground(new Color(216, 164, 200));
    jBut_selectAll.setMargin(new Insets(0, 0, 0, 0));
    jBut_selectAll.setText("SelectALL");
    jBut_selectAll.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_selectAll_actionPerformed(e);
      }
    });
    this.add(jPanelSelectBase, BorderLayout.WEST);
    jTabbedPane1.add(jPanelDIR, "DIR");
    jPanelDIR.add(jScrollPane2, BorderLayout.CENTER);
    jPanelDIR.add(jPanel1, BorderLayout.NORTH);
    jPanel1.add(jBut_select, null);
    jPanel1.add(jBut_selectAll, null);
    jTabbedPane1.add(jPanelSelected, "Selected");
    jPanelSelected.add(jScrollPane1, BorderLayout.CENTER);
    jPanelSelected.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(jBut_remove, null);
    jPanel2.add(jBut_removeall, null);
    jTabbedPane1.add(jPanelImport, "Import Options");
    jPanelImport.add(panelCatgSelect2, BorderLayout.CENTER);
    this.add(jPanelPreview, BorderLayout.CENTER);
    jPanelPreview.add(scrollPanePanel1, BorderLayout.CENTER);
    jPanelPreview.add(jPanelSelectedInfo, BorderLayout.SOUTH);
    jPanelSelectedInfo.add(jLabel1, null);
    jPanelSelectedInfo.add(jLab_count, null);
    jPanelSelectedInfo.add(jLab_imginfo, null);
    jPanelInputDir.add(jTextField1, null);
    jPanelInputDir.add(jBut_OK, null);
    jScrollPane1.getViewport().add(jListSelected, null);
    jScrollPane2.getViewport().add(jList_Dir, null);
    jPanelSelectBase.add(jPanelInputDir, BorderLayout.NORTH);
    jPanelSelectBase.add(jTabbedPane1, BorderLayout.CENTER);
  }

  void jBut_OK_actionPerformed(ActionEvent e) {
    refreshFileList(jTextField1.getText());
  }

  void jList_Dir_mouseClicked(MouseEvent e) {
      String file = String.valueOf(jList_Dir.getSelectedValue());
      File f = new File(file);
      if (e.getClickCount()>1) {
        if (f.isDirectory()) {
          refreshFileList(file);
          jTextField1.setText(file);
        }
        else
          displayFile(file);
      }
      else
        displayFile(file);

/*
      Object[] oo=jList_Dir.getSelectedValues();
      for (int i=0;i<oo.length;i++) {
        selectedFiles.add(String.valueOf(oo[i]));
        System.out.println(String.valueOf(oo[i]));
      }
*/
  }
  void displayFile(String file) {
    File f = new File(file);
    if (f.isFile()) {

      int filesize=(int)f.length();
      byte buf[] = new byte[filesize];
      try {
      FileInputStream fis = new FileInputStream(f);
      fis.read(buf,0,filesize);
      } catch (Exception e) {
        System.out.println("displayFile: cannot load file !");

      }

//      ImageIcon img = new ImageIcon(f.toString());
      ImageIcon img = new ImageIcon(buf);
      scrollPanePanel1.setImageIcon(img);
      jLab_imginfo.setText("File:"+f.getName()
      +" Size:"+img.getIconWidth()+"x"+img.getIconHeight());
    }
  }

  private String[] getFileList(String dir,int mode) {
  // mode=1 : file only
  // mode=2 : dir only
  // mode=3 : all
     File ff = new File(dir);
     File files[] = ff.listFiles(new com.solar.mmquery.mm_filefilter());
     String[] filenames = null;
     Vector v= new Vector();
     for (int i=0;i<files.length;i++) {
       if (files[i].isDirectory()) {
         if (mode>=2)
           v.add(files[i].toString()+File.separator);
       }
       else if (files[i].isFile()) {
         if (mode==1 || mode==3)
           v.add(files[i].toString());
       }
     }

     if (ff.getParentFile() != null && mode >=2) {
       v.add(0,ff.getParentFile().toString());
     }
     filenames = new String[v.size()];
     for (int i=0; i<v.size();i++)
       filenames[i] = (String)v.get(i);

     com.solar.utility.MergeSort  fileMS = new com.solar.utility.MergeSort() {
       public int compareElementsAt(int a, int b) {
         String A=(String)toSort[a];
         String B=(String)toSort[b];

         if (A.lastIndexOf(File.separator)==A.length()-1)
           A="  "+A;
         if (B.lastIndexOf(File.separator)==B.length()-1)
           B="  "+B;
	 return (A.toUpperCase().compareTo(B.toUpperCase()));
       }
     };
    fileMS.sort(filenames);
    return filenames;
  }


  void refreshFileList(String dir) {
     info.busy();
     info.print("Loading directory "+dir);
     jList_Dir.setListData(getFileList(dir,3));
     info.done();
  }

  void jList_Dir_mousePressed(MouseEvent e) {

  }
  void jList_Dir_mouseReleased(MouseEvent e) {

  }
  void jList_Dir_mouseEntered(MouseEvent e) {

  }
  void jList_Dir_mouseExited(MouseEvent e) {

  }


  int logindb() {
    LoginPrompt lp = new LoginPrompt(null,"Oracle Database Login",true,"ORACLE",loginparms);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = lp.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    lp.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    lp.show();
    info.busy();
    info.print("Connecting to db...");
    if (loginparms.getProperty("LOGIN").equals("ORACLE"))
      dbbridge = new oraDBBridge();
    else if (loginparms.getProperty("LOGIN").equals("MySQL"))
      dbbridge = new mysqlDBBridge();
    else {
      System.out.println("Database not supported !");
      return -1;
    }
    dbbridge.setLoginParms(loginparms)  ;
    if (dbbridge.connectDB()== -1) {
      info.print("Cannot connect to database!");
      info.done();
      com.solar.utility.MsgBox.show("Error","Database connection failed! Please retry.");
      return -1;
    }
    if (loginparms.getProperty("LOGIN").equals("ORACLE"))
      mq = new ora_mmquery(dbbridge);
    else if (loginparms.getProperty("LOGIN").equals("MySQL"))
      mq = new mysql_mmquery(dbbridge);

    info.print("Database connection established!");
    info.done();
    return 0;
  }

  void jBut_select_actionPerformed(ActionEvent e) {
    String file = String.valueOf(jList_Dir.getSelectedValue());
    File f = new File(file);
    boolean bduplicated = false;
    for (int i=0;i<selected_list.size();i++) {
      if (selected_list.get(i).toString().equalsIgnoreCase(f.toString())) {
        bduplicated = true;
        break;
      }
    }
    if (bduplicated) {
      return;
    }
    selected_list.add(f.toString());
    jListSelected.setListData(selected_list);
    jLab_count.setText(String.valueOf(selected_list.size()));
  }

  void addToDatabase() {
    TNthread tn = new TNthread(this);
    try {
      tn.start();
    } catch (Exception e) {}
  }

  void insertDatabase() {

    File f = new File(jTextField1.getText());
    File outputfile = new File(f.getAbsolutePath()+File.separator+"movebestdup.bat");
    PrintWriter out=null;
    FileOutputStream fos=null;
    try {
      fos = new FileOutputStream(outputfile.toString(),true);
      out = new PrintWriter(fos,true);
      out.println("mkdir bestdup");
    } catch (Exception e) {
      com.solar.utility.MsgBox.show("File IO Error","Cannot create movebestdup.bat");
      System.out.println("UploadFileSelectionPanel.insertDatabase():Cannot create movebestdup.bat");
      return ;
    }

    Vector vcatg = panelCatgSelect2.getCatgCodes();
 //   Vector venccatg = panelCatgSelect2.getEncCatgCodes();

    String[] catgcodes = null;
    String[] enccatgcodes = null;
    String magicword=panelCatgSelect2.getEncryptPasswd();
    if (magicword==null || magicword.trim().equals("")) {
      catgcodes = new String[vcatg.size()];
      for (int i=0;i<vcatg.size();i++) {
        catgcodes[i] = (String)vcatg.get(i);
      }
      enccatgcodes = new String[0];
    }
    else {
      enccatgcodes = new String[vcatg.size()];
      for (int i=0;i<vcatg.size();i++) {
        enccatgcodes[i] = (String)vcatg.get(i);
      }
      catgcodes = new String[0];
    }
//    String[] enccatgcodes = new String[venccatg.size()];
//    for (int i=0;i<venccatg.size();i++) {
//      enccatgcodes[i] = (String)venccatg.get(i);
//    }
    int iok=0;
    int ierr=0;
    int idup=0;
    for (int i=0;i<selected_list.size();i++) {
      info.print("Adding file "+selected_list.get(i)+" ...");
      int key=mq.addMMData(selected_list.get(i).toString(),"New FILE",catgcodes,enccatgcodes,magicword);
      if (key >0) {
        iok++;
      }
      else {
        if (key==-2) {
          info.print("[DUPLICATED]"+selected_list.get(i).toString());
          if (out!=null) {
            out.println("move \""+selected_list.get(i).toString()+"\" bestdup");
          }
          idup++;
        }
        else
          ierr++;
      }
    } /* end for */
    info.print("No of files added : "+iok);
    info.print("No of duplicats   : "+idup);
    info.print("No of errors      : "+ierr);
    try {
    if (out!=null) {
      out.close();
    }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  void jBut_remove_actionPerformed(ActionEvent e) {
    String file = String.valueOf(jListSelected.getSelectedValue());
    for (int i=0;i<selected_list.size();i++) {
      if (selected_list.get(i).toString().equalsIgnoreCase(file)) {
        selected_list.removeElementAt(i);
        break;
      }
    }
    jListSelected.setListData(selected_list);
    jLab_count.setText(String.valueOf(selected_list.size()));

  }

  void jListSelected_mouseClicked(MouseEvent e) {
    String file = String.valueOf(jListSelected.getSelectedValue());
    displayFile(file);
  }

  void jBut_removeall_actionPerformed(ActionEvent e) {
    selected_list.clear();
  }

  void jBut_selectAll_actionPerformed(ActionEvent e) {
    info.busy();
    info.print("<select all>");
    String[] allfiles = this.getFileList(jTextField1.getText(),1);
    int icount=0;
    for (int i=0; i<allfiles.length;i++) {
      String file = allfiles[i];
      File f = new File(file);
      boolean bduplicated = false;
      for (int ii=0;ii<selected_list.size();ii++) {
        if (selected_list.get(ii).toString().equalsIgnoreCase(f.toString())) {
          bduplicated = true;
          break;
        }
      }
      if (!bduplicated) {
        selected_list.add(f.toString());
        icount++;
      }
    }
    jListSelected.setListData(selected_list);
    jLab_count.setText(String.valueOf(selected_list.size()));
    info.print(icount+" file(s) selected.");
    info.done();
  }
  public void close() {
    if (info!=null)
      info.dispose();
  }

   //
   // pop up CatgMaint for catg group selection
   //
    void selectCatgGrp() {
    if (mq==null || dbbridge == null || dbbridge.connectDB()==-1) {
      com.solar.utility.MsgBox.show("Error","Database not connected!");
      return;
    }
    BaseTableModel datamodel = null;
    if (cspFrame ==null) {
      cspFrame = new JFrame();
      cspFrame.setLocation(300,300);
      if (csp==null) {
        csp = new CatgMaint(mq);
        csp.addprocessStatusListener(new processStatusListener() {
          public void dataChanged(processStatusEvent e) {
            processGroupSelection(e);
          }
        });
      }
      cspFrame.getContentPane().add(csp);
      cspFrame.pack();
      csp.setParent(cspFrame);
    }
    else {
      if (csp != null) {
        csp.setMMQuery(mq);
      }
    }
/*
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = cspFrame.getSize();
    if (frameSize.height > screenSize.height)
      frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width)
      frameSize.width = screenSize.width;
    cspFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
*/
    com.solar.utility.screen.CentreFrame(cspFrame);
    cspFrame.setVisible(true);
  }

  void processGroupSelection(processStatusEvent e) {
    BaseTableModel datamodel = (BaseTableModel)e.getSource();
    Vector vselected=new Vector();
    for (int i=0;i<datamodel.getRowCount();i++) {
      Boolean bb=(Boolean)datamodel.getValueAt(i,5);
      if (bb.booleanValue()) {
        vselected.add(datamodel.getValueAt(i,0));
      //  System.out.println("++++:"+datamodel.getValueAt(i,2)+","+datamodel.getValueAt(i,0));
      }
    }
    panelCatgSelect2.setSelectedGroup(vselected,csp.getMagicWord());
  }

}
