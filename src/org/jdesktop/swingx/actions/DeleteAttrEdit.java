package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class DeleteAttrEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode element;
    private TreeNode parent;

    String nodeType;
    
    public DeleteAttrEdit(UndoableModel model, TreeNode parent, TreeNode element, String nodeType){
        this.model = model;
        this.element = element;
        this.parent = parent;
        this.nodeType = nodeType;
 
    }
    
    public void undo() throws CannotUndoException {
        super.undo();
     
        model.insertNodeUndoRedo(nodeType, parent, element, null, 0);
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        model.deleteNodeFromJTable(parent, element, true, true);     
    }
}
