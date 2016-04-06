/*
 * IncrementalSearcher.java
 *
 * Created on 05 February 2007, 01:07
 * @author jasonkb
 *
 * Source is LGPL by: Jason Kaan Barraclough
 *
 * purpose:
 *Perform incremental search within a JTextPane document and highlight subsequent
* occurences of the text
* thanks to camickr for issues I had with the highlight animation on the find field
* Acknowledgements also to "Swing Hacks: Tips and Tools for Building Killer GUIs"
 *
 */
 
package com.exalto.UI.util;
 
import javax.swing.*;
import javax.swing.Timer.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
public class IncrementalSearcher extends JPanel implements ActionListener, DocumentListener, KeyListener
{
 //   private JButton search = new JButton(new ImageIcon("magnifyingGlass.png));
    public JTextField query = new JTextField(25);
  //  private JLabel report = new JLabel("");
    private boolean runAnimation = false;
    
    private int lengthComparison = 0;
    
    protected JTextComponent target;
    protected Matcher matcher;
    private Timer timer = new Timer(2000, this);
  
/*	
    public IncrementalSearcher(JTextComponent c) 
    {      
        target = c;
        JPanel contents = new JPanel();
        contents.setLayout(new BorderLayout());
        
        this.setLayout(new BorderLayout());
        search.addActionListener(this);
        query.addActionListener(this);
        query.getDocument().addDocumentListener(this);
        search.setToolTipText("Find text in the current document (CTRL+F)");
        
        contents.setBorder(new EmptyBorder(5,0,0,0));
        
        contents.add(search, BorderLayout.EAST);
        contents.add(query, BorderLayout.CENTER);
        this.add(contents, BorderLayout.WEST);
        
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.add(report, BorderLayout.WEST);
        reportPanel.setBorder(new EmptyBorder(0, 10,0,0));
        this.add(reportPanel, BorderLayout.CENTER);
        
        
    }

*/	
    public IncrementalSearcher(JTextComponent c, JTextField searchField) 
    {      
        target = c;
		query = searchField;
        
    }


    public void keyPressed(KeyEvent e)
    {
        
    }
    
    public void keyReleased(KeyEvent e)
    {
        
    }
    
    public void keyTyped(KeyEvent e)
    {
        if(e.getKeyChar() == '\n')
        {
            
        }
        else
        {
            runNewSearch();
        }
    }
    
    public void insertUpdate(DocumentEvent e)
    {
        runNewSearch();
    }
    
    public void removeUpdate(DocumentEvent e)
    {
        runNewSearch();
    }
    
    public void changedUpdate(DocumentEvent e)
    {
        runNewSearch();
    }
    
    private void runNewSearch()
    {
        try
        {
            String q = query.getText();
            System.out.println(q+" this was the query");
            Pattern pattern = Pattern.compile(q);
            
            String body = target.getText();
            lengthComparison = body.length();
            matcher = pattern.matcher(body);
            continueSearch();
            
        }
        catch(Exception e)
        {
            System.out.println("Problem with the query "+e);
        }
    }
    
    private void continueSearch()
    {
        if(matcher != null)
        {
            if(matcher.find())
            {
                if(runAnimation)
                {
                    if(target.getText().length() != lengthComparison)
                    {
                        runNewSearch();
                    }
                    query.setBackground(Color.WHITE);
                    timer.stop();
                    try
                    {
                        target.getCaret().setDot(matcher.start());
                        target.getCaret().moveDot(matcher.end());
                        target.getCaret().setSelectionVisible(true);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                    
                    System.out.println("found a match");
                    query.requestFocusInWindow();
                }
                runAnimation = true;
            }
            else
            {
                if(runAnimation)
                {
                    System.out.println("no found match");
                  
                    timer.setInitialDelay(3000);
                    query.setBackground(Color.RED);
                    timer.start();
                
                    System.out.println("no match found");
                    matcher.reset();
                }
                runAnimation = true;
            }
        }
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if(query.getText().equals(""))
        {
		    System.out.println("No query specified");
         
            query.requestFocusInWindow();
        }
        
        if(e.getSource() == timer)
        {
            System.out.println("Timer has fired!");
            query.setBackground(Color.WHITE);
            runAnimation = false;
            timer.stop();
        }
        
        continueSearch();
        
    }
    
}
 
 
 
 
 
 
 
 
 
 
 
