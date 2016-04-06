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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import java.util.List;
import javax.xml.namespace.NamespaceContext;

/**
 * <p>Minimum implementation of NamespaceContext.</p>
 * 
 * <p>Assumes "<em>static</em>" namespace,
 * i.e. NamespaceContext is not updated during parsing.
 * Also assumes "<em>flat</em>" namespace,
 * i.e. only one prefix per Namespace URI.</p>
 */
public class NamespaceContextImpl1 implements NamespaceContext {
	
    private static final boolean DEBUG = false;
    
	private HashMap prefixToNamespaceURI = new HashMap();
	private HashMap namespaceURIToPrefix = new HashMap();

    HashMap namespaces;

    public NamespaceContextImpl1() {
    }

    public void setNamespaces(HashMap namespaces) {
        this.namespaces = namespaces;
    }

    public NamespaceContextImpl1(HashMap namespaces) {

        this.namespaces = namespaces;
    }


public String getNamespaceURI(String prefix) {
    /*
    if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("pre".equals(prefix)) return "http://www.example.com/books";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        return XMLConstants.NULL_NS_URI;
     *
     */

      		 if (prefix.equals("edx")) {
         		 return "http://www.edankert.com/examples/";
			 }
             else if(prefix.equals("pre")) {
                 return "http://www.example.com/books";
             }
              if (prefix.equals("ns1"))
                  return "http://www.davber.com/order-format";
                    else if (prefix.equals("ns2"))
                  return "http://www.davber.com/sales-format";


			 return null;
    }

    // This method isn't necessary for XPath processing.
    public String getPrefix(String namespaceURI) {
   //     throw new UnsupportedOperationException();

            if (namespaceURI.equals("http://www.edankert.com/examples/")) {
                return "edx";
            }
            else if(namespaceURI.equals("http://www.example.com/books/")) {
                return "pre";
            }

            return null;

    }

    // This method isn't necessary for XPath processing either.
    public Iterator getPrefixes(String namespaceURI) {
 //       throw new UnsupportedOperationException();

          List list = new ArrayList();

          if (namespaceURI.equals("http://www.edankert.com/examples/")) {
             list.add("edx");
          }
        if (namespaceURI.equals("http://www.example.com/books/")) {
             list.add("pre");
          }

 //         return list.iterator();
          return null;

    }


  
}

