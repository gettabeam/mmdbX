package com.solar.mmdb2;
import com.solar.utility.MyLogger;

/**
 * Hello world!
 *
 */
public class App 
{
  //  final static Logger logger=Logger.getLogger(App.class);
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
	  MyLogger.createInstance("mmdb2.log"); 
      MyLogger.logger.info("****test****");
    }
}
