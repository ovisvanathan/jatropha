/**
 *
 */
package com.exalto.UI.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;						
import javax.swing.text.Document;
import java.awt.Dimension;
import com.exalto.UI.XmlEditor;

import com.exalto.util.XmlUtils;
/** Creates a UI to display log messages from a SwingAppender
 * @author pshah
 *
 */
public class FrameAppenderUI  implements AppenderUI {
        //UI attributes
        private JFrame jframe;
        private JButton startPause; //button for start/pause - toggles
        private JButton stop; //stop button
        private JButton dock; //stop button

	private JButton clear; //button to clear the text area
    private JButton search; //search button
    private JTextField searchField; //search field
        private JPanel buttonsPanel; //panel to hold all buttons
        private JTextPane logMessagesDisp; //display area
        private JScrollPane scrollPane;
        //buffer to hold log statements when the UI is set to PAUSE
        private List logBuffer;
        //flag to indicate if we should display new log events
        private int appState;

        /* Constants */
        public static final String STYLE_REGULAR = "regular";
        public static final String STYLE_HIGHLIGHTED = "highlighted";
        public static final String START = "start";
        public static final String PAUSE = "pause";
        public static final String STOP = "stop";
        public static final String DOCK = "dock";

	public static final String CLEAR = "clear";
        public static final int STARTED = 0;
        public static final int PAUSED = 1;
        public static final int STOPPED = 2;

    /**
     * An instance for SwingAppenderUI class. This holds the Singleton.
     */
    private static AppenderUI instance;

	static ImageIcon startIcon;
    static ImageIcon stopIcon;
    static ImageIcon searchIcon;
    static ImageIcon pauseIcon;
    static ImageIcon dockIcon;
    static ImageIcon clearIcon;
    
	private XmlUtils xutils;
	private XmlEditor xeditor;

	IncrementalSearcher incrementalSearcher;

	/**
     * Method to get an instance of the this class. This method ensures that
     * SwingAppenderUI is a Singleton using a doule checked locking mechanism.
     * @return An instance of SwingAppenderUI
     */
    public static AppenderUI getInstance(boolean visible) {
        System.out.println("getting UI Instance");
		if (instance == null) {
                synchronized(FrameAppenderUI.class) {
                        if(instance == null) {
                                instance = new FrameAppenderUI(visible);
                        }
                }
        }
        return instance;
    }
   
        /**
         * Private constructer to ensure that this object cannot e instantiated
         * from outside this class.
         */
        private FrameAppenderUI(boolean visible) {
                //set internal attributes
                logBuffer = new ArrayList();
                appState = STARTED;
          
                xutils = XmlUtils.getInstance();				  

                //create main window
                jframe = new JFrame();
              //  jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  			    jframe.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

                //initialize buttons
                initButtonsPanel();
               
                //create text area to hold the log messages
                initMessageDispArea();
               
			//	setLayout(new BorderLayout());
			//    setPreferredSize(new Dimension(width, 30));

				
			//	add(BorderLayout.NORTH, buttonsPanel);
			//	add(BorderLayout.CENTER, scrollPane);

                //add components to the contentPane
                jframe.getContentPane().add(BorderLayout.NORTH, buttonsPanel);
                jframe.getContentPane().add(BorderLayout.CENTER, scrollPane);
                jframe.setSize(800,600);

		   

		       if(visible)
					jframe.setVisible(true);

		}
       
        /**Displays the log in the text area unless dispMsg is set to false in which
         * case it adds the log to a buffer. When dispMsg becomes true, the buffer
         * is first flushed and it's contents are displayed in the text area.
         * @param log The log message to be displayed in the text area
         */
        public void doLog(String log) {
                if(appState == STARTED) {
                        try {
                        StyledDocument sDoc = logMessagesDisp.getStyledDocument();
                        if(!logBuffer.isEmpty()) {
                                System.out.println("flushing buffer");
                                Iterator iter = logBuffer.iterator();
                                while(iter.hasNext()) {                                
                                        sDoc.insertString(0, (String)iter.next(), sDoc.getStyle(STYLE_REGULAR));                        
                                        iter.remove();
                                }
                        }                      

					//	sDoc.insertString(0, log, sDoc.getStyle(STYLE_REGULAR));

						SimpleAttributeSet aset = null;						
						if(log.contains("ERROR")) {
							aset = new SimpleAttributeSet();
							StyleConstants.setForeground(aset, Color.red);
							sDoc.insertString(sDoc.getLength(), log, aset);
						} 
						if(log.contains("OUTPUT")) {
							aset = new SimpleAttributeSet();
							StyleConstants.setForeground(aset, Color.blue);
							sDoc.insertString(sDoc.getLength(), log, aset);
						}
						else {
							sDoc.insertString(sDoc.getLength(), log, sDoc.getStyle(STYLE_REGULAR));
						}

							logMessagesDisp.setCaretPosition(logMessagesDisp.getDocument().getLength());

                        } catch(BadLocationException ble) {
                                System.out.println("Bad Location Exception : " + ble.getMessage());
                        }
                }
                else if(appState == PAUSED){
                        logBuffer.add(log);
                }
        }
       
        /**creates a panel to hold the buttons
         */

/*
        private void initButtonsPanel() {
                // TODO: Add clear button to clear the log statements.
                buttonsPanel = new JPanel();
                startPause = new JButton(PAUSE);
                startPause.addActionListener(new StartPauseActionListener());
                stop = new JButton(STOP);
                stop.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                appState = STOPPED;
                                startPause.setText(START);
                        }
                });
        clear = new JButton(CLEAR);
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logMessagesDisp.setText("");
            }
        });
       
        searchField = new JTextField(25);
        search = new JButton("Search");
        search.addActionListener(new SearchActionListener());

        dock = new JButton(DOCK);
                dock.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            
							XmlEditor xedit = XmlEditor.getInstance("");  

							jframe.setVisible(false);
						
							xedit.showConsole();  


                        }
                });

			initIcons();


			startPause.setIcon(startIcon);
			stop.setIcon(stopIcon);
			clear.setIcon(searchIcon);
			search.setIcon(pauseIcon);
			dock.setIcon(dockIcon);

				
				buttonsPanel.add(startPause);
                buttonsPanel.add(stop);
		        buttonsPanel.add(clear);
				buttonsPanel.add(searchField);
		        buttonsPanel.add(search);
				buttonsPanel.add(dock);

       
        }

*/

        private void initButtonsPanel() {
                // TODO: Add clear button to clear the log statements.
                buttonsPanel = new JPanel();
         //       startPause = new JButton(PAUSE);

				startPause = new JButton("");
				startPause.setIcon(pauseIcon);

                startPause.addActionListener(new StartPauseActionListener());
            //    stop = new JButton(STOP);
			
				stop = new JButton("");
                
				stop.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                                appState = STOPPED;
                       //         startPause.setText(START);
					            startPause.setText("");
								startPause.setIcon(startIcon);

                        }
                });
      //  clear = new JButton(CLEAR);
		clear = new JButton("");

		clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logMessagesDisp.setText("");
            }
        });
       
        searchField = new JTextField(25);
   //     search = new JButton("Search");
		search = new JButton("");

        search.addActionListener(new SearchActionListener());


	
        
 //       floatable = new JButton(FLOAT);
        dock = new JButton("");
                dock.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            
							XmlEditor xedit = XmlEditor.getInstance("");  

							jframe.setVisible(false);
							
							xedit.setConsole(FrameAppenderUI.this);
							xedit.showConsole();  

					
						}
                });
        
	/*	
		List list = Collections.synchronizedList(new LinkedList());
	    list.add(startPause);
	    list.add(stop);
        list.add(clear);
		list.add(startPause);
		list.add(search);
		list.add(startPause);
		list.add(floatable);

		XmlUtils.equalizeSizes(list);


		Iterator itr = list.iterator();
		while(itr.hasNext()) {
			Object obj = itr.next();
				if(obj instanceof JButton) { 
					JButton tbutt = (JButton) obj;

					buttonsPanel.add(tbutt);
				} // added for wordwrap
*/
			initIcons();

		//	startPause.setPreferredSize(new Dimension(, 30));
		//	stop.setPreferredSize(new Dimension(width, 30));
		//	clear.setPreferredSize(new Dimension(width, 30));
		//	search.setPreferredSize(new Dimension(width, 30));
		//	floatable.setPreferredSize(new Dimension(width, 30));


			startPause.setIcon(startIcon);
			stop.setIcon(stopIcon);
			clear.setIcon(clearIcon);
			search.setIcon(searchIcon);
			dock.setIcon(dockIcon);

			buttonsPanel.add(startPause);
			buttonsPanel.add(stop);
			buttonsPanel.add(clear);
			buttonsPanel.add(searchField);
			buttonsPanel.add(search);
			buttonsPanel.add(dock);


	//	}




        }


        /**Creates a scrollable text area
         */
        private void initMessageDispArea() {
                logMessagesDisp = new JTextPane();
                scrollPane = new JScrollPane(logMessagesDisp);
                scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                //add styles
                StyledDocument sDoc = logMessagesDisp.getStyledDocument();
                Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);        
                Style s1 = sDoc.addStyle(STYLE_REGULAR, def);
                StyleConstants.setFontFamily(def, "SansSerif");
               
                Style s2 = sDoc.addStyle(STYLE_HIGHLIGHTED, s1);
                StyleConstants.setBackground(s2, Color.BLUE);


			//	incrementalSearcher = new IncrementalSearcher(logMessagesDisp, searchField);

			  //  search.addActionListener(incrementalSearcher);
			 //   searchField.addActionListener(incrementalSearcher);
		//		searchField.getDocument().addDocumentListener(incrementalSearcher);
    


        }
       
        /**************** inner classes *************************/
       
        /**Accepts and responds to action events generated by the startPause
         * button.
         */
        class StartPauseActionListener implements ActionListener {
                /**Toggles the value of the startPause button. Also toggles
                 * the value of dispMsg.
                 * @param evt The action event
                 */
                public void actionPerformed(ActionEvent evt) {
                        JButton srcButton = (JButton)evt.getSource();
                        if(srcButton.getText().equals(START)) {
                                srcButton.setText(PAUSE);
                                appState = STARTED;
                        }
                        else if(srcButton.getText().equals(PAUSE)) {
                                appState = PAUSED;
                                srcButton.setText(START);
                        }
                }
        }
       
        class SearchActionListener implements ActionListener {


                public void actionPerformed(ActionEvent evt) {
                        JButton srcButton = (JButton)evt.getSource();
                 //       if(!"Search".equals(srcButton.getText())) {
                 //               return;
                 //       }
                        System.out.println("Highlighting search results");
                        String searchTerm = searchField.getText();
                        String allLogText = logMessagesDisp.getText();
                        int startIndex = 0;
                        int selectionIndex=-1;
                        Highlighter hLighter = logMessagesDisp.getHighlighter();
                        //clear all previous highlightes
                        hLighter.removeAllHighlights();
                        DefaultHighlighter.DefaultHighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
                        while((selectionIndex = allLogText.indexOf(searchTerm, startIndex)) != -1) {
                                startIndex = selectionIndex + searchTerm.length();
                                try {
                                        int newLines = getNumberOfNewLinesTillSelectionIndex(allLogText, selectionIndex);
                                        hLighter.addHighlight(selectionIndex-newLines, (selectionIndex+searchTerm.length()-newLines), highlightPainter);
                                } catch(BadLocationException ble) {
                                        System.out.println("Bad Location Exception: " + ble.getMessage());                                                      
                                }
                        }
                }

                private int getNumberOfNewLinesTillSelectionIndex(String allLogText, int selectionIndex) {
                        int numberOfNewlines = 0;
                        int pos = 0;
                        while((pos = allLogText.indexOf("\n", pos))!=-1 && pos <= selectionIndex) {
                                numberOfNewlines++;
                                pos++;
                        }
                        return numberOfNewlines;
                }
               
        }
       
        public void close() {
                // clean up code for UI goes here.
                jframe.setVisible(false);
        }


		public void setDocument(Document doc) {
			logMessagesDisp.setDocument(doc);		
		}

		public void setVisible(boolean visible) {
					jframe.setVisible(true);
		}

    public void initIcons() {
		URL startUrl = xutils.getResource("startImage");
		URL stopUrl = xutils.getResource("stopImage");
		URL searchUrl = xutils.getResource("searchImage");
		URL clearUrl = xutils.getResource("clearImage");
		URL dockUrl = xutils.getResource("dockImage");
		URL pauseUrl = xutils.getResource("pauseImage");


		 startIcon = new ImageIcon(startUrl);
		 stopIcon = new ImageIcon(stopUrl);
		 searchIcon = new ImageIcon(searchUrl);
		 clearIcon = new ImageIcon(clearUrl);
		 dockIcon = new ImageIcon(dockUrl);
		 pauseIcon = new ImageIcon(pauseUrl);

               
	}    

	public javax.swing.text.Document getDocument(){
		return logMessagesDisp.getDocument();
	}


}
