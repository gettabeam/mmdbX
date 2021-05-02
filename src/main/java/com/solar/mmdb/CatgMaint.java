package com.solar.mmdb;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

import com.solar.mmquery.*;
import com.solar.utility.*;
import java.awt.event.*;

/**
 * Title:        Multimedia Database
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Millennium Consultancy
 * @author
 * @version 1.0
 */

public class CatgMaint extends JPanel {
  static final int SELECT = 0;
  static final int MAINT=1;
  JFrame parentframe = null;
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable jTable_catg = new JTable();
  JPanel jPanel1 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JTextField jTxt_magicword = new JTextField();
  JButton jBut_cast = new JButton();
//  DefaultTableModel catgdataModel=null;
  CatgCodeTableModel catgdataModel= new CatgCodeTableModel();

// ora_mmquery mq = null;
//  oraDBBridge dbbridge = null;
  mm_query mq = null;
  DBBridge dbbridge = null;
  Properties loginparms = new Properties();

  Vector columns = new Vector();
  JButton jBut_query = new JButton();
  private transient Vector processStatusListeners;
  JPanel jPanel2 = new JPanel();
  JList jList1 = new JList();
  JButton jBut_edit = new JButton();
  ImageIcon image1 = null;
  ImageIcon image2 = null;
  ImageIcon image3 = null;
  ImageIcon image4 = null;
  ImageIcon image5 = null;
  ImageIcon image6 = null;
  ImageIcon image7 = null;
  ImageIcon image8 = null;
  ImageIcon image9 = null;
  ImageIcon image10= null;
  ImageIcon image11 = null;
  ImageIcon image12 = null;
  JButton jBut_insert = new JButton();
  JButton jBut_del = new JButton();
  JButton jBut_save = new JButton();
  boolean editcols[] = null;
  boolean _ignore_event=false;
  private int mode =MAINT;
  JLabel jLabel2 = new JLabel();
  JLabel jLab_mode = new JLabel();
  JLabel jLabel3 = new JLabel();
  JButton jBut_logon = new JButton();

  public static void main(String argv[]) {
/*
    oraDBBridge db = new oraDBBridge();

    Properties login = new Properties();
    login.put("username","eric");
    login.put("password","abc123");
    login.put("host","blackgate");
    login.put("port","1521");
    login.put("sid","orastart");
    db.setLoginParms(login);
    db.initConnection();
    ora_mmquery mq= new ora_mmquery(db);

    JFrame jf = new JFrame();
    CatgMaint cs = new CatgMaint(mq);
    jf.getContentPane().add(cs);
    jf.pack();
    jf.show();
*/
    CatgMaint cs = new CatgMaint();
    cs.showFrame();
  }

  class CatgCodeTableModel extends BaseTableModel {
    public CatgCodeTableModel() {
    }
    public boolean isCellEditable(int row, int col) {
      return editcols[col];
    }
  }

  public void setParent(JFrame jf ) {
    parentframe = jf;
  }

  public CatgMaint() {
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setMode(int i){
    mode = i;
    if (i==this.SELECT) {
      editcols[0]=false;
      editcols[1]=false;
      editcols[2]=false;
      editcols[3]=false;
      editcols[4]=false;
      editcols[5]=true;
      jBut_edit.setEnabled(false);
      jBut_save.setEnabled(false);
    } else if (i== this.MAINT) {
      editcols[0]=true;
      editcols[1]=true;
      editcols[2]=false;
      editcols[3]=true;
      editcols[4]=false;
      editcols[5]=false;
      jBut_edit.setEnabled(true);
      jBut_save.setEnabled(true);

    }

  }
  public CatgMaint(mm_query mq) {
    this.mq = mq;
    try {
      jbInit();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public void setMMQuery(mm_query mq) {
    this.mq = mq;
  }

  void jbInit() throws Exception {
    editcols = new boolean[6];


/*
    image1 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_filmfolder.gif"));
    image2 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_shutdown.gif"));
    image3 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_mycomputer.gif"));
    image4 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_floppy1.gif"));
    image5 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon31.gif"));
    image6 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_text_doc.gif"));
    image7 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_admintools.gif"));
    image8 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_picfolder.gif"));
    image9 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon4.gif"));
    image10 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_connect.gif"));
    image11 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon_controlpanel2.gif"));
    image12 = new ImageIcon(com.solar.mmdb.nevigator.class.getResource("../resources/icon44.gif"));
*/
    image1 = new ImageIcon("../resources/icon_filmfolder.gif");
    image2 = new ImageIcon("../resources/icon_shutdown.gif");
    image3 = new ImageIcon("../resources/icon_mycomputer.gif");
    image4 = new ImageIcon("../resources/icon_floppy1.gif");
    image5 = new ImageIcon("../resources/icon31.gif");
    image6 = new ImageIcon("../resources/icon_text_doc.gif");
    image7 = new ImageIcon("../resources/icon_admintools.gif");
    image8 = new ImageIcon("../resources/icon_picfolder.gif");
    image9 = new ImageIcon("../resources/icon4.gif");
    image10 = new ImageIcon("../resources/icon_connect.gif");
    image11 = new ImageIcon("../resources/icon_controlpanel2.gif");
    image12 = new ImageIcon("../resources/icon44.gif");

    this.setLayout(borderLayout1);
    jLabel1.setText("Magic Word");
    jTxt_magicword.setMinimumSize(new Dimension(80, 23));
    jTxt_magicword.setPreferredSize(new Dimension(80, 23));
    jTxt_magicword.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jTxt_magicword_actionPerformed(e);
      }
    });
    this.setMinimumSize(new Dimension(450,300));
    this.setPreferredSize(new Dimension(450,300));
    jBut_cast.setMaximumSize(new Dimension(32, 32));
    jBut_cast.setMinimumSize(new Dimension(32, 32));
    jBut_cast.setPreferredSize(new Dimension(32, 32));
    jBut_cast.setToolTipText("Process");
    jBut_cast.setIcon(image12);
    jBut_cast.setMargin(new Insets(0, 0, 0, 0));
    jBut_cast.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_cast_actionPerformed(e);
      }
    });
    jTable_catg.setBackground(SystemColor.info);
    jTable_catg.setBorder(BorderFactory.createEtchedBorder());
    jTable_catg.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    jBut_query.setMaximumSize(new Dimension(32, 32));
    jBut_query.setMinimumSize(new Dimension(32, 32));
    jBut_query.setPreferredSize(new Dimension(32, 32));
    jBut_query.setToolTipText("Refresh List");
    jBut_query.setIcon(image11);
    jBut_query.setMargin(new Insets(0, 0, 0, 0));
    jBut_query.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_query_actionPerformed(e);
      }
    });
    jBut_edit.setMaximumSize(new Dimension(32, 32));
    jBut_edit.setMinimumSize(new Dimension(32, 32));
    jBut_edit.setPreferredSize(new Dimension(32, 32));
    jBut_edit.setToolTipText("Toggle Edit Mode");
    jBut_edit.setIcon(image6);
    jBut_edit.setMargin(new Insets(0, 0, 0, 0));
    jBut_edit.setMnemonic('0');
    jBut_edit.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_edit_actionPerformed(e);
      }
    });
    jBut_insert.setMaximumSize(new Dimension(32, 32));
    jBut_insert.setMinimumSize(new Dimension(32, 32));
    jBut_insert.setPreferredSize(new Dimension(32, 32));
    jBut_insert.setToolTipText("Add Row");
    jBut_insert.setIcon(image8);
    jBut_insert.setMargin(new Insets(0, 0, 0, 0));
    jBut_insert.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_insert_actionPerformed(e);
      }
    });
    jBut_del.setMaximumSize(new Dimension(32, 32));
    jBut_del.setMinimumSize(new Dimension(32, 32));
    jBut_del.setPreferredSize(new Dimension(32, 32));
    jBut_del.setToolTipText("..nothing");
    jBut_del.setIcon(image2);
    jBut_del.setMargin(new Insets(0, 0, 0, 0));
    jBut_del.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_del_actionPerformed(e);
      }
    });
    jBut_save.setMaximumSize(new Dimension(32, 32));
    jBut_save.setMinimumSize(new Dimension(32, 32));
    jBut_save.setPreferredSize(new Dimension(32, 32));
    jBut_save.setToolTipText("Update");
    jBut_save.setIcon(image4);
    jBut_save.setMargin(new Insets(0, 0, 0, 0));
    jBut_save.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_save_actionPerformed(e);
      }
    });
    jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel2.setForeground(new Color(0, 0, 105));
    jLabel2.setToolTipText("");
    jLabel2.setText("Category Maintenance");
    jLab_mode.setBackground(new Color(0, 92, 164));
    jLab_mode.setFont(new java.awt.Font("Dialog", 1, 12));
    jLab_mode.setForeground(Color.orange);
    jLab_mode.setBorder(BorderFactory.createEtchedBorder());
    jLab_mode.setOpaque(true);
    jLab_mode.setText("MODE");
    jLabel3.setBorder(BorderFactory.createEtchedBorder());
    jLabel3.setToolTipText("");
    jLabel3.setText("                   ");
    jBut_logon.setMaximumSize(new Dimension(32, 32));
    jBut_logon.setMinimumSize(new Dimension(32, 32));
    jBut_logon.setPreferredSize(new Dimension(32, 32));
    jBut_logon.setToolTipText("");
    jBut_logon.setIcon(image10);
    jBut_logon.setMargin(new Insets(0, 0, 0, 0));
    jBut_logon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_logon_actionPerformed(e);
      }
    });
    this.add(jScrollPane1, BorderLayout.CENTER);
    this.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jBut_logon, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jTxt_magicword, null);
    jPanel1.add(jBut_query, null);
    jPanel1.add(jBut_cast, null);
    jPanel1.add(jLab_mode, null);
    this.add(jPanel2, BorderLayout.NORTH);
    jPanel2.add(jLabel2, null);
    jPanel2.add(jLabel3, null);
    jPanel2.add(jList1, null);
    jPanel2.add(jBut_edit, null);
    jPanel2.add(jBut_insert, null);
    jPanel2.add(jBut_del, null);
    jPanel2.add(jBut_save, null);
    columns.add("catg_code");
    columns.add("description");
    columns.add("catg_key");
    columns.add("seclvl");
    columns.add("total");
    columns.add("select");
    catgdataModel.setColumns(columns);
    jTable_catg.setModel(catgdataModel);
    TableColumn tc=jTable_catg.getColumn("description");
    tc.setPreferredWidth(250);
    tc=jTable_catg.getColumn("catg_code");
    tc.setPreferredWidth(200);
    jScrollPane1.getViewport().add(jTable_catg, null);

    this.setPreferredSize(new Dimension(600,400));
    this.setMinimumSize(new Dimension(600,400));

    catgdataModel.addTableModelListener(new TableModelListener() {
      public void tableChanged (TableModelEvent e) {
        catgDataModel_tableChanged(e);
      }
    });

    this.toggleMode();

  }
  public String getMagicWord() {
    return jTxt_magicword.getText();
  }

  public void queryResult(String passwd) {
    Vector v= mq.getCatgGrpDetails(passwd);
    catgdataModel.setDataVector(v);
  }

  void jBut_cast_actionPerformed(ActionEvent e) {
    this.fireDataChanged(new processStatusEvent((Object)catgdataModel));
    if (parentframe!=null)
      parentframe.setVisible(false);
  }

  void jBut_query_actionPerformed(ActionEvent e) {
    loadCatgGrp();
  }

  void loadCatgGrp() {
    if (mq == null)
      return;
    _ignore_event=true;
    queryResult(jTxt_magicword.getText());
    _ignore_event=false;
  }
  public BaseTableModel getDataModel() {
    return (BaseTableModel)catgdataModel;
  }

  public synchronized void addprocessStatusListener(processStatusListener l) {
    Vector v = processStatusListeners == null ? new Vector(2) : (Vector) processStatusListeners.clone();
    if (!v.contains(l)) {
      v.addElement(l);
      processStatusListeners = v;
    }
  }

  protected void fireDataChanged(processStatusEvent e) {
    if (processStatusListeners != null) {
      Vector listeners = processStatusListeners;
      int count = listeners.size();
      for (int i = 0; i < count; i++) {
        ((processStatusListener) listeners.elementAt(i)).dataChanged(e);
      }
    }
  }

  void jTxt_magicword_actionPerformed(ActionEvent e) {

  }/* end of registerMM() */

  void jBut_edit_actionPerformed(ActionEvent e) {
    toggleMode();
  }/* end of registerMM() */

  public void toggleMode() {
    if (mode==this.SELECT) {
      mode=this.MAINT;
      jLab_mode.setText("** EDIT **");
    }
    else {
      mode=this.SELECT;
      jLab_mode.setText("** SELECT **");
    }
    if (mode==this.SELECT) {
      editcols[0]=false;
      editcols[1]=false;
      editcols[2]=false;
      editcols[3]=false;
      editcols[4]=false;
      editcols[5]=true;
      jBut_cast.setEnabled(true);
      jBut_query.setEnabled(true);
      jBut_insert.setEnabled(false);
      jBut_save.setEnabled(false);
    } else if (mode==this.MAINT) {
      editcols[0]=true;
      editcols[1]=true;
      editcols[2]=false;
      editcols[3]=true;
      editcols[4]=false;
      editcols[5]=false;
      jBut_cast.setEnabled(false);
      jBut_query.setEnabled(false);
      jBut_save.setEnabled(true);
      jBut_insert.setEnabled(true);
    }
    loadCatgGrp();
  }

  void insert() {
    Vector v = new Vector();
    v.add("");
    v.add("");
    v.add(Integer.valueOf("0"));
    v.add(Boolean.FALSE);
    v.add(Integer.valueOf("0"));
    v.add(Boolean.FALSE);
    catgdataModel.addRow(v);
  }
  void catgDataModel_tableChanged(TableModelEvent e) {
    if (! _ignore_event)
      catgdataModel.setValueAt(Boolean.TRUE,e.getFirstRow(),5);
  }
  boolean updateChanges() {
    try {
    for (int i=0;i<catgdataModel.getRowCount();i++) {
      Boolean _modified=(Boolean)catgdataModel.getValueAt(i,5);
      if (_modified.booleanValue()) {
        String _code = (String)catgdataModel.getValueAt(i,0);
        String _desc = (String)catgdataModel.getValueAt(i,1);
        Boolean _sec = (Boolean)catgdataModel.getValueAt(i,3);
        Integer _key = (Integer)catgdataModel.getValueAt(i,2);
        int _isec=0;
        if (_sec.booleanValue())
          _isec=1;

        if (_key.intValue()==0) {
          int newkey= mq.addCatgKey("MM",_code,_desc,_isec,this.getMagicWord());
          System.out.println("new key="+newkey);
        }
        else {
          mq.updateCatgKey(_key.intValue(),"MM",_code,_desc,_isec,this.getMagicWord());
        }
        catgdataModel.setValueAt(Boolean.FALSE,i,5);
      } /* end if */
    } /* end for */
    System.out.println("Updated !!");
    loadCatgGrp();
    return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  void jBut_save_actionPerformed(ActionEvent e) {
    updateChanges();
  }

  void jBut_insert_actionPerformed(ActionEvent e) {
    insert();
  }

  void jBut_del_actionPerformed(ActionEvent e) {

  }

  void jBut_logon_actionPerformed(ActionEvent e) {
    if (logindb()!=0) {
      com.solar.utility.MsgBox.show("ERROR","Unable to login database !");
    }
  }

  int logindb() {
    LoginPrompt lp = new LoginPrompt(null,"Oracle Database Login",true,"ORACLE",loginparms);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = lp.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    lp.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

    
    lp.show();

    if (loginparms.getProperty("LOGIN").equals("ORACLE"))
      dbbridge = new oraDBBridge();
    else if (loginparms.getProperty("LOGIN").equals("MySQL") )
      dbbridge = new mysqlDBBridge();
    else {
      com.solar.utility.MsgBox.show("Error","Database not supported !");
      return -1;
    }
    dbbridge.setLoginParms(loginparms)  ;
    if (dbbridge.connectDB()== -1) {
      com.solar.utility.MsgBox.show("Error","Database connection failed! Please retry.");
      return -1;
    }
    if (loginparms.getProperty("LOGIN").equals("ORACLE"))
      mq = new ora_mmquery(dbbridge);
    else if (loginparms.getProperty("LOGIN").equals("MySQL") )
      mq = new mysql_mmquery(dbbridge);

    return 0;
  }

  public void showFrame() {
    JFrame jf = new JFrame();
    jf.getContentPane().add(this);
    jf.setSize(600,400);
    jf.pack();
    com.solar.utility.screen.CentreFrame(jf);
    jf.show();
  }

}
