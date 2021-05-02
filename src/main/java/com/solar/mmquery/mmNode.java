package com.solar.mmquery;

//import com.solar.utility.TreeNodeBasic;

public class mmNode extends com.solar.mmquery.TreeNodeBasic {
  protected byte[] item_code=null;
  protected byte[] item_desc=null;
  protected int item_seclvl = 0;

  public mmNode(int itemkey) {
    super(itemkey,"noname");
  }

  public void setProperties(byte[] cde, byte[] desc, int seclvl) {
    item_code = cde;
    item_desc = desc;
    item_seclvl = seclvl;
  }

  public byte[] getCode () {
	  return item_code;
  }
  public byte[] getDesc() {
	  return item_desc;
  }
  
  public int getSecLvl() {
	  return item_seclvl;
  }
}
