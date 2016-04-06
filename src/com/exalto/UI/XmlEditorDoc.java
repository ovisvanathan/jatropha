/*
 * XmlEditorDoc.java
 *
 * Created on March 20, 2008, 12:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.exalto.CustomEntityResolver;
import com.exalto.util.XmlUtils;


/**
 *
 * @author omprakash.v
 */
public class XmlEditorDoc {
    
    
    
    
    /** * This constructor builds an XTree object using the XML text
     * passed in through the constructor.
     ** @param text A String of XML formatted text
     ** @exception ParserConfigurationException
     ** This exception is potentially thrown if
     * the constructor configures the parser improperly. It won't.*/
    
    private           DocumentBuilderFactory 	dbf;
    private           DocumentBuilder 		db;
    private           Document                    doc;
    
    private 	Node	docRoot;
    
    private Logger logger;
    private boolean initialized = false;
    private String catalogFile = null;
    private XmlEditor xeditor;
    private XmlUtils xutils;
    
    boolean validation;
    String rootElem;
    boolean hasDoctype;
    
    
    /*** This constructor builds the generic portion of the XTree object that
     * is true for
     * any XTree object. It includes a default tree model
     ** @exception ParserConfigurationException
     * This exception is potentially thrown if
     * the constructor configures the parser improperly. It won't.*/
    
    /** Creates a new instance of XmlEditorDoc */
    public XmlEditorDoc() throws ParserConfigurationException, FileNotFoundException {
        
        // Initialize the superclass portion of the object
        super();
        
        logger = Logger.getLogger(XTree.class.getName());
        
        xutils = XmlUtils.getInstance();
        
        catalogFile = xutils.getCatalogFile();
        
        System.out.println("&&&&&&&&&&&^^%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("&&&&&&&" + catalogFile + "%%%%%%%%%%%%%%");
        System.out.println("&&&&&&&&&&&^^%%%%%%%%%%%%%%%%%%%%%%%%%%");
        
        xeditor = XmlEditor.getInstance("");
        
        // Begin by initializing the object's DOM parsing objects
        dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating( false );
        db = dbf.newDocumentBuilder();
        
    } //end XTree()
    
    
    public void setValidating(boolean b ) {
    	validation = b; 	
    }
    
    public boolean hasDocType() {
    	return this.hasDoctype;	
    }
    
    /**
     * This method returns a string representing the type of node passed in.
     *
     * @param node org.w3c.Node.Node
     *
     * @return Returns a String representing the node type
     */
    private String getNodeType( Node node ) {
        String type;
        
        switch( node.getNodeType() ) {
            case Node.ELEMENT_NODE:
            {
                type = "Element";
                break;
            }
            case Node.ATTRIBUTE_NODE:
            {
                type = "Attribute";
                break;
            }
            case Node.TEXT_NODE:
            {
                type = "Text";
                break;
            }
            case Node.CDATA_SECTION_NODE:
            {
                type = "CData section";
                break;
          }
            case Node.ENTITY_REFERENCE_NODE:
            {
                type = "Entity reference";
                break;
            }
            case Node.ENTITY_NODE:
            {
                type = "Entity";
                break;
            }
            case Node.PROCESSING_INSTRUCTION_NODE:
            {
                type = "Processing instruction";
                break;
            }
            case Node.COMMENT_NODE:
            {
                type = "Comment";
                break;
            }
            case Node.DOCUMENT_NODE:
            {
                type = "Document";
                break;
            }
            case Node.DOCUMENT_TYPE_NODE:
            {
                type = "Document type";
                break;
            }
            case Node.DOCUMENT_FRAGMENT_NODE:
            {
                type = "Document fragment";
                break;
            }
            case Node.NOTATION_NODE:
            {
                type = "Notation";
                break;
            }
            default:
            {
                type = "???";
                break;
            }
        }// end switch( node.getNodeType() )
        return type;
    } //end getNodeType()
    
    
    
    public String parseXml(String text) throws ParserConfigurationException, IOException, SAXException, Exception {
        
        ByteArrayInputStream	byteStream;
        /*uncommented by exalto 16/1/05 */
        CatalogResolver cr = null;
        
        try {
            
            //	if(catalogFile == null)
            //		throw new FileNotFoundException();
            
            
            cr = new CatalogResolver();
            Catalog myCatalog = cr.getCatalog();
            //   myCatalog.loadSystemCatalogs();
   
            if(catalogFile != null) {
                myCatalog.parseCatalog(catalogFile);
            }
            
            byteStream = new ByteArrayInputStream( text.getBytes());
            
            System.out.println(" in parsexml file");
            dbf =DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            dbf.setValidating(false);
            
            if(catalogFile != null)
                db.setEntityResolver(cr);
            else {
            	CustomEntityResolver cres = CustomEntityResolver.getInstance();
            	db.setEntityResolver(cres);
            }
            
            doc = db.parse(byteStream);
        
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }
        

        initialized = true;

        docRoot = doc.getDocumentElement();
        rootElem = doc.getDocumentElement().getNodeName();
       
        DocumentType dt = doc.getDoctype();
        
        if(dt != null)
        	hasDoctype = true;
        
        
        return doc.getDocumentElement().getNodeName();
        
    }
    
    public String parseXml(File file) throws ParserConfigurationException, IOException, SAXException, Exception {
        
        /*uncommented by exalto 16/1/05 */
        CatalogResolver cr = null;
        
        cr = new CatalogResolver();
        Catalog myCatalog = cr.getCatalog();
        //   myCatalog.loadSystemCatalogs();
        
        if(catalogFile != null)
            myCatalog.parseCatalog(catalogFile);
        
        //  File fileObj = new File(file);
        System.out.println(" in parsexml file");
        dbf=DocumentBuilderFactory.newInstance();
        db= dbf.newDocumentBuilder();
        dbf.setValidating(false);
      
        if(catalogFile != null)
            db.setEntityResolver(cr);
        else {
        	CustomEntityResolver cres = CustomEntityResolver.getInstance();
        	db.setEntityResolver(cres);
        }
        	
        doc = db.parse(new FileInputStream(file.getAbsolutePath()));
        initialized = true;
        
        docRoot = doc.getDocumentElement();
        rootElem = doc.getDocumentElement().getNodeName();
    
        DocumentType dt = doc.getDoctype();
        
        if(dt != null)
        	hasDoctype = true;
        
    
        return doc.getDocumentElement().getNodeName();
       
        
    }

    public class DocTypeDefn {
        String name;
        String pubId;
        String sysId;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPubId() {
			return pubId;
		}
		public void setPubId(String pubId) {
			this.pubId = pubId;
		}
		public String getSysId() {
			return sysId;
		}
		public void setSysId(String sysId) {
			this.sysId = sysId;
		}
        
    }
    
    public DocTypeDefn getDTD() {
    	
        DocumentType dt = doc.getDoctype();

        DocTypeDefn docDefn = new DocTypeDefn();
        docDefn.setName(dt.getName());
        docDefn.setPubId(dt.getPublicId());
        docDefn.setSysId(dt.getSystemId());
        
        return docDefn;
        
    }
    
    public String getSchemaFile() {
        try {
            System.out.println(" xtree in grntext " + doc.getDocumentElement());
            Node root = doc.getDocumentElement();
            
            if(root != null) {
                
                NamedNodeMap  attribs = root.getAttributes();
                if( attribs != null ) {
                    System.out.println(" xtree ATTRIBS LEN " + attribs.getLength());
                    
                    for( int i = 0; i < attribs.getLength(); i++ ) {
                        Node attribNode = attribs.item(i);
                        String nodeName = attribNode.getNodeName().trim();
                        System.out.println(" xtree name " + nodeName);
                        
                        if(nodeName.startsWith("xsi:")) {
                            int eqpos = nodeName.indexOf("=");
                            String schemaFile = attribNode.getNodeValue().trim();
                            
                            return schemaFile;
                        }
                    }
                }
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public String getRootElementName() throws Exception {
        return docRoot.getNodeName();
    }
    
    public Element getRootElement() throws Exception {
        return (Element) docRoot;
    }
    
}

