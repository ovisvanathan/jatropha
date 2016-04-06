package com.exalto.UI.mdi;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;

/**
 * A transferable for styled text.
 *
 * <p>It can be used in drag and drop operations or in copy and paste
 * operations. Additional to StyledText it supports the String and plain
 * text data flavors.</p>
 *
 * @see de.calcom.cclib.text.StyledText
 * @see java.awt.datatransfer.DataFlavor.stringFlavor
 * @see java.awt.datatransfer.DataFlavor.plainTextFlavor
 *
 * @author Ulrich Hilger
 * @author CalCom
 * @author <a href="http://www.calcom.de">http://www.calcom.de</a>
 * @author <a href="mailto:info@calcom.de">info@calcom.de</a>
 * @version 1.0, October 5, 2001
 */

public class StyledTextSelection implements Transferable, ClipboardOwner
{
  private static final int STYLED_TEXT = 0;
  private static final int STRING = 1;
  private static final int PLAIN_TEXT = 2;

  /** the data to transfer */
  private StyledText data;

  /**
   * the data flavor of this transferable
   *
   * @deprecated plainTextFlavor only contained for backward compatibility, do not use
   */
  private static final DataFlavor[] flavors = {
    new DataFlavor(com.exalto.UI.mdi.StyledText.class, "StyledText"),
    DataFlavor.stringFlavor,
    DataFlavor.plainTextFlavor // deprecated
  };

  /**
   * construct a <code>StyledTextSelection</code> with a chunk
   * of styled text.
   *
   * @param data - a StyledText object
   *
   * @see de.calcom.cclib.text.StyledText
   */
  public StyledTextSelection(StyledText data) {
    this.data = data;
  }



  /* ---- start of Transferable implementation ----------------------------*/

  /**
   * Returns an array of DataFlavor objects indicating the flavors the data
   * can be provided in.  The array should be ordered according to preference
   * for providing the data (from most richly descriptive to least descriptive).
   * @return an array of data flavors in which this data can be transferred
   */
  public DataFlavor[] getTransferDataFlavors() {
    return (DataFlavor[])flavors.clone();
  }

  /**
   * Returns whether or not the specified data flavor is supported for
   * this object.
   * @param flavor the requested flavor for the data
   * @return boolean indicating wjether or not the data flavor is supported
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    for (int i = 0; i < flavors.length; i++) {
      if (flavors[i].equals(flavor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an object which represents the data to be transferred.  The class
   * of the object returned is defined by the representation class of the flavor.
   *
   * @param flavor the requested flavor for the data
   * @see DataFlavor#getRepresentationClass
   * @exception IOException                if the data is no longer available
   *              in the requested flavor.
   * @exception UnsupportedFlavorException if the requested data flavor is
   *              not supported.
   */
  public Object getTransferData(DataFlavor flavor) throws
      UnsupportedFlavorException, IOException
  {
    if (flavor.equals(flavors[STYLED_TEXT])) {
      return (Object) data;
    }
    else if (flavor.equals(flavors[STRING])) {
      return (Object) data.toString();
    }
    else if (flavor.equals(flavors[PLAIN_TEXT])) {
      return new StringReader(data.toString());
    }

    else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  /* ----------- end of Transferable implementation ------------------- */



  /* ----------- start of ClipboardOwner implementation --------------- */

  /**
   * Notifies this object that it is no longer the owner of
   * the contents of the clipboard.
   * @param clipboard the clipboard that is no longer owned
   * @param contents the contents which this owner had placed on the clipboard
   */
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
  }

  /* ------------ end of ClipboardOwner implementation ---------------- */
}