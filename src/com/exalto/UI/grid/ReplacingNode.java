package com.exalto.UI.grid;

import java.io.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import javax.xml.transform.dom.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class ReplacingNode {

  /**
   Searchs and returns the first node which is identified by the input
   parameters, localname and namespaceURI. The localname and namespaceURI 
   should uniquely identify a certain element type. Performs a depth first 
   search.
   @param nodename The nodename of an element
   @param root The DOM tree element to be searched
   @returns null if no matching element found, otherwise a reference to the first
   matching element.
   **/
   public Node findElementNode(String searchnodename, Node root){

     Node matchingNode = null;


     //Check to see if root is the desired element. If so return a root.
     String nodeName = root.getNodeName();
     
     if((nodeName != null) & (nodeName.equals(searchnodename))) 
		return root;

     //Check to see if root has any children if not return null
     if(!(root.hasChildNodes()))
		return null;

     //Root has children, so continue searching for them
     NodeList childNodes = root.getChildNodes();
     int noChildren = childNodes.getLength();
     for(int i = 0; i < noChildren; i++){
	 if(matchingNode == null){
		Node child = childNodes.item(i);
                matchingNode = findElementNode(searchnodename,child);
         } else break;

     }

     return matchingNode;
   }

   
   /**
    Will create a documentFragment of the replacingDocument, will import the 
    replacingDocument as a node of the replacedDocument, and then will replace
    the replaceNode with the documentFragment of replacingDocument.
    @param replacedDocument The document which will have a node replace
    @param replacingDocument The document that will replace a node
    @param replacedNode The node in replacedDocument that will be replaced
    @return The new version of replacedDocument will replacedNode replaced
    **/
    public Node replaceNode(Document replacedDocument, 
				Document replacingDocument, 
				Node replacedNode){
       
    //Create a documentFragment of the replacingDocument
    DocumentFragment docFrag = replacingDocument.createDocumentFragment();
    Element rootElement = replacingDocument.getDocumentElement();
    docFrag.appendChild(rootElement);    
  

    //Import docFrag under the ownership of replacedDocument
    Node replacingNode = 
        ((replacedDocument).importNode(docFrag, true)); 

    
    //In order to replace the node need to retrieve replacedNode's parent
    Node replaceNodeParent = replacedNode.getParentNode();
    replaceNodeParent.replaceChild(replacingNode, replacedNode);
    return replacedDocument;
    }


    /**
     Outputs the DOM representation given as root to a file called outputFile
     @param root The DOM representation to be outputted
     @param outputFile The name of the file.
     **/
    public void printOutDocument(org.w3c.dom.Node root, String outputFile)
                        throws TransformerException,
                               TransformerConfigurationException,
                               FileNotFoundException{
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(new DOMSource(root),
                        new StreamResult(new FileOutputStream(outputFile)));
    }


    /**
     Does the parsing of xmlFile into its DOM representation
     @param xmlFile String 
     @return DOM representation
     **/
    public Document parseFileToDom(String xmlFile) throws
					 ParserConfigurationException,
					 IOException,
					 SAXException{
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    return db.parse(xmlFile);
    }


    
    public static void main(String args[]){
        if(args.length < 3){
		System.out.println("Expecting 2 xml files as input, then"+ 
				"the element name of the node to be replaced");
		System.exit(0);
        }
		
        //The first xml file is the replacedDocument, and the second is the
	//replacingDocument

       try {
        ReplacingNode replaceFunction = new ReplacingNode();
        Document replacingDocument = replaceFunction.parseFileToDom(args[1]);
        Document replacedDocument = replaceFunction.parseFileToDom(args[0]);
        Node replacedNode = replaceFunction.
			findElementNode(args[2], replacedDocument.getDocumentElement());

        if(replacedNode != null){
        Node modifiedReplacedDocument = replaceFunction.
		replaceNode(replacedDocument, replacingDocument, replacedNode);
        replaceFunction.printOutDocument(replacedDocument, "output.xml");
        }else 
		System.out.println("replace node is null");

       }catch(Exception e){
	e.printStackTrace();
       }
     }
}        

	

		
  
		
	

