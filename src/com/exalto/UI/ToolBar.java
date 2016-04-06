package com.exalto.UI;

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
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


import com.exalto.util.XmlUtils;
import org.apache.log4j.Logger;


/**
 * ToolBar to control individual demo graphic attributes.  Also, control for
 * start & stop on animated demos; control for cloning the demo; control for
 * printing the demo.
 */
public class ToolBar extends JPanel implements ActionListener {

//    private static ImageIcon stopIcon = 
//            new ImageIcon(ToolBar.class.getResource("images/go.gif"));
//    private static ImageIcon startIcon = 
//            new ImageIcon(ToolBar.class.getResource("images/go.gif"));
    private static Font font = new Font("serif", Font.PLAIN, 10);

    private JButton xsltB;
    private JButton pdfB;
    private JButton updateB;
    private JButton xsltB2;
    private JButton pdfB2;
    private JButton updateB2;
    private JButton xsltB3;
    private JButton pdfB3;
    private JButton updateB3;

	private Logger logger;

    private JToolBar toolbar;
    private Thread thread;

	private XmlEditor xeditor;
 		
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
    XmlEditorActions _actions;

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



    public ToolBar(XmlEditor parent) {

		try {

		logger = Logger.getLogger(ToolBar.class.getName());
		logger.info("Inside Toolbar ctor::creating xutils");
		
	//	System.out.println("Inside Toolbar ctor::creating xutils");
		
	//	xutils = new XmlUtils();
		xutils = XmlUtils.getInstance();
		
                //OV c 21/03/08
	//	resources = xutils.getResourceBundle();
                
        toolbar = new JToolBar("Toolbar", JToolBar.HORIZONTAL);
       	xeditor = parent;
        
            // OV a 24/03/08
          _actions = XmlEditorActions.getSharedInstance(xeditor);
          
        String[] toolKeys = tokenize(xutils.getResourceString("toolbar"));

		logger = Logger.getLogger(ToolBar.class.getName());

		List list = Collections.synchronizedList(new LinkedList());
		for (int i = 0; i < toolKeys.length; i++) {
	    	if (toolKeys[i].equals("-")) {
				list.add("Strut");
	//			toolbar.add(Box.createHorizontalStrut(5));
	    	} else {
				list.add(createTool(toolKeys[i]));
	//    		toolbar.add(createTool(toolKeys[i]));
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
				toolbar.add(tbutt);
			} // added for wordwrap
			else if(obj instanceof JToggleButton) {
				JToggleButton tbutt = (JToggleButton) obj;
				toolbar.add(tbutt);			
			} else {
//				System.out.println("Inside if found strut");
				toolbar.add(Box.createHorizontalStrut(5));
			}
		}
		toolbar.add(Box.createHorizontalGlue());
		
//        xsltB = addTool("movie.gif", "generate FO", this);
//        updateB = addTool("graphic.gif", "Balance column widths", this);
//        pdfB = addTool("text.gif", "generate PDF", this);
        
        
//        toolbar.add(xsltB);
//        toolbar.add(updateB);
//        toolbar.add(pdfB);
        
//        toolbar.add(Box.createHorizontalGlue());
 
//        imgTypeCombo = new JComboBox();
//        imgTypeCombo.setFont(font);
//        imgTypeCombo.setBackground(Color.lightGray);
//        imgTypeCombo.addItem("Auto Screen");
//        imgTypeCombo.addItem("On Screen");
//        imgTypeCombo.addItem("Off Screen");
//        imgTypeCombo.addItem("INT_RGB");
//        imgTypeCombo.addItem("INT_ARGB");
//        imgTypeCombo.addItem("INT_ARGB_PRE");
//        imgTypeCombo.addItem("INT_BGR");
//        imgTypeCombo.addItem("3BYTE_BGR");
//        imgTypeCombo.addItem("4BYTE_ABGR");
//        imgTypeCombo.addItem("4BYTE_ABGR_PRE");
//        imgTypeCombo.addItem("USHORT_565_RGB");
//        imgTypeCombo.addItem("USHORT_555_RGB");
//        imgTypeCombo.addItem("BYTE_GRAY");
//        imgTypeCombo.addItem("USHORT_GRAY");
//        imgTypeCombo.addItem("BYTE_BINARY");
//        imgTypeCombo.setPreferredSize(new Dimension(100, 20));
//		imgTypeCombo.setSelectedIndex(0);
//        imgTypeCombo.addActionListener(this);

//		this.setSize(new Dimension(xeditor.WIDTH, 40));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//        this.setPreferredSize(new Dimension(xeditor.WIDTH, 64));
		toolbar.setPreferredSize(new Dimension(xeditor.WIDTH, 64));
         
        //        setBackground(Color.gray);
        add("Center", toolbar);
//        add(imgTypeCombo);

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

     //    System.out.println(" in tbar res load key = " + (key + imageSuffix));

	    URL url = xutils.getResource(key + imageSuffix);
		
	//    System.out.println(" in tbar url = " + url);

		logger.info("Inside Toolbar createtoolbarbutt::url" + url);

		JButton b = null;
		if("wordwrap".equals(key)) {
    		 JToggleButton jtb = new JToggleButton(new ImageIcon(url)) {
		     public float getAlignmentY() { return 0.5f; }
		  	 };	
			jtb.setRequestFocusEnabled(false);
			jtb.setMargin(new Insets(5,5,5,5));
			
			//OM31082015
			jtb.setSize(48, 48);
			String jtb_astr = xutils.getResourceString(key + actionSuffix);
			if (jtb_astr == null) {
			    jtb_astr = key;
			}
			Action a = _actions.getAction(jtb_astr);
			jtb.setActionCommand(jtb_astr);

			/*
			jtb.addActionListener( new ActionListener()	
			{
				public void actionPerformed(ActionEvent evt) 
				{
					AbstractButton b = (AbstractButton)(evt.getSource());      
						if (b instanceof JToggleButton)
						{ 
							JToggleButton jtb = (JToggleButton) b;
							xeditor.doWordWrap(jtb);
						}

				}
			});					       
			*/
			jtb.addActionListener(a);
			
			jtb.setRolloverIcon(new ImageIcon(url));

			URL url2 = xutils.getResource(key + "alt" + imageSuffix);
			jtb.setPressedIcon(new ImageIcon(url));
			jtb.setSelectedIcon(new ImageIcon(url2));

			 jtb.setRolloverEnabled(true);
			 jtb.setToolTipText("wordwrap");
			 jtb.setBorderPainted(false);

			jtb.setEnabled(true);
			
			String tip = xutils.getResourceString(key + tipSuffix);
			if (tip != null) {
				jtb.setToolTipText(tip);
			}
			
	        return jtb;

		} else {
			b = new JButton(new ImageIcon(url)) {
	  	        public float getAlignmentY() { return 0.5f; }
			};
		}
		
        b.setRequestFocusEnabled(false);
        b.setMargin(new Insets(1,1,1,1));
		b.setSize(64,64);

	String astr = xutils.getResourceString(key + actionSuffix);
	
  //  System.out.println(" toolbar action astr = " + astr);

    if (astr == null) {
	    astr = key;
	}

	Action a = _actions.getAction(astr);
	logger.info("Inside Toolbar createtoolbarbutt::acrion " + a);
//	System.out.println(" toolbar action = " + a);

	if (a != null) {
	    b.setActionCommand(astr);
	    b.addActionListener(a);
		b.setEnabled(a.isEnabled());
		a.addPropertyChangeListener(createActionChangeListener(b));
	} else {
			if(astr != null ) {
				if(astr.equals("xslt-transform")) {
					b.setActionCommand(astr);
					b.addActionListener( new ActionListener()	
					   {
							public void actionPerformed(ActionEvent evt) 
							{
								xeditor.doFO(); 
							}
					  });
					  
					b.setEnabled(true);

				} else if(astr.equals("auto-table-layout") ) {
					b.setActionCommand(astr);
						b.addActionListener( new ActionListener()	
						   {
								public void actionPerformed(ActionEvent evt) 
								{
									xeditor.doTableLayout(); 
								}
						  });
						b.setEnabled(true);

				} else if(astr.equals("generate-pdf")) {
						b.setActionCommand(astr);
							b.addActionListener( new ActionListener()	
							   {
									public void actionPerformed(ActionEvent evt) 
									{
										xeditor.doPdf(); 
									}
							  });
							b.setEnabled(true);
				
				} 
				else if(astr.equals("textview")) {
		//			System.out.println(" toolbar Text View ");

						b.setActionCommand(astr);
							b.addActionListener( new ActionListener()	
							   {
									public void actionPerformed(ActionEvent evt) 
									{
										xeditor.showTextView(evt); 
									}
							  });
					b.setEnabled(true);
				
				} 
				else if(astr.equals("gridview")) {
		//				System.out.println(" toolbar Grid View ");

						b.setActionCommand(astr);
							b.addActionListener( new ActionListener()	
							   {
									public void actionPerformed(ActionEvent evt) 
									{
										xeditor.showGridView(evt); 
									}
							  });
					b.setEnabled(true);
				
				} 
				else if(astr.equals("check-well-formedness")) {
		//			System.out.println(" toolbar well-fromed ");

							b.setActionCommand(astr);
								b.addActionListener( new ActionListener()	
								   {
										public void actionPerformed(ActionEvent evt) 
										{
											xeditor.doCheckWellFormed(); 
										}
								  });
						b.setEnabled(true);
					
					} 
				else if(astr.equals("check-validity")) {
	//				System.out.println(" toolbar Grid View ");

							b.setActionCommand(astr);
								b.addActionListener( new ActionListener()	
								   {
										public void actionPerformed(ActionEvent evt) 
										{
											xeditor.doCheckValidity(); 
										}
								  });
						b.setEnabled(true);
					
					} 
				
				/*
				else if(astr.equals("Word Wrap")) {
						b.setActionCommand(astr);
						b.addActionListener( new ActionListener()	
				 	    {
							public void actionPerformed(ActionEvent evt) 
							{
								xeditor.doWordwrap(b);
								
							}
						});					        	
				}
				*/
			} else 	
				b.setEnabled(false);
	}

	String tip = xutils.getResourceString(key + tipSuffix);
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
