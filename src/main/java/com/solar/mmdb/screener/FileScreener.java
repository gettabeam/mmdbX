package com.solar.mmdb.screener;

import java.awt.*;
import javax.swing.*;
import com.solar.mmquery.*;
import com.solar.imgproc.ImgProc;

import java.awt.event.*;
import java.util.*;
import java.io.*;


/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 *  When            What
 *  2001-12-29      Add scaling capability
*   2005-05-30      Add Zoom/Hide features
 */

public class FileScreener extends JPanel {
  BorderLayout borderLayout1 = new BorderLayout();
  DirBrowsePanel dirBrowser1 = new DirBrowsePanel(true);
  JPanel jPanel1 = new JPanel();
  JButton jBut_selectall = new JButton();
  JButton jBut_Add = new JButton();
  JFrame _previewFrame[] = new JFrame[5];
//  JScrollPane _scrollPane[] = new JScrollPane[_previewFrame.length];
  JComboBox jComboBox1 = new JComboBox();
  JLabel jLabel1 = new JLabel();
  Properties _applyCommand = new Properties();
  int imgcount=0;
  int _zoom_factor = 0;
  JFrame _parentFrame = null;
  String _curFilename=null;
  int    _curFrameIdx=0;
  JFrame _frameCmdSetup = new JFrame("Command/Path Setup");
  CmdSetup _cmdSetup = new CmdSetup(this);
  ImgProc imp = new ImgProc();
  int sh=0;
  int sw=0;
  int _idupcount=0;
  int _icount=0;
  int _ierrcount=0;

  class FilePreviewThread extends Thread {
    FileScreener _fs = null;
    String _file = null;
    public FilePreviewThread(FileScreener _fs, String _file) {
      this._fs = _fs;
      this._file = _file;
    }
    public void run() {
      _fs.previewFileTh(_file);
    }
  }
  public static void main(String[] argv) {
    JFrame jf = new JFrame();
    jf.setSize(250,400);
    jf.setLocation(200,200);
    FileScreener dirb = new FileScreener();
    jf.getContentPane().add(dirb);
    dirb.setParent(jf);
    jf.show();
  }



  public void setParent(JFrame jf) {
    this._parentFrame=jf;
  }

  public FileScreener() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }
  void jbInit() throws Exception {
    sh=java.awt.Toolkit.getDefaultToolkit().getScreenSize().height ;
    sw=java.awt.Toolkit.getDefaultToolkit().getScreenSize().width ;

    this.setLayout(borderLayout1);
    jBut_selectall.setMargin(new Insets(0, 0, 0, 0));
    jBut_selectall.setText("Select All");
    jBut_Add.setMargin(new Insets(0, 0, 0, 0));
    jBut_Add.setText("ADD");
    jComboBox1.setMinimumSize(new Dimension(30, 21));
    jComboBox1.setPreferredSize(new Dimension(30, 21));
    jLabel1.setText("Preview Windows");
    this.add(dirBrowser1, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jComboBox1, null);
    jPanel1.add(jBut_selectall, null);
    jPanel1.add(jBut_Add, null);
    dirBrowser1.addprocessStatusListener(new processStatusListener() {
      public void dataChanged(processStatusEvent e) {
 //       long t0=System.currentTimeMillis();
        String s=(String)e.getSource();
        previewFile(s);
//        System.out.println("Total time to preview ["+(System.currentTimeMillis()-t0)+"]"+s);
      }
    });

    jComboBox1.addItem("1");
    jComboBox1.addItem("2");
    jComboBox1.addItem("3");
    jComboBox1.addItem("4");
    jComboBox1.addItem("5");
    _frameCmdSetup.getContentPane().add(_cmdSetup);
    _frameCmdSetup.setSize(330,370);

  }

  synchronized void _keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE:
      case KeyEvent.VK_RIGHT:
        dirBrowser1.fileList_Scroll2NextRow(1);
        break;
      case KeyEvent.VK_LEFT:
        dirBrowser1.fileList_Scroll2NextRow(-1);
        break;
//      case KeyEvent.VK_ENTER:
//        dirBrowser1.fileList_MarkFile();
//        break;
      case KeyEvent.VK_DELETE:
      case KeyEvent.VK_1:
      case KeyEvent.VK_2:
      case KeyEvent.VK_3:
      case KeyEvent.VK_4:
      case KeyEvent.VK_5:
      case KeyEvent.VK_6:
      case KeyEvent.VK_7:
      case KeyEvent.VK_8:
      case KeyEvent.VK_9:
      case KeyEvent.VK_BACK_SPACE:
//      case KeyEvent.VK_0:
        setGroup(e.getKeyCode());
        break;
      case KeyEvent.VK_ENTER:
      case KeyEvent.VK_ESCAPE:
        hideFrames();
        cmdSetup();
        break;
//      case KeyEvent.VK_ENTER:
//        processFile();
//        break;
      case KeyEvent.VK_EQUALS:
      case KeyEvent.VK_UP:
      case KeyEvent.VK_PLUS:
        zoomImg(1);
        break;
      case KeyEvent.VK_DOWN:
      case KeyEvent.VK_MINUS:
        zoomImg(-1);
        break;
      case KeyEvent.VK_F12:
        showFrames();
        break;
    }
  }

  synchronized void zoomImg(int iz) {
    System.out.println("Zoom Factor >"+iz);
    _zoom_factor+=iz;
    if (_zoom_factor <-3)
        _zoom_factor = -3;
    if (_zoom_factor >3 )
        _zoom_factor = 3;
    dirBrowser1.fileList_Scroll2NextRow(0);

  }

  void hideFrames() {
    for (int i=0; i<_previewFrame.length;i++) {
      if (_previewFrame[i]!=null)
        _previewFrame[i].hide();
    }
  }

  void showFrames() {
      for (int i=0; i<_previewFrame.length;i++) {
        if (_previewFrame[i]!=null)
          _previewFrame[i].show();
      }
    }


  int getTotalCount() {
    return _applyCommand.size();
  }

  public Properties getApplyCommand() {
    return _applyCommand;
  }
/*
  void processFile() {
    _idupcount=0;
    _icount=0;
    _ierrcount=0;
    //_applyCommand.list(System.out);
    Enumeration e = _applyCommand.keys();
    Properties cmdsetup = _cmdSetup.getPathSetup();
    int _ic=0;
    while (e.hasMoreElements()) {
      _ic++;
      String srcfile=(String)e.nextElement();
      File f = new File(srcfile);

      String cmd = _applyCommand.getProperty(srcfile);
      String destpath = (String)cmdsetup.getProperty(cmd);
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
      if(filecopy.move(srcfile,destfile)!=0) {
        _ierrcount++;
        System.out.println("FileCopy: Failed to move file "+srcfile);
      }
      else {
       _icount++;
        _applyCommand.remove(srcfile);
      }
    _cmdSetup.setProcessStatus(_ic);
    } //end while
  }

  int getDupCount() {return _idupcount;}
  int getErrCount() {return _ierrcount;}
  int getCount() { return _icount;}
*/

  void cmdSetup() {
    _frameCmdSetup.setLocation(sw/2-150,sh/2-150);
    _cmdSetup.setMarkedCount(this.getTotalCount());
    _frameCmdSetup.setVisible(true);

  }
  void setGroup(int ikey) {
    int i=0;
    switch (ikey) {

//      case KeyEvent.VK_0:
      case KeyEvent.VK_DELETE:
        i=0;
        break;
      case KeyEvent.VK_1:
        i=1;
        break;
      case KeyEvent.VK_2:
        i=2;
        break;
      case KeyEvent.VK_3:
        i=3;
        break;
      case KeyEvent.VK_4:
        i=4;
        break;
      case KeyEvent.VK_5:
        i=5;
        break;
      case KeyEvent.VK_6:
        i=6;
        break;
      case KeyEvent.VK_7:
        i=7;
        break;
      case KeyEvent.VK_8:
        i=8;
        break;
      case KeyEvent.VK_9:
        i=9;
        break;
      case KeyEvent.VK_BACK_SPACE:
        i=-1;
        break;
    }
    if (i>=0) {
      _applyCommand.setProperty(_curFilename,String.valueOf(i));
      _previewFrame[_curFrameIdx].setTitle(_previewFrame[_curFrameIdx].getTitle()+" ==> "+String.valueOf(i));
    } else {
      _applyCommand.remove(_curFilename);
      _previewFrame[_curFrameIdx].setTitle(_previewFrame[_curFrameIdx].getTitle()+" ==> "+"*CLEAR");
    }
    if (i>=0 && i<=9) // Load next frame automatically
      dirBrowser1.fileList_Scroll2NextRow(1);

  }


  void previewFile(String filename) {
    FilePreviewThread _th = new FilePreviewThread(this,filename);
    _th.start();
  }
  synchronized void previewFileTh(String filename) {
    String s=(String)jComboBox1.getSelectedItem();
    int _noofframe=Integer.parseInt(s);
    for (int i=0;i<_noofframe;i++) {
      if (_previewFrame[i] == null) {
//        _scrollPane[i] = new JScrollPane();
        _previewFrame[i] = new JFrame("Preview"+i);
        _previewFrame[i].setSize(500,500);
        _previewFrame[i].setLocation(100+10*i,100+10*i);

        _previewFrame[i].addKeyListener(
           new java.awt.event.KeyAdapter() {
             public void keyPressed(KeyEvent e) {
                 _keyPressed(e);
             }
             public void keyReleased(KeyEvent e) {
             }
         });
//        _previewFrame[i].getContentPane().add(_scrollPane[i]);
      }
    }
    for (int i=_noofframe;i<5;i++) {
      if (_previewFrame[i]!=null)
        _previewFrame[i].setVisible(false);
    }
    imgcount++;
    imp.clear();
    imp.loadImage(filename);

    int h=imp.getHeight();
    int w=imp.getWidth();
    System.out.println("h="+h+" w="+w);

    double _zf = 1.0;
    if (_zoom_factor == -1)
      _zf=0.9;
    if (_zoom_factor == -2)
      _zf=0.7;
    if (_zoom_factor == -3)
      _zf=0.5;
    if (_zoom_factor == 1)
      _zf=1.4;
    if (_zoom_factor == 2)
      _zf=1.6;
    if (_zoom_factor == 3)
      _zf=2;

    h=(int)((double)h*_zf);
    w=(int)((double)w*_zf);


    if (h>sh-50 || w>sw-50) {
      if (h>w)
        imp.scaleToPixel(sh-50);
      else
        imp.scaleToPixel(sw-50);
      h=imp.getHeight();
      w=imp.getWidth();
    }
    else
    if (_zoom_factor != 0) {
      if (h>w)
        imp.scaleToPixel(h);
      else
        imp.scaleToPixel(w);
      h=imp.getHeight();
      w=imp.getWidth();

    }

    if (h<300) h=300;
    if (w<300) w=300;
    int H=h;
    int W=w;
    if (H>sh-50)
      H=sh-50;
    if (W>sw-50)
      W=sw-50;
    ImageIcon img=imp.getImageIcon();
//    ImageIcon img=new ImageIcon((Image)imp.getImage());
   // ImageIcon img = new ImageIcon(filename);
    JLabel jl = new JLabel(img);

    int _frameidx = imgcount%_noofframe;
//    _scrollPane[_frameidx].setSize(w,h);
    _previewFrame[_frameidx].setSize(W+30,H+40);
//    _scrollPane[_frameidx].getViewport().removeAll();
//    _previewFrame[imgcount%_noofframe].getContentPane().removeAll();
//    _previewFrame[imgcount%_noofframe].getContentPane().add(jl);
//    _scrollPane[_frameidx].getViewport().add(jl);

    _previewFrame[_frameidx].getContentPane().removeAll();
    _previewFrame[_frameidx].getContentPane().add(jl);
    _previewFrame[_frameidx].setVisible(true);

    File ff = new File(filename);
    String grp=_applyCommand.getProperty(filename);
    if (grp!=null)
      _previewFrame[_frameidx].setTitle("["+_frameidx+"] "+filename+" ==> "+grp);
    else
      _previewFrame[_frameidx].setTitle("["+_frameidx+"] "+filename);
//    _parentFrame.show();
    _curFilename = filename;
    _curFrameIdx = _frameidx;
  }
}
