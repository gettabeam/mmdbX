package com.solar.imgproc;

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.*;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.*;
//import com.sun.image.codec.jpeg.JPEGCodec;
//import com.sun.image.codec.jpeg.JPEGImageEncoder;
import javax.imageio.*;
import com.solar.utility.*;
/*****************************************************/
/*                                                   */
/* ImgProc                                           */
/*                                                   */
/*                                                   */
/*                                                   */
/*                                                   */
/*                                                   */
/* When       What                                   */
/* 2001-05-19 Add anti-aliasing for better quality   */
/* 2001-12-29 Add methods to get Image properties    */
/* 2004-08-16 Add toByteArray()                      */
/* 2004-08-27                                        */
/* 2004-08-31                                        */
/*****************************************************/


public class ImgProc {
  String srcfile=null;
  String desfile=null;
  int iH=0;
  int iW=0;
  int size=0;
  Image inImage=null;
  BufferedImage outBImage=null;

  public static void main(String[] args) {
    ImgProc mm= new ImgProc();
    String file="d:\\large_jpg.jpg";
    mm.loadImage(file);
    mm.scaleToPixel(1000);
 /*
        mm.createImage(300,400);
        mm.fillShape();
        mm.drawCastBoard();
 */
//    mm.printText("HelloWorld");
 //   	  mm.filterEdgeDetect();
//    mm.filterReverse();
//    mm.saveAsJPEG(args[1]);
    System.exit(0);
  }

    public int getHeight() {
      return iH;
    }

    public int getWidth() {
      return iW;
    }
    public int getSize() {
      return size;
    }
    /*****************************************/
    /* Create blank sheet                    */
    /*****************************************/

    /*****************************************/
    /* Load image from byte[]                */
    /*****************************************/
    public void clear() {
      outBImage = null;
      inImage = null;
      iH=0;
      iW=0;
      srcfile=null;
      desfile=null;
    }

    public void loadImage(byte b[]) {
//    	srcfile = new String(orig);
    	long t0=System.currentTimeMillis();
      try {	
      size=b.length;
      inImage = new ImageIcon(b).getImage();
      iH=inImage.getHeight(null);
      iW=inImage.getWidth(null);

//      outBImage = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
 //    Graphics2D g2d = outBImage.createGraphics();

  //    g2d.drawImage(inImage,null,null);
  //    g2d.dispose();
      System.out.println("loadImage(byte[])>size:"+size+","+iH+"x"+iW);
      } catch (Exception e) {
    	  e.printStackTrace();
    	  MyLogger.logger.error(e.getMessage());
      }
    }

    /*****************************************/
    /* Load image from file                  */
    /*****************************************/
    public void loadImage(String orig) {
      long t0=System.currentTimeMillis();
/*
      File f = new File(orig);
      int filesize=(int)f.length();
      byte[] buf = new byte[filesize];
      try {
        FileInputStream fis = new FileInputStream(f);
        fis.read(buf,0,filesize);

        inImage = new ImageIcon(buf).getImage();
        iH=inImage.getHeight(null);
        iW=inImage.getWidth(null);
        System.out.println("image size:"+iH+"x"+iW+" "+filesize+"B");

      } catch (Exception e) {
        inImage=null;
        iH=-1;
        iW=-1;
        srcfile=null;
      }

      //   outBImage = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      //   Graphics2D g2d = outBImage.createGraphics();
      //   g2d.drawImage(inImage,null,null);
      //   g2d.dispose();
      System.out.println("Total time for loading image using new method: "+(System.currentTimeMillis()-t0));
*/
      t0=System.currentTimeMillis();
      srcfile = new String(orig);
      inImage = new ImageIcon(orig).getImage();
      iH=inImage.getHeight(null);
      iW=inImage.getWidth(null);
      
     //   MyLogger.logger.info("Load time of "+orig+":"+(System.currentTimeMillis()-t0)+" Image size:"+iH+"x"+iW);
      System.out.println("Load time of "+orig+":"+(System.currentTimeMillis()-t0)+" Image size:"+iH+"x"+iW);


    }

    public void synLoadImage(String orig) {
      loadImage(orig);
    }

    private void noScale() {
    	 if (outBImage==null) {
           outBImage = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
           Graphics2D g2d = outBImage.createGraphics();
           g2d.drawImage(inImage,null,null);
           g2d.dispose();
    	 }

    }

    /*****************************************/
    /* Scale the input image to defined size */
    /*****************************************/
    public  void scaleToPixel(int maxDim) {
      long t0=System.currentTimeMillis();
        // Determine the scale.
        double scale = 1.0;
        if (maxDim>0) {
  	      scale = (double)maxDim/(double)iH;
          if (iW > iH)
           scale = (double)maxDim/(double)iW;
          if (scale>=0.9 )
            scale = 1.0;
        }

        if (scale== 1.0) {
          noScale();
          return;
        }

        // Determine size of new image.
        // One of them should equal maxDim.
        int scaledW = (int)(scale*iW);
        int scaledH = (int)(scale*iH);

        // Create an image buffer in
        // which to paint on.
        outBImage = new BufferedImage(scaledW, scaledH,BufferedImage.TYPE_INT_RGB);
        // Set the scale.
        AffineTransform tx = new AffineTransform();
        // If the image is smaller than
        // the desired image size, don't bother scaling.
        //if (scale < 1.0d) {
          tx.scale(scale, scale);
        //}

        Graphics2D g2d = outBImage.createGraphics();

        /* Add anti-aliasing to improve image quality */
       	RenderingHints qualityHints = new  RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        qualityHints.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(qualityHints);

        g2d.drawImage(inImage, tx, null);

        g2d.dispose();
//        iW=scaledW;
//        iH=scaledH;
        System.out.println("scaleToPixel():"+scaledW+"x"+scaledH);
//        System.out.println("Rescale time:"+(System.currentTimeMillis()-t0));
    }
    /*****************************************/
    /* Save output as JPEG                   */
    /*****************************************/
    public void printText(String text) {
    	Graphics2D g2d = outBImage.createGraphics();
    	g2d.setColor(Color.red);
    	g2d.drawString(text,iW/2,iH/2);
    	g2d.dispose();
    }

    public void filterShapen() {
      float[] SHARPEN3x3 = {
                            0.f, -1.f, 0.f,
                            -1.f, 5.0f, -1.f,
                            0.f, -1.f, 0.f};
      BufferedImage dstbimg = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      Kernel kernel = new Kernel(3,3,SHARPEN3x3);
      ConvolveOp cop = new ConvolveOp(kernel,ConvolveOp.EDGE_NO_OP,null);
      cop.filter(outBImage,dstbimg);
      outBImage=dstbimg;
    }



    public void filterBur() {
      float weight = 1.0f/9.0f;
      float[] elements = new float[9]; // create 2D array

      // fill the array with nine equal elements
      for (int i = 0; i < 9; i++) {
        elements[i] = weight;
      }
      // use the array of elements as argument to create a Kernel
      BufferedImage dstbimg = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      Kernel myKernel = new Kernel(3, 3, elements);
      ConvolveOp simpleBlur = new ConvolveOp(myKernel);

      // sourceImage and destImage are instances of BufferedImage
      simpleBlur.filter(outBImage, dstbimg); // blur the image
      outBImage=dstbimg;
    }

    public void filterIntensity() {
      BufferedImage dstbimg = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      RescaleOp rop = new RescaleOp(1.5f, 1.0f, null);
      rop.filter(outBImage,dstbimg);
      outBImage=dstbimg;
    }

    public void filterEdgeDetect() {
      float[] elements = { 0.0f, -1.0f, 0.0f,-1.0f, 4.f, -1.0f, 0.0f, -1.0f, 0.0f};
      BufferedImage dstbimg = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
      Kernel kernel = new Kernel(3, 3, elements);
      ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP,null);
      cop.filter(outBImage,dstbimg);
      outBImage=dstbimg;
    }

    public void filterReverse() {

    	byte reverse[] = new byte[256];
/*
    	for (int j=0; j<200; j++){
       		reverse[j]=(byte)(256-j);
	    }
*/
    	for (int j=0; j<256; j++){
    		  if (j<100)
    		  	reverse[j]=(byte)100;
    		  else
       		  reverse[j]=(byte)j;
	    }

   	  ByteLookupTable blut=new ByteLookupTable(0, reverse);
   	  LookupOp lop = new LookupOp(blut, null);
			BufferedImage dstbimg = new BufferedImage(iW,iH,BufferedImage.TYPE_INT_RGB);
			lop.filter(outBImage,dstbimg);
      outBImage=dstbimg;
    }

    public void saveAsJPEG(String desc) {
        try {
        	OutputStream os = new FileOutputStream(desc);
        	this.setOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getImage() {
    	return outBImage;
    }

    public ImageIcon getImageIcon() {
      if (outBImage==null)
        return new ImageIcon(inImage);
      else
        return new ImageIcon((Image)outBImage);
    }

    public  byte[] toByteArray() {
      if (outBImage==null)
              return null;
      java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
      this.setOutputStream(baos);
      return baos.toByteArray();

    }

    public void setOutputStream(OutputStream os) {
        // JPEG-encode the image and direct to an output stream
        if (os==null)
        	return;
        if (outBImage==null)
        	noScale();
        try {
          ImageIO.write(outBImage,"jpg",os);
 //         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
 //         encoder.encode(outBImage);
          os.close();
          MyLogger.logger.info("using new ImageIO to write file");
        } catch (IOException e) {
        	e.printStackTrace();
        	MyLogger.logger.error(e.getMessage());
        //	System.out.println(e.getMessage());
        }
    }

    public synchronized void setSynOutputStream(OutputStream os) {
      setOutputStream(os);
    }

    protected void finalize() throws Throwable {
    	try {
    		if (outBImage!=null) {
    			outBImage.flush();
    			outBImage=null;
    		}
    		if (inImage!=null) {
    			inImage.flush();
    			inImage=null;
    		}
    
    	} catch (Exception e) {
    		
    	}  finally {
    		super.finalize();
    	}
    }
}
class ImgProcTest1 {
  public static void main(String[] args) {
 	  ImgProc mm= new ImgProc();
 	  mm.loadImage(args[0]);
 	  mm.scaleToPixel(Integer.parseInt(args[2]));
 	  mm.printText("HelloWorld");
 //   	  mm.filterEdgeDetect();
 //	  mm.filterReverse();
 	  mm.saveAsJPEG(args[1]);
 	  System.exit(0);
  }
}
