package com.solar.mmdb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*
 * @version 1.9 05/03/99
 * @author Jeff Dinkins
 * @author Peter Korn (accessibility support)
 */
public class ScrollPanePanel extends JPanel      {
		PictureScrollPane ts=null;
    public ScrollPanePanel()    {
        setLayout(new BorderLayout());
        ts = new PictureScrollPane();
	add(ts, BorderLayout.CENTER);
    }
    public void setImageIcon(ImageIcon imageicon) {
    	ts.setImageIcon(imageicon);
    }

    public void setViewportView(Component obj) {
      ts.setViewportView(obj);
    }

}

class PictureScrollPane extends JScrollPane {

    private JLabel makeLabel(String name, String description) {
	String filename = "images/" + name;
	ImageIcon image = new ImageIcon(filename);
	return new JLabel(image);
    }

    public PictureScrollPane() {
	super();

//	JLabel horizontalRule = makeLabel("scrollpane/header.gif", "Horizontal ruler carved out of stone");
//	horizontalRule.getAccessibleContext().setAccessibleName("Horizontal rule");
//	JLabel verticalRule = makeLabel("scrollpane/column.gif", "Vertical ruler carved out of stone");
//	verticalRule.getAccessibleContext().setAccessibleName("Vertical rule");
	JLabel tiger = makeLabel("BigTiger.gif","A rather fierce looking tiger");
	tiger.getAccessibleContext().setAccessibleName("scrolled image");
	tiger.getAccessibleContext().setAccessibleDescription("A rather fierce looking tiger");

//	JLabel cornerLL = makeLabel("scrollpane/corner.gif","Square chunk of stone (lower left)");
//	cornerLL.getAccessibleContext().setAccessibleName("Lower left corner");
//	cornerLL.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
//	JLabel cornerLR = makeLabel("scrollpane/corner.gif","Square chunk of stone (lower right)");
//	cornerLR.getAccessibleContext().setAccessibleName("Lower right corner");
//	cornerLR.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
//	JLabel cornerUL = makeLabel("scrollpane/corner.gif","Square chunk of stone (upper left)");
//	cornerUL.getAccessibleContext().setAccessibleName("Upper left corner");
//	cornerUL.getAccessibleContext().setAccessibleDescription("Square chunk of stone");
//	JLabel cornerUR = makeLabel("scrollpane/corner.gif","Square chunk of stone (upper right)");
//	cornerUR.getAccessibleContext().setAccessibleName("Upper right corner");
//	cornerUR.getAccessibleContext().setAccessibleDescription("Square chunk of stone");

	setViewportView(tiger);
//	setRowHeaderView(verticalRule);
//	setColumnHeaderView(horizontalRule);

//	setCorner(LOWER_LEFT_CORNER, cornerLL);
//	setCorner(LOWER_RIGHT_CORNER, cornerLR);
//	setCorner(UPPER_LEFT_CORNER, cornerUL);
//	setCorner(UPPER_RIGHT_CORNER, cornerUR);
    }

    public void setImageIcon(ImageIcon imgicon) {
    	JLabel jl = new JLabel(imgicon);
    	setViewportView(jl);
    }


    public Dimension getMinimumSize() {
	return new Dimension(25, 25);
    }

}