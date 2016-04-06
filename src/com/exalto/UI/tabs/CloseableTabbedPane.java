package com.exalto.UI.tabs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import com.exalto.UI.mdi.DesktopView;
import com.exalto.util.XmlUtils;


import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
 
/**
 * A JTabbedPane which has a close ('X') icon on each tab.
 *
 * To add a tab, use the method addTab(String, Component)
 *
 * To have an extra icon on each tab (e.g. like in JBuilder, showing the file
 * type) use the method addTab(String, Component, Icon). Only clicking the 'X'
 * closes the tab.
 */
public class CloseableTabbedPane extends JTabbedPane implements MouseListener,
  MouseMotionListener {
 
  /**
   * The <code>EventListenerList</code>.
   */
  private EventListenerList listenerList = null;
 
  /**
   * The viewport of the scrolled tabs.
   */
  private JViewport headerViewport = null;
 
  /**
   * The normal closeicon.
   */
  private Icon normalCloseIcon = null;
 
  /**
   * The closeicon when the mouse is over.
   */
  private Icon hooverCloseIcon = null;

  /**
   * The closeicon when the mouse is over.
   */
  private Icon extraIcon = null;

  /**
   * The closeicon when the mouse is pressed.
   */
  private Icon pressedCloseIcon = null;
 
  /**
        * True when colors were already initialized, false otherwise
        */
  private static boolean colorsReady = false;
   
  private static Color unselFillBrightC, unselFillDarkC, selFillC, focusFillBrightC, focusFillDarkC, txtC, borderC, bottomBorderC, selBorderC, bgFillC;

  DesktopView desktopView;
  XmlUtils xutils;

  /**
   * Creates a new instance of <code>CloseableTabbedPane</code>
   */
  public CloseableTabbedPane() {
    super();
	
	xutils = XmlUtils.getInstance();

	init(SwingUtilities.LEFT);
  }
 
  /**
   * Creates a new instance of <code>CloseableTabbedPane</code>
   * @param horizontalTextPosition the horizontal position of the text (e.g.
   * SwingUtilities.TRAILING or SwingUtilities.LEFT)
   */
  public CloseableTabbedPane(int horizontalTextPosition) {
    super();

	xutils = XmlUtils.getInstance();

	init(horizontalTextPosition);
  }

  /**
   * Creates a new instance of <code>CloseableTabbedPane</code>
   * @param horizontalTextPosition the horizontal position of the text (e.g.
   * SwingUtilities.TRAILING or SwingUtilities.LEFT)
   */
  public CloseableTabbedPane(int tabPosition, int tabLayout, int horizontalTextPosition) {
    super(tabPosition, tabLayout);

	xutils = XmlUtils.getInstance();

	init(horizontalTextPosition);
  }

  /**
   * Creates a new instance of <code>CloseableTabbedPane</code>
   * @param horizontalTextPosition the horizontal position of the text (e.g.
   * SwingUtilities.TRAILING or SwingUtilities.LEFT)
   */
  public CloseableTabbedPane(int tabPosition, int tabLayout) {
    super(tabPosition, tabLayout);

	xutils = XmlUtils.getInstance();

	init(SwingUtilities.LEFT);

  }

  /**
   * Creates a new instance of <code>CloseableTabbedPane</code>
   * @param horizontalTextPosition the horizontal position of the text (e.g.
   * SwingUtilities.TRAILING or SwingUtilities.LEFT)
   */
  public CloseableTabbedPane(DesktopView dview, int tabPosition, int tabLayout) {
    super(tabPosition, tabLayout);
    this.desktopView = dview;
    
	this.xutils = XmlUtils.getInstance();

	init(SwingUtilities.LEFT);

  }

  /**
   * Initializes the <code>CloseableTabbedPane</code>
   * @param horizontalTextPosition the horizontal position of the text (e.g.
   * SwingUtilities.TRAILING or SwingUtilities.LEFT)
   */
  private void init(int horizontalTextPosition) {
    listenerList = new EventListenerList();
    addMouseListener(this);
    addMouseMotionListener(this);

    this.setBorder(BorderFactory.createEmptyBorder());
 // Register a change listener

    initDisplay();
    
    if (getUI() instanceof MetalTabbedPaneUI) {
        System.out.println("metal= ");
        
  //    setUI(new CloseableMetalTabbedPaneUI(horizontalTextPosition));
        setUI(new CloseableTabbedPaneUI(horizontalTextPosition));
    } else {
        System.out.println("close= ");
        
      setUI(new CloseableTabbedPaneUI(horizontalTextPosition));
    }
  }
  
  public void initDisplay() {

	 URL u = xutils.getResource("TAB_CLOSE_IMAGE");

	// URL u = this.getClass().getResource("images\\tab-close.gif");

    // System.out.println("tab close = " + u);
    
	  extraIcon = new ImageIcon(u);
	  
	  unselFillBrightC = new Color(111,234,24); 
	  unselFillDarkC = new Color(11,157,237);
	  selFillC =  new Color(135,141,216);
	  focusFillBrightC = new Color(116,214,235);
	  focusFillDarkC = new Color(117,140,234); 
	  txtC = Color.BLACK;
	  borderC = Color.BLACK;
	  bottomBorderC = Color.BLACK; 
	  selBorderC = Color.BLACK;
	  
		  
	  
  }
 
  /**
   * Allows setting own closeicons.
   * @param normal the normal closeicon
   * @param hoover the closeicon when the mouse is over
   * @param pressed the closeicon when the mouse is pressed
   */
  public void setCloseIcons(Icon normal, Icon hoover, Icon pressed) {
    normalCloseIcon = normal;
    hooverCloseIcon = hoover;
    pressedCloseIcon = pressed;
  }

  public JInternalFrame [] getAllFramesInLayer() {

      int tc = this.getTabCount();
      JInternalFrame [] jc = new JInternalFrame[tc];
      
  //OV c 02/01/2010
  //    for(int i=0;i<tc;i++) {

  //        jc[i] = (JInternalFrame) this.getTabComponentAt(i);

  //    }

      return jc;

  }

   public void removeTabAt(int index) {
       super.removeTabAt(index);

//       System.out.println("RTA index =  " + index);

   }

    

  /**
   * Adds a <code>Component</code> represented by a title and no icon.
   * @param title the title to be displayed in this tab
   * @param component the component to be displayed when this tab is clicked
   */
  public void addTab(String title, Component component) {
    addTab(title, component, null);
  }
 
  /**
   * Adds a <code>Component</code> represented by a title and an icon.
   * @param title the title to be displayed in this tab
   * @param component the component to be displayed when this tab is clicked
   * @param extraIcon the icon to be displayed in this tab
   */
  public void addTab(String title, Component component, Icon extraIcon) {
    boolean doPaintCloseIcon = true;
    try {
      
   // 	Object prop = null;
   // 	if ((prop = ((JComponent) component).
    //                getClientProperty("isClosable")) != null) {
     //   doPaintCloseIcon = ((Boolean) prop).booleanValue();
    //  }
      
    } catch (Exception ignored) {/*Could probably be a ClassCastException*/}
 
    super.addTab(title,
                 doPaintCloseIcon ? new CloseTabIcon(extraIcon) : null,
                 component);
 
    if (headerViewport == null) {
    	
      Component [] cl = getComponents();	
      for (int i=0;i< cl.length;i++) {
     // for (Component c : getComponents()) {
    	  Component c = cl[i];
        if ("TabbedPane.scrollableViewport".equals(c.getName()))
          headerViewport = (JViewport) c;
      }
    }
  }
 
  /**
   * Invoked when the mouse button has been clicked (pressed and released) on
   * a component.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseClicked(MouseEvent e) {
    processMouseEvents(e);
  }
 
  /**
   * Invoked when the mouse enters a component.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseEntered(MouseEvent e) { }
 
  /**
   * Invoked when the mouse exits a component.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseExited(MouseEvent e) {
    for (int i=0; i<getTabCount(); i++) {
      CloseTabIcon icon = (CloseTabIcon) getIconAt(i);
      if (icon != null)
        icon.mouseover = false;
    }
    repaint();
  }
 
  /**
   * Invoked when a mouse button has been pressed on a component.
   * @param e the <code>MouseEvent</code>
   */
  public void mousePressed(MouseEvent e) {
    processMouseEvents(e);
  }
 
  /**
   * Invoked when a mouse button has been released on a component.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseReleased(MouseEvent e) { }
 
  /**
   * Invoked when a mouse button is pressed on a component and then dragged.
   * <code>MOUSE_DRAGGED</code> events will continue to be delivered to the
   * component where the drag originated until the mouse button is released
   * (regardless of whether the mouse position is within the bounds of the
   * component).<br/>
   * <br/>
   * Due to platform-dependent Drag&Drop implementations,
   * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
   * Drag&amp;Drop operation.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseDragged(MouseEvent e) {
    processMouseEvents(e);
  }
 
  /**
   * Invoked when the mouse cursor has been moved onto a component but no
   * buttons have been pushed.
   * @param e the <code>MouseEvent</code>
   */
  public void mouseMoved(MouseEvent e) {
    processMouseEvents(e);
  }
 
  /**
   * Processes all caught <code>MouseEvent</code>s.
   * @param e the <code>MouseEvent</code>
   */
  private void processMouseEvents(MouseEvent e) {
    int tabNumber = getUI().tabForCoordinate(this, e.getX(), e.getY());
    if (tabNumber < 0) return;
    CloseTabIcon icon = (CloseTabIcon) getIconAt(tabNumber);
    if (icon != null) {
      Rectangle rect= icon.getBounds();
      Point pos = headerViewport == null ?
                  new Point() : headerViewport.getViewPosition();
      Rectangle drawRect = new Rectangle(
        rect.x - pos.x, rect.y - pos.y, rect.width, rect.height);
 
      if (e.getID() == e.MOUSE_PRESSED) {
        icon.mousepressed = e.getModifiers() == e.BUTTON1_MASK;
        repaint(drawRect);
      } else if (e.getID() == e.MOUSE_MOVED || e.getID() == e.MOUSE_DRAGGED ||
                 e.getID() == e.MOUSE_CLICKED) {
        pos.x += e.getX();
        pos.y += e.getY();
        if (rect.contains(pos)) {
          if (e.getID() == e.MOUSE_CLICKED) {
            int selIndex = getSelectedIndex();
            if (fireCloseTab(selIndex)) {
              if (selIndex > 0) {
                // to prevent uncatchable null-pointers
                Rectangle rec = getUI().getTabBounds(this, selIndex - 1);
 
                MouseEvent event = new MouseEvent((Component) e.getSource(),
                                                  e.getID() + 1,
                                                  System.currentTimeMillis(),
                                                  e.getModifiers(),
                                                  rec.x,
                                                  rec.y,
                                                  e.getClickCount(),
                                                  e.isPopupTrigger(),
                                                  e.getButton());
                dispatchEvent(event);
              }
              //the tab is being closed
              //removeTabAt(tabNumber);

            // OV commented 270409 
           //   remove(selIndex);




            } else {
              icon.mouseover = false;
              icon.mousepressed = false;
              repaint(drawRect);
            }
          } else {
            icon.mouseover = true;
            icon.mousepressed = e.getModifiers() == e.BUTTON1_MASK;
          }
        } else {
          icon.mouseover = false;
        }
        repaint(drawRect);
      }
    }
  }
 
  /**
   * Adds an <code>CloseableTabbedPaneListener</code> to the tabbedpane.
   * @param l the <code>CloseableTabbedPaneListener</code> to be added
   */
  public void addCloseableTabbedPaneListener(CloseableTabbedPaneListener l) {
    listenerList.add(CloseableTabbedPaneListener.class, l);
  }
 
  /**
   * Removes an <code>CloseableTabbedPaneListener</code> from the tabbedpane.
   * @param l the listener to be removed
   */
  public void removeCloseableTabbedPaneListener(CloseableTabbedPaneListener l) {
    listenerList.remove(CloseableTabbedPaneListener.class, l);
  }
 
  /**
   * Returns an array of all the <code>SearchListener</code>s added to this
   * <code>SearchPane</code> with addSearchListener().
   * @return all of the <code>SearchListener</code>s added or an empty array if
   * no listeners have been added
   */
  public CloseableTabbedPaneListener[] getCloseableTabbedPaneListener() {
    return (CloseableTabbedPaneListener[]) listenerList.getListeners(CloseableTabbedPaneListener.class);
  }
 
  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type.
   * @param tabIndexToClose the index of the tab which should be closed
   * @return true if the tab can be closed, false otherwise
   */
  protected boolean fireCloseTab(int tabIndexToClose) {
    boolean closeit = true;
    // Guaranteed to return a non-null array
    Object[] listeners = listenerList.getListenerList();
    
    for(int i=0;i<listeners.length;i++) {
//    for (Object i : listeners) {
    	Object lnr = listeners[i];
    	if (lnr instanceof CloseableTabbedPaneListener) {
        if (!((CloseableTabbedPaneListener) lnr).closeTab(tabIndexToClose)) {
          closeit = false;
          break;
        }
      }
    }
    return closeit;
  }


 
  /**
   * The class which generates the 'X' icon for the tabs. The constructor
   * accepts an icon which is extra to the 'X' icon, so you can have tabs
   * like in JBuilder. This value is null if no extra icon is required.
   */
  class CloseTabIcon implements Icon {
    /**
     * the x position of the icon
     */
    private int x_pos;
 
    /**
     * the y position of the icon
     */
    private int y_pos;
 
    /**
     * the width the icon
     */
    private int width;
 
    /**
     * the height the icon
     */
    private int height;
 
    /**
     * the additional fileicon
     */
    private Icon fileIcon;
 
    /**
     * true whether the mouse is over this icon, false otherwise
     */
    private boolean mouseover = false;
 
    /**
     * true whether the mouse is pressed on this icon, false otherwise
     */
    private boolean mousepressed = false;
 
    /**
     * Creates a new instance of <code>CloseTabIcon</code>
     * @param fileIcon the additional fileicon, if there is one set
     */
    public CloseTabIcon(Icon fileIcon) {
      this.fileIcon = fileIcon;
      width  = 16;
      height = 16;
    }
 
    /**
     * Draw the icon at the specified location. Icon implementations may use the
     * Component argument to get properties useful for painting, e.g. the
     * foreground or background color.
     * @param c the component which the icon belongs to
     * @param g the graphic object to draw on
     * @param x the upper left point of the icon in the x direction
     * @param y the upper left point of the icon in the y direction
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
      boolean doPaintCloseIcon = true;
   
//   	System.out.println(" in PI ");
	
      try {
        // JComponent.putClientProperty("isClosable", new Boolean(false));
        JTabbedPane tabbedpane = (JTabbedPane) c;
        int tabNumber = tabbedpane.getUI().tabForCoordinate(tabbedpane, x, y);
        JComponent curPanel = (JComponent) tabbedpane.getComponentAt(tabNumber);
       
      //  Object prop = null;
      //  if ((prop = curPanel.getClientProperty("isClosable")) != null) {
      //    doPaintCloseIcon = ((Boolean) prop).booleanValue();
      //  }
        
      } catch (Exception ignored) {/*Could probably be a ClassCastException*/}
      if (doPaintCloseIcon) {
        x_pos = x;
        y_pos = y;
        int y_p = y + 1;

     //  	System.out.println(" in PI true ");

        
        if (normalCloseIcon != null && !mouseover) {

       //    	System.out.println(" in PI true a ");

        	normalCloseIcon.paintIcon(c, g, x, y_p);
        } else if (hooverCloseIcon != null && mouseover && !mousepressed) {
        	
       //    	System.out.println(" in PI true b ");

          hooverCloseIcon.paintIcon(c, g, x, y_p);
        } else if (pressedCloseIcon != null && mousepressed) {
        	
       //    	System.out.println(" in PI true c ");

          pressedCloseIcon.paintIcon(c, g, x, y_p);
        } else {
        	
      //     	System.out.println(" in PI true d ");

          y_p++;
 
          Color col = g.getColor();
 
          if (mousepressed && mouseover) {
            g.setColor(Color.WHITE);
            g.fillRect(x+1, y_p, 12, 13);
          }
 
          g.setColor(Color.black);
          g.drawLine(x+1, y_p, x+12, y_p);
          g.drawLine(x+1, y_p+13, x+12, y_p+13);
          g.drawLine(x, y_p+1, x, y_p+12);
          g.drawLine(x+13, y_p+1, x+13, y_p+12);
          g.drawLine(x+3, y_p+3, x+10, y_p+10);
          if (mouseover)
            g.setColor(Color.GRAY);
          g.drawLine(x+3, y_p+4, x+9, y_p+10);
          g.drawLine(x+4, y_p+3, x+10, y_p+9);
          g.drawLine(x+10, y_p+3, x+3, y_p+10);
          g.drawLine(x+10, y_p+4, x+4, y_p+10);
          g.drawLine(x+9, y_p+3, x+3, y_p+9);
          g.setColor(col);
          if (fileIcon != null) {
            fileIcon.paintIcon(c, g, x+width, y_p);
          }
        }
      }
    }
 
    /**
     * Returns the icon's width.
     * @return an int specifying the fixed width of the icon.
     */
    public int getIconWidth() {
      return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
    }
 
    /**
     * Returns the icon's height.
     * @return an int specifying the fixed height of the icon.
     */
    public int getIconHeight() {
      return height;
    }
 
    /**
     * Gets the bounds of this icon in the form of a <code>Rectangle<code>
     * object. The bounds specify this icon's width, height, and location
     * relative to its parent.
     * @return a rectangle indicating this icon's bounds
     */
    public Rectangle getBounds() {
      return new Rectangle(x_pos, y_pos, width, height);
    }
  }
 
  /**
   * A specific <code>BasicTabbedPaneUI</code>.
   */
  class CloseableTabbedPaneUI extends BasicTabbedPaneUI {
 
   /**
    * the horizontal position of the text
    */
    private int horizontalTextPosition = SwingUtilities.LEFT;
 
    /**
     * Creates a new instance of <code>CloseableTabbedPaneUI</code>
     */
    public CloseableTabbedPaneUI() {
    }
 
    /**
     * Creates a new instance of <code>CloseableTabbedPaneUI</code>
     * @param horizontalTextPosition the horizontal position of the text (e.g.
     * SwingUtilities.TRAILING or SwingUtilities.LEFT)
     */
    public CloseableTabbedPaneUI(int horizontalTextPosition) {
      this.horizontalTextPosition = horizontalTextPosition;
    }
 
    /**
     * Layouts the label
     * @param tabPlacement the placement of the tabs
     * @param metrics the font metrics
     * @param tabIndex the index of the tab
     * @param title the title of the tab
     * @param icon the icon of the tab
     * @param tabRect the tab boundaries
     * @param iconRect the icon boundaries
     * @param textRect the text boundaries
     * @param isSelected true whether the tab is selected, false otherwise
     */
    protected void layoutLabel(int tabPlacement, FontMetrics metrics,
                               int tabIndex, String title, Icon icon,
                               Rectangle tabRect, Rectangle iconRect,
                               Rectangle textRect, boolean isSelected) {
 
      textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
   	
    //  System.out.println(" in LLB tabid = " + tabIndex);
	
      javax.swing.text.View v = getTextViewForTab(tabIndex);
      if (v != null) {
        tabPane.putClientProperty("html", v);
      }
 
      SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                                         metrics, title, icon,
                                         SwingUtilities.CENTER,
                                         SwingUtilities.CENTER,
                                         SwingUtilities.CENTER,
                                         //SwingUtilities.TRAILING,
                                         horizontalTextPosition,
                                         tabRect,
                                         iconRect,
                                         textRect,
                                         textIconGap + 2);
 
      tabPane.putClientProperty("html", null);
      
      int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
      int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
      iconRect.x += xNudge;
      iconRect.y += yNudge;
      textRect.x += xNudge;
      textRect.y += yNudge;
    }


    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y,
                                       int width, int height, boolean isSelected) {
    	// shrink rectangle - don't affect border and tab header
    	
   // 	System.out.println(" in PTB ");

             y += 3;
	         width -= 1;
	         height -= 4;
	         // background body, colored according to state
	     	
	//         System.out.println(" tab index ptb = " + tabIndex);
	    	
	         
	         boolean selected = isSelected(tabIndex);

	 //        System.out.println(" tab index sel = " + selected);

       //      boolean focused = selected && isActive();
	   //      boolean attention = isAttention(index);
	     //    if (focused) {
	     //        ColorUtil.xpFillRectGradient((Graphics2D) g, x, y, width, height,
	     //                                     focusFillBrightC, focusFillDarkC);
	     //    } else 
	         
	         
	              switch(tabPlacement) {
	                case LEFT:
	 //                   g.fillRect(x+1, y+1, width-2, h-3);
	                    break;

	                    
	                case RIGHT:
	   //                 g.fillRect(x, y+1, w-2, h-3);
	                    break;
	                
	                case BOTTOM:
	     //               g.fillRect(x+1, y, w-3, h-1);
	                    break;
	                
	                case TOP:
	                default:
	 //                   g.fillRect(x+1, y+1, w-3, h-1);
	       	      
	                	if (selected) {
	       		     //    System.out.println(" sel fill = " + selFillC);
	    	             ColorUtil.xpFillRectGradient((Graphics2D) g, x, y, width, height,
	    	                                          unselFillBrightC, unselFillDarkC);
	    	             
                   
               // OV c 300409
	       	   //     	 g.setColor(selFillC);
	    	   //          g.fillRect(x, y, width, height);
	    	             //, a, b); 
	    	         } else {
	    	        	 
	       		//         System.out.println(" sel fill not = " + unselFillBrightC);

	       		//         System.out.println(" sel fill not = " + unselFillDarkC);


                           ColorUtil.xpFillRectGradient((Graphics2D) g, x, y, width, height,
	    	                                          selFillC, selFillC);


                      //    g.setColor(tabPane.getBackgroundAt(tabIndex));
                      //    g.fillRect(x, y, width, height);

	    	         }
           
	              }

	      
   
	     }

/*
    // overrrodden paint methods
    
    /**
     * Paints the tabs in the tab area.
     * Invoked by paint().
     * The graphics parameter must be a valid <code>Graphics</code>
     * object.  Tab placement may be either: 
     * <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
     * The selected index must be a valid tabbed pane tab index (0 to
     * tab count - 1, inclusive) or -1 if no tab is currently selected.
     * The handling of invalid parameters is unspecified.
     *
     * @param g the graphics object to use for rendering
     * @param tabPlacement the placement for the tabs within the JTabbedPane
     * @param selectedIndex the tab index of the selected component
     *
     * @since 1.4
     */
  
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {

        int tabCount = tabPane.getTabCount();

        Rectangle iconRect = new Rectangle(),
                  textRect = new Rectangle();

        Rectangle clipRect = g.getClipBounds();

        // Paint tabRuns of tabs from back to front
        for (int i = runCount - 1; i >= 0; i--) {
            int start = tabRuns[i];
            int next = tabRuns[(i == runCount - 1)? 0 : i + 1];
            int end = (next != 0? next - 1: tabCount - 1);
            for (int j = start; j <= end; j++) {
                if (rects[j].intersects(clipRect)) {

          //        System.out.println(" calling painttab j = " + j);

                    paintTab(g, tabPlacement, rects, j, iconRect, textRect);
                }
            }
        }

        // Paint selected tab if its in the front run
        // since it may overlap other tabs
        if (selectedIndex >= 0 && getRunForTab(tabCount, selectedIndex) == 0) {
            if (rects[selectedIndex].intersects(clipRect)) {

     //               System.out.println(" calling painttab 2 selid = " + selectedIndex);


                paintTab(g, tabPlacement, rects, selectedIndex, iconRect, textRect);
            }
        }
    }

    protected void paintTab(Graphics g, int tabPlacement,
                            Rectangle[] rects, int tabIndex, 
                            Rectangle iconRect, Rectangle textRect) {

        Rectangle tabRect = rects[tabIndex];
        int selectedIndex = tabPane.getSelectedIndex();
        boolean isSelected = selectedIndex == tabIndex;

        Graphics2D g2 = null;
        Polygon cropShape = null;

        Shape save = null;
        int cropx = 0;
        int cropy = 0;

     //   System.out.println(" in PT calling painttabback tabIndex = " + tabIndex);

        paintTabBackground(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, 
                           tabRect.width, tabRect.height, isSelected);

        paintTabBorder(g, tabPlacement, tabIndex, tabRect.x, tabRect.y, 
                       tabRect.width, tabRect.height, isSelected);
        
        String title = tabPane.getTitleAt(tabIndex);
        Font font = tabPane.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        Icon icon = getIconForTab(tabIndex);

        layoutLabel(tabPlacement, metrics, tabIndex, title, icon, 
                    tabRect, iconRect, textRect, isSelected);

        paintText(g, tabPlacement, font, metrics, 
                  tabIndex, title, textRect, isSelected);

        paintIcon(g, tabPlacement, tabIndex, icon, iconRect, isSelected);

        paintFocusIndicator(g, tabPlacement, rects, tabIndex, 
                  iconRect, textRect, isSelected);

    }


    
   
    
  }
  
  
  protected final boolean isSelected(int index) {
	  return getSelectedIndex() == index;
  }

 
  /**
   * A specific <code>MetalTabbedPaneUI</code>.
   */
  class CloseableMetalTabbedPaneUI extends MetalTabbedPaneUI {
 
   /**
    * the horizontal position of the text
    */
    private int horizontalTextPosition = SwingUtilities.LEFT;
 
    /**
     * Creates a new instance of <code>CloseableMetalTabbedPaneUI</code>
     */
    public CloseableMetalTabbedPaneUI() {
    }
 
    /**
     * Creates a new instance of <code>CloseableMetalTabbedPaneUI</code>
     * @param horizontalTextPosition the horizontal position of the text (e.g.
     * SwingUtilities.TRAILING or SwingUtilities.LEFT)
     */
    public CloseableMetalTabbedPaneUI(int horizontalTextPosition) {
      this.horizontalTextPosition = horizontalTextPosition;
    }
 
    /**
     * Layouts the label
     * @param tabPlacement the placement of the tabs
     * @param metrics the font metrics
     * @param tabIndex the index of the tab
     * @param title the title of the tab
     * @param icon the icon of the tab
     * @param tabRect the tab boundaries
     * @param iconRect the icon boundaries
     * @param textRect the text boundaries
     * @param isSelected true whether the tab is selected, false otherwise
     */
    protected void layoutLabel(int tabPlacement, FontMetrics metrics,
                               int tabIndex, String title, Icon icon,
                               Rectangle tabRect, Rectangle iconRect,
                               Rectangle textRect, boolean isSelected) {
 
      textRect.x = textRect.y = iconRect.x = iconRect.y = 0;
 
      javax.swing.text.View v = getTextViewForTab(tabIndex);
      if (v != null) {
        tabPane.putClientProperty("html", v);
      }
 
      SwingUtilities.layoutCompoundLabel((JComponent) tabPane,
                                         metrics, title, icon,
                                         SwingUtilities.CENTER,
                                         SwingUtilities.CENTER,
                                         SwingUtilities.CENTER,
                                         //SwingUtilities.TRAILING,
                                         horizontalTextPosition,
                                         tabRect,
                                         iconRect,
                                         textRect,
                                         textIconGap + 2);
 
      tabPane.putClientProperty("html", null);
      
      int xNudge = getTabLabelShiftX(tabPlacement, tabIndex, isSelected);
      int yNudge = getTabLabelShiftY(tabPlacement, tabIndex, isSelected);
      iconRect.x += xNudge;
      iconRect.y += yNudge;
      textRect.x += xNudge;
      textRect.y += yNudge;
    }
  }
  
  	public static void main(String[] args)
   
  	{
   
  		CloseableTabbedPane pane = new CloseableTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
  	//	CloseableTabbedPane pane = new CloseableTabbedPane();
  		
        try {
            
                
   //             PlasticLookAndFeel.setMyCurrentTheme(new DesertBlue());
                
             //  UIManager.setLookAndFeel(new com.jgoodies.looks.windows.WindowsLookAndFeel());
             //   UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
                
              //  SwingUtilities.updateComponentTreeUI(pane);
                
            } catch(Exception e) {
                System.out.println("lookAndFeel= ");
            
            }

  		
   
  		pane.add("Panel 1",new JLabel("Content of Panel 1"));
   
  		pane.add("Panel 2",new JLabel("Content of Panel 2"));
   
  		pane.add("Panel 3",new JLabel("Content of Panel 3"));
   
  		pane.add("Panel 4",new JLabel("Content of Panel 4"));

  		
  		for(int i=0;i<15;i++) {
  			pane.add("Panel "+i, new JLabel("Content of Panel " + i));
  		}
  		
  		
  		
  		JFrame frame = new JFrame();
   
  		frame.getContentPane().add(pane);
   
  		frame.setSize(400,100);
   
  		frame.setVisible(true);
   
  		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   
  	}

	public DesktopView getDesktopView() {
		return desktopView;
	}

	public void setDesktopView(DesktopView desktopView) {
		this.desktopView = desktopView;
	}
   
   
}
