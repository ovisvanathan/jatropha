package com.exalto.UI.util;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.font.*;
import java.awt.event.*;



public class  JBubblePanel extends JPanel
{

      private static final Color YELLOW_SNOW =
            new Color(255, 255, 204);
      private static final Color BUBBLE_BORDER = Color.GRAY;

      protected static final int X_MARGIN = 4;
      protected static final int Y_MARGIN = 2;

      private final int ARC_WIDTH = 8;
      private final int ARC_HEIGHT = 8;


      public JBubblePanel() {
            super();
         init();
      }

      protected void init() {
            this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 
3));
      }

      public static void main(String[] args)
      {
            System.out.println("Hello World!");
      }

      protected Insets getRealInsets() {
            return super.getInsets();
      }

      public Insets getInsets() {
            Insets realInsets = getRealInsets();
            Insets fakeInsets =
                  new Insets(realInsets.top + Y_MARGIN,
                                 realInsets.left + X_MARGIN,
                                 realInsets.bottom + Y_MARGIN,
                                 realInsets.right + X_MARGIN);

            return fakeInsets;
      }

      protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            Insets insets = getRealInsets();
            Color savedColor = g2d.getColor();

            int rectX = insets.left;
            int rectY = insets.top;
            int rectWidth = getWidth() - insets.left - insets.right;
            int rectHeight = getHeight() - insets.top - insets.bottom;

            // Paint the yellow interior
            g2d.setColor(YELLOW_SNOW);
            g2d.fillRoundRect(rectX, rectY,
                  rectWidth, rectHeight,
                  ARC_WIDTH, ARC_HEIGHT);


            //OV added for adding icon to top right corner
      int ICON_DIMENSION = 16;
      Color backgroundColor = Color.WHITE;
       BufferedImage image = new BufferedImage(ICON_DIMENSION,
ICON_DIMENSION, BufferedImage.TYPE_INT_ARGB);
   // set completely transparent
      for (int col = 0; col < ICON_DIMENSION; col++) {
      for (int row = 0; row < ICON_DIMENSION; row++) {
         image.setRGB(col, row, 0x0);
      }
   }


// Now, retrieve the graphics context of this image and set rendering hints for antialiasing
//   Graphics2D graphics = (Graphics2D) image.getGraphics();

       Graphics2D graphics = g2d;

   graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);

// Now, we fill a circle with specified background color

   graphics.setColor(backgroundColor);


   graphics.fillOval(rectX+rectWidth-20, 10, ICON_DIMENSION - 1, ICON_DIMENSION - 1);
// Now, before we draw the border, we create a whitish spot. 
// This one is not particularly tricky - based on the distance 
// from a pixel to the spotlight location, make the color of the 
// current pixel closer to white color (or the color of the spotlight). 
// Important - don't forget to preserve the transparency (or opacity) of each pixel:

   // create a whitish spot in the left-top corner of the icon
   double id4 = ICON_DIMENSION / 4.0;
   double spotX = id4;
   double spotY = id4;
   for (int col = 0; col < ICON_DIMENSION; col++) {
      for (int row = 0; row < ICON_DIMENSION; row++) {
         // distance to spot
         double dx = col - spotX;
         double dy = row - spotY;
         double dist = Math.sqrt(dx * dx + dy * dy);

         // distance of 0.0 - comes 90% to Color.white
         // distance of ICON_DIMENSION - stays the same

         if (dist > ICON_DIMENSION) {
            dist = ICON_DIMENSION;
         }

         int currColor = image.getRGB(col, row);
         int transp = (currColor >>> 24) & 0xFF;
         int oldR = (currColor >>> 16) & 0xFF;
         int oldG = (currColor >>> 8) & 0xFF;
         int oldB = (currColor >>> 0) & 0xFF;

         double coef = 0.9 - 0.9 * dist / ICON_DIMENSION;
         int dr = 255 - oldR;
         int dg = 255 - oldG;
         int db = 255 - oldB;

         int newR = (int) (oldR + coef * dr);
         int newG = (int) (oldG + coef * dg);
         int newB = (int) (oldB + coef * db);

         int newColor = (transp << 24) | (newR << 16) | (newG << 8)
            | newB;
         image.setRGB(col, row, newColor);
      }
   }

// Now, draw the outline of the icon in black. Here, based on the
// background color, we can choose the color of the border as lying 
// somewhere 70-80% on the way from the background color to black. 
// In this way, the icon will have matching border color.
   // draw outline of the icon

   graphics.setColor(Color.black);

   graphics.drawOval(rectX+rectWidth-20, 10, ICON_DIMENSION - 1,
ICON_DIMENSION - 1);

// Now, take the input letter and make it capital (this looks much 
// better on icons). Then, set font that is a few pixels smaller than the icon
// dimension. Compute the bounds of this letter, and set the position for this
// letter so that it will be centered in the icon's center:

   char letter = 'x';
   letter = Character.toUpperCase(letter);
   graphics.setFont(new Font("Arial", Font.BOLD, ICON_DIMENSION-5));
   FontRenderContext frc = graphics.getFontRenderContext();
   TextLayout mLayout = new TextLayout("" + letter, graphics.getFont(),
      frc);

//   float x = (float) (-.5 + (ICON_DIMENSION - mLayout.getBounds()
  //    .getWidth()) / 2);
   float x = (float) (rectX+rectWidth-19 + (ICON_DIMENSION - mLayout.getBounds().getWidth()) / 2);

   float y = ICON_DIMENSION - (float) ((ICON_DIMENSION - mLayout.getBounds().getHeight()) / 2) 
   + (float) 8.8;
// Now we can draw the letter (in black color):
   // draw the letter
   graphics.drawString("" + letter, x, y);
// Put optional plus sign in the top-right corner of the icon. First,
// create a semi-transparent white background for it, and then draw a red
// plus-sign two pixels thick:
   // if collection - draw '+' sign
   /*
   if (
   int height = 6;
   BufferedImage image = new BufferedImage(width, height,
      BufferedImage.TYPE_INT_ARGB);
   // set completely transparent
   for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
         image.setRGB(col, row, 0x0);
      }
   }
      */

// Get the graphics context, set antialiasing hint and draw an arrow of specified color:

      /*
      Graphics2D graphics = (Graphics2D) image.getGraphics();
   graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);

   // draw arrow
   Polygon pol = new Polygon();
   int ya = 3;
   pol.addPoint(1, ya);
   pol.addPoint(width / 2 + 3, ya);
   pol.addPoint(width / 2 + 3, ya + 2);
   pol.addPoint(width - 1, ya);
   pol.addPoint(width / 2 + 3, ya - 2);
   pol.addPoint(width / 2 + 3, ya);
   graphics.setColor(color);
   graphics.drawPolygon(pol);
// And now for the tricky part - we have to compute the halo. Here, if 
// an arrow pixel was completely opaque, it should have less transparent halo
// than arrow pixel that was only partly opaque (as on arrow's head for
// example). Here, we create another image with the halo footprint, and 
// then draw the original arrow on top of it. Each arrow pixel contributes to 
// its 8 neighbouring pixels. The final opacity of the halo footprint is the 
// maximal opacity of all neighbouring arrow pixels:    // create semi-transparent
// halo around arrow (to make it stand out) 

   BufferedImage fimage = new BufferedImage(width, height,
      BufferedImage.TYPE_INT_ARGB);
   // set completely transparent
   for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
         fimage.setRGB(col, row, 0x0);
      }
   }
   Graphics2D fgraphics = (Graphics2D) fimage.getGraphics();
   for (int col = 0; col < width; col++) {
      int xs = Math.max(0, col - 1);
      int xe = Math.min(width - 1, col + 1);
      for (int row = 0; row < height; row++) {
         int ys = Math.max(0, row - 1);
         int ye = Math.min(height - 1, row + 1);
         int currColor = image.getRGB(col, row);
         int opacity = (currColor >>> 24) & 0xFF;
         if (opacity > 0) {
            // mark all pixels in 3*3 area
            for (int x = xs; x <= xe; x++) {
               for (int y = ys; y <= ye; y++) {
                  int oldOpacity = (fimage.getRGB(x, y) >>> 24) & 0xFF;
                  int newOpacity = Math.max(oldOpacity, opacity);
                  // set semi-transparent white
                  int newColor = (newOpacity << 24) | (255 << 16) |
                     (255 << 8) | 255;
                  fimage.setRGB(x, y, newColor);
               }
            }
         }
      }
   }

// The final step - reduce the opacity of the halo by 30%. This is 
// needed to reduce complete opacity around vertical and horizontal lines:    
// reduce opacity of all pixels by 30%

   for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
         int oldOpacity = (fimage.getRGB(col, row) >>> 24) & 0xFF;
         int newOpacity = (int)(0.7*oldOpacity);
         int newColor = (newOpacity << 24) | (255 << 16) |
            (255 << 8) | 255;
         fimage.setRGB(col, row, newColor);
      }
   }
// Now, draw the original arrow on top of its halo    
// draw the original arrow image on top of the halo
   fgraphics.drawImage(image, 0, 0, null);
// Going back to the original image (the letter and optional plus sign) - draw the arrow image on top of it:
      BufferedImage arrowImage = getArrowImage(arrowColor, 
ICON_DIMENSION);
   graphics.drawImage(arrowImage, 0, ICON_DIMENSION -
arrowImage.getHeight(), null);
      */


            //ov added end


            // Draw the gray border
            g2d.setColor(BUBBLE_BORDER);
            g2d.drawRoundRect(rectX, rectY,
                  rectWidth, rectHeight,
                  ARC_WIDTH, ARC_HEIGHT);

            g2d.setColor(savedColor);
      }


      public void paint(Graphics g) {
            super.paint(g);
      }

      public Rectangle getBoundsForCloseIcon() {

            int ICON_DIMENSION = 16;
            Insets insets = getRealInsets();
            int rectX = insets.left;
            int rectY = insets.top;
            int rectWidth = getWidth() - insets.left - insets.right;
            int rectHeight = getHeight() - insets.top - insets.bottom;

            return new Rectangle(rectX+rectWidth-20, 10, ICON_DIMENSION 
-
1, ICON_DIMENSION - 1);

      }

}
