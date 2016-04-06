package com.exalto.UI.util;

import javax.swing.*;
import java.awt.Point;
import java.awt.event.*;

public class DialogAnimator extends Object {

      public static final int SHAKE_DISTANCE = 10;
      public static final double SHAKE_CYCLE = 50;
      public static final int SHAKE_DURATION = 10000;
      public static final int POS_UPDATE = 5;
      public static final int DISPOSE = 30;
      private JDialog dialog;
      private Point naturalLocation;
      private long startTime;
      private Timer shakeTimer;
      private Point origLoc;
      private Timer stopTimer;
      private Timer eraseTimer;


      public DialogAnimator(JDialog d) {
            dialog = d;
      }

      public void animate( ) {

            naturalLocation = dialog.getLocation();
            startTime = System.currentTimeMillis( );
            origLoc = naturalLocation;

            System.out.println("origLoc.y " + origLoc.y);

            shakeTimer = new Timer(POS_UPDATE, new ActionListener( ) {
                              public void actionPerformed (ActionEvent e) {
                                    // calculate elapsed time
                                    long elapsed =
											System.currentTimeMillis() - startTime;
                                    naturalLocation = 
											dialog.getLocation();


			System.out.println("naturalLocation.y " + naturalLocation.y);

            dialog.setLocation(naturalLocation.x, naturalLocation.y-3);
            dialog.repaint();

            if (origLoc.y - (naturalLocation.y - 3) > 30)
	                stop( );

		            // should we stop timer?
            }
                        });

	            shakeTimer.start( );

	          System.out.println(" dialog isVisisble 1 " + dialog.isVisible());

	            stopTimer = new Timer(DISPOSE, new ActionListener( ) {
     
	 			public void actionPerformed (ActionEvent e) {
                                    // calculate elapsed time
                                    if(dialog.isShowing())  {
                                          long elapsed =
												System.currentTimeMillis() - startTime;
                                          if(elapsed >= SHAKE_DURATION) 
										  {
							                  System.out.println(" dialog isVisisble " +
dialog.isVisible());
							                  System.out.println(" dialog isshowing " +
dialog.isShowing());
                                                            
deanimate();
                                          }

                                    }
                                    // should we stop timer?
                              }
                        });


                  stopTimer.start( );


      }


      public void deanimate( ) {

            naturalLocation = dialog.getLocation();
            startTime = System.currentTimeMillis( );
            origLoc = naturalLocation;

            System.out.println("origLoc.y " + origLoc.y);

            eraseTimer = new Timer(POS_UPDATE, new ActionListener( ) {
                              public void actionPerformed (ActionEvent e) {
                                    // calculate elapsed time
                                    long elapsed = System.currentTimeMillis() - startTime;
                                    naturalLocation = dialog.getLocation();

                                    System.out.println("origLoc.y " + origLoc.y);
                                    
									System.out.println("naturalLocation.y " + naturalLocation.y);

                                    dialog.setLocation(naturalLocation.x, naturalLocation.y+3);
                                    dialog.repaint();

                                    if (naturalLocation.y + 3 - origLoc.y > 30) {
                                          System.out.println(" inside range disposing... ");
                                          eraseTimer.stop();
                                          dialog.dispose();

                                    }
                                    // should we stop timer?
                              }
                        });

            eraseTimer.start( );

      }


      public void stop( ) {
            shakeTimer.stop( );
            dialog.setLocation (naturalLocation);
            dialog.repaint();
      }


}

