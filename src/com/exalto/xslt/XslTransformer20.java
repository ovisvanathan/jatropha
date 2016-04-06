/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.xslt;

/**
 *
 * @author omprakash.v
 */
import com.exalto.*;
import com.exalto.UI.XmlEditor;
import com.exalto.util.ExaltoResource;
import com.exalto.util.StatusEvent;
import com.exalto.util.XmlFileUtil;
import com.exalto.xslt.StylesheetCache.SourceEntry;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Controller;
import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.instruct.TerminationException;
import net.sf.saxon.trans.DynamicError;
import net.sf.saxon.trans.XPathException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.URIResolver;
import net.sf.saxon.event.StandardOutputResolver;
import org.apache.xml.resolver.Catalog;
import org.xml.sax.EntityResolver;
import org.xml.sax.XMLReader;
//import com.sun.org.apache.xml.internal.resolver.Catalog;


//import com.sun.org.apache.xpath.internal.NodeSetDTM;

/**
 *
 * @author 510342
 *
 * This class is the controller and is responsible for calling the
 * right method  and returning the results as
 * an array list
 */
public class XslTransformer20  implements TransformerIntf, EntityResolver, URIResolver {
	private String xmlFile;

	private String outFile;
	Logger logger;
	private String tformerToUse;

	//OV added 17/04/08 for catalog support
	String catalogFile;
//	Catalog catalog;
	DocumentBuilderFactoryImpl factory;
	Hashtable cache = new Hashtable();
	Transformer transformer;

	PrintStream origError;
	PrintStream origOut;
	ByteArrayOutputStream allOutput = new ByteArrayOutputStream();

	String strOut;
	PrintStream err;
	PrintStream out;
	public static String errorMessages;
	String systemId;

	String lineNo;
	String colNo;

	String pubId;
	String sysId;
	String errMsg;
	boolean isSaxException;
	boolean enableCache;

	//a field to store the id and name of result document
	int resultId;


//	String beanPath = "f:\\Caps\\wavepredictor\\com\\vegaspro\\beans\\";

	String beanPath;
	String beanFile = "dummy.xml";

    Properties props;
    XmlEditor editor;
    String version;
    
//	String xslFile = "bean.xsl";
	SourceEntry sentry;
	
	Catalog catalog;
	private HashMap referencedDocs = new HashMap();

    
	protected TransformerFactoryImpl tfact = new TransformerFactoryImpl();

	public XslTransformer20(Properties p, XmlEditor editor) {

        this.props = p;
        this.editor = editor;

        beanPath = props.getProperty("xslt20.bean.path");

    }

	public void doTransform() throws Exception {

    }

	public void doTransform(InputStream xmlFile, InputStream xslFile) throws Exception {

			InputSource srcInput = new InputSource(xmlFile);

			Source sourceInput = new SAXSource(srcInput);

			InputSource srcStyle = new InputSource(xslFile);

			Source style = new SAXSource(srcStyle);

			System.out.println(" xslFile= " + xslFile);


    		Templates sheet = tfact.newTemplates(style);

            Transformer instance = sheet.newTransformer();

           //  setParams(instance, parameterList);

	         outFile = beanPath + beanFile;

			 PrintWriter prtout = new PrintWriter(new FileOutputStream(outFile));

   //          StreamResult  result =
    //                 (outFile == null ? new StreamResult (System.out) : new StreamResult (outFile.toURI().toString()));

             try {

            	 StandardOutputResolver stdOutpResolver = new StandardOutputResolver();

            	 ((Controller) instance).setOutputURIResolver(stdOutpResolver);

           		 String uri = "file:/" + replaceSlashes(outFile);

                 StreamResult res = new StreamResult(prtout);

                 res.setSystemId(uri);

            	 instance.transform(sourceInput, res);

             } catch (TerminationException err) {
                 throw err;
             } catch (XPathException err) {
                 // The error message will already have been displayed; don't do it twice
                 editor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"xpath.error") + " " + err.getLocalizedMessage(),0, ColWidthTypes.ERROR));
            	 throw new DynamicError("Run-time errors were reported");
             }
	}



	public void transform(InputStream xmlFile, InputStream xslFile) throws Exception {

		try {

					System.out.println("inside xsltrans transform = " + System.getProperty("javax.xml.transform.TransformerFactory"));

	//				System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.processor.TransformerFactoryImpl");
					System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

					System.out.println(" proc saxon 8 ");

					origError = System.err;
					origOut = System.out;

					allOutput = new ByteArrayOutputStream();
					err = new PrintStream(allOutput);
					out = new PrintStream(allOutput);



			//		System.setErr(err);
			//		System.setOut(out);

					factory = new DocumentBuilderFactoryImpl();
			        DocumentBuilder builder = factory.newDocumentBuilder();
		//	        builder.setEntityResolver(this);

			        Document document = builder.parse(xmlFile);

			        String bpath = beanPath + beanFile;

 				    PrintWriter prtout = new PrintWriter(new FileOutputStream(bpath));

				    TransformerFactory tFactory = TransformerFactory.newInstance();

			        System.err.println(" xslFile  " + xslFile);

 //				    FileInputStream fsi = new FileInputStream(xslFile);

 				    Source xslSource = new StreamSource(xslFile);

      // Generate the transformer.
      // OV c 180408
      Transformer transformer = tFactory.newTransformer(xslSource);


      // Perform the transformation, sending the output to the response.
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      transformer.transform(new DOMSource(document), new StreamResult(prtout));


    //  transformer.transform(xmlSource, new StreamResult(out));

	}
	catch(org.xml.sax.SAXParseException spe) {
//		System.out.println(" spe  " + spe.getMessage());

		treeWalk(spe, "org.xml.sax.SAXParseException");
	//	collectOutput(spe);

	//	xeditor.fireStatusChanged(new StatusEvent(strOut, 0, ColWidthTypes.ERROR));
		throw spe;
	}
	catch(javax.xml.transform.TransformerConfigurationException tce) {
		logger.error("encountered error in transformation" + tce.getMessage());


		treeWalk(tce, "javax.xml.transform.TransformerConfigurationException");
		collectOutput(tce);
      //  xeditor.fireStatusChanged(new StatusEvent(strOut, 0, ColWidthTypes.ERROR));
		throw tce;
	}
	catch(javax.xml.transform.TransformerException te) {
//		logger.error("encountered error in transformation" + tce.getMessage());
	//	System.out.println(" te loc  " + te.getMessageAndLocation());

		treeWalk(te, "javax.xml.transform.TransformerException");
		collectOutput(te);
//		System.out.println(" allOutput.toString()  " + allOutput.toString());

      //  xeditor.fireStatusChanged(new StatusEvent(strOut, 0, ColWidthTypes.ERROR));
		throw te;

	}
	 catch(IOException ioe) {
//		logger.error("encountered error in transformation" + ioe.getMessage());
//		System.out.println(" allOutput.toString()  " + allOutput.toString());
		collectOutput(ioe);
      //  xeditor.fireStatusChanged(new StatusEvent(ioe.getMessage(), 0, ColWidthTypes.ERROR));
			throw ioe;
	 }
	 catch(Exception xpe) {
		 xpe.printStackTrace();

//			collectOutput(xpe.getMessage());

//		 logger.error("encountered error in transformation" + xpe.getMessage());
			throw xpe;
		}
	 finally {

		 if(isSaxException)
			 errorMessages = sysId + ":" + " Line: " + lineNo + " col: " + 	colNo + " "  + errMsg;

		 System.setErr(origError);
		 System.setOut(origOut);

		errorMessages += allOutput.toString();

//		if(errorMessages != null && !errorMessages.equals(""))

//		     throw new Exception(errorMessages);
		 //			System.out.println(" allOutput.toString()  " + allOutput.toString());

	 }

}

		private void treeWalk(Exception e, String name) {

		String msg = null;

		if(tformerToUse.equals("xalan")) {


		if(name.equals("javax.xml.transform.TransformerConfigurationException")) {

			javax.xml.transform.TransformerConfigurationException tce =
				(javax.xml.transform.TransformerConfigurationException) e;

			Throwable innerException = tce.getException();

			if(innerException != null) {

				if(innerException instanceof javax.xml.transform.TransformerException) {
					javax.xml.transform.TransformerException te = (javax.xml.transform.TransformerException) innerException;

					treeWalk(te, "javax.xml.transform.TransformerException");
				}
				else if(innerException instanceof org.xml.sax.SAXParseException) {
					SAXParseException spe = (SAXParseException) innerException;
					treeWalk(spe, "org.xml.sax.SAXParseException");
				}
			}
			else
				System.out.println(" in tw tce =  " + innerException.getMessage());
		}
		else if(name.equals("org.xml.sax.SAXParseException")) {

			org.xml.sax.SAXParseException spe =
				(org.xml.sax.SAXParseException) e;


//			System.out.println(" in tw speeee =  " + spe.getMessage());



		}
		else if(name.equals("javax.xml.transform.TransformerException")) {

			javax.xml.transform.TransformerException te =
				(javax.xml.transform.TransformerException) e;

			Throwable innerException = te.getException();


			if(innerException != null) {

		//		System.out.println(" in spe inner =  " + innerException.getClass().getName());

				if(innerException instanceof org.xml.sax.SAXException) {
					SAXException se = (SAXException) innerException;
					treeWalk(se, "org.xml.sax.SAXException");
				}
			}
			else
				System.out.println(" in tw tee =  " + innerException.getMessage());

		}
		else if(name.equals("org.xml.sax.SAXException")) {

			org.xml.sax.SAXException se =
				(org.xml.sax.SAXException) e;

			isSaxException = true;

			Throwable innerException = se.getException();


			if(innerException  instanceof javax.xml.transform.TransformerException) {

				javax.xml.transform.TransformerException te =
					(javax.xml.transform.TransformerException) innerException;

				String pubId = null;
				String sysId = null;
				if(te != null) {

					errMsg = te.getLocalizedMessage();
					SourceLocator sloc = te.getLocator();

					if(sloc != null) {

						lineNo = new Integer(sloc.getLineNumber()).toString();
						colNo = new Integer(sloc.getColumnNumber()).toString();

								pubId = sloc.getPublicId();
								sysId = sloc.getSystemId();


					String file = "";

					if(pubId == null)
						file = sysId;
					else
						file = pubId;



					System.out.println(msg);


					}
				}
			}


		}

		else {
			System.out.println(" in else =  " + e.getClass().getName());
		}

		}

	}

	private String consumeName(String s) {

			String [] sa = s.split("javax.xml.transform.TransformerException");

//			System.out.println(" cons name ret  " + sa.toString());

			return sa.toString();
	}

	private void collectOutput(Exception e) {

		if(e instanceof IOException) {
			strOut = e.getMessage();
			return;
		}

		strOut = allOutput.toString();
	}

	private void collectOutput() {
		strOut = allOutput.toString();
	}

	private void collectOutput(String s) {
		strOut = s;
	}

	private synchronized Templates tryCache(String uri) throws TransformerException, java.io.IOException {

			Templates x = null;
			try {
				int slashPos = uri.lastIndexOf("\\");
				String uriPrefix = uri.substring(0, slashPos);
				String uriFile = uri.substring(slashPos+1);

	//			System.out.println(" uriPrefix = " + uriPrefix);
	//			System.out.println(" uriFile = " + uriFile);

				String uriKey = uriPrefix + File.separator + uriFile;

				uri = "file:/" + replaceSlashes(uri);

				if(enableCache)
					x = (Templates)this.cache.get(uriKey);

					if (x==null) {
						TransformerFactory tfactory = TransformerFactory.newInstance();

				//		tfactory.setURIResolver(this);

				     /*
				        DocumentBuilder builder = factory.newDocumentBuilder();
				        builder.setEntityResolver(this);

				        Document document = builder.parse(uri);
					    DOMSource domXslSrc = new DOMSource(document);
				     */
				       if(tformerToUse.equals("xalan")) {
				    	   StreamSource strmSource = new StreamSource(new File(uri));
				    	   strmSource.setSystemId(uri);
				    	   systemId = uri;
				    	   x = tfactory.newTemplates(strmSource);

				       } else {
				    	   x = tfactory.newTemplates(new StreamSource(uri));
				       }


						if(enableCache)
							this.cache.put(uriKey, x);
				  }
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				throw e;
//				e.printStackTrace();
//				  xeditor.fireStatusChanged(new StatusEvent(e.getMessage(), 0, ColWidthTypes.ERROR));

			}
			  return x;
	}


	private String replaceSlashes(String uri) {
			return uri.replace('\\', '/');
	}

	public String getXsltOutput() {
		String xslResult = outFile;
		File outf = new File(outFile);

		outf.delete();

		return xslResult;
	}

    public InputSource resolveEntity(java.lang.String publicId,
             java.lang.String systemId)
	{

		 InputSource is = new InputSource(new StringReader(""));
		 return is;
	}


 public Source resolve(String href, String base) {


		 String srcUri = null;

	//		System.out.println(" in RES href = " + href);
	//		System.out.println(" in RES base = " + base);

		 if(href== null && base == null)
			 return null;


		 if(base == null || base.equals(""))
			 base = sentry.systemId;

		try {
			if(this.catalogFile != null) {

					srcUri = this.catalog.resolveURI(href);
	//				System.out.println(" in URIRES srcuri = " + srcUri);

					if(srcUri != null && srcUri.trim().length() > 0)
						href = srcUri;
				//	else
				//		return null;

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}




			if(enableCache) {

				// referencedDocs not used. Always reads from disk
				if (referencedDocs.get(href) != null) {
				                try {
				                    // This doc has already been stored, so return it
				                    return getFromCache(href, null);
				                } catch (SAXException ex) {
				                   ex.printStackTrace();
				                }

				} else {

					try {

					URI targetURI = null;


				 targetURI = URI.create(XmlFileUtil.escapeSpaces(base)).resolve(XmlFileUtil.escapeSpaces(href));

	//			 String baseUri = 	XmlFileUtil.escapeSpaces(base);
	//			 String hrefUri = 	XmlFileUtil.escapeSpaces(srcUri);

	//			 URI uri2 = URI.create(baseUri);
	//			 targetURI = uri2.resolve(hrefUri);

				 return getFromCache(href, targetURI);

	         } catch (Exception ex) {
	             ex.printStackTrace();
	         }


			}

		} else { // cache not enabled

			try {

	//			URI targetURI = null;

	//			if(this.tformerToUse.startsWith("saxon")) {

			 URI targetURI = URI.create(XmlFileUtil.escapeSpaces(base)).resolve(XmlFileUtil.escapeSpaces(href));
/*
			 String baseUri = 	XmlFileUtil.escapeSpaces(base);
			 String hrefUri = 	XmlFileUtil.escapeSpaces(srcUri);

			 URI uri2 = URI.create(baseUri);
			 targetURI = uri2.resolve(hrefUri);
*/
	//			}	else {
//					TODO:


	//			}
			 return getFromCache(href, targetURI);

         } catch (Exception ex) {
             ex.printStackTrace();
         }


		}

		 return null;
	 }


 	 private Source getFromCache(String key, URI targetURI) throws SAXException {

		 byte[] byteArray = null;
		 File targetFile = null;

			try {

				if(targetURI != null) {

					targetFile = new File(targetURI);

					if(targetFile.isDirectory() || !targetFile.exists())
						return null;

					String targetName;
					byteArray = getBytesFromFile(targetFile);

				}

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
				XMLReader xmlReader = saxParser.getXMLReader();

				//   XMLReader xmlReader = XMLReaderFactory.createXMLReader();
				xmlReader.setEntityResolver(CustomEntityResolver.getInstance());

		        // Set an ErrorHandler before parsing
		        xmlReader.setErrorHandler(new MyErrorHandler(err));


				SAXSource saxSource = null;
				//TODO: saxsource from ByteArray stream does not throw errors when xml is not well-formed.
				// Needs to be checked. OV 280408
				if(targetURI != null) {
					xmlReader.parse(com.exalto.util.XmlUtils.convertToFileURL(targetFile.getAbsolutePath()));
					saxSource = new SAXSource(xmlReader, new InputSource(targetFile.getAbsolutePath()));

					if(enableCache)
						referencedDocs.put(key, byteArray);

				} else {
					saxSource = new SAXSource(xmlReader, new InputSource(new ByteArrayInputStream((byte [])referencedDocs.get(key))));
				}
				return 	saxSource;


			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(org.xml.sax.SAXParseException spe) {

				System.out.println(spe.getMessage());

				collectOutput(spe);

                editor.fireStatusChanged(new StatusEvent(strOut, 0, ColWidthTypes.ERROR));

			}
			catch (FileNotFoundException fe) {
				// TODO Auto-generated catch block
				fe.printStackTrace();

				System.out.println(fe.getMessage());

//				xeditor.fireStatusChanged(new StatusEvent(strOut, 0, ColWidthTypes.ERROR));

			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
         //return new StreamSource(new ByteArrayInputStream(referencedDocs.get(key)));
			return null;
	 }

    public static byte[] getBytesFromFile(File file) throws IOException {

    	InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }




	public void invalidateCache(String path, String name) {

	//	System.out.println(" in ivcache =  " + name);

	//	System.out.println(" ivcache =  " + this.cache);

		String key = path + File.separator + name;

		if(!name.equals("ALL")) {

			if(this.cache.containsKey(key)) {
				this.cache.remove(key);
			}
		}
		else
			this.cache.clear();
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
            errorMessages = "Fatal Error: " + getParseExceptionInfo(spe);
            throw new SAXException(errorMessages);
        }
    }



    public String getTFormerToUse()
    {
        return this.tformerToUse;
    }


  	public synchronized int getNextId() {
		return ++resultId;
	}

    public void setXslVersion(String version) {
        this.version = version;
    }

    	public String getXsltOutFile() {
		String xslResult = outFile;
		File outf = new File(outFile);

		outf.delete();

		return xslResult;
	}

    public String getErrorMessages() {
        return errorMessages;
    }

    
}

