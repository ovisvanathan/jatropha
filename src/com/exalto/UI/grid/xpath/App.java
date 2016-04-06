/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.UI.grid.xpath;

/**
 *
 * @author omprakash.v
 */

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.w3c.dom.Document;

public class App
{
    public static void main( String[] args )
    {
        try {
            // First, the XML document

            String xmlStr =
                "<?xml version=\"1.0\" ?>\n" +
                "<Sales xmlns=\"http://www.davber.com/sales-format\">\n" +
                "<Customer name=\"CostCo, Inc.\">\n" +
                "<ord:Order xmlns:ord=\"http://www.davber.com/order-format\" price=\"12000\">\n" +
                "<ord:Description>A bunch of stuff" +
                "</ord:Description>\n" +
                "</ord:Order>\n" +
                "</Customer>\n" +
                "</Sales>\n";

            DocumentBuilderFactory xmlFact =
                DocumentBuilderFactory.newInstance();
            xmlFact.setNamespaceAware(true);
            DocumentBuilder builder = xmlFact.
                newDocumentBuilder();
            Document doc = builder.parse(
                    new java.io.ByteArrayInputStream(
                            xmlStr.getBytes()));

            // We map the prefixes to URIs

            NamespaceContext ctx = new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    String uri;
                    if (prefix.equals("ns1"))
                        uri = "http://www.davber.com/order-format";
                    else if (prefix.equals("ns2"))
                        uri = "http://www.davber.com/sales-format";
                    else
                        uri = null;
                    return uri;
                }

                // Dummy implementation - not used!
                public Iterator getPrefixes(String val) {
                    return null;
                }

                // Dummy implemenation - not used!
                public String getPrefix(String uri) {
                    return null;
                }
            };

            // Now the XPath expression

            String xpathStr =
                "//ns1:Order/ns1:Description";
            String xpathStr2 =
                "//ns2:Sales/ns2:Customer/@name";

            XPathFactory xpathFact =
                XPathFactory.newInstance();
            XPath xpath = xpathFact.newXPath();
            xpath.setNamespaceContext(ctx);
            String result = xpath.evaluate(xpathStr2, doc);
            System.out.println("XPath result is \"" +
                    result + "\"");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}