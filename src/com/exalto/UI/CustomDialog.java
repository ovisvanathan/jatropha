package com.exalto.UI;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/* 1.4 example used by DialogDemo.java. */
class CustomDialog extends JDialog
                   implements ActionListener,
                              PropertyChangeListener {
    private String typedText = null;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;

    private XmlEditor dd;
    private FileDialog fileDialog;

    private String magicWord;
    private JOptionPane optionPane;

    private String btnString1 = "OK";
    private String btnString2 = "Cancel";
	private Logger logger;
	
    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }

    /** Creates the reusable dialog. */
    public CustomDialog(Frame aFrame) {
        super(aFrame, true);
        dd = (XmlEditor) aFrame;

		logger = Logger.getLogger(CustomDialog.class.getName());

//        magicWord = aWord.toUpperCase();
        setTitle("Enter file names");

        textField1 = new JTextField();
        textField2 = new JTextField();
        textField3 = new JTextField();

        //Create an array of the text and components to be displayed.
        String msgString1 = "Enter name of XHTML file to use:";
        String msgString2 = "Enter name of FO file to use:";
        String msgString3 = "Enter name of PDF file to use:";
        
        
 //       String msgString2 = "(The answer is \"" + magicWord
   //                           + "\".)";
   
   JButton browseXml = new JButton("BrowseFile");
   browseXml.addActionListener(new ActionListener() {
   
   			public void actionPerformed(ActionEvent e) {
   				String xmlFile =null;
   				String xmlFileDir = null;
   	
   				try {
						if(fileDialog == null) {
							fileDialog = new FileDialog(dd);
		
				    	  	fileDialog.setMode(FileDialog.LOAD);
		    				fileDialog.show();
						} else {				
		    				fileDialog.show();
						}
					    xmlFile = fileDialog.getFile();
						xmlFileDir = fileDialog.getDirectory();
						File f = new File(xmlFileDir, xmlFile);
	//	    				String fname = f.getCanonicalFile().getName();
	    				String fname = f.getCanonicalFile().getPath();
	//	    				fname = pathname + "\\" + fname;
	    				System.out.println(" file name = " + fname);
	 					textField1.setText(fname);
	 					int dpos = fname.lastIndexOf(".");		
	 					String foname = fname.substring(0, dpos);
	 					textField2.setText(foname + "out.fo");
	 					textField2.setText(foname + "out.pdf");	 					
	    				
					} catch(Exception ee) {
						logger.error("unable to browse file");
					}
   				
   				}});

   JButton browseFO = new JButton("BrowseFile");
   browseFO.addActionListener(new ActionListener() {
   
   			public void actionPerformed(ActionEvent e) {
   				String foFile =null;
   				String foFileDir = null;
   	
   				try {
						if(fileDialog == null) {
							fileDialog = new FileDialog(dd);
		
				    	  	fileDialog.setMode(FileDialog.LOAD);
		    				fileDialog.show();
						} else {				
		    				fileDialog.show();
						}
					    foFile = fileDialog.getFile();
						foFileDir = fileDialog.getDirectory();
						File f = new File(foFileDir, foFile);
	//	    				String fname = f.getCanonicalFile().getName();
	    				String fname = f.getCanonicalFile().getPath();
	//	    				fname = pathname + "\\" + fname;
	    				System.out.println(" file name = " + fname);
	 					textField1.setText(fname);
	 					int dpos = fname.lastIndexOf(".");		
	 					String foname = fname.substring(0, dpos);
	 					logger.info("xmlname = " + foname);
	 					textField2.setText(foname + ".out.fo");
	 					logger.info("foname = " + textField2.getText());
	 					textField3.setText(foname + ".out.pdf");	 					
	 					logger.info("pdfname = " + textField3.getText());
	    				
					} catch(Exception ee) {
						logger.error("unable to browse file");
					}
   				
   				}});


   
     
        Object[] array = {msgString1, textField1, browseXml, msgString2, textField2,
        					browseFO, msgString3, textField3 };

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
                                    

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                    optionPane.setValue(new Integer(
                                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
       //         textField1.requestFocusInWindow();
	   				System.out.println("");
            }
        });

        //Register an event handler that puts the text into the option pane.
//        textField1.addActionListener(this);
//        textField1.addActionListener(this);
//        textField1.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the text field. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
         && (e.getSource() == optionPane)
         && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
             JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                    dd.setXmlFile(textField1.getText());
                    dd.setFmtObjFile(textField2.getText());
                    dd.setPdfFile(textField3.getText());
                    
                    
                    
//                String ucText = typedText.toUpperCase();
//                if (magicWord.equals(ucText)) {
//                    //we're done; clear and dismiss the dialog
//                    clearAndHide();
//                } else {
//                    //text was invalid
//                    textField.selectAll();
//                    JOptionPane.showMessageDialog(
//                                    CustomDialog.this,
//                                    "Sorry, \"" + typedText + "\" "
//                                    + "isn't a valid response.\n"
//                                    + "Please enter "
//                                    + magicWord + ".",
//                                    "Try again",
//                                    JOptionPane.ERROR_MESSAGE);
//                    typedText = null;
//                    textField.requestFocusInWindow();
//                }
            } else { //user closed dialog or clicked cancel
//                dd.setLabel("It's OK.  "
//                         + "We won't force you to type "
//                         + magicWord + ".");
//                typedText = null;
				dd.setStatus("no files names selected");
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        textField1.setText(null);
        textField2.setText(null);
        textField3.setText(null);

        setVisible(false);
    }
}
