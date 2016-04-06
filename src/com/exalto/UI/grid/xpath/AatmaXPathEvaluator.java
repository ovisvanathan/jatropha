package com.exalto.UI.grid.xpath;

//$Id: XPathDemo.java,v 1.1 2004/11/24 23:45:45 jsuttor Exp $
//Copyright 2004 Sun Microsystems, Inc. All rights reserved.

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.XPathEvaluator;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.exalto.UI.grid.ActionsHandler;
import com.exalto.UI.grid.ExaltoXmlNode;
import com.exalto.UI.grid.JXmlTreeTable;



public class AatmaXPathEvaluator {
	
  /**
   * <p>usage java XPathDemo &lt;XML file&gt; &lt;XPath Expression&gt;</p>
   * 
   * <p>Apply XPath Express against XML file and
   * output resulting NodeList.</p>
   */
	NamespaceContextImpl namespaceContextImpl;
	XPathFactory xpf;
	XPath xpath;
	Document xmlDoc;
	ActionsHandler treeModel;
	
	ExaltoXmlNode foundNode;

	private List listeners = new ArrayList();
	
	String xpathExpression;
	
	JXmlTreeTable treeTable;
	
	public AatmaXPathEvaluator(JXmlTreeTable treeTable, ActionsHandler treeModel) {

		// for jaxp
	//	System.setProperty("javax.xml.xpath.XPathFactory:http://java.sun.com/jaxp/xpath/dom", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl"); 

	    // Following is specific to Saxon: should be in a properties file
		 System.setProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON,
		                  "net.sf.saxon.xpath.XPathFactoryImpl");

		
		try {

            		System.out.println(
            		 System.getProperty("javax.xml.xpath.XPathFactory:"+NamespaceConstant.OBJECT_MODEL_SAXON));


			// create XPath
			xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		xpath = xpf.newXPath();
		
		namespaceContextImpl = new NamespaceContextImpl();

         xpath.setNamespaceContext(namespaceContextImpl);

		this.treeModel = treeModel;
		
		this.treeTable = treeTable;
		
		this.xmlDoc = treeModel.getDocument();
		
		addPropertyChangeListener(treeTable);
		
		bindPrefixToNamespace();
	
	}
	
	public void bindPrefixToNamespace() {

      HashMap namespaces = treeModel.getNamespaces();

      namespaceContextImpl.setNamespaces(namespaces);

      namespaceContextImpl.bindPrefixToNamespaceURI(
              XMLConstants.DEFAULT_NS_PREFIX,
              "http://schemas.xmlsoap.org/wsdl/");
 
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "tns",
              "http://hello.org/wsdl");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "ns2",
              "http://hello.org/types");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "xsd",
              "http://www.w3.org/2001/XMLSchema");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "soap",
              "http://schemas.xmlsoap.org/wsdl/soap/");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "soap11-enc",
              "http://schemas.xmlsoap.org/soap/encoding/");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "wsdl",
              "http://schemas.xmlsoap.org/wsdl/");
              
		xpath.setNamespaceContext(namespaceContextImpl);
		
		NamespaceContext namespaceContext = xpath.getNamespaceContext();
		String namespaceContextClassName = null;
		if (namespaceContext != null) {
			namespaceContextClassName = namespaceContext.getClass().getName();
		} else {
			namespaceContextClassName = "null";
		}
		System.out.println(
              "XPath.getNamespaceContext() = "
              + namespaceContextClassName);

	}
	
	public ExaltoXmlNode [] evaluate(String xpathExpression, boolean bool) {
		
		NodeList nodeList = null;
		
		this.xpathExpression = xpathExpression;
	
			   try {

					Source xmlSrc = new DOMSource(xmlDoc);
					
				//	InputSource xmlsrc = new InputSource(xmlSrc); 
					
					NodeInfo doc = ((XPathEvaluator)xpath).build(xmlSrc);
					     
				    // Declare a variable resolver to return the value of variables used in XPath expressions
				    // xpe.setXPathVariableResolver(this);
				 
				         // Compile the XPath expressions used by the application
				 
				   //      XPathExpression findLine =
				   //          xpe.compile("//LINE[contains(., $word)]");
				   //      XPathExpression findLocation =
				   //          xpe.compile("concat(ancestor::ACT/TITLE, ' ', ancestor::SCENE/TITLE)");
				   XPathExpression findSpeaker =
				             xpath.compile("string(/project/property[3])");
				 
				   nodeList = (NodeList)xpath.evaluate(xpathExpression,
							xmlSrc,
							XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			   
		return processNodes(nodeList);

	}

	
	
	public ExaltoXmlNode [] processNodes(NodeList nodeList) {

		// dump each Node's info
		for (int onNode = 0; onNode < nodeList.getLength(); onNode++) {
			
			Node node = nodeList.item(onNode);
		
			dumpNode(node);
			
			ExaltoXmlNode exNode = getDOMNodeToTreeNode(node);

		}
			
		return null;
	}
	
	
	public ExaltoXmlNode getDOMNodeToTreeNode(Node node) {
		
		HashMap domToTreeMap = treeModel.getDomToTreeMap();

		ExaltoXmlNode oldValue = foundNode; 
		
		foundNode = (ExaltoXmlNode) domToTreeMap.get(node);
		
		 for(int i=0; i<listeners.size();i++) {
			 PropertyChangeListener listener = (PropertyChangeListener)listeners.get(i);
			 
			 PropertyChangeObject oldProp = new PropertyChangeObject();
			 
			 oldProp.setFoundNode(oldValue);
			 oldProp.setXpathExpression(null);
			 
			 PropertyChangeObject newProp = new PropertyChangeObject();
			 newProp.setFoundNode(foundNode);
			 newProp.setXpathExpression(xpathExpression);
			 
			 listener.propertyChange(new PropertyChangeEvent(this, "foundNode", oldProp, newProp));
		 }

		
		return foundNode;
		
	}
	
	
	public static void main(String[] args) {
		
		// must have exactly 2 args
		if (args.length != 2) {
			System.err.println("Usage:"
					+ "java XPathDemo <XML file>"
					+ " <XPath Expression>");
			System.exit(1);
		}
		
		String xmlFile = args[0];
		String xpathExpression = args[1];
		
		// create XPath
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpath = xpf.newXPath();
		
		NamespaceContextImpl namespaceContextImpl = new NamespaceContextImpl();
      
      namespaceContextImpl.bindPrefixToNamespaceURI(
              XMLConstants.DEFAULT_NS_PREFIX,
              "http://schemas.xmlsoap.org/wsdl/");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "tns",
              "http://hello.org/wsdl");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "ns2",
              "http://hello.org/types");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "xsd",
              "http://www.w3.org/2001/XMLSchema");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "soap",
              "http://schemas.xmlsoap.org/wsdl/soap/");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "soap11-enc",
              "http://schemas.xmlsoap.org/soap/encoding/");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "xsi",
              "http://www.w3.org/2001/XMLSchema-instance");
      namespaceContextImpl.bindPrefixToNamespaceURI(
              "wsdl",
              "http://schemas.xmlsoap.org/wsdl/");
              
		xpath.setNamespaceContext(namespaceContextImpl);
		
		NamespaceContext namespaceContext = xpath.getNamespaceContext();
		String namespaceContextClassName = null;
		if (namespaceContext != null) {
			namespaceContextClassName = namespaceContext.getClass().getName();
		} else {
			namespaceContextClassName = "null";
		}
		System.out.println(
              "XPath.getNamespaceContext() = "
              + namespaceContextClassName);
		
		// SAX as data model
		FileInputStream saxStream = null;
		try {
			saxStream = new FileInputStream(xmlFile);
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
			System.exit(1);
		}
		
		NodeList saxNodeList = null;
		try {
			saxNodeList = (NodeList)xpath.evaluate(xpathExpression,
					new InputSource(saxStream),
					XPathConstants.NODESET);
		} catch (XPathExpressionException xpathExpressionException) {
			xpathExpressionException.printStackTrace();
			System.exit(1);
		}
		
		dumpNode("SAX via InputSource", xmlFile, xpathExpression, saxNodeList);
	}
	
	static void dumpNode(String objectModel,
			String inputFile,
			String xpathExpression,
			NodeList nodeList) {
		
		System.out.println("Object model: " + objectModel + "created from: " + inputFile + "\n"
				+ "XPath expression: " + xpathExpression + "\n"
				+ "NodeList.getLength(): " + nodeList.getLength());
		
		// dump each Node's info
		for (int onNode = 0; onNode < nodeList.getLength(); onNode++) {
			
			Node node = nodeList.item(onNode);
			String nodeName = node.getNodeName();
			String nodeValue = node.getNodeValue();
			if (nodeValue == null) {
				nodeValue = "null";
			}
			String namespaceURI = node.getNamespaceURI();
			if (namespaceURI == null) {
				namespaceURI = "null";
			}
			String namespacePrefix = node.getPrefix();
			if (namespacePrefix == null) {
				namespacePrefix = "null";
			}
			String localName = node.getLocalName();
			if (localName == null) {
				localName = "null";
			}
			
			System.out.println("result #: " + onNode + "\n"
					+ "\tNode name: " + nodeName + "\n"
					+ "\tNode value: " + nodeValue + "\n"
					+ "\tNamespace URI: " + namespaceURI + "\n"
					+ "\tNamespace prefix: " + namespacePrefix + "\n"
					+ "\tLocal name: " + localName);
		}
		// dump each Node's info
		
	}
	
	
	
	static void dumpNode(Node node) {
		
			String nodeName = node.getNodeName();
			String nodeValue = node.getNodeValue();
			if (nodeValue == null) {
				nodeValue = "null";
			}
			String namespaceURI = node.getNamespaceURI();
			if (namespaceURI == null) {
				namespaceURI = "null";
			}
			String namespacePrefix = node.getPrefix();
			if (namespacePrefix == null) {
				namespacePrefix = "null";
			}
			String localName = node.getLocalName();
			if (localName == null) {
				localName = "null";
			}
			
			System.out.println("result #: " +  "\n"
					+ "\tNode name: " + nodeName + "\n"
					+ "\tNode value: " + nodeValue + "\n"
					+ "\tNamespace URI: " + namespaceURI + "\n"
					+ "\tNamespace prefix: " + namespacePrefix + "\n"
					+ "\tLocal name: " + localName);
		}

	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		  listeners.add(l);
		 }
		 public void removePropertyChangeListener(PropertyChangeListener l) {
		  listeners.remove(l);
		 }


		 public class PropertyChangeObject {

			 	ExaltoXmlNode foundNode;
			 	String xpathExpression;
				public ExaltoXmlNode getFoundNode() {
					return foundNode;
				}
				public void setFoundNode(ExaltoXmlNode foundNode) {
					this.foundNode = foundNode;
				}
				public String getXpathExpression() {
					return xpathExpression;
				}
				public void setXpathExpression(String xpathExpression) {
					this.xpathExpression = xpathExpression;
				}
			 
			 
		 }
		 
}

