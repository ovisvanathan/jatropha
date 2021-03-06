package com.exalto;

/**
* @author 510342
*
* This class is the controller and is responsible for calling the
* right method  and returning the results as
* an array list
*/
public interface ColWidthTypes {

	public final int COLUMN_WIDTH_UNSPECIFIED = 0;
	public final int COLUMN_WIDTH_INCHES = 1;
	public final int COLUMN_WIDTH_CMS = 2;
	public final int COLUMN_WIDTH_PERCENT = 3;
	public final int COLUMN_WIDTH_POINTS = 4;
	public final int COLUMN_WIDTH_PROPORTIONAL = 5;
	public final int COLUMN_WIDTH_EMPHASIS = 6;
	public final int COLUMN_WIDTH_PICA = 7;
	public final int COLUMN_WIDTH_PIXELS = 8;	


	public final int NUMPASSES = 1;

	public final int COLUMN_WIDTH_MAX = 0;
	public final int COLUMN_WIDTH_MIN = 1;
	public final int COLUMN_WIDTH_OTHER = 2;
	public final int COLUMN_WIDTH_DONE = 3;


	public final int NUMCOLTYPES = 6;

	public final String [] labelNames = { 
			"page-width", "page-height", "margin-left", "margin-right",
			"margin-top", "margin-bottom", "extent-after", "extent-before",
			"extent-start", "extent-end" };
				
	public final String DEFAULT_HEIGHT = "11.0";
	public final String DEFAULT_WIDTH = "8.5";
	public final String DEFAULT_RTMARGIN = "1.0";
	public final String DEFAULT_LTMARGIN = "1.0";
	
	public final String FRAME_IMAGE = "logo";

	public static final String ACTION_MENU_ACCELERATOR = "menu.accelerator";
	
	public static final int WINDOWS = 1;
	public static final int SOLARIS = 2;
	public static final int LINUX   = 3;
	public static final int MACOS   = 4;
	
	public static final String BACKGROUND_COLOR = "backcolor";
	public final static String APP_ICON_SMALL = "app.icon.s";
        public final static String APP_ICON_LARGE = "app.icon.l";
	public final static String ICON_LOADER      = "exalto.iconloader.class";
	public final static String APPICON_LOADER      = "exalto.appiconloader.class";
	public final static int LARGE_ICON = 32;
	public final static int SMALL_ICON = 16;

	public static final String ERR  = "err";
	public static final String CONFIG  = "config";
    
	public static final String ERROR  = "error";
	public static final String NOERROR  = "noerror";
	public static final String DEFAULT  = "default";
	public static final String VALIDATION  = "validcheck";
	
        public final String ERROR_STATUS = "ERROR_STATUS_IMAGE";
	public final String INFO_STATUS = "INFO_STATUS_IMAGE";
	public final String DEFAULT_STATUS = "DEFAULT_STATUS_IMAGE";
	public final String VALIDATION_ERROR_STATUS = "VALID_ERROR_STATUS_IMAGE";


}

