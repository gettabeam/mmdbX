package com.solar.mmdb;

/*
   MMDB Main Application


-- 20190407 - First version started with Git
*/

import javax.swing.UIManager;
import java.awt.*;
import java.io.*;
import com.solar.utility.*;

public class mmapp {
  boolean packFrame = false;

  /**Construct the application*/
  public mmapp() {
	  init();
  }
  
    protected void init() {
    	MyLogger.createInstance("mmdb.log");
        MyLogger.logger.info("Starting mmdb......");
        mmdb_window frame = new mmdb_window();
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
          frame.pack();
        }
        else {
          frame.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
          frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
          frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);    
    
    }
  /**Main method*/
  public static void main(String[] args) {
    try {
//      PrintStream ps = new PrintStream(new FileOutputStream("mmdb_err.log"));
//      System.setErr(ps);
//      System.setOut(ps);
      System.out.println("***** Starting mmdb ... ");
//      UIManager.setLookAndFeel(UIManager.getLookAndFeelDefaults());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new mmapp();
  }
}

