/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.exalto.UI;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.DefaultKeyboardFocusManager;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;


/**
 *  Popup manager allows to display an arbitrary popup component
 *  over the underlying text component.
 *
 *  @author  Martin Roskanin, Miloslav Metelka
 *  @since   03/2002
 */
public class PopupManager {
    
    private JComponent popup = null;
    private JTextComponent textComponent; 

    /** Place popup always above cursor */
    public static final Placement Above = new Placement("Above"); //NOI18N
    
    /** Place popup always below cursor */
    public static final Placement Below = new Placement("Below"); //NOI18N
    
    /** Place popup to larger area. i.e. if place below cursor is 
        larger than place above, then popup will be placed below cursor. */
    public static final Placement Largest = new Placement("Largest"); //NOI18N
    
    /** Place popup above cursor. If a place above cursor is insufficient, 
        then popup will be placed below cursor. */
    public static final Placement AbovePreferred = new Placement("AbovePreferred"); //NOI18N
    
    /** Place popup below cursor. If a place below cursor is insufficient, 
        then popup will be placed above cursor. */
    public static final Placement BelowPreferred = new Placement("BelowPreferred"); //NOI18N
    
    /** Place popup inside the scrollbar's viewport */
    public static final HorizontalBounds ViewPortBounds = new HorizontalBounds("ViewPort"); //NOI18N
    
    /** Place popup inside the whole scrollbar */
    public static final HorizontalBounds ScrollBarBounds = new HorizontalBounds("ScrollBar"); //NOI18N
    
    private KeyListener keyListener;
    
    private TextComponentListener componentListener;
    
    /** Creates a new instance of PopupManager */
    public PopupManager(JTextComponent textComponent) {
        this.textComponent = textComponent;
        keyListener = new PopupKeyListener();
  //    textComponent.addKeyListener(keyListener);
        componentListener = new TextComponentListener();
        textComponent.addComponentListener(componentListener);
    }
    
    /** Install popup component to textComponent root pane
     *  based on caret coordinates with the <CODE>Largest</CODE> placement.
     *  Note: Make sure the component is properly uninstalled later,
     *  if it is not necessary. See issue #35325 for details.
     *  @param popup popup component to be installed into
     *  root pane of the text component.
     */
    public void install(JComponent popup) {
        if (textComponent == null) return;
        int caretPos = textComponent.getCaret().getDot();
        try {
            Rectangle caretBounds = textComponent.modelToView(caretPos);
            install(popup, caretBounds, Largest);
        } catch (BadLocationException e) {
            // do not install if the caret position is invalid
        }
    }

    /** Removes popup component from textComponent root pane
     *  @param popup popup component to be removed from
     *  root pane of the text component.
     */
    public void uninstall(JComponent popup){

		System.out.println( " inside popup UNINSTALL UNINSTALL uninstall ");	
	
        if (this.popup != null){
		
			System.out.println( " is popup visible " + this.popup.isVisible());	
            if (this.popup.isVisible()) this.popup.setVisible(false);
			
            removeFromRootPane(this.popup);
        }
        
        if (popup!=this.popup && popup!= null){
            if (popup.isVisible()) popup.setVisible(false);
            removeFromRootPane(popup);
        }
    }

    public void install(JComponent popup, Rectangle cursorBounds,
    Placement placement, HorizontalBounds horizontalBounds, int horizontalAdjustment, int verticalAdjustment){
   
   //		Rectangle bounds = new Rectangle(cursorBounds.x, cursorBounds.y, 80, 100);
   /* Uninstall the old popup from root pane
         * and install the new one. Even in case
         * they are the same objects it's necessary
         * to cover the workspace switches etc.
         */
        if (this.popup != null) {
            // if i.e. completion is visible and tooltip is being installed, 
            // completion popup should be closed.
            if (this.popup.isVisible() && this.popup!=popup) this.popup.setVisible(false);
            removeFromRootPane(this.popup);
        }

		System.out.println( " inside popup install ");	

        this.popup = popup;

		System.out.println( " popup assigned popup = " + popup);	

        if (this.popup != null) {
            installToRootPane(this.popup);
        }
        

        // Update the bounds of the popup
        Rectangle bounds = computeBounds(this.popup, textComponent,
            cursorBounds, placement, horizontalBounds);
			
        if (bounds != null){
            // Convert to layered pane's coordinates
            
            if (horizontalBounds == ScrollBarBounds){
				System.out.println(" in horizontalBounds eq ScrollBarBounds");			
                bounds.x = 0;
            }
			
			
            
            JRootPane rp = textComponent.getRootPane();
            if (rp!=null){
				System.out.println(" in rp not null ");			
                bounds = SwingUtilities.convertRectangle(textComponent, bounds,
                    rp.getLayeredPane());
            }
			
			System.out.println(" Bounds after rootpane ");			
			System.out.println(" bounds x  = " + bounds.x);
			System.out.println(" bounds y = " + bounds.y);

            
            if (horizontalBounds == ScrollBarBounds){
				System.out.println(" again in  horizontalBounds == ScrollBarBounds check");			
			
                if (textComponent.getParent() instanceof JViewport){
                    int shift = textComponent.getParent().getX();
                    Rectangle viewBounds = ((JViewport)textComponent.getParent()).getViewRect();
                    bounds.x += viewBounds.x;
                    bounds.x -= shift;
                    bounds.width += shift;
                }
            }
			
			System.out.println(" Bounds after horizontalBounds eq ScrollBarBounds check");			
			System.out.println(" bounds x  = " + bounds.x);
			System.out.println(" bounds y = " + bounds.y);

			System.out.println(" verticalAdjustment " + verticalAdjustment);


            bounds.x = bounds.x + horizontalAdjustment;
            bounds.y = bounds.y + verticalAdjustment;
            bounds.width = bounds.width - horizontalAdjustment;
            bounds.height = bounds.height - verticalAdjustment;
			
			System.out.println(" bounds x final = " + bounds.x);
			System.out.println(" bounds y final = " + bounds.y);

			System.out.println(" bounds h final = " + bounds.height);
			System.out.println(" bounds w final = " + bounds.width);
	
	
        //    bounds.x = cursorBounds.x + horizontalAdjustment;
        //    bounds.y = cursorBounds.y + verticalAdjustment;
        //    bounds.width = bounds.width - horizontalAdjustment;
        //    bounds.height = bounds.height - verticalAdjustment;
			
			
			if(!popup.isVisible())
				popup.setVisible(true);

            this.popup.setBounds(bounds);

        } else { // can't fit -> hide
		
				System.out.println(" inside else hiding ");

            this.popup.setVisible(false);
        }
		
	
	}
    
    public void install(JComponent popup, Rectangle cursorBounds,
    Placement placement, HorizontalBounds horizontalBounds){
        install(popup, cursorBounds, placement, ViewPortBounds, 1, 1);
    }
    
    
    public void install(JComponent popup, Rectangle cursorBounds,
    Placement placement){
        install(popup, cursorBounds, placement, ViewPortBounds);
    }
    
    /** Returns installed popup panel component */
    public JComponent get(){
        return popup;
    }
    

    /** Install popup panel to current textComponent root pane */
    private void installToRootPane(JComponent c) {
        JRootPane rp = textComponent.getRootPane();
        if (rp != null) {
            rp.getLayeredPane().add(c, JLayeredPane.POPUP_LAYER, 0);
        }
    }

    /** Remove popup panel from previous textComponent root pane */
    private void removeFromRootPane(JComponent c) {
	
	System.out.println( " in popup removeFromRootPane ");	

        JRootPane rp = c.getRootPane();
        if (rp != null) {
            rp.getLayeredPane().remove(c);
        }
    }

    /** Variation of the method for computing the bounds
     * for the concrete view component. As the component can possibly
     * be placed in a scroll pane it's first necessary
     * to translate the cursor bounds and also translate
     * back the resulting popup bounds.
     * @param popup  popup panel to be displayed
     * @param view component over which the popup is displayed.
     * @param cursorBounds the bounds of the caret or mouse cursor
     *    relative to the upper-left corner of the visible view.
     * @param placement where to place the popup panel according to
     *    the cursor position.
     * @return bounds of popup panel relative to the upper-left corner
     *    of the underlying view component.
     *    <CODE>null</CODE> if there is no place to display popup.
     */
    protected static Rectangle computeBounds(JComponent popup,
    JComponent view, Rectangle cursorBounds, Placement placement, HorizontalBounds horizontalBounds) {
        
        if (horizontalBounds == null) horizontalBounds = ViewPortBounds;
        
        Rectangle ret;
        Component viewParent = popup.getParent();

		System.out.println(" popup class= " + popup.getClass());        
		System.out.println(" parenmt component class= " + viewParent.getClass());
		
		
        if (viewParent instanceof JViewport) {
			System.out.println(" in viewport ");
            Rectangle viewBounds = ((JViewport)viewParent).getViewRect();

            Rectangle translatedCursorBounds = (Rectangle)cursorBounds.clone();
            translatedCursorBounds.translate(-viewBounds.x, -viewBounds.y);
						
            ret = computeBounds(popup, viewBounds.width, viewBounds.height,
                translatedCursorBounds, placement, horizontalBounds);
            
            if (ret != null) { // valid bounds
                ret.translate(viewBounds.x, viewBounds.y);
            }
			            
        } else { // not in scroll pane
	//		Dimension d = popup.getMinimumSize();
		// newly added 14082005	
            ret = computeBounds(popup, view.getWidth(), view.getHeight(),
                cursorBounds, placement);
				
	//		ret = computeBounds(popup, d.width, d.height,
    //            cursorBounds, placement);
	
        }
        
        return ret;
    }

    protected static Rectangle computeBounds(JComponent popup,
    JComponent view, Rectangle cursorBounds, Placement placement) {
		System.out.println("############# SHOULD NOT COME HERE ###############");
        return computeBounds(popup, view, cursorBounds, placement, ViewPortBounds);
    }    
	
	// modified added 14082005	
	protected static Rectangle computeBounds(JComponent popup,
	    int viewWidth, int viewHeight, Rectangle cursorBounds, Placement placement) {
	    return computeBounds(popup, viewWidth, viewHeight, cursorBounds, placement, ViewPortBounds);
	}    

    
    /** Computes a best-fit bounds of popup panel
     *  according to available space in the underlying view
     *  (visible part of the pane).
     *  The placement is first evaluated and put into the popup's client property
     *  by <CODE>popup.putClientProperty(Placement.class, actual-placement)</CODE>.
     *  The actual placement is <UL>
     *  <LI> <CODE>Above</CODE> if the original placement was <CODE>Above</CODE>.
     *  Or if the original placement was <CODE>AbovePreferred</CODE>
     *  or <CODE>Largest</CODE>
     *  and there is more space above the cursor than below it.
     *  <LI> <CODE>Below</CODE> if the original placement was <CODE>Below</CODE>.
     *  Or if the original placement was <CODE>BelowPreferred</CODE>
     *  or <CODE>Largest</CODE>
     *  and there is more space below the cursor than above it.
     *  <LI> <CODE>AbovePreferred</CODE> if the original placement
     *  was <CODE>AbovePreferred</CODE>
     *  and there is less space above the cursor than below it.
     *  <LI> <CODE>BelowPreferred</CODE> if the original placement
     *  was <CODE>BelowPreferred</CODE>
     *  and there is less space below the cursor than above it.
     *  <P>Once the placement client property is set
     *  the <CODE>popup.setSize()</CODE> is called with the size of the area
     *  above/below the cursor (indicated by the placement).
     *  The popup responds by updating its size to the equal or smaller
     *  size. If it cannot physically fit into the requested area
     *  it can call
     *  <CODE>putClientProperty(Placement.class, null)</CODE>
     *  on itself to indicate that it cannot fit. The method scans
     *  the content of the client property upon return from
     *  <CODE>popup.setSize()</CODE> and if it finds null there it returns
     *  null bounds in that case. The only exception is
     *  if the placement was either <CODE>AbovePreferred</CODE>
     *  or <CODE>BelowPreferred</CODE>. In that case the method
     *  gives it one more try
     *  by attempting to fit the popup into (bigger) complementary
     *  <CODE>Below</CODE> and <CODE>Above</CODE> areas (respectively).
     *  The popup either fits into these (bigger) areas or it again responds
     *  by returning <CODE>null</CODE> in the client property in which case
     *  the method finally gives up and returns null bounds.
     *   
     *  @param popup popup panel to be displayed
     *  @param viewWidth width of the visible view area.
     *  @param viewHeight height of the visible view area.
     *  @param cursorBounds the bounds of the caret or mouse cursor
     *    relative to the upper-left corner of the visible view
     *  @param placement where to place the popup panel according to
     *    the cursor position
     *  @return bounds of popup panel relative to the upper-left corner
     *    of the underlying view.
     *    <CODE>null</CODE> if there is no place to display popup.
     */
    protected static Rectangle computeBounds(JComponent popup,
    int viewWidth, int viewHeight, Rectangle cursorBounds, Placement placement, HorizontalBounds horizontalBounds) {
        int vwWidth = 0;
		
        if (placement == null) {
            throw new NullPointerException("placement cannot be null"); // NOI18N
        }
        
        // Compute available height above the cursor
        int aboveCursorHeight = cursorBounds.y;
		
        int belowCursorY = cursorBounds.y + cursorBounds.height;
	    int belowCursorHeight = viewHeight - belowCursorY;
		
		// viewWidth = Math.min(viewWidth, 100);
		
		System.out.println(" viewWidth k = " + viewWidth);
		System.out.println(" value of placement = " + placement);		
		        
        // resolve Largest and *Preferred placements if possible
        if (placement == Largest) {
            placement = (aboveCursorHeight < belowCursorHeight)
                ? Below
                : Above;

        } else if (placement == AbovePreferred
            && aboveCursorHeight > belowCursorHeight // more space above
        ) {
            placement = Above;
            
        } else if (placement == BelowPreferred
            && belowCursorHeight > aboveCursorHeight // more space below
        ) {
            placement = Below;
        }

        Rectangle popupBounds = null;
        
        while (true) { // do one or two passes
            popup.putClientProperty(Placement.class, placement);

            int height = (placement == Above || placement == AbovePreferred)
                ? aboveCursorHeight
                : belowCursorHeight;

		
		//	height = Math.min(height, 200);
		
		   System.out.println(" height k = " + height);	
		
     //      popup.setSize(viewWidth, height);
	
	 //      popup.setSize(80, 100);
		
		
		
			Dimension popupMaxSize = popup.getMaximumSize();
	
	
			// popup size setting begin
			Dimension minSizeDefault = new Dimension(4,4);
			Dimension maxSize = new Dimension(40, 60);
			
			Dimension minSize = new Dimension();
			minSize.width = minSizeDefault.width;
			minSize.height = minSizeDefault.height;
			popup.setMinimumSize(minSize);
			        
			Dimension ps = popup.getPreferredSize();
			
			System.out.println(" pref size width = " + ps.width);	
			System.out.println(" pref size height = " + ps.height);	

			// Add size of the vertical scrollbar by default. This could be improved
			// to be done only if the height exceeds the bounds. 
		//	ps.width += scrollBarSize.width;
			ps.width = Math.max(ps.width, minSize.width);
			
		//	ps.height = Math.max(ps.height, minSize.height);
			
			System.out.println(" max size height = " + maxSize.height);	
			
			vwWidth = viewWidth;
			viewWidth = Math.min(viewWidth, maxSize.width);
			height = Math.min(height, maxSize.height);
			
			System.out.println(" height = " + height);	

			ps.height = Math.min(Math.max(ps.height, minSize.height), height);

			System.out.println(" using height = " + ps.height);	

	        popup.setSize(ps.width, ps.height);
			
			viewWidth = ps.width;
			
			popupBounds = popup.getBounds(); 

		
		// popup size setting end
        

		

            Placement updatedPlacement = (Placement)popup.getClientProperty(Placement.class);

            if (updatedPlacement != placement) { // popup does not fit with the orig placement
                if (placement == AbovePreferred && updatedPlacement == null) {
                    placement = Below;
                    continue;
                    
                } else if (placement == BelowPreferred && updatedPlacement == null) {
                    placement = Above;
                    continue;
                }
            }
            
            if (updatedPlacement == null) {
                popupBounds = null;
            }
            
            break;
        }

			System.out.println(" value of placement 2 = " + placement);				
        
        if (popupBounds != null) {
            //place popup according to caret position and Placement
			
			System.out.println(" cursorBounds.x = " + cursorBounds.x);				
			System.out.println(" viewWidth - popupBounds.width = " + (vwWidth - popupBounds.width));				

            popupBounds.x = Math.min(cursorBounds.x, vwWidth - popupBounds.width);
	       // popupBounds.x = cursorBounds.x;
			
			System.out.println("  popupBounds.x ### = " +  popupBounds.x);
	
			if(popupBounds.x == 0) {
				System.out.println("  in eq 0 check ## = ");			
				popupBounds.x = cursorBounds.x;
			}
		
			System.out.println("  popupBounds.x ### = " +  popupBounds.x);
	
	        popupBounds.y = (placement == Above || placement == AbovePreferred)
                ? (aboveCursorHeight - popupBounds.height)
 				: cursorBounds.y;   
        //        : belowCursorY;
				
			System.out.println("  popupBounds.x ARRIVED AT = " +  popupBounds.x);
			System.out.println("  popupBounds.y ARRIVED AT = " +  popupBounds.y);
	
        }

        return popupBounds;
    }

	// newly added 14082005	
	    protected static Rectangle computeBounds_original(JComponent popup,
	    int viewWidth, int viewHeight, Rectangle cursorBounds, Placement placement, HorizontalBounds horizontalBounds) {
	        
	        if (placement == null) {
	            throw new NullPointerException("placement cannot be null"); // NOI18N
	        }
	        
	        // Compute available height above the cursor
	        int aboveCursorHeight = cursorBounds.y;
	        int belowCursorY = cursorBounds.y + cursorBounds.height;
	        int belowCursorHeight = viewHeight - belowCursorY;
	        
	        // resolve Largest and *Preferred placements if possible
	        if (placement == Largest) {
	            placement = (aboveCursorHeight < belowCursorHeight)
	                ? Below
	                : Above;
	
	        } else if (placement == AbovePreferred
	            && aboveCursorHeight > belowCursorHeight // more space above
	        ) {
	            placement = Above;
	            
	        } else if (placement == BelowPreferred
	            && belowCursorHeight > aboveCursorHeight // more space below
	        ) {
	            placement = Below;
	        }
	
	        Rectangle popupBounds = null;
	        
	        while (true) { // do one or two passes
	            popup.putClientProperty(Placement.class, placement);
	
	            int height = (placement == Above || placement == AbovePreferred)
	                ? aboveCursorHeight
	                : belowCursorHeight;
	
	            popup.setSize(viewWidth, height);
	            popupBounds = popup.getBounds();
	
	            Placement updatedPlacement = (Placement)popup.getClientProperty(Placement.class);
	
	            if (updatedPlacement != placement) { // popup does not fit with the orig placement
	                if (placement == AbovePreferred && updatedPlacement == null) {
	                    placement = Below;
	                    continue;
	                    
	                } else if (placement == BelowPreferred && updatedPlacement == null) {
	                    placement = Above;
	                    continue;
	                }
	            }
	            
	            if (updatedPlacement == null) {
	                popupBounds = null;
	            }
	            
	            break;
	        }
	        
	        if (popupBounds != null) {
	            //place popup according to caret position and Placement
	            popupBounds.x = Math.min(cursorBounds.x, viewWidth - popupBounds.width);
	
	            popupBounds.y = (placement == Above || placement == AbovePreferred)
	                ? (aboveCursorHeight - popupBounds.height)
	                : belowCursorY;
	        }
	
	        return popupBounds;
	    }

	
    /** Popup's key filter */
    private class PopupKeyListener implements KeyListener{
        
        public void keyTyped(KeyEvent e){}
        public void keyReleased(KeyEvent e){}
        
        public void keyPressed(KeyEvent e){
		
		    System.out.println(" in pmgr keypressed e =" + e);
		   				
            if (e == null) return;
			
			System.out.println(" in pmgr popup =" + popup);
			
			

			if(popup != null)
				System.out.println(" in pmgr popup.isshowing =" + popup.isShowing());
		   	
            if (popup != null  && popup.isShowing()) {
			
			    System.out.println(" in pmgr popup not null");
			
				KeyStroke [] ksk = popup.getRegisteredKeyStrokes();
				
                System.out.println(" ksk len =" + ksk.length);
				
				for(int i=0;i<ksk.length;i++) {
				
					System.out.println(" ksk =" + ksk[i]);
					KeyStroke kst = ksk[i];
					System.out.println(" kst key =" + ksk[i]);
				}
				
                
                // get popup's registered keyboard actions
                ActionMap am = popup.getActionMap();
                InputMap  im = popup.getInputMap();
				
				KeyStroke [] isk = im.keys();

                System.out.println(" curr focus comp =" + popup.isFocusOwner());
				
                
                // check whether popup registers keystroke
                Object obj = im.get(KeyStroke.getKeyStrokeForEvent(e));
				
				System.out.println(" obj popup =" + obj);
                if (obj!=null){
                    // if yes, gets the popup's action for this keystroke, perform it 
                    // and consume key event
                    Action action = am.get(obj);
                    if (action != null) {
                        action.actionPerformed(null);
                        e.consume();
                    }
                }
            } 
        }

    }
    
    private final class TextComponentListener extends ComponentAdapter {

        public void componentHidden(ComponentEvent evt) {
            install(null); // hide popup
        }

    }
    
    /** Placement of popup panel specification */
    public static final class Placement {
        
        private final String representation;
        
        public Placement(String representation) {
            this.representation = representation;
        }
        
        public String toString() {
            return representation;
        }
        
    }    
    
    /** Horizontal bounds of popup panel specification */
    public static final class HorizontalBounds {
        
        private final String representation;
        
        public HorizontalBounds(String representation) {
            this.representation = representation;
        }
        
        public String toString() {
            return representation;
        }
        
    }    
    
}

