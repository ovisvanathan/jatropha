package com.exalto.UI;
/*
 * @(#)MetalworksPrefs.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;

import com.exalto.ColWidthTypes;
import com.exalto.util.ExaltoResource;
import com.exalto.util.StatusEvent;
import com.exalto.util.XmlUtils;



/**
  * This is dialog which allows users to choose preferences
  *
 * @version 1.7 12/03/01
  * @author Steve Wilson
  */
public class ApplicationPrefs extends JDialog implements ColWidthTypes, ActionListener,
ItemListener {
	
	FileDialog fopFileDialog;
	JButton browse;
	JFrame parent;
	String fopBat;
	String fopDir;
	JTextField pathFop;
	JTextField destDir;
	JTextField numIterTxt;
	String pathToFop;
	int noOfIters;
	String dest;
	Logger logger;
	JComboBox [] unitSpecs;
	JLabel [] labels	;
	JTextField [] textFields;
	
	
	XmlUtils xutils;
	
	// OV added
	 JCheckBox[] checkboxes = null;
	      JTextField pathPdf;
	
	      public final int CBOX_TAG = 0;
	      public final int CBOX_POPUP = 1;
	      public final int CBOX_CATALG = 2;
	      public final int CBOX_RELOAD = 3;
	      public final int CBOX_VALIDSAVE = 4;
	      public final int CBOX_INDENTSAVE = 5;
	
	      String[] colors = { "RED", "BLUE", "GREEN", "YELLOW", "MAGENTA",
	"PINK", "BLACK", "MORE" };
	
	      JComboBox elemCombo = null;
	      JComboBox attrCombo = null;
	      JComboBox piCombo = null;
	      JComboBox etyCombo = null;
	      JComboBox notnCombo = null;
	      JComboBox namespCombo = null;
	
	      JCheckBox txtview;
	      JCheckBox gridview;
	      JCheckBox treeview;
	      JTextField pathXsl;
	
	      JRadioButton utf8but;
	      JRadioButton otherbut;
	      JTextField encoding;
	
	      String disabletag = "false";
	      String disablepopup = "false";
	      String enablecatg = "true";
	      String validsave = "false";
	      String indentsave = "false";
	      String reloadlast = "true";
	      String defaultview = "false";
	      String pathToPdf = "";
	      String pathToXsl = "";
	      String defaultenc = "UTF-8";

	      Properties prop;

	      
      	  String folderKey = "folder";
	    	
	      String imageSuffix = "Image";

						
//	String [] unitNames = { "in", "cm" }; 
    
    public ApplicationPrefs(JFrame f) {
        super(f, "Preferences", true);
	
		logger = Logger.getLogger(ApplicationPrefs.class.getName());	
		xutils = XmlUtils.getInstance();

		parent = f;

	      XmlEditor xedit = (XmlEditor) parent;

	      prop = ((XmlEditor)parent).getPreferences();
		

		
		JPanel container = new JPanel();
	container.setLayout( new BorderLayout() );

 	JTabbedPane tabs = new JTabbedPane();
	JPanel filters = buildFilterPanel();
//	JPanel conn = buildSettingsPanel();

 	// OV added
 	JPanel editor = buildEditorPanel();

 	String lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.general.tab.label");
 	
	tabs.addTab( lbl, null, filters ); 
	// OV added
 	lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.editor.tab.label");

	tabs.addTab( lbl, null, editor );
	
//	tabs.addTab( "Settings", null, conn );


	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout ( new FlowLayout(FlowLayout.RIGHT) );

 	lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.cancel.button.label");

	JButton cancel = new JButton(lbl);
	cancel.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   CancelPressed();
			       }});
	buttonPanel.add( cancel );
	
	lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.ok.button.label");

	JButton ok = new JButton(lbl);
	ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
				   OKPressed();
			       }});
	buttonPanel.add( ok ); 
	getRootPane().setDefaultButton(ok);


    
	container.add(tabs, BorderLayout.CENTER);
	container.add(buttonPanel, BorderLayout.SOUTH);
	getContentPane().add(container);
	pack();
	xutils.centerDialog((JDialog)this);
//	UIManager.addPropertyChangeListener(new UISwitchListener(container));
    }



/**
  * This is dialog which allows users to choose preferences
  *
 * @version 1.7 12/03/01
  * @author Steve Wilson
  */
 public JPanel buildFilterPanel() {
      JPanel  prefs = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
//    setFont(new Font("Helvetica", Font.PLAIN, 14));
      prefs.setLayout(gridbag);

       c.insets = new Insets(5,5,5,5);  //top padding
       c.gridx=0;
       c.gridy=0;
       c.weightx=0.4;
       c.anchor = GridBagConstraints.WEST; //bottom of space

       String lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "specify.xsl-fo.processor");

       JLabel path = makeLabel(lbl, gridbag, c);

       c.gridx=1;
       c.gridy=0;
       c.weightx=0.3;
       pathFop = makeText(gridbag, c);

       c.gridx=2;
       c.gridy=0;
       c.weightx=1.0;
      // c.gridwidth = GridBagConstraints.REMAINDER; //end row
       c.anchor = GridBagConstraints.WEST; //bottom of space

       lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.browse.file.label");
       JButton browse = makebutton(lbl, gridbag, c);

       c.gridx=0;
       c.gridy=1;
       c.weightx=0.4;
      // c.gridwidth=2;
       lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "prefs.number.iterations");
       JLabel numIter = makeLabel(lbl, gridbag, c);

       c.gridx=1;
       c.gridy=1;
       c.weightx=0.3;
     numIterTxt = makeText(gridbag, c);


       c.gridx=0;
       c.gridy=2;
       c.gridwidth=2;
       JPanel cboxpane = new JPanel();
       gridbag.setConstraints(cboxpane, c);

       cboxpane.setLayout(new GridLayout(0,2));
 // Create checkboxes, and group them.
        checkboxes = new JCheckBox[6];
        
        lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "disable.tag.completion");        
        checkboxes[CBOX_TAG] = new JCheckBox(lbl);
            checkboxes[CBOX_TAG].setSelected(false);
            checkboxes[CBOX_TAG].addActionListener(this);

            lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "disable.popup.help");        
            checkboxes[CBOX_POPUP] = new JCheckBox(lbl);
            checkboxes[CBOX_POPUP].setSelected(false);
            checkboxes[CBOX_POPUP].addActionListener(this);

            lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "enable.catalogs");        
            checkboxes[CBOX_CATALG] = new JCheckBox(lbl);
            checkboxes[CBOX_CATALG].setSelected(false);
            checkboxes[CBOX_CATALG].addActionListener(this);

            lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "reload.last.open");        
        checkboxes[CBOX_RELOAD] = new JCheckBox(lbl);
            checkboxes[CBOX_RELOAD].setSelected(false);
            checkboxes[CBOX_RELOAD].addActionListener(this);

            lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "validate.on.save");        
        checkboxes[CBOX_VALIDSAVE] = new JCheckBox(lbl);
            checkboxes[CBOX_VALIDSAVE].setSelected(false);
            checkboxes[CBOX_VALIDSAVE].addActionListener(this);

            lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "indent.on.save");        
        checkboxes[CBOX_INDENTSAVE] = new JCheckBox(lbl);
            checkboxes[CBOX_INDENTSAVE].setSelected(false);
            checkboxes[CBOX_INDENTSAVE].addActionListener(this);

            cboxpane.add(checkboxes[0]);
            cboxpane.add(checkboxes[1]);
            cboxpane.add(checkboxes[2]);
            cboxpane.add(checkboxes[3]);
            cboxpane.add(checkboxes[4]);
            cboxpane.add(checkboxes[5]);


      lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "more.options");              
      prefs.setBorder( new TitledBorder(lbl) );

    browse.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
            Object obj = e.getSource();
            if(obj instanceof JButton) {
                  JButton fopB = (JButton) obj;
                        try {

                              if(fopFileDialog == null) {
                                          fopFileDialog = new FileDialog(getAppPrefs());
 	                                  
									fopFileDialog.setMode(FileDialog.LOAD);
                                    fopFileDialog.show();
                              } else {
                                          fopFileDialog.show();
                              }
                                        fopBat = fopFileDialog.getFile();
                                          fopDir = fopFileDialog.getDirectory();
                                          File f = new File(fopDir, fopBat);
                                    String fname = f.getCanonicalFile().getPath();
                        //          logger.debug(" selected file name = " + fname);
                                          System.out.println(" selected file name = " + fname);

                                          pathFop.setText(fname);

                              } catch(Exception ee) {
                        //          logger.error("unable to browse file");
                                    System.out.println(" unable to browse file ");

                              }

            }
      }
    });

    setGeneralPropertyValues();

      prefs.add(path);
      prefs.add(pathFop);
      prefs.add(browse);
      prefs.add(numIter);
      prefs.add(numIterTxt);

      prefs.add(cboxpane);

//    prefs.add(destPath);
//    prefs.add(destDir);
//    prefs.add(browseDest);

    prefs.setSize(200,175);


//    ButtonGroup respondGroup = new ButtonGroup();
//    JRadioButton none = new JRadioButton("None");
//    JRadioButton vaca = new JRadioButton("Send Vacation Message");
//    JRadioButton thx = new JRadioButton("Send Thank You Message");
//
//    respondGroup.add(none);
//    respondGroup.add(vaca);
//    respondGroup.add(thx);
//
//    autoRespond.add(none);
//    autoRespond.add(vaca);
//    autoRespond.add(thx);
//
//    none.setSelected(true);
//    prefs.add(otherOpts);

      return prefs;
    }


      public void setGeneralPropertyValues() {


            if(prop.getProperty("pathToFop") != null && 
					!prop.getProperty("pathToFop").equals(""))
					
                  pathFop.setText(prop.getProperty("pathToFop"));
      
	  		//    if(prop.getProperty("DestinationDirectory") != null)
		//		destDir.setText(prop.getProperty("DestinationDirectory"));

            if(prop.getProperty("noOfIterations") != null)
	            numIterTxt.setText(prop.getProperty("noOfIterations"));

            if(prop.getProperty("enablecatalog") != null)
				checkboxes[CBOX_CATALG].setSelected(Boolean.valueOf(((String)prop.getProperty("enablecatalog"))).booleanValue());

            if(prop.getProperty("disablepopup") != null)
				checkboxes[CBOX_POPUP].setSelected(Boolean.valueOf(((String)prop.getProperty("disablepopup"))).booleanValue());

            if(prop.getProperty("disabletagcompletion") != null)
				checkboxes[CBOX_TAG].setSelected(Boolean.valueOf(((String)prop.getProperty("disabletagcompletion"))).booleanValue());

            if(prop.getProperty("showsplash") != null)
				checkboxes[CBOX_VALIDSAVE].setSelected(Boolean.valueOf(((String)prop.getProperty("showsplash"))).booleanValue());

            if(prop.getProperty("indentonsave") != null)
				checkboxes[CBOX_INDENTSAVE].setSelected(Boolean.valueOf(((String)prop.getProperty("indentonsave"))).booleanValue());

            if(prop.getProperty("reloadlastopen") != null)
				checkboxes[CBOX_RELOAD].setSelected(Boolean.valueOf(((String)prop.getProperty("reloadlastopen"))).booleanValue());

      }

      public void setEditorPropertyValues() {

            XmlEditor xedit = (XmlEditor) parent;

		    Properties prop = ((XmlEditor)parent).getPreferences();

		    if(prop.getProperty("pathToXsl") != null &&
				!prop.getProperty("pathToXsl").equals(""))
            
				pathXsl.setText(prop.getProperty("pathToXsl"));

            	if(prop.getProperty("defaultview") != null) {
            	    String cview = prop.getProperty("defaultview");

                  if (cview.equals("text")) {
                        txtview.setSelected(true);
                  } else if (cview.equals("grid")) {
                        gridview.setSelected(true);
                  //...make a note of it...
                  } else if (cview.equals(treeview)) {
                        //...make a note of it...
                        treeview.setSelected(true);
                  }
            }

            if(prop.getProperty("readencoding") != null) {
                  String enc = prop.getProperty("readencoding");

                  if (enc.equals("UTF-8")) {
                        utf8but.setSelected(true);
                  } else {
                        otherbut.setSelected(true);
                  }
            }

            if(prop.getProperty("pathToPdf") != null &&
				!prop.getProperty("pathToPdf").equals(""))
                  pathPdf.setText(prop.getProperty("pathToPdf"));




      }    

	public void setPropertyValues() {

    	XmlEditor xedit = (XmlEditor) parent;
    	
    	Properties prop = ((XmlEditor)parent).getProperty();

		if(prop.getProperty("pathToFop") != null) 
			pathFop.setText(prop.getProperty("pathToFop"));
		if(prop.getProperty("DestinationDirectory") != null) 
			destDir.setText(prop.getProperty("DestinationDirectory"));
		if(prop.getProperty("noOfIterations") != null) 
		numIterTxt.setText(prop.getProperty("noOfIterations"));
	
	}    
    
    
    
    public JFrame getAppPrefs() {
    	return parent;
    }

   public JPanel buildEditorPanel() {

            JPanel editPane = new JPanel();

          GridBagLayout gridbag = new GridBagLayout();
            GridBagConstraints c = new GridBagConstraints();
            //    setFont(new Font("Helvetica", Font.PLAIN, 14));
            editPane.setLayout(gridbag);

             c.insets = new Insets(5,5,5,5);  //top padding
             c.gridx=0;
             c.gridy=0;
             c.weightx=0.4;
             c.anchor = GridBagConstraints.WEST; //bottom of space

             String lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "XSLT.processor.to.use");              

             JLabel xsltpath = makeLabel(lbl, gridbag, c);

             c.gridx=1;
             c.gridy=0;
             c.weightx=0.3;
             pathXsl = makeText(gridbag, c);

             c.gridx=0;
             c.gridy=1;
             c.weightx=1.0;
             c.gridwidth=2;
            // c.gridwidth = GridBagConstraints.REMAINDER; //end row
             c.anchor = GridBagConstraints.WEST; //bottom of space

             JPanel cbxpane = new JPanel();
             cbxpane.setLayout(new BoxLayout(cbxpane, BoxLayout.LINE_AXIS));

             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "select.startup.view");              
             JLabel title = makeLabel("<html>Select view to be used on startup </html>", gridbag, c);

             c.gridx=0;
             c.gridy=2;
             c.weightx=0.4;

             cbxpane.add(title);


             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "startup.text.view");               
             txtview = new JCheckBox(lbl);
             txtview.setSelected(true);
             txtview.addItemListener(this);

             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "startup.grid.view");               
             gridview = new JCheckBox(lbl);
             gridview.setSelected(false);
             gridview.addItemListener(this);

    //         treeview = new JCheckBox("Tree view");
    //         treeview.setSelected(false);
    //         treeview.addItemListener(this);

             //Group the radio buttons.
             ButtonGroup cbxgroup = new ButtonGroup();
     //        cbxgroup.add(treeview);
             cbxgroup.add(gridview);
             cbxgroup.add(txtview);


             cbxpane.add(txtview);
             cbxpane.add(gridview);
      //       cbxpane.add(treeview);

             gridbag.setConstraints(cbxpane, c);

             c.gridx=0;
             c.gridy=3;
             c.weightx=1.0;
             c.gridwidth = GridBagConstraints.REMAINDER; //end row
             c.anchor = GridBagConstraints.WEST; //bottom of space
        
             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "select.default.encoding");               
             title = makeLabel(lbl, gridbag, c);

             c.gridx=0;
             c.gridy=4;
             c.weightx=1.0;
            // c.gridwidth = GridBagConstraints.REMAINDER; //end row
             c.anchor = GridBagConstraints.WEST; //bottom of space

             JPanel encpane = new JPanel();
             encpane.setLayout(new BoxLayout(encpane, BoxLayout.LINE_AXIS));

             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "encoding.utf8");               
             utf8but = new JRadioButton(lbl);
             utf8but.setSelected(true);
             utf8but.setActionCommand("UTF-8");

             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "encoding.other");                
             otherbut = new JRadioButton(lbl);
             otherbut.setSelected(false);
             otherbut.setActionCommand("OTHER");

             encoding = makeText(gridbag, c);

             //Group the radio buttons.
             ButtonGroup group = new ButtonGroup();
             group.add(utf8but);
             group.add(otherbut);

            //Register a listener for the radio buttons.
            utf8but.addActionListener(this);
            otherbut.addActionListener(this);

            encpane.add(utf8but);
            encpane.add(otherbut);
            encpane.add(encoding);

            gridbag.setConstraints(encpane, c);

          c.gridx=0;
          c.gridy=5;
          c.weightx=0.4;
          // c.gridwidth=2;

          lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "enter.pdf.viewer");                

          JLabel pdfview = makeLabel(lbl, gridbag, c);

             c.gridx=1;
             c.gridy=5;
             c.weightx=0.3;
             pathPdf = makeText(gridbag, c);

             lbl = ExaltoResource.getString(ColWidthTypes.CONFIG, "more.options");                
            editPane.setBorder( new TitledBorder(lbl) );

            setEditorPropertyValues();

            editPane.add(xsltpath);
            editPane.add(pathXsl);
            editPane.add(title);
            editPane.add(cbxpane);
            editPane.add(encpane);
            editPane.add(pdfview);
            editPane.add(pathPdf);

            return editPane;

      }


    
    protected JButton makebutton(
							   String toolTipTxt,
                               GridBagLayout gridbag,
                               GridBagConstraints c) {
    	
    	 URL url = xutils.getResource(folderKey + imageSuffix);
    		
 //   	  ToolBar.class.getClassLoader().getResource("resources/folder.gif");
    			
//      URL url = ToolBar.class.getClassLoader().getResource("resources/folder.gif");
	  browse = new JButton(new ImageIcon(url));
      gridbag.setConstraints(browse, c);
	  browse.setToolTipText(toolTipTxt);
		return browse; 
     }

	 protected JLabel makeLabel(String name,
		                      GridBagLayout gridbag,
                               GridBagConstraints c) {


         JLabel label = new JLabel(name);
		 gridbag.setConstraints(label, c);
         return label;
     }

	 protected JTextField makeText(
                               GridBagLayout gridbag,
                               GridBagConstraints c) {
         JTextField txtField = new JTextField(30);
         gridbag.setConstraints(txtField, c);
         return txtField;
     }



    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
	Dimension size = this.getSize();
	screenSize.height = screenSize.height/2;
	screenSize.width = screenSize.width/2;
	size.height = size.height/2;
	size.width = size.width/2;
	int y = screenSize.height - size.height;
	int x = screenSize.width - size.width;
	this.setLocation(x,y);
    }

    public void CancelPressed() {
        this.setVisible(false);

    }

   public void OKPressed() {

            //Save General panel settings first
      pathToFop = pathFop.getText();
      try {
            noOfIters = Integer.parseInt(numIterTxt.getText());
      } catch(Exception e) {
            XmlEditor xedit = (XmlEditor) parent;
                  System.out.println("enter a valid number");
              //  xedit.setStatus("enter a valid number");
				xedit.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"enter.valid.number"),0, ColWidthTypes.ERROR));

      }


            //Save Editor panel settings
      pathToXsl = pathXsl.getText();

            if(defaultenc == null) {
                  defaultenc = encoding.getText();

                  if(defaultenc == null) {
                        // beep();
                        return;
                  }

            }

            pathToPdf = pathPdf.getText();

      XmlEditor xedit = (XmlEditor) parent;

      Properties prop = ((XmlEditor)parent).getPreferences();

      prop.setProperty("pathToFop", pathToFop);
      prop.setProperty("noOfIterations", new Integer(noOfIters).toString());
	    prop.setProperty("enablecatalog", enablecatg);
	    prop.setProperty("disablepopup", disablepopup);
	    prop.setProperty("disabletagcompletion", disabletag);
	    prop.setProperty("showsplash", validsave);
	    prop.setProperty("indentonsave", indentsave);
	    prop.setProperty("reloadlastopen", reloadlast);
      prop.setProperty("pathToXsl", pathToXsl);
      prop.setProperty("defaultview", defaultview);
      prop.setProperty("readencoding", defaultenc);
      prop.setProperty("pathToPdf", pathToPdf);

      try {
                  Calendar cal = Calendar.getInstance();
                  String propFile = xedit.getConfigFile();
              

                  propFile = "d:/aatma/bin/resources/config.properties"; 

                  System.out.println(" propFile " + propFile);
                	  
                	  
                  prop.store(new FileOutputStream(new File(propFile)),
						"Created By XmlEditor v1.0" + cal.getTime());
                  
                  
                  
                  
            } catch(java.io.IOException e) {
      //          logger.warn("Cannot save properties file");
                e.printStackTrace();  
            	System.out.println(" Cannot save properties file ");

    //            xedit.setStatus("Cannot save properties file");
            	xedit.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"cannot.save.properties"),0, ColWidthTypes.ERROR));

            }
        this.setVisible(false);
    }
	
	
// Ok, something in the font changed, so figure that out and make a
  // new font for the preview label
  public void actionPerformed(ActionEvent ae) {

        Object obj = ae.getSource();
        java.awt.Color color = null;

        if(obj instanceof JCheckBox) {

            JCheckBox jcbx = (JCheckBox) obj;

            if(jcbx == checkboxes[CBOX_CATALG]) {
                  System.out.println(" catalog cbox is " + jcbx.getModel().isSelected());
                  enablecatg = String.valueOf(jcbx.getModel().isSelected());
            } else if(jcbx == checkboxes[CBOX_POPUP]) {
                  System.out.println(" popup cbox is " +
						jcbx.getModel().isSelected());
                  disablepopup = String.valueOf(jcbx.getModel().isSelected());
            } else if(jcbx == checkboxes[CBOX_TAG]) {
                  System.out.println(" tag completion cbox is " +
					jcbx.getModel().isSelected());
                  disabletag = String.valueOf(jcbx.getModel().isSelected());
            } else if(jcbx == checkboxes[CBOX_VALIDSAVE]) {
                  System.out.println(" valid save cbox is " +
						jcbx.getModel().isSelected());
                  validsave = (jcbx.getModel().isSelected())? "true" : "false"; 
                  System.out.println(" validsave is " + validsave);
                  
            } else if(jcbx == checkboxes[CBOX_INDENTSAVE]) {
                  System.out.println(" indent save cbox is " +
						jcbx.getModel().isSelected());
                  indentsave = String.valueOf(jcbx.getModel().isSelected());
            } else if(jcbx == checkboxes[CBOX_RELOAD]) {
                  System.out.println(" reload last cbox is " +
					jcbx.getModel().isSelected());
                  reloadlast = String.valueOf(jcbx.getModel().isSelected());
            }



        }
        else if(obj instanceof JComboBox) {

            JComboBox jcmbx = (JComboBox) obj;

            String colorName = (String)jcmbx.getSelectedItem();

            if(colorName.equals("more")) {
     //             color = getColorFromChooser();
	 				color = Color.RED;
            }
        else {
                  if(jcmbx == elemCombo) {
                        String cname = (String)jcmbx.getSelectedItem();
                        System.out.println(" element color is " + jcmbx.getSelectedItem());
                  } else if(jcmbx == attrCombo) {
                        System.out.println(" attr color is " +
						jcmbx.getSelectedItem());
                  }
            }

        }
        else if(obj instanceof JRadioButton) {
             String encval = ae.getActionCommand();
             if(encval.equals("UTF-8")) {
                        System.out.println(" user selected utf-8 ");
                        defaultenc = "UTF-8";
             }
        }

	  }	

 	public void itemStateChanged(ItemEvent e) {

            Object source = e.getItemSelectable();
            boolean selected = false;

            if (e.getStateChange() == ItemEvent.SELECTED)
                  selected = true;

            if (source == txtview) {
                  System.out.println(" text view is " + selected);
                  defaultview = "text";
            } else if (source == gridview) {
                  System.out.println(" grid view is " + selected);
                  defaultview = "grid";
                  //...make a note of it...
            } else if (source == treeview) {
                  //...make a note of it...
                  System.out.println(" tree view is " + selected);
                  defaultview = "tree";
            }

      }


}

class ColumnLayout implements LayoutManager {

	int xInset = 5;
	int yInset = 5;
	int yGap = 2;
	int xGap = 2;
	int xoffset = 0;
	int colNum = 0;  

  public void addLayoutComponent(String s, Component c) {}

  public void layoutContainer(Container c) {

      Insets insets = c.getInsets();
      int height = yInset + insets.top;
	  int n =3;
	        
      Component[] children = c.getComponents();
      Dimension compSize = null;
      for (int i = 0; i < children.length; i++) {
		  compSize = children[i].getPreferredSize();
		  children[i].setSize(compSize.width, compSize.height);
	
		  children[i].setLocation( xInset + insets.left + xoffset, height);
		  
	  	  n--;
		  if(n == 0) {
		  	xGap += 75;
		  	n = 3;
		  }
		  xoffset += compSize.width + xGap; 
		  	xGap =2;
						  			  
		  colNum++;
		  		
		  if(colNum > 5) {
		  	colNum = 0;
		  	xoffset =0;
		  }
	
		if(colNum == 0)
			height += compSize.height + yGap;
	}
  }

  public Dimension minimumLayoutSize(Container c) {
      Insets insets = c.getInsets();
      int height = yInset + insets.top;
      int width = 0 + insets.left + insets.right;
      
      Component[] children = c.getComponents();
      Dimension compSize = null;
      Dimension compSize2 = null;

	  System.out.println(" array len = " + children.length);
      
      for (int i = 0; i < children.length; i+=3) {
		  compSize = children[i].getPreferredSize();
		  compSize2 = children[i+1].getPreferredSize();
		  height += compSize.height + yGap;
		  width = Math.max(width, compSize.width + compSize2.width + insets.left + insets.right + xInset*2);
      }
      height += insets.bottom;
      return new Dimension( width, height);
  }
  
  public Dimension preferredLayoutSize(Container c) {
      return minimumLayoutSize(c);
  }
   
  public void removeLayoutComponent(Component c) {}

}
