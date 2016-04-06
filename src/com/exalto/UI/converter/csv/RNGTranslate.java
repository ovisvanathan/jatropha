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


import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.*;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.input.xml.XmlInputFormat;
import com.thaiopensource.relaxng.output.*;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.rnc.RncOutputFormat;
import com.thaiopensource.relaxng.output.rng.RngOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;
import com.thaiopensource.util.*;
import com.thaiopensource.xml.sax.ErrorHandlerImpl;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import org.xml.sax.SAXException;



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
public class RNGTranslate  {
    /** Constants used for JAXP 1.2 */
    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    static final String RELAXNG_XML_SCHEMA =
        "http://relaxng.org/ns/structure/1.0";


    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private final ErrorHandlerImpl eh = new ErrorHandlerImpl();


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

    public static void main(String[] args) throws Exception {

//      args =   new String [] { "-I", "rng", "-O", "xsd",  "xslt/library.rng", "xslt/library.xsd" };

//xx        args =   new String [] { "-I", "xsd", "-O", "xml",  "xslt/personal.xsd", "xslt/person.xml" };

//       args =   new String [] { "-I", "dtd", "-O", "xsd",  "xslt/play.dtd", "xslt/play.xsd" };

         args =   new String [] { "-I", "rng", "-O", "dtd",  "xslt/addr.rng", "xslt/addr.dtd" };

      RNGTranslate rngtrans = new RNGTranslate();
      rngtrans.translate(args);

    }



    public void translate(String[] args) throws Exception {

      Vector vector;
      Vector vector1;
      vector = new Vector();
      vector1 = new Vector();
      String inputType = "";
      String outputType = "";
      String inputFile = "book.rng";
      String outputFile = "book.rnc";
      String [] as = new String[2];
      String infile = null;
      String outfile = null;

      Object obj = null;
      Object obj2 = null;
      Object obj1 = null;

       // Parse arguments
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-I")) {
                inputType = args[++i];
            System.out.println(" inputType = " + inputType);
            } else if (args[i].equals("-O")) {
                outputType = args[++i];
                System.out.println(" outputType " + outputType);
            } else if (args[i].equals("-i")) {
                vector.add(args[++i]);
            } else if (args[i].equals("-o")) {
                vector1.add(args[++i]);
            } else if (args[i].equals("-help")) {
                usage();
            } else {

                infile = args[i++];
                outfile = args[i];
                as[0] = infile;
                as[1] = outfile;

                // Must be last arg
                if (i != args.length - 1) {
           System.out.println(" last arg");

                    usage();
                }
            }
        }

       if(inputType.equalsIgnoreCase("rng"))
       {
            obj1 = new SAXParseInputFormat();
       }
      else if(inputType.equalsIgnoreCase("rnc"))
       {
            obj1 = new CompactParseInputFormat();
       }
      else if(inputType.equalsIgnoreCase("dtd"))
       {
            obj1 = new DtdInputFormat();
       }
      else if(inputType.equalsIgnoreCase("xml"))
       {
            obj1 = new XmlInputFormat();
       }
       else {
           System.out.println(" inputtype not supported ");
           return;
       }


        Object obj3 = null;
        String s;
        s = extension(as[as.length - 1]);

        if(outputType.equalsIgnoreCase("dtd"))
        {
            obj3 = new DtdOutputFormat();
        }
        if(outputType.equalsIgnoreCase("rng"))
        {
            obj3 = new RngOutputFormat();
        }
        if(outputType.equalsIgnoreCase("xsd"))
        {
            obj3 = new XsdOutputFormat();
        }
        if(outputType.equalsIgnoreCase("rnc"))
        {
            obj3 = new RncOutputFormat();
        }

        String as1[];
        as1 = (String[])vector.toArray(new String[0]);
        outputType = outputType.toLowerCase();

        SchemaCollection schemacollection;
        String as2[] = new String[as.length - 1];
        for(int i = 0; i < as2.length; i++)
            as2[i] = UriOrFile.toUri(as[i]);

//        schemacollection = ((MultiInputFormat)obj1).load(as2, as1, outputType, eh);
       System.out.println(" as[0] infile = " + as[0]);


       System.out.println(" as[0] infile uri = " + UriOrFile.toUri(as[0]));


       System.out.println(" as[0] outfile  = " + as[1]);

       System.out.println(" outtype  = " + outputType);

       // OV added null below line on 15/09/09
        schemacollection = ((InputFormat)
(obj1)).load(UriOrFile.toUri(as[0]), as1, outputType, eh, null);
        if(s.length() == 0)
            s = outputType;
        LocalOutputDirectory localoutputdirectory = new
LocalOutputDirectory(schemacollection.getMainUri(), new File(as[as.length -
1]), s, "UTF-8", 72, 2);
        ((OutputFormat) (obj3)).output(schemacollection,
localoutputdirectory, (String[])vector1.toArray(new String[0]),
inputType.toLowerCase(), eh);

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

    private static String extension(String s)
    {
        int i = s.lastIndexOf(".");
        if(i < 0)
            return "";
        else
            return s.substring(i);
    }



}
