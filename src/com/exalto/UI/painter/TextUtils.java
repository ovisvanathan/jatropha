package com.exalto.UI.painter;

import java.awt.Shape;

import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.CompositeView;
import javax.swing.text.Position;
import javax.swing.text.View;

public class TextUtils {

    /**
     * Provides a way to determine the next visually represented model 
     * location that one might place a caret.  Some views may not be visible,
     * they might not be in the same order found in the model, or they just
     * might not allow access to some of the locations in the model.
     * <p>
     * This implementation assumes the views are layed out in a logical
     * manner. That is, that the view at index x + 1 is visually after
     * the View at index x, and that the View at index x - 1 is visually
     * before the View at x. There is support for reversing this behavior
     * only if the passed in <code>View</code> is an instance of
     * <code>CompositeView</code>. The <code>CompositeView</code>
     * must then override the <code>flipEastAndWestAtEnds</code> method.
     *
     * @param v View to query
     * @param pos the position to convert >= 0
     * @param a the allocated region to render into
     * @param direction the direction from the current position that can
     *  be thought of as the arrow keys typically found on a keyboard;
     *  this may be one of the following: 
     *  <ul>
     *  <li><code>SwingConstants.WEST</code>
     *  <li><code>SwingConstants.EAST</code> 
     *  <li><code>SwingConstants.NORTH</code>
     *  <li><code>SwingConstants.SOUTH</code>  
     *  </ul>
     * @param biasRet an array contain the bias that was checked
     * @return the location within the model that best represents the next
     *  location visual position
     * @exception BadLocationException
     * @exception IllegalArgumentException if <code>direction</code> is invalid
     */
    public static int getNextVisualPositionFrom(View v, int pos, Position.Bias b,
                                          Shape alloc, int direction,
                                          Position.Bias[] biasRet)
                             throws BadLocationException {
        if (v.getViewCount() == 0) {
            // Nothing to do.
            return pos;
        }
        boolean top = (direction == SwingConstants.NORTH ||
                       direction == SwingConstants.WEST);
        int retValue;
        if (pos == -1) {
            // Start from the first View.
            int childIndex = (top) ? v.getViewCount() - 1 : 0;
            View child = v.getView(childIndex);
            Shape childBounds = v.getChildAllocation(childIndex, alloc);
            retValue = child.getNextVisualPositionFrom(pos, b, childBounds,
                                                       direction, biasRet);
	    if (retValue == -1 && !top && v.getViewCount() > 1) {
		// Special case that should ONLY happen if first view
		// isn't valid (can happen when end position is put at
		// beginning of line.
		child = v.getView(1);
                childBounds = v.getChildAllocation(1, alloc);
		retValue = child.getNextVisualPositionFrom(-1, biasRet[0],
                                                           childBounds,
                                                           direction, biasRet);
	    }
        }
        else {
            int increment = (top) ? -1 : 1;
            int childIndex;
            if (b == Position.Bias.Backward && pos > 0) {
                childIndex = v.getViewIndex(pos - 1, Position.Bias.Forward);
            }
            else {
                childIndex = v.getViewIndex(pos, Position.Bias.Forward);
            }
            View child = v.getView(childIndex);
            Shape childBounds = v.getChildAllocation(childIndex, alloc);
            retValue = child.getNextVisualPositionFrom(pos, b, childBounds,
                                                       direction, biasRet);
            
            
            
            if ((direction == SwingConstants.EAST ||
                 direction == SwingConstants.WEST) &&
                (v instanceof CompositeView) &&
                flipEastAndWestAtEnds(pos, b)) {
                increment *= -1;
            }
            childIndex += increment;
            if (retValue == -1 && childIndex >= 0 &&
                                  childIndex < v.getViewCount()) {
                child = v.getView(childIndex);
                childBounds = v.getChildAllocation(childIndex, alloc);
                retValue = child.getNextVisualPositionFrom(
                                     -1, b, childBounds, direction, biasRet);
                // If there is a bias change, it is a fake position
                // and we should skip it. This is usually the result
                // of two elements side be side flowing the same way.
                if (retValue == pos && biasRet[0] != b) {
                    return getNextVisualPositionFrom(v, pos, biasRet[0],
                                                     alloc, direction,
                                                     biasRet);
                }
            }
            else if (retValue != -1 && biasRet[0] != b &&
                     ((increment == 1 && child.getEndOffset() == retValue) ||
                      (increment == -1 &&
                       child.getStartOffset() == retValue)) &&
                     childIndex >= 0 && childIndex < v.getViewCount()) {
                // Reached the end of a view, make sure the next view
                // is a different direction.
                child = v.getView(childIndex);
                childBounds = v.getChildAllocation(childIndex, alloc);
                Position.Bias originalBias = biasRet[0];
                int nextPos = child.getNextVisualPositionFrom(
                                    -1, b, childBounds, direction, biasRet);
                if (biasRet[0] == b) {
                    retValue = nextPos;
                }
                else {
                    biasRet[0] = originalBias;
                }
            }
        }
        
        System.out.println(" retval = " + retValue);
        return retValue;
    }

    
    /**
     * Determines in which direction the next view lays.
     * Consider the <code>View</code> at index n. Typically the
     * <code>View</code>s are layed out from left to right,
     * so that the <code>View</code> to the EAST will be
     * at index n + 1, and the <code>View</code> to the WEST
     * will be at index n - 1. In certain situations,
     * such as with bidirectional text, it is possible
     * that the <code>View</code> to EAST is not at index n + 1,
     * but rather at index n - 1, or that the <code>View</code>
     * to the WEST is not at index n - 1, but index n + 1.
     * In this case this method would return true, indicating the 
     * <code>View</code>s are layed out in descending order.
     * <p>
     * This unconditionally returns false, subclasses should override this
     * method if there is the possibility for laying <code>View</code>s in
     * descending order.
     *
     * @param position position into the model
     * @param bias either <code>Position.Bias.Forward</code> or
     *          <code>Position.Bias.Backward</code>
     * @return false
     */
    protected static boolean flipEastAndWestAtEnds(int position,
					    Position.Bias bias) {
	return false;
    }

	
}
