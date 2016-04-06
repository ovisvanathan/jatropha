package com.exalto.UI.grid;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

//import parser.SimplelangParser;
//import parser.SimpleNode;


/* This adapter converts the current Document (a DOM) into
    // a JTree model.
 */
public class SimpleTreeModel extends DefaultTreeModel implements XmlTreeModel {
    static protected String[]  cNames = {"Name", "Text", "book", "note", "pen", "paper", "pad", "clip", "marker", "sketch", "post", "label", "scissor", "eraser", "sharpener"};
    /*    static protected String[]  cNames = null; */
    /* Types of the columns. */
    static protected Class[]  cTypes = {TreeTableModel.class, String.class};
    /* An array of names for DOM node-types
    // (Array indexes = nodeType() values.)
     */
    int depth = 0;
    final int MAPCOLNUM = 1;
    Hashtable  nodeMapTbl = new Hashtable();
    ArrayList nodeList = new ArrayList();
    Vector nodeMapVec = new Vector();
    int currRow = 0;
    int maxCol = 0;
    int rowId = 0;
    ArrayList parentList = new ArrayList();
    ExaltoXmlNode top = null;
    
    boolean isRootVisible;
    
    // OV added for xpath 25/04/09
    HashMap domToTreeMap = new HashMap();
    
    HashMap rowMapper = new HashMap();
    HashMap columnMapping = null;
    Element root;
    Document document;
    GridHelper ghelper;
    HashMap nmspHash = new HashMap();
    
    public SimpleTreeModel(ExaltoXmlNode rootNode, org.w3c.dom.Document doc, boolean isRootVisible) {
        super(rootNode);
        
        this.isRootVisible = isRootVisible;
        root = doc.getDocumentElement();
        
        this.document = (org.w3c.dom.Document) doc;

        ghelper = new GridHelper();
        
        if(isRootVisible)
            top = createTreeNode(document);
        else
            top = createTreeNode(root);
        
        top.setDocument(document);
        
        buildColumnMapping(top, 0, "main");
        
        Iterator iter = nodeMapTbl.keySet().iterator();
        int x = 0;
        int y = 0;
        while(iter.hasNext()) {
            StringBuffer sbuf = new StringBuffer();
            ExaltoXmlNode aptr = (ExaltoXmlNode) iter.next();
            
            String rc = (String) nodeMapTbl.get(aptr);
            
            int n = parentList.size();
            
   //         System.out.println(" ????? aptr ????????????? = " + aptr.toString());
            
            parentList.add(aptr);
            
            String [] rowCol = rc.split(",");
            
            int nrow = Integer.parseInt(rowCol[0]);
            ArrayList nlist = (ArrayList) rowMapper.get(new Integer(nrow));
            if(nlist != null) {
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
        
 //       System.out.println(" rowMapper = " + rowMapper);
 //       for(int r=0;r<parentList.size();r++)
  //          System.out.println(" parentList[" + r + "]=" + parentList.get(r));
        
    }
    
    /* Basic TreeModel operations */
    public Object  getRoot() {
        return top;
    }
    
    public boolean isLeaf(Object aNode) {
        /* Determines whether the icon shows up to the left.
        // Return true for any node with no children
         */
     
    	ExaltoXmlNode viewNode = (ExaltoXmlNode) aNode;
        
    	if(!viewNode.hasElementChildren())
            return true;
        else
            return false;
           /*
            
        ExaltoXmlNode viewNode = (ExaltoXmlNode) aNode;
                if(!viewNode.hasElementChildren() && viewNode.getXmlNode().hasChildNodes())
                        return true;
                else
                        if(viewNode.getChildCount() > 0)
                                return false;
            
            return true;
            *
            */
    }
    
    public int getColumnCount() {
        return maxCol;
    }
    
    public String     getColumnName(int ci) {
/*
                 if(cNames == null) {
            cNames = new String[maxCol];
         }
         return cNames[0];
 */
        //return cNames[ci];
        return null;
    }
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(Object node, int column) {
        return null;
        
    }
    
    /**
     * getValueAt overloaded method
     * Takes 3 params
     * @node
     * @row
     * @col
     */
    public Object getValueAt(Object node, int row, int column) {
        String nodeLabel = "";
            /*       String rc = (String) nodeList.get(row);
                     System.out.println("In GVA 3 arg ");
             */
        try {
            
            
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
            
            /*       System.out.println("input rw = " + row);
            //       System.out.println("input col = " + column);
             */
            
            
            for(int t=0;t<ct;t++) {
                
                Arrays.sort(parts, new GridHelper.ColumnComparator());
                /*   System.out.println(" parts[t][0] = " + parts[t][0]);
                //   System.out.println(" parts[t][1] = " + parts[t][1]);
                //   System.out.println(" parts[t][2] = " + parts[t][2]);
                 */
                
                int rw = Integer.parseInt(parts[t][0]);
                int col = Integer.parseInt(parts[t][1]);
                int px = Integer.parseInt(parts[t][2]);
                
                /*   System.out.println("in gva3 rw = " + rw);
                //   System.out.println("in gva3 col = " + col);
                 */
                
                ExaltoXmlNode viewerNode = ((ExaltoXmlNode) parentList.get(px));
     
        		//   System.out.println(" gva3 row = " + row);
       // 		   System.out.println(" gva3 col = " + column);
        		   
          		   
                if(row == rw) {
                    if(column == col) {

            //        	System.out.println("in gva3 nodetype = " + viewerNode.getNodeType());
     
                                     
                        if(viewerNode.getNodeType() == 2) { //attr
                //        	System.out.println("  getNodeType 2  ");
                            
                        	StringBuffer sbuf = new StringBuffer();
                            Node domNode = viewerNode.getXmlNode();
                            sbuf.append(domNode.getNodeName());
                            sbuf.append("=");
                            sbuf.append(domNode.getNodeValue());
                            nodeLabel = sbuf.toString();
                        } else if(viewerNode.getNodeType() == 3 || viewerNode.getNodeType() == 8) { // text 3, comment 8

                 //       	System.out.println("  getNodeType =  " + viewerNode.getNodeType());

                        	Node domNode = viewerNode.getXmlNode();
                            nodeLabel = domNode.getNodeValue();
                        }   
                        else if(viewerNode.getNodeType() == 1) {
                            nodeLabel = viewerNode.toString();
                        }
                        else if(viewerNode.getNodeType() == 4) {
//                        	System.out.println("  getNodeType 4  ");
                            
                        	nodeLabel = viewerNode.toString();
                        }
                        else if(viewerNode.getNodeType() == 8) {
                            nodeLabel = "comment";
                        } 
                        else if(viewerNode.getNodeType() == 9) { // doc node
                            nodeLabel = viewerNode.toString();
                        }
                        else if(viewerNode.getNodeType() == 10) { // doctype node
                            nodeLabel = viewerNode.toString();
                        }
                        else if(viewerNode.getNodeType() == 7) { // pi node
                            nodeLabel = viewerNode.toString();
                        }
                        
                        
                        /*        System.out.println("In rowcol eq returning " + nodeLabel); */
                        return nodeLabel;
                    } else {
                        /*				System.out.println("In rowcol else "); */
                        nodeLabel = "";
                    }
                } else {
                    /*           System.out.println("in gva ret string ");    */
                    nodeLabel = "";
                }
                
            } /* end for(t<ct); */
            
            /*     System.out.println("in gva3 nodeLabel = " + nodeLabel); */
            
        } catch(Exception e) {
            
        }
        
        return nodeLabel;
        
        
    }
    
    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     */
    public boolean isCellEditable(Object node, int row, int column) {

    // 	System.out.println(" in STM ICE row = " + row);
    //     System.out.println(" in STM ICE column = " + column);
    //     System.out.println(" in STM ICE node type = " + node.getClass().getName());
        
        
        ExaltoXmlNode xmlNode = null;
        if(node instanceof ExaltoXmlDoctype) {
  //          xmlNode = (ExaltoXmlNode) node;
            
            //	System.out.println(" in STM ICE ret true ");
            
            /* if(xmlNode.getNodeType() != ELEMENT_TYPE && xmlNode.getNodeType() != DOCUMENT_TYPE) */
            return false;
            
        } else
        	return true;
        	
        //	System.out.println(" in STM ICE ret false ");
        
    }
    
    /**
     * Indicates whether the the value for node <code>node</code>,
     * at column number <code>column</code> is editable.
     */
    //CAUTION
    public boolean isCellEditable(Object node, int column) {
        return false;
    }
    
    
    
    /**
     * Sets the value for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public void setValueAt(Object aValue, Object node, int column) {
        
    }
    
    public int     getChildCount(Object parent) {
        ExaltoXmlNode node = (ExaltoXmlNode) parent;
        return node.getChildCount();
    }
    
    public int     getElementChildCount(Object parent) {
        ExaltoXmlNode node = (ExaltoXmlNode) parent;
        org.w3c.dom.Node domNode = node.getXmlNode();
        org.w3c.dom.NodeList nlist = domNode.getChildNodes();
        int count = 0;
        for(int k=0;k<nlist.getLength();k++) {
            if( nlist.item(k).getNodeType() == Node.ELEMENT_NODE)
                count++;
        }
        return count;
        
    }
    
    public Object getChild(Object parent, int index) {
        ExaltoXmlNode node = (ExaltoXmlNode) parent;
        return node.getChildAt(index);
    }
    public int getIndexOfChild(Object parent, Object child) {
        ExaltoXmlNode node = (ExaltoXmlNode) parent;
        return node.getIndex((ExaltoXmlNode) child);
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {
        /*  Null. We won't be making changes in the GUI
        // If we did, we would ensure the new value was really new,
        // adjust the model, and then fire a TreeNodesChanged event.
         */
    }
    
      /*
       * Use these methods to add and remove event listeners.
       * (Needed to satisfy TreeModel interface, but not used.)
       */
    private Vector listenerList = new Vector();
    public void addTreeModelListener(TreeModelListener listener) {
        if ( listener != null
                && ! listenerList.contains( listener ) ) {
            listenerList.addElement( listener );
        }
    }
    public void removeTreeModelListener(TreeModelListener listener) {
        if ( listener != null ) {
            listenerList.removeElement( listener );
        }
    }
      /* Note: Since XML works with 1.1, this example uses Vector.
      // If coding for 1.2 or later, though, I'd use this instead:
      //   private List listenerList = new LinkedList();
      // The operations on the List are then add(), remove() and
      // iteration, via:
      //  Iterator it = listenerList.iterator();
      //  while ( it.hasNext() ) {
      //    TreeModelListener listener = (TreeModelListener) it.next();
      //    ...
      //  }
       */
      /*
       * Invoke these methods to inform listeners of changes.
       * (Not needed for this example.)
       * Methods taken from TreeModelSupport class described at
       *
http://java.sun.com/products/jfc/tsc/articles/jtree/index.html
       * That architecture (produced by Tom Santos and Steve Wilson)
       * is more elegant. I just hacked 'em in here so they are
       * immediately at hand.
       */
    public void fireTreeNodesChanged( TreeModelEvent e ) {
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
            listener.treeNodesChanged( e );
        }
    }
    public void fireTreeNodesInserted( TreeModelEvent e ) {
        
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
            listener.treeNodesInserted( e );
        }
    }
    
    public void fireTreeNodesRemoved( TreeModelEvent e ) {
        
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
            TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
            listener.treeNodesRemoved( e );
        }
    }
    public void fireTreeStructureChanged(TreeModelEvent e ) {
        
        Enumeration listeners = listenerList.elements();
        while ( listeners.hasMoreElements() ) {
            
            TreeModelListener listener =
                    (TreeModelListener) listeners.nextElement();
            
            listener.treeStructureChanged( e );
        }
    }
    
    
    public class ColumnComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            
            String [] str1 = (String []) o1;
            String [] str2 = (String []) o2;
            
            int result = 0;
            
            
            /* Sort on first element of each array (last name) */
            if ((result = str1[1].compareTo(str2[1])) == 0) {
                return result;
            }
            
            return result;
        }
        
    }
    
    public ArrayList getColumnMappingList() {
        return nodeList;
    }
    public ArrayList getParentList() {
        return parentList;
    }
    
    public void setColumnMapping(Hashtable nodeMapping) {
        this.nodeMapTbl = nodeMapping;
    }
    
    public Hashtable getColumnMapping() {
        return nodeMapTbl;
    }
    
    public void setMaxCol(int mcol) {
        maxCol = mcol;
    }
    public void setNodeList(ArrayList nlist) {
        nodeList = nlist;
    }
    public void setParentList(ArrayList plist) {
        parentList = plist;
    }
    
    private void buildColumnMapping(ExaltoXmlNode root, int currCol, String src) {
        
        nodeMapTbl.put(root, currRow + "," + currCol);
        
        SelectiveBreadthFirstEnumeration benumer = new
                SelectiveBreadthFirstEnumeration(root);
        
        while (benumer.hasMoreElements()) {
            ExaltoXmlNode inode = (ExaltoXmlNode) benumer.nextElement();
            
            if(inode.getNodeType() == Node.ELEMENT_NODE || inode.getNodeType() == Node.DOCUMENT_NODE) {
                currCol = 0;
                
                nodeMapTbl.put(inode, currRow + "," + currCol);
                Node xmlNode = inode.getXmlNode();
                
                int h = 0;
                if(inode.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap nmp = xmlNode.getAttributes();
                    
                    HashMap nmspHash = new HashMap();
                    
                    for(;h<nmp.getLength();h++) {
                        Node attr = nmp.item(h);
                        
   //          			System.out.println(" attr name = " + attr.getNodeName());                        		
   //          			System.out.println(" attr namesp = " + attr.getPrefix());                        		
   //                     System.out.println(" attr namesp val = " + attr.getNodeVaue());
                        
//                        System.out.println("  row no  " + currRow);
                        
 //                       System.out.println("  data avl for col no  " + (currCol+h+1));
                        
             			if(attr.getNodeName().startsWith("xmlns")) {
             				addNamespaceToHash(attr);
             			}
             			
                        ExaltoXmlNode anode = new ExaltoXmlAttr(attr);

                        this.domToTreeMap.put(attr, anode);


                        nodeMapTbl.put(anode, currRow + "," + (currCol+h+1));
                    }
                    
                }
                
                if(currCol+h+1 > maxCol)
                    maxCol = currCol+h+1;
                
                                        /*  OV commented 12/1/2006 2 lines
                                        //	ExaltoXmlNode txtNode = new ExaltoXmlNode(xmlNode.getFirstChild());
                                        //	nodeMapTbl.put(txtNode, currRow + "," + (currCol+h+1));
                                         */
                
//                System.out.println(" tc xmlnode name " + xmlNode.getNodeName());
                
                if(xmlNode.hasChildNodes()) {

  //                  System.out.println(" tc xmlnode hasChildNodes ");

                	NodeList textChilds = xmlNode.getChildNodes();
                    int w=0;
                    
                    for(int u=0;u<textChilds.getLength();u++) {
                        Node tc = textChilds.item(u);
                        
    //                    System.out.println(" tc cdata name " + tc.getNodeName());
    //                    System.out.println(" tc cdata type " + tc.getNodeType());
                        
                        ExaltoXmlNode txtNode = null;
                        if(tc.getNodeType() == 3) {
             
                        	txtNode = new ExaltoXmlText(tc);


                            this.domToTreeMap.put(tc, txtNode);

                        	int d = currCol +h+u+1;
      //                      System.out.println("  data avl for col no  " + d);
                        	
                        	nodeMapTbl.put(txtNode, currRow + "," + (currCol +h+u+1));

     //                       System.out.println(" tc cdata type = 3 adding...  ");

                            
                        } else if(tc.getNodeType() == 4) {
                        	
                        	txtNode = new ExaltoXmlCData(tc);

                            this.domToTreeMap.put(tc, txtNode);


                        	int d = currCol +h+u+1;
       //                     System.out.println("  data avl for col no  " + d);

                        	nodeMapTbl.put(txtNode, currRow + "," + (currCol +h+u+1));
                            
        //                    System.out.println(" tc cdata type = 4 adding...  ");
                        	
                        }
                            
                            if(currCol+h+u+1 > maxCol)
                                maxCol = currCol+h+u+1;
                            
                            
                            w++;
                        
                        
                    }
                }
                
                
/*                
                currRow++;
                if(benumer.dequeued) {
                    currCol++;
                    if(currCol > maxCol)
                        maxCol = currCol;
                }
*/                
                          /*       System.out.println("for currRow " + currRow);
                                 System.out.println("for currCol " + currCol);
                           */
            }
            else if(inode.getNodeType() == Node.COMMENT_NODE) {
                currCol = 0;
                nodeMapTbl.put(inode, currRow + "," + currCol);
                Node xmlNode = inode.getXmlNode();
            	
                org.w3c.dom.Comment cmt = (org.w3c.dom.Comment) xmlNode;
                
                String data = cmt.getData();
                
        //        System.out.println(" ###########ATTR comment data ############# = " + data);
                
                
                int h=0;
          		Node attrNode = document.createTextNode(data);
          	  
                ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
                nodeMapTbl.put(anode1, currRow + "," + (currCol+h+1));
                h++;

                
            }
            else if(inode.getNodeType() == Node.DOCUMENT_TYPE_NODE) {
                currCol = 0;
                nodeMapTbl.put(inode, currRow + "," + currCol);
                Node xmlNode = inode.getXmlNode();
            
                org.w3c.dom.DocumentType docType = (org.w3c.dom.DocumentType) xmlNode;

                String name = docType.getName();
            	int h=0;

       //         System.out.println(" ###########ATTR DT name ############# = " + name);

            	Node attrNode = document.createAttribute(name);
              		
                ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
                nodeMapTbl.put(anode1, currRow + "," + (currCol+h+1));
                h++;
                
                
                String pubId = docType.getPublicId();
                String sysId = docType.getSystemId();
         

      //          System.out.println(" ###########ATTR pubid type ############# = " + pubId);
      //          System.out.println(" ###########ATTR sysid type ############# = " + sysId);

            	attrNode = document.createTextNode(pubId);
                
                ExaltoXmlAttr anode2 = new ExaltoXmlAttr(attrNode);
                nodeMapTbl.put(anode2, currRow + "," + (currCol+h+1));
                h++;
                
            	attrNode = document.createTextNode(sysId);
                
                ExaltoXmlAttr anode3 = new ExaltoXmlAttr(attrNode);
                nodeMapTbl.put(anode3, currRow + "," + (currCol+h+1));
                h++;
                
                                
            }
            else if(inode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
                currCol = 0;
                nodeMapTbl.put(inode, currRow + "," + currCol);
                Node xmlNode = inode.getXmlNode();

                ProcessingInstruction p = (ProcessingInstruction) xmlNode;
                
                String  target = p.getTarget();
                String piData = p.getData();
             
                
                StringTokenizer stok = new StringTokenizer(piData, " ");
                int h=0;
                while(stok.hasMoreTokens()) {
                	
                	String tok = stok.nextToken();
                	String [] s = tok.split("=");
                	
                	String name = s[0];
                	String value = s[1];
                	
                	Node attrNode = document.createAttribute(name);
                	attrNode.setNodeValue(value);
              		
                    ExaltoXmlAttr anode1 = new ExaltoXmlAttr(attrNode);
                    nodeMapTbl.put(anode1, currRow + "," + (currCol+h+1));                
                    h++;
                	
                }
                
                
            }
    
            currRow++;
            if(benumer.dequeued) {
                currCol++;
                if(currCol > maxCol)
                    maxCol = currCol;
            }

                
        }
        

        ArrayList nmspList = new ArrayList();
        Set set = nmspHash.keySet();
        
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        	String nmsp = (String) iterator.next();
        	String nmspval = (String) nmspHash.get(nmsp);
        	nmspList.add(nmsp + "&" + nmspval);
        }
        
        if(root != null)
        	root.setNamespaces(nmspList);
        
    }
    
    public void addNamespaceToHash(Node attr) {

    		String nmsp = attr.getNodeName();
			String nmspval = attr.getNodeValue();
			
			if(!nmspHash.containsKey(nmsp))
				nmspHash.put(nmsp, nmspval);	
    		
    	
    }
    
    public void setDocument(org.w3c.dom.Document doc) {
        this.document = doc;
        root = document.getDocumentElement();
        
    }
    
    
    public void update() {
        
        if(isRootVisible)
            top = createTreeNode(document);
        else
            top = createTreeNode(root);
    }
    
    
    
    protected ExaltoXmlNode createTreeNode(Node root) {
        
    	
        if (!canDisplayNode(root))
            return null;

        ExaltoXmlNode treeNode = ghelper.getNodeForNodeType(root);

        // Only element node is added here. Attr nodes and text nodes are added in
        // buildcolumnmapping
        domToTreeMap.put(root, treeNode);
        
//        ExaltoXmlNode treeNode = new ExaltoXmlNode(root);

        NodeList list = null;
        if(root.getNodeType() == Node.DOCUMENT_NODE || root.getNodeType() == Node.ELEMENT_NODE) {
        
        	list = root.getChildNodes();
        }
        
        if(root.getNodeType() == Node.DOCUMENT_TYPE_NODE) { 
    //        System.out.println(" doctype found ");
            return treeNode;	
        }
        
        if(root.getNodeType() == Node.COMMENT_NODE) { 
    //        System.out.println(" comment found ");
            return treeNode;	
        }

        if(root.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) { 
    //        System.out.println(" PI found ");
            return treeNode;	
        }
       
        if(list != null) {
        	for (int k=0; k<list.getLength(); k++) {
	            Node nd = list.item(k);
	  
	//            System.out.println(" child nd name  " + nd.getNodeName());
	            
	 //           System.out.println(" child nd type " + nd.getNodeType());
	            
	            if(nd.getNodeType() == Node.ELEMENT_NODE) {
	                ExaltoXmlNode child = createTreeNode(nd);
	                if (child != null)
	                    treeNode.add(child);
	            }
	            else if(nd.getNodeType() == Node.DOCUMENT_TYPE_NODE) { 
	    //            System.out.println(" doctype found ");
	                ExaltoXmlNode child = ghelper.getNodeForNodeType(nd);
	                treeNode.add(child);
	            }
	            
	            else if(nd.getNodeType() == Node.COMMENT_NODE) { 
	   //             System.out.println(" comment found ");
	                ExaltoXmlNode child = ghelper.getNodeForNodeType(nd);
	                treeNode.add(child);
	            }

	            else if(nd.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) { 
	  //              System.out.println(" PI found ");
	                ExaltoXmlNode child = ghelper.getNodeForNodeType(nd);
	                treeNode.add(child);
	            }
	            
	        }
        }
        
            return treeNode;
    }
    
    
    protected boolean canDisplayNode(Node node) {
    /*	
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
    */
    	return true;
    }
    
    
    
    
    public HashMap getRowMapper() {
        return rowMapper;
    }
    
    public void setRowMapper(HashMap rowMapper) {
        this.rowMapper = rowMapper;
    }
    
    public Class getColumnClass(int ci) {
        if(ci == 0)
            return TreeTableModel.class;
        
        return String.class;
        
    }
    
    public Document getDocument() {
        return document;
    }
    
    public boolean getRootVisible() {
        return isRootVisible;
    }
    
    public HashMap getDomToTreeMap() {
    	return domToTreeMap;
    }

	@Override
	public int getHierarchicalColumn() {
		// TODO Auto-generated method stub
		return 0;
	}

    
}