/*
 * $Id: AttrTextEditor.java,v 1.11 2006/05/14 08:19:46 dmouse Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx.treetable;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.jdesktop.swingx.JXTreeTable;


/**
 * An editor that can be used to edit the tree column. This extends
 * DefaultCellEditor and uses a JTextField (actually, TreeTableTextField)
 * to perform the actual editing.
 * <p>To support editing of the tree column we can not make the tree
 * editable. The reason this doesn't work is that you can not use
 * the same component for editing and renderering. The table may have
 * the need to paint cells, while a cell is being edited. If the same
 * component were used for the rendering and editing the component would
 * be moved around, and the contents would change. When editing, this
 * is undesirable, the contents of the text field must stay the same,
 * including the caret blinking, and selections persisting. For this
 * reason the editing is done via a TableCellEditor.
 * <p>Another interesting thing to be aware of is how tree positions
 * its render and editor. The render/editor is responsible for drawing the
 * icon indicating the type of node (leaf, branch...). The tree is
 * responsible for drawing any other indicators, perhaps an additional
 * +/- sign, or lines connecting the various nodes. So, the renderer
 * is positioned based on depth. On the other hand, table always makes
 * its editor fill the contents of the cell. To get the allusion
 * that the table cell editor is part of the tree, we don't want the
 * table cell editor to fill the cell bounds. We want it to be placed
 * in the same manner as tree places it editor, and have table message
 * the tree to paint any decorations the tree wants. Then, we would
 * only have to worry about the editing part. The approach taken
 * here is to determine where tree would place the editor, and to override
 * the <code>reshape</code> method in the JTextField component to
 * nudge the textfield to the location tree would place it. Since
 * JXTreeTable will paint the tree behind the editor everything should
 * just work. So, that is what we are doing here. Determining of
 * the icon position will only work if the TreeCellRenderer is
 * an instance of DefaultTreeCellRenderer. If you need custom
 * TreeCellRenderers, that don't descend from DefaultTreeCellRenderer,
 * and you want to support editing in JXTreeTable, you will have
 * to do something similiar.
 *
 * @author Scott Violet
 * @author Ramesh Gupta
 */
public class TreeTableCellEditor extends DefaultCellEditor {

    public TreeTableCellEditor(JTree tree) {
    	
        super(new TreeTableTextField());

  //     System.out.println(" in TTCE tree ");


        if (tree == null) {
            throw new IllegalArgumentException("null tree");
        }
        // JW: no need to...
        this.tree = tree; // immutable
    }



    public TreeTableCellEditor(JTree tree, JXTreeTable treeTable) {
        super(new TreeTableTextField());
        if (tree == null) {
            throw new IllegalArgumentException("null tree");
        }
        // JW: no need to...
        this.tree = tree; // immutable
        this.treeTable = treeTable;
    }

    /**
     * Overriden to determine an offset that tree would place the editor at. The
     * offset is determined from the <code>getRowBounds</code> JTree method,
     * and additionaly from the icon DefaultTreeCellRenderer will use.
     * <p>
     * The offset is then set on the TreeTableTextField component created in the
     * constructor, and returned.
     */
   /*  
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        Component component = super.getTableCellEditorComponent(table, value,
                isSelected, row, column);
        // JW: this implementation is not bidi-compliant, need to do better
        initEditorOffset(table, row, column, isSelected);
        return component;
    }
   */
         public Component getTableCellEditorComponent(JTable table,
                                         Object value,
                                         boolean isSelected,
                                         int r, int c) {
          Component component = super.getTableCellEditorComponent
            (table, value, isSelected, r, c);
        //  JTree t = getTree();
          boolean rv = tree.isRootVisible();
          int offsetRow = rv ? r : r - 1;
          Rectangle bounds = tree.getRowBounds(offsetRow);
          int offset = bounds.x;

		    System.out.println(" in GTCEC offset =  " + offset);


          TreeCellRenderer tcr = tree.getCellRenderer();
          if (tcr instanceof DefaultTreeCellRenderer) {
            Object node = tree.getPathForRow(offsetRow).
                            getLastPathComponent();
            Icon icon = null;
            if (tree.getModel().isLeaf(node)) {
   			    System.out.println(" nodeisleaf   " + offset);
            
       //         icon = ((DefaultTreeCellRenderer)tcr).getLeafIcon();
            } else if (tree.isExpanded(offsetRow)) {
   			    System.out.println(" node is expanded row=  " + offsetRow);
            	
     //           icon = ((DefaultTreeCellRenderer)tcr).getOpenIcon();
            } else {
   			    System.out.println(" in else   " + offsetRow);            
       //         icon = ((DefaultTreeCellRenderer)tcr).getClosedIcon();
            }
                
            if (icon != null) {
            	

                offset +=
((DefaultTreeCellRenderer)tcr).getIconTextGap() +
                        icon.getIconWidth();
                        
   			    System.out.println(" in icon not null offset =  " + offset);

            }
          }


          ((TreeTableTextField)getComponent()).offset = offset;
          
  	      // System.out.println(" in GTCEC comp =  "  + component);

           ((TreeTableTextField)getComponent()).offset = offset;

          return component;
      }
 

    /**
     * @param row
     * @param isSelected
     */
    protected void initEditorOffset(JTable table, int row, int column, boolean isSelected) {
        
             System.out.println(" in initEditorOffset ");

        
        if (tree == null) return;
        Rectangle bounds = tree.getRowBounds(row);
        int offset = bounds.x;
        
              System.out.println(" offset = " + offset);
        
        Object node = tree.getPathForRow(row).getLastPathComponent();
        boolean leaf = tree.getModel().isLeaf(node);
        boolean expanded = tree.isExpanded(row);
        TreeCellRenderer tcr = tree.getCellRenderer();
        Component treeComponent = tcr.getTreeCellRendererComponent(tree, node,
                isSelected, expanded, leaf, row, false);
   
   
        if ((treeComponent instanceof JLabel) 
                // adjust the offset to account for the icon - at least
                // in LToR orientation. RToL is hard to tackle anyway...
                && table.getComponentOrientation().isLeftToRight()) {
                	
   	           System.out.println("inside  isLTOR  ");
	
                	
            JLabel label = (JLabel) treeComponent;

            Icon icon = label.getIcon();
            if(icon != null) {
	            offset += icon.getIconWidth() + label.getIconTextGap();
    	           System.out.println(" icon not null  offset = " + offset);

            } else {
            
                offset += label.getIconTextGap();
   	           System.out.println(" icon is null  offset = " + offset);

            }
        }
          ((TreeTableTextField)getComponent()).offset = offset;
    }

    /**
     * This is overriden to forward the event to the tree. This will
     * return true if the click count >= clickCountToStart, or the event is null.
     */
    public boolean isCellEditable(EventObject e) {
       
       
      // System.out.println(" in TTCE ICE ");
       
       
        if (e == null) {
   	     //  System.out.println(" in ICE e null ");

            return true;
        }
        else if (e instanceof MouseEvent) {

   //	       System.out.println(" in ICE MouseEvent ");

        	MouseEvent me = (MouseEvent) e;
        	
   	     //  System.out.println(" in ICE me.getClickCount() = "+ me.getClickCount());
        	
        	
            if (me.getClickCount() >= 2) {
                return true;
            }

   	     //  System.out.println(" in ICE clickCountToStart = "+ clickCountToStart);

            return (((MouseEvent) e).getClickCount() >= clickCountToStart);
        }

	// e is some other type of event...
        return false;
     

/*     
     if (e instanceof MouseEvent) {
     	
   	       System.out.println(" in ICE mouseevt ");

     	
            MouseEvent me = (MouseEvent)e;
            // If the modifiers are not 0 (or the left mouse button),
                // tree may try and toggle the selection, and table
                // will then try and toggle, resulting in the
                // selection remaining the same. To avoid this, we
                // only dispatch when the modifiers are 0 (or the left
mouse
                // button).
                        
                        
            if (me.getModifiers() == 0 ||
                    me.getModifiers() == InputEvent.BUTTON1_MASK) {
                    	
                     	       System.out.println(" in ICE b1mask ");
  	
                    	

                for (int counter = treeTable.getColumnCount() - 1; counter >= 0;
                   counter--) {
                if (treeTable.isHierarchical(counter))
              	{
    				
             	       System.out.println(" in ICE tree column dispatching  ");

                      MouseEvent newME = new MouseEvent(
                            tree, me.getID(),
                           me.getWhen(), me.getModifiers(),
                           me.getX() - treeTable.getCellRect(0, counter, true).x,
                           me.getY(), me.getClickCount(),
                                   me.isPopupTrigger());
                      tree.dispatchEvent(newME);
                      break;
                  }
                }
            }
            if (me.getClickCount() >= 2) {
            	
     	       System.out.println(" in ICE cc >= 3  ");
	
                return true;
            }
            
              	       System.out.println(" in ICE ret false  ");
   
            return false;
          }
          if (e == null) {
            return true;
          }
          return false;
          
          */
          
      }   
        
    
    
    /**
     * Component used by AttrTextEditor. The only thing this does
     * is to override the <code>reshape</code> method, and to ALWAYS
     * make the x location be <code>offset</code>.
     */
      static class TreeTableTextField extends JTextField {
      public int offset;

      public void reshape(int x, int y, int w, int h) {
          int newX = Math.max(x, offset);
          
		  System.out.println(" reshape newX =  " + newX);
		  System.out.println(" reshape y =  " + y);
		  System.out.println(" reshape w =  " + w);
		  System.out.println(" reshape x =  " + x);
		  System.out.println(" reshape h =  " + h);

          
          super.reshape(newX, y, w - (newX - x), h);
      }
    }

    /**
     * Component used by AttrTextEditor. The only thing this does
     * is to override the <code>reshape</code> method, and to ALWAYS
     * make the x location be <code>offset</code>.
     */
    /* 
    static class TreeTableTextField extends JTextField {
    	
        void init(int offset, int column, int width, JTable table) {
            this.offset = offset;
            this.column = column;
            this.width = width;
            this.table = table;
            setComponentOrientation(table.getComponentOrientation());
        }
        
        private int offset; // changed to package private instead of public
        private int column;
        private int width;
        private JTable table;
        public void reshape(int x, int y, int width, int height) {
            // Allows precise positioning of text field in the tree cell.
            //Border border = this.getBorder(); // get this text field's border
            //Insets insets = border == null ? null : border.getBorderInsets(this);
            //int newOffset = offset - (insets == null ? 0 : insets.left);
            
            System.out.println(" in reshape table = " + table);

           if(table.getComponentOrientation().isLeftToRight()) {
            
                int newOffset = offset - getInsets().left;
                // this is LtR version
                super.reshape(x + newOffset, y, width - newOffset, height);
            } else {
                // right to left version
                int newOffset = offset + getInsets().left;
                int pos = getColumnPositionBidi();
                width = table.getColumnModel().getColumn(getBidiTreeColumn()).getWidth();
                
                width = width - (width - newOffset - this.width);
                super.reshape(pos, y, width, height);
            }
        }
        
        //
         // Returns the column for the tree in a bidi situation
         //
        private int getBidiTreeColumn() {
            // invert the column offet since this method will always be invoked
            // in a bidi situation
            return table.getColumnCount() - this.column - 1;
           
        }
        
        private int getColumnPositionBidi() {
            int width = 0;
            
            int column = getBidiTreeColumn();
            for(int iter = 0 ; iter < column ; iter++) {
          //OV  	width += table.getColumnModel().getColumn(iter).getWidth();
                width += treeTable.getColumnModel().getColumn(iter).getWidth();
            }
            return width;
        }
    }
    
    
    */

    private final JTree tree; // immutable
    private JXTreeTable treeTable;
    
}