package com.solar.mmdb;

import java.util.*;

public class Res extends java.util.ListResourceBundle {
  static final Object[][] contents = new String[][]{
	{ "SELECTION", "SELECTION" },
	{ "Group", "Group" },
	{ "KEY", "|<" },
	{ "KEY1", "<" },
	{ "KEY2", ">" },
	{ "KEY3", ">|" },
	{ "_OFF", "** OFF" },
	{ "ORACLE", "ORACLE" },
	{ "MSSQL", "MSSQL" },
	{ "SYBASE", "SYBASE" },
	{ "comboBoxChanged", "comboBoxChanged" }};
  public Object[][] getContents() {
    return contents;
  }
}