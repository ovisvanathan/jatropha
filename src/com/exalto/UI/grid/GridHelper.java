package com.exalto.UI.grid;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.exalto.ColWidthTypes;
import com.exalto.UI.XmlEditor;
import org.jdesktop.swingx.actions.PasteUndoableEdit;
import com.exalto.util.ExaltoResource;
import com.exalto.util.StatusEvent;

public class GridHelper
{
	HashMap rowMapper;
	ArrayList plist;
	int row;
	HashMap nodeMapTbl;
	SimpleTreeModelAdapter tdap;
	XmlTreeModel domTreeModel;
        //OV added for undo/redo 03122006
        private Vector listeners = new Vector();
        protected XmlEditor editorFrame;
    
    
	public GridHelper() {

	}

	public GridHelper(SimpleTreeModelAdapter tdap, JFrame frame) {
		this.tdap = tdap;
		domTreeModel = tdap.getModel();
		rowMapper = domTreeModel.getRowMapper();
                this.editorFrame = (XmlEditor) frame;
	}
	
	public GridHelper(XmlTreeModel treeTableModel) {
          domTreeModel = treeTableModel;
          rowMapper = domTreeModel.getRowMapper();
    }



	public GridHelper(HashMap rowMapper, int row) {
		this.rowMapper = rowMapper;
		this.row = row;	
	}

	public String [][] retrieveSortedParts(int row) {

		if(tdap != null)	
			domTreeModel = tdap.getModel();
			
		rowMapper = domTreeModel.getRowMapper();

		ArrayList nlist = (ArrayList) rowMapper.get(new Integer(row));
		String rowcol = (String) nlist.get(0);    

        String [][] parts = null;
		StringTokenizer stok2 = new StringTokenizer(rowcol, "|");
		int num = stok2.countTokens();
		parts = new String[num][3];
		int ct=0;
		while(stok2.hasMoreTokens()) {
			String rwc = stok2.nextToken();			
			StringTokenizer stok3 = new StringTokenizer(rwc, ",");
			parts[ct][0] = stok3.nextToken();
			parts[ct][1] = stok3.nextToken();
			parts[ct++][2] = stok3.nextToken();
			
		}
		
			Arrays.sort(parts, new ColumnComparator());

		return parts;

	}


	public ExaltoXmlNode getNodeForRowColumn(int row, int column) {

		domTreeModel = tdap.getModel();
		rowMapper = domTreeModel.getRowMapper();
	
	//	System.out.println(" in helper Row ======================== " + row);
	
		ArrayList nlist = (ArrayList) rowMapper.get(new Integer(row));
		String rowcol = (String) nlist.get(0);   

        String [][] parts = null;
		StringTokenizer stok2 = new StringTokenizer(rowcol, "|");
		int num = stok2.countTokens();

		parts = new String[num][3];
		int ct=0;
		while(stok2.hasMoreTokens()) {
			String rwc = stok2.nextToken();			
			StringTokenizer stok3 = new StringTokenizer(rwc, ",");
			parts[ct][0] = stok3.nextToken();
			parts[ct][1] = stok3.nextToken();
			parts[ct++][2] = stok3.nextToken();			
		}
		
			Arrays.sort(parts, new ColumnComparator());
	
		for(int c=0;c<ct;c++) {
		
			int rw = Integer.parseInt(parts[c][0]);
			int col = Integer.parseInt(parts[c][1]);
			int px = Integer.parseInt(parts[c][2]);

			if(row == rw && column == col) {
				return (ExaltoXmlNode) domTreeModel.getParentList().get(px);		
			}

		}
	
		return null;
	
	}

	public Vector updatePaths(Vector expandedPaths, HashMap expandedNodes, ExaltoXmlNode newNode, int index) {

		TreeNode [] tna = newNode.getPath();
		int nx = tna.length - 1;

		Set set = expandedNodes.keySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()) {
			ExaltoXmlNode inode = (ExaltoXmlNode) iter.next();
			int n = ((Integer)expandedNodes.get(inode)).intValue();
			if(n < index)
				continue;
			
			if(newNode.isNodeDescendant(inode)) {
		
				Enumeration en = newNode.breadthFirstEnumeration(); 
				while(en.hasMoreElements()) {
					ExaltoXmlNode childNode = (ExaltoXmlNode) en.nextElement();
					if(childNode.equals(inode)) {
						TreeNode [] cn = childNode.getPath();
						TreePath cp = new TreePath(cn);
						expandedPaths.set(n, cp);
					}
				}

/*				
				TreePath tpath = (TreePath) expandedPaths.get(n);
				Object obj = tpath.getPath();
			
			System.out.println(" obj type " + obj.getClass().getName());
			
				TreeNode [] tn = (TreeNode []) tpath.getPath();
				tn[nx] = tna[nx];
				TreePath newPath = new TreePath(tn);	
				expandedPaths.set(n, newPath);
*/

			}
		}
		return expandedPaths;
	}


	public static String getElementName(Node node) {
	
		String nodeName = null;

		if(node == null)
			return "";

		nodeName = node.toString();
		nodeName = nodeName.substring(1);
		int cpos = nodeName.indexOf(":");
		if(cpos > 0) 
			nodeName = nodeName.substring(0, cpos);

		return nodeName;

	}



	public static class ColumnComparator implements Comparator {
 
		public int compare(Object o1, Object o2) {
	
		String [] str1 = (String []) o1;
		String [] str2 = (String []) o2;

		int result = 0;

	
	   /* Sort on first element of each array (last name) */
	   if ((result = str1[1].compareTo(str2[1])) == 0)
	   {
			return result;
	   }

		return result;
	}

 }

	public Object [] performSort(NodeList nlist, String order) {
	
/*			System.out.println(" in performSort "); */

		ArrayList nodes = new ArrayList();
		int j=0;
		for(int h=0;h<nlist.getLength();h++) {
			Node child = (Node) nlist.item(h);
			if(child.getNodeType() != 3) {
				nodes.add(child);
				j++;
			}
		}

	Object[] alist = nodes.toArray();



	Object [] newobj = new Object[j];
	for(int g=0;g<j;g++)
		newobj[g] = alist[g];
	

		Arrays.sort(newobj, new NodeComparator(order));		

		return newobj;

	}


	public Object [] performSort(ExaltoXmlNode xnode, String order) {
	
/*		System.out.println(" in performSort 2 "); */
		
		Enumeration en = xnode.children();

		ArrayList nodes = new ArrayList();
		int j=0;
		for(;en.hasMoreElements();) {
			ExaltoXmlNode child = (ExaltoXmlNode) en.nextElement();
			if(child.getNodeType() != 3) {
				nodes.add(child);
				j++;
			}
		}

	Object[] alist = nodes.toArray();



	Object [] newobj = new Object[j];
	for(int g=0;g<j;g++)
		newobj[g] = alist[g];
	

		Arrays.sort(newobj, new NodeComparator(order));		

		return newobj;

	}


	public Object [] performSort(ArrayList alist, String order) {
	
	/*	System.out.println(" in performSort 3 "); */ 
		
		Object[] nodeobj = alist.toArray();

		Arrays.sort(nodeobj, new NodeComparator(order));		

		return nodeobj;

	}

	public void moveNodes(ExaltoXmlNode enode, ArrayList prevUnsorted, ArrayList unsorted, Object [] sorted) throws Exception {
	
/*	System.out.println(" GridHelper sorted = " + sorted);  */ 

		if(sorted != null) {

			for(int s=0;s<unsorted.size();s++) {
				ExaltoXmlNode child = (ExaltoXmlNode) unsorted.get(s);
				child.remove();
			}

			for(int t=0;t<sorted.length;t++) {
				ExaltoXmlNode child = (ExaltoXmlNode) sorted[t];
				enode.addXmlNode(child);
			}
		
		} else {
	
			  Enumeration en = enode.children();
			  while(en.hasMoreElements()) {
				  ExaltoXmlNode cnode = (ExaltoXmlNode) en.nextElement();
				  cnode.remove(true);	
			  }

			for(int t=0;t<prevUnsorted.size();t++) {
				ExaltoXmlNode child = (ExaltoXmlNode) prevUnsorted.get(t);
				enode.addXmlNode(child);
			}
	

		}

	}


	
public Object [] getNodesAsNodeArray(ExaltoXmlNode enode) {
		Object [] sortedList = null; 
		Node domNode = enode.getXmlNode();
		
		NodeList nlist = domNode.getChildNodes();
		ArrayList nodes = new ArrayList();
		int j=0;
		for(int h=0;h<nlist.getLength();h++) {
			Node child = (Node) nlist.item(h);
			if(child.getNodeType() != 3) {
				nodes.add(child);
				j++;
			}
		}

		sortedList = (Object []) nodes.toArray();
		
		return sortedList;


}

public ExaltoXmlNode [] getNodesAsTreeNodeArray(ExaltoXmlNode enode) {
		int sz = enode.getChildCount();
		ExaltoXmlNode [] sortedXmlList = new ExaltoXmlNode[sz];
		Enumeration en = enode.children();
		int w=0;
		while(en.hasMoreElements()) {
			ExaltoXmlNode wnode = (ExaltoXmlNode) en.nextElement();
			sortedXmlList[w++] = wnode;
		}  			

	return 	sortedXmlList;

}


	
public int getElementColumnForRow(int row) {

	String [][] parts = retrieveSortedParts(row);
	int col = 0;
	for(int r=0;r<1;r++) {
		Arrays.sort(parts, new ColumnComparator());
	    int rw = Integer.parseInt(parts[r][0]);
	    col = Integer.parseInt(parts[r][1]);
	}

	return col;
}



public ExaltoXmlNode replaceChildren(ExaltoXmlNode replaced, ExaltoXmlNode replacing, Document replacedDoc, Document replacingDoc) {
		
		ExaltoXmlNode replacingTreeNode = null;
		
		try {

		    Node replacingDomNode = 
		        ((replacedDoc).importNode(replacing.getXmlNode(), true));

		    replacingTreeNode = createTreeNode(replacingDomNode, replacing);	
		    
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return replacingTreeNode;
}

protected ExaltoXmlNode createTreeNode(Node root, ExaltoXmlNode replacing) {
	  
	  if (!canDisplayNode(root))
	   return null;

	  
	  ExaltoXmlNode treeNode = new ExaltoXmlNode(root);
	  treeNode.setExpanded(replacing.getExpanded());
	  treeNode.setSortStatus(replacing.getSortStatus());
	  NodeList list = root.getChildNodes();
	  ArrayList childNodes = new ArrayList();
	  Enumeration en = replacing.children();
	  while(en.hasMoreElements()) {
	  	 childNodes.add((ExaltoXmlNode) en.nextElement());
	  }
	  
	  int z=0;
	  for (int k=0; k<list.getLength(); k++) {
		Node nd = list.item(k);
		if(nd.getNodeType() != 3) {
		   ExaltoXmlNode child = createTreeNode(nd, (ExaltoXmlNode) childNodes.get(z++));
		   if (child != null)
			   treeNode.add(child);
		}
	  }
		
		return treeNode;
	 }

protected boolean canDisplayNode(Node node) {
	 switch (node.getNodeType()) {
		case Node.ELEMENT_NODE:
		   return true;
		case Node.TEXT_NODE:
		   String text = node.getNodeValue().trim();
		   return !(text.equals("") || text.equals("\n") || text.equals("\r\n"));
	    case Node.DOCUMENT_NODE:
			return true;
	}
 return false;
}


private class NodeComparator  implements Comparator 
{
	String order;

	public NodeComparator(String order) {
		this.order = order;
	}


	public int compare(Object a, Object b) {

		Node n1 = null, n2 = null;
		ExaltoXmlNode e1 = null, e2 = null;
		boolean isNode = false;

/*
		System.out.println(" in compare e1 " + e1);
		System.out.println(" in compare e2 " + e2);
		System.out.println(" in compare n1 " + n1);
		System.out.println(" in compare n2 " + n2);

		System.out.println(" isNode " + isNode);
*/
		if(a instanceof Node && b instanceof Node) {
			n1 = (Node) a;
			n2 = (Node) b;
			isNode = true;
		} else if(a instanceof ExaltoXmlNode && b instanceof ExaltoXmlNode) {
			e1 = (ExaltoXmlNode) a;
			e2 = (ExaltoXmlNode) b;		
			isNode = false;
		}

		if(isNode) {
			if((n1 == null || n1.getNodeType() != 1 || n2 == null || n2.getNodeType() != 1)) {
				return -1;
			}
		} else if(e1 == null || e1.getNodeType() != 1 || e2 == null || e2.getNodeType() != 1) {
			return -1;
		}
	
		int result = 0;
		if(isNode) {
			    result = n1.toString().compareTo(n2.toString());	
		} else {
				result = e1.getXmlNode().toString().compareTo(e2.getXmlNode().toString());	
		}

			if(order.intern() == "ASC".intern())
				return result;
			else 
				return result*(-1);
			
	}
	
}


public int getSelectionIndex(ExaltoXmlNode treeNode, TreePath selPath, JTree tree) {
	int numChildren = 0;
	
	         Enumeration en = treeNode.children(); 
        	 while(en.hasMoreElements()) {
        		ExaltoXmlNode child = (ExaltoXmlNode) en.nextElement();
    		    TreeNode [] tn = child.getPath();
    			TreePath treePath = new TreePath(tn);

  //      		if(tree.isVisible(treePath)) {
        			numChildren++;
        			if(tree.isExpanded(treePath))
        			    numChildren += getSelectionIndex(child, treePath, tree);
        	//	}
	//			numChildren++;        	
        
	        }
	        
	        

	return numChildren;
	
}

/**
* This gets the location of a node specified by the indices of the
* nodes in the path within their parent containers. This creates a
* snapshot of where a node was located at a certain time specifically
* for undoing operations
*/
public int[] getLocationPathForNode(ExaltoXmlNode nd)
{
    Object[] path = getTreePathForNode(nd);
    int len = path.length;
    int[] loc = new int[len];
    loc[0] = 0; // root is always zero
    if (len > 1) {
        for (int i=1; i < len; i++) {
            loc[i] = ((ExaltoXmlNode)path[i]).getChildIndex();
        }
    }
    return loc;
}

/**
* builds the path from this object up to the root and then make an array with
* it in correct traversal order i.e. root down
*/

public Object[] getTreePathForNode(ExaltoXmlNode nd)
{
    Vector v = new Vector();
    Object root = this.tdap.atree.getModel().getRoot();
    ExaltoXmlNode node = nd;
    Object[] objs = null;
        try {

            
            while (node != null && node != root) {
                v.addElement(node);

                String dbg_str = node.getXmlNode().getNodeName();
                if (node instanceof ExaltoXmlNode) {
                    NamedNodeMap nmp = node.getXmlNode().getAttributes();
                    dbg_str += " name="+ nmp.getNamedItem("name");
                }
                dbg_str += " id="+node;

                //MerlotDebug.msg("addtopath: "+dbg_str);
                node = node.getParentNode();
            }
            if (node == root) {
                // add the root to the end of the vector
                v.addElement(node);

                String dbg_str = node.getXmlNode().getNodeName();
                if (node instanceof ExaltoXmlNode) {
                    NamedNodeMap nmp = node.getXmlNode().getAttributes();
                    dbg_str += " name="+ nmp.getNamedItem("name");
                }
                dbg_str += " id="+node;

                //MerlotDebug.msg("addtopath: "+dbg_str+"\n");

                // we completed the loop sucessfully
                // now reverse the order of the path
                int len = v.size();
                objs = new Object[len];
                for (int i=len-1,j=0;i>=0;i--,j++) {
                    objs[j] = v.elementAt(i);
                }
            }
            else {
                objs = new Object[1];
                objs[0] = root;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    
    
    
    return objs;
}
/*
public void deleteNode(ExaltoXmlNode node) {
    
    try {
        
        tdap.storeTreeExpansionState();
    
        node.remove();
    
        
    
    
    }
    catch(Exception e) {
        e.printStackTrace();
    }
    
}
*/

public void importFragment(int selectedRow, ExaltoXmlFragment frag, boolean paste, String  pasteMode) {
        try {
    
      //  		CutUndoableEdit cutEdit = null;
        
      //           Document doc  = tdap.getModel().getDocument();
   
                 boolean ret = false;
                 
                 CompoundEdit bigedit = new CompoundEdit();

                 PasteUndoableEdit edit;
 //                String undoDesc = "import";
   
                 boolean moving = false;	// true if we're just moving a node within a document
               
  //               ExaltoXmlNode newChild = null;
                 
                 ExaltoXmlNode droppedon = tdap.getSelectedTreeNode();
                 
                 TreeNode parent = null;
   
        //         ExaltoXmlNode[] children = frag.getChildNodes();
                 
                 ExaltoXmlNode[] children = frag.getClonedFrom();
                 
                  // see if they have the same parent document... if so we're doing a move
                  // unless this is a paste
                 /*
                 if (!paste && frag.getDocument() == droppedon.getDocument()) {
                             moving = true;
                            //	undoType = MerlotUndoableEdit.MOVE;
                             undoDesc = "move";
                   }
				*/	
    
                //	   parent = droppedon.getParentNode();

                	   parent = droppedon.getParent();
                	   int droppedonIndex = parent.getIndex(droppedon);

                	   // see if this child is allowed here
                   
                   if (parent != null) {
                    //       int droppedonIndex = droppedon.getChildIndex();
  
                   //      int dropIndex = droppedon.getElemChildrenCount();
                       
                         ExaltoXmlNode child = null;
                         
                         boolean isAttr = false;
                         boolean isBeforeAfter = false;
                         ExaltoXmlAttr eattr = null;
                         ExaltoXmlNode refChild = null;
                 	 	Node refNode = null;
                         if (children.length > 0) {
   
          //              	 tdap.storeTreeExpansionState();
                        	 
                        	 for(int p=0;p<children.length;p++) {
                             	 
                                 child = children[p];
                        	 
    //                           if(child instanceof ExaltoXmlAttr) {
//                            		 isAttr = true;

                               	 String nodeType = "Element";
                            	 if(child.getNodeType() == Node.TEXT_NODE)
                            		 nodeType = "Text";

                            	if(child != null && child.getXmlNode().getNodeName().equals("dummy")) {
                                           
	                        		 NamedNodeMap nmp = child.getXmlNode().getAttributes();
	                        		 
	                        		 for(int r=0;r<nmp.getLength();r++) {
	                        			 
	                        			 Attr aNode = (Attr) nmp.item(r);
	                        			 eattr = new ExaltoXmlAttr(aNode);
	                        			 isAttr = true;
	                            		 nodeType = "Attr";
	                                     
	                        		 }
	                        		 
                                 }
                            	
                            	if(!isAttr) {
	    //	                             if (parent.isAllowableChild(child, dropIndex,!moving)) {
	    	                            // Disable the IDManager to prevent Ids changing on move
	    	                                      //   tdap.insertNodeInParent(droppedon, child, "Element", child.getXmlNode().getNodeName(), droppedon.getXmlNode(), dropIndex);

	    	                            		 refChild = droppedon;
	    	                //            		 dropIndex = droppedonIndex;
	    	                            		 if(refChild != null)
	    	                            			 refNode  = refChild.getXmlNode();
	    	                            	 
	    	                          
	    	                            	
	    	                            	 if(pasteMode.equals("before")) { 
	 	    	                            	 tdap.insertNodeForPaste(nodeType, parent, child, refNode, droppedonIndex, pasteMode);
	 	    	       //                          edit = new PasteUndoableEdit(tdap, parent, child, nodeType, droppedon.getXmlNode(), droppedonIndex);
		                              	 } 
	    	                            	 else if(pasteMode.equals("after")) { 
	 	    	                            	 tdap.insertNodeForPaste(nodeType, parent, child, refNode, droppedonIndex+1, pasteMode);
	 	    	       //                          edit = new PasteUndoableEdit(tdap, parent, child, nodeType, droppedon.getXmlNode(), droppedonIndex);
		                              	 }
	    	                            	 else { 
	    	                            		 tdap.insertNodeForPaste(nodeType, droppedon, child, null, 0, pasteMode);
	    	           //                 	     edit = new PasteUndoableEdit(tdap, droppedon, child, nodeType, null, 0);
	   	    	                                 
	    	                            	 }

	    	                            	 //     	 newChild = droppedon.importChildBefore(children[i]);
	    	                              //           int[] location = getLocationPathForNode(newChild);
	    	                                  //       bigedit.addEdit(edit);
	    	      

	    	                                     ret = true;

	    	//                             }
	    	                             
	                                 } else {
	                                	 tdap.insertNodeForPaste(nodeType, droppedon, eattr, null, 0, pasteMode);
	               //                      edit = new PasteUndoableEdit(tdap, droppedon, child, "Attr", null, 0);

	               //                      bigedit.addEdit(edit);

	                                 }
	                        		 
	                        		 
	                        		 
                                 if (!isAttr && !ret) {
                                     editorFrame.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERROR,"dom.insert.before.err")));
                                     System.out.println("Parent doesn't allow child. Parent="+parent+"["+parent.toString() +"] child="+child+"["+child.getXmlNode().getNodeName()+"]");
                                 }

                        	 
                        	 
                        	 
                        	 }
                        	 
                     //        bigedit.end();
                             
                             System.out.println("importFragment returns:  " + bigedit);                           
                             return;
                        		 
                         
                         }

                        
                             
	                          
                         }

            

        } catch (MissingResourceException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

   //OV added for undo/redo begin 03122006
    public TreeNode deleteNodeUndoable(ExaltoXmlNode treeNode) {
        
          if (treeNode == null) {
              return null;
         }

          
           System.out.println(" node =  " + treeNode.getXmlNode().getNodeName());
          
              if(treeNode.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(" node type elem ");
                        System.out.println(" node name " + treeNode.getXmlNode().getNodeName());
                        tdap.deleteNode(treeNode, true, true);
              } 

          return null;
    }   
    
 

	/**
	 * gets the transfer data for the current selection, or null if no selection
	 * exists
	 * 
	 * @return The transferable value
	 */

	public Transferable getTransferable(ExaltoXmlNode[] treeNodes) {

		Document d = domTreeModel.getDocument();
		ExaltoXmlFragment frag = null;
		ExaltoXmlNode[] clonedset = null;

		try {

			if (d != null && treeNodes != null) {

				frag = new ExaltoXmlFragment(d.createDocumentFragment());
				clonedset = new ExaltoXmlNode[treeNodes.length]; // nodes we
																	// cloned
																	// from
				ExaltoXmlNode node;
				for (int i = 0; i < treeNodes.length; i++) {
					node = treeNodes[i];
					clonedset[i] = node;

					// frag.appendChild((ExaltoXmlNode)node.getXmlNode()); //No
					// cloning
					frag.appendChild(node); // No cloning
				}
			}

			ExaltoXmlNode[] dset = frag.getChildNodes();

			System.out.println( " dset len = " + dset.length);

	//		for (int i = 0; i < dset.length; i++)
//				System.out.println(dset[i].getXmlNode().getNodeName());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		frag.setClonedFrom(clonedset);
		return frag;

	}

	/**
	 * gets the transfer data for the current selection, or null if no selection
	 * exists
	 * 
	 * @return The transferable value
	 */

	public Transferable getTransferable(ExaltoXmlNode othNode) {

		Document d = domTreeModel.getDocument();
		ExaltoXmlFragment frag = null;
		ExaltoXmlNode[] clonedset = null;

		try {

			if (d != null && othNode != null) {

				frag = new ExaltoXmlFragment(d.createDocumentFragment());
				clonedset = new ExaltoXmlNode[1]; // nodes we
																	// cloned
																	// from
				ExaltoXmlNode node = null;
	//			clonedset[0] = othNode;
				Element e = d.createElement("dummy");

				if(othNode instanceof ExaltoXmlAttr) {
					node = new ExaltoXmlNode(e);
					node.addAttrNode(othNode, false);
					frag.addXmlNode(node, false);
		//			frag.addXmlNode(othNode, false);
					clonedset[0] = node;
					
				} else if(othNode instanceof ExaltoXmlText) {
					node = new ExaltoXmlNode(e);
					node.addXmlNode(othNode, false);
					frag.addXmlNode(node, false);
	
	//				frag.addXmlNode(othNode, false);
					
				}

			ExaltoXmlNode[] dset = frag.getChildNodes();

			System.out.println( " dset len = " + dset.length);

	//		for (int i = 0; i < dset.length; i++)
//			System.out.println(dset[i].getXmlNode().getNodeName());
			
			
			}
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		frag.setClonedFrom(clonedset);
		return frag;

	}

    

    public void fireUndoableEditHappened(UndoableEdit edit){
        System.out.println(" in fireUndoableEditHappened ");
        
    	UndoableEditEvent event = new UndoableEditEvent(tdap, edit);
        Iterator iter = ((List)listeners.clone()).iterator();
        while(iter.hasNext()) {
             System.out.println(" firing undo edit ");
        	((UndoableEditListener)iter.next()).undoableEditHappened(event);
        }
    }

    public void fireUndoableEditHappened(CompoundEdit edit){
        System.out.println(" in fireUndoableEditHappened CE ");
        
    	UndoableEditEvent event = new UndoableEditEvent(tdap, edit);
        Iterator iter = ((List)listeners.clone()).iterator();
        while(iter.hasNext()) {
             System.out.println(" firing undo edit ce ");
        	((UndoableEditListener)iter.next()).undoableEditHappened(event);
        }
    }


public ExaltoXmlNode getNodeForNodeType(Node node) {

	if(node.getNodeType() == Node.DOCUMENT_NODE)
		return new ExaltoXmlDocument(node); 
	else 	if(node.getNodeType() == Node.DOCUMENT_TYPE_NODE)
		return new ExaltoXmlDoctype(node); 
	else 	if(node.getNodeType() == Node.ELEMENT_NODE)
		return new ExaltoXmlNode(node); 
	else 	if(node.getNodeType() == Node.ATTRIBUTE_NODE)
		return new ExaltoXmlAttr(node); 
	else 	if(node.getNodeType() == Node.COMMENT_NODE)
		return new ExaltoXmlComment(node); 
	else 	if(node.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE)
		return new ExaltoXmlPI(node); 
	else 	if(node.getNodeType() == Node.TEXT_NODE)
		return new ExaltoXmlText(node); 
	else 
		return new ExaltoXmlText(node); 

	
}

public Vector getListeners() {
	return listeners;
}

public void setListeners(Vector listeners) {
	this.listeners = listeners;
}


}
