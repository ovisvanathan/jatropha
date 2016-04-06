package com.exalto.UI.mdi;


import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
public class HelpDialog extends JDialog implements ActionListener, MouseListener {
	private JTextField fileName = new JTextField("", 10);
	private JButton ok = new JButton("OK");
	private String name;
	private DefaultListModel dlm = new DefaultListModel();
	private JList jl = new JList(dlm);
	private int selection = -1;
	private String text = "";
	public HelpDialog() {
		super((JFrame) null, true);
		jl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jl.addMouseListener(this);
		getContentPane().add(jl);
	}
	public void display() {
		setSize(800, 600);
		show();
	}
	public void add(String s) {
		dlm.addElement(s);
	}
	public void actionPerformed(ActionEvent ae) {
	}
	public void mousePressed(MouseEvent me) {
	}
	public void mouseReleased(MouseEvent me) {
	}
	public void mouseClicked(MouseEvent me) {
		if (me.getClickCount() == 2) {
			//System.out.println(jl.locationToIndex(me.getPoint()));
			//System.out.println(jl.getSelectedValue());
			selection = jl.locationToIndex(me.getPoint());
			text = jl.getSelectedValue().toString();
			dispose();
		}
	}
	public void mouseEntered(MouseEvent me) {
	}
	public void mouseExited(MouseEvent me) {
	}
	public String getText() {
		return text;
	}
	public Dimension getPreferredSize() {
		return new Dimension(200, 200);
	}
}
