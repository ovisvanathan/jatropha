 package com.exalto;
/*--

$Id: TableAutoFormatter.java,v 1.3gm 2004/02/06 09:39:10 jhunter Exp $

Copyright (C) 2000-2004 Jason Hunter & Brett McLaughlin.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
    notice, this list of conditions, and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions, and the disclaimer that follows
    these conditions in the documentation and/or other materials
    provided with the distribution.

3. The name "JDOM" must not be used to endorse or promote products
    derived from this software without prior written permission.  For
    written permission, please contact <request_AT_jdom_DOT_org>.

4. Products derived from this software may not be called "JDOM", nor
    may "JDOM" appear in their name, without prior written permission
    from the JDOM Project Management <request_AT_jdom_DOT_org>.

In addition, we request (but do not require) that you include in the
end-user documentation provided with the redistribution and/or in the
software itself an acknowledgement equivalent to the following:
     "This product includes software developed by the
      JDOM Project (http://www.jdom.org/)."
Alternatively, the acknowledgment may be graphical using the logos
available at http://www.jdom.org/images/logos.

THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED.  IN NO EVENT SHALL THE JDOM AUTHORS OR THE PROJECT
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.

This software consists of voluntary contributions made by many
individuals on behalf of the JDOM Project and was originally
created by Jason Hunter <jhunter_AT_jdom_DOT_org> and
Brett McLaughlin <brett_AT_jdom_DOT_org>.  For more information
on the JDOM Project, please see <http://www.jdom.org/>.

*/

import java.io.FileOutputStream; 
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Properties;
import org.apache.fop.fo.properties.WhiteSpaceCollapse;
import org.apache.fop.layout.FontInfo;
import org.apache.fop.layout.FontState;
import org.apache.fop.layout.HyphenationProps;
import org.apache.fop.layout.inline.InlineSpace;
import org.apache.fop.render.pdf.FontSetup;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.apache.log4j.Logger;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.*;
import org.xml.sax.helpers.*;


/**
* Demonstrates the use of {@link Parent#getDescendants}.
*/
public class TableAutoFormatter {

	 double availWidth = 0.0;
	 ArrayList tabList = null;
	 String unit = null;
	 boolean fullAlloc = false;
	 boolean tableIsAutoWidth = false;
	 Hashtable colWidths = new Hashtable();
	 ArrayList cellMap = new ArrayList();
	 int tableid = 0;
	 Hashtable tableWidths = new Hashtable();
	 Hashtable columnWidths = null;
	 Hashtable cellWidths = null;
	 ArrayList tableWidthInfo = null;
	 ArrayList colList = null;
	 ColWidthInfo cwidInfo = null;
	 int index = 0;
	 String inputFile = null;	
	 List propList = new LinkedList();
 	 FontInfo fontInfo = new FontInfo();			
	 Hashtable defaultProperties = new Hashtable();
	 Properties prop;
	 StringBuffer lockBuf = new StringBuffer();

	
	 Stack nodeStack = new Stack();
	 int whiteSpaceCollapse = 0;
	 FontState currentFontState = null;
      HyphenationProps hyphProps = null;
	String outputFile;
	private Logger logger;
	private int noOfIterations;
	Iterator itr;
	DocHandler handler;
	
  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */		
//	public  void main(String[] args) throws Exception {

//	public TableAutoFormatter(String fileName, String outFile, Properties p) {
	public TableAutoFormatter(String fileName, Properties p) {
	
		logger = Logger.getLogger(TableAutoFormatter.class.getName());	
		inputFile = fileName;
//		outputFile = outFile;
		outputFile = "Layout result";
		prop = p;
		try {
			noOfIterations = Integer.parseInt(p.getProperty("noOfIterations"));
			System.out.println(" no iters = " + noOfIterations);
		} catch(NumberFormatException ne) {
			ne.printStackTrace();
			logger.debug("property no of iterations not numeric" + ne.getMessage());
		} catch(Exception e) {
			e.printStackTrace();
			logger.debug("property no of iterations not numeric" + e.getMessage());
		}
	}
	
	public void run() throws Exception {
			
		try {
			
		SAXBuilder builder = new SAXBuilder();
			logger.debug("DDEmo in file" + inputFile);
		
		Document doc = builder.build(inputFile);
				
		Attribute pha = null;
		Attribute pwa = null;
		Attribute lma = null;
		Attribute rma = null;
		boolean awset = false;
		String ph = null;
		String pw = null;
		String lm = null;
		String rm = null;
	 	int iterCount = 0;
	 
		setDefaultPropertyValues();

		try {
			
 			FontSetup.setup(fontInfo);

			logger.debug("All content:");
			for (int passnum = 0;
				passnum < ColWidthTypes.NUMPASSES;
				passnum++) {
				itr = doc.getDescendants();

				tableid = 0;

				//		if(passnum == 1) {
				//		Enumeration em = tableWidths.keys();
				//		System.out.println("MAIN Begin debug");
				//
				//		while(em.hasMoreElements()) {
				//
				//		String tkey = (String) em.nextElement();
				//		System.out.println("Table Name = " + tkey);
				//		
				//		ArrayList allList = (ArrayList) tableWidths.get(tkey);
				//				
				//		for(int i=0;i<allList.size();i++) {
				//			// table min and max width
				//			if(i == 0) {
				//		System.out.println("Table width");
				//				
				//				System.out.println(((ColWidthInfo)allList.get(i)).GetMinWidth());
				//				System.out.println(((ColWidthInfo)allList.get(i)).GetMaxWidth());
				//			} else if(i == 1 || i == 2) {
				//
				//				Hashtable widths = (Hashtable) allList.get(i);
				//				Enumeration enm = widths.keys();
				//				while(enm.hasMoreElements()) {
				//					String key = (String) enm.nextElement();
				//					if( i == 1) 
				//						System.out.println("COL name = " + key);
				//					else
				//						System.out.println("CELL name = " + key);
				//
				//					ColWidthInfo info = (ColWidthInfo) widths.get(key);
				//
				//					System.out.println(info.GetMinWidth());
				//					System.out.println(info.GetMaxWidth());
				//
				//				}
				//			}
				//		}
				//
				//		}		
				//		
				//		}
				//		System.out.println("MAIN END debug");
				
				 
				
				while (itr.hasNext()) {
					Object tags = (Content) itr.next();

					if (tags instanceof Element) {

						Element child = (Element) tags;					
				
						if (child.getName().equals("simple-page-master")) {
							pha = child.getAttribute("page-height");
							ph = pha.getValue();
							pwa = child.getAttribute("page-width");
							pw = pwa.getValue();
							if (pw.indexOf("in") != -1) {
								int pwin = pw.indexOf("in");
								pw = pw.substring(0, pwin);
								unit = "in";
							} else if (pw.indexOf("cm") != -1) {
								int pwcm = pw.indexOf("in");
								pw = pw.substring(0, pwcm);
								unit = "cm";
							}

						}

						if (child.getName().equals("region-body")) {

							lma = child.getAttribute("margin-left");
							lm = lma.getValue();
							rma = child.getAttribute("margin-right");
							rm = rma.getValue();
						}

						if (pw != null && lm != null && rm != null) {
							try {
							availWidth =
								Double.parseDouble(pw)
									- (Double.parseDouble(lm)
										+ Double.parseDouble(rm));
							//						System.out.println("aw " + availWidth);
							}
							catch(NumberFormatException ne) {
								ph = ColWidthTypes.DEFAULT_HEIGHT;
								pw = ColWidthTypes.DEFAULT_WIDTH;
								rm = ColWidthTypes.DEFAULT_RTMARGIN;
								lm = ColWidthTypes.DEFAULT_LTMARGIN;
								
								
								availWidth =
									Double.parseDouble(pw)
									- (Double.parseDouble(lm)
										+ Double.parseDouble(rm));
							}
						
						awset = true;
						}

						if (child.getName().equals("table")) {
							iterCount++;
							boolean processTable = true;
							Element cpar = (Element) child.getParent();

							tabList = new ArrayList();
							tabList.add(child);

							while (cpar instanceof Element) {
								if (cpar.getName().equals("table")) {
									processTable = false;
									break;
								} else {
									if (!(cpar.getParent() instanceof Element))
										break;
									cpar = (Element) cpar.getParent();
								}
							}

							String layout = null;
							Attribute lout = child.getAttribute("table-layout");
							if (lout != null)
								layout = lout.getValue();

							if ("Fixed".equalsIgnoreCase(layout)) {
								tableIsAutoWidth = false;
								logger.info("table-layout: found fixed");
							} else if ("Auto".equalsIgnoreCase(layout)) {
								logger.info("table-layout: found Auto");
								tableIsAutoWidth = true;
							}

							if (processTable) {
								System.out.println(" iterct = " + iterCount);
								if (passnum == 0 && iterCount > 1)
									tableid++;
								processTableElem(
									child,
									false,
									colList,
									null,
									null,
									passnum,
									null,
									tableWidthInfo,
									columnWidths,
									cellWidths, null);
							}
						}
					}
				}

			} // for numpasses
			
			
			// 2nd pass using sax to build DOM tree and assign widths 
			// at the same time
			String apacheXercesPropertyValue;
			String jaxpPropertyName =
			    "javax.xml.parsers.SAXParserFactory";
			jaxpPropertyName ="org.xml.sax.driver";
			
			if (System.getProperty(jaxpPropertyName) == null) {
				apacheXercesPropertyValue =
			      "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
				System.setProperty(jaxpPropertyName,
			                       apacheXercesPropertyValue);
			}					   
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser = factory.newSAXParser();
				
		//	 XMLReader parser;
		//	 parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");

				handler = new DocHandler(tableWidths, this);
		//		parser.setContentHandler(handler);
		//		CustomResolver eresolver = new CustomResolver();
		//		parser.setEntityResolver(eresolver);
				parser.parse(inputFile, handler);

		

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

	if(outputFile == null) {
		int dotpos = inputFile.indexOf(".");
		if(dotpos > -1) {
			outputFile = inputFile.substring(0, dotpos);
			outputFile = outputFile + ".out.";
			outputFile = outputFile + inputFile.substring(dotpos+1, inputFile.length());
		}
	} 
		logger.debug("output file name is "+ outputFile.toString());
		
		
		FileOutputStream pstr = new FileOutputStream(outputFile.toString());					
			XMLOutputter outp = new XMLOutputter();
			outp.output(handler.getDocument(), pstr);
		pstr.close();
	
		} 
		
		catch(org.jdom.JDOMException ex) {
	//		throw ex;
			ex.printStackTrace();
		}
		catch(java.io.IOException ex) {
	//		throw ex;
			ex.printStackTrace();
	//		logger.error("Error balancing column widths");
		}
	
		return;
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  int processTableElem(
		Element child,
		boolean countTags,
		ArrayList colList,
		String parentTableName,
		String parentCellName,
		int passnum,
		String colCellName,
		ArrayList tableWidthInfo,
		Hashtable colWidths,
		Hashtable cellWidths, ColWidthInfo childTableWidth)
		throws Exception {
			

		int contSize = 0;
		int contSum = 0;
		int contSumr = 0;
		int tagCount = 0;
		int tc = 0; 
		int numCols = 0;
		int currCol = 1;
		boolean gotCol = false;
		String colno = null;
		int cellnum = 0;
		int cellCount = 0;
		String cellName = null;
		int cellid = 0;
		String colName = null;
		int colid = 0;
		int rowCount = 0;
		String tableName = null;
		boolean columnTagPresent = false;
		boolean colWidthsSet = false;
		boolean columnsProcessed = false;
		boolean isMultiple = false;		
				

		System.out.println("IN PTE passnum = " + passnum);
		
		

			tableName = "t" + prefixZeros(tableid);
		

			if(passnum == 1) {		

//					Attribute tableWidth = child.getAttribute("inline-progression-dimension");
//					if(tableWidth == null) {
//						double colsumw = 0.0;
//						for(int i=0;i<colNameWidths.length;i++) 
//							colsumw += colNameWidths[i];					
//						child.setAttribute("inline-progression-dimension", new Double(colsumw).toString());	
//					}					
//				
				assignPctColWidths(tableName, parentTableName, colCellName);
				
				System.out.println("returned from APCW ");

					ArrayList tableNameList =
						(ArrayList) tableWidths.get(tableName);
					ColWidthInfo tabWidInfo =
						(ColWidthInfo) tableNameList.get(0);

					int noOfCols = tabWidInfo.getNumberOfCols();

					System.out.println("no of cols " + noOfCols);
					
					Hashtable columnWidths = (Hashtable) tableNameList.get(1);
					System.out.println("columnWidths size =" + columnWidths.size());
					
					double[] colNameWidths = new double[noOfCols]; 

					Enumeration enumerate = columnWidths.keys();
					while(enumerate.hasMoreElements()) {
						String key = (String) enumerate.nextElement();
						System.out.println("currColName  =" + key);

						String colnum = key.substring(3, key.length());
						int currentCol = Integer.parseInt(colnum);
						
						ColWidthInfo tcInfo = (ColWidthInfo) columnWidths.get(key);
						double colWidth =0.0;
						colWidth = tcInfo.getWidth();
						colNameWidths[currentCol] = colWidth;
					}
					
					List l2 = child.getChildren();
					
					int colSz = 0;	
					for(int k=0;k<l2.size();k++) {
						Object obj =  l2.get(k);
						if(obj instanceof Element) {
							Element olem = (Element) obj;
				
							if(olem.getName().equals("table-column")) {
								colSz++;
								olem.setAttribute("column-width", new Double(colNameWidths[colSz-1]).toString());							
							}
						}
					}			
							
					System.out.println("no of col tags" + colSz);
								
				
					if(colSz == 0) {
						System.out.println("Adding col tags " + noOfCols);
						synchronized(lockBuf) {
							for(int i=0;i<noOfCols;i++) {
								Element tableCol = new Element("table-column", "fo", "http://www.w3.org/1999/XSL/Format");
								tableCol.setAttribute("column-width", new Double(colNameWidths[i]).toString());	
							//	itr.add((Object)tableCol);			
								child.addContent(tableCol);																	
							} 			
						}
					}
			}

			
		Iterator rowItr = child.getDescendants();
		
				try {

			logger.debug("inside processTableElem");
			logger.debug("passnum =" + passnum);
						
			colList = new ArrayList();

			if (countTags) {
				tabList = new ArrayList();
				tabList.add(child);
			}

			logger.info("tableName " + tableName);
			System.out.println("tableName " + tableName);

			ColWidthInfo tableMiscInfo = null;
			if (passnum == 0) {
				tableWidthInfo = new ArrayList();
				tableMiscInfo = new ColWidthInfo("table");
				tableWidthInfo.add(tableMiscInfo);
				tableWidths.put(tableName, tableWidthInfo);

				colWidths = new Hashtable();
				cellWidths = new Hashtable();

			}

			if (passnum == 1) {

				double availableWidth = 0.0;
				if (parentTableName != null) {

					logger.debug(" parentTableName = " + parentTableName);
		//			System.out.println(" parentTableName = " + parentTableName);

					tableWidthInfo =
						(ArrayList) tableWidths.get(parentTableName);

					ArrayList tableNameList =
						(ArrayList) tableWidths.get(tableName);
					ColWidthInfo tabWidInfo =
						(ColWidthInfo) tableNameList.get(0);

					// for border-spacing and border width
					Attribute spacing = child.getAttribute("border-spacing");
					// CELLSPACING
					String cellSpacing = null;
					String border = null;
					ColumnInfo [] spaceInfo = new ColumnInfo[2];

					if (spacing != null) {
						cellSpacing = spacing.getValue();
						spaceInfo[0] = new ColumnInfo();
						setWidthAndType(cellSpacing, spaceInfo[0]);
					}

					Attribute bord = child.getAttribute("border");
					// CELLSPACING
					if (bord != null) {
						border = bord.getValue();
						spaceInfo[1] = new ColumnInfo();
						setWidthAndType(cellSpacing, spaceInfo[1]);
					}

					ArrayList columnNameList =
						(ArrayList) tableWidthInfo.get(3);
					Hashtable colNameWidths = (Hashtable) tableWidthInfo.get(1);

					ColWidthInfo tableCellWidth =
						(ColWidthInfo) colNameWidths.get(colCellName);

					availableWidth = tableCellWidth.getWidth();

					//							System.out.println(" in pte avail width before = " + availableWidth);
					//							System.out.println(" in pte border spacing = " + cellSpacing);
					//							System.out.println(" in pte border = " + border);

					availableWidth =
						calcAvailWidth(spaceInfo, availableWidth);

					tabWidInfo.setAvailableWidth(availableWidth);

					//							System.out.println(" in PTE avail width after = " + availableWidth);
					
					// table-column handling
					
					

				} else {

					// for border-spacing and border width
					ArrayList tableNameList =
						(ArrayList) tableWidths.get(tableName);
					ColWidthInfo tabWidInfo =
						(ColWidthInfo) tableNameList.get(0);

					double currTblAvailWidth = tabWidInfo.getAvailableWidth();
					//							System.out.println(" curr table got avail width = " + currTblAvailWidth);
					Attribute spacing = child.getAttribute("border-spacing");
					// CELLSPACING
					String cellSpacing = null;
					String border = null;
					ColumnInfo [] spaceInfo = new ColumnInfo[2];

					if (spacing != null) {
						cellSpacing = spacing.getValue();
						spaceInfo[0] = new ColumnInfo();
						setWidthAndType(cellSpacing, spaceInfo[0]);

					}

					Attribute bord = child.getAttribute("border");
					// CELLSPACING
					if (bord != null) {
						border = bord.getValue();
						spaceInfo[1] = new ColumnInfo();
						setWidthAndType(cellSpacing, spaceInfo[1]);
					}

					currTblAvailWidth =
						calcAvailWidth(spaceInfo, currTblAvailWidth);

					tabWidInfo.setAvailableWidth(currTblAvailWidth);

					System.out.println(
						" in PTE avail width t00 t02 = " + currTblAvailWidth);
				}


			}
			int numOrigCols = 0;
			
			
			
			while (rowItr.hasNext()) {

//							System.out.println(" inside while rowitr ");
				Object tchild = (Content) rowItr.next();

//							System.out.println(" contsize 222 "+ contSumr);

				if (contSumr != 0 && contSumr > 0) {
					contSumr--;
					continue;
				}
				
//				System.out.println("curr tag " + tchild);


				ColumnInfo cinfo = null;
				if (tchild instanceof Element) {
					Element trowe = (Element) tchild;

					if (trowe.getName().equals("table-column")) {
						numCols++;
						cinfo = new ColumnInfo();
//					System.out.println(" col id before asgn = " + colid);

						colName = "col" + prefixZeros(colid);



//					System.out.println(" col name before id incr = " + colName);

						colid++;
						columnTagPresent = true;
						
						if (countTags)
							tagCount++;


						cinfo.setColName(colName);
						cwidInfo = new ColWidthInfo("column");
						if(passnum == 0)
							colWidths.put(colName, cwidInfo);
					}
					

					if (passnum == 0) {
						if (trowe.getAttribute("column-number") != null) {
							Attribute colnum =
								trowe.getAttribute("column-number");
							colno = colnum.getValue();

							//						System.out.println(" This col number is = " + colno );
							cinfo.setColNo(Integer.parseInt(colno));
						}

						if (trowe.getAttribute("column-width") != null) {
							Attribute cwa = trowe.getAttribute("column-width");
							String cw = cwa.getValue();

							setWidthAndType(cw, cinfo);

							//						System.out.println(" This col width is = " + cw);
						}

						if (cinfo != null)
							colList.add(cinfo);

					} // end if passnum ==0

					int currColNo = 0;
					if (trowe.getName().equals("table-row")) {

						if (countTags)
							tagCount++;

						Iterator colItr = trowe.getDescendants();
//						boolean isMultiple = false;
						boolean initialized = false;

						String ctens = Integer.toString(rowCount);
						//					System.out.println("table row cllid = " + cellid);

						String cunits = null;

//											System.out.println("cell count = " + cellCount);
//											System.out.println("currcolno  = " + currColNo);

//						if (cellCount != 0) {
//							cunits = Integer.toString(currColNo % cellCount);
//						} else {
							cunits = Integer.toString(currColNo);
//						}

						rowCount++;				
						
						cellCount = 0;

						cellid = Integer.parseInt(ctens + cunits);

						int numEffCols = 0;
						while (colItr.hasNext()) {

//													System.out.println(" inside while colitr =");
							Object rchild = (Content) colItr.next();
//								System.out.println("current tag " + rchild);

//						System.out.println(" contSum =" + contSum);

							if (isMultiple) {
								if (!initialized) {
									contSumr = contSum;
									initialized = true;
								}
								if (contSum != 0 && contSum > 0) {
									contSum--;
									continue;
								}
							}

							if (countTags)
								tagCount++;


							if (rchild instanceof Element) {
								Element tcol = (Element) rchild;
								List cellContent = null;
								
								if (tcol.getName().equals("table-cell")) {

										Attribute spanAttr =
											tcol.getAttribute(
												"number-columns-spanned");
										String colspan = null;
										int csp = 0;
										if (spanAttr != null) {
											colspan = spanAttr.getValue();
											csp = Integer.parseInt(colspan);
											numEffCols += (csp - 1);
										}
									
									Namespace ns = tcol.getNamespace();
									Element block = tcol.getChild("block", ns);

									colCellName =
										"col" + prefixZeros(currColNo);

										System.out.println(" table col cell name = " + colCellName);

									cellName = "cell" + prefixZeros(cellid);

									currColNo++;
									// for getting no of cols 
									cellCount++;
									
									
									cwidInfo = new ColWidthInfo("cell");
									// ov add 21/07/04 begin
									Attribute cellAttr =
										tcol.getAttribute("padding");
									String cellPad = cellAttr.getValue();

									ColumnInfo cellWidInfo = new ColumnInfo();
									setWidthAndType(cellPad, cellWidInfo);

									cwidInfo.setPadding(
										cellWidInfo.getColWidth());
									// ov add 21/07/04 end

									//	System.out.println(" table cell name ****** = " + cellName);

									if (passnum == 0) {

										cellWidths.put(cellName, cwidInfo);

									if(!colWidthsSet) {
										tableWidthInfo.add(colWidths); //1
										tableWidthInfo.add(cellWidths); //2
									
										if(columnTagPresent) 
											tableWidthInfo.add(colList); //3
										colWidthsSet = true;		
									}

									} // end if passnum == 0
									cellContent = block.getContent();
									contSize = cellContent.size();


								//	tagCount += contSize;
									contSum += contSize;
									//									System.out.println(" size " + contSize);
									if (contSize > 1) {
										double maxwidall = 0.0;
										double minwidall = 99999.0;
										for (int i = 0; i < contSize; i++) {
											Object tab =
												(Object) cellContent.get(i);

											if (tab instanceof Element) {
												Element tabElem = (Element) tab;											

												if (tabElem
													.getName()
													.equals("table-and-caption")) {
													isMultiple = true;
													Element tableElem =
														tabElem.getChild(
															"table",
															ns);
													tableid++;

													if(childTableWidth == null) 
														childTableWidth = new ColWidthInfo();
													

													contSum
														+= processTableElem(
															tableElem,
															true,
															colList,
															tableName,
															cellName,
															passnum,
															colCellName,
															tableWidthInfo,
															colWidths,
															cellWidths,
															childTableWidth);
															
														if(childTableWidth != null) {
														
															double tableMinWidth = childTableWidth.getMinWidth();
															double tableMaxWidth = childTableWidth.getMaxWidth();
														
													//		if(tableMinWidth < minwidall) 
																minwidall = KeepMax(minwidall, tableMinWidth);
																
													//		if(tableMaxWidth > maxwidall) 
																maxwidall = KeepMax(maxwidall, tableMaxWidth);
														}															
														
													tagCount += contSum;
															
													cellid++;
																									
											//		System.out.println(" table tag count = " + contSum);
												} else if(tabElem.getName().equals("table")) {
													isMultiple = true;
													tableid++;

													if(childTableWidth == null) 
														childTableWidth = new ColWidthInfo();

													contSum += processTableElem(
															tabElem,
															true,
															colList,
															tableName,
															cellName,
															passnum,
															colCellName,
															tableWidthInfo,
															colWidths,
															cellWidths,
															childTableWidth);


													tagCount += contSum;


													
													if(childTableWidth != null)  {

															double tableMinWidth = childTableWidth.getMinWidth();
															double tableMaxWidth = childTableWidth.getMaxWidth();
															
//													System.out.println("##########child table minwid " + tableMinWidth);
//											System.out.println("##############child  table maxwid " + tableMaxWidth);
//													System.out.println("##########minwidall " + minwidall);
//											System.out.println("##############maxwidall " + maxwidall);

													//		if(tableMinWidth < minwidall) 
																minwidall = KeepMax(minwidall, tableMinWidth);
																
													//		if(tableMaxWidth > maxwidall) 
																maxwidall = KeepMax(maxwidall, tableMaxWidth);

//											System.out.println("##############minwidall " + minwidall);
//											System.out.println("##############maxwidall " + maxwidall);
																									}
															
//													cellid++;
												} else {
//													System.out.println(" unknown  elem type : " + tab);
													String imgwid = null;
													double imwidth = 0.0;
													if(tabElem.getName().equals("external-graphic")) {
														Attribute imgWidth = tabElem.getAttribute("width");
														if(imgWidth != null) {
															imgwid = imgWidth.getValue();
														}
														
														int inpos = imgwid.indexOf("in");
														if(inpos > -1)  {
															imgwid = imgwid.substring(0, inpos);
															imwidth = Double.parseDouble(imgwid);		

//														System.out.println("external graphic width " +  imwidth);
										logger.info("external graphic width " +  imwidth);
													
//															if(imwidth < minwidall) 
//																minwidall = imwidth;
														
															if(imwidth > maxwidall) 
																maxwidall = imwidth;

//											System.out.println("##############minwidall " + minwidall);
//											System.out.println("##############maxwidall " + maxwidall);


														} // end inpos														
													} // end if ext-graphic
													
													
													
												} // end else !table

											} else { // end tab instanceof element
//											System.out.println(" Tab text len  = " + ((Text)tab).getTextTrim().length());
									
												if (tab instanceof Text) {
												Text textElem = (Text) tab;
									logger.info("The inner text is " + textElem.getText());

												System.out.println(
													" the inner text is "
														+ textElem.getText());
												String text =
													textElem.getTextTrim();

												if(text.length() > 0) {

													setFontState(fontInfo, textElem);									

													double maxwid =
														GetMaximumWidthOfText(text);
													double minwid =
														GetMinimumWidthOfText(text);
														
//										System.out.println("$$$$$$$$$ inner minwid&&&&&&&&& " + minwid);
//										System.out.println("$$$$$$$$$ inner maxwid&&&&&&&&&& " + maxwid);
	

													minwidall = KeepMax(minwidall, minwid);
													maxwidall = KeepMax(maxwidall, maxwid);

//										System.out.println("========== inner minwidall ========= " + minwidall);
//										System.out.println("========== inner maxwidall ========= " + maxwidall);

																											
//													if(maxwid > maxwidall) 
//														maxwidall = maxwid;														
//														
//													if(minwid < minwidall)
//														minwidall = minwid;	
												} // end text len > 0


												} 	// end if text
											}		//	end else 


										}		// for
										
										// cellid incremented once for all items in cellContent
										cellid++;


//										System.out.println("&&&&&&minwidall final inner table" + minwidall);
//										System.out.println("&&&&&&&&&maxwidALL final inner table " + maxwidall);
	
												if (passnum == 0) 
													cellWidths.put(
														cellName,
														new ColWidthInfo(
															minwidall,
															maxwidall));


									} else {	// end else contSize > 1
										if (cellContent.get(0)
											instanceof Text) {

											Text textElem =
												(Text) cellContent.get(0);

									logger.info("The text is " + textElem.getText());

											System.out.println(
												" the text is "
													+ textElem.getText());
											String text = textElem.getText();
											int n = text.length();
											
									//		FontState fs = GetFontState(fontInfo, textElem);									
											setFontState(fontInfo, textElem);									
											
											double maxwid =
												GetMaximumWidthOfText(text);
											double minwid =
												GetMinimumWidthOfText(text);

											// to keep min and max width within a cell

//											System.out.println(
//												"&&&&&&minwid " + minwid);
//											System.out.println(
//												"&&&&&&&&&maxwid " + maxwid);

											if (passnum == 0) 
												cellWidths.put(
													cellName,
													new ColWidthInfo(
														minwid,
														maxwid));

											cellid++;

										} // end if content is text

									} // end else

								} // end if table-cell

							} // end if rchild is element

						} // end while colitr
						
//						System.out.println("After colitr cellCount = " + cellCount);
						
						numOrigCols = KeepMax(cellCount, numOrigCols);
						
//						System.out.println(" numorigcols =" + numOrigCols);


						

					} // end if table-row
					
					if(trowe.getName().equals("table-header")) {
						if(countTags) 
							tagCount++;
					}
					
					if(trowe.getName().equals("table-body")) {
						if(countTags) 
							tagCount++;
					}

				} // end if tchild element 

			} // end while rowitr
			
			
						if(passnum == 0 && !columnTagPresent) {
						ColumnInfo colCustomInfo = null;
						
						for(int colCtr=0;colCtr<numOrigCols;colCtr++) {
							colCustomInfo = new ColumnInfo();
							colName = "col" + prefixZeros(colCtr);
							colCustomInfo.setColName(colName);
							// create empty colwidthinfo and add to colWidths
							cwidInfo = new ColWidthInfo("column");
							colWidths.put(colName, cwidInfo);
							
							// set colNo and colwidth to columninfo and add to list
							colCustomInfo.setColNo(colCtr+1);
							
							colList.add(colCustomInfo);
							
						} // end for
						
						tableWidthInfo.add(colList);
						
						}
					
					if(passnum==0) {	
						aggregateWidths(
							tableName,
							countTags,
							parentTableName,
							parentCellName, childTableWidth, numOrigCols, isMultiple);
					}
						

		} catch (Exception pe) {
			pe.printStackTrace();
			throw pe;
		}

		return tagCount;
	}
	
  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */	
	public  void setFontState(FontInfo fontInfo, Text text) throws Exception  {
	  FontState fontState = null;
	  
	  Element parent = (Element) text.getParent();
	  HashMap properties = new HashMap();
	  
	  List props = parent.getAttributes();
	  ListIterator iter = props.listIterator();
	  while(iter.hasNext()) {
	  	Attribute attr = (Attribute) iter.next();
	  	String key = attr.getQualifiedName();
	  	String val = attr.getValue();
	  	properties.put(key,val);
	  }
	  String fontFamily = null;
	  String fontStyle = null;
	  String fontWeight = null;
	  int fontSize = 0;
	  int fontVariant = 0;
	  int letterSpacing = 0;

	  if (fontState == null) {
			if(properties.get("font-family") != null) {	      		
            	fontFamily = (String) properties.get("font-family");
			} else {
				fontFamily = getPropertyFromParent(parent, "font-family", true, true);
			}
			
			if(properties.get("font-style") != null) {	      		
 	            fontStyle = (String) properties.get("font-style");
			} else {
				fontStyle = getPropertyFromParent(parent, "font-style", true, true);
			}
    	 
    		if(properties.get("font-weight") != null) {	      		
 	            fontWeight = (String) properties.get("font-weight");
    		} else {
				fontWeight = getPropertyFromParent(parent, "font-weight", true, true);
			}

            // NOTE: this is incomplete. font-size may be specified with
            // various kinds of keywords too
    		if(properties.get("font-size") != null) {	      		
            	fontSize = Integer.parseInt((String) properties.get("font-size"));
    		} else {
				fontSize = Integer.parseInt(getPropertyFromParent(parent, "font-size", true, true));
			}
    		
    		if(properties.get("font-variant") != null) {	      		
            	fontVariant = Integer.parseInt((String)properties.get("font-variant"));
    		} else {
    			fontVariant = Integer.parseInt(getPropertyFromParent(parent, "font-variant", true, true));
			}
			
			if(properties.get("letter-spacing") != null) {	      		
            	fontVariant = Integer.parseInt((String)properties.get("letter-spacing"));
    		} else {
    			fontVariant = Integer.parseInt(getPropertyFromParent(parent, "letter-spacing", true, true));
			}

			if(properties.get("white-space-collapse") != null) {	      		
            	whiteSpaceCollapse = Integer.parseInt((String)properties.get("white-space-collapse"));
    		} else {
    			whiteSpaceCollapse = Integer.parseInt(getPropertyFromParent(parent, "white-space-collapse", true, true));
			}
			
//			textState = getTextDecoration();
			
            // fontInfo is same for the whole FOP run but set in all FontState
            currentFontState = new FontState(fontInfo, fontFamily, fontStyle,
                                      fontWeight, fontSize, fontVariant, letterSpacing);
        }
	}


  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  String getPropertyFromParent(Element parent, String propertyName, 
		boolean inherit, boolean tryDefault) throws Exception {
	
        /* Handle request for one part of a compound property */
        int sepchar = propertyName.indexOf('.');
        String subpropName = null;
		String prop = null;

        if (sepchar > -1) {
            subpropName = propertyName.substring(sepchar + 1);
            propertyName = propertyName.substring(0, sepchar);
        }

		try {
        
        Parent gpar = parent.getParent();

        if(gpar instanceof Element) {
        	Element pelem = (Element) gpar;

        prop = findProperty(pelem, propertyName, inherit);
       	if (prop == null && tryDefault) {    // default value for this FO!
            try {
           	//		create property with default values here
           //     p = this.builder.makeProperty(this, namespace, element,
            //			                               propertyName);
            	return null;   
            } catch (Exception e) {
                // don't know what to do here
            }
        	
        } else {
       		prop = getDefaultPropertyValue(propertyName);
       	}   
        
        } // end gpar element
        
        
		} catch(Exception ex) {
			ex.printStackTrace();
		} 
        return prop; 
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */	
	public  String findProperty(Element parent, 
	String propertyName, boolean inherit) throws Exception {
	  
	  	String prop = null;
	  	Namespace namespace = parent.getNamespace();

		try {	  	
        if (isCorrespondingForced(namespace.getPrefix(), parent,propertyName)) {
      //      prop = computeProperty(namespace, parent,
      //                                  propertyName);
        		prop = null;
        } else {
        	// get the value explicitly specified on this fo
            Attribute propAttr = parent.getAttribute(propertyName);
           	if(propAttr != null) 
	            prop = propAttr.getValue();
			
            if (prop == null) {
       //         prop = computeProperty(namespace, parent,
       //                                          propertyName);
            	prop = null;
            }
            if (prop == null) {    // check for shorthand specification
       //         prop = builder.getShorthand(this, namespace, parent,
       //                                propertyName);
       			  prop = null;
            }

			// getParent may not always return element	            
            Parent grandParent =  parent.getParent();                           	
            if(grandParent instanceof Element) {
	           Element gelem = (Element) grandParent;
	            if (prop == null
	                    && inherit) {    // else inherit (if has parent and is inheritable)
	                if (grandParent != null
	                        && isInherited(namespace.getPrefix(), parent,
	                                               propertyName)) {
	                    prop = findProperty(gelem, propertyName, true);
	                }
	            }
            } else {
            		prop = getDefaultPropertyValue(propertyName);
            }
        }
        
		} catch(Exception e) {
			throw e;
		}
        return prop;
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  boolean isInherited(String ns, Element parent, String pname) {
		return true;	
	}


  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  String getDefaultPropertyValue(String propName) throws Exception {
		
//		System.out.println(" propName =" + propName);
		if(!(propName.equals("font-family") || propName.equals("font-style")
		|| propName.equals("font-weight") || propName.equals("font-size") 
		|| propName.equals("font-variant") || propName.equals("letter-spacing") 
		|| propName.equals("white-space-collapse")) ) {

			logger.error("property undefined" + propName);
			throw new Exception("property uindefined" + propName);
		}
		
		return (String) defaultProperties.get(propName);		
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  void setDefaultPropertyValues() {
		
		defaultProperties.put("font-family", "serif");
		defaultProperties.put("font-style", "normal");
		defaultProperties.put("font-weight", "normal");
		defaultProperties.put("font-size", "18");
		defaultProperties.put("font-variant", "11");
		defaultProperties.put("letter-spacing", "2");
		defaultProperties.put("white-space-collapse", "1");
		
	}


  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  HashMap convertListToHashMap(List lst) {
		HashMap hmap = new HashMap();
		ListIterator iter = lst.listIterator();
		while(iter.hasNext()) {
			Attribute attr = (Attribute) iter.next();
			String key = attr.getQualifiedName();
			String val = attr.getValue();
			hmap.put(key,val);	
		}
		
		return hmap;
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	 public  boolean isCorrespondingForced(String namespace, Element parent, String propName) {
            return false;
     }

	
	
  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  void setWidthAndType(String cw, ColumnInfo cinfo) {

		int pct = cw.indexOf("%");
		if (pct != -1) {

			String pctw = cw.substring(0, pct);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_PERCENT);

			cinfo.setColWidth(Double.parseDouble(pctw));

		} else if (cw.indexOf("proportional-column-width") != -1) {

			int openBr = cw.indexOf("(");
			int closeBr = cw.indexOf(")");
			String prowid = cw.substring(openBr + 1, closeBr);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_PROPORTIONAL);
			cinfo.setColWidth(Double.parseDouble(prowid));

		} else if (cw.indexOf("in") != -1) {

			int unit_in = cw.indexOf("in");
			String prowid = cw.substring(0, unit_in);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_INCHES);
			cinfo.setColWidth(Double.parseDouble(prowid));

		} else if (cw.indexOf("cm") != -1) {

			int unit_cm = cw.indexOf("cm");
			String prowid = cw.substring(0, unit_cm);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_CMS);
			cinfo.setColWidth(Double.parseDouble(prowid));

		} else if (cw.indexOf("pc") != -1) {

			int unit_pc = cw.indexOf("pc");
			String prowid = cw.substring(0, unit_pc);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_PICA);
			cinfo.setColWidth(Double.parseDouble(prowid));

		} else if (cw.indexOf("px") != -1) {

			int unit_px = cw.indexOf("px");
			String prowid = cw.substring(0, unit_px);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_PIXELS);
			double pxwid = Double.parseDouble(prowid) / 72.0;
			cinfo.setColWidth((pxwid == 0.0) ? 0.01 : pxwid);

		} else if (cw.indexOf("em") != -1) {
			int unit_em = cw.indexOf("em");
			String prowid = cw.substring(0, unit_em);
			cinfo.setColWidthType(ColWidthTypes.COLUMN_WIDTH_EMPHASIS);
			cinfo.setColWidth(Double.parseDouble(prowid));
		}

	}


  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  void assignPctColWidths(String tableName, 
						String parentTableName, String parentColName) throws Exception {
						
		try {

		ArrayList allList = (ArrayList) tableWidths.get(tableName);

		ArrayList parentList = null;
		Hashtable columnWidths = null;
		ColWidthInfo colWidInfo = null;
		double minimumWidth = 0.0;
		double maximumWidth = 0.0;
		double availableWidth = 0.0;

		Hashtable colWidths = (Hashtable) allList.get(1);
		ArrayList colList = (ArrayList) allList.get(3);

		colWidInfo = (ColWidthInfo) allList.get(0);

		if(parentTableName == null) {
			availableWidth = colWidInfo.getAvailableWidth();
		} else {
			parentList = (ArrayList) tableWidths.get(parentTableName);
			Hashtable parentColWidths = (Hashtable) parentList.get(1);
			System.out.println(" parentCellname = " + parentColName);
			
			ColWidthInfo parentWidthInfo = (ColWidthInfo) parentColWidths.get(parentColName);
			availableWidth = parentWidthInfo.getWidth();
		}

		System.out.println(" in APCW av width = " + availableWidth);

		maximumWidth = colWidInfo.getMaxWidth();
		minimumWidth = colWidInfo.getMinWidth();

		double widthD = maximumWidth - minimumWidth;
		double widthW = availableWidth - minimumWidth;

		assignPctColWidths(colList, colWidths, availableWidth, widthW, widthD, tableName);

		//	for(int i=0;i<colList.size();i++) {
		//		ColumnInfo coi = (ColumnInfo) colList.get(i);	
		//		ColWidthInfo cwi = (ColWidthInfo) colWidths.get(coi.getColName());
		//		System.out.println(" width of col " + coi.getColName() + " = " + cwi.getWidth());
		//	}
		
		
		} catch(Exception e) {
			e.printStackTrace();
		}

	}


  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double calcAvailWidth(
		ColumnInfo [] spaceInfo,
		double avWidth) {
		
		double cellSp = 0.0;
		double	dBord = 0.0;
		
		if(spaceInfo[0] != null) 
			cellSp = spaceInfo[0].getColWidth();
		
		if(spaceInfo[1] != null) 
			dBord = spaceInfo[1].getColWidth();

		avWidth -= (cellSp + dBord);
		return avWidth;

	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  String prefixZeros(int id) {

		String idstr = Integer.toString(id);
		if (idstr.length() == 1) {
			idstr = "0" + idstr;
			return idstr;
		} else {
			return idstr;
		}
	}



  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double GetMaximumWidthOfText(String text) {

		StringTokenizer stokmax = new StringTokenizer(text, "<BR>");
		double maxwid = 0;

		while (stokmax.hasMoreTokens()) {
			String tok = stokmax.nextToken();
			double charWid =
//				GetWidthFromFontMetrics(tok, "sans-serif", 24, Font.BOLD);
				GetWidthFromFontMetrics(tok);
			maxwid = KeepMax(maxwid, charWid);
		}

		return maxwid;
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double GetMinimumWidthOfText(String text) {
		StringTokenizer stokmax = new StringTokenizer(text, " ");
		double mwid = 0;
		while (stokmax.hasMoreTokens()) {
			String tok = stokmax.nextToken();
			double charWid =
				GetWidthFromFontMetrics(tok);
			mwid = KeepMax(mwid, charWid);
		}
		return mwid;
	}

/*	
	public int getTextState(String text, FontState fontState) {

		 // parse text for upper/lower case and call addRealText
            char c;
			int end = text.length();
		 	char [] data = text.toCharArray();
            char newdata[] = new char[end];
            boolean isLowerCase;
            int caseStart;
            FontState fontStateToUse;
	        FontState smallCapsFontState;
	            
            if (fontState.getFontVariant() == FontVariant.SMALL_CAPS) {
	            try {
	                int smallCapsFontHeight =
	                    (int)(((double)fontState.getFontSize()) * 0.8d);
	                smallCapsFontState = new FontState(fontState.getFontInfo(),
	                                                   fontState.getFontFamily(),
	                                                   fontState.getFontStyle(),
	                                                   fontState.getFontWeight(),
	                                                   smallCapsFontHeight,
	                                                   FontVariant.NORMAL);
	            } catch (FOPException ex) {
	                smallCapsFontState = fontState;
	                //log.error("Error creating small-caps FontState: "
	                //                       + ex.getMessage());
	            }
            }           
           
            for (int i = 0; i < end; ) {
                caseStart = i;
                c = data[i];
                isLowerCase = (java.lang.Character.isLetter(c)
                               && java.lang.Character.isLowerCase(c));
                while (isLowerCase
                        == (java.lang.Character.isLetter(c)
                            && java.lang.Character.isLowerCase(c))) {
                    if (isLowerCase) {
                        newdata[i] = java.lang.Character.toUpperCase(c);
                    } else {
                        newdata[i] = c;
                    }
                    i++;
                    if (i == end)
                        break;
                    c = data[i];
                }
                if (isLowerCase) {
                    fontStateToUse = smallCapsFontState;
                } else {
                    fontStateToUse = fontState;
                }

                int index = getWidthOfRealText(fontStateToUse,
                                        wrapOption, whiteSpaceCollapse,
                                        newdata, caseStart, i, textState,
                                        vAlign);
                if (index != -1) {
                    return index;
                }
            }
		return -1;	
	}
*/	
	

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double GetWidthFromFontMetrics(
		String text) {

//		int fontStateToUse = getFontStateToUse(text);
		int wordStart = 0;
        int wordLength = 0;
        int wordWidth = 0;
        int start = 0;
        int end = text.length();
		char [] data = text.toCharArray();
		int spaceWidth = 0;		        
		int finalWidth = 0;		        
		int pendingWidth = 0;		        
		int embeddedLinkStart = 0;
		boolean spacePresent = false;
				
        // With CID fonts, space isn't neccesary currentFontState.width(32)
        int whitespaceWidth = currentFontState.getCharWidth(' ');
		boolean isText = false;
        boolean isMultiByteChar = false;
//		Panel p = new Panel();
 		int TEXT = 2;
 		int WHITESPACE = 1;
	    int MULTIBYTECHAR = 3;

 		int prev = WHITESPACE;
 		
        /* iterate over each character */
        for (int i = start; i < end; i++) {
            int charWidth;
            /* get the character */
            char c = data[i];
            if (!(isSpace(c) || (c == '\n') || (c == '\r') || (c == '\t')
                  || (c == '\u2028'))) {
                charWidth = currentFontState.getCharWidth(c);
                isText = true;
                isMultiByteChar = (c > 127);
                // Add support for zero-width spaces
                if (charWidth <= 0 && c != '\u200B' && c != '\uFEFF')
                    charWidth = whitespaceWidth;
            } else {
                if ((c == '\n') || (c == '\r') || (c == '\t'))
                    charWidth = whitespaceWidth;
                else
                    charWidth = currentFontState.getCharWidth(c);

                isText = false;
                isMultiByteChar = false;

                if (prev == WHITESPACE) {

                    // if current & previous are WHITESPACE
                    if (whiteSpaceCollapse == WhiteSpaceCollapse.FALSE) {
                        if (isSpace(c)) {
                            spaceWidth += currentFontState.getCharWidth(c);
                        } else if (c == '\n' || c == '\u2028') {
                            // force line break
                            if (spaceWidth > 0) {
                                InlineSpace is = new InlineSpace(spaceWidth);
                                finalWidth += spaceWidth;
                                spaceWidth = 0;
                            }
                            return finalWidth;
                        } else if (c == '\t') {
                            spaceWidth += 8 * whitespaceWidth;
                        }
                    } else if (c == '\u2028') {
                        // Line separator
                        // Breaks line even if WhiteSpaceCollapse = True
                        if (spaceWidth > 0) {
                            InlineSpace is = new InlineSpace(spaceWidth);
                            finalWidth += spaceWidth;
                            spaceWidth = 0;
                        }
                        return finalWidth;
                    }

                } else if (prev == TEXT || prev == MULTIBYTECHAR ) {

                    // if current is WHITESPACE and previous TEXT
                    // the current word made it, so
                    // add the space before the current word (if there
                    // was some)
                    
                    spacePresent = true;

                    if (spaceWidth > 0) {
                        InlineSpace is = new InlineSpace(spaceWidth);
                        finalWidth += spaceWidth;
                        spaceWidth = 0;
                    }

                    // add the current word

                    if (wordLength > 0) {
                        // The word might contain nonbreaking
                        // spaces. Split the word and add InlineSpace
                        // as necessary. All spaces inside the word
                        // Have a fixed width.
                        addSpacedWord(new String(data, wordStart, wordLength),
                                      finalWidth, 0, false);
                        finalWidth += wordWidth;

                        // reset word width
                        wordWidth = 0;
                    }

                    // deal with this new whitespace following the
                    // word we just added
                    prev = WHITESPACE;

                     embeddedLinkStart = 0;    
                    // reset embeddedLinkStart since a space was encountered

                    spaceWidth = currentFontState.getCharWidth(c);

                    /*
                     * here is the place for white-space-treatment value 'ignore':
                     * if (this.spaceTreatment ==
                     * SpaceTreatment.IGNORE) {
                     * // do nothing
                     * } else {
                     * spaceWidth = currentFontState.width(32);
                     * }
                     */
                    if (whiteSpaceCollapse == WhiteSpaceCollapse.FALSE) {
                        if (c == '\n' || c == '\u2028') {
                            // force a line break
                            return finalWidth;
                        } else if (c == '\t') {
                            spaceWidth = whitespaceWidth;
                        }
                    } else if (c == '\u2028') {
                        return finalWidth;
                    }
                } else {

                    // if current is WHITESPACE and no previous

                    if (whiteSpaceCollapse == WhiteSpaceCollapse.FALSE) {
                        if (isSpace(c)) {
                            prev = WHITESPACE;
                            spaceWidth = currentFontState.getCharWidth(c);
                        } else if (c == '\n') {
                            // force line break
                            // textdecoration not used because spaceWidth is 0
                            InlineSpace is = new InlineSpace(spaceWidth);
                      //      addChild(is);
                            return finalWidth;
                        } else if (c == '\t') {
                            prev = WHITESPACE;
                            spaceWidth = 8 * whitespaceWidth;
                        }

                    } else {
                        // skip over it
                        wordStart++;
                    }
                }   // end outer else
            }  // end isSpace else

		    if (isText) {                        // current is TEXT
                int curr = isMultiByteChar ? MULTIBYTECHAR : TEXT;
                if (prev == WHITESPACE) {

                    // if current is TEXT and previous WHITESPACE
                    wordWidth = charWidth;
                    prev = curr;
                    wordStart = i;
                    wordLength = 1;
                } else if (prev == TEXT || prev == MULTIBYTECHAR ) {
                    if ( prev == TEXT && curr == TEXT || ! canBreakMidWord()) {
                        wordLength++;
                        wordWidth += charWidth;
                    } else {
                        InlineSpace is = new InlineSpace(spaceWidth);
                        finalWidth += spaceWidth;
                        spaceWidth = 0;

                        finalWidth += pendingWidth;

                        // reset pending areas array
                        pendingWidth = 0;

                        // add the current word
                        if (wordLength > 0) {
                            // The word might contain nonbreaking
                            // spaces. Split the word and add InlineSpace
                            // as necessary. All spaces inside the word
                            // have a fixed width.
                            addSpacedWord(new String(data, wordStart, wordLength),
                                          finalWidth, 0, false);
                            finalWidth += wordWidth;
 							
                        }
                        spaceWidth = 0;
                        wordStart = i;
                        wordLength = 1;
                        wordWidth = charWidth;
                    }
                    prev = curr;
                } else {                         // nothing previous

                    prev = curr;
                    wordStart = i;
                    wordLength = 1;
                    wordWidth = charWidth;
                }

            } // end isText
        } // end of iteration over text

        if (prev == TEXT || prev == MULTIBYTECHAR) {

            if (spaceWidth > 0) {
                InlineSpace pis = new InlineSpace(spaceWidth);
                // Make sure that this space doesn't occur as
                // first thing in the next line
                pis.setEatable(true);
                spaceWidth = 0;
            }

            
            addSpacedWord(new String(data, wordStart, wordLength),
                          finalWidth + pendingWidth,
                          spaceWidth, true);

             finalWidth += wordWidth;

//            embeddedLinkStart += wordWidth;
//            wordWidth = 0;
        }
        
        
        if(spacePresent) {
        	wordWidth = finalWidth;	
        }

        
		return wordWidth/72.0;

//		Font f = new Font(font, fontStyle, sz);
//		FontMetrics fm = p.getFontMetrics(f);

		//		System.out.println(" width for text " + text);	
		//		System.out.println(" width from FM = " + fm.stringWidth(text)/72.0);
//		return fm.stringWidth(text) / 72.0;

	}


 /**
     * Checks if it's legal to break a word in the middle
     * based on the current language property.
     * @return true if legal to break word in the middle
     */
    private  boolean canBreakMidWord() {
        boolean ret = false;
//        if (hyphProps != null && hyphProps.language != null
//            &&!hyphProps.language.equals("NONE")) {
//            String lang = hyphProps.language.toLowerCase();
//            if ("zh".equals(lang) || "ja".equals(lang) || "ko".equals(lang)
//                || "vi".equals(lang))
//                ret = true;
//        }
        return ret;
    }

    /**
     * Helper method to determine if the character is a
     * space with normal behaviour. Normal behaviour means that
     * it's not non-breaking
     */
    private  boolean isSpace(char c) {
        if (c == ' ' || c == '\u2000' ||    // en quad
            c == '\u2001' ||                    // em quad
            c == '\u2002' ||                    // en space
            c == '\u2003' ||                    // em space
            c == '\u2004' ||                    // three-per-em space
            c == '\u2005' ||                    // four--per-em space
            c == '\u2006' ||                    // six-per-em space
            c == '\u2007' ||                    // figure space
            c == '\u2008' ||                    // punctuation space
            c == '\u2009' ||                    // thin space
            c == '\u200A' ||                    // hair space
            c == '\u200B')                      // zero width space
            return true;
        else
            return false;
    }

    /**
     * Method to determine if the character is a nonbreaking
     * space.
     */
    private  boolean isNBSP(char c) {
        if (c == '\u00A0' || c == '\u202F' ||    // narrow no-break space
            c == '\u3000' ||                    // ideographic space
            c == '\uFEFF') {                    // zero width no-break space
            return true;
        } else
            return false;
    }


    /**
     * Add a word that might contain non-breaking spaces.
     * Split the word into WordArea and InlineSpace and add it.
     * If addToPending is true, add to pending areas.
     */
    private  int addSpacedWord(String word, int startw,
                               int spacew, 
                               boolean addToPending) {
                               	
        StringTokenizer st = new StringTokenizer(word, "\u00A0\u202F\u3000\uFEFF", true);
        while (st.hasMoreTokens()) {
            String currentWord = st.nextToken();

            if (currentWord.length() == 1
                && (isNBSP(currentWord.charAt(0)))) {
                // Add an InlineSpace
                int spaceWidth = currentFontState
                    .getCharWidth(currentWord.charAt(0));
                if (spaceWidth > 0) {
                    InlineSpace is = new InlineSpace(spaceWidth);
                    startw += spaceWidth;
//                    if (prevUlState) {
//                        is.setUnderlined(textState.getUnderlined());
//                    }
//                    if (prevOlState) {
//                        is.setOverlined(textState.getOverlined());
//                    }
//                    if (prevLTState) {
//                        is.setLineThrough(textState.getLineThrough());
//                    }
//
//                    if (addToPending) {
//                        pendingAreas.add(is);
//                        pendingWidth += spaceWidth;
//                    } else {
//                        addChild(is);
//                    }
                }
            } else {
                int wordWidth = currentFontState.getWordWidth(currentWord);
//                WordArea ia = new WordArea(currentFontState, this.red,
//                                           this.green, this.blue,
//                                           currentWord,
//                                           wordWidth);
//                ia.setYOffset(placementOffset);
//                ia.setUnderlined(textState.getUnderlined());
//                prevUlState = textState.getUnderlined();
//                ia.setOverlined(textState.getOverlined());
//                prevOlState = textState.getOverlined();
//                ia.setLineThrough(textState.getLineThrough());
//                prevLTState = textState.getLineThrough();
//                ia.setVerticalAlign(vAlign);
//
//                if (addToPending) {
//                    pendingAreas.add(ia);
//                    pendingWidth += wordWidth;
//                } else {
//                    addChild(ia);
//                }
//                if (ls != null) {
//                    Rectangle lr = new Rectangle(startw, spacew,
//                                                 ia.getContentWidth(),
//                                                 fontState.getFontSize());
//                    ls.addRect(lr, this, ia);
//                }
                startw += wordWidth;
            }
        }
        
        return startw;
    }


	
  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */	
	public  void assignPctColWidths(
		ArrayList columnList,
		Hashtable colWidths,
		double availableWidth,
		double diffW,
		double diffD, String tableName)
		throws Exception {

		boolean addedUnspecified = false;
		boolean addedPercentCols = false;
		boolean addedAbsCols = false;
		boolean addedProCols = false;
		int colSize = columnList.size();

//		       System.out.println("BEGIN DEBUG colwidths size " + colWidths.size());
//				for(int j=0;j<colSize;j++)
//					System.out.println("unsorted " + 
//		((ColumnInfo)columnList.get(j)).getColWidthType());
//				System.out.println("END DEBUG");


		try {

		ArrayList sortedCols = new ArrayList();
		ColumnInfo coInfo = null;



		for (int i = 0; i < ColWidthTypes.NUMCOLTYPES; i++) {

			//   System.out.println("iteration " + (i+1));

			if (!addedUnspecified) {
				for (int j = 0; j < colSize; j++) {
					coInfo = (ColumnInfo) columnList.get(j);
					Element elem = coInfo.getColElement();

					if (!coInfo.isColWidthSpecified()) {
						sortedCols.add(coInfo);
					}
				}
				addedUnspecified = true;
				continue;
			}

			if (addedUnspecified && !addedPercentCols) {
				for (int j = 0; j < colSize; j++) {
					coInfo = (ColumnInfo) columnList.get(j);
					Element elem = coInfo.getColElement();

					if (coInfo.isColWidthSpecified()) {
						int type = coInfo.getColWidthType();
						if (type == ColWidthTypes.COLUMN_WIDTH_PERCENT) {
							sortedCols.add(coInfo);
						}
					}
				}

				addedPercentCols = true;
				continue;
			}

			if (addedUnspecified && addedPercentCols && !addedAbsCols) {
				for (int j = 0; j < colSize; j++) {
					coInfo = (ColumnInfo) columnList.get(j);
					Element elem = coInfo.getColElement();

					if (coInfo.isColWidthSpecified()) {
						int type = coInfo.getColWidthType();
						if (type == ColWidthTypes.COLUMN_WIDTH_CMS
							|| type == ColWidthTypes.COLUMN_WIDTH_INCHES) {
							sortedCols.add(coInfo);
						}
					}
				}
				addedAbsCols = true;
				continue;
			}

			if (addedUnspecified
				&& addedPercentCols
				&& addedAbsCols
				&& !addedProCols) {
				for (int j = 0; j < colSize; j++) {
					coInfo = (ColumnInfo) columnList.get(j);
					Element elem = coInfo.getColElement();

					if (coInfo.isColWidthSpecified()) {
						int type = coInfo.getColWidthType();
						if (type == ColWidthTypes.COLUMN_WIDTH_PROPORTIONAL) {
							sortedCols.add(coInfo);
						}
					}
				}

				addedProCols = true;
				continue;
			}

//			        System.out.println("BEGIN DEBUG");
//					for(int j=0;j<sortedCols.size();j++) {
//						System.out.println("sorted 4" + 
//			((ColumnInfo)sortedCols.get(j)).getColWidthType());
//						System.out.println("sorted 4" + 
//			((ColumnInfo)sortedCols.get(j)).getColWidth());
//			
//					System.out.println("END DEBUG");
//					}

		} // end for

		reflowLayout(
//			availWidth,
			tableName,
			sortedCols,
			columnList,
			colWidths,
			availableWidth,
			diffW,
			diffD);
			
			} catch(Exception e) {
				e.printStackTrace();
			}

	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  void reflowLayout(
//		double availWidth,
		String tableName, 
		ArrayList sortedCols,
		ArrayList columnList,
		Hashtable colWidths,
		double availableWidth,
		double diffW,
		double diffD) {

		ColumnInfo colInfo = null;
		Element colElem = null;
		Element tabElem = null;
		int colWidthType = ColWidthTypes.COLUMN_WIDTH_UNSPECIFIED;
		double tw = 0.0;
		double diff = 0.0;
		double currwid = 0.0;
		int colSize = columnList.size();
		int sz = colSize;
		int alloc = 0;
		double PHI = 0.0;
		double PHISUM = 0.0;
		int widthToUse = ColWidthTypes.COLUMN_WIDTH_MAX;
		boolean done = false;
		boolean unspecifiedColsPresent = true;
		ColWidthInfo coInfo = null;
		double pctwid = 0.0;
		double[] cols = null;

		cols = new double[colSize];

		int rj = 1;

//		System.out.println(" reflow layout  av width " + availableWidth);
//				System.out.println("n o of iters = " + noOfIterations);

		try {

		while (!done) {

			tw = 0.0;
			unspecifiedColsPresent = true;

//			System.out.println(" Inside while for the  " + rj + " time ");
			rj++;

			for (int j = 0; j < sortedCols.size(); j++) {

				colInfo = (ColumnInfo) sortedCols.get(j);
				colElem = colInfo.getColElement();
				int colNumber = colInfo.getColNo();
				String colName = colInfo.getColName();
				int colNo = colInfo.getColNo();

				coInfo = (ColWidthInfo) colWidths.get(colName);
				
				double minwid = coInfo.getMinWidth();
				double maxwid = coInfo.getMaxWidth();

				if (widthToUse == ColWidthTypes.COLUMN_WIDTH_OTHER)
					minwid = cols[j];

				pctwid = 0.0;

				if (colInfo.isColWidthSpecified()) {
					// if col at j=0 has width specified then there
					// are no unspecified cols
					if (j == 0)
						unspecifiedColsPresent = false;

					pctwid = colInfo.getColWidth();
					colWidthType = colInfo.getColWidthType();
				} else {

//					System.out.println(" COLUMN_WIDTH_UNSPECIFIED ");
//					System.out.println(" colName " + colName);
//					System.out.println(" min width = " + minwid);
//					System.out.println(" max width = " + maxwid);
//					System.out.println("width 2 use= " + widthToUse);
					colWidthType = ColWidthTypes.COLUMN_WIDTH_UNSPECIFIED;
					pctwid =
						assignWidth(
							widthToUse,
							maxwid,
							minwid,
							diffW,
							diffD,
							rj - 2);
					cols[j] = pctwid;
				}

				if (colWidthType == ColWidthTypes.COLUMN_WIDTH_PERCENT) {
					currwid = (availableWidth * pctwid) / 100.0;

					//			if(currwid < minwid)
					//				currwid = minwid;

					//setting the col width attribute
					unit = getUnitOfWidth(colWidthType);

					//			System.out.println(" unit %%% = " + unit);

					String currentWidth = Double.toString(currwid);
					int dotpos = currentWidth.indexOf(".");
					if (dotpos > 0 && dotpos + 3 < currentWidth.length())
						currentWidth = currentWidth.substring(0, dotpos + 3);

					currwid = Double.parseDouble(currentWidth);

					coInfo.setWidth(currwid);
					coInfo.setUnitOfWidth(unit);

					PHI = pctwid / 10;
					alloc++;
					PHISUM += PHI;

				} else if (
					colWidthType == ColWidthTypes.COLUMN_WIDTH_PROPORTIONAL) {

					if (PHISUM == 0) {

						currwid = availableWidth * (pctwid / 10);

						//				if(currwid < minwid)
						//					currwid = minwid;

						//setting the col width attribute  for the first col
						unit = getUnitOfWidth(colWidthType);

						String currentWidth = Double.toString(currwid);
						int dotpos = currentWidth.indexOf(".");
						if (dotpos > 0 && dotpos + 3 < currentWidth.length())
							currentWidth =
								currentWidth.substring(0, dotpos + 3);

						currwid = Double.parseDouble(currentWidth);

						coInfo.setWidth(currwid);
						coInfo.setUnitOfWidth(unit);

						PHI = currwid;
						PHISUM += PHI;

					} else { // col width pro

						currwid = (availableWidth / 10) * pctwid;

						//				if(currwid < minwid)
						//					currwid = minwid;

						//setting the col width attribute for other pro cols
						unit = getUnitOfWidth(colWidthType);

						String currentWidth = Double.toString(currwid);
						int dotpos = currentWidth.indexOf(".");
						if (dotpos > 0 && dotpos + 3 < currentWidth.length())
							currentWidth =
								currentWidth.substring(0, dotpos + 3);

						currwid = Double.parseDouble(currentWidth);

						coInfo.setWidth(currwid);
						coInfo.setUnitOfWidth(unit);

						PHI = currwid;
						PHISUM += PHI;

					}

				} else {
					currwid = pctwid;

//					System.out.println(" currwid = " + currwid);

				//		if(currwid < minwid)
				//			currwid = minwid;

					//setting the col width attribute
					unit = getUnitOfWidth(colWidthType);

					String currentWidth = Double.toString(currwid);
					int dotpos = currentWidth.indexOf(".");
					if (dotpos > 0 && dotpos + 3 < currentWidth.length())
						currentWidth = currentWidth.substring(0, dotpos + 3);

					currwid = Double.parseDouble(currentWidth);

					coInfo.setWidth(currwid);
					coInfo.setUnitOfWidth(unit);

					PHI = currwid;
					PHISUM += PHI;

				}

				// table width running sum
				tw += currwid;

			} // end for sortedcols

			if (unspecifiedColsPresent) {
//				System.out.println("table width = " + tw);

				if ((tw <= availableWidth
					&& widthToUse == ColWidthTypes.COLUMN_WIDTH_MAX)
					|| (availableWidth > tw)
					&& Math.abs(availableWidth - tw) < 0.1
					|| rj > noOfIterations) {
//						System.out.println("setting done true ");
						done = true;
				}
				
				
				
			}

			widthToUse = getWidthToUse(widthToUse);

			if (!unspecifiedColsPresent) {
				logger.info("no columns exist with width unspecified");
				done = true;
			}

		} // end while !done
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		

//		int tz = tabList.size();
//		if (alloc == sortedCols.size()) {
//			tabElem = (Element) tabList.get(0);
//			tabElem.setAttribute(
//				"inline-progression-dimension",
//				Double.toString(tw) + unit);
//		}

			ArrayList allList = (ArrayList) tableWidths.get(tableName);
			ColWidthInfo tableInfo = (ColWidthInfo) allList.get(0);
			tableInfo.setAvailableWidth(tw);

	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  int getWidthToUse(int widthUsed) {

		if (widthUsed == ColWidthTypes.COLUMN_WIDTH_MAX) {
			return ColWidthTypes.COLUMN_WIDTH_MIN;
		} else if (widthUsed == ColWidthTypes.COLUMN_WIDTH_MIN) {
			return ColWidthTypes.COLUMN_WIDTH_OTHER;
		} else {
			return ColWidthTypes.COLUMN_WIDTH_OTHER;
		}

	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double assignWidth(
		int widthUsed,
		double max,
		double min,
		double widthW,
		double widthD,
		int rj) {

		double minMaxDiff = max - min;
		double Kfactor = 0.33;
		
		
		
		if (widthUsed == ColWidthTypes.COLUMN_WIDTH_MAX) {
			return max;
		} else if (widthUsed == ColWidthTypes.COLUMN_WIDTH_MIN) {
			return min;
		} else {
			Kfactor = Kfactor/rj;
			return min + (minMaxDiff * (widthW * Kfactor / widthD));

		}
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  String getUnitOfWidth(int type) {

		if (type == ColWidthTypes.COLUMN_WIDTH_UNSPECIFIED)
			return "in";
		else if (type == ColWidthTypes.COLUMN_WIDTH_CMS)
			return "cm";
		else if (type == ColWidthTypes.COLUMN_WIDTH_INCHES)
			return "in";
		else if (type == ColWidthTypes.COLUMN_WIDTH_POINTS)
			return "pt";
		else if (type == ColWidthTypes.COLUMN_WIDTH_PROPORTIONAL)
			return "in";
		else
			return "in";
	}

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  double KeepMax(double currVal, double newVal) {

		if(currVal == 99999.0) 
			return newVal;

		if (currVal < newVal)
			return newVal;

		return currVal;
	}
	
  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  int KeepMax(int newVal, int oldVal) {
		
		if (newVal > oldVal)
			return newVal;

		return oldVal;
	}

	

  /**
     * Return the value explicitly specified on this FO.
     * @param propertyName The name of the base property whose value is desired.
     * @return The value if the property is explicitly set, otherwise null.
     */
	public  void aggregateWidths(
		String tabName,
		boolean countTags,
		String parentTableName,
		String parentTableCellName,
		ColWidthInfo childTableWidth,
		int numOrigCols, boolean isMultiple) {

		double widsum = 0.0;

		logger.debug(" In aggregate widths tname  = " + tabName);
		System.out.println(" In aggregate widths tname  = " + tabName);

		//		Enumeration ems = tableWidths.keys();
		//		System.out.println("AGGI Begin debug");
		//
		//		while(ems.hasMoreElements()) {
		//
		//		String tkey = (String) ems.nextElement();
		//		System.out.println("Table Name = " + tkey);
		//		
		//		ArrayList allList = (ArrayList) tableWidths.get(tkey);
		//				
		//		for(int i=0;i<allList.size();i++) {
		//			// table min and max width
		//			if(i == 0) {
		//		System.out.println("Table width");
		//				
		//				System.out.println(((ColWidthInfo)allList.get(i)).GetMinWidth());
		//				System.out.println(((ColWidthInfo)allList.get(i)).GetMaxWidth());
		//			} else if(i == 1 || i == 2) {
		//
		//				Hashtable widths = (Hashtable) allList.get(i);
		//				Enumeration enm = widths.keys();
		//				while(enm.hasMoreElements()) {
		//					String key = (String) enm.nextElement();
		//					if( i == 1) 
		//						System.out.println("COL name = " + key);
		//					else
		//						System.out.println("CELL name = " + key);
		//
		//
		//					ColWidthInfo info = (ColWidthInfo) widths.get(key);
		//				
		//						
		//
		//					System.out.println(info.GetMinWidth());
		//					System.out.println(info.GetMaxWidth());
		//
		//				}
		//			}
		//		}
		//
		//		}		
		//		
		//		System.out.println("AGGI END debug");

		ArrayList allList = (ArrayList) tableWidths.get(tabName);
		Hashtable ceWidths = (Hashtable) allList.get(2);
		ColWidthInfo tabWidInfo = (ColWidthInfo) allList.get(0);

		Enumeration en = ceWidths.keys();
		int tensp = 0;
		int unitsp = 0;
		int unitsmax = 0;

		double maxwid = 0.0;
		double minwid = 0.0;
		Hashtable colMinHt = new Hashtable();
		Hashtable colMaxHt = new Hashtable();
		double padsum = 0.0;

		while (en.hasMoreElements()) {

			String key = (String) en.nextElement();

			ColWidthInfo cellInfo = (ColWidthInfo) ceWidths.get(key);
			padsum += cellInfo.getPadding();

			// 4 used to position at first number after prefix cell
			String suffix = key.substring(4, key.length());
//						System.out.println(" suffix = " + suffix);
			int suffint = Integer.parseInt(suffix);

			unitsp = suffint % 10;

			double minVal = ((ColWidthInfo) ceWidths.get(key)).getMinWidth();
			double maxVal = ((ColWidthInfo) ceWidths.get(key)).getMaxWidth();

//						System.out.println(" unitsp = " + unitsp);
//						System.out.println(" minwid = " + minVal);
//						System.out.println(" maxwid = " + maxVal);

			Double dmin = (Double) colMinHt.get(new Integer(unitsp));

			if(dmin != null) 
				minVal = Math.max(minVal, dmin.doubleValue());
			
		//	if (dmin != null) {
		//		if (minVal < dmin.doubleValue())
					colMinHt.put(new Integer(unitsp), new Double(minVal));
		//	} else {
		//		colMinHt.put(new Integer(unitsp), new Double(minVal));
		//	}

			Double dmax = (Double) colMaxHt.get(new Integer(unitsp));
			if (dmax != null) {
				if (maxVal > dmax.doubleValue())
					colMaxHt.put(new Integer(unitsp), new Double(maxVal));
			} else {
				colMaxHt.put(new Integer(unitsp), new Double(maxVal));
			}

		}

//		System.out.println(" padsum = " + padsum);
//		System.out.println(" avail width before padsum = " + availWidth);
		availWidth -= padsum;

//		System.out.println(" avail width after padsum = " + availWidth);
		tabWidInfo.setAvailableWidth(availWidth);
		tabWidInfo.setNumberOfCols(numOrigCols);
		
//						System.out.println(" minhash size = " + colMinHt.size());
//						System.out.println(" maxhash size = " + colMaxHt.size());

		int minkeys = colMinHt.size();
		Double [] minwidths = new Double[minkeys];		
		for (Enumeration e = colMinHt.keys(); e.hasMoreElements();) {
			Integer key = (Integer) e.nextElement();
			Double dmn = (Double) colMinHt.get(key);
//							System.out.println(" minhash key = " + key + " val = " + dmn);
			minwidths[key.intValue()] = dmn;
		}

		int maxkeys = colMaxHt.size();
		Double [] maxwidths = new Double[maxkeys];		
		for (Enumeration enum1 = colMaxHt.keys(); enum1.hasMoreElements();) {
			Integer key = (Integer) enum1.nextElement();
			Double dmx = (Double) colMaxHt.get(key);
//							System.out.println(" maxhash key = " + key + " val = " + dmx);
			maxwidths[key.intValue()] = dmx;
		}

		Hashtable tabColWidths = null;
		for (int i = 0; i < minwidths.length; i++) {
//				System.out.println(" Column " + i + "  minwidth  = " + minwidths[i]);
//				System.out.println(" Column " + i + "  maxwidth  = " + maxwidths[i]);

			tabColWidths = (Hashtable) allList.get(1);

			ColWidthInfo winfo =
				(ColWidthInfo) tabColWidths.get("col" + prefixZeros(i));
			winfo.setMinWidth(((Double) minwidths[i]).doubleValue());
			winfo.setMaxWidth(((Double) maxwidths[i]).doubleValue());
			tabColWidths.put("col" + prefixZeros(i), winfo);
		}

		double minsum = 0.0;
		double maxsum = 0.0;
		for (Enumeration em = tabColWidths.keys(); em.hasMoreElements();) {
			String key = (String) em.nextElement();
			ColWidthInfo vinfo = (ColWidthInfo) tabColWidths.get(key);
			if (vinfo != null) {
				minsum += vinfo.getMinWidth();
				maxsum += vinfo.getMaxWidth();
			}
		}

//				System.out.println(" minsum " + minsum);
//				System.out.println(" maxsum " + maxsum);


		if (tabWidInfo != null) {

			if(!isMultiple) {				
				tabWidInfo.setMinWidth(minsum);
				tabWidInfo.setMaxWidth(maxsum);
			} else {
				tabWidInfo.setMinWidth(KeepMax(minsum,childTableWidth.getMinWidth()));
				tabWidInfo.setMaxWidth(KeepMax(maxsum,childTableWidth.getMaxWidth()));
			}
			
			if (countTags) {
//				System.out.println(" counttags " + countTags);
				childTableWidth.setMinWidth(tabWidInfo.getMinWidth());
				childTableWidth.setMaxWidth(tabWidInfo.getMaxWidth());
//				System.out.println("childTableWidth minwidth set to " + childTableWidth.getMinWidth());
//				System.out.println("childTableWidth maxwidth set to " + childTableWidth.getMaxWidth());
				
			}				

		}


		Enumeration ems = tableWidths.keys();
		System.out.println("AGGI Begin debug");

		while (ems.hasMoreElements()) {

			String tkey = (String) ems.nextElement();
			System.out.println("Table Name = " + tkey);

			allList = (ArrayList) tableWidths.get(tkey);

			for (int i = 0; i < allList.size(); i++) {
				// table min and max width
				if (i == 0) { 
					System.out.println("Table width");

					System.out.println(
						((ColWidthInfo) allList.get(i)).getMinWidth());
					System.out.println(
						((ColWidthInfo) allList.get(i)).getMaxWidth());
				} else if (i == 1 || i == 2) {

					Hashtable widths = (Hashtable) allList.get(i);
					Enumeration enm = widths.keys();
					while (enm.hasMoreElements()) {
						String key = (String) enm.nextElement();
						if (i == 1)
							System.out.println("COL name = " + key);
						else
							System.out.println("CELL name = " + key);

						ColWidthInfo info = (ColWidthInfo) widths.get(key);

						System.out.println(info.getMinWidth());
						System.out.println(info.getMaxWidth());

					}
				}
			}

		}

		System.out.println("AGGI END debug");
		

	}

	/*
	        System.out.println();
	        System.out.println("Only elements:");
	        itr = doc.getDescendants(new ElementFilter());
	        while (itr.hasNext()) {
	            Content c = (Content) itr.next();
	            System.out.println(c);
	        }
	
	        System.out.println();
	        System.out.println("Everything that's not an element:");
	        itr = doc.getDescendants(new ElementFilter().negate());
	        while (itr.hasNext()) {
	            Content c = (Content) itr.next();
	            System.out.println(c);
	        }
	
	        System.out.println();
	        System.out.println("Only elements with localname of servlet:");
	        itr = doc.getDescendants(new ElementFilter("servlet"));
	        while (itr.hasNext()) {
	            Content c = (Content) itr.next();
	            System.out.println(c);
	        }
	
	        System.out.println();
	        System.out.println(
	             "Only elements with localname of servlet-name or 
	servlet-class:");
	        itr = doc.getDescendants(new ElementFilter("servlet-name")
	                                 .or(new ElementFilter("servlet-class")));
	        while (itr.hasNext()) {
	            Content c = (Content) itr.next();
	            System.out.println(c);
	        }
	
	        System.out.println();
	        System.out.println("Remove elements with localname of servlet:");
	        itr = doc.getDescendants(new ElementFilter("servlet"));
	        while (itr.hasNext()) {
	            itr.next();
	            itr.remove();
	        }
	
	        XMLOutputter outp = new XMLOutputter();
	        outp.output(doc, System.out);
	
	*/
	
	
	public String getFmtFile() {
		return outputFile;
	}

}
