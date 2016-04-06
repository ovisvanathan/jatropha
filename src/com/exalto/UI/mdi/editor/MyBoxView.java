package com.exalto.UI.mdi.editor;

import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.Position;

import com.exalto.UI.painter.TextUtils;

public class MyBoxView extends BoxView {
	
	public MyBoxView(Element e) {
		super(e, BoxView.Y_AXIS);
		
	}

	public MyBoxView(Element e, int direc) {
		super(e, direc);
		
	}

    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a, 
			 int direction, Position.Bias[] biasRet)  throws BadLocationException {
   	

		   Rectangle alloc = getInsideAllocation(a);
		
		switch (direction) {
		case NORTH:
		   return getNextNorthSouthVisualPositionFrom(pos, b, a, direction,
							       biasRet);
		case SOUTH:
		   return getNextNorthSouthVisualPositionFrom(pos, b, a, direction,
							       biasRet);
		case EAST:
		   return getNextEastWestVisualPositionFrom(pos, b, a, direction,
							     biasRet);
		case WEST:
		   return getNextEastWestVisualPositionFrom(pos, b, a, direction,
							     biasRet);
		default:
		   throw new IllegalArgumentException("Bad direction: " + direction);
		}
   }
   
   /**
    * Returns the next visual position for the cursor, in either the
    * north or south direction.
    *
    * @param pos the position to convert >= 0
    * @param b a bias value of either <code>Position.Bias.Forward</code>
    *  or <code>Position.Bias.Backward</code>
    * @param a the allocated region to render into
    * @param direction the direction from the current position that can
    *  be thought of as the arrow keys typically found on a keyboard;
    *  this may be one of the following:
    *  <ul>
    *  <li><code>SwingConstants.NORTH</code>
    *  <li><code>SwingConstants.SOUTH</code>
    *  </ul>
    * @param biasRet an array containing the bias that was checked
    * @return the location within the model that best represents the next
    *  north or south location 
    * @exception BadLocationException
    * @exception IllegalArgumentException if <code>direction</code> is invalid
    * @see #getNextVisualPositionFrom
    *
    * @return the next position west of the passed in position
    */
   protected int getNextNorthSouthVisualPositionFrom(int pos, Position.Bias b,
						      Shape a, int direction,
						      Position.Bias[] biasRet)
	                                        throws BadLocationException {
       return TextUtils.getNextVisualPositionFrom(
                           this, pos, b, a, direction, biasRet);
   }

   /**
    * Returns the next visual position for the cursor, in either the
    * east or west direction.
    *
   * @param pos the position to convert >= 0
    * @param b a bias value of either <code>Position.Bias.Forward</code>
    *  or <code>Position.Bias.Backward</code>
    * @param a the allocated region to render into
    * @param direction the direction from the current position that can
    *  be thought of as the arrow keys typically found on a keyboard;
    *  this may be one of the following:
    *  <ul>
    *  <li><code>SwingConstants.WEST</code>
    *  <li><code>SwingConstants.EAST</code>
    *  </ul>
    * @param biasRet an array containing the bias that was checked
    * @return the location within the model that best represents the next
    *  west or east location 
    * @exception BadLocationException
    * @exception IllegalArgumentException if <code>direction</code> is invalid
    * @see #getNextVisualPositionFrom
    */
   protected int getNextEastWestVisualPositionFrom(int pos, Position.Bias b,
						    Shape a,
						    int direction,
						    Position.Bias[] biasRet)
	                                        throws BadLocationException {
       return TextUtils.getNextVisualPositionFrom(
                           this, pos, b, a, direction, biasRet);
   }


   /**
    * Fetches the allocation for the given child view. 
    * This enables finding out where various views
    * are located.  This is implemented to return
    * <code>null</code> if the layout is invalid,
    * otherwise the superclass behavior is executed.
    *
    * @param index the index of the child, >= 0 && < getViewCount()
    * @param a  the allocation to this view
    * @return the allocation to the child; or <code>null</code>
    *		if <code>a</code> is <code>null</code>;
    *		or <code>null</code> if the layout is invalid
    */
  /* 
   public Shape getChildAllocation(int index, Shape a) {
	   
	   super.getChildAllocation(index, a);
	   
	if (a != null) {

		Rectangle alloc = getInsideAllocation(a);
		childAllocation(index, alloc);

		Shape ca = a;
	    
	    if ((ca != null) && (! isAllocationValid())) {
		// The child allocation may not have been set yet.
		Rectangle r = (ca instanceof Rectangle) ? 
		    (Rectangle) ca : ca.getBounds();
		if ((r.width == 0) && (r.height == 0)) {
		    return null;
		}
	    }
	    return ca;
	}
	return null;
   }
*/
   /**
    * Translates the immutable allocation given to the view 
    * to a mutable allocation that represents the interior
    * allocation (i.e. the bounds of the given allocation
    * with the top, left, bottom, and right insets removed.
    * It is expected that the returned value would be further
    * mutated to represent an allocation to a child view. 
    * This is implemented to reuse an instance variable so
    * it avoids creating excessive Rectangles.  Typically
    * the result of calling this method would be fed to
    * the <code>childAllocation</code> method.
    *
    * @param a the allocation given to the view
    * @return the allocation that represents the inside of the 
    *   view after the margins have all been removed; if the
    *   given allocation was <code>null</code>,
    *   the return value is <code>null</code>
    */
   /*
   protected Rectangle getInsideAllocation(Shape a) {
	if (a != null) {
	    // get the bounds, hopefully without allocating
	    // a new rectangle.  The Shape argument should 
	    // not be modified... we copy it into the
	    // child allocation.
	    Rectangle alloc;
	    if (a instanceof Rectangle) {
		alloc = (Rectangle) a;
	    } else {
		alloc = a.getBounds();
	    }
	    
	    Rectangle childAlloc = new Rectangle();
	    
	    childAlloc.setBounds(alloc);
	    childAlloc.x += getLeftInset();
	    childAlloc.y += getTopInset();
	    childAlloc.width -= getLeftInset() + getRightInset();
	    childAlloc.height -= getTopInset() + getBottomInset();
	    
	    return childAlloc;
	}
	return null;
   }
*/
   /**
    * Allocates a region for a child view.  
    *
    * @param index the index of the child view to
    *   allocate, >= 0 && < getViewCount()
    * @param alloc the allocated region
    */
   /*
   protected void childAllocation(int index, Rectangle alloc) {
	alloc.x += getOffset(X_AXIS, index);
	alloc.y += getOffset(Y_AXIS, index);
	alloc.width = getSpan(X_AXIS, index);
	alloc.height = getSpan(Y_AXIS, index);

   }
*/


   

}
