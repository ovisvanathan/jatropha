package com.exalto.UI.multiview;

import java.awt.Image;


public class MVTextDesc extends MVDesc {

	  public MVTextDesc(String  name, Image  img, int persType, MultiViewElement element) {
          el = element;
          this.name = name;
          this.img = img;
          type = persType;
      }
    
	
    public MultiViewElement createElement() {
        if (el == null) {
            // for persistence.. elem is transient..
       	 el = new MVTextElem();
        }
        
        return el;
    }


}
