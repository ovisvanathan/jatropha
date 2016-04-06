/*
 * ResponseView.java
 *
 * Created on April 2, 2007, 3:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI.mdi.editor;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.Evaluator;
import org.jdesktop.animation.timing.interpolation.Interpolator;
import org.jdesktop.animation.timing.interpolation.KeyFrames;
import org.jdesktop.animation.timing.interpolation.KeyTimes;
import org.jdesktop.animation.timing.interpolation.KeyValues;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

import com.exalto.UI.painter.TextUtils;

/**
 *
 * @author omprakash.v
 */
    public final class ResponseView extends BoxView {
        private static final int MIN_HEIGHT = 20;
        private int depth;
        private int fontCenter;
        private boolean expanded = true;
        private boolean animating;
        private float height;
        private static final int RESPONSE_INDENT = 11;
        Animator animation = null;
        
        public ResponseView(Element e) {
            super(e, BoxView.Y_AXIS);
        }
        
        public String getSender() {
            Element e = getElement();
            Element parent = e.getParentElement();
            int index = parent.getElementIndex(getStartOffset());
            if (index > 0) {
                Element previousSibling = parent.getElement(index - 1);
                if (AbstractDocument.ParagraphElementName == previousSibling.getName()) {
                    for (int i = 0; i < previousSibling.getElementCount(); i++) {
                        Element childE = previousSibling.getElement(i);
                        if (childE.getAttributes().getAttribute(StyleConstants.Foreground) != null) {
                            try {
                                return getDocument().getText(childE.getStartOffset(),
                                        childE.getEndOffset() - childE.getStartOffset());
                            } catch (BadLocationException ex) {
                            }
                            break;
                        }
                    }
                }
            }
            return "";
        }
        
        public void setParent(View parent) {
            super.setParent(parent);
            depth = 1;
            while (parent != null) {
                if (parent instanceof ResponseView) {
                    depth++;
                }
                parent = parent.getParent();
            }
        }
        
        private int getDepth() {
            return depth;
        }
        
        public void paint(Graphics g, Shape a) {

        	
        	Element e = getElement();
        	
  //      	System.out.println("  paint called for elem e = ");
        	
            if (fontCenter == 0) {
                calcFontHeight();
            }
            Rectangle bounds = (a instanceof Rectangle) ? (Rectangle)a :
                a.getBounds();
            Icon icon;
            if (!isExpanded()) {
                icon = UIManager.getIcon("Tree.collapsedIcon");
            } else {
                icon = UIManager.getIcon("Tree.expandedIcon");
            }
            int xOffset = 0;
            int yOffset = fontCenter - icon.getIconHeight() / 2;
            int lineX = bounds.x + 4;
            int lineY = bounds.y + RESPONSE_INDENT / 2 + yOffset;
            int endY = bounds.y + bounds.height - fontCenter + 2;
            Color lineColor = getColorForDepth(getDepth());
            if (!isExpanded() || animating || getHeight() > MIN_HEIGHT) {
                if (animating || isExpanded()) {
                    g.setColor(lineColor);
                    g.fillRect(lineX, lineY, 2, endY - lineY);
                    g.fillRect(lineX, endY - 2, 6, 2);
                }
                icon.paintIcon(getContainer(), g, bounds.x + xOffset, bounds.y + yOffset);
            } else {
                g.setColor(lineColor);
                g.fillRect(lineX, bounds.y, 2, bounds.height);
            }
            if (height != 0) {
                Graphics g2 = g.create();
                g2.clipRect(bounds.x, bounds.y, bounds.width, (int)height);
                super.paint(g2, a);
                g2.dispose();
            } else {
                super.paint(g, a);
            }
        }
        
        
        
        
        protected short getLeftInset() {
            return RESPONSE_INDENT;
        }
        
        private boolean isExpanded() {
            return expanded;
        }
        
        private void calcFontHeight() {
            Container host = getContainer();
            FontMetrics metrics = host.getFontMetrics(host.getFont());
            fontCenter = metrics.getAscent() / 2 + 3;
        }
        
        
        public void toggleIfNecessary(float x, float y, Shape alloc) {
            Rectangle bounds = alloc.getBounds();
            if ((!isExpanded() || bounds.height > MIN_HEIGHT) &&
                    inClickArea((int)x, (int)y, bounds)) {
                startAnimation();
                expanded = !expanded;
                preferenceChanged(this, true, true);
                getContainer().repaint();
            }
        }
        
        public Point getClickCenter(int x, int y) {
            return new Point(x + 7, y + fontCenter);
        }
        
        private boolean inClickArea(int x, int y, Rectangle bounds) {
            return (x >= bounds.x + 2 && x <= bounds.x + 13 &&
                    y >= bounds.y + fontCenter - 4 &&
                    y <= bounds.y + fontCenter + 5);
        }
        
        public float getPreferredSpan(int axis) {
            if (axis == View.Y_AXIS && (animating || !isExpanded()) &&
                    height != 0) {
                return height;
            }
            return super.getPreferredSpan(axis);
        }
        
        public float getMinimumSpan(int axis) {
            if (axis == View.Y_AXIS && (animating || !isExpanded()) &&
                    height != 0) {
                return height;
            }
            return super.getMinimumSpan(axis);
        }
        
        public float getMaximumSpan(int axis) {
            if (axis == View.Y_AXIS && (animating || !isExpanded()) &&
                    height != 0) {
                return height;
            }
            return super.getMaximumSpan(axis);
        }
        
        private float getCollapsedHeight() {
            return 16f;
        }
        
        public void setSize(float width, float height) {
            super.setSize(width, height);
        }
        
        
        
        private void startAnimation() {
            float start;
            float end;
            if (isExpanded()) {
                start = getPreferredSpan(View.Y_AXIS);
                end = getCollapsedHeight();
            } else {
                start = getCollapsedHeight();
                end = super.getPreferredSpan(View.Y_AXIS);
            }
            animating = true;
        
            // Create the Animator with a PropertySetter as
            // the TimingTarget; this will do the work of setting the property
            // specified above
            
            int duration=220;
            int repeatCount = 0;
            
            int resolution = 20;
      
            int numKeyframes = 2;
            float times[] = new float[numKeyframes];
            Point points[] = new Point[numKeyframes];
            
            Interpolator interpolators[] = null;
          /* 
            if (nonlinearButton.isSelected()) {
                interpolators = new Interpolator[numKeyframes-1];
                for (int i = 0; i < numKeyframes - 1; ++i) {
                    interpolators[i] = new SplineInterpolator(
                            (float)getFieldValueAsDouble(splineX0[i]),
                            (float)getFieldValueAsDouble(splineY0[i]),
                            (float)getFieldValueAsDouble(splineX1[i]),
                            (float)getFieldValueAsDouble(splineY1[i]));
                }
            }
          */  
                times[0] = 0.0f;
                times[1] = 1.0f;
                points[0] = new Point(0,0);
                points[1] = new Point(0, 190);
            
        
            KeyTimes keyTimes = new KeyTimes(times);
            KeyValues keyValues = KeyValues.create(points);
            //KeySplines keySplines = (interpolationType == InterpolationType.NONLINEAR) ?
            //    new KeySplines(splines) : null;
            KeyFrames keyFrames;
         /*   
            if (nonlinearButton.isSelected()) {
                keyFrames = new KeyFrames(keyValues, keyTimes, interpolators);
            } else if (discreteButton.isSelected()) {
                keyFrames = new KeyFrames(keyValues, keyTimes, DiscreteInterpolator.getInstance());
            } else {
         */   
            	keyFrames = new KeyFrames(keyValues, keyTimes);
          //  }
            
         
            
            
            Animator.Direction direction = Animator.Direction.FORWARD;
            
            Animator.RepeatBehavior repeatBehavior =
            	Animator.RepeatBehavior.LOOP;
            
            Animator.EndBehavior behavior = 
                Animator.EndBehavior.HOLD; 
            
            Object [] range = new Float[2];
         
            range[0] = new Float(start);
            range[1] = new Float(end);
            
            
            
            animation = PropertySetter.createAnimator(1000, this, "height", new RangeEvaluator(),  
                    range);

     //       animation = new Animator(duration, repeatCount, repeatBehavior,
     //               new PropertySetter(this, "location", keyFrames));
            animation.setResolution(resolution);
            animation.setStartDelay(0);
            animation.setEndBehavior(behavior);
            animation.setStartFraction((float) 0);
            animation.setStartDirection(direction);
            
            // Now add another TimingTarget to the animation; this will track
            // and display the animation fraction
            animation.addTarget(new AnimateListener());
    //        animationView.setTimer(animation);
            
            // Vary the acceleration/deceleration values appropriately
            animation.setAcceleration(0);
            animation.setDeceleration(0);
            
            // Finally: start the animation
            animation.start();
        
        
        
        }
        
        public void setHeight(float height) {
            Container host = getContainer();
            this.height = height;
            preferenceChanged(this, true, true);
            host.repaint();
            if (host instanceof JComponent) {
                ((JComponent)host).revalidate();
            }
        }

    private static Color getColorForDepth(int depth) {
        return ColorScheme.getScheme(depth - 1).getOuterColor();
    }

         
       
        
        private class AnimateListener implements TimingTarget {
            public void timingEvent(long l, long l0, float f) {
            }
            
            public void timingEvent(float f) {
            }
            
            public void begin() {
            }

            public void repeat() {
            }

            public void end() {
                animating = false;
            }
        }
        
        class RangeEvaluator extends Evaluator {
        	
        	public RangeEvaluator() {
        		super();
        	}
        	
        	public Object evaluate(Object v0, Object v1, float f) {
        		
        		Float f0 = (Float) v0;
        		Float f1 = (Float) v1;
        		
        		float v = f0.floatValue() + (f1.floatValue() - f0.floatValue()) * f;
        		
        		return new Float(v);
        		
        	}
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




        
    }
