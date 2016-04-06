package com.exalto.UI.mdi.editor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.ParagraphView;
import javax.swing.text.Position;
import javax.swing.text.View;

import com.exalto.UI.painter.TextUtils;

public class MyParagraphView extends ParagraphView {

	
	public MyParagraphView(Element e) {
		super(e);
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
     * Returns the next visual position for the cursor, in 
     * either the east or west direction.
     * Overridden from <code>CompositeView</code>.
     * @param pos position into the model
     * @param b either <code>Position.Bias.Forward</code> or
     *          <code>Position.Bias.Backward</code>
     * @param a the allocated region to render into
     * @param direction either <code>SwingConstants.NORTH</code>
     *		or <code>SwingConstants.SOUTH</code>
     * @param biasRet an array containing the bias that were checked
     *	in this method
     * @return the location in the model that represents the
     *	next location visual position
     */
    protected int getNextNorthSouthVisualPositionFrom(int pos, Position.Bias b,
						      Shape a, int direction,
						      Position.Bias[] biasRet)
	                                        throws BadLocationException {
    	
    	System.out.println(" in para view getNextNorthSouthVisualPositionFrom ");

	int vIndex;
	int x;
	try {
		if(pos == -1) {
		    vIndex = (direction == NORTH) ?
			     getViewCount() - 1 : 0;
		}
		else {
		    if(b == Position.Bias.Backward && pos > 0) {
			vIndex = getViewIndexAtPosition(pos - 1);
		    }
		    else {
			vIndex = getViewIndexAtPosition(pos);
		    }
		    if(direction == NORTH) {
			if(vIndex == 0) {
			    return -1;
			}
			vIndex--;
		    }
		    else if(++vIndex >= getViewCount()) {
			return -1;
		    }
		}
		// vIndex gives index of row to look in.
		JTextComponent text = (JTextComponent)getContainer();
		Caret c = text.getCaret();
		Point magicPoint;
		magicPoint = (c != null) ? c.getMagicCaretPosition() : null;
		if(magicPoint == null) {
		    Shape posBounds = text.getUI().modelToView(text, pos, b);
		    if(posBounds == null) {
			x = 0;
		    }
		    else {
			x = posBounds.getBounds().x;
		    }
		}
		else {
		    x = magicPoint.x;
		}
	} catch (RuntimeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		
		
		throw e;
	}
	
	int ret = getClosestPositionTo(pos, b, a, direction, biasRet, vIndex, x); 

		System.out.println(" ret = " + ret);
	return ret;
    }
    
    

    /**
     * Returns the closest model position to <code>x</code>.
     * <code>rowIndex</code> gives the index of the view that corresponds
     * that should be looked in.
     * @param pos  position into the model
     * @param a the allocated region to render into
     * @param direction one of the following values:
     * <ul>
     * <li><code>SwingConstants.NORTH</code>
     * <li><code>SwingConstants.SOUTH</code>
     * </ul>
     * @param biasRet an array containing the bias that were checked
     *	in this method
     * @param rowIndex the index of the view
     * @param x the x coordinate of interest
     * @return the closest model position to <code>x</code>
     */
    // NOTE: This will not properly work if ParagraphView contains
    // other ParagraphViews. It won't raise, but this does not message
    // the children views with getNextVisualPositionFrom.
   
    protected int getClosestPositionTo(int pos, Position.Bias b, Shape a,
				       int direction, Position.Bias[] biasRet,
				       int rowIndex, int x)
	      throws BadLocationException {
	JTextComponent text = (JTextComponent)getContainer();
	Document doc = getDocument();
	AbstractDocument aDoc = (doc instanceof AbstractDocument) ?
	                        (AbstractDocument)doc : null;

	                        
//	Instead of getting view at RowIndex, here
//	we check if getView(0) is a responseView 
//	and if so recurse to the first paragraph view child
//	of response view and call getView(rowIndex) of this paragraph view
	         
             System.out.println(" para code = " + this.hashCode());
             System.out.println(" para start = " + this.getStartOffset());
             System.out.println(" para end = " + this.getEndOffset());
             
             
	   View cv = getView(0);
	   
	   ResponseView rview = null;
	   if(cv != null) {
		   View uv = cv.getView(0);
		   if(uv instanceof ResponseView) 
			   rview = (ResponseView) uv; 
	   }  
	   
	   View pchild = rview.getView(rowIndex);
	
	   View row = pchild.getView(0);
	
//	Rectangle palloc = getInsideAllocation(a);

//	int lastPos = row.getNextVisualPositionFrom(pos, b, palloc, direction, biasRet);

	
	int lastPos = -1;
	// This could be made better to check backward positions too.
	biasRet[0] = Position.Bias.Forward;
	for(int vc = 0, numViews = row.getViewCount(); vc < numViews; vc++) {
	    View v = row.getView(vc);
	    int start = v.getStartOffset();
	    boolean ltr = (aDoc != null) ? isLeftToRight
		           (start, start + 1) : true;
	    if(ltr) {
		lastPos = start;
		for(int end = v.getEndOffset(); lastPos < end; lastPos++) {
		    if(text.modelToView(lastPos).getBounds().x >= x) {
			return lastPos;
		    }
		}
		lastPos--;
	    }
	    else {
		for(lastPos = v.getEndOffset() - 1; lastPos >= start;
		    lastPos--) {
		    if(text.modelToView(lastPos).getBounds().x >= x) {
			return lastPos;
		    }
		}
		lastPos++;
	    }
	}
	
	
	if(lastPos == -1) {
	    return getStartOffset();
	}
	return lastPos;
    }
    
    

    public boolean isLeftToRight(int a, int b) {
    	return true;
    }
}
