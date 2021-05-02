package com.solar.utility;

import java.lang.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.*;


/**
 * Title:        JCE Test with PBE
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 * Add this line to java.Security
 * security.provider.2=com.sun.crypto.provider.SunJCE
 */

public class jcetest {
  public static void main (String argv[]) {
    jcetest jj = new jcetest();
    jj.testPBE("abc123","Hello World !");
    jj.getBlowFishCipher();
  }

  /********************************/
  /*     PBE Implementation       */
  /********************************/
  public Cipher getPBECipher(String password,int mode) {
    PBEKeySpec pbeKeySpec;
    PBEParameterSpec pbeParamSpec;
    SecretKeyFactory keyFac;

    // Salt
    byte[] salt = {
        (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c,
        (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99
    };

    // Iteration count
    int count = 50;
    try {
    // Create PBE parameter set
    pbeParamSpec = new PBEParameterSpec(salt, count);

    // Prompt user for encryption password.
    // Collect user password as char array (using the
    // "readPasswd" method from above), and convert
    // it into a SecretKey object, using a PBE key
    // factory.
    pbeKeySpec = new PBEKeySpec(password.toCharArray());
    keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
//    keyFac = new com.sun.crypto.provider.PBEWithMD5AndDESCipher();
    SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

    // Create PBE Cipher
    Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
    pbeCipher.init(mode, pbeKey, pbeParamSpec);

    return pbeCipher;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public void getBlowFishCipher()  {
  	// Install SunJCE provider
        try {
	Provider sunJce = new com.sun.crypto.provider.SunJCE();
	Security.addProvider(sunJce);


        System.out.println("Generate Blowfish key");

	KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
	SecretKey skey = kgen.generateKey();
	byte[] raw = skey.getEncoded();
	SecretKeySpec skeySpec = new SecretKeySpec(raw, "Blowfish");

        System.out.println("Original text");
        String origintext="This is just an example";
        byte[] b=origintext.getBytes();
        for (int i=0;i<b.length;i++)
          System.out.print(b[i]+",");
        System.out.println();

        Cipher cipher = Cipher.getInstance("Blowfish");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        System.out.println("Encrypted bytes");
        byte[] encrypted = cipher.doFinal(origintext.getBytes());

        for (int i=0;i<encrypted.length;i++)
          System.out.print(encrypted[i]+",");
        System.out.println();


        Cipher decrypt = Cipher.getInstance("Blowfish");
        decrypt.init(Cipher.DECRYPT_MODE, skeySpec);
        System.out.println("Decrypted bytes");

        byte[] decryptedtext = cipher.doFinal(encrypted);
        for (int i=0;i<decryptedtext.length;i++)
          System.out.print(decryptedtext[i]+",");
        System.out.println();
        } catch (Exception e) {
          e.printStackTrace();
        }
  }


  public void testPBE(String password, String text) {
    try {
    Cipher pbeCipher= getPBECipher(password,Cipher.ENCRYPT_MODE);
    byte[] cleartext = text.getBytes();
    System.out.println("Original Text:"+text);
    for (int i=0;i<cleartext.length;i++) {
      System.out.print(cleartext[i]+",");
    }
    System.out.println();
    // Encrypt the cleartext
    System.out.println("Encrypted bytes");
    byte[] ciphertext = pbeCipher.doFinal(cleartext);
    for (int i=0;i<ciphertext.length;i++)
      System.out.print(ciphertext[i]+",");
    System.out.println();

    System.out.println("Decrypted bytes");
    Cipher pbeDecrypt= getPBECipher(password, Cipher.DECRYPT_MODE );
    byte[] decrypttext = pbeDecrypt.doFinal(ciphertext);
    for (int i=0;i<decrypttext.length;i++)
      System.out.print(decrypttext[i]+",");
    System.out.println();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}