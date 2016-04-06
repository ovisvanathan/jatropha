package com.exalto.UI.util;

/*
 * @(#)ToolBar.java	1.22  98/09/22
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import com.exalto.UI.grid.JXmlTreeTable;
import com.exalto.util.ExaltoResource;
import com.exalto.util.XmlUtils;


/**
 * ToolBar to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.
 */
public class GridToolBar extends JToolBar implements ActionListener {

    private static Font font = new Font("serif", Font.PLAIN, 10);

    private JButton insElemB;
    private JButton insAttrB;
    private JButton insTextB;
    private JButton renElemB;
    private JButton delElemB;
    private JButton delAttrB;
    private JButton delTextB;
    private JButton expAllB;
    private JButton expNodeB;

	private Logger logger;

    private Thread thread;

	private JXmlTreeTable treeTable;
 		
    public JComboBox imgTypeCombo;
    public JButton renderB, aliasB;
    public JButton textureB, compositeB;
    public JButton startStopB;
    public JButton cloneB;
    public boolean runSuccessful = false;
    public boolean loaded = false;
	private String outfile;    
    private XmlUtils xutils;

    //OV a 20/03/08 1 line
    HashMap _actions;

    //OV c 21/03/08
//    private static ResourceBundle resources;
    
   /**
     * Suffix applied to the key used in resource file
     * lookups for an image.
     */
    private static final String imageSuffix = "Image";

    /**
     * Suffix applied to the key used in resource file
     * lookups for an action.
     */
    private static final String actionSuffix = "Action";

    /**
     * Suffix applied to the key used in resource file
     * lookups for tooltip text.
     */
    private static final String tipSuffix = "Tooltip";
	

//    static {
//        try {
//            resources = ResourceBundle.getBundle("resources.XmlEditor", 
//                                                 Locale.getDefault());
//        } catch (MissingResourceException mre) {
//            System.err.println("resources/XmlEditor.properties not found");
//            System.exit(1); 
//        }
//    }



    public GridToolBar(JXmlTreeTable parent) {
    	
    	super("Toolbar", JToolBar.HORIZONTAL);
		
		try {
						
		logger = Logger.getLogger(GridToolBar.class.getName());
		logger.info("Inside Toolbar ctor::creating xutils");
		
		xutils = XmlUtils.getInstance();
		
       // toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
       	treeTable = parent;
        
       	setFloatable(false);
       	
           // OV a 24/03/08
         _actions = treeTable.getActionsHandler().getGridCommands();
          
			
         String[] toolKeys = tokenize(java.util.ResourceBundle.getBundle(
			"exalto/xmlgrid").getString("grid_toolbar"));

		 logger = Logger.getLogger(GridToolBar.class.getName());

		List list = Collections.synchronizedList(new LinkedList());
		for (int i = 0; i < toolKeys.length; i++) {
	    	if (toolKeys[i].equals("-")) {
				list.add("Strut");
	    	} else {
				list.add(createTool(toolKeys[i]));
	    	}
		}
	
		XmlUtils.equalizeSizes(list);

	//	System.out.println("Inside Toolbar ctor::after equalize sizes");

		Iterator itr = list.iterator();
		while(itr.hasNext()) {
//				System.out.println("Inside while iter");
			Object obj = itr.next();
			if(obj instanceof JButton) { 
//				System.out.println("Inside if found jbutton");
				JButton tbutt = (JButton) obj;
				add(tbutt);
			} // added for wordwrap
			else if(obj instanceof JToggleButton) {
				JToggleButton tbutt = (JToggleButton) obj;
				add(tbutt);			
			} else {
//				System.out.println("Inside if found strut");
				add(Box.createHorizontalStrut(5));
			}
		}
		
		add(Box.createHorizontalGlue());
		
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	//	setPreferredSize(new Dimension(xeditor.WIDTH, 64));
         
     //   add("Center", toolbar);

		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    
    
        /**
     * Hook through which every toolbar item is created.
     */
    protected Component createTool(String key) {
		logger.info("Inside Toolbar createtool::key" + key);
		
//		JButton jb = createToolbarButton(key);
		return createToolbarButton(key);
    }

    /**
     * Create a button to go inside of the toolbar.  By default this
     * will load an image resource.  The image filename is relative to
     * the classpath (including the '.' directory if its a part of the
     * classpath), and may either be in a JAR file or a separate file.
     * 
     * @param key The key in the resource file to serve as the basis
     *  of lookups.
     */ 
//    protected JButton createToolbarButton(String key) {
     protected Component createToolbarButton(String key) {

		logger.info("Inside Toolbar createtoolbarbutt::key" + (key + imageSuffix));

         System.out.println(" in tbar res load key = " + (key + imageSuffix));

		URL url = ExaltoResource.class.getResource(java.util.ResourceBundle.getBundle(
		"exalto/xmlgrid").getString(key + imageSuffix));
		
	 //   URL url = xutils.getResource(key + imageSuffix);
		
	  //  System.out.println(" in tbar url = " + url);

		logger.info("Inside Toolbar createtoolbarbutt::url" + url);

		JButton b = null;
		b = new JButton(new ImageIcon(url)) {
  	        public float getAlignmentY() { return 0.5f; }
		};
		
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(1,1,1,1));
		b.setSize(64,64);

		
		String astr = 	java.util.ResourceBundle.getBundle(
							"exalto/xmlgrid").getString(key + actionSuffix);
		
	//	String astr = xutils.getResourceString(key + actionSuffix);
		if (astr == null) {
		    astr = key;
		}

		System.out.println(" astr = " + astr);
		
	Action a = (Action) _actions.get(astr);
	logger.info("Inside Toolbar createtoolbarbutt::acrion " + a);

	System.out.println(" in tbar action key = " + key);
	System.out.println(" in tbar action = " + a);
	
	
	if (a != null) {
	//	System.out.println(" toolbar action = " + a.toString());
	    b.setActionCommand(astr);
	    b.addActionListener(a);
		b.setEnabled(a.isEnabled());
		a.addPropertyChangeListener(createActionChangeListener(b));
	} 
    
	String tip = 	java.util.ResourceBundle.getBundle(
		"exalto/xmlgrid").getString(key + tipSuffix);

	//	String tip = xutils.getResourceString(key + tipSuffix);
	if (tip != null) {
	    b.setToolTipText(tip);
	}
 
        return b;
    }




//    public JButton addTool(String str, 
//                           String toolTip,
//                           ActionListener al) {
//        JButton b = null;
//            URL url = ToolBar.class.getClassLoader().getResource("images/" + str);
//            b = new JButton(new ImageIcon(url)) {
//            public float getAlignmentY() { return 0.5f; }
//	};
//		 b.setRequestFocusEnabled(false);
//    	 b.setMargin(new Insets(1,1,1,1));
//        
//     //       b.setSelected(true);
//      
//        b.setToolTipText(toolTip);
//        b.addActionListener(al);
//        return b;
//    }

     /* OV c 21/03/08
    protected String getResourceString(String nm) {
	String str;
	try {
	    str = resources.getString(nm);
	} catch (MissingResourceException mre) {
		logger.warn("Missing resource for key in properties file " + mre.getMessage());
	    str = null;
	}
	return str;
    }
    */
     
    /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundries.  This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    protected String[] tokenize(String input) {
	Vector v = new Vector();
	StringTokenizer t = new StringTokenizer(input);
	String cmd[];

	while (t.hasMoreTokens())
	    v.addElement(t.nextToken());
	cmd = new String[v.size()];
	for (int i = 0; i < cmd.length; i++)
	    cmd[i] = (String) v.elementAt(i);

	return cmd;
    }
    


    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
       /*
        if (obj.equals(xsltB)) {
			try {
			XslTransformer xtrans = new XslTransformer(xeditor.getXmlFile(), "xhtml2fo.xsl");
			xtrans.transform(); 
        	xeditor.setFmtObjFile(xtrans.getFmtObjFile());
			} catch(Exception ex) {
				logger.error("Filename does not end with xhtml");
				xeditor.setStatus("Filename does not end with xhtml" + ex.getMessage());
			}
        	return;
        } 
        if (obj.equals(updateB)) {
			try {
			TableAutoFormatter ddemo = new TableAutoFormatter(xeditor.getFmtObjFile());	 		
			outfile = ddemo.initiateAction();	
			runSuccessful = true;
			xeditor.setStatus("scan completed");
			} catch(Exception pe) {
				pe.printStackTrace();
			} 
        	return;
        }

        if (obj.equals(pdfB)) { 
        	try {
			
				Properties p = xeditor.getProperty();
				String pathToFop = p.getProperty("pathToFop");
				
				if(pathToFop == null) {
					throw new Exception("Fop path not set");
				}
			if(runSuccessful ) {
        	 	Runtime.getRuntime().exec("H:\\fop-0.20.5\\fop.bat " + outfile + " " + outfile + ".pdf" );
				xeditor.setStatus("pdf generation completed");
        	}
        	} catch(Exception ie) {
        		ie.printStackTrace();
        		xeditor.setStatus("FOP path not set");
        	}
        	return;
        }
		*/
        
    }


    public Dimension getPreferredSize() {
        return new Dimension(200,38);
    }
	
	// Yarked from JMenu, ideally this would be public.
	    protected PropertyChangeListener createActionChangeListener(JButton b) {
			return new ActionChangedListener(b);
	    }


    // Yarked from JMenu, ideally this would be public.
    private class ActionChangedListener implements PropertyChangeListener {
        JButton jb;
		
		
		ActionChangedListener(JButton jbutt) {
		     super();
		     this.jb = jbutt;
		}

        public void propertyChange(PropertyChangeEvent e) {
		
			String propertyName = e.getPropertyName();
            if (e.getPropertyName().equals(Action.NAME)) {
                String text = (String) e.getNewValue();
                jb.setText(text);
            } else if (propertyName.equals("enabled")) {
                Boolean enabledState = (Boolean) e.getNewValue();
				jb.setEnabled(enabledState.booleanValue());				
	//			System.out.println("inside prop change event");
            }
        }
    }





}
