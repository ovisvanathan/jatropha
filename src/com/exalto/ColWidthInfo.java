package com.exalto;

public class ColWidthInfo {

    private String type = null;
    private double minwid = 99999;
    private double maxwid = 0.0;
    private double width = 0.0;
    private String unitOfWidth = null;
    private double borderSpacing = 0.0;
    private double border = 0.0;
    private double padding = 0.0;
	private double availableWidth = 0.0;       
    private int numCols = 0;
	
	public ColWidthInfo() {
	}

	public ColWidthInfo(String tp) {
		type = tp;
	}

	public ColWidthInfo(double min, double max) {

	    minwid = min;
	    maxwid = max;

	}

	public double getMinWidth() {
		return minwid;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double d) {
		width = d;
	}
	
	public int getNumberOfCols() {
		return numCols;
	}
	
	public void setNumberOfCols(int d) {
		numCols = d;
	}


	public double getMaxWidth() {
		return maxwid;
	}

	public void setMaxWidth(double max) {
		maxwid = max;
	}

	public void setMinWidth(double min) {
		minwid = min;
	}



/**
 * Returns the unitOfWidth.
 * @return String
 */
public String getUnitOfWidth() {
	return unitOfWidth;
}

/**
 * Sets the unitOfWidth.
 * @param unitOfWidth The unitOfWidth to set
 */
public void setUnitOfWidth(String unitOfWidth) {
	this.unitOfWidth = unitOfWidth;
}

/**
 * Returns the type.
 * @return String
 */
public String getType() {
	return type;
}

/**
 * Sets the type.
 * @param type The type to set
 */
public void setType(String type) {
	this.type = type;
}

/**
 * Returns the border.
 * @return double
 */
public double getBorder() {
	return border;
}

/**
 * Returns the borderSpacing.
 * @return double
 */
public double getBorderSpacing() {
	return borderSpacing;
}

/**
 * Sets the border.
 * @param border The border to set
 */
public void setBorder(double border) {
	this.border = border;
}

/**
 * Sets the borderSpacing.
 * @param borderSpacing The borderSpacing to set
 */
public void setBorderSpacing(double borderSpacing) {
	this.borderSpacing = borderSpacing;
}

/**
 * Returns the padding.
 * @return double
 */
public double getPadding() {
	return padding;
}

/**
 * Sets the padding.
 * @param padding The padding to set
 */
public void setPadding(double padding) {
	this.padding = padding;
}

	/**
	 * Returns the availableWidth.
	 * @return double
	 */
	public double getAvailableWidth() {
		return availableWidth;
	}

	/**
	 * Sets the availableWidth.
	 * @param availableWidth The availableWidth to set
	 */
	public void setAvailableWidth(double availableWidth) {
		this.availableWidth = availableWidth;
	}

}
