package com.solar.utility;

import java.security.*;
import java.security.spec.*;
import java.io.*;


/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class FileSign {

  PublicKey pubKey = null;
  PrivateKey privKey = null;
  Signature dsa_priv = null;

  public static void main(String argv[]) throws Exception {
    PublicKey pubKey = null;
    FileSign fs = new FileSign();
    fs.loadKeys("pubkey","privkey");
    FileInputStream fis = new FileInputStream("utility.jar");
    int i=0;
    i=fis.available();
    while (i>=0 ) {
      byte[] buf = new byte[i];
      fis.read(buf);
      System.out.println(i+"::"+buf.length);
      fs.feedData(buf);
      i=fis.available();
      if (i==0)
        i=-1;
    }
    fis.close();
    System.out.println("Calculating signature....");
    byte[] sig = fs.getSig();
    System.out.println("Signature length="+sig.length);

    pubKey = fs.getPublicKey();
    System.out.println("PublicKey algorithm :"+pubKey.getAlgorithm());
    Signature vsig = Signature.getInstance("SHA1withDSA", "SUN");
    vsig.initVerify(pubKey);

    FileInputStream datafis = new FileInputStream("utility.jar");
    BufferedInputStream bufin = new BufferedInputStream(datafis);

    byte[] buffer = new byte[1024];
    int len;
    while (bufin.available() != 0) {
      len = bufin.read(buffer);
      vsig.update(buffer, 0, len);
    };
    bufin.close();
    System.out.println("Check signature "+vsig.verify(sig));
  }
  public FileSign() {
  }
  public PublicKey getPublicKey() {return pubKey;}
  public PrivateKey getPrivateKey() {return privKey;}

  public byte[] loadFile(String file) {
    try {
      FileInputStream fis = new FileInputStream(file);
      byte[] data = new byte[fis.available()];
      fis.read(data);
      fis.close();
      return  data;
    } catch (Exception e) {
      MyLogger.logger.error("Unable to load file "+file);
      return null;
    }
  }

  public boolean loadKeys(String spub, String spriv) {
    try {
      byte[] encKey = loadFile(spub);
      MyLogger.logger.debug("Public key length :"+encKey.length);
      X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
      KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
      pubKey = keyFactory.generatePublic(pubKeySpec);

      encKey = loadFile(spriv);
      MyLogger.logger.debug("Private key length :"+encKey.length);
      PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(encKey);
      privKey = keyFactory.generatePrivate(privKeySpec);

      dsa_priv = Signature.getInstance("SHA1withDSA", "SUN");
      dsa_priv.initSign(privKey);
      return true;
    } catch (Exception e) {
    	MyLogger.logger.error(e.getMessage());
      return false;
    }
  }
  public  void feedData(byte[] data) {
    try {
      if (dsa_priv != null) {
        dsa_priv.update(data,0,data.length);
      }
    } catch (Exception e) {
    	MyLogger.logger.error(e.getMessage());
    }
  }

  public byte[] getSig() {
    try {
      byte[] realSig = dsa_priv.sign();
      return realSig;
    } catch (Exception e) {
    	MyLogger.logger.error(e.getMessage());

      return null;
    }
  }

}