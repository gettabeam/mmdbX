package com.solar.mmquery;
import java.util.*;
import java.io.Serializable;
import com.solar.utility.*;

/*

WHen       What
20050518   Initial Version

*/


public class TreeNodeBasic implements Serializable {
  ArrayList _childs = new ArrayList();
  ArrayList _parents = new ArrayList();
  int item_key =-1;
  String item_desc=null;

  public TreeNodeBasic(int key, String desc) {
    item_key = key;
    item_desc = desc;

  }

  public int getItemKey() {
    return item_key;
  }
  public void addChild(TreeNodeBasic _item) {
  if (_item==null) {
	MyLogger.logger.info("TreeNodeBasic.addChild: item is null!");
	return ;
  }

	  if (!hasChild(_item))
      _childs.add(_item);
  }

  public boolean hasChild(TreeNodeBasic _item) {
	  return hasItem(_childs,_item);
 }
  public boolean hasParent(TreeNodeBasic _item) {
	  return hasItem(_parents,_item);
  }
  
  private boolean hasItem(ArrayList _list,TreeNodeBasic _item) {
	if (_item==null) {
		//System.out.println("TreeNodeBasic.hasChild: item is null!");
		return false;
	}
    for (int i=0;i<_list.size();i++) {
      TreeNodeBasic _c = (TreeNodeBasic)_list.get(i);
      if (_c.getItemKey()==_item.getItemKey())
        return true;
    }
    return false;
  }

  public void addParent(TreeNodeBasic _p) {
	 _parents.add(_p);
      }

  public String getItemDesc() {
    return item_desc;
  }
  public ArrayList getParents() {
    return _parents;
  }



  public ArrayList getChilds() {
    return _childs;
  }
  public boolean isLeaf() {
    if (_childs.size()==0)
      return true;
    else
      return false;
  }

  public boolean isRoot() {
    if (_parents.size()==0)
      return true;
    else
      return false;
  }
}
