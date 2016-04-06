package com.exalto.UI.grid;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import com.exalto.UI.grid.JXmlTreeTable;

import org.jdesktop.swingx.decorator.Highlighter;
 
 
 
public class TreeIconRenderer extends DefaultTreeCellRenderer implements ChangeListener
{
//  Color backColor = new Color(0xD0, 0xCC, 0xFF);
  Color backColor = new Color(0xF0, 0xF0, 0xE0);
    
  Icon openIcon, closedIcon, leafIcon, expIcon, collIcon;

  String tipText = "";
  JTree tree;
  int currRow = 0;
  int currentRow = 0;
  
  boolean m_selected;
  JXmlTreeTable table;
  boolean treeNeedsHighlighting;
  
//OV  public TreeIconRenderer(JTree tree, Icon open, Icon closed, Icon leaf, Icon exp, Icon coll, JTable table) {
  public TreeIconRenderer(JTree tree, Icon open, Icon closed, Icon leaf, Icon exp, Icon coll, JXmlTreeTable table) {
    openIcon = open;
    closedIcon = closed;
    leafIcon = leaf;
    this.table = table;
    
   // setBackground(backColor);
  //  setBackground(Color.GREEN);
 //   setForeground(Color.black);
	
    expIcon = exp;
	collIcon = coll;
	this.tree = tree;   
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                boolean selected,
                                                boolean expanded, boolean leaf,
                                                int row, boolean hasFocus) {
     
	 Object node = null;
                                                 
	 super.getTreeCellRendererComponent(
                        tree, value, selected,
                        expanded, leaf, row,
                        hasFocus);
    
	  TreePath treePath = tree.getPathForRow(row);
      if(treePath != null)
		 node = treePath.getLastPathComponent();

      currRow = row;
      m_selected = selected;
	  JLabel label = new JLabel();


 //  	 System.out.println(" TIR row = " + row);   		
 //  	 System.out.println(" TIR row selected= " + m_selected);
 //  	 System.out.println(" TIR treeNeedsHighlighting = " + treeNeedsHighlighting);
   	 
   	       
   	       
   	       
      if(m_selected){
      	 Color bColor = JXmlTreeTable.selBackground;
   //		 System.out.println("in else selbg =  " + bColor);   		
//   		bColor = new Color((getMask() << 24) | (bColor.getRGB() & 0x00FFD06D), true);
   		 bColor = new Color((getMask() << 24) | (bColor.getRGB() & 0x00FFD06D));

   	//	 System.out.println("in aft selbg = " + bColor);   		
        
               label.setOpaque(true);
   //     label.setBackground(bColor);
      //   label.setBackground(new Color(0xCC, 0xC0, 0x6D));
         label.setBackground(new Color(0x6D, 0xB3, 0xCB));
                
      } 
      else if(treeNeedsHighlighting && row == currentRow) {
    	  System.out.println(" TIR rowe == currentRow = ");   		
		  Color color = Color.YELLOW.brighter();
          label.setBackground(color);
      }
      else {
    	if(currRow % 2 == 0)
    		label.setBackground(JXmlTreeTable.background);
    	else
    		label.setBackground(backColor);
    		
        
    }
      
      label.setText(value.toString());

    
   ExaltoXmlNode itc = null;
   if (node instanceof ExaltoXmlNode) {
      itc = (ExaltoXmlNode)node;
    }
    
				setClosedIcon(closedIcon);
		       setOpenIcon(openIcon);
	
		         setLeafIcon(leafIcon);
		
		
//    return this;
		         return label;
		
         
  }



  /* Override the default to send back different strings for folders and leaves. */
  public String getToolTipText() {
    return tipText;
  }

   /**
     * sets the icon for the handle of an expanded node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     * 
     * @param expanded
     */
    public void setExpandedIcon(Icon expanded) {
        if (tree.getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) tree.getUI()).setExpandedIcon(expanded);
        }
    }
    
    /**
     * sets the icon for the handel of a collapsed node.
     * 
     * Note: this will only succeed if the current ui delegate is
     * a BasicTreeUI otherwise it will do nothing.
     *  
     * @param collapsed
     */
    public void setCollapsedIcon(Icon collapsed) {
        if (tree.getUI() instanceof BasicTreeUI) {
            ((BasicTreeUI) tree.getUI()).setCollapsedIcon(collapsed);
        }
    }

    
    /**
     * Paints the value.  The background is filled based on selected.
     */
    
   public void paint(Graphics g) {

   	super.paint(g);
  
   	/*
   	Color bColor = null; 
   	
   	if(!m_selected) {
   	  
   		System.out.println(" iconren  not sel  currRow " + currRow);
      	
	   	if(currRow % 2 == 0) {
	   		System.out.println(" row white  ");
	      	
	   		bColor = Color.WHITE;
	   	} else {
	   		System.out.println(" row bkcolor  ");
	        bColor = backColor;
	   	}
	   	
   	} else {
   		bColor = table.getSelectionBackground();
   		System.out.println("in else selbg =  " + bColor);   		
   		bColor = new Color((getMask() << 24) | (bColor.getRGB() & 0x00FFD06D), true);
   		System.out.println(" bColor aft = " + bColor);   		
   	}
	   	
	int imageOffset = -1;
	if(bColor != null) {
	 //   Icon currentI = getIcon();

	    imageOffset = getLabelStart();
	    
	    if(!m_selected) {
	    	System.out.println(" not sel setting white ");
		    g.setColor(bColor);

	     	g.setXORMode(new Color(0xFF, 0xFF, 0xFF));
	    } else {
		    g.setColor(new Color(0x00, 0x0C, 0x92));
//	     	g.setXORMode(new Color(0xCC, 0xCC, 0x9F));
	     	g.setXORMode(new Color(0x00, 0x00, 0x00));
		}
	    
		System.out.println(" using color = " + g.getColor());   		
	    
	    
	    if(getComponentOrientation().isLeftToRight()) {
	    	
	//        System.out.println(" FR LTOR ");
	        g.fillRect(imageOffset, 0, getWidth() - 1 - imageOffset,
			   getHeight());
	    } else {
	        g.fillRect(0, 0, getWidth() - 1 - imageOffset,
			   getHeight());
	    }
	}
*/
   
   }
   
   
   private int getMask() {
   	   return 128;
   }
    
   private int getLabelStart() {
	Icon currentI = getIcon();
	if(currentI != null && getText() != null) {
	    return currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
	}
	return 0;
    }
 
   protected Color computeBackgroundSeed(Color seed) {
    return new Color(Math.max((int)(seed.getRed()  * 0.95), 0),
                     Math.max((int)(seed.getGreen()* 0.95), 0),
                     Math.max((int)(seed.getBlue() * 0.95), 0));
}



	public void stateChanged(ChangeEvent ce) {


	//OM	Highlighter hliter = (Highlighter) ce.getSource();	
	//OM	currentRow = hliter.getCurrentRow();
		currentRow = 0;
		System.out.println(" TIR stateChanged currentRow = " + currentRow);   		
  		treeNeedsHighlighting = true;
  		
		TreeModel tmodel = tree.getModel();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

		tree.setModel(new DefaultTreeModel(root));
		
		tree.setModel(tmodel);
		
		expandTree();		

	}



    protected ExaltoXmlNode expandTree() {

     ExaltoXmlNode treeNode = getSelectedTreeNode();

     TreeNode root = (TreeNode)tree.getModel().getRoot();
     TreePath path = new TreePath(root);
     for (int k = 0; k<root.getChildCount(); k++) {
   TreeNode child = (TreeNode)root.getChildAt(k);
   expandTree(path, child);
  }

  return null;
 }
  
 protected void expandTree(TreePath path, TreeNode node) {
  		if (path==null || node==null)
   			return;
	  	tree.expandPath(path);
	  	TreePath newPath = path.pathByAddingChild(node);
	       for (int k = 0; k<node.getChildCount(); k++) {
		       TreeNode child = (TreeNode)node.getChildAt(k);
		       if (child != null) {
	    		   expandTree(newPath, child);
	   	       }
	  	   }
    }


 public ExaltoXmlNode getSelectedTreeNode() {

  TreePath path = tree.getSelectionPath();

  if (path == null)
   return null;

  Object obj = path.getLastPathComponent();
  if (!(obj instanceof ExaltoXmlNode))
   return null;

  return (ExaltoXmlNode)obj;
 }

    
 }