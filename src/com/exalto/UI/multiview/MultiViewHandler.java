package com.exalto.UI.multiview;

 
 /**
  * A handler for the multiview's {@link org.openide.windows.TopComponent}, obtainable via
  * {@link org.netbeans.core.spi.multiview.MultiViewFactory}, that allows
  * examination of Component's content and programatic changes in visible/activated elements.
  * @author mkleint
  */
 public final class MultiViewHandler {
 
 //    static {
 //        AccessorImpl.createAccesor();
 //    }
 
     private MultiViewHandlerDelegate del;
 
     MultiViewHandler(MultiViewHandlerDelegate delegate) {
         del = delegate;
     }
     /**
      * Returns the array of <code>MultiViewPerspective</code>s that the {@link org.openide.windows.TopComponent} is composed of.
      * @return array of defined perspectives.
      */
     public MultiViewPerspective[] getPerspectives() {
         return del.getDescriptions();
     }
     
     /**
      * Returns the currently selected <code>MultiViewPerspective</code> in the {@link org.openide.windows.TopComponent}.
      * It's element can be either visible or activated.
      * @return selected perspective
      */
     public MultiViewPerspective getSelectedPerspective() {
         return del.getSelectedDescription();
     }
     
     /**
      * returns the MultiViewElement for the given Description if previously created,
      * otherwise null.
      */
 // SHOULD NOT BE USED, ONLY IN EMERGENCY CASE! 
 // public MultiViewPerspectiveComponent getElementForPerspective(MultiViewPerspective desc) {
 // return del.getElementForDescription(desc);
 // }
 
     /**
      * Requests focus for the <code>MultiViewPerspective</code> passed as parameter, if necessary
      * will switch from previously selected <code>MultiViewPerspective</code>
      * @param desc the new active selection
      */
     public void requestActive(MultiViewPerspective desc) {
         del.requestActive(desc);
     }
     
     /**
      * Changes the visible <code>MultiViewPerspective</code> to the one passed as parameter.
      * @param desc the new selection
      *
      */
     
     public void requestVisible(MultiViewPerspective desc) {
         del.requestVisible(desc);
     }
     
     
  
 }
 
