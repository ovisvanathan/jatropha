/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.UI.grid.xquery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Properties;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import net.sf.saxon.Configuration;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import org.xml.sax.InputSource;

/**
 *
 * @author omprakash.v
 */
public class XQueryProcessor {

   	int resultId;
    String outFile = "XQuery Result" + getNextId();

    public String getOutFile() {
        return outFile;
    }

    

     public static void main(String[] args) {
     
     }

     public void processXQuery(String queryText, String xmlFileName) {

       //the Q.xquery file
       FileInputStream queryStream=null;
  //     String queryFileName="f://Caps//aatma//pub.xquery";

       //documentul XML ce va fi interogat este reprezentat de
       //fisierul AircraftDealer.xml
       File XMLStream=null;
 //      String xmlFileName="f://Caps//aatma//prices.xml";

       //print the result to the console
       OutputStream destStream=System.out;

       //compile the XQuery expression
       XQueryExpression exp=null;

       //create a Configuration object
       Configuration C=new Configuration();

       //static and dynamic context
       StaticQueryContext SQC=new StaticQueryContext(C);
       DynamicQueryContext DQC=new DynamicQueryContext(C);

       //indentation
       Properties props=new Properties();
       props.setProperty(OutputKeys.METHOD,"xml");
       props.setProperty(OutputKeys.INDENT,"yes");

        try{
            //  queryStream=new FileInputStream(queryFileName);
            //   SQC.setBaseURI(new File(queryFileName).toURI().toString());

               //compilation
               exp=SQC.compileQuery(new StringReader(queryText));
       
               //TODO: OV commented on 15/09/09.  
      //         SQC=exp.getStaticContext();

           }catch(net.sf.saxon.trans.XPathException e)
             {System.err.println(e.getMessage());
           }catch(java.io.IOException e)
             {System.err.println(e.getMessage());}

          //get the XML ready
          try{

                 XMLStream=new File(xmlFileName);
                 InputSource XMLSource=new InputSource(XMLStream.toURI().toString());
                 SAXSource SAXs=new SAXSource(XMLSource);
                 DocumentInfo DI=SQC.buildDocument(SAXs);
                 DQC.setContextNode(DI);

   			    PrintWriter prtout = new PrintWriter(new FileOutputStream(outFile.toString()));

                 //evaluating
                 exp.run(DQC, new StreamResult(prtout), props);
                 destStream.close();
                 
             }catch(net.sf.saxon.trans.XPathException e)
                  {System.err.println(e.getMessage());
             }catch (java.io.IOException e)
                  {System.err.println(e.getMessage());}

    }

     public void processXQueryFromFile(File queryFile, File xmlFile) {
         processXQueryFromFile(queryFile.getAbsolutePath(), xmlFile.getAbsolutePath());
     }
     public void processXQueryFromFile(String queryFileName, String xmlFileName) {

        //the Q.xquery file
       FileInputStream queryStream=null;
 //      String queryFileName="f://Caps//aatma//pub.xquery";

       //documentul XML ce va fi interogat este reprezentat de
       //fisierul AircraftDealer.xml
       File XMLStream=null;
 //      String xmlFileName="f://Caps//aatma//prices.xml";

       //print the result to the console
       OutputStream destStream=System.out;

       //compile the XQuery expression
       XQueryExpression exp=null;

       //create a Configuration object
       Configuration C=new Configuration();

       //static and dynamic context
       StaticQueryContext SQC=new StaticQueryContext(C);
       DynamicQueryContext DQC=new DynamicQueryContext(C);

       //indentation
       Properties props=new Properties();
       props.setProperty(OutputKeys.METHOD,"xml");
       props.setProperty(OutputKeys.INDENT,"yes");

        try{
               queryStream=new FileInputStream(queryFileName);
               SQC.setBaseURI(new File(queryFileName).toURI().toString());

               //compilation
               exp=SQC.compileQuery(new FileReader(queryFileName));
               //TODO: OV commented on 15/09/09.     
               //       SQC=exp.getStaticContext();

           }catch(net.sf.saxon.trans.XPathException e)
             {System.err.println(e.getMessage());
           }catch(java.io.IOException e)
             {System.err.println(e.getMessage());}

          //get the XML ready
          try{
                 XMLStream=new File(xmlFileName);
                 InputSource XMLSource=new InputSource(XMLStream.toURI().toString());
                 SAXSource SAXs=new SAXSource(XMLSource);
                 DocumentInfo DI=SQC.buildDocument(SAXs);
                 DQC.setContextNode(DI);

			    PrintWriter prtout = new PrintWriter(new FileOutputStream(outFile.toString()));

                 //evaluating
                 exp.run(DQC, new StreamResult(prtout), props);
                 destStream.close();

             }catch(net.sf.saxon.trans.XPathException e)
                  {System.err.println(e.getMessage());
             }catch (java.io.IOException e)
                  {System.err.println(e.getMessage());

             }
     }


	private synchronized int getNextId() {
		return ++resultId;
	}

}
