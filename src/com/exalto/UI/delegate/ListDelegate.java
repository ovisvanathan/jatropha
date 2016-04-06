package com.exalto.UI.delegate;

import java.awt.Rectangle;
import javax.swing.BorderFactory;

import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JList;
import javax.swing.text.JTextComponent;
import javax.swing.JScrollPane;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

import com.exalto.UI.mdi.Editor;
import com.exalto.UI.PopupManager;
import com.exalto.util.StringUtil;

import com.exalto.UI.mdi.editor.XmlDocument;

//import xml.parser.SAXParserImpl;
//import xml.XmlParsedData;

import com.wutka.dtd.DTDAttribute;
import com.exalto.UI.delegate.MyDTDParser;


public class ListDelegate {

	private Editor editor;
	private boolean displayMessage;
	
	// OMP added begin 03/05/2005 for intellisense 
//	public static final SAXParserImpl parserInst = new SAXParserImpl();
	private String bufName;
	private String bufPath;
	JScrollPane jscp = new JScrollPane();
	private PopupManager pmgr;
	private static Properties props;
    private static ResourceBundle resources;
	private boolean found;
	private HashMap attrHash = null;
	
	static {
        try {
            resources = ResourceBundle.getBundle("resources.completion", 
                                                 Locale.getDefault());
        } catch (MissingResourceException mre) {
            System.err.println("resources/completion.properties not found");
            System.exit(1);
        }
		
			props = new Properties ();
			for (Enumeration keys = resources.getKeys (); keys.hasMoreElements ();)
			{
				final String key = (String) keys.nextElement ();
				final String value = resources.getString (key);
				props.put (key, value);
			} 

    }


	public ListDelegate(Editor editor, String bufName, String bufPath) {
		this.editor = editor;	
		this.bufName = bufName;
		this.bufPath = bufPath;
		pmgr = new PopupManager(editor);
	//	parserInst.setProperties(props);

	}


/*	
	public void displayCodeAssist() {

		try {
		
		System.out.println(" in displayCodeAssist");
		
		parserInst.stop();
		
		XmlParsedData parseData = parserInst.parse(editor, bufName, bufPath);
		
		System.out.println(" in displayCodeAssist after parse");
		
		Vector allowedElems = (Vector) parseData.getAllowedElements(editor, editor.getCaretPosition());
		
		System.out.println(" in displayCodeAssist after getAllowedElements size = " + allowedElems.size());
		
		showPopup(allowedElems);
		
		} catch(javax.swing.text.BadLocationException ble) {
			ble.printStackTrace();
		} catch(Exception  e) {
			e.printStackTrace();
		}
		
	}
*/	
	
	public void showPopup(Vector allowedElems) throws javax.swing.text.BadLocationException, Exception  {
	
		System.out.println(" ####################SPACE PRESSED############");
					int currLine = editor.getCaretLine();
					System.out.println(" Curr line in editor = " + currLine);
	
					int start = editor.getLineStartOffset(currLine);
					int dot = editor.getCaretPosition() - start;
	
					Rectangle bounds = editor.modelToView(dot);
	
					System.out.println(" input x = " +  bounds.x);
					System.out.println(" input y = " + bounds.y);
					System.out.println(" input height = " + bounds.height);
					System.out.println(" input width = " + bounds.width);
	
	
					/** Place popup inside the scrollbar's viewport */
					 PopupManager.HorizontalBounds viewPortBounds = new PopupManager.HorizontalBounds("ViewPort"); 
					//NOI18N
	
					/** Place popup inside the whole scrollbar */
					//	PopupManager.HorizontalBounds scrollBarBounds = new PopupManager.HorizontalBounds("ScrollBar"); //NOI18N
	
					//OMP added 3/4/3005 for intellisense - code in xtree2\jlist		
					JList jcmb = new JList(allowedElems);
		//			jcmb.setCellRenderer( new CustomCellRenderer() );
		
					jscp.getViewport().add(jcmb);
					jcmb.setBorder( BorderFactory.createLoweredBevelBorder() );
			 	    pmgr.install(jscp, bounds, new PopupManager.Placement("BelowPreferred"), viewPortBounds, 0, 0);
			
	}
	
		public void displayCodeAssistDTD() {	
	
				System.out.println("in 	displayCodeAssistDTD");
	
				int pos = editor.getCaretPosition();
				
			/*	
				Element insertAt = ((StyledDocument)editor.getDocument()).getCharacterElement(pos);
			
				Element parent = insertAt.getParentElement();
				System.out.println("parent name " + parent.getName());
				
				Hashtable dtdElements = editor.getDTDElements();
				
				AttributeSet set = insertAt.getAttributes();
				if (set != null)
				{
				
					System.out.println("inside set not null ");

					Object o = set.getAttribute(XML.Attribute.ENDTAG);
					if (o != null && (o instanceof Boolean) &&
								   ((Boolean) o).equals(new Boolean("false")))
					{
						parent = parent.getParentElement();
						System.out.println("new parent name " + parent.getName());

					}
				}	

				
				
				System.out.println("dtdElements = " + dtdElements);

					 
				//System.out.println("Element at pos " +pos + " is " +parent.getName());
				if(dtdElements != null){
					Vector innerElems = new Vector();
					System.out.println("dtdElements getting for key = " + parent.getName());
					MyDTDElement dtdElem = (MyDTDElement)dtdElements.get(parent.getName());
					if (dtdElem != null) {
						innerElems = dtdElem.getInnerElems();
					}
			*/
			
				try {
			
					Vector innerElems = getAllowedElements(editor, pos);

					System.out.println(" in displayCodeAssistDTD after dtdelements size = " + innerElems.size());
					
					

						showPopup(innerElems);

					} catch(javax.swing.text.BadLocationException ble) {
						ble.printStackTrace();
					} catch(Exception  e) {
						e.printStackTrace();
					}


				
				
		/*
				else
				{
					tagWindow.showInstance(new Vector());	
				}	
		*/		
				//System.out.println("name " +dtdElem.getInnerElems());
				
			

		
		}
		
	      //{{{ getAllowedElements() method
      public Vector getAllowedElements(JTextComponent buffer, int pos) 
throws BadLocationException
      {
            // make sure we are not inside a tag
			
			MyDTDParser dtdParser = editor.getDTDParser();
			
			if(dtdParser != null) {
					Hashtable dtdElements = dtdParser.getDTDElements();

					System.out.println("DTDElements size " + dtdElements.size());

					Vector orderedElems = null;

					if(TagParser.isInsideTag(buffer.getText(0,pos),pos)) 
					{
						// OMP modified 17/4/2005 to return vector
						  return new Vector();
					}

					TagParser.Tag parentTag = TagParser.findLastOpenTag(
						  buffer.getText(0,pos),pos, dtdElements);

					// OMP modified 17/4/2005 to instantiate vector
					// ArrayList returnValue = new ArrayList();
					 Vector returnValue = new Vector();
					 Hashtable attrs = null;

					if(parentTag == null)
					{
						  // add everything
						  Iterator iter = dtdElements.keySet().iterator();
						  while(iter.hasNext())
						  {
								String prefix = (String)iter.next();
								MyDTDElement dtdElem = (MyDTDElement) dtdElements.get(prefix);
								orderedElems = dtdElem.getInnerElems();
								attrs = dtdElem.getAttributes();
								System.out.println("curr elem attrs  " + attrs);

						  }
					}
					else {
						  // add everything
						  Iterator iter = dtdElements.keySet().iterator();
						  while(iter.hasNext())
						  {
							  String prefix = (String)iter.next();
					  //		MyDTDElement allelem = (MyDTDElement) dtdElements.get(prefix);
					  //		System.out.println("iter dtd elems = " + allelem.getAttributes());


					//  		System.out.println(" prefix =  " + prefix);

								MyDTDElement dtdElem = (MyDTDElement) dtdElements.get(parentTag.getName());
								orderedElems = dtdElem.getInnerElems();

							/*
								attrHash = new HashMap();						
								System.out.println(" orderedElems.size()  " + orderedElems.size());

								for(int i=0;i<orderedElems.size();i++) {
									String listItem = (String) orderedElems.elementAt(i);
									System.out.println(" listItem =  " + listItem);

									MyDTDElement childElem = (MyDTDElement) dtdElements.get(listItem);
									Hashtable childAttrs = childElem.getAttributes();
									Iterator attrKeys = childAttrs.keySet().iterator();
									Vector attrVec = new Vector();
									HashMap attrValHash = new HashMap();

									while(attrKeys.hasNext()) {
										String eAttr = (String) attrKeys.next();
										DTDAttribute dtdAttr = (DTDAttribute) childAttrs.get(eAttr);
										System.out.println(" dtdattr name =  " + dtdAttr.getName());
										attrVec.add(eAttr);

									//	String defVal = (dtdAttr.getDefaultValue() == null)? null : dtdAttr.getDefaultValue();

									//	if(defVal != null) {
									//		String [] defValArr = new String[1];
									//		defValArr[0] = defVal;
									//		attrValHash.put(listItem + "&&" + eAttr, defValArr);
									//	}


									}

									attrHash.put(listItem, attrVec);
								//	if(attrDefValVec != null)
								//		attrHash.put(listItem, attrDefValVec);

								}

							*/	


							//	for(int k=0;k<orderedElems.size();k++)  {
							//		System.out.println("innerelems class type = " + orderedElems.elementAt(k).getClass());
								//    MyDTDElement myelem = (MyDTDElement) orderedElems.elementAt(k);
								//	if(myelem.hasAttributes()) 
								//		System.out.println("curr elem name = " + myelem.getName() + " attrs = " + myelem.getAttributes());
							//	}


						  }
					}
          /*
		  //  else
            {

				   returnValue.addAll(parentDecl.getChildElements(parentPrefix));

                  // add everything but the parent's prefix now
                  Iterator iter = mappings.keySet().iterator();
                  while(iter.hasNext())
                  {
                        String prefix = (String)iter.next();
                        if(!prefix.equals(parentPrefix))
                        {
                              CompletionInfo info = (CompletionInfo)
                                    mappings.get(prefix);
                              info.getAllElements(prefix,returnValue);
                        }
                  }
            }
			
			*/				
            Collections.sort(orderedElems, new Compare());			
            return orderedElems;
		
		 }
		 
		 return null;
			
		
      } //}}}
	  
	  public void undisplay() {
		  pmgr.uninstall(jscp);
	  }

 
	  public Vector getAttributeVector(JTextComponent buffer, int pos, String selElem) {
	  
	  	Vector attrVec = null;
		String currTag = null;
	  	 try { 
		 
		 	 MyDTDParser dtdParser = editor.getDTDParser();
		 
		 	if(dtdParser != null) {
	
				Hashtable dtdElements = dtdParser.getDTDElements();

				 currTag = TagParser.getInsideTag(buffer.getText(0, pos), pos); 
				 System.out.println(" start tag obj = " + currTag); 
				 
				if(currTag != null) { 	
					int apos = currTag.indexOf("&");
					int prevpos = 0;
					if(apos != -1) {
						prevpos = Integer.parseInt(currTag.substring(apos+1));
						System.out.println(" prevpos = " + prevpos); 
					}		 
		
				 Vector allowedElems = getAllowedElements(buffer, prevpos);
				 System.out.println(" allowedElems size = " + allowedElems.size()); 
			 
			 
				 for(int i=0;i<allowedElems.size();i++) {
				 	String aelem = (String) allowedElems.elementAt(i);
				
				 System.out.println(" curr elem " + aelem); 
					
				if(aelem.intern() == currTag.substring(0, apos).intern()) {
					 System.out.println(" in if "); 
				
					MyDTDElement childElem = (MyDTDElement) dtdElements.get(aelem);
					Hashtable childAttrs = childElem.getAttributes();
					Iterator attrKeys = childAttrs.keySet().iterator();
					attrVec = new Vector();
					while(attrKeys.hasNext()) {
						String eAttr = (String) attrKeys.next();
						 System.out.println(" eattr " + eAttr); 
						attrVec.add(eAttr);
					}
			 	}
			 }
		  }
		  
		  	return attrVec;
		  
		  }
		  
			 
	 } catch(Exception e) {
			e.printStackTrace();
	 }


	  	 return null;
	  }

	  
	
	//{{{ Compare class
		public static class Compare implements TagParser.Compare
		{
			public int compare(Object obj1, Object obj2)
			{
				return StringUtil.compareStrings(
					(String)obj1,
					(String)obj2,true);
			}
		} //}}}
	

}