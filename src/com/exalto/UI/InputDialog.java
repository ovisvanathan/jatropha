package com.exalto.UI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JPasswordField;

import com.exalto.util.XmlUtils;    

/**
 * <p>A dialog for setup of a new page/screen</p>
 * <p>Copyright (c) Xoetrope Ltd., 1998-2003</p>
 * $Revision: 1.2 $
 */
public class InputDialog extends JDialog implements ActionListener
{
  protected JTextField inputField;
  protected JButton btnOK, btnCancel;
  protected String inputString;

  public InputDialog( String title, String prompt )
  {
    setTitle( title );
    getContentPane().setLayout( null );
    setModal( true );

    setFont( XmlUtils.defaultFont );

    JLabel lbl = new JLabel( prompt );
    lbl.setBounds( 10, 10, 200, 20 );
    lbl.setFont( XmlUtils.defaultFont );
    getContentPane().add( lbl );

    inputField = new JTextField();
    inputField.setBounds( 10, 30, 210, 20 );
    inputField.setFont( XmlUtils.defaultFont );
    getContentPane().add( inputField );

    btnCancel = new JButton( "Cancel" );
    btnCancel.setBounds( 10, 60, 100, 20 );
    btnCancel.setFont( XmlUtils.defaultFont );
    btnCancel.addActionListener( this );
    getContentPane().add( btnCancel );

    btnOK = new JButton( "OK" );
    btnOK.setBounds( 120, 60, 100, 20 );
    btnOK.setFont( XmlUtils.defaultFont );
    btnOK.addActionListener( this );
    getContentPane().add( btnOK );

    setLocation( 100, 100 );
    setSize( 240, 125 );
  }

  /**
   * Gets the user input value
   * @return the text of the input field
   */
  public String getInputValue()
  {
  	System.out.println(" in GIV");
    show();
    return inputString;
  }

  public void actionPerformed( ActionEvent evt )
  {
  	try {
  
		if ( evt.getSource().equals( btnOK ) ) {
		
			System.out.println(" in butt ok ");
  		    inputString = inputField.getText();
		} else {
		  inputString = null;
		}
		
	   } catch(Exception e) {
	   	  e.printStackTrace();
	   }
	  
    hide();
  }

  /**
   * Changes the input field to a password field
   * @param b true to make the input field a password field
   */
  public void setPassword( boolean b )
  {
    if ( b )
      inputField = new JPasswordField( inputField.getText());
    else
      inputField = new JTextField( inputField.getText());
  }
  

  
}