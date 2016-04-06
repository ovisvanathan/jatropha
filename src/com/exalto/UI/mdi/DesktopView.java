package com.exalto.UI.mdi;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.tree.*;
import javax.swing.undo.*;

import com.exalto.util.XmlUtils;
import com.exalto.ColWidthTypes;
import com.exalto.UI.ExaltoExplorer;
import com.exalto.UI.XmlEditor;
import com.exalto.UI.XTree;
import com.exalto.UI.XmlEditorActions;

import com.exalto.UI.mdi.editor.XmlEditorKit;
import com.exalto.UI.util.ExplorerNode;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



public class DesktopView extends MDIDesktopPane implements ErrorListener, 
	InternalFrameListener, ColWidthTypes, MouseListener, PropertyChangeListener {
	
	private Font theFont = new Font("MS Sans Serif", Font.PLAIN, 12);
	private XmlUtils xutils;
	private XmlEditor xmlEditor;
        private ExaltoExplorer explorer;
        
        
//	private Project project;
   
	public DesktopView() {
	
	//	xutils = new XmlUtils();
		xutils = XmlUtils.getInstance();
		
	//	putClientProperty("JDesktopPane.dragMode", "faster");
	//    setBackground(xutils.getResourceString(colWidthTypes.BACKGROUND_COLOR));
    
//		xmlEditor = xeditor;
	
	}

//	public void setModel(Project project) {
//		this.project = project;
//	}


	public void cut(ActionEvent e) {
		if (getSelectedFrame() instanceof MyInternalFrame) {
			((MyInternalFrame) getSelectedFrame()).cut(e);
                        
                XmlEditorActions _actions = XmlEditorActions.getSharedInstance();
                _actions.clipBoardHasContents = true;
       
                        
		}
	}
	public void copy(ActionEvent e) {
		if (getSelectedFrame() instanceof MyInternalFrame) {
			((MyInternalFrame) getSelectedFrame()).copy(e);
		}
                XmlEditorActions _actions = XmlEditorActions.getSharedInstance();
                _actions.clipBoardHasContents = true;

        }

        public void paste(ActionEvent e) {
		if (getSelectedFrame() instanceof MyInternalFrame) {
			((MyInternalFrame) getSelectedFrame()).paste(e);
		}
	}
	
   /**
     * Delegates to undo method in the current foreground XMLEditorDoc
     * @see XMLEditorDoc#undo
     */
    public void undo(ActionEvent evt) 
    {
	
		System.out.println("Frame undo");
	
		if (getSelectedFrame() instanceof MyInternalFrame) {
			((MyInternalFrame) getSelectedFrame()).undo(evt);
		}
		else {
			System.out.println("No listener available for Undo.");
		}
	}
	
	
	/**
	     * Delegates to undo method in the current foreground XMLEditorDoc
	     * @see XMLEditorDoc#undo
	     */
	    public void redo(ActionEvent evt) 
	    {
		
			System.out.println("Frame redo");
		
			if (getSelectedFrame() instanceof MyInternalFrame) {
				((MyInternalFrame) getSelectedFrame()).redo(evt);
			}
			else {
				System.out.println("No listener available for Redo.");
			}
	}
	
	
	
	public void updateView() {
//		Vector v = project.getOpenFiles();
//		for (int i = 0; i < v.size(); i++) {
//			display((File) v.get(i));
//		}
	}
	
	public boolean selectedHasFileName() {
		if (((MyInternalFrame) getSelectedFrame()).getFile() != null) {
			return true;
		}
		return false;
	}
	public boolean fileSelected() {
		if (getSelectedFrame() instanceof MyInternalFrame) {
			return true;
		}
		return false;
	}
	public void saveSelectedFile() {
		((MyInternalFrame) getSelectedFrame()).save();
	}
	public void saveSelectedFileAs(File file) {
		((MyInternalFrame) getSelectedFrame()).saveAs(file);
	}


	public void setFont(Font f) {
		theFont = f;
		JInternalFrame j[] = getAllFramesInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		for (int i = 0; i < getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()); i++) {
			((MyInternalFrame) j[i]).setFont(theFont);
		}
	}

	
	public void newFile() throws Exception {
		newWindow(null);
	}
	
	public void newFile(File f, String s) throws Exception {
		try {
			PrintStream oos = new PrintStream(new FileOutputStream(f), true);
			oos.print(s);
			oos.flush();
			oos.close();
		} catch (IOException ioe) {
			System.out.println("MyDesktopPane write - IOException");
		}
		display(f);
	}

	public void display(File f) throws Exception {
		if (f == null) {
			open(f);
		}

             if(xmlEditor == null)
                xmlEditor = XmlEditor.getInstance("");

                /*
                if(explorer == null)
                    explorer = xmlEditor.getExplorer();
                
                explorer.addFile(f.getAbsolutePath());
                */
                
//		System.out.println("MyIntf display f not null ");
		JInternalFrame j[] = getAllFramesInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		boolean found = false;
		
		System.out.println("getComponentCountInLayer =" + getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()));
		
		System.out.println(" arr size =" + j.length);
		
		
		for (int i = 0; i < getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()); i++) {
			if (j[i].getTitle().equals(f.getName())) {
				if(f.getName().startsWith("XSL")) {
					System.out.println("###########Filename begins with xsl####### ");
						this.remove(i);
						repaint();
						revalidate();
				} else {
					moveToFront(j[i]);
					found = true;
					((MyInternalFrame) j[i]).select();
				}
			}
		}
		if (!found) {
			open(f);
		}
                
                
	}
	
	public void open(File f) throws Exception {
		newWindow(f);
	}

	private void newWindow(File f) throws Exception {

//		System.out.println("MyIntf newwin ");
		// Ensure the internal frame is smaller than the desktop
		Dimension desktopsize = getSize();
		
		xmlEditor = XmlEditor.getInstance("");

		int newwidth = (int)((double)desktopsize.getWidth() -100);
		int newheight = (int)((double)desktopsize.getHeight() - 50); 

		String defaultView = xmlEditor.getDefaultViewForNewFiles();
		
	
	// sanity check
		if (newwidth < 0) { 
			newwidth = 100;
		}
		if (newheight < 0) {
			newheight = 100;
		}
		
		
		MyInternalFrame m = new MyInternalFrame(f, newwidth, newheight, defaultView);
	
		System.out.println("MyIntf newwidtth " + m.getSize().width);
		System.out.println("MyIntf newheight " + m.getSize().height);

					
	//	m.addInternalFrameListener(this);
			
		Editor editor = (Editor) m.getTextComponent();
		editor.addCaretListener(xmlEditor);			
		
		m.addInternalFrameListener(xmlEditor);
		
		((JTextComponent)m.getTextComponent()).getDocument().addUndoableEditListener(
						xmlEditor.new MyUndoableEditListener());
	
			
                
                ExaltoExplorer explorer = xmlEditor.getExplorer();
                
                int ret = explorer.addFile(m.getTitle(), null);
                
                if(ret == -1) {
                    return;
                }
                    
                
                try {
				Icon icon = xutils.getFrameIcon();
				if (icon != null) {
					m.setFrameIcon(icon);
				}
			}
			catch (Throwable t) {
				throw new Exception(t);
			}
			
	
	 	add(m);
	//	m.setLocation((getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()) - 1) * 30, (getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()) - 1) * 30);
		moveToFront(m);
		m.select();
	}


	
	public void open(ErrorEvent ee)  {
		try {
			display(ee.getFile());
				((MyInternalFrame) getSelectedFrame()).setCaretPosition(ee.getLineNum() + ee.getCol());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
	}
	public void saveAll() {
		Component j[];
		j = getComponentsInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		int c = getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		for (int i = 0; i < c; i++) {
			if (j[i] instanceof MyInternalFrame) {
				((MyInternalFrame) j[i]).save();
			}
		}
		invalidate();
	}

	public void closeAll() {
		Component j[];
		j = getComponentsInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		int c = getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		for (int i = 0; i < c; i++) {
			if (j[i] instanceof MyInternalFrame) {
				((MyInternalFrame) j[i]).dispose();
				remove(j[i]);
				j[i] = null;
			}
		}
	}
	
	
	public Dimension getPreferredSize() {
//		System.out.println(" inside preferredsize");
		return new Dimension(600, 400);
	}
	public void internalFrameActivated(InternalFrameEvent we) {
	}
	public void internalFrameClosed(InternalFrameEvent ife) {
//		project.closeFile(((MyInternalFrame) ife.getSource()).getFile());
	}
	public void internalFrameClosing(InternalFrameEvent we) {
	}
	public void internalFrameIconified(InternalFrameEvent we) {
	}
	public void internalFrameDeiconified(InternalFrameEvent we) {
	}
	public void internalFrameOpened(InternalFrameEvent we) {
	}
	public void internalFrameDeactivated(InternalFrameEvent we) {
	}
	
	public boolean isStyleAssigned() {
			return (((MyInternalFrame) getSelectedFrame()).getXslFile() != null);
	}
		
	public File getSelectedFile() {
	
		if(selectedHasFileName() == false) {
			return null;
		}
		
		return ((MyInternalFrame) getSelectedFrame()).getFile();
	
	}
	
		
		public File getXslFile() {
			return ((MyInternalFrame) getSelectedFrame()).getXslFile();
		}
		
		public void setStyleSheet(String file, MyInternalFrame frame) {
			if(frame == null) 
				((MyInternalFrame)getSelectedFrame()).setStyleSheet(file);
			else 
				frame.setStyleSheet(file);
		}
		
		
	public UndoManager getCurrentUndoManager() {
		return ((MyInternalFrame)getSelectedFrame()).getUndoManager();
	}
	
	public boolean selectedIsNew() {
		return ((MyInternalFrame) getSelectedFrame()).isNew();
	}
	
	
		public void mouseExited(MouseEvent me) {
	
		}
	
		public void mousePressed(MouseEvent evt) {
		 System.out.println(" mouse preseed");
			 if (evt.isPopupTrigger()) {
				 xmlEditor.getPopupMenu().show(evt.getComponent(), evt.getX(),
				   evt.getY());
			 }
		}
		
		public void mouseReleased(MouseEvent evt) {
		// ov added 13/11/2004
		       if (evt.isPopupTrigger()) {
		             xmlEditor.getPopupMenu().show(evt.getComponent(), evt.getX(),
		               evt.getY());
		         }		
		}

		public void mouseClicked(MouseEvent me) {
		}
	
		public void mouseEntered(MouseEvent me) {
		}

		public boolean isDtdAssigned() {
			return (((MyInternalFrame) getSelectedFrame()).getDtdFile() != null);
		}
		
		public boolean isSchemaAssigned() {
					return (((MyInternalFrame) getSelectedFrame()).getSchemaFile() != null);
		}
	
		public void assignDTD(String dtdFile, String rootElem) {
		
		try {
		
			MyInternalFrame mf = (MyInternalFrame) getSelectedFrame();
			Editor editor = (Editor) mf.getTextComponent();
			Document doc = editor.getDocument();
						
			String docText = doc.getText(0, doc.getLength());

            int xmldecl = 0;

            System.out.println(" docText bef " + docText.substring(0, xmldecl+100));

			boolean dtdFound = false;
	        String lines[] = docText.split("\n");
	 		StringBuffer result = new StringBuffer();
			for( int j=0; j<lines.length; j++ ){
				int k = lines[j].indexOf("DOCTYPE");
				int r = lines[j].indexOf("<?xml");
				
				if(r > -1) {

                    int ct = lines[j].indexOf("?>");

                    if(ct > 0)
    					xmldecl = ct+2;

                }
				if( k > -1 ){
					dtdFound = true;
					lines[j] = lines[j].substring( 0, lines[j].lastIndexOf(" ")+1) 
					+ "\"" + dtdFile + "\"";
				}
				result.append(lines[j]+"\n");
			}
			
			if(!dtdFound) {
				String docType = "<!DOCTYPE " + rootElem + " SYSTEM \"" + dtdFile + "\">";
				result.insert(xmldecl, docType);
			}

                System.out.println(" docText res " + result.toString());

   //     	editor.read(new StringReader(result.toString()), null);

            Font f = ((XmlEditorKit)editor.getEditorKit()).getFont();
			editor.setText(result.toString());
    		editor.setFont(f);

       		doc = editor.getDocument();

			docText = doc.getText(0, doc.getLength());

            System.out.println(" docText aft " + docText.substring(0, xmldecl+100));

		} catch(Exception e) {
			System.out.println(" err assignDTD " + e.getMessage());
		}
			
		}
		
		public void assignSchema(String schemaFile)  {
		
			XTree xTree = null;
			Editor editor = null;
			File xmlFile = null;
		try {
		
			MyInternalFrame mf = (MyInternalFrame) getSelectedFrame();
			editor = (Editor) mf.getTextComponent();
			Document doc = editor.getDocument();
			String text = null;
			if(selectedIsNew())  {
				text = ((MyInternalFrame)getSelectedFrame()).getTextComponent().getText();
			}

			// Next, create the XTree
			xTree = new XTree();
			xTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );
			xTree.setShowsRootHandles( true );
			// A more advanced version of this tool would allow the JTree to be editable
			xTree.setEditable( false );
			
			if(text != null) {		
				System.out.println(" inside if no filename");	
				xTree.refresh(text);
			} else { 	
				System.out.println(" inside else filename");
				xmlFile = getSelectedFile();
				System.out.println(" filename = " + xmlFile);							
				xTree.refresh(xmlFile);
			}
				
		    org.w3c.dom.Element rootElem = xTree.getRootElement();
			rootElem.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			rootElem.setAttribute("xsi:noNamespaceSchemaLocation", schemaFile);
		
			System.out.println(" got root = " + rootElem.getNodeName());							
			
			//create a new DOMSource using the root node of an existing DOM tree
		    Reader xmlInput =
		        new FileReader(xmlFile);

			StringWriter swrite = new StringWriter(1024);
 		   // Source xmlSource = new StreamSource(rootElem);
    		DOMSource dsource = new  DOMSource(rootElem);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.transform(dsource, new StreamResult(swrite));

			System.out.println(" after transform ");							
		
			// Open output as input
			Reader reader =
			      new StringReader(swrite.toString());
			BufferedReader bufferedReader =
			       new BufferedReader(reader);

			System.out.println(" before read ");							
			editor.read(bufferedReader, null);
			System.out.println(" after read ");							
			
			repaint();
			revalidate();
			
		} catch(javax.xml.parsers.ParserConfigurationException pcfge) {
		//	logger.warn("Could not parse xml file xmlFile " + pcfge.getMessage());
		//	throw pcfge;
			pcfge.printStackTrace();
		} catch(Exception e) {
	//		throw e;
			e.printStackTrace();
		}
	
			
			
		}
		
		public void addProject() {
			
		
		}

   		public boolean closeProject(ExplorerNode e, boolean isDelete) {

            return true;
		}

         public void closeFile() {
         }

         public boolean closeAllProjects() {
             return false;
         }

   /*
        JInternalFrame getNextFrame(JInternalFrame f) {

            System.out.println(" in GNF  dvw ");

            return null;
    }

        private JInternalFrame getNextFrame(JInternalFrame f, boolean forward) {

                    System.out.println(" in GNF  dvw 2");

            return null;
        }
        */


    public void propertyChange(PropertyChangeEvent pe) {

     
    }

public void addPopup(JPopupMenu menu) {
}

}



