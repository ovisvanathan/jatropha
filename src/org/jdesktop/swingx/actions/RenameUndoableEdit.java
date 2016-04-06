package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class RenameUndoableEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode pastedNode;
    private TreeNode parent;
    private String nodeType;
    int [] where;
    Node refChild;
    int refIndex;
    TreeNode [] childNodes;
    private TreeNode oldNode;
    
    private interface renameConsts {
    	
    	public static final int NODE_DELETE = 0;
    	public static final int TREENODE_ADD = 1;
    	public static final int DOMNODE_ADD = 2;
    	public static final int ATTR_ADD = 3;
    	
    	
    }
    
    
//    public CutUndoableEdit(UndoableModel model, TreeNode parent, TreeNode element, String nodeType) {
    public RenameUndoableEdit(UndoableModel model, TreeNode parent, TreeNode pastedNode, TreeNode oldNode, TreeNode [] childNodes, String nodeType, Node refChild, int refIndex) {
        this.model = model;
        this.pastedNode = pastedNode;
        this.parent = parent;
//        this.where = where;
        this.nodeType = nodeType;
        this.refChild = refChild;
        this.refIndex = refIndex;
        this.childNodes = childNodes;
        this.oldNode = oldNode;
    }
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
        
        		model.insertElementUndoRedo(nodeType, parent, pastedNode, oldNode, childNodes, refChild, refIndex);
      
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
     //   	if(nodeType.equals("Element") || nodeType.equals("Text"))
                
      //  		model.insertElementUndoRedo(nodeType, parent, pastedNode, childNodes, refChild, refIndex);
     
     			model.insertElementUndoRedo("Element", parent, oldNode, pastedNode, childNodes, refChild, refIndex);
     		
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
