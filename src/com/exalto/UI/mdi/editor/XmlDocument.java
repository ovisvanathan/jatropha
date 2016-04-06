/*
 * $Id: XmlDocument.java,v 1.5 2002/11/29 12:44:32 edankert Exp $
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.exalto.UI.mdi.Editor;
import java.util.HashMap;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoableEdit;

/**
 * The XML Document is responsible for handling the user insertions and 
 * deletions, for changing the tab characters to spaces and to automatically 
 * indent the text correctly.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.5 $, $Date: 2002/11/29 12:44:32 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public class XmlDocument extends DefaultStyledDocument {
	private Editor editor = null;
	
	private boolean loading = false;

    private static final Object RESPONSE_TYPE = new String("Response");
    List specList = new ArrayList(100);
    SimpleAttributeSet attrs = new SimpleAttributeSet();
    boolean isFirst = true;
    private static final char[] NEWLINE_TEXT = new char[] { '\n' };
    boolean isFirstTag = true;
    
    /**
     * Batched ElementSpecs
     */
    private ArrayList batch = null;
    StringBuffer textBuffer = new StringBuffer();

    /**
     * EOL tag that we re-use when creating ElementSpecs
     */
    private static final char[] EOL_ARRAY = { '\n' };

    boolean endTag = false;

    HashMap seenHash = new HashMap();
    
    /**
	 * Constructs the XML Document with a new GapContent buffer.
	 */
    public XmlDocument(Editor editor) {
	//	super( new GapContent( 1024));
		super();	
		this.editor = editor;
		
	    batch = new ArrayList();

    }
	
	/**
	 * Lets the document know that it is being loaded, don't attempt to 
	 * do any text conversion.
	 */
	public void setLoading( boolean loading) {
		this.loading = loading;
	}

    /**
     * Gets the location where the last significant tag ended, this
	 * location can be used as a start for the scanner. 
	 * If the current element is a comment, the last significant tag 
	 * is before the begin of the comment. 
	 *
	 * @param p the preferred start position.
	 *
	 * @return the position where the last significant tag ended or 0. 
     */
    public int getTagEnd( int p) {
		int elementEnd = 0;

		if ( p > 0) {
			try {
				int index = 0;
				
				String s = getText( 0, p);
				int commentStart = s.lastIndexOf( "<!--");
				int commentEnd = s.lastIndexOf( "-->");
				
				if ( commentStart > 0 && commentStart > commentEnd) {
					index = s.lastIndexOf( ">", commentStart);
				} else {
					index = s.lastIndexOf( ">");
				}

			    if ( index != -1) {
					elementEnd = index;
		    	}
			} catch ( BadLocationException bl) {}
		}

		return elementEnd;
    }

	/**
	 * Inserts some content into the document.
	 * When the content is a tab character, the character will be replaced 
	 * by spaces. When the content is a new line character, an indentation will
	 * be added to the content.
	 */ 
	public void insertString( int off, String str, AttributeSet set) throws BadLocationException {
	//	System.out.println("[1]insertString( "+str+")");

	//	System.out.println("is editor loading = " +  getProperty());

		if ( !editor.isLoading() && str.equals( ">")) {
			
			if (editor.isTagCompletion()) {
				int caretPosition = editor.getCaretPosition();
				
		//		System.out.println(" caret pos initial = " +  caretPosition); 
		//		System.out.println(" str = " +  str); 

				StringBuffer endTag = new StringBuffer( str);

				String text = getText( 0, off);
				int startTag = text.lastIndexOf( '<', off);
				int prefEndTag = text.lastIndexOf( '>', off);

				// If there was a start tag and if the start tag is not empty and 
				// if the start-tag has not got an end-tag already.
				if ( (startTag > 0) && (startTag > prefEndTag) && (startTag < text.length()-1) ) {
					String tag = text.substring( startTag, text.length());
					char first = tag.charAt( 1);
					
			//		System.out.println(" in first if "); 

					if ( first != '/' && first != '!' && first != '?' && !Character.isWhitespace( first)) {
						boolean finished = false;
						char previous = tag.charAt( tag.length() - 1);

			//		System.out.println(" in 2nd if "); 
						
						if ( previous != '/' && previous != '-') {

			//		System.out.println(" in 3rd if "); 

							endTag.append( "</");
							
							for ( int i = 1; (i < tag.length()) && !finished; i++) {
								char ch = tag.charAt( i);
								
								if ( !Character.isWhitespace( ch)) {
									endTag.append( ch);
								} else {
									finished = true;
								}
							}

							endTag.append( ">");
						}
					}
				}
				
				str = endTag.toString();

		//		System.out.println(" caret pos = " +  caretPosition); 
		//		System.out.println(" off = " + off); 
		//		System.out.println(" str = " + str); 

			
				super.insertString( off, str, set);
				
				editor.setCaretPosition( caretPosition+1);
			} else {

				super.insertString( off, str, set);
			}
		} else if ( !loading && str.equals( "\n")) {
		
			StringBuffer newStr = new StringBuffer( str);
			Element elem = getDefaultRootElement().getElement( getDefaultRootElement().getElementIndex( off));
			int start = elem.getStartOffset();
			int end = elem.getEndOffset();
		    String line = getText( start, off - start);
			
			boolean finished = false;
			
			for ( int i = 0; (i < line.length()) && !finished; i++) {
				char ch = line.charAt( i);
				
				if ( ((ch != '\n') && (ch != '\f') && (ch != '\r')) && Character.isWhitespace( ch)) {
					newStr.append( ch);
				} else {
					finished = true;
				}
			}
			
			if ( isStartElement( line)) {
				newStr.append( getTabString());
			}
			
			str = newStr.toString();

			
			//specList.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));

			super.insertString( off, str, set);

		//    batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
		   /* 
//		  Then add attributes for element start/end tags. Ideally
	        // we'd get the attributes for the current position, but we
	        // don't know what those are yet if we have unprocessed
	        // batch inserts. Alternatives would be to get the last
	        // paragraph element (instead of the first), or to process
	        // any batch changes when a linefeed is inserted.
	        Element paragraph = getParagraphElement(0);
	        AttributeSet pattr = paragraph.getAttributes();
	        batch.add(new ElementSpec(null, ElementSpec.EndTagType));
	        batch.add(new ElementSpec(pattr, ElementSpec.StartTagType));

			*/
		} else {
			
			
		//	parseInputTextAsXml(str);
		//			System.out.println(" xml str = " +  str);
			
		    attrs.addAttribute(AbstractDocument.ElementNameAttribute, RESPONSE_TYPE);
			
	//		BufferedReader bufRead = new BufferedReader(new StringReader(str));
	//		String s = null;
			
/*
			try {
				
            
					if(str.contains("<inventory")) {
		//				if(!seenHash.containsKey("<inventory")) {
                            specList.add(new ElementSpec(attrs, ElementSpec.StartTagType));
        //                    seenHash.put("<inventory", 1);
        //                }
                    }

				//		batch.add(new ElementSpec(attrs, ElementSpec.StartTagType));


  					
					if(str.contains("<person>")) {
						
						specList.add(new ElementSpec(attrs, ElementSpec.StartTagType));
						
					}
					if(str.contains("</person>")) {
					    specList.add(new ElementSpec(null, ElementSpec.EndTagType));
					}
					
				  //  specList.add(new ElementSpec(null, ElementSpec.StartTagType));
				  //  specList.add(new ElementSpec(null, ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
				  //  specList.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
				  //  specList.add(new ElementSpec(null, ElementSpec.EndTagType));
				    
					batch.add(new ElementSpec(null, ElementSpec.StartTagType));
				    batch.add(new ElementSpec(null, ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
				    batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
				    batch.add(new ElementSpec(null, ElementSpec.EndTagType));
					
		
				    
                    else if(str.contains("</inventory>")) {
		//				if(!seenHash.containsKey("</inventory>")) {
                            specList.add(new ElementSpec(null, ElementSpec.EndTagType));
        //                    seenHash.put("<inventory", 1);
        //                }

                        
		//			    batch.add(new ElementSpec(attrs, ElementSpec.EndTagType));
					}

                //    specList.add(new ElementSpec(null, ElementSpec.StartTagType));
				    specList.add(new ElementSpec(null, ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
				    specList.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
				//    specList.add(new ElementSpec(null, ElementSpec.EndTagType));

					
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            */
				
			/*
            try {
         //       ElementSpec es[] = new ElementSpec[specList.size()];

            
                ElementSpec [] revList = (ElementSpec []) specList.toArray(new ElementSpec[specList.size()]);
                for(int i=0;i<revList.length;i++) {
                    ElementSpec e =  (ElementSpec) revList[i];
                    System.out.println(" espec tostr = " +  e.toString());
                    char [] c = e.getArray();
                    if(c != null && c.length>0)
                        for(int j=0;j<c.length;j++)
                          System.out.println(c[j]);
                    System.out.println(" espec type = " +  e.getType());
                }
                
                insert(0, (ElementSpec []) specList.toArray(new ElementSpec[specList.size()]));
                specList = new ArrayList();

            } catch (BadLocationException ex) {
                System.err.println("bad insert!");
                throw ex;
            }
 
            */
					super.insertString( off, str, set);
		}
	}
	

	private void parseInputTextAsXml(String str) {
		
			
			try {
				
			//	foldDocHandler = new foldDocHandler();
				
				String parserFac = System.getProperty("javax.xml.parsers.SAXParserFactory");

				if(parserFac == null) {
					System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
				}

				SAXParserFactory spf = SAXParserFactory.newInstance();
				
				// Set namespaceAware to true to get a parser that corresponds to
				// the default SAX2 namespace feature setting.  This is necessary
				// because the default value from JAXP 1.0 was defined to be false.
				spf.setNamespaceAware(true);
				// Validation part 1: set whether validation is on
				spf.setValidating(false);

				// Create a JAXP SAXParser
				SAXParser saxParser = spf.newSAXParser();
			
			//	saxParser.parse(new InputSource(new StringReader(str)), foldDocHandler);
			
			
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

	        

		
	}

	
	private String getTabString() {
		byte[] tab = new byte[editor.getSpaces()];
		
		for ( int i = 0; i < tab.length; i++) {
			tab[i] = ' ';
		}

		return new String( tab);
	}

	// Tries to find out if the line finishes with an element start
	private boolean isStartElement( String line) {
		boolean result = false;
		
		int first = line.lastIndexOf( "<");
		int last = line.lastIndexOf( ">");
		
		if ( last < first) { // In the Tag
			result = true;
		} else {
			int firstEnd = line.lastIndexOf( "</");
			int lastEnd = line.lastIndexOf( "/>");

			// Last Tag is not an End Tag
			if ( (firstEnd != first) && ((lastEnd + 1) != last)) {
				result = true;
			}
		}
		
		return result;
	}
	
	/**
     * Adds a String (assumed to not contain linefeeds) for
     * later batch insertion.
     */
    public void appendBatchString(int pos, String str,
        AttributeSet a) {
        // We could synchronize this if multiple threads
        // would be in here. Since we're trying to boost speed,
        // we'll leave it off for now.

        // Make a copy of the attributes, since we will hang onto
        // them indefinitely and the caller might change them
        // before they are processed.
     //   a = a.copyAttributes();
        char[] chars = str.toCharArray();
        
        	textBuffer.append(str);

     //   	if(str.replace('\n', ' ').trim().length() != 0) {
            	batch.add(new ElementSpec(null, ElementSpec.StartTagType));
        		batch.add(new ElementSpec(null, ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
        		batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
        		batch.add(new ElementSpec(null, ElementSpec.EndTagType));
    	    
      //  	} else
        //		batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
        	     
    }
    
    /**
     * Adds a linefeed for later batch processing
     */
    public void appendBatchLineFeed(int pos, String str, AttributeSet a) {
        // See sync notes above. In the interest of speed, this
        // isn't synchronized.

        // Add a spec with the linefeed characters
    	
    	
 //       String str = textBuffer.toString(); 

  //      batch.add(new ElementSpec(null, ElementSpec.StartTagType));
	//    batch.add(new ElementSpec(null, ElementSpec.ContentType, str.toCharArray(), 0, str.length()));
	    batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
	//    batch.add(new ElementSpec(null, ElementSpec.EndTagType));

	    textBuffer = new StringBuffer();
        
	        
            // Then add attributes for element start/end tags. Ideally
        // we'd get the attributes for the current position, but we
        // don't know what those are yet if we have unprocessed
        // batch inserts. Alternatives would be to get the last
        // paragraph element (instead of the first), or to process
        // any batch changes when a linefeed is inserted.
      //  Element paragraph = getParagraphElement(0);
      //  AttributeSet pattr = paragraph.getAttributes();
      //  batch.add(new ElementSpec(null, ElementSpec.EndTagType));
      //  batch.add(new ElementSpec(pattr, ElementSpec.StartTagType));
    }

    
    public void processBatchUpdates(int offs) throws
    BadLocationException {
    	
    	/*
    	String instr = textBuffer.toString();

    	String [] lines = instr.split("\n");
    	
    	ArrayList rlines = new ArrayList();
   // 	instr.replace("\n", ",#");
    	
    	System.out.println(" lines len = " + lines.length);

    	int k=0;
    	for(int i=0;i<lines.length;i++) {
    	 	 String line = lines[i];
 	         if(line.replace('\n', ' ').trim().length() != 0) {
 	        	 rlines.add(line);
 	         }
    	} 
    	
    	for(int j=0;j<rlines.size();j++) {
    		String rline = (String) rlines.get(j);
    		batch.add(new ElementSpec(null, ElementSpec.StartTagType));
 	        batch.add(new ElementSpec(null, ElementSpec.ContentType, rline.toCharArray(), 0, rline.length()));
 	        batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
		    batch.add(new ElementSpec(null, ElementSpec.EndTagType));
    	}
    	
    	
    	System.out.println(" str nl = " + instr.contains("\n"));
    	
    	 StringTokenizer stok = new StringTokenizer(instr, ",");
    	 while(stok.hasMoreTokens()) {
	    		 String tok = stok.nextToken();
	    		 
	        	 StringTokenizer stok2 = new StringTokenizer(tok, ",#");
	        	 while(stok2.hasMoreTokens()) {
	    	    		 String tok2 = stok2.nextToken();
	    		 
	 	 	         batch.add(new ElementSpec(null, ElementSpec.StartTagType));
				     batch.add(new ElementSpec(null, ElementSpec.ContentType, tok.toCharArray(), 0, tok.length()));
			    	 batch.add(new ElementSpec(null, ElementSpec.ContentType, NEWLINE_TEXT, 0, 1));
				     batch.add(new ElementSpec(null, ElementSpec.EndTagType));
	   	 }
	    	
    */
    	
    	
    // As with insertBatchString, this could be synchronized if
    // there was a chance multiple threads would be in here.
    	ElementSpec[] inserts = new ElementSpec[batch.size()];
    	batch.toArray(inserts);

    	
 //   	for(int i=0;i<batch.size();i++) {
  //  		ElementSpec e = (ElementSpec) batch.get(i);
   // 		System.out.println(" espec  = " + e);
   // 	}
    		
    	
    	// Process all of the inserts in bulk
    	super.insert(offs, inserts);
    //	 insert(offs, inserts);
    }

    
protected void insert(int offset, ElementSpec[] data) throws BadLocationException {
	if (data == null || data.length == 0) {
	    return;
	}

	try {
	    writeLock();

	    // install the content
	    Content c = getContent();
	    int n = data.length;
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < n; i++) {
		ElementSpec es = data[i];
		if (es.getLength() > 0) {
		    sb.append(es.getArray(), es.getOffset(),  es.getLength());
		}
	    }
	    if (sb.length() == 0) {
		// Nothing to insert, bail.
		return;
	    }
	    UndoableEdit cEdit = c.insertString(offset, sb.toString());

	    // create event and build the element structure
	    int length = sb.length();
	    DefaultDocumentEvent evnt = 
		new DefaultDocumentEvent(offset, length, DocumentEvent.EventType.INSERT);
	    evnt.addEdit(cEdit);
	    buffer.insert(offset, length, data, evnt);

	    // update bidi (possibly)
	    super.insertUpdate(evnt, null);

	    // notify the listeners
	    evnt.end();
	    fireInsertUpdate(evnt);
	    fireUndoableEditUpdate(new UndoableEditEvent(this, evnt));
	} finally {
	    writeUnlock();
	}
    }
    
	
}
