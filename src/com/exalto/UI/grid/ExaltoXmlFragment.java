/*
 * ExaltoXmlFragment.java
 *
 * Created on March 25, 2008, 11:58 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI.grid;

import javax.swing.tree.TreeNode;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;

/**
 *
 * @author omprakash.v
 * Doc Fragment containter
 *
 *
 */
public class ExaltoXmlFragment extends ExaltoXmlNode {
    
    ExaltoXmlNode [] clonedFrom;
    DocumentFragment doc;
    
    /** Creates a new instance of ExaltoXmlFragment */

    public ExaltoXmlFragment(ExaltoXmlNode data)
    {
        super(data);
    }

    public ExaltoXmlFragment(org.w3c.dom.DocumentFragment doc)
    {
        super(doc);
        this.doc = doc;
    }
    
    public void setClonedFrom(ExaltoXmlNode [] data) {
        this.clonedFrom = data;
        
    }    

    public void appendChild(ExaltoXmlNode node) {
       doc.appendChild(node.getXmlNode());
        
    }    

    public ExaltoXmlNode [] getClonedFrom() {
        return clonedFrom;
        
    }    
    
    public TreeNode getFirstChild() {
        return new ExaltoXmlNode(doc.getFirstChild());
    }    

    public ExaltoXmlNode [] getChildNodes() {
        NodeList nlist = doc.getChildNodes();
        
        ExaltoXmlNode [] enodes = new ExaltoXmlNode[nlist.getLength()];
        for(int i=0;i<nlist.getLength();i++) {
            
            ExaltoXmlNode enode = new ExaltoXmlNode(nlist.item(i));
            enodes[i] = enode;
        }
       
        return enodes;
    }    


}

    
    
    
    
    

