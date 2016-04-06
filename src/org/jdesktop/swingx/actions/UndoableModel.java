package org.jdesktop.swingx.actions;


import com.exalto.UI.grid.ExaltoXmlNode;
import java.util.Vector;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import org.w3c.dom.Node;


/**
 * @author Omprakash Visvanathan
 */
public interface UndoableModel{
    public void addUndoableEditListener(UndoableEditListener listener);
    public void removeUndoableEditListener(UndoableEditListener listener);
    public TreeNode deleteNode(TreeNode treeNode, boolean delete, boolean isUndoRedo);
    public void deleteNodeFromJTable(TreeNode parent, TreeNode treeNode, boolean delete, boolean isUndoRedo);
    public void insertNodeUndoRedo(String type, TreeNode parent, TreeNode child, Node refchild, int index);
    public Vector insertNodeInParent(TreeNode parent, TreeNode nodeElement, TreeNode oldNode, TreeNode [] childNodes, String nodeType, String newValue, Node refChild, int index); 
	public void insertElementUndoRedo(String nodeType, TreeNode parentNode, TreeNode child, TreeNode oldNode, TreeNode [] childNodes, Node refChild, int refIndex);
}