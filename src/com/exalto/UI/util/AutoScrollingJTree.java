/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.UI.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.Autoscroll;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEditSupport;

import org.jdesktop.swingx.treetable.FileNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.exalto.UI.ExaltoExplorer;

/**
 *
 * @author omprakash.v
 */
    public class AutoScrollingJTree extends JTree implements Autoscroll, Scrollable {

        private int margin = 12;
        private UndoableEditSupport support = new UndoableEditSupport(this);
        private UndoManager manager;

        JPopupMenu m_popup;
        Action m_action;
        Action a1;
        Action a2;

    protected DefaultTreeCellEditor jTree1Editor;
    protected Node m_editingNode = null;
    protected ExplorerNode m_draggingTreeNode;
    protected ExplorerNode m_draggingOverNode;
	// NEW
    protected Cursor m_dragCursor;
    protected Cursor m_nodropCursor;

    public ExplorerNode selectedProject;

    ExaltoExplorer explorer;
    Vector listeners = new Vector();
        
    public AutoScrollingJTree(DefaultMutableTreeNode treeNode, ExaltoExplorer explorer) {
          super(treeNode);
      // Create the undo manager and actions
        manager = new UndoManager();
        addUndoableEditListener(manager);

        Action undoAction = new UndoAction(manager);
        Action redoAction = new RedoAction(manager);

        
        this.explorer = explorer;
        
    // Assign the actions to keys
        registerKeyboardAction(undoAction,
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        registerKeyboardAction(redoAction,
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW);
  
        
        
            	getSelectionModel().setSelectionMode(
                    TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		setEditable(false);

		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
			// NEW
			Color m_draggingBackground = new Color(0, 0, 128);
			Color m_draggingForeground = Color.white;
			Color m_standardBackground = getBackgroundNonSelectionColor();
			Color m_standardForeground = getTextNonSelectionColor();

			public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
				// NEW
				if (value.equals(m_draggingOverNode)) {
					setBackgroundNonSelectionColor(m_draggingBackground);
					setTextNonSelectionColor(m_draggingForeground);
					sel = false;
				}
				else {
					setBackgroundNonSelectionColor(m_standardBackground);
					setTextNonSelectionColor(m_standardForeground);
				}

				Component res = super.getTreeCellRendererComponent(tree,
					value, sel, expanded, leaf, row, hasFocus);
				if (value instanceof ExplorerNode) {
					Node node = ((ExplorerNode)value).getXmlNode();
					if (node instanceof Element) {
                                              
						setIcon(expanded ? openIcon : closedIcon);
                    //ExplorerNode selectedProject;
                                                if(value == selectedProject)
                                                    res.setFont(Font.decode("Ariel-bold-12"));
                                                else
                                                    res.setFont(Font.decode("Ariel-normal-12"));
                                        } else
						setIcon(leafIcon);
				}
				return res;
			}
		};
		setCellRenderer(renderer);

		jTree1Editor = new DefaultTreeCellEditor(this, renderer) {
			public boolean isCellEditable(EventObject event) {
				Node node = getSelectedNode();
				if (node != null && node.getNodeType() == Node.TEXT_NODE)
					return super.isCellEditable(event);
				else
					return false;
			}

			public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
				if (value instanceof ExplorerNode)
					m_editingNode = ((ExplorerNode)value).getXmlNode();
				return super.getTreeCellEditorComponent(tree,
					value, isSelected, expanded, leaf, row);
			}
		};
                
		jTree1Editor.addCellEditorListener(new XmlEditorListener());
		setCellEditor(jTree1Editor);
		setEditable(true);
		setInvokesStopCellEditing(true);

                
                
                		// NEW
		// Load drag-and-drop cursors.
		try {
			ImageIcon icon = new ImageIcon("DragCursor.gif");
			m_dragCursor = Toolkit.getDefaultToolkit().
				createCustomCursor(icon.getImage(),
				new Point(5, 5), "D&D Cursor");
			icon = new ImageIcon("NodropCursor.gif");
			m_nodropCursor = Toolkit.getDefaultToolkit().
				createCustomCursor(icon.getImage(),
				new Point(15, 15), "NoDrop Cursor");
		} catch (Exception ex) {
			System.out.println("Loading cursor: "+ex);
			m_dragCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			m_nodropCursor = m_dragCursor;
		}

                attachPopup();

             //   this.addPropertyChangeListener("SelectedProject", explorer);
    
    }

    
    // The Undo action
  public class UndoAction extends AbstractAction {

      public UndoAction(UndoManager manager) {
//        this.manager = manager;
    }

    public void actionPerformed(ActionEvent evt) {
      try {
        manager.undo();
      } catch (CannotUndoException e) {
        Toolkit.getDefaultToolkit().beep();
      }
    }

  }

  // The Redo action
  public class RedoAction extends AbstractAction {
    public RedoAction(UndoManager manager) {
  //      manager = manager;
    }

    public void actionPerformed(ActionEvent evt) {
      try {
        manager.redo();
      } catch (CannotRedoException e) {
        Toolkit.getDefaultToolkit().beep();
      }
    }
  }
  
     public void addUndoableEditListener(UndoableEditListener l) {
          support.addUndoableEditListener(l);
     }

  public void removeUndoableEditListener(UndoableEditListener l) {
    support.removeUndoableEditListener(l);
  }

  public void collapsePath(TreePath path) {
    boolean wasExpanded = isExpanded(path);

    super.collapsePath(path);

    boolean isExpanded = isExpanded(path);
    if (isExpanded != wasExpanded) {
      support.postEdit(new CollapseEdit(path));
    }
  }

  public void expandPath(TreePath path) {
    boolean wasExpanded = isExpanded(path);

    super.expandPath(path);

    boolean isExpanded = isExpanded(path);
    if (isExpanded != wasExpanded) {
      support.postEdit(new ExpandEdit(path));
    }
  }

  private void undoExpansion(TreePath path) {
    super.collapsePath(path);
  }

  private void undoCollapse(TreePath path) {
    super.expandPath(path);
  }


  
  private class CollapseEdit extends AbstractUndoableEdit {
    public CollapseEdit(TreePath path) {
      this.path = path;
    }

    public void undo() throws CannotUndoException {
      super.undo();
      AutoScrollingJTree.this.undoCollapse(path);
    }

    public void redo() throws CannotRedoException {
      super.redo();
      AutoScrollingJTree.this.undoExpansion(path);
    }

    public String getPresentationName() {
      return "node collapse";
    }

    private TreePath path;
  }

  private class ExpandEdit extends AbstractUndoableEdit {
    public ExpandEdit(TreePath path) {
      this.path = path;
    }

    public void undo() throws CannotUndoException {
      super.undo();
      AutoScrollingJTree.this.undoExpansion(path);
    }

    public void redo() throws CannotRedoException {
      super.redo();
      AutoScrollingJTree.this.undoCollapse(path);
    }

    public String getPresentationName() {
      return "node expansion";
    }
    
    private TreePath path;
  }


  
    public void autoscroll(Point p) {
      int realrow = getRowForLocation(p.x, p.y);
      Rectangle outer = getBounds();
      realrow = (p.y + outer.y <= margin ? realrow < 1 ? 0 : realrow - 1
          : realrow < getRowCount() - 1 ? realrow + 1 : realrow);
      scrollRowToVisible(realrow);
    }

    public Insets getAutoscrollInsets() {
      Rectangle outer = getBounds();
      Rectangle inner = getParent().getBounds();
      return new Insets(inner.y - outer.y + margin, inner.x - outer.x
          + margin, outer.height - inner.height - inner.y + outer.y
          + margin, outer.width - inner.width - inner.x + outer.x
          + margin);
    }

    // Use this method if you want to see the boundaries of the
    // autoscroll active region

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Rectangle outer = getBounds();
      Rectangle inner = getParent().getBounds();
      g.setColor(Color.red);
 //     g.drawRect(-outer.x + 12, -outer.y + 12, inner.width - 24,
  //        inner.height - 24);
    }
  
//TreeDragSource.java
//A drag source wrapper for a JTree. This class can be used to make
//a rearrangeable DnD tree with the TransferableTreeNode class as the
//transfer data type.

class TreeDragSource implements DragSourceListener, DragGestureListener {

  DragSource source;

  DragGestureRecognizer recognizer;

  TransferableTreeNode transferable;

  DefaultMutableTreeNode oldNode;

  JTree sourceTree;

  public TreeDragSource(JTree tree, int actions) {
    sourceTree = tree;
    source = new DragSource();
    recognizer = source.createDefaultDragGestureRecognizer(sourceTree,
        actions, this);
  }

  /*
   * Drag Gesture Handler
   */
  public void dragGestureRecognized(DragGestureEvent dge) {
    TreePath path = sourceTree.getSelectionPath();
    if ((path == null) || (path.getPathCount() <= 1)) {
      // We can't move the root node or an empty selection
      return;
    }
    oldNode = (DefaultMutableTreeNode) path.getLastPathComponent();
    transferable = new TransferableTreeNode(path);
    source.startDrag(dge, DragSource.DefaultMoveNoDrop, transferable, this);

    // If you support dropping the node anywhere, you should probably
    // start with a valid move cursor:
    //source.startDrag(dge, DragSource.DefaultMoveDrop, transferable,
    // this);
  }

  /*
   * Drag Event Handlers
   */
  public void dragEnter(DragSourceDragEvent dsde) {
  }

  public void dragExit(DragSourceEvent dse) {
  }

  public void dragOver(DragSourceDragEvent dsde) {
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {
    System.out.println("Action: " + dsde.getDropAction());
    System.out.println("Target Action: " + dsde.getTargetActions());
    System.out.println("User Action: " + dsde.getUserAction());
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
    /*
     * to support move or copy, we have to check which occurred:
     */
    System.out.println("Drop Action: " + dsde.getDropAction());
    if (dsde.getDropSuccess()
        && (dsde.getDropAction() == DnDConstants.ACTION_MOVE)) {
      ((DefaultTreeModel) sourceTree.getModel())
          .removeNodeFromParent(oldNode);
    }

    /*
     * to support move only... if (dsde.getDropSuccess()) {
     * ((DefaultTreeModel)sourceTree.getModel()).removeNodeFromParent(oldNode); }
     */
  }
}

//TreeDropTarget.java
//A quick DropTarget that's looking for drops from draggable JTrees.
//

class TreeDropTarget implements DropTargetListener {

  DropTarget target;

  JTree targetTree;

  public TreeDropTarget(JTree tree) {
    targetTree = tree;
    target = new DropTarget(targetTree, this);
  }

  /*
   * Drop Event Handlers
   */
  private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
    Point p = dtde.getLocation();
    DropTargetContext dtc = dtde.getDropTargetContext();
    JTree tree = (JTree) dtc.getComponent();
    TreePath path = tree.getClosestPathForLocation(p.x, p.y);
    return (TreeNode) path.getLastPathComponent();
  }

  public void dragEnter(DropTargetDragEvent dtde) {
    TreeNode node = getNodeForEvent(dtde);
    if (node.isLeaf()) {
      dtde.rejectDrag();
    } else {
      // start by supporting move operations
      //dtde.acceptDrag(DnDConstants.ACTION_MOVE);
      dtde.acceptDrag(dtde.getDropAction());
    }
  }

  public void dragOver(DropTargetDragEvent dtde) {
    TreeNode node = getNodeForEvent(dtde);
    if (node.isLeaf()) {
      dtde.rejectDrag();
    } else {
      // start by supporting move operations
      //dtde.acceptDrag(DnDConstants.ACTION_MOVE);
      dtde.acceptDrag(dtde.getDropAction());
    }
  }

  public void dragExit(DropTargetEvent dte) {
  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  public void drop(DropTargetDropEvent dtde) {
    Point pt = dtde.getLocation();
    DropTargetContext dtc = dtde.getDropTargetContext();
    JTree tree = (JTree) dtc.getComponent();
    TreePath parentpath = tree.getClosestPathForLocation(pt.x, pt.y);
    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) parentpath
        .getLastPathComponent();
    if (parent.isLeaf()) {
      dtde.rejectDrop();
      return;
    }

    try {
      Transferable tr = dtde.getTransferable();
      DataFlavor[] flavors = tr.getTransferDataFlavors();
      for (int i = 0; i < flavors.length; i++) {
        if (tr.isDataFlavorSupported(flavors[i])) {
          dtde.acceptDrop(dtde.getDropAction());
          TreePath p = (TreePath) tr.getTransferData(flavors[i]);
          DefaultMutableTreeNode node = (DefaultMutableTreeNode) p
              .getLastPathComponent();
          DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
          model.insertNodeInto(node, parent, 0);
          dtde.dropComplete(true);
          return;
        }
      }
      dtde.rejectDrop();
    } catch (Exception e) {
      e.printStackTrace();
      dtde.rejectDrop();
    }
  }
}

//TransferableTreeNode.java
//A Transferable TreePath to be used with Drag & Drop applications.
//

class TransferableTreeNode implements Transferable {

  public DataFlavor TREE_PATH_FLAVOR = new DataFlavor(TreePath.class,
      "Tree Path");

  DataFlavor flavors[] = { TREE_PATH_FLAVOR };

  TreePath path;

  public TransferableTreeNode(TreePath tp) {
    path = tp;
  }

  public synchronized DataFlavor[] getTransferDataFlavors() {
    return flavors;
  }

  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return (flavor.getRepresentationClass() == TreePath.class);
  }

  public synchronized Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException, IOException {
    if (isDataFlavorSupported(flavor)) {
      return (Object) path;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }
  
}



// drag n drop stuff

    	class XmlEditorListener implements CellEditorListener {
		public void editingStopped(ChangeEvent e) {
			String value = jTree1Editor.getCellEditorValue().toString();
			if (m_editingNode != null)
				m_editingNode.setNodeValue(value);
			TreePath path = getSelectionPath();
			if (path != null) {

                                DefaultTreeModel m_model = (DefaultTreeModel) getModel();
                        
                                DefaultMutableTreeNode treeNode =
					(DefaultMutableTreeNode)path.getLastPathComponent();
				treeNode.setUserObject(m_editingNode);
				m_model.nodeStructureChanged(treeNode);
			}
		//	m_xmlChanged = true;
			m_editingNode = null;
		}

		public void editingCanceled(ChangeEvent e) {
			m_editingNode = null;
		}
	}



      	public ExplorerNode getSelectedTreeNode() {
		TreePath path = getSelectionPath();
		if (path == null)
			return null;
		Object obj = path.getLastPathComponent();
		if (!(obj instanceof ExplorerNode))
			return null;
		return (ExplorerNode) obj;
	}
        
       	public Node getSelectedNode() {
		ExplorerNode treeNode = getSelectedTreeNode();
		if (treeNode == null)
			return null;
		return treeNode.getXmlNode();
	}



  
  // popup stuff

      public void attachPopup() {
        
        m_popup = new JPopupMenu();

        m_action = new AbstractAction("Expand") {
            
            public void actionPerformed(ActionEvent e)
            {
                
            }
            
        };

		
		// PREREL SAMP ACTION NOT USED 090610. CODE MOVED TO EXPLORER.MOUSECLICKED
        /*
		Action a00 = new AbstractAction("SetasMainproject") 
        { 
            public void actionPerformed(ActionEvent e)
            {
				try
				{
					
				
                 ExplorerNode selectedNode = (ExplorerNode) getLastSelectedPathComponent();
                 
				// System.out.println("SMp selectedNode= " + selectedNode);

				 String nn = selectedNode.getXmlNode().getNodeName();
				 
   //              if(!selectedNode.isLeaf() || (selectedNode.isLeaf() && nn.equals("project"))) {
                   
				   if(nn.equals("project")) { 
                     ExplorerNode oldProject = selectedProject;
                //     if(selectedNode.getXmlNode().getNodeName().equals("project"))
                         selectedProject = selectedNode;
                     
				//	   System.out.println("Set as Main projec= = " + selectedProject);
                     explorer.selectedProject = selectedProject;
                  
								TreeNode [] tn = selectedProject.getPath();

								TreePath path = new TreePath(tn);

								explorer.jTree1.setSelectionPath(path);



					// this.firePropertyChange("SelectedProject", oldProject, selectedProject);
                     
				}
				 
				 } catch (Exception ey)
					{
						ey.printStackTrace();
					}
				
				 

            }
 
        };

  //      m_popup.add(a00);
		*/

        Action a0  = new AbstractAction("Cut") 
        { 
            public void actionPerformed(ActionEvent e)
            {
                try {
                
                 DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                 TreeNode parent = selectedNode.getParent();
        
                     int ix = parent.getIndex(selectedNode); 
                     int [] ci = new int [1];
                     ci[0] =  ix ; 
                     
                     Object [] ch = new Object [1];
                     ch[0] = selectedNode;

                         TreePath tp = AutoScrollingJTree.this.getSelectionPath();
                         
                         Object [] tp1 = tp.getPath();
                         
                         
                 if (parent != null) {
                     DefaultTreeModel tm = (DefaultTreeModel) AutoScrollingJTree.this.getModel();
                //     tm.removeNodeFromParent(selectedNode);
                     ExplorerNode expNode = (ExplorerNode) selectedNode;
                     expNode.remove(tm);
                     
                     
                     return;
                 }
                
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
    }
 
 };
     
        m_popup.addSeparator();
        m_popup.add(a0);
     
        Action a1 = new AbstractAction("Copy") 
    { 
      public void actionPerformed(ActionEvent e)
      {
         repaint();
        JOptionPane.showMessageDialog(AutoScrollingJTree.this, 
          "Delete option is not implemented",
          "Info", JOptionPane.INFORMATION_MESSAGE);
      }
    };
    m_popup.add(a1);

    Action a2 = new AbstractAction("Paste") 
    { 
      public void actionPerformed(ActionEvent e)
      {
                                repaint();
        JOptionPane.showMessageDialog(AutoScrollingJTree.this, 
          "Rename option is not implemented",
          "Info", JOptionPane.INFORMATION_MESSAGE);
      }
    };
    m_popup.add(a2);
    
    Action a3 = new AbstractAction("Undo") 
    { 
      public void actionPerformed(ActionEvent e)
      {
                                repaint();
        JOptionPane.showMessageDialog(AutoScrollingJTree.this, 
          "Rename option is not implemented",
          "Info", JOptionPane.INFORMATION_MESSAGE);
      }
    };
    
    m_popup.addSeparator();

    m_popup.add(a3);

    
    Action a4 = new AbstractAction("Redo") 
    { 
      public void actionPerformed(ActionEvent e)
      {
                                repaint();
        JOptionPane.showMessageDialog(AutoScrollingJTree.this, 
          "Rename option is not implemented",
          "Info", JOptionPane.INFORMATION_MESSAGE);
      }
    };
    m_popup.add(a4);

    
    add(m_popup);
    addMouseListener(new PopupTrigger());

    setVisible(true);
  }

    
  public DefaultMutableTreeNode getTreeNode(TreePath path)
  {
    return (DefaultMutableTreeNode)(path.getLastPathComponent());
  }

  public FileNode getFileNode(DefaultMutableTreeNode node)
  {
    if (node == null)
      return null;
    Object obj = node.getUserObject();
      return null;

  }

// NEW
  class PopupTrigger extends MouseAdapter
  {
    public void mouseReleased(MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        int x = e.getX();
        int y = e.getY();
        TreePath path = getPathForLocation(x, y);
        if (path != null)
        {
          if (isExpanded(path))
            m_action.putValue(Action.NAME, "Collapse");
          else
            m_action.putValue(Action.NAME, "Expand");
          m_popup.show(AutoScrollingJTree.this, x, y);
      //    m_clickedPath = path;
        }
      }
    }
  }

  
/*
    public void addPropertyChangeListener(String pname, PropertyChangeListener plist) {
        super.addPropertyChangeListener(pname, plist);
        if(listeners != null)
            listeners.add(plist);
    }
    
    public void removePropertyChangeListener(String pname, PropertyChangeListener plist) {
        super.removePropertyChangeListener(pname, plist);
        if(listeners != null)
            listeners.remove(plist);
    }

    
    public void firePropertyChange(String propName, Object oldVal, Object newVal) {
        super.firePropertyChange(propName, oldVal, newVal);
        if(listeners != null) {
            for(int k=0;k<listeners.size();k++) {
                PropertyChangeListener plist = (PropertyChangeListener) listeners.get(k);
                PropertyChangeEvent evt = new PropertyChangeEvent(this, propName, oldVal, newVal);
                plist.propertyChange(evt);
            }
        }
    }
*/
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return new Dimension(250, 700);
	}

  
 }  
  

