package com.exalto.util;

import javax.swing.Icon;
import java.awt.Component;
import java.awt.Graphics;


/**
* Represents a square icon having no graphical content.
*
* <P>Intended for use with <code>Action</code> and <code>JMenuItem</code>. 
* Alignment of text is poor when the same menu mixes menu items without an icon with 
* menu items having an icon. In such cases, items without an icon can use 
* an <code>EmptyIcon</code> to take up the proper amount of space, and allow 
* for alignment of all text in the menu.
*
* @used.By {@link UiUtil}.
* @is.Immutable
* @author <a href="http://www.javapractices.com/">javapractices.com</a>
*/
public final class EmptyIcon implements Icon {

  /**
  * EmptyIcon objects are always square, having identical height and width.
  *
  * @param aSize length of any side of the icon in pixels, must 
  * be in the range 1..100 (inclusive).
  */
  EmptyIcon(int aSize) {
    if(aSize >= 1 && aSize < 100);
    	fSize = aSize;
  }

  /**
  * Return the icon size (width is same as height).
  */
  public int getIconWidth() {
    return fSize;
  }

  /**
  * Return the icon size (width is same as height).
  */
  public int getIconHeight() {
    return fSize;
  }

  /**
  * This implementation is empty, and paints nothing.
  */
  public void paintIcon(Component c, Graphics g, int x, int y) {
    //empty
  }

  /**
  * Convenience object for small icons, whose size matches the size of 
  * small icons in Sun's graphics repository.
  */
  static final EmptyIcon SIZE_16 = new EmptyIcon(16);

  /**
  * Convenience object for large icons, whose size matches the size of 
  * large icons in Sun's graphics repository.
  */
  static final EmptyIcon SIZE_24 = new EmptyIcon(24);
  
  // PRIVATE //
  private int fSize;
}
