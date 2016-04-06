package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class DeleteElementEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode oldElement;
    private TreeNode parent;
    int refIndex;
    Node refNode;
    private TreeNode newElement;
    
    public DeleteElementEdit(UndoableModel model, TreeNode parent, TreeNode oldElement, Node refNode, int refIndex){
        this.model = model;
        this.oldElement = oldElement;
        this.newElement = newElement;
        this.parent = parent;
        this.refIndex = refIndex;
        this.refNode = refNode;
    }
    
    public void undo() throws CannotUndoException {
        super.undo();
			
        		model.insertNodeUndoRedo("Element", parent, oldElement, refNode, refIndex);
		
        	
        }
    
    public TreeNode getNewElement() {
		return newElement;
	}

	public void setNewElement(TreeNode newElement) {
		this.newElement = newElement;
	}

	public void redo() throws CannotRedoException {
        super.redo();
  
        	model.insertNodeUndoRedo("Element", parent, newElement, refNode, refIndex);

//        	model.deleteNode(element, false, true);
        
    }
}
