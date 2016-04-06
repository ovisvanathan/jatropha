package com.exalto.UI.mdi;

import java.awt.AWTEvent;
import java.awt.Event;
import java.io.File;
public class ErrorEvent extends AWTEvent {
	
	private File theFile;
	private int lineNum;
	private int col;
	
	public ErrorEvent(Object source, int id, File f, int l, int c) {
		super(source, id);
		theFile = f;
		lineNum = l;
		col = c;
	}
	public File getFile() {
		return theFile;
	}
	public int getLineNum() {
		return lineNum;
	}
	public int getCol() {
		return col;
	}
}
