// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 7/24/2008 4:08:55 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   XmlViewer.java

package com.exalto.UI.util;


import java.io.Writer;
import java.util.Iterator;
import java.util.List;
// PREREL  OVERRIDE TO RET STRING
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//OM import com.exalto.UI.grid.xpath.JaxenXPathEvaluator;

public class XMLRoutines
{
	static StringBuffer namebuf = new StringBuffer(); 
	static JaxenXPathEvaluator xpe;

// PREREL  A 2 LNS
	static Vector unsaved;
	static boolean skipped;
	
	
	static class JaxenXPathEvaluator {
	
		static void setDocument(Document doc) {
		
		}
	
		static List evaluate(String s, boolean b) {
			return null;
		}
	}
	
    static
    {
	//	xpe = new JaxenXPathEvaluator();
    }

    public static void write(Document document, Writer writer, Vector unsaved)
        throws Exception
    {
   // PREREL  A 1 LNS
		XMLRoutines.unsaved = unsaved;
   // 	xpe = new XMLRoutines.JaxenXPathEvaluator();
    //OM	xpe.setDocument(document);
    
		JaxenXPathEvaluator.setDocument(document);
    
		write(((Node) (document.getDocumentElement())), writer);
    }

    public static void write(Node node, Writer writer)
        throws Exception
    {
        if(node == null || writer == null)
            return;
        short word0 = node.getNodeType();
        switch(word0)
        {
        case 9: // '\t'
            write(((Node) (((Document)node).getDocumentElement())), writer);
            writer.flush();
            break;

        case 1: // '\001'

		// PREREL  M TO WRITE TO NAMEBUF BEGIN

          //  writer.write(60);
            namebuf.append("<");

            NamedNodeMap namednodemap = node.getAttributes();
            
            if(namednodemap.getLength() > 0 || node.getNodeName().equals("projects")) {
            	
    //        	writer.write(node.getNodeName());
                namebuf.append(node.getNodeName());

            	for(int i = 0; i < namednodemap.getLength(); i++)
            	{
	                Node node1 = namednodemap.item(i);
	    //            writer.write(32);
					 namebuf.append(" ");
		//	          writer.write(node1.getNodeName());
					 namebuf.append(node1.getNodeName());
					
					
			//		writer.write("=\"");
	                
					 namebuf.append("=\"");

			//		writer.write(node1.getNodeValue());
					 namebuf.append(node1.getNodeValue());

	           //     writer.write(34);
			   		 namebuf.append("\"");


				}

           // 	writer.write(62);
            	 namebuf.append(">");

				writer.write(namebuf.toString());
				 	namebuf = new StringBuffer();
           


            }  else {
           	    namebuf.append(node.getNodeName());
           	    
            }
            // PREREL  M TO WRITE TO NAMEBUF END
            	
            break;

        case 5: // '\005'
            writer.write(38);
            writer.write(node.getNodeName());
            writer.write(59);
            break;

        case 4: // '\004'
            writer.write("<![CDATA[");
            writer.write(node.getNodeValue());
            writer.write("]]>");
            break;

        case 3: // '\003'
		 // PREREL  M TO HIDE UNSAVED FILES BEGIN
					System.out.println( "  case 3 namebuf = " + namebuf);
				
					System.out.println( "  case 3 alreadyExists = " + alreadyExists(node.getNodeValue()));

					System.out.println( "  case 3 alreadyExists = " + presentInUnsaved(node.getNodeValue()));

					System.out.println( "  case 3 getNodeValue = " + node.getNodeValue());
	
					System.out.println( "  case 3 skipped = " + skipped);


			if(namebuf.length() > 0 && !alreadyExists(node.getNodeValue())  && !presentInUnsaved(node.getNodeValue()) ) {
            	writer.write(namebuf.toString());
            	writer.write(62);
            	writer.write(node.getNodeValue());
            	namebuf = new StringBuffer();
                break;

        	} else
				skipped = true;

                namebuf = new StringBuffer();
        		
				// PREREL  M TO HIDE UNSAVED FILES END
				break;
    
        case 7: // '\007'
            writer.write("<?");
            writer.write(node.getNodeName());
            String s = node.getNodeValue();
            if(s != null && s.length() > 0)
            {
                writer.write(32);
                writer.write(s);
            }
            writer.write("?>");
            break;

        case 2: // '\002'
        case 6: // '\006'
        case 8: // '\b'
        default:
            writer.write("<TYPE=" + word0);
            writer.write(node.getNodeName());
            writer.write("?>");
            break;
        }

        NodeList nodelist = node.getChildNodes();
        if(nodelist != null)
        {
            for(int j = 0; j < nodelist.getLength(); j++)
                write(nodelist.item(j), writer);

        }
		// PREREL  M TO HIDE UNSAVED FILES BEGIN
        if(node.getNodeType() == 1 && !skipped)
        {
            writer.write("</");
            writer.write(node.getNodeName());
            writer.write(62);
        } else if(node.getNodeType() != 3) {
			skipped = false;
		}
		// PREREL  M TO HIDE UNSAVED FILES END
        writer.flush();
    }
    
	// PREREL  M TO HIDE UNSAVED FILES BEGIN
    
	public static boolean presentInUnsaved(String entry) {
	
	  System.out.println(" entry = " + entry);

		 for (int i = 0; i < XMLRoutines.unsaved.size(); i++) {

			 if(entry.equals(unsaved.get(i))  && (entry.startsWith("Untitled")  ||  entry.startsWith("XSL Result") )   ) {
				return true;
			 }
   
		 }

		return false;

	}
    	// PREREL  M TO HIDE UNSAVED FILES END
    
	
	public static boolean alreadyExists(String entry) {

    	String xpath = "./text() = '" + entry + "'";
    	
    	List results = JaxenXPathEvaluator.evaluate(xpath, false);
    	
    	
    	Iterator iter = results.iterator();


    	if(iter.hasNext()) {

    		Object o = iter.next();

      		System.out.println( " node type = " + o.getClass().getName());

    		Boolean bool = (Boolean) o;
    		
			System.out.println( " bool = " + bool);
			 
    		return bool.booleanValue();

    	}
    	
		 System.out.println( " node false ");
    	
    	return false;
    
    	
    	
    	
    }
    
    
    public static boolean isLegalXmlName(String s)
    {
        if(s == null || s.length() == 0)
            return false;
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if(!Character.isLetter(c) && c != '_' && c != ':' && (i <= 0 || !Character.isDigit(c) && c != '.' && c != '-'))
                return false;
        }

        return true;
    }
}