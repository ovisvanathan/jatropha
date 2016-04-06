package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class CutUndoableEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode cutNode;
    private TreeNode parent;
    private String nodeType;
    int [] where;
    Node refChild;
    int refIndex;
    
    
//    public CutUndoableEdit(UndoableModel model, TreeNode parent, TreeNode element, String nodeType) {
    public CutUndoableEdit(UndoableModel model, TreeNode parent, TreeNode cutNode, int [] where, String nodeType, Node refChild, int refIndex) {
        this.model = model;
        this.cutNode = cutNode;
        this.parent = parent;
        this.where = where;
        this.nodeType = nodeType;
        this.refChild = refChild;
        this.refIndex = refIndex;
        
    }
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
        	
        	if(nodeType == null || 
        			(!(nodeType.equals("ELEMENT") || nodeType.equals("ATTR") || nodeType.equals("TEXT"))))
        			throw new CannotUndoException();
       
        	if(nodeType == null)
        		throw new CannotUndoException();
            
        		model.insertNodeUndoRedo(nodeType, parent, cutNode, refChild, refIndex);
        	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
        	if(nodeType == null || 
        			(!(nodeType.equals("ELEMENT") || nodeType.equals("ATTR") || nodeType.equals("TEXT"))))
        			throw new CannotRedoException();

        	if(nodeType.equals("ELEMENT"))
        		model.deleteNode(cutNode, false, true);
        	else {
           		model.deleteNodeFromJTable(parent, cutNode, false, true);
                
        	}
       
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
