package com.exalto.UI.grid;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.dom.*; 


public class ExaltoHandler extends DefaultHandler {

	private Node root, currentNode;  

	protected Element rootNode;
	protected Document doc;
	protected String newValue;

	public ExaltoHandler(String newval) {
		newValue = newval;
	}

	/* Parser calls this once at the beginning of a document */
    public void startDocument() throws SAXException {
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
			DocumentBuilder db = dbf.newDocumentBuilder ();
			doc = db.newDocument ();
			rootNode = doc.createElement (newValue);
		}
		catch (ParserConfigurationException pcfg)
		{
			pcfg.printStackTrace();
		}


    }

	public Document getRoot() {
       return doc;
    }

    /* Parser calls this for each element in a document */
    public void startElement(String namespaceURI, String lName,
                             String qName, Attributes atts)
	throws SAXException
    {
		Element newNode = null;
		String eName = lName; /* element name */
		if ("".equals(eName)) eName = qName;

		/*  DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(t); */
		if (currentNode == null) {

			for(int j=0;j<atts.getLength();j++) {

				String attrNSUri = atts.getURI(j);
				String attrQName = atts.getQName(j);
				String attrVal = atts.getValue(j);

				rootNode.setAttributeNS(attrNSUri, attrQName, attrVal);
			}

			newNode = rootNode;
			doc.appendChild(newNode);
		}
		else {
			/* must not be the root node... */
			newNode = doc.createElementNS(namespaceURI, qName);


			for(int j=0;j<atts.getLength();j++) {

				String attrNSUri = atts.getURI(j);
				String attrQName = atts.getQName(j);
				String attrVal = atts.getValue(j);


				newNode.setAttributeNS(attrNSUri, attrQName, attrVal);
			}

			currentNode.appendChild(newNode);
		}
      
		currentNode = newNode;
    }

	public void endElement(String namespaceURI,
                           String sName, 
                           String qName  
                          )
    throws SAXException
    {
	   	 if(currentNode != null)
	          currentNode = (Node)currentNode.getParentNode();
    }

	public void characters(char buf[], int offset, int len) throws SAXException {
        String s = new String(buf, offset, len).trim();
	    Text txtNode = doc.createTextNode(s);	
		if(currentNode != null)
	        ((Node)currentNode).appendChild(txtNode);
    }

}

