package com.exalto.UI.grid;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;

import com.exalto.util.XmlUtils;


public class ExaltoXmlDoctype extends ExaltoXmlNode implements Transferable {
    // implements Transferable {
    
    ExaltoXmlDoctype userData;
    
    Node theNode;
//    Node doctype;
    
    static Document document;
    
    
    public ExaltoXmlDoctype() {
        
    }
    
    public ExaltoXmlDoctype(Node node) {
        super(node);
        this.theNode = node;
        XmlUtils xutils = XmlUtils.getInstance();
        URL url = xutils.getResource("attrImage");
        
        imgIcon = new ImageIcon(url);
        tipText = node.toString(); 
    }
    
    public ExaltoXmlDoctype(DocumentType node) {
    	super(node);
    	this.theNode = node;
    }
    
    public ExaltoXmlDoctype(ExaltoXmlDoctype node) {
        this.userData = node;
        this.theNode = node.getXmlNode();
    }
    
    public ExaltoXmlDoctype(TreeNode node) {
        this.userData = (ExaltoXmlDoctype) node;
        this.theNode = userData.getXmlNode();
    }
    
    public void setDocument(Document document) {
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }
    
    public Node getXmlNode() {
        
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
        return new ExaltoXmlNode(treeParent);
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
            case Node.DOCUMENT_TYPE_NODE:
                sb.append("doctype");
                break;

        }
        return sb.toString();
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
            if (this instanceof ExaltoXmlDoctype) {
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
    
    
    
}