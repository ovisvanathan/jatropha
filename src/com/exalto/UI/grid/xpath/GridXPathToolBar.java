package com.exalto.UI.grid.xpath;

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
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import com.exalto.UI.grid.ActionsHandler;
import com.exalto.UI.grid.JXmlTreeTable;
import com.exalto.util.ExaltoResource;
import com.exalto.util.XmlUtils;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import javax.swing.ComboBoxModel;


/**
 * ToolBar to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.
 */
public class GridXPathToolBar extends JToolBar implements ActionListener {

    private static Font font = new Font("serif", Font.PLAIN, 10);


	private Logger logger;


	private ActionsHandler treeModel;
 		
    public JComboBox imgTypeCombo;
    public JButton renderB, aliasB;
    public JButton textureB, compositeB;
    public JButton startStopB;
    public JButton cloneB;
    public boolean runSuccessful = false;
    public boolean loaded = false;

    XmlUtils xutils;
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
	

    static JXmlTreeTable treeTable;
    
//    static {
//        try {
//            resources = ResourceBundle.getBundle("resources.XmlEditor", 
//                                                 Locale.getDefault());
//        } catch (MissingResourceException mre) {
//            System.err.println("resources/XmlEditor.properties not found");
//            System.exit(1); 
//        }
//    }

    JComboBox comboBox;

    ArrayList comboItems;

    public ArrayList getComboItems() {
        return comboItems;
    }

    static GridXPathToolBar gridXPathToolBar;

    public static GridXPathToolBar getInstance(JXmlTreeTable treeTable, ActionsHandler parent) {

        if(gridXPathToolBar == null) {
            gridXPathToolBar = new GridXPathToolBar(treeTable, parent);
        }

        return gridXPathToolBar;

    }

    private GridXPathToolBar(JXmlTreeTable treeTable, ActionsHandler parent) {
    	
    	super("Toolbar", JToolBar.HORIZONTAL);
		
		try {
						
		logger = Logger.getLogger(GridXPathToolBar.class.getName());
		logger.info("Inside Toolbar ctor::creating xutils");
		
		xutils = XmlUtils.getInstance();
		
       // toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
		
       	treeModel = parent;
       	this.treeTable = treeTable;
       	
       	setFloatable(false);
       	
       	logger = Logger.getLogger(GridXPathToolBar.class.getName());

		
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        
 //       JLabel lab = new JLabel(java.util.ResourceBundle.getBundle(
//			"exalto/xmlgrid").getString("grid.xpath.search"));
    
        JLabel lab = new JLabel(" xpath to search");

        comboBox = new JComboBox();

        initCombo();

        comboBox.setEditable(true);

 //       JRoundButton rndButton = new JRoundButton(java.util.ResourceBundle.getBundle(
//		"exalto/xmlgrid").getString("grid.xpath.go")); 

        JRoundButton rndButton = new JRoundButton("Go"); 

        rndButton.addActionListener(this);
        
        add(lab);
        
        add(comboBox);
        
        add(rndButton);
        
        
		} catch(Exception e) {
			e.printStackTrace();
		}
    }
    


    private void initCombo() {

        String file = java.util.ResourceBundle.getBundle(
                "exalto/xmlgrid").getString("xpath.history.file");

        comboItems = new ArrayList();

        try {

             FileReader fred = new FileReader(new File(file));

             java.io.BufferedReader bufRead = new java.io.BufferedReader(fred);

             String s = null;
             while((s = bufRead.readLine()) != null) {

                 comboBox.addItem(s);

             }
         
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
    
    	if(obj instanceof JRoundButton) {
    		
    		String xpath = (String) comboBox.getModel().getSelectedItem();
    		
    		System.out.println(" do xpath search = " + xpath);

            ComboBoxModel cmbModel = comboBox.getModel();

            comboBox.addItem(xpath);

            comboItems.add(xpath);
    		
   // 		if(xpathCache.containsKey(frameid)) {
    			
   // 			XPathEvaluator xpe = xpathCache.get(frameid);
   
    	//		AatmaXPathEvaluator xpe = new AatmaXPathEvaluator(treeTable, treeModel);
    		
        //		DOML3XPathEvaluator xpe = new DOML3XPathEvaluator(treeTable, treeModel, null);
    	
    	//		ApacheXPathEvaluator xpe = new ApacheXPathEvaluator(treeTable, treeModel, null);
    			
    	//		JaxenXPathEvaluator xpe = new JaxenXPathEvaluator(treeTable, treeModel, null);

		
        //OM240815        JAXPXPathEvaluator xpe = new JAXPXPathEvaluator(treeTable, treeModel);
    	//OM240815		xpe.evaluate(xpath, true);
    			
    			
    			
    			
   // 		}
    	}
    	
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
