package com.exalto.UI.multiview;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class MVTableElem extends MVElem {

	JTable table;
	JScrollPane gridScroller;
	
	String[] columnNames = {"First Name",
            "Last Name",
            "Sport",
            "# of Years",
            "Vegetarian"};


	
	Object[][] data = {
		    {"Mary", "Campione",
		     "Snowboarding", new Integer(5), new Boolean(false)},
		    {"Alison", "Huml",
		     "Rowing", new Integer(3), new Boolean(true)},
		    {"Kathy", "Walrath",
		     "Knitting", new Integer(2), new Boolean(false)},
		    {"Sharon", "Zakhour",
		     "Speed reading", new Integer(20), new Boolean(true)},
		    {"Philip", "Milne",
		     "Pool", new Integer(10), new Boolean(false)}
		};


	
	TableModel tm = new DefaultTableModel();
	
	public MVTableElem() {
		super();
		
//		table = new JTable(data, columnNames);
		
        table = new JTable();

		visualRepre.add(table);
	}

	public MVTableElem(JTable gridTable) {
		super();
		
		this.table = gridTable;
	//	table = new JTable(data, columnNames);
		
		gridScroller = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		
		visualRepre.add(table);
	}

	public MVTableElem(JPanel parent) {
		super();
		
		table = new JTable(data, columnNames);
		
		visualRepre.add(table);
		
		visualRepre.setPreferredSize(parent.getPreferredSize());
		
		table.setPreferredSize(parent.getPreferredSize());
		
	}

	 public javax.swing.JComponent  getVisualRepresentation() {
         return table;
	 }

}
