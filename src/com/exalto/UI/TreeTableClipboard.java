/*
 * Clipboard.java
 *
 * Created on March 19, 2008, 10:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI;

import java.awt.event.ActionEvent;
import javax.swing.undo.UndoManager;

/**
 *
 * @author omprakash.v
 */
public interface TreeTableClipboard {
    
    public void cut(ActionEvent evt);
    public void copy(ActionEvent evt);
    public void paste(ActionEvent evt);
 //   public void undo();
 //   public void redo();

    public void undo(ActionEvent evt);
    public void redo(ActionEvent evt);
    public UndoManager getUndoManager();
    
}