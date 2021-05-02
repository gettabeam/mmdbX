package com.solar.utility;
import java.util.*;

public class TreeBasic {
  ArrayList nodes = new ArrayList();

  public TreeBasic() {
  }

  public ArrayList getAllNodes() {
    return nodes;
  }

  public void addNode(TreeNodeBasic tn) {
    if (tn!=null && !contains(tn.getKey()))
      nodes.add(tn);
  }

  public boolean contains(int ikey) {
    boolean found=false;
    int i=0;
    while (i<=nodes.size() && !found) {
      TreeNodeBasic tn = (TreeNodeBasic)nodes.get(i);
      if (tn.getKey()==ikey)
        found=true;
      i++;
    }
    return found;
  }

  public TreeNodeBasic getNode(int ikey) {
    boolean found=false;
    int i=0;
    while (i<=nodes.size() && !found) {
      TreeNodeBasic tn = (TreeNodeBasic)nodes.get(i);
      if (tn.getKey()==ikey)
        return tn;
      i++;
    }
    return null;
  }

  public int size() {
    return nodes.size();
  }

}
