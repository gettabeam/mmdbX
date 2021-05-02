package com.solar.mmquery;

import java.io.FileFilter;
import java.io.File;

/**
 * Title:        mmquery
 * Description:  mmquery lib
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class mm_filefilter implements FileFilter {
  static String[] mmexts = {".PNG",".GIF",".JPG",".JPEG"};
  public mm_filefilter() {
  }
  public boolean accept(File pathname) {
    if (pathname.isDirectory())
      return true;
    boolean accept=false;
    for (int i=0;i<mmexts.length;i++) {
      if (pathname.getName().toUpperCase().endsWith(mmexts[i])) {
        accept=true;
        break;
      }
    }
    return accept;
  }
}