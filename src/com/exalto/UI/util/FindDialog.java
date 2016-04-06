package com.exalto.UI.util;
/*
 * $Id: FindDialog.java,v 1.2 2002/10/22 14:20:13 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Edwin Dankert <edankert@cladonia.com>
 */


import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;


import java.text.DateFormat;

import java.util.Vector;
import java.util.Date;

import java.io.StringWriter;
import java.io.File;

import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.BorderFactory;
import java.awt.Insets;


import com.exalto.UI.XmlEditor;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



/**
 * The find dialog for the editor.
 *
 * @version	$Revision: 1.2 $, $Date: 2002/10/22 14:20:13 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public class FindDialog extends JDialog {
	private static final Dimension SIZE = new Dimension( 250, 150);

	private XmlEditor editor = null;

//	private JButton cancelButton	= null;
//	private JButton findButton		= null;
	
	// The components that contain the values
//	private JComboBox jtfPhrase		= null;

//	private JCheckBox matchCaseButton	= null;
//	private JRadioButton upButton		= null;
//	private JRadioButton downButton		= null;
	
	  /* ---- GUI elements start ---------*/
	
	  private TitledBorder titledBorder1;
	  private JButton jbtnFindNext = new JButton();
	  private JCheckBox jcbStartOnTop = new JCheckBox();
	  private JRadioButton jrbDown = new JRadioButton();
	  private JCheckBox jcbWholeWords = new JCheckBox();
	  private JPanel jpnlBtn = new JPanel();
	  private JPanel jpnlOptions = new JPanel();
	  private JPanel jpnlFind = new JPanel();
	  private JTextField jtfReplace = new JTextField();
	  private JPanel jpnlMain = new JPanel();
	  private JRadioButton jrbUp = new JRadioButton();
	  private JTextField jtfPhrase = new JTextField();
	  private JCheckBox jcbMatchCase = new JCheckBox();
	  private JLabel jLabel3 = new JLabel();
	  private JLabel jLabel4 = new JLabel();
	  private GridBagLayout gridBagLayout4 = new GridBagLayout();
	  private GridBagLayout gridBagLayout5 = new GridBagLayout();
	  private JButton jbtnClose = new JButton();
	  private GridBagLayout gridBagLayout6 = new GridBagLayout();
	  private GridLayout gridLayout2 = new GridLayout();
	  private JButton jbtnReplace = new JButton();
	  private JButton jbtnCancel = new JButton();
	  private JCheckBox jcbUnused = new JCheckBox();
	  private JCheckBox jcbProject = new JCheckBox();
	  private boolean searchDirection = true;		
	  //omp added on 18/9/05
	  private boolean matchWord = false;
	  private boolean matchCase = false;

	  /* ---- GUI elements end ---------*/

	
	
	

	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public FindDialog( XmlEditor editor) {
		super( (JFrame) editor, false);
		
		this.editor = editor;
		
		setResizable( false);
		setTitle( "Find");
		setSize( SIZE);
		
		
		try {
			jbInit();
		} catch(Exception e) {
			e.printStackTrace();
		}
		initDialogContents();
		pack();
		
		WindowListener flst = new WindowAdapter() {
		public void windowActivated(WindowEvent e) {
			getEditor().searchIndex = -1;
		}
		
		public void windowDeactivated(WindowEvent e) {
			getEditor().searchData = null;
		}
		};
		
		addWindowListener(flst);

/*		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel searchPanel = new JPanel( new FormLayout( 10, 0));
	
		jtfPhrase = new JComboBox();
		jtfPhrase.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					findButtonPressed();
				}
			}
		});

		jtfPhrase.setFont( jtfPhrase.getFont().deriveFont( Font.PLAIN));
		jtfPhrase.setPreferredSize( new Dimension( 100, 19));
		jtfPhrase.setEditable(true);

		searchPanel.add( new JLabel("Find:"), FormLayout.LEFT);
		searchPanel.add( jtfPhrase, FormLayout.RIGHT_FILL);

		matchCaseButton = new JCheckBox( "Match case");
		matchCaseButton.setMnemonic( 'M');
		matchCaseButton.setFont( matchCaseButton.getFont().deriveFont( Font.PLAIN));

		JPanel matchCasePanel = new JPanel( new BorderLayout());
		matchCasePanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		matchCasePanel.add( matchCaseButton, BorderLayout.NORTH);

		upButton = new JRadioButton( "Up");
		upButton.setMnemonic( 'U');
		upButton.setFont( upButton.getFont().deriveFont( Font.PLAIN));
		downButton = new JRadioButton( "Down");
		downButton.setFont( upButton.getFont());
		downButton.setMnemonic( 'D');
		downButton.setSelected( true);

		ButtonGroup group = new ButtonGroup();
		group.add( upButton);
		group.add( downButton);
		
		JPanel directionPanel = new JPanel( new FormLayout( 10, 0));
	
		directionPanel.add( new JLabel("Direction:"), FormLayout.LEFT);
		directionPanel.add( upButton, FormLayout.RIGHT);
		directionPanel.add( new JLabel(), FormLayout.LEFT);
		directionPanel.add( downButton, FormLayout.RIGHT);
		directionPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));

		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		findButton = new JButton( "Find");
		findButton.setMnemonic( 'F');
		findButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				findButtonPressed();
			}
		});
		getRootPane().setDefaultButton( findButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( findButton);
		buttonPanel.add( cancelButton);

		main.add( searchPanel, BorderLayout.NORTH);
		main.add( matchCasePanel, BorderLayout.WEST);
		main.add( directionPanel, BorderLayout.EAST);
		main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);

		setLocationRelativeTo( editor);

 */		
		
	}
	
	private void findButtonPressed() {
	
		String searchtext = jtfPhrase.getText();
		// ov commented 25/3/05		
		// editor.search( searchtext, jcbMatchCase.isSelected(), jrbDown.isSelected());		
		editor.search( searchtext, matchCase, searchDirection, matchWord, false, null, false);		
	//	setVisible( false); 
	}
	
	private void replaceButtonPressed() {
		
			String searchtext = jtfPhrase.getText();	
			String replacetext = jtfReplace.getText();	
			
			editor.search( searchtext, matchCase, searchDirection, matchWord, true, replacetext, false);
		//	setVisible(false); 
	}
	
		public void replaceAllButtonPressed() {	
			int counter = 0;
			
				System.out.println(" in replaceAllButtonPressed");
			
				String searchtext = jtfPhrase.getText();	
				String replacetext = jtfReplace.getText();	
				editor.searchIndex = -1;
						
				editor.search( searchtext, matchCase, searchDirection, matchWord, true, replacetext, true);
					
	
		//	JOptionPane.showMessageDialog(m_owner, counter+" replacement(s) have been done", HtmlProcessor.APP_NAME, JOptionPane.INFORMATION_MESSAGE);
		
		}
	
	

	private void cancelButtonPressed() {
		setVisible( false);
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void init() {
		jtfPhrase.requestFocus();
		
	//	if ( jtfPhrase.getItemCount() > 0) {
	//		jtfPhrase.removeAllItems();
	//	}
		
	//	Vector searches = editor.getProperties().getSearches();
		
	//	for ( int i = 0; i < searches.size(); i++) {
	//		jtfPhrase.addItem( searches.elementAt(i));
	//	}

	//	if ( jtfPhrase.getItemCount() > 0) {
	//		jtfPhrase.setSelectedIndex( 0);
	//	}

	//	jcbMatchCase.setSelected( editor.getProperties().isMatchCase());
		
	//	if ( editor.getProperties().isDirectionDown()) {
	//		jrbDown.setSelected( true);
	//	} else {
	//		jrbUp.setSelected( true);
	//	}

//		jtfPhrase.getEditor().selectAll();
	}
	
	/** GUI builder init */
	  private void jbInit() throws Exception {
	    titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(Color.white,new Color(142, 142, 142)),"Options");
	    ButtonGroup bgSearchDirection = new ButtonGroup();
	    jbtnFindNext.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	          findButtonPressed();			
	      }
	    });
		
		
		ChangeListener cl = new ChangeListener() {
		      public void stateChanged(ChangeEvent ce) {
		        tryMatch();
		      }
		    };
		
		jrbUp.addChangeListener(cl);
		jrbDown.addChangeListener(cl);
	
	    jbtnFindNext.setText("Find Next");
	    jbtnFindNext.setPreferredSize(new Dimension(100, 27));
	    jbtnFindNext.setMinimumSize(new Dimension(100, 27));
	    jbtnFindNext.setMaximumSize(new Dimension(100, 27));
	    jcbStartOnTop.setText("Search from start");
	    jcbStartOnTop.setToolTipText("");
	    jrbDown.setText("Search down");
		
	    jcbWholeWords.setText("Whole words only");	
		jcbWholeWords.addChangeListener(new ChangeListener() {
		    public void stateChanged(ChangeEvent e){
		    matchWord=!matchWord; } });

		jpnlBtn.setLayout(gridBagLayout4);
	    jpnlOptions.setBorder(titledBorder1);
	    jpnlOptions.setLayout(gridLayout2);
	    jpnlFind.setLayout(gridBagLayout5);
	    jtfReplace.setMinimumSize(new Dimension(4, 12));
	    jtfReplace.setPreferredSize(new Dimension(59, 12));
	    jtfReplace.setText("jtfReplace");
	    jpnlMain.setLayout(gridBagLayout6);
	    jrbUp.setText("Search up");
	    jtfPhrase.setMinimumSize(new Dimension(4, 12));
	    jtfPhrase.setPreferredSize(new Dimension(63, 12));
	    jtfPhrase.setText("jtfPhrase");
	    jcbMatchCase.setText("Match case");
		
		jcbMatchCase.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e){
			matchCase=!matchCase; } });
		
	    jLabel3.setText("Replace with:");
	    jLabel4.setText("Text to find:");
	    jbtnClose.setMaximumSize(new Dimension(100, 27));
	    jbtnClose.setMinimumSize(new Dimension(100, 27));
	    jbtnClose.setPreferredSize(new Dimension(100, 27));
	    jbtnClose.setText("Close");
	    jbtnClose.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        cancelButtonPressed();
	      }
	    });
	    gridLayout2.setRows(4);
	    gridLayout2.setColumns(2);
	    this.setModal(true);

// ov uncommented 25/3/05		
	    this.setTitle("Find and Replace");
//	    this.setTitle("Find");
	    jbtnReplace.setMaximumSize(new Dimension(100, 27));
	    jbtnReplace.setMinimumSize(new Dimension(100, 27));
	    jbtnReplace.setPreferredSize(new Dimension(100, 27));
	    jbtnReplace.setText("Replace...");
	    jbtnReplace.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
//	        jbtnReplace_actionPerformed(e);
	          replaceButtonPressed();			

	      }
	    });

	    jbtnCancel.setMaximumSize(new Dimension(100, 27));
	    jbtnCancel.setMinimumSize(new Dimension(100, 27));
	    jbtnCancel.setPreferredSize(new Dimension(100, 27));
	    jbtnCancel.setText("Replace all");
	    jbtnCancel.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        replaceAllButtonPressed();
	      }
	    });
	    jcbUnused.setText("jcbUnused");
	    jcbUnused.setVisible(false);
	    jcbProject.setText("Search whole project");
	    this.getContentPane().add(jpnlMain, BorderLayout.NORTH);
	    jpnlBtn.add(jbtnFindNext,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(4, 4, 0, 4), 0, 0));
	    jpnlBtn.add(jbtnClose,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(0, 4, 4, 4), 0, 0));
// ov uncommented 25/3/05		
	    jpnlBtn.add(jbtnReplace,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlBtn.add(jbtnCancel,  new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.NORTH, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlMain.add(jpnlFind, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
	            ,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlMain.add(jpnlBtn,  new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0
	            ,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlFind.add(jtfPhrase,  new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 12));
	    jpnlFind.add(jLabel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
// ov uncommented 25/3/05		
	    jpnlFind.add(jtfReplace,       new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 4, 4, 4), 0, 12));
	    jpnlFind.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
	            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlMain.add(jpnlOptions,   new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
	            ,GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(4, 4, 4, 4), 0, 0));
	    jpnlOptions.add(jcbWholeWords, null);
	    jpnlOptions.add(jrbUp, null);
	    jpnlOptions.add(jcbMatchCase, null);
	    jpnlOptions.add(jrbDown, null);
	    jpnlOptions.add(jcbStartOnTop, null);
	    jpnlOptions.add(jcbUnused, null);
	    jpnlOptions.add(jcbProject, null);
	    bgSearchDirection.add(jrbUp);
	    bgSearchDirection.add(jrbDown);
  }
  
    /**
     * Set dialog components to their inital state
     */
    private void initDialogContents() {
      jbtnCancel.setEnabled(true);
      jrbUp.setSelected(false);
      jrbDown.setSelected(true);
      jcbWholeWords.setSelected(false);
      jcbMatchCase.setSelected(false);
      jcbStartOnTop.setSelected(true);
      jcbProject.setSelected(false);
      jtfPhrase.setText("");
      jtfReplace.setText("");
    }
	
	
	public XmlEditor getEditor() {
		return editor;
	}
	
	private void tryMatch() {
	
		if (jrbUp.isSelected()) {
			System.out.println(" Up selected ");
			searchDirection = false;
		
		} else if(jrbDown.isSelected()) {
			System.out.println(" Down selected ");
			searchDirection = true;
		} else if(jcbWholeWords.isSelected()) {
			System.out.println(" matchWord selected ");
			matchWord = true;
		} else if(!jcbWholeWords.isSelected()) {
			System.out.println(" matchWord unselected ");
			matchWord = false;
		} else if(jcbMatchCase.isSelected()) {
			System.out.println(" Down selected ");
			matchCase = true;
		} else if(!jcbMatchCase.isSelected()) {
			matchCase = false;
		}

	
	
	}
	

	
} 
