package com.solar.utility;

import org.apache.log4j.*;


public class MyLogger {

   public static Logger logger;
   
   public void MyLogger() {
	   System.out.println("default constructor !");
   }

   
   public static void createInstance(String _fileName) {
	   if (logger==null) {
			  System.out.println("CreateInstance>MyLogger instantiated !");
			  logger=Logger.getLogger(MyLogger.class);
			  PatternLayout layout = new PatternLayout("%-5p %d{yy-MM-dd HH:mm:ss}[%15.15C{1}.%-15.15M]%m%n");
			  DailyRollingFileAppender appender = null;
			  try {
			    appender = new DailyRollingFileAppender(layout,_fileName,"'.'yyyyMMdd");
			  } catch(Exception e) {}
		       logger.addAppender(appender);
			   logger.setLevel((Level) Level.DEBUG);
	   }
   }

      
   
   public static void main(String argv[]) {
	  MyLogger.createInstance("test.log"); 
      MyLogger.logger.info("****test****");
   }
   
}


/*
log4j.rootLogger=INFO, A1, A2

# A1 is set to be a ConsoleAppender.
log4j.appender.A1=org.apache.log4j.ConsoleAppender
log4j.appender.A1.layout=org.apache.log4j.PatternLayout
log4j.appender.A1.layout.ConversionPattern=
      [%d{yy/MM/dd HH:mm:ss:SSS}][%C-%M] %m%n

# A2 is set to be a file
log4j.appender.A2=org.apache.log4j.DailyRollingFileAppender
log4j.appender.A2.layout=org.apache.log4j.PatternLayout
log4j.appender.A2.layout.ConversionPattern=
      [%d{HH:mm:ss:SSS}][%C-%M] %m%n
log4j.appender.A2.File=C:/temp/connector_agent_trace.log	
*/