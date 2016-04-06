package com.exalto.UI.misc;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.*;
import java.io.*;

import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;

public class JTip extends JDialog implements ActionListener
{
  private ArrayList tipList;
  private int currentTip = 0;
  private File xmlFile;
  private Element root;
  private JButton nextTipButton = new JButton( "Next Tip" );
  private JButton closeButton = new JButton( "Close" );
  private JTextArea textBody = new JTextArea();
  private JCheckBox showOnStart = new JCheckBox(
  "Show Tips on StartUp", true );
  
  public JTip( String tipFile )
  {
    /**
     * Read the tips
     */
    try
    {
      // Open and parse the XML document
      SAXBuilder builder = new SAXBuilder();
      xmlFile = new File( tipFile );
      Document doc = builder.build( xmlFile );
      this.root = doc.getRootElement();

      // Load tips into our ArrayList
      java.util.List tips = root.getChildren( "tip" );
      tipList = new ArrayList( tips.size() + 1 );
      for( Iterator itr = tips.iterator(); itr.hasNext(); )
      {
        Element tipElement = ( Element )itr.next();
        int number = Integer.parseInt( 
 tipElement.getAttributeValue( "number" ) );
        this.tipList.add( tipElement );
      }

      // Record the current tip number
      this.currentTip = Integer.parseInt(
  root.getAttributeValue( "current-tip" ) );
      String showOnStartState = root.getAttributeValue( "show-on-start" );
      if( showOnStartState.equalsIgnoreCase( "false" ) )
      {
        this.showOnStart.setSelected( false );
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    /**
     * Build the screen
     */
    this.setTitle( "Tip of the Day" );

    // Build bottom panel
    JPanel bottomPanel = new JPanel( new GridLayout( 1, 2 ) );
    bottomPanel.add( getLeftFlow( showOnStart ) );
    JPanel buttonPanel = getRightFlow( this.nextTipButton );
    buttonPanel.add( this.closeButton );
    bottomPanel.add( buttonPanel );
    this.getContentPane().add( bottomPanel, BorderLayout.SOUTH );
    this.closeButton.addActionListener( this );
    this.nextTipButton.addActionListener( this );

    // Build the center panel
    JPanel centerPanel = new JPanel( new BorderLayout() );
    centerPanel.setBorder( BorderFactory.createCompoundBorder( 
      BorderFactory.createEmptyBorder( 10, 10, 10, 10 ),
      BorderFactory.createLineBorder( Color.black ) ) );
    JPanel centerCenterPanel = new JPanel( new BorderLayout() );
    centerCenterPanel.setBorder( BorderFactory.createEmptyBorder(
  10, 10, 10, 10 ) );
    this.textBody.setEditable( false );
    this.textBody.setLineWrap( true );
    this.textBody.setWrapStyleWord( true );
    this.textBody.setDisabledTextColor( Color.black );
    centerCenterPanel.add( getBufferedPanel( this.textBody, 20 ) );
    centerPanel.add( centerCenterPanel );
    this.getContentPane().add( centerPanel, BorderLayout.CENTER );

    // Top Center
    ImageIcon bulbIcon = new ImageIcon( "bulb.jpg" );
    JPanel topPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
    topPanel.setBackground( Color.white );
    JLabel bulbLabel = new JLabel( "Did you know...", bulbIcon,
  JLabel.LEFT );
    bulbLabel.setFont( this.textBody.getFont() );
    bulbLabel.setForeground( Color.black );
    bulbLabel.setBackground( Color.white );
    bulbLabel.setOpaque( true );
    topPanel.add( bulbLabel );
    centerCenterPanel.add( topPanel, BorderLayout.NORTH );

    // Set the size and center..
    this.setSize( 350, 200 );
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation( d.width/2 - 200, d.height/2 - 200 );

    this.addWindowListener( new WindowAdapter() {
        public void windowClosing(WindowEvent e)
        {
          handleClose();
        }
      } );
  }

  public void actionPerformed( ActionEvent ae )
  {
    if( ae.getSource() == this.closeButton )
    {
      handleClose();
    }
    else if( ae.getSource() == this.nextTipButton )
    {
      showNextTip();
    }
  }

  protected JPanel getBufferedPanel( JComponent c, int size )
  {
    JPanel panel = new JPanel( new BorderLayout() );
    panel.setBorder( BorderFactory.createMatteBorder( size, size,
  size, size, Color.white ) );
    panel.add( c );
    return panel;
  }

  protected void handleClose()
  {
    // Hide the window
    this.setVisible( false );

    // Update the XML file to match our parameters
    if( this.showOnStart.isSelected() )
    {
      root.setAttribute( "show-on-start", "true" );
    }
    else
    {
      root.setAttribute( "show-on-start", "false" );
    }
    root.setAttribute( "current-tip", Integer.toString( 
 this.currentTip ) );

    try
    {
      // Save the XML file
 //     XMLOutputter out = new XMLOutputter( "\t", true );
      XMLOutputter out = new XMLOutputter( );
      out.output( root, new FileOutputStream( this.xmlFile ) );
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
  }

  protected void showNextTip()
  {
    // Figure out what the next tip is (allow for wrap around)
    if( ( this.currentTip + 1 ) >= this.tipList.size() )
    {
      this.currentTip = 0;
    }
    else
    {
      this.currentTip++;
    }

    // Get the tip and send it to the text area
    Element tipElement = ( Element )this.tipList.get(
  this.currentTip );
    this.textBody.setText( tipElement.getTextTrim() );
  }

  protected JPanel getLeftFlow( JComponent c )
  {
    JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
    panel.add( c );
    return panel;
  }

  protected JPanel getRightFlow( JComponent c )
  {
    JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    panel.add( c );
    return panel;
  }

  public void showTips()
  {
    this.showNextTip();
    this.setVisible( true );
  }

  public boolean isShowOnStart()
  {
    return this.showOnStart.isSelected();
  }

//  public static void main( String[] args )
//  {
//    JTip tip = new JTip( "tips.xml" );
//	if(tip.isShowOnStart())	
//    	tip.showTips();
//  }
}
