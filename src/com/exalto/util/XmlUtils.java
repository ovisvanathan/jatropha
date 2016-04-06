package com.exalto.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.exalto.ColWidthTypes;
import com.exalto.CustomEntityResolver;
import com.exalto.UI.XmlEditor;
import java.util.Enumeration;

/**
 * @author 510342
 *
 * This class is the controller and is responsible for calling the 
 * right method  and returning the results as 
 * an array list 
 */
public class XmlUtils implements ColWidthTypes {
	
	private Hashtable commands;
	private static ResourceBundle resources;
    private static Hashtable _keycodes;
	protected String _current_dir = null;
	private static XmlUtils xutils;
	public static Font defaultFont = new Font("Serif", Font.BOLD, 12);

	
	private           DocumentBuilderFactory 	dbf;
	private           DocumentBuilder 		db;
	private           Document                    doc;

	XmlEditor xeditor;
	
	
	/****** OMP added begin 5/2/05 schema validation **************/
	static final String JAXP_SCHEMA_LANGUAGE =
	        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	    static final String W3C_XML_SCHEMA =
	        "http://www.w3.org/2001/XMLSchema";
	    static final String JAXP_SCHEMA_SOURCE =
	        "http://java.sun.com/xml/jaxp/properties/schemaSource";
	/****** OMP added end 5/2/05 schema validation **************/
	
	private static String catalogFile;
	
	private String [] predefinedEntities = { "amp", "apos", "gt", "lt", "quot" };

	
	static {
		
        try {
      
        	resources = ResourceBundle.getBundle("resources.XmlEditor", 
                                                 Locale.getDefault());
			catalogFile = System.getProperty("xml.catalog.files");	
		
			
			System.out.println(" static catalog file = " + catalogFile);
												 
        } catch (MissingResourceException mre) {
            System.err.println("resources/XmlEditor.properties not found");
            System.exit(1);
        }
    }
    
   // public XmlUtils() {
   	  private XmlUtils() { 
      
   	  }
	  
	  public static XmlUtils getInstance() {
	  
	  	if(xutils == null) {
			xutils = new XmlUtils();
			return xutils;
		}
		
		return xutils;
	  }
	  
	  
    
   /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundries.  This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    protected String[] tokenize(String input) {
	Vector v = new Vector();
	StringTokenizer t = new StringTokenizer(input);
	String cmd[];

	while (t.hasMoreTokens())
	    v.addElement(t.nextToken());
	cmd = new String[v.size()];
	for (int i = 0; i < cmd.length; i++)
	    cmd[i] = (String) v.elementAt(i);

	return cmd;
    }
    
    
    public static String getResourceString(String nm) {
	String str;
	try {
		
	    str = resources.getString(nm);

    //        System.out.println(" in xutils val = " + str);	
	 
	} catch (MissingResourceException mre) {
	    str = null;
	}
	return str;
    }

    public ResourceBundle getResourceBundle() {
    	return resources;
    }

    public URL getResource(String key) {
    
//    System.out.println(" in xutils GR key = " + key);	
    	
	String name = getResourceString(key);
	if (name != null) {
		URL url = this.getClass().getResource(name);

//	    System.out.println(" in xutils GR url = " + url);	
		
	    return url;
	}
	return null;
    }
    
    public void centerDialog(JDialog jdlg) {
        Dimension screenSize = jdlg.getToolkit().getScreenSize();
		Dimension size = jdlg.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		jdlg.setLocation(x,y);
	}


    public void centerDialog(JFrame jfr) {
        Dimension screenSize = jfr.getToolkit().getScreenSize();
		Dimension size = jfr.getSize();
		screenSize.height = screenSize.height/2;
		screenSize.width = screenSize.width/2;
		size.height = size.height/2;
		size.width = size.width/2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		jfr.setLocation(x,y);
	}
	
	 /**
	   * Sets the items in <code>aComponents</code> to the same size.
	   *
	   * Sets each component's preferred and maximum sizes. 
	   * The actual size is determined by the layout manager, whcih adjusts 
	   * for locale-specific strings and customized fonts. (See this 
	   * <a href="http://java.sun.com/products/jlf/ed2/samcode/prefere.html">Sun doc</a> 
	   * for more information.)
	   *
	   * @param aComponents contains <code>JComponent</code> objects.
	   */
	  public static void equalizeSizes(java.util.List aComponents) {
	    Dimension targetSize = new Dimension(0,0);
	    Iterator compsIter = aComponents.iterator();
	    double width = 0.0;
		double height = 0.0;
		while ( compsIter.hasNext() ) {
		  Object obj = compsIter.next();	
	      if(obj instanceof String) 
		  	continue;
			
			JComponent comp = (JComponent) obj;
			Dimension compSize = comp.getPreferredSize();
			width = Math.max(targetSize.getWidth(), compSize.getWidth());
			height = Math.max(targetSize.getHeight(), compSize.getHeight());
	   	}
		
//			System.out.println("width = " + width);
//			System.out.println("height = " + height);
		
		targetSize.setSize(width, height);
//		System.out.println("dmd = " + targetSize);
		
	    setSizes(aComponents, targetSize);
  	}
  
   public static void setSizes(java.util.List aComponents, Dimension aDimension){
      Iterator compsIter = aComponents.iterator();      
      while ( compsIter.hasNext() ) {
	  	Object obj = compsIter.next();
		if(obj instanceof String)
			continue;
		JComponent comp = (JComponent) obj;
       
		comp.setPreferredSize( (Dimension)aDimension.clone() );
//        System.out.println("pref size = " + comp.getPreferredSize());
		comp.setMaximumSize( (Dimension)aDimension.clone() );
//        System.out.println("max size = " + comp.getMaximumSize());
		
	  }
  }
  
  
  /** This method clears the dialog and hides it. */
      public void clearAndHide(JComponent [] jcomp, JDialog jdlg) {
        	for(int i=0;i<jcomp.length;i++) {
				if(jcomp[0] instanceof JTextField) {
					JTextField tf = (JTextField) jcomp[0];
					tf.setText("");
				}
			}				
          jdlg.setVisible(false);
    }
	
	 /**
	  * Return a square icon which paints nothing, and whose dimensions correspond 
	  * to the user preference for icon size.
	  *
	  * <P>A common problem occurs with text alignment in menus, where there is 
	  * a mixture of menu items with and without an icon. Adding an empty icon 
	  * to menu items which do not have one will adjust its alignment.
	  */
	public  static Icon getEmptyIcon(){
	//    GeneralLookPreferencesEditor prefs = new GeneralLookPreferencesEditor();
	    return EmptyIcon.SIZE_24;
	  }
 
 
  public Icon getFrameIcon() 
     {
 		Icon rtn = null;
 		
 //		Document doc = _xmlFile.getDocument();
 		// get the doctype
 //		DocumentType doctype = doc.getDoctype();
 //		if (doctype != null) {
 //			String nm = doctype.getName();
 //			rtn = XMLEditorSettings.getSharedInstance().getIcon(nm, colWidthTypes.SMALL_ICON);
 //		}
 		if (rtn == null) {
 			rtn = getAppIconSmall();
 		}
 		
 		return rtn;	
    }
	
		
		    public ImageIcon getAppIconSmall() 
		    {
		        return (loadImageFromProp(ColWidthTypes.APP_ICON_SMALL));
		        
		    }
		    public ImageIcon getAppIconLarge()
		    {
		         return (loadImageFromProp(ColWidthTypes.APP_ICON_LARGE));
		    }

			protected ImageIcon loadImageFromProp(String propname) 
			{
			//    System.out.println(" in xutils propname = " + propname);	
				
				String val = getResourceString(propname);
				if (val != null) {
		//		    System.out.println(" in xutils val = " + val);	
					
					return ExaltoResource.loadImage(val,getResourceString(ColWidthTypes.ICON_LOADER));
				}
				return null;
				
			}
			
			
		
		public void validate(String fileNameText, String inputType, XmlEditor xeditor, boolean checkValid, String schemaFile) throws Exception {
		
					XMLReader parser = null;
					StringBuffer sb = new StringBuffer();
					// Create a JAXP SAXParserFactory and configure it
					// OMP added 5/2/05 begin
					String inFile = null;
					File iFile = null;
					
					String idePath = System.getProperty("user.dir");
					System.out.println(" idepath = " + idePath); 
					
				//	System.out.println(" filenametext = " + fileNameText); 
                
                    String  schemaFileName = null;
		
					System.out.println(" schemaFile = " + schemaFile); 
					try {
						
						if("FILE".equals(inputType)) 
						{
							iFile = new File(fileNameText);
							inFile = iFile.getAbsolutePath();

						} else if("TEXT".equals(inputType)) {
							inFile = idePath + "\\";
						}
						
							if(schemaFile != null && ( (schemaFile.indexOf("\\") == -1)
								&& (schemaFile.indexOf("/") == -1) ) ) {

/*
								if(inFile != null) {
									int spos = inFile.lastIndexOf("\\");	
									if(spos != -1) {
										String schemaFilePath = inFile.substring(0, spos);
										System.out.println(" schemaFilepath = " + schemaFilePath); 
										schemaFile = schemaFilePath + "\\" + schemaFile;
										System.out.println(" new schemaFile = " + schemaFile); 
									}
								}
	*/
							} 
							else if(schemaFile != null) {
                                  schemaFileName = getSchemaFileName(schemaFile);
                        		  System.out.println(" schema file 2 = " + schemaFileName); 
							}
                    	} catch(Exception e) {
							e.printStackTrace();
							xeditor.fireStatusChanged(new StatusEvent("document.validate.cannot", ColWidthTypes.NOERROR));	  						 
						}
						
					
					try {
					
					String parserFac = System.getProperty("javax.xml.parsers.SAXParserFactory");

					if(parserFac == null) {
						System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
					}

					
					SAXParserFactory spf = SAXParserFactory.newInstance();
					
					// Set namespaceAware to true to get a parser that corresponds to
					// the default SAX2 namespace feature setting.  This is necessary
					// because the default value from JAXP 1.0 was defined to be false.
					spf.setNamespaceAware(true);
			        // Validation part 1: set whether validation is on
			        spf.setValidating(checkValid);
		
			        // Create a JAXP SAXParser
			        SAXParser saxParser = spf.newSAXParser();
					
					
					// Validation part 2a: set the schema language if necessary
				   if (schemaFile != null || (schemaFileName != null && !("".equals(schemaFileName)))) {
           
						try {
							saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
						
						} catch (SAXNotRecognizedException x) {
							// This can happen if the parser does not support JAXP 1.2
							System.err.println(
								"Error: JAXP SAXParser property not recognized: "
								+ JAXP_SCHEMA_LANGUAGE);
							System.err.println(
								"Check to see if parser conforms to JAXP 1.2 spec.");
							System.exit(1);
						}


		        	}

		// PREREL M BEGIN  
		// Set the EntityResolver before parsing
							CatalogResolver cr = new CatalogResolver();
							Catalog clog = cr.getCatalog();
                            String  esSys = null;
							
					if(catalogFile != null) {				
            
						 clog.parseCatalog(catalogFile);

							esSys = clog.resolveSystem(schemaFileName);

							System.out.println(" xutils resolveSystem " + esSys); 
							          
							String  esSys1 = esSys.replaceAll("%5C","/");	
							
							System.out.println(" xutils resolveSystem 2" + esSys1); 

					    saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(esSys1));
		        	

					} else {
					
									if (schemaFile != null || schemaFileName != null) {
										    saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaFile));
									}
					}

					// PREREL M END
					
					// Get the encapsulated SAX XMLReader
							XMLReader xmlReader = saxParser.getXMLReader();
					
					        // Set the ContentHandler of the XMLReader
							//  xmlReader.setContentHandler(new DefaultHandler());
					
					        // Set an ErrorHandler before parsing
					        xmlReader.setErrorHandler(new MyErrorHandler(System.err));
							
							// PREREL C MOVED ABOVE 2 LINES
							// Set the EntityResolver before parsing
						//x	CatalogResolver cr = new CatalogResolver();
						//x	Catalog clog = cr.getCatalog();
							

							//	System.out.println(" xutils catalogfile " + xeditor.getCatalogFile()); 
								
							//	File cfile = new File(xeditor.getCatalogFile());
								
							//	if(!cfile.exists())
							//		System.out.println(" xutils catalogfile DOES NOT EXIST"); 
							
					if(catalogFile != null) {				
					// PREREL C MOVED ABOVE
                     //x    clog.parseCatalog(catalogFile);

						 
						 xmlReader.setEntityResolver(cr);
					}  else if(!checkValid){
			        	CustomEntityResolver cres = CustomEntityResolver.getInstance();
			        	xmlReader.setEntityResolver(cres);
			        }
					
					        // Tell the XMLReader to parse the XML document
							if("FILE".equals(inputType)) {
								xmlReader.parse(convertToFileURL(iFile.getAbsolutePath()));
					    	} else {
								xmlReader.parse(new InputSource(new StringReader(fileNameText))); 
							}

					    	String msg = (checkValid) ? ExaltoResource.getString(ColWidthTypes.ERR, "doc.well.formed.valid") 
					    			: ExaltoResource.getString(ColWidthTypes.ERR, "doc.well.formed");
							xeditor.fireStatusChanged(new StatusEvent(msg, ColWidthTypes.NOERROR));
					    	
					} 
					catch (SAXParseException e) { // well-formedness error
								        System.out.println("document is not well formed.");
								        System.out.println(e.getMessage()
								         + " at over here line " + e.getLineNumber() 
								         + ", column " + e.getColumnNumber());
										 
										 sb.append(e.getMessage()
										 + " at over here line " + e.getLineNumber() 
								         + ", column " + e.getColumnNumber());	    	
								
										String [] fmtArgs = new String[3];
										fmtArgs[0] = new Integer(e.getLineNumber()).toString();
										fmtArgs[1] = new Integer(e.getColumnNumber()).toString();
										fmtArgs[3] = e.getMessage();
										
										String statusMsg = getFormattedMsg(ExaltoResource.getString(ColWidthTypes.ERR, "well.formed.valid.err"), fmtArgs);	
										
										xeditor.fireStatusChanged(new StatusEvent(statusMsg, ColWidthTypes.VALIDATION));	  						 
								//		xeditor.setStatus(sb.toString());
									
									}
								      catch (SAXException e) { // some other kind of error
								        System.out.println("here" + e.getMessage());
										
										String [] fmtArgs = new String[1];
										fmtArgs[0] = e.getMessage();
										
										String statusMsg = getFormattedMsg(ExaltoResource.getString(ColWidthTypes.ERR, "sax.exception"), fmtArgs);
										xeditor.fireStatusChanged(new StatusEvent(statusMsg, ColWidthTypes.VALIDATION));	  						 
					
							
								      }
								      catch (IOException e) {
								        System.out.println("Could not check document because of the IOException " + e);
									//	xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(colWidthTypes.ERR, "sax.parse.io.err"), 0));	  
								  		throw e;
						}
					
					
					// OMP added 5/2/05 end
		
		}
  
		public void validate(File inputFile, XmlEditor xeditor, boolean checkValid, String schemaFile) throws Exception {
		
			validate(inputFile.getAbsolutePath(), "FILE", xeditor, checkValid, schemaFile);
		}
		
		public void validate(String inText, XmlEditor xeditor, boolean checkValid, String schemaFile) throws Exception {				

			validate(inText, "TEXT", xeditor, checkValid, schemaFile);
		}

			
		// This method needs to be upgraded to validate against schema OMP 16/01/2005
		
		/*
		try {
			 	parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
				if(checkValid) {
					 parser.setFeature(
						   "http://xml.org/sax/features/validation", true);
						  parser.setErrorHandler(new ValidityErrorReporter());
				}
				//TODO : OMP
				// handle schema validation here
				
			}
			catch (SAXNotRecognizedException e) {
			      System.err.println(
			       "Installed XML parser cannot validate;"
			       + " checking for well-formedness instead...");
			    } 
			    catch (SAXNotSupportedException e) {
			      System.err.println(
			       "Cannot turn on validation here; "
			       + "checking for well-formedness instead...");
			    }
				catch (SAXException e) {
		//		  System.err.println("Caught sax exception... Exiting");
		
		     }
			 
		try {
				parser.parse(inputFile.getAbsolutePath());
	//			xeditor.setStatus("Document is well-formed and valid");	
				xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(colWidthTypes.ERR, "doc.well.formed.valid"), colWidthTypes.NOERROR));	  				
			} 
			catch (SAXParseException e) { // well-formedness error
			        System.out.println("document is not well formed.");
			        System.out.println(e.getMessage()
			         + " at over here line " + e.getLineNumber() 
			         + ", column " + e.getColumnNumber());
					 
					 sb.append(e.getMessage()
					 + " at over here line " + e.getLineNumber() 
			         + ", column " + e.getColumnNumber());	    	
			
					String [] fmtArgs = new String[3];
					fmtArgs[0] = new Integer(e.getLineNumber()).toString();
					fmtArgs[1] = new Integer(e.getColumnNumber()).toString();
					fmtArgs[3] = e.getMessage();
					
					String statusMsg = getFormattedMsg(ExaltoResource.getString(colWidthTypes.ERR, "well.formed.valid.err"), fmtArgs);	
					
					 xeditor.fireStatusChanged(new StatusEvent(statusMsg, colWidthTypes.ERROR));	  						 
			//		xeditor.setStatus(sb.toString());
				
				}
			      catch (SAXException e) { // some other kind of error
			        System.out.println("here" + e.getMessage());
					
					String [] fmtArgs = new String[1];
					fmtArgs[0] = e.getMessage();

					String statusMsg = getFormattedMsg(ExaltoResource.getString(colWidthTypes.ERR, "sax.exception"), fmtArgs);
					xeditor.fireStatusChanged(new StatusEvent(statusMsg, colWidthTypes.ERROR));	  						 

		
			      }
			      catch (IOException e) {
			        System.out.println("Could not check document because of the IOException " + e);
				//	xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(colWidthTypes.ERR, "sax.parse.io.err"), 0));	  
			  		throw e;
				}
			*/	
				

	
		public void validate(String text, XmlEditor xeditor, boolean checkValid) throws Exception {
		
		     DocumentBuilderFactory 	dbf;
		     DocumentBuilder 		db;
    	  //   Document                    doc;
		     ByteArrayInputStream	byteStream;
		 
		// Begin by initializing the object's DOM parsing objects
		dbf = DocumentBuilderFactory.newInstance();
			
				
		     byteStream = new ByteArrayInputStream( text.getBytes() );
			
			  try
			  {
			 
			 	if(checkValid) 
				 	dbf.setValidating(true);
		
				db = dbf.newDocumentBuilder();
		
				System.out.println(" in xutils valid parsing checkValid =" + checkValid);
				db.setErrorHandler(new ValidityErrorReporter());
	 
				db.parse( byteStream );
		
				 String msg = (checkValid)? "valid" : "well-formed";
				 
				System.out.println(" in xutils msg =" + msg);
 			String statusMsg = null;
			if(checkValid) 
				statusMsg = "doc.well.formed.valid";
			else	
				statusMsg = "doc.well.formed";
				
	 			xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR, statusMsg), ColWidthTypes.NOERROR));	  				
				
	
	//			 xeditor.setStatus("Document is " + msg);
			  	System.out.println(" in xutils finished parsing ");

			  }
			  catch ( Exception e )
			  {
			  	throw e;
	//			e.printStackTrace();
	//			System.exit(0);
  			  }
			
		}
	
                
        private String getSchemaFileName(String sfile) {
            
            String schemaFileName = null;
            String idePath = System.getProperty("user.dir");
			
            java.util.StringTokenizer stk = new StringTokenizer(sfile, " ");
            
           int n = stk.countTokens();
           
           if(n%2 == 0) {
               
               while(stk.hasMoreTokens()) {
                   String nmsp = stk.nextToken();
                   
                   schemaFileName = stk.nextToken();
                   
                   break;
               }
               
               
           } else {
               while(stk.hasMoreTokens()) {
                   schemaFileName = stk.nextToken();
               }
           }
               	String sFile = idePath + "\\" + schemaFileName;
               return sFile;
        }        

	public void prettyPrint(String text, XmlEditor editor) {
	
		String tempFile = "PrettyPrint.xml";
		
		try {

			File tfile = new File(tempFile);
			FileOutputStream pstr = new FileOutputStream(tempFile.toString());				
		
		//	 	parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

			 	
				XMLReader parser = null;
								
				
					String parserFac = System.getProperty("javax.xml.parsers.SAXParserFactory");

					if(parserFac == null) {
						System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
					}

				
					SAXParserFactory spf = SAXParserFactory.newInstance();
				
					// Set namespaceAware to true to get a parser that corresponds to
					// the default SAX2 namespace feature setting.  This is necessary
					// because the default value from JAXP 1.0 was defined to be false.
					spf.setNamespaceAware(true);
			        // Validation part 1: set whether validation is on
			        spf.setValidating(false);
	
			        // Create a JAXP SAXParser
			        SAXParser saxParser = spf.newSAXParser();
				
				
			        // Get the encapsulated SAX XMLReader
					parser = saxParser.getXMLReader();
			 	
			 	
					 // Install the Document Handler      
					 parser.setFeature(
						   "http://xml.org/sax/features/validation", false);
				
					// Set the EntityResolver before parsing
						CatalogResolver cr = new CatalogResolver();
						Catalog clog = cr.getCatalog();

					if(catalogFile != null) {
						System.out.println("catalogFile is not null");				
						System.out.println(" xutils catalogfile " + catalogFile); 				
						File cfile = new File(catalogFile); 
						if(cfile.exists())  {
							System.out.println(" xutils catalogfile DOES EXIST"); 
							clog.parseCatalog(catalogFile);
							parser.setEntityResolver(cr);
						}	
					}
				
					PrettyPrinter3 prettyPrinter3 = new PrettyPrinter3(pstr);
					parser.setContentHandler(prettyPrinter3);
				
				  try {
			         parser.parse(new InputSource(new StringReader(text))); 
			      }
			      catch (SAXParseException e) { // well-formedness error
				  
				  		String [] fmtArgs = new String[3];
				  		fmtArgs[0] = new Integer(e.getLineNumber()).toString();
				  		fmtArgs[1] = new Integer(e.getColumnNumber()).toString();
				  		fmtArgs[2] = e.getMessage();
				  					
				  		String statusMsg = getFormattedMsg(ExaltoResource.getString(ColWidthTypes.ERR, "well.formed.valid.err"), fmtArgs);	
				  					
				   	    editor.fireStatusChanged(new StatusEvent(statusMsg, ColWidthTypes.ERROR));	  						 
				  }		

				//	XMLOutputter outp = new XMLOutputter();
				//	outp.output(prettyPrinter3, pstr);
		
					System.out.println("in xutils after parsing");
					editor.performNewAction(false, tfile.getAbsolutePath(), false);
			
				
				//	if(tmpFile.exists()) {
				//		System.out.println("removing temp file");
				//		tmpFile.remove();
				//	}
		
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
			
	}
	
	public void prettyPrint(File infile, XmlEditor editor) {
	
			String tempFile = "PrettyPrint.xml";
			XMLReader parser = null;
			
			try {
		
			File tfile = new File(tempFile);
			FileOutputStream pstr = new FileOutputStream(tfile.getPath());				

		//	 	parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		
			String parserFac = System.getProperty("javax.xml.parsers.SAXParserFactory");

			if(parserFac == null) {
				System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
			}

		
			SAXParserFactory spf = SAXParserFactory.newInstance();
		
			// Set namespaceAware to true to get a parser that corresponds to
			// the default SAX2 namespace feature setting.  This is necessary
			// because the default value from JAXP 1.0 was defined to be false.
			spf.setNamespaceAware(true);
	        // Validation part 1: set whether validation is on
	        spf.setValidating(false);

	        // Create a JAXP SAXParser
	        SAXParser saxParser = spf.newSAXParser();
		
		
	        // Get the encapsulated SAX XMLReader
			parser = saxParser.getXMLReader();
	 	
	 	
			 // Install the Document Handler      
			 parser.setFeature(
				   "http://xml.org/sax/features/validation", false);
		
			// Set the EntityResolver before parsing
				CatalogResolver cr = new CatalogResolver();
				Catalog clog = cr.getCatalog();

			if(catalogFile != null) {
				System.out.println("catalogFile is not null");				
				System.out.println(" xutils catalogfile " + catalogFile); 				
				File cfile = new File(catalogFile); 
				if(cfile.exists())  {
					System.out.println(" xutils catalogfile DOES EXIST"); 
					clog.parseCatalog(catalogFile);
					parser.setEntityResolver(cr);
				}	
			}

			
			 	// Install the Document Handler     
	 			PrettyPrinter3 prettyPrinter3 = new PrettyPrinter3(pstr);
				parser.setContentHandler(prettyPrinter3);
					
				  try {
			        parser.parse(infile.getPath()); 
			      }
			      catch (SAXParseException e) { // well-formedness error
			  		String [] fmtArgs = new String[3];
					fmtArgs[0] = new Integer(e.getLineNumber()).toString();
					fmtArgs[1] = new Integer(e.getColumnNumber()).toString();
					fmtArgs[3] = e.getMessage();

					String statusMsg = getFormattedMsg(ExaltoResource.getString(ColWidthTypes.ERR, "well.formed.valid.err"), fmtArgs);	

					editor.fireStatusChanged(new StatusEvent(statusMsg, ColWidthTypes.ERROR));	  						 
				  }		
/*	
			
			MyInternalFrame myFrame = (MyInternalFrame) editor.getDesktopView().getSelectedFrame();
			Editor comp = (Editor) myFrame.getTextComponent();
		//	comp.setCaretPosition(0);
		//	comp.setFile(tfile);
		//	comp.open();
			BufferedReader br = new BufferedReader(new FileReader(tfile));
			comp.read(br, null);
*/		
			editor.performNewAction(false, tfile.getAbsolutePath(), false);
		
		
		//	display(new File(tempFile));
			
			System.out.println("in xutils after display");
					
				if(tfile.exists()) {
					System.out.println("removing temp file");
					tfile.delete();
				}
			
			} catch(FileNotFoundException fe) {
				fe.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
	
	}



	/**
	 * loads up the resources for an action
	 */
	public static void loadActionResources(Action a, 
										   String bundle,
										   String keyprefix)
	{
		// name
		try {
			a.putValue(Action.NAME, getResourceString(keyprefix));
		}
		catch (MissingResourceException ex){
			System.out.println(" missing name");
		}

		// icon
		try {
			a.putValue(Action.SMALL_ICON,ExaltoResource.getImage(bundle,keyprefix+".icon"));
		}
		catch (MissingResourceException ex){
				System.out.println(" missing smallicon");
	
		}

		// tooltip text
		try {
			a.putValue(Action.SHORT_DESCRIPTION, getResourceString(keyprefix+".tt"));
		}
		catch (MissingResourceException ex){
				System.out.println(" missing toltip");
	
		}
		
		try {
			a.putValue("menu.icon",ExaltoResource.getImage(bundle,keyprefix+".micon"));
		}
		catch (MissingResourceException ex){
				System.out.println(" missing menu icon");
	
		}
	
		try {
			a.putValue(ACTION_MENU_ACCELERATOR, ExaltoResource.getKeyStroke(bundle,keyprefix+".accel"));
		}
		catch (MissingResourceException ex){
				System.out.println(" missing accel");
	
		}
	
	}
	
	public String getFormattedMsg(String key, String [] val) {
	   String ret = null;
	   MessageFormat mf = null;
	   try {
	   
		   mf = new MessageFormat(key);

	//	   System.out.println(" val = " + val);
		  } catch(Exception e) {
		  	e.printStackTrace();
		  }
		  
		  ret = mf.format(val);
			return ret;  
		// return mf.format(val);
	}
	
		/**
		     * Retrieve the current user working directory. This could be the directory
		     * the previous file was located in or if that is null, it would be the user.dir
		     * from the System properties
		     */
			
		    public String getCurrentDir()
		    {
				String dir = _current_dir;
				if (dir == null) {
					dir = System.getProperty("user.dir");
				}

				System.out.println(" getcurrentdir returning " + dir);
	
				return dir;
	    	}
			
			public void setCurrentDir(String dir) {
				_current_dir = dir;
			}
			
		    // Error handler to report errors and warnings
    public static class MyErrorHandler implements ErrorHandler {
        /** Error handler output goes here */
        private PrintStream out;

        MyErrorHandler(PrintStream out) {
            this.out = out;
        }

        /**
         * Returns a string describing parse exception details
         */
        private String getParseExceptionInfo(SAXParseException spe) {
            String systemId = spe.getSystemId();
            if (systemId == null) {
                systemId = "null";
            }
            String info = "URI=" + systemId +
                " Line=" + spe.getLineNumber() +
                ": " + " Col=" +  spe.getColumnNumber() + ":" + spe.getMessage();
            return info;
        }

        // The following methods are standard SAX ErrorHandler methods.
        // See SAX documentation for more info.

        public void warning(SAXParseException spe) throws SAXException {
            out.println("Warning: " + getParseExceptionInfo(spe));
        }
        
        public void error(SAXParseException spe) throws SAXException {
            String message = "Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }

        public void fatalError(SAXParseException spe) throws SAXException {
            String message = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
	
    /**
     * Convert from a filename to a file URL.
     */
    public static String convertToFileURL(String filename) {
        // On JDK 1.2 and later, simplify this to:
        // "path = file.toURL().toString()".
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }
	
	// Unchanged code from example 20.6
	public static final char[] WORD_SEPARATORS = {' ', '\t', '\n','\r', '\f', '.', ',', ':', '-', '(', ')', '[', ']', '{',
'}', '<', '>', '/', '|', '\\', '\'', '\"'};

	public static boolean isSeparator(char ch) 
	{
		for (int k=0; k<WORD_SEPARATORS.length; k++)
			if (ch == WORD_SEPARATORS[k])
				return true;
			
			return false;
	}


	public Document parseXml(File file) throws ParserConfigurationException, IOException, SAXException {

		/*uncommented by exalto 16/1/05 */
		CatalogResolver cr = null;
		Catalog myCatalog = null;
		
		try {
		
		System.out.println("catalogFile =" + catalogFile);
	    
			if(catalogFile == null) 
				throw new FileNotFoundException();
			
		
			cr = new CatalogResolver();
			myCatalog = cr.getCatalog();
		 //   myCatalog.loadSystemCatalogs();
		 	  myCatalog.parseCatalog(catalogFile);
			
			
	//	System.out.println("resolveURI =" + myCatalog.resolveURI());
			
		    
			} catch(Exception e) {			
				e.printStackTrace();
			}

      //  File fileObj = new File(file);
		System.out.println(" in parsexml file");
	    DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
        DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
		docbuilderfact.setValidating(true);
		docbuilder.setEntityResolver(cr);
 	    doc = docbuilder.parse(new FileInputStream(file.getAbsolutePath()));
	
	/*
		org.w3c.dom.DocumentType docType = doc.getDoctype();
		if(docType != null)
		{
		//	DTDName = docType.getSystemId(); // added by nupura Jan 19 2004
		//	System.out.println(" resolvePublic =" + myCatalog.makeAbsolute(myCatalog.resolvePublic(docType.getPublicId(), docType.getSystemId())));
		//	System.out.println("resolveSystem =" + myCatalog.makeAbsolute(myCatalog.resolveSystem(docType.getSystemId())));
			String resolvePub = myCatalog.resolvePublic(docType.getPublicId(), docType.getSystemId());
			String resolveSys = myCatalog.resolveSystem(docType.getSystemId());

		System.out.println(" resolvepub =" + makeAbsolute("xslt/example.dtd"));
		

			return (resolvePub == null)? resolveSys : resolvePub;
			
		//	return (resolvePub == null)? myCatalog.makeAbsolute(resolveSys) : myCatalog.makeAbsolute(resolvePub);
		
			// return doc;
		}
	*/	
		return doc;
	}
	
	
	public String parseXmlWithCatalog(File file) throws ParserConfigurationException, IOException, SAXException {
	
			/*uncommented by exalto 16/1/05 */
			CatalogResolver cr = null;
			Catalog myCatalog = null;
			
			try {
			
			System.out.println("catalogFile =" + catalogFile);
		    
			//	if(catalogFile == null) 
			//		throw new FileNotFoundException();
				
			
				cr = new CatalogResolver();
				myCatalog = cr.getCatalog();
			 //   myCatalog.loadSystemCatalogs();
			
				if(catalogFile != null)
					myCatalog.parseCatalog(catalogFile);
				
				
		//	System.out.println("resolveURI =" + myCatalog.resolveURI());
				
			    
				} catch(Exception e) {			
					e.printStackTrace();
				}
	
	      //  File fileObj = new File(file);
			System.out.println(" in parsexml file");
		    DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
	        DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
			docbuilderfact.setValidating(true);

			if(catalogFile != null)
				docbuilder.setEntityResolver(cr);

	 	    doc = docbuilder.parse(new FileInputStream(file.getAbsolutePath()));
		
			org.w3c.dom.DocumentType docType = doc.getDoctype();
			if(docType != null)
			{
			//	DTDName = docType.getSystemId(); // added by nupura Jan 19 2004
			//	System.out.println(" resolvePublic =" + myCatalog.makeAbsolute(myCatalog.resolvePublic(docType.getPublicId(), docType.getSystemId())));
			//	System.out.println("resolveSystem =" + myCatalog.makeAbsolute(myCatalog.resolveSystem(docType.getSystemId())));
				String resolvePub = myCatalog.resolvePublic(docType.getPublicId(), docType.getSystemId());
				String resolveSys = myCatalog.resolveSystem(docType.getSystemId());
	
			// System.out.println(" resolvepub =" + makeAbsolute("xslt/example.dtd"));
			
	
				return (resolvePub == null)? resolveSys : stripURL(resolvePub);
				
			}
			
			return null;
	}
	
	
  protected String makeAbsolute(String sysid) {
    String local = null;
	
	System.out.println(" makeabs inp =" + sysid);
	try {
	
		String userdir = fixSlashes(System.getProperty("user.dir"));
	
		System.out.println(" userdir =" + userdir);
	    
	//	URL base = new URL("file:////");
		
	//	System.out.println(" base =" + base.toString());

		sysid = fixSlashes(sysid);

    //    local = new URL(base, sysid);
		 local =userdir + "\\" + sysid;
	
	     if (local != null) {
		     System.out.println(" makeabs outp 1=" + local);
		     return local;
    	 } else {
			System.out.println(" makeabs outp 2=" + sysid);
		    return sysid;
    	 }
	
	} catch (Exception e) {
			e.printStackTrace();
    }
	
	return null;
	
  }
  
    protected String fixSlashes (String sysid) {
      return sysid.replace('\\', '/');
    }

    public String stripURL(String sysid) {
	    if(sysid.startsWith("file:"))	
			return sysid.substring(5);
			
      return sysid;
    }

	public void enableCatalogs(Properties p) {
		
		String enabCatalog = p.getProperty("enablecatalog");
		
		boolean enableCatalogs = new Boolean(enabCatalog).booleanValue();
		

      	System.out.println("enableCatalogs============ =" + enableCatalogs);	  		

		
		if(!enableCatalogs)
			catalogFile = null;		
	}

    
	public String getCatalogFile() {
		return catalogFile;
	}
	
	public String [] getPredefinedEntities() {
		return predefinedEntities;
	}


	public void requestFocus(final Component comp)
	{

		System.out.println(" in window activated ");
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				comp.requestFocus();
			}
		});
	}
			
public Properties getProps() {
    
    Properties p = new Properties();
    Enumeration e = resources.getKeys();
    while(e.hasMoreElements()) {
        String key = (String) e.nextElement();
        String val = (String) resources.getObject(key);
        p.put(key, val);
    }
    
    return p;
}
		
}
