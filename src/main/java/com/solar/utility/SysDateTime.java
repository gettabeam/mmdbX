package com.solar.utility;

import java.lang.*;
import java.text.*;

public class SysDateTime {
  public static void main(String argv[]) {
    System.out.println(SysDateTime.getDate()+"\n");
    System.out.println(SysDateTime.getTime()+"\n");
  }
  public SysDateTime() {
  }

  public static String getDate() {
    long d = System.currentTimeMillis();
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    return df.format(new java.util.Date(d));
  }
  public static String getTime() {
    long d = System.currentTimeMillis();
    SimpleDateFormat df = new SimpleDateFormat("HHmmss");
    return df.format(new java.util.Date(d));
  }
}
