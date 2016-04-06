package com.exalto.UI.grid;

/*
 * Copyright 1997-1999 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import com.exalto.ColWidthTypes;
import com.exalto.UI.XmlEditor;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.actions.CutUndoableEdit;
import org.jdesktop.swingx.actions.DeleteAttrEdit;
import org.jdesktop.swingx.actions.DeleteElementEdit;
import org.jdesktop.swingx.actions.InsertAttrEdit;
import org.jdesktop.swingx.actions.InsertElementEdit;
import org.jdesktop.swingx.actions.InsertTextEdit;
import org.jdesktop.swingx.actions.PasteUndoableEdit;
import org.jdesktop.swingx.actions.RenameUndoableEdit;
import org.jdesktop.swingx.actions.UndoableModel;

import com.exalto.UI.mdi.DesktopView;
import com.exalto.util.ExaltoConstants;
import com.exalto.util.ExaltoResource;
import com.exalto.util.StatusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model
 * interface. The implementation is trivial, with all of the event dispatching
 * support provided by the superclass: the AbstractTableModel.
 * 
 * @version 1.2 10/27/98
 * 
 * @author Philip Milne
 * @author Scott Violet
 */
public class SimpleTreeModelAdapter extends JXTreeTable.TreeTableModelAdapter
		implements MouseListener, ActionListener, ActionsHandler, UndoableModel, ClipboardOwner {
	JTree atree;
	XmlTreeModel treeTableModel;
	int maxCol = 0;
	int currCol = 0;
	int currRow = 0;

	boolean treeExpanded = false;
	boolean treeCollapsed = false;

	protected boolean m_xmlChanged = false;

    public void setXmlChanged(boolean m_xmlChanged) {
        boolean oldval = this.m_xmlChanged;
        this.m_xmlChanged = m_xmlChanged;
        // xmlchanged = true isSaved = false
        tellPropertyChange("xmlchanged", !oldval, !this.m_xmlChanged);
    }

	protected Vector expandedPaths;
	protected HashMap expandedNodes;
	protected Vector expandedRows = new Vector();

	SelectiveBreadthFirstEnumeration senum;

	public static final String APP_NAME = "EXALTO GRID EDITOR";
	protected int selectedRow;
	protected int selectedCol;
	protected GridHelper gridHelper;
	protected JTable table;

	// for popup menu
/*
	protected JMenuItem insElemItem1;
	protected JMenuItem insAttrItem1;
	protected JMenuItem insTextItem1;

	protected JMenuItem delElemItem;
	protected JMenuItem delAttrItem1;
	protected JMenuItem delTextItem1;

	protected JMenuItem renameItem;
	protected JMenuItem expandAllItem;
*/
	protected JPopupMenu popupMenu;

	// OV added for undo/redo 03122006
	private Vector listeners = new Vector();

   	// OV added for xml changed 03122006
	private Vector listenerList = new Vector();

	// OV added for undo/redo 25/03/08
	private Clipboard treeClipboard;
	protected XmlEditor editorFrame;

	HashMap namespacesHash = new HashMap();


	 CompoundEdit cedit = new CompoundEdit();
	 DeleteElementEdit uedit1 = null;
	 RenameUndoableEdit uedit2 = null;
	 
	public SimpleTreeModelAdapter(XmlTreeModel treeTblModel, JTree tree,
			JTable table, JFrame frame) throws Exception {

	//OM	super(treeTblModel, tree);
		super(tree);

		this.atree = tree;
		this.table = table;
		this.editorFrame = (XmlEditor) frame;

		this.treeTableModel = (XmlTreeModel) treeTblModel;
		senum = new SelectiveBreadthFirstEnumeration(treeTableModel, atree);

		gridHelper = new GridHelper(this, editorFrame);

		initComponents();

        DesktopView desktopView = editorFrame.getDesktopView();
        this.addPropertyChangeListener(desktopView);

		tree.addTreeExpansionListener(new TreeExpansionListener() {

			/*
			 * Don't use fireTableRowsInserted() here; the selection model //
			 * would get updated twice.
			 */
			public void treeExpanded(TreeExpansionEvent event) {
				/*
				 * System.out.println(" in treeExpanded firing table changed
				 * treepath = " + event.getPath());
				 */
				fireTableDataChanged();
				treeExpanded = true;
				treeCollapsed = false;

				// System.out.println(" tree expanded
				// true%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% ");

			}

			public void treeCollapsed(TreeExpansionEvent event) {

				System.out
						.println(" tree collapsed true $$$$$$$$$$$$$$$$$$$$$$$$");

				treeCollapsed = true;
				treeExpanded = false;
				fireTableDataChanged();
			}
		});

		/*
		 * Installs a TreeSelectionListener that can update the table when //
		 * the tree node changes.
		 */
		TreeSelectionListener lSel = new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				/* ExaltoXmlNode node = getSelectedTreeNode(); */

				ExaltoXmlNode node = (ExaltoXmlNode) atree
						.getLastSelectedPathComponent();

				// System.out.println(" in valuechanged node = " + node);

				if (node == null)
					return;

				atree.startEditingAtPath(new TreePath(node.getPath()));
				// System.out.println(" new value " + node);

			}
		};

		atree.addTreeSelectionListener(lSel);

		/*
		 * Installs a TreeModelListener that can update the table when // the
		 * tree changes. We use delayedFireTableDataChanged as we can // not be
		 * guaranteed the tree will have finished processing // the event before
		 * us.
		 */
		treeTableModel.addTreeModelListener(new TreeModelListener() {

			public void treeNodesChanged(TreeModelEvent e) {
				/* System.out.println(" in treeNodesChanged"); */
				delayedFireTableDataChanged();
				// delayedFireTableDataChanged(e, 0);
			}

			public void treeNodesInserted(TreeModelEvent e) {
				/* System.out.println(" in treeNodesInserted"); */
				// delayedFireTableDataChanged(e, 1);
				// delayedFireTableDataChanged();
			}

			public void treeNodesRemoved(TreeModelEvent e) {
				/* System.out.println(" in treeNodesRemoved"); */
				// delayedFireTableDataChanged(e, 2);
				delayedFireTableDataChanged();
			}

			public void treeStructureChanged(TreeModelEvent e) {
				/* System.out.println(" in treeStructureChanged"); */
				delayedFireTableDataChanged();
			}
		});

		// popupmenu support
		// OV added for popupmenu
	/* OV c 270309	
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		popupMenu = new JPopupMenu("Exalto XML Editor");
		popupMenu.setPopupSize(110, 75);

		JMenu insertMenu = new JMenu("Insert");
		JMenu deleteMenu = new JMenu("Delete");

		insElemItem1 = new JMenuItem("Insert Element");
		insElemItem1.addActionListener(this);

		insAttrItem1 = new JMenuItem("Insert Attribute");
		insAttrItem1.addActionListener(this);

		insTextItem1 = new JMenuItem("Insert Text");
		insTextItem1.addActionListener(this);

		delElemItem = new JMenuItem("Delete Element");
		delElemItem.addActionListener(this);

		delAttrItem1 = new JMenuItem("Delete Attribute");
		delAttrItem1.addActionListener(this);

		delTextItem1 = new JMenuItem("Delete Text");
		delTextItem1.addActionListener(this);

		renameItem = new JMenuItem("Rename Element");
		renameItem.addActionListener(this);

		expandAllItem = new JMenuItem("Expand All");
		expandAllItem.addActionListener(this);

		insertMenu.add(insElemItem1);
		insertMenu.add(insAttrItem1);
		insertMenu.add(insTextItem1);

		deleteMenu.add(delElemItem);
		deleteMenu.add(delAttrItem1);
		deleteMenu.add(delTextItem1);

		popupMenu.add(insertMenu);
		popupMenu.add(deleteMenu);
		popupMenu.add(renameItem);
		popupMenu.add(expandAllItem);

		popupMenu.addSeparator();
*/
	}

	/* Wrappers, implementing TableModel interface. */
	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	public String getColumnName(int column) {
		return treeTableModel.getColumnName(column);
	}

	/*
	 * public Class getColumnClass(int column) { return
	 * treeTableModel.getColumnClass(column); }
	 */
	public int getRowCount() {
		return atree.getRowCount();
	}

	protected Object nodeForRow(int row) {
		TreePath treePath = atree.getPathForRow(row);

		return treePath.getLastPathComponent();
	}

	/*
	 * public Object getValueAt(int row, int column) { return
	 * treeTableModel.getValueAt(nodeForRow(row), column); }
	 */
	public Object getValueAt(int row, int column) {
		return treeTableModel.getValueAt(nodeForRow(row), row, column);
	}

	public boolean isCellEditable(int row, int column) {
		return treeTableModel.isCellEditable(nodeForRow(row), row, column);
	}

	/*
	 * public void setValueAt(Object value, int row, int column) {
	 *  }
	 */
	/**
	 * Invokes fireTableDataChanged after all the pending events have been
	 * processed. SwingUtilities.invokeLater is used to handle this.
	 */
	protected void delayedFireTableDataChanged() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				fireTableDataChanged();
			}
		});
	}

	/**
	 * Invokes fireTableDataChanged after all the pending events have been
	 * processed. SwingUtilities.invokeLater is used to handle this.
	 */
	protected void delayedFireTableDataChanged(final TreeModelEvent tme,
			final int typeChange) {
		/*
		 * SwingUtilities.invokeLater(new Runnable() { public void run() {
		 * fireTableDataChanged(); } });
		 */

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				int indices[] = tme.getChildIndices();
				TreePath path = tme.getTreePath();
				if (indices != null) {
					if (atree.isExpanded(path)) { /*
													 * Dont bother to update if
													 * the parent // node is
													 * collapsed
													 */

						int startingRow = atree.getRowForPath(path) + 1;
						int min = Integer.MAX_VALUE;
						int max = Integer.MIN_VALUE;
						for (int i = 0; i < indices.length; i++) {
							if (indices[i] < min) {
								min = indices[i];
							}
							if (indices[i] > max) {
								max = indices[i];
							}
						}
						switch (typeChange) {
						case 0:
							fireTableRowsUpdated(startingRow + min, startingRow
									+ max);
							break;
						case 1:
							fireTableRowsInserted(startingRow + min,
									startingRow + max);
							break;
						case 2:
							fireTableRowsDeleted(startingRow + min, startingRow
									+ max);
							break;
						}
					} else {
						/*
						 * not expanded - but change might effect appearance of
						 * parent // Issue #82-swingx
						 */
						int row = atree.getRowForPath(path);
						fireTableRowsUpdated(row, row);
					}
				} else { /* case where the event is fired to identify root. */
					fireTableDataChanged();
				}
			}
		});
	}

	public ArrayList getColumnMappingList() {
		return treeTableModel.getColumnMappingList();
	}

	public ArrayList getParentList() {
		return treeTableModel.getParentList();
	}

	// OV added for undo/redo 03122006
	public void addUndoableEditListener(UndoableEditListener listener) {
		listeners.add(listener);
	}

	public void removeUndoableEditListener(UndoableEditListener listener) {
		listeners.remove(listener);
	}

	// OV added for undo/redo end 03122006

	public SelectiveBreadthFirstEnumeration getSelectiveBreadthFirstEnumeration() {
		return senum;
	}

	public void updateColumnMapping(String opfunc) {

		// System.out.println(" @@@@@@@@@@@@@@@@@@@@@@@@@@@in
		// updatecolumnmapping@@@@@@@@@@@@@@@@@@@@@");

		HashMap rowMapper = new HashMap();

		int currRow = 0;
		int currCol = 0;
		ArrayList nodeList = new ArrayList();
		ArrayList parentList = new ArrayList();
		Hashtable nodeMapTbl = new Hashtable();
		Stack colStack = new Stack();
		int tempCol = 0;
		boolean restored = false;
		// System.out.println("TTMA currRow " + currRow);
		// System.out.println("TTMA currCol " + currCol);

		TreeNode root = (TreeNode) treeTableModel.getRoot();

		ExaltoXmlNode docnode = (ExaltoXmlNode) root;
		nodeMapTbl.put(docnode, currRow + "," + currCol);

		senum.reset(opfunc);
		while (senum.hasMoreElements()) {
			ExaltoXmlNode inode = (ExaltoXmlNode) senum.nextElement();

			// System.out.println("TTMA child Node name = " + inode.toString());

			/*
			 * // System.out.println("TTMA child Node type = " + //
			 * inode.getNodeType());
			 */
			int nlev = inode.getLevel();

			// System.out.println("TTMA child Node level = " + nlev);

			if (inode.getNodeType() == Node.ELEMENT_NODE
					|| inode.getNodeType() == Node.DOCUMENT_NODE) {

				/*
				 * System.out.println("TTMA adding at currRow " + currRow); //
				 * System.out.println("TTMA adding at currCol " + currCol);
				 */
				nlev = 0;

				nodeMapTbl.put(inode, currRow + "," + nlev);

				if (nlev > maxCol)
					maxCol = nlev;

				Node xmlNode = inode.getXmlNode();

				int g = 0;
				if (inode.getNodeType() == Node.ELEMENT_NODE) {
					NamedNodeMap nnmp = xmlNode.getAttributes();

					for (; g < nnmp.getLength(); g++) {
						Node attr = nnmp.item(g);

		 //   			System.out.println(" attr name = " + attr.getNodeName());                        		
         //    			System.out.println(" attr namesp = " + attr.getPrefix());                        		
         //               System.out.println(" attr namesp val = " + attr.getNodeValue());                        		
                        
             			if(attr.getNodeName().startsWith("xmlns")) {
             				addNamespaceToHash(attr);
             			}
         
						ExaltoXmlNode enode = new ExaltoXmlAttr(attr);

                        this.treeTableModel.getDomToTreeMap().put(attr, enode);

						currCol = g + 1;
						if (currCol > maxCol)
							maxCol = currCol;

						nodeMapTbl.put(enode, currRow + "," + (g + 1));

                    }

				}

				if (xmlNode.hasChildNodes()) {
					NodeList textChilds = xmlNode.getChildNodes();
					int w = 0;
					for (int u = 0; u < textChilds.getLength(); u++) {
						Node tc = textChilds.item(u);
                        
						ExaltoXmlNode txtNode = null;
                        if(tc.getNodeType() == 3) {
             
                        	txtNode = new ExaltoXmlText(tc);

                            this.treeTableModel.getDomToTreeMap().put(tc, txtNode);

                            nodeMapTbl.put(txtNode, currRow + "," + (g+u+1));
                        
                        } else if(tc.getNodeType() == 4) {
                        	
                        	txtNode = new ExaltoXmlCData(tc);
                            nodeMapTbl.put(txtNode, currRow + "," + (g+u+1));
                            
                        }
                            
                            if(currCol+w+u+1 > maxCol)
                                maxCol = currCol+w+u+1;
                            
                            
                            w++;
					}
				}
			}
			// OV added newly 130508
			else if (inode.getNodeType() == Node.COMMENT_NODE) {
				currCol = 0;
				nodeMapTbl.put(inode, currRow + "," + currCol);
				Node xmlNode = inode.getXmlNode();

				org.w3c.dom.Comment cmt = (org.w3c.dom.Comment) xmlNode;

				String data = cmt.getData();

				int h = 0;
				Node attrNode = ((org.w3c.dom.Document) treeTableModel
						.getDocument()).createTextNode(data);
				ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
				nodeMapTbl.put(anode1, currRow + "," + (currCol + h + 1));
				h++;

			} else if (inode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
				currCol = 0;
				nodeMapTbl.put(inode, currRow + "," + currCol);
				Node xmlNode = inode.getXmlNode();

				org.w3c.dom.DocumentType docType = (org.w3c.dom.DocumentType) xmlNode;

				String name = docType.getName();
				int h = 0;

				Node attrNode = ((org.w3c.dom.Document) treeTableModel
						.getDocument()).createAttribute(name);

				ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
				nodeMapTbl.put(anode1, currRow + "," + (currCol + h + 1));
				h++;

				String pubId = docType.getPublicId();
				String sysId = docType.getSystemId();

		/*
				System.out
						.println(" ###########ATTR pubid type ############# = "
								+ pubId);
				System.out
						.println(" ###########ATTR sysid type ############# = "
								+ sysId);
		*/
				attrNode = ((org.w3c.dom.Document) treeTableModel.getDocument())
						.createTextNode(pubId);

				ExaltoXmlAttr anode2 = new ExaltoXmlAttr(attrNode);
				nodeMapTbl.put(anode2, currRow + "," + (currCol + h + 1));
				h++;

				attrNode = ((org.w3c.dom.Document) treeTableModel.getDocument())
						.createTextNode(sysId);

				ExaltoXmlAttr anode3 = new ExaltoXmlAttr(attrNode);
				nodeMapTbl.put(anode3, currRow + "," + (currCol + h + 1));
				h++;

			} else if (inode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
				currCol = 0;
				nodeMapTbl.put(inode, currRow + "," + currCol);
				Node xmlNode = inode.getXmlNode();

				ProcessingInstruction p = (ProcessingInstruction) xmlNode;

				String target = p.getTarget();
				String piData = p.getData();

				StringTokenizer stok = new StringTokenizer(piData, " ");
				int h = 0;
				while (stok.hasMoreTokens()) {

					String tok = stok.nextToken();
					String[] s = tok.split("=");

					String name = s[0];
					String value = s[1];

					Node attrNode = ((org.w3c.dom.Document) treeTableModel
							.getDocument()).createAttribute(name);

					attrNode.setNodeValue(value);

					ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
					nodeMapTbl.put(anode1, currRow + "," + (currCol + h + 1));
					h++;

				}
			}

			currRow++;

		}

		Iterator iter = nodeMapTbl.keySet().iterator();
		int x = 0;
		int y = 0;
		while (iter.hasNext()) {
			StringBuffer sbuf = new StringBuffer();
			ExaltoXmlNode aptr = (ExaltoXmlNode) iter.next();

			String rc = (String) nodeMapTbl.get(aptr);

			int n = parentList.size();
			parentList.add(aptr);

			String[] rowCol = rc.split(",");

			int nrow = Integer.parseInt(rowCol[0]);

			ArrayList nlist = (ArrayList) rowMapper.get(new Integer(nrow));
			if (nlist != null) {
				sbuf.append((String) nlist.get(0));
				sbuf.append("|");
				sbuf.append(rc);
				sbuf.append(",");
				sbuf.append(y++);
				nlist.set(0, sbuf.toString());
				rowMapper.put(new Integer(nrow), nlist);
			} else {
				nlist = new ArrayList();
				sbuf.append(rc);
				sbuf.append(",");
				sbuf.append(y++);
				nlist.add(sbuf.toString());
				rowMapper.put(new Integer(nrow), nlist);
				StringBuffer strBuf = new StringBuffer();
				strBuf.append(rc);
				strBuf.append(",");
				strBuf.append(x++);
				nodeList.add(strBuf.toString());
			}

		}

        ArrayList nmspList = new ArrayList();
        Set set = namespacesHash.keySet();
        
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        	String nmsp = (String) iterator.next();
        	String nmspval = (String) namespacesHash.get(nmsp);
        	nmspList.add(nmsp + "&" + nmspval);
        }
        
        if(docnode != null)
        	docnode.setNamespaces(nmspList);

		// treeTableModel.setMaxCol(maxCol+1);
		treeTableModel.setRowMapper(rowMapper);
		treeTableModel.setParentList(parentList);

	}

    public HashMap getNamespaces() {
        return namespacesHash;
    }

	public Node getSelectedNode() {
		ExaltoXmlNode treeNode = getSelectedTreeNode();
		if (treeNode == null)
			return null;
		return treeNode.getXmlNode();
	}

	public ExaltoXmlNode getSelectedTreeNode() {

		TreePath path = atree.getSelectionPath();

		/* System.out.println(" in getSelectedTreeNode path = " + path); */

		if (path == null)
			return null;

		Object obj = path.getLastPathComponent();
		if (!(obj instanceof ExaltoXmlNode))
			return null;

		return (ExaltoXmlNode) obj;
	}

	public ExaltoXmlNode[] getSelectedTreeNodes() {

		TreePath[] paths = atree.getSelectionPaths();
		ExaltoXmlNode[] enodes = new ExaltoXmlNode[paths.length];
		/* System.out.println(" in getSelectedTreeNode path = " + path); */

		if (paths == null)
			return null;
		for (int i = 0; i < paths.length; i++) {
			Object obj = paths[i].getLastPathComponent();
			if (!(obj instanceof ExaltoXmlNode))
				return null;
			enodes[i] = (ExaltoXmlNode) obj;
		}

		return enodes;
	}

	public void mousePressed(MouseEvent e) {
		System.out.println(" in mousePressed ");

		if (treeExpanded || treeCollapsed) {
			/*
			 * OV commented 211106 already existing int selRow =
			 * atree.getRowForLocation(e.getX(), e.getY()); selectedRow =
			 * atree.getRowForLocation(e.getX(), e.getY());
			 *  // System.out.println(" selRow = " + selRow);
			 * 
			 * Point p = e.getPoint();
			 * 
			 * int sRow = table.rowAtPoint(p); int selCol =
			 * table.columnAtPoint(p);
			 *  // System.out.println(" sRow = " + sRow); //
			 * System.out.println(" selCol = " + selCol);
			 * 
			 * TreePath selPath = null;
			 * 
			 * if(sRow != -1) {
			 * 
			 * ExaltoXmlNode xnode = gridHelper.getNodeForRowColumn(sRow,
			 * selCol);
			 * 
			 * selPath = atree.getPathForLocation(e.getX(), e.getY());
			 * 
			 * senum.setRow(sRow); senum.setPath(selPath);
			 * senum.setExpanded(treeExpanded);
			 * senum.setCollapsed(treeCollapsed);
			 * 
			 * updateColumnMapping("");
			 *  }
			 * 
			 * treeExpanded = false; treeCollapsed = false;
			 */

			int selRow = atree.getRowForLocation(e.getX(), e.getY());
			selectedRow = atree.getRowForLocation(e.getX(), e.getY());

			Point p = e.getPoint();

			int sRow = treeTable.rowAtPoint(p);
			int selCol = treeTable.columnAtPoint(p);

			TreePath selPath = null;

			System.out.println(" tdap  selRow " + selRow);
			System.out.println(" tdap  sRow " + sRow);
			// System.out.println(" tdap getRowCount() " + getRowCount());

			// if(selRow != -1) {
			if (sRow != -1) {

				ExaltoXmlNode xnode = gridHelper.getNodeForRowColumn(sRow,
						selCol);

				selPath = atree.getPathForLocation(e.getX(), e.getY());

				// System.out.println(" in mouse pressedtreeNode " + xnode);

				if (treeExpanded && expandedPaths != null) {
					int n = expandedPaths.size();
					expandedPaths.add(selPath);
					System.out.println(" in mouse pressed expandedPaths  "
							+ expandedPaths);
					expandedNodes.put(xnode, new Integer(n));
				} else if (e.getClickCount() <= 1) {
					if (expandedPaths != null) {
						expandedPaths.remove(selPath);
						expandedNodes.remove(xnode);
					}
				}

				// System.out.println(" in mouse pressed expandedNodes = "+
				// expandedNodes);

				senum.setRow(selRow);
				senum.setPath(selPath);
				senum.setExpanded(treeExpanded);
				senum.setCollapsed(treeCollapsed);
				// System.out.println(" tree sel row = " + selRow);
				// System.out.println(" tree sel path = " + selPath);

				// System.out.println(" calling updateColumnMapping treeExpanded
				// Collapesed = " + treeExpanded + ", " + treeCollapsed);

				updateColumnMapping("");

			}

			treeExpanded = false;
			treeCollapsed = false;

		} else {
			/* System.out.println(" tree not acted on "); */

			try {
				Point origin = e.getPoint();
				selectedRow = table.rowAtPoint(origin);
				selectedCol = table.columnAtPoint(origin);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	public void mouseClicked(MouseEvent e) {
	//OM	treeTable.reset();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
		// System.out.println(" in mouseReleased ");
		// treeTable.reset();

		System.out.println(" sel trow - " + treeTable.getSelectedRow());
		System.out.println(" sel tcol - " + treeTable.getSelectedColumn());

		if (e.isPopupTrigger() && treeTable.getSelectedRow() != -1) {

			// popupMenu.show(e.getComponent(), e.getX(), e.getY());
			jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
			return;
		}
	}

	public void treeStateChanged(MouseEvent e) {
		/*
		 * OV commented 211106 already exisiting
		 * 
		 * int selRow = atree.getRowForLocation(e.getX(), e.getY()); selectedRow =
		 * atree.getRowForLocation(e.getX(), e.getY());
		 * 
		 * System.out.println(" selRow = " + selRow);
		 * 
		 * Point p = e.getPoint();
		 * 
		 * int sRow = table.rowAtPoint(p); int selCol = table.columnAtPoint(p);
		 * 
		 * System.out.println(" sRow = " + sRow); System.out.println(" selCol = " +
		 * selCol);
		 * 
		 * TreePath selPath = null;
		 * 
		 * if(sRow != -1) {
		 * 
		 * ExaltoXmlNode xnode = gridHelper.getNodeForRowColumn(sRow, selCol);
		 * 
		 * selPath = atree.getPathForLocation(e.getX(), e.getY());
		 * 
		 * senum.setRow(sRow); senum.setPath(selPath);
		 * senum.setExpanded(treeExpanded); senum.setCollapsed(treeCollapsed);
		 * 
		 * updateColumnMapping("");
		 *  }
		 * 
		 * treeExpanded = false; treeCollapsed = false;
		 */

		int selRow = atree.getRowForLocation(e.getX(), e.getY());
		selectedRow = atree.getRowForLocation(e.getX(), e.getY());

		Point p = e.getPoint();

		int sRow = treeTable.rowAtPoint(p);
		int selCol = treeTable.columnAtPoint(p);

		TreePath selPath = null;

		// System.out.println(" tdap selRow " + selRow);
		// System.out.println(" tdap getRowCount() " + getRowCount());
		// System.out.println(" tdap sRow " + sRow);

		// OV commented 211106
		// if(selRow != -1) {
		if (sRow != -1) {

			ExaltoXmlNode xnode = gridHelper.getNodeForRowColumn(sRow, selCol);

			selPath = atree.getPathForLocation(e.getX(), e.getY());

			// System.out.println(" in mouse pressedtreeNode " + xnode);

			if (treeExpanded) {
				int n = (expandedPaths != null) ? expandedPaths.size() : 0;
				if (expandedPaths == null)
					expandedPaths = new Vector();
				if (expandedNodes == null)
					expandedNodes = new HashMap();

				expandedPaths.add(selPath);
				// System.out.println(" in mouse pressed expandedPaths " +
				// expandedPaths);
				expandedNodes.put(xnode, new Integer(n));
			} else if (e.getClickCount() <= 1) {
				if (expandedPaths != null) {
					expandedPaths.remove(selPath);
					expandedNodes.remove(xnode);
				}
			}

			// System.out.println(" in mouse pressed expandedNodes = "+
			// expandedNodes);

			senum.setRow(sRow);
			senum.setPath(selPath);
			senum.setExpanded(treeExpanded);
			senum.setCollapsed(treeCollapsed);
			// System.out.println(" tree sel row = " + selRow);
			// System.out.println(" tree sel path = " + selPath);

			// System.out.println(" calling updateColumnMapping treeExpanded
			// Collapesed = " + treeExpanded + ", " + treeCollapsed);

			updateColumnMapping("");

		}

		treeExpanded = false;
		treeCollapsed = false;

	}

	/*
	// tree edit handling methods begin
	public void insertNode(String nodeType) throws Exception {

		ExaltoXmlNode treeNode = getSelectedTreeNode();

		if (treeNode == null) {
			return;
		}

		if (treeNode.getNodeType() == 3 || treeNode.getNodeType() == 8
				|| treeNode.getNodeType() == 7 || treeNode.getNodeType() == 10) {

			// throw new Exception("Cannot add node to text node");
			int response = JOptionPane.showConfirmDialog(null, "Cannot add "
					+ nodeType + " Element to Non-element node",
					"Element add error", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			return;

		}

		Node parent = treeNode.getXmlNode();
		if (parent == null)
			return;
		
		
		ArrayList nmspList = (ArrayList) treeNode.getNamespaces();
		
	//	for(int k=0;k<nmspList.size();k++)
	//		System.out.println(" nmsp  = " + nmspList.get(k));
			
			
		String input = displayMessage(nodeType);

		// System.out.println(" input " + input); 

		if ((nodeType.intern() == "Element".intern() || nodeType.intern() == "Attr"
				.intern())
				&& !isLegalXmlName(input)) {
			return;
		}

		try {
			ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();
			Node newElement = null;
			if (nodeType.intern() == "Element".intern()) {
				newElement = ((org.w3c.dom.Document) treeTableModel
						.getDocument()).createElement(input);

			} else if (nodeType.intern() == "Text".intern()) {
				newElement = ((org.w3c.dom.Document) treeTableModel
						.getDocument()).createTextNode(input);
			} else if (nodeType.intern() == "Attr".intern()) {
				newElement = ((org.w3c.dom.Document) treeTableModel
						.getDocument()).createAttribute(input);
				// insertNode("Attr", treeNode, nodeElement);
			}

			ExaltoXmlNode nodeElement = new ExaltoXmlNode(newElement);

			boolean addTextNode = true;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Attr".intern())
				addTextNode = false;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Element".intern())
				treeNode.addXmlNode(nodeElement, addTextNode);
			else
				treeNode.addAttrNode(nodeElement, addTextNode);

			int[] cind = new int[1];
			cind[0] = treeNode.getChildCount();
			Node[] celem = new Node[1];
			celem[0] = newElement;

			TreeModelEvent tme = new TreeModelEvent(this, treeTableModel
					.getPathToRoot(treeNode), cind, celem);

			int rowCount = atree.getRowCount();
			Vector expandedPaths = new Vector();
			for (int i = 0; i < rowCount; i++) {
				TreePath treePath = atree.getPathForRow(i);
				if (atree.isExpanded(treePath)) {
					expandedPaths.add(treePath);
				}
			}

			// System.out.println(" expandedPaths = " + expandedPaths);

	
			// OV c 25/03/08
			// ((DefaultTreeModel)tree.getModel()).reload(treeNode);

			// OV commented 17/9/06
			((SimpleTreeModel) treeTableModel).fireTreeStructureChanged(tme);

			// if(nodeType.intern() == "Element".intern()) {
			// nodesWereInserted(treeNode, new int[]
			// {treeNode.getIndex(nodeElement) });
			// }

			for (int i = 0; i < expandedPaths.size(); i++) {
				atree.expandPath((TreePath) expandedPaths.get(i));
			}

			rowCount = atree.getRowCount();

			// OV c 25/03/08
			// fireTableDataChanged();

			TreeNode[] tn = treeNode.getPath();
			TreePath selPath = new TreePath(tn);
			int selRow = atree.getRowForPath(selPath);

			if (!atree.isExpanded(selPath))
				atree.expandPath(selPath);

			// System.out.println(" selRow = " + selRow);

			senum.setRow(selRow);
			senum.setPath(selPath);

			updateColumnMapping("NODE_ADD");

			if (nodeType.intern() == "Element".intern())
				gridHelper.fireUndoableEditHappened(new InsertElementEdit(this,
						treeNode, nodeElement)); // todo
			else if (nodeType.intern() == "Attr".intern())
				gridHelper.fireUndoableEditHappened(new InsertAttrEdit(this,
						treeNode, nodeElement)); // todo
			else
				gridHelper.fireUndoableEditHappened(new InsertTextEdit(this,
						treeNode, nodeElement)); // todo

			// if(nodeType.intern() == "Element".intern())
			// fireTableStructureChanged();

			// DefaultMutableTreeNode

			// JTree
			int numNodes = gridHelper.getSelectionIndex(treeNode, selPath,
					atree);

	//		System.out.println(" numNodes = " + numNodes);

			if (!(nodeType.intern() == "Text".intern() || (nodeType.intern() == "Attr"
					.intern()))) {

				// table.scrollRectToVisible(rect);
				// if (selPath != null) {
				// TreePath path = selPath.pathByAddingChild(nodeElement);
				// tree.setSelectionPath(path);
				// tree.scrollPathToVisible(path);
				// }

		//		System.out.println(" selectedRow " + selectedRow);
		//		System.out.println(" rowCount " + rowCount);

				int incr = selectedRow + numNodes;
				// if(selectedRow+numNodes >= rowCount)
				// incr = rowCount-1;
				// incr--;

				// OV 25/03/08
				// treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				// treeTable.setColumnSelectionAllowed(false);

		//		System.out.println(" incr " + incr);
				// OV 25/03/08
				// treeTable.setRowSelectionInterval(incr, incr);
	
				
				System.out.println(" table sel model getSelectedRow "
						+ treeTable.getSelectedRow());

				// treeTable.getSelectionModel().setSelectionInterval(incr,
				// incr);

				// treeTable.getComponentAdapter().row = incr;
				// treeTable.repaint();
				// treeTable.revalidate();

				// treeTable.removeRowSelectionInterval(incr+1, incr+1);

				// treeTable.setColumnSelectionInterval(selectedCol,
				// selectedCol);
				// reapplySettings();
				// table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

				// table.revalidate();
				// table.repaint();

				// }

			}

			setXmlChanged(true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

*/
	public void insertNode(String nodeType) {

		ExaltoXmlNode treeNode = null;
		boolean isUndoRedo = true;
		ExaltoXmlNode nodeElement = null;
		Node newElement = null;


		try {
		
			treeNode = getSelectedTreeNode();

		if (treeNode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "node.not.selected"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.not.selected"), 0, ColWidthTypes.ERR));
			return;
		}

		if (treeNode.getNodeType() != 1) {
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "insert.not.element"), 0, ColWidthTypes.ERR));
//			throw new Exception("Cannot add node to a Non-element node");
			return;
		}

		Node parent = treeNode.getXmlNode();
		if (parent == null)
			return;

		String input = null;

		input = displayMessage(nodeType);

				// System.out.println(" input " + input); 

				if ((nodeType.intern() == "Element".intern() || nodeType
						.intern() == "Attr".intern())
						&& !isLegalXmlName(input)) {
					editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "name.illegal"), 0, ColWidthTypes.ERR));
					return;
				}

				// try {
				ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();
				if (nodeType.intern() == "Element".intern())
					newElement = ((org.w3c.dom.Document) treeTableModel
							.getDocument()).createElement(input);
				else if (nodeType.intern() == "Text".intern())
					newElement = ((org.w3c.dom.Document) treeTableModel
							.getDocument()).createTextNode(input);
				else if (nodeType.intern() == "Attr".intern())
					newElement = ((org.w3c.dom.Document) treeTableModel
							.getDocument()).createAttribute(input);

				nodeElement = new ExaltoXmlNode(newElement);

				
				
			boolean addTextNode = true;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Attr".intern())
				addTextNode = false;
		
			
			if (nodeType.intern() == "Attr".intern())
				nodeElement.setOwnerElement(treeNode.getXmlNode());


			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Element".intern()) {
				
					treeNode.addXmlNode(nodeElement, addTextNode);
			} else
				treeNode.addAttrNode(nodeElement, addTextNode);

			
			
				this.storeTreeExpansionState();
			
			if (nodeType.intern() == "Element".intern())
				nodesWereInserted(treeNode, new int[] { treeNode
						.getIndex(nodeElement) });

			
				this.loadTreeExpansionState();

			TreeNode[] tn = treeNode.getPath();
			TreePath selPath = new TreePath(tn);
			int selRow = atree.getRowForPath(selPath);

			// System.out.println(" selRow = " + selRow); 

			senum.setRow(selRow);
			senum.setPath(selPath);

			updateColumnMapping("NODE_ADD");

				if (nodeType.intern() == "Element".intern())
					fireUndoableEditHappened(
							new InsertElementEdit(this, treeNode, nodeElement, null, 0)); // todo
				else if (nodeType.intern() == "Attr".intern())
					fireUndoableEditHappened(new InsertAttrEdit(this,
							treeNode, nodeElement)); // todo
				else if (nodeType.intern() == "Text".intern())
					fireUndoableEditHappened(new InsertTextEdit(this,
							treeNode, nodeElement)); // todo

			setXmlChanged(true);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void insertNodeUndoRedo(String nodeType, TreeNode parentNode, TreeNode child, Node refChild, int refIndex) {

		ExaltoXmlNode treeNode = null;
		boolean isUndoRedo = true;
		ExaltoXmlNode nodeElement = null;
		Node newElement = null;


		try {

			treeNode = (ExaltoXmlNode) parentNode;

			nodeElement = (ExaltoXmlNode) child;

			
		if (treeNode == null) {
			return;
		}

		if (treeNode.getNodeType() != 1) {
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "add.text.element.not"), 0, ColWidthTypes.ERR));
//			throw new Exception("Cannot add node to a Non-element node");
		}

		Node parent = treeNode.getXmlNode();
		if (parent == null)
			return;

			boolean addTextNode = true;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Attr".intern())
				addTextNode = false;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Element".intern()) {
				
		//		if(!isUndoRedo)
		//			treeNode.addXmlNode(nodeElement, addTextNode);
		//		else					
					treeNode.insertXmlNode(nodeElement, refChild, refIndex, addTextNode);
			} else
				treeNode.addAttrNode(nodeElement, addTextNode);

				this.storeTreeExpansionState();
			
				 System.out.println(" index node = " + treeNode.getIndex(nodeElement)); 

				
				if (nodeType.intern() == "Element".intern())
					nodesWereInserted(treeNode, new int[] { treeNode
						.getIndex(nodeElement) });

			
			this.loadTreeExpansionState();
			
			TreeNode[] tn = treeNode.getPath();
			TreePath selPath = new TreePath(tn);
			int selRow = atree.getRowForPath(selPath);

			// System.out.println(" selRow = " + selRow); 

			senum.setRow(selRow);
			senum.setPath(selPath);

			updateColumnMapping("NODE_ADD");

			setXmlChanged(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	public void insertElementUndoRedo(String nodeType, TreeNode parentNode, TreeNode child, TreeNode oldNode, TreeNode [] childNodes, Node refChild, int refIndex) {

		ExaltoXmlNode treeNode = null;
		boolean isUndoRedo = true;
		ExaltoXmlNode nodeElement = null;
		Node newElement = null;
		ExaltoXmlNode oldElement = null;


		try {

			treeNode = (ExaltoXmlNode) parentNode;

			nodeElement = (ExaltoXmlNode) child;


			oldElement = (ExaltoXmlNode) oldNode;

			
		if (treeNode == null) {
			return;
		}

		if (treeNode.getNodeType() != 1) {
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "add.text.element.not"), 0, ColWidthTypes.ERR));
//			throw new Exception("Cannot add node to a Non-element node");
		}

		Node parent = treeNode.getXmlNode();
		if (parent == null)
			return;

			boolean addTextNode = true;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Attr".intern())
				addTextNode = false;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Element".intern()) {
				
		//		if(!isUndoRedo)
		//			treeNode.addXmlNode(nodeElement, addTextNode);
		//		else					
				
					
				
				//					treeNode.insertXmlNode(nodeElement, refChild, refIndex, addTextNode);
			} else
				treeNode.addAttrNode(nodeElement, addTextNode);

			
			for(int k=0;k<childNodes.length;k++) {
				ExaltoXmlNode tn = (ExaltoXmlNode) childNodes[k];
				oldElement.addXmlNode(tn, true);
			}

				this.storeTreeExpansionState();
			

				
				if (nodeType.intern() == "Element".intern()) {
					int [] ind = new int[childNodes.length];
					for(int k=0;k<childNodes.length;k++) {					
						ind[k] = oldElement.getIndex(childNodes[k]);
					}
					
					nodesWereInserted(oldElement, ind);
					
				}

				System.out.println(" tree node  =  " + treeNode.getXmlNode().getNodeName()); 
				System.out.println(" tree node index =  " + treeNode.getIndex(nodeElement)); 
				
			
			int[] childIndices = new int[1];
			Object[] removedChildren = new TreeNode[1];
			removedChildren[0] = nodeElement;
			childIndices[0] = treeNode.getIndex(nodeElement);

				nodeElement.remove();
						
				
					int rowCount = atree.getRowCount();
					expandedPaths = new Vector();
					for (int i = 0; i < rowCount; i++) {
						TreePath treePath = atree.getPathForRow(i);
				
						ExaltoXmlNode tn = (ExaltoXmlNode) treePath.getLastPathComponent(); 
					
						System.out.println(" node name = " + tn.getXmlNode().getNodeName()); 

						System.out.println(" node name equals nodeleme =  " + (tn == nodeElement)); 
						
					}


				nodesWereRemoved(treeNode, childIndices, removedChildren);
				
				
				
			this.loadTreeExpansionState();
			
			TreeNode[] tn = treeNode.getPath();
			TreePath selPath = new TreePath(tn);
			int selRow = atree.getRowForPath(selPath);

			// System.out.println(" selRow = " + selRow); 

			senum.setRow(selRow);
			senum.setPath(selPath);

			updateColumnMapping("NODE_ADD");

			setXmlChanged(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	public void insertNodeForPaste(String nodeType, TreeNode parentNode, TreeNode child, Node refChild, int refIndex, String  pasteMode) {

		ExaltoXmlNode treeNode = null;
		boolean isUndoRedo = true;
		ExaltoXmlNode nodeElement = null;
		Node newElement = null;
		PasteUndoableEdit edit = null;

		try {

			treeNode = (ExaltoXmlNode) parentNode;

			nodeElement = (ExaltoXmlNode) child;

			
		if (treeNode == null) {
			return;
		}

		if (treeNode.getNodeType() != 1) {
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "add.text.element.not"), 0, ColWidthTypes.ERR));
//			throw new Exception("Cannot add node to a Non-element node");
		}

		Node parent = treeNode.getXmlNode();
		if (parent == null)
			return;

			boolean addTextNode = true;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Attr".intern())
				addTextNode = false;

			if (nodeType.intern() == "Text".intern()
					|| nodeType.intern() == "Element".intern()) {
			
				
                edit = new PasteUndoableEdit(this, parentNode, child, nodeType, refChild, refIndex);
    	    	
			//	if(!isUndoRedo)
			//		treeNode.addXmlNode(nodeElement, addTextNode);
			//	else
   //             	if(pasteMode.equals("before") || pasteMode.equals("into"))
                		treeNode.insertXmlNode(nodeElement, refChild, refIndex, addTextNode);
					
			} else {
                
				edit = new PasteUndoableEdit(this, parentNode, child, "Attr",  refChild, refIndex);
    	        
				treeNode.addAttrNode(nodeElement, addTextNode);
			}
			
			
				this.storeTreeExpansionState();
			
				 System.out.println(" index node = " + treeNode.getIndex(nodeElement)); 

				
				if (nodeType.intern() == "Element".intern()) {
					
					nodesWereInserted(treeNode, new int[] { treeNode
						.getIndex(nodeElement) });
            
			/*		
                    ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();
                    
                    TreeModelEvent tme = new TreeModelEvent(treeTableModel,
                            getPathToRoot(doc));
                    
                    treeTableModel.fireTreeStructureChanged(tme); 
			*/
					
				}
				
			
			this.loadTreeExpansionState();
			
			TreeNode[] tn = treeNode.getPath();
			TreePath selPath = new TreePath(tn);
			int selRow = atree.getRowForPath(selPath);

			// System.out.println(" selRow = " + selRow); 

			senum.setRow(selRow);
			senum.setPath(selPath);
			
			fireUndoableEditHappened(edit);
  	      
			updateColumnMapping("NODE_ADD");

			setXmlChanged(true);
		
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	
	
	/**
	 * Invoke this method after you've inserted some TreeNodes into node.
	 * childIndices should be the index of the new elements and must be sorted
	 * in ascending order.
	 */
	public void nodesWereInserted(TreeNode node, int[] childIndices) {
		if (listenerList != null && node != null && childIndices != null
				&& childIndices.length > 0) {
			int cCount = childIndices.length;
			Object[] newChildren = new Object[cCount];

			for (int counter = 0; counter < cCount; counter++)
				newChildren[counter] = node.getChildAt(childIndices[counter]);
			TreeModelEvent tme = new TreeModelEvent(this, getPathToRoot(node),
					childIndices, newChildren);
			treeTableModel.fireTreeNodesInserted(tme);
		}
	}

	/**
	 * Invoke this method after you've removed some TreeNodes from node.
	 * childIndices should be the index of the removed elements and must be
	 * sorted in ascending order. And removedChildren should be the array of the
	 * children objects that were removed.
	 */
	public void nodesWereRemoved(TreeNode node, int[] childIndices,
			Object[] removedChildren) {
		if (node != null && childIndices != null) {
			TreeModelEvent tme = new TreeModelEvent(this, getPathToRoot(node),
					childIndices, removedChildren);
			treeTableModel.fireTreeNodesRemoved(tme);
		}
	}

	public TreeNode[] getPathToRoot(TreeNode node) {
		return getPathToRoot(node, 0);
	}

	protected TreeNode[] getPathToRoot(TreeNode node, int depth) {
		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/*
		 * Check for null, in case someone passed in a null node, or they passed
		 * in an element that isn't rooted at root.
		 */
		if (node == null) {
			if (depth == 0)
				return null;
			else
				retNodes = new TreeNode[depth];
		} else {
			depth++;
			if (node == treeTableModel.getRoot())
				retNodes = new TreeNode[depth];
			else
				retNodes = getPathToRoot(node.getParent(), depth);
			retNodes[retNodes.length - depth] = node;
		}
		return retNodes;
	}

	private String displayMessage(String nodeType) {

		String input = null;

		if (nodeType.intern() == "Element".intern())
			input = (String) JOptionPane.showInputDialog(atree,
					"Please enter name of the new XML node", ExaltoConstants.APP_NAME,
					JOptionPane.PLAIN_MESSAGE, null, null, "");
		else if (nodeType.intern() == "Text".intern())
			input = (String) JOptionPane.showInputDialog(atree,
					"Please enter value for the new Text node", ExaltoConstants.APP_NAME,
					JOptionPane.PLAIN_MESSAGE, null, null, "");
		else if (nodeType.intern() == "Attr".intern())
			input = (String) JOptionPane.showInputDialog(atree,
					"Please enter value for the new Attribute", ExaltoConstants.APP_NAME,
					JOptionPane.PLAIN_MESSAGE, null, null, "");

		return input;

	}

	public boolean isLegalXmlName(String input) {
		if (input == null || input.length() == 0)
			return false;
		if (!(XMLRoutines.isLegalXmlName(input))) {
			JOptionPane.showMessageDialog(atree, "Invalid XML name", APP_NAME,
					JOptionPane.WARNING_MESSAGE);
			return false;
		}
		return true;
	}

	// ActionListener methods

	
	public void actionPerformed(ActionEvent ae) {
	/*
		if (ae.getActionCommand() == "Insert Element") {

			// System.out.println(" insert clicked "); 
			try {
				insertNode("Element");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(atree,
						"Cannot add node to text node", APP_NAME,
						JOptionPane.WARNING_MESSAGE);
				return;
			}

		} else if (ae.getActionCommand() == "Delete Element") {
			// System.out.println(" delete clicked "); 
			deleteNode();

		} else if (ae.getActionCommand() == "Rename Element") {

			// System.out.println(" rename clicked "); 
			treeTable.setEditing(true);

			ExaltoXmlNode xnode = getSelectedTreeNode();

			TreeNode[] tn = xnode.getPath();
			TreePath selPath = new TreePath(tn);

			if (expandedPaths != null) {

				int n = expandedPaths.size();
				expandedPaths.add(selPath);
				expandedNodes.put(xnode, new Integer(n));
			}

			System.out
					.println(" table.editCellAt selectedRow = " + selectedRow);
			System.out
					.println(" table.editCellAt selectedCol = " + selectedCol);
			System.out.println(" table.editCellAt(selectedRow, selectedCol) = "
					+ treeTable.editCellAt(selectedRow, selectedCol));

			if (treeTable.editCellAt(selectedRow, selectedCol))
				treeTable.getEditorComponent().requestFocusInWindow();

		} else if (ae.getActionCommand() == "Insert Text") {
			// System.out.println(" insert text clicked "); 
			try {
				insertNode("Text");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(atree,
						"Cannot add node to text node", APP_NAME,
						JOptionPane.WARNING_MESSAGE);
				return;
			}

		} else if (ae.getActionCommand() == "Delete Text") {
			// System.out.println(" Delete Text clicked ");

			if (selectedRow == -1 || selectedCol == -1)
				return;

			
			 // System.out.println(" ttma mousePressed srow " + selectedRow);
			 // System.out.println(" ttma mousePressed srow " + selectedCol);
			 

			ExaltoXmlNode txtNode = gridHelper.getNodeForRowColumn(selectedRow, selectedCol);

			deleteNodeFromJTable(txtNode, true);

		} else if (ae.getActionCommand() == "Insert Attribute") {
			// System.out.println(" Insert Attribute clicked "); 

			try {
				insertNode("Attr");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(atree,
						"Cannot add node to text node", APP_NAME,
						JOptionPane.WARNING_MESSAGE);
				return;
			}

		} else if (ae.getActionCommand() == "Delete Attribute") {

			// System.out.println(" Delete Attribute clicked "); 

			if (selectedRow == -1 || selectedCol == -1)
				return;

			// System.out.println(" ttma mousePressed srow " + selectedRow);
			// System.out.println(" ttma mousePressed srow " + selectedCol);
			 

			ExaltoXmlNode treeNode = getSelectedTreeNode();

			if (treeNode == null) {
				return;
			}

			Node parent = treeNode.getXmlNode();
			if (parent == null)
				return;
			
			ExaltoXmlNode attrNode = gridHelper.getNodeForRowColumn(selectedRow, selectedCol);

			deleteNodeFromJTable(attrNode, true);


		}
	 
		if (ae.getActionCommand() == "Expand All") {
			// System.out.println(" expandAll clicked "); 
			// ExaltoXmlNode treeNode = expandTree(atree);

			updateColumnMapping("NODE_EXPAND");

			expandAllItem.setActionCommand("Collapse All");
			expandAllItem.setLabel("Collapse All");

		} else if (ae.getActionCommand() == "Collapse All") {
			// System.out.println(" Collapse All clicked "); 

			ExaltoXmlNode root = (ExaltoXmlNode) atree.getModel().getRoot();

			((DefaultTreeModel) atree.getModel()).reload();
			ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();

			TreeModelEvent tme = new TreeModelEvent(treeTableModel,
					treeTableModel.getPathToRoot(doc));
			treeTableModel.fireTreeStructureChanged(tme); 
	
			// Necessary to display your new node

			TreeNode[] tn = root.getPath();
			TreePath tp = new TreePath(tn);
			int selRow = atree.getRowForPath(tp);
			updateColumnMapping(null);

			expandAllItem.setActionCommand("Expand All");
			expandAllItem.setLabel("Expand All");

		}
		*/
	}

	/*
	 * private TreeNode deleteNode() {
	 * 
	 * TreeNode treeParent = null;
	 * 
	 * TreePath path = tree.getSelectionPath(); ExaltoXmlNode treeNode =
	 * getSelectedTreeNode(); if (treeNode == null) return null;
	 * 
	 * Node node = treeNode.getXmlNode(); if (node == null) return null;
	 * 
	 * 
	 * int result = JOptionPane.showConfirmDialog( tree, "Delete node "+
	 * node.getNodeName()+" ?", APP_NAME, JOptionPane.YES_NO_OPTION);
	 * 
	 * if (result != JOptionPane.YES_OPTION) return null;
	 * 
	 * try {
	 * 
	 * treeParent = treeNode.getParent(); treeNode.remove();
	 * 
	 * setXmlChanged(true); } catch (Exception ex) { ex.printStackTrace(); }
	 * 
	 * ExaltoXmlNode selNode = getSelectedTreeNode();
	 * 
	 * if (selNode == null) { return null; }
	 * 
	 * TreeModelEvent tme = new TreeModelEvent(treeTableModel,
	 * treeTableModel.getPathToRoot(selNode));
	 * 
	 * int rowCount = tree.getRowCount(); Vector expandedPaths = new Vector();
	 * for(int i=0;i<rowCount;i++) { TreePath treePath = tree.getPathForRow(i);
	 * if(tree.isExpanded(treePath)) { expandedPaths.add(treePath); } }
	 * 
	 * treeTableModel.fireTreeStructureChanged(tme); // Necessary to display
	 * your new node
	 * 
	 * for(int i=0;i<expandedPaths.size();i++) {
	 * tree.expandPath((TreePath)expandedPaths.get(i)); }
	 * 
	 * 
	 * 
	 * TreeNode [] tn = selNode.getPath(); TreePath tp = new TreePath(tn); int
	 * selRow = tree.getRowForPath(tp);
	 * 
	 * updateColumnMapping("NODE_DELETE"); return treeParent; }
	 */

	// OV added for undo/redo begin 03122006
	public TreeNode deleteNode() {

		TreePath path = atree.getSelectionPath();
		ExaltoXmlNode treeNode = getSelectedTreeNode();

		if (treeNode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "node.not.selected"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.not.selected"), 0, ColWidthTypes.ERR));
			return null;
		}

		return deleteNode(treeNode, true, true);

	}

	public TreeNode deleteNode(TreeNode tnode, boolean showDialog,
			boolean refreshTree) {

		TreeNode treeParent = null;
		ExaltoXmlNode treeNode = (ExaltoXmlNode) tnode;
		int index = 0;
		ExaltoXmlNode refChild = null;

		Node node = treeNode.getXmlNode();
		Node domNode = null;
		
		if (node == null)
			return null;

		if (showDialog) {

			int result = JOptionPane.showConfirmDialog(atree, "Delete node "
					+ node.getNodeName() + " ?", APP_NAME,
					JOptionPane.YES_NO_OPTION);

			if (result != JOptionPane.YES_OPTION)
				return null;
		}

		try {

			treeParent = treeNode.getParent();
			index = treeParent.getIndex(treeNode);

			treeNode.remove();

			if(treeParent.getChildCount() > index) {
				refChild = (ExaltoXmlNode) treeParent.getChildAt(index);
				domNode = refChild.getXmlNode(); 
			}
			
			setXmlChanged(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		if (refreshTree)
			treeRefresh(treeParent, treeNode, index, domNode, showDialog);

		return treeParent;

	}

	public void storeTreeExpansionState() {

		
		int rowCount = atree.getRowCount();
		expandedPaths = new Vector();
		for (int i = 0; i < rowCount; i++) {
			TreePath treePath = atree.getPathForRow(i);
			if (atree.isExpanded(treePath) || isRowNodeAdded(i)) {
				expandedPaths.add(treePath);
				expandedRows.add(new Integer(i));
			}
		}

	}

	private boolean isRowNodeAdded(int row) {
		
		int [] rows = atree.getSelectionRows();
		
		if(rows != null) {
			for(int r=0;r<rows.length;r++) 
				if(rows[r] == row)
					return true;
			
		}
		return false;
	}
	
	public void loadTreeExpansionState() {
		
		for (int i = 0; i < expandedPaths.size(); i++) {
			 TreePath path = (TreePath)expandedPaths.get(i);
			 atree.expandPath(path);

			/*
			 * TreeModel model = atree.getModel();
			 * 
			 * TreePath path = (TreePath)expandedPaths.get(i);
			 * 
			 * if(path != null && model != null) //
			 * if(!((TreeNode)path.getLastPathComponent()).isLeaf())
			 * ((JXTreeTable.TreeTableCellRenderer)atree).setExpandedState(path,
			 * true);
			 */

		}

	}

	public void treeRefresh(TreeNode treeParent, TreeNode selNode, int index, Node refNode, boolean showDialog) {

		ExaltoXmlNode pNode = (ExaltoXmlNode) treeParent;

		ExaltoXmlNode treeNode = (ExaltoXmlNode) selNode;

		System.out.println(" tree ref selnode = "
				+ treeNode.getXmlNode().getNodeName());

		int refIndex = pNode.getIndex(treeNode);
		
		storeTreeExpansionState();

		int[] childIndices = new int[1];
		Object[] removedChildren = new TreeNode[1];
		removedChildren[0] = treeNode;
		childIndices[0] = index;

		nodesWereRemoved(treeParent, childIndices, removedChildren);

		loadTreeExpansionState();

		TreeNode[] tn = treeNode.getPath();
		TreePath tp = new TreePath(tn);
		int selRow = atree.getRowForPath(tp);

		updateColumnMapping("NODE_DELETE");

		//hack
		if(showDialog)
			fireUndoableEditHappened(new DeleteElementEdit(this, treeParent, treeNode, refNode, index));
		
	}

	public void treeRefresh(TreeNode[] treeParents, TreeNode[] treeNodes,
			int[] indexes) {

		ExaltoXmlNode selNode = getSelectedTreeNode();

		if (selNode == null) {
			return;
		}

		storeTreeExpansionState();

		for (int i = 0; i < treeParents.length; i++) {

			int[] childIndices = new int[1];
			Object[] removedChildren = new TreeNode[1];
			removedChildren[0] = treeNodes[i];
			childIndices[0] = indexes[i];

			nodesWereRemoved(treeParents[i], childIndices, removedChildren);
		}

		loadTreeExpansionState();

		// TreeNode [] tn = selNode.getPath();
		// TreePath tp = new TreePath(tn);
		// int selRow = atree.getRowForPath(tp);

		// updateColumnMapping("NODE_DELETE");

	}

	// ov added end

	
//OV c 27/05/08 to pass node as parameter instd of selrow	
//	public void refreshTable(int selectedRow, boolean delete) {
	public void deleteNodeFromJTable(TreeNode parentNode, TreeNode attrNode, boolean delete, boolean isUndoRedo) {

		/*
		 * System.out.println(" refreshTable delete = " + delete);
		 * System.out.println(" refreshTable row = " + selectedRow);
		 */
		try {

					ExaltoXmlNode treeNode = this.getSelectedTreeNode();
			
    				System.out.println(" sel  treeNodet= " +  treeNode);

					ExaltoXmlNode exn = (ExaltoXmlNode) attrNode;
					ExaltoXmlNode pnode = (ExaltoXmlNode) parentNode;
					Node nde = exn.getXmlNode();

					int nodeTyp = nde.getNodeType();

					if (nodeTyp == Node.ATTRIBUTE_NODE && pnode == null) {
						JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "grid.select.attr"));
						this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "grid.select.attr"), 0, ColWidthTypes.ERR));
						return;					
					}
					 
					Element parent = null;
					Attr atNode = null;
					boolean isAttr = false;
					if (nodeTyp == Node.TEXT_NODE)
						parent = (Element) nde.getParentNode();
					else if (nodeTyp == Node.ATTRIBUTE_NODE) {
						atNode = (Attr) nde;
						parent = (Element) pnode.getXmlNode();
						isAttr = true;
					}

					/*
					 * System.out.println(" reftable parent " + parent);
					 */


					/* OV added 25/03/08 */
					if (delete) {
						if (!isAttr) {
							exn.remove(false);
						} else {
							
							parent.removeAttribute(atNode.getNodeName());
					
						}
					}
					// OV end
					
					
					String ntype = (nodeTyp == 2)? "Attr" : "Text";

					// This method handles refreshing JTable UI and updates column mapping
					// and restores tree state
					tableRefresh(delete, ntype, treeNode, exn, isUndoRedo);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void tableRefresh(boolean delete, String nodeType, ExaltoXmlNode treeNode, ExaltoXmlNode nodeElement, boolean isUndoRedo) {

		ExaltoXmlNode selNode = (ExaltoXmlNode) nodeForRow(selectedRow);
		
		  System.out.println(" selNode nodeForRow = " +selNode);
		 

		if (selNode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "grid.select.attr"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "grid.select.attr"), 0, ColWidthTypes.ERR));
			return;					
		}

//		this.storeTreeExpansionState();

		fireTableDataChanged();

		
//		this.loadTreeExpansionState();

		String mode = "";
		if (delete)
			mode = "NODE_DELETE";

		updateColumnMapping(mode);

		if(!isUndoRedo)
			fireUndoableEditHappened(new DeleteAttrEdit(this, treeNode, nodeElement, nodeType)); 

	}
	
	   public void setValueAt(Object value, int row, int column) {
	        
	        
	       // System.out.println(" setting value = " + value);
	       // System.out.println(" row  = " + row);
	       // System.out.println(" col  = " + column);
	        
	        
	        try {
	            
	            String newValue = (String) value;
	            
	            cedit = new CompoundEdit();
	/*
	     Object node = nodeForRow(row);
	 
	            if(node instanceof ExaltoXmlNode) {
	 
	                  ExaltoXmlNode xnode = (ExaltoXmlNode) node;
	 
	           System.out.println(" xnode  = " + xnode.toString());
	           System.out.println(" xnode  type =  " +
	xnode.getNodeType());
	 
	               int type = xnode.getNodeType();
	 */
	            
	            //  DomToTreeModel domToTreeModel =  null;
	            
	            ExaltoXmlNode xnode = gridHelper.getNodeForRowColumn(row, column);
	            
	            if(xnode != null) {
	                
	                org.w3c.dom.Node domNode = xnode.getXmlNode();
	                int type = xnode.getNodeType();
	                
	                if(type == 3) {
	                    domNode.setNodeValue((String) value);
                        setXmlChanged(true);
	                    TreeNode pnode = (TreeNode) this.nodeForRow(row);
	                    deleteNodeFromJTable(pnode, xnode, false, false); // node, delete, isUndoredo
	                } else if(type == 2) {
	                    Attr attrNode =(Attr) domNode;
	                    int eqpos = newValue.indexOf("=");
	                    if(eqpos > 0) {
		                    TreeNode pnode = (TreeNode) this.nodeForRow(row);
	                        String attrval = newValue.substring(eqpos+1);
	                        attrNode.setValue(attrval);
	                        deleteNodeFromJTable(pnode, xnode, false, false);  // node, delete, isUndoredo
                            setXmlChanged(true);
	                    }
	                } else if(type == 1) {
	                    
	                  //  System.out.println(" SVA type = " + type);
	                    
	                    if(isEditing()) {
	                        
	                     //   System.out.println(" in isediting ");
	                        
	                        //OV changed for undo/redo 03122006
	                        //  ArrayList deleted = deleteNode(xnode);
	                        
	                    	storeTreeExpansionState();
	                    	
	                    	ArrayList deleted = deleteNodeForRename(xnode);
	                        
	                        /*        System.out.println(" ArrayList deleted " + deleted); */
	                        
	                        TreeNode parent = null;
	                        int index = 0;
	                        int refCount = 0;
	                        if(deleted != null) {
	                            
	                            if(deleted.size() > 2) {
	                                parent = (TreeNode) deleted.get(0);
	                                index = ((Integer)deleted.get(1)).intValue();
	                                refCount = ((Integer)deleted.get(2)).intValue();
	                            }
	                            
	                        } else {
	                            
	                            // 		domToTreeModel = (DomToTreeModel) treeTableModel;
	                            
	                          //  System.out.println(" in not isediting ");
	                            
	                            
	                            org.w3c.dom.Document domDoc = treeTableModel.getDocument();
	                            Source xmlSource = new javax.xml.transform.dom.DOMSource(domDoc);
	                            
	                            /* Create a handler to handle the SAX events */
	                            ExaltoHandler handler = new ExaltoHandler(newValue);
	                            
	                            try {
	                                /* Prepare the result */
	                                SAXResult result = new SAXResult(handler);
	                                
	                                /* Create a transformer */
	                                Transformer xformer = TransformerFactory.newInstance().newTransformer();
	                                
	                                /* Traverse the DOM tree */
	                                xformer.transform(xmlSource, result);
	                                
	                                org.w3c.dom.Document newdoc = handler.getRoot();
	                                
	        /*
	                           OutputStreamWriter outWriter =
	                          new OutputStreamWriter(System.out, outputEncoding);
	                     out = new PrintWriter(outWriter, true);
	                           echo(newdoc);
	         */
	                                treeTableModel.setDocument(newdoc);
	                                treeTableModel.update();
	                                
	                                ExaltoXmlNode root = (ExaltoXmlNode)atree.getModel().getRoot();
	                                
	                                ((DefaultTreeModel)atree.getModel()).reload();
	                                ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();
	                                
	                                TreeModelEvent tme = new TreeModelEvent(treeTableModel,
	                                        getPathToRoot(doc));
	                                
	                                int rowCount = atree.getRowCount();
	                                Vector expandedPaths = new Vector();
	                                for(int i=0;i<rowCount;i++) {
	                                    TreePath treePath = atree.getPathForRow(i);
	                                    if(atree.isExpanded(treePath)) {
	                                        expandedPaths.add(treePath);
	                                    }
	                                }
	                                
	                                treeTableModel.fireTreeStructureChanged(tme); 
	                                /*	Necessary to display your new node */
	                                
	                                for(int i=0;i<expandedPaths.size();i++) {
	                                    
	                                    atree.expandPath((TreePath)expandedPaths.get(i));
	                                }
	                                
	                                TreeNode [] tn = root.getPath();
	                                TreePath tp = new TreePath(tn);
	                                int selRow = atree.getRowForPath(tp);
	                                updateColumnMapping(null);
	                                
	                                setXmlChanged(true);
	                                
	                                setEditing(false);
	                                
	                            } catch (TransformerConfigurationException e) {
	                                e.printStackTrace();
	                            } catch (TransformerException te) {
	                                te.printStackTrace();
	                            }
	                            
	                            return;
	                            
	                        }
	                        
	                      //  System.out.println(" deleted index " + index);
	                      //  System.out.println(" parent of deleted " + parent);
	                      //  System.out.println(" refCount " + refCount);
	                        
	                        
	                        String newval = (String) value;
	                        
	                        ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();
	                        Node child =
	                                ((org.w3c.dom.Document)treeTableModel.getDocument()).createElement(newval);
	                        
	                        ExaltoXmlNode nodeElement = new ExaltoXmlNode(child);
	                        
	                      //  System.out.println(" no of children in deleted  " + (deleted.size()-2));
	                        int m = 0;
	                        Node refChild = null;
	                        for(int j=3;j<3+refCount;j++) {
	                            if(m == index) {
	                                refChild = (Node) deleted.get(j);
	                            }
	                            
	                            m++;
	                        }
	                        	                        
	                        //  System.out.println(" m = " + m);
	                        int n = m+3;
	                        
	    //                    n = 6
	                        int childCount = ((Integer)deleted.get(n++)).intValue();
	                        
	                      //  System.out.println(" childCount = " + childCount);
	                        
	                        // n = 7
	                        int p=n;
	                        // p = 7
	                      //  System.out.println(" p = " + p);
	                        
	                        
	                        ExaltoXmlNode [] exnodes = new ExaltoXmlNode[childCount];
	                        int x=0;
	                        if(childCount > 0) {
	                            
	                            for(;n<p+childCount;n++) {
	                                
	                          //  	if(xnode.hasElementChildren()) {
	                                    
	                                    System.out.println(" adding cnodes for n = " + n);
	                                    ExaltoXmlNode enode = (ExaltoXmlNode) deleted.get(n);
	                                    
	                                    System.out.println(" adding cnode = " + enode);
	                                    nodeElement.addXmlNode(enode);

	                                    // public InsertElementEdit(UndoableModel model, TreeNode parent, TreeNode element, Node refNode, int refIndex){

	    //                              InsertElementEdit insEdit = new InsertElementEdit(this, nodeElement, enode, null, 0);

	                                    exnodes[x++] = enode;
	              
	                                    	       	                         
	     //                             cedit.addEdit(insEdit);
 
	                                    
	                          //      } else {
	                          //          Node enode = (Node) deleted.get(n);
	                          //          child.appendChild(enode);
	                          //      }
	                    
	                            
	                            }
	                        } 
	                        
	                        // n = 8;
	                        
	                        int cc = ((Integer)deleted.get(n++)).intValue();
	                       
	                        // n = 9;
	                        int s=n;
	                        
	                        
	                        // s = 9;
	                        
	                        
	                        if(cc > 0) {
	                            
	                            for(;n<s+cc;n++) {
	                                   Node enode = (Node) deleted.get(n);
	                                   child.appendChild(enode);

	       //                             InsertTextEdit insEdit = new InsertTextEdit(this, nodeElement, enode);

	       //                             cedit.addEdit(insEdit);
	                              	 
	                            }
	                        } 
	                        
	                        
	                        int r = n;
	                        int attrCount = ((Integer)deleted.get(n++)).intValue();
	                        
	                        if(attrCount > 0) {
	                            
	                            for(;n<r+attrCount+1;n++) {
	                                ExaltoXmlNode enode = (ExaltoXmlNode) deleted.get(n);
	                                Attr anode = (Attr) enode.getXmlNode();
	                                nodeElement.addAttrNode(enode, false);

	 //                               InsertAttrEdit insEdit = new InsertAttrEdit(this, nodeElement, enode);
	 //                               cedit.addEdit(insEdit);
	                           	 
	                            
	                            }
	                            
	                        }
	                        
	                        /*    Vector expandedPaths = (Vector)deleted.get(p+m); */
	        
	                        ExaltoXmlNode [] oldNodes = exfrag.getClonedFrom();
	                        
	                        insertNodeInParent(parent, nodeElement, oldNodes[0], exnodes, "Element", newval,  refChild, index);
	                        
	                    /*    
	                        ExaltoXmlNode [] cnodes = exfrag.getClonedFrom();
	                        
	                        ExaltoXmlNode cnode = cnodes[0];
	                        
	                        for(int d=0;d<exnodes.length;d++) {
	                        	InsertElementEdit insEdit2 = new InsertElementEdit(this, cnode, exnodes[d], null, 0);
	                        	cedit.addEdit(insEdit2);
	                        }
	                    */    
	                        
	                        int [] cind = new int[1];
	                        cind[0] = parent.getChildCount();
	                        Node [] celem = new Node[1];
	                        celem[0] = child;
	                        
	                        
	                        
	                        TreeModelEvent tme = new TreeModelEvent(this,
	                                getPathToRoot(parent),
	                                cind, celem);
	                        
	                        
	                        treeTableModel.fireTreeStructureChanged(tme);
	                        /* Necessary to display your new node */
	                        
	                        
	                        this.loadTreeExpansionState();
	                        
	                        
	                   /*     
	                        TreeNode [] tna = nodeElement.getPath();
	                        TreePath newPath = new TreePath(tna);
	                        
	                        
	                        if(expandedNodes != null && expandedPaths != null) {
	                            
	                          //  System.out.println(" expandedNodes " + expandedNodes);
	                            
	                            int ind = 0;
	                            boolean isPresent = false;
	                            if(expandedNodes != null && expandedNodes.containsKey(xnode)) {
	                                ind = ((Integer)expandedNodes.get(xnode)).intValue();
	                                isPresent = true;
	                            }
	                            
	                            if(isPresent) {
	                                expandedPaths.set(ind, newPath);
	                                expandedPaths = gridHelper.updatePaths(expandedPaths,
	                                        expandedNodes, nodeElement, ind);
	                            }
	                            
	                            for(int i=0;i<expandedPaths.size();i++) {
	                                atree.expandPath((TreePath)expandedPaths.get(i));
	                            }
	                            
	                        }
	                     
	                    */    
	                        
	                        /*    System.out.println(" newPath = " + newPath); */
	                        
	                        /*    atree.expandPath(newPath); */
	                        
	                        ExaltoXmlNode enode = (ExaltoXmlNode) parent;
	                        
	                        TreeNode [] tn = enode.getPath();
	                        TreePath tp = new TreePath(tn);
	                        int selRow = atree.getRowForPath(tp);
	                        
	                        /*    System.out.println(" selRow = " + selRow); */
	                        
	                        updateColumnMapping("NODE_ADD");
	                        
	                        TreePath path = atree.getSelectionPath();
	                        
	                        if (path != null) {
	                            path = path.pathByAddingChild(nodeElement);
	                            atree.setSelectionPath(path);
	                            atree.scrollPathToVisible(path);
	                        }
	                        
	                        cedit.end();
	                        this.fireUndoableEditHappened(cedit);
	                        
	                        setXmlChanged(true);
	                    }
	                    
	                    setEditing(false);
                       
	                }
	                
	            }    /* xnode != null */
	            
	            
	        } catch(Exception e) {
	            e.printStackTrace();
	        }
	        
	        /*    treeTableModel.setValueAt(value, nodeForRow(row),
	column); */
	        
	    }
	 
	
	
	    public Vector insertNodeInParent(TreeNode parent, TreeNode
	    		newNode, TreeNode oldNode, TreeNode [] childNodes, String nodeType, String newValue, Node refChild, int
	            index)  {
	        
	        Vector expandedPaths = new Vector();
	        
	        ExaltoXmlNode treeNode = (ExaltoXmlNode) parent;

	        ExaltoXmlNode nodeElement = (ExaltoXmlNode) newNode;
	        
	        if (treeNode == null) {
	            return null;
	        }
	        
	        if(treeNode.getNodeType() == 3) {
				this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.add.text"), 0, ColWidthTypes.ERR));

//	        	throw new Exception("Cannot add node to text node");
	        }
	        
	        Node domNode = treeNode.getXmlNode();
	        if (domNode == null)
	            return null;
	        
	        if(domNode.getNodeType() == Node.DOCUMENT_NODE) {
	        	this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "document.node.insert"), 0, ColWidthTypes.ERR));
	            return null;
	        }
	        
	        
	        if (nodeType.intern() == "Element".intern() && !isLegalXmlName(newValue)) {
				this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "name.illegal"), 0, ColWidthTypes.ERR));
	            return null;
	        }
	        
	        try {	            

	        		treeNode.insertXmlNode(nodeElement, refChild, index,  !(treeNode.getNodeType() == Node.TEXT_NODE));
	     
	        		uedit2 = new RenameUndoableEdit(this, treeNode, nodeElement, oldNode, childNodes, "Element", refChild, index);
	        		
	        		cedit.addEdit(uedit2);
	        		
	        		uedit1.setNewElement(nodeElement);
	        		
	        		cedit.addEdit(uedit1);
	             
	          	 
	            setXmlChanged(true);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        
	        return expandedPaths;
	        
	    }
	    
	    ExaltoXmlFragment exfrag;
	    
	//OV renamed for undo/redo 031206
	// private ArrayList deleteNode(ExaltoXmlNode treeNode) {
    private ArrayList deleteNodeForRename(ExaltoXmlNode treeNode) {
	        
	/*     System.out.println("in del node arrlist enter treeNode = " +
	treeNode); */
	        
	    	exfrag = new ExaltoXmlFragment(treeTableModel.getDocument().createDocumentFragment());
	    	
	 //   	exfrag.appendChild(new ExaltoXmlNode(treeNode.getXmlNode()));
	    	ExaltoXmlNode [] xnode = new ExaltoXmlNode[1];
	    	xnode[0] = treeNode;
	    	exfrag.setClonedFrom(xnode);
	    	
	        ArrayList children = new ArrayList();
	        TreeNode treeParent = null;
	        
	        if (treeNode == null) {
	            /*  System.out.println("in del node arrlist ret null"); */
	            return null;
	        }
	        
	        Node node = treeNode.getXmlNode();
	        if (node == null) {
	            /*  System.out.println("in del node arrlist ret xml null"); */
	            return null;
	        }
	        
	        NodeList childNodes = null;
	        int index = 0;
	        ExaltoXmlNode pnode = null;
	        
	        try {
	            
	            treeParent = treeNode.getParent();
	            
	            if(treeParent == null) {
	                return null;
	            }
	            
	            pnode = (ExaltoXmlNode) treeParent;
	            index = pnode.getIndex(treeNode);
	            
	            children.add(treeParent);
	            children.add(new Integer(index));

	            // refChild not used. only index is used
	            Node refNode = null; 
	            ExaltoXmlNode refChild = null;
	            if(index != 0 && treeParent.getChildCount() > index) {
					refChild = (ExaltoXmlNode) treeParent.getChildAt(index);
					refNode = refChild.getXmlNode(); 					
				}

/*	            
	            ExaltoXmlNode refChild = (ExaltoXmlNode) treeNode.getNextSibling();
	            
	            if(refChild != null)
	            	refNode = refChild.getXmlNode();
*/	            
	            	            
	            treeNode.remove();
	            
	            childNodes = pnode.getXmlNode().getChildNodes();
	            
	            children.add(new Integer(childNodes.getLength()));
	            
	            
	            for(int g=0;g<childNodes.getLength();g++) {
	                Node cnode = childNodes.item(g);
	                System.out.println("  INIP cnode " + cnode.getNodeName());
	                children.add(cnode);
	            }
	            
	  /*          
	            if(!treeNode.hasElementChildren()) {
	                
	                childNodes = node.getChildNodes();
	                
	                children.add(new Integer(childNodes.getLength()));
	                for(int g=0;g<childNodes.getLength();g++) {
	                    Node cnode = childNodes.item(g);
	                    children.add(cnode);
	                }
	                
	                addAttributes(treeNode, children);
	                
	            } else  {
	 */               
	            

	            	// adding element children
	            	int count = treeNode.getChildCount();
	                children.add(new Integer(count));
	                Enumeration en = treeNode.children();

	                while(en.hasMoreElements()) {
	                    ExaltoXmlNode cnode = (ExaltoXmlNode) en.nextElement();
	                    children.add(cnode);
	                }

	            	// adding text children
	            	childNodes = treeNode.getXmlNode().getChildNodes();

	            	Vector nodeVec = new Vector();
	            	int cc=0;
	                for(int p=0;p<childNodes.getLength();p++) {
	                	Node cnode = childNodes.item(p);
		                System.out.println("  INIP cnode " + cnode.getNodeName());
		                if(cnode.getNodeType() == Node.TEXT_NODE) {
		                	cc++;
		                	nodeVec.add(cnode);
		                }
	                }

	            	children.add(new Integer(cc));

	                for(int p=0;p<nodeVec.size();p++) {
	                	Node cnode = (Node) nodeVec.get(p);
		                System.out.println("  INIP cnode " + cnode.getNodeName());
		                	children.add(cnode);
		            }

	                addAttributes(treeNode, children);
	                
	   //         }
	                
	                uedit1 = new DeleteElementEdit(this, treeParent, treeNode, refNode, index);
	                
	 //               cedit.addEdit(delEdit);	                
	            
	            setXmlChanged(true);
	            
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        }
	        
	      //  System.out.println("  deleted size " + children.size());
	        
	      //  for(int i=0;i<children.size();i++) {
	      //      System.out.println(" c[ " + i + "] = " + children.get(i));
	      //  }
	        
	        
	        return children;
	    }
	    

	   private ArrayList addAttributes(ExaltoXmlNode treeNode, ArrayList children) {

		Node domNode = treeNode.getXmlNode();
		NamedNodeMap nmp = domNode.getAttributes();

		if (nmp != null) {
			children.add(new Integer(nmp.getLength()));
			for (int t = 0; t < nmp.getLength(); t++) {
				Attr attr = (Attr) nmp.item(t);
				ExaltoXmlNode attrNode = new ExaltoXmlNode(attr);
				children.add(attrNode);
			}
		} else
			children.add(new Integer(0));

		return children;

	}

	public void treeStartEdit(int col, EventObject e) {

		ExaltoXmlNode node = (ExaltoXmlNode) atree
				.getLastSelectedPathComponent();

		// System.out.println(" in startedit node = " + node);

		if (node == null)
			return;

		atree.startEditingAtPath(new TreePath(node.getPath()));
		// System.out.println(" new value " + node);

	}

	public XmlTreeModel getModel() {
		return treeTableModel;
	}

	// ov added for popupmenu
	public void insertElementClicked(java.awt.event.ActionEvent evt) {
		try {
			insertNode("Element");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertAttrClicked(java.awt.event.ActionEvent evt) {
		try {
			insertNode("Attr");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void insertTextClicked(java.awt.event.ActionEvent evt) {
		try {
			insertNode("Text");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteElementClicked(java.awt.event.ActionEvent evt) {
		deleteNode();
	}

	public void deleteAttrClicked(java.awt.event.ActionEvent evt) {
		/* System.out.println(" Delete Attribute clicked "); */

		deleteAttr();
		
	
	}

	public void deleteTextClicked(java.awt.event.ActionEvent evt) {
		/* System.out.println(" Delete Text clicked "); */

		deleteText();
		
	}

	public void deleteAttr() {
		
		if (selectedRow == -1 || selectedCol == -1) {
			return;
		}
		
		// System.out.println(" ttma mousePressed srow " + selectedRow);
		// System.out.println(" ttma mousePressed srow " + selectedCol);

		ExaltoXmlNode treeNode = getSelectedTreeNode();

		if (treeNode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "node.not.selected"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.not.selected"), 0, ColWidthTypes.ERR));
			return;
		}

		if (treeNode.getNodeType() != 1) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "grid.select.attr"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "grid.select.attr"), 0, ColWidthTypes.ERR));
			return;
		}

		/* System.out.println(" treenode name " + treeNode); */
	//	Node parent = treeNode.getXmlNode();
	//	if (parent == null)
	//		return;
		
		ExaltoXmlNode attrNode = gridHelper.getNodeForRowColumn(selectedRow, selectedCol);

		deleteNodeFromJTable(treeNode, attrNode, true, false);
	
	}

	public void deleteText() {
		if (selectedRow == -1 || selectedCol == -1)
			return;

		
		ExaltoXmlNode textNode = gridHelper.getNodeForRowColumn(this.selectedRow, this.selectedCol); 
			
		if (textNode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "node.not.selected"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.not.selected"), 0, ColWidthTypes.ERR));
			return;
		}

		if (textNode.getNodeType() != 3) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "grid.select.text"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "grid.select.text"), 0, ColWidthTypes.ERR));
			return;
		}

		/*
		 * System.out.println(" ttma mousePressed srow " + selectedRow);
		 * System.out.println(" ttma mousePressed srow " + selectedCol);
		 */
		deleteNodeFromJTable(null, textNode, true, false);
		
	}

	
	public void renameElemClicked(java.awt.event.ActionEvent evt) {

		renameNode();

	}

	public void renameNode() {

		/* System.out.println(" rename clicked "); */
		setEditing(true);

		ExaltoXmlNode xnode = getSelectedTreeNode();

		if (xnode == null) {
			JOptionPane.showMessageDialog(table, ExaltoResource.getString("err", "node.not.selected"));
			this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString("err", "node.not.selected"), 0, ColWidthTypes.ERR));
			return;
		}
		
		TreeNode[] tn = xnode.getPath();
		TreePath selPath = new TreePath(tn);

		if (expandedPaths != null) {
			int n = expandedPaths.size();
			expandedPaths.add(selPath);
			expandedNodes.put(xnode, new Integer(n));
		}

		// System.out.println(" table.editCellAt selectedRow = " + selectedRow);
		// System.out.println(" table.editCellAt selectedCol = " + selectedCol);
		// System.out.println(" table.editCellAt(selectedRow, selectedCol) = " +
		// treeTable.editCellAt(selectedRow, selectedCol));

		if (treeTable.editCellAt(selectedRow, selectedCol))
			treeTable.getEditorComponent().requestFocusInWindow();
		
	}

	public void expandAllClicked(java.awt.event.ActionEvent ae) {
		
		String actionType = ae.getActionCommand();
		expandAll(actionType);
		
	}

	public void expandAll(String actionType) {
		/* System.out.println(" expandAll clicked "); */
		// ExaltoXmlNode treeNode = expandTree(atree);
		if (actionType.equals("Expand All")) {
			/* System.out.println(" expandAll clicked "); */
	//		 ExaltoXmlNode treeNode = expandTree(atree);
			/*
			 * if (treeNode == null) { return; }
			 * 
			 * 
			 * TreeNode [] tn = treeNode.getPath(); TreePath tp = new
			 * TreePath(tn); int selRow = atree.getRowForPath(tp);
			 */

            expandAll(true);

			updateColumnMapping("NODE_EXPAND");

			expandAllItem.setActionCommand("Collapse All");
			expandAllItem.setLabel("Collapse All");

		} else if (actionType.equals("Collapse All")) {
			/* System.out.println(" Collapse All clicked "); */

			ExaltoXmlNode root = (ExaltoXmlNode) atree.getModel().getRoot();

			((DefaultTreeModel) atree.getModel()).reload();
			ExaltoXmlNode doc = (ExaltoXmlNode) treeTableModel.getRoot();

			TreeModelEvent tme = new TreeModelEvent(treeTableModel,
					treeTableModel.getPathToRoot(doc));
			treeTableModel.fireTreeStructureChanged(tme); 
	
			// Necessary to display your new node

			TreeNode[] tn = root.getPath();
			TreePath tp = new TreePath(tn);
			int selRow = atree.getRowForPath(tp);
			updateColumnMapping(null);

			expandAllItem.setActionCommand("Expand All");
			expandAllItem.setLabel("Expand All");

		}
		
	}

    // If expand is true, expands all nodes in the tree.
    // Otherwise, collapses all nodes in the tree.
    public void expandAll(boolean expand) {
        TreeNode root = (TreeNode) atree.getModel().getRoot();

        // Traverse tree from root
        expandAll(new TreePath(root), expand);
    }
    
    private void expandAll(TreePath parent, boolean expand) {
        // Traverse children
        TreeNode node = (TreeNode)parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e=node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode)e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(path, expand);
            }
        }

        // Expansion or collapse must be done bottom-up
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }

    }
    
	public int getSelectedRow() {
		return selectedRow;
	}


	// OV a 24/03/08 for clipboard ops
	public void cut(java.awt.event.ActionEvent evt) {

		if (selectedRow == -1 || selectedCol == -1)
			return;

		ExaltoXmlNode[] treeNodes = getSelectedTreeNodes();

		if(selectedCol == 0) {
		
			Transferable t = gridHelper.getTransferable(treeNodes);
			
			treeClipboard.setContents(t, this);

			System.out.println(" Clipboard Name = " + treeClipboard.getName());

			TreeNode[] treeParents = new TreeNode[treeNodes.length];
			int[] indices = new int[treeNodes.length];

			CompoundEdit bigedit = new CompoundEdit();
			CutUndoableEdit cutEdit = null;
			for (int p = 0; p < treeNodes.length; p++) {
	
				ExaltoXmlNode treeNode = treeNodes[p];
				Node node = treeNode.getXmlNode();
				if (node == null)
					return;
	
				try {
	
					treeParents[p] = treeNode.getParent();
					indices[p] = treeParents[p].getIndex(treeNode);
			//		int[] where = gridHelper.getLocationPathForNode(treeNode);
	
	//				cutEdit = new CutUndoableEdit(this, treeParents[p], treeNode, where);
					
					gridHelper.deleteNodeUndoable(treeNode);
					
	//				bigedit.addEdit(cutEdit);
	
				} catch (Exception ex) {
					ex.printStackTrace();
				}
	
			}

			bigedit.end();
	//		fireUndoableEditHappened(bigedit); // todo
		}
		else {
			
			ExaltoXmlNode gNode = gridHelper.getNodeForRowColumn(this.selectedRow, this.selectedCol);
			
			Transferable t = gridHelper.getTransferable(gNode);
			
			treeClipboard.setContents(t, this);

			System.out.println(" Clipboard Name = " + treeClipboard.getName());

	//		CutUndoableEdit cutEdit =  
	//			new CutUndoableEdit(this, treeParents[p], treeNode,
	//						where);

			ExaltoXmlNode tnode = null;
			if(treeNodes != null && treeNodes.length > 0)
				tnode =  treeNodes[0];
			
			deleteNodeFromJTable(tnode, gNode, true, false); // // node, delete, isUndoredo
 	//			fireUndoableEditHappened(cutEdit); 
				
				
		}
		// treeRefresh(treeParents, treeNodes, indices);

	}

	public void copy(java.awt.event.ActionEvent evt) {



	}

	public void paste(java.awt.event.ActionEvent evt) {

		// when pasting we just insert the node. we leave it up to copy or cut
		// to handle any deep copying that needs to be done
		// boolean paste = true;
		String pasteMode = null;
		
		JMenuItem jmi = (JMenuItem) evt.getSource();
		
		if(jmi.getActionCommand().equals("paste into")) 
			pasteMode = "into";
		else if(jmi.getActionCommand().equals("paste before")) 
			pasteMode = "before";
		else if(jmi.getActionCommand().equals("paste after")) 
			pasteMode = "after";

		
		
		System.out.println(" Getting contents from clipboard = "
				+ treeClipboard.getName());

		Object o = treeClipboard.getContents(this);

	try {

			ExaltoXmlFragment frag = null;
			if (o instanceof ExaltoXmlFragment)
				frag = (ExaltoXmlFragment) o;

			gridHelper.importFragment(selectedRow, frag, true, pasteMode);

		} catch (MissingResourceException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Notification that we lost ownership of a clipboard item
	 * 
	 * @param c
	 *            Description of the Parameter
	 * @param t
	 *            Description of the Parameter
	 */
	public void lostOwnership(Clipboard c, Transferable t) {

		System.out.println(" ALERT ALERT lost ownership of contents name = "
				+ treeClipboard.getName());

	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {

		jPopupMenu1 = new javax.swing.JPopupMenu();
		
		insertMenu = new javax.swing.JMenu();
		insElemItem = new javax.swing.JMenuItem();
		insAttrItem = new javax.swing.JMenuItem();
		insTextItem = new javax.swing.JMenuItem();
		deleteMenu = new javax.swing.JMenu();
		delElemItem = new javax.swing.JMenuItem();
		delAttrItem = new javax.swing.JMenuItem();
		delTextItem = new javax.swing.JMenuItem();
		renameMenu = new javax.swing.JMenuItem();
		jSeparator1 = new javax.swing.JSeparator();
		/*
		 * cutItem = new javax.swing.JMenuItem(cutAction); copyItem = new
		 * javax.swing.JMenuItem(copyAction); pasteItem = new
		 * javax.swing.JMenuItem(pasteAction); undoItem = new
		 * javax.swing.JMenuItem(undoAction); redoItem = new
		 * javax.swing.JMenuItem(redoAction);
		 */
		cutItem = new javax.swing.JMenuItem();
		copyItem = new javax.swing.JMenuItem();
		
		pasteMenu = new javax.swing.JMenu();

		pasteMenu.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.paste"));

		pasteIntoItem = new javax.swing.JMenuItem();
		pasteBeforeItem = new javax.swing.JMenuItem();
		pasteAfterItem = new javax.swing.JMenuItem();
		
		undoItem = new javax.swing.JMenuItem();
		redoItem = new javax.swing.JMenuItem();
		jSeparator2 = new javax.swing.JSeparator();
		expandAllItem = new javax.swing.JMenuItem();

		insertMenu.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.insert"));
		insElemItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		insElemItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.insert.element.mnemonic")
				.charAt(0));
		insElemItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.insert.element"));

	/*
	 * 
	 	insElemItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				insertElementClicked(evt);
			}
		});
	*/	
		insElemItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.insert.element"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							insertElementClicked(e);
						}
				});
				


		System.out.println(" gridCommands.put(INS_ELEM " + insElemItem.getAction());
		System.out.println(" gridCommands.put(INS_ATTR " + insElemItem.getAction());
		System.out.println(" gridCommands.put(INS_TEXT " + insElemItem.getAction());
				

		gridCommands.put("INS_ELEM", insElemItem);
		
		insertMenu.add(insElemItem);

		insAttrItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		insAttrItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.delete.element.mnemonic")
				.charAt(0));
		insAttrItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.insert.attr"));
	/*	
		insAttrItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				insertAttrClicked(evt);
			}
		});
	 */
		
		insAttrItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.insert.attr"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							insertAttrClicked(e);
						}
				});
		
		gridCommands.put("INS_ATTR", insAttrItem.getAction());
		
		insertMenu.add(insAttrItem);

		insTextItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		insTextItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.rename.mnemonic").charAt(0));
		insTextItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.insert.text"));
	/*	
		insTextItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			    
				insertTextClicked(evt);
			}
		});
     */
		
		insTextItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.insert.text"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							insertTextClicked(e);
						}
				});
		
		gridCommands.put("INS_TEXT", insTextItem.getAction());
		insertMenu.add(insTextItem);

		jPopupMenu1.add(insertMenu);

		deleteMenu.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.delete"));
		delElemItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E,
				java.awt.event.InputEvent.ALT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		delElemItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.delete.element.mnemonic")
				.charAt(0));
		delElemItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.delete.element"));
		delElemItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteElementClicked(evt);
			}
		});

		delElemItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.delete.element"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							deleteElementClicked(e);
						}
				});

		
		gridCommands.put("DEL_TEXT", delElemItem.getAction());
		deleteMenu.add(delElemItem);

		delAttrItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_A,
				java.awt.event.InputEvent.ALT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		delAttrItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.delete.attr.mnemonic")
				.charAt(0));
		delAttrItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.delete.attr"));
	/*	
		delAttrItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteAttrClicked(evt);
			}
		});
	*/
		
		delElemItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.delete.attr"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							deleteAttrClicked(e);
						}
				});

		gridCommands.put("DEL_ATTR", delAttrItem.getAction());
		deleteMenu.add(delAttrItem);

		delTextItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_T,
				java.awt.event.InputEvent.ALT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		delTextItem.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.delete.text.mnemonic")
				.charAt(0));
		delTextItem.setText(java.util.ResourceBundle
				.getBundle("exalto/xmlgrid").getString("grid.delete.text"));
		/*
		delTextItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				deleteTextClicked(evt);
			}
		});
		*/
		delTextItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.delete.text"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							deleteTextClicked(e);
						}
				});

		gridCommands.put("DEL_TEXT", delTextItem.getAction());
		deleteMenu.add(delTextItem);

		jPopupMenu1.add(deleteMenu);

		renameMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_R,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		renameMenu.setMnemonic(java.util.ResourceBundle.getBundle(
				"exalto/xmlgrid").getString("grid.rename.mnemonic").charAt(0));
		renameMenu.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.rename"));
		/*
		renameMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				renameElemClicked(evt);
			}
		});
		*/
		renameMenu.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.rename"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							renameElemClicked(e);
						}
				});

		
		gridCommands.put("REN_ELEM", renameMenu.getAction());
		
		jPopupMenu1.add(renameMenu);

		jPopupMenu1.add(jSeparator1);

		cutItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
		cutItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.cut"));
		/*
		cutItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cut(evt);
			}
		});
		*/
		cutItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.cut"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							cut(e);
						}
				});

		gridCommands.put("CUT_ITEM", cutItem.getAction());

		jPopupMenu1.add(cutItem);

		copyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_C,
				java.awt.event.InputEvent.CTRL_MASK));
		copyItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.copy"));
		copyItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_X,
				java.awt.event.InputEvent.CTRL_MASK));
		copyItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.copy"));
		/*
		copyItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				copy(evt);
			}
		});	
		*/
		copyItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.copy"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							copy(e);
						}
				});

		gridCommands.put("COPY_ITEM", copyItem.getAction());
		jPopupMenu1.add(copyItem);

		pasteIntoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V,
				java.awt.event.InputEvent.CTRL_MASK));
		pasteIntoItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.paste.into"));
		/*
		pasteIntoItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				paste(evt);
			}
		});
		*/
		pasteIntoItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.paste.into"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							paste(e);
						}
				});

		pasteBeforeItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V,
				java.awt.event.InputEvent.CTRL_MASK));
		pasteBeforeItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.paste.before"));
		/*
		pasteBeforeItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				paste(evt);
			}
		});
		*/
		pasteBeforeItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.paste.before"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							paste(e);
						}
				});

		pasteAfterItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_V,
				java.awt.event.InputEvent.CTRL_MASK));
		pasteAfterItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.paste.after"));
		/*
		pasteAfterItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				paste(evt);
			}
		});
		*/
		pasteAfterItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.paste.after"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							paste(e);
						}
				});

		jPopupMenu1.add(pasteMenu);
		pasteMenu.add(pasteIntoItem);
		pasteMenu.add(pasteBeforeItem);
		pasteMenu.add(pasteAfterItem);

		gridCommands.put("PASTE_INTO", pasteIntoItem.getAction());
		gridCommands.put("PASTE_BEFORE", pasteBeforeItem.getAction());
		gridCommands.put("PASTE_AFTER", pasteAfterItem.getAction());

		jPopupMenu1.add(jSeparator2);

		undoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Z,
				java.awt.event.InputEvent.CTRL_MASK));
		undoItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.undo"));
		jPopupMenu1.add(undoItem);

		redoItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_Y,
				java.awt.event.InputEvent.CTRL_MASK));
		redoItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.redo"));
		redoItem.setPreferredSize(new java.awt.Dimension(59, 19));
		jPopupMenu1.add(redoItem);

		jPopupMenu1.add(jSeparator2);

		
		expandAllItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_F4,
				java.awt.event.InputEvent.SHIFT_MASK
						| java.awt.event.InputEvent.CTRL_MASK));
		expandAllItem.setText(java.util.ResourceBundle.getBundle("exalto/xmlgrid")
				.getString("grid.expand"));
	/*	
		expandAllItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				expandAllClicked(evt);
			}
		});
	*/
		expandAllItem.setAction(
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("grid.expand"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
							expandAllClicked(e);
						}
				});

		
		gridCommands.put("EXPAND_ALL", expandAllItem.getAction());

		jPopupMenu1.add(expandAllItem);

		treeClipboard = new Clipboard("Grid XML editor");

	}

	public ExaltoXmlNode foo() {
	
		ExaltoXmlNode selNode = gridHelper.getNodeForRowColumn(selectedRow, selectedCol);

		return selNode;
	
	}
	
	public void addNamespaceToHash(Node attr) {

		String nmsp = attr.getNodeName();
		String nmspval = attr.getNodeValue();
		
		if(!namespacesHash.containsKey(nmsp))
			namespacesHash.put(nmsp, nmspval);
		
	
}
	
    public void fireUndoableEditHappened(UndoableEdit edit){
        System.out.println(" in fireUndoableEditHappened ");
        
    	UndoableEditEvent event = new UndoableEditEvent(this, edit);
        Iterator iter = ((List)listeners.clone()).iterator();
        while(iter.hasNext()) {
             
             UndoableEditListener undomgr = (UndoableEditListener)iter.next();

             System.out.println(" firing undo edit to undomgr  " + undomgr.hashCode());
             
             undomgr.undoableEditHappened(event);
        }
    }
    
    public HashMap getDomToTreeMap() {
    	return treeTableModel.getDomToTreeMap();
    }
    
    
    public org.w3c.dom.Document getDocument() {
    	return treeTableModel.getDocument();
    }
    

    public HashMap getGridCommands() {
		return gridCommands;
	}

  	public void addPropertyChangeListener(PropertyChangeListener l) {
        if(this.listenerList != null)
            listenerList.add(l);
	 }

    public void removePropertyChangeListener(PropertyChangeListener l) {
          if(this.listenerList != null && this.listenerList.size() > 0)
    		  listenerList.remove(l);
	}


      public void tellPropertyChange(String prop, java.lang.Object oldobj, java.lang.Object newobj) {

         if(listenerList != null && prop.equals("xmlchanged")) {
             for(int i=0; i<listenerList.size();i++) {

                 PropertyChangeListener listener = (PropertyChangeListener) listenerList.get(i);
                 listener.propertyChange(new PropertyChangeEvent(this, prop, (Boolean) oldobj, (Boolean) newobj));

             }
         }
    }

    public void fireStatusChanged(String msg, String type) {
     	this.editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString(type, msg), 0, type));
    }
   
    boolean editing;
	
    private boolean isEditing() {
		return editing;
	}

	private void setEditing(boolean editing) {
		this.editing = editing;
	}

	// variables used for popupmenu

    HashMap gridCommands = new HashMap(); 

	// Variables declaration - do not modify
	private javax.swing.JMenuItem copyItem;
	private javax.swing.JMenuItem cutItem;
	private javax.swing.JMenuItem delAttrItem;
	private javax.swing.JMenuItem delElemItem;
	private javax.swing.JMenuItem delTextItem;
	private javax.swing.JMenu deleteMenu;
	private javax.swing.JMenuItem expandAllItem;
	private javax.swing.JMenuItem insAttrItem;
	private javax.swing.JMenuItem insElemItem;
	private javax.swing.JMenuItem insTextItem;
	private javax.swing.JMenu insertMenu;
	private javax.swing.JPopupMenu jPopupMenu1;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;

	private javax.swing.JMenu pasteMenu;
	private javax.swing.JMenuItem pasteIntoItem;
	private javax.swing.JMenuItem pasteBeforeItem;
	private javax.swing.JMenuItem pasteAfterItem;
	
	private javax.swing.JMenuItem redoItem;
	private javax.swing.JMenuItem renameMenu;
	private javax.swing.JMenuItem undoItem;
	// End of variables declaration


}