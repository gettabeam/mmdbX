package com.solar.utility;

import java.io.*;

public class sorttest {
  public static void main(String argv[]) {

    MergeSort  fileMS = new MergeSort() {
	public int compareElementsAt(int a, int b) {
	    return ((String)toSort[a]).compareTo((String)toSort[b]);
	}
    };

    File fs = new File("C:\\");
    String filelist[] = fs.list();
    fileMS.sort(filelist);
    for (int i=0;i<filelist.length;i++)
      System.out.println(filelist[i]);

  }
  public sorttest() {
  }
}