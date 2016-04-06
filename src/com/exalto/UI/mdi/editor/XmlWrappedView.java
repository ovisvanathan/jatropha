/*
 * $Id: XmlView.java,v 1.2 2002/09/25 17:17:14 edankert Exp $
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

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.plaf.ComponentUI;

//import javax.swing.text.WrappedPlainView;
import javax.swing.text.WrappedPlainView;
import javax.swing.text.Element;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.SizeRequirements;

import javax.swing.event.DocumentEvent;
import javax.swing.text.ViewFactory;

import com.exalto.UI.mdi.Editor;

/**
 * The XML View uses the XML scanner to determine the style (font, color) of the 
 * text that it renders.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.2 $, $Date: 2002/09/25 17:17:14 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public class XmlWrappedView extends WrappedPlainView  { //WrappedPlainView {
	private static final boolean DEBUG = false;
	
	private XmlScanner lexer;
	private boolean lexerValid;
	private XmlContext context = null;
	int longLineWidth = 4000;
	int defaultWidth=295;
	
	/**
	 * Construct a colorized view of xml text for the element.
	 * Gets the current document and creates a new Scanner object.
	 *
	 * @param context the styles used to colorize the view.
	 * @param elem the element to create the view for.
	 */
	public XmlWrappedView( XmlContext context, Element elem) {
	    super( elem);
		
		this.context = context;
	    XmlDocument doc = (XmlDocument) getDocument();
		
	
	    try {
		    lexer = new XmlScanner( doc);
	    } catch ( Exception e) {
	        lexer = null;
	    }
			
	    lexerValid = false;
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

			Style style = context.getStyle( lexer.token);

			// If the style changes, do paint...
			if ( style != lastStyle && lastStyle != null) {
			    // color change, flush what we have
		    	g.setColor( context.getForeground( lastStyle));
			    g.setFont( context.getFont( lastStyle));

			    Segment text = getLineBuffer();
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
	    g.setColor( context.getForeground( lastStyle));
	    g.setFont( context.getFont( lastStyle));
	    Segment text = getLineBuffer();
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

			Style style = context.getStyle( lexer.token);

			// If the style changes, do paint...
			if ( style != lastStyle && lastStyle != null) {
			    // color change, flush what we have
			    g.setFont( context.getFont( lastStyle));

			    Segment text = getLineBuffer();
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
	    g.setFont( context.getFont( lastStyle));
	    Segment text = getLineBuffer();
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
	
	protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements result) {
				result = super.calculateMinorAxisRequirements(axis, result);					
	
		
	
				if(!context.getWordWrap()) {
					System.out.println(" calc size req if ");
			
					int width = longLineWidth;
					result.preferred = width;
					Editor ed = (Editor) getContainer();
					if(ed != null) {
						Dimension d = ed.getSize();
						ed.setSize(width, d.height);
					}
				}
				else {
					 Editor edtr = (Editor) getContainer();
					 Component parent= edtr.getParent();
					 
	 				 System.out.println(" current axis = " + axis);  	 
					 
				   	 int parentWidth=parent.getSize().width;
				  	 
					 System.out.println(" parent width " + parentWidth);
			
					 result.minimum = parentWidth;
					 System.out.println(" res pref = " + result.minimum);
				}
				
	
		
				return result;
		}
	
}
