package com.solar.utility;

/**
  Purpose: Misc functions for controlling screen placement
  History:
  2002-06-11  Initial Version
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Vector;
import java.util.*;
import javax.swing.border.*;

public class screen {

  public screen() {
  }

  public static void CentreFrame(JFrame f) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = f.getSize();
    if (frameSize.height > screenSize.height)
      frameSize.height = screenSize.height;
    if (frameSize.width > screenSize.width)
      frameSize.width = screenSize.width;
    f.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
  }
}