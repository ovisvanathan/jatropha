package com.exalto.UI.painter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/** A SAX handler that prints out the start tags, end tags,
 *  and first word of tag body. Indents two spaces
 *  for each nesting level.
 */

public class FoldDocHandler extends DefaultHandler {

	Locator locator;

	TreeModel treeModel;
	
  
  	public FoldDocHandler(Writer fout) {

  	}
	
	  public FoldDocHandler(OutputStream fout) {
	
		
	  }

  	public FoldDocHandler() {
	
	}


   /**
       * SAX Handler for the start of the document
       */
      public void startDocument()
      throws SAXException {

	
      }
  
      public void endDocument()
      throws SAXException {
	
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
	   //   flushTextBuffer();
	    
	      if(treeModel == null) {
	    	  
	    	  DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(qualifiedName); 
	    	  
	    	  treeModel = new DefaultTreeModel(treeNode);  
	      }
			

	      
	    
   }
   
/** When you see the end tag, print it out and decrease
   *  indentation level by 2.
   */
  
  public void endElement(String namespaceUri,
                         String localName,
                         String qualifiedName)
      throws SAXException {
	//	  flushTextBuffer();
		   
		  
  }

  private StringBuffer textBuffer = new StringBuffer();
  
  protected void flushTextBuffer() throws IOException {
    
    if (textBuffer.length() > 0) {
  
     
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

	//  flushTextBuffer();
    
  }

    public void setDocumentLocator(Locator locator) {
          this.locator = locator;
    }
  
}