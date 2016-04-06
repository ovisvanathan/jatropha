/*
 * InsertTextEdit.java
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

import com.exalto.UI.grid.ExaltoXmlText;

/**
 *
 * @author omprakash.v
 */
public class InsertTextEdit  extends AbstractUndoableEdit {
      private UndoableModel model;
    private TreeNode parent;
    private TreeNode textNode;

    /** Creates a new instance of InsertTextEdit */
    public InsertTextEdit(UndoableModel model, TreeNode parent, TreeNode textNode) {
     this.model = model;
        this.parent = parent;
        this.textNode = textNode;

    }

    public InsertTextEdit(UndoableModel model, TreeNode parent, Node textNode) {
        this.model = model;
           this.parent = parent;
           this.textNode = new ExaltoXmlText(textNode);

       }

    public void undo() throws CannotUndoException {
        super.undo();
        //TODO: verify 25/03/08 OV
        model.deleteNodeFromJTable(parent, textNode, true, true);
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
        	model.insertNodeUndoRedo("Text", parent, textNode, null, 0);

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
