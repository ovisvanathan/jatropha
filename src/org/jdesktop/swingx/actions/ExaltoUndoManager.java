/*
 * ExaltoUndoManager.java
 *
 * Created on October 27, 2006, 12:41 PM
 *
 * Author: Omprakash Visvanathan
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.actions;

import com.exalto.UI.grid.JXmlTreeTable;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

/**
 * An extension to default UndoManager which
 * manages undo/redo actions
 *
 *
 * @author omprakash.v
 */
public class ExaltoUndoManager extends UndoManager {
    
    JXmlTreeTable xmlTreeTable;
    
    public ExaltoUndoManager(JXmlTreeTable xtable){
        this.xmlTreeTable = xtable;
        stateChanged();
    }
    
    /*---------------------[ Actions ]-----------------------*/
    
    private Action undoAction = new UndoAction();
    private Action redoAction = new RedoAction();
    
    public Action getUndoAction(){
        return undoAction;
    }
    
    public Action getRedoAction(){
        return redoAction;
    }
    
    private class UndoAction extends AbstractAction{
        public UndoAction(){
            super("Undo");
            System.out.println(getClass().getResource("MyUndoManager.class"));
  //          putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("undo.gif")));
            putValue(Action.ACCELERATOR_KEY
                    , KeyStroke.getKeyStroke(KeyEvent.VK_Z
                    , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent ae){
            xmlTreeTable.stopCellEditing();
            undo();
            
            
            
        }
    }
    
    private class RedoAction extends AbstractAction{
        public RedoAction(){
            super("Undo");
    //        putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource("redo.gif")));
            putValue(Action.ACCELERATOR_KEY
                    , KeyStroke.getKeyStroke(KeyEvent.VK_Z
                    , Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        }
        
        public void actionPerformed(ActionEvent ae){
            xmlTreeTable.stopCellEditing();
            redo();
        }
    }
    
    /*---------------------[ StateChange ]----------------------*/
    
    
    public synchronized void undo() throws CannotUndoException{
        super.undo();

        System.out.println(" canredo = " + this.canRedo());
        System.out.println(" in prog = " + this.isInProgress());
        System.out.println(" is sig = " + this.isSignificant());

        stateChanged();
    }
    
    public synchronized void redo() throws CannotRedoException{
        super.redo();
        stateChanged();
    }
    
    
    public void undoableEditHappened(UndoableEditEvent undoableEditEvent){
        super.undoableEditHappened(undoableEditEvent);
        stateChanged();
    }
    
    private void stateChanged(){
        undoAction.setEnabled(canUndo());
        redoAction.setEnabled(canRedo());
    }
}
