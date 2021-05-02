package com.solar.mmquery;

import java.sql.*;

public class MySQLRfsQuery extends RfsQuery{
  public MySQLRfsQuery() {
  }

  public int getProfileID(String _profile) {
    return 0;
  }

  public int getDirID(String _profile, String _dirpath) {
    int _profileid = getProfileID(_profile);
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    int _dir_id = -1;
    if (_profileid==-1)
      return -1;
    try {
      String _sql = "select dir_id from rfs_dirs where rfs_id = ? and dir_path = ?";
      pstmt = con.prepareStatement(_sql);
      pstmt.setInt(1,_profileid);
      pstmt.setObject(2,_dirpath.getBytes(_DEFAULT_CHARSET));
      rs=pstmt.executeQuery();
      while (rs.next()) {
        _dir_id = rs.getInt(1);
      }
      rs.close();
      pstmt.close();
    } catch (Exception e) {

    }

    return 0;
  }

  public int getFileID(String _profile, String _dir, String _file) {
    return 0;
  }


}
