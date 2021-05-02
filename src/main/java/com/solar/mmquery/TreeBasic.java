package com.solar.mmquery;

import java.util.*;
import java.sql.*;
import com.solar.utility.*;

//  Class : ItemTree
//  Purpose :
//
//  Who           When                   What
//  eRic          20050503    Initial Ver

public class TreeBasic {
  protected static HashMap trees = new HashMap(20);
  protected static int treecount = 0;
  protected int tree_id = 0;
  protected String _treeID = null;
  protected Vector items = new Vector();
  protected Vector roots = new Vector();
  public static int CHILD=0;
  public static int PARENT=1;
  
  /* testing only */
  public static void main(String argv[]) {
    System.out.println("test");
  }

  public Vector getAllItems() {
	  return items;
	  
  }
  public void reset() {
	  items.clear();
	  roots.clear();
	  
  }
  public int size() {
	  return items.size();
  }
  public TreeBasic(String _id) {
    _treeID= _id;
    treecount++;
    tree_id = treecount;
  }

  public TreeBasic() {
    _treeID="[default]";
    treecount++;
    tree_id = treecount;
  }

  public static int getTreeCount() {
    return treecount;
  }

  public int getTreeID() {
    return tree_id;
  }

  public boolean hasItem(int ikey) {
    for (int i = 0; i < items.size(); i++) {
      TreeNodeBasic pf = (TreeNodeBasic) items.get(i);
      if (pf.getItemKey() == ikey) {
        return true;
      }
    }
    return false;
  }
  
  /* get all roots of a tree */
  public Vector getRoots() {
    return roots;
  }

  public void initRoots() {
    roots.clear();
    for (int i = 0; i < items.size(); i++) {
      TreeNodeBasic pf = (TreeNodeBasic) items.get(i);
//      Logger.log(this,"##DEBUG >>>"+pf.getParentKey()+","+pf.getItemKey()+","+pf.getChildKeys().size(),Logger.INFO);
      if (pf.isRoot())
        roots.add(pf);
    }
    if (roots.size() == 0)
      MyLogger.logger.error( "## Error ! Not ROOT node is found for Tree ID=" + this.getTreeID());
    else
      MyLogger.logger.error("##"+ roots.size() + " root(s) is/are found for Tree ID=" +  this.getTreeID());

  }

  /*   return the immediate root of a given key */
  public Vector getRoot(int itemkey) {
    return null;
  }

  /*
  public Vector getChildPairs(TreeNodeBasic _item) {
    Vector v = new Vector();
    for (int i = 0; i < roots.size(); i++) {
      TreeNodeBasic root = (TreeNodeBasic) roots.get(i);
      treeTraverse(root.getItemKey(), v);
    }
    System.out.println( "### getAllChilds() return size=" + v.size());
    return v;
  }
*/
  
  
  /* add a relation as well as items into a tree */
  public void addRelation(TreeNodeBasic _p, TreeNodeBasic _c) {

    if (_p.getItemKey() != _c.getItemKey()) {
      _p.addChild(_c);
      _c.addParent(_p);
    }
    addItem(_p);
    addItem(_c);
  }

  /* add a node into a tree */
  public void addItem(TreeNodeBasic item) {
    if (hasItem(item.getItemKey()))
      return;
    items.add(item);
 //   System.out.println("TreeSize>"+items.size());

//    Logger.log(this,"##>>> Adding item "+item.getItemKey(),Logger.INFO);
  }

  /* get an item based on the itemkey */
  public TreeNodeBasic getItem(int key) {
    for (int i = 0; i < items.size(); i++) {
      TreeNodeBasic pf = (TreeNodeBasic) items.get(i);
      boolean b = (pf.getItemKey() == key);
      if (b)
        return pf;
    }
    return null;
  }



  /* traverse each node to rebuild the tree and store the structure in a vector */
  protected void treeTraverse(TreeNodeBasic pi, Vector output, int _mode) {
    if (pi.isRoot()) {
      Object pair[] = {
          pi, pi};
      output.add(pair);
    }
    if (pi.isLeaf())
      return;
    ArrayList al=null;
    if (_mode==CHILD)
        al = pi.getChilds();
    else 
    	al = pi.getParents();
    Iterator ir = al.iterator();
    while (ir.hasNext()) {
      TreeNodeBasic ci = (TreeNodeBasic)ir.next();
      Object pair[] = {
          pi, ci};
      output.add(pair);
      
   //   System.out.println("##>"+pi.getItemKey()+","+ci.getItemKey());
      treeTraverse(ci, output,_mode);
    }
  }

  public Vector getAllChilds() {
    Vector v = new Vector();
    for (int i = 0; i < roots.size(); i++) {
      TreeNodeBasic root = (TreeNodeBasic) roots.get(i);
      populateChildVector(root,v);
    }
 //   System.out.println("### getAllChilds() return size=" + v.size());
    return v;
  }
  
  public Vector getChilds(TreeNodeBasic _p) {
	    Vector v = new Vector();
	    populateChildVector(_p,v);
	//    System.out.println("### getAllChilds() return size=" + v.size());
	    return v;
  }
  

  private void populateAncestors(TreeNodeBasic parentItem, Vector vecAncestors){
	  ArrayList al = parentItem.getParents();
	 // System.out.println("### populateChildVector, item="+parentItem.getItemKey()+" size="+al.size());
	  Iterator ir = al.iterator();
	  while(ir.hasNext()){
	    TreeNodeBasic item = (TreeNodeBasic) ir.next();
	    vecAncestors.add(item);
	    populateAncestors(item,vecAncestors);
	  }

	}  

  private void populateChildVector(TreeNodeBasic parentItem, Vector vecChildItems){
  ArrayList al = parentItem.getChilds();
 // System.out.println("### populateChildVector, item="+parentItem.getItemKey()+" size="+al.size());
  Iterator ir = al.iterator();
  while(ir.hasNext()){
    TreeNodeBasic item = (TreeNodeBasic) ir.next();
    vecChildItems.add(item);
    populateChildVector(item,vecChildItems);
  }

}


  /* get all childs with parent_key id as parameter*/
/*  
  public static synchronized TreeNodeBasic[] getChilds(String _treeName, TreeNodeBasic _p) {

    TreeNodeBasic[] TreeNodeBasicArray = null;
    Vector vecChildItems = new Vector();
    getInstance(_treeName).populateChildVector(_p, vecChildItems);

    System.out.println("getChilds>>"+vecChildItems.size());
    if(vecChildItems != null){
      TreeNodeBasicArray = new TreeNodeBasic[vecChildItems.size()];

      for (int i = 0; i < vecChildItems.size(); i++){
        TreeNodeBasicArray[i] = (TreeNodeBasic) vecChildItems.elementAt(i);
      }
    }
    return TreeNodeBasicArray;
  }
*/
  public Vector getChildPairs( TreeNodeBasic _item) {
	 Vector output = new Vector();
	 treeTraverse(_item,output,CHILD);
	 return output;
  }
  
  public Vector getAncestorPairs( TreeNodeBasic _item) {
		 Vector output = new Vector();
		 treeTraverse(_item,output,PARENT);
		 return output;
	  }
  
  
  public TreeNodeBasic[] getAncestors(TreeNodeBasic _item){

    TreeNodeBasic[] TreeNodeBasicArray = null;
    Vector vecAncestors  = new Vector();
    populateAncestors(_item, vecAncestors);

    if (vecAncestors != null) {
      TreeNodeBasicArray = new TreeNodeBasic[vecAncestors.size()];

      for (int i = 0; i < vecAncestors.size(); i++) {
        TreeNodeBasicArray[i] = (TreeNodeBasic) vecAncestors.elementAt(i);
      }
    }
    return TreeNodeBasicArray;
  }

  /* create an instance of a tree for "catg"+"hosp" combination */
/*
  public static synchronized void createInstance(String _treeName) {
    TreeBasic it = new TreeBasic(_treeName);
    trees.put(_treeName, it);
    System.out.println("### CREATE new ItemTree Instance for [" + _treeName + "], tree count=" +  TreeBasic.treecount);
  }
*/

/*

public static synchronized TreeBasic getInstance(String _treeName)
  {
    TreeBasic it;
    if (!trees.containsKey(_treeName)) {
      createInstance(_treeName);
      it = (TreeBasic) trees.get(_treeName);
    }
    else
      it = (TreeBasic) trees.get(_treeName);
    return it;
  }
*/

}
