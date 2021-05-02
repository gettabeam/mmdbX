package com.solar.mmquery;
import java.util.*;

/*

When
20050329  Add file_desc, view_frequency
20070712  Add Category

*/

public class mmdata {
  byte[] data=null;
  int size = 0;
  int view_frequency=0;
  byte[] file_desc=null;
  byte[] name=null;
  byte[] signature=null;
  boolean isEncrypted=false;
  Vector category=null;
  

  int type=0;
  String info=null;
  int mmkey=0;
  public mmdata() {
  }
  public void setData(byte[] in) {
    data=in;
  }
  public byte[] getData() {
    return data;
  }
  public void setSize(int in) {
    size=in;
  }
  public int getSize() {
    return size;
  }
  public void setViewFrequency(int in) {
    view_frequency=in;
  }
  public int getViewFrequency() {
    return view_frequency;
  }
  public void setName(byte[] in) {
    name=in;
  }
  public byte[] getName() {
    return name;
  }
  public void setDesc (byte[] in) {
    file_desc = in;
  }
  public byte[] getDesc () {
    return file_desc;
  }
  public void setCategory(Vector v) {
	  category=v;
  }
  public Vector getCategory() {
	  return category;
  }
  public boolean isEncrypted() {
    return isEncrypted;
  }
  public void setEncrypted(boolean in) {
    isEncrypted=in;
  }
}
