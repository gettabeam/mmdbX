package com.solar.mmdb;

/**

  History
  2002-06-11   Initial Version
  2007-06-08   Fix login to MySQL problem
 */
import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.util.*;

import com.solar.mmquery.*;
import com.solar.utility.*;

import java.awt.event.*;

public class CatgTreeMaint extends JPanel{
  com.solar.utility.Login dblogin = null;
  com.solar.mmquery.DBBridge dbbridge = null;
  mm_query mq = null;
  Properties loginparms = new Properties();

  JSplitPane jSplitPane1 = new JSplitPane();
  BorderLayout borderLayout1 = new BorderLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();

  Vector parentcolumns = new Vector();
  Vector childcolumns = new Vector();
  ParentTableModel parentdata = new ParentTableModel();
  ChildTableModel childdata = new ChildTableModel();
  JTable parenttable = new JTable();
  JTable childtable = new JTable();
  DefaultListSelectionModel selectmodel = new DefaultListSelectionModel();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JButton jBut_reload = new JButton();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JButton jBut_update = new JButton();
  JLabel jLabel4 = new JLabel();
  FlowLayout flowLayout1 = new FlowLayout();
  JButton jBut_logon = new JButton();
  JTextField jTxt_magicword = new JTextField();
  JButton jBut_refreshparent = new JButton();

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

  class TableSelectionListener implements ListSelectionListener {
    public void valueChanged(ListSelectionEvent e) {
      System.out.println(e.getFirstIndex());
    }
  }
  class ParentTableModel extends BaseTableModel {
    public ParentTableModel() {
    }
    public boolean isCellEditable(int row, int col) {
        return false;
    }
  }

  class ChildTableModel extends BaseTableModel {
    public ChildTableModel() {
    }
    public boolean isCellEditable(int row, int col) {
      if (col==5)
        return true;
      else
        return false;

    }
  }


  public static void main(String argv[]) {
//    oraDBBridge db = new oraDBBridge();

 //   Properties login = new Properties();
 //   login.put("username","eric");
 //   login.put("password","abc123");
 //   login.put("host","blackgate");
 //   login.put("port","1521");
 //   login.put("sid","orastart");
 //   db.setLoginParms(login);
 //   db.initConnection();
 //   ora_mmquery mq= new ora_mmquery(db);
 //   JFrame jf = new JFrame();
 //   CatgTreeMaint ctm = new CatgTreeMaint();
//    ctm.setMMQuery(mq);
//    ctm.queryResult("123456");
//    jf.getContentPane().add(ctm);
//    jf.pack();
//    jf.show();
       CatgTreeMaint ctm = new CatgTreeMaint();
       ctm.showFrame();
  }

  public void showFrame() {
    JFrame jf = new JFrame();
    jf.getContentPane().add(this);
    jf.setSize(700,500);
    jf.pack();
    com.solar.utility.screen.CentreFrame(jf);
    jf.show();
  }

  public CatgTreeMaint() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }
  private void jbInit() throws Exception {
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
    jLabel1.setBackground(SystemColor.activeCaption);
    jLabel1.setForeground(Color.yellow);
    jLabel1.setBorder(BorderFactory.createEtchedBorder());
    jLabel1.setOpaque(true);
    jLabel1.setToolTipText("");
    jLabel1.setText("GROUP NAME");
    jBut_reload.setEnabled(false);
    jBut_reload.setMaximumSize(new Dimension(32, 32));
    jBut_reload.setMinimumSize(new Dimension(32, 32));
    jBut_reload.setPreferredSize(new Dimension(32, 32));
    jBut_reload.setToolTipText("Reload Child");
    jBut_reload.setIcon(image8);
    jBut_reload.setMargin(new Insets(2, 2, 2, 2));
    jBut_reload.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_reload_actionPerformed(e);
      }
    });
    jLabel2.setToolTipText("");
    jLabel2.setText("Parent Group");
    jLabel3.setBorder(BorderFactory.createEtchedBorder());
    jLabel3.setMaximumSize(new Dimension(30, 21));
    jLabel3.setMinimumSize(new Dimension(30, 21));
    jLabel3.setPreferredSize(new Dimension(30, 21));
    jLabel3.setToolTipText("");
    jBut_update.setEnabled(false);
    jBut_update.setMaximumSize(new Dimension(32, 32));
    jBut_update.setMinimumSize(new Dimension(32, 32));
    jBut_update.setPreferredSize(new Dimension(32, 32));
    jBut_update.setToolTipText("Update");
    jBut_update.setIcon(image4);
    jBut_update.setMnemonic('0');
    jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12));
    jLabel4.setText("Category Relation Maintenance");
    jPanel1.setLayout(flowLayout1);
    jBut_logon.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_logon_actionPerformed(e);
      }
    });
    jTxt_magicword.setMinimumSize(new Dimension(70, 21));
    jTxt_magicword.setPreferredSize(new Dimension(70, 21));
    jTxt_magicword.setToolTipText("");
    jBut_refreshparent.setEnabled(false);
    jBut_refreshparent.setMaximumSize(new Dimension(32, 32));
    jBut_refreshparent.setMinimumSize(new Dimension(32, 32));
    jBut_refreshparent.setPreferredSize(new Dimension(32, 32));
    jBut_refreshparent.setToolTipText("Refresh Parent");
    jBut_refreshparent.setIcon(image9);
    jBut_refreshparent.setMargin(new Insets(2, 2, 2, 2));
    jBut_refreshparent.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_refreshparent_actionPerformed(e);
      }
    });
    jBut_update.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jBut_update_actionPerformed(e);
      }
    });

    jBut_logon.setMaximumSize(new Dimension(32, 32));
    jBut_logon.setMinimumSize(new Dimension(32, 32));
    jBut_logon.setPreferredSize(new Dimension(32, 32));
    jBut_logon.setToolTipText("Logon");
    jBut_logon.setIcon(image10);
    jBut_logon.setMnemonic('0');
    this.add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(jScrollPane1, JSplitPane.LEFT);
    jSplitPane1.add(jScrollPane2, JSplitPane.RIGHT);
    this.add(jPanel1, BorderLayout.SOUTH);
    jPanel1.add(jBut_logon, null);
    jPanel1.add(jTxt_magicword, null);
    jPanel1.add(jBut_refreshparent, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jBut_reload, null);
    jPanel1.add(jLabel3, null);
    jPanel1.add(jBut_update, null);
    this.add(jPanel2, BorderLayout.NORTH);
// setup parent table
    parentcolumns.add("catg_code");
    parentcolumns.add("description");
    parentcolumns.add("catg_key");
    parentcolumns.add("seclvl");
    parentcolumns.add("total");
    parentcolumns.add("select");
    parentdata.setColumns(parentcolumns);
    parenttable.setBackground(SystemColor.info);
    parenttable.setBorder(BorderFactory.createEtchedBorder());
    parenttable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    parenttable.setModel(parentdata);

    TableColumn tc=parenttable.getColumn("description");
    tc.setPreferredWidth(300);
    tc=parenttable.getColumn("catg_code");
    tc.setPreferredWidth(220);
    tc=parenttable.getColumn("catg_key");
    tc.setPreferredWidth(1);

    TableColumnModel tcm = null;
    tcm = parenttable.getColumnModel();
    tc=parenttable.getColumn("total");
    tcm.removeColumn(tc);
    tc=parenttable.getColumn("select");
    tcm.removeColumn(tc);
//    tc=parenttable.getColumn("catg_key");
//    tcm.removeColumn(tc);

// setup child table
    childcolumns.add("catg_code");
    childcolumns.add("description");
    childcolumns.add("catg_key");
    childcolumns.add("seclvl");
    childcolumns.add("total");
    childcolumns.add("select");
    childdata.setColumns(childcolumns);
    childtable.setBackground(SystemColor.info);
    childtable.setBorder(BorderFactory.createEtchedBorder());
    childtable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    childtable.setModel(childdata);

    tc=childtable.getColumn("description");
    tc.setPreferredWidth(250);
    tc=childtable.getColumn("catg_code");
    tc.setPreferredWidth(200);
    tc=childtable.getColumn("catg_key");
    tc.setPreferredWidth(1);

    tcm = childtable.getColumnModel();
    tc=childtable.getColumn("total");
    tcm.removeColumn(tc);
//    tc=childtable.getColumn("catg_key");
//    tcm.removeColumn(tc);

// setup location
    jScrollPane1.getViewport().add(parenttable, null);
    jScrollPane2.getViewport().add(childtable, null);
    jPanel2.add(jLabel4, null);

    this.setPreferredSize(new Dimension(600,400));
    this.setMinimumSize(new Dimension(600,400));
// Add table listener
    childdata.addTableModelListener(new TableModelListener() {
      public void tableChanged (TableModelEvent e) {
        childdata_tableChanged(e);
      }
    });

    parentdata.addTableModelListener(new TableModelListener() {
      public void tableChanged (TableModelEvent e) {
        parentdata_tableChanged(e);
      }
    });

    selectmodel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    selectmodel.addListSelectionListener( new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        table_selectionchange(e);
      }
    });

    parenttable.setSelectionModel(selectmodel);
    jSplitPane1.setDividerLocation(400);

  }  // end of jbInit()

  void setMMQuery(ora_mmquery x) {
    if (x!=null)
      mq = x;
  }
  public void queryResult(String passwd) {
    Vector v= mq.getCatgGrpDetails(passwd);
    System.out.println("vector size="+v.size());
    parentdata.setDataVector(v);
//    childdata.setDataVector(v);
  }
  public void refreshChildTable(int parentkey,String password) {
    Vector v = mq.getChildGrp(parentkey,password);
    System.out.println("Child vector size="+v.size());
    childdata.setDataVector(v);
  }
// Table Listeners
  void childdata_tableChanged(TableModelEvent e) {
  }
  void parentdata_tableChanged(TableModelEvent e) {
  }
  void table_selectionchange(ListSelectionEvent e) {
 //   parentdata.getValueAt(e.getLastIndex(),3)
   // System.out.println("selection changed:"+e.getFirstIndex()+","+e.getLastIndex()+":"+parentdata.getValueAt(e.getLastIndex(),2));
    jLabel1.setText( String.valueOf(parenttable.getValueAt(parenttable.getSelectedRow(),0)) );
  }

  void jBut_reload_actionPerformed(ActionEvent e) {
    int parentkey=Integer.parseInt(String.valueOf(parenttable.getValueAt(parenttable.getSelectedRow(),2)));
    refreshChildTable(parentkey,jTxt_magicword.getText());
    jBut_update.setEnabled(true);
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


  void jBut_logon_actionPerformed(ActionEvent e) {
    if (logindb()==0) {
      jBut_refreshparent.setEnabled(true);
      jBut_reload.setEnabled(true);

    }

  }

  void jBut_refreshparent_actionPerformed(ActionEvent e) {
    queryResult(jTxt_magicword.getText());
  }

  void jBut_update_actionPerformed(ActionEvent e) {
    Vector v = new Vector();
    for (int i=0;i<childdata.getRowCount();i++) {
      if (((Boolean)childdata.getValueAt(i,5)).booleanValue()) {
        v.add((Integer)childdata.getValueAt(i,2));
//        System.out.println((String)childdata.getValueAt(i,0)+","+(String)childdata.getValueAt(i,2));
      }
    }
    if (mq!=null) {
      System.out.println(parenttable.getValueAt(parenttable.getSelectedRow(),2));
      mq.updateCatgRelation(((Integer)parenttable.getValueAt(parenttable.getSelectedRow(),2)).intValue(),v);
      int parentkey=Integer.parseInt(String.valueOf(parenttable.getValueAt(parenttable.getSelectedRow(),2)));
      refreshChildTable(parentkey,jTxt_magicword.getText());
    }
    else
      com.solar.utility.MsgBox.show("DB Error","Connection handler not initialized!");
  }

}
