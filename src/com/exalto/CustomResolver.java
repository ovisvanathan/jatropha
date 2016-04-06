package com.exalto;

import java.io.StringReader;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class CustomResolver implements EntityResolver {
  public InputSource resolveEntity (String publicId, String systemId) {

    StringReader strReader = new StringReader("This is a custom entity");

	System.out.println(" sys id = " + systemId);
	System.out.println(" pub id = " + publicId);
	
/*
    if
(systemId.equals("http://www.builder.com/xml/entities/MyCus
tomEntity")) {
       System.out.println("Resolving entity: " + publicId);
       return new InputSource(strReader);
     } else {
       return null;
     }
	 */
	return null;	 
   }
 } 

