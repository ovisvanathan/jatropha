package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class InsertElementEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode element;
    private TreeNode parent;
    Node refNode;
    int refIndex;
    
    public InsertElementEdit(UndoableModel model, TreeNode parent, TreeNode element, Node refNode, int refIndex){
        this.model = model;
        this.element = element;
        this.parent = parent;
        this.refNode = refNode;
        this.refIndex = refIndex;

    }
    
    public void undo() throws CannotUndoException {
        super.undo();

        model.deleteNode(element, false, true);
    }
    
    public void redo() throws CannotRedoException {
        super.redo();

        model.insertNodeUndoRedo("Element", parent, element, refNode, refIndex);

    }
}
