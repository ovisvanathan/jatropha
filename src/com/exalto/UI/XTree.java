package com.exalto.UI;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.apache.log4j.Logger;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.tools.CatalogResolver;

import com.exalto.util.XmlUtils;
import com.exalto.util.ExaltoResource;
import com.exalto.ColWidthTypes;
import com.exalto.util.StatusEvent;


public class XTree extends JTree {

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

	 private           DefaultMutableTreeNode      treeNode;
	private Logger logger;	 
	private boolean initialized = false;
	private String catalogFile = null;
	private XmlEditor xeditor;
	private XmlUtils xutils;
	
	
/*
	public XTree( String text ) throws ParserConfigurationException {
		this();
		 logger = Logger.getLogger(XTree.class.getName());
		refresh( text );
	} //end XTree( String text )
*/	

	/*** This constructor builds the generic portion of the XTree object that 
is true for
	* any XTree object. It includes a default tree model
	** @exception ParserConfigurationException
	This exception is potentially thrown if
	* the constructor configures the parser improperly. It won't.*/

	public XTree() throws ParserConfigurationException, FileNotFoundException {
	// Initialize the superclass portion of the object
		super();

 	    logger = Logger.getLogger(XTree.class.getName());

		xutils = XmlUtils.getInstance();

	//	catalogFile = System.getProperty("xml.catalog.files");	
	
		catalogFile = xutils.getCatalogFile();
							
		System.out.println("&&&&&&&&&&&^^%%%%%%%%%%%%%%%%%%%%%%%%%%");
		System.out.println("&&&&&&&" + catalogFile + "%%%%%%%%%%%%%%");
		System.out.println("&&&&&&&&&&&^^%%%%%%%%%%%%%%%%%%%%%%%%%%");
		
	//	File cfile = new File(catalogFile);
		
		xeditor = XmlEditor.getInstance("");
	//	xeditor.setCatalogFile(catalogFile);
				
	//	if(!cfile.exists()) {
	//		throw new FileNotFoundException();
	//	}
		    

		

		// Set basic properties for the Tree rendering
		getSelectionModel().setSelectionMode( 
TreeSelectionModel.SINGLE_TREE_SELECTION );
		setShowsRootHandles( true );
		setEditable( false );

		// A more advanced version of this tool would allow the Tree to be editable
		// Begin by initializing the object's DOM parsing objects
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating( false );
		db = dbf.newDocumentBuilder();
		// Take the DOM root node and convert it to a Tree model for the JTree

		setModel( buildWelcomeTree() );
	} //end XTree()

	/**
	 * This method builds a default DefaultTreeModel that can then be used to 
construct
	 * the graphical tree structure from that data*/

	 private DefaultTreeModel buildWelcomeTree() {
	 	DefaultMutableTreeNode root;
		DefaultMutableTreeNode instructions, openingDoc, editingDoc, savingDoc;
		DefaultMutableTreeNode openingDocText, editingDocText, savingDocText;
		DefaultMutableTreeNode development, addingFeatures, contactingKyle;
		root = new DefaultMutableTreeNode( "Welcome to XML View 1.0" );
		instructions = new DefaultMutableTreeNode( "Instructions" );
		openingDoc = new DefaultMutableTreeNode( "Opening XML Documents" );
		openingDocText = new DefaultMutableTreeNode(
			"When invoking the XmlEditor fromthe command-line, you must specify the filename." );
		editingDoc = new DefaultMutableTreeNode( "Editing an XML Document" );
		editingDocText = new DefaultMutableTreeNode(
		"XML text in the right hand framecan be edited directly. The \"refresh\" button will rebuild the JTree in the leftframe." );

		savingDoc = new DefaultMutableTreeNode( "Saving an XML Document" );
		savingDocText = new DefaultMutableTreeNode(
		"This iteration of the XmlEditor doesnot provide the ability to save your document. That will come with the next article." );
		root.add( instructions );
		instructions.add( openingDoc );
		instructions.add( editingDoc );
		openingDoc.add( openingDocText );
		editingDoc.add( editingDocText );
		return new DefaultTreeModel( root );
	} //end buildWelcomeTree()


	public void refresh(File file) throws Exception {
	//	try {
		
	//	String xmlFile = file.getName();

		if(treeNode != null) 	
			treeNode.removeAllChildren();
		docRoot = parseXml( file);	
		treeNode = createTreeNode(docRoot); 
		setModel( new DefaultTreeModel( treeNode ) );

		
	}
	
	public void refresh(String xmlText) throws Exception { 
			try {
			
			
			if(treeNode != null) 	
				treeNode.removeAllChildren();
			docRoot = parseXml( xmlText);	
			treeNode = createTreeNode(docRoot); 
			setModel( new DefaultTreeModel( treeNode ) );
			} catch(ParserConfigurationException pce) {
				logger.error("cannot refresh xtree");
			}catch(SAXException se) {
				logger.error("cannot refresh xtree");
			} 
			catch(IOException ioe) {
				logger.error("cannot refresh xtree");
			}
	}
	
	
	
	public void clear() {
		
		if(treeNode != null) 	
			treeNode.removeAllChildren();
	}

	   /**
	    * This takes a DOM Node and recurses through the children until each 
one is added
	    * to a DefaultMutableTreeNode. The JTree then uses this object as a 
tree model.
	    *
	    * @param root org.w3c.Node.Node
	    *
	    * @return Returns a DefaultMutableTreeNode object based on the root 
Node passed in
	    */
	   private DefaultMutableTreeNode createTreeNode( Node root )
	   {
	      DefaultMutableTreeNode  treeNode = null;
	      String                  type, name, value;
	      NamedNodeMap            attribs;
	      Node                    attribNode;

	      // Get data from root node
	      type = getNodeType( root );
	      name = root.getNodeName();
	      value = root.getNodeValue();
		  
	      // Special case for TEXT_NODE
	      treeNode = new DefaultMutableTreeNode( root.getNodeType() == 
Node.TEXT_NODE ? value : name );

	      // Display the attributes if there are any
	      attribs = root.getAttributes();
	      if( attribs != null )
	      {
	         for( int i = 0; i < attribs.getLength(); i++ )
	         {
	            attribNode = attribs.item(i);
	            name = attribNode.getNodeName().trim();
	            value = attribNode.getNodeValue().trim();

	            if ( value != null )
	            {
	               if ( value.length() > 0 )
	               {
	                  treeNode.add( new DefaultMutableTreeNode( "[Attribute] --> " + name + "=\"" + value + "\"" ) );
	               } //end if ( value.length() > 0 )
	            } //end if ( value != null )
	         } //end for( int i = 0; i < attribs.getLength(); i++ )
	      } //end if( attribs != null )

	      // Recurse children nodes if any exist
	      if( root.hasChildNodes() )
	      {
	         NodeList             children;
	         int                  numChildren;
	         Node                 node;
	         String               data;

	         children = root.getChildNodes();
	         // Only recurse if Child Nodes are non-null
	         if( children != null )
	         {
	            numChildren = children.getLength();

	            for (int i=0; i < numChildren; i++)
	            {
	               node = children.item(i);
	               if( node != null )
	               {
	                  // A special case could be made for each Node type.
	                  if( node.getNodeType() == Node.ELEMENT_NODE )
	                  {
	                     treeNode.add( createTreeNode(node) );
	                  } //end if( node.getNodeType() == Node.ELEMENT_NODE )

	                  data = node.getNodeValue();

	                  if( data != null )
	                  {
	                     data = data.trim();
	                     if ( !data.equals("\n") && !data.equals("\r\n") && 
data.length() > 0 )
	                     {
	                        treeNode.add(createTreeNode(node));
	                     } //end if ( !data.equals("\n") && !data.equals("\r\n") && data.length() > 0 )
	                  } //end if( data != null )
	               } //end if( node != null )
	            } //end for (int i=0; i < numChildren; i++)
	         } //end if( children != null )
	      } //end if( root.hasChildNodes() )
	      return treeNode;
	   } //end createTreeNode( Node root )


	   /**
    * This method returns a string representing the type of node passed in.
    *
    * @param node org.w3c.Node.Node
    *
    * @return Returns a String representing the node type
    */
   private String getNodeType( Node node )
   {
      String type;

      switch( node.getNodeType() )
      {
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



	public Node parseXml(String text) throws ParserConfigurationException, IOException, SAXException, Exception {

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
			DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
			DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
			docbuilderfact.setValidating(false);
			
			if(catalogFile != null) 
				docbuilder.setEntityResolver(cr);
			
	   	    doc = db.parse(byteStream);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			throw e;
		}

		initialized = true;
		
		return ( Node )doc.getDocumentElement();

	}

	public Node parseXml(File file) throws ParserConfigurationException, IOException, SAXException, Exception {

		/*uncommented by exalto 16/1/05 */
		CatalogResolver cr = null;
		
	//	try {
		
		//	if(catalogFile == null) 
		//		throw new FileNotFoundException();
			
		
			cr = new CatalogResolver();
			Catalog myCatalog = cr.getCatalog();
		 //   myCatalog.loadSystemCatalogs();
		
			if(catalogFile != null) 
				myCatalog.parseCatalog(catalogFile);
		    
			

      //  File fileObj = new File(file);
		System.out.println(" in parsexml file");
	    DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
        DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
		docbuilderfact.setValidating(false);
		
		if(catalogFile != null) 
			docbuilder.setEntityResolver(cr);
			
 	    doc = docbuilder.parse(new FileInputStream(file.getAbsolutePath()));
		initialized = true;
		return (Node)doc.getDocumentElement();
	
	//	return null;
		
	}
	
	public String getSchemaFile() {
		try {
		System.out.println(" xtree in grntext " + doc.getDocumentElement());
		Node root = doc.getDocumentElement();
		
		if(root != null) {
		
			NamedNodeMap  attribs = root.getAttributes();
			if( attribs != null )
			{
				System.out.println(" xtree ATTRIBS LEN " + attribs.getLength());
			
				for( int i = 0; i < attribs.getLength(); i++ )
				{
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
