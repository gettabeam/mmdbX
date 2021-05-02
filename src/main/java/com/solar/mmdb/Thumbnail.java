package com.solar.mmdb;

import javax.swing.JLabel;
import java.awt.event.*;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.*;

/**
 * <p>Title: Multimedia Database</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Millennium Consultancy</p>
 * @author not attributable
 * @version 1.0
 *
 * Date :
 * 20040831
 */

public class Thumbnail extends JLabel {
  com.solar.mmdb.PicFrame pf = null;
  int mmkey = 0;
  int width=0;
  int height=0;
  int size=0;
  boolean bMarked=false;
  boolean enableWheel = false;
//  TitledBorder titledBorder1;
  public Thumbnail() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  public Thumbnail(javax.swing.ImageIcon ic) {
    super(ic);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  public Thumbnail(String ss) {
    super(ss);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

  }

  public void setParent(PicFrame inpf) {
    this.pf = inpf;
  }

  public void setMMKey(int key) {
    this.mmkey = key;
  }

  public void setImgInfo(int w,int h, int s) {
    width=w;
    height=h;
    size=s/1000;
    this.setToolTipText("mmkey:"+mmkey+" Size:"+w+"x"+h+" "+size+"KB" );

  }
  private void jbInit() throws Exception {
//    titledBorder1 = new TitledBorder("TEST");
    this.setDoubleBuffered(true);
//    this.addMouseWheelListener(new Thumbnail_this_mouseWheelAdapter(this));
    this.addMouseListener(new Thumbnail_this_mouseAdapter(this));
  }

  public void enableWheel() {
    this.addMouseWheelListener(new Thumbnail_this_mouseWheelAdapter(this));
  }
  void this_mouseClicked(MouseEvent e) {

    if (e.getButton()==e.BUTTON3) {
      System.out.println("Button 2 clicked");
      pf.markImage(mmkey);
      bMarked=!bMarked;
    }
    if (bMarked) {
      this.setBorder(BorderFactory.createEtchedBorder(Color.red,Color.white));
//      this.setBorder(BorderFactory.createLineBorder(new Color(255,0,0)));
    }
    else {
      this.setBorder(null);
    }

    if (e.getButton()==e.BUTTON1) {
      pf.showImgByMMKey(mmkey);
    }

  }

  void this_mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation()<0) {
      pf.image_nevigate(nevigator.NEV_PREV);
    }
    if (e.getWheelRotation()>0) {
      pf.image_nevigate(nevigator.NEV_NEXT);
    }
  }

}

class Thumbnail_this_mouseAdapter extends java.awt.event.MouseAdapter {
  Thumbnail adaptee;

  Thumbnail_this_mouseAdapter(Thumbnail adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.this_mouseClicked(e);
  }
}

class Thumbnail_this_mouseWheelAdapter implements java.awt.event.MouseWheelListener {
  Thumbnail adaptee;

  Thumbnail_this_mouseWheelAdapter(Thumbnail adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseWheelMoved(MouseWheelEvent e) {
    adaptee.this_mouseWheelMoved(e);
  }
}
