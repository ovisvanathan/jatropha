/*
 * $Id: XmlScanner.java,v 1.6 2003/10/01 17:38:54 edankert Exp $
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

import java.io.IOException;

import javax.swing.text.Document;

/**
 * Associates XML input stream characters with styles specific 
 * for the XML Editor.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.6 $, $Date: 2003/10/01 17:38:54 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public class XmlScanner {
	private Scanner tagScanner = null;

	private final AttributeScanner ATTRIBUTE_SCANNER = new AttributeScanner();
	private final ElementEndTagScanner ELEMENT_END_TAG_SCANNER = new ElementEndTagScanner();
	private final ElementStartTagScanner ELEMENT_START_TAG_SCANNER = new ElementStartTagScanner();
	private final ElementNameScanner ELEMENT_NAME_SCANNER = new ElementNameScanner();
	private final EntityTagScanner ENTITY_TAG_SCANNER = new EntityTagScanner();
	private final CommentScanner COMMENT_SCANNER = new CommentScanner();
	private final TagScanner TAG_SCANNER = new TagScanner();
	
    private int start = 0;

    protected XmlInputReader in	= null;

    public int token = -1;
    public long pos = 0;

	/**
	 * Constructs a scanner for the Document.
	 *
	 * @param document the document containing the XML content.
	 */
    public XmlScanner( Document document) throws IOException {
	    try {
	        in = new XmlInputReader( new XmlInputStream( document));
	    } catch( Exception exception) {
	    	exception.printStackTrace();
	    }

	    in.read();
	    scan();
    }

	/**
	 * Sets the scanning range.
	 *
	 * @param start the start of the range.
	 * @param end the end of the range.
	 */
	public void setRange( int start, int end) throws IOException {
		in.setRange( start, end);

	    this.start = start;

	    token = -1;
	    pos = 0;
	    tagScanner = null;

	    in.read();
	    scan();
	}

    /**
     * Gets the starting location of the current token in the 
	 * document.
     *
     * @return the starting location.
     */
    public final int getStartOffset() {
	    int begOffs = (int) (pos & Constants.MAXFILESIZE);
	    return start + begOffs;
	}

    /**
     * Gets the end location of the current token in the 
     * document.
     *
     * @return the end location.
     */
    public final int getEndOffset() {
	    int endOffs = (int) (in.pos & Constants.MAXFILESIZE);
	    return start + endOffs;
	}

    /**
     * Scans the Xml Stream for XML specific tokens.
     *
     * @return the last location.
     */
    public long scan() throws IOException {
        long l = pos;
		
		if ( tagScanner != null) {
			token = tagScanner.scan( in);

			if ( tagScanner.isFinished()) {
				tagScanner = null;
			}
			
			return l;
		} else {

	        while ( true) {
			    pos = in.pos;
				int ch = in.getLastChar();
	          
			    switch(ch) {
	            case -1: 
	                token = -1;
	                return l;

	            case 60: // '<'
					ch = in.read();

					tagScanner = TAG_SCANNER;
	                tagScanner.reset();
					
	                token = tagScanner.scan( in);
	                return l;

	            default:
                    scanValue();
                    token = Constants.ELEMENT_VALUE;
                    return l;
	            }
	        }
		}
    }
	
	// Scans a XML element value.
	private void scanValue() throws IOException {
		int ch = in.read();

		do {
		    switch( ch) {
		        case -1: 
		            // eof
		            return;

		        case 60: // '<'
					return;

		        default:
		            ch = in.read();
		            break;

		    }
		} while( true);
	}

	// Returns when a non whitespace character has been detected.
	private void skipWhitespace() throws IOException {
		int ch = in.read();
//		int ch = in.getLastChar();
	
	    while ( true) {
			if ( Character.isWhitespace( (char)ch)) {
				ch = in.read();
			} else {
				return;
	        }
	    }
	}

	// Scans a String.
	private void scanString( int end) throws IOException {
	    int ch = in.read();

	    while ( ch != end && ch != '>' && ch != -1) {
            ch = in.read();
	    }
	}

	/**
	 * A scanner for anything starting with a '<'.
	 */
	private class TagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {

			if ( scanner != null) {
				int token = scanner.scan( in);

				if ( scanner.isFinished()) {
					scanner = null;
				}
				
				return token;
			} else {
				int character = in.getLastChar();

				if ( character == 33) { // '!'
					character = in.read();
					if ( character == 45) { // '-'
						character = in.read();
						if ( character == 45) { // '-'
							scanner = COMMENT_SCANNER;
						}
							
					}

					if ( scanner == null) {
						scanner = ENTITY_TAG_SCANNER;
					}
					
					scanner.reset();
					return Constants.SPECIAL;

				} else if ( character == 63) { // '?'
					character = in.read();
					scanner = ENTITY_TAG_SCANNER;
					scanner.reset();

				    return Constants.SPECIAL;

				} else if ( character == 47) { // '/'
					character = in.read();
					scanner = ELEMENT_END_TAG_SCANNER;
					scanner.reset();
					
				    return Constants.SPECIAL;

				} else if ( character == 62) { // '>'
					character = in.read();
				    finished();
				    return Constants.SPECIAL;

				} else  {
					scanner = ELEMENT_START_TAG_SCANNER;
					scanner.reset();

					return Constants.SPECIAL;
				}
			}
		}

		public void reset() {
			super.reset();
			scanner = null;
		}
	}
	
	/**
	 * Scans a entity '<!'.
	 */
	private class EntityTagScanner extends Scanner {

		public int scan( XmlInputReader in) throws IOException {
			int character = in.read();

			while ( true) {
			    switch( character) {
			        case -1: 
//				        System.err.println("Error ["+pos+"]: eof in entity!");
			            finished();
			            return Constants.ENTITY;
	
			        case 62: // '>'
						finished();
						return Constants.ENTITY;
	
			        default:
			            character = in.read();
			            break;
	
			    }
			}
		}

		public void reset() {
			super.reset();
		}
	}

	/**
	 * Scans a comment entity '<!--'.
	 */
	private class CommentScanner extends Scanner {
		public int scan( XmlInputReader in) throws IOException {
			int character = in.read();

			while ( true) {
//				System.out.print((char)character);
			
			    switch( character) {
			        case -1: // EOF
			            finished();
			            return Constants.COMMENT;
	
			        case 45: // '-'
						character = in.read();
						if ( character == 45) { // '-'
							character = in.read();
							if ( character == 62) { // '>'
								finished();
								return Constants.COMMENT;
							}
						}
						break;
	
			        default:
			            character = in.read();
			            break;
	
			    }
			}
		}

		public void reset() {
			super.reset();
		}
	}

	/**
	 * Scans an element end tag '</xxx:xxxx>'.
	 */
	private class ElementEndTagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {
//			System.out.println( "ElementStartTagScanner.scan()");
			if ( scanner == null) {
				scanner = ELEMENT_NAME_SCANNER;
				scanner.reset();
			}

			int token = scanner.scan( in);

			if ( scanner.isFinished()) {
				finished();
			}
			
			return token;
		}

		public void reset() {
			super.reset();
			scanner = null;
		}
	}

	/**
	 * Scans an element start tag '<xxx:xxxx yyy:yyyy="yyyyy" xmlns:hsshhs="sffsfsf">'.
	 */
	private class ElementStartTagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {
			int token = 0;
			
			if ( scanner == null) {
				scanner = ELEMENT_NAME_SCANNER;
				scanner.reset();

				token = scanner.scan( in);
			} else {
				token = scanner.scan( in);
			}
			
			if ( scanner.isFinished()) {
				if ( scanner instanceof ElementNameScanner) {
					scanner = ATTRIBUTE_SCANNER;
					scanner.reset();
				} else {
					finished();
				}
			}

			return token;
		}
		
		public void reset() {
			super.reset();
			scanner = null;
		}
	}

	/**
	 * Scans an element name '<xxx:xxxx'.
	 */
	private class ElementNameScanner extends Scanner {
		private boolean hasPrefix = false;
		private boolean emptyElement = false;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
//			            System.err.println("Error ["+pos+"]: eof in element name!");
			            finished();
			            return Constants.ELEMENT_NAME;
	
			        case 58: // ':'
			        	if ( hasPrefix) {
			        		character = in.read();
			        		return Constants.SPECIAL;
			        	} else {
			        		hasPrefix = true;
			        		return Constants.ELEMENT_PREFIX;
			        	}

			        case 47: // '/'
						if ( emptyElement) {
				            character = in.read();
						} else {
			            	emptyElement = true;
			            	return Constants.ELEMENT_NAME;
						}

			        case 62: // '>'
				        finished();

						if ( emptyElement) {
							return Constants.SPECIAL;
						} else {
					        return Constants.ELEMENT_NAME;
						}

			        case 32: // ' '
			        case 10: // '\r'
			        case 13: // '\n'
						skipWhitespace();
						finished();
				        return Constants.ELEMENT_NAME;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			emptyElement = false;
			hasPrefix = false;
		}
	}

	/**
	 * Scans an elements attribute 'xxx:xxxx="hhhh"' or 'xmlns:xxxx="hhhh"'.
	 */
	private class AttributeScanner extends Scanner {
		private final int NAME = 0;
		private final int VALUE = 1;
		private final int END = 2;

		private int mode = NAME;
		
		private boolean hasPrefix = false;
		private boolean firstTime = true;
		private boolean isNamespace = false;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
			
//			System.out.println("AttributeScanner.scan() ["+(char)character+"]");
		
			do {
				if ( mode == NAME) {
//					System.out.println("NAME ["+(char)character+"] "+firstTime);
				
				    switch( character) {
				        case -1: 
//				            System.err.println("Error ["+pos+"]: eof in attribute!");
				            finished();
				            return Constants.ATTRIBUTE_NAME;
		
				        case 120: // 'x'
				        	if ( firstTime) { // Still before a prefix has been established
								character = in.read();
								if ( character == 109) { // 'm'
									character = in.read();

									if ( character == 108) { // 'l'
										character = in.read();

										if ( character == 110) { // 'n'

											character = in.read();
											if ( character == 115) { // 's'
												skipWhitespace();
												character = in.getLastChar();
												
												if ( character == 58 || character == 61) { // ':' '='
													isNamespace = true;
												}
											}
										}
									}
								}
				        	} else {
					        	character = in.read();
				        	}
							break;

				        case 58: // ':'
				        	if ( hasPrefix) {
				        		character = in.read();
				        		return Constants.SPECIAL;
				        	} else if ( isNamespace) {
					        	hasPrefix = true;
					        	return Constants.NAMESPACE_NAME;
				        	} else {
				        		hasPrefix = true;
				        		return Constants.ATTRIBUTE_PREFIX;
				        	}

				        case 62: // '>'
//					        character = in.read();
					        finished();
					        return Constants.SPECIAL;

				        case 61: // '='
							mode = VALUE;

							if ( isNamespace && hasPrefix) {
								return Constants.NAMESPACE_PREFIX;
							} else if ( isNamespace) {
								return Constants.NAMESPACE_NAME;
							} else {
								return Constants.ATTRIBUTE_NAME;
							}

				        default:
				            character = in.read();
				            break;
				    }

					firstTime = false;
				} else if ( mode == VALUE) {

//					System.out.println("VALUE ["+(char)character+"]");

					switch( character) {
					    case -1: 
//					        System.err.println("Error ["+pos+"]: eof in attribute value!");
					        return -1;
		
					    case 61: // '='
						    character = in.read();
						    return Constants.SPECIAL;

					    case 39: // '''
					    case 34: // '"'
							scanString( character);
							skipWhitespace();
														
							if ( isNamespace) {
								reset();
								return Constants.NAMESPACE_VALUE;
							} else {
								reset();
								return Constants.ATTRIBUTE_VALUE;
							}

					    case 62: // '>'
					        character = in.read();
					        finished();
					        return Constants.SPECIAL;

					    default:
					        character = in.read();
					        break;
					}

				} 
			} while( true);
		}
		
		public void reset() {
			super.reset();
			mode = NAME;
			
			hasPrefix = false;
			firstTime = true;
			isNamespace = false;
		}
	}

	/**
	 * Abstract scanner class..
	 */
	abstract class Scanner {
		protected int token = -1;
		private boolean finished = false;
		
		public abstract int scan( XmlInputReader in) throws IOException;

		/**
		 * The scanner has finished scanning the information, 
		 * only a reset can change this.
		 */
		protected void finished() {
			finished = true;
		}

		/**
		 * returns whether this scanner has finished scanning all
		 * it was supposed to scan.
		 *
		 * @return true when the scanner is finished.
		 */
		public boolean isFinished() {
			return finished;
		}

		/**
		 * Resets all the variables to the start value.
		 */
		public void reset() {
			finished = false;
			token = -1;
		}

		/**
		 * returns the token value for the currently scanned text.
		 *
		 * @return the token value.
		 */
		public int getToken() {
			return token;
		}
	}
}
