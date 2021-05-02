package com.solar.utility;

import javax.swing.table.*;
import java.util.*;

/**
 * Title:        utility library
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium
 * @author eric
 * @version 1.0
 */

public class BaseTableModel extends AbstractTableModel {
  String[] columnNames=null;
  Object[][] data=null;

  public BaseTableModel() {
  }

  public void setDataVector(Vector vdata) {
    data = new Object[vdata.size()][columnNames.length];
    for (int i = 0;i< vdata.size();i++) {
      Vector vrow = (Vector)vdata.get(i);
      for (int j=0;j<columnNames.length;j++) {
        data[i][j] = vrow.get(j);
      }
    }
    fireTableDataChanged();
  }

  public void addRow(Vector vdata) {
    Object[][] olddata = data;
    if (olddata!=null) {
      data = new Object[olddata.length+1][columnNames.length];
      for (int i=0;i<olddata.length;i++) {
        for (int j=0;j<columnNames.length;j++) {
          data[i][j]=olddata[i][j];
        }
      }
      for (int j=0;j<vdata.size();j++) {
        data[olddata.length][j]=vdata.get(j);
      }
      fireTableRowsInserted(olddata.length,olddata.length);
    }
  }
  public void setColumns(Vector vcolumn) {
    columnNames = new String[vcolumn.size()];
    for (int i=0;i<vcolumn.size();i++)
      columnNames[i] = (String)vcolumn.get(i);
  }

  public String getColumnName(int column) { return columnNames[column]; }
  public int getColumnCount() { return columnNames.length; }
  public int getRowCount() {
    if (data==null)
      return 0;
    else
      return data.length;
  }
  public Object getValueAt(int row, int col) { return data[row][col];}
  public Class getColumnClass(int c) {return getValueAt(0,c).getClass(); }
  public boolean isCellEditable(int row, int col) { return true; }

  public void setValueAt(Object value,int row, int col) {
    if (data[row][col]!=null && value!=null) {
      if (data[row][col].toString().trim().equals(value.toString().trim())) {
        return;
      }
    }
    if (data[row][col]==null && value==null)
      return;
    data[row][col]=value;
    fireTableCellUpdated(row,col);
  }
}