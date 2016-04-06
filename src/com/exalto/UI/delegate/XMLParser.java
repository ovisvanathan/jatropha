package com.exalto.UI.delegate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.Reader;
import java.util.*; //added by nupura after Nov 4

import org.xml.sax.*;
import com.exalto.DocHandler;

public class XMLParser
{
    private File _inputFile = null;
    private org.w3c.dom.Document _dom = null;
	private Set          printed = new HashSet(); //added
	private org.w3c.dom.NamedNodeMap entities;  //added
	
    public XMLParser()
    {
        //Hardcoded. This should never get called.
        this._inputFile = new File("input.xhtml");
    }

    public XMLParser(File f)
    {
        this._inputFile = f;
    }

    public synchronized void parse(Reader r, DocHandler dh, boolean ignoreCharSet)
    {
        buildDOM();
    }

    private void buildDOM()
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(false);
		dbf.setExpandEntityReferences(false); // added by nupura after Nov 4
        try
        {
            DocumentBuilder b = dbf.newDocumentBuilder();
            b.setEntityResolver(new MyEntityResolver());
          //  b.setErrorHandler(null);
            _dom = b.parse(this._inputFile);
			//printEntities(_dom);
        } catch (ParserConfigurationException pce)
        {
            // Parser with specified options can't be built
            pce.printStackTrace();
        } catch (org.xml.sax.SAXException sxe)
        {
            sxe.printStackTrace();
        } catch (java.io.IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public org.w3c.dom.Document getDOM()
    {
        return (this._dom);
    }
    
    // added by nupura after merge Nov 4
	class MyEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
			if (systemID.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent")) {
				System.out.println("Return local copy of the xhtml-lat1.ent file");
				return new InputSource("xhtml-lat1.ent");
			}
			if (systemID.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent")) {
				System.out.println("Return local copy of the xhtml-special.ent file");
				return new InputSource("xhtml-special.ent");
		 	}
			if (systemID.equals("http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent")) {
					System.out.println("Return local copy of the xhtml-symbol.ent file");
					return new InputSource("xhtml-symbol.ent");
			}
			// If no match, returning null makes process continue normally
			return null;
		}
	}	

}

