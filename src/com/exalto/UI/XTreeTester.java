package com.exalto.UI;

import java.util.*;
import java.io.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.*;

// import com.vs.ezlicrun.*; // import ezlm runtime library


public class XTreeTester {

	static Logger logger;
	
		/* File types */
		static final int EZLM_FILE_TYPE_UNKNOWN	= 0;
		static final int EZLM_FILE_TYPE_TXT		= 1;
		static final int EZLM_FILE_TYPE_XML		= 2;
		static final int EZLM_FILE_TYPE_CSV		= 3;
		static final int EZLM_FILE_TYPE_TAB		= 4;
		static final int EZLM_FILE_TYPE_INI		= 5;
		static final int EZLM_FILE_TYPE_KEY		= 6;
	
		static final int KEY_MAXLENGTH = 800;


public static void main( String[] args ){

	String fileName = "";
	BufferedReader reader;
	String line;
	ArrayList xmlText = null;
	XmlEditor xmlEditor;
	// Build a Document object based on the specified XML file
	
	
	 logger = Logger.getLogger(XTreeTester.class.getName());
//OV10	 PropertyConfigurator.configure("resources\\log4j.properties");
//OV10		logger.info("Entered XmlEditor application");
		
		

	try{
	
	// ov added
	
	/*
			String keyfile = args[0];
			String userhost = args[1];
			String productName = null;
			String appPwd = null;
			String keytext = null;

			if (args.length > 2) {
				productName = args[2];
				appPwd = args[3];
			}
			System.out.println("\nHello EasyLicenser World.\n");
			System.out.println("Arguments:");
			System.out.println("  License key file: " + keyfile);
			System.out.println("  User / Host name: " + userhost);
			System.out.println("  Product name:     " + (productName == null ? "<not specified>":productName));
			System.out.println("  App Passwd Public Key:     " + (appPwd == null ? "<not specified>":appPwd));
			if (userhost.equals("null"))
				userhost = null;
			int fileType = getFileType(keyfile);
			Object[] ret = readFile(keyfile, 1000);
			if (ret == null) {
				System.out.println("*** Cannot process license key file due to errors.  Aborting. ***");
				System.exit(1);
			}
			int numlines = ((Integer)ret[1]).intValue();
			if (numlines == 0) {
				System.out.println("License key file is empty.  Nothing to do.  Exiting.");
				System.exit(0);
			}
			String[] filelines = (String[])ret[0];
			System.out.println("\n========================================================\nChecking all license key in file...");
			int currLine = 0;
			int keyno = 0;
			Object[] ret2 = null;
			while (currLine < numlines &&
				   (ret2 = getKey(filelines, numlines, currLine, fileType)) != null) {
				keyno++;
				keytext = (String)ret2[0];
				currLine = ((Integer)ret2[1]).intValue();
		
			}
			
	
		// First obtain the user login name
		// (You could get the user name from any
		// number of places, including the OS user)
		// Note: this code is orthogonal to ezlm.
		String userName = 
			(String) JOptionPane.showInputDialog(
						null, 
						"Enter User Name:", "Login", 
						JOptionPane.PLAIN_MESSAGE);
		userName = userName.trim();
	
		// Check the license key that is already obtained 
		// from the registry / property file, in this example into 
		// a "config" class.  For examples on how to read a license
		// key from an exported file, see "jdemo.java".
		// EZLM code
		
		System.out.println("Showing dialog");
		
		
		EzLicenseInfo licenseInfo = new EzLicenseInfo();
		try {
			// EZLM code
			int warnings = 
				licenseInfo.checkSingleUserLicenseKeyBasic(
						keytext, // NOTE: *not* ezlm code						0, 0, 0, 0, 0,
						userName);
		} catch (EzLicExceptionBase eek) {
			JOptionPane.showMessageDialog(
							(JComponent)null,
							"You are not licensed to use this product. " +
							"Please contact technical support.",
							"Licensing Error",
							JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
		System.out.println(" success");

	// ov added end
	*/
		if( args.length > 0 ){
			fileName = args[0];
			if ( fileName.substring( fileName.indexOf( '.' ) ).equals( ".xml" ) ||
			fileName.substring( fileName.indexOf( '.' ) ).equals( ".fo" ) ||
			fileName.substring( fileName.indexOf( '.' ) ).equals( ".xhtml" ) ){


				reader = new BufferedReader( new FileReader( fileName ) );
				xmlText = new ArrayList();

				while ( ( line = reader.readLine() ) != null ){
					xmlText.add( line );
				} //end while

				 	// The file will have to be re-read when the Document object is parsed
					reader.close();

			// Construct the GUI components and pass a reference to the XML root node
//					xmlEditor = new XmlEditor( "XmlEditor 1.0", xmlText );

				// ov added for catalog support			
					xmlEditor = new XmlEditor( "XmlEditor 1.0", fileName);
			} else {
				help();
			} //end if
		}
		else {
		
		System.out.println(" success xmleditor");

		//omprakash changed 30/1/2005
		xmlEditor = XmlEditor.getInstance("XmlEditor version 1.0");
		
		
		} //end if( args.length > 0 )
		
		

				}catch( FileNotFoundException fnfEx ){
			//OV10		logger.info(fileName + " was not found");
					System.out.println(" file not found ");

					System.exit(1);
				}catch( Exception ex ) {
					ex.printStackTrace();
					System.out.println(" logger error ");

					//OV10		logger.error("cannot initialize XmlEditor. Exiting..." + ex.getMessage());
					System.exit(1);
				}// end try/catch
			} // end main()


	public static void help() {
	//OV10	logger.info("Filenaame should end in XML, FO or XHTML");
	}

	

	}  // end class

