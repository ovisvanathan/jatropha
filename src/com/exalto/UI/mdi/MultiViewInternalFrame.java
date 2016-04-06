package com.exalto.UI.mdi;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.InputMethodEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.exalto.ColWidthTypes;
import com.exalto.CustomEntityResolver;
import com.exalto.UI.TreeTableClipboard;
import com.exalto.UI.XmlEditor;
import com.exalto.UI.XmlEditorActions.TreeTableXmlOps;
import com.exalto.UI.grid.JXmlTreeTable;
import com.exalto.UI.grid.XmlKitTest;
import com.exalto.UI.multiview.MVTableDesc;
import com.exalto.UI.multiview.MVTableElem;
import com.exalto.UI.multiview.MVTextDesc;
import com.exalto.UI.multiview.MVTextElem;
import com.exalto.UI.multiview.MultiViewDescription;
//import com.exalto.UI.multiview.MultiViewElement;
import com.exalto.UI.multiview.MultiViewFactory;
import com.exalto.UI.multiview.MultiViewModel;
import com.exalto.UI.multiview.MultiViewTopComponent;
import com.exalto.UI.multiview.TabsComponent;
import com.exalto.util.StatusEvent;
import com.exalto.util.XmlUtils;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.text.AbstractDocument;
import javax.swing.text.View;
import org.w3c.dom.Document;


public class MultiViewInternalFrame extends MyInternalFrame implements DocumentListener, KeyEventDispatcher, TreeTableClipboard  {
	private Editor editor;
	private JScrollPane jsp;
	// for caret events
	public boolean saved = true;
	public boolean _new = false;
	// ov added for find/replace 14/11/2004
	public boolean processed = false;
	
	protected boolean _ok_to_close = false;
	private XmlUtils xutils;
	private boolean waitingToAssign = false;
	protected String currentView = "";
	protected JPanel emptyPanel = null;
	protected XmlEditor xmlEditor = null; 	
	protected XmlKitTest ktest;
	protected File inFile;

	// OV a 200309
	JXmlTreeTable gridTable;
	
	TabsComponent tabsComp;
	MultiViewModel multiViewModel;
	
	/**
	  * Sequence which is incremented each time a new document is created. used to generate
	  * the Untitled-# filename.
	  */
	  protected static int _docseq = 1;
	EventListenerList listenerList;

         // OV added for adding * to title
    ArrayList listeners = new ArrayList();

    /**
	     * The undo manager for document node actions
	     */
	    protected UndoManager _undoManager;

            String _filename;
		String _title;
		org.w3c.dom.Document document; 
	
	    static MyOwnFocusTraversalPolicy newPolicy;

	
	public MultiViewInternalFrame(File f, int width, int height, String defaultView) throws Exception {
	//	super("New File", true, true, true, true);
		
		if(f == null) {
	//		System.out.println(" filename = " + xutils.getResourceString("untitled")+" " + _docseq++);
			_filename = xutils.getResourceString("untitled")+" " + _docseq++;
			f = new File(_filename);
			_new = true;
            _title = f.getAbsolutePath();

            if(_title.indexOf("*") < 0)
                _title = _title + "*";

        } else if(f.getName().contains("XSL Result") || f.getName().contains("XQuery Result")) {
            _new = true;
            _title = f.getAbsolutePath();

            if(_title.indexOf("*") < 0)
                _title = _title + "*";
        } else
            _title = f.getAbsolutePath();


        //  _title = f.getAbsolutePath();
                
	//	System.out.println(" new file _new = " + _new);
			
		_undoManager = new UndoManager();
	//	_undoManager.setLimit(xutils.getUndoLimit());

		
	//	if (f != null) {
			inFile = f;
			setTitle(_title);
	//	}
		
	//	xutils = new XmlUtils();	
		xutils = XmlUtils.getInstance();	
		
		setSize(new Dimension(width, height));

		editor = new Editor("", width, height);
		
		xmlEditor = XmlEditor.getInstance("");
		editor.add(xmlEditor.getPopupMenu());
		
		
	//	EditorDropTarget2 target = new EditorDropTarget2(editor);
		EditorDropTarget2 target = new EditorDropTarget2(this);
		
		
		editor.setCaretPosition(0);
		document = editor.setFile(f);

        //OV added 080509
        editor.addCaretListener(xmlEditor);
        addInternalFrameListener(xmlEditor);

		try {
			if(!_new || _title.contains("XSL Result") || _title.contains("XQuery Result")) {
				editor.setLoading(true);
				editor.read(new FileReader(f), f);
				editor.setLoading(false);

            
			//      AbstractDocument abs = (AbstractDocument) editor.getDocument();
			//      abs.dump(System.out);
			    
			//		System.out.println(" views ::::::::::::: = ");
			      
					View root = editor.getUI().getRootView(editor);
					
			//		System.out.println("root = " + root);
					
			//		editor.printViewHierarchy(root);
                

				
			}
		} catch(FileNotFoundException fe) {
            fe.printStackTrace();

            		String msg = xutils.getResourceString("file.open.not.found");

                    msg = msg + " " + f.getName();

					JOptionPane.showMessageDialog(this, msg, title,JOptionPane.YES_NO_CANCEL_OPTION);

                    throw fe;

		} catch(IOException ie) {
			ie.printStackTrace();
		}
		
		addVetoableChangeListener(new VetoableChangeListener() {
								public void vetoableChange(PropertyChangeEvent evt) 
									throws PropertyVetoException
								{
									String s = evt.getPropertyName();
									if (s.equals(JInternalFrame.IS_CLOSED_PROPERTY) && !_ok_to_close) {
										Object o = evt.getNewValue();
										if (o instanceof Boolean) {
											if (((Boolean)o).booleanValue()) {
												if (!checkClose()) {
													throw new PropertyVetoException("User chose to not close unsaved document.",evt);
												}
				                                				else {
				                                    					try {
				                                        					setClosed(true); //Don't use the default
				                                        		//			activateDocumentAfterClose();
				                                    					}
				                                    					catch (java.beans.PropertyVetoException ex) {
				                                            					ex.printStackTrace();
				                                    					}
																}
				                                			}
				
											
										}
									}
								}
								
					});


         newPolicy = new MyOwnFocusTraversalPolicy();
		 setFocusTraversalPolicy(newPolicy);
       
	
         
//		getContentPane().setLayout(new MyCardLayout()); 
//		getContentPane().setLayout(new BorderLayout());
		jsp = new JScrollPane(editor);
//		getContentPane().add(jsp, BorderLayout.CENTER);

		/* ov added for gridview on 28012006 begin */
		
 		tabsComp = new TabsComponent(this, true); 

 		if(defaultView.intern() == "GRIDVIEW".intern())  
 			createOrUpdateTreeTable();
	    	
	    	
		MultiViewDescription desc1 = new MVTextDesc("text", null, 0, new MVTextElem(editor));
		 
		
		MultiViewDescription desc2 = new MVTableDesc("grid", null, 0, new MVTableElem( (gridTable == null)? new JTable() : gridTable));

		MultiViewDescription[] descs = new MultiViewDescription[] { desc1, desc2 };
		MultiViewTopComponent tc = (MultiViewTopComponent) MultiViewFactory.createMultiView(descs, desc1);
		 
		multiViewModel = new MultiViewModel(descs, desc1, tc);  
		   
	    tabsComp.setModel(multiViewModel);

		getContentPane().add(tabsComp);

		/* ov added for gridview on 28012006 end */
		/* ov comment begin 200309 
		emptyPanel = new JPanel();
		getContentPane().add("TEXTVIEW", jsp);
		getContentPane().add("GRIDVIEW", emptyPanel);
		
		
		if(defaultView.intern() == "TEXTVIEW".intern())  {
		    ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "TEXTVIEW"); 		
		    currentView = "TEXTVIEW";
		} else if(defaultView.intern() == "GRIDVIEW".intern())  {
			createAndAddTreeTable();
		    ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "GRIDVIEW"); 		
		    currentView = "GRIDVIEW";
		}   
		
		OV c end */
		
	//	getContentPane().add(jsp);
		editor.getDocument().addDocumentListener(this);
	//	pack();
	//	show();
		
		
	}

	public TabsComponent getTabsComp() {
        return tabsComp;
    }

        public boolean isSaved() {
            return saved && !_new;
        }
		    /**
		     * returns true if the document window can be closed, false otherwise
		     */
		    public boolean checkClose() 
		    {
				boolean ret = true;
				
				if (!isSaved() || isGridModified()) {
						
					String[] docname = new String[1];
					docname[0] = getTitle();
					MessageFormat mf;
		
					mf = new MessageFormat(xutils.getResourceString("checkclosedirtyques"));
					String ques  = mf.format(docname);
					mf = new MessageFormat(xutils.getResourceString("checkclosedirtytitle"));
					String title = mf.format(docname);
					

					int save = JOptionPane.showConfirmDialog(this, ques, title,JOptionPane.YES_NO_CANCEL_OPTION);
					
					System.out.println(" save = " + save);
					System.out.println(" cancel = " + JOptionPane.CANCEL_OPTION );
					System.out.println(" yes = " + JOptionPane.YES_OPTION );
					System.out.println(" no = " + JOptionPane.NO_OPTION );
					System.out.println(" docname 0 = " + docname[0] );
					
					
					switch (save) {
					case JOptionPane.CANCEL_OPTION:
						ret = false;
						break;
					case JOptionPane.YES_OPTION:
						if(docname[0].startsWith("Untitled")) {
							ret = getFileNameAndSave();
						} else {
							ret = save();
						}	
						break;
					case JOptionPane.NO_OPTION:
						ret = true;
						break;
						
					default:
					    // There is a bug in the current JDK Yes/No/Cancel
					    // key mappings, ESC is mapped to something
					    // odd - value is NONE OF THE ABOVE, assume
					    // cancel behavior.  On Linux - save = -1
					    // Assume cancel behavior for cross-platform.
					    ret = false;
						System.out.println(" case def ");
						break;
					}
				}
				_ok_to_close = ret;
				return ret;
		    }


			/**
		     *  PREREL  OVERRIDE CHECKCLOSE TO RET STRING Y,N,MAYBE
		     */
		    public String okToClose() 
		    {
				String ret = "true";
				
				if (!isSaved() || isGridModified()) {
						
					String[] docname = new String[1];
					docname[0] = getTitle();
					MessageFormat mf;
		
					mf = new MessageFormat(xutils.getResourceString("checkclosedirtyques"));
					String ques  = mf.format(docname);
					mf = new MessageFormat(xutils.getResourceString("checkclosedirtytitle"));
					String title = mf.format(docname);
					

					int save = JOptionPane.showConfirmDialog(this, ques, title,JOptionPane.YES_NO_CANCEL_OPTION);
					
					System.out.println(" save = " + save);
					System.out.println(" cancel = " + JOptionPane.CANCEL_OPTION );
					System.out.println(" yes = " + JOptionPane.YES_OPTION );
					System.out.println(" no = " + JOptionPane.NO_OPTION );
					System.out.println(" docname 0 = " + docname[0] );
					
					
					switch (save) {
					case JOptionPane.CANCEL_OPTION:
						ret = "false";
						break;
					case JOptionPane.YES_OPTION:
						if(docname[0].startsWith("Untitled")) {
							ret = fetchFileNameAndSave();
						} else {
							ret = saveFrame();
						}	
						break;
					case JOptionPane.NO_OPTION:
						ret = "maybe";
						break;
						
					default:
					    // There is a bug in the current JDK Yes/No/Cancel
					    // key mappings, ESC is mapped to something
					    // odd - value is NONE OF THE ABOVE, assume
					    // cancel behavior.  On Linux - save = -1
					    // Assume cancel behavior for cross-platform.
					    ret = "false";
						System.out.println(" case def ");
						break;
					}
				}

				if(ret.equals("true") || ret.equals("maybe"))
					_ok_to_close = true;
				else
					_ok_to_close = false;

				return ret;
		    }





    public boolean isGridModified() {
    	if(getTreeTableClipboard() != null)
    		return getTreeTableClipboard().getUndoManager().canUndo();
    	
    	return false;
    }

    public boolean getFileNameAndSave() {
	
		String dir = xutils.getCurrentDir();

		System.out.println(" save as dir " + dir);
		
		boolean ret = false;

	try {

		JFileChooser jfc = null;

		jfc = new JFileChooser(dir);
		jfc.setCurrentDirectory(new File(dir));


		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println(" save as file name " + jfc.getSelectedFile());
			File selectedFile = jfc.getSelectedFile();


			xutils.setCurrentDir(selectedFile.getAbsolutePath());


			if( selectedFile.exists())
			 {
				  int response =             JOptionPane.showConfirmDialog(null,
						"Overwrite existing file?",
						"Confirm Overwrite",
						 JOptionPane.OK_CANCEL_OPTION,
						 JOptionPane.QUESTION_MESSAGE);
				  if( response == JOptionPane.CANCEL_OPTION)
					  return ret;
			}					


            saveAs(jfc.getSelectedFile());

			ret = true;
		}	
		
		} catch(Exception e) {
			e.printStackTrace();
			ret = false;
			
		}
		
		return ret;
	}

	// PREREL  
	// override to ret string
    public String fetchFileNameAndSave() {
	
		String dir = xutils.getCurrentDir();

		System.out.println(" save as dir " + dir);
		
		String ret = "false";

	try {

		JFileChooser jfc = null;

		jfc = new JFileChooser(dir);
		jfc.setCurrentDirectory(new File(dir));


		if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			System.out.println(" save as file name " + jfc.getSelectedFile());
			File selectedFile = jfc.getSelectedFile();


			xutils.setCurrentDir(selectedFile.getAbsolutePath());


			if( selectedFile.exists())
			 {
				  int response =             JOptionPane.showConfirmDialog(null,
						"Overwrite existing file?",
						"Confirm Overwrite",
						 JOptionPane.OK_CANCEL_OPTION,
						 JOptionPane.QUESTION_MESSAGE);
				  if( response == JOptionPane.CANCEL_OPTION)
					  return ret;
			}					


            saveAs(jfc.getSelectedFile());

			ret = "true";
		}	
		
		} catch(Exception e) {
			e.printStackTrace();
			ret = "false";
			
		}
		
		return ret;
	}



        //OV c 24/03/08
//    public boolean cut(ActionEvent evt) 
    public void cut(ActionEvent evt) 
    {
		boolean pasteAble = false;

		Object o = evt.getSource();
		System.out.println("cut: evt source = "+o);

		if (o instanceof JTextComponent) {
			((JTextComponent)o).cut();
			pasteAble = true;			
		} 
		
		return;
   }

    //OV c 24/03/08
  //  public boolean copy(ActionEvent evt)
    public void copy(ActionEvent evt)
       {
   		boolean pasteAble = false;
   		Object o = evt.getSource();
   		System.out.println("copy: evt source = "+o);
   		if (o instanceof JTextComponent) {
   			((JTextComponent)o).copy();
   			pasteAble = true;
		}
		
		return;
	}
	
	public void paste (ActionEvent evt) 
	    {
			Object o = evt.getSource();
			System.out.println("paste: evt source = "+o);
	
			if (o instanceof JTextComponent) {
				((JTextComponent)o).paste();
			}
			else {
	
				// asks where the user wants to paste and then calls one of after, 
				// before, into
			}
			
	    }
		
		
	public void undo(ActionEvent evt) 
    {
		try {
			System.out.println("Undoing...");
			_undoManager.undo();
			// seems like a bug. Have to call this twice to get it to undo first time
			// undo is clicked
		//	_undoManager.undo();
			
		}
		catch (CannotUndoException ex) {
			ex.printStackTrace();	
		}
    }
	
	public void redo(ActionEvent evt) 
    {
		try {
			System.out.println("Redoing...");
	
			_undoManager.redo();
			// seems like a bug. Have to call this twice to get it to undo first time
			// undo is clicked
		//	_undoManager.redo();
		}
		catch (CannotUndoException ex) {
			ex.printStackTrace();			
		}
    }
	
	/**
	 * Closes the document unless the document is dirty and the user hits cancel 
	 * when prompted.
	 * @return true if the document was closed, false otherwise
	 */
    public boolean closeDocument()
    {
		if (checkClose()) {
			try {
				setClosed(true);
				//	_frame.dispose();
				// dispose should be automagic from _frame.setDefaultCloseOperation
			//	XMLEditorFrame.getSharedInstance().activateDocumentAfterClose();
				return true;
				
			}
			catch (java.beans.PropertyVetoException ex) {
				ex.printStackTrace();
		//		MerlotDebug.exception(ex);
			}
		}
		return false;
		
    }


/*
	public void cut() {
		editor.cut();
		editor.requestFocus();
	}
	
	public void copy() {
		editor.copy();
		editor.requestFocus();
	}
	public void paste() {
		editor.paste();
		editor.requestFocus();
	}
 */	
	public void select() {
		try {
			setSelected(true);
			editor.grabFocus();
		} catch (java.beans.PropertyVetoException pve) {
		}
	}
	public void setCaretPosition(int n) {
//		try {
			editor.grabFocus();
//			editor.setCaretPosition(editor.getLineStartOffset(n));
//		} catch (BadLocationException ble) {
//			System.out.println("MyInternalFrame setCaretPosition - Bad Location Exception");
//		}
	}
	
	@Override
	public File getFile() {
		return inFile;
	}
	
	public void saveAs(File f) {
		editor.setFile(f);
		saveAsDifferentFile(f);
	}
	
	public boolean save() {
		boolean ret = true;
		
		try {
		if(editor.getFile() != null) 
			editor.save();
		else {	
		 	editor.saveas();
			save();
		}
		
		// file saved and has filename- set _new to false
		_new = false;
		
		System.out.println(" _new = " + _new);
		
		editor.grabFocus();

        boolean oldSaved = saved;

		saved = true;
		if (getTitle().indexOf("*") != -1) {
			setTitle(getTitle().substring(0, getTitle().indexOf("*")));
		}

        this.tellPropertyChange("SAVED", new Boolean(oldSaved), new Boolean(saved));
        
		} catch(Exception e) {
			ret=false;
		}

        

		return ret;
	}

// PREREL  OVERRIDE TO RET STRING
	public String saveFrame() {
		String ret = "true";
		
		try {
		if(editor.getFile() != null) 
			editor.save();
		else {	
		 	editor.saveas();
			save();
		}
		
		// file saved and has filename- set _new to false
		_new = false;
		
		System.out.println(" _new = " + _new);
		
		editor.grabFocus();

        boolean oldSaved = saved;

		saved = true;
		if (getTitle().indexOf("*") != -1) {
			setTitle(getTitle().substring(0, getTitle().indexOf("*")));
		}

        this.tellPropertyChange("SAVED", new Boolean(oldSaved), new Boolean(saved));
        
		} catch(Exception e) {
			ret="false";
		}

        

		return ret;
	}



  	public boolean saveAsDifferentFile(File f) {
		boolean ret = true;

		try {

            String title = f.getName();

		if(editor.getFile() != null)
			editor.save();
		else {
		 	editor.saveas();
			save();
		}

		// file saved and has filename- set _new to false
		_new = false;

		System.out.println(" _new = " + _new);

		editor.grabFocus();

        boolean old_saved = saved;

		saved = true;

        if (getTitle().indexOf("*") != -1) {
	//		setTitle(getTitle().substring(0, getTitle().indexOf("*")));
            setTitle((title));
		}

        this.tellPropertyChange("SAVED",  new Boolean(old_saved), new Boolean(saved));

		} catch(Exception e) {
			ret=false;
		}
		return ret;
	}





	public void changedUpdate(DocumentEvent de) {
	}

    public void insertUpdate(DocumentEvent de) {
	//	if (saved) {
            boolean old_saved = saved;
			if(getTitle().indexOf("*") == -1) {
				setTitle(getTitle() + "*");
				saved = false;

                   this.tellPropertyChange("SAVED", new Boolean(old_saved), new Boolean(saved));

			}
	//	}
	}
	public void removeUpdate(DocumentEvent de) {
		if (saved) {
            if(getTitle().indexOf("*") == -1) 
    			setTitle(getTitle() + "*");
        		saved = false;
		}
	}
	public void inputMethodTextChanged(InputMethodEvent ime) {
	}
	public void caretPositionChanged(InputMethodEvent ime) {
	}
	public void setFont(Font f) {
		editor.setFont(f);
	}
	public Dimension getPreferredSize() {
		return new Dimension(300, 200);
	}
	
	public JTextComponent getTextComponent() {
		return editor;
	}

    
    


	public File getXslFile() {
		return editor.getXslFile();
	}
	
	public void setStyleSheet(String file) {
		editor.setStyleSheet(file);
	}
	
    public UndoManager getUndoManager() 
    {
        if(getCurrentView() != null && getCurrentView().equals("TEXTVIEW"))
        	return _undoManager;		
        else
        	// OV c 200309
   //     	if(ktest.getTreeTable() != null)
        	if(ktest != null && ktest.getTreeTable() != null)
        		return ktest.getTreeTable().getUndoManager();
        return null;
        
    }
	
	public boolean isNew() {
		return _new;
	}
	
	public void setNew(boolean isnew) {
        boolean old_new = !_new;
		_new = isnew;
        this.tellPropertyChange("SAVED", new Boolean(old_new), new Boolean(!_new));
	}
	
	public boolean getWordwrap() {
		return editor.getWordWrap();
	}
	
	public boolean isLoaded() {
	
		if(editor.getDocument().getLength() >0 )
			return true;
		
		return false;
	
	}
	
	public void setWordwrap(boolean wrap) {
		System.out.println(" in myintf setwordwrap  wrap = " + wrap); 
		editor.setWordWrap(wrap);
	}
	
	public void setProcessed(boolean proc) {
		processed = proc;
	}
	
	public boolean isProcessed() {
		return processed;
	}
	
	public void setWaitingForAssign(boolean assign) {
		waitingToAssign = assign;
	}
	
	public boolean isWaitingForAssign() {
		return waitingToAssign;
	}

	public File getDtdFile() {
		return editor.getDtdFile();
	}
	
	public File getSchemaFile() {
		return editor.getSchemaFile();
	}


	public void actionPerformed(ActionEvent e){ 	
        ((CardLayout)getLayout()).show(this, e.getActionCommand()); 
    } 


	class MyCardLayout extends CardLayout { 
        public void show(Container parent, String name){ 
            super.show(parent, name); 
            updateToolbar(); 
        } 
 
        public void first(Container parent){ 
            super.first(parent); 
            updateToolbar(); 
        } 
 
        public void next(Container parent){ 
            super.next(parent); 
            updateToolbar(); 
        } 
 
        public void previous(Container parent){ 
            super.previous(parent); 
            updateToolbar(); 
        } 
 
        public void last(Container parent){ 
            super.last(parent); 
            updateToolbar(); 
        } 
 
        private void updateToolbar(){ 
          
        } 
 
        public Dimension preferredLayoutSize(Container parent){ 
            int count = getComponentCount(); 
            for(int i = 0; i<count; i++){ 
                Component comp = getComponent(i); 
                if(comp.isVisible()) 
                    return comp.getPreferredSize(); 
            } 
            return new Dimension(100, 100); // fallback 
        } 
    } 
	
    public void selectionActivatedByButton(ActionEvent e) {
	
    	tabsComp.selectionActivatedByButton(e);
	
    	currentView = tabsComp.getCurrentView();
    }
    
   public String getCurrentView() {
	   
	   
   	   return tabsComp.getCurrentView();
   } 
   
   public void setCurrentView(String view) {
   	
   	try {
   	
   		/*
   		((CardLayout)getContentPane().getLayout()).removeLayoutComponent(emptyPanel); 
   		createAndAddTreeTable();
		System.out.println("xfer focus to grid ");					
	
		setSelected(true);		
		ktest.getTreeTable().requestFocusInWindow();   	    
	*/
   	
   	
   	if(view.intern() == "TEXTVIEW".intern()) {
   		
   		document = ktest.getDocument();

		System.setProperty("javax.xml.transform.TransformerFactory", "com.icl.saxon.TransformerFactoryImpl");					
		
		// Write it out again
		TransformerFactory xformFactory 
		 = TransformerFactory.newInstance();
		
		Transformer idTransform = xformFactory.newTransformer();

		Source input = new DOMSource(document);
		
		java.io.StringWriter strWrite = new StringWriter();
		
		Result output = new StreamResult(strWrite);
		
		idTransform.setOutputProperty(OutputKeys.INDENT, "4");				

		String dt = getDocType();
		boolean isinXHTMLNamespace = isInNamespace();

		
		if(dt == null)
			dt = "";
		
		String otyp = "xml";
		if(inFile.getName().endsWith("txt") || inFile.getName().endsWith("text"))
			otyp = "text";
		else if(dt.indexOf("xhtml") > 0 && isinXHTMLNamespace) {
			otyp = "xhtml";
		} else if(dt.indexOf("html") > 0)
			otyp = "html";
		
		if(dt.trim().startsWith("PUBLIC")) { 
			int hpos = dt.indexOf("#");
			int hpos2 = dt.indexOf("#", hpos+1);
			String pubId = dt.substring(hpos+1, hpos2);
			
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, pubId);
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.substring(hpos2+1));
		}
		else {
			int hpos = dt.indexOf("#");
			
			idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, dt.substring(hpos+1));
		}
			
		idTransform.setOutputProperty(com.icl.saxon.output.SaxonOutputKeys.INDENT_SPACES, "4");

		idTransform.transform(input, output);
		
		String bufText = strWrite.toString();
		
//		bufText = replaceDoctype(strWrite, dt);
		
		editor.setText(bufText);	

		editor.setSaved(false);
		editor.setCaretPosition(0);
   		
    	 
   	}    

				
			} catch (TransformerFactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
			} catch(Exception e) {
				e.printStackTrace();
				this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
			}
				// OV commented 250409 for multiview		
				//    ((CardLayout)getContentPane().getLayout()).show(getContentPane(), view); 		   		
	
			currentView = view;
		
			  //			repaint();
			  //	revalidate();	
   	}	
   	
   	
    private boolean isInNamespace() {
	   
	   String xhtmuri = "http://www.w3.org/1999/xhtml";
	   
	   org.w3c.dom.Node root = document.getDocumentElement();
	   

	   NamedNodeMap nmp = root.getAttributes();
       
       HashMap nmspHash = new HashMap();
       
       for(int h=0;h<nmp.getLength();h++) {
           Node attr = nmp.item(h);
           
			if(attr.getNodeName().startsWith("xmlns")) {
	    		String nmsp = attr.getNodeName();
				String nmspval = attr.getNodeValue();

				if(nmspval != null && nmspval.equals(xhtmuri))
					return true;
			}
			   
       }
       
	   return false;
	   
   }

   
   private String getDocType() {
	   
	   DocumentType docType = document.getDoctype();
	   
	   if(docType == null)
		   return null;
	   
	   String doctypename = docType.getName();
	   
		String resolvePub = null;	
		String resolveSys = null;	

	   if(docType != null)
		{
				resolvePub = docType.getPublicId();	
				resolveSys = docType.getSystemId();	
			}

	   String type = resolvePub == null? "SYSTEM" : "PUBLIC";
	   
		System.out.println(" resolvePub  " + resolvePub);
		System.out.println(" resolveSys " + resolveSys);
		
		String doctype = "";
		if(type.equals("PUBLIC"))
	//		doctype = "<!DOCTYPE " + doctypename + " " + type + "" + resolvePub + " '" + resolveSys + "' >";
			doctype = " " + type + "# " + resolvePub + " # " + resolveSys + " ";
		else
		//	doctype = "<!DOCTYPE " + doctypename + " " + type + " '" + resolveSys + "' >";
			doctype = " " + type + "# " + resolveSys + " ";
		
		
		System.out.println(" docType ret " + doctype);
		
		return doctype;
   }
   
   private void parseBuffer() throws Exception { 
	   
			
			System.setProperty("javax.xml.transform.TransformerFactory", "com.icl.saxon.TransformerFactoryImpl");					
			
			DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
            docbuilderfact.setNamespaceAware(true);
			DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
			docbuilderfact.setValidating(false);

			CustomEntityResolver cress = CustomEntityResolver.getInstance(); 
			
			docbuilder.setEntityResolver(cress);
			
			javax.swing.text.Document doc = editor.getDocument();
			
			String bufText = doc.getText(0, doc.getLength());
			
			document = docbuilder.parse(new InputSource(new StringReader(bufText)));

			
			

   }
   
	public JXmlTreeTable createOrUpdateTreeTable() {

	//	JScrollPane gridScroller = null;
        gridTable = null;

		try {
	
			Properties p = xmlEditor.getProperty();
			String rvis = p.getProperty("ISROOTVISIBLE");
			
			System.out.println("isRootVisible " + rvis);			
			boolean  isRootVisible = (rvis.intern() == "true".intern())? true : false;

			System.out.println("isRootVisible " + isRootVisible);			

                        try {
                            parseBuffer();
                        }
                       	catch (ParserConfigurationException pcfg)
                        {
                            if(document == null) {
                                
                                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance ();
                                dbf.setNamespaceAware(true);
                                DocumentBuilder db = dbf.newDocumentBuilder ();
                                document = db.newDocument ();
                                document.createElement ("doc");

                            }
                        }

                        
			ktest = new XmlKitTest(isRootVisible, document, xmlEditor);

			gridTable = ktest.getTreeTable();
			
    	//OV c 200309
		//	gridScroller = new JScrollPane(ktest.getTreeTable(),
		//		JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
		//		JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//    gridScroller.setRowHeaderView(ktest.getRowHeader());
	
			if(!ktest.getTreeTable().hasFocus()) { 
				ktest.getTreeTable().grabFocus(); 
			}

	//OV c 200309
	//		getContentPane().add("GRIDVIEW", gridScroller);

	} catch (FactoryConfigurationError e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
        
	} catch (ParserConfigurationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
	} 
               
    catch (BadLocationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
	} catch (SAXException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
	}
              
	catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		this.xmlEditor.fireStatusChanged(new StatusEvent(e.getLocalizedMessage(), 0,ColWidthTypes.ERROR));
	}
	
		return gridTable;
	}   		

	public boolean dispatchKeyEvent(java.awt.event.KeyEvent e){
		
		System.out.println(" in frame dke ");
   	    KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
	
		if(ktest != null && getCurrentView().intern() == "GRIDVIEW".intern()) {
			manager.redispatchEvent(ktest.getTreeTable(),e);	return false;
		} else  {
			manager.redispatchEvent(xmlEditor,e);			return false;
		}
	}


    public class MyOwnFocusTraversalPolicy
                 extends FocusTraversalPolicy {

        public Component getComponentAfter(Container focusCycleRoot,
                                           Component aComponent) {
            if(ktest  == null)
                return null;
            
            if (aComponent.equals(editor)) {
                return ktest.getTreeTable();
            } else if (aComponent.equals(ktest.getTreeTable())) {
                return editor;
            } 
            
            return editor;
        }

        public Component getComponentBefore(Container focusCycleRoot,
                                       Component aComponent) {
            if (aComponent.equals(editor)) {
                return ktest.getTreeTable();
            } else if (aComponent.equals(ktest.getTreeTable())) {
                return editor;
            } 
            return editor;
        }

        public Component getDefaultComponent(Container focusCycleRoot) {
            return editor;
        }

        public Component getLastComponent(Container focusCycleRoot) {
            return ktest.getTreeTable();
        }

        public Component getFirstComponent(Container focusCycleRoot) {
            return editor;
        }
    }

    public TreeTableClipboard getTreeTableClipboard() {
        if(ktest == null)
            return null;
        
        return ktest.getTreeTable();
    }

    public TreeTableXmlOps getTreeTableXmlOps() {
        return ktest.getTreeTable();
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        _title = title;
        super.setTitle(title);

    }

	public void addPropertyChangeListener(PropertyChangeListener l) {
        if(this.listeners != null)
            listeners.add(l);
	 }

    public void removePropertyChangeListener(PropertyChangeListener l) {
          if(this.listeners != null && this.listeners.size() > 0)
    		  listeners.remove(l);
	}

     public void tellPropertyChange(String prop, java.lang.Object oldobj, java.lang.Object newobj) {

         if(listeners != null && prop.equals("SAVED")) {
             for(int i=0; i<listeners.size();i++) {

                 PropertyChangeListener listener = (PropertyChangeListener)listeners.get(i);
                 listener.propertyChange(new PropertyChangeEvent(this, prop, (Boolean) oldobj, (Boolean) newobj));

             }
         }
    }


    public void loadFromDom(Document doc) {

            try {
                    // use specific Xerces class to write DOM-data to a file:
                    XMLSerializer serializer = new XMLSerializer();
                    StringWriter sw = new StringWriter();

                    serializer.setOutputCharStream(sw);
                    serializer.serialize(gridTable.getDocument());

                    this.editor.setText(sw.toString());


            } catch(Exception e) {

                    this.xmlEditor.fireStatusChanged(new StatusEvent("document.load.error", ColWidthTypes.ERR));

             }

    }
    


}








