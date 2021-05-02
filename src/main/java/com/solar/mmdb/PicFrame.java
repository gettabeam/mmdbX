/*
  Update History
  2002-03-29
  2004-08-31
*/

package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import javax.swing.JSplitPane;
import java.util.Properties;
import java.util.*;
import java.lang.*;

import com.solar.utility.*;
import com.solar.imgproc.*;
import com.solar.mmquery.*;

import javax.swing.event.*;


public class PicFrame extends JFrame {
  com.solar.utility.Login dblogin = null;
  Properties loginparms = new Properties();
  Vector markedList = new Vector();
  Vector loadedList = null;
  Vector currentList = null;
//  int [] imgkey = null;
  int imgkey_iterator=0;

  int threadcount=0;
  JPanel jPanel1 = new JPanel();
  nevigator nv = null;
  JPanel jpd_main = new JPanel();
  JPanel jpd_sub = new JPanel();
  ScrollPanePanel jpp_main = new ScrollPanePanel();
  ScrollPanePanel jpp_sub = new ScrollPanePanel();
  javax.swing.JSplitPane jsp_base = new javax.swing.JSplitPane();

  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenu jMenuConnect = new JMenu();
//  JPanel jPanel2 = new JPanel();
  CatgMaint csp =null;
  SlideShowPropPanel sspp = null;
  JFrame ssppf = null;
  JFrame cspFrame = null;
  JFrame fileCategoryEditorFrame = null;
  FileCategoryEditor fileCategoryEditor = null;
  FilePropPanel fpp=null;
  Properties sprop = new Properties();
  Vector vmq = new Vector();
  boolean _bSlideShow=false;
  SlideShowThread _slideShowThread = null;
  class TNthread extends Thread {
    PicFrame picframe = null;
    int dir=0;
    TNthread(PicFrame pc) {
      this.picframe = pc;
    }
    public void dir(int dir) { this.dir = dir;}
    public void run() {
      picframe.create_thumbnail(dir);
      System.out.println("**END");
    }
  } /* end class TNthread */

  class STNthread extends Thread {
    PicFrame picframe = null;
    JPanel jp=null;
    int mmkey=0;
    int shift=0;
    int size=0;
    Vector vmmkey=null;
    STNthread(PicFrame pc, JPanel jp, Vector v,int i,int size) {
      this.size=size;
      this.picframe = pc;
      this.jp = jp;
      this.vmmkey = v;
      picframe.threadcount++;
      shift=i;
      System.out.println("Starting TSNthread "+i+" with load="+v.size());
    }
    public void run() {
//      nv.setScrollMax(vmmkey.size());
      for (int i=0;i<vmmkey.size();i++) {
//        nv.setScrollVal(i+1);
        picframe.create_single_thumbnail(((Integer)vmmkey.get(i)).intValue(),jp,shift,size);
      }
    }
    protected void finalize() {
      picframe.threadcount--;
      System.out.println("Killing 1 thread, no of thread left:"+picframe.threadcount);
    }

  } /* end class TNthread */

  class SlideShowThread extends Thread {
    PicFrame picframe = null;

    SlideShowThread(PicFrame pc) {
      this.picframe = pc;
    }

    public void run() {
      try {
        while(true) {
          picframe.image_nevigate(nevigator.NEV_PREV);
          sleep(3000);
        }
      } catch (Exception e) {}
    }
  }


  public PicFrame() {
    try {
      jbInit();
      this.show();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    PicFrame pf = this;
    nv = new nevigator();
    nv.setParent(pf);
    jMenuFile.setText("File");
    jMenuConnect.setText("Connect");
    this.setMenuBar(null);
    this.setResizable(true);
//  this.setUI(null);

/* JInternal Frame properties */
//    this.setClosable(true);
//    this.setSelected(true);
//    this.setMaximizable(true);
//    this.setIconifiable(true);
//    this.setFrameIcon(null);
//    this.setDoubleBuffered(true);

    this.setTitle("Database Connect");
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuConnect);

    jsp_base.setOrientation(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
//    jsp_base.setDividerSize(2);
    jsp_base.setLeftComponent(jpp_sub);
    jsp_base.setRightComponent(jpp_main);
    jsp_base.setOneTouchExpandable(true);

    this.getContentPane().setLayout(new BorderLayout());
//    this.getContentPane().add(jpp, BorderLayout.CENTER);
    this.getContentPane().add(jsp_base,BorderLayout.CENTER);
    this.getContentPane().add(nv, BorderLayout.NORTH);
    JLabel ll = new JLabel("   Welcome to mmdb browser!");
    ll.setFont(new Font("Arial",1,40));
    jpp_main.setViewportView(jpd_main);
    jpp_sub.setViewportView(jpd_sub);
    jpd_main.add(ll,BorderLayout.CENTER);
    sprop.setProperty("row","4");
    sprop.setProperty("col","4");
    sprop.setProperty("width","300");
    sprop.setProperty("height","300");

}

  boolean logindb() {
    LoginPrompt lp = new LoginPrompt(null,"Oracle Database Login",true,"ORACLE",loginparms);
    lp.setLocation(400,200);
    lp.show();
    if (create_mq(loginparms))
      return create_mq(loginparms);
    return false;
  }

  public boolean create_mq(Properties loginparms) {
    com.solar.mmquery.DBBridge dbbridge = null;
    nv.setInfo("Connecting...");
    nv.update(nv.getGraphics());
    if (loginparms.getProperty("LOGIN").equals("ORACLE"))
      dbbridge = new oraDBBridge();
    else if (loginparms.getProperty("LOGIN").equals("MySQL"))
      dbbridge = new mysqlDBBridge();
    else {
      nv.setInfo("Database not supported !");
      return false;
    }
    dbbridge.setLoginParms(loginparms)  ;
    if (dbbridge.connectDB()==-1) {
      nv.setInfo("Failed to connect");
      return false;
    }
    else {
      com.solar.mmquery.mm_query mq = null;
      if (loginparms.getProperty("LOGIN").equals("ORACLE"))
        mq = new ora_mmquery(dbbridge);
      else if (loginparms.getProperty("LOGIN").equals("MySQL"))
        mq = new mysql_mmquery(dbbridge);
      vmq.add(mq);
      nv.setInfo("Intiated "+vmq.size()+" Connected");
      return true;
    }
  }

  void changeCatg(Vector v) {
    int[] catgkeys = new int[v.size()];
    for (int i=0;i<v.size();i++) {
      Integer I = (Integer)v.get(i);
      catgkeys[i] = I.intValue();
    }
    loadedList = ((com.solar.mmquery.mm_query)vmq.get(0)).getImgIdx(catgkeys);
//    imgkey = new int[vv.size()];
    nv.setInfo(loadedList.size()+" files indexed.");
    for (int i=0;i<loadedList.size();i++) {
      System.out.println((Integer)loadedList.get(i));
    }
    currentList = loadedList;
    changeList();
  }

  private void changeList() {
    if (currentList ==null) return;
    imgkey_iterator = -1;
    nv.setScrollMax(currentList.size());
    nv.setScrollVal(1);
  }

  boolean nevigate(int dir) {
    boolean rt=true;
    switch (dir) {
      case nevigator.NEV_NEXT:
      case nevigator.NEV_PREV:
        image_nevigate(dir);
        break;
      case nevigator.NEV_SHOWALL:
        slideShowProp();
        break;
      case nevigator.NEV_THUMB:
        show_thumbnail(dir);
        break;
      case nevigator.NEV_CONNECT:
        rt= logindb();
        break;
      case nevigator.NEV_SELECTGRP:
        selectCatgGrp();
        break;
      case nevigator.NEV_FILEPROP:
        editfileprop();
        break;
      case nevigator.NEV_SLIDE:
        slideShow();
        break;
      case nevigator.TOGGLE_LIST:
        if (currentList== null){break;}
        if (currentList == loadedList)
          currentList = markedList;
        else
          currentList = loadedList;
        changeList();
        break;
    }
    return rt;
  }

  void slideShow() {
    _bSlideShow = !_bSlideShow;
    if (_bSlideShow) {
      _slideShowThread = new SlideShowThread(this);
      _slideShowThread.start();
    }
    else
      _slideShowThread.interrupt();
  }
  void selectCatgGrp() {
    BaseTableModel datamodel = null;
    if (cspFrame ==null) {
      cspFrame = new JFrame();
      cspFrame.setLocation(300,300);
   //eRic@20040824
      mm_query oramq = (mm_query)vmq.get(0);
      if (csp==null) {
        csp = new CatgMaint(oramq);
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
        mm_query oramq = (mm_query) vmq.get(0);
        csp.setMMQuery(oramq);
      }
    }
    cspFrame.setLocation(300,400);
    cspFrame.setVisible(true);
  }

  void processGroupSelection(processStatusEvent e) {
    BaseTableModel datamodel = (BaseTableModel)e.getSource();
    Vector vselected=new Vector();
    for (int i=0;i<datamodel.getRowCount();i++) {
      Boolean bb=(Boolean)datamodel.getValueAt(i,5);
      if (bb.booleanValue()) {
        vselected.add(datamodel.getValueAt(i,2));
      }
    }
    this.changeCatg(vselected);
  }


  void image_nevigate(int dir) {
//    jpp_main.setViewportView(jpd_main);
    com.solar.mmquery.mm_query mq = (mm_query)vmq.get(0);
    JLabel jl = null;
    switch (dir) {
      case nevigator.NEV_THUMB :
              /* show all image */
        break;
      case nevigator.NEV_PREV :
        if (imgkey_iterator < 0 )
          imgkey_iterator = currentList.size();
        else
          imgkey_iterator --;
        break;
      case nevigator.NEV_NEXT:
        if (imgkey_iterator < 0 )
          imgkey_iterator = 0;
        else
          imgkey_iterator ++;
        break;
      case nevigator.NEV_SHOWALL:
        break;
    }
    showCurrentImage();
  }

  void showCurrentImage(int key) {
    imgkey_iterator=key;
    showCurrentImage();
  }

  void showCurrentImage() {
    int key = 0;
    if (imgkey_iterator < 0)
      imgkey_iterator = currentList.size() - 1;
    if (imgkey_iterator >= currentList.size())
      imgkey_iterator = 0;

System.out.println(">>>"+imgkey_iterator);
    key=((Integer)currentList.get(imgkey_iterator)).intValue();

    nv.setScrollVal(imgkey_iterator + 1);
    showImgByMMKey(key);
  }

  public void showImgByMMKey(int key) {
//    jpp_main.setViewportView(jpd_main);
    com.solar.mmquery.mm_query mq = (mm_query)vmq.get(0);
    Thumbnail jl = null;
    nv.setInfo("Loading mmkey:"+key);
    try {
      com.solar.imgproc.ImgProc imgproc = new com.solar.imgproc.ImgProc();
      byte[] b =mq.readMMData(key,csp.getMagicWord());
      System.out.println(">>>"+b.length);

      imgproc.loadImage(b);
      nv.setInfo("mmkey:"+key);
//      imgproc.scaleToPixel(900);
      imgproc.scaleToPixel(1000);
      ImageIcon imgicon = new ImageIcon(imgproc.getImage());
      jl = new Thumbnail(imgicon);
      jl.setParent(this);
      jl.setMMKey(key);
      jl.setImgInfo(imgproc.getHeight(),imgproc.getWidth(),imgproc.getSize());
      jl.enableWheel();
      if (fileCategoryEditorFrame!=null && fileCategoryEditorFrame.isVisible()) {
        editfileprop();
      }
    } catch (Exception e) {
      jl = new Thumbnail("Cannot load mmkey:"+key);
      jl.setFont(new Font("Arial",3,20));
      nv.setInfo("Cannot load mmkey:"+key);
      e.printStackTrace();
    }

    if (jpd_main!=null) {
      System.out.println("no of objs in jpdislay = "+jpd_main.getComponentCount());
      jpd_main.removeAll();
      jpd_main.setLayout(new BorderLayout());
    }
    jpd_main.add(jl,BorderLayout.CENTER);
  }



  public void create_thumbnail(int dir) {
    if (jpd_main!=null) {
      System.out.println("no of objs in jpdislay = "+jpd_main.getComponentCount());
      jpd_main.removeAll();
//      jpd.setLayout(new BorderLayout());
    }
//    jpd = new JPanel();
//    jpp_main.setViewportView(jpd_main);

    int size=0;
    int noofgrid=0;
    int row=0;
    int col=0;

    row=Integer.parseInt((String)sprop.getProperty("row"));
    col=Integer.parseInt((String)sprop.getProperty("col"));
    jpd_main.setLayout(new GridLayout(row,col));
    jpd_sub.removeAll();
 //   jpd_sub.setSize(800,800);
//    FlowLayout layout = new java.awt.FlowLayout();
  //  layout.setAlignment(FlowLayout.LEADING);
 //   jpd_sub.setLayout(layout);
 jpd_sub.setLayout(new GridLayout(row,col));

    size=Integer.parseInt((String)sprop.getProperty("height"));
    noofgrid=row*col;

   //long
   nv.setInfo("Loaded "+noofgrid+" images");
   populateToThumbnailPanel(dir,noofgrid,size,jpd_sub);
 } /* end create_thumbnails */

 public JPanel createThumbnail4CategoryEditor() {
   JPanel jp = new JPanel();
   jp.setLayout(new GridLayout(20,5));
   int size=100;
   int noofgrid= 100;
   populateToThumbnailPanel(0,noofgrid,size,jp);
   return jp;

 }
 public void populateToThumbnailPanel(int dir, int noofgrid,int size,JPanel displayPanel) {
   Vector vthread[] = new Vector[vmq.size()];
   for (int ii=0;ii<vmq.size();ii++) {
     vthread[ii] = new Vector();
   }
   for (int ii=0;ii<noofgrid;ii++) {
     imgkey_iterator ++;
     if (imgkey_iterator >= currentList.size()) {
       imgkey_iterator = 0;
       if (ii>0)
         break;
     }
//     nv.setScrollVal(imgkey_iterator+1);
     vthread[ii%vmq.size()].add(((Integer)currentList.get(imgkey_iterator)));
   }
   try {
     for (int ii=0;ii<vmq.size();ii++)  {
       System.out.println("Starting STNthread no. "+ii);
       STNthread stn = new STNthread(this,displayPanel,vthread[ii],ii,size);
       stn.start();
     }
   } catch (Exception e)
   {
     e.printStackTrace();
   }
} /* end create_thumbnails */


  public void create_single_thumbnail(int mmkey,JPanel jpd,int shift,int size) {
    System.out.println("Creating thumbnail with TSNthread No. "+shift);
    com.solar.imgproc.ImgProc imgproc = new com.solar.imgproc.ImgProc();

    shift=shift%vmq.size();
    com.solar.mmquery.mm_query mq = (mm_query)vmq.get(shift);
//    int size=0;
    int noofgrid=0;
    int row=0;
    int col=0;

    row=Integer.parseInt((String)sprop.getProperty("row"));
    col=Integer.parseInt((String)sprop.getProperty("col"));
  //  jpd.setLayout(new java.awt.FlowLayout());
  //  jpd.setLayout(new GridLayout(row,col));
//    size=Integer.parseInt((String)sprop.getProperty("height"));
    noofgrid=row*col;

    try {
      imgproc.loadImage(mq.readMMData(mmkey,csp.getMagicWord(),"pc"));
      nv.setInfo("mmkey:"+mmkey);

//      imgproc.scaleToPixel(size);
      imgproc.scaleToPixel(size);  // original size
      ImageIcon imgicon = new ImageIcon(imgproc.getImage());
//      JLabel jl = new JLabel(imgicon);
      Thumbnail jl = new Thumbnail(imgicon);
      jl.setParent(this);
      jl.setMMKey(mmkey);
      jl.setImgInfo(imgproc.getHeight(),imgproc.getWidth(),imgproc.getSize());

      jpd.add(jl);
    } catch (Exception e) {
       JLabel jl = new JLabel("Error loading image");
       jl.setFont(new Font("Arial",2,20));
       jpd.add(jl);
       nv.setInfo("Cannot load mmkey:"+mmkey);
       e.printStackTrace();
    }
  } /* end create_single_thumbnails */



  void show_thumbnail(int dir) {
    TNthread tnh = new TNthread(this);
    tnh.dir(dir);
    try {
      tnh.start();
    } catch (Exception e) {}
  }

  void editfileprop() {
    if (csp==null)
      return;
    com.solar.mmquery.mm_query mq = (mm_query)vmq.get(0);
    if (fileCategoryEditorFrame ==null) {
      fileCategoryEditorFrame = new JFrame("Edit File Properties");
      fileCategoryEditorFrame.setSize(550,550);
      com.solar.utility.screen.CentreFrame(fileCategoryEditorFrame);
      fileCategoryEditor = new FileCategoryEditor();
      fpp=fileCategoryEditor.getFilePropPanel();
      fileCategoryEditorFrame.getContentPane().add(fileCategoryEditor);
    }

    fileCategoryEditor.addPreviewPanel(createThumbnail4CategoryEditor());
    System.out.println(">>>"+imgkey_iterator);
    fpp.setFileInfo("Properties for mmkey:"+((Integer)currentList.get(imgkey_iterator)).intValue());
//    ora_mmquery mmq = (ora_mmquery)mq;
    fpp.setMMQuery(mq);
    fpp.setMMKeyVector(markedList);
    if (markedList.size()==0)
      fpp.queryResult(((Integer)markedList.get(imgkey_iterator)).intValue(),csp.getMagicWord());
    else
      fpp.queryResult(((Integer)markedList.get(0)).intValue(),csp.getMagicWord());
    fileCategoryEditorFrame.pack();
    fileCategoryEditorFrame.show();
    /* Does not load pics to increase performance */
/*
    com.solar.imgproc.ImgProc imgproc = new com.solar.imgproc.ImgProc();
    imgproc.loadImage(mq.readMMData(imgkey[imgkey_iterator],csp.getMagicWord()));
    ImageIcon imgicon = new ImageIcon(imgproc.getImage());
    JLabel jl = new JLabel(imgicon);
    cfp.addPicPanel(jl);
*/
  }
  public void slideShowProp() {
    if (ssppf == null) {
      ssppf = new JFrame("Slide Show Properties");
      ssppf.setSize(320,220);
      ssppf.setLocation(200,150);
      sspp = new SlideShowPropPanel(sprop);
      ssppf.getContentPane().add(sspp);
      sspp.setParent(ssppf);
      ssppf.setVisible(true);
    }
    else {
      ssppf.setVisible(true);
    }
  }

  public void markImage(int mmkey) {
    markedList.add(new Integer(mmkey));
    System.out.println("markImage >>>"+mmkey);
  }
} /* end class PicFrame */
