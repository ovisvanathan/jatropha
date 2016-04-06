/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.UI.converter.csv;

/**
 *
 * @author omprakash.v
 */
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import java.util.*;
import java.io.*;

//import org.iso_relax.verifier.*;
//import org.iso_relax.jaxp.*;

import javax.xml.validation.*;
import javax.xml.transform.stream.StreamSource;


import com.thaiopensource.relaxng.util.*;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import com.thaiopensource.util.PropertyMapBuilder;
import com.thaiopensource.validate.ValidateProperty;
import com.thaiopensource.validate.prop.rng.RngProperty;
import com.thaiopensource.validate.auto.AutoSchemaReader;
import com.thaiopensource.validate.rng.CompactSchemaReader;
import com.thaiopensource.validate.ValidationDriver;
import com.thaiopensource.validate.SchemaReader;



/**
 * Program to count the number of elements using only the localName
 * component of the element, in an XML document.  Namespace names are
 * ignored for simplicity.  This example also shows one way to turn on
 * validation and how to use a SAX ErrorHandler.
 *
 * Notes: DefaultHandler is a SAX helper class that implements the SAX
 * ContentHandler interface by providing no-op methods.  This class
 * overrides some of the methods by extending DefaultHandler.  This program
 * turns on namespace processing and uses SAX2 interfaces to process XML
 * documents which may or may not be using namespaces.
 *
 * Update 2002-04-18: Added code that shows how to use JAXP 1.2 features to
 * support W3C XML Schema validation.  See the JAXP 1.2 maintenance review
 * specification for more information on these features.
 *
 * @author Edwin Goei
 */
public class RNGNative  {
    /** Constants used for JAXP 1.2 */
    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    static final String RELAXNG_XML_SCHEMA =
        "http://relaxng.org/ns/structure/1.0";


    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    /** A Hashtable with tag names as keys and Integers as values */
    private Hashtable tags;

    // Parser calls this once at the beginning of a document
    public void startDocument() throws SAXException {
        tags = new Hashtable();
    }

    // Parser calls this for each element in a document
    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes atts)
      throws SAXException
    {
        String key = localName;
        Object value = tags.get(key);
        if (value == null) {
            // Add a new entry
            tags.put(key, new Integer(1));
        } else {
            // Get the current count and increment it
            int count = ((Integer)value).intValue();
            count++;
            tags.put(key, new Integer(count));
        }
    }

    // Parser calls this once after parsing a document
    public void endDocument() throws SAXException {
        Enumeration e = tags.keys();
        while (e.hasMoreElements()) {
            String tag = (String)e.nextElement();
            int count = ((Integer)tags.get(tag)).intValue();
            System.out.println("Local Name \"" + tag +
"\" occurs " + count
                               + " times");
        }
    }

    /**
     * Convert from a filename to a file URL.
     */
    private static String convertToFileURL(String filename) {
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

    private static void usage() {
        System.err.println("Usage: SAXLocalNameCount [-options] <file.xml>");
        System.err.println("       -dtd = DTD validation");
        System.err.println("       -xsd | -xsdss <file.xsd> = W3C XML Schema validation using xsi: hints");
        System.err.println("           in instance document or schema source <file.xsd>");
        System.err.println("       -xsdss <file> = W3C XML Schema validation using schema source <file>");
        System.err.println("       -usage or -help = this message");
        System.exit(1);
    }

    static public void main(String[] args) throws Exception {
        ErrorHandlerImpl errorhandlerimpl;
        PropertyMapBuilder propertymapbuilder;
        boolean flag;

        Object obj = null;
        errorhandlerimpl = new ErrorHandlerImpl(System.out);
        propertymapbuilder = new PropertyMapBuilder();

        ValidateProperty.ERROR_HANDLER.put(propertymapbuilder,
                errorhandlerimpl);

        RngProperty.CHECK_ID_IDREF.add(propertymapbuilder);
        flag = true;

        propertymapbuilder.put(RngProperty.CHECK_ID_IDREF, null);
        RngProperty.FEASIBLE.add(propertymapbuilder);
        String encoding = "UTF-8";
        boolean flag1 = false;

        if(obj == null) {
                  System.out.println(" auto schema rdr");
                  obj = new AutoSchemaReader();
        }

        if(flag)
            obj = CompactSchemaReader.getInstance();

      try
        {

           String [] args1 = { "xslt/library.rng", "xslt/patron.xml" };

           String [] args2 = { "xslt/library.rnc", "xslt/patron.xml" };

           args1 = args2;
           
            ValidationDriver validationdriver = new
            ValidationDriver(propertymapbuilder.toPropertyMap(), ((SchemaReader) (obj)));
            InputSource inputsource = ValidationDriver.uriOrFileInputSource(args1[0]);
            if(encoding != null)
                inputsource.setEncoding(encoding);

            if(validationdriver.loadSchema(inputsource))
            {
                for(int i = 1; i < args1.length; i++)

                        if(!validationdriver.validate(ValidationDriver.uriOrFileInputSource(args1[i])))
                        flag1 = true;

            } else
            {
                flag1 = true;
            }

        System.out.println(" is valid inst");

      }
        catch(SAXException saxexception)
        {
            flag1 = true;
            errorhandlerimpl.printException(saxexception);
        }
        catch(IOException ioexception)
        {
            flag1 = true;
            errorhandlerimpl.printException(ioexception);
        }


      }

    // Error handler to report errors and warnings
    private static class MyErrorHandler implements ErrorHandler {
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
                ": " + spe.getMessage();
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
            String message = "Fatal Error: " +
getParseExceptionInfo(spe);
            throw new SAXException(message);
        }
    }
}