package com.solar.utility;

import java.io.*;
import java.security.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class GenSig {

    public static void main(String[] args) {

        /* Generate a DSA signature */

        try {

          /* initialize using DSA provided by SUN */
          KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
          /* using SHA1PRNG pseudo-random-number generation provided by SUN */
          SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
          /* Key size for DSA is 1024 bit */
          keyGen.initialize(1024, random);

          /* generate key pair */
          KeyPair pair = keyGen.generateKeyPair();
          PrivateKey priv = pair.getPrivate();
          PublicKey pub = pair.getPublic();

          /* get a signature object using DSA */
          Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
          /* initialize the object with private key */
          dsa.initSign(priv);

          /* read input file and  supply to signature object*/
          if (args.length>=1) {
            FileInputStream fis = new FileInputStream(args[0]);
            BufferedInputStream bufin = new BufferedInputStream(fis);
            byte[] buffer = new byte[1024];
            int len;
            while (bufin.available() != 0) {
              len = bufin.read(buffer);
              dsa.update(buffer, 0, len);
            };
            bufin.close();

            /* generate the signature */
            byte[] realSig = dsa.sign();

            /* save the signature in a file */
            FileOutputStream sigfos = new FileOutputStream("sig");
            sigfos.write(realSig);
            sigfos.close();

          /* save the public key in a file */
          }

          byte[] key = pub.getEncoded();
     //     System.out.println("Public key is generated using "+pub.getAlgorithm());
  //        System.out.println("Public Key is encoded using "+pub.getFormat());

          FileOutputStream keyfos = new FileOutputStream("pubkey");
          keyfos.write(key);
          keyfos.close();

          byte[] prikey = priv.getEncoded();
     //     System.out.println("Private key is generated using "+priv.getAlgorithm());
     //     System.out.println("Private Key is encoded using "+priv.getFormat());
          keyfos = new FileOutputStream("privkey");
          keyfos.write(prikey);
          keyfos.close();


        } catch (Exception e) {
            System.err.println("Caught exception " + e.toString());
        }
    }

  public GenSig() {
  }
}