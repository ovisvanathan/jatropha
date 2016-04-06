package com.exalto.UI.grid;


// Decompiled by DJ v3.6.6.79 Copyright 2004 Atanas Neshkov  Date: 5/12/2006 5:54:41 PM
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 



import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;

public class ResourceLocator
{

    public static ResourceLocator getInstance()
    {
        return instance;
    }

    private ResourceLocator()
    {
        resourcePaths = new ArrayList();
    }

    public ImageIcon getIcon(String s)
    {
        Iterator iterator = resourcePaths.iterator();
        
        System.out.println(" s= ");
       	
        
        while(iterator.hasNext()) {
        	System.out.println(" ==== ");
           	    
       			String s1;
       	        java.net.URL url;
       	        if(!iterator.hasNext())
       	            break; /* Loop/switch isn't completed */
       	        s1 = iterator.next().toString() + s;
       	        
       	        System.out.println(" s= " +s);
       	        url = (ResourceLocator.class).getResource(s1);
       	        
       	        System.out.println(" url= " + url);

       	        
       	        ImageIcon imageicon = new ImageIcon(url, s1);
       	        return imageicon;
       		}
        return null;
    }

    public void addResourcePath(String s)
    {
        resourcePaths.add(s);
    }

    private static ResourceLocator instance = new ResourceLocator();
    private ArrayList resourcePaths;

}