package com.exalto.UI.grid;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import java.io.*;

public class XMLRoutines {
 public static void write(Document doc, Writer out) throws Exception {
  write(doc.getDocumentElement(), out);
 }
 public static void write(Node node, Writer out) throws Exception {
  if (node==null || out==null)
   return;
  int type = node.getNodeType();
  switch (type) {
  case Node.DOCUMENT_NODE:
   write(((Document)node).getDocumentElement(), out);
   out.flush();
   break;
  case Node.ELEMENT_NODE:
   out.write('<');
   out.write(node.getNodeName());
   NamedNodeMap attrs = node.getAttributes();
   for (int k = 0; k< attrs.getLength(); k++ ) {
    Node attr = attrs.item(k);
    out.write(' ');
    out.write(attr.getNodeName());
    out.write("=\"");
    out.write(attr.getNodeValue());
    out.write('"');
   }
   out.write('>');
   break;
  case Node.ENTITY_REFERENCE_NODE:
   out.write('&');
   out.write(node.getNodeName());
   out.write(';');
   break;
  // print cdata sections
  case Node.CDATA_SECTION_NODE:
   out.write("<![CDATA[");
   out.write(node.getNodeValue());
   out.write("]]>");
   break;
  // print text
  case Node.TEXT_NODE:
   out.write(node.getNodeValue());
   break;
  // print processing instruction
  case Node.PROCESSING_INSTRUCTION_NODE:
   out.write("<?");
   out.write(node.getNodeName());
   String data = node.getNodeValue();
   if ( data != null && data.length() > 0 ) {
    out.write(' ');
    out.write(data);
   }
   out.write("?>");
   break;
  default:
   out.write("<TYPE="+type);
   out.write(node.getNodeName());
   out.write("?>");
   break;
  }
  NodeList children = node.getChildNodes();
  if ( children != null ) {
    for ( int k = 0; k<children.getLength(); k++ ) {
    write(children.item(k), out);
   }
  }
  if (node.getNodeType() == Node.ELEMENT_NODE ) {
   out.write("</");
   out.write(node.getNodeName());
   out.write('>');
  }
  out.flush();
 }
 public static boolean isLegalXmlName(String input) {
  if (input == null || input.length() == 0)
   return false;
  for (int k=0; k<input.length(); k++) {
   char ch = input.charAt(k);
   if (
    Character.isLetter(ch) ||
    (ch == '_') || (ch == ':') ||
    (k>0 &&
     (Character.isDigit(ch) ||
     (ch == '.') || (ch == '-')
     )
    )
   )
    continue;
   return false;
  }
  return true;
 }


 protected static boolean canDisplayNode(Node node) {
	
	 switch (node.getNodeType()) {
		
		case Node.ELEMENT_NODE:
		   return true;
		
		case Node.TEXT_NODE:
		   String text = node.getNodeValue().trim();
		   return !(text.equals("") || text.equals("\n") || text.equals("\r\n"));
	
		case Node.DOCUMENT_NODE:
			return true;
	}
  
	return false;
 }



}