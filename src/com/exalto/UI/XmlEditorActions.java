/*
 * XmlUtilActions.java
 *
 * Created on March 18, 2008, 4:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI;

import com.exalto.ColWidthTypes;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import javax.swing.undo.UndoManager;

import com.exalto.UI.font.FontDialog;
import com.exalto.UI.grid.xquery.XQueryProcessor;
import com.exalto.UI.mdi.DesktopView;
import com.exalto.UI.mdi.Editor;
import com.exalto.UI.mdi.MyInternalFrame;
import com.exalto.UI.util.FindDialog;
import com.exalto.util.ExaltoResource;
import com.exalto.util.FileUtil;
import com.exalto.util.StatusEvent;
import com.exalto.util.SwingWorker;
import com.exalto.util.TrangUtil;
import com.exalto.util.XmlUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.MissingResourceException;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

/**
 *
 * @author omprakash.v
 */
public class XmlEditorActions  implements TreeTableClipboard {
    //   implements XmlUtilConstants {
    
    protected static XmlEditor xeditor;
    
    private static XmlEditorActions _instance = null;
    
    private TreeTableClipboard viewAction = null;

    protected GridViewAction _gridVWAction;
    protected TextViewAction _textVWAction;
    protected CutAction cutAction;
    protected CopyAction copyAction;
    protected PasteAction pasteAction;
    protected UndoAction undoAction = new UndoAction();
    protected RedoAction redoAction = new RedoAction();
    protected QuitAction _quitAction;
    protected NewAction _newAction;
    protected OpenAction _openAction;
    
    protected NewProjectAction _newProjAction;

	// PREREL A FR PRINT  2 LNS
    protected PrintAction _printAction;
    protected PrintPreviewAction _printPreviewAction;
   
    protected SaveAction _saveAction;
    protected SaveAsAction _saveAsAction;
    protected CloseAction _closeFileAction;
    
    protected CloseProjectAction _closeProjectAction;

    protected WellformedAction _wellFormedAction;
    
    protected ValidityAction _validityAction;
    protected WordWrapAction _wordwrapAction;
    
    protected CDATAAction _cdataAction;
    protected CommentAction _commentAction;
    
    protected FindAction _findAction;
    protected FindNextAction _findNextAction;
    
    protected GridopsAction _gridopsAction;
  //  protected RenameAction _renameAction;
  //  protected DeleteAction _deleteAction;
  //  protected ExpandAction _expandAction;
  //  protected ExpandAllAction _expandAllAction;    
    protected FormatAction _formatAction;

    protected PrettyAction _prettyAction;

    protected Hashtable commands = new Hashtable();
    
    ArrayList actionsList = new ArrayList();

    public static final String openAction = "open";
    public static final String newAction  = "newfile";
    public static final String newProjectAction  = "newproject";
    public static final String saveAction = "save";
    public static final String saveasAction = "saveas";
    public static final String closeAction = "close";

    public static final String closeProjAction = "closeproject";

    public static final String quitAction = "quit";
    public static final String genfoAction = "generate-fo";
    public static final String balanceAction = "balance-column-width";
    public static final String genpdfAction = "generate-pdf";
    public static final String optionAction = "show-options";
    public static final String aboutAction = "about";
    public static final String selectAllAction = "select-all";
    public static final String wordwrapAction = "wordwrap";
   // public static final String jdcshowAction = "jdcshow";
   // public static final String jdchideAction = "jdchide";
    public static final String textviewAction = "textview";
    public static final String gridviewAction = "gridview";
    public static final String findAction = "find";
    public static final String findNextAction = "FindNext";

    public static final String cdataAction = "add-cdata";
    public static final String commentAction = "add-comment";
   
    public static final String gridopsAction = "gridops";

    public static final String prettyAction = "pretty-print";
   // PREREL A FR PRINT  2 LNS
	public static final String printAction = "print";
   	public static final String printPreviewAction = "printprev";
   
    
    protected XmlUtils xutils;

    // delegates
    JTextComponent jtexComp;
    TreeTableClipboard treeTableClipboard;
    TreeTableClipboard viewAct;
    MyInternalFrame myf;
    
    TreeTableXmlOps treeTableXmlOps;
    
    FindDialog dialog;
    
    public boolean clipBoardHasContents;
    
    FontDialog m_fontDialog;
    protected String[] m_fontNames;
    protected String[] m_fontSizes;
    
    
    /**
     * Actions defined by the Notepad class
     */
    private Action[] defaultActions = {
             cutAction,
                copyAction,
                pasteAction,
        _textVWAction,
        _gridVWAction
    };

    XQueryProcessor xqueryProcessor;

    /** Creates a new instance of XmlUtilActions */
    public XmlEditorActions() {
        _instance = this;
    }
    
    /** Creates a new instance of XmlUtilActions */
    public XmlEditorActions(XmlEditor xeditor) {
        this.xeditor = xeditor;

	xutils = XmlUtils.getInstance();
        initActions();
        _instance = this;

    }
    
    /** Creates a new instance of XmlUtilActions */
    public static XmlEditorActions getSharedInstance(XmlEditor xeditor) {
           if(_instance == null)
               new XmlEditorActions(xeditor);
                
           return _instance;
    }

        /** Creates a new instance of XmlUtilActions */
    public static XmlEditorActions getSharedInstance() {
           if(_instance == null)
               new XmlEditorActions(xeditor);
                
           return _instance;
    }

    public XmlEditorActions(MyInternalFrame myf) {
        this.myf = myf;
     }

    
    private void initActions() {
    
            cutAction = new CutAction();
            actionsList.add(cutAction);
            
            copyAction = new CopyAction();
            actionsList.add(copyAction);
            
            pasteAction = new PasteAction();
            actionsList.add(pasteAction);
            
            
            UndoAction undoAction = new UndoAction();
            actionsList.add(undoAction);
            
            RedoAction redoAction = new RedoAction();
            actionsList.add(redoAction);
            
        /*    
        // ov added for find/find next
            FindAction findAction = new FindAction(this);
            FindNextAction findnextAction = new FindNextAction(this);
     
            AssignStyleAction assignStyleAction = new AssignStyleAction();
        */    
     
           _quitAction = new QuitAction();
           actionsList.add(_quitAction);
            
           _saveAction = new SaveAction();
           actionsList.add(_saveAction);

           _saveAsAction = new SaveAsAction();
           actionsList.add(_saveAsAction);

   //        _saveAsDavAction = new SaveAsDavAction();
   //        _revertFileAction = new RevertFileAction();
           _closeFileAction = new CloseAction();
           actionsList.add(_closeFileAction);

           _closeProjectAction = new CloseProjectAction();
           actionsList.add(_closeProjectAction);

   //        _saveAction.setEnabled(false);
   //        _saveAsAction.setEnabled(false);
   //        _saveAsDavAction.setEnabled(false);
   //        _revertFileAction.setEnabled(false);
   //        _closeFileAction.setEnabled(false);
           
           _newAction = new NewAction();
           actionsList.add(_newAction);

           _newProjAction = new NewProjectAction();
           actionsList.add(_newProjAction);

           _openAction = new OpenAction();
           actionsList.add(_openAction);

			// PREREL A FR PRINT  4 LNS
           _printAction = new PrintAction();
           actionsList.add(_printAction);

           _printPreviewAction = new PrintPreviewAction();
           actionsList.add(_printPreviewAction);
           
           _wellFormedAction = new WellformedAction();
           actionsList.add(_wellFormedAction);

           _validityAction = new ValidityAction();
           actionsList.add(_validityAction);
           
           _wordwrapAction = new WordWrapAction();
           actionsList.add(_wordwrapAction);

           _cdataAction = new CDATAAction();
           actionsList.add(_cdataAction);

           _commentAction = new CommentAction();
           actionsList.add(_commentAction);

           
           _findAction = new FindAction(xeditor);
           actionsList.add(_findAction);
           
           _findNextAction = new FindNextAction(xeditor);
           actionsList.add(_findNextAction);
   
     /*  
           _openDavFileAction = new OpenDavFileAction();
           _openLibraryAction = new OpenLibraryAction();
           _newLibraryAction = new NewLibraryAction();
           //	_newLibraryAction.setEnabled(false); // XXX not implemented
     
           _undoAction = new UndoAction();
           _cutAction = new CutAction();
           _copyAction = new CopyAction();
           _pasteAction = new PasteAction();
           _backAction = new BackAction();
           _cancelAction = new CancelAction();
     
           _pasteAction.setEnabled(false);
           _undoAction.setEnabled(false);
           _backAction.setEnabled(false);
           _cancelAction.setEnabled(false);
     
           _prefsAction = new EditPrefsAction();
           //_prefsAction.setEnabled(false);
     
           _systemLFAction = new DefaultLFAction();
           _metalLFAction = new MetalLFAction();
     
           _windowCascadeAction = new WindowCascadeAction();
           _windowTileHorizontalAction = new WindowTileHorizontalAction();
           _windowTileVerticalAction = new WindowTileVerticalAction();
     
           _windowCascadeAction.setEnabled(false);
           _windowTileHorizontalAction.setEnabled(false);
           _windowTileVerticalAction.setEnabled(false);
     
           _pluginLoadAction = new PluginLoadAction();
     
           _aboutAction = new AboutAction();
     
           _helpAction = new HelpAction();
     */
        
	        _gridVWAction = new GridViewAction();
	        actionsList.add(_gridVWAction);
	        
	        _textVWAction = new TextViewAction();
	        actionsList.add(_textVWAction);
	        
	        _gridopsAction = new GridopsAction();
	         actionsList.add(_gridopsAction);

	        _formatAction = new FormatAction(xeditor);
	         actionsList.add(_formatAction);

	           _prettyAction = new PrettyAction(xeditor);
	           actionsList.add(_prettyAction);

               // XML conversion stuff               
               AbstractAction xqueryAction =
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("xquery"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {

                //            XQueryProcessor xqproc = new XQueryProcessor();
                            processXQuery(e);
                            
						}
				};

               AbstractAction toCSVAction =
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("toCSV"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {

                            convertToCSV(e);
						}
				};

               AbstractAction toXMLAction =
				new AbstractAction(java.util.ResourceBundle
						.getBundle("exalto/xmlgrid").getString("toXML"), new ImageIcon("images/inselem.gif")) {
						public void actionPerformed(ActionEvent e) {
                            convertToXML(e);
						}
				};

	        actionsList.add(xqueryAction);
	        actionsList.add(toCSVAction);
	        actionsList.add(toXMLAction);



	         GraphicsEnvironment ge = GraphicsEnvironment.
	         getLocalGraphicsEnvironment();
	         m_fontNames = ge.getAvailableFontFamilyNames();
	         m_fontSizes = new String[] {"8", "9", "10", "11", "12", "14",
	        	      "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};

	         
	         m_fontDialog = new FontDialog(xeditor, m_fontNames, m_fontSizes);

	         
                    // install the command table
                    commands = new Hashtable();
        
                    for (int i = 0; i < actionsList.size(); i++) {
                        Action a = (Action) actionsList.get(i);

                //        System.out.println(" def act a name = " + a.getValue(Action.NAME));
                //        System.out.println(" def act a = " + a);

                        commands.put(a.getValue(Action.NAME), a);
                    }


	           AbstractAction [] trangActions = TrangUtil.getTrangActions(xeditor);

               for(int k=0;k<trangActions.length;k++) {

                        Action a = (Action) trangActions[k];

                  //      System.out.println(" def act a name = " + a.getValue(Action.NAME));
                  //      System.out.println(" def act a = " + a);

                        commands.put(a.getValue(Action.NAME), a);
               }





    }
    
    
    /**
     * Fetch the list of actions supported by this
     * editor.  It is implemented to return the list
     * of actions supported by the embedded JTextComponent
     * augmented with the actions defined locally.
     */
    public Action[] getActions() {
		DocumentPane je =  new DocumentPane();
		//OV 18/03/08 c 1 line
               return TextAction.augmentList(je.getActions(), defaultActions);
//	return TextAction.augmentList(textArea.getActions(), defaultActions);
//      	return defaultActions;
                
    }
      
    protected Action getAction(String cmd) {
   //             System.out.println(" commands = " + commands.toString());

		return (Action) commands.get(cmd);
    }

    
 // begin code for clipboard ops text/grid
    public TreeTableXmlOps getTargetComponentForXml(MyInternalFrame myf, JTextComponent jc) {

         String currentView = myf.getCurrentView();
  
         if(currentView != null) {
             
             if(currentView.equals("TEXTVIEW")) {
                 JTextComponent jtexComp = (JTextComponent) jc;
                 TextXmlAction txtxmlOpsAction = new TextXmlAction(jtexComp, this);    
                 return txtxmlOpsAction;
             
             } else {
            	 treeTableXmlOps = myf.getTreeTableXmlOps();
                 GridXmlAction grxmlOpsAction = new GridXmlAction(treeTableXmlOps);    
                 return grxmlOpsAction;
             }
         }
         
         return null;
         
     }
    
// begin code for clipboard ops text/grid
   public TreeTableClipboard getTargetComponent(MyInternalFrame myf, JTextComponent jc) {

        String currentView = myf.getCurrentView();
 
        if(currentView != null) {
            
            if(currentView.equals("TEXTVIEW")) {
                JTextComponent jtexComp = (JTextComponent) jc;
                viewAction = new TextEditAction(jtexComp);    
                return viewAction;
            
            } else {
                treeTableClipboard = myf.getTreeTableClipboard();
                viewAction = new GridEditAction(treeTableClipboard);    
                return viewAction;
            }
        }
        
        return null;
        
    }

   private class TextXmlAction implements TreeTableXmlOps {

	   JTextComponent jtexComp;
	   XmlEditorActions xeditorActions;
	   
       public TextXmlAction(JTextComponent jc, XmlEditorActions xeditorActions) {
           this.jtexComp = jc;
           this.xeditorActions = xeditorActions;
       }
       
       public void checkWellFormed() {
    	   xeditorActions.doCheckWellFormed();
       }    

       public void checkValid() {
    	   xeditorActions.doCheckValidity();
       }    

	   public void doSave(String s) {
		   xeditorActions.doSave();
	   }
	
	   public void doSaveAs(File s){
		   xeditorActions.doSaveAs(s);
	   }

	   public void find(){
		   xeditorActions.find();
	   }

// PREREL A FR PRINT  4 LNS
	   public boolean print()  throws java.awt.print.PrinterException {
		   return xeditorActions.print();
	   }

	   public void printPreview(){
	   }


	   public void insertNode(String type) {} 
       public void renameNode() {}
       public void deleteNode(String type) {}
       public void expandNode() {}
       public void expandAll(String type) {}

   }

   private class GridXmlAction implements TreeTableXmlOps {

       public GridXmlAction(TreeTableXmlOps jc) {
       		treeTableXmlOps = jc;
       }

       public void checkWellFormed() {
    	   treeTableXmlOps.checkWellFormed();
       }    

       public void checkValid() {
    	   treeTableXmlOps.checkValid();
       }    
	   
       public void doSave(String s) {

           DesktopView desktopView = xeditor.getDesktopView();

           MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

           try {
               if (desktopView.fileSelected()) {
                //		System.out.println(" file selected");
                //		if (desktopView.selectedHasFileName()) {
                if (!desktopView.selectedIsNew()) {

                    File file = desktopView.getSelectedFile();
                    //	if(!file.getName().equals("XSL result") && !file.getName().endsWith("fmt.fo")) {
                    System.out.println(" file selected has file name");

                 //   desktopView.saveSelectedFile();

                    treeTableXmlOps.doSave(file.getAbsolutePath());

                    String saveFile = file.getAbsolutePath();
                    long filesum = FileUtil.calculateFileChecksum(saveFile);

                    System.out.println(" file saved = " + saveFile);
                    System.out.println(" file checksum = " + filesum);

                    Hashtable checkSumHash = xeditor.getCheckSumHash();
                            
                    checkSumHash.put(saveFile, new Long(filesum));

                    System.out.println("checksumhash after  save = " + checkSumHash);

                } else {
                    System.out.println(" file selected is new.. calling save as");
                    doSaveAs(null);
                }

                if(myf != null)
                    myf.tellPropertyChange("SAVED",  false, true);

            } else {
                System.out.println("Dialog Error - No file selected.");
            }

        } catch(Exception e) {
            //		setStatus("unable to save document");
            xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"file.save.err"),0, ColWidthTypes.ERROR));
            return;
        }


       }
	   
       public void doSaveAs(File f) {

            if(f != null)
                return;       //bail

            DesktopView desktopView = xeditor.getDesktopView();

            if (desktopView.fileSelected()) {

                String dir = xeditor.getCurrentDir();

                System.out.println(" save as dir " + dir);

                JFileChooser jfc = null;

                jfc = new JFileChooser(dir);
                jfc.setCurrentDirectory(new File(dir));

                if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    System.out.println(" save as file name " + jfc.getSelectedFile());
                    File selectedFile = jfc.getSelectedFile();
                    //	_current_dir = selectedFile;
                    if( selectedFile.exists()) {
                        int response =             JOptionPane.showConfirmDialog(null,
                                "Overwrite existing file?",
                                "Confirm Overwrite",
                                JOptionPane.OK_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if( response == JOptionPane.CANCEL_OPTION)
                            return;
                    }

                   treeTableXmlOps.doSaveAs(jfc.getSelectedFile());

                }

            }

            xeditor.updateMenuAndTitle();


       }

	   public void find(){
    	   treeTableXmlOps.find();
	   }

// PREREL A FR PRINT  5 LNS
	   public boolean print()  throws java.awt.print.PrinterException{
		   return treeTableXmlOps.print();
	   }

	   public void printPreview(){
	   }


	   
	   public void insertNode(String type) {
    	   
     	   treeTableXmlOps.insertNode(type);
     	   
       } 
       public void renameNode() {
     	   treeTableXmlOps.renameNode();
     	   
    	   
       }
       public void deleteNode(String type) {
     	   treeTableXmlOps.deleteNode(type);
     	   
       }
       public void expandNode() {
    	   treeTableXmlOps.expandNode();
    	   
       }
       public void expandAll(String type) {
    	   treeTableXmlOps.expandAll(type);
    	   
       }
       
   }

   
   public interface TreeTableXmlOps {
	   public void doSave(String s);
	   public void doSaveAs(File file);
       public void checkWellFormed(); 
       public void checkValid();
       public void find();
	   // PREREL A FR PRINT  2 LNS
       public boolean print() throws java.awt.print.PrinterException;
       public void printPreview();

         
       public void insertNode(String type);
       public void renameNode();
       public void deleteNode(String type);
       public void expandNode();
       public void expandAll(String type);
   }
   
    // begin code for clipboard ops text/grid
   private UndoManager getTargetComponent() {
 
	   String currentView = myf.getCurrentView();
	   
       if(currentView != null) {
           
           if(currentView.equals("TEXTVIEW")) {
               return myf.getUndoManager();
           } else {
               treeTableClipboard = myf.getTreeTableClipboard();
               return treeTableClipboard.getUndoManager();
           }
       }
       
       return null;
	   
	   
   }

   private class TextEditAction implements TreeTableClipboard {

        public TextEditAction(JTextComponent jc) {
            jtexComp = jc;
        }
        
        public void cut(ActionEvent evt) {
            jtexComp.cut();
        }    

        public void paste(ActionEvent evt) {
            jtexComp.paste();
        }    

        public void copy(ActionEvent evt) {
            jtexComp.copy();
        }    

        public void undo(ActionEvent evt) {
      //      jtexComp
        }    

        public void redo(ActionEvent evt) {
     //       jtexComp    
            
        }    

        public UndoManager getUndoManager() {
            return null;
        }    
        
    }

    private class GridEditAction implements TreeTableClipboard {

        public GridEditAction(TreeTableClipboard jc) {
            treeTableClipboard = jc;
        }

        public void cut(ActionEvent evt) {
            treeTableClipboard.cut(evt);
        }    

        public void paste(ActionEvent evt) {
        treeTableClipboard.paste(evt);
        }    

        public void copy(ActionEvent evt) {
        treeTableClipboard.copy(evt);
        }    

        public void undo(ActionEvent evt) {
            treeTableClipboard.undo(evt);
        }    

        public void redo(ActionEvent evt) {
            treeTableClipboard.redo(evt);
        }    

        public UndoManager getUndoManager() {
            return treeTableClipboard.getUndoManager();
        }    
        
    }

// outer class clipboard methods begin 
        public void cut(ActionEvent evt) {
            getCurrentViewAction().cut(evt);
        }    

        public void paste(ActionEvent evt) {
            getCurrentViewAction().paste(evt);   
        }    

        public void copy(ActionEvent evt) {
            getCurrentViewAction().copy(evt);
        }    

        public void undo(ActionEvent evt) {
            this.getUndoManager().undo();
        }    

        public void redo(ActionEvent evt) {
            this.getUndoManager().redo();
        }    

     
        public UndoManager getUndoManager() {
    
        	   String currentView = myf.getCurrentView();
        	   
               if(currentView != null) {
                   
                   if(currentView.equals("TEXTVIEW")) {
                       return myf.getUndoManager();
                   } else {
                       treeTableClipboard = myf.getTreeTableClipboard();

                       UndoManager undomgr = treeTableClipboard.getUndoManager();

                       System.out.println(" in redo undomgr =  " + undomgr.hashCode());

                       return treeTableClipboard.getUndoManager();
                   }
               }
               
               return null;
        	
        }    
   
        
    public TreeTableClipboard getCurrentViewAction() {
          return viewAction;
    }       
 
 // end clipboard


// begin inner classes for actions

    protected class GridViewAction extends AbstractAction {
        
        public GridViewAction() {
            
            super("gridview");
         //   System.out.println(" actions gv ");
            
//            XmlUtils.loadActionResources(this,"UI","view.grid");
            
        }
        
        public void actionPerformed(final ActionEvent e) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	    	    DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

            	            if(!myf.getCurrentView().equals("GRIDVIEW"))
            	            	myf.selectionActivatedByButton(e);
            	            
            	            
     //       	            xeditor.showGridView(e);
          	                return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };
    	
                    worker.start();

        }

        
    }
    
    protected class TextViewAction extends AbstractAction {
        
        public TextViewAction() {
            super("textview");
         //   System.out.println(" actions gv ");
            
 //           XmlUtils.loadActionResources(this,"UI","view.text");
            
        }
        
        public void actionPerformed(final ActionEvent e) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	    	    DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

            	            if(!myf.getCurrentView().equals("TEXTVIEW"))
            	            		myf.selectionActivatedByButton(e);
            	    		
            	        //    xeditor.showTextView(e);
            	            
            	            
            	            
          	                return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };

                    worker.start();

        }

    }
    
     protected class CutAction extends TextAction {
          
        public CutAction() {
            super("cut");
            
            XmlUtils.loadActionResources(this,"UI","edit.cut");
        }
        
        
            public void actionPerformed(final ActionEvent evt) {

        		//	logger.debug(" New action performed");

                		final SwingWorker worker = new SwingWorker() {
                        	
                	    	public Object construct() {
                	            boolean retVal = false;
                	    		   
                	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
                	            
                	            DesktopView desktopView = xeditor.getDesktopView();            
                	            
                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {
                	                
                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = new XmlEditorActions();
                	                  TreeTableClipboard target = va.getTargetComponent(myf, textComp);

	                	              if (target != null) {
	                	                  target.cut(evt);
	                	                  pasteAction.setEnabled(true);
	                	              } else {
	                	        
	                	                   desktopView.cut(evt);
	                	                  
	                	                  System.out.println(" CutAction retval = " + clipBoardHasContents);
	                	                
	                	                  pasteAction.setEnabled(clipBoardHasContents);
	                	              }
                	                          
                	              }

                	            
                	    		return null;
                	        }
                	    	
                	 	    //Runs on the event-dispatching thread.
                            public void finished() {

                            }
                	    };        	

                        worker.start();

            }
    }
    
    protected class CopyAction extends TextAction {
        public CopyAction() {
            super("copy");
            XmlUtils.loadActionResources(this,"UI","edit.copy");
        }
        
        public void actionPerformed(final ActionEvent evt) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	    	    DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
            	            
            	            if(myf != null) {
            	                
            	            
            	            //    if(myf.getCurrentView().equals("TEXTVIEW")) {
            	            
            	                
            	                System.out.println(" CopyAction actionPerformed TEXTVIEW ");

            	                //OV c 24/03/08
            	            JTextComponent textComp = getTextComponent(evt);

            	             //OV a 24/03/08
            	             XmlEditorActions va = new XmlEditorActions();
            	             TreeTableClipboard target = va.getTargetComponent(myf, textComp);

            	            if (target != null) {
            	                target.copy(evt);
            	                pasteAction.setEnabled(true);
            	            } else {
            	                desktopView.copy(evt);
            	                pasteAction.setEnabled(true);

            	            }

            	            }

            	    		return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };        	

                    worker.start();

        }
    
    
    }
    
    protected class PasteAction extends TextAction {
        public PasteAction() {
            super("paste");
            setEnabled(false);
            XmlUtils.loadActionResources(this,"UI","edit.paste");
            
        }
       
        
        public void actionPerformed(final ActionEvent evt) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	            DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
            	            
            	            if(myf != null) {
            	                
            	              //  if(myf.getCurrentView().equals("TEXTVIEW")) {
            	                
            	                System.out.println(" pasteAction actionPerformed TEXTVIEW ");
            	                
            	        //OV c 24/03/08    
            	                JTextComponent textComp = getTextComponent(evt);

            	                 //OV a 24/03/08
            	                 XmlEditorActions va = new XmlEditorActions();
            	                 TreeTableClipboard target = va.getTargetComponent(myf, textComp);

            	                if (target != null) {
            	                    target.paste(evt);
            	                } else {
            	                    desktopView.paste(evt);
            	                }

            	                
            	            }

            	    		return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };        	

                    worker.start();

        
        }

    
    }
     	/******************New undoAction class begin************************/
	
    protected class UndoAction extends AbstractAction {
        public UndoAction() {
            super("undo");
            XmlUtils.loadActionResources(this,"UI","edit.undo");
            
        }

    
        public void actionPerformed(final ActionEvent evt) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	            DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
            	            
            	            if(myf != null) {

            	             //   if(myf.getCurrentView().equals("TEXTVIEW")) {

                                try {



            	                    System.out.println(" undoAction actionPerformed TEXTVIEW ");
            	                    //OV a 24/03/08
            	                    XmlEditorActions vact = XmlEditorActions.getSharedInstance();
            	                    
            	                    vact.setTargetComponent(myf);

            	                    //OV c 24/03/08    
            	                   // desktopView.undo(evt);
            	                    
            	                    vact.undo(evt);
            	                    
            	                    boolean canUndo = vact.getUndoManager().canUndo();
            	                    if(!canUndo) {
            	                        String title = myf.getTitle();
            	                        if(title.indexOf("*") != -1) {
            	                            int spos = title.indexOf("*");
            	                            String s = title.substring(0, spos).trim();
            	                            myf.setTitle(s);
            	                        }
            	                    }


            	                 vact.resetActions();
/*
            	                 String currentView = myf.getCurrentView();
            	                 if(currentView != null) {
            	                     
            	                     if(currentView.equals("TEXTVIEW")) {
            	                        myf.getUndoManager().undo(); 	
            	                         
            	                     } else {
            	                    	 TreeTableClipboard clipbrd = myf.getTreeTableClipboard();
            	                    	 clipbrd.getUndoManager().undo();
            	                     }
            	                 }
*/
            	                 
            	              } catch(Exception e) {

                              }
            	                 
            	            }

                            return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };        	

                    worker.start();

        }

    
    }
    
    /******************New UndoAction class end **************************/
    /******************New RedoAction class begin************************/
    protected class RedoAction extends AbstractAction {
        public RedoAction() {
            super("redo");
            XmlUtils.loadActionResources(this,"UI","edit.redo");
            
        }

        /*
        public void actionPerformed(ActionEvent evt) {

            DesktopView desktopView = xeditor.getDesktopView();            
            
            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
            
            if(myf != null) {
                
      //          if(myf.getCurrentView().equals("TEXTVIEW")) {
                
                System.out.println(" undoAction actionPerformed TEXTVIEW ");
                    //OV a 24/03/08
                    XmlEditorActions vact = new XmlEditorActions(myf);
            
                    // OV c 24/03/08
                  //  desktopView.redo(evt);
                    
                    vact.redo(evt);
                    vact.resetActions();
            }
        }

    	*/
        
        public void actionPerformed(final ActionEvent evt) {

    		//	logger.debug(" New action performed");

            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	    	    DesktopView desktopView = xeditor.getDesktopView();            
            	            
            	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
            	            
            	            if(myf != null) {
            	                
            	      //          if(myf.getCurrentView().equals("TEXTVIEW")) {
            	                
            	                System.out.println(" undoAction actionPerformed TEXTVIEW ");
            	                    //OV a 24/03/08
  /*
            	                XmlEditorActions vact = XmlEditorActions.getSharedInstance();
            	                    
            	                    vact.setTargetComponent(myf);
            	            
            	                    // OV c 24/03/08
            	                  //  desktopView.redo(evt);
            	                    
            	                    vact.redo(evt);
            	                    vact.resetActions();
*/            	            
               	                 String currentView = myf.getCurrentView();
            	                 if(currentView != null) {
            	                     
            	                     if(currentView.equals("TEXTVIEW")) {
            	                      try {
                                         myf.getUndoManager().redo();
                                      } catch(Exception e) {

                                      }
            	                         
            	                     } else {
            	                    	 TreeTableClipboard clipbrd = myf.getTreeTableClipboard();
            	                    	 clipbrd.getUndoManager().redo();
            	                     }
            	                 }


            	            
            	            
            	            }

            	            
            	            
            	    		return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }
            	    };        	

                    worker.start();

        
        }

    
    }

    public void resetActions() {
       UndoManager undoManager = getUndoManager();
       
                 resetUndoAction(undoManager);
                 resetRedoAction(undoManager);

   }

      	/**
	     * Updates the undo action with information about what it is going to undo
	     * using the mgr getUndoPresentationName() call.
	     * @param mgr the UndoManager to consult about whether an undo is available,
	     * and what it's presentation name is. If this is passed as null, the undo action
	     * is disabled and set to the defaults from the UI resource bundle keys "edit.undo"
	     *
	     */ 
	    protected void resetUndoAction(UndoManager mgr) 
	    {
		if (mgr != null) {
		
		    undoAction.setEnabled(mgr.canUndo());
//			redoAction.setEnabled(mgr.canRedo());
				
			
		    // if there's an undoable get the presentation name and change the undo action
	
		    String undo = xutils.getResourceString("edit.undo");
				
		    String newundo = undo;
				
		    String pname = mgr.getUndoPresentationName();
	//	    logger.debug("undo = "+undo+ " pname = "+pname);
				
		    if (pname != null && !pname.equalsIgnoreCase("undo")) {
				newundo = undo + " " + pname;
		    }
		    undoAction.putValue(Action.SHORT_DESCRIPTION,newundo);
		}
		else {
		    undoAction.putValue(Action.SHORT_DESCRIPTION, xutils.getResourceString("edit.undo.tt"));
		    undoAction.setEnabled(false);
		}							
        }
    
       		/**
		     * Updates the undo action with information about what it is going to undo
		     * using the mgr getUndoPresentationName() call.
		     * @param mgr the UndoManager to consult about whether an undo is available,
		     * and what it's presentation name is. If this is passed as null, the undo action
		     * is disabled and set to the defaults from the UI resource bundle keys "edit.undo"
		     *
		     */ 
		    protected void resetRedoAction(UndoManager mgr) 
		    {
			if (mgr != null) {
				redoAction.setEnabled(mgr.canRedo());
					
			    // if there's an undoable get the presentation name and change the undo action
		
			    String redo = xutils.getResourceString("edit.redo");
					
			    String newredo = redo;
					
			    String pname = mgr.getRedoPresentationName();
		//	    logger.debug("redo = "+redo+ " pname = "+pname);
					
			    if (pname != null && !pname.equalsIgnoreCase("redo")) {
					newredo = redo + " " + pname;
			    }
			    redoAction.putValue(Action.SHORT_DESCRIPTION,newredo);
			}
			else {
			    redoAction.putValue(Action.SHORT_DESCRIPTION, xutils.getResourceString("edit.redo.tt"));
			    redoAction.setEnabled(false);
			}		
	    }

            
	/******************New RedoAction class end************************/
		
		
	
    class OpenAction extends NewAction {

	OpenAction() {
	    super(openAction);
	}

        public void actionPerformed(ActionEvent e) {
	    	        	
       
        		final SwingWorker worker = new SwingWorker() {
        	
        	    	public Object construct() {
        	            //...code that might take a while to execute is here...
        	        	
                                xeditor.doOpen();
                                
        	        	return null;
        	        }
        	    	
        	 	    //Runs on the event-dispatching thread.
                    public void finished() {

                    }

            	    

        	    };
        	    
              worker.start();
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

		//	logger.debug(" New action performed");

			
        		final SwingWorker worker = new SwingWorker() {
                	
        	    	public Object construct() {

        	    		try {

                            System.out.println(" in new file ");
        	    			
        	    		//...code that might take a while to execute is here...
        	    		//	performNewAction(false, null);
      	                DesktopView desktopView = xeditor.getDesktopView();            
      	                desktopView.newFile();
                        
      	                MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();
      	                // OV c 270309
      	                //   if(myf.getCurrentView().equals("GRIDVIEW"))
                 	 //   	xeditor.enableGridMenu(true);

        			} catch(Exception ex) {
        				ex.printStackTrace();
        			}

      	                
      	                return null;
        	        }
        	    	
        	 	    //Runs on the event-dispatching thread.
                    public void finished() {

                    }

            	    

        	    };

                worker.start();
		}
		
		
		
		}

    class NewProjectAction extends AbstractAction {

    	NewProjectAction() {
    	    super(newProjectAction);
    	}

    	NewProjectAction(String nm) {
    	    super(nm);
    	}
     
            public void actionPerformed(ActionEvent e) {

    		//	logger.debug(" New action performed");

    			
            		final SwingWorker worker = new SwingWorker() {
                    	
            	    	public Object construct() {

            	    		try {
        
                                    System.out.println(" in new proj ");

              	                DesktopView desktopView = xeditor.getDesktopView();            

              	                desktopView.addProject();
            	    				
            	    				
            			} catch(Exception ex) {
            				ex.printStackTrace();
            			}

          	                
          	                return null;
            	        }
            	    	
            	 	    //Runs on the event-dispatching thread.
                        public void finished() {

                        }

                	    

            	    };

                    worker.start();
    		}
    		
    		
    		
    		}
    
    
    
	    protected class CloseAction extends AbstractAction 
	    {
			
			public CloseAction () 
			{
				super(closeAction);
	//			XmlUtils.loadActionResources(this,"UI","file.close");
	
			}
			
			public void actionPerformed (ActionEvent evt) 
			{
        		final SwingWorker worker = new SwingWorker() {
                	
        	    	public Object construct() {


                        System.out.println(" in close proj ");

              	        DesktopView desktopView = xeditor.getDesktopView();

                        desktopView.closeFile();


   //     				xeditor.doClose();
      	                return null;
        	        }
        	    	
        	 	    //Runs on the event-dispatching thread.
                    public void finished() {

                    }

            	    

        	    };
                worker.start();
				
			}
	    }


   	    protected class CloseProjectAction extends AbstractAction
	    {

			public CloseProjectAction ()
			{
				super(closeProjAction);
	//			XmlUtils.loadActionResources(this,"UI","file.close");

			}

			public void actionPerformed (ActionEvent evt)
			{
        		final SwingWorker worker = new SwingWorker() {

        	    	public Object construct() {

                        System.out.println(" in close proj ");

              	        DesktopView desktopView = xeditor.getDesktopView();

                        desktopView.closeProject(null, false);

      	                return null;
        	        }

        	 	    //Runs on the event-dispatching thread.
                    public void finished() {

                    }



        	    };
                worker.start();

			}
	    }


/**
     * Really lame implementation of an exit command
     */
    class QuitAction extends AbstractAction {

	QuitAction() {
	    super(quitAction);
	}

        public void actionPerformed (ActionEvent evt) 
		{
    		final SwingWorker worker = new SwingWorker() {
            	
    	    	public Object construct() {

    	        	System.out.println(" in act perf");
    	    		if(xeditor.checkQuit()) {
    	    		    System.exit(0);
    	    		}
  	                return null;
    	        }
    	    	
    	 	    //Runs on the event-dispatching thread.
                public void finished() {

                }

        	    

    	    };
            worker.start();
			
		}

    }
    
    
    /**
     * Trys to write the document as a serialization.
     */
    class SaveAction extends TextAction {

	SaveAction() {
	    super(saveAction);
	}

        public void actionPerformed (final ActionEvent evt) 
		{
    		final SwingWorker worker = new SwingWorker() {
            	
    	    	public Object construct() {

    	            boolean retVal = false;
 	    		   
    	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
    	            
System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());

                	            DesktopView desktopView = xeditor.getDesktopView();

                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {

                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = XmlEditorActions.getSharedInstance();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {

	                	            	  target.doSave(null);

	                	              }

                	              }


                	    		return null;    	        }
    	    	
    	 	    //Runs on the event-dispatching thread.
                public void finished() {

                }

        	    

    	    };
            worker.start();
			
		}

    }


// PREREL A CLASS FR PRINT  
    /**
     * Trys to write the document as a serialization.
     */
    class PrintAction extends TextAction {

	PrintAction() {
	    super(printAction);
	}

        public void actionPerformed (final ActionEvent evt) 
		{
    		final SwingWorker worker = new SwingWorker() {
            	
    	    	public Object construct() {

    	            boolean retVal = false;
 	    		   
    	            System.out.println(" in CA PrintAction actionPerformed evt src " + evt.getSource().getClass().getName());
    	            
					            DesktopView desktopView = xeditor.getDesktopView();

                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {

                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = XmlEditorActions.getSharedInstance();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {
	                	            	
										  try
										  {
												target.print();
	
										  } catch (Exception pe)
										  {
							    	            System.err.println(" cannot print ");

										  }

									  
									  }

                	              }


                	    		return null;    	        }
    	    	
    	 	    //Runs on the event-dispatching thread.
                public void finished() {

                }

        	    

    	    };
            worker.start();
			
		}

    }

// PREREL A CLASS FR PRINT  PREVIEW
    /**
     * Trys to write the document as a serialization.
     */
    class PrintPreviewAction extends TextAction {

	PrintPreviewAction() {
	    super(printPreviewAction);
	}

        public void actionPerformed (final ActionEvent evt) 
		{
    		final SwingWorker worker = new SwingWorker() {
            	
    	    	public Object construct() {

    	            boolean retVal = false;
 	    		   
    	            System.out.println(" in CA PrintAction actionPerformed evt src " + evt.getSource().getClass().getName());
    	            
					            DesktopView desktopView = xeditor.getDesktopView();

                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {

                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = XmlEditorActions.getSharedInstance();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {
	                	            	  target.printPreview();
	                	              }

                	              }


                	    		return null;    	        }
    	    	
    	 	    //Runs on the event-dispatching thread.
                public void finished() {

                }

        	    

    	    };
            worker.start();
			
		}

    }



    /**
     * Trys to write the document as a serialization.
     */
    class SaveAsAction extends TextAction {

	SaveAsAction() {
	    super(saveasAction);
	}

        public void actionPerformed (final ActionEvent evt) 
		{
    		final SwingWorker worker = new SwingWorker() {
            	
    	    	public Object construct() {

    	            boolean retVal = false;
  	    		   
    	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
    	            
                	            DesktopView desktopView = xeditor.getDesktopView();

                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {

                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = new XmlEditorActions();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {

	                	            	  target.doSaveAs(null);

	                	              }

                	              }


                	    		return null;    	        

    	        }
    	    	
    	 	    //Runs on the event-dispatching thread.
                public void finished() {

                }

        	    

    	    };
            worker.start();
			
		}

    }

  protected class WellformedAction extends TextAction {
        
        public WellformedAction() {
            super("well-formed");
            
  //          XmlUtils.loadActionResources(this,"UI","well.formed");
        }
        
            public void actionPerformed(final ActionEvent evt) {

        		//	logger.debug(" New action performed");

                		final SwingWorker worker = new SwingWorker() {
                        	
                	    	public Object construct() {
                	            boolean retVal = false;
                	    		   
                	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
                	            
                	            DesktopView desktopView = xeditor.getDesktopView();            
                	            
                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {
                	                
                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = new XmlEditorActions();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {
	                	                  
	                	            	  target.checkWellFormed();
	                	                  
	                	              }
                	                          
                	              }

                	            
                	    		return null;
                	        }
                	    	
                	 	    //Runs on the event-dispatching thread.
                            public void finished() {

                            }
                	    };        	

                        worker.start();

            }
    }

    protected class ValidityAction extends TextAction {
        
        public ValidityAction() {
            super("valid");
            
//            XmlUtils.loadActionResources(this,"UI","validity");
        }
        
            public void actionPerformed(final ActionEvent evt) {

        		//	logger.debug(" New action performed");

                		final SwingWorker worker = new SwingWorker() {
                        	
                	    	public Object construct() {
                	            boolean retVal = false;
                	    		   
                	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
                	            
                	            DesktopView desktopView = xeditor.getDesktopView();            
                	            
                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {
                	                
                	            	  JTextComponent textComp = getTextComponent(evt);

                	                  XmlEditorActions va = new XmlEditorActions();
                	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {
	                	                  
	                	            	  target.checkValid();
	                	                  
	                	              }
                	                          
                	              }

                	            
                	    		return null;
                	        }
                	    	
                	 	    //Runs on the event-dispatching thread.
                            public void finished() {

                            }
                	    };        	

                        worker.start();

            }
    }
    
    
	 private void doCheckWellFormed(TreeTableXmlOps target) {
		 
		 if(target instanceof TextXmlAction) {
			 
			 xeditor.doCheckWellFormed();
			 
		 } else if(target instanceof GridXmlAction) {
			 
	            System.out.println(" Grid check WF  ");
	             
		 }
		 
	 }

	 private void doCheckValid(TreeTableXmlOps target) {

		    System.out.println(" Grid check do validity ");
		 if(target instanceof TextXmlAction) {
			 
			 xeditor.doCheckValidity();
			 
		 } else if(target instanceof GridXmlAction) {
			 
	            System.out.println(" Grid check validity ");
			 
		 }
		 
	 }
    
	 class WordWrapAction extends AbstractAction {
	        
	        WordWrapAction() {
	            super(wordwrapAction);
	        }
	        	        
	        
	        public void actionPerformed(final ActionEvent evt) {

        		//	logger.debug(" New action performed");

                		final SwingWorker worker = new SwingWorker() {
                        	
                	    	public Object construct() {
                	            boolean retVal = false;
                	    		   
                	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
                	            
                	            DesktopView desktopView = xeditor.getDesktopView();            
                	            
                	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                	            if(myf != null) {
                	                
                	            	   String currentView = myf.getCurrentView();
                	            	   
                	                   if(currentView != null) {
                	                       
                	                       if(currentView.equals("TEXTVIEW")) {
                	                      
	                	           	            AbstractButton b = (AbstractButton)(evt.getSource());
	                	        	        //    if (b instanceof JToggleButton) {
	                	        	        //        JToggleButton jtb = (JToggleButton) b;
	                	        	                xeditor.doWordWrap(b);
	                	        	         //   }

                	                       } 
                	                       
                	                   }        	   
                	                          
                	              }

                	            
                	    		return null;
                	        }
                	    	
                	 	    //Runs on the event-dispatching thread.
                            public void finished() {

                            }
                	    };        	

                        worker.start();

            }
	        
	    }
	    

	 class CDATAAction extends TextAction {
	        
		 CDATAAction() {
	            super(cdataAction);
	            setEnabled(true);

	        }
	        	        
	        
	        public void actionPerformed(final ActionEvent evt) {

     		//	logger.debug(" New action performed");

             		final SwingWorker worker = new SwingWorker() {
                     	
             	    	public Object construct() {
             	            boolean retVal = false;
             	    		   
             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
             	            
             	            DesktopView desktopView = xeditor.getDesktopView();            
             	            
             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

             	            if(myf != null) {
             	                
             	            	   String currentView = myf.getCurrentView();
             	            	   
             	                   if(currentView != null) {
             	                       
             	                       if(currentView.equals("TEXTVIEW")) {
             	                      

                     	            	  JTextComponent textComp = getTextComponent(evt);
                     	            	  Editor editor = (Editor) textComp;
                     	
                     	            	  String selText = editor.getSelectedText();
	                	           	            
                     	            	  StringBuffer sbuf = new StringBuffer();
                     	            	  sbuf.append(" <![CDATA[ ");
                     	            	  sbuf.append(selText);
                     	            	  sbuf.append(" ]]> ");
                     	            	 
                     	            	  editor.replaceSelection(sbuf.toString());
                     	            	  editor.repaint();
                     	            	  editor.revalidate();
             	                       } 
             	                       
             	                   }        	   
             	                          
             	              }

             	            
             	    		return null;
             	        }
             	    	
             	 	    //Runs on the event-dispatching thread.
                         public void finished() {

                         }
             	    };        	

                     worker.start();

         }
	        
	    }


	 class CommentAction extends TextAction {
	        
		 CommentAction() {
	            super(commentAction);
	            setEnabled(true);

	        }
	        	        
	        
	        public void actionPerformed(final ActionEvent evt) {

     		//	logger.debug(" New action performed");

             		final SwingWorker worker = new SwingWorker() {
                     	
             	    	public Object construct() {
             	            boolean retVal = false;
             	    		   
             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
             	            
             	            DesktopView desktopView = xeditor.getDesktopView();            
             	            
             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

             	            if(myf != null) {
             	                
             	            	   String currentView = myf.getCurrentView();
             	            	   
             	                   if(currentView != null) {
             	                       
             	                       if(currentView.equals("TEXTVIEW")) {
             	                      
                      	            	  JTextComponent textComp = getTextComponent(evt);
                     	            	  Editor editor = (Editor) textComp;
                     	
                     	            	  String selText = editor.getSelectedText();
	                	           	            
                     	            	  StringBuffer sbuf = new StringBuffer();
                     	            	  sbuf.append(" <!-- ");
                     	            	  sbuf.append(selText);
                     	            	  sbuf.append(" --> ");
                     	            	 
                     	            	  editor.replaceSelection(sbuf.toString());

                     	            	  editor.repaint();
                     	            	  editor.revalidate();
	                	           	            
             	                       } 
             	                       
             	                   }        	   
             	                          
             	              }

             	            
             	    		return null;
             	        }
             	    	
             	 	    //Runs on the event-dispatching thread.
                         public void finished() {

                         }
             	    };        	

                     worker.start();

         }
	        
	    }


	 class GridopsAction extends TextAction {
	        
		 GridopsAction() {
	            super(gridopsAction);
	        }
	        	        
	        
	        public void actionPerformed(final ActionEvent evt) {

     		//	logger.debug(" New action performed");

             		final SwingWorker worker = new SwingWorker() {
                     	
             	    	public Object construct() {
             	            boolean retVal = false;
             	    		   
             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
             	            
             	            JMenuItem jmi = (JMenuItem) evt.getSource();
             	            
             	            DesktopView desktopView = xeditor.getDesktopView();            
             	            
             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

             	            if(myf != null) {

          	            	  JTextComponent textComp = getTextComponent(evt);

        	                  XmlEditorActions va = new XmlEditorActions();
        	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

            	              if (target != null) {

            	            	  if(jmi.getActionCommand().equals("gridops-inselement"))
		            	              target.insertNode("Element");
            	            	  else if(jmi.getActionCommand().equals("gridops-insattr"))
		            	       		  target.insertNode("Attr");
            	            	  else if(jmi.getActionCommand().equals("gridops-instext"))
		            	       		  target.insertNode("Text");            	            	  
            	            	  else if(jmi.getActionCommand().equals("gridops-delelement"))
		            	       		  target.deleteNode("Element");
		            	          else if(jmi.getActionCommand().equals("gridops-delattr"))
		            	       		  target.deleteNode("Attr");
		            	      	  else if(jmi.getActionCommand().equals("gridops-deltext"))
		            	       		  target.deleteNode("Text");
            	            	  else if(jmi.getActionCommand().equals("gridops-rename"))
            	            		  target.renameNode();
            	            	  else if(jmi.getActionCommand().equals("gridops-expand"))
            	            		  target.expandNode();
            	            	  else if(jmi.getActionCommand().equals("gridops-expandall")
            	            			||  jmi.getActionCommand().equals("gridops-collapseall") ) {
            	            		  
            	            		  String actType = jmi.getActionCommand();
            	            		  target.expandAll(actType);
            	            		  
            	            		  String actCmd = jmi.getActionCommand().equals("gridops-expandall")? "gridops-collapseall"
            	            				  : "gridops-expandall";
            	            		  
            	            		  jmi.setActionCommand(actCmd);
            	            		  
            	            	  }
            	            	  
            	             }
             	            	
             	            }             	            
             	    		return null;
             	        }
             	    	
             	 	    //Runs on the event-dispatching thread.
                         public void finished() {

                         }
             	    };        	

                     worker.start();

         }
	        
	    }


	 public class FormatAction extends TextAction {
		 
		 	private static final boolean DEBUG = false;
			
			private XmlEditor editor = null;
			private String search = null;
			private boolean matchCase = false; 
			private boolean down = false; 
			private boolean matchWord = false; 
			private boolean doReplace = false; 
			private String replaceText = null; 

		 	/**
			 * The constructor for the action which allows for searching 
			 * the Xml Editor content.
			 *
			 * @param editor the XML Editor
			 */
		 	public FormatAction( XmlEditor editor) {
				super("format");
				
				if (DEBUG) System.out.println( "FormatAction( "+editor+")");
				
				putValue( MNEMONIC_KEY, new Integer( 'f'));
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0, false));
			//	putValue( SMALL_ICON, new ImageIcon( getClass().getResource( "/org/xngr/browser/icons/FindAgain16.gif")));
				putValue( SHORT_DESCRIPTION, "Format");

				this.editor = editor;
			//	setEnabled(false);

		 	}
		 	
			/**
			 * The implementation of the find next action, called 
			 * after a user action.
			 *
			 * @param event the action event.
			 */
	        public void actionPerformed(final ActionEvent evt) {

	     		//	logger.debug(" New action performed");

	             		final SwingWorker worker = new SwingWorker() {
	                     	
	             	    	public Object construct() {
	             	            boolean retVal = false;
	             	    		   
	             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
	             	            
	             	            DesktopView desktopView = xeditor.getDesktopView();            
	             	            
	             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

	             	            if(myf != null) {

	              	                  	if(myf.getCurrentView().equals("TEXTVIEW")) {

	              	                  		 System.out.println( "FormatAction.actionPerformed");

	                    	            	  Editor editPane = (Editor) getTextComponent(evt);

	              	                          Dimension d1 = m_fontDialog.getSize();
	              	                          Dimension d2 = XmlEditorActions.this.xeditor.getSize();
	              	                          int x = Math.max((d2.width-d1.width)/2, 0);
	              	                          int y = Math.max((d2.height-d1.height)/2, 0);
	              	                          m_fontDialog.setBounds(x + XmlEditorActions.this.xeditor.getX(),
	              	                            y + XmlEditorActions.this.xeditor.getY(), d1.width, d1.height);

	              	                          m_fontDialog.show();
	              	                          if (m_fontDialog.getOption()==JOptionPane.OK_OPTION) {
	              	                        	  editPane.setFont(m_fontDialog.getFont());
	              	                          }
	              	                      
	              	                  	}	
		                	       
          	            	}
	             	    		return null;
	             	        }
	             	    	
	             	 	    //Runs on the event-dispatching thread.
	                         public void finished() {

	                         }
	             	    };        	

	                     worker.start();

	         }

		 	
		}



	 public class PrettyAction extends TextAction {
		 
		 	private static final boolean DEBUG = false;
			
			private XmlEditor editor = null;
			private String search = null;
			private boolean matchCase = false; 
			private boolean down = false; 
			private boolean matchWord = false; 
			private boolean doReplace = false; 
			private String replaceText = null; 

		 	/**
			 * The constructor for the action which allows for searching 
			 * the Xml Editor content.
			 *
			 * @param editor the XML Editor
			 */
		 	public PrettyAction( XmlEditor editor) {
				super( "pretty-print");
				
				if (DEBUG) System.out.println( "PrettyAction( "+editor+")");
				
				putValue( MNEMONIC_KEY, new Integer( 'f'));
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0, false));
			//	putValue( SMALL_ICON, new ImageIcon( getClass().getResource( "/org/xngr/browser/icons/FindAgain16.gif")));
				putValue( SHORT_DESCRIPTION, "Format");

				this.editor = editor;
				setEnabled(true);

		 	}
		 	
			/**
			 * The implementation of the find next action, called 
			 * after a user action.
			 *
			 * @param event the action event.
			 */
	        public void actionPerformed(final ActionEvent evt) {

	     		//	logger.debug(" New action performed");

	             		final SwingWorker worker = new SwingWorker() {
	                     	
	             	    	public Object construct() {
	             	            boolean retVal = false;
	             	    		   
	             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
	             	            
	             	            DesktopView desktopView = xeditor.getDesktopView();            
	             	            
	             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

	             	            if(myf != null) {

	              	                  	if(myf.getCurrentView().equals("TEXTVIEW")) {

	              	                  		 System.out.println( "PrettyAction.actionPerformed");

	              	                  		 xeditor.doPrettyPrint();		
	              	                      
	              	                  	}	
		                	       
       	            	}
	             	    		return null;
	             	        }
	             	    	
	             	 	    //Runs on the event-dispatching thread.
	                         public void finished() {

	                         }
	             	    };        	

	                     worker.start();

	         }

		 	
		}

	 
	 
	 public class FindAction extends TextAction {
		 	private static final boolean DEBUG = false;
			
			private FindDialog dialog = null;
			private XmlEditor editor = null;
			
		 	/**
			 * The constructor for the action which allows for searching 
			 * the Xml Editor content.
			 *
			 * @param editor the XML Editor
			 */
		 	public FindAction(XmlEditor editor) {
				super( "Find");
				
				if (DEBUG) System.out.println( "FindAction( "+editor+")");
				
				putValue( MNEMONIC_KEY, new Integer( 'F'));
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK, false));
//				putValue( SMALL_ICON, new ImageIcon( getClass().getResource( "resources/find.gif")));
				putValue( SHORT_DESCRIPTION, "Find...");

				this.editor = editor;
		 	}
		 	
			/**
			 * The implementation of the search action, called 
			 * after a user action.
			 *
			 * @param event the action event.
			 */
	        public void actionPerformed(final ActionEvent evt) {

	     		//	logger.debug(" New action performed");

	             		final SwingWorker worker = new SwingWorker() {
	                     	
	             	    	public Object construct() {
	             	            boolean retVal = false;
	             	    		   
	             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
	             	            
	             	            DesktopView desktopView = xeditor.getDesktopView();            
	             	            
	             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

	             	            if(myf != null) {

                	                
              	            	  JTextComponent textComp = getTextComponent(evt);

              	                  XmlEditorActions va = new XmlEditorActions();
              	                  TreeTableXmlOps target = va.getTargetComponentForXml(myf, textComp);

	                	              if (target != null) {	                	                  
	                	            	  target.find();
	                	              }
		             	          	             	                       
           	                   }        	   
	             	                          
	             	    		return null;
	             	        }
	             	    	
	             	 	    //Runs on the event-dispatching thread.
	                         public void finished() {

	                         }
	             	    };        	

	                     worker.start();

	         }

			
		}

	 public class FindNextAction extends TextAction {
		 	private static final boolean DEBUG = false;
			
			private XmlEditor editor = null;
			private String search = null;
			private boolean matchCase = false; 
			private boolean down = false; 
			private boolean matchWord = false; 
			private boolean doReplace = false; 
			private String replaceText = null; 

		 	/**
			 * The constructor for the action which allows for searching 
			 * the Xml Editor content.
			 *
			 * @param editor the XML Editor
			 */
		 	public FindNextAction( XmlEditor editor) {
				super(findNextAction);
				
				if (DEBUG) System.out.println( "FindNextAction( "+editor+")");
				
				putValue( MNEMONIC_KEY, new Integer( 'N'));
				putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0, false));
			//	putValue( SMALL_ICON, new ImageIcon( getClass().getResource( "/org/xngr/browser/icons/FindAgain16.gif")));
				putValue( SHORT_DESCRIPTION, "Find Next");

				this.editor = editor;
				setEnabled( false);
		 	}
		 	
			/**
			 * Sets the previous search values. 
			 *
			 * @param search the previous search string.
			 * @param matchCase the previous matchCase value.
			 * @param down the previous direction value.
			 */
			public void setValues( String search, boolean matchCase, boolean down, boolean mword, boolean replace, String rtext) {
				if ( search != null && (search.length() > 0)) {
					setEnabled( true);
				}
				
				this.search = search;
				this.matchCase = matchCase;
				this.down = down;
				this.matchWord = mword;
				this.doReplace = replace;
				this.replaceText = rtext;
				
			}

			/**
			 * The implementation of the find next action, called 
			 * after a user action.
			 *
			 * @param event the action event.
			 */
	        public void actionPerformed(final ActionEvent evt) {

	     		//	logger.debug(" New action performed");

	             		final SwingWorker worker = new SwingWorker() {
	                     	
	             	    	public Object construct() {
	             	            boolean retVal = false;
	             	    		   
	             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());
	             	            
	             	            DesktopView desktopView = xeditor.getDesktopView();            
	             	            
	             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

	             	            if(myf != null) {

	              	                  	if(myf.getCurrentView().equals("TEXTVIEW")) {
	              	                  		System.out.println( "FindNextAction.actionPerformed");
	              	                  		xeditor.search( search, matchCase, down, matchWord, doReplace, replaceText, false);
	              	                  	}	
		                	       
             	            	}
	             	    		return null;
	             	        }
	             	    	
	             	 	    //Runs on the event-dispatching thread.
	                         public void finished() {

	                         }
	             	    };        	

	                     worker.start();

	         }

		 	
		}

	 public void  doCheckWellFormed() {
		   xeditor.doCheckWellFormed();
	 }    
	
	 public void  doCheckValidity() {
		   xeditor.doCheckValidity();
	 }    
	
	public void doSave() {
		   xeditor.doSave();
	}
	
	public void doSaveAs(File f){
		   xeditor.doSaveAs();
	}	 

	public void find(){
		if ( dialog == null) {
			
			if(xeditor == null) {			
				xeditor = XmlEditor.getInstance("");
			}
			
 			dialog = new FindDialog(xeditor);
 		}

		dialog.init();
		dialog.show();

	}	 

// PREREL A FR PRINT 4 LNS  
	public boolean print()  throws java.awt.print.PrinterException  {			
		xeditor.doPrint();
		return true;
	}	 

	public void setTargetComponent(MyInternalFrame myf) {
		this.myf = myf;
	}

		 
	public Action [] getDefaultActions() {
	    return defaultActions;
	}

    public void processXQuery(ActionEvent e) {
         System.out.println( " in processXQuery ");

         final DesktopView desktopView = xeditor.getDesktopView();


                 		final SwingWorker worker = new SwingWorker() {

	             	    	public Object construct() {
	             	            boolean retVal = false;
                	    		String retStatus = "SUCCESS";

                                boolean queryFileSaved = true;
                                boolean xmlFileSaved = true;

                                try {
	             	            System.out.println(" in CA actionPerformed xquery ");


	             	            MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                                if(desktopView.selectedHasFileName() == false)  {
                                    queryFileSaved = false;
                                }

                                if(!desktopView.isStyleAssigned()) 
                                    return "UNASSIGNED";


	             	            if(myf != null) {

	              	                 if(myf.getCurrentView().equals("TEXTVIEW")) {

  //                                        if(xqueryProcessor == null)
  //                                            xqueryProcessor = new xqueryProcessor();
        
                                            MyInternalFrame myxmlf = xeditor.getXmlFileFrame();

                              //              if(desktopView.selectedHasFileName() == false)  {
                              //                 return "UNSAVED";
                              //              }

                                            if(myxmlf.getFile().getName().contains("Untitled")) {
                                                xmlFileSaved = false;
                            	                JOptionPane.showMessageDialog(null, ExaltoResource.getString(ColWidthTypes.ERR,"xmlfile.not.saved"));
                                            }

                                         File xmlFile = desktopView.getSelectedFile();

                                         File queryFile = desktopView.getXslFile();
                                          if(xqueryProcessor == null)
                                             xqueryProcessor = new XQueryProcessor();

                                            if(!queryFileSaved) {

                                                  BufferedReader br = new BufferedReader(new FileReader(queryFile));

                                                  String line = null;
                                                  StringBuffer sbuf = new StringBuffer();
                                                  while ((line = br.readLine()) != null)
                                                    sbuf.append(line);

                                                
                                      
                                                xqueryProcessor.processXQuery(sbuf.toString(), xmlFile.getAbsolutePath());
                                                
                                            } else {
                                                xqueryProcessor.processXQueryFromFile(queryFile, xmlFile);
                                            }

                                            
                                     }

             	            	}

                                }
                                catch(Exception e) {
                                    e.printStackTrace();
                                    retStatus = "ERROR";
                                }

	             	    		return retStatus;
	             	        }

	             	 	    //Runs on the event-dispatching thread.
	                         public void finished() {

                       try {
                        	String ret = (String) this.get();

                        	if(ret != null) {

                        		if(ret.equals("UNASSIGNED")) {

                        			MyInternalFrame myf = (MyInternalFrame) desktopView.getSelectedFrame();

                    	            if(myf == null)
                    	                return;

                	                JOptionPane.showMessageDialog(null,
                	                        ExaltoResource.getString(ColWidthTypes.ERR,"stylesheet.not.assign.detail"));
                	                xeditor.waitingToAssign = true;
                	                //	fileToFrameMap.put(myf.getFile().getAbsolutePath(), myf);

                	                xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"stylesheet.not.assign"),0, ColWidthTypes.ERROR));
            	            		return;

                        		}
                                else if(ret.equals("ERROR")) {
                                    xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"stylesheet.not.assign"),0, ColWidthTypes.ERROR));
            	            		return;                                    
                                }
                                else {

                        				desktopView.display(new File(xqueryProcessor.getOutFile()));

                                        try {
                                            Thread.sleep(2000);
                                        } catch (Exception e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }

                                  /*
                        				MyInternalFrame mf = (MyInternalFrame) desktopView.getSelectedFrame();

                        				mf.addInternalFrameListener(xeditor);

                        				mf.setNew(true);

			    						Editor editor = (Editor) mf.getTextComponent();
			    						editor.addCaretListener( xeditor);
                                   */
                        				xeditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"xquery.execution.complete"),0, ColWidthTypes.NOERROR));

	    							}

                        	}

                        } catch (MissingResourceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}



	                         }
	             	    };

	                     worker.start();


    }

    public void convertToXML(ActionEvent e) {
         System.out.println( " in convertToXML ");

    }

    public void convertToCSV(ActionEvent e) {
        System.out.println( " in convertToCSV ");
    }

}
