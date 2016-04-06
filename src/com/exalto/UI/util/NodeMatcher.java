package com.exalto.UI.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.exalto.UI.grid.ExaltoXmlNode;
import com.exalto.UI.grid.GridHelper;
import com.exalto.UI.grid.SimpleTreeModelAdapter;
import com.exalto.UI.grid.XmlTreeModel;

public class NodeMatcher {

	SimpleTreeModelAdapter treeTableAdapter;
	ExaltoXmlNode xpathNode;
	int row, column;
	String xpathExpr;
	String nodeVal;
	
	public NodeMatcher(String xpathExpr, SimpleTreeModelAdapter treeTableAdapter, ExaltoXmlNode xpathNode, int row, int column) {

		this.treeTableAdapter = treeTableAdapter;
		
		this.xpathNode = xpathNode;

		this.row = row;
		
		this.column = column;
		
		this.xpathExpr = xpathExpr;
	}

	public NodeMatcher(String xpathExpr, SimpleTreeModelAdapter treeTableAdapter, ExaltoXmlNode xpathNode) {

		this.treeTableAdapter = treeTableAdapter;
		
		this.xpathNode = xpathNode;

		this.xpathExpr = xpathExpr;
	}

	
	public boolean find() {
		
		XmlTreeModel xmodel = treeTableAdapter.getModel();
		
		HashMap rowMapper = xmodel.getRowMapper();
		
		ArrayList plist = xmodel.getParentList();
		
        try {
            
            
            ArrayList nlist = (ArrayList) rowMapper.get(new Integer(row));
            String rowcol = (String) nlist.get(0);
            
            String [][] parts = null;
            StringTokenizer stok2 = new StringTokenizer(rowcol, "|");
            int num = stok2.countTokens();
            parts = new String[num][3];
            int ct=0;
            while(stok2.hasMoreTokens()) {
                String rwc = stok2.nextToken();
                StringTokenizer stok3 = new StringTokenizer(rwc, ",");
                parts[ct][0] = stok3.nextToken();
                parts[ct][1] = stok3.nextToken();
                parts[ct++][2] = stok3.nextToken();
                
            }
            
            /*       System.out.println("input rw = " + row);
            //       System.out.println("input col = " + column);
             */
            
            
            for(int t=0;t<ct;t++) {
                
                Arrays.sort(parts, new GridHelper.ColumnComparator());
                /*   System.out.println(" parts[t][0] = " + parts[t][0]);
                //   System.out.println(" parts[t][1] = " + parts[t][1]);
                //   System.out.println(" parts[t][2] = " + parts[t][2]);
                 */
                
                int rw = Integer.parseInt(parts[t][0]);
                int col = Integer.parseInt(parts[t][1]);
                int px = Integer.parseInt(parts[t][2]);
                
                /*   System.out.println("in gva3 rw = " + rw);
                //   System.out.println("in gva3 col = " + col);
                 */
                
                ExaltoXmlNode viewerNode = ((ExaltoXmlNode) plist.get(px));
     
        		//   System.out.println(" gva3 row = " + row);
       // 		   System.out.println(" gva3 col = " + column);
        		   
          		   
                if(row == rw) {
                    if(column == col) {
                    	
                    	nodeVal = viewerNode.toString();
                    	
                    	if(viewerNode == xpathNode) {
                    		return true;
                    	}
                    }
                }
		
            }

                    		return false;

            
        } catch(Exception e) {
        	e.printStackTrace();	
        }

        return false;
    	
	}


	public String getXpathExpr() {
		return xpathExpr;
	}


	public void setXpathExpr(String xpathExpr) {
		this.xpathExpr = xpathExpr;
	}
	
	public XPATHResult toXPathResult() {
		
		XPATHResult res = new XPATHResult(xpathExpr, xpathNode, nodeVal, row, column);
		
		return res;
		
		
		
	}

	
	public static class XPATHResult {
	
		String xpath;
		ExaltoXmlNode xpathNode;
		String nodeValue;
		int row;
		int column;
		
		public XPATHResult(String xpath, ExaltoXmlNode xpathNode, String nodeValue,
				int row, int column) { 

			this.xpath = xpath;
			this.xpathNode = xpathNode;
			this.nodeValue = nodeValue;
			this.row = row;
			this.column = column;

		}
		
	}


	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
}
