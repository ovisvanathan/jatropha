package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.renderer.StringValues;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.rollover.RolloverRenderer;
import org.jdesktop.swingx.tree.DefaultXTreeCellRenderer;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class TreeTableCellRenderer extends JXTree implements
        TableCellRenderer
        // need to implement RolloverRenderer
        // PENDING JW: method name clash rolloverRenderer.isEnabled and
        // component.isEnabled .. don't extend, use? And change
        // the method name in rolloverRenderer? 
        // commented - so doesn't show the rollover cursor.
        // 
//      ,  RolloverRenderer 
        {
        private PropertyChangeListener rolloverListener;
        private Border cellBorder;

        /**
         * Key for clientProperty to decide whether to apply hack around #168-jdnc.
         */
        public static final String DRAG_HACK_FLAG_KEY = "tree.dragHackFlag";
        /**
         * Key for clientProperty to decide whether to apply hack around #766-swingx.
         */
        public static final String DROP_HACK_FLAG_KEY = "tree.dropHackFlag";
        
        // Force user to specify TreeTableModel instead of more general
        // TreeModel
        public TreeTableCellRenderer(TreeTableModel model) {
            super(model);
            putClientProperty("JTree.lineStyle", "None");
            setRootVisible(false); // superclass default is "true"
            setShowsRootHandles(true); // superclass default is "false"
                /**
                 * TODO: Support truncated text directly in
                 * DefaultTreeCellRenderer.
                 */
            // removed as fix for #769-swingx: defaults for treetable should be same as tree
//            setOverwriteRendererIcons(true);
// setCellRenderer(new DefaultTreeRenderer());
            setCellRenderer(new ClippedTreeCellRenderer());
        }

        
        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to hack around #766-swingx: cursor flickering in DnD
         * when dragging over tree column. This is a core bug (#6700748) related
         * to painting the rendering component on a CellRendererPane. A trick
         * around is to let this return false. <p>
         * 
         * This implementation applies the trick, that is returns false always. 
         * The hack can be disabled by setting the treeTable's client property
         * DROP_HACK_FLAG_KEY to Boolean.FALSE. 
         * 
         */
        @Override
        public boolean isVisible() {
            return shouldApplyDropHack() ? false : super.isVisible();
        }


        /**
         * Returns a boolean indicating whether the drop hack should be applied.
         * 
         * @return a boolean indicating whether the drop hack should be applied.
         */
        protected boolean shouldApplyDropHack() {
            return !Boolean.FALSE.equals(treeTable.getClientProperty(DROP_HACK_FLAG_KEY));
        }


        /**
         * Hack around #297-swingx: tooltips shown at wrong row.
         * 
         * The problem is that - due to much tricksery when rendering the tree -
         * the given coordinates are rather useless. As a consequence, super
         * maps to wrong coordinates. This takes over completely.
         * 
         * PENDING: bidi?
         * 
         * @param event the mouseEvent in treetable coordinates
         * @param row the view row index
         * @param column the view column index
         * @return the tooltip as appropriate for the given row
         */
        String getToolTipText(MouseEvent event, int row, int column) {
            if (row < 0) return null;
            String toolTip = null;
            TreeCellRenderer renderer = getCellRenderer();
            TreePath     path = getPathForRow(row);
            Object       lastPath = path.getLastPathComponent();
            Component    rComponent = renderer.getTreeCellRendererComponent
                (this, lastPath, isRowSelected(row),
                 isExpanded(row), getModel().isLeaf(lastPath), row,
                 true);

            if(rComponent instanceof JComponent) {
                Rectangle       pathBounds = getPathBounds(path);
                Rectangle cellRect = treeTable.getCellRect(row, column, false);
                // JW: what we are after
                // is the offset into the hierarchical column 
                // then intersect this with the pathbounds   
                Point mousePoint = event.getPoint();
                // translate to coordinates relative to cell
                mousePoint.translate(-cellRect.x, -cellRect.y);
                // translate horizontally to 
                mousePoint.translate(-pathBounds.x, 0);
                // show tooltip only if over renderer?
//                if (mousePoint.x < 0) return null;
//                p.translate(-pathBounds.x, -pathBounds.y);
                MouseEvent newEvent = new MouseEvent(rComponent, event.getID(),
                      event.getWhen(),
                      event.getModifiers(),
                      mousePoint.x, 
                      mousePoint.y,
//                    p.x, p.y, 
                      event.getClickCount(),
                      event.isPopupTrigger());
                
                toolTip = ((JComponent)rComponent).getToolTipText(newEvent);
            }
            if (toolTip != null) {
                return toolTip;
            }
            return getToolTipText();
        }

        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to not automatically de/register itself from/to the ToolTipManager.
         * As rendering component it is not considered to be active in any way, so the
         * manager must not listen. 
         */
        @Override
        public void setToolTipText(String text) {
            putClientProperty(TOOL_TIP_TEXT_KEY, text);
        }

        /**
         * Immutably binds this TreeTableModelAdapter to the specified JXTreeTable.
         * For internal use by JXTreeTable only.
         *
         * @param treeTable the JXTreeTable instance that this renderer is bound to
         */
        public final void bind(JXTreeTable2 treeTable) {
            // Suppress potentially subversive invocation!
            // Prevent clearing out the deck for possible hijack attempt later!
            if (treeTable == null) {
                throw new IllegalArgumentException("null treeTable");
            }

            if (this.treeTable == null) {
                this.treeTable = treeTable;
                // commented because still has issus
//                bindRollover();
            }
            else {
                throw new IllegalArgumentException("renderer already bound");
            }
        }

        /**
         * Install rollover support.
         * Not used - still has issues.
         * - not bidi-compliant
         * - no coordinate transformation for hierarchical column != 0
         * - method name clash enabled
         * - keyboard triggered click unreliable (triggers the treetable)
         * ...
         */
        @SuppressWarnings("unused")
        private void bindRollover() {
            setRolloverEnabled(treeTable.isRolloverEnabled());
            treeTable.addPropertyChangeListener(getRolloverListener());
        }

        
        /**
         * @return
         */
        private PropertyChangeListener getRolloverListener() {
            if (rolloverListener == null) {
                rolloverListener = createRolloverListener();
            }
            return rolloverListener;
        }

        /**
         * Creates and returns a property change listener for 
         * table's rollover related properties. 
         * 
         * This implementation 
         * - Synchs the tree's rolloverEnabled 
         * - maps rollover cell from the table to the cell 
         *   (still incomplete: first column only)
         * 
         * @return
         */
        protected PropertyChangeListener createRolloverListener() {
            PropertyChangeListener l = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ((treeTable == null) || (treeTable != evt.getSource()))
                        return;
                    if ("rolloverEnabled".equals(evt.getPropertyName())) {
                        setRolloverEnabled(((Boolean) evt.getNewValue()).booleanValue());
                    }
                    if (RolloverProducer.ROLLOVER_KEY.equals(evt.getPropertyName())){
                        rollover(evt);
                    } 
                }

                private void rollover(PropertyChangeEvent evt) {
                    boolean isHierarchical = isHierarchical((Point)evt.getNewValue());
                    putClientProperty(evt.getPropertyName(), isHierarchical ? 
                           new Point((Point) evt.getNewValue()) : null);
                }
                
                private boolean isHierarchical(Point point) {
                    if (point != null) {
                        int column = point.x;
                        if (column >= 0) {
                            return treeTable.isHierarchical(column);
                        }
                    }
                   return false;
                }
                @SuppressWarnings("unused")
                Point rollover = new Point(-1, -1);
            };
            return l;
        }

        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to produce clicked client props only. The
         * rollover are produced by a propertyChangeListener to 
         * the table's corresponding prop.
         * 
         */
        @Override
        protected RolloverProducer createRolloverProducer() {
            return new RolloverProducer() {

                /**
                 * Overridden to do nothing.
                 * 
                 * @param e
                 * @param property
                 */
                @Override
                protected void updateRollover(MouseEvent e, String property, boolean fireAlways) {
                    if (CLICKED_KEY.equals(property)) {
                        super.updateRollover(e, property, fireAlways);
                    }
                }
                @Override
                protected void updateRolloverPoint(JComponent component,
                        Point mousePoint) {
                    JXTree tree = (JXTree) component;
                    int row = tree.getClosestRowForLocation(mousePoint.x, mousePoint.y);
                    Rectangle bounds = tree.getRowBounds(row);
                    if (bounds == null) {
                        row = -1;
                    } else {
                        if ((bounds.y + bounds.height < mousePoint.y) || 
                                bounds.x > mousePoint.x)   {
                               row = -1;
                           }
                    }
                    int col = row < 0 ? -1 : 0;
                    rollover.x = col;
                    rollover.y = row;
                }
                
            };
        }

        
        @Override
        public void scrollRectToVisible(Rectangle aRect) {
            treeTable.scrollRectToVisible(aRect);
        }

        @Override
        protected void setExpandedState(TreePath path, boolean state) {
            // JW: fix for #1126 - CellEditors are removed immediately after starting an
            // edit if they involve a change of selection and the 
            // expandsOnSelection property is true
            // back out if the selection change does not cause a change in 
            // expansion state
            if (isExpanded(path) == state) return;
            // on change of expansion state, the editor's row might be changed
            // for simplicity, it's stopped always (even if the row is not changed)
            treeTable.getTreeTableHacker().completeEditing();
            super.setExpandedState(path, state);
            treeTable.getTreeTableHacker().expansionChanged();
            
        }

        /**
         * updateUI is overridden to set the colors of the Tree's renderer
         * to match that of the table.
         */
        @Override
        public void updateUI() {
            super.updateUI();
            // Make the tree's cell renderer use the table's cell selection
            // colors.
            // TODO JW: need to revisit...
            // a) the "real" of a JXTree is always wrapped into a DelegatingRenderer
            //  consequently the if-block never executes
            // b) even if it does it probably (?) should not 
            // unconditionally overwrite custom selection colors. 
            // Check for UIResources instead. 
            TreeCellRenderer tcr = getCellRenderer();
            if (tcr instanceof DefaultTreeCellRenderer) {
                DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer) tcr);
                // For 1.1 uncomment this, 1.2 has a bug that will cause an
                // exception to be thrown if the border selection color is null.
                dtcr.setBorderSelectionColor(null);
                dtcr.setTextSelectionColor(
                    UIManager.getColor("Table.selectionForeground"));
                dtcr.setBackgroundSelectionColor(
                    UIManager.getColor("Table.selectionBackground"));
            }
        }

        /**
         * Sets the row height of the tree, and forwards the row height to
         * the table.
         * 
         *
         */
        @Override
        public void setRowHeight(int rowHeight) {
            // JW: can't ... updateUI invoked with rowHeight = 0
            // hmmm... looks fishy ...
//            if (rowHeight <= 0) throw 
//               new IllegalArgumentException("the rendering tree must have a fixed rowHeight > 0");
            super.setRowHeight(rowHeight);
            if (rowHeight > 0) {
                if (treeTable != null) {
                    treeTable.adjustTableRowHeight(rowHeight);
                }
            }
        }


        /**
         * This is overridden to set the location to (0, 0) and set
         * the dimension to exactly fill the bounds of the hierarchical
         * column.<p>
         */
        @Override
        public void setBounds(int x, int y, int w, int h) {
            // location is relative to the hierarchical column
            y = 0;
            x = 0;
            if (treeTable != null) {
                // adjust height to table height
                // It is not enough to set the height to treeTable.getHeight()
                // JW: why not?
                h = treeTable.getRowCount() * this.getRowHeight();
                int hierarchicalC = treeTable.getHierarchicalColumn();
                // JW: re-introduced to fix Issue 1168-swingx
                if (hierarchicalC >= 0) {
                    TableColumn column = treeTable.getColumn(hierarchicalC);
                    // adjust width to width of hierarchical column
                    w = column.getWidth();
                }
            }
            super.setBounds(x, y, w, h);
        }

        /**
         * Sublcassed to translate the graphics such that the last visible row
         * will be drawn at 0,0.
         */
        @Override
        public void paint(Graphics g) {
            Rectangle cellRect = treeTable.getCellRect(visibleRow, 0, false);
            g.translate(0, -cellRect.y);

            hierarchicalColumnWidth = getWidth();
            super.paint(g);

            Border border = cellBorder;
            if (highlightBorder != null) {
                border = highlightBorder;
            }
            // Draw the Table border if we have focus.
            if (border != null) {
                // #170: border not drawn correctly
                // JW: position the border to be drawn in translated area
                // still not satifying in all cases...
                // RG: Now it satisfies (at least for the row margins)
                // Still need to make similar adjustments for column margins...
                border.paintBorder(this, g, 0, cellRect.y,
                        getWidth(), cellRect.height);
            }
        }
        
        /**
         * {@inheritDoc} <p>
         * 
         * Overridden to fix #swingx-1525: BorderHighlighter fills tree column.<p>
         * 
         * Basically, the reason was that the border is set on the tree as a whole
         * instead of on the cell level. The fix is to bypass super completely, keep 
         * a reference to the cell border and manually paint it around the cell 
         * in the overridden paint. <p>
         * 
         * Note: in the paint we need to paint either the focus border or the 
         * cellBorder, the former taking precedence.
         * 
         */
        @Override
        public void setBorder(Border border) {
            cellBorder = border;
        }


        public void doClick() {
            if ((getCellRenderer() instanceof RolloverRenderer)
                    && ((RolloverRenderer) getCellRenderer()).isEnabled()) {
                ((RolloverRenderer) getCellRenderer()).doClick();
            }
            
        }

        
        @Override
        public boolean isRowSelected(int row) {
            if ((treeTable == null) || (treeTable.getHierarchicalColumn() <0)) return false;
            return treeTable.isCellSelected(row, treeTable.getHierarchicalColumn());
        }


        @Override
        public Component getTableCellRendererComponent(JTable table,
            Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            assert table == treeTable;
            // JW: quick fix for the tooltip part of #794-swingx:
            // visual properties must be reset in each cycle.
            // reverted - otherwise tooltip per Highlighter doesn't work
            // 
//            setToolTipText(null);
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            else {
                setBackground(table.getBackground());
               setForeground(table.getForeground());
            }

            highlightBorder = null;
            if (treeTable != null) {
                if (treeTable.realEditingRow() == row &&
                    treeTable.getEditingColumn() == column) {
                }
                else if (hasFocus) {
                    highlightBorder = UIManager.getBorder(
                        "Table.focusCellHighlightBorder");
                }
            }
            visibleRow = row;

            return this;
        }

        private class ClippedTreeCellRenderer extends DefaultXTreeCellRenderer 
            implements StringValue 
            {
            @SuppressWarnings("unused")
            private boolean inpainting;
            private String shortText;
            @Override
            public void paint(Graphics g) {
                String fullText = super.getText();
        
                 shortText = SwingUtilities.layoutCompoundLabel(
                    this, g.getFontMetrics(), fullText, getIcon(),
                    getVerticalAlignment(), getHorizontalAlignment(),
                    getVerticalTextPosition(), getHorizontalTextPosition(),
                    getItemRect(itemRect), iconRect, textRect,
                    getIconTextGap());

                /** TODO: setText is more heavyweight than we want in this
                 * situation. Make JLabel.text protected instead of private.
         */

                try {
                    inpainting = true;
                    // TODO JW: don't - override getText to return the short version
                    // during painting
                    setText(shortText); // temporarily truncate text
                    super.paint(g);
                } finally {
                    inpainting = false;
                    setText(fullText); // restore full text
                }
            }

            
            private Rectangle getItemRect(Rectangle itemRect) {
                getBounds(itemRect);
//                LOG.info("rect" + itemRect);
                itemRect.width = hierarchicalColumnWidth - itemRect.x;
                return itemRect;
            }

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                return super.getTreeCellRendererComponent(tree, getHierarchicalTableValue(value), sel, expanded, leaf,
                        row, hasFocus);
            }


            /**
             * 
             * @param node the node in the treeModel as passed into the TreeCellRenderer
             * @return the corresponding value of the hierarchical cell in the TreeTableModel
             */
            private Object getHierarchicalTableValue(Object node) {
                Object val = node;
                
                if (treeTable != null) {
                    int treeColumn = treeTable.getTreeTableModel().getHierarchicalColumn();
                    Object o = null; 
                    if (treeColumn >= 0) {
                        // following is unreliable during a paint cycle
                        // somehow interferes with BasicTreeUIs painting cache
//                        o = treeTable.getValueAt(row, treeColumn);
                        // ask the model - that's always okay
                        // might blow if the TreeTableModel is strict in
                        // checking the containment of the value and 
                        // this renderer is called for sizing with a prototype
                        o = treeTable.getTreeTableModel().getValueAt(node, treeColumn);
                    }
                    val = o;
                }
                return val;
            }

            /**
             * {@inheritDoc} <p>
             */
            @Override
            public String getString(Object node) {
//                int treeColumn = treeTable.getTreeTableModel().getHierarchicalColumn();
//                if (treeColumn >= 0) {
//                    return StringValues.TO_STRING.getString(treeTable.getTreeTableModel().getValueAt(value, treeColumn));
//                }
                return StringValues.TO_STRING.getString(getHierarchicalTableValue(node));
            }

            // Rectangles filled in by SwingUtilities.layoutCompoundLabel();
            private final Rectangle iconRect = new Rectangle();
            private final Rectangle textRect = new Rectangle();
            // Rectangle filled in by this.getItemRect();
            private final Rectangle itemRect = new Rectangle();
        }

        /** Border to draw around the tree, if this is non-null, it will
         * be painted. */
        protected Border highlightBorder = null;
        protected JXTreeTable2 treeTable = null;
        protected int visibleRow = 0;

        // A JXTreeTable may not have more than one hierarchical column
        private int hierarchicalColumnWidth = 0;

    }
