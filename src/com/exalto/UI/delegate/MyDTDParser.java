package com.exalto.UI.delegate;

/**
 * @author Nupura
 * Created on Sep 22, 2003
 */
import com.wutka.dtd.*;
import java.io.*;
import java.util.*;


public class MyDTDParser {
	private Hashtable dtdElements;
	private Hashtable entities;
	
	private String dtdName;
	private DTD dtd;
	MyDTDElement element;
	
	public MyDTDParser(String name) {
		super();
		dtdName = name;
		dtdElements = new Hashtable();
		entities = new Hashtable();
	} 

	public DTD getDTD()
	{
		return dtd;
	}
	public Hashtable getEntities()
	{
		return entities;
	}
	
	public void parseDTD()
	{
		try {
			com.wutka.dtd.DTDParser parser =  new com.wutka.dtd.DTDParser(new File(dtdName));
			
			dtd = parser.parse();
			
			Hashtable elem = dtd.elements; // contains all elements defined in dtd
			//entities = dtd.entities; // all entities
			for (Enumeration e = elem.elements() ; e.hasMoreElements() ;) 
			{
				element = new MyDTDElement();
				DTDElement el = (DTDElement) e.nextElement(); 
				element.setName(el.getName());				
				handleDTDItem(el.getContent());
				element.setattributes(el.attributes);
				dtdElements.put(el.getName(),element);										
			} // enumeration
			Hashtable ent  = dtd.entities; // contains all elements defined in dtd
			String name = "";
			String value = null;
			for (Enumeration e = ent.elements() ; e.hasMoreElements() ;) 
			{
				DTDEntity en = (DTDEntity) e.nextElement(); 
				name = en.getName();
				DTDEntity newEntity = parser.expandEntity(name);
				value = getEntityValue(newEntity);				
				if(value != null)
				{
					String unicodeStr = fromXMLUnicodeString(value.trim());
					entities.put(en.name,unicodeStr);
					//System.out.println("name and Value " +name +new Character('\u00A9').toString());
				}										
			} // enumeration 
		}// try
		
		catch(Exception e)
		{
			e.printStackTrace();//System.out.println("Excpetion " +e.getMessage());
		}
		
		System.out.println("dtdp dtdElements " +dtdElements);
			
		
	}
	
	private String getEntityValue(DTDEntity entity)
	{
		String value = null;
		
		if (entity.isParsed) 
		{
			return value;
		}
		else
		{
			if (entity.externalID != null)
			{
				if (entity.externalID instanceof DTDSystem)
				{
					try
					{
	
						String fileName = entity.externalID.system;
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
							String line = ""; value ="";
							while((line = br.readLine()) != null) {
							//System.out.println(line);
							value += line;
						} 
						value.trim();
						
					}
					catch(FileNotFoundException fe)
					{
						System.out.println("File related to external entity " +entity.getName() + "not Found !!!");
					}	
					catch(IOException ioe)
					{
						System.out.println("Error reading File !!!");
					}	
				}
				else
				{
				}
			}
			else
				value = entity.value;
		}	
		return value;
	}


	public Hashtable getDTDElements(){
		return dtdElements;
	}

	
	public  void handleDTDItem(DTDItem item)
	{
		if (item == null) return;

		if (item instanceof DTDAny)
		{
			//System.out.print("Any");
		}
		else if (item instanceof DTDEmpty)
		{
			//System.out.print("Empty");
		}
		else if (item instanceof DTDName)
		{
			//System.out.print(((DTDName) item).value);
			if(!((DTDName)item).value.equals("ANY"))
			{
				element.setInnerElems(((DTDName)item).value);
			//	System.out.print("cardinality for " +((DTDName)item).getValue() + " is " +item.getCardinal().type);
			//	System.out.print(" ");
				element.addCardinality(item.getCardinal().type);
			}	
		}
		else if (item instanceof DTDChoice)
		{
			//System.out.print("(");
			DTDItem[] items = ((DTDChoice) item).getItems();

			for (int i=0; i < items.length; i++)
			{
				//if (i > 0)// System.out.print("|");
				handleDTDItem(items[i]);
			}
			//System.out.print(")");
		}
		else if (item instanceof DTDSequence)
		{
			//System.out.print("(");
			DTDItem[] items = ((DTDSequence) item).getItems();

			for (int i=0; i < items.length; i++)
			{
				//if (i > 0) System.out.print(",");
				handleDTDItem(items[i]);
			}
			//System.out.print(")");
		}
		else if (item instanceof DTDMixed)
		{
			//System.out.print("(");
			DTDItem[] items = ((DTDMixed) item).getItems();

			for (int i=0; i < items.length; i++)
			{
				if (i > 0) //System.out.print(",");
				handleDTDItem(items[i]);
			}
			//System.out.print(")");
		}
		else if (item instanceof DTDPCData)
		{
			//System.out.print("#PCDATA");
		}

		if (item.cardinal == DTDCardinal.OPTIONAL)
		{
			//System.out.print("?");
			//element.setCardinalities(DTDCardinal.OPTIONAL.type);
		}
		else if (item.cardinal == DTDCardinal.ZEROMANY)
		{
			//System.out.print("*");
		}
		else if (item.cardinal == DTDCardinal.ONEMANY)
		{
			//System.out.print("+");
		}
	}

	public static void main(String args[])
	{
		MyDTDParser p = new MyDTDParser("book.dtd");
		p.parseDTD();
		
	}
	/**
	 * Convert a String from XML unicode string. Basically, we look for
	 * anything starting with &# followed by a semicolon and convert it to
	 * the actual Java character representation
	 *
	 * @param s the String to be converted
	 * @return the converted string
	 */
	public static String fromXMLUnicodeString(String s) {
		StringBuffer sb = new StringBuffer(s.length());
       
		char c[] = s.toCharArray();
		int cpos = 0;
		int spos = -1;
		int epos = -1;
		int mpos = s.length();
		String str = null;
		while (cpos<mpos) {
			spos = s.indexOf("&#",cpos);
			if (spos>-1) epos = s.indexOf(";", spos);
            
			if (spos>-1 && epos>-1) {
				sb.append(s.substring(cpos,spos));
				String unicode = s.substring(spos+2,epos);
				String newChar = null;
			
				try {
					if(unicode.startsWith("x"))
					{
						int newi = Integer.decode("0"+unicode).intValue();
						char newch = (char) newi;
						sb.append(newch);
						cpos = epos+1;
					}
					else
					{
						int newi = Integer.decode(unicode).intValue();
						char newch = (char) newi;
						str = Integer.decode(unicode).toString();
						sb.append(newch);
						cpos = epos+1;
					
					}	
				} catch (Exception e) {
					sb.append(s.substring(spos,spos+2));
					cpos = spos + 2;
				}
                    
			} else {
				sb.append(s.substring(cpos,mpos));
				cpos = mpos;
			}
		}
		//System.out.println("Number is " +s + " Char is " +sb.toString() +" string is " +str);
		return sb.toString();
	}
        

}