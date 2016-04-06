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

import java.io.*;
import org.xml.sax.Locator;


/** A SAX handler that prints out the start tags, end tags,
 *  and first word of tag body. Indents two spaces
 *  for each nesting level.
 */

public class PrettyPrinter2 extends DefaultHandler {
  	protected Writer out;
    protected int depth = 0;  // depth in hierarchy
	Locator locator;

  
  	public PrettyPrinter2(Writer fout) {
		this.out = fout;
	}
	
	  public PrettyPrinter2(OutputStream fout) {
	
	try {
	      this.out = new OutputStreamWriter(fout, "UTF-8");
	    }
	    catch (UnsupportedEncodingException e) {
	      System.out.println(
	       "Something is seriously wrong."
	       + " Your VM does not support UTF-8 encoding!"); 
	    }
		
	  }

  	public PrettyPrinter2() {
	    try {
		  FileOutputStream fout = new FileOutputStream(new File("e:\\jdom\\prettyPrint.xml"));
	      this.out = new OutputStreamWriter(fout, "UTF-8");
	    }
	    catch (Exception e) {
	      System.out.println(
	       "Something is seriously wrong."
	       + " Your VM does not support UTF-8 encoding!"); 
	    }
	}


   /**
       * SAX Handler for the start of the document
       */
      public void startDocument()
      throws SAXException {

		depth = 0; // so instance can be reused
		try {
		  out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		}
		catch (IOException e) {
		  throw new SAXException(e);
		}
      }
  
      public void endDocument()
      throws SAXException {
	    try {
	        out.flush();  
			out.close();
	      }
	      catch (IOException e) {
	        throw new SAXException(e);
    	  }
    }

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
	      out.write("<" + qualifiedName + ">\r\n");
	      depth++;
	    }
	    catch (IOException e) {
	      throw new SAXException(e);
	    } 
   }
   
/** When you see the end tag, print it out and decrease
   *  indentation level by 2.
   */
  
  public void endElement(String namespaceUri,
                         String localName,
                         String qualifiedName)
      throws SAXException {
		   try {
		  flushTextBuffer();
		  depth--;
		  indent();
		  out.write("</" + qualifiedName + ">\r\n");   
		}
		catch (IOException e) {
		  throw new SAXException(e);
		}  
  }

  private StringBuffer textBuffer = new StringBuffer();
  
  protected void flushTextBuffer() throws IOException {
    
    if (textBuffer.length() > 0) {
  
      indent();
      out.write(textBuffer.toString());
      textBuffer = new StringBuffer();
      out.write("\r\n"); 
    }
    
  }

 // I could have word wrapped the buffer writing it out but since 
 // that's just a lot of String processing code that really doesn't 
 // say anything about XML I'll leave it as an exercise. 
 
  
  public void characters(char[] text, int start, int length) 
   throws SAXException {
    for (int i = start; i < start+length; i++) {
      switch (text[i]) {
        case '\r': 
          textBuffer.append(' ');
          break;  
        case '\n': 
          textBuffer.append(' ');
          break;  
        case '&': 
          textBuffer.append("&amp;");
          break;  
        case '<': 
          textBuffer.append("&lt;");
          break;
        default:  
          textBuffer.append(text[i]);
      }
    }
  }
  
  public void ignorableWhitespace(char[] text, int start, int length)
   throws SAXException {
    // ignore ignorable white space
  }
  
  public void processingInstruction(String target, String data)
   throws SAXException {
    try {
      flushTextBuffer();
      indent();
      out.write("<?" + target + " " + data + "?>\r\n"); 
    }
    catch (IOException e) {
      throw new SAXException(e);
    }    
  }

  protected void indent() throws IOException {
    
    int spaces = 2; // number of spaces to indent
    
    for (int i = 0; i < depth*spaces; i++) {
      out.write(' ');
    }    
  }  
	  public void setDocumentLocator(Locator locator) {
          this.locator = locator;
    }
  
}