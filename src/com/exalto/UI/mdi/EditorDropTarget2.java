package com.exalto.UI.mdi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.net.*;
import java.util.List;
import javax.swing.text.JTextComponent;

import com.exalto.UI.XmlEditor;
import com.exalto.util.XmlUtils;

public class EditorDropTarget2 implements DropTargetListener {

	//OV c 200309
	private MyInternalFrame myframe = null;
	
	MultiViewInternalFrame myFrame;
	
	private JFrame jframe = null;
	private String catalogFile = null;

//	public EditorDropTarget2(JEditorPane pane) {
	public EditorDropTarget2(MyInternalFrame myFrame) {
	
		this.myframe = myFrame;
	
		this.pane = (Editor) myFrame.getTextComponent();

//		this.pane = pane;

		// Create the DropTarget and register 
		// it with the JEditorPane.
		dropTarget = new DropTarget(pane,
									DnDConstants.ACTION_COPY_OR_MOVE, 
									this, true, null);
	}

	public EditorDropTarget2(MultiViewInternalFrame myFrame) {
		
		this.myFrame = myFrame;
	
		this.pane = (Editor) myFrame.getTextComponent();

//		this.pane = pane;

		// Create the DropTarget and register 
		// it with the JEditorPane.
		dropTarget = new DropTarget(pane,
									DnDConstants.ACTION_COPY_OR_MOVE, 
									this, true, null);
	}

	public EditorDropTarget2(JFrame jframe) {
	
		this.jframe = jframe;
		
		// Create the DropTarget and register 
		// it with the JEditorPane.
		dropTarget = new DropTarget(jframe,
									DnDConstants.ACTION_COPY_OR_MOVE, 
									this, true, null);
		XmlUtils xutils = XmlUtils.getInstance();
	
	}
	
	

	// Implementation of the DropTargetListener interface
	public void dragEnter(DropTargetDragEvent dtde) {
		System.out.println("dragEnter, drop action = " 
						+ showActions(dtde.getDropAction()));
	
		// Get the type of object being transferred and determine
		// whether it is appropriate.
		checkTransferType(dtde);

		// Accept or reject the drag.
		acceptOrRejectDrag(dtde);
	}

	public void dragExit(DropTargetEvent dte) {
		System.out.println("DropTarget dragExit");
	}

	public void dragOver(DropTargetDragEvent dtde) {
		System.out.println("DropTarget dragOver, drop action = "
						+ showActions(dtde.getDropAction()));

		// Accept or reject the drag
		acceptOrRejectDrag(dtde);
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
		System.out.println("DropTarget dropActionChanged, drop action = "
						+ showActions(dtde.getDropAction()));

		// Accept or reject the drag
		acceptOrRejectDrag(dtde);		
	}

	public void drop(DropTargetDropEvent dtde) {
		System.out.println("DropTarget drop, drop action = "
						+ showActions(dtde.getDropAction()));
						
					
		// Check the drop action
		if ((dtde.getDropAction() & DnDConstants.ACTION_COPY_OR_MOVE) != 0) {
			// Accept the drop and get the transfer data
			dtde.acceptDrop(dtde.getDropAction());
			Transferable transferable = dtde.getTransferable();
			
			try {
				boolean result = false;

				if (draggingFile) {
					result = dropFile(transferable);
				} 
				else {
					System.out.println(" Inside drop calling dropcontent ");			
					result = dropContent(transferable, dtde);
				}
				
				dtde.dropComplete(result);
				System.out.println("Drop completed, success: " + result);
			} catch (Exception e) {
				System.out.println("Exception while handling drop " + e);
				dtde.dropComplete(false);
			}
		} else {
			System.out.println("Drop target rejected drop");
			dtde.rejectDrop();
		}
	}

	// Internal methods start here

	protected boolean acceptOrRejectDrag(DropTargetDragEvent dtde) {
		int dropAction = dtde.getDropAction();
		int sourceActions = dtde.getSourceActions();
		boolean acceptedDrag = false;

		System.out.println("\tSource actions are " + 
							showActions(sourceActions) + 
							", drop action is " + 
							showActions(dropAction));
		
		// Reject if the object being transferred 
		// or the operations available are not acceptable
		if (!acceptableType ||
			(sourceActions & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
			System.out.println("Drop target rejecting drag");			
			dtde.rejectDrag();
		} else if (!draggingFile && !pane.isEditable()) {
			// Can't drag text to a read-only JEditorPane
			System.out.println("Drop target rejecting drag");			
			dtde.rejectDrag();
		} else if ((dropAction & DnDConstants.ACTION_COPY_OR_MOVE) == 0) {
			// Not offering copy or move - suggest a copy
			System.out.println("Drop target offering COPY");
			dtde.acceptDrag(DnDConstants.ACTION_COPY);
			acceptedDrag = true;
		} else {
			// Offering an acceptable operation: accept
			System.out.println("Drop target accepting drag");
			dtde.acceptDrag(dropAction);
			acceptedDrag = true;
		}

		return acceptedDrag;
	}

	protected void checkTransferType(DropTargetDragEvent dtde) {
		// Accept a list of files, or data content that
		// amounts to plain text or a Unicode text string
		acceptableType = false;
		draggingFile = false;

			DataFlavor[] flavors = dtde.getCurrentDataFlavors();
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor flavor = flavors[i];
				System.out.println("Drop MIME type " 
					+ flavor.getMimeType() + " is available");	
			}
		
		if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			acceptableType = true;
			draggingFile = true;
		} else if (dtde.isDataFlavorSupported(DataFlavor.plainTextFlavor) 
			|| dtde.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			acceptableType = true;
		}
		System.out.println("File type acceptable - " + acceptableType);
	}

	// This method handles a drop for a list of files
	protected boolean dropFile(Transferable transferable) 
							throws IOException, UnsupportedFlavorException,
								MalformedURLException {
		List fileList = (List)transferable.getTransferData(
								DataFlavor.javaFileListFlavor);
		File transferFile = (File)fileList.get(0);
		
		try {
		
		if(pane != null) {
		
		//	pane.read(new FileReader(transferFile), transferFile);
		//	myframe.setTitle(transferFile.getName());
		
			XmlEditor xmlEditor = XmlEditor.getInstance("");
			xmlEditor.performNewAction(false, transferFile.getAbsolutePath(), true);
			
		
		} else {
			System.out.println(" editor drop in outer frame");

			XmlEditor xmlEditor = (XmlEditor) jframe;
				
			DesktopView dview = xmlEditor.getDesktopView();
				
			xmlEditor.performNewAction(true, transferFile.getAbsolutePath(), true);
				
			//	editor.read(new FileReader(transferFile), transferFile);
			//	myFrame.setTitle(transferFile.getName());

		
		}
		
		/*
		} catch(FileNotFoundException fe) {
			fe.printStackTrace();	
		} catch(IOException ie) {
			ie.printStackTrace();
		*/
		
		} catch(Exception ex) {
				ex.printStackTrace();
		}

		
		
	//	final URL transferURL = transferFile.toURL();
	//	System.out.println("File URL is " + transferURL);

	//	pane.setPage(transferURL);

		return true;
	}

	// This method handles a drop with data content
	protected boolean dropContent(Transferable transferable, DropTargetDropEvent dtde) {
		if (!pane.isEditable()) {
			// Can't drop content on a read-only text control
			return false;
		}
	System.out.println(" inside dropcontent ");
			
		try {
			// Check for a match with the current content type
			DataFlavor[] flavors = dtde.getCurrentDataFlavors();

			DataFlavor selectedFlavor = null;
			
			// Look for either plain text or a String.
			for (int i = 0; i < flavors.length; i++) {
				DataFlavor flavor = flavors[i];

				if (flavor.equals(DataFlavor.plainTextFlavor)
					|| flavor.equals(DataFlavor.stringFlavor)) {
					selectedFlavor = flavor;
					break;
				}
			}
			
			if (selectedFlavor == null) {
				// No compatible flavor - should never happen
				return false;

			}
			
			System.out.println("Selected flavor is " + 
							selectedFlavor.getHumanPresentableName());

			// Get the transferable and then obtain the data
			Object data = transferable.getTransferData(selectedFlavor);

			System.out.println("Transfer data type is " 
							+ data.getClass().getName());
			
			String insertData = null;
			if (data instanceof InputStream) {
				// Plain text flavor
				String charSet = selectedFlavor.getParameter("charset");
				InputStream is = (InputStream)data;
				byte[] bytes = new byte[is.available()];
				is.read(bytes);
				try {
					insertData = new String(bytes, charSet);
				} catch (UnsupportedEncodingException e) {
					// Use the platform default encoding
					insertData = new String(bytes);
				}			
			} else if (data instanceof String) {
				// String flavor
				insertData = (String)data;
			}

		System.out.println(" insertdata " + insertData);
			

			if (insertData != null) {
				int selectionStart = pane.getCaretPosition();
				System.out.println(" dropcontent sel start = " + selectionStart);
				pane.replaceSelection(insertData);
				pane.select(selectionStart, 
								selectionStart + insertData.length());
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

/*
	public static void main(String[] args) {
		final JFrame f = new JFrame("JEditor Pane Drop Target Example 2");
		
		final JEditorPane pane = new JEditorPane();
		
		// Add a drop target to the JEditorPane
		EditorDropTarget2 target = new EditorDropTarget2(pane);

		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				System.exit(0);
			}
		});

		JPanel panel = new JPanel();
		final JCheckBox editable = new JCheckBox("Editable");
		editable.setSelected(true);
		panel.add(editable);
		editable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pane.setEditable(editable.isSelected());
			}
		});
		
		f.getContentPane().add(new JScrollPane(pane), BorderLayout.CENTER);
		f.getContentPane().add(panel, BorderLayout.SOUTH);
		f.setSize(500, 400);
		f.setVisible(true);
	}
*/	
	
	public static String showActions(int action) {
			String actions = "";
			if ((action & (DnDConstants.ACTION_LINK|DnDConstants.ACTION_COPY_OR_MOVE)) == 0) {
				return "None";
			}
	
			if ((action & DnDConstants.ACTION_COPY) != 0) {
				actions += "Copy ";
			}
	
			if ((action & DnDConstants.ACTION_MOVE) != 0) {
				actions += "Move ";
			}
	
			if ((action & DnDConstants.ACTION_LINK) != 0) {
				actions += "Link";
			}
	
			return actions;
	}

	protected JEditorPane pane;

	protected DropTarget dropTarget;
	protected boolean acceptableType;	// Indicates whether data is acceptable
	protected boolean draggingFile;		// True if dragging an entire file	
}

