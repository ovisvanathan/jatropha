package com.exalto.util;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import java.util.StringTokenizer;

import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.output.XMLOutputter;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Enumeration;

import java.io.*;
import org.xml.sax.Locator;


/** A SAX handler that prints out the start tags, end tags,
 *  and first word of tag body. Indents two spaces
 *  for each nesting level.
 */

public class PrettyPrinter3 extends PrettyPrinter2 {
  private int indentation = 0;
  
  
  
  	public PrettyPrinter3(Writer fout) {
		super(fout);
	}
	
	  public PrettyPrinter3(OutputStream out) {
	    super(out);
	  }

	  public PrettyPrinter3() {
	    super();
	  }


   /**
       * SAX Handler for the start of the document
       */
/*	   
      public void startDocument()
      throws SAXException {

      }
  
      public void endDocument()
      throws SAXException {

    }
*/

  /** When you see a start tag, print it out and then
   *  increase indentation by two spaces. If the
   *  element has attributes, place them in parens
   *  after the element name.
   */

  public void startElement(String namespaceUri,
                           String localName,
                           String qualifiedName,
                           Attributes attributes)
      throws SAXException 
   {
	    try {
	         flushTextBuffer();
	         indent();
	         out.write("<" + qualifiedName);
	         for (int i = 0; i < attributes.getLength(); i++) {
	           out.write(" "); 
	           out.write(attributes.getQName(i)); 
	           out.write("=\""); 
	           out.write(attributes.getValue(i)); 
	           out.write("\""); 
	         }
	         out.write(">\r\n");
	         depth++;
	       }
	       catch (IOException e) {
	         throw new SAXException(e);
    	}
   }
   

  
//	  public void setDocumentLocator(Locator locator) {
//          this.locator = locator;
//    }

//	public Document getDocument() {
//		if(doc == null) {
//			throw new NullPointerException("doc is null");
//		}
//		return doc;
//	}
	
  
}