package com.solar.utility;
import java.io.*;
import java.net.*;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

public class HttpDownload {
  public HttpDownload() {
  }

  public boolean forwardBinaries(String urlstr, OutputStream os)  {
  try {
  boolean b_ret=false;
  Properties prop = new Properties();
  URL url= new URL(urlstr);
  String s_post_str = null;

  HttpURLConnection yc = (HttpURLConnection)url.openConnection();

/*
 * Proxy Server Setting, if no proxy server, comment this section
 *
 */

//User name and password for proxy server.
  String authString = "wongspp:wongspp";
  String auth = "Basic " + Base64.encodeBase64(authString.getBytes());


 //       System.getProperties().put("proxySet", "true");
 //       System.getProperties().put("proxyHost", "proxy.ha.org.hk");
 //       System.getProperties().put("proxyPort", "8080");
//        yc.setRequestProperty("Proxy-Authorization", auth);

  yc.setDoOutput(true);
  yc.setDoInput(true);
  yc.setRequestMethod("GET");
  String resContent = yc.getContentType();
  int resCode = yc.getResponseCode();
  String resMsg = yc.getResponseMessage();
  System.out.println(">> Content Type="+resContent);
  System.out.println(">> Response Code="+resCode);
  System.out.println(">> Response Msg="+resMsg);



/* set cookie
  yc.setDoInput(true);
//Cookies set by QuamNet *possibily changed*
  yc.setRequestProperty("Cookie","SCB=00180560");
  yc.setRequestMethod("POST");
  yc.setRequestMethod("GET");
  PrintWriter out = new PrintWriter(yc.getOutputStream());
  s_post_str="code="+s_trade_no+"&button=Quote&sbut=OK";
  out.println(s_post_str);
  out.close();
  BufferedReader in = new BufferedReader(
  new InputStreamReader(
  yc.getInputStream()));
  String inputLine;
  while ((inputLine = in.readLine()) != null) {
    System.out.println(inputLine);
  }
  in.close();
*/
//  FileOutputStream fos = new FileOutputStream(outputfile);

  BufferedInputStream bif = new BufferedInputStream(yc.getInputStream());
  int ia = bif.available();
  int ib=0;
  byte b[] = new byte[1024*1024];
  StringBuffer sb = new StringBuffer();
  while (ia>=0 && ib>=0) {
    ib=bif.read(b,0,b.length);
    if (ib>0) {
      os.write(b,0,ib);
      String s = new String(b,0,ib);
      sb.append(s);
    }
    ia=bif.available();
  }
  bif.close();
  os.close();
  return true;
  } catch (Exception e) {
    return false;
  }
}


  public boolean downloadFile(String urlstr,String outputfile) throws Exception {
    FileOutputStream fos = new FileOutputStream(outputfile);
    return forwardBinaries(urlstr,fos);

  }

}
