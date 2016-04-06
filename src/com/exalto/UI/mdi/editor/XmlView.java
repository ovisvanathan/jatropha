package com.exalto.UI.mdi.editor;
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

// package xngr;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.GlyphView;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.LabelView;
import javax.swing.text.LayeredHighlighter;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabExpander;
import javax.swing.text.Utilities;
import javax.swing.text.View;

import com.exalto.UI.painter.SegmentCache;
import com.exalto.UI.painter.SimpleClassLoader;
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
public class XmlView extends LabelView implements TabExpander  { //WrappedPlainView {
	private static final boolean DEBUG = false;
	
	private XmlScanner lexer;
	private boolean lexerValid;
	private XmlContext context = null;

    private static HashMap patternColors;
    private static String TAG_PATTERN = "(</?[a-z]*:?[a-z]*)\\s?>?";
    private static String TAG_END_PATTERN = "(/>)";
    private static String TAG_ATTRIBUTE_PATTERN = "\\s(\\w*)\\=";
    private static String TAG_ATTRIBUTE_VALUE = "[a-z-]*\\=(\"[^\"]*\")";
    private static String TAG_COMMENT = "(<!--.*-->)";
    private static String TAG_CDATA_START = "(\\<!\\[CDATA\\[).*";
    private static String TAG_CDATA_END = ".*(]]>)";

    
    static {
        // NOTE: the order is important!
        patternColors = new HashMap();
        patternColors.put(Pattern.compile(TAG_CDATA_START), new Color(128, 128, 128));
        patternColors.put(Pattern.compile(TAG_CDATA_END), new Color(128, 128, 128));
        patternColors
 //               .put(Pattern.compile(TAG_PATTERN), new Color(63, 127, 127));
        		  .put(Pattern.compile(TAG_PATTERN), new Color(255, 0, 0));
        
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_PATTERN), new Color(
                127, 0, 127));
        patternColors.put(Pattern.compile(TAG_END_PATTERN), new Color(63, 127,
                127));
        patternColors.put(Pattern.compile(TAG_ATTRIBUTE_VALUE), new Color(42,
                0, 255));
  //      patternColors.put(Pattern.compile(TAG_COMMENT), new Color(63, 95, 191));
        patternColors.put(Pattern.compile(TAG_COMMENT), new Color(0, 183, 239));
    }

    /**
     * Glyph rendering functionality.
     */
    GlyphPainter painter;
    /**
     * The prototype painter used by default.
     */
    static GlyphPainter defaultPainter;

    /**
     * Used by paint() to store highlighted view positions
     */
     private byte[] selections = null;
	
     // For caching colors
     HashMap colorsMap = new HashMap();
     
	/**
	 * Construct a colorized view of xml text for the element.
	 * Gets the current document and creates a new Scanner object.
	 *
	 * @param context the styles used to colorize the view.
	 * @param elem the element to create the view for.
	 */
	public XmlView( XmlContext context, Element elem) {
	    super( elem);
		
		this.context = context;
	    XmlDocument doc = (XmlDocument) getDocument();

	    // Set tabsize to 4 (instead of the default 8)
        getDocument().putProperty(PlainDocument.tabSizeAttribute, new Integer(4));

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
    	
    	
	  //  super.paint( g, a);
    	
    	checkPainter();

    	boolean paintedText = false;
    	Component c = getContainer();
    	int p0 = getStartOffset();
    	int p1 = getEndOffset();
    	Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
    	Color bg = getBackground();
    	Color fg = getForeground();
    	if (bg != null) {
    	    g.setColor(bg);
    	    g.fillRect(alloc.x, alloc.y, alloc.width, alloc.height);
    	}
    	if (c instanceof JTextComponent) {
    	    JTextComponent tc = (JTextComponent) c;
    	    Highlighter h = tc.getHighlighter();
    	    if (h instanceof LayeredHighlighter) {
    		((LayeredHighlighter)h).paintLayeredHighlights
    		    (g, p0, p1, a, tc, this);
    	    }
    	}

    	if (isComposedTextElement(getElement())) {
    	    paintComposedText(g, a.getBounds(), this);
    	    paintedText = true;
    	} else if(c instanceof JTextComponent) {
    	    JTextComponent tc = (JTextComponent) c;
    	    Color selFG = tc.getSelectedTextColor();

    	    if(selFG != null && !selFG.equals(fg)) {
    		Highlighter.Highlight[] h = tc.getHighlighter().getHighlights();
      
                    if(h.length != 0) {
                        boolean initialized = false;
                        int viewSelectionCount = 0;
                        for (int i = 0; i < h.length; i++) {
                            Highlighter.Highlight highlight = h[i];
                            int hStart = highlight.getStartOffset();
                            int hEnd = highlight.getEndOffset();
                            if (hStart > p1 || hEnd < p0) {
                                // the selection is out of this view
                                continue;
                            }
                            if (hStart <= p0 && hEnd >= p1){
                                // the whole view is selected
                            	paintTextUsingColor(g, a, selFG, p0, p1);
                                paintedText = true;
                                break;
                            }
                            // the array is lazily created only when the view
                            // is partially selected
                            if (!initialized) {
                                initSelections(p0, p1);
                                initialized = true;
                            }
                            hStart = Math.max(p0, hStart);
                            hEnd = Math.min(p1, hEnd);
                            paintTextUsingColor(g, a, selFG, hStart, hEnd);
                            // the array represents view positions [0, p1-p0+1]
                            // later will iterate this array and sum its
                            // elements. Positions with sum == 0 are not selected.
                            selections[hStart-p0]++;
                            selections[hEnd-p0]--;

                            viewSelectionCount++;
                        }

                        if (!paintedText && viewSelectionCount > 0) {
                            // the view is partially selected
                            int curPos = -1;
                            int startPos = 0;
                            int viewLen = p1 - p0;
                            while (curPos++ < viewLen) {
                                // searching for the next selection start
                                while(curPos < viewLen &&
                                        selections[curPos] == 0) curPos++;

                                if (startPos != curPos) {
                                	paintRegionsUsingColor(g, a, fg,
                                            p0 + startPos, p0 + curPos);
                                	
                                	// paint unselected text
                               //     paintTextUsingColor(g, a, fg,
                              //              p0 + startPos, p0 + curPos);
                                }
                                int checkSum = 0;
                                // searching for next start position of unselected text
                                while (curPos < viewLen &&
                                        (checkSum += selections[curPos]) != 0) curPos++;
                                startPos = curPos;
                            }
    			paintedText = true;
    		    }
    		}
    	    }
    	}
    	if(!paintedText)
    	    paintRegionsUsingColor(g, a, fg, p0, p1);

    	
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
/*
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
	*/
    
    /*
  OV 09/01/09  
	protected int drawUnselectedText(Graphics graphics, int x, int y, int p0,
            int p1) throws BadLocationException {

        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);
        
        Segment segment = null;
        
//        Segment segment = getLineBuffer();

        SortedMap startMap = new TreeMap();
        SortedMap colorMap = new TreeMap();

        // Match all regexes on this snippet, store positions
        
        Set entrySet = patternColors.entrySet();
        Iterator iter = entrySet.iterator();
        while(iter.hasNext()) {
        	
        	Map.Entry entry = (Map.Entry) iter.next();
        	
        	Pattern pat = (Pattern) entry.getKey();
        	Color color = (Color) entry.getValue();

        	Matcher matcher = ((Pattern) entry.getKey()).matcher(text);
        	
        	while (matcher.find()) {
                startMap.put(new Integer(matcher.start(1)), new Integer(matcher.end()));
                colorMap.put(new Integer(matcher.start(1)), (Color) entry.getValue());
            }
        	
        }
        
        // TODO: check the map for overlapping parts
        
        int i = 0;
        
        Set startSet = startMap.entrySet();
        Iterator ite2 = startSet.iterator();
        while(ite2.hasNext()) {
        	
        	Map.Entry entry = (Map.Entry) ite2.next();
        	
        	int start = ((Integer) entry.getKey()).intValue();
        	int  end = ((Integer) entry.getValue()).intValue();
 

            if (i < start) {
                graphics.setColor(Color.black);
                
  //              doc.getText(p0 + i, start - i, segment);
                
                  segment = getText(p0 + i, start - i);
                
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

            graphics.setColor((Color) colorMap.get(new Integer(start)));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }

*/
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
/*
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
*/
	
/*	
	protected int drawSelectedText(Graphics graphics, int x, int y, int p0,
            int p1) throws BadLocationException {

        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);

        Segment segment = getLineBuffer();

        SortedMap startMap = new TreeMap();
        SortedMap colorMap = new TreeMap();

        // Match all regexes on this snippet, store positions
        
        Set entrySet = patternColors.entrySet();
        Iterator iter = entrySet.iterator();
        while(iter.hasNext()) {
        	
        	Map.Entry entry = (Map.Entry) iter.next();
        	
        	Pattern pat = (Pattern) entry.getKey();
        	Color color = (Color) entry.getValue();

        	Matcher matcher = ((Pattern) entry.getKey()).matcher(text);
        	
        	while (matcher.find()) {
                startMap.put(new Integer(matcher.start(1)), new Integer(matcher.end()));
                colorMap.put(new Integer(matcher.start(1)), (Color) entry.getValue());
            }
        	
        }
        
        // TODO: check the map for overlapping parts
        
        int i = 0;
        
        Set startSet = startMap.entrySet();
        Iterator ite2 = startSet.iterator();
        while(ite2.hasNext()) {
        	
        	Map.Entry entry = (Map.Entry) ite2.next();
        	
        	int start = ((Integer) entry.getKey()).intValue();
        	int  end = ((Integer) entry.getValue()).intValue();
 

            if (i < start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + i, start - i, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
            }

         //   graphics.setColor((Color) colorMap.get(new Integer(start)));
            i = end;
            doc.getText(p0 + start, i - start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, start);
        }

        // Paint possible remaining text black
        if (i < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + i, text.length() - i, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, i);
        }

        return x;
    }
*/
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
	
	public float nextTabStop(float f, int i) { 
		return 0f;
	}

public boolean   getScrollableTracksViewportWidth() {
   return true;
}

static boolean isComposedTextElement(Document doc, int offset) {
	Element elem = doc.getDefaultRootElement();
	while (!elem.isLeaf()) {
	    elem = elem.getElement(elem.getElementIndex(offset));
	}
	return isComposedTextElement(elem);
    }

    static boolean isComposedTextElement(Element elem) {
        AttributeSet as = elem.getAttributes();
	return isComposedTextAttributeDefined(as);
    }

    static boolean isComposedTextAttributeDefined(AttributeSet as) {
    	return ((as != null) && 
    	        (as.isDefined(StyleConstants.ComposedTextAttribute)));
        }

    /**
     * Paints the composed text in a GlyphView
     */
    static void paintComposedText(Graphics g, Rectangle alloc, GlyphView v) {
	if (g instanceof Graphics2D) {
	    Graphics2D g2d = (Graphics2D) g;
	    int p0 = v.getStartOffset();
	    int p1 = v.getEndOffset();
	    AttributeSet attrSet = v.getElement().getAttributes();
	    AttributedString as = 
		(AttributedString)attrSet.getAttribute(StyleConstants.ComposedTextAttribute);
	    int start = v.getElement().getStartOffset();
	    int y = alloc.y + (int) v.getGlyphPainter().getAscent(v);
	    int x = alloc.x;
	    
	    //Add text attributes
	    as.addAttribute(TextAttribute.FONT, v.getFont());
	    as.addAttribute(TextAttribute.FOREGROUND, v.getForeground());
	    if (StyleConstants.isBold(v.getAttributes())) {
		as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
	    }
	    if (StyleConstants.isItalic(v.getAttributes())) {
		as.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE);
	    }
	    if (v.isUnderline()) {
		as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
	    }
	    if (v.isStrikeThrough()) {
		as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
	    }
	    if (v.isSuperscript()) {
		as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER);
	    }
	    if (v.isSubscript()) {
		as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
	    }
	
	    // draw
	    AttributedCharacterIterator aci = as.getIterator(null, p0 - start, p1 - start);
	    TextLayout layout = new TextLayout(aci, g2d.getFontRenderContext());
	    layout.draw(g2d, x, y);
	}
    }

    /**
     * Paints the specified region of text in the specified color. 
     */
    final void paintRegionsUsingColor(Graphics g, Shape a, Color c, int p0, int p1) {
	// render the glyphs
    	
    	try {
    		
  //  		System.out.println(" LINE P0 = " + p0 + "    Line p1 = " + p1);
    		Rectangle r = a.getBounds();
  //  		System.out.println(" a.x = " + r.x + " a.y = " + r.y);
    		
			Document doc = getDocument();
			String text = doc.getText(p0, p1 - p0);
			
			Segment segment = null;

			SortedMap startMap = null;
			SortedMap colorMap = null;
			ArrayList offsetList = null;
			
//        Segment segment = getLineBuffer();

			
		//	if(!colorsMap.containsKey(p0 + "," + p1)) {
			
					startMap = new TreeMap();
					colorMap = new TreeMap();
	
				// Match all regexes on this snippet, store positions
				
				Set entrySet = patternColors.entrySet();
				Iterator iter = entrySet.iterator();
				while(iter.hasNext()) {
					
					Map.Entry entry = (Map.Entry) iter.next();
					
					Pattern pat = (Pattern) entry.getKey();
					Color color = (Color) entry.getValue();
	
					Matcher matcher = ((Pattern) entry.getKey()).matcher(text);
					
					while (matcher.find()) {
				        startMap.put(new Integer(matcher.start(1)), new Integer(matcher.end()));
				        colorMap.put(new Integer(matcher.start(1)), (Color) entry.getValue());
		
	//		        	offsetList = new ArrayList();
				
	//					offsetList.add(startMap);
	//					offsetList.add(colorMap);
						
	//					colorsMap.put(p0 + "," + p1, offsetList);
				    }
					
				}

		//	} else {
				
		//		ArrayList offList = (ArrayList) colorsMap.get(p0 + "," + p1);
				
		//		startMap = (SortedMap) offList.get(0);
		//		colorMap = (SortedMap) offList.get(1);
				
		//	}
			// TODO: check the map for overlapping parts
			int i = 0;
			Set startSet = startMap.entrySet();
			Iterator ite2 = startSet.iterator();
			while(ite2.hasNext()) {
				
				Map.Entry entry = (Map.Entry) ite2.next();
				
				int start = ((Integer) entry.getKey()).intValue();
				int  end = ((Integer) entry.getValue()).intValue();
 

			    if (i < start) {
			        g.setColor(Color.black);

	//	    		System.out.println(" start = " + start);
	//	    		System.out.println(" end = " + end);
	//	    		System.out.println(" i =  " + i);
	//	    		System.out.println(" p0 =  " + p0);

			        painter.paint(this, g, a, p0 + i, p0 + start);
			        
			    }

			    g.setColor((Color) colorMap.get(new Integer(start)));
			    i = end;
	//		    painter.paint(this, g, a, p0 + start, i - start);
	    	
	//    			System.out.println(" calling paint from PRUC ");
				
	    			int offset = p0 + i; 
	    			int len = doc.getLength();
	    			if(offset > len)
	    				offset = len;
	    			
			    painter.paint(this, g, a, p0 + start, offset);
			
			}

			// Paint possible remaining text black
			if (i < text.length()) {
			    g.setColor(Color.black);
	//		    painter.paint(this, g, a, p0 + i, text.length() - i);
	//    		System.out.println(" calling paint from PRUC ");
			    
			    painter.paint(this, g, a, p0 + i, p0 + text.length());
			   
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	// render underline or strikethrough if set.
	boolean underline = isUnderline();
	boolean strike = isStrikeThrough();
	if (underline || strike) {
	    // calculate x coordinates
	    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
	    View parent = getParent();
	    if ((parent != null) && (parent.getEndOffset() == p1)) {
		// strip whitespace on end
		Segment s = getText(p0, p1);
		while ((s.count > 0) && (Character.isWhitespace(s.array[s.count-1]))) {
		    p1 -= 1;
		    s.count -= 1;
		}
                SegmentCache.releaseSharedSegment(s);
	    }
	    int x0 = alloc.x;
	    int p = getStartOffset();
	    if (p != p0) {
		x0 += (int) painter.getSpan(this, p, p0, getTabExpander(), x0);
	    }
	    int x1 = x0 + (int) painter.getSpan(this, p0, p1, getTabExpander(), x0);

	    // calculate y coordinate
	    int d = (int) painter.getDescent(this);
	    int y = alloc.y + alloc.height - (int) painter.getDescent(this);
	    if (underline) {
		int yTmp = y;
		yTmp += 1;
		g.drawLine(x0, yTmp, x1, yTmp);
	    } 
	    if (strike) {
		int yTmp = y;
		// move y coordinate above baseline
		yTmp -= (int) (painter.getAscent(this) * 0.3f);
		g.drawLine(x0, yTmp, x1, yTmp);
	    }

	}
    }

    
    /**
     * Paints the specified region of text in the specified color. 
     */
    final void paintTextUsingColor(Graphics g, Shape a, Color c, int p0, int p1) {
	// render the glyphs
	g.setColor(c);
//	System.out.println(" calling paint from PTUC ");

	painter.paint(this, g, a, p0, p1);

	// render underline or strikethrough if set.
	boolean underline = isUnderline();
	boolean strike = isStrikeThrough();
	if (underline || strike) {
	    // calculate x coordinates
	    Rectangle alloc = (a instanceof Rectangle) ? (Rectangle)a : a.getBounds();
	    View parent = getParent();
	    if ((parent != null) && (parent.getEndOffset() == p1)) {
		// strip whitespace on end
		Segment s = getText(p0, p1);
		while ((s.count > 0) && (Character.isWhitespace(s.array[s.count-1]))) {
		    p1 -= 1;
		    s.count -= 1;
		}
                SegmentCache.releaseSharedSegment(s);
	    }
	    int x0 = alloc.x;
	    int p = getStartOffset();
	    if (p != p0) {
		x0 += (int) painter.getSpan(this, p, p0, getTabExpander(), x0);
	    }
	    int x1 = x0 + (int) painter.getSpan(this, p0, p1, getTabExpander(), x0);

	    // calculate y coordinate
	    int d = (int) painter.getDescent(this);
	    int y = alloc.y + alloc.height - (int) painter.getDescent(this);
	    if (underline) {
		int yTmp = y;
		yTmp += 1;
		g.drawLine(x0, yTmp, x1, yTmp);
	    } 
	    if (strike) {
		int yTmp = y;
		// move y coordinate above baseline
		yTmp -= (int) (painter.getAscent(this) * 0.3f);
		g.drawLine(x0, yTmp, x1, yTmp);
	    }

	}
    }

    /**
     * Check to see that a glyph painter exists.  If a painter
     * doesn't exist, a default glyph painter will be installed.  
     */
    protected void checkPainter() {
	if (painter == null) {
	    if (defaultPainter == null) {
		// the classname should probably come from a property file.
		String classname = "com.exalto.UI.painter.GlyphPainter1"; 
		try {
		  
	/*		
			Class c;
		    ClassLoader loader = getClass().getClassLoader();
		    if (loader != null) {
			c = loader.loadClass(classname);
		    } else {
		        c = Class.forName(classname);
		    }
	*/	    

		    SimpleClassLoader fsc = new SimpleClassLoader(this.getClass().getClassLoader()); 

		    Class cls = fsc.findClassByPath(classname);
		    
		    
		    Object o = cls.newInstance();
		    
		    if (o instanceof GlyphPainter) {
		    	defaultPainter = (GlyphPainter) o;
		    }
		    
		} catch (Throwable e) {
		    e.printStackTrace();

			throw new Error("GlyphView: Can't load glyph painter: " 
						  + classname);
		}
		
	    }

	    setGlyphPainter(defaultPainter.getPainter(this, getStartOffset(), 
						      getEndOffset()));
	    
	     this.painter = defaultPainter.getPainter(this, getStartOffset(), 
	    						      getEndOffset());
	}
    }

    /**
     * Lazily initializes the selections field
     */
    private void initSelections(int p0, int p1) {
        int viewPosCount = p1 - p0 + 1;
        if (selections == null || viewPosCount > selections.length) {
            selections = new byte[viewPosCount];
            return;
        }
        for (int i = 0; i < viewPosCount; selections[i++] = 0);
    }

    
    /**
     * Fetch a reference to the text that occupies
     * the given range.  This is normally used by
     * the GlyphPainter to determine what characters
     * it should render glyphs for.
     *
     * @param p0  the starting document offset >= 0
     * @param p1  the ending document offset >= p0
     * @return    the <code>Segment</code> containing the text
     */
     public Segment getText(int p0, int p1) {
         // When done with the returned Segment it should be released by
         // invoking:
         //    SegmentCache.releaseSharedSegment(segment);
         Segment text = SegmentCache.getSharedSegment();
         try {
             Document doc = getDocument();
             doc.getText(p0, p1 - p0, text);
             
         } catch (BadLocationException bl) {
             throw new Error("GlyphView: Stale view: " + bl);
         }
         return text;
     }

     
     
    
}
