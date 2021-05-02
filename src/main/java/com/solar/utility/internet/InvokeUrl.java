package com.solar.utility.internet;

import java.util.*;
import java.lang.*;
import java.net.*;
import java.io.*;

public class InvokeUrl {
  public InvokeUrl() {
  }

  public static synchronized String getURLContent(String urlstr) {
    boolean b_ret = false;
    StringBuffer sb = new StringBuffer(2000);
    try {
      Properties prop = new Properties();
      URL url = new URL(urlstr);
      String s_post_str = null;

      HttpURLConnection yc = (HttpURLConnection) url.openConnection();

      yc.setDoOutput(true);
      yc.setDoInput(true);
      yc.setRequestMethod("GET");
      String resContent = yc.getContentType();
      int resCode = yc.getResponseCode();
      String resMsg = yc.getResponseMessage();
      System.out.println(">> URL="+urlstr);
      System.out.println(">> Content Type=" + resContent);
      System.out.println(">> Response Code=" + resCode);
      System.out.println(">> Response Msg=" + resMsg);

      sb.append("<!-- [HTTPResponseMsg="+resMsg.trim()+"]-->");
  //    yc.setDoInput(true);

    //Cookies set by QuamNet *possibily changed*
  //        yc.setRequestProperty("Cookie","SCB=00180560");
//				yc.setRequestMethod("POST");
  //    yc.setRequestMethod("GET");
  //    PrintWriter out = new PrintWriter(yc.getOutputStream());
  //    out.println("cmd=test");
  //    out.close();

      BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
      String inputLine;
      while ((inputLine = in.readLine()) != null) {
        sb.append(inputLine);
      }
      in.close();

    } catch (Exception e) {
      sb.append(e.getMessage());
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
    return sb.toString();
  }

}
