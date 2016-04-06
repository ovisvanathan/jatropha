package com.exalto.UI.multiview;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class MVTextElem extends MVElem {
	
	 private JTextPane  textPane;
	 
	 public MVTextElem(JTextPane textPane) {
		 
	//	 textPane = new JTextPane();
		 this.textPane = textPane;
		 
	//	 textPane.setText("This is a test");
		 
		 visualRepre = new JPanel();
		 
		 visualRepre.add(textPane);
		 
	 }

	 public MVTextElem() {
		 
				 textPane = new JTextPane();
				 
				 textPane.setText("This is a test");
				 
				 visualRepre = new JPanel();
				 
				 visualRepre.add(textPane);
				 
			 }

	 public MVTextElem(JPanel parent) {
			super();
			
			 textPane = new JTextPane();
			 
			 textPane.setText("This is a test");
			
			 textPane.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		//	 visualRepre = new JPanel();
			 
			 visualRepre.setBackground(Color.white);
			
			 textPane.setSize(new Dimension(500, 500));
				
			 
			 visualRepre.add(textPane);			
			
			 visualRepre.setSize(new Dimension(500, 500));
			
			
		}
	 
	 public javax.swing.JComponent  getVisualRepresentation() {
         return textPane;
     }

}
