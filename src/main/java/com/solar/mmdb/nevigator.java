/*
  Last Update
  2002-03-29
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
import java.util.ResourceBundle;
import java.util.Vector;
import java.io.*;
import javax.swing.border.*;


public class nevigator extends JPanel implements Serializable {
  static final int  NEV_PREV = -1;
  static final int NEV_NEXT = 1;
  static final int  NEV_THUMB = -2;
  
  static final int  NEV_SHOWALL = 2;
  static final int NEV_CONNECT = 4;
  static final int NEV_SELECTGRP = 5;
  static final int NEV_FILEPROP = 6;
  static final int NEV_SLIDE = 7;
  static final int TOGGLE_LIST = 8;

  static ResourceBundle res = ResourceBundle.getBundle("com.solar.mmdb.Res");
  PicFrame parent = null;
  JToolBar jToolBar1 = new JToolBar();
  JButton JBut_prev1 = new JButton();
  JButton JBut_browall = new JButton();
  JButton JBut_next2 = new JButton();
  JButton JBut_next1 = new JButton();
  BorderLayout borderLayout1 = new BorderLayout();
  ImageIcon image1 = null;
  ImageIcon image2 = null;
  ImageIcon image3 = null;
  ImageIcon image4 = null;
  ImageIcon image5 = null;
  ImageIcon image6 = null;
  ImageIcon image7 = null;
  ImageIcon image8 = null;
  ImageIcon image9 = null;
  ImageIcon image10= null;
  ImageIcon image11 = null;
  ImageIcon image12 = null;
  JButton jBut_Connect = new JButton();
  JButton jBut_setcatg = new JButton();
  JLabel jLab_info = new JLabel();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  JButton jbut_fileinfo = new JButton();
  JButton jBut_slideshow = new JButton();
  JLabel jLabScrollVal = new JLabel();
  JScrollBar jScrollBar1 = new JScrollBar();
  TitledBorder titledBorder3;
  TitledBorder titledBorder4;
  JButton jBut_changelist = new JButton();


  public nevigator() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  public void setParent(PicFrame pf) {
    parent = pf;
  }

  public void setScrollVal(int Val) {
    jScrollBar1.setValue(Val);
    jLabScrollVal.setText(String.valueOf(Val));
  }
  public void setScrollMax(int max) {
    jScrollBar1.setMaximum(max);
    jScrollBar1.setMinimum(1);
    jScrollBar1.setValue(1);
  }
  void jbInit() throws Exception {
/*
    image1 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/NextArrow.gif"));
    image2 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/PreviousArrow.gif"));
    image3 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_mycomputer.gif"));
    image4 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/nativeHeader.gif"));
    image5 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon31.gif"));
    image6 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_text_doc.gif"));
    image7 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_admintools.gif"));
    image8 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_picfolder.gif"));
    image9 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon4.gif"));
    image10 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_connect.gif"));
    image11 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_controlpanel2.gif"));
    image12 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon44.gif"));
*/
    image1 = new ImageIcon("../resources/icon_filmfolder.gif");
    image2 = new ImageIcon("../resources/icon_shutdown.gif");
    image3 = new ImageIcon("../resources/icon_mycomputer.gif");
    image4 = new ImageIcon("../resources/icon_floppy1.gif");
    image5 = new ImageIcon("../resources/icon31.gif");
    image6 = new ImageIcon("../resources/icon_text_doc.gif");
    image7 = new ImageIcon("../resources/icon_admintools.gif");
    image8 = new ImageIcon("../resources/icon_picfolder.gif");
    image9 = new ImageIcon("../resources/icon4.gif");
    image10 = new ImageIcon("../resources/icon_connect.gif");
    image11 = new ImageIcon("../resources/icon_controlpanel2.gif");
    image12 = new ImageIcon("../resources/icon44.gif");

    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    titledBorder3 = new TitledBorder("");
    titledBorder4 = new TitledBorder("");
    this.setLayout(borderLayout1);
    JBut_prev1.setBackground(Color.lightGray);
    JBut_prev1.setEnabled(false);
    JBut_prev1.setMaximumSize(new Dimension(32, 32));
    JBut_prev1.setMinimumSize(new Dimension(32, 32));
    JBut_prev1.setToolTipText("Previous");
    JBut_prev1.setHorizontalTextPosition(SwingConstants.CENTER);
    JBut_prev1.setIcon(image1);
    JBut_prev1.setMargin(new Insets(0, 0, 0, 0));
    JBut_prev1.setText("Prev");
    JBut_prev1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JBut_prev1_actionPerformed(e);
      }
    });
    JBut_browall.setBackground(Color.lightGray);
    JBut_browall.setEnabled(false);
    JBut_browall.setMaximumSize(new Dimension(32, 32));
    JBut_browall.setMinimumSize(new Dimension(32, 32));
    JBut_browall.setToolTipText("ThumbNails");
    JBut_browall.setHorizontalTextPosition(SwingConstants.CENTER);
    JBut_browall.setIcon(image5);
    JBut_browall.setMargin(new Insets(0, 0, 0, 0));
    JBut_browall.setText("Thumb");
    JBut_browall.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JBut_browall_actionPerformed(e);
      }
    });
    JBut_next2.setBackground(Color.lightGray);
    JBut_next2.setEnabled(false);
    JBut_next2.setMaximumSize(new Dimension(40, 32));
    JBut_next2.setMinimumSize(new Dimension(40, 32));
    JBut_next2.setToolTipText("Setup");
    JBut_next2.setHorizontalTextPosition(SwingConstants.CENTER);
    JBut_next2.setIcon(image11);
    JBut_next2.setMargin(new Insets(0, 0, 0, 0));
    JBut_next2.setText("Setup");
    JBut_next2.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JBut_next2_actionPerformed(e);
      }
    });
    JBut_next1.setBackground(Color.lightGray);
    JBut_next1.setEnabled(false);
    JBut_next1.setMaximumSize(new Dimension(32, 32));
    JBut_next1.setMinimumSize(new Dimension(32, 32));
    JBut_next1.setPreferredSize(new Dimension(33, 33));
    JBut_next1.setToolTipText("Next");
    JBut_next1.setHorizontalTextPosition(SwingConstants.CENTER);
    JBut_next1.setIcon(image2);
    JBut_next1.setMargin(new Insets(0, 0, 0, 0));
    JBut_next1.setMnemonic('0');
    JBut_next1.setText("Next");
    JBut_next1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JBut_next1_actionPerformed(e);
      }
    });

    jBut_Connect.setBackground(Color.lightGray);
    jBut_Connect.setForeground(Color.black);
    jBut_Connect.setMaximumSize(new Dimension(40, 32));
    jBut_Connect.setMinimumSize(new Dimension(40, 32));
    jBut_Connect.setToolTipText("Connect");
    jBut_Connect.setHorizontalAlignment(SwingConstants.CENTER);
    jBut_Connect.setHorizontalTextPosition(SwingConstants.CENTER);
    jBut_Connect.setIcon(image10);
    jBut_Connect.setMargin(new Insets(0, 0, 0, 0));
    jBut_Connect.setMnemonic('0');
    jBut_Connect.setText("Connect");
    jBut_Connect.setVerticalTextPosition(SwingConstants.CENTER);
    jBut_Connect.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_Connect_actionPerformed(e);
      }
    });
    jBut_setcatg.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_setcatg_actionPerformed(e);
      }
    });
    jBut_setcatg.setMargin(new Insets(1, 1, 1, 1));
    jBut_setcatg.setText("Grp");
    jBut_setcatg.setIcon(image9);
    jBut_setcatg.setBackground(Color.lightGray);
    jBut_setcatg.setEnabled(false);
    jBut_setcatg.setForeground(Color.black);
    jBut_setcatg.setMaximumSize(new Dimension(40, 32));
    jBut_setcatg.setMinimumSize(new Dimension(40, 32));
    jBut_setcatg.setToolTipText("Select Group");
    jBut_setcatg.setHorizontalTextPosition(SwingConstants.CENTER);
    jLab_info.setBorder(titledBorder1);
    jLab_info.setMaximumSize(new Dimension(100, 30));
    jLab_info.setMinimumSize(new Dimension(100, 30));
    jLab_info.setPreferredSize(new Dimension(130, 30));
    jLab_info.setToolTipText("");
    jLab_info.setText("INFO                 ");
    jbut_fileinfo.setBackground(Color.lightGray);
    jbut_fileinfo.setEnabled(false);
    jbut_fileinfo.setMaximumSize(new Dimension(40, 32));
    jbut_fileinfo.setMinimumSize(new Dimension(40, 32));
    jbut_fileinfo.setToolTipText("File Info");
    jbut_fileinfo.setHorizontalTextPosition(SwingConstants.CENTER);
    jbut_fileinfo.setIcon(image6);
    jbut_fileinfo.setMargin(new Insets(0, 0, 0, 0));
    jbut_fileinfo.setMnemonic('0');
    jbut_fileinfo.setText("Prop");
    jbut_fileinfo.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jbut_fileinfo_actionPerformed(e);
      }
    });
    jToolBar1.setBackground(new Color(192, 192, 223));
    jToolBar1.setBorder(BorderFactory.createEtchedBorder());
    jBut_slideshow.setBackground(Color.lightGray);
    jBut_slideshow.setEnabled(false);
    jBut_slideshow.setMaximumSize(new Dimension(40, 32));
    jBut_slideshow.setMinimumSize(new Dimension(40, 32));
    jBut_slideshow.setToolTipText("SlideShow");
    jBut_slideshow.setFocusPainted(true);
    jBut_slideshow.setHorizontalTextPosition(SwingConstants.CENTER);
    jBut_slideshow.setIcon(image8);
    jBut_slideshow.setMargin(new Insets(0, 0, 0, 0));
    jBut_slideshow.setMnemonic('0');
    jBut_slideshow.setText("Slide");
    jBut_slideshow.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_slideshow_actionPerformed(e);
      }
    });
    jLabScrollVal.setBackground(Color.gray);
    jLabScrollVal.setForeground(Color.green);
    jLabScrollVal.setAlignmentX((float) 2.0);
    jLabScrollVal.setBorder(titledBorder3);
    jLabScrollVal.setMaximumSize(new Dimension(32, 30));
    jLabScrollVal.setMinimumSize(new Dimension(32, 30));
    jLabScrollVal.setOpaque(true);
    jLabScrollVal.setPreferredSize(new Dimension(32, 30));
    jLabScrollVal.setToolTipText("");
    jScrollBar1.setOrientation(JScrollBar.HORIZONTAL);
    jScrollBar1.setFont(new java.awt.Font("SansSerif", 0, 11));
    jScrollBar1.setBorder(titledBorder4);
    jScrollBar1.setMaximumSize(new Dimension(32767, 30));
    jScrollBar1.setMinimumSize(new Dimension(20, 30));
    jScrollBar1.setPreferredSize(new Dimension(48, 30));
    jScrollBar1.setToolTipText("File Scroller");
    jScrollBar1.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseReleased(MouseEvent e) {
        jScrollBar1_mouseReleased(e);
      }
    });
    jScrollBar1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
      public void adjustmentValueChanged(AdjustmentEvent e) {
        jScrollBar1_adjustmentValueChanged(e);
      }
    });
    jBut_changelist.setMaximumSize(new Dimension(40, 32));
    jBut_changelist.setMinimumSize(new Dimension(40, 32));
    jBut_changelist.setPreferredSize(new Dimension(39, 37));
   // jBut_changelist.setActionCommand("jButton1");
    jBut_changelist.setBorderPainted(true);
    jBut_changelist.setHorizontalTextPosition(SwingConstants.CENTER);
    jBut_changelist.setIcon(image6);
    jBut_changelist.setText("Change");
    jBut_changelist.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_changelist_actionPerformed(e);
      }
    });
    this.add(jToolBar1, BorderLayout.SOUTH);
    jToolBar1.add(jBut_Connect, null);
    jToolBar1.add(jBut_slideshow, null);
    jToolBar1.add(JBut_browall, null);
    jToolBar1.add(JBut_next2, null);
    jToolBar1.add(JBut_prev1, null);
    jToolBar1.add(JBut_next1, null);
    jToolBar1.add(jBut_setcatg, null);
    jToolBar1.add(jbut_fileinfo, null);
    jToolBar1.add(jBut_changelist, null);
    jToolBar1.add(jLab_info, null);
    jToolBar1.add(jLabScrollVal, null);
    jToolBar1.add(jScrollBar1, null);

/////////////////////////////////

  }
  public void setInfo(String s) {
    jLab_info.setText(s);
    jLab_info.updateUI();
  }
  void JBut_prev1_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_PREV);
  }
  void JBut_browall_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_THUMB);
  }
  void JBut_next2_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_SHOWALL);
  }
  void JBut_next1_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_NEXT);
  }
  void jBut_Connect_actionPerformed(ActionEvent e) {
    if (parent.nevigate(NEV_CONNECT)) {
 //     JBut_prev2.setEnabled(true);
      jBut_Connect.setEnabled(false);
      jBut_setcatg.setEnabled(true);

    }
  }
  void jBut_setcatg_actionPerformed(ActionEvent e) {
    if (parent.nevigate(NEV_SELECTGRP)) {
      JBut_next1.setEnabled(true);
      JBut_next2.setEnabled(true);
      JBut_prev1.setEnabled(true);
      JBut_browall.setEnabled(true);
      jbut_fileinfo.setEnabled(true);
      jBut_slideshow.setEnabled(true);
    }
  }
  private void writeObject(ObjectOutputStream oos) throws IOException {
    oos.defaultWriteObject();
  }
  private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
    ois.defaultReadObject();
  }

  void jbut_fileinfo_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_FILEPROP);
  }

  void jBut_slideshow_actionPerformed(ActionEvent e) {
    parent.nevigate(NEV_SLIDE);
  }

  void jScrollBar1_adjustmentValueChanged(AdjustmentEvent e) {

     jLabScrollVal.setText(String.valueOf(jScrollBar1.getValue()));

  }

  void jScrollBar1_mouseReleased(MouseEvent e) {
    parent.showCurrentImage(jScrollBar1.getValue());
  }

  void jBut_changelist_actionPerformed(ActionEvent e) {
    parent.nevigate(TOGGLE_LIST);
  }




}
