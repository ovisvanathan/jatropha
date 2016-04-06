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
 
 import java.util.TooManyListenersException ;
 import javax.swing.Action ;
 import javax.swing.JComponent ;
 import javax.swing.JToolBar ;
 import org.openide.util.Lookup;
 
 
 
 /**
  * Delegate to be implemented by the MultiViewTopComponent
  */
 public interface MultiViewElementCallbackDelegate {
 
     public void requestActive();
 
     public void requestVisible();
 
     public Action [] createDefaultActions();
 
     public void updateTitle(String  title);
 
     public boolean isSelectedElement();
     
//     public TopComponent getTopComponent();
     
 }
 
