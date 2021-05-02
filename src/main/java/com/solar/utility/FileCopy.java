package com.solar.utility;

import java.io.*;

public class FileCopy {

  public static void main(String argv[]) {
    int i=0;
    if (argv[0].equals("mv"))
      i=FileCopy.move(argv[1],argv[2]);
    else
      i=FileCopy.copy(argv[1],argv[2]);
    if (i!=0)
       System.out.println("Error !!");
  }

  public static int move(String srcfile,String destfile) {
    int i=0;
    i=copy(srcfile,destfile);
    if (i==0) {
      File orgfile = new File(srcfile);
      if (orgfile.delete())
        return 0;
      else
        return -1;
    }
    else
     return i;
  }

  public static int copy(String srcfile,String destfile) {
    FileInputStream fis=null;
    FileOutputStream fos=null;
    try {
    fis = new FileInputStream(srcfile);
    fos = new FileOutputStream(destfile);
    byte buf[] = new byte[5000000];
    int i=fis.read(buf);
    while (i>=0) {
      fos.write(buf,0,i);
      i=fis.read(buf);
    }
    fis.close();
    fos.close();
    } catch (Exception e) {
      try {
      fis.close();
      fos.close();
      } catch (Exception ex) {}
      return -1;
    }
    File orgfile = new File(srcfile);
    File newfile = new File(destfile);
    if (orgfile.length()!=newfile.length())
      return -1;
    else
      return 0;

  }

}
