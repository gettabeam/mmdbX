package com.solar.utility;

import java.security.MessageDigest;
import java.io.*;
import java.lang.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 **/

public class FileDigest {
  MessageDigest md = null;
  int filesize=0;
  public static void main(String argv[]) {
    String file=null;
    if (argv.length <1)
      file="utility.jar";
    else
      file=argv[0];
    FileDigest fd = new FileDigest();
    byte[] digest = fd.getDigest(file);
    try {
    FileOutputStream fos = new FileOutputStream(argv[1]);
    fos.write(digest);
    fos.close();
    } catch (Exception e) {
        MyLogger.logger.error(e.getMessage());
    }
    MyLogger.logger.info("Length="+digest.length);
  }

  public FileDigest() {
  }
/*
  public byte[] getDigest2(String filename) {
    try {
      md=MessageDigest.getInstance("MD5");
      FileInputStream fis = new FileInputStream(filename);
      System.out.println("Digesting "+filename);
      int i=fis.available();
      while (i>0) {
        byte[] buf = new byte[i];
        fis.read(buf);
        md.update(buf);
        i=fis.available();
      }
      fis.close();
      return md.digest();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
*/
    public byte[] getDigest(byte[] data) {
    try {
      md=MessageDigest.getInstance("MD5");
    //  System.out.println("Digesting binary array ...");
      md.update(data,0,data.length);
      return md.digest();
    } catch (Exception e) {
    	MyLogger.logger.error(e.getMessage());
      return null;
    }
  }

    public int getFileSize() {
      return filesize;
    }
    public byte[] getDigest(String filename) {
    try {
      filesize=0;
      md=MessageDigest.getInstance("MD5");
      FileInputStream fis = new FileInputStream(filename);
      byte[] buf=new byte[1024*1024];
      int i=fis.read(buf);
      while (i>=0) {
        filesize+=i;
        md.update(buf,0,i);
        i=fis.read(buf);
      }
      fis.close();
      return md.digest();
    } catch (Exception e) {
      MyLogger.logger.error("Error digesting file "+filename);
      MyLogger.logger.error(e.getMessage());

      return null;
    }
  }

}
