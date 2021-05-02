package com.solar.utility;
import java.util.*;
public class TreeNodeBasic {
  int itemkey=-1;
  private static int count=0;
  TreeNodeBasic parent=null;
  protected ArrayList childs = new ArrayList();

  public TreeNodeBasic() {
    count++;
  }

  public TreeNodeBasic(int ikey) {
    super();
    itemkey = ikey;
  }

  public static int getNodeCount() {
    return count;
  }


  public void addChild(TreeNodeBasic c) {
    if (c!=null && !this.contains(c.getKey()))
      childs.add(c);
  }

  public void setParent(TreeNodeBasic p) {
    if (p.getKey()==this.itemkey)
      return ;
    parent=p;
  }

  public int getParentKey() {
    if (parent!=null)
      return parent.getKey();
    else
      return -1;
  }

  public int getKey() {
    return itemkey;
  }

  public boolean contains(int nodekey) {
    int i=0;
    boolean found=false;
    while (i<childs.size() && !found) {
      TreeNodeBasic tn = (TreeNodeBasic)childs.get(i);
      if (nodekey ==tn.getKey()) {
        found = true;
      }
      i++;
    }
    return found;
  }
}
