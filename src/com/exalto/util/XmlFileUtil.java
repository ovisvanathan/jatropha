package com.exalto.util;


import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlFileUtil {      
  
//    private enum XQuerySuffixes { xq, xquery };
    
    /**
     * Copied verbatim from Saxon's ResolveURI.java
     * Replace spaces by %20
     */
    public static String escapeSpaces(String s) {
        // It's not entirely clear why we have to escape spaces by hand, and not other special characters;
        // it's just that tests with a variety of filenames show that this approach seems to work.
        if (s == null) return s;
        int i = s.indexOf(' ');
        if (i < 0) {
            return s;
        }
        return (i == 0 ? "" : s.substring(0, i))
                + "%20"
                + (i == s.length()-1 ? "" : escapeSpaces(s.substring(i+1)));
    }
    
    /**
     * Creates a URI from a given String.  It checks to see if the String starts with
     * "http" and creates a URI after escaping the spaces, otherwise it creates a File
     * and then a URI.  This is the only way I can see to create URI from both Windows 
     * paths and web addresses
     */
    public static URI createURI(String s) throws URISyntaxException {
        // TODO: Use URI.create() but this is not correct IMHO.  See the Javadoc
        // for URI.create().  Solution: either remove "throws URISyntaxException"
        // or use "new URI()", depending on the needed behaviour (I think this is
        // the later).  -fg
        if (s.startsWith("http")) {
            return URI.create(escapeSpaces(s));
        } else if (s.startsWith("file:/")) {
            return URI.create(escapeSpaces(s));
        } else {            
            return new File(s).toURI();
        }
    }
    
    /**
     * Creates a Source from a String by calling <code>createURLFromString()</code>
     */
    public static Source createSource(String s) throws URISyntaxException {
        return new StreamSource(createURI(s).toString());
    }

    /**
     * Creates a Result from a String by calling <code>createURLFromString()</code>
     */
    public static Result createResult(String s) throws URISyntaxException {
        return new StreamResult(createURI(s).toString());
    }  
 /*   
    public static boolean isXqueryFile(String path) {
        if (path.endsWith(XQuerySuffixes.xq.toString()) || 
                    path.endsWith(XQuerySuffixes.xquery.toString())) {
             return true;
        } else {
            return false;
        }
    }    
*/
}


