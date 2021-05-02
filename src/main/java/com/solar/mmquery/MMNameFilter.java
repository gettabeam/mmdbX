package com.solar.mmquery;

import java.io.FilenameFilter;
import java.io.File;

/**
 * Title:        mmquery
 * Description:  mmquery lib
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class MMNameFilter implements FilenameFilter {
  public MMNameFilter() {
  }
  public boolean accept(File dir, String name) {
    //System.out.println(">>>"+dir.getName());
    boolean found=false;
    for (int i=0;i<com.solar.mmquery.mm_filefilter.mmexts.length;i++) {
      if (name.toUpperCase().endsWith(com.solar.mmquery.mm_filefilter.mmexts[i])) {
        found=true;
        break;
      }
    }
    return found;
  }
}