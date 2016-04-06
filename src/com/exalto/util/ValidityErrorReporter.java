package com.exalto.util;

import org.xml.sax.*;
import java.io.*;


public class ValidityErrorReporter implements ErrorHandler {
 
  Writer out;
 
  public ValidityErrorReporter(Writer out) {
    this.out = out;
  }
 
  public ValidityErrorReporter() {
 	   this(new OutputStreamWriter(System.out));		  	
  }
 
  public void warning(SAXParseException ex)
   throws SAXException {

    try {
      out.write(ex.getMessage() + "\r\n");
      out.write(" at line " + ex.getLineNumber() + ", column " 
       + ex.getColumnNumber() + "\r\n");
      out.flush();
 
 
 		throw ex;
 
 }
    catch (IOException e) {
      throw new SAXException(e); 
    }
    
  }

  public void error(SAXParseException ex)
   throws SAXException {
    
    try {
      out.write(ex.getMessage() + "\r\n");
      out.write(" at line " + ex.getLineNumber() + ", column " 
       + ex.getColumnNumber() + "\r\n");
      out.flush();
     
		throw ex;

    }
    catch (IOException e) {
      throw new SAXException(e); 
    }
    
  }
    
  public void fatalError(SAXParseException ex)
   throws SAXException {
    
    try {
      out.write(ex.getMessage() + "\r\n");
      out.write(" at line " + ex.getLineNumber() + ", column " 
       + ex.getColumnNumber() + "\r\n");
      out.flush();
    }
    catch (IOException e) {
      throw new SAXException(e); 
    }
    
  }
    
}
