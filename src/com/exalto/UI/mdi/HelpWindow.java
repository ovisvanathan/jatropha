package com.exalto.UI.mdi;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.html.*;
public class HelpWindow extends JFrame {
	private JTextPane jep;
	public HelpWindow(String s) {
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					dispose();
				}
			}
		);
		jep = new JTextPane();
		jep.setStyledDocument(new HTMLDocument());
		try {
			jep.setPage(s);
		} catch (IOException ioe) {
		}
		getContentPane().add(new JScrollPane(jep));
		setSize(800, 600);
		show();
	}
}