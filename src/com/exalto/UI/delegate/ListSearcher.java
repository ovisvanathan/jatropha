package com.exalto.UI.delegate;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.util.Iterator;
import java.util.List;


import com.exalto.UI.XmlEditor;
import com.exalto.UI.mdi.Editor;
import com.exalto.UI.mdi.MyInternalFrame;

public class ListSearcher implements KeyEventDispatcher
// extends KeyAdapter 
{
    protected JList m_list;
    protected ListModel m_model;
    protected String m_key = "";
    protected long m_time = 0;
	
    public static int CHAR_DELTA = 1000;
	protected String itemType = null;
	private Editor editor;
	private KeyboardFocusManager manager;

    public ListSearcher(Editor parent, JList list, KeyboardFocusManager manager, String itemType)
    {
		m_list = list;
		m_model = m_list.getModel();
		this.editor = parent;
		this.manager = manager;
		this.itemType = itemType;
    }

	public boolean dispatchKeyEvent(java.awt.event.KeyEvent e){
		System.out.println(" in dke search ");
		Editor ed = null;
		
	//	processKeyEvent(e); 	
	
			 // null is returned if none of the components in this application has the focus
				Component compFocusOwner =
					KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
    
			Object obj = e.getSource();
	
			System.out.println(" curr focus owner " + obj.getClass());
			
			if(compFocusOwner instanceof Editor) {
			   ed = (Editor) compFocusOwner; 
			   System.out.println(" ed.getFile().getName() " + ed.getFile().getName());
			} 
				
	
			char ch = e.getKeyChar();		
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) 
			{
				System.out.println(" in keytyped escape ");
				manager.redispatchEvent(editor,e);
				return false;
			}
			
			if (Character.isLetterOrDigit(ch)) {
			   System.out.println(" in  isLetterOrDigit 1 ");
				boolean discardEvent = false;
				manager.redispatchEvent(editor,e);
				return discardEvent;
			}

			if(e.getKeyChar() == '>' )  {
			   System.out.println(" in  isLetterOrDigit 2 ");
				boolean discardEvent = false;
				manager.redispatchEvent(editor,e);
				return discardEvent;			
			}
			
		
	/*			
			if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				if(m_list.isSelectionEmpty()) {
					System.out.println("in isselempty");			
					m_list.setSelectedIndex(0);
					m_list.ensureIndexIsVisible(0);
					boolean discardEvent = false;
					return discardEvent;
				} else {
					int selIndex = 	m_list.getSelectedIndex();
					System.out.println(" sel index = " + selIndex);
					System.out.println(" model size = " + m_model.getSize());
					if(selIndex <= m_model.getSize()-1) {
						m_list.setSelectedIndex(selIndex+1);
						m_list.ensureIndexIsVisible(selIndex+1);
						e.consume();
						boolean discardEvent = true;
						return discardEvent;
					}
					
				}
			}		
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				if(m_list.isSelectionEmpty()) {
					m_list.setSelectedIndex(0);
					m_list.ensureIndexIsVisible(0);
					return true;		
				} else {
					int selIndex = 	m_list.getSelectedIndex();
					if(selIndex >= 0) {
						m_list.setSelectedIndex(selIndex-1);
						m_list.ensureIndexIsVisible(selIndex-1);
						boolean discardEvent = true;
						return discardEvent;
					}					
				}
			}
		*/
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				System.out.println(" in keytyped enter listsearcher listshowing =" + m_list.isShowing());
			try {	
				if(ed != null) {
					System.out.println(" in enter editor if ");
					manager.redispatchEvent(ed,e);					
				} else {
					MyInternalFrame myf = (MyInternalFrame) XmlEditor.getInstance("").getDesktopView().getSelectedFrame();
					Editor edt = (Editor) myf.getTextComponent();
					System.out.println(" in enter editor else edt =" + edt);
					manager.redispatchEvent(edt, e);					
				}
			} catch(Exception ke) {
				ke.printStackTrace();
			}
				return false;
			}
			if(e.getKeyCode() == KeyEvent.VK_TAB) {
				System.out.println(" in keytyped tab listsearcher ");
				manager.redispatchEvent(editor,e);
				return false;
			}
			if (!Character.isLetterOrDigit(ch)) {
			   System.out.println(" in  isLetterOrDigit ");
			   boolean discardEvent = false;
				return discardEvent;
			}
		
			if (m_time+CHAR_DELTA < System.currentTimeMillis())
			   m_key = "";

			m_time = System.currentTimeMillis();
		
			m_key += Character.toLowerCase(ch);
			for (int k = 0; k < m_model.getSize(); k++)
			{
			    String str = ((String)m_model.getElementAt(k)).toLowerCase();
			    if (str.startsWith(m_key))
			    {
						m_list.setSelectedIndex(k);
						m_list.ensureIndexIsVisible(k);
						break;
			    }
			}
	
		
	//	manager.redispatchEvent(editor,e);
		boolean discardEvent = false;
		return discardEvent;

	}
 
	// Processing of key event
	protected void processKeyEvent(KeyEvent e) {
	
		System.out.println(" in pke search ");
	 
		//	super.processKeyEvent(e);
		//	keyTyped(e);	
	
			char ch = e.getKeyChar();		
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
				System.out.println(" in keytyped escape ");
				manager.getCurrentKeyboardFocusManager().
				redispatchEvent(editor,e);
				return;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				if(m_list.isSelectionEmpty()) {
					System.out.println("in isselempty");			
					m_list.setSelectedIndex(0);
					m_list.ensureIndexIsVisible(0);
					e.consume();
					return;		
				} else {
					int selIndex = 	m_list.getSelectedIndex();
					System.out.println(" sel index = " + selIndex);
					System.out.println(" model size = " + m_model.getSize());
					if(selIndex <= m_model.getSize()-1) {
						m_list.setSelectedIndex(selIndex+1);
						m_list.ensureIndexIsVisible(selIndex+1);
						e.consume();
						return;		
					}
				
				}
			}
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				if(m_list.isSelectionEmpty()) {
					m_list.setSelectedIndex(0);
					m_list.ensureIndexIsVisible(0);
					return;		
				} else {
					int selIndex = 	m_list.getSelectedIndex();
					if(selIndex >= 0) {
						m_list.setSelectedIndex(selIndex-1);
						m_list.ensureIndexIsVisible(selIndex-1);
						return;		
					}					
				}
			}
			
			
		
		if (!Character.isLetterOrDigit(ch)) {
		   System.out.println(" in  isLetterOrDigit ");
		   return;
		}
	
		if (m_time+CHAR_DELTA < System.currentTimeMillis())
		   m_key = "";
		m_time = System.currentTimeMillis();
	
		m_key += Character.toLowerCase(ch);
		for (int k = 0; k < m_model.getSize(); k++)
		{
		    String str = ((String)m_model.getElementAt(k)).toLowerCase();
		    if (str.startsWith(m_key))
		    {
					m_list.setSelectedIndex(k);
					m_list.ensureIndexIsVisible(k);
					break;
		    }
		}

	
	
	
	}
	
    public void keyTyped(KeyEvent e)
    {
		System.out.println(" in keytyped search ");
	
		char ch = e.getKeyChar();
	
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE ) {

			System.out.println(" in keytyped escape ");
			DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().
			redispatchEvent(editor,e);
			return;
		}
/*		
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			if(m_list.isSelectionEmpty()) {
				System.out.println("in isselempty");			
				m_list.setSelectedIndex(0);
				m_list.ensureIndexIsVisible(0);
				e.consume();
				return;		
			} else {
				int selIndex = 	m_list.getSelectedIndex();
				System.out.println(" sel index = " + selIndex);
				System.out.println(" model size = " + m_model.getSize());
				if(selIndex <= m_model.getSize()-1) {
					m_list.setSelectedIndex(selIndex+1);
					m_list.ensureIndexIsVisible(selIndex+1);
					e.consume();
					return;		
				}
			
			}
		}
	*/	
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			if(m_list.isSelectionEmpty()) {
				m_list.setSelectedIndex(0);
				m_list.ensureIndexIsVisible(0);
				return;		
			} else {
				int selIndex = 	m_list.getSelectedIndex();
				if(selIndex >= 0) {
					m_list.setSelectedIndex(selIndex-1);
					m_list.ensureIndexIsVisible(selIndex-1);
					return;		
				}					
			}
		}
		
		
	
	if (!Character.isLetterOrDigit(ch)) {
	   System.out.println(" in  isLetterOrDigit ");
	   return;
	}

	if (m_time+CHAR_DELTA < System.currentTimeMillis())
	   m_key = "";
	m_time = System.currentTimeMillis();

	m_key += Character.toLowerCase(ch);
	for (int k = 0; k < m_model.getSize(); k++)
	{
	    String str = ((String)m_model.getElementAt(k)).toLowerCase();
	    if (str.startsWith(m_key))
	    {
				m_list.setSelectedIndex(k);
				m_list.ensureIndexIsVisible(k);
				break;
	    }
	}
    }
}
