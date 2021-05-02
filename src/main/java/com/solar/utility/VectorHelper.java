package com.solar.utility;
import java.util.Vector;;

public class VectorHelper {
	  public static Vector removeDuplicates(Vector s) {
		    int i = 0;
		    int j = 0;
		    boolean duplicates = false;
		    if (s==null) return null;
		    Vector v = new Vector();

		    for (i = 0; i < s.size(); i++) {
		      duplicates = false;
		      for (j = (i + 1); j < s.size(); j++) {
		        if (s.elementAt(i).toString().equalsIgnoreCase(
		            s.elementAt(j).toString())) {
		          duplicates = true;
		        }

		      }
		      if (duplicates == false) {
		        v.addElement(s.elementAt(i));
		      }

		    }

		    return v;
		  }
}
