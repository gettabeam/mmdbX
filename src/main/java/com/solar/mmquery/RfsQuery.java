package com.solar.mmquery;

import java.sql.*;

public class RfsQuery {
  Connection con = null;
  static String _DEFAULT_CHARSET = "UTF8";
  public RfsQuery() {
  }
  public RfsQuery(DBBridge db) {
    if (!db.isConnected())
      return;
    con = db.getConnection();
  }
}
