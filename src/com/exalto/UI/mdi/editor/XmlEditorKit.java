/*
 * $Id: XmlEditorKit.java,v 1.5 2003/10/01 17:38:09 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Edwin Dankert <edankert@cladonia.com>
 */
package com.exalto.UI.mdi.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.SizeRequirements;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.ParagraphView;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.JEditorPane;

import com.exalto.UI.mdi.Editor;

/**
 * The XML editor kit supports handling of editing XML content.  
 * It supports syntax highlighting, tab replacements and automatic 
 * indents.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.5 $, $Date: 2003/10/01 17:38:09 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public class XmlEditorKit extends StyledEditorKit {

    private static Font font = null;

    private XmlContext context = null;
	private ViewFactory factory = null;
	private Editor editor = null;
    private static final Object RESPONSE_TYPE = new String("Response");

    String prev_kind = "";
    
	/**
	 * Constructs the view factory and the Context.
	 */
    public XmlEditorKit( Editor editor) {
		super();
		
		this.editor = editor;

		factory = new XmlViewFactory();
	    context = new XmlContext();
    }


    /**
     * Get the MIME type of the data that this
     * kit represents support for. This kit supports
     * the type <code>text/xml</code>.
	 *
	 * @return the type.
     */
    public String getContentType() {
		return "text/xml";
    }

    public void setFont( Font font) {
    	this.font = font;
    	
    	context.setFont( font);
    }

    /**
     * Get the Font type for this kit.
     *
     * @return the font.
     */
    public Font getFont() {
		return font;
    }
	
	public XmlContext getContext() {
		return context;
	}

    /**
     * Creates an uninitialized xml document.
     *
     * @return the document
     */
    public Document createDefaultDocument() {
		return new XmlDocument( editor);
    }

    /**
     * Fetches the XML factory that can produce views for 
	 * XML Documents.
     *
     * @return the XML factory
     */
    public final ViewFactory getViewFactory() {
		return factory;
    }
	
	/**
	 * A simple view factory implementation. 
	 */
	class XmlViewFactory implements ViewFactory {
		// Creates the XML View.
		public View create(Element elem) {
		//	System.out.println(" inside xleditorkit create ");

			  String kind = elem.getName();
				
		//	  System.out.println(" kind  " + kind);
		//	  System.out.println(" elem hash  " + elem.hashCode());

		/*	  OMP OLD code word wrap 
			if(!context.getWordWrap()) {
				System.out.println(" inside xleditorkit if wrodwrap ");

				if(elem.getName().equals("paragraph")) {
					System.out.println(" inside xleditorkit paragraph ");
					return new NoWrapParagraphView(context, elem);
				 } else {  
					System.out.println(" inside xleditorkit other ");
					return XmlEditorKit.super.getViewFactory().create(elem);
				}
			}
	          
			return new XmlView(context, elem);
			
		*/	
			  
			  
			  /* OMP new code jtextpane no fold 
	     
	  	     if (kind.equals(AbstractDocument.ContentElementName)) {
                  return new XmlView(context, elem);
	  	     } 
	  	     else if (kind.equals(AbstractDocument.ParagraphElementName)) {
		   	        	return new ParagraphView(elem);
	          } else if (kind.equals(AbstractDocument.SectionElementName)) {
              	return new BoxView(elem, BoxView.Y_AXIS);
              } else if (kind.equals(StyleConstants.ComponentElementName)) {
                  return new ComponentView(elem);
              } else if (kind.equals(StyleConstants.IconElementName)) {
                  return new IconView(elem);
              }

		 return new XmlView( context, elem);
    	
         */
    		 
		//		  System.out.println(" kind  " + kind);
				 
	                if (kind.equals(RESPONSE_TYPE)) {
	                   return new ResponseView(elem);
       //         		 return new ParagraphView(elem);
	               } 
					else 
						if (kind.equals(AbstractDocument.ContentElementName)) {
							return new XmlView(context, elem);
        //                    return XmlEditorKit.super.getViewFactory().create(elem);
	                } 
	                else 
	                	if (kind.equals(AbstractDocument.ParagraphElementName)) {
	                	
	         //       		if(child.getName().equals(RESPONSE_TYPE))
	         //       		return new ResponseView(elem);
	         //       	else
	                			return new ParagraphView(elem);
	                		
               // 			return new MyParagraphView(elem);

	                		
	                } else if (kind.equals(AbstractDocument.SectionElementName)) {
	        //        	return new ResponseView(elem);
	                	return new BoxView(elem, BoxView.Y_AXIS);
	                } else if (kind.equals(StyleConstants.ComponentElementName)) {
	                    return new ComponentView(elem);
	                } else if (kind.equals(StyleConstants.IconElementName)) {
	                    return new IconView(elem);
	                }

			 return new XmlView( context, elem);
				
				
		}
	}
	
	
	
	private void dumpTree(Element elem) {
		
		int n = elem.getElementCount();
		
		for(int k=0;k<n;k++) {
			
			Element e = elem.getElement(k);
			
			if(!e.isLeaf()) {
				dumpTree(e);
			}
			
		//	System.out.println("name = " + e.getName());
		//	System.out.println(" type = " + e.toString());
			
		}
		
		
	}
	private class NoWrapParagraphView extends ParagraphView 
	{
		private XmlScanner lexer;
		private boolean lexerValid;
		private XmlContext ctx = null;	
		int longLineWidth = 4000;
		private static final boolean DEBUG = false;
		
		public NoWrapParagraphView(XmlContext context, Element elem) {
			super(elem);
			
			this.ctx = context;
			
			layoutSpan = Integer.MAX_VALUE;
			// ov added 06/11/2004
			    XmlDocument doc = (XmlDocument) getDocument();
		
			    try {
				    lexer = new XmlScanner( doc);
			    } catch ( Exception e) {
			        lexer = null;
			    }
					
			    lexerValid = false;

		
		}
		
		
		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements result) {
			result = super.calculateMinorAxisRequirements(axis, result);
			
			if(!ctx.getWordWrap()) {
				int width = longLineWidth;
				result.preferred = width;
				Editor ed = (Editor) getContainer();
				if(ed != null) {
					Dimension d = editor.getSize();
					ed.setSize(width, d.height);
				}
			}
			
			return result;
		}
		
			/**
			 * Invalidates the scanner, to make sure a new range is set later. 
			 *
			 * @see View#paint
			 */
		    public void paint( Graphics g, Shape a) {
			    super.paint( g, a);
			    lexerValid = false;
			}
		
			/**
			 * Renders the given range in the model as normal unselected
			 * text. This will paint the text according to the styles..
			 *
			 * @param g the graphics context
			 * @param x the starting X coordinate
			 * @param y the starting Y coordinate
			 * @param start the beginning position in the model
			 * @param end the ending position in the model
			 *
			 * @returns the location of the end of the range
			 *
			 * @exception BadLocationException if the range is invalid
			 */
			protected int drawUnselectedText( Graphics g, int x, int y, int start, int end) throws BadLocationException {
			    Document doc = getDocument();
			    Style lastStyle = null;
				int lastToken = 0;
			    int mark = start;
		
			    if (DEBUG) System.out.println( "drawUnselectedText()");
		
			    while ( start < end) {
					updateScanner( start);
		
					int p = Math.min( lexer.getEndOffset(), end);
					p = (p <= start) ? end : p;
		
					Style style = ctx.getStyle( lexer.token);
		
					// If the style changes, do paint...
					if ( style != lastStyle && lastStyle != null) {
					    // color change, flush what we have
				    	g.setColor( ctx.getForeground( lastStyle));
					    g.setFont( ctx.getFont( lastStyle));
		
				//	    Segment text = getLineBuffer();
						Segment text = new Segment();
						doc.getText( mark, start - mark, text);
					    if (DEBUG) System.out.println( text.toString()+" ["+lastToken+"]");
						
					    x = Utilities.drawTabbedText( text, x, y, g, this, mark);
					    mark = start;
					}
		
					lastToken = lexer.token;
					lastStyle = style;
					start = p;
			    }
		
			    // flush remaining
			    g.setColor( ctx.getForeground( lastStyle));
			    g.setFont( ctx.getFont( lastStyle));
		//	    Segment text = getLineBuffer();
				Segment text = new Segment();
			    doc.getText( mark, end - mark, text);
		
			    if (DEBUG) System.out.println( "flush: "+text.toString()+" ["+lastToken+"]");
			    x = Utilities.drawTabbedText( text, x, y, g, this, mark);
		
			    return x;
			}
		
			/**
			 * Renders the given range in the model as selected text. 
			 * This will paint the text according to the font as found in the styles..
			 *
			 * @param g the graphics context
			 * @param x the starting X coordinate
			 * @param y the starting Y coordinate
			 * @param start the beginning position in the model
			 * @param end the ending position in the model
			 *
			 * @returns the location of the end of the range
			 *
			 * @exception BadLocationException if the range is invalid
			 */
			protected int drawSelectedText( Graphics g, int x, int y, int start, int end) throws BadLocationException {
			    Document doc = getDocument();
			    Style lastStyle = null;
				int lastToken = 0;
			    int mark = start;
		
			    g.setColor( Color.black);
		//	    g.setBackground( Color.black);
		
			    while ( start < end) {
					updateScanner( start);
		
					int p = Math.min( lexer.getEndOffset(), end);
					p = (p <= start) ? end : p;
		
					Style style = ctx.getStyle( lexer.token);
		
					// If the style changes, do paint...
					if ( style != lastStyle && lastStyle != null) {
					    // color change, flush what we have
					    g.setFont( ctx.getFont( lastStyle));
		
					    // Segment text = getLineBuffer();
						Segment text = new Segment();
					    doc.getText( mark, start - mark, text);
					    if (DEBUG) System.out.println( text.toString()+" ["+lastToken+"]");
						
					    x = Utilities.drawTabbedText( text, x, y, g, this, mark);
					    mark = start;
					}
		
					lastToken = lexer.token;
					lastStyle = style;
					start = p;
			    }
		
			    // flush remaining
			    g.setFont( ctx.getFont( lastStyle));
			   // Segment text = getLineBuffer();
			   Segment text = new Segment();
			   doc.getText( mark, end - mark, text);
		
			    if (DEBUG) System.out.println( "flush: "+text.toString()+" ["+lastToken+"]");
			    x = Utilities.drawTabbedText( text, x, y, g, this, mark);
		
			    return x;
			}
		
			// Update the scanner to point to the '<' begin token.
			private void updateScanner( int p) {
			    try {
					if ( !lexerValid) {
					    XmlDocument doc = (XmlDocument) getDocument();
					    lexer.setRange( doc.getTagEnd( p), doc.getLength());
					    lexerValid = true;
					}
		
					while ( lexer.getEndOffset() <= p) {
					    lexer.scan();
					}
			    } catch ( Throwable e) {
					// can't adjust scanner... calling logic
					// will simply render the remaining text.
					e.printStackTrace();
			    }
			}

		
		
	}
	
	
    /**
     * Inserts content from the given stream which is expected 
     * to be in a format appropriate for this kind of content
     * handler.
     * 
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void read(InputStream in, Document doc, int pos) 
        throws IOException, BadLocationException {

        read(new InputStreamReader(in), doc, pos);
    }

    /**
     * Inserts content from the given stream, which will be 
     * treated as plain text.
     * 
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
   /*
    public void read(Reader in, Document doc, int pos) 
        throws IOException, BadLocationException {

        char[] buff = new char[4096];
        int nch;
	boolean lastWasCR = false;
	boolean isCRLF = false;
	boolean isCR = false;
	int last;
	boolean wasEmpty = (doc.getLength() == 0);
        AttributeSet attr = getInputAttributes();
        
	//	XmlDocument xmldoc = (XmlDocument) doc;
        

	// Read in a block at a time, mapping \r\n to \n, as well as single
        // \r's to \n's. If a \r\n is encountered, \r\n will be set as the
        // newline string for the document, if \r is encountered it will
        // be set as the newline character, otherwise the newline property
        // for the document will be removed.
        while ((nch = in.read(buff, 0, buff.length)) != -1) {
	    last = 0;
	    for(int counter = 0; counter < nch; counter++) {
		switch(buff[counter]) {
		case '\r':
		    if (lastWasCR) {
			isCR = true;
			if (counter == 0) {
                            doc.insertString(pos, "\n", attr);
        //        			  xmldoc.appendBatchLineFeed(pos, "\n", attr);
			    pos++;
			}
			else {
			    buff[counter - 1] = '\n';
			}
		    }
		    else {
			lastWasCR = true;
		    }
		    break;
		case '\n':
		    if (lastWasCR) {
			if (counter > (last + 1)) {
			    
				doc.insertString(pos, new String(buff, last,
                                            counter - last - 1), attr);
		//		xmldoc.appendBatchString(pos, new String(buff, last,
			//	                                           counter - last - 1), attr);
			    
			    
			    pos += (counter - last - 1);
			}
			// else nothing to do, can skip \r, next write will
			// write \n
			lastWasCR = false;
			last = counter;
			isCRLF = true;
		    }
		    break;
		default:
		    if (lastWasCR) {
			isCR = true;
			if (counter == 0) {
                            
					doc.insertString(pos, "\n", attr);
				//	xmldoc.appendBatchLineFeed(pos, "\n", attr);
                            
			    pos++;
			}
			else {
			    buff[counter - 1] = '\n';
			}
			lastWasCR = false;
		    }
		    break;
		}
	    }
	    if (last < nch) {
		if(lastWasCR) {
		    if (last < (nch - 1)) {
			doc.insertString(pos, new String(buff, last,
                                         nch - last - 1), attr);
		//	xmldoc.appendBatchString(pos, new String(buff, last,
         //           nch - last - 1), attr);
			
			pos += (nch - last - 1);
		    }
		}
		else {
		    doc.insertString(pos, new String(buff, last,
                                     nch - last), attr);
		    
		 //   xmldoc.appendBatchString(pos, new String(buff, last,
          //          nch - last), attr);
		    
		    pos += (nch - last);
		}
	    }
        }
	if (lastWasCR) {
            doc.insertString(pos, "\n", attr);
    	//	xmldoc.appendBatchLineFeed(pos, "\n", attr);
            
	    isCR = true;
	}
	if (wasEmpty) {
	    if (isCRLF) {
		doc.putProperty(EndOfLineStringProperty, "\r\n");
	    }
	    else if (isCR) {
		doc.putProperty(EndOfLineStringProperty, "\r");
	    }
	    else {
		doc.putProperty(EndOfLineStringProperty, "\n");
	    }
	}
	
//	((XmlDocument) doc).processBatchUpdates(0);

	
    }
*/
    
    /**
     * Inserts content from the given stream, which will be 
     * treated as plain text.
     * 
     * @param in  The stream to read from
     * @param doc The destination for the insertion.
     * @param pos The location in the document to place the
     *   content >= 0.
     * @exception IOException on any I/O error
     * @exception BadLocationException if pos represents an invalid
     *   location within the document.
     */
    public void readBulk(Reader in, Document doc, int pos) 
        throws IOException, BadLocationException {

    char[] buff = new char[4096];
    int nch;
	boolean lastWasCR = false;
	boolean isCRLF = false;
	boolean isCR = false;
	int last;
	boolean wasEmpty = (doc.getLength() == 0);
    AttributeSet attr = getInputAttributes();

	try {
		// Read in a block at a time, mapping \r\n to \n, as well as single
		    // \r's to \n's. If a \r\n is encountered, \r\n will be set as the
		    // newline string for the document, if \r is encountered it will
		    // be set as the newline character, otherwise the newline property
		    // for the document will be removed.
	
		
		XmlDocument xmldoc = (XmlDocument) doc;
		
		
		BufferedReader bufRead = new BufferedReader((FileReader) in); 
//		while ((nch = in.read(buff, 0, buff.length)) != -1) {
		String s = null;
		while((s = bufRead.readLine()) != null) {
		
			xmldoc.appendBatchString(0, s, attr);
		  	
		}
		
		xmldoc.processBatchUpdates(0);
		    	
	/*	    	
		    last = 0;
		    for(int counter = 0; counter < nch; counter++) {
			switch(buff[counter]) {
			case '\r':
			    if (lastWasCR) {
				isCR = true;
				if (counter == 0) {
 //                           doc.insertString(pos, "\n", attr);
				//		doc.insertString(pos, "\n", attr);
			//		      xmldoc.appendBatchLineFeed(attr);
				          
					
				    pos++;
				}
				else {
				    buff[counter - 1] = '\n';
				}
			    }
			    else {
				lastWasCR = true;
			    }
			    break;
			case '\n':
			    if (lastWasCR) {
				if (counter > (last + 1)) {
				//    doc.insertString(pos, new String(buff, last,
		          //                              counter - last - 1), attr);
				    
					String sb = new String(buff, last, counter - last - 1);
					
					xmldoc.appendBatchString(pos, sb, attr);

				    
				    pos += (counter - last - 1);
				}
				// else nothing to do, can skip \r, next write will
				// write \n
				lastWasCR = false;
				last = counter;
				isCRLF = true;
			    }
			    break;
			default:
			    if (lastWasCR) {
				isCR = true;
				if (counter == 0) {
		     //                   doc.insertString(pos, "\n", attr);
		//			doc.insertString(pos, "\n", attr);
		//		      xmldoc.appendBatchString(pos, "\n", attr);
						
				    pos++;
				}
				else {
				    buff[counter - 1] = '\n';
				}
				lastWasCR = false;
			    }
			    break;
			}
		    }
		    if (last < nch) {
			if(lastWasCR) {
			    if (last < (nch - 1)) {
				
			//    	doc.insertString(pos, new String(buff, last,
		    //                                 nch - last - 1), attr);

			   // 	doc.insertString(pos, new String(buff, last,
		        //            nch - last - 1), attr);

			    	xmldoc.appendBatchString(pos, new String(buff, last,
		                    nch - last - 1), attr);

			    	
				pos += (nch - last - 1);
			    }
			}
			else {
				
			//    doc.insertString(pos, new String(buff, last,
		    //                             nch - last), attr);
			    
			//  	doc.insertString(pos, new String(buff, last,
		     //           nch - last), attr);
			  	
				xmldoc.appendBatchString(pos, new String(buff, last,
	                    nch - last), attr);

  
			    pos += (nch - last);
			}
		    }
		    }
		if (lastWasCR) {
		    //    doc.insertString(pos, "\n", attr);
	//		doc.insertString(pos, "\n", attr);
		 //     xmldoc.appendBatchLineFeed(attr);
				
			    
		    isCR = true;
		}
		if (wasEmpty) {
		    if (isCRLF) {
			doc.putProperty(EndOfLineStringProperty, "\r\n");
		    }
		    else if (isCR) {
			doc.putProperty(EndOfLineStringProperty, "\r");
		    }
		    else {
			doc.putProperty(EndOfLineStringProperty, "\n");
		    }
		}
		
			((XmlDocument) doc).processBatchUpdates(0);
		*/
			
			
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		throw e;
	}
	
	
    }
	
	
		
	
}
