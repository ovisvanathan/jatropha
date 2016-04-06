/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.util;

import com.exalto.UI.XmlEditor;
import com.exalto.UI.XmlEditorActions;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

/**
 *
 * @author omprakash.v
 */
public class TrangUtil {

    static String [] availableConversions = { "rng2dtd", "rng2xsd",  "rng2rnc", "rnc2dtd",  "rnc2rng", "rnc2xsd", "dtd2rng", "dtd2xsd", "dtd2rnc",  "xml2dtd", "xml2rng", "xml2xsd", "xml2rnc" };


       protected static class TrangAction extends AbstractAction {

           static XmlEditor editor;

        public TrangAction(String cmd, ImageIcon icon, XmlEditor xmlEditor) {

            super(cmd, icon);

            this.editor = xmlEditor;

        }

        public void actionPerformed(final ActionEvent e) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {

            	    	public Object construct() {

                            String cmd = e.getActionCommand();

                            System.out.println(" cmd =" + cmd);

                            convert(cmd);
                            

                            return null;
            	        }

            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };

                    worker.start();

        }

        public void convert(String cmd) {
            String [] s = cmd.split("2");
            convert(s[0], s[1]);
        }

        public void convert(String fmt1, String fmt2) {
                System.out.println(" fmt1 =" + fmt1);
                System.out.println(" fmt2 =" + fmt2);

   //             editor.doFO();
   //             xmlEditorActions.convertToCSV(e);
        }

    }


    public static AbstractAction [] getTrangActions(final XmlEditor xmlEditor) {

        AbstractAction [] trangActions = new TrangAction [availableConversions.length];

        for(int i=0;i<availableConversions.length;i++) {

            String action = java.util.ResourceBundle.getBundle("exalto/xmlgrid").getString(availableConversions[i]);

            trangActions[i] =
				new TrangAction(action, new ImageIcon("images/new.gif"), xmlEditor);
        }

           /*
            {

						public void actionPerformed(ActionEvent e) {

                            String cmd = e.getActionCommand();
                                  convert(cmd);
                            }

                            public void convert(String cmd) {
                                    String [] s = cmd.split("2");
                                    convert(s[0], s[1]);
                            }

                            public void convert(String fmt1, String fmt2) {
                                    System.out.println(" fmt1 =" + fmt1);
                                    System.out.println(" fmt2 =" + fmt2);
                            }

				};

        }
        */

            return trangActions;


        }

}
