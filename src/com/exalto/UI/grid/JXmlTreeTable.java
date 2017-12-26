/*
 *
 * Created on October 31, 2006, 5:48 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.exalto.UI.grid;


import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.undo.UndoManager;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.TreeTableCellRenderer;
import org.jdesktop.swingx.JXTreeTable2;
import org.jdesktop.swingx.JXTreeTable2.Actions;
import org.jdesktop.swingx.JXTreeTable2.TreeTableDataAdapter;
import org.jdesktop.swingx.JXTreeTable2.TreeTableHacker;
import org.jdesktop.swingx.JXTreeTable2.TreeTableHackerExt;
import org.jdesktop.swingx.actions.ExaltoUndoManager;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.plaf.UIAction;
import org.jdesktop.swingx.search.Searchable;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;
import org.jdesktop.swingx.treetable.AttrTextEditor;
import org.jdesktop.swingx.treetable.TreeTableCellEditor;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.w3c.dom.Document;

import com.exalto.ColWidthTypes;
import com.exalto.UI.TreeTableClipboard;
import com.exalto.UI.grid.xpath.PropertyChangeObject;
import com.exalto.UI.util.NodeMatcher.XPATHResult;
import com.exalto.UI.util.SearchFactory;
import com.exalto.util.XmlUtils;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 *
 * @author omprakash.v
 */
public class JXmlTreeTable extends JXTreeTable2 implements TreeTableClipboard, PropertyChangeListener  {

    private XmlTreeModel domTreeModel;
    SimpleTreeModelAdapter treeTableAdapter;
	public static Color selBackground;
	public static Color background;
        boolean rootVisible;
       	JFrame parent = null;

      static ImageIcon attrIcon = null;
      static ImageIcon elemIcon = null;
      static ImageIcon textIcon = null;

   boolean showingFind = false;
   // OV commented 210409 for xpath
   protected Searchable searchable;

    static XmlUtils xutils = null;

    
    IndicatorRenderer indicatorRenderer = null;
    AttributeRenderer attributeRenderer = null; 

  //OM  HighlighterPipeline highlighterPipeline = null;

    XmlTreeTableCellRenderer renderer;
 
     //OV added for undo/redo 031206
    ExaltoUndoManager undoManager;

    // OV added 300409
    protected Color selectionBackground;

    /** Creates a new instance of JXmlTreeTable */
    public JXmlTreeTable(XmlTreeModel model)  throws Exception {
        this(model, null, false);
    }
    
     /**
     * Constructs a JXTreeTable using the specified
     * {@link org.jdesktop.swingx.treetable.TreeTableModel}.ic 
     *
     * @param treeModel model for the JXTreeTable
     */
    public JXmlTreeTable(XmlTreeModel treeModel, JFrame parent)  throws Exception {
        // Implementation note:
        // Make sure that the SAME instance of treeModel is passed to the
        // constructor for TreeTableCellRenderer as is passed in the first
        // argument to the following chained constructor for this JXTreeTable:
  //OV      this(treeModel, new JXTreeTable.TreeTableCellRenderer(treeModel));

    		this(treeModel, parent, false);
 	        System.out.println(" in jxmlt 2 arg ctor  ");

    		
    }

    
        private JXmlTreeTable(XmlTreeModel treeModel, JFrame parent, boolean redun) throws Exception {
        // To avoid unnecessary object creation, such as the construction of a
        // DefaultTableModel, it is better to invoke super(TreeTableModelAdapter)
        // directly, instead of first invoking super() followed by a call to
        // setTreeTableModel(TreeTableModel).
        // Adapt tree model to table model before invoking super()
        
	    	super();

        System.out.println(" *********** in jxmlt 3 arg ctor ***************** ");


    	xutils = XmlUtils.getInstance();

        System.out.println(" xutils inited = " + xutils);

        initIcons();
        
        UIManager.put("Tree.leafIcon", elemIcon);
        UIManager.put("Tree.openIcon", elemIcon);
        UIManager.put("Tree.closedIcon", elemIcon);
        
     	this.renderer = new XmlTreeTableCellRenderer(treeModel);

     	
     	this.parent = parent;

    	treeTableAdapter = new SimpleTreeModelAdapter(treeModel, renderer, this, parent);
  		
    	super.setModel(treeTableAdapter);
    	
  //  	this.selectionBackground = new Color(0xCC, 0xC0, 0x6D);
        // OV added 300409
        //      super.selectionBackground = new Color(0xCC, 0xC0, 0x6D);

    	super.selectionBackground = new Color(0x6D, 0xB3, 0xCB);

        selBackground = this.selectionBackground;
    	this.background = this.getBackground();
    
        // Enforce referential integrity; bail on fail
        if (treeModel != renderer.getModel()) { // do not use assert here!
            throw new IllegalArgumentException("Mismatched TreeTableModel");
        }

  		this.domTreeModel = treeModel;


            TableColumnModel tcm = getColumnModel();
        
            int colCount = treeTableAdapter.getColumnCount();
        
            for(int s=0;s<colCount;s++) {
                  tcm.getColumn(s).setPreferredWidth(150);
                  tcm.getColumn(s).setMinWidth(150);
            }
        
            
        /*
		 Highlighter[]   highlighters = new Highlighter[] {
		 	      new AlternateRowHighlighter(Color.white,
		 	                                         new Color(0xF0, 0xF0, 0xE0), null),
		 	      new HierarchicalColumnHighlighter(Color.WHITE, new Color(0xF0, 0xF0, 0xE0), this.selectionBackground),
				  new ConditionalHighlighter()};
		 
		 	  highlighterPipeline = new HighlighterPipeline(highlighters);
		 	  setHighlighters(highlighterPipeline);
		*/ 	  
        //    setHighlighters(new HighlighterPipeline(new Highlighter[]{ AlternateRowHighlighter.classicLinePrinter }));
        
		/**OM
				Highlighter[]   highlighters = new Highlighter[] {
		 	      new AlternateRowHighlighter(Color.white,
		 	                                         new Color(0xF0, 0xF0, 0xE0), null),
		 	      new HierarchicalColumnHighlighter(Color.WHITE, new Color(0xF0, 0xF0, 0xE0), super.selectionBackground)
				  };
		 
		 	  highlighterPipeline = new HighlighterPipeline(highlighters);
		 //	  setHighlighters(highlighterPipeline);
		 	  setHighlighters(highlighters);
			*/
			  
        int col = this.getColumn(0).getModelIndex();
        
	System.out.println(" col 0 model index " + col);

        putClientProperty("JTable.autoStartsEdit", Boolean.FALSE);    
        
        // renderer-related initialization -- also called from setTreeTableModel()
        init(renderer); // private method
        initActions();
        initActionsAndBindings();
        // disable sorting
        super.setSortable(true);
        setColumnControlVisible(true);
        configureEnclosingScrollPane();
        
        // Install the default editor.
      //  cellEditor =  new TreeTableCellEditor(renderer);
      //  setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
        //		cellEditor);

        // No grid.
        setShowGrid(false); // superclass default is "true"

        // Default intercell spacing
   //     setIntercellSpacing(spacing); 
        // for both row margin and column margin

        // JTable supports row margins and intercell spacing, but JTree doesn't.
        // We must reconcile the differences in the semantics of rowHeight as
        // understood by JTable and JTree by overriding both setRowHeight() and
        // setRowMargin();
   //OV     adminSetRowHeight(getRowHeight());
        setRowHeight(50);

     
        
    }
        
    public void initIcons() {
		URL attrUrl = xutils.getResource("attrImage");
		URL elemUrl = xutils.getResource("elementImage");
		URL textUrl = xutils.getResource("textImage");

        attrIcon = new ImageIcon(attrUrl);
        elemIcon = new ImageIcon(elemUrl);
        textIcon = new ImageIcon(textUrl);
    }    
        
    
    /**
     * Initializes this JXTreeTable and permanently binds the specified renderer
     * to it.
     *
     * @param renderer private tree/renderer permanently and exclusively bound
     * to this JXTreeTable.
     */
    public void init(TreeTableCellRenderer renderer) {
        super.init(renderer);
        
       System.out.println(" xutils = " + xutils);

		URL attrUrl = xutils.getResource("attrImage");
		URL elemUrl = xutils.getResource("elementImage");
		URL textUrl = xutils.getResource("textImage");

        attrIcon = new ImageIcon(attrUrl);
        elemIcon = new ImageIcon(elemUrl);
        textIcon = new ImageIcon(textUrl);

       	    indicatorRenderer = new IndicatorRenderer(treeTableAdapter, domTreeModel, elemIcon, attrIcon, textIcon);	    	   
 	    attributeRenderer = new AttributeRenderer(attrIcon, this); 
            
 	   // 	this.setTableHeader(null);
            if (getTableHeader() != null) {
                System.out.println(" in header not null ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ "); 
                getTableHeader().setResizingAllowed(true);
        }


		 // Install the default editor.
         setDefaultEditor(AbstractTreeTableModel.hierarchicalColumnClass,
            new TreeTableCellEditor(renderer));
                
         setDefaultEditor(String.class,
     //       new AttrTextEditor(renderer, this));
        	       new AttrTextEditor(this.treeTableAdapter.getTree()));
              
    }
    
    private class GridActions extends UIAction {
        
          public GridActions(String name) {
              super(name);
          }

          public void actionPerformed(ActionEvent evt) {

            if ("undo".equals(getName())) {
                 System.out.println(" calling jxt undo ");
                 undoManager.undo();
            }
            else if ("redo".equals(getName())) {
                 System.out.println(" calling jxt redo ");
                 undoManager.redo();
            }
            else if ("delete".equals(getName())) {
                System.out.println(" calling jxt delete ");
                delete();
           }

          }        
    }
    

   public void initActions() {
        // Register the actions that this class can handle.
		super.initActions();

        ActionMap map = getActionMap();
        undoManager = new ExaltoUndoManager(this);

        System.out.println(" xml treetable actionmap = " + map);
    
        
        map.put("print", new Actions("print"));
        map.put("find", new Actions("find"));
        map.put("undo", undoManager.getUndoAction());
        map.put("redo", undoManager.getRedoAction());
        map.put("undo", new Actions("undo"));
        map.put("redo", new Actions("redo"));
     
        map.put("delete", new Actions("delete"));
   
        KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");
      //  getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(findStroke, "find");

        KeyStroke undoStroke = KeyStroke.getKeyStroke("control Z");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(undoStroke, "undo");
        
        KeyStroke redoStroke = KeyStroke.getKeyStroke("control Y");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(redoStroke, "redo");

        KeyStroke delStroke = KeyStroke.getKeyStroke("control X");
//        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delStroke, "delete");
        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(delStroke, "delete");

        KeyStroke copyStroke = KeyStroke.getKeyStroke("control C");
//        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delStroke, "copy");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delStroke, "copy");
        
        KeyStroke pasteStroke = KeyStroke.getKeyStroke("control V");
//        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delStroke, "paste");
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(delStroke, "paste");

        addMouseListener(treeTableAdapter);
        treeTableAdapter.addUndoableEditListener(undoManager);
	   
   //   	   addMouseListener(renderer);

    }


    protected void initActionsAndBindings() {

        // Register the actions that this class can handle.
        ActionMap map = getActionMap();
        map.put("print", new Actions("print"));
  //      map.put("find", new Actions("find"));
    
	//OM	map.put(PACKALL_ACTION_COMMAND, createPackAllAction());
    //OM    map.put(PACKSELECTED_ACTION_COMMAND, createPackSelectedAction());
    //OM    map.put(HORIZONTALSCROLL_ACTION_COMMAND, createHorizontalScrollAction());
        // JW: this should be handled by the LF!
    //    KeyStroke findStroke = KeyStroke.getKeyStroke("control F");
    //    getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(findStroke, "find");
    }


            public TableCellRenderer getCellRenderer(int row, int col) {


			try {
			
		             ArrayList parentList = treeTableAdapter.getParentList();
		            
		             HashMap rowMapper = domTreeModel.getRowMapper();
		             int type = 0;
		             
		     		 ArrayList nlist = (ArrayList) rowMapper.get(new Integer(row));
		    
		     		String rowcol = "";
		     		
		     			try {

		     				rowcol = (String) nlist.get(0);
		     				
		     			} catch (RuntimeException e) {
							// TODO Auto-generated catch block
				//			e.printStackTrace();
		     				return indicatorRenderer;
		     			}
		
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
		 
		    		for(int t=0;t<ct;t++) {
		
		    			Arrays.sort(parts, new GridHelper.ColumnComparator());
		
		            int prw = Integer.parseInt(parts[t][0]);
		            int pcol = Integer.parseInt(parts[t][1]);
		            int ppx = Integer.parseInt(parts[t][2]);
		
		    		ExaltoXmlNode viewerNode = (ExaltoXmlNode) parentList.get(ppx);
		
		    		       if(prw == row)  {
		                       if(col == pcol) {
		    		
		                           int nodeType = viewerNode.getNodeType();
		
		    						if((nodeType == 1) || (nodeType == 9))
		    						{
		    								type = 0;         
		    						} 
		    						else if(nodeType == 2) {
		    							type = 2;
		    						}
		    						else if(nodeType == 10) {  //doctype
		    							type = 10;
		    						}
		    						else if(nodeType == 8) { //comment
		    							type = 8;
		    						}
		    						else if(nodeType == 7) {
		    							type = 7;
		    						}
		    						else if(nodeType == 4) {
		    							type = 4;
		    						}
		    						else {
		    								type = 1;
		    						}
		    						break;
		
		                       } else {
		                          type = 1;
		                       }
		                   }
		
		    		}
		
		            if(type == 0) {
		      //                    System.out.println(" in type ==0 ret tree "); 
		                    return this.renderer;
		                } 
		    			else if(type == 2) {
		    				return attributeRenderer;
		    			}
		    			else if(type == 10) {
		    				return  this.renderer;
		    			}
		    			else if(type == 8) {
		    				return  this.renderer;
				    	}
		    			else if(type == 7) {
		    				return  this.renderer;
		    			}
		    			else if(type == 4) { // cdata
		    				return  indicatorRenderer;
		    			}
		    			else {
		          //        System.out.println(" in else ret indic"); 
		    				return indicatorRenderer;
		              //       return super.getCellRenderer(row, col); 
		                   }
		                   
			} catch(Exception e) {
				e.printStackTrace();
			}          
				
			return 	indicatorRenderer;
         }
        
            
    /**
     * Overridden to account for row index mapping. 
     * {@inheritDoc}
     */
    public Object getValueAt(int row, int column) {
 //       return getModel().getValueAt(convertRowIndexToModel(row), 
  //              convertColumnIndexToModel(column));

	//	System.out.println(" in GVA row = " + row);
	//	System.out.println(" in GVA origrow = " + convertRowIndexToModel(row));
    	
                return this.treeTableAdapter.getValueAt(convertRowIndexToModel(row), column);
    }
    
    public boolean isCellEditable(int row, int column) {
        return this.treeTableAdapter.isCellEditable(row, column);
    }
    
    public void setValueAt(Object aValue, int row, int column) {
    	this.treeTableAdapter.setValueAt(aValue, row, column);
    }

    /**
     * Convert row index from view coordinates to model coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param row
     *            row index in view coordinates
     * @return row index in model coordinates
     */
    public int convertRowIndexToModel(int row) {
  //OM      return getFilters() != null ?  getFilters().convertRowIndexToModel(row): row;

		return row;

	}

    /**
     * Convert row index from model coordinates to view coordinates accounting
     * for the presence of sorters and filters.
     * 
     * @param row
     *            row index in model coordinates
     * @return row index in view coordinates
     */
    public int convertRowIndexToView(int row) {
   //OM     return getFilters() != null ? getFilters().convertRowIndexToView(row): row;

        return row;

	}
    
        
        protected class XmlTreeTableCellRenderer extends TreeTableCellRenderer  {

              private Icon    closedIcon = null;
	      private Icon    openIcon = null;
	      private Icon    leafIcon = null;
	      private Icon    expIcon = null;
	      private Icon    collapseIcon = null;
              private Icon    elementIcon = null;
              
        
              public XmlTreeTableCellRenderer(XmlTreeModel xmlModel) {
                  super((TreeTableModel) xmlModel);
                  setOverwriteRendererIcons(true);
            //      updateIcons();
                  
   //                TreeIconRenderer ren = new TreeIconRenderer(this, openIcon, closedIcon, leafIcon, expIcon, collapseIcon, JXmlTreeTable.this);
                   TreeIconRenderer ren = new TreeIconRenderer(this, openIcon, closedIcon, leafIcon, expIcon, collapseIcon, JXmlTreeTable.this);
           //        ren.addMouseListener(this);
            //       setCellRenderer(ren);
                  
              }  
              
              public Component getTableCellRendererComponent(JTable table,
                            Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                  
                    Component c = super.getTableCellRendererComponent(table,
                            value, isSelected, hasFocus, row, column);        
                    /*
                        JTree xrenderer = (JTree) getTree();
                        TreeCellRenderer cellRenderer = xrenderer.getCellRenderer();

                        System.out.println(" TCR type GTCRC = " + cellRenderer.getClass().getName());

                        if(cellRenderer.getClass().getName().equals("javax.swing.tree.DefaultTreeCellRenderer")) {
                            DefaultTreeCellRenderer dtcr = (DefaultTreeCellRenderer) cellRenderer;
                            dtcr.setIcon(elemIcon);
                        }
                     */   
                    return c;
              }

              
              
                      /**
         * tries to set the renderers icons. Can succeed only if the
         * delegate is a DefaultTreeCellRenderer.
         * THINK: how to update? always override with this.icons, only
         * if renderer's icons are null, update this icons if they are not,
         * update all if only one is != null.... ??
         * 
         */
        public void updateIcons() {
            if (!isOverwriteRendererIcons()) return;

	       closedIcon = elemIcon;
	       openIcon = elemIcon;
	
	       URL expUrl = xutils.getResource("expImage");	
	       URL colUrl = xutils.getResource("collapseImage");	
	       
	       leafIcon = textIcon;
	       
	       expIcon = new ImageIcon(expUrl);
	       collapseIcon = new ImageIcon(colUrl);

               
                TreeCellRenderer tcr = getCellRenderer();

             System.out.println(" in update icons iconrend type = " + tcr.getClass().getName());

    //       if (tcr.getClass().getName().equals(javax.swing.tree.DefaultTreeCellRenderer) {

           if (tcr.getClass().getName().equals("org.jdesktop.swingx.JXTree$DelegatingRenderer")) {
                 
             System.out.println(" in update icons iconrend ");

             JXTree.DelegatingRenderer dtcr = ((JXTree.DelegatingRenderer)tcr);

                TreeCellRenderer tcren = dtcr.getDelegateRenderer();
                

                if(tcren instanceof javax.swing.tree.DefaultTreeCellRenderer) {
                     dtcr.setClosedIcon(closedIcon);
                     dtcr.setOpenIcon(openIcon);
      //        dtcr.setClosedIcon(null);
      //       dtcr.setOpenIcon(null);

                    dtcr.setLeafIcon(leafIcon);
         //   setExpandedIcon(openIcon);
         //    setCollapsedIcon(closedIcon);
                }
          }

        }

    public void mousePressed(MouseEvent e) {
        Point p = e.getPoint();
        
        int r = treeTable.rowAtPoint(p);
        int c = treeTable.columnAtPoint(p);
        treeTable.setRowSelectionInterval(r,r);
        treeTable.setRowSelectionInterval(c,c);
        
        treeTableAdapter.mousePressed(e);        
    }
    
    public void mouseClicked(MouseEvent e) {
        treeTableAdapter.mouseClicked(e);        
    }
    
    public void mouseEntered(MouseEvent e) {

    }
    
    public void mouseExited(MouseEvent e) {

    }
    
    public void mouseReleased(MouseEvent e) {
   //        	   System.out.println(" in mouseReleased ");
        }

    }
        
    /**
     * Overrides superclass version to provide support for cell decorators.
     *
     * @param renderer the <code>TableCellRenderer</code> to prepare
     * @param row the row of the cell to render, where 0 is the first row
     * @param column the column of the cell to render, where 0 is the first column
     * @return the <code>Component</code> used as a stamp to render the specified cell
     */
    
    public Component prepareRenderer(TableCellRenderer renderer, int row,
        int column) {

        Component component = super.prepareRenderer(renderer, row, column);
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        ComponentAdapter    adapter = getComponentAdapter();
        adapter.row = row;
        adapter.column = column;
        
        return applyRenderer(component, adapter); 
    }

        /**
     * Returns the adapter that knows how to access the component data model.
     * The component data adapter is used by filters, sorters, and highlighters.
     *
     * @return the adapter that knows how to access the component data model
     */

    public ComponentAdapter getComponentAdapter() {
        if (dataAdapter == null) {
            dataAdapter = new TreeTableDataAdapter(this); 
        }
        // MUST ALWAYS ACCESS dataAdapter through accessor method!!!
        return dataAdapter;
    }

        /**
     * Overridden to enable hit handle detection a mouseEvent which triggered
     * a expand/collapse. 
     */    
//OV21    protected void processMouseEvent(MouseEvent e) {
        
   //       System.out.println(" process mouse event ");
      /*    
                // BasicTableUI selects on released if the pressed had been 
        // consumed. So we try to fish for the accompanying released
        // here and consume it as wll. 
        if ((e.getID() == MouseEvent.MOUSE_RELEASED) && consumedOnPress) {
            consumedOnPress = false;
            e.consume();
            return;
        }
          
        System.out.println(" calling hacker ");
            
        if (getTreeTableHacker().hitHandleDetectionFromProcessMouse(e)) {
            // Issue #332-swing: hacking around selection loss.
            // prevent the
            // _table_ selection by consuming the mouseEvent
            // if it resulted in a expand/collapse
            consumedOnPress = true;
            e.consume();
            return;
        }
        consumedOnPress = false;
     */   
 //OV21       super.processMouseEvent(e);

 //OV21   }
   /*OV21 
        protected TreeTableHacker getTreeTableHacker() {
        if (treeTableHacker == null) {
            treeTableHacker = createTreeTableHacker();
        }
        return treeTableHacker;
    }
    */
    
    protected TreeTableHacker createTreeTableHacker() {
//        return new TreeTableHacker();
        return new TreeTableHackerExt2();
    }
    
    
        /**
     * 
     * Note: currently this class looks a bit funny (only overriding
     * the hit decision method). That's because the "experimental" code
     * as of the last round moved to stable. But I expect that there's more
     * to come, so I leave it here.
     * 
     * <ol>
     * <li> hit handle detection in processMouse
     * </ol>
     */
    public class TreeTableHackerExt2 extends TreeTableHackerExt {

        /**
         * Here: returns true.
         * @inheritDoc
         */
        protected boolean isHitDetectionFromProcessMouse() {
         //   System.out.println(" pmouse ret true ");
            return true;
        }
        
       protected void completeEditing() {
            if (isEditing()) {
                System.out.println(" done editing ###############################################");
          //      getCellEditor().cancelCellEditing();
             }
        }

               /**
         * Entry point for hit handle detection called from processMouse.
         * Does nothing if isHitDetectionFromProcessMouse is false. 
         * 
         * @return true if the mouseEvent triggered an expand/collapse in
         *   the renderer, false otherwise. 
         *   
         * @see #processMouseEvent(MouseEvent)
         * @see #isHitDetectionFromProcessMouse()
                *
                * /*   
            if(e.getClickCount() >= 2) {
                System.out.println(" hacker2 cc > 2 ");

                 
                        Point p = e.getPoint();
                
                        int row = rowAtPoint(p);
                
                        TableCellRenderer tabcr = getCellRenderer(row,col);
                
                        if(tabcr instanceof JTree) {
                            JTree tree = (JTree) tabcr;
                            TreeCellRenderer tcr = tree.getCellRenderer();
                            if(tcr instanceof DefaultTreeCellRenderer) {
                                DefaultTreeCellRenderer dtcr = (DefaultTreeCellRenderer) tcr;
                                Rectangle r = tree.getRowBounds(row);
                                System.out.println(" evt x = " + p.x);
                                System.out.println(" evt y = " + p.y);

                                System.out.println(" rect x = " + r.x);
                                System.out.println(" rect y = " + r.y);

                                Rectangle cr = dtcr.getBounds();

                                System.out.println(" click on dtcr x = " + cr.x);
                                System.out.println(" click on dtcr y = " + cr.y);

                                System.out.println(" click on dtcr HTP = " + dtcr.getHorizontalTextPosition());
                                System.out.println(" click on dtcr ITG = " + dtcr.getIconTextGap());
                                System.out.println(" click on dtcr VTP = " + dtcr.getVerticalTextPosition());

                            }    
                    }
                   hitHandleDetectionFromEditCell(col, new EventObject(e));
                }

           */ 
           
        public boolean hitHandleDetectionFromProcessMouse(MouseEvent e) {
           /*
            if (!isHitDetectionFromProcessMouse())
                return false;
           
            int col = columnAtPoint(e.getPoint());
         
            boolean hit = ((col >= 0) && expandOrCollapseNode(columnAtPoint(e
                    .getPoint()), e));
           */
            boolean hit = super.hitHandleDetectionFromProcessMouse(e);
            
            if(hit) {
                // handle clicked
      //          System.out.println(" ********************handle clicked******************** ");
               treeTableAdapter.treeStateChanged(e);
            
            }
            
            return hit;
        }

    }

    // search support
        //search support
    public void find() {    	
    	System.out.println(" inside find of jxmltreetable ++++++++");
    	
        Searchable searchable = getSearchable();
        SearchFactory.getInstance().showFindInput(this.parent, (JComponent) this, searchable);
		stopCellEditing();
  			// TODO: OV xpath from find dialog c 210409 
  		//	SearchFactory.getInstance().showFindInput(parent, this, getXPathSearchable());
        searchable = null;
    }

    // PREREL A FR PRINT
        //print support
    public void printPreview(ActionEvent e) {    	
    	System.out.println(" inside printPreview of jxmltreetable ++++++++");
    	
		

    }

    public void print(ActionEvent e) {    	
    	System.out.println(" inside printPreview of jxmltreetable ++++++++");
    	
    	try {
			super.print();
		} catch (PrinterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

    }

    
     public boolean stopCellEditing() {
	    try {
	      int column = editingColumn;
	      if (column > -1) {
	        TableCellEditor cellEditor = getColumnModel().getColumn(column
	).getCellEditor();
	        if ( cellEditor == null ) {
	          cellEditor = getDefaultEditor(getColumnClass(column));
	        }
	        if ( cellEditor != null ) {
	   	System.out.println(" stopCellEditing");
	        	
	        	 cellEditor.stopCellEditing(); 
	        	 
	        	 }
	      }
	    } catch (Exception e) {
	      return false;
	    }
	    return true;
  }

  
    public Searchable getSearchable() {
        if (searchable == null) {
        //OM240815	  searchable = new TableSearchable();
        }
        return searchable;
    }

    
/**OM	
    public XPathTableSearchable getXPathSearchable(String xpathExpr, ExaltoXmlNode xpathNode) {
    	XPathTableSearchable searchable = null;
    	
        if (searchable == null) {
        	//OV commented 210409 for xpath
//            searchable = new TableSearchable();
        	 searchable = new XPathTableSearchable(xpathExpr, xpathNode);
        }
        return searchable;
    }
	*/

   /* OV c 210409 
    public void setSearchable(Searchable searchable) {
        this.searchable = searchable;
    }
    */
    
    
    /* OV c 210409
	public void reset() {
    //		System.out.println(" find dialog dismissed ");    	
        
        	if(searchable != null && searchable.getReset()) {
        		this.searchable.reset();
        		searchable.mustReset(false);
    		}
    }	
    */

	/**OM
    public class XPathTableSearchable {

        private XPathSearchHighlighter searchHighlighter;
        String xpathExpr;
        ExaltoXmlNode xpathNode;
        
        public XPathTableSearchable(String xpathExpr, ExaltoXmlNode xpathNode) {
        	this.xpathExpr = xpathExpr;
            this.xpathNode = xpathNode;
        }
        
         // stores the result of the previous search.
        protected SearchResult lastSearchResult = new SearchResult();


        protected void findMatchAndUpdateState(int startRow, boolean backwards) {
                	
              	
            System.out.println(" in TS FMAUS ");
                	
            SearchResult matchRow = null;
            	
  	            System.out.println(" forwards ");
           	
                for (int r = startRow; r <= getSize() && matchRow == null; r++) {
                    matchRow = findMatchForwardInRow(r);
                    updateState(matchRow);
                }
            
            // KEEP - JW: Needed to update if loop wasn't entered!
            // the alternative is to go one off in the loop. Hmm - which is
            // preferable?
            // updateState(matchRow);
    	 

        }

        protected SearchResult findExtendedMatch(int row) {
            return findMatchAt(row, lastSearchResult.foundColumn);
        }

        private SearchResult findMatchForwardInRow(int row) {
        	
        	System.out.println(" in TS FMFIR ");
           
            int startColumn = (lastSearchResult.foundColumn < 0) ? 0 : lastSearchResult.foundColumn;
            if (isValidIndex(row)) {
            	
            	System.out.println(" in if isValidIndex row =  " + row);
           
            	
                for (int column = startColumn; column < getColumnCount(); column++) {
                    SearchResult result = findMatchAt(row, column);
                    if (result != null)
                        return result;
                }
            }
            return null;
        }

        private SearchResult findMatchBackwardsInRow(int row) {
            int startColumn = (lastSearchResult.foundColumn < 0) ? getColumnCount() - 1
                    : lastSearchResult.foundColumn;
            if (isValidIndex(row)) {
                for (int column = startColumn; column >= 0; column--) {
                    SearchResult result = findMatchAt(row, column);
                    if (result != null)
                        return result;
                }
            }
            return null;
        }

        protected SearchResult findMatchAt(int row, int column) {
       
           	System.out.println(" in TS FMA row =  " + row);
           	System.out.println(" in TS FMA column =  " + column);
 	
        	
            Object value = getValueAt(row, column);
           
           	System.out.println(" value =  " + value);


            NodeMatcher nmatch = new NodeMatcher(xpathExpr, treeTableAdapter, xpathNode, row, column);
            
            if (value != null) {
  //              Matcher matcher = new Matcher(pattern, value.toString());
                
                
                if (nmatch.find()) {

       	           	System.out.println(" found match ");
                    return createSearchResult(nmatch, row, column);
                }
            }
            return null;
        }
        
        
         // Factory method to create a SearchResult from the given parameters.
         // 
         // @param matcher the matcher after a successful find. Must not be null.
         // @param row the found index
         // @param column the found column
         // @return newly created <code>SearchResult</code>
         
        protected SearchResult createSearchResult(NodeMatcher matcher, int row, int column) {
            return new SearchResult(xpathExpr, 
                    matcher.toXPathResult(), row, column);
        }


        protected int adjustStartPosition(int startIndex, boolean backwards) {
            lastSearchResult.foundColumn = -1;

            if (startIndex < 0) {
                if (backwards) {
                    return getSize() - 1;
                } else {
                    return 0;
                }
            }
            return startIndex;
  //          return super.adjustStartPosition(startIndex, backwards);
        }

        protected int moveStartPosition(int startRow, boolean backwards) {
            if (backwards) {
                lastSearchResult.foundColumn--;
                if (lastSearchResult.foundColumn < 0) {
                    startRow--;
                }
            } else {
                lastSearchResult.foundColumn++;
                if (lastSearchResult.foundColumn >= getColumnCount()) {
                    lastSearchResult.foundColumn = -1;
                    startRow++;
                }
            }
            return startRow;
        }
        
         
         // checks if index is in range: 0 <= index < getSize().
         // @param index possible start position that we will check for validity
         // @return <code>true</code> if given parameter is valid index
          
        protected boolean isValidIndex(int index) {
             return index >= 0 && index < getSize();
         }


        protected boolean isEqualStartIndex(final int startIndex) {
    
        	   if(isValidIndex(startIndex) && (startIndex == lastSearchResult.foundRow)) {
        		   
        		   if(isValidColumn(lastSearchResult.foundColumn)) {
        			   
        			   return true;
        		   }
        	   }
        	   
        	   return false;
        	
     //   	return super.isEqualStartIndex(startIndex)
     //               && isValidColumn(lastSearchResult.foundColumn);
        	
        	
        }

        private boolean isValidColumn(int column) {
            return column >= 0 && column < getColumnCount();
        }


        protected int getSize() {
            return getRowCount();
        }

		public void reset() {
    		System.out.println(" find dialog dismissed ");    	
        	getHighlighters().removeHighlighter(searchHighlighter);
  		}
 	
   

        protected void moveMatchMarker() {
        	
        	try {
			  	System.out.println(" in 3M ");
                         	
            int row = lastSearchResult.foundRow;
            int column = lastSearchResult.foundColumn;
            String pattern = lastSearchResult.pattern;
            
              	System.out.println(" in 3M row =" + row);
              	System.out.println(" in 3M column =" + column);
            
              	System.out.println(" in 3M markByHighlighter =" + markByHighlighter());
            
            if ((row < 0) || (column < 0)) {
                if (markByHighlighter()) {
                    getSearchHighlighter().setPattern(null);
                }
                return;
            }
            if (markByHighlighter()) {
            	
   			  	System.out.println(" in MBH true ");
	            Rectangle cellRect = getCellRect(row, column, true);
                if (cellRect != null) {
                    scrollRectToVisible(cellRect);
                }

                ensureInsertedSearchHighlighters();
                // TODO (JW) - cleanup SearchHighlighter state management
                getSearchHighlighter().setPattern(pattern);
                int modelColumn = convertColumnIndexToModel(column);
                
               	System.out.println(" in 3M modelColumn= " + modelColumn);
	            
                getSearchHighlighter().setHighlightCell(row, modelColumn);
                
            } else { // use selection
                changeSelection(row, column, false, false);
                if (!getAutoscrolls()) {
                    // scrolling not handled by moving selection
                    Rectangle cellRect = getCellRect(row, column, true);
                    if (cellRect != null) {
                        scrollRectToVisible(cellRect);
                    }
                }
            }
            
            } catch(Exception e) {
            	e.printStackTrace();
            }
            
        }

        private boolean markByHighlighter() {
 //           return Boolean.TRUE.equals(getClientProperty(MATCH_HIGHLIGHTER));
 			  return true;
        }


        private void ensureInsertedSearchHighlighters() {
        	try {
        	
            if (getHighlighters() == null) {
            	
 			  	System.out.println(" in GH null ");
 	
                setHighlighters(
                        new Highlighter[] { getSearchHighlighter() });

			} else if (!isInPipeline(getSearchHighlighter())) {
            	
    			  	System.out.println(" in SH !inpipeline ");
 	
                getHighlighters().addHighlighter(getSearchHighlighter());
            }
            
            } catch(Exception e) {
            	e.printStackTrace();
            }
        }

        private boolean isInPipeline(PatternHighlighter searchHighlighter) {
            Highlighter[] inPipeline = getHighlighters().getHighlighters();
            if ((inPipeline.length > 0) && 
               (searchHighlighter.equals(inPipeline[inPipeline.length -1]))) {

                    getHighlighters().removeHighlighter(searchHighlighter);
                        return false;
  //              return true;
            }
            getHighlighters().removeHighlighter(searchHighlighter);
            return false;
        }


        private XPathSearchHighlighter getSearchHighlighter() throws Exception {
            if (searchHighlighter == null) {
                searchHighlighter = createSearchHighlighter();
            }
            return searchHighlighter;
        }

        protected XPathSearchHighlighter createSearchHighlighter() throws Exception {

		//	System.out.println(" class type = " + SearchHighlighter.class);
			
			
	//		System.out.println(" class type new = " + renderer.getCellRenderer().getClass().getName());

		//	TreeCellRenderer treeCellRenderer = renderer.getCellRenderer();
			
		//	if(treeCellRenderer instanceof DelegatingRenderer) {
			
		//		DelegatingRenderer drender = (DelegatingRenderer) treeCellRenderer;
				
		//		TreeIconRenderer clistener = (TreeIconRenderer) drender.getDelegateRenderer();
			
				// OV modified to take 0 args 250409
			//	searchHighlighter = new SearchHighlighter((ChangeListener) renderer.getCellRenderer());        	
				
			    searchHighlighter = new XPathSearchHighlighter(xpathExpr, treeTableAdapter, xpathNode);
			
				// OV added 250409
				addHighlighter(searchHighlighter);
				
//			}
			
        	return searchHighlighter;
        }
        
		 // Update inner searchable state based on provided search result
         //
         // @param searchResult <code>SearchResult</code> that represents the new state 
         //  of this <code>AbstractSearchable</code>
        protected void updateState(SearchResult searchResult) {
            lastSearchResult.updateFrom(searchResult);
        }


	}
	*/
    


    public UndoManager getUndoManager() {
        return undoManager;
    }


    public void undo() {
    		System.out.println(" grid undo 0 ");
            undoManager.undo();
      }

    public void redo() {
    		System.out.println(" grid redo 0 ");
            undoManager.redo();
      }

  public void undo(ActionEvent evt) {
	System.out.println(" grid undo ");
        undoManager.undo();
  }
  
  public void redo(ActionEvent evt) {
       System.out.println(" grid redo ");
       undoManager.redo(); 	
  }

  public void delete() {
      System.out.println(" grid delete ");
      this.treeTableAdapter.deleteNode();
  }

   public void paste(ActionEvent evt) {
	System.out.println(" grid paste ");
        treeTableAdapter.paste(evt);
   }

   public void cut(ActionEvent evt) {
	System.out.println(" grid cut ");
        treeTableAdapter.cut(evt);
  }

    public void copy(ActionEvent evt) {
	System.out.println(" grid copy ");
        treeTableAdapter.copy(evt);
    }

    public void doSave(String fileName)   {
    
        try {
                System.out.println(" grid save");
                saveDomDocument(fileName);

            } catch(Exception e) {
               	e.printStackTrace();
            }

    }






    public void doSaveAs(File file)  {
        
        System.out.println(" grid saveas");

        		 // Set up an identity transformer to use as serializer.
	      try {

                      // use specific Xerces class to write DOM-data to a file:
                    XMLSerializer serializer = new XMLSerializer();
                    serializer.setOutputCharStream(
                        new OutputStreamWriter(new FileOutputStream(file.getAbsolutePath()), "UTF8"));
                    serializer.serialize(this.treeTableAdapter.getDocument());


          } catch (java.io.UnsupportedEncodingException ue) {
            ue.printStackTrace();
            this.treeTableAdapter.fireStatusChanged("document.save.error", ColWidthTypes.ERR);

		} catch(java.io.FileNotFoundException fo) {
            fo.printStackTrace();
            this.treeTableAdapter.fireStatusChanged("document.save.error", ColWidthTypes.ERR);
        }
        catch(Exception fo) {
            fo.printStackTrace();
        }

    }

    private void saveDomDocument(String fileName)  {
        

        try {
        // use specific Xerces class to write DOM-data to a file:
        XMLSerializer serializer = new XMLSerializer();
        serializer.setOutputCharStream(
            new OutputStreamWriter(new FileOutputStream(fileName), "UTF8"));
        serializer.serialize(this.treeTableAdapter.getDocument());

        } catch(Exception e) {
            
            this.treeTableAdapter.fireStatusChanged("document.save.error", ColWidthTypes.ERR);

        }

    }


    public void checkValid() {
    	System.out.println(" grid checkValid");
//            treeTableAdapter.copy(evt);
        }

    public void checkWellFormed() {
    	System.out.println(" grid checkWellformed");
//            treeTableAdapter.copy(evt);
        }

    public Document getDocument() {
    	return domTreeModel.getDocument(); 	
    }

// grid ops
    
    public void insertNode(String type) {
    	treeTableAdapter.insertNode(type); 	
    }

    public void deleteNode(String type) {
    	if(type.equals("Element"))
    		treeTableAdapter.deleteNode();
    	else if(type.equals("Attr"))
    		treeTableAdapter.deleteAttr();
    	else if(type.equals("Text"))
    		treeTableAdapter.deleteText();

    }

    public void renameNode() {
    	treeTableAdapter.renameNode(); 	
    }
    
    public void expandNode() {
//    	treeTableAdapter.expandNode(); 	
    }

    public void expandAll(String actType) {
    	treeTableAdapter.expandAll(actType); 	
    }

    public ActionsHandler getActionsHandler() {
    	 return treeTableAdapter; 	
    }
    
    /**
     * A convenience class to hold search state.
     * NOTE: this is still in-flow, probably will take more responsibility/
     * or even change altogether on further factoring
     */
    public static class SearchResult {
        public int foundRow;
        public int foundColumn;
        XPATHResult xpathResult;
        public String pattern;

        public SearchResult() {
            reset();
        }
        
        public void updateFrom(SearchResult searchResult) {
            if (searchResult == null) {
                reset();
                return;
            }
            foundRow = searchResult.foundRow;
            foundColumn = searchResult.foundColumn;
            xpathResult = searchResult.xpathResult;
            pattern = searchResult.pattern;
        }

        public String getRegEx() {
            return pattern != null ? pattern : null;
        }

        public SearchResult(String ex, XPATHResult result, int row, int column) {
            pattern = ex;
            xpathResult = result;
            foundRow = row;
            foundColumn = column;
        }
        
        public void reset() {
            foundRow= -1;
            foundColumn = -1;
            xpathResult = null;
            pattern = null;
        }   
    }

    
    public void propertyChange(PropertyChangeEvent pe) {
    	
    	System.out.println( "newval = " + pe.getNewValue().getClass().getName());
    	
   // 	com.exalto.UI.grid.xpath.JaxenXPathEvaluator.PropertyChangeObject pobj = (com.exalto.UI.grid.xpath.JaxenXPathEvaluator.PropertyChangeObject) pe.getNewValue();

   //     com.exalto.UI.grid.xpath.ApacheXPathEvaluator.PropertyChangeObject pobj = (com.exalto.UI.grid.xpath.ApacheXPathEvaluator.PropertyChangeObject) pe.getNewValue();

        PropertyChangeObject pobj = (PropertyChangeObject) pe.getNewValue();

    	ExaltoXmlNode enode = pobj.getFoundNode();
    	
    	String xpathExpr = pobj.getXpathExpression();
    	
		/**OM
    	XPathTableSearchable searchable = getXPathSearchable(xpathExpr, enode);
    	
    	searchable.findMatchAndUpdateState(0, false);
    	searchable.moveMatchMarker();
		*/
    	
    	
    }
    
	/*
	boolean editing;
	
	public boolean isEditing() {
		return true;
	}

	public void setEditing(boolean editing) {
		this.editing = editing;
	}
	*/
	
/*
    public Color getselectionBackground() {
        return super.selectionBackground;
    }
*/
    
}
