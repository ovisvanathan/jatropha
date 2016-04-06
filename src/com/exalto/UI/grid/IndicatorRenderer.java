package com.exalto.UI.grid;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import javax.swing.table.*;
import javax.swing.plaf.basic.*;

import java.awt.event.*;
import java.util.EventObject;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Arrays;
    
    /**
     * A renderer that will give an indicator when a cell is being reloaded.
     */
 public class IndicatorRenderer extends DefaultTableCellRenderer {
 /** Makes sure the number of displayed in an internationalized
  * manner. */
 protected NumberFormat       formatter;
 /** Row that is currently being painted. */
 protected int                lastRow;
   protected int                reloadRow;
   protected int                reloadCounter;
  Icon elemIcon, attrIcon, textIcon;
  protected XmlTreeModel treeTableModel;
  protected SimpleTreeModelAdapter treeTblAdapter;
  protected JTable table;
  Component renderer = null;

  Color backColor = new Color(0xF0, 0xF0, 0xE0);
  
 public IndicatorRenderer(SimpleTreeModelAdapter treeTblAdapter, XmlTreeModel treeTableModel, Icon elem, Icon attr, Icon text) {
 	setHorizontalAlignment(JLabel.RIGHT);
 	setFont(new Font("serif", Font.BOLD, 12));
	 elemIcon = elem;
	 attrIcon = attr;
     textIcon = text;
	 this.treeTableModel = treeTableModel;
	 this.treeTblAdapter = treeTblAdapter;

 }
 /**
  * Invoked as part of DefaultTableCellRenderers implemention. Sets
  * the text of the label.
  */
 public void setValue(Object value) { 
/*     setText((value == null) ? "---" : formatter.format(value));  */
 	setText((value == null) ? "---" : (String) value); 
 }
 
 /**
  * Returns this.
  */
 public Component getTableCellRendererComponent(JTable table,
       Object value, boolean isSelected, boolean hasFocus,
       int row, int column) {

	 renderer = super.getTableCellRendererComponent(table, value, isSelected,
      hasFocus, row, column);

	 lastRow = row;

     	this.table = table;
        String nodeLabel = "";

  //      System.out.println(" value is String ");
           nodeLabel = (String) value;
           
           if(nodeLabel != null && nodeLabel.trim().length() >0)
              setIcon(textIcon);
           else
              setIcon(null);
        
		if(isSelected) {
			doMask(hasFocus, isSelected);
		} 
		
		/*	
		else {
		
			System.out.println(" not sel row " + row);
			System.out.println(" not sel col " + column);
			System.out.println(" not sel bg " + table.getBackground());
			
			doMask(hasFocus, isSelected);
		}		
	*/	
     
     return renderer;
 }
 /**
  * If the row being painted is also being reloaded this will draw
  * a little indicator.
  */
 public void paint(Graphics g) {
 	super.paint(g); 
 }
 
 private void doMask(boolean hasFocus, boolean selected) {
		maskBackground(hasFocus, selected);
}

private void maskBackground(boolean hasFocus, boolean selected) {
	
		Color seed = null;
		seed = table.getSelectionBackground();
		
		Color color = hasFocus ? computeSelectedBackground(seed) : seed;
		/* fix issue#21-swingx: foreground of renderers can be null */
		
		if (color != null) {
			
	//		System.out.println(" indic setting color " + (new Color((getMask() << 24) | (color.getRGB() & 0x00FFD06D), true).toString()));
			
			setBackground(
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
 