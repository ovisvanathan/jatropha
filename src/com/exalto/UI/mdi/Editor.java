package com.exalto.UI.mdi;

import java.awt.Color;
import java.awt.Component;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.RepaintManager;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.Position;
import javax.swing.text.Style;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.TabStop;
import javax.swing.text.View;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.resolver.Catalog;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.exalto.UI.PopupManager;
import com.exalto.UI.XmlEditor;
import com.exalto.UI.delegate.ListCompletionView;
import com.exalto.UI.delegate.ListDelegate;
import com.exalto.UI.delegate.ListSearcher;
import com.exalto.UI.delegate.MyDTDParser;
import com.exalto.UI.mdi.editor.ResponseView;
import com.exalto.UI.mdi.editor.XmlContext;
import com.exalto.UI.mdi.editor.XmlEditorKit;
import com.exalto.util.XmlUtils;
import java.awt.ComponentOrientation;


import java.awt.Shape;
import java.awt.print.Printable;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;


public class Editor extends JTextPane implements MouseListener, 
InputMethodListener, DocumentListener, Printable,
//, KeyListener, 
// ov added 13/11/2004
 DragSourceListener,
    DragGestureListener 
// ov added 13/11/2004 end	
	{
	
	private int pos;
	private int tabCount = 0;
	private File theFile;
	private File xslFile;
	private boolean saved = false;
	private Vector keywords = new Vector();	private Vector keywordColors = new Vector();
	EventListenerList _listeners;
	// for caret events
	public boolean installed = false;
	// OV changed from JLIst to listcompletionview
	public ListCompletionView jcmb = null;
	private String selectedElem = null;
	Hashtable innerAttrs = null;

	Style keywordStyle;
	Style defaultStyle;
	char lastChar = '\0';
	
	private boolean wrap=true;
	
	// Added for DTD code assist 24/04/2005
//	private XMLParserCallback _pc;
	private Hashtable _dtdElements; 
	private Hashtable _entities; 
	private String DTDName;
	private MyDTDParser dtdParser = null;

	private boolean loading = false;
	

// omp added trial basis 26/2/2005
	String[] data = { "Apple", "Orange", "Grape", "Banana", "Mango", "Pineapple", "Peach", "Pear",
	"Guava", "Jackfruit", "custardapple", "Greenapple", "greengrapes", "browngrapes", "rubyred", "tangerine" } ;

	public PopupManager pmgr = null;
	JScrollPane jscp = new JScrollPane();
								

	private String idePath = System.getProperty("user.dir") + File.separator;
	
	private boolean tagCompletion = true;
	private static int defaultTabSize = 4;
	private int spaces = 4;
	private XmlEditorKit kit = null;
	private XmlContext context;
	
	// ov added begin 13/11/2004

	 /** a data flavor for transferables processed by this component */
     private DataFlavor df =
        new DataFlavor(com.exalto.UI.mdi.StyledText.class, "StyledText");

	 private static ClipboardOwner defaultClipboardOwner = new ClipboardObserver();

	 /** enables this component to be a Drag Source */
	  DragSource dragSource = null;
	
	  /** the last selection start */
	  private int lastSelStart = 0;
	
	  /** the last selection end */
	  private int lastSelEnd = 0;
	
	  /** the location of the last event in the text component */
	  private int dndEventLocation = 0;
	  
	  private boolean dragStartedHere = false;
	  private XmlEditor xmlEditor;
	  private XmlUtils xutils;
	  
	  private ListDelegate listDelegate = null;
	  
	  protected String catalogFile = null;
	  protected Vector userEntities = new Vector();
	  protected String [] predefinedEntities = null;
	  
	  
      private           DocumentBuilderFactory 	dbf;
	  private           DocumentBuilder 		db;
	  private           org.w3c.dom.Document                    domDoc;

		// ov added end 13/11/2004 
	  KeyboardFocusManager manager = null;
	  ListSearcher lstSearch = null;
	  
	  // ov added 31/07/2005 for assign dtd schema
	  private File dtdFile;
	  private File schemaFile;

 double currentPage = -1;
            double pageStartY = 0;
            double pageEndY = 0;

	  
	public Editor(String s, int width, int height) {
		super();

	// ov added begin 13/11/2004
		
		dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(
		                          this, DnDConstants.ACTION_MOVE, this);
			
		 try {	
			jbInit();
		} catch(Exception e) {
    		e.printStackTrace();
    	}	
		
	// ov added end 13/11/2004
	
		xmlEditor = XmlEditor.getInstance("");
		xutils = XmlUtils.getInstance();
	
	// Omprakash commented 04/12/2005 to make factory configurable
	//	System.setProperty("javax.xml.parsers.SAXParserFactory", 
	//	"org.apache.xerces.jaxp.SAXParserFactoryImpl");
	//	System.setProperty("javax.xml.parsers.DocumentBuilderFactory", 
	//	"com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
			
		catalogFile = xutils.getCatalogFile();
		predefinedEntities = xutils.getPredefinedEntities();
		for(int k=0;k<predefinedEntities.length;k++)
			userEntities.add(predefinedEntities[k]);
				
		kit = new XmlEditorKit( this);		
		context = kit.getContext();

     //   System.out.println(" kit.getContentType() " + kit.getContentType());

		setEditorKitForContentType( kit.getContentType(), kit);
		setBackground( Color.white);

		setContentType( kit.getContentType());
		super.setFont( kit.getFont());

		setEditable( true);
		
		saved = true;
		
		setPreferredSize(new Dimension(width, height));
		
//		setSelectionColor(Color.red);
		int fontSize = 12;
		int tabsize = 4;
//		defaultStyle = getStyle(StyleContext.DEFAULT_STYLE);
		TabStop [] ts = new TabStop[256];
		for (int i = 0; i < 256; i++) {
			ts[i] = new TabStop(tabsize * (fontSize + fontSize / tabsize) * (i + 1) / 2);
		}
		
		
		
		

		
		
			
//		StyleConstants.setTabSet(defaultStyle, new TabSet(ts));
//		StyleConstants.setFontFamily(defaultStyle, "Courier");
//		StyleConstants.setFontSize(defaultStyle, 12);
//		keywordStyle = addStyle("keywords", null);
//		StyleConstants.setBold(keywordStyle, true);
//		StyleConstants.setTabSet(keywordStyle, new TabSet(ts));
	
		this.setWordWrap(true);
		context.setWordWrap(false);
		
		
		//OV cmt on 31/12/08
	//	addMouseListener(this);
		
	
		//OV comment 02/01/09
//		addKeyListener(this);
			
		//		try {
//		Class c = Class.forName("Tester");
//		Method arr[] = c.getDeclaredMethods();
//		for (int i = 0; i < arr.length; i++) {
//			System.out.println(arr[i].getName());
//		}
//		} catch (Exception e) {
//			System.out.println(e);
//		}

	}
	
	int indent = 4;
	public void printViewHierarchy(View root) {
		
			int n = root.getViewCount();

		//	System.out.println(" n = " + n);
			
			for(int i=0;i<n;i++) {
				
				
				View view = root.getView(i);

		//		for(int k=0;k<indent;k++)
		//			System.out.print(" ");
		//		System.out.println(" i = " + i);
		//		System.out.println(" view = " + view);

				indent += 4;
				
		//		if(!(view instanceof LabelView)) { 
		//			System.out.println(" calling printViewHierar ");
		//			printViewHierarchy(view);
		//		}
				
			}
			
		
		
	}
	
	public void addKeyword(String word, Color c) {
		keywords.add(word);
		keywordColors.add(c);
	}
	/*
	public void process() {
		final Runnable checkSyntax = new Runnable() {
			public void run() {
				String s = new String();
				try {
					int caret = getCaretPosition();
					
					System.out.println("in process 0 arg run method caret = " + caret);

					s = getDocument().getText(0, getDocument().getLength());
					StyledDocument sd = getStyledDocument();
					StyleConstants.setForeground(defaultStyle, Color.black);
					sd.setParagraphAttributes(0, s.length(), defaultStyle, true);
					sd.setCharacterAttributes(0, s.length(), defaultStyle, true);
					int pos = s.indexOf("\"");
					StyleConstants.setForeground(keywordStyle, new Color(0, 128, 0));
					while (pos != -1) {
						int end = s.indexOf("\"", pos + 1);
						if (end == -1) end = s.length();
						sd.setCharacterAttributes(pos, end - pos + 1, keywordStyle, false);
						pos = s.indexOf("\"", end + 1);
					}
					System.out.println("char attribs = " + getCharacterAttributes().getAttribute(StyleConstants.Foreground));
					for (int i = 0; i < keywords.size(); i++) {
						pos = s.indexOf((String) keywords.get(i), 0);
						while (pos != -1) {
							StyleConstants.setForeground(keywordStyle, (Color) keywordColors.get(i));
							setCaretPosition(pos);
							Color c = (Color) getCharacterAttributes().getAttribute(StyleConstants.Foreground);
							setCaretPosition(caret);
							if (!c.equals(new Color(0, 128, 0))) {
								if ((pos == 0 || (!Character.isLetterOrDigit(s.charAt(pos - 1)))) && (pos + ((String) keywords.get(i)).length() <= s.length() && !Character.isLetterOrDigit(s.charAt(pos + ((String) keywords.get(i)).length())))) {
			    						sd.setCharacterAttributes(pos, ((String) keywords.get(i)).length(), keywordStyle, false);
								}
							}
							pos = s.indexOf((String) keywords.get(i), pos + ((String) keywords.get(i)).length());
						}
					}
					System.out.println("curr pos = " + caret);
					setCaretPosition(caret);
				} catch (BadLocationException ble) {
					System.out.println(ble);
				}
			}
		};
	
	
	Thread appThread = new Thread() {
			public void run() {
				try {
					checkSyntax.run();
				} catch (Exception e) {
				}
			}
		};
		appThread.start();
	
	
	}
	*/
	public File getFile() {
		return theFile;
	}

	public org.w3c.dom.Document setFile(File f) {
		theFile = f;
		String pubSys = null;

		try {

		//	System.out.println(" in displayCodeAssist f =" + f.getAbsolutePath());						

			try {
			
					CatalogResolver cr = null;
					Catalog myCatalog = null;

					try {
				
			//			System.out.println("catalogFile =" + catalogFile);

						if(catalogFile == null) {
							catalogFile = xutils.getCatalogFile();
				//			if(catalogFile == null) {
				//				System.out.println("catalogFile is null");
				//			}
						}	

						cr = new CatalogResolver();
						myCatalog = cr.getCatalog();
					 
					 	if(catalogFile != null)
						   myCatalog.parseCatalog(catalogFile);

						} catch(Exception e) {			
							e.printStackTrace();
						}			
			
			org.w3c.dom.DocumentType docType = null;			
			if(f.exists() && !f.getName().startsWith("untitled")) {
			//	pubSys = xutils.parseXmlWithCatalog(theFile);
			
					DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
					DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
					docbuilderfact.setValidating(true);

					if(catalogFile != null) 
						docbuilder.setEntityResolver(cr);

					domDoc = docbuilder.parse(new FileInputStream(theFile.getAbsolutePath()));

					docType = domDoc.getDoctype();
					if(docType != null)
					{
					
						docType.getInternalSubset();
						NamedNodeMap entityMap = docType.getEntities();
						for(int i=0;i<entityMap.getLength();i++) {
							Node eitem = entityMap.item(i);
							String ename = eitem.getNodeName();
			//				System.out.println(" entity name " + ename);
							userEntities.add(ename);
						}
						
						String resolvePub = null;
						String resolveSys = null;
						if(catalogFile != null) {
							resolvePub = myCatalog.resolvePublic(docType.getPublicId(), docType.getSystemId());
							resolveSys = myCatalog.resolveSystem(docType.getSystemId());
						} else {
							resolvePub = docType.getPublicId();	
							resolveSys = docType.getSystemId();	
						}

			//		System.out.println(" resolvePub  " + resolvePub);
			//		System.out.println(" resolveSys " + resolveSys);
					
			//		System.out.println(" strip " + xutils.stripURL(resolvePub));
					
					if(resolvePub == null || catalogFile == null)  {
						pubSys = resolveSys;
					} else {
					
						if(resolvePub.startsWith("http:") || resolvePub.startsWith("file:")) {
							pubSys = xutils.stripURL(resolvePub);
						} else {
							pubSys = resolvePub;
						}
					}
		
				}
					
			}
					
			} catch(Exception e) {
		//		e.printStackTrace();
			}

			
		//	System.out.println(" pubsys in editor " + pubSys);
			
			if(pubSys != null) {
				dtdParser = new MyDTDParser(pubSys); 
				dtdParser.parseDTD();  // parse the dtd;
				_dtdElements = dtdParser.getDTDElements();
				_entities = dtdParser.getEntities();
		
			}
	
		 
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		
	//	if(pubSys != null) 
	//		listDelegate = new ListDelegate(this, theFile.getName(), theFile.getAbsolutePath());

		return domDoc;		
		
	}
	
	
	public void save() {
		write();
		saved = true;
	}
	private void write() {
		try {
		System.out.println(" in  editor write theFile = " + theFile );
		
			PrintStream oos = new PrintStream(new FileOutputStream(theFile), true);
			oos.print(getText());
			oos.flush();
			oos.close();
			saved = true;
		} catch (IOException ioe) {
			System.out.println("Editor write - IOException");
		}
	}

	public void open() {
		if (theFile != null) {
			read();
		}
	}
	private void read() {
		try {
		
			System.out.println(" in editor read");						
		
			BufferedReader bufferedReader = new BufferedReader(new FileReader(theFile));
			read(bufferedReader, null);
	
//			setCaretPosition(0);
//			process();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			System.out.println("Editor read - IOException");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int getLineEndOffset(int n) {
		try {
			String s = getDocument().getText(0, getDocument().getLength());
			int end = s.indexOf("\n", n);
			if (end == -1) {
				end = s.length();
			}
			return end;
		} catch (Exception e) {
		}
		return 0;
	}
	public int getLineStartOffset(int line) {
		try {
		
		/*
			String s = getDocument().getText(0, getDocument().getLength());
			int start;
			if (n == 0) {
				start = 0;
			} else  {
				start = s.lastIndexOf("\n", n - 1);
				if (start == -1) return 0;
				start++;
			}
			return start;
			
		*/
		 Element map = getDocument().getDefaultRootElement();
			if (line < 0) {
				throw new BadLocationException("Negative line", -1);
			} else if (line >= map.getElementCount()) {
				throw new BadLocationException("No such line", getDocument().getLength()+1);
			} else {
				Element lineElem = map.getElement(line);
				return lineElem.getStartOffset();
			}

		} catch (Exception e) {
		}
		return 0;
	}

/*	
	public void keyPressed(KeyEvent ke) {
		try {
		
		System.out.println(" ####################IN KEY PRESSED############");
	
			if (ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_DELETE) {
				String s = getText();
				if (s.length() > 0) {
					System.out.println(" slen = " + s.length());
					System.out.println(" curr caret pos  = " + getCaretPosition());
					int caretPos = getCaretPosition();
					if(caretPos != 0)
						lastChar = s.charAt(caretPos - 1);
					else 
						lastChar = s.charAt(caretPos);
				}
			}
			if (ke.getKeyChar() ==  '<' ) {			
			
				System.out.println(" ####################LESS THAN PRESSED############");
			
				try {
				File loadedFile = getFile();
							
		//		listDelegate.displayCodeAssistDTD();
		
				pos = getCaretPosition();
		
				// OMP changes 15/5/2005 BEGIN
				Vector innerElems = null;
				if(listDelegate != null)
					innerElems = listDelegate.getAllowedElements(this, pos);
				
				int currLine = getCaretLine();
				System.out.println(" Curr line in editor = " + currLine);

				int start = getLineStartOffset(currLine);

				System.out.println(" start = " +  start);
				int dot = getCaretPosition() - start;
				System.out.println(" dot = " +  dot);
				
		//		Point point = getCaret().getMagicCaretPosition();

				Rectangle bounds = getUI().modelToView(this, dot, Position.Bias.Forward);
		//		Rectangle bounds = new Rectangle((int)point.getX(), (int) point.getY(), 80, 100);

				System.out.println(" input x = " +  bounds.x);
				System.out.println(" input y = " + bounds.y);
				System.out.println(" input height = " + bounds.height);
				System.out.println(" input width = " + bounds.width);


			//	 Place popup inside the scrollbar's viewport 
				 PopupManager.HorizontalBounds viewPortBounds = new PopupManager.HorizontalBounds("ViewPort"); 
				//NOI18N

				// Place popup inside the whole scrollbar 
				//	PopupManager.HorizontalBounds scrollBarBounds = new PopupManager.HorizontalBounds("ScrollBar"); //NOI18N

				System.out.println(" innerElems  = " + innerElems);  

				//OMP added 3/4/3005 for intellisense - code in xtree2\jlist
				if(innerElems != null && innerElems.size() > 0) {
				
				 System.out.println("In less than pressed setting install true");	
					installed = true;
					
					jcmb = new ListCompletionView();
					jcmb.setResult(innerElems);
					jcmb.setCellRenderer( new CustomCellRenderer() );
					jcmb.setPrototypeCellValue("Index 12345");
					jcmb.setVisibleRowCount(4);
		
				//	Font displayFont = new Font("Serif", Font.BOLD, 10);
				//	jcmb.setFont(displayFont);
				
				jcmb.addMouseListener(this);
		
			manager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();
		
		//	jcmb.addKeyListener(this);
			
			
			MyInternalFrame myf = (MyInternalFrame) xmlEditor.getDesktopView().getSelectedFrame();
			Editor etr = (Editor) myf.getTextComponent();
			
			lstSearch = new ListSearcher(etr, jcmb, manager, "element");
			
			manager.addKeyEventDispatcher(lstSearch);
					
				jscp.getViewport().setMinimumSize(new Dimension(4,4));
				Dimension d = jcmb.getPreferredSize();
				jscp.getViewport().setPreferredSize(d);
			
					jscp.getViewport().add(jcmb);
					jscp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					jcmb.setBorder( BorderFactory.createLoweredBevelBorder() );
					pmgr = new PopupManager(this);

	//		 	    pmgr.install(jscp, bounds, new PopupManager.Placement("Below"), viewPortBounds, 0, 0);
					pmgr.install(jscp);				
	//				Window w = getFocusWindow(jcmb);
					requestFocus(jcmb);
	
				}
				
				
				
				
				
				// OMP changes 15/5/2005 END
				
				} catch(Exception e) {
					e.printStackTrace();
				}

			}
			if (ke.getKeyCode() == KeyEvent.VK_SPACE ) {
				System.out.println(" ####################SPACE PRESSED############");

				pos = getCaretPosition();
				// OMP changes 15/5/2005 BEGIN
			
				// getting attributes
				Vector attrVec = null;
				if(listDelegate != null)
					attrVec = listDelegate.getAttributeVector(this, pos, selectedElem);
				
				
				int currLine = getCaretLine();
				System.out.println(" Curr line in editor = " + currLine);
				
				int start = getLineStartOffset(currLine);

				System.out.println(" start = " +  start);
				int dot = getCaretPosition() - start;
				System.out.println(" dot = " +  dot);

		//		Point point = getCaret().getMagicCaretPosition();

				Rectangle bounds = getUI().modelToView(this, dot, Position.Bias.Forward);
		//		Rectangle bounds = new Rectangle((int)point.getX(), (int) point.getY(), 80, 100);

				System.out.println(" input x = " +  bounds.x);
				System.out.println(" input y = " + bounds.y);
				System.out.println(" input height = " + bounds.height);
				System.out.println(" input width = " + bounds.width);


			//	 Place popup inside the scrollbar's viewport 
				 PopupManager.HorizontalBounds viewPortBounds = new PopupManager.HorizontalBounds("ViewPort"); 
				//NOI18N

				// Place popup inside the whole scrollbar 
				//	PopupManager.HorizontalBounds scrollBarBounds = new PopupManager.HorizontalBounds("ScrollBar"); //NOI18N

				//OMP added 3/4/3005 for intellisense - code in xtree2\jlist
				
				if(attrVec != null && attrVec.size() > 0) {
					installed = true;
					jcmb = new ListCompletionView();
					jcmb.setResult(attrVec);
					jcmb.setCellRenderer( new CustomCellRenderer() );
					jcmb.setPrototypeCellValue("Index 12345");
				
					jcmb.setVisibleRowCount(4);
				//	Font displayFont = new Font("Serif", Font.BOLD, 10);
				//	jcmb.setFont(displayFont);

				jcmb.addMouseListener(new MouseAdapter() {
				     public void mouseClicked(MouseEvent e) {
				         if (e.getClickCount() == 2) {
						 	 ListCompletionView mjlist = getEditor().getCombo();	
				             int index = mjlist.locationToIndex(e.getPoint());
				             System.out.println("Double clicked on Item " + index);
							 System.out.println("Double clicked on Item val " + mjlist.getSelectedValue());
							 String selectedItem = (String) jcmb.getSelectedValue();
							 selectedItem = selectedItem + "=\"\"";
							 System.out.println("attr sel elem  " + selectedItem);				   
							 replaceSelection(selectedItem);
							 pmgr.uninstall(jscp);
							 manager.removeKeyEventDispatcher(lstSearch);
							 System.out.println("space pressed settin g installed false");
							 installed = false;
						 }
				     }
				 });
				 
			
			manager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();

			MyInternalFrame myf = (MyInternalFrame) xmlEditor.getDesktopView().getSelectedFrame();
			Editor etr = (Editor) myf.getTextComponent();

			lstSearch = new ListSearcher(etr, jcmb, manager, "attribute");
			
			manager.addKeyEventDispatcher(lstSearch);	

				jcmb.addKeyListener(this);
				
					jscp.getViewport().add(jcmb);
					jcmb.setBorder( BorderFactory.createLoweredBevelBorder() );
					jscp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	//		 	    pmgr.install(jscp, bounds, new PopupManager.Placement("Below"), viewPortBounds, 0, 0);
					pmgr.install(jscp);				
					
	//				Window w = getFocusWindow(jcmb);
					requestFocus(jcmb);
	

				}
			

				
				}
			
			if (ke.getKeyCode() == KeyEvent.VK_ESCAPE ) {
				System.out.println(" $$$$ESCAPE PRESSED$$$$$installed = " + installed);
				System.out.println(" $$$$ESCAPE PRESSED jlist visible = " + jcmb.isVisible());
				
				System.out.println(" $$$$ESCAPE PRESSED$$$$$curr file name = " + getFile().getName());
			
				MyInternalFrame myf = (MyInternalFrame) xmlEditor.getDesktopView().getSelectedFrame();
				Editor etr = (Editor) myf.getTextComponent();
				
			//	if(installed) {
				if(etr.jcmb.isVisible()) {
					etr.pmgr.uninstall(jscp);
					etr.installed = false;
					etr.repaint();
					etr.revalidate();
					
				}
		//		listDelegate.undisplay(); 
			}

			
			if (ke.getKeyCode() == KeyEvent.VK_ENTER) {			
			
				System.out.println( "In enter press focus owner = " + DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
				Component fowner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

				System.out.println( "focus owner class name = " + fowner.getClass().getName());

				if(fowner.getClass().getName().equals("com.exalto.UI.delegate.ListCompletionView")) {
					 grabFocus();
					 
					 System.out.println( "focus owner class name 2 = " + fowner.getClass().getName());

					 int index = jcmb.getSelectedIndex();
					 System.out.println("enter pressed on Item " + index);
					 System.out.println("enter pressed  on Item val " + jcmb.getSelectedValue());
					 String selectedItem = (String) jcmb.getSelectedValue();
				//	 selectedItem = selectedItem + "=\"\"";
					 System.out.println("curr sel elem  " + selectedItem);				   
					 replaceSelection(selectedItem);
					 pmgr.uninstall(jscp);
					 
					 System.out.println("In enter pressed setting install false");					 
					 installed = false;			
					 manager.removeKeyEventDispatcher(lstSearch);
					 ke.consume();
				
				} 
				
					System.out.println( "In enter press past list ");
			
				
					int start = getLineStartOffset(getCaretPosition());
					int end = getLineEndOffset(getCaretPosition());
					String s = getText(start, end - start);
					if (s.indexOf("{") != -1 && s.indexOf("{") > s.indexOf("}")) {
						tabCount = 1;
					}
					while (s.startsWith("\t")) {
						tabCount++;
						s = s.substring(1, s.length());
					}
					String tabs = "";
					for (int i = 0; i < tabCount; i++) {
						tabs += "\t";
					}
					replaceSelection(tabs);
					System.out.println( "In enter press getCaretPosition() " + getCaretPosition());
					System.out.println( "In enter press tabCount " + tabCount);

					setCaretPosition(getCaretPosition() - tabCount);
			
							
			
			}
			else if (ke.getKeyCode() == KeyEvent.VK_F1) {
				if (!getSelectedText().equals("")) {
					File f = new File("d:\\jdk1.3.1\\docs\\api");
					LinkedList list = new LinkedList();
					addFiles(f, list);
					HelpDialog hd = new HelpDialog();
					for (int i = 0; i < list.size(); i++) {
						if (((File) list.get(i)).getName().startsWith(getSelectedText())) {
							hd.add(list.get(i).toString());
						}
					}
					hd.display();
					hd.getText();
					new HelpWindow("file:" + hd.getText());
				}
			}
			else if (ke.getKeyCode() == KeyEvent.VK_TAB) {
			
				System.out.println( "In Tab press focus owner = " + DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner());
				Component fowner = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();

				System.out.println( "focus owner class name = " + fowner.getClass().getName());
					
				if(fowner.getClass().getName().equals("com.exalto.UI.delegate.ListCompletionView")) {
					 grabFocus();
					 
					 System.out.println( "focus owner class name 2 = " + fowner.getClass().getName());

					 int index = jcmb.getSelectedIndex();
					 System.out.println("enter pressed on Item " + index);
					 System.out.println("enter pressed  on Item val " + jcmb.getSelectedValue());
					 String selectedItem = (String) jcmb.getSelectedValue();
				//	 selectedItem = selectedItem + "=\"\"";
					 System.out.println("curr sel elem  " + selectedItem);				   
					 replaceSelection(selectedItem);
					 pmgr.uninstall(jscp);
					 
				 System.out.println("In tab pressed setting install false");		 
					 installed = false;					
					 manager.removeKeyEventDispatcher(lstSearch);
 					 ke.consume();
				}

			}
			
			// TODO: OMP move this code to event handler in XMLEditor in edit menu
			else
			if (ke.getKeyCode() == KeyEvent.VK_F2) {
			
				gotoLine(6);
			}
			if (ke.getKeyChar() == '&') {
			
				System.out.println(" userEntities = " + userEntities);
			
			
				if(userEntities != null && userEntities.size() > 0) 
					showPopup(userEntities);

			
			}
			
			
		} catch (BadLocationException ble) {
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
*/	
	
	private void addFiles(File f, List list) {
		List l = Arrays.asList(f.list());
		for (int i = 0; i < l.size(); i++) {
			File nf = new File(f.toString() + "\\" + l.get(i));
			if (nf.isDirectory()) {
				addFiles(nf, list);
			} else {
				list.add(nf);
			}
		}
	}
	public void keyTyped(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
			System.out.println("inside keytyped Enter caret pos = " + getCaretPosition());
			System.out.println("tabCount = " + tabCount);

			setCaretPosition(getCaretPosition() + tabCount);
			tabCount = 0;
		}
	}
	/*
	public void process(int p) {
		try {
		System.out.println("inside process(int) arg = " + p);
		
			p = getCaretPosition();
			
			System.out.println("inside process(int) caret pos = " + p);

			int start;
			int end;
			String line = "";
			String s = getDocument().getText(0, getDocument().getLength());
			end = s.indexOf("\n", p);
			if (end == -1) {
				end = s.length();
			}
			if (end == p) {
				start = s.lastIndexOf("\n", p - 1);
			} else {
				start = s.lastIndexOf("\n", p);
			}
			if (start == -1) {
				start = 0;
			} else {
				start++;
			}
			line = s.substring(start, end);
			StyledDocument sd = getStyledDocument();
			if (line.length() > 0) {
				sd.setCharacterAttributes(start, line.length(), defaultStyle, true);
				for (int i = 0; i < keywords.size(); i++) {
					String searchString = (String) keywords.get(i);
					StyleConstants.ColorConstants.setForeground(keywordStyle, (Color) keywordColors.get(i));
					int pos = line.indexOf(searchString);
					while (pos != -1) {
						boolean startChar = pos == 0 || !Character.isLetterOrDigit(line.charAt(pos - 1));
						boolean endChar = (pos + searchString.length()) == line.length() || 
						  !Character.isLetterOrDigit(line.charAt(pos + searchString.length()));
						if (startChar && endChar) {
							sd.setCharacterAttributes(pos + start, ((String) keywords.get(i)).length(), keywordStyle, false);
						}
						pos = line.indexOf(searchString, pos + searchString.length());
					}
				}
				pos = line.indexOf("\"");
				StyleConstants.setForeground(keywordStyle, new Color(0, 128, 0));
				while (pos != -1) {
					end = line.indexOf("\"", pos + 1);
					if (end == -1) end = s.length() - 1;
					sd.setCharacterAttributes(start + pos, end - pos + 1, keywordStyle, false);
					pos = line.indexOf("\"", end + 1);
				}
			}
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}
	}
	
	*/
	
	public void keyReleased(KeyEvent ke) {
		final KeyEvent k = ke;
	/*	
		final Runnable checkSyntax = new Runnable() {
			public void run() {
				if (((k.getKeyCode() == KeyEvent.VK_DELETE || k.getKeyCode() == KeyEvent.VK_BACK_SPACE) && lastChar == '\"') || k.getKeyChar() == '\"') {
					process();
				} else {
					process(0);
				}
			}
		};
		
		Thread appThread = new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeLater(checkSyntax);
				} catch (Exception e) {
				}
			}
		};
		
		if (Character.isLetterOrDigit(ke.getKeyChar()) || ke.getKeyChar() == '\"' || ke.getKeyCode() == KeyEvent.VK_BACK_SPACE || ke.getKeyCode() == KeyEvent.VK_DELETE) {
			appThread.start();
		}
	
	*/
	/*
		final Runnable parser = new Runnable() {
			public void run() {
				try {
					int p = 0;
					int start;
					int end;
					//String line = "";
					String s = getDocument().getText(0, getDocument().getLength());
					end = s.indexOf("\n", p);
					if (end == -1) {
						end = s.length();
					}
					if (end == p) {
						start = s.lastIndexOf("\n", p - 1);
					} else {
						start = s.lastIndexOf("\n", p);
					}
					if (start == -1) {
						start = 0;
					} else {
						start++;
					}
//					line = s.substring(start, end);
					while (p < s.length()) {
						String line = s.substring(start, end);
						line = line.trim();
						if (line.startsWith("public") || line.startsWith("private")) {
							if (line.lastIndexOf(")") == -1) {
								//System.out.println("Data:");
								line = line.substring(7, line.length() - 1);
							} else {
								//System.out.println("Method:");
								line = line.substring(7, line.lastIndexOf(")") + 1);
							}
							line = line.trim();
							//System.out.println(line);
						}
						p = end;
						end = s.indexOf("\n", end + 1);
						if (end == -1) {
							end = s.length();
						}
						if (end == p) {
							start = s.lastIndexOf("\n", p - 1);
						} else {
							start = s.lastIndexOf("\n", p);
						}
						if (start == -1) {
							start = 0;
						} else {
							start++;
						}
					}
				} catch (BadLocationException ble) {
					System.out.println(ble);
				}
			}
		};
		Thread appThread1 = new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeLater(parser);
				} catch (Exception e) {
				}
			}
		};
		appThread1.start();
	*/
	
	}
	public void changedUpdate(DocumentEvent de) {
		System.out.println("Changed");
		saved = false;
	}
	public void insertUpdate(DocumentEvent de) {
	}
	public void removeUpdate(DocumentEvent de) {
	}
	public void inputMethodTextChanged(InputMethodEvent ime) {
	}
	public void caretPositionChanged(InputMethodEvent ime) {
	}
	public void mouseEntered(MouseEvent me) {
	}
	public void mouseClicked(MouseEvent me) {
	// ov added 13/11/2004
     
	 if(me.getClickCount() > 1) {
	        lastSelStart = getSelectionStart();
	        lastSelEnd = getSelectionEnd();
			
		     if (me.getClickCount() == 2) {
			 
				 int index = jcmb.locationToIndex(me.getPoint());
				 System.out.println("Double clicked on Item " + index);
				 System.out.println("Double clicked on Item val " + jcmb.getSelectedValue());
				 selectedElem = (String) jcmb.getSelectedValue();
				 
				 System.out.println("attr sel elem  " + selectedElem);				   
				 
				 replaceSelection(selectedElem);
				 pmgr.uninstall(jscp);
		 System.out.println("In mouse double click setting install false");			 
				 installed = false;
			}
	  }
    

	// ov commented 13/11/2004
	/*
	try {
		if (me.getClickCount() == 2) {
			int ep = pos;
			int sp = pos;
			while (Character.isLetterOrDigit(getText(ep, 1).charAt(0))) {
				ep++;
			}
			while (Character.isLetterOrDigit(getText(sp, 1).charAt(0))) {
				sp--;
			}
			if (sp != 0) {
				sp++;
			}
			select(sp, ep);
			getCaret().setSelectionVisible(true);
		} else {
			pos = getCaretPosition();
		}
		} catch (BadLocationException ble) {
		}
	*/
	
	}

	public void mouseExited(MouseEvent me) {

	}

	public void mousePressed(MouseEvent evt) {
		 System.out.println(" mouse preseed");
			 if (evt.isPopupTrigger()) {
			 	// xmlEditor = XmlEditor.getInstance("");
				xmlEditor.getPopupMenu().show(evt.getComponent(), evt.getX(),
				   evt.getY());
			 }
	}
	
	public void mouseReleased(MouseEvent evt) {
	// ov added 13/11/2004
          lastSelStart = getSelectionStart();
	      lastSelEnd = getSelectionEnd();
		  
		  
		  // ov added 13/11/2004
		   if (evt.isPopupTrigger()) {
		   
		  // 		System.out.println(" xmlEditor = " + xmlEditor);	
		  // 		System.out.println(" xmlEditor popupmenu = " + xmlEditor.getPopupMenu());	
		  // 		System.out.println(" event comp = " + evt.getComponent());	
		   
				 xmlEditor.getPopupMenu().show(evt.getComponent(), evt.getX(),
				   evt.getY());
	  	   }		
	}
	
	public File getXslFile() {
		return xslFile;
	}

	public void setStyleSheet(String file) {
		xslFile = new File(file);
	}
	
	public void saveas() {
		JFileChooser jfc = new JFileChooser(idePath);
		   			
		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
		System.out.println(" save as file name " + jfc.getSelectedFile());
		File selectedFile = jfc.getSelectedFile();
 	    if( selectedFile.exists())
		{
			  int response =             JOptionPane.showConfirmDialog(null,
				"Overwrite existing file?",
				"Confirm Overwrite",
				 JOptionPane.OK_CANCEL_OPTION,
				 JOptionPane.QUESTION_MESSAGE);
			  if( response == JOptionPane.CANCEL_OPTION)
			  return;
		}
			setFile(jfc.getSelectedFile());
		}

	}
	
	// newly added
	    /**
	     * sets word wrap on or off.
	     * @param wrap whether the text editor pane should wrap or not
	     */

	    public void setWordWrap(boolean wrap)
	    {
		//	System.out.println(" in editor setwordwrap wrap = " + wrap);
			this.wrap = wrap;
			
			this.revalidate();
			/*
			this.wrap= context.getWordWrap();
		
			System.out.println(" curr wordwrap in ctx = " + this.wrap);
		
			context.setWordWrap(wrap);
		
			System.out.println(" new wordwrap in ctx = " + context.getWordWrap());

		//	super.setFont(new Font("Ariel", Font.PLAIN, 12));
			
		//	super.setFont(new Font("Times New Roman", Font.PLAIN, 14));
			
			this.setWordWrap(wrap);
*/
			
	    }

	    /**
	     * returns whether the editor wraps text.
	     * @return the value of the word wrap property
	     */
	    public boolean getWordWrap()
	    {
		return  context.getWordWrap();
	    }
	
	/*
	    public boolean getScrollableTracksViewportWidth()
	    {
		//	System.out.println(" in STVW ");
		if (!wrap)
		{
		//			System.out.println(" inside if wrap STVW ");

		    Component parent=this.getParent();
		    ComponentUI ui=this.getUI();
		    int uiWidth=ui.getPreferredSize(this).width;
		    int parentWidth=parent.getSize().width;
		    boolean bool= (parent !=null)
			? (ui.getPreferredSize(this).width < parent.getSize().width)
			: true;	
	
		    return bool;
		}
		else return super.getScrollableTracksViewportWidth();
	    }
		
		*/
	
	    public void setBounds(int x, int y, int width, int height) 
	    {
		if (wrap) {
		//	System.out.println(" inside setbounds if wrap ");	
			super.setBounds(x, y, width, height);
		
		} else
		{
		//		System.out.println(" inside setbounds else wrap ");	
			    Dimension size = this.getPreferredSize();
		    super.setBounds(x,y,Math.max(size.width, width),Math.max(size.height, height));
		}
	    }
		
		
		
		public int getCaretLine() throws Exception {
		
		    javax.swing.text.Document doc = getDocument();
			int offset = getCaretPosition();
		        if (offset < 0) {
		            throw new BadLocationException("Can't translate offset to line", -1);
		        } else if (offset > doc.getLength()) {
		            throw new BadLocationException("Can't translate offset to line", doc.getLength()+1);
		        } else {
		            Element map = getDocument().getDefaultRootElement();
		            return map.getElementIndex(offset);
		        }

		}

	public boolean isTagCompletion() {
		return tagCompletion;
	}

	public void setFont( Font font) {
		if ( kit != null) {
			kit.setFont( font);
		}
		super.setFont( font);
	}
	
	public int getSpaces() {
			return spaces;
	}
	
	
	
	
	// everything below added newly by OV 13/11/2004
	
	  private void jbInit() throws Exception {
	    this.addMouseListener(new java.awt.event.MouseAdapter() {
	      public void mouseReleased(MouseEvent e) {
	        this_mouseReleased(e);
	      }
	      public void mouseClicked(MouseEvent e) {
	        this_mouseClicked(e);
	      }
	    });
	  }
	  
	
	/**
	   * Transfers the currently selected range in the associated
	   * text model to the system clipboard, removing the contents
	   * from the model. The current selection is reset.
	   *
	   * @see #replaceSelection
	   */
	
	public void cut() {
	  System.out.println(" inside editor cut");
	  
	    if (isEditable() && isEnabled()) {
	      copy();
	      replaceSelection("");
	    }
	  }
	
	
	  /**
	   * Transfers the currently selected range in the associated
	   * text model to the system clipboard, leaving the contents
	   * in the text model.  The current selection remains intact.
	   */
	
	public void copy() {
	 
	  System.out.println(" inside editor copy");

  	  try
	    {
	      StyledText st = new StyledText(this);
	      StyledTextSelection contents = new StyledTextSelection(st);
	      Clipboard clipboard = getToolkit().getSystemClipboard();
	      clipboard.setContents(contents, defaultClipboardOwner);
	    }
	    catch(Exception e) {
	      //getToolkit().beep();
	    }
	  }
	
	
	  /**
	   * Transfers the contents of the system clipboard into the
	   * associated text model. If there is a selection in the
	   * associated view, it is replaced with the contents of the
	   * clipboard. If there is no selection, the clipboard contents
	   * are inserted in front of the current insert position in
	   * the associated view. If the clipboard is empty, does nothing.
	   *
	   * @see #replaceSelection
	   */
	
	public void paste() {
	 
	  System.out.println(" inside editor paste");
	 
	 
	    Clipboard clipboard = getToolkit().getSystemClipboard();
	    Transferable content = clipboard.getContents(this);
	    if (content != null) {
	      try {
	        if(content.isDataFlavorSupported(df)) {
	          StyledText st = (StyledText) content.getTransferData(df);
	          replaceSelection(st);
	        }
	        else if(content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
	          String text = (String) content.getTransferData(DataFlavor.stringFlavor);
	          replaceSelection(text);
	        }
	      }
	      catch (Exception e) {
	        //getToolkit().beep();
	      }
	    }
	  }
	
	  // ------ end of cut, copy and paste implementation for styled text -----

	
	/** a drag gesture has been initiated */
	 
	 public void dragGestureRecognized(DragGestureEvent event) {
	    int selStart = getSelectionStart();
	    int selEnd = getSelectionEnd();
	    try {
	      if( (lastSelEnd > lastSelStart) &&
	          (selStart >= lastSelStart) &&
	          (selStart < lastSelEnd) )
	      {
	        dragStartedHere = true;
	        select(lastSelStart, lastSelEnd);
	        StyledText text = new StyledText(this);
	        StyledTextSelection trans = new StyledTextSelection(text);
	        dragSource.startDrag(event, DragSource.DefaultMoveDrop, trans, this);
	      }
	    }
	    catch(Exception e) {
	      //getToolkit().beep();
	    }
	  }

	
  /** is invoked when a drag operation is going on */
  
  public void dragOver (DropTargetDragEvent event) {
    dndEventLocation = viewToModel(event.getLocation());
    try {
      setCaretPosition(dndEventLocation);
    }
    catch(Exception e) {
      //getToolkit().beep();
    }
  }
  
    /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has entered the DropSite
     */
  
  public void dragEnter (DragSourceDragEvent event) {
    }
	
	
	/** remember current selection when mouse button is released */
	
	void this_mouseReleased(MouseEvent e) {
	    lastSelStart = getSelectionStart();
	    lastSelEnd = getSelectionEnd();
	  }
	
	  /** remember current selection when mouse button is double clicked */
	
	void this_mouseClicked(MouseEvent e) {
	    if(e.getClickCount() > 1) {
	      lastSelStart = getSelectionStart();
	      lastSelEnd = getSelectionEnd();
		  
		  
		  
	    }
  }

    /**
     * this message goes to DragSourceListener, informing it that
     * the dragging is currently ocurring over the DropSite
     */
    
	public void dragOver (DragSourceDragEvent event) {
    }
	

 /** is invoked when the user changes the dropAction */
  
  public void dropActionChanged ( DragSourceDragEvent event) {
  }

  /**
   * this message goes to DragSourceListener, informing it that the dragging
   * has exited the DropSite
   */
  
  public void dragExit (DragSourceEvent event) {
  }
  
   /**
     * this message goes to DragSourceListener, informing it that the dragging
     * has ended
     */
  
  public void dragDropEnd (DragSourceDropEvent event) {
      dragStartedHere = false;
  }

  static class ClipboardObserver implements ClipboardOwner {
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }
  }
  
    /**
     * Replaces the currently selected content with new content
     * represented by the given <code>StyledText</code>.  If there is no selection
     * this amounts to an insert of the given text.  If there
     * is no replacement text this amounts to a removal of the
     * current selection.
     *
     * @param content  the content to replace the selection with
     */
  
  public void replaceSelection(StyledText content) {
      javax.swing.text.Document doc = getDocument();
      String text;
      Caret caret = getCaret();
      int insertPos = 0;
      int i;
      int contentSize;
      if (doc != null) {
        try {
//			System.out.println(" dot = " + caret.getDot());
//          	System.out.println(" caret pos = " + getCaretPosition());

		  int p0 = Math.min(caret.getDot(), caret.getMark());
          int p1 = Math.max(caret.getDot(), caret.getMark());
          if (p0 != p1) {
            doc.remove(p0, p1 - p0);
          }
          if (content != null) {
            content.insert(doc, p0);
          }
        }
        catch (Exception e) {
          //getToolkit().beep();
        }
      }
    }
	
public void setSelection(int xStart, int xFinish, boolean moveUp) {
	if (moveUp) 
	{
		setCaretPosition(xFinish);
		moveCaretPosition(xStart);
	}
	else
		select(xStart, xFinish);
		
//	m_xStart = getSelectionStart();
//	m_xFinish = getSelectionEnd();

}


/*
 **  Return the number of lines of text.
 */
    public int getLineCount()
	{
		Element root = getDocument().getDefaultRootElement();
		return root.getElementCount();
	}
	
	public MyDTDParser getDTDParser() {
		if(dtdParser != null)
			return dtdParser;
		
		return null;
	}
	
	public Hashtable getEntities() {
		return _entities;
	}
	
	private void showPopup(Vector innerElems) throws Exception {
	
		int currLine = getCaretLine();
		System.out.println(" Curr line in editor = " + currLine);
	
		int start = getLineStartOffset(currLine);
	
		System.out.println(" start = " +  start);
		int dot = getCaretPosition() - start;
		System.out.println(" dot = " +  dot);

	//		Point point = getCaret().getMagicCaretPosition();

		Rectangle bounds = getUI().modelToView(this, dot, Position.Bias.Forward);
	//	Rectangle bounds = new Rectangle((int)point.getX(), (int) point.getY(), 80, 100);

		System.out.println(" input x = " +  bounds.x);
		System.out.println(" input y = " + bounds.y);
		System.out.println(" input height = " + bounds.height);
		System.out.println(" input width = " + bounds.width);


		/** Place popup inside the scrollbar's viewport */
		 PopupManager.HorizontalBounds viewPortBounds = new PopupManager.HorizontalBounds("ViewPort"); 
		//NOI18N

		/** Place popup inside the whole scrollbar */
		//	PopupManager.HorizontalBounds scrollBarBounds = new PopupManager.HorizontalBounds("ScrollBar"); //NOI18N

		//OMP added 3/4/3005 for intellisense - code in xtree2\jlist
		if(innerElems.size() > 0) {
			installed = true;
			jcmb = new ListCompletionView();
			
			jcmb.setResult(innerElems);
			
			jcmb.setCellRenderer( new CustomCellRenderer() );
			jcmb.setPrototypeCellValue("Index 12345");
			jcmb.setVisibleRowCount(4);
		

		//	Font displayFont = new Font("Serif", Font.BOLD, 10);
		//	jcmb.setFont(displayFont);

		//	jcmb.addMouseListener(this);
			jcmb.addMouseListener(new MouseAdapter() {
				 public void mouseClicked(MouseEvent e) {
					 if (e.getClickCount() == 2) {
						 ListCompletionView mjlist = getEditor().getCombo();	
						 int index = mjlist.locationToIndex(e.getPoint());
						 System.out.println("Double clicked on Item " + index);
						 System.out.println("Double clicked on Item val " + mjlist.getSelectedValue());
						 String selectedItem = (String) jcmb.getSelectedValue();
						 selectedItem = selectedItem + ";";
						 System.out.println("entity sel elem  " + selectedItem);				   
						 replaceSelection(selectedItem);
						 pmgr.uninstall(jscp);
				 System.out.println("In showpopup mouse 2 click setting install false");			 
						 installed = false;
						 manager.addKeyEventDispatcher(lstSearch);

					 }
				 }
			 });
			 
	
	//		manager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();
			 
			//OV comment 02/01/09
			//	jcmb.addKeyListener(this);
			
			MyInternalFrame myf = (MyInternalFrame) xmlEditor.getDesktopView().getSelectedFrame();
			Editor etr = (Editor) myf.getTextComponent();
			lstSearch = new ListSearcher(etr, jcmb, manager, "entity");
			
			manager.addKeyEventDispatcher(lstSearch);
			
			jscp.getViewport().add(jcmb);
			jcmb.setBorder( BorderFactory.createLoweredBevelBorder() );
			jscp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//		    pmgr.install(jscp, bounds, new PopupManager.Placement("Below"), viewPortBounds, 0, 0);
			pmgr.install(jscp);

			requestFocus(jcmb);
	
		}
	}
	
	/*
		**  Position the caret at the start of a line.
		*/
		public void gotoLine(int line)
		{
			Element root = this.getDocument().getDefaultRootElement();
			line = Math.max(line, 1);
			line = Math.min(line, root.getElementCount());
			this.setCaretPosition( root.getElement( line - 1 ).getStartOffset() );
	/*
			//  The following will position the caret at the start of the first word
			try
			{
				component.setCaretPosition(
					Utilities.getNextWord(component, component.getCaretPosition()));
			}
			catch(Exception e) {System.out.println(e);}
	*/
		}

	
	
	public Editor getEditor() {
		return this;
	}
	
	private Window getFocusWindow(JList component) {
	    java.awt.Window w = SwingUtilities.windowForComponent(
	                                component);
									
		System.out.println(" focus window = " + w);
		
		Component focusOwner = (w == null) ?
		null : w.getFocusOwner();
	
		System.out.println(" focus comp = " + focusOwner);
		
		return w;
	}
	
	public ListCompletionView getCombo() {
		return jcmb;
	}
	
	
	public void requestFocus(final Component comp)
	{

		System.out.println(" in window activated ");
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				comp.requestFocus();
			}
		});
	}
	
	public void cleanup() {
	
		System.out.println(" in cleanup editor ");
		manager = DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.removeKeyEventDispatcher(lstSearch);
	
	}
	
	public File getDtdFile() {
		return dtdFile;
	}

	public File getSchemaFile() {
		return schemaFile;
	}
	
	public void setLoading(boolean loading) {
		this.loading = loading;
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public boolean isDirty() {
	    return !saved;
	}
	public void setSaved(boolean saved) {
		this.saved = saved;
	}
	
	
    public Point getExpandLoc(int[] path) {
        return getExpandLoc(getUI().getRootView(this).getView(0), getTextRect(), path, 0);
    }
    
    public Point getExpandLoc(javax.swing.text.View view, Rectangle bounds, int[] path, int depth) {
        System.err.println("getExpandLoc, view=" + view + " bounds=" + bounds + " depth=" + depth);
        int responseCount = 0;
        for (int i = 0; i < view.getViewCount(); i++) {
            if (view.getView(i) instanceof ResponseView) {
                if (responseCount++ == path[depth]) {
                    Rectangle childBounds = view.getChildAllocation(i, bounds).
                            getBounds();
                    if (++depth == path.length) {
                        // Return it for this one
                        System.err.println("getting from click view");
                        return ((ResponseView)view.getView(i)).getClickCenter(
                                childBounds.x, childBounds.y);
                    } else {
                        return getExpandLoc(view.getView(i), 
                                childBounds, path, depth);
                    }
                }
            }
        }
        return null;
    }
    
    public void setFoldsQuotes(boolean foldsQuotes) {
        if (foldsQuotes) {
            setEditorKit(kit);
        } else {
            setEditorKit(new StyledEditorKit());
        }
        
		try {
			String text = getDocument().getText(0, getDocument().getLength());

			setText(text);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public boolean getFoldsQuotes() {
        return (getEditorKit() instanceof XmlEditorKit);
    }
    
    public List getQuotedPath() {
        List path = new ArrayList(1);
        Rectangle rect = getTextRect();
        Rectangle visRect = getVisibleRect();
        javax.swing.text.View view = getDeepestView(getUI().getRootView(this), (float)(rect.x + rect.width - 1),
                (float)visRect.y, rect);
        while (view != null) {
            if (view instanceof ResponseView) {
                path.add(((ResponseView)view).getSender());
            }
            view = view.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    public void setText(String text) {
        
    	
    	String docText = text;
        // OV commented 250409 for tabbed views
     /*
        if (getEditorKit() instanceof XmlEditorKit) {
            XmlDocument doc;
                doc = new XmlDocument(this);
                setDocument(doc);
        } else {
     */
            setDocument(getEditorKit().createDefaultDocument());
            try {
                getDocument().insertString(0, docText, null);
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
     //   }
        select(0, 0);
        scrollRectToVisible(new Rectangle());
    }
    
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (!e.isConsumed()) {
            if (e.isPopupTrigger()) {
                getPopupMenu().show(this, e.getX(), e.getY());
            } else {
                if (getFoldsQuotes() && e.getClickCount() == 1 &&
                        e.getID() == MouseEvent.MOUSE_CLICKED) {
                    handleClick(e);
                }
            }
        }

    }

    protected void processKeyEvent(KeyEvent e) {
        super.processKeyEvent(e);
        if (!e.isConsumed()) {
        /*
        	if (e.isPopupTrigger()) {
                getPopupMenu().show(this, e.getX(), e.getY());
            } else {
                if (getFoldsQuotes() && e.getClickCount() == 1 &&
                        e.getID() == MouseEvent.MOUSE_CLICKED) {
                    handleClick(e);
                }
            }
        */    
        }

    }

    
    
    
    
    private JPopupMenu getPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JCheckBoxMenuItem foldsMI = new JCheckBoxMenuItem("Fold Quotes");
        foldsMI.setSelected(getFoldsQuotes());
        foldsMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setFoldsQuotes(((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
      
        xmlEditor.getPopupMenu().add(foldsMI);
    /*
        popupMenu.add(foldsMI);
        
        JCheckBoxMenuItem pathPanelMI = new JCheckBoxMenuItem("Show Quote Path");
        pathPanelMI.setSelected(pathPanel.isVisible());
        pathPanelMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pathPanel.setVisible(((JCheckBoxMenuItem)e.getSource()).isSelected());
            }
        });
        popupMenu.add(pathPanelMI);
     */   
        return popupMenu;
    }
    
    private View getDeepestView(View v, float x, float y, Shape alloc) {
        if (v == null) {
            return null;
        }
        View child = null;
        int index = v.getViewIndex(x, y, alloc);
        if (index != -1) {
            alloc = v.getChildAllocation(index, alloc);
            child = getDeepestView(v.getView(index), x, y, alloc);
        }
        if (child == null) {
            return v;
        }
        return child;
    }
    
    private Rectangle getTextRect() {
        Rectangle bounds = getBounds();
        bounds.x = 0;
        bounds.y = 0;
        Insets insets = getInsets();
        bounds.x += insets.left;
        bounds.y += insets.top;
        bounds.width -= (insets.left + insets.right);
        bounds.height -= (insets.top + insets.bottom);
        return bounds;
    }
    
    private void handleClick(MouseEvent e) {
        float x = (float)e.getX();
        float y = (float)e.getY();
        View view = getUI().getRootView(this);
        toggleView(view, x, y, getTextRect());
    }
    
    private View toggleView(View v, float x, float y, Shape alloc) {
        if (v == null) {
            return null;
        }
        int index = v.getViewIndex(x, y, alloc);
        if (index == -1) {
            if (v instanceof ResponseView) {
                ((ResponseView)v).toggleIfNecessary(x, y, alloc);
                return v;
            } else {
                return null;
            }
        }
        alloc = v.getChildAllocation(index, alloc);
        return toggleView(v.getView(index), x, y, alloc);
    }
    
    public void reshape(int x, int y, int w, int h) {
        super.reshape(x, y, w, h);
        firePropertyChange("quotedPath", null, null);
    }

    public boolean getScrollableTracksViewportWidth() {
    	   if (!wrap) {
                    Component parent = this .getParent();
                    ComponentUI ui = this .getUI();
                    int uiWidth = ui.getPreferredSize(this ).width;
                    int parentWidth = parent.getSize().width;
                    boolean bool = (parent != null) ? (ui
                            .getPreferredSize(this ).width < parent.getSize().width)
                            : true;
                    return bool;
                } else
                    return super .getScrollableTracksViewportWidth();
            }

  
	// printing stuff
   // PREREL A FR PRINT BEGIN
public int print(Graphics g, PageFormat fmt, int index) throws
PrinterException
{

        Graphics2D g2d = (Graphics2D) g;

        // Set default foreground color to black
        g2d.setColor(Color.black);

        // For faster printing, turn off double buffering

RepaintManager.currentManager(this).setDoubleBufferingEnabled(false);

        // Shift Graphic to line up with beginning of print-imageable  region
        g2d.translate(fmt.getImageableX(), fmt.getImageableY());

        View rootView = getUI().getRootView(this);

        if (index > currentPage)
        {
                  currentPage = index;

            pageStartY += pageEndY;
            pageEndY = g2d.getClipBounds().getHeight();
        }

        g2d.translate(g2d.getClipBounds().getX(), g2d.getClipBounds().getY());

        Rectangle allocation = new Rectangle(0, (int) -pageStartY,
getWidth(), getHeight());

        if (printView(g2d, allocation, rootView))
        {
            return Printable.PAGE_EXISTS;
        }
        else
        {
            currentPage = -1;

            pageStartY = 0.0;
            pageEndY = 0.0;

            return Printable.NO_SUCH_PAGE;
        }
}


protected boolean printView(Graphics2D g2d, Shape alloc, View view)
      {
                  boolean pageExists = false;

                  Rectangle clip = g2d.getClipBounds();

                  Shape childAlloc;
                  View childView;

                  //System.out.println("View name = " +  view.getElement().getName());
                  if (view.getViewCount() > 0 &&
						!view.getElement().getName().equalsIgnoreCase("td"))
                  {
                        for (int i = 0; i < view.getViewCount(); i++)
                        {
                              childAlloc = view.getChildAllocation(i, alloc);

                              if (childAlloc != null)
                              {
                                    childView = view.getView(i);

                                    if (printView(g2d, childAlloc, childView))
                                    {
                                          pageExists = true;
                                    }
                              }
                        }
                  }
                  else
                  {
//          I
                        if (alloc.getBounds().getMaxY() >= clip.getY())
                        {
                              pageExists = true;
//          II
                              if ((alloc.getBounds().getHeight() >
									clip.getHeight()) && (alloc.intersects(clip)))
                              {
                              //    System.out.println("Calling  view.paint()... (1)");
                                    view.paint(g2d, alloc);
                              }
                              else
                              {
//          III
                                    if (alloc.getBounds().getY() >= clip.getY())
                                    {
                                          if (alloc.getBounds().getMaxY() <= clip.getMaxY())
                                          {
                                    //          System.out.println("Calling view.paint()... (2)");
                                    //System.out.println(view.getClass().getName());
                                    //System.out.println(view.getElement().getName());
                                                view.paint(g2d, alloc);
                                          }
                                          else
                                          {
//          IV
                                                if(alloc.getBounds().getY() < pageEndY)
                                                {
                                                      pageEndY = alloc.getBounds().getY();
                                                }
                                          }
                                    }
                              }
                        }
                  }

                  return pageExists;

      }


public void doPrint() {

            //--- Create a printerJob object
        PrinterJob printJob = PrinterJob.getPrinterJob ();

        //--- Set the printable class to this one since we
        //--- are implementing the Printable interface
        printJob.setPrintable (this);

        //--- Show a print dialog to the user. If the user
        //--- clicks the print button, then print, otherwise
        //--- cancel the print job
        if (printJob.printDialog()) {
          try {
              printJob.print();
           } catch (Exception PrintException) {
              PrintException.printStackTrace();
           }
        }

}
  // PREREL A FR PRINT END

}
