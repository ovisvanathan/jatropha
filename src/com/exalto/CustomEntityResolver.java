package com.exalto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
//import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
//import net.sf.kernow.Config;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class CustomEntityResolver implements EntityResolver {
    
  //  private ConcurrentHashMap<String, String> referencedDocs;
	 private HashMap referencedDocs;
 //   private Config config;
    
    private static CustomEntityResolver customEntityResolver = new CustomEntityResolver();
    
    private CustomEntityResolver() {
    //    referencedDocs = new ConcurrentHashMap<String, String>();
    //    config = Config.getConfig();
    }
    
    public static CustomEntityResolver getInstance() {
        return customEntityResolver;
    }
    
    public InputSource resolveEntity(String publicId, String systemId) {
        synchronized(this) {

  //          System.out.println(" CER publicId " + publicId);
   //         System.out.println(" CER systemId " + systemId);
	    	
            try {
/* OV c 250408 Hack should remove later 
            		File entFile = new File(systemId);

                    
                //    File f = new File(config.getLocalCacheDir(), entFile.getName());
                    BufferedReader in;
                    URL url = new URL(systemId);
    //                System.out.println(" CER url " + url);
        	    	          
                    	in = new BufferedReader(new InputStreamReader(url.openStream()));  

                    StringBuffer buff = new StringBuffer();
                    String line = in.readLine();
                    while (line != null) {
                        buff.append(line);
                        line = in.readLine();
                    }

                    in.close();
                    return (new InputSource(new StringReader((buff.toString()))));                
*/
            	
                    InputSource is = new InputSource(new StringReader(""));
                    return is;                

                    
            
            } catch (Exception e) {
             //   e.printStackTrace();
                
                System.out.println("FAILED fetching network resource. Trying local...  ");
                
                InputSource is = new InputSource(new StringReader(""));
                return is;                
                
            }
        }
        
    }
}

