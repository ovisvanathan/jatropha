package com.exalto.UI.grid;

import javax.swing.*;
import javax.swing.tree.*;
import org.w3c.dom.*;
import java.util.*;
import java.net.URL;

import com.exalto.util.XmlUtils;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;


public class ExaltoXmlNode extends DefaultMutableTreeNode implements Transferable {
    // implements Transferable {
    
    protected ImageIcon imgIcon = null;
    protected String tipText;
    Node text;
    protected boolean isExpanded;
    protected String sortStatus;
    
    final static int TREE = 0;
    final static int STRING = 1;
    final static int PLAIN_TEXT = 2;
    
    final public static DataFlavor
            DOM_TREENODE_FLAVOR =
            new DataFlavor(ExaltoXmlNode.class,  "Dom Node");
    
    static DataFlavor flavors[] = {
        DOM_TREENODE_FLAVOR,
        DataFlavor.stringFlavor,
        DataFlavor.plainTextFlavor};
    
    ExaltoXmlNode userData;
    
    Node theNode;
    
    static Document document;
    static List namespaces;
    
    Node ownerElement;

    
    public ExaltoXmlNode() {
        
    }
    
    public ExaltoXmlNode(Node node) {
        super(node);
        this.theNode = node;
        XmlUtils xutils = XmlUtils.getInstance();
        URL url = xutils.getResource("elementImage");
        
        imgIcon = new ImageIcon(url);
        tipText = node.toString(); 
    }
   
 /*   
    public ExaltoXmlNode(Text node) {
        this.text = node;
    }
 */   
    public ExaltoXmlNode(ExaltoXmlNode node) {
    	super(node.getXmlNode());
    	this.userData = node;
        this.theNode = node.getXmlNode();
    }
    
    public ExaltoXmlNode(TreeNode node) {
        this.userData = (ExaltoXmlNode) node;
        this.theNode = userData.getXmlNode();
    }
    
    
    public Object getUserObject() {
    	return theNode;
    }
    
    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
    
    public Node getXmlNode() {
        
   //     if(text != null)
   //         return (Node) text;
        
               return theNode;
        
    }
    
    public Icon getIcon() {
        return imgIcon;
    }
    
    public String getToolTipText() {
        return tipText;
    }
    
    public int getNodeType() {
       
    	Object obj = getUserObject();
        
    	if (obj instanceof Node) {
            Node nodeObj = (Node) obj;
            return nodeObj.getNodeType();
        }
        
        return -1;
    
    }
    
    public int addXmlNode(ExaltoXmlNode child)
    throws Exception {
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        
        node.appendChild(child.getXmlNode());
        add(child);
        
        return child.getChildIndex();
        
    }
    
    public void addXmlNode(ExaltoXmlNode child, boolean addToTree)
    throws Exception {
        
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        
        node.appendChild(child.getXmlNode());
        if(addToTree)
            add(child);
    }
    
    public void addAttrNode(ExaltoXmlNode child, boolean addToTree)
    throws Exception {
        
        Element node = (Element) getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        
        node.setAttribute(child.getXmlNode().getNodeName(), ((Attr)child.getXmlNode()).getValue());
        
        if(addToTree)
            add(child);
    }
    
    
    public void insertXmlNode(ExaltoXmlNode child, Node refChild, int index, boolean addToTree)
    throws Exception {

    	/* OV changed 29/05/08 for undo/redo */
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        
        
       /* 
        NodeList nl = node.getChildNodes();
  
        int ct = 0;
        for(int i=0;i<nl.getLength();i++) {
            System.out.println(" refChild = " + nl.item(i));
            
            Node n = (Node) nl.item(i);
            int type = n.getNodeType();
            
            if(type == 1)
            	ct++;
            
        }
        
        if(refChild != null)
        	node.insertBefore(child.getXmlNode(), refChild);
        else
       */ 

        	node.appendChild(child.getXmlNode());

      /*  	
        if(refChild == null)
        	index = 0;
      */
        	
        if(addToTree)
        	insert(child, index);

    }
    

    public void remove() throws Exception {
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        Node parent = node.getParentNode();
        if (parent == null)
            throw new Exception(
                    "Cannot remove root node");
        TreeNode treeParent = getParent();
        if (!(treeParent instanceof DefaultMutableTreeNode))
            throw new Exception(
                    "Cannot remove tree node");
        parent.removeChild(node);
        ((DefaultMutableTreeNode)treeParent).remove(this);
    }
    
        
    public void remove(boolean remFromTree) throws Exception {
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        Node parent = node.getParentNode();
        if (parent == null)
            throw new Exception(
                    "Cannot remove root node");
        
        TreeNode treeParent = getParent();
        parent.removeChild(node);
        
        if(remFromTree)
            ((DefaultMutableTreeNode)treeParent).remove(this);
        
    }
    
    public void removeAll() throws Exception {
        
/*
  Node node = getXmlNode();
  if (node == null)
   throw new Exception(
    "Corrupted XML node");
 
        NodeList nl = node.getChildNodes();
        for(int e=0;e<nl.getLength();e++) {
                Node c = nl.item(e);
                node.removeChild(c);
        }
 */
        
        Enumeration en = children();
        while(en.hasMoreElements()) {
            ExaltoXmlNode tn = (ExaltoXmlNode) en.nextElement();
            tn.remove();
        }
        
        
    }
    
    public ExaltoXmlNode getParentNode() throws Exception {
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        Node parent = node.getParentNode();
        if (parent == null)
            throw new Exception(
                    "Cannot remove root node");
        TreeNode treeParent = getParent();
        if (!(treeParent instanceof DefaultMutableTreeNode))
            throw new Exception(
                    "Cannot remove tree node");
//        return new ExaltoXmlNode (treeParent);
        return (ExaltoXmlNode) treeParent;
    }
    
    public int getChildIndex() {
        TreeNode parent = this.getParent();
        return parent.getIndex(this);
    }
    
    //TODO: needs to check against grammar
    public boolean isAllowableChild(ExaltoXmlNode node, int index, boolean moving) {
        return true;
    }
    
    public ExaltoXmlNode importChildBefore(ExaltoXmlNode node) {

    	
    	
        return null;
    }
    
    
    public String toString() {
        Node node = getXmlNode();
      
        if (node == null)
            return getUserObject().toString();
        StringBuffer sb = new StringBuffer();
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                /* sb.append('<'); */
                sb.append(node.getNodeName());
                /* sb.append('>'); */
                break;
            case Node.TEXT_NODE:
                sb.append(node.getNodeValue());
                break;
            case Node.DOCUMENT_NODE:
                sb.append("doc");
                break;
        }
        return sb.toString();
    }
    
    public boolean hasElementChildren() {
        
        Node node = getXmlNode();
        NodeList nl = node.getChildNodes();
        for(int r=0;r<nl.getLength();r++) {
            Node t = nl.item(r);
            if(t.getNodeType() == 1)
                return true;
        }
        
        return false;
    }
    
/*
 public Enumeration children() {
 
  Object obj = getUserObject();
 
        System.out.println(" obj type = " + obj.getClass().getName());
 
 // if(obj instanceof org.w3c.dom.Document)
 // {
   org.w3c.dom.Node docNode = (org.w3c.dom.Node) obj;
            Vector v = new Vector();
            NodeList nl = docNode.getChildNodes();
            for(int k=0;k<nl.getLength();k++) {
                Node cnode = nl.item(k);
                XmlViewerNode anode = new XmlViewerNode(cnode);
                v.add(anode);
            }
            System.out.println(" v size = " + v.size());
            System.out.println(" vector v = " + v);
            return v.elements();
 // }
 // else {
 //  Enumeration en = super.children();
 //   System.out.println("in else xmlview ");
 //  for (;en.hasMoreElements();) {
   //       System.out.println("in else xmlview " + en.nextElement());
  //    }
 
     //     return en;
 
     }
 
 
        public boolean equals(Object obj) {
 
                if(obj.hashCode() == hashCode())
                        return true;
                else
                        return false;
 
        }
 */
    
    public int getElemChildrenCount()  {
        int incr = 0;
        Node d = getXmlNode();
        NodeList nl = d.getChildNodes();
        for(int k=0;k<nl.getLength();k++) {
            Node cnode = nl.item(k);
            if(cnode.getNodeType() == 1)
                incr++;
        }
        
        return incr;
    }
    
    
    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }
    
    public boolean getExpanded() {
        return isExpanded;
    }
    
    public String getSortStatus() {
        return sortStatus;
    }
    
    public void setSortStatus(String sortStatus) {
        this.sortStatus = sortStatus;
    }
    
    public void replaceAttr(Document doc, String attrName, String attrVal) throws Exception {
        
        Attr newElement = null;
        newElement =
                doc.createAttribute(attrName);
        newElement.setValue(attrVal);
        ExaltoXmlNode nodeElement = new ExaltoXmlNode(newElement);
        addAttrNode(nodeElement, false);
    }
    
    
    // Transferable stuff
    
    
    
    
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }
    
    public Object getTransferData(DataFlavor flavor)
    throws UnsupportedFlavorException,
            IOException {
        Object returnObject = null;
        if (flavor.equals(flavors[TREE])) {
            // check to see if we are a doc frag, if so no need to do
            // and extra cloning
            if (this instanceof ExaltoXmlFragment) {
                returnObject = this;
            } else {
                // create a DocumentFragment containing the node
                ExaltoXmlNode cloned = (ExaltoXmlNode)this.clone();
                // create a document fragment to hold this
                //	cloned.setClonedFrom(this);
                
                
                if (document != null) {
                    DocumentFragment frag = document.createDocumentFragment();
                    frag.appendChild(cloned.getXmlNode());
                    ExaltoXmlFragment gfrag = new ExaltoXmlFragment(frag);
                    ExaltoXmlNode[] clonedset = new ExaltoXmlNode[1];
                    clonedset[0] = this;
                    gfrag.setClonedFrom(clonedset);
                    
                    //gfrag._listeners = cloned._listeners;
                    
                    returnObject = gfrag;
                }
            }
            
        } else if (flavor.equals(flavors[STRING])) {
            
            returnObject = getXmlNode().toString();
            
        } else if (flavor.equals(flavors[PLAIN_TEXT])) {
            
            String string = getXmlNode().toString();
            returnObject = new ByteArrayInputStream(string.getBytes());
            
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
        return returnObject;
    }
    
    
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        boolean returnValue = false;
        for (int i=0, n=flavors.length; i<n; i++) {
            if (flavor.equals(flavors[i])) {
                returnValue = true;
                break;
            }
        }
        return returnValue;
    }
    
    public Object clone() {
        Object c = null;
        
        try {
            c = super.clone();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Node _theNode = getXmlNode();
        if (! (c instanceof ExaltoXmlNode)) {
            System.out.println("super.clone didn't return a ExaltoXmlNode!!!");
        }
        
        // now deep copy all our fields
        ((ExaltoXmlNode)c).setUserObject(_theNode.cloneNode(true));
        // no parent for cloned nodes
   //     ((ExaltoXmlNode)c)._parent = null;
   //     ((ExaltoXmlNode)c)._listeners = this._listeners;
        return c;
        
    }

	public static List getNamespaces() {
		return namespaces;
	}

	public static void setNamespaces(List namespaces) {
		ExaltoXmlNode.namespaces = namespaces;
	}

	public Node getOwnerElement() {
		return ownerElement;
	}

	public void setOwnerElement(Node ownerElement) {
		this.ownerElement = ownerElement;
	}
    
    
    
}