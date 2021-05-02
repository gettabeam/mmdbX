package com.solar.utility.file;

/**
 * <p>Title: utility library</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: Millennium</p>
 * @author eric
 * @version 1.0
 */

/* 2006-02-12  Added update() function */
import java.util.*;
import com.solar.utility.*;

public class FileIndexer {
  int _idx = 0;

  private Hashtable file2idx = new Hashtable(200);
  private Hashtable idx2file = new Hashtable(200);


  public static void main(String argv[]) {
    FileIndexer fi= new FileIndexer();
    String _txt[] = {"The","Wheel","On","The","Bus","Go","Round","and","Round"};
    for (int i=0;i<_txt.length;i++) {
      fi.put(_txt[i]);
    }

  //  fi.update("The","Hello World");
    fi.listContent();
    fi.update("The","Hello World");
    fi.listContent();
    fi.update("Hello","The");
    fi.listContent();
  }
  public FileIndexer() {
  }

  public synchronized int put(Object value) {
    Integer key = null;
    key= (Integer)file2idx.get(value);
    if (key == null) {
      _idx++;
      key = new Integer(_idx);
      file2idx.put(value,key);
      idx2file.put(key,value);
    }
    return key.intValue();
  }

  public Object get(int key) {
    if (idx2file.size()!=file2idx.size()) {
      MyLogger.logger.error("Corrupted FileIndexer!!");
      return null;
    }
    Integer KEY = new Integer(key);
    return idx2file.get(KEY);
  }

  public Object get(String skey) {
    if (idx2file.size()!=file2idx.size()) {
      MyLogger.logger.error("Corrupted FileIndexer!!");
      return null;
    }
    Integer KEY = new Integer(skey);
    return idx2file.get(KEY);
  }

  public int size() {
    return idx2file.size();
  }

  public synchronized boolean update(Object obj_old, Object obj_new) {
    Integer oldKey = (Integer)file2idx.get(obj_old);
    if (oldKey==null)
      return false;
    Integer newKey = (Integer)file2idx.get(obj_new);
    if (newKey!=null)
      return false;
    file2idx.remove(obj_old);
    file2idx.put(obj_new,oldKey);
    idx2file.put(oldKey,obj_new);
    return true;
  }

  public void listContent() {
    MyLogger.logger.debug("file2idx=======>");
    MyLogger.logger.debug(file2idx.toString());
    MyLogger.logger.debug("idx2file========>");
    MyLogger.logger.debug(idx2file.toString());

  }
}
