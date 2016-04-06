/*
 * SideKickCompletionPopup.java - Completer popup
 * :tabSize=8:indentSize=8:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2000, 2003 Slava Pestov
 *
 * The XML plugin is licensed under the GNU General Public License, with
 * the following exception:
 *
 * "Permission is granted to link this code with software released under
 * the Apache license version 1.1, for example used by the Xerces XML
 * parser package."
 */

package com.exalto.UI.delegate;

//{{{ Imports
import javax.swing.event.ListDataListener;
import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.*;
import java.util.*;
//import org.gjt.sp.jedit.gui.KeyEventWorkaround;
//import org.gjt.sp.jedit.textarea.JEditTextArea;
//import org.gjt.sp.jedit.*;
//}}}

import com.exalto.UI.mdi.Editor;
import com.exalto.UI.mdi.CustomCellRenderer;

public class ExaltoPopup extends JWindow
{
	//{{{ SideKickCompletionPopup constructor
	public ExaltoPopup(Editor etr, Vector v)
	{
		super();

		list = new JList();
		
		listItems = v;
		
		this.editor = etr;

	//	list.addMouseListener(new MouseHandler());
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer( new CustomCellRenderer() );


		// stupid scrollbar policy is an attempt to work around
		// bugs people have been seeing with IBM's JDK -- 7 Sep 2000
		JScrollPane scroller = new JScrollPane(list,
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		getContentPane().add(scroller, BorderLayout.CENTER);

	/*
		KeyHandler keyHandler = new KeyHandler();
		addKeyListener(keyHandler);
		getRootPane().addKeyListener(keyHandler);
		list.addKeyListener(keyHandler);
	*/
	
		
		requestFocus(this,list);

		updateListModel();

		show();
	}

	//{{{ dispose() method
	public void dispose()
	{
		super.dispose();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				editor.requestFocus();
			}
		});
	} //}}}

	//{{{ Private members

	//{{{ Instance variables
	private int mode;
	private JList list;
	private Vector listItems;
	private Editor editor;	
	//}}}

	//{{{ updateListModel() method
	private void updateListModel()
	{
		if(listItems == null || listItems.size() == 0)
		{
			list.setListData(new String[] {
				""
			});
			list.setCellRenderer(new DefaultListCellRenderer());
			list.setVisibleRowCount(1);
		}
		else
		{
			setListModel(listItems);
			list.setCellRenderer(new CustomCellRenderer());
			list.setVisibleRowCount(Math.min(8, listItems.size()));
		}

		pack();
	} //}}}

	//{{{ setListModel() method
	private void setListModel(Vector items)
	{
		ListModel model = new ListModel()
		{
			public int getSize()
			{
				return getListItems().size();
			}

			public Object getElementAt(int index)
			{
				return getListItems().get(index);
			}

			public void addListDataListener(ListDataListener l) {}
			public void removeListDataListener(ListDataListener l) {}
		};

		list.setModel(model);
		list.setSelectedIndex(0);
	} //}}}

	//}}}
	
	
	
	public void requestFocus(final Window win, final Component comp)
	{
			win.addWindowListener(new WindowAdapter()
			{
				public void windowActivated(WindowEvent evt)
				{
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							comp.requestFocus();
						}
					});
					win.removeWindowListener(this);
				}
			});
	}
	
	private Vector getListItems() {
		return listItems;
	}
	
	public JList getList() {
		return list;
	}
	
	
}
