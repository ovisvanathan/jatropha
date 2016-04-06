/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.UI.converter.csv;

/**
 *
 * @author omprakash.v
 */
/*
 * Copyright (c) 2004-2005 Progress Software Corporation. All rights reserved.
 * http://www.progress.com
 *
 * Stylus Studio®
 * http://www.stylusstudio.com/
 *
 * Convert to XML CSV Adapter Java Source Code
 *
 * This code is part of Stylus Studio®'s Non-XML Adapter interface.  Only
 * licensed users of Stylus Studio® XML Professional Edition may use this
 * code.  For those licensed users, this code may be used as a basis for
 * designing your own classes for use with Stylus Studio®.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//import com.stylusstudio.adapter.AdapterBase;
//import com.stylusstudio.adapter.AdapterHandler;
//import com.stylusstudio.adapter.InvalidFormatException;

public class CSV // extends AdapterBase
{
	public String getExtensions() {
		return "csv";
	}
	public String getUrlName() {
		return "Comma";
	}
	public String getDescription() {
		return "Comma Separated Values";
	}

	static public final char m_comma = ',';
	// Just change m_comma to '\t' to turn it into a Tab Separated Value adapter
	// or ':' for a Colon Separated Value adapter, or whatever you desire.

	public void toXML(InputStream in, OutputStream out) throws IOException //, InvalidFormatException
	{

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;

		Document doc = new DocumentImpl();
		Element root = doc.createElement("table");
		doc.appendChild(root);

		Element row, col;
		char c;
		StringBuffer text;
		char quote;
		int escape;
		char prevc, prevquote;

		while ((line = br.readLine()) != null) {
			row = doc.createElement("row");
			root.appendChild(row);
			quote = 0;
			escape = 0;
			text = new StringBuffer();
			c = 0;
			prevquote = 0;
			for (int i = 0; i < line.length(); i++) {
				prevc = c;
				c = line.charAt(i);
				if (escape > 0) {
					escape--;
				}
				else
				if (c == '\\') {
					escape = 1; // a backslash escapes any character within the line
					continue;
				}
				else
				if (quote == 0 && prevquote == c && prevc == c) {
					prevquote = c;
					quote = c; // a doubled quote within a string escapes it
				}
				else
				if (quote == 0 && (c == '"' || c == '\'')) {
					prevquote = quote;
					quote = c;
					continue;
				}
				else
				if (c == quote) {
					prevquote = quote;
					quote = 0;
					continue;
				}
				else
				if (c == m_comma && quote == 0) {
					col = doc.createElement("column");
					if (text != null)
						col.appendChild(doc.createTextNode(text.toString()));
					row.appendChild(col);
					text = new StringBuffer();
					continue;
				}

				text.append(c);
			}
			if (text != null) {
				col = doc.createElement("column");
				col.appendChild(doc.createTextNode(text.toString()));
				row.appendChild(col);
			}
		}

		OutputFormat format = new OutputFormat(doc);
		format.setIndent(4);
		format.setLineSeparator(getEndOfLine());
		XMLSerializer serial = new XMLSerializer(out, format);
		serial.serialize(doc);
		out.flush();
	}

	public void fromXML(InputStream in, OutputStream out) throws IOException //, InvalidFormatException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			parser.parse(in, new CommaHandler(out));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CommaHandler extends DefaultHandler {
		private BufferedWriter m_out;
		private int m_depth = 0;
		private StringBuffer m_text = null;
		private boolean m_first = true;

		public CommaHandler(OutputStream out) {
			m_out = new BufferedWriter(new OutputStreamWriter(out));
		}

		public void startElement(String uri, String local, String qName, Attributes atts) throws SAXException {
			m_depth++;
			if (m_depth == 2) // start row
				m_first = true;
			else
			if (m_depth == 3) // start column
				m_text = new StringBuffer();
		}

		public void endElement(String uri, String local, String qName) throws SAXException {
			if (m_depth == 2) { // finish row
				try {
					m_out.write(getEndOfLine());
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			else
			if (m_depth == 3) { // finish column
				String text = m_text.toString();
				if (text.indexOf(m_comma) != -1 || text.indexOf('"') != -1 || text.indexOf('\'') != -1) {
					if (text.indexOf('"') != -1)
						text = text.replaceAll("\"", "\"\"");
					text = '"' + text + '"';
				}

				try {
					if (!m_first)
						m_out.write(m_comma);
					m_out.write(text);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}

				m_first = false;
				m_text = null;
			}
			m_depth--;
		}

		public void characters(char[] text, int start, int length) throws SAXException {
			if (m_text != null)
				m_text.append(text, start, length);
		}

		public void endDocument() throws SAXException {
			try {
				m_out.flush();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
    

    public static void main(String [] args) {

        CSV csv = new CSV();

        FileWriter fout;
        	try {

                String path = "f:/Caps/aatma/csv.out";

                String path2 = "f:/Caps/aatma/prices.csv";

  //              FileInputStream fin = new FileInputStream("f:/Caps/aatma/prices.xml");

    //            FileOutputStream fouts = new FileOutputStream(new File(path));

//                csv.fromXML(fin, fouts);

                FileInputStream fin2 = new FileInputStream("f:/Caps/aatma/csv.out");

                FileOutputStream fouts2 = new FileOutputStream(new File(path2));

      //          BufferedReader br = new BufferedReader(new FileReader(new File("f:/Caps/aatma/csv.out")));

      //          String line = null;
      //  		while ((line = br.readLine()) != null)
      //           		System.out.println(line);


                csv.toXML(fin2, fouts2);


            //    fin.close();
                fin2.close();
            //    fouts.close();
                fouts2.close();
            }
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
			catch (Exception ioe) {
				ioe.printStackTrace();
			}



    }

public String getEndOfLine() {
    String OS = "windows";
    if(OS.equals("Unix"))
        return "\n";
    else
        return "\r\n";

       

}

}
