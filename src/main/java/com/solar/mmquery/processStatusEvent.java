package com.solar.mmquery;

import java.util.*;

/**
 * Title:        mmquery
 * Description:  mmquery lib
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class processStatusEvent extends EventObject {
  Object sourceobj=null;
  public processStatusEvent(Object source) {
    super(source);
    sourceobj = source;
  }

  public String toString() {
    if (sourceobj == null)
      return "processStatusEvent:NULL OBJECT";
    else
      return sourceobj.toString();
  }

  public Object getObj() {
    return sourceobj;
  }
  /*
  String s=null;
  public processStatusEvent(Object source) {
    super(source);
    s=(String)source;
  }
  public String toString() {
    return s;
  }
*/

}