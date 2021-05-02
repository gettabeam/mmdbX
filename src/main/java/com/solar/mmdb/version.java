package com.solar.mmdb;
import java.lang.*;

/*
  eric@20190407 - revive the project using new development approach - just for fun !!!

*/

public class version {
//  static public String info="com.solar.mmdb\t\t05.11.26.0";

//  static public String info="com.solar.mmdb\t\t2007-12-30";
  static public String info="com.solar.mmdb\t\t2019-04-07";

  static public String packagesInfo="\nPackages\t\tVersion\n-------------\t\t------------\n"+com.solar.mmdb.version.info+"\n"+com.solar.mmquery.version.info+"\n"+com.solar.utility.version.info+"\n"+com.solar.imgproc.version.info+"\n\nFree Memory\t"+Runtime.getRuntime().freeMemory()+" bytes\nTotal Memory\t"+Runtime.getRuntime().totalMemory()+" bytes";
  
  public version() {
  }

}
