package com.exalto.UI.grid;

// import GridHelper;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class AttributeRenderer extends JPanel implements  TableCellRenderer {
	ImageIcon a,b;
	int fWidth = 0;
	int sWidth = 0;
	String text = "";
	JLabel one, two, three;
	boolean selected;
	GridHelper ghelper;
	Rectangle rect = null;
	
	JLabel attrName;
	JLabel attrVal;
	boolean m_selected;
	Color backColor = new Color(0xD0, 0xCC, 0xFF);
	Icon attrIcon = null;
	JTable table;

	/** Creates a new instance of PLIconLabel */
	public AttributeRenderer(Icon attrIcon, JTable table) {
		super(); /* null indicates no layour manager */

		this.table = table;
		setLayout(new GridLayout(2,1));
		setOpaque(true);
		attrName = new JLabel("");
		attrName.setHorizontalAlignment(JLabel.CENTER);
		attrVal = new JLabel("");
		attrVal.setHorizontalAlignment(JLabel.CENTER);
		this.attrIcon = attrIcon;
	}


	public java.awt.Component getTableCellRendererComponent(JTable table,
                                           Object value,
                                           boolean isSelected,
                                           boolean hasFocus,
                                           int row, int column) { 
	

		m_selected = isSelected;
		
		int r = table.getSelectedRow();
		int c = table.getSelectedColumn();

		rect = table.getCellRect(row, column, true);
		
	
		if(isSelected) {
	//		System.out.println(" attr sel domask ");   		
		 	doMask(hasFocus, isSelected);
		} 
		
		if(hasFocus)
		//	setBorder(BorderFactory.createLineBorder(new Color(132, 154,215)));
			setBorder(BorderFactory.createLineBorder(Color.YELLOW));
		else
			setBorder(null);
		
		
		String nval = (String) value;
		String [] rval = nval.split("=");

		int cpos = nval.indexOf("=");
		String aname = null;
		String aval = null;
		if(cpos > -1) {
			aname = nval.substring(0, cpos);
			aval = nval.substring(cpos+1);		
		} else {
			aname = "";
			aval = "";
		}

		attrName.setForeground(Color.BLUE);
		attrVal.setForeground(Color.RED);

	

		attrName.setText(aname);
		attrName.setIcon(attrIcon);
		attrName.setIconTextGap(1);

		attrVal.setText(aval);

		add(attrName);
		add(attrVal);
		

		return this; 
	}
	
	 private void doMask(boolean hasFocus, boolean selected) {
		maskBackground(hasFocus, selected);
}

private void maskBackground(boolean hasFocus, boolean selected) {
	
		Color seed = null;
		seed = table.getSelectionBackground();
		
		Color color = hasFocus ? computeSelectedBackground(seed) : seed;
		/* fix issue#21-swingx: foreground of renderers can be null */
		
//		System.out.println(" maskbg color = " + color);   		

//		System.out.println(" setting color = " + (new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D), true).toString()));   		
		
		if (color != null) {
			attrName.setBackground(
    //                  new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D), true));
	                  new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D)));
			attrVal.setBackground(
   //                 new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D), true));
	                 new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D)));
			setBackground(
      //              new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D), true));
					new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D)));

			
		} 
		        
}

protected Color computeSelectedBackground(Color seed) {
	
		return table.getSelectionBackground() == null ? 
        seed == null ? null : seed.brighter() : table.getSelectionBackground();
        
}

public int getMask() {
return 128;
}


}
