/************************/
/*   user profile bean  */
/************************/

package com.solar.mmquery;

public class UserProfileBean {
  String _username =null;
  String _usergroup = null;
  String _password = null;
  int _access_level = 0;
  public UserProfileBean() {
  }
  public void setUserName(String in) {
      _username = in;
  }
  public String getUserName() {
    return _username;
  }
  public void setUserGroup(String in) {
    _usergroup = in;
  }
  public String getUserGroup () {
    return _usergroup;
  }
  public void setAccessLevel(int in) {
    _access_level = in;
  }
  public int getAccessLevel() {
    return _access_level;
  }
  public void setUserPass(String in) {
    _password = in;
  }
  public String getUserPass() {
    return _password;
  }
}
