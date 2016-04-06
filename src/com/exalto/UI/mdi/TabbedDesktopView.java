package com.exalto.UI.mdi;

import com.exalto.ColWidthTypes;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import javax.swing.JPopupMenu;

import com.exalto.UI.ExaltoExplorer;
import com.exalto.UI.XmlEditor;
import com.exalto.UI.multiview.TabsComponent;
import com.exalto.UI.tabs.CloseableTabbedPane;
import com.exalto.UI.tabs.CloseableTabbedPaneListener;
import com.exalto.UI.util.ExplorerNode;
import com.exalto.util.ExaltoConstants;
import com.exalto.util.ExaltoResource;
import com.exalto.util.StatusEvent;
import com.exalto.util.XmlUtils;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.undo.UndoManager;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class TabbedDesktopView extends DesktopView 
         implements CloseableTabbedPaneListener {
	
	private Font theFont = new Font("MS Sans Serif", Font.PLAIN, 12);
	private XmlUtils xutils;
	private XmlEditor xmlEditor;
    private ExaltoExplorer explorer;
    
    boolean useTabbedEmulation;
        
    /** If we are using tabbed emulation, a reference to the closable
      *  tabbed pane we are using to contain JInternalFrames
      */
    
	// used to pass unsaved files to xplorer
	Vector unsaved = null;

    /**
      * Cache of JInternalFrames in the JDesktopPane
      */
      protected Vector frames = new Vector();
 
     /** Cache of the currently selected frame */
      protected JInternalFrame pSelectedFrame = null;
         
    protected CloseableTabbedPane jtp = null;

    // OV added 270409
     int retval = -1;
//	private Project project;
   
	public TabbedDesktopView(boolean useTabbedEmulation) {
		
		this.useTabbedEmulation = useTabbedEmulation; 
	
		xutils = XmlUtils.getInstance();
		
	//	putClientProperty("JDesktopPane.dragMode", "faster");
	//    setBackground(xutils.getResourceString(colWidthTypes.BACKGROUND_COLOR));
    
	//		xmlEditor = xeditor;

		
		
//		 If we are using the tabbed stuff, we need to create
         // a closable tabbed pane and add it as a single child
         // of this component, then fill it with the frames.
         if (useTabbedEmulation) {
             // Add items onto a tabbed pane, filling this
             // container.
             setLayout(new BorderLayout());
             jtp = new CloseableTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
             jtp.setTabPlacement(SwingConstants.TOP);

             jtp.addCloseableTabbedPaneListener(this);

             jtp.addChangeListener(new ChangeListener() {
                // This method is called whenever the selected tab changes
                public void stateChanged(ChangeEvent evt) {
                     JTabbedPane pane = (JTabbedPane)evt.getSource();

                    // Get current tab
                    int sel = pane.getSelectedIndex();

                    assignStylesheet(sel);

                }
            });



             
             add(jtp, BorderLayout.CENTER);
             Iterator i = frames.iterator();
             while (i.hasNext()) {
                 JInternalFrame frame = (JInternalFrame) i.next();

                 String fname = frame.getTitle();

          //       System.out.println(" in TDV fname = " + fname);

                 int spos = fname.lastIndexOf("\\");

                 if(spos < 0)
                     spos = fname.lastIndexOf("/");

         //        System.out.println(" in TDV spos = " + spos);

                 if(spos > 0)
                    fname = fname.substring(spos+1);

         //        System.out.println(" in TDV spos fname= " + fname);

                 jtp.addTab(fname, frame.getFrameIcon(), frame, frame.getTitle());
             }
             
             
             // Listen for close events
    //         jtp.addTabCloseListener(new TabCloseListener() {
    //             public boolean tabClosed(int index) {
    //                 return ((JInternalFrame) frames.get(index)).processFrameClosing();
    //             }
    //         });
             // Listen for tab changed events and call newTabSelected
 	    // - can be overriden by subclasses.
 	    jtp.addChangeListener(new ChangeListener() {
                 public void stateChanged(ChangeEvent e) {
                     newTabSelected(getSelectedFrame());
 		}
 	    });
         }
         else {
             // If we aren't using tabbed emulation, then
             // the JInternalFrames can draw themselves on the
             // JLayeredPane - this is already taken care of in
             // the call to super.setSwingWTParent() as they are
             // regular JComponents
         }

	
	}


	/** Overridden add method to deal with JInternalFrames */
//     public Component add(final Component c) {
    public Component add(final JInternalFrame c) {
         /** If it's not a JInternalFrame, or we aren't using tabbed emulation, just
           * add the component to us as normal. */
         if (!(c instanceof JInternalFrame)) {
             super.add(c);
             return c;
         }
         else
         {
             // Add the frame to the cache of internal frames we
             // are holding and set a reference to us on the frame itself.
        //     System.out.println(" in TDV add ");

        	 frames.add((JInternalFrame) c);
          //   ((JInternalFrame) c).setParentPane(this);
             if (useTabbedEmulation) {
                 return (Component) addInternalFrameToTabbedPane((JInternalFrame) c);
             }
             else {
                 // Define initial size/location
                 // Size should be overriden by the subclass anyway
         //        Point loc = getNextFrameLocation();
         //        c.setBounds(loc.x, loc.y, 320, 200);
                 super.add(c);
                 return c;
             }
         }
     }

     /**
       * TABBED EMULATION ONLY:
       * Adds a JInternalFrame to the tabbed pane */
     protected JInternalFrame addInternalFrameToTabbedPane(final JInternalFrame frame) {
 
         frame.getRootPane().setWindowDecorationStyle(0);
         
         undecorateFrame(frame);
       //  .frame.setu.setDrawDecoration(false); // Tabbed pane emulation doesn't want the frame to draw
                                         // window decorations.
         if (jtp == null) return frame;
 
         final JDesktopPane me = this;
         
         
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 if (jtp != null) {

                 String fname = frame.getTitle();

                 System.out.println(" in TDV fname = " + fname);

                 int spos = fname.lastIndexOf("\\");

                 if(spos < 0)
                     spos = fname.lastIndexOf("/");

      //           System.out.println(" in TDV spos = " + spos);

                 if(spos > 0)
                    fname = fname.substring(spos+1);

      //           System.out.println(" in TDV spos fname= " + fname);



                //	 jtp.add(fname, frame);

                     jtp.addTab(fname, frame, frame.getFrameIcon());

                	 //      jtp.addTab(frame.getTitle(), frame.getFrameIcon(), frame, frame.getTitle());
                     // Set focus to the added frame
 
       //              System.out.println(" in AIFTTP jtp set sel index = " + (jtp.getTabCount() - 1));

                     int x = jtp.getTabCount();
                     if(x == 0)
                         x = 1;
                     jtp.setSelectedIndex(x - 1);
                     // Selected frame has changed
                     newTabSelected(frame);
       //              System.out.println(" in add f sz   =" + frames.size());

                 }
             }
         });
         return frame;
     }
     
     
     /**
      * TABBED EMULATION ONLY
      * Useful for subclasses - gets called when the user clicks
      * to change frame.
      */
     protected void newTabSelected(JInternalFrame j) {
     }
     /**
      * TABBED EMULATION ONLY:
      * Updates a frame's components from it's cached properties.
      */
     protected void refreshFrame(final JInternalFrame frame) {
         // If we aren't using tabbed emulation, stop now
         if (!useTabbedEmulation) return;
         // If no frame specified, drop out
         if (frame == null) return;
         
         // Go onto event dispatch thread to prevent frames being
         // closed whilst we process
         SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 // Get all frames to find this one (drop out if none exist)
                 final JInternalFrame[] frames = getAllFrames();
                 if (frames == null) return;
                 for (int i = 0; i < frames.length; i++)
                     if (frames[i] == frame)
                         updateFrameAt(i, frame);
             }
         });
     }
     
     private void undecorateFrame(JInternalFrame f) {
    	 
    	 f.setFrameIcon(null);
    	 f.setClosable(false);
    	 f.setIconifiable(false);
    	 f.setMaximizable(false);
    	 f.setResizable(false);
    //	 f.setTitle(null);
    	 
//    	 Get the titlebar and set it to null
    //	 setRootPaneCheckingEnabled(false);
    	 javax.swing.plaf.InternalFrameUI ifu= f.getUI();
    	 ((javax.swing.plaf.basic.BasicInternalFrameUI)ifu).setNorthPane(null);

     }
     
     /**
      * TABBED EMULATION ONLY:
      * Copies an internal frame's properties onto it's component
      */
     protected void updateFrameAt(int index, JInternalFrame frame) {
         jtp.setTitleAt(index, frame.getTitle());
         jtp.setIconAt(index, frame.getFrameIcon());
     }
     
    /* 
     public JInternalFrame[] getAllFrames() {
    	 JInternalFrame[] ret = null;
    
    	 System.out.println(" frames size = " + ret.length);
    	 if(frames != null && frames.size() > 0) {
	    	 ret = new JInternalFrame[frames.size()];
	         Object[] jf = frames.toArray();
	         for (int i = 0; i < jf.length; i ++) {
	             ret[i] = (JInternalFrame) jf[i];
	         }
	         jf = null;
	         
    	 }
    	 
    	 return ret;
    	 
     }
     */
     
     /**
      * Returns all <code>JInternalFrames</code> currently displayed in the
      * specified layer of the desktop. Returns iconified frames as well
      * expanded frames.
      *
      * @param layer  an int specifying the desktop layer
      * @return an array of <code>JInternalFrame</code> objects
      * @see JLayeredPane
      */
  /*
     public JInternalFrame[] getAllFramesInLayer(int layer) {
         int i, count;
         JInternalFrame[] results;
         Vector vResults = new Vector(10);
         Object next, tmp;

 		System.out.println(" in GAFIL f sz  =" + frames.size());
 		
 		System.out.println(" in GAFIL gcc  =" + this.getComponentCount());

 		
         count = getComponentCount();
         for(i = 0; i < count; i++) {
    //    	 next = frames.get(i);
             next = getComponent(i);
             
      		System.out.println(" in GAFIL NEXT  =" + next);

             if(next instanceof JInternalFrame) {
                 if(((JInternalFrame)next).getLayer() == layer)
                     vResults.addElement(next);
             } else if(next instanceof JInternalFrame.JDesktopIcon)  {
                 tmp = ((JInternalFrame.JDesktopIcon)next).getInternalFrame();
                 if(tmp != null && ((JInternalFrame)tmp).getLayer() == layer)
                     vResults.addElement(tmp);
             } else if(next instanceof CloseableTabbedPane)  {

                CloseableTabbedPane nextpane = (CloseableTabbedPane) next;

                return (JInternalFrame[]) nextpane.getAllFramesInLayer();
             }
             
         }

         results = new JInternalFrame[vResults.size()];
         vResults.copyInto(results);

         return results;
     }
*/
     public JInternalFrame[] getAllFramesInLayer(int layer) {

         JInternalFrame[] results = null;

         results = new JInternalFrame[frames.size()];
         frames.copyInto(results);

         return results;

     }


     public int getComponentCountInLayer(int layer) {
    	 return frames.size(); 
     }
     
     /** Removes an internal frame */
     protected void removeFrame(JInternalFrame frame) {
         if (useTabbedEmulation) {
             int i = frames.indexOf(frame);
             if (i != -1) {
                 jtp.removeTabAt(i);
                 frames.remove(frame);
             }
         }
         else {
             frames.remove(frame);
             remove(frame);
         }
     }
     
     
     
     public JInternalFrame getSelectedFrame() {
         if (useTabbedEmulation) {
             if (jtp == null) return null;
             
  //           System.out.println(" sel index = " + jtp.getSelectedIndex());
             
             int selIndex = jtp.getSelectedIndex();
  //           System.out.println(" sel index new = " + selIndex);

             if(selIndex < 0)
                 selIndex = 0;

  //          System.out.println(" fms.get sel index = " + frames.get(selIndex));

             return (JInternalFrame) frames.get(selIndex);
         }
         else
         {
             // We cache the currently selected frame
             // in setSelectedFrame. If none is cached, we
             // return the first one if there is one,
             // otherwise return null.
             if (pSelectedFrame != null)
                 return pSelectedFrame;
             else if (frames.size() > 0)
                 return (JInternalFrame) frames.get(0);
             else
                 return null;
         }
     }
     public void setSelectedFrame(JInternalFrame frame) {
         if (useTabbedEmulation) {
             if (jtp == null) return;
             int i = frames.indexOf(frame);
             if (i != -1)
                 jtp.setSelectedIndex(i);
         }
         else {
             // Mark the JInternalFrame as selected and
             // bring it to the front of the layered pane.
 	    //
 	    // This is called back from events in JComponent
 	    // when they detect they are added to a JInternalFrame
 	    // that isn't using tabbed emulation
             pSelectedFrame = frame;
             moveToFront(frame);
         }
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

        boolean selectedHasFileName = true;
        String untitledFile = null;

         MyInternalFrame selectedFrame = (MyInternalFrame) jtp.getSelectedComponent();

        if(selectedFrame.getFile().getName().startsWith("Untitled")) {
            selectedHasFileName = false;
            untitledFile = selectedFrame.getFile().getAbsolutePath();

        }

        JInternalFrame [] j = this.getAllFramesInLayer(JDesktopPane.DEFAULT_LAYER.intValue());

        for(int i=0;i<j.length;i++)
          	System.out.println(" bef frame title at " + i + " = " + j[i].getTitle());


		((MyInternalFrame) getSelectedFrame()).saveAs(file);
        if(selectedFrame == getSelectedFrame()) {
            int id = jtp.getSelectedIndex();
            jtp.setTitleAt(id, file.getName());
            selectedFrame.setTitle(file.getName());
        }

    
        for(int i=0;i<j.length;i++)
          	System.out.println(" aft frame title at " + i + " = " + j[i].getTitle());



        try {

                    if(!selectedHasFileName)
                        this.getExplorer().addFile(file.getCanonicalPath(), untitledFile);
                    else
                        this.getExplorer().addFile(file.getCanonicalPath(), null);

        	} catch (IOException ioe) {
            	System.out.println("MyDesktopPane write - IOException");
            }


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

                
                explorer = XmlEditor.getInstance("").getExplorer();

              //  explorer.addFile(f.getAbsolutePath());
                
                
//		System.out.println("MyIntf display f not null ");
		JInternalFrame j[] = getAllFramesInLayer(JDesktopPane.DEFAULT_LAYER.intValue());
		boolean found = false;
		
		System.out.println("getComponentCountInLayer =" + getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()));
		
		System.out.println(" arr size =" + j.length);
		
		for (int i = 0; i < getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()); i++) {

            System.out.println(" j[i].getTitle() =" + j[i].getTitle());
			System.out.println(" f.getName() =" + f.getName());
	
			System.out.println(" f.getCanonicalPath() =" + f.getCanonicalPath());

            String s = j[i].getTitle();

			// PREREL BUG FIX 090610
			if(s.indexOf("*") > 0) {
				s = s.substring(0,s.indexOf("*"));				
			}

            // changed f.getcanpath to f.getName 
 			// PREREL BUG FIX 090610 changed f.getName  to f.getcanpath
		
			if (s.equals(f.getCanonicalPath())) {
				if(f.getName().startsWith("XSL")) {
					System.out.println("###########Filename begins with xsl####### ");
						this.removeFrame(j[i]);
						repaint();
						revalidate();
				} else {
					moveToFront(j[i]);
					found = true;
					((MyInternalFrame) j[i]).select();
                    jtp.setSelectedComponent(j[i]);
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
		

	//	MyInternalFrame m = new MyInternalFrame(f, newwidth, newheight, defaultView);
    try {

        MyInternalFrame m = new MultiViewInternalFrame(f, newwidth, newheight, defaultView);

        m.addPropertyChangeListener(this);

        if(f != null) {
            if(!xmlEditor.getFileToFrameMap().containsKey(f.getName()))
                xmlEditor.getFileToFrameMap().put(f.getAbsolutePath(), m);
        }
        else {
            f = m.getFile();
            String file = m.getFile().getAbsolutePath();
            if(!xmlEditor.getFileToFrameMap().containsKey(file))
                xmlEditor.getFileToFrameMap().put(file, m);
        }
        
		System.out.println("MyIntf newwidtth " + m.getSize().width);
		System.out.println("MyIntf newheight " + m.getSize().height);

		m.setBorder(BorderFactory.createEmptyBorder());			
	//	m.addInternalFrameListener(this);
			
		Editor editor = (Editor) m.getTextComponent();
		editor.addCaretListener(xmlEditor);			
		
		m.addInternalFrameListener(xmlEditor);
		
		((JTextComponent)m.getTextComponent()).getDocument().addUndoableEditListener(
						xmlEditor.new MyUndoableEditListener());
	
			
                
                ExaltoExplorer explorer = xmlEditor.getExplorer();
                
          //      int ret = explorer.addFile(f.getAbsolutePath(), f.getAbsolutePath());
                int ret = 0;
                if(f.getName().indexOf("XSL Result") < 0 && f.getName().indexOf("XQuery Result") < 0)
                      ret = explorer.addFile(f.getAbsolutePath(), null);
                else
                      ret = explorer.addFile(f.getAbsolutePath(), f.getAbsolutePath());


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

        System.out.println(" jtp sel index = " + jtp.getSelectedIndex());

        System.out.println(" jtp sel comp == m is " + (jtp.getSelectedComponent() == m));

        // OV added 010509
     //   jtp.setSelectedComponent(m);


	//	m.setLocation((getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()) - 1) * 30, (getComponentCountInLayer(JDesktopPane.DEFAULT_LAYER.intValue()) - 1) * 30);
        
        moveToFront(m);
		m.select();

        } catch(Exception e) {

            e.printStackTrace();

        }
        
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
	
     
	public void addProject() {
     
        String input = (String) JOptionPane.showInputDialog(this,
        "Please enter name of new project", ExaltoConstants.APP_NAME,
        JOptionPane.PLAIN_MESSAGE, null, null, "");

        ExaltoExplorer explorer = XmlEditor.getInstance("").getExplorer();

       ProjectInfo pinfo = new ProjectInfo();

       pinfo.setProjectName(input);
        
        explorer.addProject(pinfo);
	}


   		public boolean closeProject(ExplorerNode selNode, boolean isDelete) {

            boolean shouldDelete = false;
            ExplorerNode exnode = null;
            boolean ret = true;

            exnode = selNode;
            if(selNode == null) {
               exnode = getExplorer().getSelectedProject();
            }

            if(isDelete) {

               if(xmlEditor == null)
                   xmlEditor = XmlEditor.getInstance("");

         //          CloseProjOkCancelDialog clsProjDlg = new CloseProjOkCancelDialog(xmlEditor, true);

         //          clsProjDlg.setVisible(true);

         //          shouldDelete =  clsProjDlg.shouldDelete();

          //         if(clsProjDlg.getReturnStatus() == CloseProjOkCancelDialog.RET_CANCEL)
          //             return false;

            }

            Node xnode = exnode.getXmlNode();

            NodeList nl = xnode.getChildNodes();

            for(int i=0;i<nl.getLength();i++) {

                Node ch = nl.item(i);

                String file = ch.getNodeName();

                if(file.equals("file")) {

                    Node tnode = ch.getFirstChild();

                    String name = tnode.getNodeValue();

                    ret = closeTab(name, shouldDelete);

                    if(!ret)
                        break;

                }

            }


            if(ret)
                explorer.closeProject(shouldDelete);

            return ret;
		}


     public boolean closeTab(String fname, boolean shouldDelete) {

         int tabid = -1;


         for(int i=0;i<frames.size();i++) {

             if(fname.equals(((MyInternalFrame)frames.get(i)).getTitle()))
             {
                     tabid = i;
                     break;
             }

             if(tabid == -1) {
                  fname = fname + "*";
                  if(fname.equals(((MyInternalFrame)frames.get(i)).getTitle()))
                  {
                     tabid = i;
                     break;
                  }
             }

             int spos = fname.indexOf("*");
             if(spos > 0)
                 fname = fname.substring(0, spos);

         }

         if(tabid == -1) {
         
             int spos = fname.indexOf("*");
             if(spos > 0)
                 fname = fname.substring(0, spos);

             File f = new File(fname);

             if(!f.exists()) {
                 
                 int ret = JOptionPane.showConfirmDialog(explorer, "File doesn't exist. WOuld you like to remove node? " + fname, "Error",  JOptionPane.ERROR_MESSAGE);


                 if(ret == JOptionPane.OK_OPTION) {
                      getExplorer().closeFile(fname, false, true);
                 }
                 else
                     return false;

             }

             return true;
         }



         return closeTab(tabid, fname, shouldDelete);

     }


      public boolean closeTab(int tabid, String file, boolean shouldDelete) {

            MyInternalFrame mvf = (MyInternalFrame) frames.get(tabid);

            String title = mvf.getTitle();

			// PREREL  M FOR VECTOR ARG
				String chkClose = mvf.okToClose();

               if (chkClose.equals("false")) {
                   return false;
               }
               else {

                    removeFrame(mvf);

 //                   jtp.remove(tabid);
               }

         // commented for closing tab should not remove file from dom
      //          getExplorer().closeFile(file, shouldDelete, false);

                if(xmlEditor == null)
                   xmlEditor = XmlEditor.getInstance("");

				// PREREL  M FOR VECTOR ARG
				    if (chkClose.equals("maybe")) {

						 int spos = file.indexOf("*");
			             if(spos > 0)
						     file = file.substring(0, spos);


						unsaved.add(file);
					}	
					

                  // remove file from file to frame map
                xmlEditor.checkFileExistsDelete(file);


                return true;

    }

   
     public boolean closeTab(int tabid) {

     //     MyInternalFrame jc = (MyInternalFrame) jtp.getTabComponentAt(tabid);

    
          MyInternalFrame mvf = (MyInternalFrame) frames.get(tabid);

            String title = mvf.getTitle();




   //       if(mvf == jc)  {

               if (!mvf.checkClose()) {
                   return false;
               }
               else {
                    //        try {
                    //            mvf.setClosed(true); //Don't use the default
                    //			activateDocumentAfterClose();
                    //        }
                    //        catch (java.beans.PropertyVetoException ex) {
                    //                ex.printStackTrace();
                    //        }


                             removeFrame(mvf); 
                            // remove file from file to frame map
                            xmlEditor.checkFileExistsDelete(title);

                     }
            //    }

              //      getExplorer().closeFile(null, false);

        return true;

    }

     public void closeFile() {

         boolean ret = false;
         Node exnode = getExplorer().getSelectedNode();
         
           if(exnode != null) {

               if(exnode.getNodeName().equals("project")) {
                   ret = closeProject(null, false);
               } else {
                   Node tnode = exnode.getFirstChild();
                   ret = closeTab(tnode.getNodeValue(), false);
               
               
                   if(ret)
                          getExplorer().closeFile(null, false, true);


               }
           }

     }
// PREREL  M TO GET UNSAVED FILE VEC
      public boolean closeAllProjects() {
            boolean ret = true;

            saveFrameXPaths();

			unsaved = new Vector();

            ExplorerNode exnode = getExplorer().getSelectedProject();

            ExplorerNode top = (ExplorerNode) exnode.getParent();
            
            if(top != null) {

                    int i = 0;
                    while(i<= top.getChildCount() && top.getChildCount() > 0) {

                        if(top.getChildCount() == i)
                            break;
                        
                        ExplorerNode temp = (ExplorerNode) top.getChildAt(i++);

                        NamedNodeMap nmp = temp.getXmlNode().getAttributes();

                        Node attr = nmp.getNamedItem("name");

                        String pname = attr.getNodeValue();

                        if(temp.getChildCount() > 0) {

                            for(int x=0;x<temp.getChildCount();x++) {

                                ExplorerNode ftemp = (ExplorerNode) temp.getChildAt(x);

                                if(ftemp.getXmlNode().getNodeName().equals("file")) {
                                    Node ftext = ftemp.getXmlNode().getFirstChild();
                                    String val = ftext.getNodeValue();
                                    ret = closeTab(val, false);
                                }

                                if(!ret)
                                   break;

                                System.out.println(" top GCC  = " + top.getChildCount());

                            }

                        } else {
                        
                            if(retval == -1)
                                retval = JOptionPane.showConfirmDialog(explorer, "Projects exist with no files. Do you want to delete these projects? ", "Info",  JOptionPane.INFORMATION_MESSAGE);

                            if(retval == JOptionPane.OK_OPTION) {

                                if(!pname.equals("a")) {
                                    getExplorer().closeFile(pname, false, true);
                                    i--;
                                }
                            }

                            System.out.println(" top GCC  = " + top.getChildCount());
                            System.out.println(" top GCC i = " + i);


                        }

                    } // end while

                    retval = -1;

            }

			   System.out.println(" unsaved = " + unsaved);

			// PREREL  M ADDEE VECTOR ARG
               getExplorer().shutDown(unsaved);
			   unsaved = null;
         

            return ret;

      }


      public void saveFrameXPaths() {

        ArrayList master = new ArrayList();
        for(int i=0;i<frames.size();i++) {

            MultiViewInternalFrame myf = (MultiViewInternalFrame) frames.get(i);

            TabsComponent tabsComp = myf.getTabsComp();

            ArrayList alist = tabsComp.getComboItems();

            if(alist != null)
                master.addAll(alist);

        }

        // Stream to write file
		FileOutputStream fout;

		try
		{

            String file = java.util.ResourceBundle.getBundle(
                "exalto/xmlgrid").getString("xpath.history.file");

            File f = new File(file);

            if(f.exists())
                f.delete();

		    // Open an output stream
		    fout = new FileOutputStream (file);


            for(int k=0;k<master.size();k++) {
                // Print a line of text
                new PrintStream(fout).println (master.get(k));
            
            }
		    // Close our output stream
		    fout.close();
		}
		// Catches any error conditions
		catch (IOException e)
		{
			System.err.println ("Unable to write to file");
		}


      }


  /*
    JInternalFrame getNextFrame(JInternalFrame f) {

        System.out.println(" in GNF  ");

            return (JInternalFrame) frames.get(tabid+1);
    }


    private JInternalFrame getNextFrame(JInternalFrame f, boolean forward) {

        System.out.println(" in GNF  ");

        if (f == null) {
            return (JInternalFrame) frames.get(0);
        }

        return (JInternalFrame) frames.get(tabid+1);

       
    }
*/

     public ExaltoExplorer getExplorer() {
         if(explorer == null)
             explorer = XmlEditor.getInstance("").getExplorer();
         
         return explorer;
         
     }

    public void propertyChange(PropertyChangeEvent pe) {

            Boolean isSaved = (Boolean) pe.getNewValue();
            
            MyInternalFrame selectedFrame = (MyInternalFrame) jtp.getSelectedComponent();

            if(selectedFrame == getSelectedFrame()) {

                if(isSaved.booleanValue() == false) {
                
                        int id = jtp.getSelectedIndex();

                        String title = jtp.getTitleAt(id);

                        if(title.indexOf("*") < 0)
                            title = title + "*";
                        
                        jtp.setTitleAt(id, title);
                        
                 } else {

                        int id = jtp.getSelectedIndex();

                        String title = jtp.getTitleAt(id);

                        if(title.indexOf("*") > 0)
                            title = title.substring(0, title.indexOf("*"));

                        jtp.setTitleAt(id, title);
               
                 }

            }


    }


    public void assignStylesheet(int selIndex) {


        try {

            // switch out certain menu items
                   MyInternalFrame xmlFrame = xmlEditor.getXmlFileFrame();
                   
                   if(xmlFrame != null && xmlFrame.isWaitingForAssign()) {


            Object o = jtp.getComponentAt(selIndex);

            if (o instanceof JInternalFrame) {
                //  Object doc = _frameToDocumentMap.get((JInternalFrame)o);
                //  if (doc instanceof XMLEditorDoc) {

                // OV 24/07/2005 dcommented for reopen  begin

                UndoManager undoManager = getCurrentUndoManager();

             if(xmlEditor == null)
                xmlEditor = XmlEditor.getInstance("");

                xmlEditor.resetUndoAction(undoManager);
                xmlEditor.resetRedoAction(undoManager);

                // OV 24/07/2005 dcommented for reopen end

                //		docActivated((XMLEditorDoc)doc);
                //  }


                MyInternalFrame selFrame = null;

                selFrame = (MyInternalFrame) getSelectedFrame();

          //      MyInternalFrame xmlFrame = xmlEditor.getXmlFileFrame();


        //        System.out.println(" selframe title " + selFrame.getTitle());

        //        System.out.println(" selframe assign " + selFrame.isWaitingForAssign());


          //      if(xmlFrame != null && xmlFrame.isWaitingForAssign()) {

          //          System.out.println(" in waitingtoassign ");

                    String frameFile = xmlFrame.getFile().getAbsolutePath();

            
            //        System.out.println(" xsl file = " + selFrame.getFile().getAbsolutePath());

                    String xslFile = selFrame.getFile().getAbsolutePath();

                /*
                    if(!xslFile.endsWith(".xsl")) {
                        JOptionPane.showMessageDialog(null,
                                "Not a valid XSL File ");
                    }
                 */
                    
            //        System.out.println(" frame map file = " + frameMapFile);


                    setStyleSheet(xslFile,
                            (MyInternalFrame) xmlEditor.getFileToFrameMap().get(frameFile));
                    
                    frameFile = null;
                    xmlFrame.setWaitingForAssign(false);

                    xmlEditor.assignStyleAction.shouldInvoke = false;
                    
                    xmlEditor.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"stylesheet.assign"),0, ColWidthTypes.NOERROR));

            //    }

            }


            }
            else {
                    if(xmlEditor.getTimer() != null)
                        xmlEditor.getTimer().stop();
            }

        } catch(Exception cse) {
            cse.printStackTrace();
        }


    }

public void addPopup(JPopupMenu menu) {

    add(menu);
	jtp.add(menu);

}


}


