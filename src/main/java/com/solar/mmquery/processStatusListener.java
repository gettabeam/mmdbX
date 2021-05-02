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

public interface processStatusListener extends EventListener {
  public void dataChanged(com.solar.mmquery.processStatusEvent e);
}