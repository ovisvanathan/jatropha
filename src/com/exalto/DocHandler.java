package com.exalto;

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

import org.apache.log4j.Logger;

/** A SAX handler that prints out the start tags, end tags,
 *  and first word of tag body. Indents two spaces
 *  for each nesting level.
 */

public class DocHandler extends DefaultHandler {
  private int indentation = 0;
  
  Document doc;
  Element root;	
  Element currElem;
  int tableid = 0;
  String tableName;
  private Logger logger = Logger.getLogger(TableAutoFormatter.class.getName());	
  Hashtable tableWidths;
  Locator locator;
  boolean tableColPresent = false;
  int colNum = 0;
  String colCellName;
//  boolean tableHeaderPresent = false;
	boolean addTableColElement = false;	
	boolean captionElement = false;
  
  ColWidthInfo twInfo;
  ArrayList tableInfoList;
  Hashtable ColWidths;
  Hashtable cellWidths;
  Stack tableStack = new Stack();
  TableAutoFormatter formatter;
  double[] colNameWidths = null;
  int colCellNo = 0;
  int noOfCols = 0;
  boolean tableHeaderPresent = false;
  StringBuffer textBuffer;
  
  	public DocHandler(Hashtable twidths, TableAutoFormatter tableFormatter) {
		tableWidths = twidths;
		formatter = tableFormatter;
	}

   /**
       * SAX Handler for the start of the document
       */
      public void startDocument()
      throws SAXException {
		  logger.info("building formatting object tree");
      }
  
      public void endDocument()
      throws SAXException {
          logger.info("Parsing of document complete, stopping renderer");
          
		  try {
			  XMLOutputter outp = new XMLOutputter();
			  outp.output(doc, System.out);
		  } catch(IOException e) {
		  	logger.error("Caught ioexception in XMLOutputter" + e.getMessage());
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
      throws SAXException {
 //   indent(indentation);
    System.out.print("Start tag: " + qualifiedName);
    int numAttributes = attributes.getLength();

	echoText();

	Hashtable otherAttrs = new Hashtable();
	
	try {
	
	if(qualifiedName.equals("fo:table")) {
		tableName = "t" + prefixZeros(tableid);
		tableid++;
		
		String parentTableName = null;
		ColumnInfo parentTableInfo = new ColumnInfo();		
				
		if(!tableStack.isEmpty())
			parentTableInfo = (ColumnInfo) tableStack.peek();
			
		parentTableName = parentTableInfo.getColName();
		
		ColumnInfo currTableInfo = new ColumnInfo();
		currTableInfo.setColName(tableName);
		if(parentTableName != null) {
			ColumnInfo parentInfo = (ColumnInfo) tableStack.pop();
			parentInfo.setColNo(colNum);
			tableStack.push(parentInfo);
			colNum = 0;
		}
		
		tableStack.push(currTableInfo);

		System.out.println("table elem  name " + tableName);
		System.out.println("table elem parent table name " + parentTableName);
		System.out.println("table elem col cell name " + colCellName);
		
		formatter.assignPctColWidths(tableName, parentTableName, colCellName);

		
		ArrayList tableInfoList = (ArrayList) tableWidths.get(tableName);	
		ColWidthInfo twInfo = (ColWidthInfo) tableInfoList.get(0);
		
		noOfCols = twInfo.getNumberOfCols();

		System.out.println("no of cols " + noOfCols);

		Hashtable colWidths = (Hashtable) tableInfoList.get(1);
		System.out.println("columnWidths size =" + colWidths.size());

		colNameWidths = new double[noOfCols]; 

		Enumeration enumerate = colWidths.keys();
		while(enumerate.hasMoreElements()) {
			String key = (String) enumerate.nextElement();
			System.out.println("currColName  =" + key);

			String colnum = key.substring(3, key.length());
			int currentCol = Integer.parseInt(colnum);

			ColWidthInfo tcInfo = (ColWidthInfo) colWidths.get(key);
			double colWidth =0.0;
			colWidth = tcInfo.getWidth();
			colNameWidths[currentCol] = colWidth;
		} //end while
		
	//	tableElementSeen = true;
	} 
	else if(qualifiedName.equals("fo:table-column")) {
			System.out.println(" inside fo:tablecolumn " );

			double val = colNameWidths[colNum]; 
			otherAttrs = new Hashtable();
			String attrKey = "column-width";
			otherAttrs.put(attrKey, new Double(val).toString());
			tableColPresent = true;

			System.out.println("otherattrs size " +otherAttrs.size());

			colNum++;
	}
	else if(qualifiedName.equals("fo:table-row")) {
		colCellNo = 0;
	}
	else if(qualifiedName.equals("fo:table-cell")) {
		colCellName = "col" + prefixZeros(colCellNo);
		colCellNo++;
	}
	else if(qualifiedName.equals("fo:table-header")) {
		if(!tableColPresent) {
			addTableColElement = true;	
		}
		tableHeaderPresent = true;
	}
	else if(qualifiedName.equals("fo:table-body")) {
			if(!tableColPresent && tableHeaderPresent == false) {
				addTableColElement = true;	
			}
			tableHeaderPresent = false;
	} else if(qualifiedName.equals("fo:table-and-caption") || 
			qualifiedName.equals("fo:table-caption") ) {
				return;
	//	captionElement = true;
	}
	
	
      		System.out.println(" qname = " + qualifiedName);
			int cpos = qualifiedName.indexOf(":");	
			String qname = qualifiedName.substring(cpos+1, qualifiedName.length());
			
			if(addTableColElement) {
				System.out.println(" inside addtablecolelement " );

				for(int i=0;i<noOfCols;i++) {
					Element colElem = new Element("table-column", "fo","http://www.w3.org/1999/XSL/Format"); 
					colElem.setAttribute("column-number", new Integer(i+1).toString());
					colElem.setAttribute("column-width", new Double(colNameWidths[i]).toString());					
		
					double val = colNameWidths[i]; 
					String attrKey = "column-width";
					colElem.setAttribute(attrKey, new Double(val).toString());
					currElem.addContent(colElem);
				}
				
				addTableColElement = false;
			}
			
			Element elem = new Element(qname, "fo","http://www.w3.org/1999/XSL/Format"); 
			List attrs = Collections.synchronizedList(new LinkedList());
			int len = attributes.getLength();
			for(int i=0;i<len;i++) {
				String name = attributes.getQName(i);
            	String value = attributes.getValue(i);
				
				int colpos = name.indexOf(":");
				name = name.substring(colpos+1, name.length());
				Attribute attr = new Attribute(name, value);
				attrs.add(attr);	
			}
		
			System.out.println(" otherattrs size" + otherAttrs.size());
		
		
			Attribute colAttr = null;
			String dval = null;
			for(Enumeration e=otherAttrs.keys();e.hasMoreElements();) {
				String key = (String) e.nextElement();
				dval = (String) otherAttrs.get(key);
				colAttr = new Attribute(key, dval);
			//	attrs.add(colAttr);
			
			System.out.println(" inside other attr setting colwidth ");			

			
			//	elem.addAttribute(key, dval);
			}
			
			elem.setAttributes(attrs);
			if(colAttr != null)
				elem.setAttribute(colAttr);
			
			if(root == null) {
				doc = new Document(elem);
				root = elem;	
			} else {
				currElem.addContent(elem);
			}
			
			
			currElem = elem;
			
				} catch(Exception e) {
			e.printStackTrace();
		}
	
	
   }
  /** When you see the end tag, print it out and decrease
   *  indentation level by 2.
   */
  
  public void endElement(String namespaceUri,
                         String localName,
                         String qualifiedName)
      throws SAXException {
    System.out.println("End tag: " + qualifiedName);
  
  		echoText();

  		if(qualifiedName.equals("fo:table")) {
  			tableStack.pop();
		}
			
			
			
  		if(!qualifiedName.equals("fo:root") && 
			!qualifiedName.equals("fo:table-and-caption") &&
			!qualifiedName.equals("fo:table-caption") ) 
		{
  			currElem = (Element) currElem.getParent();
		}
  		
  }

  /** Print out the first word of each tag body. */
  
  public void characters(char[] chars,
                         int startIndex,
                         int endIndex) 
  {
	  String s = new String(chars, startIndex, endIndex);
	  if (textBuffer == null) {
	    textBuffer = new StringBuffer(s);
	  } else {
	    textBuffer.append(s);
	  }
  }
  
  public void setDocumentLocator(Locator locator) {
          this.locator = locator;
    }

  private void indent(int indentation) {
    for(int i=0; i<indentation; i++) {
      System.out.print(" ");
    }
  }
  
    /**
       * Return the value explicitly specified on this FO.
       * @param propertyName The name of the base property whose value is desired.
       * @return The value if the property is explicitly set, otherwise null.
       */
  	public  String prefixZeros(int id) {
  
  		String idstr = Integer.toString(id);
  		if (idstr.length() == 1) {
  			idstr = "0" + idstr;
  			return idstr;
  		} else {
  			return idstr;
  		}
  	}
	
	public Document getDocument() {
		if(doc == null) {
			throw new NullPointerException("doc is null");
		}
		return doc;
	}
	
	private void echoText() throws SAXException
	{
	  if (textBuffer == null) return;
		  String s = ""+textBuffer;
		  currElem.addContent(new Text(s));
		  System.out.println("printing chars:::" + s);
		  textBuffer = null;
	}


  
}