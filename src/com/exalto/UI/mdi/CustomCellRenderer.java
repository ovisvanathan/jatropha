package com.exalto.UI.mdi;

// Imports
import java.awt.*;
import javax.swing.*;
import javax.swing.ListCellRenderer.*;

public class CustomCellRenderer
	extends		JLabel
	implements	ListCellRenderer	
{
	private	ImageIcon	image[];
	
	public CustomCellRenderer()
	{
		setOpaque(true);

		// Pre-load the graphics images to save time
		image = new ImageIcon[1];
		image[0] = new ImageIcon( "element.gif" );
	}
     
	public Component getListCellRendererComponent(
			JList list, Object value, int index, 
			boolean isSelected, boolean cellHasFocus ) 
	{
		// Display the text for this item
		setText(value.toString());
		
		// Set the correct image
		setIcon( image[0] );
		
		// Draw the correct colors and font
		if( isSelected )
		{
			// Set the color and font for a selected item
			setBackground( Color.red );
			setForeground( Color.white );
			setFont( new Font( "Roman", Font.BOLD, 10 ) );
			setBorder(BorderFactory.createLineBorder(Color.blue, 2));
		}
		else
		{
			// Set the color and font for an unselected item
			setBackground( Color.yellow );
			setForeground( Color.blue );
			setFont( new Font( "Roman", Font.PLAIN, 10 ) );
			setBorder(BorderFactory.createLineBorder(list.getBackground(), 2));
		}
		
		
		return this;
	}
}

