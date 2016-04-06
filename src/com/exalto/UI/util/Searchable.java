/*
 * $Id: Searchable.java,v 1.4 2005/11/14 15:23:55 kizune Exp $
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package com.exalto.UI.util;


/**
 * Interface that used to implement search logic in all the search capable 
 * components. 
 *
 * @author Ramesh Gupta
 */
public interface Searchable {

    /**
     * Search <code>searchString</code> from the beginning of a document.
     *
     * @param searchString <code>String</code> we should find in a document.
     *
     * @return index of matched <code>String</code> or -1 if a match cannot be found.
     */
    public int search(String searchString);
    
    /**
     * Search <code>searchString</code> from the given position in a document.
     *
     * @param searchString <code>String</code> we should find in a document.
     * @param startIndex Start position in a document or -1 if we want to search from the beginning.
     *
     * @return index of matched <code>String</code> or -1 if a match cannot be found.
     */
    public int search(String searchString, int startIndex);

    /**
     * Search <code>searchString</code> in the given direction from the some position in a document.
     *
     * @param searchString <code>String</code> we should find in a document.
     * @param startIndex Start position in a document or -1 if we want to search from the beginning.
     * @param backward Indicates search direction, will search from the given position towards the 
     *                 beginning of a document if this parameter is <code>true</code>.
     *
     * @return index of matched <code>String</code> or -1 if a match cannot be found.
     */
    public int search(String searchString, int startIndex, boolean backward);

	public String getSearchPattern();    

	public int getSearchResultRow();    

	public int getSearchResultColumn();    
	
	public void reset();

	public void mustReset(boolean reset);

	public boolean getReset();

    public void setSearchType(String searchType);

}
