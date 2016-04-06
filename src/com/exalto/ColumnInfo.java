package com.exalto;

import org.jdom.*;


/**
 * @author 510342
 *
 * This class is the controller and is responsible for calling the 
 * right method  and returning the results as 
 * an array list 
 */
public class ColumnInfo implements com.exalto.ColWidthTypes {
	private int colNo = 0;
	private boolean isColWidthSpecified =  false;
	private int colWidthType = 0;;
	private double colWidth = 0.0;
	private Element colElement = null;
	private String colName = null;


	/**
	 * Returns the colNo.
	 * @return int
	 */
	public int getColNo() {
		return colNo;
	}

	/**
	 * Returns the colWidth.
	 * @return double
	 */
	public double getColWidth() {
		return colWidth;
	}

	/**
	 * Returns the colWidthType.
	 * @return int
	 */
	public int getColWidthType() {
		return colWidthType;
	}

	/**
	 * Returns the isColWidthSpecified.
	 * @return boolean
	 */
	public boolean isColWidthSpecified() {
		return isColWidthSpecified;
	}

	/**
	 * Sets the colNo.
	 * @param colNo The colNo to set
	 */
	public void setColNo(int colNo) {
		this.colNo = colNo;
	}

	/**
	 * Sets the colNo.
	 * @param colNo The colNo to set
	 */
	public void setColName(String name) {
		this.colName = name;
	}

	/**
	 * Sets the colNo.
	 * @param colNo The colNo to set
	 */
	public String getColName() {
		return this.colName;
	}


	/**
	 * Sets the colWidth.
	 * @param colWidth The colWidth to set
	 */
	public void setColWidth(double colWidth) {
		this.colWidth = colWidth;
	}

	/**
	 * Sets the colWidthType.
	 * @param colWidthType The colWidthType to set
	 */
	public void setColWidthType(int colWidthType) {
		this.colWidthType = colWidthType;
		isColWidthSpecified =  true;
	}

	/**
	 * Sets the isColWidthSpecified.
	 * @param isColWidthSpecified The isColWidthSpecified to set
	 */
	public void setIsColWidthSpecified(boolean isColWidthSpecified) {
		this.isColWidthSpecified = isColWidthSpecified;
	}

	/**
	 * Returns the tableDescendant.
	 * @return Element
	 */
	public Element getColElement() {
		return colElement;
	}

	/**
	 * Sets the tableDescendant.
	 * @param tableDescendant The tableDescendant to set
	 */
	public void setColElement(Element tableDescendant) {
		this.colElement = tableDescendant;
	}

}
