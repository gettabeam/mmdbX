package com.solar.mmdb;

//
//
//  Module :MMDB
//
//  When              What
//  20040822
//  20041216
//
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Properties;
import java.lang.*;

import com.solar.utility.*;
import com.solar.imgproc.*;
import com.solar.mmquery.*;
import com.solar.mmdb.screener.*;

public class mmdb_window extends JFrame {

  class myFrame extends JFrame {
    public myFrame() {
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    }
    protected void processWindowEvent(WindowEvent e) {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING) {
  //      com.solar.utility.MsgBox.show("INFO","Close Window");
        this.dispose();
      }
    }
  };

  com.solar.utility.Login dblogin = null;
  com.solar.mmquery.DBBridge dbbridge = null;
  com.solar.mmquery.mm_query mq = null;


/*****************/
/*****************/
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  ImageIcon image_splash;
  JLabel statusBar = new JLabel();
  JMenuItem jMenuFileOpen = new JMenuItem();
  JMenu jMenu1 = new JMenu();
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem jMenuItem2 = new JMenuItem();
  JMenuItem jMenuItem3 = new JMenuItem();
  JMenu jMenu_Test = new JMenu();
  JMenuItem jMenuItem5 = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem jMenuItem7 = new JMenuItem();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JTextArea jTextArea_version_info = new JTextArea();

  /**Construct the frame*/
  public mmdb_window() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
/*
    image1 = new ImageIcon(com.solar.mmdb.mmdb_window.class.getResource("../resources/openFile.gif"));
    image2 = new ImageIcon(com.solar.mmdb.mmdb_window.class.getResource("../resources/closeFile.gif"));
    image3 = new ImageIcon(com.solar.mmdb.mmdb_window.class.getResource("../resources/help.gif"));
    image_splash= new ImageIcon(com.solar.mmdb.mmdb_window.class.getResource("../resources/bg000.jpg"));
*/

image1 = new ImageIcon("../resources/openFile.gif");
image2 = new ImageIcon("../resources/closeFile.gif");
image3 = new ImageIcon("../resources/help.gif");
image_splash= new ImageIcon("../resources/bg000.jpg");

//    image_splash = new ImageIcon();
//    jLabel1 = new JLabel(image_splash);

    //setIconImage(Toolkit.getDefaultToolkit().createImage(mmdb_window.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(null);
    this.setSize(new Dimension(640, 480));
    this.setTitle("mmdb");
    statusBar.setText(" ");
    statusBar.setBounds(new Rectangle(0, 283, 400, 17));
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuFileExit_actionPerformed(e);
      }
    });
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new ActionListener()  {
      public void actionPerformed(ActionEvent e) {
        jMenuHelpAbout_actionPerformed(e);
      }
    });
    jMenuFileOpen.setText("Open");
    jMenu1.setText("DB Tools");
    jMenuItem1.setText("DB Viewer/Organizer");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem1_actionPerformed(e);
      }
    });
    jMenuItem2.setText("File Uploader (File -> DB)");
    jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem2_actionPerformed(e);
      }
    });
    jMenuItem3.setText("File (MM) Register");
    jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem3_actionPerformed(e);
      }
    });

    jMenuItem5.setText("File Screener");
    jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem5_actionPerformed(e);
      }
    });


    jMenu2.setText("DB Maint");
    jMenuItem6.setText("Category Maintenance");
    jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem6_actionPerformed(e);
      }
    });
    jMenuItem7.setText("Group Relation Maintenance");
    jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuItem7_actionPerformed(e);
      }
    });
    jLabel2.setFont(new java.awt.Font("Dialog", 2, 30));
    jLabel2.setForeground(Color.orange);
    jLabel2.setText("Giga Base");
    jLabel2.setBounds(new Rectangle(83, 46, 239, 47));
    jLabel3.setBounds(new Rectangle(83, 48, 239, 47));
    jLabel3.setText("Giga Base");
    jLabel3.setForeground(new Color(255,255,255));
    jLabel3.setFont(new java.awt.Font("Dialog", 2, 30));
    jTextArea_version_info.setBackground(Color.lightGray);
    jTextArea_version_info.setEnabled(false);
    jTextArea_version_info.setFont(new java.awt.Font("Arial", 1, 14));
    jTextArea_version_info.setForeground(SystemColor.desktop);
    jTextArea_version_info.setAlignmentY((float) 0.5);
    jTextArea_version_info.setDisabledTextColor(SystemColor.desktop);
    jTextArea_version_info.setEditable(false);
    jTextArea_version_info.setText(com.solar.mmdb.version.packagesInfo);
    jTextArea_version_info.setBounds(new Rectangle(84, 110, 461, 135));
    jMenuFile.add(jMenuItem5);
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenu1);
    jMenuBar1.add(jMenu2);
    jMenuBar1.add(jMenu_Test);
    jMenuBar1.add(jMenuHelp);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(statusBar, null);
    contentPane.add(jLabel2, null);
    contentPane.add(jLabel3, null);
    contentPane.add(jTextArea_version_info, null);
    jMenu1.add(jMenuItem1);
    jMenu1.add(jMenuItem2);
    jMenu1.add(jMenuItem3);

    jMenu2.add(jMenuItem6);
    jMenu2.add(jMenuItem7);
  }
  /**File | Exit action performed*/
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }
  /**Help | About action performed*/
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    mmdb_window_AboutBox dlg = new mmdb_window_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  void jMenuConnectOracle_actionPerformed(ActionEvent e) {
    Properties loginparms = new Properties();
    LoginPrompt lp = new LoginPrompt(this,"Oracle Database Login",true,"ORACLE",loginparms);
    lp.show();
    dbbridge = new oraDBBridge();
    dbbridge.setLoginParms(loginparms)  ;
    dbbridge.connectDB();
    mq = new ora_mmquery(dbbridge);
  }

  void jMenuItem1_actionPerformed(ActionEvent e) {
    System.out.println("Loading DB Viewer.....");
    PicFrame pf = new PicFrame();
    pf.setSize(new Dimension(600,420));
    com.solar.utility.screen.CentreFrame(pf);
    pf.setVisible(true);
  }

  void jMenuItem2_actionPerformed(ActionEvent e) {
    UploadFileFrame lf = new UploadFileFrame();
    lf.setSize(new Dimension(600,400));
    com.solar.utility.screen.CentreFrame(lf);
    lf.show();
  }

  void jMenuItem3_actionPerformed(ActionEvent e) {
    myFrame jf = new myFrame();
    mm_register_panel mmp = new mm_register_panel();
    jf.getContentPane().add(mmp);
    jf.setSize(mmp.getPreferredSize());
    com.solar.utility.screen.CentreFrame(jf);
    jf.show();
  }

  void jMenuItem5_actionPerformed(ActionEvent e) {
    JFrame jf = new myFrame() ;
    jf.setTitle("File Screener");
    jf.setSize(300,500);
    jf.setLocation(200,200);
    FileScreener dirb = new FileScreener();
    jf.getContentPane().add(dirb);
    dirb.setParent(jf);
    com.solar.utility.screen.CentreFrame(jf);
    jf.show();

  }

  void jMenuItem6_actionPerformed(ActionEvent e) {
// Catg Maint
    CatgMaint cm = new CatgMaint();
    cm.showFrame();
  }

  void jMenuItem7_actionPerformed(ActionEvent e) {
// Group Relation Builder
    CatgTreeMaint ctm = new CatgTreeMaint();
    ctm.showFrame();
  }

}

