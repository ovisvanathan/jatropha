// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 7/24/2008 4:47:24 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   XmlViewer.java
package com.exalto.UI.util;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ExplorerNode extends DefaultMutableTreeNode
{

       String filePath;
       String fileName;

       Node ownerElement;

    public ExplorerNode(Node node)
    {
        super(node);
        
       String name = node.getNodeName();

       String value = null;
        if(name.equals("file")) {
        
        	  value = node.getFirstChild().getNodeValue();
        
              int spos = value.lastIndexOf('/');
              if(spos == -1)
                  spos = value.lastIndexOf('\\');

              if(spos > 0) {
                  filePath = value.substring(0, spos);
                  fileName = value.substring(spos+1);
              }
              
  			System.out.println( " EE filename = " + fileName);
      
              
        }
        
        
        
    }

    public Node getXmlNode()
    {
        Object obj = getUserObject();
        if(obj instanceof Node)
            return (Node)obj;
        else
            return null;
    }

    public void addXmlNode(ExplorerNode ExplorerNode)
        throws Exception
    {
        Node node = getXmlNode();
        if(node == null)
        {
            throw new Exception("Corrupted XML node");
        } else
        {
            node.appendChild(ExplorerNode.getXmlNode());
            add(ExplorerNode);
            return;
        }
    }

    public void remove(DefaultTreeModel tm)
        throws Exception
    {
        Node node = getXmlNode();
        if(node == null)
            throw new Exception("Corrupted XML node");
        Node node1 = node.getParentNode();
        if(node1 == null)
            throw new Exception("Cannot remove root node");
        javax.swing.tree.TreeNode treenode = getParent();
        if(!(treenode instanceof DefaultMutableTreeNode))
        {
            throw new Exception("Cannot remove tree node");
        } else
        {
            node1.removeChild(node);
            
          //  DefaultTreeModel tm = (DefaultTreeModel) AutoScrollingJTree.this.getModel();
            tm.removeNodeFromParent(this);
       //     ((DefaultMutableTreeNode)treenode).remove(this);
            return;
        }
    }


    public String toString()
    {
        String value = null;
        
        Node node = getXmlNode();
        if(node == null)
            return getUserObject().toString();
       
        String name = node.getNodeName();
        
        if(name.equals("project")) {
        
              NamedNodeMap nmap = node.getAttributes();
           
              value = nmap.item(0).getNodeValue();

     // 		System.out.println( " EE tostr value = " + value);

        } else if(name.equals("file")) {
        //      value = node.getFirstChild().getNodeValue();
        	
       // 		System.out.println( " EE tostr = " + fileName);
            
                return fileName;
        }
        
		
        return value;
    }


    public void addAttrNode(ExplorerNode child, boolean addToTree)
    throws Exception {

        Element node = (Element) getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");

        node.setAttribute(child.getXmlNode().getNodeName(), ((Attr)child.getXmlNode()).getValue());

        if(addToTree)
            add(child);
    }

  	public void setOwnerElement(Node ownerElement) {
		this.ownerElement = ownerElement;
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
      //  parent.removeChild(node);
        ((DefaultMutableTreeNode)treeParent).remove(this);
    }

     public void remove(boolean remFromDom) throws Exception {
        Node node = getXmlNode();
        if (node == null)
            throw new Exception(
                    "Corrupted XML node");
        Node parent = node.getParentNode();
        if (parent == null)
            throw new Exception(
                    "Cannot remove root node");

        TreeNode treeParent = getParent();

        if(remFromDom)
            parent.removeChild(node);

       //     ((DefaultMutableTreeNode)treeParent).remove(this);

    }



    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }


}