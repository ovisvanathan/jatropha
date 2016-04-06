package com.exalto.UI;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.exalto.util.XmlUtils;


/**
 * @author 510342
 *
 * This class is the controller and is responsible for calling the 
 * right method  and returning the results as 
 * an array list 
 */
public class XmlEditorHelper {

	protected final static String SPLASH_SCREEN = "exalto.splashscreen";
	protected final static String ICON_LOADER      = "exalto.iconloader.class";
	protected final static String APPICON_LOADER      = "exalto.appiconloader.class";
		
	private Logger logger;
    private static ResourceBundle resources;
	protected ExaltoSplashScreen _splash = null;
    private XmlUtils xutils;
    boolean showSplash;

		public XmlEditorHelper() {
			logger = Logger.getLogger(XTreeTester.class.getName());
			xutils = XmlUtils.getInstance();
			resources = xutils.getResourceBundle();
  //     	startSplashScreen();	
       	}
		
		public void startSplashScreen() {
		// load the image for the splash screen
			
				ImageIcon icon = getSplashScreenImage();
		
				if (icon != null) {
						logger.info("Showing splash screen");
			
				_splash = new ExaltoSplashScreen(icon);
			// that's it.. now set the status
		}
	}
	
	    public void closeSplash() 
    {
        logger.info("Closing splash screen");
		if (_splash != null) {
			_splash.close();
			_splash = null;
			// make it gc'able
		}
    }

	public void showSplashStatus(String s) 
    {
		if (_splash != null) {
			_splash.showStatus(s);
		}
		
    }
	
	public ImageIcon getSplashScreenImage()
	{
		ImageIcon icon = loadImageFromProp(SPLASH_SCREEN);
		return icon;
	}
	
	protected ImageIcon loadImageFromProp(String propname) 
	{
		logger.info(" resources = " + resources);
		logger.info(" propname = " + propname);
		try {
			
			String val = resources.getString(propname);
			
			System.out.println(" val = " + val);
	
			if (val != null) {
				return loadImage(val,resources.getString(ICON_LOADER));
			}
		} catch(Exception e) {
			logger.debug("load property error " + e.getMessage());
		}
		return null;
		
	}
	
		public ImageIcon loadImage(String filename, String loaderclassname) 
	{
		return loadImage(filename, loaderclassname, false) ;
	}
	
	protected ImageIcon loadImage(String filename, 
								  String imgldrclass, 
								  boolean recursing) 
	{
		
		ImageIcon i = null;
		//	String val = getProperty(prop_name);
		if (filename != null) {
			
		System.out.println(" splash fname not null  = " + filename);
	
			try {
				try {
					// total hack to make images load from a jar
					// file via getResource when they're within
					// a subdir. Basically, the class that loads 
					// them via getResource must be in the same dir.
					Class imageloader = this.getClass();
					
					
					//	String imgldrclass = getProperty(IMAGE_LOADER);
					if (imgldrclass != null) {
						
			System.out.println(" in if (imgldrclass != null) ");
		
						
						try {
							Class tmploader  = Class.forName(imgldrclass);
							imageloader = tmploader;
						}
						catch (ClassNotFoundException ex){
						}
					}
											
					//	MerlotDebug.msg("val = " + val);
					URL u = xutils.getResource(SPLASH_SCREEN);
				//	URL u = imageloader.getResource(filename);
					if (u != null) {
						
						System.out.println(" in u != null = " + u);
		
						i = new ImageIcon(u);
					}
					else {
						
					System.out.println(" in u is null ");
			
						u = new URL(filename);
					
						i = new ImageIcon(u);
					}

				}
				catch (MalformedURLException mf) {
					mf.printStackTrace();
				}
				// hack hack hack cough hack
				if (i == null && !recursing) {
					// try prepending the graphics dir
					// using ICON_LOADER instead of APPICON_LOADER
					String appiconloader = resources.getString(ICON_LOADER);
					return loadImage(filename,appiconloader,true);
					
					/*	String gdir = getProperty(MERLOT_GRAPHICS_DIR);
					return loadImage(gdir+FILESEP+filename,true);
					*/
				}
			}
			catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
		//	MerlotDebug.msg("i = " + i);
		
		return i;
		
    
  }

	public void setShowSplash(boolean showSplash) {
		this.showSplash = showSplash;
	}




}
