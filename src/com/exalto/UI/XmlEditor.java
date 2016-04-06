package com.exalto.UI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
//import org.jdesktop.swingx.MultiSplitLayout;
//import org.jdesktop.swingx.MultiSplitPane;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.menu.SingleCDockableListMenuPiece;
import bibliothek.gui.dock.facile.menu.RootMenuPiece;
import bibliothek.gui.dock.support.menu.MenuPiece;

import com.exalto.ColWidthTypes;
import com.exalto.TableAutoFormatter;
import com.exalto.UI.mdi.DesktopView;
import com.exalto.UI.mdi.Editor;
import com.exalto.UI.mdi.EditorDropTarget2;
import com.exalto.UI.mdi.MyInternalFrame;
import com.exalto.UI.mdi.TabbedDesktopView;
import com.exalto.UI.util.AppenderUI;
import com.exalto.UI.util.BubblePanelSimulator;
import com.exalto.UI.util.DialogAnimator;
import com.exalto.UI.util.OutputLevel;
import com.exalto.UI.util.SwingAppenderUI;
import com.exalto.util.ExaltoResource;
import com.exalto.util.ExampleFileFilter;
import com.exalto.util.ExtensionFileFilter;
import com.exalto.util.FileUtil;
import com.exalto.util.StatusEvent;
import com.exalto.util.StatusListener;
import com.exalto.util.SwingWorker;
import com.exalto.util.XmlUtils;
import com.exalto.xslt.TransformerIntf;
import com.exalto.xslt.XslTransformer;
import com.exalto.xslt.XslTransformer20;

public class XmlEditor extends JFrame implements ChangeListener,
		PropertyChangeListener, ActionListener, ColWidthTypes,
		InternalFrameListener, StatusListener, CaretListener {

	/***
	 * This constructor passes the graphical construction off to the overloaded
	 * constructor and then handles the processing of the XML text
	 */

	// This is the XTree object which displays the XML in a JTree:

	private XTree xTree;
	// This is the textArea object that will display the raw XML text
	private JTextArea textArea;
	// One JScrollPane is the container for the JTree, the other is for the
	// textArea
	private JScrollPane jScroll, jScrollRt;
	// This JSplitPane is the container for the two JScrollPanesp
	private JSplitPane splitPane;
	// This JButton handles the tree Refresh feature
	private JButton refreshButton;
	// This Listener allows the frame's close button to work properly
	private WindowListener winClosing;
	private Dimension minimumSize;
	private String xmlFile;
	private String fmtObjFile;
	private String pdfFile;

	private String xmlFileDir;
	protected String _current_dir = null;

	private ToolBar tBar;
	private JPanel tBarPanel;
	private StatusBar statusBar;
	private StatusBar status;
	private JFrame elementTreeFrame;
	private JFrame helpFrame;
	private JFrame logFrame;
	JLabel statusTxt;
	JScrollPane logScroll;
	private boolean fileNamesSelected = false;

	private Hashtable commands;
	private Hashtable menuItems;
	protected FileDialog fileDialog;
	protected JOptionPane jDialog;
	private boolean runSuccessful = false;
	private String outfile;
	private XmlUtils xutils;
	/*
	 * OV c 24/03/08 private CutAction cutAction = new CutAction(); private
	 * CopyAction copyAction = new CopyAction(); private PasteAction pasteAction
	 * = new PasteAction();
	 */
	// OV a 24/03/08 1 line
	XmlEditorActions _actions;

	private UndoAction undoAction = new UndoAction();
	private RedoAction redoAction = new RedoAction();
	// ov added for find/find next
	// private FindAction findAction = new FindAction(this);
	// private FindNextAction findnextAction = new FindNextAction(this);

	public AssignStyleAction assignStyleAction = new AssignStyleAction();

	private CustomDialog customDialog;

	private DocumentPane[] documents;
	private JTabbedPane tabbedPane;
	private DesktopView desktopView;
	private String idePath = System.getProperty("user.dir") + File.separator;
	// strings to hold status string
	private String lineNumber = null;
	private String colNumber = null;
	private String statusLine = null;
	// OV added for replaceall
	private int repCount = 0;
	private boolean reset = true;

	// ov added for undo/redo
	private Hashtable fileToFrameMap;

	public Hashtable getFileToFrameMap() {
		return fileToFrameMap;
	}

	// ov added for file update
	private Hashtable checkSumHash = new Hashtable();

	public Hashtable getCheckSumHash() {
		return checkSumHash;
	}

	// ov added for views
	protected String defaultViewForNewFiles = null;

	protected String SKIN_THEMEPACK = "coronaHthemepack.zip";

	/**
	 * Directory to go to when opening a file. This is set to the last directory
	 * that the user opened a file from.
	 */
	// protected File _current_dir = null;

	protected Vector _statusListeners = new Vector();
	protected int linenos = 1;

	// Credit goes to Rick Ross of Javalobby for this - I needed something
	// to cut and paste
	private static final String TEXT = "Tools for Java developers are like "
			+ "restaurants in New  York City. Even if you visited a different restaurant "
			+ "in NYC for every  meal, every day, you would never get to all the eateries "
			+ "in the Big Apple.  Likewise with Java tools, you could probably try a new "
			+ "Java development  tool every single day and never get through all of them. "
			+ "There is simply  an amazing adundance of Java tools available today, and "
			+ "a great many  of them are absolutely free. Why do we still hear industry "
			+ "pundits complaining that  Java lacks tools? I don't know what the heck they "
			+ "are talking about?  This seems to be one of those criticisms that lives long "
			+ "past the time  when it is no longer valid. A few years ago it may have been "
			+ "true that  there weren't as many Java tools available as most developers "
			+ "would have  liked, but today there are so many that no-one among us could "
			+ "possibly  hope to keep up with the flow. Here's a sample list from A to Z, "
			+ "and none of these will cost you a penny.";

	/**
	 * Actions defined by the Notepad class
	 */
	/*
	 * OV c 24/03/08 private Action[] defaultActions = { new NewAction(), new
	 * OpenAction(), new SaveAction(), new CloseAction(), new QuitAction(), //
	 * new ShowElementTreeAction(), cutAction, copyAction, pasteAction,
	 * undoAction, redoAction, findAction, findnextAction, new WordWrapAction(),
	 * // new GenFOAction(), // new BalanceAction(), // new GenPDFAction(), //
	 * new AboutAction()
	 * 
	 * // new JDCPopupShowAction(), // new JDCPopupHideAction() };
	 */
	public static final String MNEMONIC_KEY = "MnemonicKey";

	private static final int XHTML = 0;
	private static final int XSL = 1;
	private static final int FO = 2;
	private static final int FO_BAL = 3;

	private static final String[] extensions = { "xml", "xhtml", "xsl", "fo",
			"xsd", "rng" };

	private static final String[] schema_extns = { "xsd", "rng", "xdr" };

	private String[] dtd_extn = { "dtd" };

	private static final String[] inputFiles = new String[3];
	// private Hashtable extensions;
	// private String [] contentTypes = { "HTML", "TEXT" };

	private String[] tabTitles = { "XHTML", "XSL", "FO", "FO-BAL" };
	/**
	 * Suffix applied to the key used in resource file lookups for an image.
	 */
	public static final String imageSuffix = "Image";

	/**
	 * Suffix applied to the key used in resource file lookups for a label.
	 */
	public static final String labelSuffix = "Label";

	/**
	 * Suffix applied to the key used in resource file lookups for an action.
	 */
	public static final String actionSuffix = "Action";
	public static final String suffixSuffix = "Suffix";
	public static final String accelSuffix = "Accel";

	public static final String showElementTreeAction = "show-element-tree";
	public static final String propertiesFile = "resources\\XmlEditor.properties";
	public static final String configFile = "resources\\config.properties";

	/**
	 * Suffix applied to the key used in resource file lookups for tooltip text.
	 */
	public static final String tipSuffix = "Tooltip";

	public static final String openAction = "open";
	public static final String newAction = "new";
	public static final String saveAction = "save";
	public static final String closeAction = "close";
	public static final String quitAction = "quit";
	public static final String genfoAction = "generate-fo";
	public static final String balanceAction = "balance-column-width";
	public static final String genpdfAction = "generate-pdf";
	public static final String optionAction = "show-options";
	public static final String aboutAction = "about";
	public static final String selectAllAction = "select-all";
	public static final String wordwrapAction = "wordwrap";
	public static final String jdcshowAction = "jdcshow";
	public static final String jdchideAction = "jdchide";
	public static final String textviewAction = "textview";
	public static final String gridviewAction = "gridview";

	private int fileOpened = 0;
	private static Logger logger;
	private XmlEditorHelper xhelper;
	private static final String DEFAULT_TITLE = "eXalto Formatter";

	private JFileChooser chooser;
	private String readFile;
	private String xslFile;
	private JDialog assignDlg;

	private JOptionPane optionPane;
	private String okButton;
	private String cancelButton;
	private JTextField xslFileName;
	ExampleFileFilter xmlFilter, xslFilter, foFilter;

	private static ResourceBundle resources;
	private static ResourceBundle preferences;
	private Properties props;
	private Properties prefs;

	// ov added for find replace
	public int searchIndex = -1;
	private MyInternalFrame findStart;
	private MyInternalFrame xmlFileFrame;

	public String searchData = null;
	public boolean searchDirection;
	public boolean wholeWord;
	public boolean replace;
	public String repText = null;
	public String newSearch = null;

	JTextPane output = new JTextPane();

	SwingAppenderUI appenderUI = null;
	JSplitPane editorPane = null;
	Object console;

	// private MDIDesktopPane desktop = new MDIDesktopPane();
	// private JMenuBar menuBar;
	// private JScrollPane mdiScrollPane = new JScrollPane();
	// private int docIndex = 0;
	// private Hashtable mdiDocuments = new Hashtable();

	public boolean waitingToAssign = false;
	private String frameMapFile = null;
	private JPopupMenu popupMenu;
	private static XmlEditor xmlEditor;

	// ov added for catalog support
	// private String catalog = null;

	// OV added for assign xslt
	Timer timer = null;

	public Timer getTimer() {
		return timer;
	}

	// OV added for new status display 9/12/2005
	private static BubblePanelSimulator bpsm;
	private static DialogAnimator dec;

	ApplicationPrefs dialog;

	// OV added 27/06/08
	// OV c 27/03/09
	// JMenu gridMenu;

	// OV added 18/07/08
	ExaltoExplorer explorer;

	static {
		try {
			// PREREL UNCOMENTED TO LOAD RESOURCES HERE
			resources = ResourceBundle.getBundle("resources.XmlEditor",
					Locale.getDefault());

			// ResourceBundle fr_resources =
			// ResourceBundle.getBundle("resources.XmlEditor",
			// new Locale("fr", "FR"));

			/*
			 * 
			 * preferences = ResourceBundle.getBundle("resources.config",
			 * Locale.getDefault());
			 */

			logger = Logger.getLogger(XmlEditor.class.getName());

		} catch (MissingResourceException mre) {
			logger.warn("XmlEditor.properties not found");
			System.exit(1);
		}
	}

	private void loadResources() {
		// OV c 2 lines 20/03/08
		// resources = ResourceBundle.getBundle("resources.XmlEditor",
		// Locale.getDefault());

		// ResourceBundle fr_resources =
		// ResourceBundle.getBundle("resources.XmlEditor",
		// new Locale("fr", "FR"));
		preferences = ResourceBundle.getBundle("resources.config",
				Locale.getDefault());

	}

	private XmlEditor(String title, ArrayList xmlText)
			throws ParserConfigurationException {
		super(title);
		// resources = xutils.getResourceBundle();

		System.out.println("creating helper");
		xhelper = new XmlEditorHelper();
		System.out.println("created helper");

		// OV a 24/03/08
		_actions = new XmlEditorActions(this);

		init();

		textArea.setText((String) xmlText.get(0) + "\n");
		for (int i = 1; i < xmlText.size(); i++)
			textArea.append((String) xmlText.get(i) + "\n");
		// xTree.refresh( textArea.getText() );
	} // end XmlEditor( String title, String xml )

	public XmlEditor(String title, String fileName)
			throws ParserConfigurationException {
		super(title);

		xutils = XmlUtils.getInstance();

		System.out.println("creating helper");
		xhelper = new XmlEditorHelper();
		System.out.println("created helper");

		init();

		xmlFile = fileName;

	} // end XmlEditor( String title, String xml )

	private XmlEditor(String title) throws ParserConfigurationException {
		super(title);
		System.out.println("creating helper");
		xhelper = new XmlEditorHelper();
		System.out.println("created helper");

		xutils = XmlUtils.getInstance();

		// OV a 24/03/08
		_actions = new XmlEditorActions(this);

		// ov added for undo/redo
		fileToFrameMap = new Hashtable();

		init();

	} // end XmlEditor( String title)

	public static XmlEditor getInstance(String title) {

		try {
			if (xmlEditor == null) {
				xmlEditor = new XmlEditor(title);
				return xmlEditor;
			}
		} catch (Exception e) {
			logger.error("Couldn't start XmlEditor 1.0" + e.getMessage());
			e.printStackTrace();
		}
		return xmlEditor;
	}

	/***
	 * When a user event occurs, this method is called. If the action performed
	 * was a click of the "Refresh" button, then the XTree object is updated
	 * using the current XML text contained in the JTextArea
	 */

	public void actionPerformed(ActionEvent ae) {
		if (ae.getActionCommand().equals("Refresh"))
			textArea.getText();

		// System.out.println(" action source = " + ae.getSource());
		optionPane.setValue(okButton);

		// xTree.refresh( textArea.getText() );
	} // end actionPerformed()

	public void init() throws ParserConfigurationException  {
        
        setVisible(false);
        
 		logger.info(" creating helper");
        
		logger.log(OutputLevel.Output, "This is a output");

        loadResources();
        
        prefs = new Properties();
        for (Enumeration keys = preferences.getKeys(); keys.hasMoreElements();) {
            final String key = (String) keys.nextElement();
            final String value = preferences.getString(key);
            prefs.put(key, value);
        }
 
 // PREREL M TO ADD ALL KEY-VALS TO PREFS
        for (Enumeration keys = resources.getKeys(); keys.hasMoreElements();) {
            final String key = (String) keys.nextElement();
            final String value = resources.getString(key);
            prefs.put(key, value);
        }


        // Important!!! needed for enabling/disabling catalogs
        xutils.enableCatalogs(prefs);
        
        String valsave = prefs.getProperty("showsplash");
        
  //      System.out.println(" %%%%%%%%%%%%%%% prefs %%%%%%%%  = "  + prefs);
        
   //     System.out.println(" splash show = "  + valsave);
        
        
        boolean showSplash = new Boolean(valsave).booleanValue();
        
   //     System.out.println(" splash show 1= "  + showSplash);
        
        if(showSplash)
            xhelper.startSplashScreen();
        
        xhelper.showSplashStatus("Loading resources...");
        
            /* OV c 20/03/08 begin
        props = new Properties ();
        for (Enumeration keys = resources.getKeys (); keys.hasMoreElements ();)
        {
            final String key = (String) keys.nextElement ();
            final String value = resources.getString (key);
            props.put (key, value);
        }
             */
        //OV 19/03/08 add 1 line
        props = xutils.getProps();
        
        
        xhelper.showSplashStatus("Loaded resources...");
        
        xhelper.showSplashStatus("setting Look and feel");
        
        try {
            
//		String lf = resources.getString ("lookAndFeel");
            //		if(lf != null && lf.length() != 0) {
            //		System.out.println("lookAndFeel= " + lf);
            //  		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //	 UIManager.put("ClassLoader", LookUtils.class.getClassLoader());
            //		UIManager.setLookAndFeel(lf);
            //	  UIManager.setLookAndFeel(new WindowsLookAndFeel());
            
      //OV      PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
            //	UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		//	com.jgoodies.looks.plastic.PlasticLookAndFeel.setPlasticTheme(new DesertBlue());

           System.out.println("lookAndFeel= jg ");
			logger.info(" lookAndFeel= jg ");

     //OV       UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
              
			//	UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticXPLookAndFeel());
	
				// UIManager.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
				// UIManager.setLookAndFeel("org.fife.plaf.OfficeXP.OfficeXPLookAndFeel");
				//UIManager.setLookAndFeel("org.fife.plaf.VisualStudio2005.VisualStudio2005LookAndFeel");
  
		// PREREL A FR TATTOO LAF
			String lf = resources.getString ("lookAndFeel");
            		if(lf != null && lf.length() != 0) {
            		System.out.println("lookAndFeel= " + lf);
			   UIManager.setLookAndFeel(lf);
            }
		//	   UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");



            // 	}
            
            //    MetalLookAndFeel.setCurrentTheme(new AquaTheme());
            
            SwingUtilities.updateComponentTreeUI(this);
            
        } catch(Exception e) {
            System.out.println("lookAndFeel= ");
            logger.error("unsupported L&F");
        
        }
        
        
        
        try {
            
       //     System.out.println(" colWidthTypes.FRAME_IMAGE = " + ColWidthTypes.FRAME_IMAGE);
            
            URL url = xutils.getResource(ColWidthTypes.FRAME_IMAGE);
            
         //   System.out.println(" logo url = " + url);
            
            if (url != null)
                setIconImage(new ImageIcon(url).getImage());
            
            
            //	WindowUtilities.setNativeLookAndFeel();
            
            
/*
        //create Skin Look and Feel object begin
 
                Skin newLaf = SkinLookAndFeel.loadThemePack (SKIN_THEMEPACK);
                SkinLookAndFeel.setSkin(newLaf);
 
                UIManager.setLookAndFeel(new SkinLookAndFeel());
 
        //create Skin Look and Feel object end
 */
                        
            // needed for skin laf
            SwingUtilities.updateComponentTreeUI(this);
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        
     //     desktopView = new TabbedDesktopView(true);
     //           explorer = new ExaltoExplorer(this, desktopView);
	  
        menuItems = new Hashtable();
        
        tBar = createToolBar();
        
        logger.debug("Inside XmlEditor::after createtoolbar");
        
        
        tBarPanel = new JPanel();
        tBarPanel.setLayout(new BorderLayout());
        
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        
        popupMenu = new JPopupMenu("Exalto XML Editor");
        
        
        popupMenu.setPopupSize(150,150);
        
        
        // Action and mouse listener support
        //		enableEvents( AWTEvent.MOUSE_EVENT_MASK );
        
        //	desktopView.addPopup(popupMenu);
        addMouseListener(new MousePopupListener(desktopView, popupMenu));
	      
        xhelper.showSplashStatus("creating status bar");
        
        statusBar = createStatusBar();
        
        //	System.out.println("########################ADDSTATUS###################");
        addStatusListener(this);
        
        logger.info("Inside XmlEditor::before createmenubar");
        
        xhelper.showSplashStatus("Loading menus");
        
        // Add the split pane to the frame
        //OV c 11/07/08
        
        
        // Set the component to show the popup menu.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        
        JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());

        JPanel barPane = new JPanel();
		barPane.setLayout(new BorderLayout());


        logger.info("Inside XmlEditor::xhelper status menu");
		
			
	//		String app_type = xutils.getResourceString("CONSOLE_APPENDER_TYPE");

	//		logger.info("Inside XmlEditor:::::::::::::::::: app_type = " + app_type);
			
	//		if(app_type == null || app_type.equals("INLINE"))
	//			appenderUI = SwingAppenderUI.getUIInstance();

	
		//Create a split pane with the two scroll panes in it.
		//	editorPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        //						desktopView, new Console(desktopView.getWidth(), this));
    
		//editorPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
        //						desktopView, appenderUI);
    
 //   editorPane.setOneTouchExpandable(true);
 //       editorPane.setDividerLocation(300);
//		editorPane.setBorder(null);

		//Create a split pane with the two scroll panes in it.
    //    splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
    //                               explorerPane, editorPane);

     // OV c 190309
    //    derivoBar = createDerivoToolBar();
	 
		barPane.add( "North", createMenuBar());

        tBarPanel.add("North", tBar);
     //   tBarPanel.add("Center", derivoBar);

		barPane.add( "Center", tBarPanel);

		mainPane.add( "North", barPane);
		mainPane.add( "Center", contentPane);
		mainPane.add( "South", statusBar);

        
        CControl control = new CControl(this);
        
		xhelper.showSplashStatus("initializing toolbar");
		
		CGrid  grid = new CGrid (control) ;
		grid.add(  0 ,  0,  1 ,  1 , this.createDockable("explorer")) ;
		grid.add(  1 ,  0,  4 ,  1 , this.createDockable("desktop")) ;
		
		control.getContentArea().deploy( grid );
      
		mainPane.add(control.getContentArea());
		
		this.getContentPane().add(mainPane);
		
        logger.debug("Inside XmlEditor::xhelper status toolbar");
        
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        addWindowListener(new WindowAdapter() {
            
            public void windowClosing(WindowEvent e) {
                if (checkQuit()) {
                    System.exit(0);
                }
            }
            
                /*
                                                        public void windowActivated(WindowEvent e) {
                //					     frame.textPane.requestFocus();
                                                                 updateOpenFiles();
                                                        }
                                        }
                 */
            
            public void windowDeiconified(WindowEvent e) {
                updateOpenFiles();
            }
        });
        
        xhelper.showSplashStatus("configuring drag and drop");
        
        EditorDropTarget2 target = new EditorDropTarget2(this);
        
//		xhelper.showSplashStatus("initialized menus");
        
        
        xutils.centerDialog(this);
        try {
            Thread.sleep(500);
        } catch(InterruptedException e) {
            
        }
        
        logger.debug("Inside XmlEditor::xhelper closing");
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        //	Once you have the screen width and height, you can simply subtract the
        //	window width and height from the screen width and height and divide it in half.
        
        xhelper.closeSplash();
        this.setSize(815, 600);
        
//		Calculate the frame location
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        
//		Set the new frame location
        this.setLocation(x, y);
        
        //	validate();
        setVisible(true);
//		JTip tip = new JTip( "resources\\tips.xml" );
        
        
        //OM31082015
        GraphicsEnvironment env =
        		GraphicsEnvironment.getLocalGraphicsEnvironment();
        		        this.setMaximizedBounds(env.getMaximumWindowBounds());
        		        this.setExtendedState(this.getExtendedState() | this.MAXIMIZED_BOTH);
        
        logger.debug("Inside XmlEditor::Tip created");
        
//		if(tip.isShowOnStart())
//    		tip.showTips();
        
        
        
    }

	 public SingleCDockable  createDockable(String item) {

		 JComponent j = null;
		 SingleCDockable  dockable = null;
		 
		 if(item.equals("menu")) {
			 JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		     popupMenu = new JPopupMenu("Exalto XML Editor");
		     popupMenu.setPopupSize(150,150);

			 j = createMenuBar();
			 dockable = new DefaultSingleCDockable(item, item, j);
			 
			 
             
		 } else if(item.equals("toolbar")) {
			 j = createToolBar();
			 tBar = (ToolBar) j;			 
			 dockable = new DefaultSingleCDockable(item, item, j);

		 } else if(item.equals("desktop")) {
		     j = new TabbedDesktopView(true);
			 desktopView = (DesktopView) j;
			 dockable = new DefaultSingleCDockable(item, item, j);
		        
		        // Action and mouse listener support
		        //		enableEvents( AWTEvent.MOUSE_EVENT_MASK );
		        
		  //   desktopView.addPopup(popupMenu);
		     addMouseListener(new MousePopupListener(desktopView, popupMenu));

		 } else if(item.equals("explorer")) {
	          j = new ExaltoExplorer(this, desktopView);
			  explorer = (ExaltoExplorer) j;			  
   			  dockable = new DefaultSingleCDockable(item, item, j);
   			
		 } else if(item.equals("statusbar")) {
		        j = createStatusBar();
		        statusBar = (StatusBar) j;
				dockable = new DefaultSingleCDockable(item, item, j);
			    addStatusListener(this);
 			 
		 }

		 return dockable;
		 
 }
	
	// PREREL A FR DESKTOPVIEW POPUP
	// An inner class to check whether mouse events are the popup trigger
	class MousePopupListener extends MouseAdapter {

		JPopupMenu pmenu;

		public MousePopupListener(DesktopView desktopView, JPopupMenu menu) {
			pmenu = menu;

		}

		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		private void checkPopup(MouseEvent e) {

			System.out.println("##### ct checkPopup");

			if (e.isPopupTrigger()) {

				System.out.println("##### ct isPopupTrigger###");
				System.out.println("##### ct isPopupTriggercoo###"
						+ e.getComponent().getClass().getName());

				System.out.println("##### desktopView getLocationOnScreen### x"
						+ desktopView.getLocationOnScreen().x);
				System.out.println("##### desktopView getLocationOnScreen### y"
						+ desktopView.getLocationOnScreen().y);

				System.out.println("##### ct getLocationOnScreen### x"
						+ e.getComponent().getLocationOnScreen().x);
				System.out.println("##### ct getLocationOnScreen### y"
						+ e.getComponent().getLocationOnScreen().y);

				System.out.println("##### ct getMousePosition "
						+ desktopView.getMousePosition());

				System.out.println("##### ct getLoc### x" + e.getX());
				System.out.println("##### ct getLoc### y" + e.getY());

				pmenu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private ToolBar createToolBar() {
		return new ToolBar(this);
	}

	/**
	 * Create a status bar
	 */
	protected StatusBar createStatusBar() {
		// need to do something reasonable here
		statusBar = new StatusBar();

		/*
		 * statusBar = new JPanel();
		 * 
		 * PercentLayout statusLay = new PercentLayout(PercentLayout.HORIZONTAL,
		 * 5); // statusLay.DEBUG = true;
		 * 
		 * statusBar.setLayout(statusLay);
		 * 
		 * _status1 = new JLabel();
		 * 
		 * _status2 = new JLabel(); _status2.setPreferredSize(new
		 * Dimension(100,20));
		 * 
		 * _status3 = new JLabel(); statusBar.add(_status1,new
		 * PercentLayout.Constraints(60,PercentLayout.BOTH));
		 * statusBar.add(_status2,new
		 * PercentLayout.Constraints(20,PercentLayout.BOTH));
		 * statusBar.add(_status3,new
		 * PercentLayout.Constraints(20,PercentLayout.BOTH));
		 * 
		 * _status1.setBorder(new BevelBorder(BevelBorder.LOWERED));
		 * _status2.setBorder(new BevelBorder(BevelBorder.LOWERED));
		 * _status3.setBorder(new BevelBorder(BevelBorder.LOWERED));
		 * 
		 * statusBar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1)); // new
		 * EmptyBorder(2,2,2,2)); //_statusPanel.setInsets(new Insets(2,2,2,2));
		 * 
		 * // this.add(statusBar, BorderLayout.SOUTH);
		 */

		return statusBar;
	}

	/**
	 * Take the given string and chop it up into a series of strings on
	 * whitespace boundries. This is useful for trying to get an array of
	 * strings out of the resource file.
	 */
	protected String[] tokenize(String input) {

		if (input == null)
			return null;

		Vector v = new Vector();
		StringTokenizer t = new StringTokenizer(input);
		String cmd[];

		while (t.hasMoreTokens())
			v.addElement(t.nextToken());
		cmd = new String[v.size()];
		for (int i = 0; i < cmd.length; i++)
			cmd[i] = (String) v.elementAt(i);

		return cmd;
	}

	protected JMenuBar createMenuBar() {
		JMenuBar mb = new JMenuBar();

		// OM31082015
		Font f = new Font("sans-serif", Font.PLAIN, 12);
		UIManager.put("Menu.font", f);

		String[] menuKeys = tokenize(xutils.getResourceString("menubar"));

		for (int i = 0; i < menuKeys.length; i++) {

			JMenu m = createMenu(menuKeys[i]);

			if (m != null) {
				mb.add(m);
			}
		}

		// mb.add(new WindowMenu(desktopView));

		menuKeys = new String[1];
		menuKeys[0] = xutils.getResourceString("help.menu");
		JMenu jm = createMenu(menuKeys[0]);
		if (jm != null) {
			mb.add(jm);
		}

		return mb;

	}

	protected JMenu createMenu(String key) {
		JMenu menu = null;
		boolean popupExists = false;

		String[] itemKeys = tokenize(xutils.getResourceString(key));
		String strPopup = xutils.getResourceString(key + "Popup");

		// System.out.println(" in createmenu key = " + key);
		// System.out.println(" in createmenu strpopup = " + strPopup);

		String[] popupKeys = null;
		if (strPopup != null)
			popupKeys = tokenize(strPopup);

		if (popupKeys != null && popupKeys.length > 0)
			popupExists = true;

		menu = new JMenu(xutils.getResourceString(key + "Label"));

		// System.out.println(" key = " + key);

		// OV c 260309 for no grid menu
		// if(key.equals("grid")) {
		// gridMenu = menu;
		// gridMenu.setEnabled(false);
		// }

		for (int i = 0; i < itemKeys.length; i++) {

			String itemKey = itemKeys[i];

			// System.out.println(" CMI itemKey = " + itemKey);

			String[] subItemKeys = tokenize(xutils.getResourceString(itemKey));

			if (subItemKeys != null && subItemKeys.length > 0) {

				JMenu itemMenu = new JMenu(xutils.getResourceString(itemKey
						+ "Label"));

				for (int k = 0; k < subItemKeys.length; k++) {

					// System.out.println(" CMI subkey = " + subItemKeys[k]);

					if (subItemKeys[k].equals("-")) {
						itemMenu.addSeparator();
					} else {

						JMenuItem mi = createMenuItem(subItemKeys[k], true);

						itemMenu.add(mi);

						String accel = xutils.getResourceString(itemKey
								+ suffixSuffix);

						itemMenu.setMnemonic(ExaltoResource.getKeyEvent(accel));

					}

				}

				menu.add(itemMenu);

			} else {

				if (itemKeys[i].equals("-")) {
					menu.addSeparator();
				} else {

					JMenuItem mi = createMenuItem(itemKeys[i], true);

					menu.add(mi);

					String accel = xutils.getResourceString(key + suffixSuffix);

					menu.setMnemonic(ExaltoResource.getKeyEvent(accel));

				}

			}
		}

		if (popupExists) {
			// System.out.println("popupExists 1 = ");

			for (int j = 0; j < popupKeys.length; j++) {
				if (popupKeys[j].equals("-")) {
					popupMenu.addSeparator();
				} else {
					JMenuItem mi = createMenuItem(popupKeys[j], false);

					// System.out.println("popupKeys[j]= "+popupKeys[j]);

					popupMenu.add(mi);
				}
			}
		}

		return menu;
	}

	protected JMenuItem createMenuItem(String cmd, boolean setMnemonic) {

		try {
			// System.out.println("cmd = " + cmd);

			String lblSuffix = xutils.getResourceString(cmd + labelSuffix);
			String mnemonicSuffix = xutils
					.getResourceString(cmd + suffixSuffix);

			// System.out.println("lblsuffix = " + lblSuffix);
			JMenuItem mi = new JMenuItem(lblSuffix, lblSuffix.charAt(0));

			URL url = xutils.getResource(cmd + imageSuffix);

			// System.out.println(" menu url = " + url);

			if (url != null) {
				mi.setHorizontalTextPosition(JButton.RIGHT);
				mi.setIcon(new ImageIcon(url));
				// addspace = new Boolean(true);
			} else
				mi.setIcon(xutils.getEmptyIcon());

			// else {
			// if(addspace.booleanValue())
			// mi.setIcon(xutils.getEmptyIcon());
			// }

			String accel = xutils.getResourceString(cmd + accelSuffix);

			// System.out.println(" accel = " + accel);

			String astr = xutils.getResourceString(cmd + actionSuffix);

			if (astr == null) {
				astr = cmd;
			}

			mi.setActionCommand(astr);

			// OV c 24/03/08
			// Action a = getAction(astr);

			if (astr.startsWith("gridops")) {
				int hpos = astr.indexOf('-');
				if (hpos > 0)
					astr = astr.substring(0, hpos);
			}

			// System.out.println(" astr = " + astr);

			Action a = _actions.getAction(astr);

			// System.out.println(" act cmd = " + a);

			if (a != null) {

				if (mi.getText().equals("Getting Started")) {

					// Defaults for Main Help
					// static final String helpsetName = "IdeHelp";

					// Main HelpSet & Broker
					// HelpSet mainHS = null;
					// HelpBroker mainHB;

					try {
						// ClassLoader cl = XmlEditor.class.getClassLoader();
						// URL url = HelpSet.findHelpSet(cl, helpsetName);
						// mainHS = new HelpSet(cl, url);
					} catch (Exception ee) {
						// System.out.println
						// ("Help Set "+helpsetName+" not found");
						return null;
					} catch (ExceptionInInitializerError ex) {
						System.err.println("initialization error:");
						ex.getException().printStackTrace();
					}

					// mainHB = mainHS.createHelpBroker();

					// new javax.help.CSH.DisplayHelpFromSource(mainHB);

					/*
					 * 
					 * mi.addActionListener(new ActionListener() { public void
					 * actionPerformed(ActionEvent evt) { doHelp(); } });
					 */

				} else {

					a.putValue(ACTION_MENU_ACCELERATOR,
							ExaltoResource.getKeyStroke("UI", accel));

					// System.out.println(" mi text = " + mi.getText());

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) a
								.getValue(ACTION_MENU_ACCELERATOR));
					Integer i = (Integer) a.getValue(MNEMONIC_KEY);

					if (i != null) {
						mi.setMnemonic(i.intValue());
					}

					mi.addActionListener(a);

				}

				a.addPropertyChangeListener(createActionChangeListener(mi));

				// System.out.println(" a is enabled = " + a.isEnabled());

				mi.setEnabled(a.isEnabled());

			} else {

				if (mi.getActionCommand().equals("get-started")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							// doHelp();
						}
					});
					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("show-options")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doPrefs();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));
					mi.setEnabled(true);
				} else if (mi.getText().equals("Show in TreeView")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							try {
								doView(true);
							} catch (Exception e) {
							}
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("xslt-transform")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doFO();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("generate-pdf")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doPdf();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("auto-table-layout")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doTableLayout();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getText().equals("Show Log")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doShowLog();
						}
					});
					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("about")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doAbout();
						}
					});
					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("check-well-formed")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doCheckWellFormed();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("check-validity")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doCheckValidity();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("assign-stylesheet")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doAssignStyleSheet();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("pretty-print")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doPrettyPrint();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("save-as")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doSaveAs();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("save")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doSave();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("close")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doClose();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("goto-line")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doGoTo();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("assign-dtd")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doAssignDTD();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("assign-schema")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							doAssignSchema();
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("textview")) {

					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							showTextView(evt);
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				} else if (mi.getActionCommand().equals("gridview")) {
					mi.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							showGridView(evt);
						}
					});

					if (setMnemonic)
						mi.setAccelerator((KeyStroke) ExaltoResource
								.getKeyStroke("UI", accel));

					mi.setEnabled(true);
				}

				else {
					// if(!mi.getText().equals("Getting started"))
					mi.setEnabled(false);
				}

			}
			menuItems.put(cmd, mi);
			return mi;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	protected URL getResource(String key) {

		// System.out.println(" getResource key  = " + key);

		// String name = getResourceString(key);

		URL url = xutils.getResource(key);
		return url;

		/*
		 * System.out.println(" getResourceString ret  = " + name);
		 * 
		 * if (name != null) { URL url = this.getClass().getResource(name);
		 * return url; } return null;
		 */

	}

	/**
	 * Fetch the list of actions supported by this editor. It is implemented to
	 * return the list of actions supported by the embedded JTextComponent
	 * augmented with the actions defined locally.
	 */
	/*
	 * OV c 24/03/08 public Action[] getActions() { DocumentPane je = new
	 * DocumentPane(); //OV c 21/03/08 // return
	 * TextAction.augmentList(je.getActions(), defaultActions); return
	 * defaultActions;
	 * 
	 * }
	 */

	protected String getResourceString(String nm) {
		String str;
		try {
			str = resources.getString(nm);
		} catch (MissingResourceException mre) {
			str = null;
		}

		return str;
	}

	/*
	 * OV c 24/03/08
	 * 
	 * protected Action getAction(String cmd) { return (Action)
	 * commands.get(cmd); }
	 */

	/**
	 * Checks to see which tabbed pane was selected by the user. If the XML and
	 * XSL panes hold a document, then selecting the XSLT tab will perform the
	 * transformation.
	 */

	public void stateChanged(ChangeEvent event) {
		int index = tabbedPane.getSelectedIndex();

		switch (index) {
		case FO:
			if (documents[XHTML].isLoaded() && documents[XSL].isLoaded()) {
				doFO();
			}
		case XHTML:
		case XSL:
			updateMenuAndTitle();
			break;
		default:
		}
	}

	/*
	 * public void processMouseEvent( MouseEvent event ) { if(
	 * event.isPopupTrigger() ) { popupMenu.show( event.getComponent(),
	 * event.getX(), event.getY() ); }
	 * 
	 * super.processMouseEvent( event ); }
	 */

	// This one listens for edits that can be undone.
	public class MyUndoableEditListener implements UndoableEditListener {
		public void undoableEditHappened(UndoableEditEvent e) {
			// Remember the edit and update the menus
			System.out.println(" undo happend");

			// undo.addEdit(e.getEdit());
			desktopView.getCurrentUndoManager().addEdit(e.getEdit());
			resetUndoAction(desktopView.getCurrentUndoManager());
			resetRedoAction(desktopView.getCurrentUndoManager());

			// undoAction.update();
			// redoAction.update();
		}
	}

	/**
	 * Update the title of the application to present the name of the file
	 * loaded into the selected tabbed pane. Also, update the menu options
	 * (Save, Load) based on which tab is selected.
	 */

	public void updateMenuAndTitle() {
		// if ((index > -1) && (index < documents.length)) {

		System.out.println("openaction  = " + openAction);
		// System.out.println("curr frame  = " +
		// desktopView.getSelectedFrame());
		// System.out.println("text comp  = " +
		// ((MyInternalFrame)desktopView.getSelectedFrame())
		// .getTextComponent());

		// System.out.println("menu title sel frame  = " +
		// desktopView.getSelectedFrame());

		/*
		 * ov commented 270409
		 * if((MyInternalFrame)desktopView.getSelectedFrame() != null)
		 * _actions.getAction(openAction).setEnabled( ((JTextComponent)(
		 * (MyInternalFrame)desktopView.getSelectedFrame())
		 * .getTextComponent()).isEditable());
		 */

		// getAction(saveAction).setEnabled(documents[index].isLoaded());
		// getAction(openAction).setEnabled(documents[index].isEditable());

		String title = DEFAULT_TITLE;
		// String filename = documents[index].getFilename();

		String filename = ((Editor) ((MyInternalFrame) desktopView
				.getSelectedFrame()).getTextComponent()).getFile().getName();

		// System.out.println("menu title filename  = " + filename);

		if (filename.length() > 0) {
			// title += " - [" + filename + "]";
			// title = " - [" + filename + "]";
		}

		// System.out.println("menu title title  = " + title);

		// OV c 070509
		// desktopView.getSelectedFrame().setTitle(title);
		// setTitle(title);
		// }
	}

	/**
	 * Open a file dialog to either load a new file to or save the existing file
	 * in the present document pane.
	 */

	private void updateDocument(int mode) throws Exception {

		String dir = null;

		// System.out.println("idePath  = " + idePath);

		dir = getCurrentDir();

		// System.out.println(" in update doc dir = " + dir);

		// if(_current_dir == null) {
		// JFileChooser jfc = new JFileChooser(idePath + "..");
		// } else {
		// JFileChooser jfc = new JFileChooser();
		// jfc.setCurrentDirectory(_current_dir);
		// }

		JFileChooser jfc = new JFileChooser(dir);
		jfc.setCurrentDirectory(new File(dir));

		String description = "XML Files";
		String filename = ExtensionFileFilter.getFileName(dir, description,
				extensions, mode);

		// System.out.println("filename of opened file = " + filename);

		/*
		 * OV commented 31/07/2005 and moved inside filename != null) ****
		 * 
		 * long checksum = FileUtil.calculateFileChecksum(filename);
		 * 
		 * System.out.println("checksum computed for file = " + checksum);
		 * 
		 * checkSumHash.put(filename, new Long(checksum));
		 * 
		 * System.out.println("checksumhash after open = " + checkSumHash);
		 */

		// if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
		// File selectedFile = jfc.getSelectedFile();

		MyInternalFrame frame = null;
		if (filename != null) {

			File openFile = new File(filename);

			long checksum = FileUtil.calculateFileChecksum(filename);

			// System.out.println("checksum computed for file = " + checksum);

			checkSumHash.put(filename, new Long(checksum));

			// System.out.println("checksumhash after open = " + checkSumHash);

			// _current_dir = openFile;

			_current_dir = openFile.getParent();

			// System.out.println(" curr dir = " + _current_dir);

			if (!checkFileExists(openFile)) {
				desktopView.display(openFile);

				frame = (MyInternalFrame) desktopView.getSelectedFrame();

				// System.out.println("xmleditor newwidtth " +
				// frame.getSize().width);
				// System.out.println("xmleditor newheight " +
				// frame.getSize().height);

				/*
				 * ov commented 28012006 for views begin ***** Editor editor =
				 * (Editor) frame.getTextComponent();
				 * editor.addCaretListener(this);*** ov commented 28012006 for
				 * views end ****
				 */
				fileToFrameMap.put(openFile.getAbsolutePath(), frame);
			}

			updateMenuAndTitle();

			/*
			 * ov commented 28012006 for views begin *****
			 * 
			 * if(desktopView.fileSelected()) frame = (MyInternalFrame)
			 * desktopView.getSelectedFrame();
			 * 
			 * updateMenuAndTitle(); frame.addInternalFrameListener(this);
			 * 
			 * ((JTextComponent)
			 * frame.getTextComponent()).getDocument().addUndoableEditListener(
			 * new MyUndoableEditListener());*** ov commented 28012006 for views
			 * end ****
			 */

		}

		// OV c 2700309
		// if(frame.getCurrentView().equals("GRIDVIEW"))
		// gridMenu.setEnabled(true);

		// documents[index].getDocument().addUndoableEditListener(
		// new MyUndoableEditListener());

		// Bind the undo action to ctl-Z
		// documents[index].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("con//trol //pressed z"),
		// "undo");
		// Bind the redo action to ctl-Y
		// documents[index].getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("con//trol //y"),
		// "redo");

	}

	// Yarked from JMenu, ideally this would be public.
	protected PropertyChangeListener createActionChangeListener(JMenuItem b) {
		return new ActionChangedListener(b);
	}

	// Yarked from JMenu, ideally this would be public.
	private class ActionChangedListener implements PropertyChangeListener {
		JMenuItem menuItem;

		ActionChangedListener(JMenuItem mi) {
			super();
			this.menuItem = mi;
		}

		public void propertyChange(PropertyChangeEvent e) {
			// System.out.println(" inside propchange method");
			String propertyName = e.getPropertyName();
			if (e.getPropertyName().equals(Action.NAME)) {
				String text = (String) e.getNewValue();
				menuItem.setText(text);
			} else if (propertyName.equals("enabled")) {
				// System.out.println(" inside prop name Enabled");
				Boolean enabledState = (Boolean) e.getNewValue();
				menuItem.setEnabled(enabledState.booleanValue());
			}

		}
	}

	public void doPrettyPrint() {

		try {
			if (desktopView.selectedIsNew()) {
				final String text = ((MyInternalFrame) desktopView
						.getSelectedFrame()).getTextComponent().getText();
				// System.out.println(" in if calling validate text " +
				// text.length());

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						XmlEditor.this.xutils.prettyPrint(text, XmlEditor.this);
					}
				});

			} else {
				final File f = desktopView.getSelectedFile();
				// System.out.println(" file f = " + f.getPath());

				// SwingUtilities.invokeLater(new Runnable() {
				// public void run() {
				XmlEditor.this.xutils.prettyPrint(f, XmlEditor.this);
				// }
				// });

			}

			// System.out.println(" in xmle after prettyprint ");

		} catch (Exception e) {
			// setStatus("document is not well-formed: " + e.getMessage());
			fireStatusChanged(new StatusEvent(ExaltoResource.getString(
					ColWidthTypes.ERR, "well.formed.err"), 0,
					ColWidthTypes.ERROR));

		}

	}

	public void doAssignDTD() {

		int delay = 5000;
		XmlEditorDoc xdoc;

		try {

			int resp = JOptionPane.showConfirmDialog(null, ExaltoResource
					.getString(ERR, "exalto.editor.parse.announce"),
					ExaltoResource.getString(ERR, "announce.xml.parse"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (resp == JOptionPane.CANCEL_OPTION) {
				return;
			}

			// OV c 21/03/08
			// doView(false);

			xdoc = parseDocument(false);

			String rootElem = xdoc.getRootElementName();

			/*
			 * OV 21/03/08 String rootElem = null; if(xTree != null &&
			 * xTree.isInitialized()) { System.out.println(" xtree init ");
			 * rootElem = xTree.getRootElementName();
			 * System.out.println(" root elem name = " + rootElem); }
			 */

			if (desktopView.isDtdAssigned()) {
				int response = JOptionPane.showConfirmDialog(null,
						ExaltoResource.getString(ERR,
								"overwrite.dtd.assignment"), ExaltoResource
								.getString(ERR, "confirm.overwrite"),
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.CANCEL_OPTION) {
					return;
				}
			}

			xmlFileFrame = (MyInternalFrame) desktopView.getSelectedFrame();
			xmlFileFrame.setWaitingForAssign(true);
			assignStyleAction.shouldInvoke = true;
			assignStyleAction.assignType = "DTD";
			assignStyleAction.clientData = rootElem;
			timer = new Timer(delay, assignStyleAction);
			timer.start();

		} catch (Exception e) {
			fireStatusChanged(new StatusEvent(ExaltoResource.getString(
					ColWidthTypes.ERR, "err.assign.dtd"), 0,
					ColWidthTypes.ERROR));
		}

	}

	public void doAssignSchema() {

		int delay = 5000;

		int resp = JOptionPane.showConfirmDialog(null,
				ExaltoResource.getString(ERR, "exalto.editor.parse.announce"),
				ExaltoResource.getString(ERR, "announce.xml.parse"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (resp == JOptionPane.CANCEL_OPTION) {
			return;
		}

		if (desktopView.isSchemaAssigned()) {
			int response = JOptionPane.showConfirmDialog(null, ExaltoResource
					.getString(ERR, "overwrite.schema.assignment"),
					ExaltoResource.getString(ERR, "confirm.overwrite"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		xmlFileFrame = (MyInternalFrame) desktopView.getSelectedFrame();
		xmlFileFrame.setWaitingForAssign(true);
		assignStyleAction.shouldInvoke = true;
		assignStyleAction.assignType = "SCHEMA";
		timer = new Timer(delay, assignStyleAction);
		timer.start();

	}

	public void doGoTo() {
		try {

			MyInternalFrame myf = (MyInternalFrame) getDesktopView()
					.getSelectedFrame();
			Editor edtr = (Editor) myf.getTextComponent();

			String title = ExaltoResource.getString(ColWidthTypes.ERR,
					"gotoline.dialog.title"); //$NON-NLS-1$
			String message = ExaltoResource.getString(ColWidthTypes.ERR,
					"gotoline.dialog.message"); //$NON-NLS-1$

			InputDialog inp = new InputDialog(title, message); //$NON-NLS-1$
			String edline = inp.getInputValue();
			if (edline != null) {

				try {
					int line = Integer.parseInt(edline);
					edtr.gotoLine(line - 1);
				} catch (NumberFormatException x) {
				}
			}

		} catch (Exception x) {
			x.printStackTrace();
		}

	}

	public void doFind() {
		/*
		 * MyInternalFrame myf = (MyInternalFrame)
		 * getDesktopView().getSelectedFrame(); Editor edtr = (Editor)
		 * myf.getTextComponent(); findStart = myf;
		 * findStart.setProcessed(true); FindReplaceDialog fdlg = new
		 * FindReplaceDialog(this, edtr, new MyFindReplaceListener());
		 */
	}

	public void doPrefs() {

		if (dialog == null)
			dialog = new ApplicationPrefs(this);

		dialog.show();
	}

	public void doWordWrap(AbstractButton b) {
		// String WORDWRAP_IMAGEOFF = "resources\\wrap.gif";
		// String WORDWRAP_IMAGEON = "resources\\wrap2.gif";

		// ImageIcon im1 = new ImageIcon( WORDWRAP_IMAGEOFF);
		// ImageIcon im2 = new ImageIcon( WORDWRAP_IMAGEON);

		MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
		if (myf != null) {
			if (myf.getWordwrap()) {
				myf.setWordwrap(false);
			} else {
				myf.setWordwrap(true);
			}
		}
	}

	public void doShowLog() {

		logFrame = new JFrame("Log File for exalto");

		logFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent weeee) {
				logFrame.setVisible(false);
			}
		});

		Container fContentPane = logFrame.getContentPane();

		fContentPane.setLayout(new BorderLayout());

		JPanel logPanel = new JPanel();
		DocumentPane logText = new DocumentPane();

		logScroll = new JScrollPane();

		Properties p = getXmlEditor().getProperty();
		String logfile = p.getProperty("logFile");
		// System.out.println(" log file =" + logfile);
		File logFile = null;
		try {
			logFile = new File(logfile);
		} catch (Exception e) {
			logger.debug("could not read log file");
			return;
		}

		if (logFile.exists()) {
			logText.loadFile(logfile);
			// logText.setDocument(new PlainDocument());
			setTitle("Log file contents");
			// Thread loader = new FileLoader(logFile, logText.getDocument());
			// loader.start();
		}
		logText.setEditable(false);
		logScroll.getViewport().add(logText);
		// fContentPane.setLayout(new BorderLayout());
		// logPanel.add(logScroll, BorderLayout.CENTER);
		logPanel.add(logScroll);

		fContentPane.add(logPanel);
		logFrame.pack();
		xutils.centerDialog(logFrame);
		logFrame.show();

	}

	public void doHelp() {

		if (elementTreeFrame == null) {
			helpFrame = new JFrame("XML Tree View");
			helpFrame.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent weeee) {
					helpFrame.setVisible(false);
				}
			});

			Container fContentPane = helpFrame.getContentPane();

			fContentPane.setLayout(new BorderLayout());

			JPanel helpPanel = new JPanel();
			helpPanel.add(new JScrollPane(xTree), BorderLayout.CENTER);

			fContentPane.add(helpPanel);
			helpFrame.pack();
		}

		helpFrame.show();
	}

	public void doOpen() {

		try

		{
			updateDocument(ExtensionFileFilter.LOAD);
			return;
			/*
			 * int index = tabbedPane.getSelectedIndex();
			 * 
			 * chooser = new JFileChooser();
			 * chooser.setDialogType(JFileChooser.OPEN_DIALOG);
			 * chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			 * 
			 * String logf = props.getProperty("logFile"); File logFile = new
			 * File(logf);
			 * 
			 * chooser.setCurrentDirectory(logFile);
			 * 
			 * xmlFilter = new ExampleFileFilter(new String [] {"xhtml", "xml"},
			 * "xhtml, xml Files"); xslFilter = new ExampleFileFilter("xsl",
			 * "XSL stylesheet files"); foFilter = new ExampleFileFilter("fo",
			 * "Formatting object Files");
			 * 
			 * 
			 * // if(fileDialog == null) { // fileDialog = new FileDialog(this);
			 * // }
			 * 
			 * // fileDialog.setMode(FileDialog.LOAD); // fileDialog.show();
			 * 
			 * // System.out.println(" index = " + index);
			 * 
			 * 
			 * if(index == 0) { System.out.println(" xml filter " );
			 * chooser.addChoosableFileFilter(xmlFilter);
			 * chooser.setAcceptAllFileFilterUsed(false); } else if(index == 1)
			 * { System.out.println(" xml filter " );
			 * chooser.addChoosableFileFilter(xslFilter); } else if(index == 2)
			 * { System.out.println(" xml filter " );
			 * chooser.addChoosableFileFilter(foFilter); }
			 * 
			 * File theFile = null; int retval = chooser.showDialog(this, null);
			 * if(retval == JFileChooser.APPROVE_OPTION) { theFile =
			 * chooser.getSelectedFile(); if(theFile != null) { readFile =
			 * theFile.getName(); if(readFile.endsWith(".xml") ||
			 * readFile.endsWith(".xhtml") ) { fileOpened = 1; fmtObjFile =
			 * null; pdfFile = null; xmlFile = readFile; } else
			 * if(readFile.endsWith(".fo")) { fileOpened = 2; xslFile =
			 * theFile.getPath(); pdfFile = null; xmlFile = null; fmtObjFile =
			 * null; System.out.println("in 2"); System.out.println("xsl file" +
			 * xslFile); } else if(readFile.endsWith(".fo")) { fileOpened = 3;
			 * fmtObjFile = theFile.getPath(); pdfFile = null; xmlFile = null;
			 * xslFile = null; System.out.println("in 3");
			 * System.out.println("fmt file" + fmtObjFile); } else { //
			 * setStatus("please open an xml file"); fireStatusChanged(new
			 * StatusEvent
			 * (ExaltoResource.getString(colWidthTypes.ERR,"open.xml.file.first"
			 * ),0, colWidthTypes.ERROR));
			 * 
			 * logger.error("Incorrect file format"); return; }
			 * 
			 * } } else { logger.info("File open cancelled"); return; }
			 * 
			 * 
			 * // xmlFile = fileDialog.getFile(); // xmlFileDir =
			 * fileDialog.getDirectory();
			 * 
			 * // System.out.println("file opened" + xmlFile);
			 * 
			 * // if (xmlFile == null) { // return; // }
			 * 
			 * 
			 * // File inputFile = new File(xmlFileDir, xmlFile);
			 * 
			 * 
			 * // String infile = ""; // if(fileOpened == 1) { // infile =
			 * xmlFile; // } else if(fileOpened == 2) { // infile = fmtObjFile;
			 * // }
			 * 
			 * System.out.println("infile canonical exists" +
			 * theFile.getAbsoluteFile());
			 * 
			 * if (theFile.getAbsoluteFile().exists()) {
			 * System.out.println("infile exists");
			 * 
			 * documents[fileOpened].loadFile(theFile.getAbsoluteFile().getPath()
			 * ); // documents[fileOpened].setEditable(false);
			 * 
			 * 
			 * // Document oldDoc = textArea.getDocument(); // Document oldDoc =
			 * documents[index].getDocument();
			 * 
			 * // if(oldDoc != null) { //
			 * oldDoc.removeUndoableEditListener(undoHandler); // }
			 * 
			 * 
			 * // textArea.setDocument(new PlainDocument()); //
			 * documents[index].setDocument(new PlainDocument());
			 * setTitle(readFile); // Thread loader = new FileLoader(theFile,
			 * documents[index].getDocument()); // loader.start(); }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Could not open file" + e.getMessage());
		}

	}

	public void doCheckWellFormed() {

		String text = null;
		String rootElem = null;
		XmlEditorDoc xdoc;

		try {
			addStatusMouseListener();

			/*
			 * OV 23/01/08 doView(false); System.out.println(" past doview wf");
			 * 
			 * System.out.println(" xtree not null "); if(xTree != null &&
			 * xTree.isInitialized()) { System.out.println(" xtree init ");
			 * schema = xTree.getSchemaFile();
			 * System.out.println(" root schema = " + schema); }
			 */

			xdoc = parseDocument(false);

			rootElem = xdoc.getRootElementName();

			Editor editor = (Editor) ((MyInternalFrame) desktopView
					.getSelectedFrame()).getTextComponent();

			if (desktopView.selectedIsNew() || editor.isDirty()) {
				text = ((MyInternalFrame) desktopView.getSelectedFrame())
						.getTextComponent().getText();
				System.out.println(" in if calling validate text "
						+ text.length());
				xutils.validate(text, this, false, null);
				// setStatus("Document is well-formed");
				// fireStatusChanged(new
				// StatusEvent(ExaltoResource.getString(colWidthTypes.ERR,"doc.well.formed"),0,
				// colWidthTypes.VALIDATION));

			} else {
				File f = desktopView.getSelectedFile();
				System.out.println(" file f = " + f.getPath());
				xutils.validate(f, this, false, null); // checking validity =
														// false
				// setStatus("Document is well-formed");
				fireStatusChanged(new StatusEvent(ExaltoResource.getString(
						ColWidthTypes.ERR, "doc.well.formed"), 0,
						ColWidthTypes.NOERROR));

			}
		} catch (ParserConfigurationException pce) {
			String[] margs = new String[1];
			margs[0] = pce.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.ERROR));
		} catch (SAXParseException spe) {
			String[] margs = new String[1];
			margs[0] = spe.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.ERROR));

		} catch (SAXException se) {
			System.out
					.println("&&&&&&&&&&LOGGER LOGGER LOGGER=%%%%%%%%%%%%%%%%%%%%"
							+ logger);
			String[] margs = new String[1];
			margs[0] = se.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.ERROR));
		} catch (IOException ioe) {
			String[] margs = new String[1];
			margs[0] = ioe.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.file.io.err"),
					margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.ERROR));
		} catch (Exception e) {
			System.out.println(" excep = " + e.getMessage());
			e.printStackTrace();
			String[] margs = new String[1];
			margs[0] = e.getMessage();

			System.out.println(" parse error msg = " + e.getMessage());

			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.ERROR));
		}

		removeStatusMouseListener();

	}

	public void doCheckValidity() {

		System.out.println(" in do check valid ");
		String text = null;
		File f = null;
		String gmrFile = null;
		String rootElem = null;
		XmlEditorDoc xdoc;

		try {
			// do this first to get schema file

			// first addMouseListener to statusBar
			addStatusMouseListener();

			// OV c 21/03/08
			// doView(false);
			xdoc = parseDocument(true);

			rootElem = xdoc.getRootElementName();

			boolean hasDoctype = xdoc.hasDocType();

			if (!hasDoctype) {
				gmrFile = xdoc.getSchemaFile();
			}

			/*
			 * OV c 21/03/08 System.out.println(" past doview "); if(xTree ==
			 * null) { fireStatusChanged(new StatusEvent("well.formed.err",0,
			 * ColWidthTypes.ERROR)); }
			 */

			/*
			 * OV c 21/03/08 System.out.println(" xtree not null "); if(xTree !=
			 * null && xTree.isInitialized()) {
			 * System.out.println(" xtree init "); schema =
			 * xTree.getSchemaFile(); System.out.println(" root schema = " +
			 * schema); }
			 */
			Editor editor = (Editor) ((MyInternalFrame) desktopView
					.getSelectedFrame()).getTextComponent();

			if (desktopView.selectedIsNew() || editor.isDirty()) {
				System.out.println(" in if ");
				text = ((MyInternalFrame) desktopView.getSelectedFrame())
						.getTextComponent().getText();
				// xutils.validate(text, this, true);

				xutils.validate(text, this, true, gmrFile); // checking validity
															// = true

			} else {
				f = desktopView.getSelectedFile();
				/*
				 * doView(false);
				 * 
				 * System.out.println(" past doview "); if(xTree == null) {
				 * fireStatusChanged(new StatusEvent("well.formed.err",0,
				 * colWidthTypes.ERROR)); }
				 * 
				 * System.out.println(" xtree not null "); if(xTree != null &&
				 * xTree.isInitialized()) { System.out.println(" xtree init ");
				 * schema = xTree.getSchemaFile(); System.out.println(" root = "
				 * + schema); }
				 */

				System.out.println(" file f = " + f.getPath());
				// xutils.validate(f, this, true, schema);
				xutils.validate(f, this, true, gmrFile); // checking validity =
															// true
				// schema = schema file
			}

		} catch (ParserConfigurationException pce) {

			System.out.println(" XMLE PCE");
			pce.printStackTrace();

			String[] margs = new String[1];
			margs[0] = pce.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0,
					ColWidthTypes.VALIDATION_ERROR_STATUS));
		} catch (SAXParseException spe) {
			System.out.println(" XMLE SPE");

			spe.printStackTrace();
			String[] margs = new String[1];
			margs[0] = spe.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0,
					ColWidthTypes.VALIDATION_ERROR_STATUS));

		} catch (SAXException se) {
			System.out
					.println("&&&&&&&&&&LOGGER LOGGER LOGGER=%%%%%%%%%%%%%%%%%%%%"
							+ logger);
			se.printStackTrace();
			String[] margs = new String[1];
			margs[0] = se.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.io.err"), margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.VALIDATION));
		} catch (IOException ioe) {
			System.out.println(" XMLE IOE");

			String[] margs = new String[1];
			margs[0] = ioe.getMessage();
			String ques = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "sax.parse.file.io.err"),
					margs);
			fireStatusChanged(new StatusEvent(ques, 0, ColWidthTypes.VALIDATION));
		} catch (Exception e) {
			e.printStackTrace();
		}

		removeStatusMouseListener();

	}

	public void doAssignStyleSheet() {

		int delay = 5000;

		if (desktopView.isStyleAssigned()) {
			int response = JOptionPane.showConfirmDialog(null, ExaltoResource
					.getString(ERR, "overwrite.stylesheet.assignment"),
					ExaltoResource.getString(ERR, "confirm.overwrite"),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (response == JOptionPane.CANCEL_OPTION) {
				return;
			}
		}

		xmlFileFrame = (MyInternalFrame) desktopView.getSelectedFrame();
		xmlFileFrame.setWaitingForAssign(true);
		assignStyleAction.shouldInvoke = true;
		assignStyleAction.assignType = "STYLESHEET";
		timer = new Timer(delay, assignStyleAction);
		timer.start();
		/*
		 * int response = JOptionPane.showConfirmDialog(null,
		 * "Would you like to assign a stylesheet already open in the editor." +
		 * "If yes, click cancel and select the frame that has the stylesheet open"
		 * , "Click ok to browse for an xslt stylesheet",
		 * JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		 * 
		 * if( response == JOptionPane.CANCEL_OPTION) { xmlFileFrame =
		 * (MyInternalFrame) desktopView.getSelectedFrame(); waitingToAssign =
		 * true; return; }
		 */

		// if(!desktopView.selectedHasFileName()) {

	}

	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		// System.out.println(" in prop change prop = " + prop);

		if (isVisible()
				&& (e.getSource() == optionPane)
				&& (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY
						.equals(prop))) {

			// System.out.println(" in isvisible");

			// System.out.println(" Assign type = " +
			// optionPane.getClientProperty("ASSIGN_TYPE"));
			String mode = (String) optionPane.getClientProperty("ASSIGN_TYPE");
			String rootElem = (String) optionPane
					.getClientProperty("CLIENT_DATA");

			if (mode == null) {
				return;
			}

			Object value = optionPane.getValue();

			// System.out.println(" value = " + value);

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				// ignore reset
				return;
			}

			// Reset the JOptionPane's value.
			// If you don't do this, then if the user
			// presses the same button next time, no
			// property change event will be fired.
			// optionPane.setValue(
			// JOptionPane.UNINITIALIZED_VALUE);

			if (okButton.equals(value)) {
				if (xslFileName.getText() != null
						&& xslFileName.getText().length() > 0) {

					String selectedFile = xslFileName.getText();

					// OV added 31/07/2005 for assign dialgo reuse
					if ("STYLESHEET".intern() == mode.intern()) {

						desktopView.setStyleSheet(xslFileName.getText(), null);
						fireStatusChanged(new StatusEvent(
								ExaltoResource.getString(ColWidthTypes.ERR,
										"stylesheet.assign"), 0,
								ColWidthTypes.NOERROR));

					} else if ("DTD".intern() == mode.intern()) {
						// System.out.println(" selected dtd file name " +
						// selectedFile);
						desktopView.assignDTD(selectedFile, rootElem);
					} else if ("SCHEMA".intern() == mode.intern()) {
						// System.out.println(" selected schema file name " +
						// selectedFile);
						desktopView.assignSchema(selectedFile);
					}

				} else {
					fireStatusChanged(new StatusEvent(ExaltoResource.getString(
							ColWidthTypes.ERR, "stylesheet.not.assign"), 0,
							ColWidthTypes.ERROR));
				}
				JComponent[] jcomp = new JComponent[1];
				jcomp[0] = xslFileName;
				xutils.clearAndHide(jcomp, assignDlg);
			} else if (cancelButton.equals(value)) {
				// setStatus("Stylesheet Not assigned");
				String errmsg = ExaltoResource.getString(ColWidthTypes.ERR,
						"doc.not.assign");
				errmsg = mode + " " + errmsg;
				fireStatusChanged(new StatusEvent(errmsg, 0,
						ColWidthTypes.ERROR));
				JComponent[] jcomp = new JComponent[1];
				jcomp[0] = xslFileName;
				xutils.clearAndHide(jcomp, assignDlg);
			}

		}
	}

	public void doNew() {
		textArea.setText("");
		if (xTree != null) {
			xTree.clear();
		}

		xmlFile = null;
		elementTreeFrame.invalidate();

	}

	public void doExit() {
		System.exit(0);
	}

	public void doSave() {
		try {
			logger.debug(" in doSave");

			if (desktopView.fileSelected()) {
				// System.out.println(" file selected");
				// if (desktopView.selectedHasFileName()) {
				if (!desktopView.selectedIsNew()) {
					File file = desktopView.getSelectedFile();
					// if(!file.getName().equals("XSL result") &&
					// !file.getName().endsWith("fmt.fo")) {
					System.out.println(" file selected has file name");
					desktopView.saveSelectedFile();

					String saveFile = file.getAbsolutePath();
					long filesum = FileUtil.calculateFileChecksum(saveFile);

					System.out.println(" file saved = " + saveFile);
					System.out.println(" file checksum = " + filesum);

					checkSumHash.put(saveFile, new Long(filesum));

					System.out.println("checksumhash after  save = "
							+ checkSumHash);

				} else {
					System.out
							.println(" file selected is new.. calling save as");
					doSaveAs();
				}
			} else {
				System.out.println("Dialog Error - No file selected.");
			}

		} catch (Exception e) {
			// setStatus("unable to save document");
			fireStatusChanged(new StatusEvent(ExaltoResource.getString(
					ColWidthTypes.ERR, "file.save.err"), 0, ColWidthTypes.ERROR));
			logger.error("unable to save document");
			return;
		}

	}

	public void doSaveAs() {
		String dir = null;

		if (desktopView.fileSelected()) {
			System.out.println(" save as file selected idepath " + idePath);

			dir = getCurrentDir();

			System.out.println(" save as dir " + dir);

			JFileChooser jfc = null;
			// if(_current_dir == null) {
			// jfc = new JFileChooser(idePath);
			// } else {
			// jfc = new JFileChooser();
			// jfc.setCurrentDirectory(_current_dir);
			// }

			jfc = new JFileChooser(dir);
			jfc.setCurrentDirectory(new File(dir));

			if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
				System.out.println(" save as file name "
						+ jfc.getSelectedFile());
				File selectedFile = jfc.getSelectedFile();
				// _current_dir = selectedFile;
				if (selectedFile.exists()) {
					int response = JOptionPane.showConfirmDialog(null,
							"Overwrite existing file?", "Confirm Overwrite",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					if (response == JOptionPane.CANCEL_OPTION)
						return;
				}

				desktopView.saveSelectedFileAs(jfc.getSelectedFile());
			}
		}

		updateMenuAndTitle();
	}

	public void doAbout() {
		if (jDialog == null) {
			jDialog = new JOptionPane();
		}

		URL url = xutils.getResource(aboutAction + imageSuffix);
		// System.out.println(" about url = " + url);
		String imagesrc = "<img src=\"" + url
				+ "\" width=\"84\" height=\"80\">";
		String message = "Aatma XML Editor (Windows) <BR>"
				+ "Version: 1.0.0 <BR>"
				+ "(c) Copyright Sanctitysoft.com "
				+ "2007.  All rights reserved.<BR>"
				+ "<a href=http://www.sanctitysoft.com>http://www.sanctitysoft.com</a><BR>";

		JOptionPane.showMessageDialog(getXmlEditor(),
				"<html><table cellpadding=5 cellspacing=5><tr><td>" + imagesrc
						+ "</td><td>" + message + "</td></tr></table></html>");

		// JOptionPane.showMessageDialog(this,
		//
		// "by Omprakash Visvanathan for Digital Cat\n" +
		//
		// "Copyright � 1998, eXalto inc\n" +
		//
		// "www.exalto.net\n",
		//
		//
		// "About exalto",
		//
		// JOptionPane.INFORMATION_MESSAGE);

	}

	public XmlEditorDoc parseDocument(boolean validation) throws Exception {

		String text = null;
		String rootElem = null;
		XmlEditorDoc xmlEditorDoc = null;

		Editor editor = (Editor) ((MyInternalFrame) desktopView
				.getSelectedFrame()).getTextComponent();

		// if(desktopView.selectedHasFileName() == false) {
		if (desktopView.selectedIsNew() || editor.isDirty()) {
			text = ((MyInternalFrame) desktopView.getSelectedFrame())
					.getTextComponent().getText();
		}

		try {
			// Next, create the XTree
			xmlEditorDoc = new XmlEditorDoc();

			if (text != null) {
				System.out.println(" inside if no filename");
				rootElem = xmlEditorDoc.parseXml(text);

			} else {
				System.out.println(" inside else filename");
				File xmlFile = desktopView.getSelectedFile();
				System.out.println(" filename = " + xmlFile);
				rootElem = xmlEditorDoc.parseXml(xmlFile);
			}

		} catch (javax.xml.parsers.ParserConfigurationException pcfge) {
			logger.warn("Could not parse xml file xmlFile "
					+ pcfge.getMessage());
			throw pcfge;
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}

		return xmlEditorDoc;
	}

	public void doView(boolean showFrame) throws Exception {

		// if(xmlFile == null) {
		// setStatus("Please choose a file first");
		// return;
		// }

		String text = null;
		// if(desktopView.selectedHasFileName() == false) {
		if (desktopView.selectedIsNew()) {
			text = ((MyInternalFrame) desktopView.getSelectedFrame())
					.getTextComponent().getText();
		}

		try {
			// Next, create the XTree
			xTree = new XTree();
			xTree.getSelectionModel().setSelectionMode(
					TreeSelectionModel.SINGLE_TREE_SELECTION);
			xTree.setShowsRootHandles(true);
			// A more advanced version of this tool would allow the JTree to be
			// editable
			xTree.setEditable(false);

			// xTree.refresh(xmlFile);

			if (text != null) {
				System.out.println(" inside if no filename");
				xTree.refresh(text);
			} else {
				System.out.println(" inside else filename");
				File xmlFile = desktopView.getSelectedFile();
				System.out.println(" filename = " + xmlFile);
				xTree.refresh(xmlFile);
			}

		} catch (javax.xml.parsers.ParserConfigurationException pcfge) {
			logger.warn("Could not parse xml file xmlFile "
					+ pcfge.getMessage());
			throw pcfge;
		} catch (Exception e) {
			// e.printStackTrace();
			throw e;
		}

		// Create a frame containing an instance of
		// ElementTreePanel.

		if (showFrame) {
			elementTreeFrame = new JFrame("XML Tree View");

			elementTreeFrame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent weeee) {
					elementTreeFrame.setVisible(false);
				}
			});

			Container fContentPane = elementTreeFrame.getContentPane();

			fContentPane.setLayout(new BorderLayout());

			JPanel elementTreePanel = new JPanel();
			elementTreePanel.add(new JScrollPane(xTree), BorderLayout.CENTER);

			fContentPane.add(elementTreePanel);
			elementTreeFrame.pack();
			elementTreeFrame.show();

		}

	}

	public void doFO() {

		final SwingWorker worker = new SwingWorker() {

			TransformerIntf xtrans;

			public Object construct() {

				String retStatus = "SUCCESS";

				try {

					MyInternalFrame myf = (MyInternalFrame) desktopView
							.getSelectedFrame();

					if (myf == null)
						return null;

					if (desktopView.selectedHasFileName() == false) {
						// setStatus("Please open an xml file first");
						return "UNSAVED";
					}

					if (!desktopView.isStyleAssigned()) {
						return "UNASSIGNED";

						// return null;
					}

					// get the files for currently selected frame
					File xmlFile = desktopView.getSelectedFile();
					File xslFile = desktopView.getXslFile();

					System.out.println(" getXmlFile = " + xmlFile);
					System.out.println(" getXslFile = " + xslFile);

					String xslResult = ExaltoResource.getString(
							ColWidthTypes.ERR, "xslt.result.file");

					String version = getXsltVersion(xslFile.getAbsolutePath());

					if (version == null) {
						retStatus = "INCORRECT_VERSION";
					}

					if (version == "1.0") {
						xtrans = new XslTransformer(xmlFile.getPath(),
								xslFile.getPath(), prefs, getXmlEditor(),
								version);

						xtrans.setXslVersion(version);

						// xslResult += xtrans.getNextId();

						// System.out.println(" xslResult = " + xslResult);

						// File xres = new File(xslResult);

						xtrans.doTransform();

					} else if (version == "2.0") {

						String bpath = "f:/Caps/aatma";

						xtrans = new XslTransformer20(prefs, xmlEditor);

						// xslResult += xtrans.getNextId();

						// System.out.println(" xslResult = " + xslResult);

						// File xres = new File(xslResult);

						InputStream xmlin = new FileInputStream(xmlFile);

						InputStream xslin = new FileInputStream(xslFile);

						xtrans.doTransform(xmlin, xslin);

					}

				} catch (Exception e) {
					// e.printStackTrace();
					logger.debug("Exception generating FO" + e.getMessage());

					retStatus = "ERROR";

					/*
					 * if(e instanceof
					 * javax.xml.transform.TransformerConfigurationException ||
					 * e instanceof javax.xml.transform.TransformerException) {
					 * // setStatus("Unable to parse XML file");
					 * fireStatusChanged(new
					 * StatusEvent(ExaltoResource.getString
					 * (ColWidthTypes.ERR,"sax.parse.err"),0,
					 * ColWidthTypes.ERROR));
					 * 
					 * } else { // setStatus("Error performing transformation");
					 * fireStatusChanged(new
					 * StatusEvent(ExaltoResource.getString
					 * (ColWidthTypes.ERR,"xsl.transform.err"),0,
					 * ColWidthTypes.ERROR));
					 * 
					 * }
					 */
				}

				return retStatus;

			}

			// Runs on the event-dispatching thread.
			public void finished() {

				try {

					String ret = (String) this.get();

					if (ret != null) {

						if (ret.equals("UNSAVED")) {
							fireStatusChanged(new StatusEvent(
									ExaltoResource.getString(ColWidthTypes.ERR,
											"open.xml.file.first"), 0,
									ColWidthTypes.ERROR));
							return;
						} else if (ret.equals("UNASSIGNED")) {

							MyInternalFrame myf = (MyInternalFrame) desktopView
									.getSelectedFrame();

							if (myf == null)
								return;

							JOptionPane
									.showMessageDialog(
											null,
											"The selected frame does not have a stylesheet assigned. First"
													+ " assign a stylesheet using the menu or by clicking on a open frame");
							waitingToAssign = true;
							// fileToFrameMap.put(myf.getFile().getAbsolutePath(),
							// myf);
							frameMapFile = myf.getFile().getAbsolutePath();

							fireStatusChanged(new StatusEvent(
									ExaltoResource.getString(ColWidthTypes.ERR,
											"stylesheet.not.assign"), 0,
									ColWidthTypes.ERROR));
							return;

						} else if (ret.equals("INCORRECT_VERSION")) {
							fireStatusChanged(new StatusEvent(
									ExaltoResource.getString(ColWidthTypes.ERR,
											"xsl.version.incorrect"), 0,
									ColWidthTypes.ERROR));
							return;
						} else {

							System.out.println(" xtrans.errorMessages = "
									+ xtrans.getErrorMessages());

							String eMsg = xtrans.getErrorMessages();
							if (eMsg == null || eMsg.trim().equals(""))
								fireStatusChanged(new StatusEvent(
										ExaltoResource.getString(
												ColWidthTypes.ERR,
												"xsl.transform.complete"), 0,
										ColWidthTypes.NOERROR));

							File outFile = new File(xtrans.getXsltOutput());

							if (!outFile.exists()) {
								return;
							}

							desktopView.display(outFile);

							/*
							 * MyInternalFrame mf = (MyInternalFrame)
							 * desktopView.getSelectedFrame();
							 * 
							 * mf.addInternalFrameListener(getXmlEditor());
							 * 
							 * mf.setNew(true);
							 * 
							 * Editor editor = (Editor) mf.getTextComponent();
							 * editor.addCaretListener( getXmlEditor());
							 */
							// documents[FO].loadFile(getFmtObjFile());
							// setStatus("XSL transformation completed");

						}

					}

				} catch (MissingResourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					fireStatusChanged(new StatusEvent(ExaltoResource.getString(
							ColWidthTypes.ERR, "xsl.transform.error"), 0,
							ColWidthTypes.ERROR));
					return;

				}

			}

		};
		worker.start();

	}

	public void doTableLayout() {
		try {

			if (desktopView.getSelectedFrame() == null)
				return;

			if (desktopView.selectedHasFileName() == false) {
				return;
			}

			File foFile = desktopView.getSelectedFile();

			String fmtObjFile = foFile.getPath();

			System.out.println(" fmt obj file = " + fmtObjFile);

			String fmtOutFile = null;
			// int dpos = foFile.getName().lastIndexOf(".");
			// if(dpos > -1) {
			// fmtOutFile = foFile.getName().substring(0,dpos);
			// fmtOutFile = fmtOutFile + ".fmt.fo";
			// }

			// else {
			// logger.debug("fmormatting object file is null");
			// setStatus("select FO file first");
			// return;
			// }
			// }

			// JProgressBar progress = new JProgressBar();
			// progress.setMinimum(0);
			// progress.setMaximum((int) f.length());
			// statusBar.add(progress);
			// statusBar.revalidate();

			System.out.println(" fmt out file = " + fmtOutFile);
			boolean useWidthsFromPropsFile = true;
			// TableAutoFormatter ddemo = new
			// TableAutoFormatter(fmtObjFile,fmtOutFile, props );
			TableAutoFormatter ddemo = new TableAutoFormatter(fmtObjFile, props);
			ddemo.run();
			System.out.println(" fmt outfile  = " + ddemo.getFmtFile());

			// omp commented 13/2/2005
			// desktopView.open(new File(ddemo.getFmtFile()));
			desktopView.display(new File(ddemo.getFmtFile()));

			MyInternalFrame mf = (MyInternalFrame) desktopView
					.getSelectedFrame();

			mf.addInternalFrameListener(this);

			mf.setNew(true);

			Editor editor = (Editor) mf.getTextComponent();
			editor.addCaretListener(this);

			((JTextComponent) ((MyInternalFrame) desktopView.getSelectedFrame())
					.getTextComponent()).getDocument().addUndoableEditListener(
					new MyUndoableEditListener());
			// setStatus("scan completed");
			fireStatusChanged(new StatusEvent(ExaltoResource.getString(
					ColWidthTypes.ERR, "table.layout.complete"), 0,
					ColWidthTypes.NOERROR));

			// MyInternalFrame mf = (MyInternalFrame)
			// desktopView.getSelectedFrame();
			// mf.setNew(true);

		} catch (Exception e) {
			e.printStackTrace();
			// setStatus("Error in table layout");
			fireStatusChanged(new StatusEvent(ExaltoResource.getString(
					ColWidthTypes.ERR, "table.layout.err"), 0,
					ColWidthTypes.ERROR));

			logger.debug("Exception in balancing column widths "
					+ e.getMessage());
		}
	}

	public void doPdf() {
		try {

			if (desktopView.getSelectedFrame() == null)
				return;

			Properties p = getXmlEditor().getProperty();
			String pathToFop = p.getProperty("pathToFop");
			logger.info(" pathToFop = " + pathToFop);

			if (pathToFop == null) {
				logger.error("path to FOP not set");
				throw new Exception("Fop path not set");
			}

			// if(desktopView.selectedHasFileName() == false) {
			if (desktopView.selectedIsNew()) {
				// setStatus("open a formatting objects file first");
				fireStatusChanged(new StatusEvent(ExaltoResource.getString(
						ColWidthTypes.ERR, "open.fo.file.err"), 0,
						ColWidthTypes.ERROR));
				return;
			}

			File fmtFile = desktopView.getSelectedFile();
			// String fmtObjFile = fmtFile.getPath();
			String fmtObjFile = fmtFile.getName();

			if (!fmtObjFile.endsWith(".fo")) {
				fireStatusChanged(new StatusEvent(ExaltoResource.getString(
						ColWidthTypes.ERR, "open.fo.file.err"), 0,
						ColWidthTypes.ERROR));
				return;
			}

			int dpos = fmtObjFile.lastIndexOf(".");

			// System.out.println(" idepath bef = " + idePath);

			String idePath1 = null;
			if (dpos > -1) {
				if (idePath.lastIndexOf("\\") > -1)
					idePath1 = idePath.substring(0, idePath.lastIndexOf("\\"));

				// System.out.println(" idepath aft = " + idePath1);

				pdfFile = idePath1 + "\\" + fmtObjFile.substring(0, dpos)
						+ ".pdf";
			}

			System.out.println(" infile = " + fmtObjFile);
			System.out.println(" pdfFile = " + pdfFile);

			Process proc = Runtime.getRuntime().exec(
					pathToFop + " " + fmtObjFile + " " + pdfFile);
			logger.info("pdf generation to " + pdfFile + " completed ");

			JOptionPane.showMessageDialog(null,
					"The pdf has been generated to " + pdfFile);
			// setStatus("pdf generation to " + pdfFile + " completed");
			String[] margs = new String[1];
			margs[0] = pdfFile;
			String stext = xutils.getFormattedMsg(ExaltoResource.getString(
					ColWidthTypes.ERR, "pdf.generation.complete"), margs);
			fireStatusChanged(new StatusEvent(stext, 0, ColWidthTypes.NOERROR));

		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("Exception generating PDF" + e.getMessage());
		}
	}

	/**
	 * This closes the foreground XMLEditorDoc document
	 */
	public void showTextView(ActionEvent e) {
		System.out.println(" textview clicked ");
		MyInternalFrame frame = (MyInternalFrame) desktopView
				.getSelectedFrame();
		if (frame != null) {

			if (frame.getCurrentView().intern() == "TEXTVIEW".intern())
				return;

			frame.setCurrentView("TEXTVIEW");

			// OV c 2700309
			// gridMenu.setEnabled(false);
			_actions._formatAction.setEnabled(true);

			JEditorPane pane = (JEditorPane) frame.getTextComponent();

			pane.requestFocus();

		}
	}

	/**
	 * This closes the foreground XMLEditorDoc document
	 */
	public void showGridView(ActionEvent e) {
		System.out.println(" gridview clicked ");
		MyInternalFrame frame = (MyInternalFrame) desktopView
				.getSelectedFrame();
		if (frame != null) {

			if (frame.getCurrentView().intern() == "GRIDVIEW".intern())
				return;

			frame.setCurrentView("GRIDVIEW");
			// OV c 2700309
			// gridMenu.setEnabled(true);

			_actions.getAction(_actions.findNextAction).setEnabled(false);
			_actions._formatAction.setEnabled(false);

		}

	}

	/**
	 * This closes the foreground XMLEditorDoc document
	 */
	public void doClose() {
		MyInternalFrame frame = (MyInternalFrame) desktopView
				.getSelectedFrame();
		if (frame != null) {
			frame.closeDocument();
			// if(frame.closeDocument())
			// activateDocumentAfterClose();
		}
	}

	/**
	 * Updates the undo action with information about what it is going to undo
	 * using the mgr getUndoPresentationName() call.
	 * 
	 * @param mgr
	 *            the UndoManager to consult about whether an undo is available,
	 *            and what it's presentation name is. If this is passed as null,
	 *            the undo action is disabled and set to the defaults from the
	 *            UI resource bundle keys "edit.undo"
	 *
	 */
	public void resetUndoAction(UndoManager mgr) {
		if (mgr != null) {

			undoAction.setEnabled(mgr.canUndo());
			// redoAction.setEnabled(mgr.canRedo());

			// if there's an undoable get the presentation name and change the
			// undo action

			String undo = xutils.getResourceString("edit.undo");

			String newundo = undo;

			String pname = mgr.getUndoPresentationName();
			logger.debug("undo = " + undo + " pname = " + pname);

			if (pname != null && !pname.equalsIgnoreCase("undo")) {
				newundo = undo + " " + pname;
			}
			undoAction.putValue(Action.SHORT_DESCRIPTION, newundo);
		} else {
			undoAction.putValue(Action.SHORT_DESCRIPTION,
					xutils.getResourceString("edit.undo.tt"));
			undoAction.setEnabled(false);
		}
	}

	/**
	 * Updates the undo action with information about what it is going to undo
	 * using the mgr getUndoPresentationName() call.
	 * 
	 * @param mgr
	 *            the UndoManager to consult about whether an undo is available,
	 *            and what it's presentation name is. If this is passed as null,
	 *            the undo action is disabled and set to the defaults from the
	 *            UI resource bundle keys "edit.undo"
	 *
	 */
	public void resetRedoAction(UndoManager mgr) {
		if (mgr != null) {
			redoAction.setEnabled(mgr.canRedo());

			// if there's an undoable get the presentation name and change the
			// undo action

			String redo = xutils.getResourceString("edit.redo");

			String newredo = redo;

			String pname = mgr.getRedoPresentationName();
			logger.debug("redo = " + redo + " pname = " + pname);

			if (pname != null && !pname.equalsIgnoreCase("redo")) {
				newredo = redo + " " + pname;
			}
			redoAction.putValue(Action.SHORT_DESCRIPTION, newredo);
		} else {
			redoAction.putValue(Action.SHORT_DESCRIPTION,
					xutils.getResourceString("edit.redo.tt"));
			redoAction.setEnabled(false);
		}
	}

	// InternalFrameListener implementation
	/**
	 * Implementation of InternalFrameListener. Calls into docActivated to turn
	 * on menu items
	 */
	public void internalFrameActivated(InternalFrameEvent e) {

		System.out.println(" in internalframeactivated ");

		try {

			// switch out certain menu items
			Object o = e.getSource();
			if (o instanceof JInternalFrame) {
				// Object doc = _frameToDocumentMap.get((JInternalFrame)o);
				// if (doc instanceof XMLEditorDoc) {

				// OV 24/07/2005 dcommented for reopen begin

				UndoManager undoManager = desktopView.getCurrentUndoManager();
				resetUndoAction(undoManager);
				resetRedoAction(undoManager);

				// OV 24/07/2005 dcommented for reopen end

				// docActivated((XMLEditorDoc)doc);
				// }

				MyInternalFrame selFrame = null;

				selFrame = (MyInternalFrame) desktopView.getSelectedFrame();

				/*
				 * System.out.println(" checksum hash = " + checkSumHash);
				 * if(!selFrame.isNew()) { String frameFile =
				 * selFrame.getFile().getAbsolutePath();
				 * 
				 * System.out.println(" checksum framefile " + frameFile);
				 * 
				 * long checksum = ((Long)
				 * checkSumHash.get(frameFile)).longValue(); long filesum =
				 * FileUtil.calculateFileChecksum(frameFile);
				 * System.out.println(" checksum in hash = " + checksum);
				 * System.out.println(" checksum in file = " + filesum);
				 * 
				 * if(checksum != filesum) {
				 * 
				 * int response = JOptionPane.showConfirmDialog(null,
				 * "An external program has updated the file",
				 * "Would you like to reopen the file?",
				 * JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				 * if( response == JOptionPane.OK_OPTION) {
				 * System.out.println(" user chose ok. reopening..."); //
				 * doReopen(frameFile); } }
				 * 
				 * }
				 */

				// System.out.println(" selframe title " + selFrame.getTitle());

				// System.out.println(" selframe assign " +
				// selFrame.isWaitingForAssign());

				if (xmlFileFrame != null && xmlFileFrame.isWaitingForAssign()) {

					// System.out.println(" in waitingtoassign ");

					frameMapFile = xmlFileFrame.getFile().getAbsolutePath();

					selFrame = (MyInternalFrame) desktopView.getSelectedFrame();

					// System.out.println(" xsl file = " +
					// selFrame.getFile().getAbsolutePath());

					String xslFile = selFrame.getFile().getAbsolutePath();

					/*
					 * if(!xslFile.endsWith(".xsl")) {
					 * JOptionPane.showMessageDialog(null,
					 * "Not a valid XSL File "); }
					 */
					// System.out.println(" frame map file = " + frameMapFile);

					desktopView.setStyleSheet(selFrame.getFile()
							.getAbsolutePath(),
							(MyInternalFrame) fileToFrameMap.get(frameMapFile));
					frameMapFile = null;
					xmlFileFrame.setWaitingForAssign(false);
					assignStyleAction.shouldInvoke = false;
					fireStatusChanged(new StatusEvent(ExaltoResource.getString(
							ColWidthTypes.ERR, "stylesheet.assign"), 0,
							ColWidthTypes.NOERROR));

				} else {
					if (timer != null)
						timer.stop();
				}

			}

		} catch (Exception cse) {
			cse.printStackTrace();
		}

	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameClosed(InternalFrameEvent e) {
		if (desktopView.getAllFrames().length != 0) {
			updateCaretStatus();
			return;
		}

		fireStatusChanged(new StatusEvent("", 0, ColWidthTypes.DEFAULT));

	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameOpened(InternalFrameEvent e) {
	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameClosing(InternalFrameEvent e) {
		try {
			File f = desktopView.getSelectedFile();
			fileToFrameMap.remove(f.getAbsolutePath());
			checkSumHash.remove(f.getAbsolutePath());

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {

			MyInternalFrame myf = (MyInternalFrame) desktopView
					.getSelectedFrame();
			Editor etr = (Editor) myf.getTextComponent();
			System.out.println(" in  xmle calling cleanup");
			etr.cleanup();

		} catch (Exception ex1) {
			ex1.printStackTrace();
		}

	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameIconified(InternalFrameEvent e) {
	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameDeiconified(InternalFrameEvent e) {
	}

	/**
	 * Called when an internal frame has been closed. This cleans up items in
	 * the document maps and the window menu item
	 */
	public void internalFrameDeactivated(InternalFrameEvent e) {
	}

	/**
	 * ov commented for undo/redo NOT USED BEGIN *********** Listener for the
	 * edits on the current document.
	 */
	// protected UndoableEditListener undoHandler = new UndoHandler();

	/**
	 * UndoManager that we add edits to. * protected UndoManager undo = new
	 * UndoManager();
	 *
	 *
	 * class UndoHandler implements UndoableEditListener {
	 *
	 */
	/**
	 * Messaged when the Document has created an edit, the edit is added to
	 * <code>undo</code>, an instance of UndoManager.
	 *
	 * public void undoableEditHappened(UndoableEditEvent e) {
	 * System.out.println("edit happnd");
	 *
	 * undo.addEdit(e.getEdit()); undoAction.update(); redoAction.update(); } }
	 * NOT USED END
	 **************/

	public class AssignStyleAction extends AbstractAction {

		public boolean shouldInvoke = true;

		public boolean isShouldInvoke() {
			return shouldInvoke;
		}

		public String assignType = null;
		MyInternalFrame myf = null;
		String clientData = null;

		public void actionPerformed(ActionEvent e) {

			if (shouldInvoke) {
				// if(assignType.intern() == "STYLESHEET".intern()) {
				popupAssignDialog(clientData);

				/*
				 * assignDlg = new JDialog(); JLabel assign = new
				 * JLabel("Choose stylesheet file"); xslFileName = new
				 * JTextField(45); JButton browseFile = new
				 * JButton("Browse Xsl File"); okButton = "OK"; cancelButton =
				 * "Cancel";
				 * 
				 * browseFile.addActionListener(new ActionListener() {
				 * 
				 * public void actionPerformed(ActionEvent e) { String dir =
				 * getCurrentDir(); System.out.println("assign style dir = " +
				 * dir); JFileChooser chooseXsl = new JFileChooser(dir);
				 * chooseXsl.setCurrentDirectory(new File(dir)); String
				 * description = "XSL Files"; String filename =
				 * ExtensionFileFilter.getFileName(dir, description,
				 * extensions[2], FileDialog.LOAD); if(filename != null) {
				 * System.out.println("filename =  " + filename);
				 * xslFileName.setText(filename); } } });
				 * 
				 * Object[] array = {assign, xslFileName, browseFile };
				 * 
				 * //Create an array specifying the number of dialog buttons
				 * //and their text. Object[] options = {okButton,
				 * cancelButton};
				 * 
				 * //Create the JOptionPane. optionPane = new JOptionPane(array,
				 * JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
				 * null, options, options[0]);
				 * 
				 * optionPane.putClientProperty("ASSIGN_TYPE", "STYLESHEET");
				 * 
				 * 
				 * assignDlg.getContentPane().add(optionPane); //Handle window
				 * closing correctly. //
				 * assignDlg.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
				 * assignDlg.addWindowListener(new WindowAdapter() {
				 * 
				 * public void windowClosing(WindowEvent we) { // // Instead of
				 * directly closing the window, // we're going to change the
				 * JOptionPane's // value property. // optionPane.setValue(new
				 * Integer( JOptionPane.CLOSED_OPTION)); } });
				 * 
				 * //Ensure the text field always gets the first focus.
				 * addComponentListener(new ComponentAdapter() { public void
				 * componentShown(ComponentEvent ce) { //
				 * xslFileName.requestFocusInWindow(); System.out.println(""); }
				 * }); assignDlg.setSize(145, 75); assignDlg.pack();
				 * assignDlg.show();
				 * 
				 * //Register an event handler that reacts to option pane state
				 * changes.
				 * optionPane.addPropertyChangeListener(getXmlEditor()); //
				 * xslFileName.addActionListener(this);
				 */

				// }
				// else if(assignType.intern() == "DTD".intern()) {
				// popupAssignDialog("DTD");
				// } else if(assignType.intern() == "SCHEMA".intern()) {
				// popupAssignDialog("SCHEMA");
				// }

			}

		}

		private void popupAssignDialog(String rootElem) {

			myf = (MyInternalFrame) desktopView.getSelectedFrame();
			myf.setWaitingForAssign(false);

			if (desktopView.selectedIsNew()) {
				myf = (MyInternalFrame) desktopView.getSelectedFrame();
				String text = myf.getTextComponent().getText();
				if (text == null || text.trim().length() <= 0) {
					fireStatusChanged(new StatusEvent(ExaltoResource.getString(
							ColWidthTypes.ERR, "nothing.to.transform"), 0,
							ColWidthTypes.ERROR));
					return;
				}
			}

			assignDlg = new JDialog();
			// JLabel assign = new JLabel("Choose stylesheet file");

			JLabel assign = new JLabel(ExaltoResource.getString(
					ColWidthTypes.ERR, "choose." + assignType.toLowerCase()
							+ ".file.type"));
			xslFileName = new JTextField(45);
			// JButton browseFile = new JButton("Browse Xsl File");
			JButton browseFile = new JButton(ExaltoResource.getString(
					ColWidthTypes.ERR, "browse." + assignType.toLowerCase()
							+ ".file.type"));
			okButton = ExaltoResource.getString(ColWidthTypes.ERR,
					"xmleditor.ok");
			cancelButton = ExaltoResource.getString(ColWidthTypes.ERR,
					"xmleditor.cancel");

			browseFile.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					String dir = getCurrentDir();
					// System.out.println("assign style dir = " + dir);
					JFileChooser chooseXsl = new JFileChooser(dir);
					chooseXsl.setCurrentDirectory(new File(dir));
					// String description = "XSL Files";
					String description = ExaltoResource.getString(
							ColWidthTypes.ERR,
							AssignStyleAction.this.assignType.toLowerCase()
									+ ".descr.files");

					String filename = null;
					if (AssignStyleAction.this.assignType.intern() == "STYLESHEET"
							.intern()) {
						filename = ExtensionFileFilter.getFileName(dir,
								description, extensions[2], FileDialog.LOAD);

					} else if (AssignStyleAction.this.assignType.intern() == "SCHEMA"
							.intern()) {
						filename = ExtensionFileFilter.getFileName(dir,
								description, schema_extns, FileDialog.LOAD);

					} else if (AssignStyleAction.this.assignType.intern() == "DTD"
							.intern()) {
						filename = ExtensionFileFilter.getFileName(dir,
								description, dtd_extn, FileDialog.LOAD);

					}
					if (filename != null) {
						System.out.println("filename =  " + filename);
						xslFileName.setText(filename);
					}
				}
			});

			Object[] array = { assign, xslFileName, browseFile };

			// Create an array specifying the number of dialog buttons
			// and their text.
			Object[] options = { okButton, cancelButton };

			// Create the JOptionPane.
			optionPane = new JOptionPane(array, JOptionPane.QUESTION_MESSAGE,
					JOptionPane.YES_NO_OPTION, null, options, options[0]);

			optionPane.putClientProperty("ASSIGN_TYPE", assignType);
			optionPane.putClientProperty("CLIENT_DATA", rootElem);

			assignDlg.getContentPane().add(optionPane);
			// Handle window closing correctly.
			// assignDlg.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			assignDlg.addWindowListener(new WindowAdapter() {

				public void windowClosing(WindowEvent we) {
					//
					// Instead of directly closing the window,
					// we're going to change the JOptionPane's
					// value property.
					//
					optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
				}
			});

			// Ensure the text field always gets the first focus.
			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
					// xslFileName.requestFocusInWindow();
					System.out.println("");
				}
			});
			assignDlg.setSize(145, 75);
			assignDlg.pack();
			assignDlg.show();

			// Register an event handler that reacts to option pane state
			// changes.
			optionPane.addPropertyChangeListener(getXmlEditor());

			shouldInvoke = false;

		}

	}

	/*
	 * OV c 26/01/08 not used anymore. use XmlEditorActions.CutAction //
	 * protected class CutAction extends TextAction { protected class CutAction
	 * extends TextAction {
	 * 
	 * public CutAction() { super("cut");
	 * 
	 * XmlUtils.loadActionResources(this,"UI","edit.cut"); }
	 * 
	 * public void actionPerformed(ActionEvent evt) { boolean retVal = false;
	 * 
	 * System.out.println(" in CA actionPerformed evt src " +
	 * evt.getSource().getClass().getName());
	 * 
	 * DesktopView desktopView = getDesktopView();
	 * 
	 * MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
	 * 
	 * if(myf != null) {
	 * 
	 * // if(myf.getCurrentView().equals("TEXTVIEW")) {
	 * 
	 * // System.out.println(" CutAction actionPerformed TEXTVIEW ");
	 * 
	 * // OV c 24/03/08 JTextComponent textComp = getTextComponent(evt);
	 * 
	 * //OV a 24/03/08 XmlEditorActions va = new XmlEditorActions();
	 * TreeTableClipboard target = va.getTargetComponent(myf, textComp);
	 * 
	 * if (target != null) { target.cut(); //OV c 24/03/08 //
	 * pasteAction.setEnabled(true); } else { retVal = desktopView.cut(evt);
	 * 
	 * System.out.println(" CutAction retval = " + retVal); //OV c 24/03/08 //
	 * pasteAction.setEnabled(retVal); }
	 * 
	 * 
	 * }
	 * 
	 * } }
	 * 
	 * protected class CopyAction extends TextAction { public CopyAction() {
	 * super("copy"); XmlUtils.loadActionResources(this,"UI","edit.copy"); }
	 * 
	 * public void actionPerformed(ActionEvent evt) {
	 * 
	 * DesktopView desktopView = getDesktopView();
	 * 
	 * MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
	 * 
	 * if(myf != null) {
	 * 
	 * 
	 * // if(myf.getCurrentView().equals("TEXTVIEW")) {
	 * 
	 * 
	 * System.out.println(" CopyAction actionPerformed TEXTVIEW ");
	 * 
	 * //OV c 24/03/08 JTextComponent textComp = getTextComponent(evt);
	 * 
	 * //OV a 24/03/08 XmlEditorActions va = new XmlEditorActions();
	 * TreeTableClipboard target = va.getTargetComponent(myf, textComp);
	 * 
	 * if (target != null) { target.copy(); // OV c 24/03/08 //
	 * pasteAction.setEnabled(true); } else { desktopView.copy(evt); // OV c
	 * 24/03/08 // pasteAction.setEnabled(true);
	 * 
	 * }
	 * 
	 * } } }
	 * 
	 * protected class PasteAction extends TextAction { public PasteAction() {
	 * super("paste"); setEnabled(false);
	 * XmlUtils.loadActionResources(this,"UI","edit.paste");
	 * 
	 * }
	 * 
	 * public void actionPerformed(ActionEvent evt) {
	 * 
	 * DesktopView desktopView = getDesktopView();
	 * 
	 * MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
	 * 
	 * if(myf != null) {
	 * 
	 * // if(myf.getCurrentView().equals("TEXTVIEW")) {
	 * 
	 * System.out.println(" pasteAction actionPerformed TEXTVIEW ");
	 * 
	 * //OV c 24/03/08 JTextComponent textComp = getTextComponent(evt);
	 * 
	 * //OV a 24/03/08 XmlEditorActions va = new XmlEditorActions();
	 * TreeTableClipboard target = va.getTargetComponent(myf, textComp);
	 * 
	 * if (target != null) { target.paste(); } else { desktopView.paste(evt); }
	 * 
	 * } } }
	 */

	/************
	 * ov commented old undo and redo action ************** class UndoAction
	 * extends AbstractAction { public UndoAction() { super("Undo");
	 * setEnabled(false); }
	 * 
	 * public void actionPerformed(ActionEvent e) { try {
	 * System.out.println("undo pressed"); undo.undo(); } catch
	 * (CannotUndoException ex) { logger.warn("Unable to undo " +
	 * ex.getMessage()); } update(); redoAction.update(); }
	 * 
	 * protected void update() { System.out.println("in undo update");
	 * 
	 * if(undo.canUndo()) { // System.out.println("in undo update if");
	 * setEnabled(true); putValue(Action.NAME, undo.getUndoPresentationName());
	 * } else { System.out.println("in undo update else"); setEnabled(false);
	 * putValue(Action.NAME, "Undo"); } }
	 * 
	 * public String toString() { return "undoaction"; }
	 * 
	 * }
	 * 
	 * class RedoAction extends AbstractAction { public RedoAction() {
	 * super("Redo"); setEnabled(false); }
	 * 
	 * public void actionPerformed(ActionEvent e) { try { undo.redo(); } catch
	 * (CannotRedoException ex) { logger.warn("Unable to redo " +
	 * ex.getMessage()); } update(); undoAction.update(); }
	 * 
	 * protected void update() { if(undo.canRedo()) { setEnabled(true);
	 * putValue(Action.NAME, undo.getRedoPresentationName()); } else {
	 * System.out.println("in redo update else"); setEnabled(false);
	 * putValue(Action.NAME, "Redo"); } }
	 * 
	 * public String toString() { return "redoaction"; }
	 * 
	 * 
	 * }
	 * 
	 * /****************ov comment end
	 ********************/

	/****************** New undoAction class begin ************************/

	protected class UndoAction extends AbstractAction {
		public UndoAction() {
			super("undo");
			XmlUtils.loadActionResources(this, "UI", "edit.undo");

		}

		public void actionPerformed(ActionEvent evt) {

			DesktopView desktopView = getDesktopView();

			MyInternalFrame myf = (MyInternalFrame) desktopView
					.getSelectedFrame();

			if (myf != null) {

				// if(myf.getCurrentView().equals("TEXTVIEW")) {

				System.out.println(" undoAction actionPerformed TEXTVIEW ");

				/*
				 * OV c 24/03/08 desktopView.undo(evt); boolean canUndo =
				 * desktopView.getCurrentUndoManager().canUndo(); if(!canUndo) {
				 * String title = myf.getTitle(); if(title.indexOf("*") != -1) {
				 * int spos = title.indexOf("*"); String s = title.substring(0,
				 * spos).trim(); myf.setTitle(s); } }
				 */

				// OV a 24/03/08
				XmlEditorActions va = new XmlEditorActions(myf);

				va.resetActions();

				/*
				 * OV c 24/03/08
				 * resetUndoAction(desktopView.getCurrentUndoManager());
				 * resetRedoAction(desktopView.getCurrentUndoManager());
				 */

				/*
				 * OV c 24/03/08 } else {
				 * 
				 * System.out.println(" UndoAction actionPerformed GRIDVIEW ");
				 * TreeTableClipboard treeClipBoard =
				 * myf.getTreeTableClipboard(); treeClipBoard.undo();
				 * 
				 * }
				 */

			}
		}
	}

	/****************** New UndoAction class end **************************/
	/****************** New RedoAction class begin ************************/
	protected class RedoAction extends AbstractAction {
		public RedoAction() {
			super("redo");
			XmlUtils.loadActionResources(this, "UI", "edit.redo");

		}

		public void actionPerformed(ActionEvent evt) {

			DesktopView desktopView = getDesktopView();

			MyInternalFrame myf = (MyInternalFrame) desktopView
					.getSelectedFrame();

			if (myf != null) {

				// if(myf.getCurrentView().equals("TEXTVIEW")) {

				System.out.println(" undoAction actionPerformed TEXTVIEW ");

				desktopView.redo(evt);
				resetUndoAction(desktopView.getCurrentUndoManager());
				resetRedoAction(desktopView.getCurrentUndoManager());

				/*
				 * OV c 24/03/08 } else {
				 * 
				 * System.out.println(" RedoAction actionPerformed GRIDVIEW ");
				 * TreeTableClipboard treeClipBoard =
				 * myf.getTreeTableClipboard(); treeClipBoard.redo(); }
				 */
			}
		}
	}

	/****************** New RedoAction class end ************************/

	class OpenAction extends NewAction {

		OpenAction() {
			super(openAction);
		}

		public void actionPerformed(ActionEvent e) {
			doOpen();

		}
	}

	class NewAction extends AbstractAction {

		NewAction() {
			super(newAction);
		}

		NewAction(String nm) {
			super(nm);
		}

		public void actionPerformed(ActionEvent e) {

			logger.debug(" New action performed");

			try {

				// performNewAction(false, null);

				desktopView.newFile();

				/*
				 * MyInternalFrame myFrame =
				 * (MyInternalFrame)desktopView.getSelectedFrame();
				 * 
				 * 
				 * if(defaultViewForNewFiles.intern() == "TEXTVIEW".intern()) {
				 * 
				 * Editor editor = (Editor) myFrame.getTextComponent();
				 * editor.addCaretListener(getXmlEditor());
				 * 
				 * myFrame.addInternalFrameListener(getXmlEditor());
				 * 
				 * ((JTextComponent)myFrame.getTextComponent()).getDocument().
				 * addUndoableEditListener( new MyUndoableEditListener());
				 * 
				 * 
				 * }
				 */
				if (xTree != null)
					xTree.clear();

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

	protected class CloseAction extends AbstractAction {

		public CloseAction() {
			super(closeAction);
			XmlUtils.loadActionResources(this, "UI", "file.close");

		}

		public void actionPerformed(ActionEvent evt) {
			doClose();
		}
	}

	/**
	 * Really lame implementation of an exit command
	 */
	class QuitAction extends AbstractAction {

		QuitAction() {
			super(quitAction);
		}

		public void actionPerformed(ActionEvent e) {
			// System.out.println(" in act perf");
			if (checkQuit()) {
				System.exit(0);
			}
		}
	}

	/**
	 * Trys to write the document as a serialization.
	 */
	class SaveAction extends AbstractAction {

		SaveAction() {
			super(saveAction);
		}

		public void actionPerformed(ActionEvent e) {
			doSave();
			// if (fileDialog == null) {
			// fileDialog = new FileDialog(frame);
			// }
			// fileDialog.setMode(FileDialog.SAVE);
			// fileDialog.show();
			// String file = fileDialog.getFile();
			// if (file == null) {
			// return;
			// }
			// String directory = fileDialog.getDirectory();
			// File f = new File(directory, file);
			// try
			// {
			// FileOutputStream fstrm = new FileOutputStream(f);
			// ObjectOutput ostrm = new ObjectOutputStream(fstrm);
			// ostrm.writeObject(textArea.getDocument());
			// ostrm.flush();
			// } catch (IOException io) {
			// // should put in status panel
			// System.err.println("IOException: " + io.getMessage());
			// }
		}
	}

	/**
	 * Action that brings up a JFrame with a JTree showing the structure of the
	 * document.
	 */
	/*
	 * class ShowElementTreeAction extends AbstractAction {
	 * 
	 * ShowElementTreeAction() { super(showElementTreeAction); }
	 * 
	 * ShowElementTreeAction(String nm) { super(nm); }
	 * 
	 * public void actionPerformed(ActionEvent e) {
	 * 
	 * if(desktopView.selectedHasFileName() == false)
	 * 
	 * // File xmlFile = desktopView.getSelectedFile(); // String xmlFileName =
	 * fmtFile.getPath(); String text =
	 * ((MyInternalFrame)desktopView.getSelectedFrame
	 * ()).getEditor().getDocument().getText();
	 * 
	 * if(elementTreeFrame == null) { try { jScroll = new JScrollPane();
	 * 
	 * 
	 * // Next, create the XTree xTree = new XTree();
	 * xTree.getSelectionModel().setSelectionMode
	 * (TreeSelectionModel.SINGLE_TREE_SELECTION ); xTree.setShowsRootHandles(
	 * true ); // A more advanced version of this tool would allow the JTree to
	 * be editable xTree.setEditable( false ); // Wrap the JTree in a JScroll so
	 * that we can scroll it in the JSplitPane. jScroll.getViewport().add( xTree
	 * ); getContentPane().setLayout(new BorderLayout());
	 * getContentPane().add("Center", jScroll);
	 * 
	 * // if(xmlFile != null) xTree.refresh(text); // else { //
	 * logger.info("Please open xml file first"); //
	 * setStatus("Please open xml file first"); // }
	 * 
	 * } catch(javax.xml.parsers.ParserConfigurationException pcfge) {
	 * logger.warn("could not parse xml file " + pcfge.getMessage()); }
	 * 
	 * 
	 * 
	 * // Create a frame containing an instance of // ElementTreePanel. try {
	 * String title = resources.getString ("ElementTreeFrameTitle");
	 * elementTreeFrame = new JFrame(title); } catch (MissingResourceException
	 * mre) { elementTreeFrame = new JFrame(); }
	 * 
	 * elementTreeFrame.addWindowListener(new WindowAdapter() { public void
	 * windowClosing(WindowEvent weeee) { elementTreeFrame.setVisible(false); }
	 * }); Container fContentPane = elementTreeFrame.getContentPane();
	 * 
	 * fContentPane.setLayout(new BorderLayout());
	 * 
	 * JPanel elementTreePanel = new JPanel(); elementTreePanel.add(new
	 * JScrollPane(xTree), BorderLayout.CENTER);
	 * 
	 * fContentPane.add(elementTreePanel); elementTreeFrame.pack(); } if(xmlFile
	 * == null) { logger.info("Xmlfile is null...clearing tree"); xTree.clear();
	 * elementTreeFrame.invalidate(); } elementTreeFrame.show(); } }
	 */

	/**
	 * Really lame implementation of an exit command
	 */
	/*
	 * class GenFOAction extends AbstractAction {
	 * 
	 * public GenFOAction() { super(genfoAction); }
	 * 
	 * public void actionPerformed(ActionEvent e) { Object obj = e.getSource();
	 * if(obj instanceof JButton) { JButton jb = (JButton) obj; try { if
	 * (jb.getToolTipText().equals("Generate FO")) {
	 * logger.debug(" GenFOAction xmlFile = " + getXmlFile());
	 * 
	 * // if(!fileNamesSelected) { // customDialog = new
	 * CustomDialog(getXmlEditor()); // customDialog.pack(); //
	 * customDialog.setLocationRelativeTo(getXmlEditor()); //
	 * customDialog.setVisible(true); // if(xmlFile != null && fmtObjFile !=
	 * null && // pdfFile != null) { // fileNamesSelected = true; // } // // //
	 * }
	 * 
	 * 
	 * XslTransformer xtrans = new XslTransformer(getXmlFile(), "xhtml2fo.xsl",
	 * props); xtrans.transform(); setFmtObjFile(xtrans.getFmtObjFile());
	 * return; } } catch(Exception fe) { logger.info("file not an xhtml file");
	 * setStatus("file not an xhtml file");
	 * 
	 * } }
	 * 
	 * } }
	 */

	/**
	 * Really lame implementation of an exit command
	 */
	/*
	 * class BalanceAction extends AbstractAction {
	 * 
	 * public BalanceAction() { super(balanceAction); }
	 * 
	 * public void actionPerformed(ActionEvent e) { Object obj = e.getSource();
	 * 
	 * if(obj instanceof JButton) { JButton jb = (JButton) obj; if
	 * (jb.getToolTipText().equals("Balance column width")) { try { //
	 * if(fileNamesSelected) {
	 * 
	 * TableAutoFormatter ddemo = new TableAutoFormatter(getFmtObjFile(),
	 * getFmtObjFile(), props); ddemo.initiateAction(); runSuccessful = true;
	 * logger.info("Balancing algorithm completed");
	 * setStatus("scan completed"); // } else { // customDialog = new
	 * CustomDialog(getXmlEditor()); // customDialog.pack(); //
	 * customDialog.setLocationRelativeTo(getXmlEditor()); //
	 * customDialog.setVisible(true); // if(xmlFile != null && fmtObjFile !=
	 * null && // pdfFile != null) { // fileNamesSelected = true; // } // } // }
	 * catch(Exception pe) { logger.info("Balancing algorithm error" +
	 * pe.getMessage()); } return; } }
	 * 
	 * } }
	 */
	/**
	 * Really lame implementation of an exit command
	 */
	/*
	 * class GenPDFAction extends AbstractAction {
	 * 
	 * public GenPDFAction() { super(genpdfAction); }
	 * 
	 * public void actionPerformed(ActionEvent e) { Object obj = e.getSource();
	 * 
	 * if(obj instanceof JButton) { JButton jb = (JButton) obj; if
	 * (jb.getToolTipText().equals("Generate PDF")) { try {
	 * 
	 * Properties p = getXmlEditor().getProperty(); String pathToFop =
	 * p.getProperty("pathToFop"); logger.info(" pathToFop = " + pathToFop);
	 * 
	 * if(pathToFop == null) { logger.error("path to FOP not set"); throw new
	 * Exception("Fop path not set"); } // if(fileNamesSelected) { Process proc
	 * = Runtime.getRuntime().exec(pathToFop + " " + outfile + " " + outfile +
	 * ".pdf" ); proc.waitFor(); logger.info("pdf generation completed");
	 * setStatus("pdf generation completed"); // } else { // customDialog = new
	 * CustomDialog(getXmlEditor()); // customDialog.pack(); //
	 * customDialog.setLocationRelativeTo(getXmlEditor()); //
	 * customDialog.setVisible(true); // if(xmlFile != null && fmtObjFile !=
	 * null && // pdfFile != null) { // fileNamesSelected = true; // } // } }
	 * catch(Exception ie) {
	 * logger.error("Cannot generate PDF.Exception occurred"); } return; } }
	 * 
	 * } }
	 */

	class WordWrapAction extends AbstractAction {

		WordWrapAction() {
			super(wordwrapAction);
		}

		public void actionPerformed(ActionEvent e) {
			AbstractButton b = (AbstractButton) (e.getSource());
			if (b instanceof JToggleButton) {
				JToggleButton jtb = (JToggleButton) b;
				doWordWrap(jtb);
			}
		}
	}

	/**
	 * Really lame implementation of an exit command
	 */
	/*
	 * class OptionAction extends AbstractAction {
	 * 
	 * OptionAction() { super(optionAction); }
	 * 
	 * public void actionPerformed(ActionEvent e) { } }
	 */

	/**
	 * Really lame implementation of an exit command
	 */
	/*
	 * class AboutAction extends AbstractAction {
	 * 
	 * AboutAction() { super(aboutAction); }
	 * 
	 * public void actionPerformed(ActionEvent e) {
	 * 
	 * if(jDialog == null) { jDialog = new JOptionPane(); }
	 * 
	 * URL url = getResource(aboutAction + imageSuffix);
	 * 
	 * String imagesrc = "<img src=\"" + url + "\" width=\"84\" height=\"80\">";
	 * String message =
	 * "exalto XSL-FO Table Auto Layout Formatter (Windows) <BR>" +
	 * "Version: 1.0.0 <BR>" + "Build id: 20021125_2118<BR>" +
	 * "(c) Copyright exalto inc " + "2004.  All rights reserved.<BR>" +
	 * "<a href=http://www.exalto.net>http://www.exalto.net</a><BR>";
	 * 
	 * jDialog.showMessageDialog(getXmlEditor(),
	 * "<html><table cellpadding=5 cellspacing=5><tr><td>" + imagesrc +
	 * "</td><td>" + message + "</td></tr></table></html>");
	 * 
	 * } }
	 */

	public void doReopen(String file, MyInternalFrame frame, boolean updateTitle) {

		try {

			System.out.println(" in doreopen");
			// MyInternalFrame myf = (MyInternalFrame)
			// desktopView.getSelectedFrame();

			Editor editor = (Editor) frame.getTextComponent();

			editor.read(new FileReader(file), file);

			File frameFile = new File(file);

			if (updateTitle)
				frame.setTitle(frameFile.getName());

		} catch (FileNotFoundException fe) {
			fe.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		}

	}

	/**
	 * Thread to load a file into the text storage model
	 */
	class FileLoader extends Thread {

		FileLoader(File f, Document doc) {
			setPriority(4);
			this.f = f;
			this.doc = doc;
		}

		public void run() {
			try {
				// initialize the statusbar
				statusTxt.setText("");
				// JProgressBar progress = new JProgressBar();
				// progress.setMinimum(0);
				// progress.setMaximum((int) f.length());
				// statusBar.add(progress);
				// statusBar.revalidate();

				// try to start reading
				// System.out.println("in loader run" + f.getPath());

				Reader in = new FileReader(f);
				char[] buff = new char[4096];
				int nch;
				while ((nch = in.read(buff, 0, buff.length)) != -1) {
					doc.insertString(doc.getLength(), new String(buff, 0, nch),
							null);
					// progress.setValue(progress.getValue() + nch);
				}

				// we are done... get rid of progressbar
				// statusBar.remove(progress);
				// doc.addUndoableEditListener(undoHandler);
				doc.addUndoableEditListener(new MyUndoableEditListener());
				statusTxt.setText("after load");
				// editor.setTitleAt(tabIndex++, f.getName());

				// statusBar.revalidate();
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("Exception loading file into editor"
						+ e.getMessage());
			} catch (BadLocationException e) {
				e.printStackTrace();
				logger.error("Exception loading file into editor"
						+ e.getMessage());
			}
		}

		Document doc;
		File f;
	}

	/**
	 * FIXME - I'm not very useful yet
	 */
	class StatusBar extends JPanel {

		private JLabel _status1;
		private JLabel _status2;
		private JLabel _status3;
		protected final JPopupMenu popupMenu = new JPopupMenu();
		protected static final long STATUS_DISPLAY_TIME = (8 * 1000);
		// display status msgs for 5 secs

		private MouseListener mouseListener = null;

		public StatusBar() {
			super();
			PercentLayout statusLay = new PercentLayout(
					PercentLayout.HORIZONTAL, 5);

			// System.out.println(" in ctor statusbar ");
			setLayout(statusLay);

			_status1 = new JLabel();

			_status2 = new JLabel();
			_status2.setPreferredSize(new Dimension(100, 20));

			_status3 = new JLabel();
			add(_status1, new PercentLayout.Constraints(60, PercentLayout.BOTH));
			add(_status2, new PercentLayout.Constraints(20, PercentLayout.BOTH));
			add(_status3, new PercentLayout.Constraints(20, PercentLayout.BOTH));

			_status1.setBorder(new BevelBorder(BevelBorder.LOWERED));
			_status2.setBorder(new BevelBorder(BevelBorder.LOWERED));
			_status3.setBorder(new BevelBorder(BevelBorder.LOWERED));

			setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {

					StringSelection selected = new StringSelection(
							getStatusText());

					Toolkit.getDefaultToolkit().getSystemClipboard()
							.setContents(selected, null);
				}
			};

			// Copy
			JMenuItem copyMenuItem = new JMenuItem("Copy to clipboard");
			copyMenuItem.addActionListener(actionListener);
			popupMenu.add(copyMenuItem);

			// System.out.println(" exitirnyg ctor statusbar ");

		}

		public void setStatus(String text, int index, String msgType) {
			String statusImg = null;
			ImageIcon imgIcon = null;

			// System.out.println(" in setStatus msg= " + text);

			String s = null, s1 = null, s2 = null, s3 = null;
			s = text;

			// System.out.println(" in setStatus msgType= " + msgType);

			if (msgType == null)
				msgType = ColWidthTypes.DEFAULT_STATUS;

			if (index == 0) {
				if (msgType.intern() == ColWidthTypes.ERROR.intern()) {
					imgIcon = new ImageIcon(
							xutils.getResource(ColWidthTypes.ERROR_STATUS));
				} else if (msgType.intern() == ColWidthTypes.NOERROR.intern()) {
					imgIcon = new ImageIcon(
							xutils.getResource(ColWidthTypes.INFO_STATUS));
				} else if (msgType
						.equals(ColWidthTypes.VALIDATION_ERROR_STATUS)) {
					imgIcon = new ImageIcon(
							xutils.getResource(ColWidthTypes.VALIDATION_ERROR_STATUS));
				} else {
					imgIcon = new ImageIcon(
							xutils.getResource(ColWidthTypes.DEFAULT_STATUS));
				}

				_status1.setIcon(imgIcon);

			}

			if (text == null)
				text = "";

			if (index == 0) {
				if (text.length() > 55) {
					s1 = text.substring(0, 32);
					s2 = text.substring(32);
					int cpos = s2.indexOf(":");
					if (cpos != -1) {
						s3 = s2.substring(cpos);
					}
					s = s1 + " " + s2 + "<BR>" + s3;
				}

				String stext = "<html><center><font color=blue size=2>" + s
						+ "</font></center></html>";

				_status1.setText(text);
				_status1.setToolTipText(stext);

				output.setText(text);

			} else if (index == 1) {
				_status2.setText(text);
				_status1.setToolTipText(text);
			} else if (index == 2) {
				_status3.setText(text);
				_status1.setToolTipText(text);
			}

			revalidate();
		}

		public void removeMouseListener() {

			// System.out.println(" in removeMouseListener stext = " +
			// getStatusText());
			super.removeMouseListener(mouseListener);
		}

		public void addMouseListener(final DesktopView desktopView) {

			// System.out.println(" in addMouseListener ");

			mouseListener = new MouseAdapter() {
				static final int JUMP = 0;
				static final int MOVE = 1;

				private void showIfPopupTrigger(MouseEvent mouseEvent) {
					if (mouseEvent.isPopupTrigger()) {
						popupMenu.show(mouseEvent.getComponent(),
								mouseEvent.getX(), mouseEvent.getY());
					}
				}

				public void mousePressed(MouseEvent mouseEvent) {
					showIfPopupTrigger(mouseEvent);
				}

				public void mouseReleased(MouseEvent mouseEvent) {
					showIfPopupTrigger(mouseEvent);
				}

				public void mouseClicked(MouseEvent mouseEvent) {
					Point pt = new Point(mouseEvent.getX(), mouseEvent.getY());
					// int pos = multiLabel.viewToModel(pt);

					// if (pos >= 0) {
					activateLink(JUMP);
					// }
				}

				public void activateLink(int type) {
					System.out.println(" in activate link ");

					try {
						int line = 0;
						int col = 0;

						String statusText = _status1.getText();

						System.out.println(" in activate link statusText = "
								+ statusText);

						int tpos = statusText.indexOf("Line=");

						int cpos = statusText.indexOf(':', tpos);
						String token = null;
						if (cpos != -1) {
							token = statusText.substring(tpos + 5, cpos);
							line = Integer.parseInt(token);
						}

						String colText = statusText.substring(cpos);
						int pos = colText.indexOf("Col=");
						int spos = colText.indexOf(":", pos);

						if (pos != -1) {
							String coltok = colText.substring(pos + 4, spos);
							col = Integer.parseInt(coltok);
						}

						// System.out.println(" line = " + line);
						// System.out.println(" col = " + col);

						MyInternalFrame myFrame = (MyInternalFrame) desktopView
								.getSelectedFrame();
						Editor editor = (Editor) myFrame.getTextComponent();

						Element root = editor.getDocument()
								.getDefaultRootElement();
						line = Math.max(line, 1);
						line = Math.min(line, root.getElementCount());

						int offset = root.getElement(line - 1).getStartOffset()
								+ col;
						int word = Utilities.getPreviousWord(editor, offset);

						System.out.println(" offset = " + offset);
						System.out.println(" word = " + word);

						String caretText = editor.getDocument().getText(offset,
								word);

						System.out.println(" caretText = " + caretText);

						editor.setCaretPosition(offset);
						editor.moveCaretPosition(word);
						// now the essential to make it visible!!!
						editor.getCaret().setSelectionVisible(true);

						// editor.setCaretPosition(
						// root.getElement(line-1).getStartOffset() );

					} catch (Exception e) {
						System.out.println(" exception in activate link ");
						e.printStackTrace();
					}
				}

			};

			_status1.addMouseListener(mouseListener);

		}

		public void paint(Graphics g) {
			super.paint(g);
		}

		private String getStatusText() {
			return _status1.getText();
		}

		public class StatusCleaner extends Thread {
			StatusEvent _evt;

			public StatusCleaner(StatusEvent evt) {
				_evt = evt;
				// System.out.println(" in  StatusCleaner ctor ");
				// System.out.println(" in run status text = " +
				// getStatusText());

			}

			public void run() {

				// System.out.println(" in  StatusCleaner run mthd ");

				try {
					Thread.sleep(STATUS_DISPLAY_TIME);
				} catch (Exception ex) {
				}

				String s = _status1.getText();
				// System.out.println(" statusCleaner s = " + s);

				if (s != null && s.equals(_evt.getMessage())) {
					// System.out.println(" in if equal ");
					_status1.setText(null);
					_status1.setIcon(null);
					// _status1.setToolTipText("");

					repaint();
					revalidate();
				}
			}

		}

	}

	// The following added for find/find next
	/**
	 * Do a search in the XML text.
	 *
	 * @param search
	 *            the text to search for.
	 * @param matchcase
	 *            should the search match the case.
	 * @param search
	 *            down/upward from caret the position.
	 */
	public void search(String search, boolean matchCase, boolean down,
			boolean matchWord, boolean doReplace, String replacementText,
			boolean replaceAll) {

		System.out.println(" xmleditor search  search string = " + search);
		System.out.println(" xmleditor search  match case = " + matchCase);
		System.out.println(" xmleditor search  match word = " + matchWord);
		System.out.println(" xmleditor search  do replace = " + doReplace);
		System.out.println(" xmleditor search  replace text= "
				+ replacementText);
		System.out.println(" xmleditor search  searchIndex = " + searchIndex);

		// findnextAction.setValues(search, matchCase, down, matchWord,
		// doReplace, replacementText);

		// System.out.println(" xmleditor search  find next = " +
		// _actions.findNextAction);

		// Action findnextAction = _actions.getAction(_actions.findNextAction);

		// findnextAction.setEnabled(true);

		try {

			// ov added 25/4/2005
			MyInternalFrame myf = (MyInternalFrame) getDesktopView()
					.getSelectedFrame();
			Editor editor = (Editor) myf.getTextComponent();

			int pos = editor.getCaretPosition();

			if (replaceAll && searchIndex == -1) {
				pos = 0;
				repCount = 0;
			}

			System.out
					.println(" editor caret pos ################### = " + pos);

			if (searchIndex != -1) {
				if (searchDirection != down)
					reset = true;
				// down = searchDirection;
				// search = newSearch;
				// matchWord = wholeWord;
				// doReplace = replace;
				// replacementText = repText;
				if (!down)
					pos -= search.length();
				else
					pos += search.length();

			} else {
				searchDirection = down;
				// newSearch = search;
				// wholeWord = matchWord;
				// replace = doReplace;
				// repText = replacementText;
			}

			System.out.println(" pos = " + pos);
			System.out.println(" down = " + down);

			// if (down) {
			if (reset) {
				searchIndex = -1;
			}

			if (searchIndex == -1) {

				System.out.println(" in sindex -1 ");

				try {
					Document doc = editor.getDocument();
					if (!down && !replaceAll)
						searchData = doc.getText(0, pos);
					else
						searchData = doc.getText(pos, doc.getLength() - pos);
					searchIndex = pos;
				} catch (BadLocationException ex) {
					// warning(ex.toString());
					throw ex;
					// return -1;
				}
			}

			System.out.println(" searchIndex " + searchIndex);

			if (!matchCase) {
				searchData = searchData.toLowerCase();
				search = search.toLowerCase();
			}

			if (matchWord) {
				for (int k = 0; k < xutils.WORD_SEPARATORS.length; k++) {
					if (search.indexOf(xutils.WORD_SEPARATORS[k]) >= 0) {
						// warning("The text target contains an illegal "+
						// "character \'"+xutils.WORD_SEPARATORS[k]+"\'");

						MessageFormat mf;
						System.out.println(" getstring line col nos = "
								+ ExaltoResource.getString(ERR,
										"status.line.no"));
						String[] args = new String[1];
						args[0] = "" + xutils.WORD_SEPARATORS[k];

						statusLine = xutils.getFormattedMsg(ExaltoResource
								.getString(ERR,
										"search.string.contains.illegal.char"),
								args);
						fireStatusChanged(new StatusEvent(statusLine, 0,
								ColWidthTypes.ERROR));
						// return -1;
						return;
					}
				}
			}

			int xStart = -1;
			int xFinish = -1;
			while (true) {
				System.out.println(" inside while true pos " + pos);
				System.out.println(" searchindex " + searchIndex);

				System.out.println(" down " + down);
				System.out.println(" search " + search);
				System.out.println(" searchdata " + searchData);

				if (!down) {
					System.out.println(" searching up ");

					xStart = searchData.lastIndexOf(search, pos - 1);
				} else {
					System.out.println(" searching down search " + search);
					System.out.println(" searching down pos " + pos);
					if (!matchWord)
						xStart = searchData.indexOf(search, pos - searchIndex);
					else
						xStart = searchData.indexOf(search, pos);
				}

				System.out.println(" xstart = " + xStart);

				if (xStart < 0) {
					// warning("Text not found");

					System.out.println(" xstart < 0 exiting ");

					if (replaceAll) {
						MessageFormat mf;
						String[] args = new String[1];
						args[0] = "" + repCount;
						statusLine = xutils.getFormattedMsg(ExaltoResource
								.getString(ERR, "replace.all.count"), args);
						fireStatusChanged(new StatusEvent(statusLine, 0,
								ColWidthTypes.DEFAULT));
						return;
					}
					fireStatusChanged(new StatusEvent(ExaltoResource.getString(
							ColWidthTypes.ERR, "search.string.not.found"), 0,
							ColWidthTypes.ERROR));
					return;
				}

				System.out.println(" didnt exit matchWord = " + matchWord);

				xFinish = xStart + search.length();
				if (matchWord) {
					boolean s1 = xStart > 0;
					boolean b1 = s1
							&& !xutils.isSeparator(searchData
									.charAt(xStart - 1));
					boolean s2 = xFinish < searchData.length();
					boolean b2 = s2
							&& !xutils.isSeparator(searchData.charAt(xFinish));

					System.out.println(" b1 = " + b1);
					System.out.println(" b2 = " + b2);

					if (b1 || b2)// Not a whole word
					{
						if (!down && s1)// Can continue up
						{
							System.out.println(" continuing in up direction ");
							pos = xStart;
							continue;
						}

						if (down && s2)// Can continue down
						{
							System.out
									.println(" continuing in down direction ");
							pos = xFinish + 1;
							continue;
						}

						// Found, but not a whole word, and we cannot continue
						// warning("Text not found");
						fireStatusChanged(new StatusEvent(
								ExaltoResource.getString(ColWidthTypes.ERR,
										"search.string.not.found"), 0,
								ColWidthTypes.ERROR));
						return;
					}
				}
				break;
			}

			if (down) {
				xStart += searchIndex;
				xFinish += searchIndex;
			}

			if (doReplace) {
				editor.setSelection(xStart, xFinish, down);
				System.out.println(" incrementing replacing reptext = "
						+ replacementText);
				editor.replaceSelection(replacementText);
				repCount++;
				editor.setSelection(xStart, xStart + replacementText.length(),
						down);
				searchIndex = -1;
			} else {

				System.out.println(" caret pos before selection = "
						+ editor.getCaretPosition());
				editor.setSelection(xStart, xFinish, down);
				System.out.println(" caret pos after selection = "
						+ editor.getCaretPosition());

			}

			searchIndex += search.length();
			System.out.println(" searchIndex new " + searchIndex);

			if (replaceAll)
				search(search, matchCase, down, matchWord, doReplace,
						replacementText, replaceAll);

			return;

		}

		/*
		 * 
		 * 
		 * String text = editor.getText( 0, editor.getDocument().getLength());
		 * 
		 * if ( !matchCase) { text = text.toLowerCase(); newSearch =
		 * search.toLowerCase(); }
		 * 
		 * int index = 0; if ( down) { index = text.indexOf( newSearch,
		 * editor.getCaretPosition()); } else { int pos =
		 * editor.getCaretPosition() - (search.length()+1); if ( pos > 0) {
		 * index = text.lastIndexOf( newSearch, pos); } else { index =
		 * text.lastIndexOf( newSearch, editor.getCaretPosition()); } }
		 * 
		 * if ( index != -1) { editor.select( index, index+search.length());
		 * fireStatusChanged(new
		 * StatusEvent(ExaltoResource.getString(colWidthTypes
		 * .ERR,"search.completed"),0, colWidthTypes.NOERROR));
		 * 
		 * 
		 * } else { fireStatusChanged(new
		 * StatusEvent(ExaltoResource.getString(colWidthTypes
		 * .ERR,"search.string.not.found"),0, colWidthTypes.ERROR));
		 * 
		 * }
		 */
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getXmlFile() {
		return inputFiles[XHTML];
	}

	public String getXslFile() {
		return inputFiles[XSL];
	}

	public String getFmtObjFile() {
		return inputFiles[FO];
	}

	public void setFmtObjFile(String fofile) {
		inputFiles[FO] = fofile;
	}

	public void setXmlFile(String xmlf) {
		inputFiles[XHTML] = xmlf;
	}

	public void setXslFile(String xslf) {
		inputFiles[XSL] = xslf;
	}

	public void setPdfFile(String pdf_file) {
		pdfFile = pdf_file;
	}

	public void setStatus(String text) {

		statusTxt.setText(text);
		// statusBar.revalidate();
	}

	public XmlEditor getXmlEditor() {
		return this;
	}

	public Properties getProperty() {
		return props;
	}

	public Properties getPreferences() {
		return prefs;
	}

	public String getPropertiesFile() {
		return propertiesFile;
	}

	public String getConfigFile() {
		String cfgFile = this.idePath + configFile;
		return cfgFile;
	}

	public DesktopView getDesktopView() {
		return desktopView;
	}

	/**
	 * prepares the application to quit, asking the user if they want to save
	 * each of the open files. If the user selects "cancel" on any of the open
	 * file save questions, this returns false.
	 *
	 * @return true if the application can quit, false otherwise
	 */
	protected boolean checkQuit() {
		boolean ret = true;

		// run through all the documents in order from top to bottom
		// MerlotDebug.msg("checkQuit(): _documents = "+_documents);

		/*
		 * 
		 * JInternalFrame[] frames = desktopView.getAllFrames(); for (int
		 * i=0;i<frames.length;i++) { try { frames[i].setIcon(false); } catch
		 * (PropertyVetoException ex) { // MerlotDebug.exception(ex); }
		 * 
		 * }
		 * 
		 * while (frames.length > 0 && ret) { // find the first frame in the
		 * layer
		 * 
		 * for (int i=0;i < frames.length;i++) { if
		 * (desktopView.getPosition(frames[i]) == 0) { try {
		 * frames[i].setClosed(true); } catch (PropertyVetoException ex){ ret =
		 * false; } } } frames = desktopView.getAllFrames(); }
		 */

		ret = desktopView.closeAllProjects();

		return ret;

	}

	/**
	 * Allow the user to select a file to open, and then check to see if it's
	 * already open or not. If its not open, open it and create the XMLEditorDoc
	 * for it.
	 */
	public boolean checkFileExists(File openFile) {

		try {

			if (openFile != null) {

				System.out.println(" file not null ");

				// see if it exists in the map of files to frames
				Object o = fileToFrameMap.get(openFile.getAbsolutePath());
				if (o != null) {
					// bring that frame to the front instead

					System.out.println(" o not null ");

					if (o instanceof JInternalFrame) {

						System.out.println(" o inst intfrm ");

						try {
							((JInternalFrame) o).setSelected(true);
							return true;
						} catch (PropertyVetoException ex) {
						}

					} else
						System.out.println(" o inst intfrm not ");

				}

			}
		} catch (Exception ex) {
			logger.debug(xutils.getResourceString("xml.file.open.w"));
		}

		return false;

	}

	/**
	 * Allow the user to select a file to open, and then check to see if it's
	 * already open or not. If its not open, open it and create the XMLEditorDoc
	 * for it.
	 */
	public boolean checkFileExistsDelete(String openFile) {

		try {

			if (openFile != null) {

				System.out.println(" file not null ");

				if (fileToFrameMap.containsKey(openFile)) {

					Object o = fileToFrameMap.get(openFile);

					o = null;

					fileToFrameMap.remove(openFile);

					return true;
				}

			}
		} catch (Exception ex) {
			logger.debug(xutils.getResourceString("xml.file.open.w"));
		}

		return false;

	}

	public void addStatusListener(StatusListener listener) {
		_statusListeners.add(listener);
	}

	public synchronized void fireStatusChanged(StatusEvent evt) {

		// System.out.println(" in fireStatusChanged ");

		Enumeration e = _statusListeners.elements();
		// System.out.println(" _statusListeners size " +
		// _statusListeners.size());

		while (e.hasMoreElements()) {
			// System.out.println(" listener count ");
			Object o = e.nextElement();
			if (o instanceof StatusListener) {
				StatusListener l = (StatusListener) o;
				l.statusChanged(evt);
			}
		}
	}

	public void statusChanged(StatusEvent evt) {
		// System.out.println(" index = " + evt.getIndex());

		// System.out.println(" in StatusChanged msg= " + evt.getMessage());

		final String msg = evt.getMessage();

		// System.out.println(" in StatusChanged msgtype= " + evt.getMsgType());

		// System.out.println(" in StatusChanged msg= " + msg);

		if (evt.getMsgType().intern() == ColWidthTypes.VALIDATION.intern()) {

			// OV added on 10/12/2005 for new status display
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					// System.out.println(" status =  " + msg);

					if (bpsm != null) {
						System.out.println(" bpsm showing  " + bpsm.isShowing());
						if (bpsm.isShowing()) {
							bpsm.dispose();
						}
					}

					URL url = xutils.getResource("errorstatus" + imageSuffix);

					bpsm = new BubblePanelSimulator(getXmlEditor(), statusBar,
							msg, url);
					DialogAnimator dec = new DialogAnimator(bpsm);
					dec.animate();

				}

			});

		}

		// OV added end

		statusBar.setStatus(evt.getMessage(), evt.getIndex(), evt.getMsgType());
		// setStatus(evt.getMessage());
		Thread r = statusBar.new StatusCleaner(evt);
		r.start();
	}

	public void updateCaretStatus() {
		// if(!isShowing())
		// return;
		try {

			// if (showCaretStatus)
			// {
			MyInternalFrame buffer = (MyInternalFrame) desktopView
					.getSelectedFrame();

			if (!buffer.isLoaded())
			// ||
			/* can happen when switching buffers sometimes */
			// buffer != view.getTextArea().getBuffer())
			{
				// fireStatusChanged(new
				// StatusEvent(ExaltoResource.getString(colWidthTypes.ERR,"table.layout.complete")));
				// caretStatus.setText(" ");
				return;
			}

			Editor editor = (Editor) buffer.getTextComponent();

			int currLine = editor.getCaretLine();
			// System.out.println(" Curr line in editor = " + currLine);

			// there must be a better way of fixing this...
			// the problem is that this method can sometimes
			// be called as a result of a text area scroll
			// event, in which case the caret position has
			// not been updated yet.
			// if(currLine >= buffer.getLineCount())
			// return; // hopefully another caret update will come?

			int start = editor.getLineStartOffset(currLine);
			int dot = editor.getCaretPosition() - start;

			// see above
			if (dot < 0)
				return;

			// editor.getDocument().getText(start,dot);
			// int virtualPosition = MiscUtilities.getVirtualWidth(seg,
			// buffer.getTabSize());

			// buf.setLength(0);
			// buf.append(Integer.toString(currLine + 1));
			lineNumber = Integer.toString(currLine + 1);
			// buf.append(',');
			// buf.append(Integer.toString(dot + 1));
			colNumber = Integer.toString(dot + 1);

			MessageFormat mf;
			// System.out.println(" getstring line col nos = " +
			// ExaltoResource.getString(ERR,"status.line.no"));
			String[] args = new String[2];
			args[0] = lineNumber;
			args[1] = colNumber;

			statusLine = xutils.getFormattedMsg(
					ExaltoResource.getString(ERR, "status.line.no"), args);

			// System.out.println(" statusLine " + statusLine);

			/*
			 * if (virtualPosition != dot) { buf.append('-');
			 * buf.append(Integer.toString(virtualPosition + 1)); }
			 * 
			 * buf.append(' ');
			 * 
			 * int firstLine = textArea.getFirstLine(); int visible =
			 * textArea.getVisibleLines(); int lineCount =
			 * textArea.getDisplayManager().getScrollLineCount();
			 * 
			 * if (visible >= lineCount) { buf.append("All"); } else if
			 * (firstLine == 0) { buf.append("Top"); } else if (firstLine +
			 * visible >= lineCount) { buf.append("Bot"); } else { float percent
			 * = (float)firstLine / (float)lineCount 100.0f;
			 * buf.append(Integer.toString((int)percent)); buf.append('%'); }
			 */

			fireStatusChanged(new StatusEvent(statusLine, linenos));

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(xutils.getResourceString("error.caret.update"));
		}

		// }
	}

	public void caretUpdate(CaretEvent evt) {
		// System.out.println(" in caretupdate");
		updateCaretStatus();
	}

	/*
	 * protected class MyFindReplaceListener implements FindReplaceListener {
	 * public void getNextDocument(FindReplaceEvent e) { if(documentsLeft()) {
	 * // documentsLeft() is a method coded somewhere else //
	 * myEditorPane.setDocument(nextDocument()); // nextDocument() is a method
	 * coded somewhere else FindReplaceDialog findDialog = ((FindReplaceDialog)
	 * e.getSource()); findDialog.setEditor(nextEditor()); ((FindReplaceDialog)
	 * e.getSource()).resumeOperation(); } else { resetDocuments();
	 * ((FindReplaceDialog) e.getSource()).terminateOperation(); } }
	 * 
	 * public void getFirstDocument(FindReplaceEvent e) { //
	 * myEditorPane.setDocument(firstDocument()); // firstDocument() is a method
	 * coded somewhere else FindReplaceDialog findDialog = ((FindReplaceDialog)
	 * e.getSource()); findDialog.setEditor(firstEditor()); ((FindReplaceDialog)
	 * e.getSource()).resumeOperation(); } }
	 * 
	 * private boolean documentsLeft() { JInternalFrame j[] =
	 * getDesktopView().getAllFramesInLayer
	 * (JDesktopPane.DEFAULT_LAYER.intValue()); for (int i = 0; i <
	 * getDesktopView
	 * ().getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()); i++)
	 * { MyInternalFrame myframe = (MyInternalFrame) j[i];
	 * if(!myframe.isProcessed()) return true; } return false; }
	 * 
	 * public JEditorPane nextEditor() { JInternalFrame j[] =
	 * getDesktopView().getAllFramesInLayer
	 * (JDesktopPane.DEFAULT_LAYER.intValue()); boolean found = false;
	 * MyInternalFrame myframe = null; for (int i = 0; i <
	 * getDesktopView().getComponentCountInLayer
	 * (JDesktopPane.DEFAULT_LAYER.intValue()); i++) {
	 * System.out.println(" inside for i = " + i); myframe = (MyInternalFrame)
	 * j[i]; System.out.println(" inside for i title = " + myframe.getTitle());
	 * 
	 * if(found) { myframe.setProcessed(true); findStart = myframe; return
	 * (Editor) myframe.getTextComponent(); }
	 * 
	 * if(myframe.getTitle().equals(findStart.getTitle())) { found = true; }
	 * 
	 * } return (Editor)((MyInternalFrame)j[0]).getTextComponent(); }
	 * 
	 * public void resetDocuments() { JInternalFrame j[] =
	 * getDesktopView().getAllFramesInLayer
	 * (JDesktopPane.DEFAULT_LAYER.intValue()); for (int i = 0; i <
	 * getDesktopView
	 * ().getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()); i++)
	 * { MyInternalFrame myframe = (MyInternalFrame) j[i];
	 * 
	 * if(myframe.isProcessed()) myframe.setProcessed(false); } }
	 * 
	 * public JEditorPane firstEditor() {
	 * System.out.println(" in first editor "); JInternalFrame j[] =
	 * (JInternalFrame
	 * [])getDesktopView().getAllFramesInLayer(JDesktopPane.DEFAULT_LAYER
	 * .intValue()); System.out.println(" first title = " +
	 * ((MyInternalFrame)j[0]).getTitle()); return (JEditorPane)
	 * ((MyInternalFrame) j[0]).getTextComponent(); }
	 */

	/**
	 * Retrieve the current user working directory. This could be the directory
	 * the previous file was located in or if that is null, it would be the
	 * user.dir from the System properties
	 */

	public String getCurrentDir() {
		String dir = _current_dir;
		if (dir == null) {
			dir = System.getProperty("user.dir");
		}

		System.out.println(" getcurrentdir returning " + dir);

		return dir;
	}

	public void addStatusMouseListener() {
		statusBar.addMouseListener(getDesktopView());
	}

	public void removeStatusMouseListener() {
		statusBar.removeMouseListener();
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	/*
	 * public void setCatalogFile(String cfile) { catalog = cfile; }
	 * 
	 * public String getCatalogFile() { return catalog; }
	 */
	public void updateOpenFiles() {

		System.out.println(" checksum hash = " + checkSumHash);
		boolean fileUpdated = false;

		try {

			JInternalFrame[] frames = desktopView.getAllFrames();

			// System.out.println(" no of frames " + frames.length);

			for (int i = 0; i < frames.length; i++) {
				MyInternalFrame currFrame = (MyInternalFrame) frames[i];

				if (!currFrame.isNew()) {
					String frameFile = currFrame.getFile().getAbsolutePath();

					// System.out.println(" checksum framefile " + frameFile);

					long checksum = ((Long) checkSumHash.get(frameFile))
							.longValue();
					long filesum = FileUtil.calculateFileChecksum(frameFile);

					if (checksum != filesum) {
						fileUpdated = true;

						int response = JOptionPane.showConfirmDialog(null,
								ExaltoResource.getString(ERR,
										"file.update.alert.dialog"),
								ExaltoResource.getString(ERR,
										"file.update.alert"),
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);

						if (response == JOptionPane.OK_OPTION) {
							System.out.println(" user chose ok. reopening...");
							break;
							/*
							 * doReopen(frameFile, currFrame);
							 * checkSumHash.put(frameFile, new Long(filesum));
							 * 
							 * currFrame.addInternalFrameListener(this);
							 * 
							 * ((JTextComponent)
							 * currFrame.getTextComponent()).getDocument
							 * ().addUndoableEditListener( new
							 * MyUndoableEditListener());
							 */

						}

					}
				}

			}

			if (fileUpdated) {

				for (int i = 0; i < frames.length; i++) {
					MyInternalFrame currFrame = (MyInternalFrame) frames[i];

					if (!currFrame.isNew()) {
						String frameFile = currFrame.getFile()
								.getAbsolutePath();

						// System.out.println(" checksum framefile " +
						// frameFile);

						long checksum = ((Long) checkSumHash.get(frameFile))
								.longValue();
						long filesum = FileUtil
								.calculateFileChecksum(frameFile);

						if (checksum != filesum) {
							doReopen(frameFile, currFrame, true);
							checkSumHash.put(frameFile, new Long(filesum));

							currFrame.addInternalFrameListener(this);
							// System.out.println(" adding undolistener for file "
							// + frameFile);
							((JTextComponent) currFrame.getTextComponent())
									.getDocument().addUndoableEditListener(
											new MyUndoableEditListener());
						}
					}

				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

	}

	public void performNewAction(boolean loadDoc, String fileName,
			boolean updateTitle) {

		try {

			MyInternalFrame frame = null;
			if (loadDoc) {

				long checksum = FileUtil.calculateFileChecksum(fileName);

				// System.out.println("checksum computed for file = " +
				// checksum);

				checkSumHash.put(fileName, new Long(checksum));

				// System.out.println("checksumhash after open = " +
				// checkSumHash);

				File openFile = new File(fileName);

				desktopView.display(openFile);
				frame = (MyInternalFrame) desktopView.getSelectedFrame();

				// System.out.println("xmleditor newwidtth " +
				// frame.getSize().width);
				// System.out.println("xmleditor newheight " +
				// frame.getSize().height);

				Editor editor = (Editor) frame.getTextComponent();
				editor.addCaretListener(this);
				fileToFrameMap.put(openFile.getAbsolutePath(), frame);

				if (desktopView.fileSelected())
					frame = (MyInternalFrame) desktopView.getSelectedFrame();

				updateMenuAndTitle();
				frame.addInternalFrameListener(this);

				((JTextComponent) frame.getTextComponent()).getDocument()
						.addUndoableEditListener(new MyUndoableEditListener());
			} else {

				if (desktopView.fileSelected())
					frame = (MyInternalFrame) desktopView.getSelectedFrame();

				doReopen(fileName, frame, updateTitle);

				// System.out.println(" selected framer = " + frame);
				// updateMenuAndTitle();
				frame.addInternalFrameListener(this);

				((JTextComponent) frame.getTextComponent()).getDocument()
						.addUndoableEditListener(new MyUndoableEditListener());

				if (xTree != null)
					xTree.clear();

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getDefaultViewForNewFiles() {
		if (defaultViewForNewFiles == null) {
			defaultViewForNewFiles = xutils.getResourceString("defaultview");
		}

		System.out.println(" defview  = " + defaultViewForNewFiles);

		return defaultViewForNewFiles;
	}

	public ExaltoExplorer getExplorer() {
		return explorer;
	}

	public MyInternalFrame getXmlFileFrame() {
		return xmlFileFrame;
	}

	private String getXsltVersion(String xslFile) {

		String version = null;
		boolean foundPI = false;
		boolean foundCB = false;

		try {

			Vector<String> lines = new Vector<String>();
			BufferedReader br = new BufferedReader(new FileReader(xslFile));

			String line = null;
			while ((line = br.readLine()) != null) {

				System.out.println(" line  = " + line);

				if (!foundPI) {

					if (line.contains("<xsl:stylesheet ")) {
						foundPI = true;

						if (line.contains(">")) {
							lines.add(line);
							foundCB = true;
							break;
						}
					}
				} else {
					if (!foundCB) {
						lines.add(line);
						if (line.contains(">")) {
							foundCB = true;
							break;
						}
					}
				}
			}

			if (foundPI && foundCB) {

				for (int i = 0; i < lines.size(); i++) {
					line = lines.get(i);

					if (line.contains("version=\"1.0\"")) {
						return version = "1.0";
					} else if (line.contains("version=\"2.0\"")) {
						return version = "2.0";
					}
				}

				version = "1.0";

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return version;

	}

	public void setConsole(Object console) {
		this.console = console;
	}

	public Object getConsole() {
		return this.console;
	}

	public void showConsole() {

		System.out.println(" %%%%%%%%%%%%%%% show_CONSOLE %%%%%%%%  console = "
				+ console);

		System.out.println(" %%%%%%%%%%%%%%% appenderUI %%%%%%%%   = "
				+ appenderUI);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				SwingAppenderUI swAppenderUI = null;
				if (appenderUI == null) {
					System.out.println(" in appenderUI null ");

					swAppenderUI = SwingAppenderUI.getUIInstance();
					if (console instanceof AppenderUI) {
						AppenderUI appui = (AppenderUI) console;
						swAppenderUI.setDocument(appui.getDocument());
						System.out.println(" in instof AppenderUI ");
						editorPane.setBottomComponent(swAppenderUI);

					} else {

						System.out.println(" inner else ");

					}

				} else {
					System.out.println(" outer else ");
					// if user property is inline console
					editorPane.setBottomComponent(swAppenderUI);
				}

				// else
				editorPane.setDividerLocation(300);

			}

		});
		// repaint();
		// validate();

	}

	public void closeConsole() {

		System.out.println(" %%%%%%%%%%%%%%% CLOSE_CONSOLE %%%%%%%%  = "
				+ prefs);

		// console = editorPane.getBottomComponent();

		editorPane.setBottomComponent(null);

		repaint();
		validate();

	}

	// PREREL A FR PRINT
	public void doPrint() throws java.awt.print.PrinterException {

		System.out.println(" %%%%%%%%%%%%%%%XE  doPrint %%%%%%%%  = ");
		MyInternalFrame frame = (MyInternalFrame) desktopView
				.getSelectedFrame();

		Editor editor = (Editor) frame.getTextComponent();

		editor.doPrint();

	}

}