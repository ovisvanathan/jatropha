package com.exalto.UI.util;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;

import com.exalto.UI.XmlEditor;

public class BubblePanelSimulator extends JDialog {

      JFrame jfr = null;
      int mouseXPos = 0;
      int mouseYPos = 0;
      JBubblePanel bubblePanel = null;
      JTextPane textPane = null;
	  JFrame parent;

/*
      public BubblePanelSimulator(JFrame jf, JPanel jp, String statusText, int xpos, int ypos) {
            super(jp, "Message Dialog", true);
			this.parent = jf;
            mouseXPos = xpos;
            mouseYPos = ypos;
            init(jp, statusText, null);

             MouseListener mouseListener = new MouseAdapter() {

      public void mouseMoved(MouseEvent mouseEvent) {

        }

      public void mouseEntered(MouseEvent mouseEvent) {
      }

      public void mousePressed(MouseEvent mouseEvent) {

            System.out.println(" in mousepressed ");

            int evtX = mouseEvent.getX();
            int evtY = mouseEvent.getY();

            System.out.println(" evtx= " +  evtX);
            System.out.println(" evty= " +  evtY);

            Rectangle r = bubblePanel.getBoundsForCloseIcon();

            int bx = (int) r.getX();
            int by = (int) r.getY();

            System.out.println("x= " +  r.getX());
            System.out.println("y= " +  r.getY());
            System.out.println("w= " +  r.getWidth());
            System.out.println("h= " +  r.getHeight());

            if(r.contains(evtX, evtY)) {

                        System.out.println(" in range disposing");
                        dispose();
            }


        }

      public void mouseReleased(MouseEvent mouseEvent) {
      }

      public void mouseExited(MouseEvent mouseEvent) {
        }
    };

            addMouseListener(mouseListener);

      }
	*/
      public BubblePanelSimulator(JFrame jf, JPanel jp, String 
statusText)
{
	this(jf, jp, statusText, null);
}

      public BubblePanelSimulator(JFrame jf, JPanel jp, String 
statusText, URL imgURL)
{
            super();
			this.parent = jf;
            init(jp, statusText, imgURL);

                   MouseListener mouseListener = new MouseAdapter() {

      public void mouseMoved(MouseEvent mouseEvent) {

        }

      public void mouseEntered(MouseEvent mouseEvent) {
      }

      public void mousePressed(MouseEvent mouseEvent) {

            System.out.println(" in mousepressed ");

            int evtX = mouseEvent.getX();
            int evtY = mouseEvent.getY();

            System.out.println(" evtx= " +  evtX);
            System.out.println(" evty= " +  evtY);

            Rectangle r = bubblePanel.getBoundsForCloseIcon();

            int bx = (int) r.getX();
            int by = (int) r.getY();

            System.out.println("x= " +  r.getX());
            System.out.println("y= " +  r.getY());
            System.out.println("w= " +  r.getWidth());
            System.out.println("h= " +  r.getHeight());

            if(r.contains(evtX, evtY)) {

                        System.out.println(" in range disposing");





                        dispose();
            }


        }

      public void mouseReleased(MouseEvent mouseEvent) {
      }

      public void mouseExited(MouseEvent mouseEvent) {
        }
    };

            addMouseListener(mouseListener);

      }

      public void init(JComponent comp, String statusText, URL imgURL) {

            HashMap errHash = null;

            bubblePanel = new JBubblePanel();
            bubblePanel.setLayout(new BorderLayout());

            JLabel lab = new JLabel();

           lab.setPreferredSize(new Dimension(12,12));

      //    ImageIcon img = new ImageIcon("divclose.gif");
            JPanel labPanel = new JPanel();
      //    labPanel.add(Box.createRigidArea(new Dimension(335, 5)));
      //    lab.setIcon(img);
            labPanel.add(lab);
            labPanel.setBackground(new Color(22, 56, 140));
            labPanel.setOpaque(false);
            bubblePanel.add(labPanel, BorderLayout.NORTH);

            JPanel midPane = new JPanel();

            GridBagLayout gb2=new GridBagLayout();
            midPane.setLayout(gb2);
            midPane.setBorder(BorderFactory.createLineBorder(new Color(0,56, 107)));

			System.out.println(" imgurl = " + imgURL);

            ImageIcon img = new ImageIcon(imgURL);
            JLabel jlab = new JLabel();
		//	jlab.setPreferredSize(new Dimension(48,48));
            jlab.setIcon(img);
            jlab.setText("");

            GridBagConstraints gc2 = new GridBagConstraints(); // for use with gb2
            gc2.gridx=0;
            gc2.gridy=0;
            gc2.weightx=1.0;
			gc2.insets = new Insets(5,5,5,5);  //top padding    
            gc2.anchor = GridBagConstraints.NORTH; //bottom of space

            gb2.setConstraints(jlab, gc2);
            midPane.add(jlab);

	        textPane = new JTextPane();
            textPane.setEditable(false);
     	   JScrollPane jscp = new JScrollPane(textPane);

 		    jscp.setPreferredSize(new Dimension(200, 25));
			Element root = textPane.getDocument().getDefaultRootElement();
			textPane.setCaretPosition(root.getElement(0).getStartOffset() );
			/*
			//  The following will position the caret at the start of the first word
			try
			  {
					component.setCaretPosition(
					Utilities.getNextWord(component, component.getCaretPosition()));
			  }
			  catch(Exception e) {System.out.println(e);}
			*/



            gc2.fill = GridBagConstraints.BOTH;

      //      gc2.insets = new Insets(5,5,5,5);  //top padding
      //    gc2.ipady = 5;
            gc2.weightx= 1.0;
            gc2.ipadx = 250;
            gc2.gridx = 1;
	        gc2.weighty = 1.0;   //request any extra vertical space
//          gc2.gridwidth = 3;
//          gc2.anchor = GridBagConstraints.WEST; //bottom of space
     
	 		gb2.setConstraints(jscp, gc2);
	// 		gb2.setConstraints(textPane, gc2);
    //        midPane.add(textPane);
            midPane.add(jscp);

            JButton reval = new JButton("Recheck");
			
			reval.setPreferredSize(new Dimension(20,20));

      	    reval.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

               if(e.getSource() instanceof JButton) {
			   	   XmlEditor xedit = (XmlEditor) parent;
                   xedit.doCheckValidity();
               }
            }
         });


            gc2.insets = new Insets(2,2,2,2);  //top padding
      //    gc2.anchor=GridBagConstraints.NORTHEAST;
            gc2.gridx = 5;
            gc2.ipady = 0;
            gc2.ipadx = 0;
            gc2.weightx=.2;
            gb2.setConstraints(reval, gc2);
            midPane.add(reval);

		/*
            Component strut1 = Box.createHorizontalGlue();
            gc2.gridx = 0;
            gc2.gridy = 1;
            gb2.setConstraints(strut1, gc2);
            midPane.add(strut1);

            Component strut2 = Box.createHorizontalGlue();
            gc2.gridx = 2;
            gc2.gridy = 1;
            gb2.setConstraints(strut2, gc2);
            midPane.add(strut2);
	*/



//          bubblePanel.add(textPane, BorderLayout.CENTER);
            bubblePanel.add(midPane, BorderLayout.CENTER);

        SimpleAttributeSet normal = new SimpleAttributeSet();
        SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setBold(bold, true);
            try
	   		{
                  System.out.println(" calling  prepareMsgForDisplay ");

                errHash = prepareMsgForDisplay(statusText);
            } catch (Exception e)
            {
                  System.out.println(" caught excep in prepareMsgForDisplay ");
                  e.printStackTrace();
                  errHash = null;
                  insertText(errHash, statusText);
            }

            insertText(errHash, statusText);

/*
        try {

            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("ERRTYPE") + " in ",
                (SimpleAttributeSet) errHash.get("ERRTYPE_ATTR"));

            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("URI") + " \n at Line ",
                (SimpleAttributeSet) errHash.get("URI_ATTR"));
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("LINENO") + " : ",
                (SimpleAttributeSet) errHash.get("LINENO_ATTR"));

                int numparams =
((Integer)errHash.get("NUMPARAMS")).intValue();


                  for(int g=0;g<numparams;g++) {

                      String vparam = (String) errHash.get("VARPARAM" +
lpadZero(g));
                        SimpleAttributeSet vattr = (SimpleAttributeSet)
errHash.get("VARATTR" + lpadZero(g));

                        textPane.getDocument().insertString(
                              textPane.getDocument().getLength(),
                              vparam,
                              vattr);
                  }

        } catch (BadLocationException ex){
            ex.printStackTrace();
        }

      */


            //ov added newly
            jfr = new JFrame();
            JDialog.setDefaultLookAndFeelDecorated(false);
            jfr.setUndecorated(true);
            setUndecorated(true);
            getContentPane().setLayout(new BorderLayout());
            getContentPane().add(bubblePanel, BorderLayout.CENTER);

            //Show it.
          //     dialog.setSize(new Dimension(400, 100));


/*
            Rectangle rec = comp.getParent().getBounds();

            System.out.println(" xmltest bounds x = " + rec.getX());
            System.out.println(" xmltest bounds y = " + rec.getY() +
rec.getHeight());

            double mxpos = 0.0;
            if(Math.abs(rec.getX() + rec.getWidth() -
(comp.getBounds().getX() + mouseXPos)) > 0) {
                  System.out.println(" in bounds xaxis if ");
                  mxpos = comp.getBounds().getX() + mouseXPos;
            } else {
                  System.out.println(" in bounds xaxis else ");
                  mxpos = comp.getBounds().getX() + mouseXPos - 350;
            }



            double mypos = 0.0;
            if(Math.abs(rec.getY() + rec.getHeight() -
(comp.getBounds().getY() + mouseYPos)) > 0) {
                  System.out.println(" in bounds yaxis if ");
                  mypos = comp.getBounds().getY() + mouseYPos;
            } else {
                  System.out.println(" in bounds yaxis else ");
                  mypos = comp.getBounds().getY() + mouseYPos - 100;
            }

            System.out.println(" setting bounds x = " + mxpos);
            System.out.println(" setting bounds y = " + mypos);

            int w = getWidth();
            int h = getHeight();

            System.out.println(" getWidth = " + w);
            System.out.println(" getHeight = " + h);
*/
            Point p = comp.getLocationOnScreen();
            int ht = comp.getSize().height;
            int wd = comp.getSize().width;

            int lowlefty = p.y + ht;

            int ptx = p.x;
            int pty = p.y;

            double sbarht = Math.max(78, ht*0.2);

      //    setLocationRelativeTo(comp);
            setBounds(new Rectangle(ptx, lowlefty - 60, wd-1, (int)sbarht));
      //    setLocation(new Point(mouseXPos, mouseYPos));
      //    setSize(new Dimension(350,100));


            setVisible(true);
            //ov added end

      }

    public static void main(String[] args) {

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JBubblePanel bubblePanel = new JBubblePanel();
            bubblePanel.setLayout(new BorderLayout());

            JLabel lab = new JLabel();

            ImageIcon img = new ImageIcon("divclose.GIF");
            JPanel labPanel = new JPanel();
            labPanel.add(Box.createRigidArea(new Dimension(350, 5)));
            lab.setIcon(img);
            labPanel.add(lab);
            labPanel.setOpaque(false);
            bubblePanel.add(labPanel, BorderLayout.NORTH);

        JTextPane textPane = new JTextPane();
            textPane.setEditable(false);
       // JScrollPane jscp = new JScrollPane(textPane);
        bubblePanel.add(textPane, BorderLayout.CENTER);

        SimpleAttributeSet normal = new SimpleAttributeSet();
        SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setBold(bold, true);


        try {

            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                "Your connection to ",
                normal);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                "cvs.dev.java.net ",
                bold);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                "failed. Here are a few possible reasons.\n\n",
                normal);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                " Your computer is may not be " +
                "connected to the network.\n" +
                "* The CVS server name may be " +
                "entered incorrectly.\n\n",
                normal);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                "If you still can not connect, " +
                "please contact support at ",
                normal);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                "support@cvsclient.org",
                bold);
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                ".",
                normal);
        } catch (BadLocationException ex){
            ex.printStackTrace();
        }


        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(bubblePanel, BorderLayout.CENTER);
            frame.setUndecorated(true);
        frame.setBounds(200,300, 400,160);
        frame.setVisible(true);
    }

      // Exception in thread "main" org.xml.sax.SAXException: Fatal Error:URI=file:/E:/j
	  // axp-1_3/samples/SAXLocalNameCount/fcrtxn.xml Line=2: The elementtype "errormsg"
      // must be terminated by the matching end-tag "</errormsg>".

      private HashMap prepareMsgForDisplay(String statusTxt) throws Exception
      {

            HashMap errHash = new HashMap();

//          int firstCol = statusTxt.indexOf(":");

//          System.out.println(" firstCol = " + firstCol);

//          statusTxt = statusTxt.substring(firstCol+1);

            System.out.println(" new stext 1 = " + statusTxt);

            int secCol = statusTxt.indexOf(":");

            System.out.println(" secCol = " + secCol);

            String errType = statusTxt.substring(0, secCol);

            System.out.println(" errType = " + errType);

            SimpleAttributeSet errAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(errAttr, "serif");
            StyleConstants.setFontSize(errAttr, 12);
            StyleConstants.setBold(errAttr, true);
            StyleConstants.setItalic(errAttr, false);
            StyleConstants.setForeground(errAttr, Color.RED);


            errHash.put("ERRTYPE", errType);

            errHash.put("ERRTYPE_ATTR", errAttr);

            String errTxt = statusTxt.substring(secCol+1);

            System.out.println(" errTxt = " + errTxt);

            int uripos = errTxt.indexOf("URI");
            int linepos = errTxt.indexOf("Line");

            String urival = errTxt.substring(uripos+4, linepos);


            System.out.println(" uri = " + urival);

            SimpleAttributeSet uriAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(uriAttr, "serif");
            StyleConstants.setFontSize(uriAttr, 12);
            StyleConstants.setBold(uriAttr, false);
            StyleConstants.setItalic(uriAttr, false);
            StyleConstants.setForeground(uriAttr, Color.BLUE);


            if(urival.startsWith("file:/")) {
                  urival = urival.substring(6);
            }

            errHash.put("URI", urival);
            errHash.put("URI_ATTR", uriAttr);

            errTxt = errTxt.substring(linepos);

            System.out.println(" errTxt 5 = " + errTxt);

            int thirdCol = errTxt.indexOf(":");

            System.out.println(" thirdCol = " + thirdCol);

            String linenum = errTxt.substring(5, thirdCol);

            errHash.put("LINENO", linenum);

            System.out.println(" token 2 = " + linenum);

            SimpleAttributeSet lineAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(lineAttr, "serif");
            StyleConstants.setFontSize(lineAttr, 12);
            StyleConstants.setBold(lineAttr, false);
            StyleConstants.setItalic(lineAttr, false);
            StyleConstants.setForeground(lineAttr, Color.MAGENTA);


            errHash.put("LINENO_ATTR", lineAttr);

            String msgTxt = errTxt.substring(thirdCol+1);

            System.out.println(" msgTxt = " + msgTxt);

            StringTokenizer strtok = new StringTokenizer(msgTxt, "\"");

            SimpleAttributeSet msgAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(msgAttr, "serif");
            StyleConstants.setFontSize(msgAttr, 12);
            StyleConstants.setBold(msgAttr, false);
            StyleConstants.setItalic(msgAttr, false);
            StyleConstants.setForeground(msgAttr, Color.ORANGE);


      //    errHash.put("ERRMSG_ATTR", msgAttr);

            SimpleAttributeSet tagAttr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(tagAttr, "serif");
            StyleConstants.setFontSize(tagAttr, 12);
            StyleConstants.setBold(tagAttr, false);
            StyleConstants.setItalic(tagAttr, false);
            StyleConstants.setForeground(tagAttr, Color.PINK);


      //    errHash.put("ERRTAG_ATTR", tagAttr);


            int varparam = 0;
            StringBuffer sbuf = new StringBuffer();
            String tok = strtok.nextToken();
            sbuf.append(tok);

            errHash.put("VARPARAM" + lpadZero(varparam), tok);
            errHash.put("VARATTR" + lpadZero(varparam), msgAttr);
            while(strtok.hasMoreTokens()) {
                  varparam++;
                  sbuf.append(" ");
                  tok = strtok.nextToken();

                  System.out.println(" curr token  = " + tok);

                  errHash.put("VARPARAM" + lpadZero(varparam), tok);
                  if(varparam%2 != 0)
                        errHash.put("VARATTR" + lpadZero(varparam), tagAttr);
                  else
                        errHash.put("VARATTR" + lpadZero(varparam), msgAttr);
            }

            errHash.put("NUMPARAMS", new Integer(varparam+1));

            System.out.println(" errHash = " + errHash);


            return errHash;

      }

private String lpadZero(int n) {

      if(n > 0 && n < 10)
            return "000" + n;
      else if(n>=10 && n < 100) {
            return "00" + n;
      }
      else if(n>=100 && n < 1000) {
            return "0" + n;
      } else
            return "" + n;
}

public void setMessage(String msg) {
      HashMap msgHash = null;

            try
            {
                msgHash = prepareMsgForDisplay(msg);
            } catch (Exception e)
            {
                  msgHash = null;
                  insertText(msgHash, msg);
            }

}

private void insertText(HashMap errHash, String statusText) {

        try {
                  if(errHash == null) {
                        SimpleAttributeSet normal = new SimpleAttributeSet();
                        SimpleAttributeSet bold = new SimpleAttributeSet();
                        StyleConstants.setBold(bold, true);

                  textPane.getDocument().insertString(
                        textPane.getDocument().getLength(),
                          statusText,
                            normal);
                        return;
                  }

            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("ERRTYPE") + " in ",
                (SimpleAttributeSet) errHash.get("ERRTYPE_ATTR"));

            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("URI") + " at Line ",
                (SimpleAttributeSet) errHash.get("URI_ATTR"));
            textPane.getDocument().insertString(
                textPane.getDocument().getLength(),
                (String) errHash.get("LINENO") + " : ",
                (SimpleAttributeSet) errHash.get("LINENO_ATTR"));

                int numparams = ((Integer)errHash.get("NUMPARAMS")).intValue();


                  for(int g=0;g<numparams;g++) {

                      String vparam = (String) errHash.get("VARPARAM" + lpadZero(g));
                        SimpleAttributeSet vattr = (SimpleAttributeSet) errHash.get("VARATTR" + lpadZero(g));

                        textPane.getDocument().insertString(
                              textPane.getDocument().getLength(),
                              vparam,
                              vattr);
                  }

        } catch (BadLocationException ex){
            ex.printStackTrace();
        }


      }

}
