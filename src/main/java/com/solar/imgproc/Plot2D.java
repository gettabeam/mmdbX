package com.solar.imgproc;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import javax.imageio.*;

/*****************************************************/
/*                                                   */
/* ImgProc                                           */
/*                                                   */
/* When				What                                   */
/* 2001-05-19 Add anti-aliasing for better quality   */
/*                                                   */
/*                                                   */
/*                                                   */
/*****************************************************/

public class Plot2D extends ImgProc {
        double x0=0;
        double	y0=0;
        ScaleInfo si=null;
        Vector datastore = new Vector();
        int horzv[]=null;
        int vertv[]=null;
        static Color[] cc = { Color.red,Color.blue,Color.orange,Color.green,Color.cyan,Color.pink};
    public static void main(String[] args) {
              ImgProc mm= new Plot2D();
              mm.loadImage(args[0]);
              mm.scaleToPixel(Integer.parseInt(args[2]));
 /*
        mm.createImage(300,400);
        mm.fillShape();
        mm.drawCastBoard();
 */
              mm.printText("HelloWorld");
 //   	  mm.filterEdgeDetect();
              mm.filterReverse();
              mm.saveAsJPEG(args[1]);
              System.exit(0);
    }

    /*****************************************/
    /* Create blank sheet                    */
    /*****************************************/
    public void createImage(int H, int W) {
            iH=H;
            iW=W;
      outBImage = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      si=new ScaleInfo();
      si.setScreenSize(H,W);
    }

    public void addData(Object v) {
      datastore.add(v);
    }
    public void setVert(int[] vv) {
            vertv = vv;
    }
    public void setHorz(int[] hh) {
            horzv = hh;
    }

    public void fillShape() {
      Graphics2D g2d = outBImage.createGraphics();
      Color c1 = new Color(200,200,255);
      GradientPaint gp = new
      GradientPaint(0f,0f,c1,0f,80f,Color.white);
      g2d.setPaint(gp);
      g2d.fill(new java.awt.geom.RoundRectangle2D.Double(0, 0, iW,iH,20,20));
      g2d.dispose();
    }
/*
    public int xx(double x){
                  double X=10;
                  double Y=10;
                  double padding=20;
            double xx=((double)iW-2*padding)/X*x+padding;
            return (int)xx;
    }

    public int yy(double y){
                  double X=10;
                  double Y=10;
                  double padding=20;
            double yy=((double)iH-2*padding)/Y*(Y-y)+padding;
            return (int)yy;

    }
*/
public void drawCastBoard() {

  Graphics2D g2d = outBImage.createGraphics();
  g2d.setColor(new Color(180,180,180));

  for (int ii=(int)si.yl;ii<=(int)si.yu;ii+=(int)si.ys) {
    g2d.drawLine(si.xx(si.xl),si.yy(ii),si.xx(si.xu),si.yy(ii));
  }

  for (int i=(int)si.xl;i<=(int)si.xu;i+=(int)si.xs) {
    g2d.drawLine(si.xx(i),si.yy(si.yl),si.xx(i),si.yy(si.yu));
  }

  g2d.setColor(new Color(150,150,150));

  /* x-axis label */

  for (int i=(int)si.xl;i<=(int)si.xu;i+=(int)si.xs) {
    int ix=si.xx(i);
    int iy=si.yy(si.yl)+12;
    g2d.translate(ix,iy);
    g2d.rotate(0.8);
    int ii=i;
    if (ii>=13) ii-=12;
    g2d.drawString(String.valueOf(ii),0,0);
    g2d.rotate(-0.8);
    g2d.translate(-1*ix,-1*iy);

    //	  g2d.drawString(String.valueOf(i),si.xx(i),si.yy(si.yl)+12);
  }
  /* y-axis label */
  for (int ii=(int)si.yl;ii<=(int)si.yu;ii+=(int)si.ys) {
    g2d.drawString(String.valueOf(ii),si.xx(si.xl)-35,si.yy(ii));
  }

  // g2d.rotate(-2);
}

  public void plot(double x,double y) {

    Graphics2D g2d = outBImage.createGraphics();
    g2d.setColor(new Color(0,0,0));
    g2d.drawLine(si.xx(x)-3,si.yy(y)-3,si.xx(x)+3,si.yy(y)+3);
    //  g2d.setColor(new Color(0,0,0));
    //  g2d.drawLine(0,iH/Y*5,iW,iH/Y*5);
  }

  public void plot2(double x,double y,Color c1) {

    Graphics2D g2d = outBImage.createGraphics();

    GradientPaint gp = new
        GradientPaint(si.xx(x)-3,si.yy(y),c1,si.xx(x)+3,si.yy(y)+10,Color.white);
    g2d.setPaint(gp);
    g2d.fill(new
             java.awt.geom.Rectangle2D.Double(si.xx(x)-3,si.yy(y),6,10));
    g2d.setColor(c1);
    if (x0==0 && y0==0) {
      x0=x;y0=y;
    }
    g2d.drawLine(si.xx(x0),si.yy(y0),si.xx(x),si.yy(y));
    x0=x;y0=y;
    g2d.dispose();

    //  g2d.setColor(new Color(0,0,0));
    //  g2d.drawLine(0,iH/Y*5,iW,iH/Y*5);
  }

  public void plot() {
    for (int i=0;i<vertv.length;i++) {
      double[] vpts=(double[])datastore.get(vertv[i]);
      if (vpts==null) {break;}
      x0=0;y0=0;
      Color c=cc[i%cc.length];
      for (int j=0;j<vpts.length;j++)
        //                if (j<=12)
        plot2(j+1,vpts[j],c);
        //                else {
//                  c=cc[(i+1)%cc.length];
//                  plot2(j+1,vpts[j],c);
//                }
    }
  }
}

class Plot2DTest1 {
public static void main(String[] args) {
  Plot2D mm= new Plot2D();
  double line1[] = new double[20];
  double line2[] = new double[20];
      int[] v = {0,1};
      line1[0]=14608.5;
      line1[1]=13172.5;
      line1[2]=34930.0;
      line1[3]=17889.8;
      line1[4]=11814.4;
      line1[5]=11519.05;
      line1[6]=14150.2;
      line1[7]=10869.7;
      line1[8]=33597.5;
      line1[9]=14620.87;
      line1[10]=14608.5;
           line1[11]=13172.5;
           line1[12]=34930.0;
           line1[13]=17889.8;
           line1[14]=11814.4;
           line1[15]=11519.05;
           line1[16]=14150.2;
           line1[17]=10869.7;
           line1[18]=33597.5;
           line1[19]=14620.87;
           for (int i=0;i<line1.length;i++) {
                   line2[i]=line1[i]+3000.0;
           }
           mm.addData(line1);
           mm.addData(line2);
           mm.setVert(v);

 //	  mm.loadImage(args[0]);
 //	  mm.scaleToPixel(Integer.parseInt(args[2]));
        mm.createImage(400,500);
        mm.fillShape();
        mm.drawCastBoard();
   /*
        mm.plot2(1,14608.5);
        mm.plot2(2,13172.5);
        mm.plot2(3,34930.0);
        mm.plot2(4,17889.8);
        mm.plot2(5,11814.4);
        mm.plot2(6,11519.05);
        mm.plot2(7,14150.2);
        mm.plot2(8,10869.7);
        mm.plot2(9,33597.5);
        mm.plot2(10,14620.87);
        mm.plot2(11,23232.6);
        mm.plot2(12,9381.8);
        mm.plot2(13,15117.1);
        mm.plot2(14,4471.2);
     */

        mm.plot();

           mm.printText("HelloWorld");
 //   	  mm.filterEdgeDetect();
 //	  mm.filterReverse();
           mm.saveAsJPEG(args[1]);
           System.exit(0);
  }
}


class ScaleInfo {
        public double X=24;
        public double Y=8;
        public double padding=50;
        public double yl=0;
        public double yu=40000;
        public double xl=0;
        public double xu=X;
        public double iW=0;
        public double iH=0;
        public double xs=0;
        public double ys=0;
        public void setScreenSize(double h,double w) {
                iW=w;
                iH=h;
          xs=(xu-xl)/X;
          ys=(yu-yl)/Y;

        }

  public int xx(double x)  	{
          x=(x-xl)/((xu-xl)/X);
          double xx=((double)iW-2*padding)/X*x+padding;
           return (int)xx;
  }

  public int yy(double y){
          y=(y-yl)/((yu-yl)/Y);
          double yy=((double)iH-2*padding)/Y*(Y-y)+padding;
          return (int)yy;
  }


}
