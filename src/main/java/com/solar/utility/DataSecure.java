package com.solar.utility;

import java.lang.*;
import java.security.*;
import java.security.spec.*;
import java.security.interfaces.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import javax.crypto.interfaces.*;
import com.sun.crypto.provider.*;


/*

Update Date
2004-12-30

 */

public class DataSecure {
  public static void main (String argv[]) {
    DataSecure jj = new DataSecure();
    jj.testPBE("abc123","Hello World !");
    jj.BlowFishTest();
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
        MyLogger.logger.error(e.getMessage());
    }
    return null;
  }
  public byte[] genBlowFishKey(int keysize) {
    try {
      Provider sunJce = new com.sun.crypto.provider.SunJCE();
      Security.addProvider(sunJce);
      KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
      kgen.init(448); // range from 32 to 448
      SecretKey skey = kgen.generateKey();
      byte[] raw = skey.getEncoded();
      return raw;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public Cipher getBlowFishCipher(byte[] key, int mode) {
    if (key == null) {
      MyLogger.logger.error("No key is provided!");
      return null;
    }
    try {
      Cipher cipher = Cipher.getInstance("Blowfish");
      SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
      cipher.init(mode, skeySpec);
      return cipher;
    } catch (Exception e) {
        MyLogger.logger.error(e.getMessage());
    }
    return null;
  }

  public byte[] useBlowFish(int mode,byte[] key,byte[] datain) {
    Cipher cipher = getBlowFishCipher(key,mode);
    try {
      byte[] dataout = cipher.doFinal(datain);
      return dataout;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }


  public void BlowFishTest()  {
  	// Install SunJCE provider
        System.out.println("Original text");
        String origintext="This is just an example";
        byte[] b=origintext.getBytes();
        for (int i=0;i<b.length;i++)
          System.out.print(b[i]+",");
        System.out.println();

        System.out.println("Generate BlowFish Key");
        byte [] raw=genBlowFishKey(448);
        System.out.println("BlowFish key length = "+raw.length);

        System.out.println("Encrypted bytes");
        byte[] ciphertext = useBlowFish(Cipher.ENCRYPT_MODE,raw,origintext.getBytes());

        for (int i=0;i<ciphertext.length;i++)
          System.out.print(ciphertext[i]+",");
        System.out.println();

        System.out.println("Decrypted bytes");
        byte[] decryptedtext = useBlowFish(Cipher.DECRYPT_MODE,raw,ciphertext);
        for (int i=0;i<decryptedtext.length;i++)
          System.out.print(decryptedtext[i]+",");
        System.out.println();
  }

  public Cipher pbeEncryptCipher(String password) {
    try {
      return getPBECipher(password,Cipher.ENCRYPT_MODE);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Cipher pbeDecryptCipher(String password) {
    try {
      return getPBECipher(password,Cipher.DECRYPT_MODE);
    } catch (Exception e) {
      
      MyLogger.logger.error(e.getMessage());
      return null;
    }
  }

  public byte[] encryptWithPBE(String password,byte[] cleartext) {
    try {
      Cipher pbeCipher= getPBECipher(password,Cipher.ENCRYPT_MODE);
      byte[] ciphertext = pbeCipher.doFinal(cleartext);
      return ciphertext;
    } catch (Exception e) {
        MyLogger.logger.error(e.getMessage());
      return null;
    }
  }

  public byte[] decryptWithPBE(String password,byte[] ciphertext) {
    try {
 //   System.out.println("encryptkey:"+password+" Size:"+ciphertext.length);
      Cipher pbeCipher= getPBECipher(password,Cipher.DECRYPT_MODE);
      byte[] cleartext = pbeCipher.doFinal(ciphertext);
      return cleartext;
    } catch (Exception e) {
        MyLogger.logger.error(e.getMessage());
   //   e.printStackTrace();
   //   System.out.println("DataSecure.decryptWithPBE(): ERR in decryption!");
      return null;
    }
  }


  public void testPBE(String password, String text) {
    try {
    Cipher pbeCipher= getPBECipher(password,Cipher.ENCRYPT_MODE);
    byte[] cleartext = text.getBytes();
 //   System.out.println("Original Text:"+text);
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
