package com.exalto.UI.grid;

import java.util.*;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*; 

import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMResult;
import org.xml.sax.helpers.DefaultHandler; 
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;


public class ExaltoColumnHandler extends DefaultHandler {

      private Node currentNode;
      private ExaltoXmlNode root;

      protected Element rootNode;
      protected Document doc;
      protected Document oldDoc;
      protected boolean isRootVisible;
      protected int level;
      protected String order;
      protected String function;
      protected int currLevel;
      protected SimpleTreeModelAdapter tdap;
      protected JFrame frame;
      public static final String APP_NAME = "EXALTO GRID EDITOR";

    
      public ExaltoColumnHandler(JFrame f) {
			frame = f;
	  }


	public ExaltoColumnHandler(String fn, ExaltoXmlNode root, Document doc, SimpleTreeModelAdapter tdap) {

	   function = fn;
	   this.root = root;
	   this.oldDoc = doc;
	   this.tdap = tdap;
	}


	public ExaltoColumnHandler(String fn, SimpleTreeModelAdapter tdap) {
	   function = fn;
	   this.tdap = tdap;
	}


	/* Parser calls this once at the beginning of a document */
    public void startDocument() throws SAXException {
		
		try
		{
			if(function.intern() == "SORTALL".intern()) {
		
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
				DocumentBuilder db = dbf.newDocumentBuilder ();
				doc = db.newDocument ();


				if(root == null)
					throw new SAXException("Root node is null");

				Node domNode = root.getXmlNode();

				rootNode = doc.createElement(domNode.getNodeName());

			} 
			
			
		}
		catch (ParserConfigurationException pcfg)
		{
			pcfg.printStackTrace();
		}


    }

	public Document getRoot() {
       return doc;
    }


	public Document createNewDocument() throws Exception {

	
		Source xmlSource = new javax.xml.transform.dom.DOMSource(oldDoc);

		/* Prepare the result */
		SAXResult result = new SAXResult(this);
		
		/* Create a transformer */
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		
		/* Traverse the DOM tree */
		xformer.transform(xmlSource, result);

		return doc;
			
	}


	public Document retrieveCopyOfDocument(Document oldDocument)  throws Exception {
		
		Source xmlSource = new javax.xml.transform.dom.DOMSource(oldDocument);

		/* Prepare the result */
		DOMResult result = new DOMResult();
		
		/* Create a transformer */
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		
		/* Traverse the DOM tree */
		xformer.transform(xmlSource, result);

		Document doc2 = (Document) result.getNode();

		return doc2;
	
	}
	
     public Document createBlankDocument(boolean rootVisible) {
      	Document document = null;
      	
	  	 try{
		    //Create instance of DocumentBuilderFactory
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		//Get the DocumentBuilder
    		DocumentBuilder parser = factory.newDocumentBuilder();
    		//Create blank DOM Document
        	document = parser.newDocument();
        	
        	if(!rootVisible) {
       		   String input = displayMessage("Element", frame);
   		       if (!isLegalXmlName(input, frame)) {
			       input = "root";	
   			   }

	          Node child =
					((org.w3c.dom.Document)document).createElement(input);
       		   document.appendChild(child);
       		}
        	
        	
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
    
    	return document;

	  }


     public Document createBlankDocument(boolean rootVisible, String rootName) {
      	Document document = null;
      	
	  	 try{
		    //Create instance of DocumentBuilderFactory
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		//Get the DocumentBuilder
    		DocumentBuilder parser = factory.newDocumentBuilder();
    		//Create blank DOM Document
        	document = parser.newDocument();

		      System.out.println(" ECH GBD document created = " + document);


	      System.out.println(" ECH GBD rootName = " + rootName);

        	
        	String input = null;
        	if(rootName == null) {
        	
	        	if(!rootVisible) {
	       		   input = displayMessage("Element", frame);
	   		       if (!isLegalXmlName(input, frame)) {
				       input = "root";	
	   			   }
	
				} 
			} else
				input = rootName;
			
	
		      System.out.println(" ECH GBD input = " + input);

	        
	          Node child =
					((org.w3c.dom.Document)document).createElement(input);
       		   
       		   document.appendChild(child);
       		
       		
      System.out.println(" exiting ECH GBD ");
	
        	
        	
    }catch(Exception e){
      System.out.println(e.getMessage());
    }
    
    	return document;

	  }


  private String displayMessage(String nodeType, JFrame f) {

   String input = null;

    if(nodeType.intern() == "Element".intern())
     input = (String)JOptionPane.showInputDialog(f,
    "Please enter name of the new XML node",
    APP_NAME, JOptionPane.PLAIN_MESSAGE,
    null, null, "");
  else if(nodeType.intern() == "Text".intern())
     input = (String)JOptionPane.showInputDialog(f,
    "Please enter value for the new Text node",
    APP_NAME, JOptionPane.PLAIN_MESSAGE,
    null, null, "");
  else if(nodeType.intern() == "Attr".intern())
     input = (String)JOptionPane.showInputDialog(f,
    "Please enter value for the new Attribute",
    APP_NAME, JOptionPane.PLAIN_MESSAGE,
    null, null, "");

  return input;

  }
  
 public boolean isLegalXmlName(String input, JFrame f) {
  if (input==null || input.length()==0)
   return false;
  if (!(XMLRoutines.isLegalXmlName(input))) {
   JOptionPane.showMessageDialog(f,
    "Invalid XML name", APP_NAME,
    JOptionPane.WARNING_MESSAGE);
   return false;
  }
  return true;
 }


}

