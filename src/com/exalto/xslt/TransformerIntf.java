/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.exalto.xslt;

import java.io.InputStream;

/**
 *
 * @author omprakash.v
 */
public interface TransformerIntf {

    public void doTransform()  throws Exception ;

    public String getXsltOutput();
    
    public String getErrorMessages();

   public void doTransform(InputStream xmlFile, InputStream xslFile) throws Exception;

   public void setXslVersion(String version);
   
}
