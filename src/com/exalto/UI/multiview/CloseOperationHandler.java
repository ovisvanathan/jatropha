package com.exalto.UI.multiview;

public interface CloseOperationHandler {

	/**
      * Perform the closeOperation on the opened elements in the multiview topcomponent.
      * Can resolve by itself just based on the states of the elements or ask the user for
      * the decision.
      * @param elements {@link org.netbeans.core.spi.multiview.CloseOperationState} instances of {@link org.netbeans.core.spi.multiview.MultiViewElement}s that cannot be
      * closed and require resolution.
      * @returns true if component can be close, false if it shall remain opened.
      */
     boolean resolveCloseOperation(CloseOperationState[] elements);

}
