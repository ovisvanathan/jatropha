/*
 * InsertAttrEdit.java
 *
 * Created on October 27, 2006, 2:09 PM
 *
 * Omprakash Visvanathan
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.actions;

import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 *
 * @author omprakash.v
 */
public class InsertAttrEdit extends AbstractUndoableEdit  {
    private UndoableModel model;
    private TreeNode parent;
    private TreeNode attrNode;
      
    
    /** Creates a new instance of InsertAttrEdit */
    public InsertAttrEdit(UndoableModel model, TreeNode parent, TreeNode attrNode){
        this.model = model;
        this.parent = parent;
        this.attrNode = attrNode;

    }
    
    public void undo() throws CannotUndoException {
        super.undo();
        model.deleteNodeFromJTable(parent, attrNode, true, true);
    }
    
    public void redo() throws CannotRedoException {
        super.redo();

        	model.insertNodeUndoRedo("Attr", parent, attrNode, null, 0);
        
    }

    
}
