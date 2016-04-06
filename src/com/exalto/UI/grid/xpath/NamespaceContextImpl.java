package com.exalto.UI.grid.xpath;

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

// $Id: NamespaceContextImpl.java,v 1.2 2007/07/19 04:36:17 ofung Exp $

import java.util.Iterator;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * <p>Minimum implementation of NamespaceContext.</p>
 * 
 * <p>Assumes "<em>static</em>" namespace,
 * i.e. NamespaceContext is not updated during parsing.
 * Also assumes "<em>flat</em>" namespace,
 * i.e. only one prefix per Namespace URI.</p>
 */
public class NamespaceContextImpl implements NamespaceContext {
	
    private static final boolean DEBUG = false;
    
	private HashMap prefixToNamespaceURI = new HashMap();
	private HashMap namespaceURIToPrefix = new HashMap();

    HashMap namespaces;

    public NamespaceContextImpl() {
    }

    public void setNamespaces(HashMap namespaces) {
        this.namespaces = namespaces;
    }

    public NamespaceContextImpl(HashMap namespaces) {

        this.namespaces = namespaces;
    }



    /**
     * <p>Bind a prefix to a Namespace URI.</p>
     * 
     * @param prefix Prefix to bind to <code>namespaceURI</code>.
     * @param namespaceURI Namespace URI to bind to <code>prefix</code>.
     */
	public void bindPrefixToNamespaceURI(String prefix, String namespaceURI) {
        
        if (DEBUG) {
            System.err.println(
                    "bindPrefixToNamespaceURI("
                    + prefix + ", "
                    + namespaceURI + ")");
        }
        
		prefixToNamespaceURI.put(prefix, namespaceURI);
		namespaceURIToPrefix.put(namespaceURI, prefix);
	}
	
    /**
     * <p>Get the Namespace URI bound to the prefix.</p>
     * 
     * @param prefix Lookup Namespace URI that is bound to this prefix.
     * 
     * @return Namespace URI bound to <code>prefix</code>.
     */
	public String getNamespaceURI(String prefix) {
		
        if (DEBUG) {
            System.err.println(
                    "getNamespaceURI("
                    + prefix + ")");
        }
        
        if (prefix == null) {
			throw new IllegalArgumentException("NamespaceContextImpl#getNamespaceURI(String prefix) with prefix == null");
		}

        // constants
        if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        }
        if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }

        // default
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            if (prefixToNamespaceURI.containsKey(prefix)) {
                return (String)prefixToNamespaceURI.get(prefix);
            } else {
            	return XMLConstants.NULL_NS_URI;
            }
        }
        
        // bound
		if (prefixToNamespaceURI.containsKey(prefix)) {
			return (String)prefixToNamespaceURI.get(prefix);
		}
		
        // unbound
		return XMLConstants.NULL_NS_URI;
	}
	
    /**
     * <p>Get the prefix bound to the Namespace URI.</p>
     * 
     * @param namespaceURI Lookup prefix that is bound to this Namespace URI.
     * 
     * @return prefix bound to this Namespace URI.
     */
	public String getPrefix(String namespaceURI) {
		
        if (DEBUG) {
            System.err.println(
                    "getPrefix("
                    + namespaceURI + ")");
        }
        
        if (namespaceURI == null) {
			throw new IllegalArgumentException("NamespaceContextImpl#getPrefix(String namespaceURI) with namespaceURI == null");
		}
        
        // constants
		if (namespaceURI.equals(XMLConstants.XML_NS_URI)) {
			return XMLConstants.XML_NS_PREFIX;
		}
		if (namespaceURI.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
			return XMLConstants.XMLNS_ATTRIBUTE;
		}
		
        // bound
        if (namespaceURIToPrefix.containsKey(namespaceURI)) {
            return (String)namespaceURIToPrefix.get(namespaceURI);
        }

        // mimic "default Namespace URI"
        if (namespaceURI.equals(XMLConstants.NULL_NS_URI)) {
			return XMLConstants.DEFAULT_NS_PREFIX;
		}
		
		// unbound
		return null;
	}
	
    /**
     * <p>Get prefixes bould to Namespace URI.</p>
     * 
     * <p>TODO: not implemented.
     * Throws UnsupportedOperationException.</p>
     * 
     * @param namespaceURI Lookup prefixes bound to this Namespace URI
     * 
     * @return Iterator over prefixes bound to this Namespace URI.
     * 
     * @throws UnsupportedOperationException When called.
     */
	public Iterator getPrefixes(String namespaceURI) {
		
        if (DEBUG) {
            System.err.println(
                    "getPrefixes("
                    + namespaceURI + ")");
        }
        
        throw new UnsupportedOperationException(
                    "NamespaceContextImpl#getPrefixes(String namespaceURI)"
                    + " not implemented");
	}
}

