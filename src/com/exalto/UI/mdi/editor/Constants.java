/*
 * $Id: Constants.java,v 1.1 2002/07/18 17:19:52 edankert Exp $
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

/**
 * The contants used for the XML editor.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.1 $, $Date: 2002/07/18 17:19:52 $
 * @author Edwin Dankert <edankert@cladonia.com>
 */
public interface Constants {

    public static final long MAXFILESIZE		= 0xffffffffL;
    public static final long MAXLINENUMBER		= 0xffffffffL;

    public static final int ELEMENT_NAME 		= 1;
    public static final int ELEMENT_PREFIX 		= 2;
    public static final int ELEMENT_VALUE 		= 3;

    public static final int ATTRIBUTE_NAME 		= 5;
    public static final int ATTRIBUTE_PREFIX 	= 6;
    public static final int ATTRIBUTE_VALUE 	= 7;

    public static final int NAMESPACE_NAME 		= 10;
    public static final int NAMESPACE_PREFIX 	= 11;
    public static final int NAMESPACE_VALUE 	= 12;

    public static final int ENTITY		 		= 15;
    public static final int COMMENT 			= 16;
    public static final int DECLARATION 		= 17;

    public static final int SPECIAL				= 20;
    public static final int STRING				= 21;

	public static final String[] TEXT_VALUES 	= {
		null, "element-name", "element-prefix", "element-value", 
		null, "attribute-name", "attribute-prefix", "attribute-value", null,
		null, "namespace-name", "namespace-prefix", "namespace-value", null,
		null, "entity", "comment", "declaration", null,
		null, "special", "string" };
		
	public static final int MAX_TOKENS	= TEXT_VALUES.length;
}
