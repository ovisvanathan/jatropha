/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package com.exalto.UI.delegate;

import java.awt.Dimension;
import java.util.Collections;
import java.util.List;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.AbstractListModel;


/**
* Code completion view component interface. It best fits the <tt>JList</tt>
* but some users may require something else e.g. JTable.
*
* @author Miloslav Metelka
* @version 1.00
*/

public class ListCompletionView extends JList  {

    public ListCompletionView() {
        this(null);
    }

    public ListCompletionView(ListCellRenderer renderer) {
        setSelectionMode( javax.swing.ListSelectionModel.SINGLE_SELECTION );
        if (renderer != null) {
            setCellRenderer(renderer);
        }
 //       getAccessibleContext().setAccessibleName(LocaleSupport.getString("ACSN_CompletionView"));
 //       getAccessibleContext().setAccessibleDescription(LocaleSupport.getString("ACSD_CompletionView"));
        
        
    }
	
	    public void setResult(List data) {
	        if (data != null) {
	            setModel(new Model(data));
	            if (data.size() > 0) {
	                setSelectedIndex(0);
	            }
	        }
	    }


    
    /** Force the list to ignore the visible-row-count property */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }

    public void setVisible(boolean visible) {
        // ??? never called
//        System.err.println("ListCompletionView.setVisible(" + visible + ")");
        super.setVisible(visible);
    }
	
    static class Model extends AbstractListModel {

        List data;

        static final long serialVersionUID = 3292276783870598274L;

        public Model(List data) {
            this.data = data;
        }

        public int getSize() {
            return data.size();
        }

        public Object getElementAt(int index) {
            return (index >= 0 && index < data.size()) ? data.get(index) : null;
        }

        List getData() {
            return data;
        }

    }

	
    
}
