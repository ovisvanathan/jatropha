package com.exalto.xslt;

import java.io.*;
import java.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
 
/**
 * A utility class that caches XSLT stylesheets in memory.
 *
 */
public class StylesheetCache {
    // map xslt file names to MapEntry instances
    // (MapEntry is defined below)
    private static Map cache = new HashMap(  );

    private static HashMap referencedDocs = new HashMap();
 
    /**
     * Flush all cached stylesheets from memory, emptying the cache.
     */
    public static synchronized void flushAll(  ) {
        cache.clear(  );
    }
 
    /**
     * Flush a specific cached stylesheet from memory.
     *
     * @param xsltFileName the file name of the stylesheet to remove.
     */
    public static synchronized void flush(String xsltFileName) {
        cache.remove(xsltFileName);
    }
 
    /**
     * Obtain a new Transformer instance for the specified XSLT file name.
     * A new entry will be added to the cache if this is the first request
     * for the specified file name.
     *
     * @param xsltFileName the file name of an XSLT stylesheet.
     * @return a transformation context for the given stylesheet.
     */
    public static synchronized SourceEntry newTransformer(String xsltFileName, String procName)
            throws TransformerConfigurationException {

    		File xsltFile = new File(xsltFileName);
        	String uri = xsltFileName;
        	String systemId = "";
            	
			int slashPos = uri.lastIndexOf("\\");
			String uriPrefix = uri.substring(0, slashPos);
			String uriFile = uri.substring(slashPos+1);
			
//			System.out.println(" uriPrefix = " + uriPrefix);
//			System.out.println(" uriFile = " + uriFile);
			
			String uriKey = uriPrefix + File.separator + uriFile; 

			uri = "file:/" + replaceSlashes(uri); 

	        if(procName != null && procName.indexOf("xalan") > 0) {
	        	systemId = uri;
	        }
        		
        		
 
        // determine when the file was last modified on disk
        long xslLastModified = xsltFile.lastModified(  );
        MapEntry entry = (MapEntry) cache.get(xsltFileName);
 
        if (entry != null) {
            // if the file has been modified more recently than the
            // cached stylesheet, remove the entry reference
            if (xslLastModified > entry.lastModified) {
                entry = null;
            }
        }
 
        // create a new entry in the cache if necessary
        if (entry == null) {
            Source xslSource = new StreamSource(xsltFile);

            System.setProperty("javax.xml.parsers.SAXParserFactory",
                    "org.apache.xerces.jaxp.SAXParserFactoryImpl");


            TransformerFactory transFact = TransformerFactory.newInstance();
            Templates templates = transFact.newTemplates(xslSource);
 
            entry = new MapEntry(xslLastModified, templates);
            cache.put(xsltFileName, entry);
        }
 

        Transformer tformer = entry.templates.newTransformer();
        
        SourceEntry sentry = new SourceEntry(tformer, systemId); 
        
        return sentry; 
        
    }
 
    // prevent instantiation of this class
    private StylesheetCache(  ) {
    }
    
    
 
    /**
     * This class represents a value in the cache Map.
     */
    static class MapEntry {
        long lastModified;  // when the file was modified
        Templates templates;
        String systemId;
        
        MapEntry(long lastModified, Templates templates, String systemId) {
            this.lastModified = lastModified;
            this.templates = templates;
            this.systemId = systemId;
        }
    
        MapEntry(long lastModified, Templates templates) {
            this.lastModified = lastModified;
            this.templates = templates;
        }
    }

    
    /**
     * This class represents a value in the cache Map.
     */
    static class SourceEntry {
        Transformer tformer;
        String systemId;
        
        SourceEntry(Transformer tformer, String systemId) {
        	this.tformer = tformer;
        	this.systemId = systemId;
        }
    
    }

// For resolver stuff
    
    /**
     * Obtain a new Transformer instance for the specified XSLT file name.
     * A new entry will be added to the cache if this is the first request
     * for the specified file name.
     *
     * @param xsltFileName the file name of an XSLT stylesheet.
     * @return a transformation context for the given stylesheet.
     */
    public static synchronized byte [] getReferredDoc(String href) {
    	return (byte []) referencedDocs.get(href);
 
    }

    public static boolean isDocInCache(String href) {
    	return referencedDocs.get(href) != null;
    }


    public static void addDocToCache(String href, byte [] b) {
    	referencedDocs.put(href, b);	
    }

	private static String replaceSlashes(String uri) {		
		return uri.replace('\\', '/');				
	}

    
}

