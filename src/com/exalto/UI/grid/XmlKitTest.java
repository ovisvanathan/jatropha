package com.exalto.UI.grid;

/*
 * $Id: XmlKitTest.java,v 1.2 2002/10/22 14:15:09 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the 
License
 * for the specific language governing rights and limitations under the
License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. 
Portions
created
 * by the Initial Developer are Copyright (C) 2002 the Initial 
Developer.
 * All Rights Reserved.
 *
 * Contributor(s): Edwin Dankert <edankert@cladonia.com>
 */
import java.awt.*;
import java.net.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.xml.transform.*;

import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.text.NumberFormat;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import org.jdesktop.swingx.JXTreeTable;


/**
 * Simple wrapper around JEditorPane to browse java text
 * using the XmlEditorKit plug-in.
 *
 * java XmlKitTest filename
 */
public class XmlKitTest implements DocumentListener, MouseListener,
ComponentListener {
  static  JTextPane editor = new JTextPane();
/*    static XmlEditorPane htmFile = new XmlEditorPane(); */
    JList list = null;
    JPanel panel = new JPanel();
    /* Global value so it can be ref'd by the tree-adapter */
    static Document document;
   // protected DomToTreeModel   model;
    protected int                reloadRow;
    /** A counter increment as the Timer fies and the same path is
     * being reloaded. */
    protected int                reloadCounter;
    static JFrame f = null;

   // OV commented for using jxmltreetable
    // static JXTreeTable       treeTable = null;
    static JXmlTreeTable       treeTable = null;
    //  static ListRowHeader listRowHeader;

    String [] items = { "Product", "name", "version", "price", 
"language"
};
    public XmlKitTest(String filename) {

        try {
        DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
         factory.setIgnoringElementContentWhitespace(true);

         factory.setNamespaceAware(true);

           DocumentBuilder builder = factory.newDocumentBuilder();


           document = builder.parse( new File(filename) );
        } catch (SAXParseException spe) {
           /* Error generated by the parser */
         /*  System.out.println("\n** Parsing error"
              + ", line " + spe.getLineNumber()
              + ", uri " + spe.getSystemId());

           System.out.println("   " + spe.getMessage() );
            */
           /* Use the contained exception, if any */
           Exception  x = spe;
           if (spe.getException() != null)
               x = spe.getException();
           x.printStackTrace();

        } catch (SAXException sxe) {
           /* Error generated during parsing) */
           Exception  x = sxe;
           if (sxe.getException() != null)
               x = sxe.getException();
           x.printStackTrace();
        } catch (ParserConfigurationException pce) {
            /* Parser with specified options can't be built */
            pce.printStackTrace();
        } catch (IOException ioe) {
           ioe.printStackTrace();
        }


		try {
		

        ExaltoXmlNode enode = new
ExaltoXmlNode(document.getDocumentElement());

        //TODO: THis should be read from properties file    
        boolean rootVisible = true;

			//OV commented for refactoring
      //      model =  new DomToTreeModel(enode, document, rootVisible);
        
            XmlTreeModel  model =  new SimpleTreeModel(enode, document, rootVisible);


			// OV commented for using jxmltreetable
       //     treeTable = new JXTreeTable(model, null);
           	treeTable = new JXmlTreeTable(model, null);

            

            /* ov added for row header */
         //   listRowHeader = new ListRowHeader(treeTable);

            /* TableUtilities.setColumnWidths(treeTable, null, false,
false); */
            treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            treeTable.setPreferredScrollableViewportSize(new
Dimension(1500,500));

          /*  JXTreeTable      treeTable = new 
JXTreeTable(treeTableModel);

            //    
treeTable.getColumnModel().getColumn(1).setCellRenderer
            //                           (new IndicatorRenderer());
            //     editor.insertComponent(treeTable);
            //      editor.getDocument().addDocumentListener(this);
          //      editor.addMouseListener(this);
            //     editor.addMouseMotionListener(this);
          //  myGlassPane = new MyGlassPane(treeTable, 
f.getContentPane());

          //   treeTable.addMouseListener(myGlassPane);
            */

		} catch(Exception e) {
			e.printStackTrace();
		}

    }
    
      public XmlKitTest(boolean rootVisible, Document doc, JFrame parent) {

		try {
		  	
      		if(doc == null) {      		
      			ExaltoColumnHandler ehandler = new ExaltoColumnHandler(f);
      			doc = ehandler.createBlankDocument(rootVisible);
      		}	
      	
      	
			ExaltoXmlNode enode = null;
			if(!rootVisible)
				enode = new ExaltoXmlNode(doc.getDocumentElement());

			//OV commented for refactoring
            // model =  new DomToTreeModel(enode, doc, rootVisible);
        
            XmlTreeModel  model =  new SimpleTreeModel(enode, doc, rootVisible);
           
	        treeTable = new JXmlTreeTable(model, parent);

            /* ov added for row header */
     //       listRowHeader = new ListRowHeader(treeTable);

            treeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            treeTable.setPreferredScrollableViewportSize(new
Dimension(1500,500));

					
	//		XmlKitTest ktest = new XmlKitTest("marc.xml");
			JScrollPane gridScroller = new JScrollPane(treeTable,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		//    gridScroller.setRowHeaderView(listRowHeader);
		} catch(Exception e) {
			e.printStackTrace();	
		}
      	
      }
 
    

    public static void main(String[] args) {
/* if (args.length != 1) {
//     System.err.println("need filename argument");
//     System.exit(1);
// }
*/
 try {
     f = new JFrame("XmlEditorKit: simple.xml");

       XmlKitTest ktest = new XmlKitTest("marc.xml");
/*     File file = new File(args[0]);
//     editor.read( new FileReader(file), file);
*/
     JScrollPane scroller = new JScrollPane(treeTable,
JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
/*     JScrollPane scroller = new JScrollPane(treeTable,
JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS); */

    //      scroller.setRowHeaderView(listRowHeader);


       /* Set up the glass pane, which appears over both menu bar
        // and content pane and is an item listener on the change
        // button.
 //       f.setGlassPane(myGlassPane);
            */
  WindowListener l = new WindowAdapter() {
   public void windowClosing(WindowEvent e) {
   System.exit(0);
  }
  };
 /* LookAndFeel lf = UIManager.getLookAndFeel();
     // Install a different look and feel; specifically, the Windows 
look
and feel
*/
     try {
             /*com.sun.java.swing.plaf.windows.WindowsLookAndFeel
     //javax.swing.plaf.metal.MetalLookAndFeel
   //  UIMaager.installLookAndFeel("TableLookAndFeel",
"javax.swing.plaf.metal.TableLookAndFeel");
   */

   /*
     } catch (InstantiationException e) {
      e.printStackTrace();
     } catch (ClassNotFoundException e) {
       e.printStackTrace();
     } catch (UnsupportedLookAndFeelException e) {
       e.printStackTrace();
   */
     } catch (Exception e) {
       e.printStackTrace();
     }

  f.addWindowListener(l);
  f.addComponentListener(ktest);


     f.getContentPane().setLayout( new BorderLayout());
     f.getContentPane().add( scroller, BorderLayout.CENTER);
     f.setSize(600, 400);
     f.setVisible(true);

   } catch (Throwable e) {
     e.printStackTrace();
     System.exit(1);
 }
    }

public  void insertUpdate(DocumentEvent e) {
      editor.repaint();
      editor.revalidate();
}
public void removeUpdate(DocumentEvent e) {
}
public void changedUpdate(DocumentEvent e) {
}


public void componentResized(ComponentEvent e) {

       Component c = e.getComponent();

      treeTable.setSize(c.getSize());
      treeTable.revalidate();


      /*

            Component c = e.getComponent();
        displayMessage("componentResized event from "
                       + c.getClass().getName()
                       + "; new size: "
                       + c.getSize().width
                       + ", "
                       + c.getSize().height);
    */

      }

       public void componentHidden(ComponentEvent e) {
       }

       public void componentMoved(ComponentEvent e) {

       }

      public void componentShown(ComponentEvent e) {
      }




    /**
     * Creates the FileSystemModel2 that will be used.
     */
 /*   protected DomToTreeModel createModel() {
 //      return new DomToTreeModel(document);
 //   }
 */



public static void doTransform() {

/*
    try {

       FileWriter fout = new FileWriter("tree.htm");
      Reader xmlInput =
        new StringReader(editor.getText());
      Reader xslInput =
        new FileReader("defaultss2.xsl");
      XslTransformer transformer = new XslTransformer();
      transformer.process(xmlInput, xslInput, fout);
      JFrame htmFrame = new JFrame("xsl result");
      JEditorPane htmPane = new JEditorPane();
      htmFrame.getContentPane().add(htmPane);
      File htmFile = new File("tree.htm");
      htmPane.read( new FileReader(htmFile), htmFile);
      htmFrame.setSize(100,200);
      htmFrame.pack();
      htmFrame.show();

    } catch(TransformerException te) {
      te.printStackTrace();
    } catch(Exception e) {
      e.printStackTrace();
    }
*/
}

    public void mouseMoved(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mouseDragged(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mouseClicked(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mouseEntered(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mouseExited(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mousePressed(MouseEvent e) {
        redispatchMouseEvent(e, false);
    }
    public void mouseReleased(MouseEvent e) {
        redispatchMouseEvent(e, true);
    }
    /*A more finished version of this method would
    //handle mouse-dragged events specially.
      */
    private void redispatchMouseEvent(MouseEvent e,
                                      boolean repaint) {

         treeTable.getCellEditor().isCellEditable(e);
    }
    
    public JXmlTreeTable getTreeTable() {
    	return treeTable;
    }

    public Document getDocument() {
    	return treeTable.getDocument();
    }
    
}
