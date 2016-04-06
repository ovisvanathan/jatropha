package com.exalto.UI.delegate;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author Nupura
 * Created on Sep 24, 2003
 */
public class MyDTDElement {

	private String name;  // name of the element
	private Vector innerElems;  // name of inner elements
	private ArrayList cardinalities;
	private Hashtable attributes;
	private boolean hasAttributes = false;
	
	public MyDTDElement() {
		super();
		name ="";
		innerElems = new Vector();
		cardinalities = new ArrayList();
		attributes =  new Hashtable();
	}
	
	public void setName(String str)
	{
		name = str;
	}


	public String getName()
	{
		return name;
	}

	public Vector getInnerElems()
	{
		return innerElems;
	}
	public void setInnerElems(String element)
	{
		innerElems.add(element);   
	}    
    
	public ArrayList getCardinalities()
	{
		return cardinalities;
	}
	public void addCardinality(int card)
	{
		cardinalities.add(new Integer(card));   
	}    
    
	public Hashtable getAttributes()
	{
		return attributes;
	}
	public void setattributes(Hashtable atts)
	{
		//attributes.put(attribute);
		attributes = atts;
		if(attributes.size() != 0)
			hasAttributes = true;
	}    

	public boolean hasAttributes()
	{
		
		return hasAttributes ;
	}    
}
