package com.exalto.util;

import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Iterator;
import java.util.HashMap;

import java.net.URL;


import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

// import com.exalto.util.XmlUtils;
import com.exalto.ColWidthTypes;


public class ExaltoResource {

	private static XmlUtils xutils;
	private Hashtable commands;
	private static ResourceBundle resources;
	private static Hashtable _keycodes;
    protected static HashMap _bundles = new HashMap();
    protected static String _classPrepend = "resources";


	public ExaltoResource() {
	
		XmlUtils xutils = XmlUtils.getInstance();
	
	}
	
		    public static KeyStroke getKeyStroke(String bname, String keycode)
		    {	
			
		//	String keycode = getResourceString(key);
			return getKeyStrokeImpl(keycode);
		    }
		    
		    public static KeyStroke getKeyStroke(String bname, String key, Locale lc)
		    {
		
		//	String keycode = getResourceString(bname,key,lc);
		//	return getKeyStrokeImpl(keycode);
			return null;
			}
			
		    protected static KeyStroke getKeyStrokeImpl(String keycode)
	    	{
				//	String keycode = ResourceCatalog.getString(rr);
				int modifiers = 0;
				char c = '\0';
	
				if (keycode != null) {
					// now try to parse the keycode
					StringTokenizer tok = new StringTokenizer(keycode,"-");
					while (tok.hasMoreTokens()) {
					String t = tok.nextToken();
					if (t.equalsIgnoreCase("cmd")) {
						// platform default command key
						modifiers |= getCommandKeyMask();
					}
					else if (t.equalsIgnoreCase("shift")) {
						modifiers |= java.awt.Event.SHIFT_MASK;
					}
					else if (t.equalsIgnoreCase("ctrl")) {
						modifiers |= java.awt.Event.CTRL_MASK;
					}
					else if (t.equalsIgnoreCase("meta")) {
						modifiers |= java.awt.Event.META_MASK;
					}
					else if (t.equalsIgnoreCase("alt")) {
						modifiers |= java.awt.Event.ALT_MASK;
					}
					else if (t.length() == 1) {
						c = t.toUpperCase().charAt(0);
					}
					else if (t.startsWith("VK_")) {
	
						c = (char)getKeyCodeNamed(t);
	
						// XXX get a keycode field via 
						// reflection
					}
	
					}
					if ((int)c > 0) {
					KeyStroke ks = KeyStroke.getKeyStroke((int)c,modifiers);
					return ks;
					}
				}
				return null;
	    	}
		
	    /**
	     * Returns the platform's preferred command key.
	     * This is CTRL on unix and windows, and META on Mac
	     */
	    protected static int getCommandKeyMask() 
	    {
		int os = getOSType();
		switch (os) {
		case ColWidthTypes.MACOS:
		    return java.awt.Event.META_MASK;
		default:
		    return java.awt.Event.CTRL_MASK;
		}
			
	    }
	
		    protected static int getKeyCodeNamed(String n) 
		    {
			if (_keycodes == null) {
			    loadKeyCodes();
			}	
			Object o = _keycodes.get(n);
			if (o instanceof Integer) {
			    return ((Integer)o).intValue();
			}
			return -1;
						
		    }
	
		public static int getOSType() 
		{
			String s = System.getProperty("os.name").toLowerCase();
			if (s.indexOf("windows") >= 0) {
				return ColWidthTypes.WINDOWS;
			}
			if (s.indexOf("sunos") >= 0) {
				return ColWidthTypes.SOLARIS;
			}
			
			if (s.indexOf("linux") >= 0) {
				return ColWidthTypes.LINUX;
			}
			if ((s.indexOf("mac") >= 0) || (s.indexOf("mac os") >= 0)) {
				return ColWidthTypes.MACOS;
			}
			
			return ColWidthTypes.WINDOWS;
			
		}
		
		protected static void loadKeyCodes() 
		{
			_keycodes = new Hashtable();
			KeyEvent evt = new KeyEvent(new java.awt.Label(""),0,0L,0,0);
				
			Field[] f = KeyEvent.class.getDeclaredFields();
			for (int i=0;i<f.length;i++) {
			    String name = f[i].getName();
			    if (name.startsWith("VK_")) {
				try {
				    int val = f[i].getInt(evt);
				    _keycodes.put(name,new Integer(val));
				    //		MerlotDebug.msg("keycode: "+name+" = "+val);
							
				}
				catch (Exception ex) {
				}
						
			    }
			}
		}


   /**
     * Gets an image file and loads it
     */
    public static ImageIcon getImage(String bname, String key) 
    {
		return getImageImpl(bname,key,Locale.getDefault());
				
    }
	
    public static ImageIcon getImage(String bname, String key, Locale lc) 
    {
		return getImageImpl(bname,key,lc);
    }


	protected static ImageIcon getImageImpl(String bname, String key, Locale locale)
    {
		String filename = xutils.getResourceString(key);
		String imgldrclass = xutils.getResourceString(ColWidthTypes.ICON_LOADER);
		return loadImage(filename, imgldrclass);
    }

//    protected static ImageIcon loadImage(String bname, String filename) 
//    {
//		return loadImage(bname, filename, false) ;
//    }
	
	protected static ImageIcon loadImage(String filename, String loader) 
	{
		return loadImage(filename, loader, false) ;
	}

	
//	protected static ImageIcon loadImage(String bname, String filename, boolean recursing) 
//			throws MissingResourceException
	protected static ImageIcon loadImage(String filename, String imgldrclass, boolean recursing) 
			throws MissingResourceException

	{

		ImageIcon i = null;

		if (filename != null) {
		//	try {
			try {
				// hack to make images load from a jar
				// file via getResource when they're within
				// a subdir. Basically, the class that loads 
				// them via getResource must be in the same dir.
				Class imageloader = ExaltoResource.class;

			//	String imgldrclass = xutils.getResourceString(colWidthTypes.ICON_LOADER);
				if (imgldrclass != null) {
				try {
					Class tmploader  = Class.forName(imgldrclass);
					imageloader = tmploader;
				}
				catch (ClassNotFoundException ex){
				}
				}
				
			//	System.out.println(" filename = " + filename);

				URL u = imageloader.getResource(filename);
				if (u != null) {
				i = new ImageIcon(u);
				}
				else {
				u = new URL(filename);

				i = new ImageIcon(u);
				}
			}
			catch (java.net.MalformedURLException mf) {
		//		mf.printStackTrace();
			}

		//	if (i == null && !recursing) {
		//		// try prepending the graphics dir
		//		String gdir = getString(bname,MERLOT_GRAPHICS_DIR);
		//		return loadImage(bname,gdir+FILESEP+filename,true);
		//	}

		//	}
			catch (Exception e) {
				e.printStackTrace();
			//	MerlotDebug.exception(e);
			}
		}
		return i;
		}
		
		/**
		 * Returns a string from a resource bundle.
		 * @param bname the application name
		 * @param key the resource key
		 */

		public static String getString(String bname, String key)
		throws MissingResourceException
		{
			try {
			return getStringImpl(bname,key,Locale.getDefault());
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		  private static String getStringImpl(String bname, String key, Locale lc) 
			throws MissingResourceException
		    {
				ResourceBundle bun = loadBundle(bname,lc);
				if (bun == null) {
					throw new MissingResourceException("Bundle not found",bname,key);
				}
				String s = bun.getString(key);
				StringUtil.KeyFinder finder = new MyKeyFinder(bname);

				String ret = StringUtil.lookupKeysInString(s,finder);
				return ret;
		    }
	
		private static ResourceBundle loadBundle(String name, Locale locale) 
		{
			String bundlekey = name +"."+locale.toString();

			ResourceBundle rb = (ResourceBundle)_bundles.get(bundlekey);
			if (rb == null && !_bundles.containsKey(name)) {
				rb = ResourceBundle.getBundle(_classPrepend+"."+name, locale);
				_bundles.put(bundlekey,rb);
			}
			return rb;
		}

	protected static class MyKeyFinder implements StringUtil.KeyFinder 
    {
		String _bundle;
		
		public MyKeyFinder(String bundle) 
		{
			_bundle = bundle;
		}

		public String lookupString(String key) 
		{
			String ret = null;
			try {
			ret = ExaltoResource.getString(_bundle,key);
			}
			catch (Exception ex) {
			}
			if (ret == null) {
				ret = xutils.getResourceString(key);
			}
			return ret;
		}
    }
		
   public static int getKeyEvent(String ke) {
        
        if(ke == null)
            return 0;
        
        if(ke.equals("F"))
            return KeyEvent.VK_F;
        else if(ke.equals("E"))
            return KeyEvent.VK_E;
        else if(ke.equals("V"))
            return KeyEvent.VK_V;
        else if(ke.equals("M"))
            return KeyEvent.VK_M;
        else if(ke.equals("P"))
            return KeyEvent.VK_D;
        else if(ke.equals("X"))
            return KeyEvent.VK_X;
        else
            return 0;
        
        
    }    
 
}