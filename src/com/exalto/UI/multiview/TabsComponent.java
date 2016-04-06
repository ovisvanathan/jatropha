package com.exalto.UI.multiview;

/*
  * The contents of this file are subject to the terms of the Common Development
  * and Distribution License (the License). You may not use this file except in
  * compliance with the License.
  *
  * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
  * or http://www.netbeans.org/cddl.txt.
  *
  * When distributing Covered Code, include this CDDL Header Notice in each file
  * and include the License file at http://www.netbeans.org/cddl.txt.
  * If applicable, add the following below the CDDL Header, with the fields
  * enclosed by brackets [] replaced by your own identifying information:
  * "Portions Copyrighted [year] [name of copyright owner]"
  *
  * The Original Software is NetBeans. The Initial Developer of the Original
  * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
  * Microsystems, Inc. All Rights Reserved.
  */
 
import com.exalto.ColWidthTypes;
 import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.JToggleButton.ToggleButtonModel;
import javax.swing.border.Border;
import javax.swing.text.Keymap;

import org.openide.util.Lookup;
import org.openide.util.actions.CallbackSystemAction;

import com.exalto.UI.grid.JXmlTreeTable;
import com.exalto.UI.grid.xpath.GridXPathToolBar;
import com.exalto.UI.mdi.MultiViewInternalFrame;
import com.exalto.UI.multiview.MultiViewModel.ElementSelectionListener;
import com.exalto.util.SwingWorker;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.StringWriter;
import java.util.ArrayList;
 
 
 /**
  * Temporary solution tomultiview tabs..
  * @author mkleint
  */
 public class TabsComponent extends JPanel implements ElementSelectionListener {
     
     private JComponent EMPTY;
     private final static String  TOOLBAR_MARKER = "MultiViewPanel"; //NOI18N
 
     MultiViewModel model;
     private ActionListener listener;
     private MouseListener buttonMouseListener = null;
     private JComponent toolbarPanel;
     private JPanel componentPanel;
     private CardLayout cardLayout;
     private Set  alreadyAddedElements;
     private JToolBar bar;
     
     MultiViewInternalFrame gridDisplayer;
     
     private static final boolean AQUA = "Aqua".equals(UIManager.getLookAndFeel().getID()); //NOI18N
 
     private boolean toolbarVisible = true;
     
     JComponent innerbar;

     ArrayList comboItems;

  	 JXmlTreeTable gridTable = null;
     
     /** Creates a new instance of TabsComponent */
     public TabsComponent(MultiViewInternalFrame gridDisplayer, boolean toolVis) {
         super();
         bar = AQUA ? new TB() : new JToolBar();
         Border  b = (Border )UIManager.get("Nb.Editor.Toolbar.border"); //NOI18N
       //  bar.setBorder(b);
         
         this.gridDisplayer = gridDisplayer;
         
         bar.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
          
         bar.setFloatable(false);
         bar.setFocusable(true);
         
         setLayout(new BorderLayout());
 //        add(bar, BorderLayout.NORTH);
         
         this.setBackground(Color.white);
         bar.setBackground(Color.WHITE);

         add("North", bar);
         


         startToggling();
         setToolbarBarVisible(toolVis);
     }
     
     
     private static void createAndShowGUI(){
    	 
    	 
         try {
			JFrame aFrame = new JFrame("Swing Thread Example:  Broken Threading");
			 
			 
			 TabsComponent aPanel = new TabsComponent(null, true);
			 
			 MultiViewDescription desc1 = new MVTextDesc("text", null, 0, new MVTextElem());
			 MultiViewDescription desc2 = new MVTableDesc("grid", null, 0, new MVTableElem());

			 MultiViewDescription[] descs = new MultiViewDescription[] { desc1, desc2 };
			 MultiViewTopComponent tc = (MultiViewTopComponent) MultiViewFactory.createMultiView(descs, desc1);
			 
			 MultiViewModel mvm = new MultiViewModel(descs, desc1, tc);  
			   
			 aPanel.setModel(mvm);
			 
			 aFrame.getContentPane().add(aPanel);                              
			 aFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			 
			 
			 
			// aFrame.pack();
			 
			 aFrame.setSize(500, 500);
			 
			 aFrame.setVisible(true);
			 
			 
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
     }
     
     public static void main(String args[]){
       
  	   javax.swing.SwingUtilities.invokeLater(new Runnable() {
         public void run() {
             createAndShowGUI();
         }
     });
     }


     
     public void setModel(MultiViewModel model) {
         if (this.model != null) {
             bar.removeAll();
         }
         this.model = model;
    
         model.addElementSelectionListener(this);
         
         componentPanel = new JPanel();
         
         componentPanel.setBorder(BorderFactory.createEtchedBorder());
        // componentPanel.setBorder(BorderFactory.createEmptyBorder(2,2,2,2));
         
         cardLayout = new CardLayout();
         componentPanel.setLayout(cardLayout);
     
         componentPanel.setBackground(Color.WHITE);
         
       //  add(componentPanel, BorderLayout.CENTER);
         add("Center", componentPanel);
         
         alreadyAddedElements = new HashSet ();
         
         
         MultiViewDescription[] descs = model.getDescriptions();
         MultiViewDescription def = model.getActiveDescription();

         for (int i = 0; i < descs.length; i++) {
             MultiViewDescription desc = descs[i]; 
        	 MultiViewElement elem = (MultiViewElement) desc.getElement();
        	 cardLayout.addLayoutComponent(elem.getVisualRepresentation(), desc.getDisplayName());
         }

         
         MultiViewDescription adesc = model.getActiveDescription();
         this.switchToCard(adesc, adesc.getDisplayName());

         GridBagLayout grid = new GridBagLayout();
         bar.setLayout(grid);
         JToggleButton active = null;
         int prefHeight = -1;
         int prefWidth = -1;
         for (int i = 0; i < descs.length; i++) {
             JToggleButton button = createButton(descs[i]);
             model.getButtonGroup().add(button);
             GridBagConstraints cons = new GridBagConstraints();
             cons.anchor = GridBagConstraints.WEST;
             prefHeight = Math.max(button.getPreferredSize().height, prefHeight);
             bar.add(button, cons);
             prefWidth = Math.max(button.getPreferredSize().width, prefWidth);
             if (descs[i] == model.getActiveDescription()) {
                 active = button;
                 
             }
         }
         Enumeration  en = model.getButtonGroup().getElements();
         while (en.hasMoreElements()) {
             JToggleButton but = (JToggleButton)en.nextElement();
             Insets ins = but.getBorder().getBorderInsets(but);
             but.setPreferredSize(new Dimension(prefWidth + 10, prefHeight));
             but.setMinimumSize(new Dimension(prefWidth + 10, prefHeight));
             
         }
     
         if (active != null) {
             active.setSelected(true);
         }
         
         toolbarPanel = getEmptyInnerToolBar();
         GridBagConstraints cons = new GridBagConstraints();
         cons.anchor = GridBagConstraints.EAST;
         cons.fill = GridBagConstraints.BOTH;
         cons.gridwidth = GridBagConstraints.REMAINDER;
         cons.weightx = 1;
 
         toolbarPanel.setBackground(Color.WHITE);
         
         bar.add(toolbarPanel, cons);
         
  //       createInnerToolBar();
         
   //      setInnerToolBar(adesc);
         
     }
 

     void createInnerToolBar(JXmlTreeTable gridTable) {
   
  // OV commented for xpath search  210409 	 
 //   	 innerbar = new GridToolBar(gridTable);
        
    	 if(gridTable != null)
  //  		 innerbar = new GridXPathToolBar(gridTable, gridTable.getActionsHandler());
             innerbar = GridXPathToolBar.getInstance(gridTable, gridTable.getActionsHandler());

    	 
        // innerbar.setLayout(new FlowLayout());
/*         
         GridBagLayout grid = new GridBagLayout();
         innerbar.setLayout(grid);
         int prefHeight = -1;
         int prefWidth = -1;
         for (int i = 0; i < 5; i++) {
             JButton button = createButton("butt" + i);
           //  model.getButtonGroup().add(button);
             GridBagConstraints cons = new GridBagConstraints();
             cons.anchor = GridBagConstraints.WEST;
             prefHeight = Math.max(button.getPreferredSize().height, prefHeight);
             innerbar.add(button, cons);
             prefWidth = Math.max(button.getPreferredSize().width, prefWidth);
           
         }
*/         
    //     Enumeration  en = model.getButtonGroup().getElements();
    //     while (en.hasMoreElements()) {
    //         JToggleButton but = (JToggleButton)en.nextElement();
    //         Insets ins = but.getBorder().getBorderInsets(but);
    //         but.setPreferredSize(new Dimension(prefWidth + 10, prefHeight));
    //         but.setMinimumSize(new Dimension(prefWidth + 10, prefHeight));
             
    //     }

         
         
     }
     
     public void switchToCard(MultiViewDescription desc, String  id) {
    	 
    	 boolean isGrid = false;
         if(gridDisplayer != null) {
        
        	 if(id.equals("grid")) {
        		 isGrid = true;
	        	 gridTable = gridDisplayer.createOrUpdateTreeTable();

                 MVTableElem tblElem = null;
                 
                 if(gridTable == null)
                     tblElem = new MVTableElem();
                 else
                    tblElem = new MVTableElem(gridTable);

	        	 desc = new MVTableDesc("grid", null, 0, tblElem);
	        	 cardLayout.addLayoutComponent(new JScrollPane(tblElem.getVisualRepresentation()), desc.getDisplayName());

        	 } else {

                if(gridTable != null) 
                    gridDisplayer.loadFromDom(gridTable.getDocument());

                

                 if(innerbar != null && innerbar instanceof JToolBar) {
                     GridXPathToolBar gtb = (GridXPathToolBar) innerbar;

                     comboItems = gtb.getComboItems();

                 }
             }
         }
        
         MultiViewElement elem = desc.getElement();
         if (! alreadyAddedElements.contains(elem)) {
       // 	 if(!isGrid)
        		 componentPanel.add(new JScrollPane(elem.getVisualRepresentation()), id);
      //  	 else
      //  		 componentPanel.add(elem.getVisualRepresentation(), id);
        	 
             alreadyAddedElements.add(elem);
         }
         
         cardLayout.show(componentPanel, id);
         
         if(this.toolbarVisible)
        	 setInnerToolBar(desc, gridTable);
 
     }

    public ArrayList getComboItems() {
    
        GridXPathToolBar gtb = null;

        if(comboItems == null) {

            if(innerbar != null && innerbar instanceof JToolBar) {
                gtb = (GridXPathToolBar) innerbar;
                comboItems = gtb.getComboItems();
            }

        }

        return comboItems;
    }
     
     void changeActiveManually(MultiViewDescription desc) {
         Enumeration  en = model.getButtonGroup().getElements();
         while (en.hasMoreElements()) {
             JToggleButton obj = (JToggleButton)en.nextElement();
             
             if (obj.getModel() instanceof TabsComponent.TabsButtonModel) {
                 TabsButtonModel btnmodel = (TabsButtonModel)obj.getModel();
                 if (btnmodel.getButtonsDescription().equals(desc)) {
                     obj.setSelected(true);
                     MultiViewElement elem = model.getElementForDescription(desc);
                     switchToCard(desc, desc.getDisplayName());
                     elem.getVisualRepresentation().requestFocus();
                     
                     break; 
                 }
             }
         }
     }
     
     
     void changeActiveManually(Object src, MultiViewDescription desc) {

    	    System.out.println(" in CAM 2A ");
    	    if(src instanceof JToggleButton) {
    	    
	    	 	JToggleButton obj = (JToggleButton) src;
	             
	             if (obj.getModel() instanceof TabsComponent.TabsButtonModel) {
	                 TabsButtonModel btnmodel = (TabsButtonModel)obj.getModel();
	
	                 System.out.println(" Butt desc = " + btnmodel.getButtonsDescription().getDisplayName());
	
	                 if (btnmodel.getButtonsDescription().equals(desc)) {
	                     obj.setSelected(true);
	                     MultiViewElement elem = model.getElementForDescription(desc);
	                     switchToCard(desc, desc.getDisplayName());
	                     elem.getVisualRepresentation().requestFocus();
	                 }
	             }
    	    } else if(src instanceof JButton) {
    	    	 
    	         MultiViewElement elem = model.getElementForDescription(desc);
                 switchToCard(desc, desc.getDisplayName());
                 elem.getVisualRepresentation().requestFocus();
                 model.setActiveDescription(desc);
    	    }
     }

     void changeVisibleManually(MultiViewDescription desc) {
         Enumeration  en = model.getButtonGroup().getElements();
         while (en.hasMoreElements()) {
             JToggleButton obj = (JToggleButton)en.nextElement();
             
             if (obj.getModel() instanceof TabsComponent.TabsButtonModel) {
                 TabsButtonModel btnmodel = (TabsButtonModel)obj.getModel();
                 if (btnmodel.getButtonsDescription().equals(desc)) {
                     obj.setSelected(true);
                     break;
                 }
             }
         }
     }
     
     private JToggleButton createButton(MultiViewDescription description) {
         final JToggleButton button = new JToggleButton(description.getDisplayName());
         button.setModel(new TabsButtonModel(description));
         button.setRolloverEnabled(true);
         Border  b = (getButtonBorder());
         if (b != null) {
            button.setBorder(b);
         }
           
         if (buttonMouseListener == null) {
             buttonMouseListener = new ButtonMouseListener();
         }

         button.addMouseListener (buttonMouseListener);
         
         Font font = button.getFont();
         FontMetrics fm = button.getFontMetrics(font);
         int height = fm.getHeight();
 
         //HACK start - now find the global action shortcut
 Keymap  map = (Keymap )Lookup.getDefault().lookup(Keymap .class);
         KeyStroke  stroke = null;
         KeyStroke  stroke2 = null;
 //in tests map can be null, that's why the check..
 if (map != null) {
             // map is null in tests..
 Action[] acts = map.getBoundActions();
             for (int i = 0; i < acts.length;i++) {
                 if (acts[i] instanceof CallbackSystemAction) {
                     CallbackSystemAction sa = (CallbackSystemAction)acts[i];
                     if ("NextViewAction".equals(sa.getActionMapKey())) { //NOI18N
 KeyStroke [] strokes = map.getKeyStrokesForAction(acts[i]);
                         if (strokes != null && strokes.length > 0) {
                             stroke = strokes[0];
                         }
                     }
                     if ("PreviousViewAction".equals(sa.getActionMapKey())) { //NOI18N
 KeyStroke [] strokes = map.getKeyStrokesForAction(acts[i]);
                         if (strokes != null && strokes.length > 0) {
                             stroke2 = strokes[0];
                         }
                     }
                 }
             }
         }
         //HACK end
		//	 String  key1 = stroke == null ? "" : KeyEvent.getKeyModifiersText(stroke.getModifiers()) + "+" + KeyEvent.getKeyText(stroke.getKeyCode());//NOI18N
		//	 String  key2 = stroke2 == null ? "" : KeyEvent.getKeyModifiersText(stroke2.getModifiers()) + "+" + KeyEvent.getKeyText(stroke2.getKeyCode());//NOI18N
		//	 button.setToolTipText(ExaltoResource.getMessage("Config", "TabButton.tooltip"));,//NOI18N
		//	 				// description.getDisplayName(), 
		//	                               key1,
		//	                               key2));
			                               
         button.setToolTipText("TT");
         button.setFocusable(true);
         button.setFocusPainted(true);
         return button;
     }
     
     
     private JButton createButton(String name) {
    	 
         final JButton button = new JButton(name);
         Border  b = (getButtonBorder());
         if (b != null) {
            button.setBorder(b);
         }
         
         int prefHeight = 0;
         int prefWidth = 0;
         prefHeight = Math.max(button.getPreferredSize().height, prefHeight);
         prefWidth = Math.max(button.getPreferredSize().width, prefWidth);

         button.setPreferredSize(new Dimension(prefWidth + 10, prefHeight));
         button.setMinimumSize(new Dimension(prefWidth + 10, prefHeight));

         button.setToolTipText("TT");
         button.setFocusable(true);
         button.setFocusPainted(true);
         return button;
     }
     
     
 
     void setInnerToolBar(MultiViewDescription desc, JXmlTreeTable gridTable) {
 

    		 if(innerbar == null)
    			 createInnerToolBar(gridTable);
    		 
	    		 synchronized (getTreeLock()) {

	    			 if (toolbarPanel != null) {
	    				 bar.remove(toolbarPanel);
	    			 }
	
	             if (innerbar == null) {
	            	 innerbar = getEmptyInnerToolBar();
	             }
	     
	             if (!(innerbar instanceof JToolBar)) {
	             	 createInnerToolBar(gridTable);
	             }
	             
		             if(desc.getDisplayName().equals("grid")) {
		             
			             innerbar.putClientProperty(TOOLBAR_MARKER, "X"); //NOI18N
			             // need to set it to null, because CloneableEditor set's the border for the editor bar part only..
			             if (!AQUA) {
			                 innerbar.setBorder(null);
			             } else {
			                 innerbar.setBorder (BorderFactory.createEmptyBorder(2, 0, 2, 0));
			             }
			             
			             toolbarPanel = innerbar;
			             if (toolbarPanel != null) {
			                 GridBagConstraints cons = new GridBagConstraints();
			                 cons.anchor = GridBagConstraints.EAST;
			                 cons.fill = GridBagConstraints.BOTH;
			                 cons.weightx = 1;
			                 toolbarPanel.setMinimumSize(new Dimension(10, 10));
			                 cons.gridwidth = GridBagConstraints.REMAINDER;
			                 
			                 bar.add(toolbarPanel, cons);
			             }
			             // rootcycle is the tabscomponent..
			 // toolbarPanel.setFocusCycleRoot(false);
			             bar.revalidate();
			             bar.repaint();
			         } else {
			        	 	
			        	 	 innerbar = getEmptyInnerToolBar();
				             toolbarPanel = innerbar;
				             if (toolbarPanel != null) {
				                 GridBagConstraints cons = new GridBagConstraints();
				                 cons.anchor = GridBagConstraints.EAST;
				                 cons.fill = GridBagConstraints.BOTH;
				                 cons.weightx = 1;
				                 toolbarPanel.setMinimumSize(new Dimension(10, 10));
				                 cons.gridwidth = GridBagConstraints.REMAINDER;
				                 
				                 bar.add(toolbarPanel, cons);
				             }
				             			        	 
  			        	     bar.revalidate();
				             bar.repaint();
			         }
			        	 

	             
	    		 } 
     }
     
     void setToolbarBarVisible(boolean visible) {
         if (toolbarVisible == visible) {
             return;
         }
         toolbarVisible = visible;
         bar.setVisible(visible);
     }
     
     
     
     JComponent getEmptyInnerToolBar() {
         if (EMPTY == null) {
             EMPTY = new JPanel();
         }
         return EMPTY;
     }
     
     
     void requestFocusForSelectedButton() {
         bar.setFocusable(true);
         Enumeration  en = model.getButtonGroup().getElements();
         while (en.hasMoreElements()) {
             JToggleButton but = (JToggleButton)en.nextElement();
             if (model.getButtonGroup().isSelected(but.getModel())) {
                 but.requestFocus();
                 return;
             }
         }
         throw new IllegalStateException ("How come none of the buttons is selected?");
     }
 
     void requestFocusForPane() {
         bar.setFocusable(false);
         componentPanel.requestFocus();
     }
     
     
     private Border  buttonBorder = null;
     private boolean isMetal = false;
     private boolean isWindows = false;
     private Border  getButtonBorder() {
         if (buttonBorder == null) {
             //For some lf's, core will supply one
 buttonBorder = UIManager.getBorder ("nb.tabbutton.border"); //NOI18N
 }
         
         return buttonBorder;
     }
     
     public static boolean isXPTheme () {
         Boolean  isXP = (Boolean )Toolkit.getDefaultToolkit().
                         getDesktopProperty("win.xpstyle.themeActive"); //NOI18N
 return isXP == null ? false : isXP.booleanValue();
     }
     
   
     void startToggling() {
         ActionMap map = bar.getActionMap();
         Action act = new TogglesGoEastAction();
         // JToolbar action name
 map.put("navigateRight", act);
         InputMap input = bar.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
         
         act = new TogglesGoWestAction();
         // JToolbar action name
 map.put("navigateLeft", act);
         
         act = new TogglesGoDownAction();
         map.put("TogglesGoDown", act);
         // JToolbar action name
 map.put("navigateUp", act);
         KeyStroke  stroke = KeyStroke.getKeyStroke("ESCAPE"); //NOI18N
         input.put(stroke, "TogglesGoDown");
         
        KeyStroke  strokeE = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0); //NOI18N
         input.put(strokeE, "navigateRight");
         
         KeyStroke  strokeW = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0); //NOI18N
         input.put(strokeW, "navigateLeft");
         

     }
 
     
     private class TogglesGoWestAction extends AbstractAction {
         
         public void actionPerformed(ActionEvent e) {
             MultiViewDescription[] descs = model.getDescriptions();
             MultiViewDescription active = model.getActiveDescription();
             for (int i = 0; i < descs.length; i++) {
                 if (descs[i] == active) {
                     int next = i - 1;
                     if (next < 0) {
                         next = descs.length - 1;
                     }
                     changeActiveManually(descs[next]);
                     requestFocusForSelectedButton();
                 }
             }
         }
     }
     
     private class TogglesGoEastAction extends AbstractAction {
         
         public void actionPerformed(ActionEvent e) {
             MultiViewDescription[] descs = model.getDescriptions();
             MultiViewDescription active = model.getActiveDescription();
             for (int i = 0; i < descs.length; i++) {
                 if (descs[i] == active) {
                     int next = i + 1;
                     if (next >= descs.length) {
                         next = 0;
                     }
                     changeActiveManually(descs[next]);
                     requestFocusForSelectedButton();
                 }
             }
         }
     }
 
     private class TogglesGoDownAction extends AbstractAction {
         
         public void actionPerformed(ActionEvent e) {
             changeActiveManually(model.getActiveDescription());
             model.getActiveElement().getVisualRepresentation().requestFocusInWindow();
         }
     }
     
     
 /**
  * used in 
  */ 
     static class TabsButtonModel extends ToggleButtonModel {
 
         private MultiViewDescription desc;
         public TabsButtonModel(MultiViewDescription description) {
             super();
             desc = description;
         }
         
         public MultiViewDescription getButtonsDescription() {
             return desc;
         }
     }
     
     class ButtonMouseListener extends MouseAdapter {
         public void mouseEntered(MouseEvent e) {
             AbstractButton b = (AbstractButton)e.getComponent();
             b.getModel().setRollover(true);
         }
         public void mouseExited(MouseEvent e) {
             AbstractButton b = (AbstractButton)e.getComponent();
             b.getModel().setRollover(false);
         }
         
         /** for user triggered clicks, do activate the current element..
             make it on mousePressed to be in synch with the topcpomponent activation code in the winsys impl #68505
          */
         public void mousePressed(MouseEvent e) {
             AbstractButton b = (AbstractButton)e.getComponent();
             MultiViewModel model = TabsComponent.this.model;
             if (model != null) {
 //                model.getButtonGroup().setSelected(b.getModel(), true);
                 model.fireActivateCurrent(e);
             }
 
         }
         
     } 
     
     
     public void selectionChanged(MultiViewDescription oldOne, MultiViewDescription newOne) {

    	    System.out.println(" in selChg ");

    	 	MultiViewDescription[] descs = model.getDescriptions();
            MultiViewDescription active = model.getActiveDescription();
            for (int i = 0; i < descs.length; i++) {
                if (descs[i] == active) {
                    int next = i - 1;
                    if (next < 0) {
                        next = descs.length - 1;
                    }
                    changeActiveManually(descs[next]);
                }
            }
        
 	 }
     
     public void selectionActivatedByButton(MouseEvent e) {
 	 	MultiViewDescription[] descs = model.getDescriptions();
        MultiViewDescription active = model.getActiveDescription();
        for (int i = 0; i < descs.length; i++) {
            
        	System.out.println(" in SABB i = " + i);

        	System.out.println(" in descs[i] = " + descs[i].getDisplayName());
            
        	if (descs[i] == active) {
        		int next = i - 1;
                if (next < 0) {
                    next = descs.length - 1;
                }

                System.out.println(" in if next = " + next);
                System.out.println(" in if descs[next] = " + descs[next]);
                System.out.println(" call CAM = ");

                changeActiveManually(e.getSource(), descs[next]);
            }
        }
    	 
     }

     
     public void selectionActivatedByButton(ActionEvent e) {
  	 	MultiViewDescription[] descs = model.getDescriptions();
         MultiViewDescription active = model.getActiveDescription();
         for (int i = 0; i < descs.length; i++) {
             
         	System.out.println(" in SABB i = " + i);

         	System.out.println(" in descs[i] = " + descs[i].getDisplayName());
             
         	if (descs[i] == active) {
         		int next = i - 1;
                 if (next < 0) {
                     next = descs.length - 1;
                 }

                 System.out.println(" in if next = " + next);
                 System.out.println(" in if descs[next] = " + descs[next]);
                 System.out.println(" call CAM = ");

                 changeActiveManually(e.getSource(), descs[next]);
             }
         }
     	 
      }

	 class ToggleAction extends AbstractAction {
	        
		 ToggleAction() {
	            super();
	            setEnabled(true);
	        }
	        	        
	        
	        public void actionPerformed(final ActionEvent evt) {

     		//	logger.debug(" New action performed");

             		final SwingWorker worker = new SwingWorker() {
                     	
             	    	public Object construct() {
             	            
             	    		boolean retVal = false;
             	    		   
             	            System.out.println(" in CA actionPerformed evt src " + evt.getSource().getClass().getName());

             	            

             	                         	            
             	    		return null;
             	        }
             	    	
             	 	    //Runs on the event-dispatching thread.
                         public void finished() {

                         }
             	    };        	

                     worker.start();

         }
	        
	    }

     
     
     private static final class TB extends JToolBar {
         private boolean updating = false;
         
         public TB() {
             //Aqua UI will look for this value to ensure the
 //toolbar is tall enough that the "glow" which paints
 //outside the combo box bounds doesn't make a mess painting
 //into other components
 setName("editorToolbar");
         }
         
         public void setBorder (Border  b) {
             if (!updating) {
                 return;
             }
             super.setBorder(b);
         }
         
         public void updateUI() {
             updating = true;
             try {
                 super.updateUI();
             } finally {
                 updating = false;
             }
         }
         
         public String  getUIClassID() {
             return UIManager.get("Nb.Toolbar.ui") == null ?
                 super.getUIClassID() : "Nb.Toolbar.ui";
         }
     }


	public MultiViewModel getModel() {
		return model;
	}
	
	
	public String getCurrentView() {
	   	  
		String view = model.getActiveDescription().getDisplayName();
		
		return view.equals("grid")? "GRIDVIEW" : "TEXTVIEW";
	} 
	
	
 }
