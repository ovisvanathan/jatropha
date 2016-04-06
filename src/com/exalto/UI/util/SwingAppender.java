/**
 *
 */
package com.exalto.UI.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import com.exalto.util.XmlUtils;


/**
 * @author kalpak
 *
 */
public class SwingAppender extends AppenderSkeleton {

    /** The appender swing UI. */
//    private SwingAppenderUI appenderUI = SwingAppenderUI.getInstance();

    private AppenderUI appenderUI;
    

	String app_type;
	boolean initialized;

    public SwingAppender() {
	}

    /* (non-Javadoc)
     * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
     */
    protected void append(LoggingEvent event) {
        if (!performChecks()) {
                return;
        }
        String logOutput = this.layout.format(event);

		if(!initialized) {
		
			XmlUtils xutils = XmlUtils.getInstance();

			app_type = xutils.getResourceString("CONSOLE_APPENDER_TYPE");

			if(app_type == null || app_type.equals("INLINE"))
				appenderUI = SwingAppenderUI.getInstance();
			else if(app_type.equals("FRAME"))
				appenderUI = FrameAppenderUI.getInstance(true);
			
			initialized = true;
		}


			appenderUI.doLog(logOutput);
			


        if (layout.ignoresThrowable()) {
                String[] lines = event.getThrowableStrRep();
                        if (lines != null) {
                                int len = lines.length;
                                for (int i = 0; i < len; i++) {
                                        appenderUI.doLog(lines[i]);
                                        appenderUI.doLog(Layout.LINE_SEP);
                                }
                        }
                }
    }

    /*
         * (non-Javadoc)
         *
         * @see org.apache.log4j.Appender#close()
         */
    public void close() {
        //Opportunity for the appender ui to do any cleanup.
        /*appenderUI.close();
        appenderUI = null;*/
    }

    /* (non-Javadoc)
     * @see org.apache.log4j.Appender#requiresLayout()
     */
    public boolean requiresLayout() {
        return true;
    }

    /**
     * Performs checks to make sure the appender ui is still alive.
     *
     * @return
     */
    private boolean performChecks() {
        return !closed && layout != null;
    }
}
