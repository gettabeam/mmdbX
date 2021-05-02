package com.solar.mmdb.screener;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.*;
import java.io.*;
import com.solar.utility.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */


// eRic    20060413  move from eric.utility to eric.mmdb
//         20060421  fix no filepath error

public class CmdSetup extends JPanel {
  JTextField jTxt_path1 = new JTextField();
  JTextField jTxt_path2 = new JTextField();
  JTextField jTxt_path3 = new JTextField();
  JTextField jTxt_path4 = new JTextField();
  JTextField jTxt_path5 = new JTextField();
  JTextField jTxt_path0 = new JTextField();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel jLabel4 = new JLabel();
  JLabel jLabel5 = new JLabel();
  JLabel jLabel0 = new JLabel();
  JButton jButton1 = new JButton();
  JPanel jPanel1 = new JPanel();
  FileScreener parent = null;
  JPanel jPanel2 = new JPanel();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel8 = new JLabel();
  JLabel jLab_count = new JLabel();
  JLabel jLab_dupcount = new JLabel();
  JLabel jLab_errcount = new JLabel();
  JLabel jLabel9 = new JLabel();
  JLabel jLab_total = new JLabel();
  JLabel jlab_status = new JLabel();
  int _idupcount=0;
  int _icount=0;
  int _ierrcount=0;

  class ProcThread extends Thread {
    CmdSetup parent = null;
    ProcThread(CmdSetup pp) {
      parent=pp;
    }
    public void run() {
     processFile();
     parent.jButton1.setEnabled(true);

    }

    private void processFile() {
      _idupcount=0;
      _icount=0;
      _ierrcount=0;
      Properties _applyCommand = parent.parent.getApplyCommand();
      Enumeration e = _applyCommand.keys();
      Properties cmdsetup = parent.getPathSetup();
      int itotal=_applyCommand.size();
      int _ic=0;
      while (e.hasMoreElements()) {
        _ic++;
        String srcfile=(String)e.nextElement();
        File f = new File(srcfile);

        String cmd = _applyCommand.getProperty(srcfile);
        String destpath = (String)cmdsetup.getProperty(cmd);
        if (destpath==null||destpath.trim().equals(""))
          continue;

        if (destpath.lastIndexOf(File.separator)!=destpath.length()-1) {
          destpath+=File.separator;
        }
        String destfile=destpath+f.getName();
        int surfix=0;
        while(true) {
          File ff = new File(destfile);
          if (!ff.exists())
            break;
          File sf = new File(srcfile);
          if (sf.length()==ff.length()) {
            FileDigest fd = new FileDigest();
            byte[] sfsig = fd.getDigest(srcfile);
            byte[] ffsig = fd.getDigest(destfile);
            if (sfsig.length==ffsig.length) {
              boolean b=true;
              for (int x=0;x<sfsig.length;x++) {
                if (sfsig[x]!=ffsig[x]) {
                  b=false;
                  break;
                }
              } // end for
              if (b) {
                _idupcount++;
                System.out.println("Overwrite dest file !");
                break;  // overwrite the dest file !
              }
            } // end if
          } // end if
          surfix++;
          int i=f.getName().lastIndexOf(".");
          if (i>0)
            destfile=destpath+f.getName().substring(0,i+1) +
                     String.valueOf(surfix)+"." +
                     f.getName().substring(i+1,f.getName().length());
          else
            destfile=destpath+f.getName()+"."+String.valueOf(surfix);
          }
        if(FileCopy.move(srcfile,destfile)!=0) {
          _ierrcount++;
          System.out.println("FileCopy: Failed to move file "+srcfile);
        }
        else {
          _icount++;
          _applyCommand.remove(srcfile);
        }
        parent.setProcessStatus((int)((float)_ic/(float)itotal*100));
        parent.jLab_count.setText(String.valueOf(getCount()));
        parent.jLab_dupcount.setText(String.valueOf(getDupCount()));
        parent.jLab_errcount.setText(String.valueOf(getErrCount()));
      } //end while
    }

    int getDupCount() {return _idupcount;}
    int getErrCount() {return _ierrcount;}
    int getCount() { return _icount;}
  }

  public CmdSetup(FileScreener fs) {
    parent=fs;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    titledBorder1 = new TitledBorder("");
    titledBorder2 = new TitledBorder("");
    this.setLayout(null);
    jTxt_path1.setBounds(new Rectangle(75, 18, 206, 21));
    jTxt_path2.setBounds(new Rectangle(75, 43, 206, 21));
    jTxt_path3.setBounds(new Rectangle(75, 68, 206, 21));
    jTxt_path4.setBounds(new Rectangle(75, 93, 206, 21));
    jTxt_path5.setBounds(new Rectangle(75, 118, 206, 21));
    jTxt_path0.setText("z:\\filtered");
    jTxt_path0.setBounds(new Rectangle(75, 143, 206, 21));
    jLabel1.setText("Path 1");
    jLabel1.setBounds(new Rectangle(22, 18, 41, 17));
    jLabel2.setBounds(new Rectangle(22, 43, 41, 17));
    jLabel2.setText("Path 2");
    jLabel3.setText("Path 3");
    jLabel3.setBounds(new Rectangle(22, 68, 41, 17));
    jLabel4.setToolTipText("");
    jLabel4.setText("Path 4");
    jLabel4.setBounds(new Rectangle(22, 93, 41, 17));
    jLabel5.setText("Path 5");
    jLabel5.setBounds(new Rectangle(22, 118, 41, 17));
    jLabel0.setForeground(new Color(190, 0, 0));
    jLabel0.setToolTipText("");
    jLabel0.setText("Path DEL");
    jLabel0.setBounds(new Rectangle(15, 143, 54, 17));
    this.setMaximumSize(new Dimension(300, 500));
    this.setMinimumSize(new Dimension(300, 500));
    this.setPreferredSize(new Dimension(300, 500));
    jButton1.setBackground(Color.orange);
    jButton1.setText("Process");
    jButton1.setBounds(new Rectangle(29, 189, 101, 25));
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jPanel1.setBorder(titledBorder2);
    jPanel1.setBounds(new Rectangle(4, 9, 308, 172));
    jPanel2.setBackground(new Color(211, 211, 236));
    jPanel2.setBorder(titledBorder1);
    jPanel2.setBounds(new Rectangle(8, 291, 308, 113));
    jLabel6.setText("No. of file processed");
    jLabel6.setBounds(new Rectangle(28, 233, 132, 17));
    jLabel7.setToolTipText("");
    jLabel7.setText("No. of duplicated");
    jLabel7.setBounds(new Rectangle(28, 249, 120, 17));
    jLabel8.setBounds(new Rectangle(28, 267, 120, 17));
    jLabel8.setText("No. of errors");
    jLabel8.setToolTipText("");
    jLab_count.setBackground(SystemColor.info);
    jLab_count.setBorder(titledBorder1);
    jLab_count.setOpaque(true);
    jLab_count.setText("0");
    jLab_count.setBounds(new Rectangle(153, 235, 48, 17));
    jLab_dupcount.setBounds(new Rectangle(152, 254, 50, 17));
    jLab_dupcount.setBorder(titledBorder1);
    jLab_dupcount.setOpaque(true);
    jLab_dupcount.setText("0");
    jLab_errcount.setForeground(Color.red);
    jLab_errcount.setBorder(titledBorder1);
    jLab_errcount.setOpaque(true);
    jLab_errcount.setText("0");
    jLab_errcount.setBounds(new Rectangle(152, 271, 50, 17));
    jLabel9.setBounds(new Rectangle(28, 216, 132, 17));
    jLabel9.setText("Total files marked");
    jLab_total.setBounds(new Rectangle(153, 218, 48, 17));
    jLab_total.setText("0");
    jLab_total.setOpaque(true);
    jLab_total.setBorder(titledBorder1);
    jLab_total.setBackground(UIManager.getColor("window"));
    jlab_status.setBackground(new Color(0, 0, 145));
    jlab_status.setForeground(Color.yellow);
    jlab_status.setBorder(BorderFactory.createLoweredBevelBorder());
    jlab_status.setOpaque(true);
    jlab_status.setToolTipText("");
    jlab_status.setText("100% completed");
    jlab_status.setBounds(new Rectangle(153, 193, 103, 17));
    this.add(jLabel8, null);
    this.add(jLabel7, null);
    this.add(jLabel6, null);
    this.add(jLabel9, null);
    this.add(jButton1, null);
    this.add(jLab_total, null);
    this.add(jLab_count, null);
    this.add(jLab_dupcount, null);
    this.add(jLab_errcount, null);
    this.add(jLabel1, null);
    this.add(jLabel5, null);
    this.add(jLabel4, null);
    this.add(jLabel3, null);
    this.add(jLabel2, null);
    this.add(jLabel0, null);
    this.add(jTxt_path1, null);
    this.add(jTxt_path2, null);
    this.add(jTxt_path4, null);
    this.add(jTxt_path0, null);
    this.add(jTxt_path5, null);
    this.add(jTxt_path3, null);
    this.add(jPanel1, null);
    this.add(jPanel2, null);
    this.add(jlab_status, null);
  }
  public Properties getPathSetup() {
    Properties pp = new Properties();
    if (!jTxt_path1.getText().trim().equals(""))
      pp.setProperty("1",jTxt_path1.getText().trim());
    if (!jTxt_path2.getText().trim().equals(""))
      pp.setProperty("2",jTxt_path2.getText().trim());
    if (!jTxt_path3.getText().trim().equals(""))
      pp.setProperty("3",jTxt_path3.getText().trim());
    if (!jTxt_path4.getText().trim().equals(""))
      pp.setProperty("4",jTxt_path4.getText().trim());
    if (!jTxt_path5.getText().trim().equals(""))
      pp.setProperty("5",jTxt_path5.getText().trim());
    if (!jTxt_path0.getText().trim().equals(""))
      pp.setProperty("0",jTxt_path0.getText().trim());
    return pp;
  }

  void setProcessStatus(int i) {
    jlab_status.setText(i+"% completed");
  }
  void setMarkedCount(int i) {
    jLab_total.setText(String.valueOf(i));
  }
  void jButton1_actionPerformed(ActionEvent e) {
     jButton1.setEnabled(false);
     jLab_count.setText("0");
     jLab_dupcount.setText("0");
     jLab_errcount.setText("0");
     jlab_status.setText("0% completed");
     ProcThread pt = new ProcThread(this);
     pt.start();

  }


}
