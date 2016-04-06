package org.jdesktop.swingx.actions;


import javax.swing.tree.TreeNode;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.w3c.dom.Node;

/**
 * @author Omprakash Visvanathan
 */
public class PasteUndoableEdit extends AbstractUndoableEdit {
    private UndoableModel model;
    private TreeNode pastedNode;
    private TreeNode parent;
    private String nodeType;
    int [] where;
    Node refChild;
    int refIndex;
    
//    public CutUndoableEdit(UndoableModel model, TreeNode parent, TreeNode element, String nodeType) {
    public PasteUndoableEdit(UndoableModel model, TreeNode parent, TreeNode pastedNode, String nodeType, Node refChild, int refIndex) {
        this.model = model;
        this.pastedNode = pastedNode;
        this.parent = parent;
//        this.where = where;
        this.nodeType = nodeType;
        this.refChild = refChild;
        this.refIndex = refIndex;

    }
    
    public void undo() throws CannotUndoException {
        super.undo();
        try {
        	if(nodeType.equals("Element"))
        		model.deleteNode(pastedNode, false, true);
        	else if(nodeType.equals("Text"))
        		model.deleteNodeFromJTable(parent, pastedNode, true, true);
            else if(nodeType.equals("Attr"))
        	    model.deleteNodeFromJTable(parent, pastedNode, true, true);
        	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }
    
    public void redo() throws CannotRedoException {
        super.redo();
        try {
     //   	if(nodeType.equals("Element") || nodeType.equals("Text"))
                model.insertNodeUndoRedo(nodeType, parent, pastedNode, refChild, refIndex);
     
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
