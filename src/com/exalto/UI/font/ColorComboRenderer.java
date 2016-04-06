package com.exalto.UI.font;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;


public class ColorComboRenderer extends JPanel implements ListCellRenderer
{
	  protected Color m_color = Color.black;
	  protected Color m_focusColor = 
	    (Color) UIManager.get("List.selectionBackground");
	  protected Color m_nonFocusColor = Color.white;

	  public Component getListCellRendererComponent(JList list,
	   Object obj, int row, boolean sel, boolean hasFocus)
	  {
	    if (hasFocus || sel)
	      setBorder(new CompoundBorder(
	        new MatteBorder(2, 10, 2, 10, m_focusColor),
	        new LineBorder(Color.black)));
	    else
	      setBorder(new CompoundBorder(
	        new MatteBorder(2, 10, 2, 10, m_nonFocusColor),
	        new LineBorder(Color.black)));

	    if (obj instanceof Color) 
	      m_color = (Color) obj;
	    return this;
	  }
	    
	  public void paintComponent(Graphics g) {
	    setBackground(m_color);
	    super.paintComponent(g);
	  }
	
}
