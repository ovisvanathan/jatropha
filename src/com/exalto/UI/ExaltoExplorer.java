/*
 * ExaltoExplorer.java
 *
 * Created on July 16, 2008, 12:38 PM
 */

package com.exalto.UI;

//import com.exalto.UI.mdi.CloseProjOkCancelDialog;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.Scrollable;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
//import org.netbeans.swing.tabcontrol.TabbedContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import com.exalto.ColWidthTypes;
import com.exalto.UI.mdi.DesktopView;
import com.exalto.UI.mdi.ProjectInfo;
import com.exalto.UI.util.AutoScrollingJTree;
import com.exalto.UI.util.ExplorerNode;
import com.exalto.UI.util.XMLRoutines;
import com.exalto.util.ExaltoResource;
// PREREL  3 LINES
import com.exalto.util.StatusEvent;
import com.exalto.util.XmlUtils;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 *
 * @author  omprakash.v
 */
public class ExaltoExplorer extends javax.swing.JPanel implements Scrollable { 
    //implements PropertyChangeListener {

    XmlEditor parent;
    XmlUtils xutils;
    int _docseq;
    protected DefaultTreeCellEditor jTree1Editor;
    public ExplorerNode selectedProject;
    
    DesktopView dview;
    String workdir;

// OV a 190309
//    TabbedContainer tcon;
    
    boolean shouldAddToProjects = true;

    HashMap currentProjects = new HashMap();
    /** Creates new form ExaltoExplorer */
//    public ExaltoExplorer(XmlEditor parent, TabbedContainer dview) {
//        this.parent = parent;
//        xutils = XmlUtils.getInstance();
//        this.tcon = dview;
//        initComponents();
    
        
//		URL textUrl = xutils.getResource("textImage");

//        leafIcon = new ImageIcon(textUrl);

        
 //   }

   public ExaltoExplorer(XmlEditor parent) {

        this.parent = parent;
        xutils = XmlUtils.getInstance();
		// PREREL  1 LINES
		workdir = System.getProperty("user.dir");
    System.out.println("########################workdir 5###################"+workdir);
        initComponents();

 }
    
    /** Creates new form ExaltoExplorer */
    public ExaltoExplorer(XmlEditor parent, DesktopView dview) {
        this.parent = parent;
        xutils = XmlUtils.getInstance();
        this.dview = dview;
		// PREREL  1 LINES
		workdir = System.getProperty("user.dir");
    System.out.println("########################workdir 6###################"+workdir);
        
	        initComponents();

        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new AutoScrollingJTree(rootNode, this);
        loadProjects();

        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jScrollPane1.setPreferredSize(new Dimension(250, 700));
        jTree1.setAlignmentX(0.0F);
        jTree1.setAlignmentY(0.0F);
        jTree1.setRootVisible(false);
        jTree1.setShowsRootHandles(true);
        jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTree1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 2, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 248, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(0, 14, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 252, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 14, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void jTree1ValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_jTree1ValueChanged
// TODO add your handling code here:
//GEN-LAST:event_jTree1ValueChanged
}

private void jTree1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MouseClicked
// TODO add your handling code here:
//GEN-LAST:event_jTree1MouseClicked
    try {
        
    
    if(evt.getClickCount() >= 2) {
		
        ExplorerNode expNode = (ExplorerNode) jTree1.getSelectionPath().getLastPathComponent();
        
        if(expNode.isLeaf()) {

            String fpath = expNode.getFilePath();
            String fname = expNode.getFileName();
            
            File openFile = new File(fpath + File.separatorChar + fname);

            shouldAddToProjects = false;
            dview.display(openFile);
            shouldAddToProjects = true;
            
        }
    
     // PREREL SELECT PROJECT ON MC 090610   
    } else if(evt.getClickCount() == 1) {
		
		 ExplorerNode selectedNode = (ExplorerNode) jTree1.getSelectionPath().getLastPathComponent();
       	 String nn = selectedNode.getXmlNode().getNodeName();
				 
				   if(nn.equals("project")) { 
                     ExplorerNode oldProject = selectedProject;
                         selectedProject = selectedNode;
                     
				   System.out.println("Set as Main projec= = " + selectedProject);
                  	// this.firePropertyChange("SelectedProject", oldProject, selectedProject);
                     
								TreeNode [] tn = selectedProject.getPath();

								TreePath path = new TreePath(tn);

								jTree1.setSelectionPath(path);
								jTree1.repaint();
								jTree1.revalidate();
				}
		
	}
    
    } catch(Exception e) {
        e.printStackTrace();
    }

}
   

    private void loadProjects() {
        
        entries = getEntries();

        Node root = entries.getDocumentElement();

        ExplorerNode top = createTreeNode(root);

        DefaultTreeModel m_model = (DefaultTreeModel) jTree1.getModel();
        
        m_model.setRoot(top);

            
            this.selectedProject = (ExplorerNode) top.getFirstChild();

            if(selectedProject.getChildCount() > 0) {

                selectedNode = (ExplorerNode) selectedProject.getFirstChild();

                TreeNode [] tn = selectedNode.getPath();

                TreePath path = new TreePath(tn);

                jTree1.setSelectionPath(path);
            }

        
        TreeCellRenderer ren = jTree1.getCellRenderer();
        
      /*  
        TreeModel tm = jTree1.getModel();
        
        jTree1.setModel(new DefaultTreeModel(null));
        
        jTree1.setModel(tm);
     */
            
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
			// NEW

			public Component getTreeCellRendererComponent(JTree tree,
				Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
				// NEW


				// PREREL  BEGIN
                    ImageIcon textIcon = null;
                    ImageIcon xmlIcon = null;
                    ImageIcon xslIcon = null;
                    ImageIcon xsdIcon = null;
                    ImageIcon dtdIcon = null;
                    ImageIcon leafIcon = null;
                    ImageIcon htmlIcon = null;
				    ImageIcon foIcon = null;

                    URL textUrl = xutils.getResource("textImage");
                    textIcon = new ImageIcon(textUrl);

				    URL xmlUrl = xutils.getResource("xmlImage");
									xmlIcon = new ImageIcon(xmlUrl);

				    URL xslUrl = xutils.getResource("xslImage");
									xslIcon = new ImageIcon(xslUrl);

				    URL xsdUrl = xutils.getResource("xsdImage");
									xsdIcon = new ImageIcon(xsdUrl);

				    URL dtdUrl = xutils.getResource("dtdImage");
									dtdIcon = new ImageIcon(dtdUrl);

				    URL htmlUrl = xutils.getResource("htmlImage");
									htmlIcon = new ImageIcon(htmlUrl);

				//    URL foUrl = xutils.getResource("foImage");
				//		foIcon = new ImageIcon(foUrl);


					leafIcon = new ImageIcon(textUrl);

				// PREREL  END


				Component res = super.getTreeCellRendererComponent(tree,
					value, sel, expanded, leaf, row, hasFocus);

                if (value instanceof ExplorerNode) {
					Node node = ((ExplorerNode)value).getXmlNode();

					if (node instanceof Element) {
                                              
						setIcon(expanded ? openIcon : closedIcon);
                    //ExplorerNode selectedProject;
                           
						//   System.out.println("ren value= = " + value);
						//   System.out.println("ren selectedProject= = " + selectedProject);
						
						
							if(value == selectedProject) {

								res.setFont(Font.decode("Ariel-bold-12"));

							
 

							} else {
                                
								res.setFont(Font.decode("Ariel-normal-12"));
							
							}


							


                        
						if(node.getNodeName().equals("file")) {

			 				// PREREL  BEGIN

							Node doc= node.getFirstChild();

					  //      System.out.println("fileName = " + doc.getNodeName()); 

							if(doc.getNodeType() == Node.TEXT_NODE ) {
							
								String nv = doc.getNodeValue();
							
					  //      System.out.println("nv fileName = " + nv);
														


								if(nv.endsWith(".xml")){
									this.setIcon(xmlIcon);
								
								} else if(nv.endsWith(".xsl")){
								
									this.setIcon(xslIcon);
	
								} else  if(nv.endsWith(".xsd")){
								
									this.setIcon(xsdIcon);
								} else  if(nv.endsWith(".dtd")){
										this.setIcon(dtdIcon);
								
								} else  if(nv.endsWith(".txt")){
										this.setIcon(textIcon);
								
								} 
								else  if(nv.endsWith(".html") || nv.endsWith(".htm") ||
									nv.endsWith(".xhtml") || nv.endsWith(".xhtm")
								
								){
										this.setIcon(htmlIcon);	
								}
								else {
										this.setIcon(leafIcon);
								}


							// PREREL  END

								
							
							}

							
							
					//		this.setIcon(leafIcon);

						}
                        
                    } else
                        setIcon(leafIcon);
				}
                
				return res;
			}
		};
	
		jTree1.setCellRenderer(renderer);
		
		for (int i = 0; i < jTree1.getRowCount(); i++) {
	         jTree1.expandRow(i);
	}


    }
    
    private ExplorerNode createTreeNode(Node root) {

        String proj = root.getNodeName();
        String value = null;
		
		// PREREL  1 LINE
		boolean loadErr = false;
        
        if(proj.equals("project")) {
        
            NamedNodeMap nmap = root.getAttributes();
           
            value = nmap.item(0).getNodeValue();
        
		} else if(proj.equals("file")) {
              value = root.getFirstChild().getNodeValue();
			  System.out.println(" file Name = " + value); 

				// PREREL  BEGIN

				File pfile = new File(value);

				if(!pfile.exists()){
				
					System.out.println("!pfile.exists ");

					root.getParentNode().removeChild(root);

					parent.fireStatusChanged(new StatusEvent(ExaltoResource.getString(ColWidthTypes.ERR,"explore.file.load.err") + value,0, ColWidthTypes.ERROR));

				    loadErr = true;
				}
        }
        
		      ExplorerNode treeNode = null;

		      if(!loadErr)
                 treeNode = new ExplorerNode(root);
              
			  NodeList list = root.getChildNodes();
              for (int k=0; k<list.getLength(); k++) {
                    Node nd = list.item(k);

                    if(nd.getNodeType() == Node.ELEMENT_NODE) {
                        ExplorerNode child = createTreeNode(nd);
                        if (child != null) {
                           
							 System.out.println(" child Name = " + child.toString()); 
						
		  						treeNode.add(child);

						}
                    }
              }
				// PREREL  END
				
              return treeNode;
    }
    
    
    
    
//    private Map entries;

    
//We will call this method from our DataNode.
//When we do so, we parse the project.xml file
//and return org.w3c.dom.Node names to the DataNode:
// synchronized Map getEntries() {
synchronized Document getEntries() {
        
    if (entries == null) {
        entries = parseXMLProjectFile();
    }
    return entries;
}

 private Document parseXMLProjectFile() {
       
     Document doc = null;
        
    try {
    
        DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
        DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
        docbuilderfact.setValidating(false);

   //        String projFile = xutils.getResourceString("PROJECT_FILE");

			// PREREL  C 1 LN
  //        URL projurl = xutils.getResource("PROJECT_FILE");

		// PREREL  BEGIN

		String projFile = workdir + File.separatorChar + "project.xml";
   
   
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@projFile @@@@@@@@@@@@@@@@= " + projFile); 

        
    //      doc  = parseXml(projUrl);

			doc  = parseXml(projFile);
		// PREREL  END


    }
    catch (Exception ex) {
        ex.printStackTrace();
    }
          return doc;

 }
    
    private Map parseProjectFile() {
    try {
        
       /* 
        List sectionEntries = null;
    //    BufferedReader br = null;
        //Use the FileObject retrieved from the DataObject,
        //via DataObject.getPrimaryFile(), to get the input stream:
    //    br = new BufferedReader(new InputStreamReader(getPrimaryFile().getInputStream()));
    //    InputSource source = new InputSource(br);
        //You could use any kind of parser, depending on your file type,
        //though for XML files you can use the NetBeans IDE org.openide.xml.XMLUtil class
        //to convert your input source to a org.w3c.dom.Document object:
//        org.w3c.dom.Document doc = XMLUtil.parse(source, false, false, null, null);
  
        DocumentBuilderFactory docbuilderfact=DocumentBuilderFactory.newInstance();
        DocumentBuilder  docbuilder= docbuilderfact.newDocumentBuilder();
        docbuilderfact.setValidating(false);
	
   //        String projFile = xutils.getResourceString("PROJECT_FILE");
           
          URL projurl = xutils.getResource("PROJECT_FILE");
        
          Document doc = parseXml(projurl);

        ArrayList  fileEntries = null;
        org.w3c.dom.NodeList list = doc.getElementsByTagName("project");
        int length = list.getLength();
      
        if(length > 0)
            entries = new LinkedHashMap();
      
        for (int i = 0; i < length; i++) {

            org.w3c.dom.Node mainNode = list.item(i);

            NamedNodeMap nmap = mainNode.getAttributes();
            
            String value = nmap.item(0).getNodeValue();
            
            org.w3c.dom.NodeList fileList = mainNode.getChildNodes();    
            
            fileEntries = new ArrayList();
            for (int j = 0; j < fileList.getLength(); j++) {
            
                org.w3c.dom.Node fileNode = fileList.item(j);
                String fileName = null;
                if(fileNode.getNodeType() == Node.ELEMENT_NODE) {
                    fileName = fileNode.getFirstChild().getNodeValue();
                    fileEntries.add(fileName);
                }
            }
            
            //For purposes of this example, we simply put
            //the name of the node in our linked hashmap:
            entries.put(value, fileEntries);
        }
        */
   } catch (Exception ex) {
        ex.printStackTrace();
    }
    
//    return entries;
        return null;
}

	
    private Document parseXml(URL projUrl) {
        
        Document document = null;
					
					try {
						
//                                                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("projects.xml");
    
                                                 DocumentBuilderFactory dbf =
                                                    DocumentBuilderFactory.newInstance();

                                                  // Get parser
                                                  DocumentBuilder db = dbf.newDocumentBuilder();
                                                  // Get input source
                                                  // Parse input source
                                                  
                                                  try {
													document = db
															.parse(new InputSource(
																	projUrl
																			.openStream()));


                                                  Node top = document.getDocumentElement();


                                                           try {
              OutputFormat format = new OutputFormat(document);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(document);
        }
        catch (IOException e) {
          System.err.println(e);
        }

                                                  if(!top.hasChildNodes())
                                                    throw new Exception();
                                                  

												} catch (Exception e) {
													// TODO: handle exception
													
													String message = "<projects><project name=\"a\"></project></projects>";  // XML in a string

													DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
													DocumentBuilder builder = factory.newDocumentBuilder();
													document = builder.parse(new InputSource(new StringReader(message)));
													
												}
 
		
                                                
                                                
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
        
                                        return document;
    }
    

// PREREL  OVERRIDE FOR FILE
    private Document parseXml(String projFile) {
        
        Document document = null;
					
					try {
						
//                                                InputStream stream = this.getClass().getClassLoader().getResourceAsStream("projects.xml");
    
                                                 DocumentBuilderFactory dbf =
                                                    DocumentBuilderFactory.newInstance();

                                                  // Get parser
                                                  DocumentBuilder db = dbf.newDocumentBuilder();
                                                  // Get input source
                                                  // Parse input source
                                                  
                                                  try {

													FileInputStream fis = new FileInputStream(projFile);


													document = db
															.parse(new InputSource(
																	fis));


                                                  Node top = document.getDocumentElement();

															fis.close();

                                                           try {
              OutputFormat format = new OutputFormat(document);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(document);
        }
        catch (IOException e) {
          System.err.println(e);
        }

                                                  if(!top.hasChildNodes())
                                                    throw new Exception();
                                                  

												} catch (Exception e) {
													// TODO: handle exception
													
													String message = "<projects><project name=\"a\"></project></projects>";  // XML in a string

													DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
													DocumentBuilder builder = factory.newDocumentBuilder();
													document = builder.parse(new InputSource(new StringReader(message)));
													
												}
 
		
                                                
                                                
                                        } catch(Exception e) {
                                            e.printStackTrace();
                                        }
        
                                        return document;
    }
    



    public int addFile(String fileName, String replaceFile) {

        URL projurl = xutils.getResource("PROJECT_FILE");

        /*
        DefaultTreeModel model = (DefaultTreeModel) jTree1.getModel();
        
       if (selectedProject == null)
          return;

        ExplorerNode newNode = new ExplorerNode(fileName);
  
        model.insertNodeInto(newNode, selectedProject, selectedProject
          .getChildCount());
    
        // now display new node
        TreeNode[] nodes = model.getPathToRoot(newNode);
        TreePath path = new TreePath(nodes);
        jTree1.scrollPathToVisible(path);
  */
//////
        
           		if (entries == null)
        			return -1;

        if(shouldAddToProjects) {
            
                       if(selectedProject == null) {
                                   JOptionPane.showMessageDialog(this, 
                                      "Please set main project first",
                                        "Info", JOptionPane.INFORMATION_MESSAGE);
                                        return -1;    
                       }
                           
        
        ExplorerNode treeNode = selectedProject;
		// ExplorerNode treeNode = getSelectedTreeNode();
        
        if (treeNode == null)
			return -1;

        boolean fileExists = false;
        NodeList nlist = entries.getElementsByTagName("file");

        for(int i=0;i<nlist.getLength();i++) {

            Node n = nlist.item(i);

            Node t = n.getFirstChild();

                if(fileName.equals(t.getNodeValue())) {

                    if(
                            ((!(fileName.indexOf("XSL Result") > 0))
                           && (!(fileName.indexOf("XQuery Result") > 0)))
                              || !(fileName.equals(replaceFile)))  {
                        fileExists = true;
                        break;
                    }
                }
        }

        if(fileExists) {
           JOptionPane.showMessageDialog(this, "File with that name is already open", "Error", JOptionPane.ERROR_MESSAGE);
           return -1;
        }

 //       if(replaceFile == null)
                        
        Node parent = treeNode.getXmlNode();
		
        if (parent == null)
			return -1;

        System.out.println("fileName = " + fileName); 
                
        
	//	if (!isLegalXmlName(nn))
	//		return;

		try {

            java.util.Enumeration en = treeNode.children();

            while(en.hasMoreElements()) {

                ExplorerNode fileNode = (ExplorerNode) en.nextElement();

                Node fnode = fileNode.getXmlNode();

                Node tnode = fnode.getFirstChild();

                if(tnode.getNodeType() == Node.TEXT_NODE) {

				    if(tnode.getNodeValue().equals(replaceFile))  {
                        fileNode.remove(true);
                        DefaultTreeModel tm = (DefaultTreeModel) jTree1.getModel();
                        tm.removeNodeFromParent(fileNode);
                        break;
                   }
                }

            }

            // the below code is already checked. Using it to
            // replace existing node for rename
			NodeList nl = parent.getChildNodes();
			boolean shouldAdd = true;
/*
			for(int i=0;i<nl.getLength();i++) {
				
				Node child = nl.item(i);

		        System.out.println(" child Name = " + child.getNodeName()); 
		        System.out.println(" child type = " + child.getNodeType()); 

               Node tnode = child.getFirstChild();
		        
				if(tnode.getNodeType() == Node.TEXT_NODE) {
					
				//	if(tnode.getNodeValue().equals(fileName))
 				//		shouldAdd = false;
                    
              	//		break;

                    if(tnode.getNodeValue().equals(replaceFile))  {
                        treeNode.remove(true);

                        DefaultTreeModel tm = (DefaultTreeModel) jTree1.getModel();
                        tm.removeNodeFromParent(treeNode);

                        break;
                    }


				}
			}
*/
			
			Element newElement = entries.createElement("file");

			Text text = entries.createTextNode(fileName);
                       
            newElement.appendChild(text);
                        
			ExplorerNode nodeElement = new ExplorerNode(newElement);
		
            Node parNode = treeNode.getXmlNode();

            if(shouldAdd) {

            	treeNode.addXmlNode(nodeElement);

                TreeNode [] tn = treeNode.getPath();

        		TreePath tp = new TreePath(tn);

                jTree1.setSelectionPath(tp);

                selectedNode = treeNode;
                        
            	//                treeNode.addXmlNode(nodeElement);

            	DefaultTreeModel m_model = (DefaultTreeModel) jTree1.getModel();

				m_model.nodeStructureChanged(treeNode);	// Necessary to display your new node
				TreePath tpath = jTree1.getSelectionPath();
				if (tpath != null) {
					tpath = tpath.pathByAddingChild(nodeElement);
					jTree1.setSelectionPath(tpath);
					jTree1.scrollPathToVisible(tpath);
				}

				for (int i = 0; i < jTree1.getRowCount(); i++) {
			         jTree1.expandRow(i);
                }



            }
                        
                
		}
		catch (Exception ex) {
			ex.printStackTrace();
                        return -1;
		}

        }
        
        return 0;
        
    }
    
        
      	public boolean isLegalXmlName(String input) {
            if (input==null || input.length()==0)
                return false;
            if (!(XMLRoutines.isLegalXmlName(input))) {
                JOptionPane.showMessageDialog(this,
                    "Invalid XML name", "Projects",
                    JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
	}

// PREREL  M ADDEE VECTOR ARG
    public void shutDown(Vector unsaved) {
        
      //  DefaultTreeModel dfm = (DefaultTreeModel) jTree1.getModel();
        
      //  ExplorerNode root = (ExplorerNode) dfm.getRoot();
        saveFile(unsaved);
        
    }

// PREREL  M ADDEE VECTOR ARG
    private void saveFile(Vector unsaved) {
        
        if (entries == null)
            return;
	
        try {
            
          //            URL projurl = xutils.getResource("PROJECT_FILE");

			/*
				
                      String filePath = xutils.getResource("PROJECT_FILE").getPath();
                      filePath= filePath.replaceAll("%20", " ");
                      
                      filePath= filePath.replaceAll("%5c", "/");
				
			*/
			
			// PREREL  M TO LOAD FILE
				String filePath = workdir +  File.separatorChar + "project.xml";
						

                     System.out.println("filePath = " + filePath);

                      FileWriter out = new FileWriter(filePath);

                                  try {
              OutputFormat format = new OutputFormat(entries);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(entries);
        }
        catch (IOException e) {
          System.err.println(e);
        }

                      
                      XMLRoutines.write(entries, out, unsaved);
                      out.close();
            }
            catch (Exception ex) {
		ex.printStackTrace();
            }
            finally {
            }
	
//            m_xmlChanged = false;
  
    }

    public boolean addProject(ProjectInfo pinfo) {

        String pname = pinfo.getProjectName();


        try {
              OutputFormat format = new OutputFormat(entries);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(entries);
        }
        catch (IOException e) {
          System.err.println(e);
        }


        NodeList nl = entries.getElementsByTagName("project");

        for(int i=0;i<nl.getLength();i++) {
            
            Node proj = nl.item(i);
            
            NamedNodeMap nmp = proj.getAttributes();
            
            for(int j=0;j<nmp.getLength();j++) {
                
                Node a = nmp.item(j);
                
                if(a.getNodeName().equals("name") &&
                        a.getNodeValue().equals(pname))
                            return false;
                
            }
            
        }


        addProject(pname);

        System.out.println("projName added ");

       try {
              OutputFormat format = new OutputFormat(entries);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(entries);
        }
        catch (IOException e) {
          System.err.println(e);
        }


        return true;


        
	}

    private int addProject(String pname) {

            URL projurl = xutils.getResource("PROJECT_FILE");

        	if (entries == null)
        			return -1;

        if(shouldAddToProjects) {

            ExplorerNode treeNode = selectedProject;
		// ExplorerNode treeNode = getSelectedTreeNode();



            if (treeNode == null) {
                return -1;
            }


            Node sibling = treeNode.getXmlNode();

            if (sibling == null)
                return -1;

        System.out.println("projName = " + pname);


		try {

            ExplorerNode parent = (ExplorerNode) treeNode.getParent();

            Node parNode = parent.getXmlNode();


			Element newElement = entries.createElement("project");

            ExplorerNode nodeElement = new ExplorerNode(newElement);

			Attr attr =  entries.createAttribute("name");

            attr.setNodeValue(pname);
            
            ExplorerNode attrElement = new ExplorerNode(attr);

		//	attrElement.setOwnerElement(parNode);

            nodeElement.addAttrNode(attrElement, false);

			parent.addXmlNode(nodeElement);

            	//                treeNode.addXmlNode(nodeElement);
            selectedProject = nodeElement;

            	DefaultTreeModel m_model = (DefaultTreeModel) jTree1.getModel();

				m_model.nodeStructureChanged(parent);	// Necessary to display your new node
				TreePath tpath = jTree1.getSelectionPath();
				if (tpath != null) {
					tpath = tpath.pathByAddingChild(nodeElement);
					jTree1.setSelectionPath(tpath);
					jTree1.scrollPathToVisible(tpath);
				}

				for (int i = 0; i < jTree1.getRowCount(); i++) {
			         jTree1.expandRow(i);
			}


		}
		catch (Exception ex) {
			ex.printStackTrace();
                        return -1;
		}

        }

        return 0;

    }



    public void closeProject(boolean shouldDelete) {

        ExplorerNode treeNode = selectedProject;

       if(treeNode == null) {
           JOptionPane.showMessageDialog(this, "Please select a project first", "Error", JOptionPane.ERROR_MESSAGE);
           return;
       }


  //      System.out.println(" shouldDelete = " + shouldDelete);

   		try {

/*
            if(shouldDelete) {

                NodeList nl = treeNode.getXmlNode().getChildNodes();

                for(int i=0;i<nl.getLength();i++) {

                    Node child = nl.item(i);

                    if(child.getNodeName().equals("file")) {

                        Node tnode = child.getFirstChild();

                        String fname = tnode.getNodeValue();

                        File f = new File(fname);

                        f.delete();
                    }

                }


            }
*/

            TreeNode sibling = treeNode.getNextSibling();

            if(sibling == null)
               sibling = treeNode.getPreviousSibling();

            if(sibling != null)
                this.selectedProject = (ExplorerNode) sibling;

			treeNode.remove(shouldDelete);

            DefaultTreeModel tm = (DefaultTreeModel) jTree1.getModel();
            tm.removeNodeFromParent(treeNode);


		} catch (Exception ex) {
			ex.printStackTrace();
		}

        
	}

    public ExplorerNode getSelectedProject() {
        return selectedProject;
    }

    public void closeFile(String fname, boolean shouldDelete, boolean removeFromTree) {

        ExplorerNode treeNode = null;

        if(fname == null) {
            treeNode = getSelectedTreeNode();
        } else {

            int spos = fname.lastIndexOf("\\");
            
            
            if(spos < 0) {
                spos = fname.lastIndexOf("/");
            }

            if(spos > 0)
                fname = fname.substring(spos + 1);


           System.out.println(" fname = " + fname);


            TreePath tp = jTree1.getNextMatch(fname, 0, Position.Bias.Forward);
            treeNode = (ExplorerNode) tp.getLastPathComponent();

        }

        if(treeNode == null) {
           JOptionPane.showMessageDialog(this, "Please select a file to delete first", "Error", JOptionPane.ERROR_MESSAGE);
           return;
       }


        TreeNode treeParent = treeNode.getParent();
		int	index = treeParent.getIndex(treeNode);


       if(treeNode == null) {
           JOptionPane.showMessageDialog(this, "Please select a file to delete first", "Error", JOptionPane.ERROR_MESSAGE);
           return;
       }

        System.out.println(" shouldDelete = " + shouldDelete);

   		try {


            if(shouldDelete) {

                    Node child = treeNode.getXmlNode();

                    if(child.getNodeName().equals("file")) {

                        Node tnode = child.getFirstChild();

                        String name = tnode.getNodeValue();

                        File f = new File(name);

                        f.delete();
                    }


            }


 //           TreeNode nextNode = treeNode.getNextNode();
              TreeNode nextNode = treeNode.getNextSibling();

            if(nextNode == null)
   //             nextNode = treeNode.getPreviousNode();
                  nextNode = treeNode.getPreviousSibling();

            selectedNode = (ExplorerNode) nextNode;

            
            TreePath tp = null;

            if(selectedNode != null) {
            
                TreeNode [] tn = selectedNode.getPath();

                tp = new TreePath(tn);

            }


            if(removeFromTree)
    			treeNode.remove(removeFromTree);

          //  path = tree.getNextMatch(nodeName, 0, Position.Bias.Forward);
          //  mNode = (MutableTreeNode)path.getLastPathComponent();

            DefaultTreeModel tm = (DefaultTreeModel) jTree1.getModel();
            tm.removeNodeFromParent(treeNode);

            if(selectedNode != null)
                jTree1.setSelectionPath(tp);
            


            try {
              OutputFormat format = new OutputFormat(entries);
              XMLSerializer output = new XMLSerializer(System.out, format);
              output.serialize(entries);
        }
        catch (IOException e) {
          System.err.println(e);
        }
            

            

            /*
            int[] childIndices = new int[1];
    		Object[] removedChildren = new TreeNode[1];
        	removedChildren[0] = treeNode;
            childIndices[0] = index;

            nodesWereRemoved(treeNode, childIndices, removedChildren);
            */
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

  	public Node getSelectedNode() {
		ExplorerNode treeNode = getSelectedTreeNode();
		if (treeNode == null)
			return null;
		return treeNode.getXmlNode();
	}

	public ExplorerNode getSelectedTreeNode() {

		TreePath path = jTree1.getSelectionPath();

		/* System.out.println(" in getSelectedTreeNode path = " + path); */

		if (path == null)
			return null;

		Object obj = path.getLastPathComponent();
		if (!(obj instanceof ExplorerNode))
			return null;

		return (ExplorerNode) obj;
	}


/*
    public void propertyChange(PropertyChangeEvent evt) {
        
        Object newVal = evt.getNewValue();
        if(newVal instanceof ExplorerNode)
            selectedProject = (ExplorerNode) evt.getNewValue();
    }
*/
    ExplorerNode selectedNode;

	// PREREL  MOVED TO HERE
	private Document entries;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("root");
    public javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return jTree1.getPreferredScrollableViewportSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		// TODO Auto-generated method stub
		return false;
		
		
	}

}
