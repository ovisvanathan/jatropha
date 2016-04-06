package com.exalto.UI.font;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class FontDialog extends JDialog
{
  protected int m_option = JOptionPane.CLOSED_OPTION;
  protected OpenList m_lstFontName;
  protected OpenList m_lstFontSize;
  protected MutableAttributeSet m_attributes;
  protected JCheckBox m_chkBold;
  protected JCheckBox m_chkItalic;
  protected JCheckBox m_chkUnderline;
    
  protected JCheckBox m_chkStrikethrough;
  protected JCheckBox m_chkSubscript;
  protected JCheckBox m_chkSuperscript;
    
  protected JComboBox m_cbColor;
  protected JLabel m_preview;

  protected Font fn;
  
  public FontDialog(JFrame parent, 
    String[] names, String[] sizes)
  {
    super(parent, "Font", true);
    getContentPane().setLayout(new BoxLayout(getContentPane(), 
      BoxLayout.Y_AXIS));

    JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
    p.setBorder(new TitledBorder(new EtchedBorder(), "Font"));
    m_lstFontName = new OpenList(names, "Name:");
    p.add(m_lstFontName);

    m_lstFontSize = new OpenList(sizes, "Size:");
    p.add(m_lstFontSize);
    getContentPane().add(p);

    p = new JPanel(new GridLayout(2, 3, 10, 5));
    p.setBorder(new TitledBorder(new EtchedBorder(), "Effects"));
    m_chkBold = new JCheckBox("Bold");
    p.add(m_chkBold);
    m_chkItalic = new JCheckBox("Italic");
    p.add(m_chkItalic);
    m_chkUnderline = new JCheckBox("Underline");
    p.add(m_chkUnderline);
    m_chkStrikethrough = new JCheckBox("Strikeout");
    p.add(m_chkStrikethrough);
    m_chkSubscript = new JCheckBox("Subscript");
    p.add(m_chkSubscript);
    m_chkSuperscript = new JCheckBox("Superscript");
    p.add(m_chkSuperscript);
    getContentPane().add(p);

    getContentPane().add(Box.createVerticalStrut(5));
    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(Box.createHorizontalStrut(10));
    p.add(new JLabel("Color:"));
    p.add(Box.createHorizontalStrut(20));
    m_cbColor = new JComboBox();

    int[] values = new int[] { 0, 128, 192, 255 };
    for (int r=0; r<values.length; r++) {
      for (int g=0; g<values.length; g++) {
        for (int b=0; b<values.length; b++) {
          Color c = new Color(values[r], values[g], values[b]);
          m_cbColor.addItem(c);
        }
      }
    }

    m_cbColor.setRenderer(new ColorComboRenderer());
    p.add(m_cbColor);
    p.add(Box.createHorizontalStrut(10));
    getContentPane().add(p);

    p = new JPanel(new BorderLayout());
    p.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
    m_preview = new JLabel("Preview Font", JLabel.CENTER);
    m_preview.setBackground(Color.white);
    m_preview.setForeground(Color.black);
    m_preview.setOpaque(true);
    m_preview.setBorder(new LineBorder(Color.black));
    m_preview.setPreferredSize(new Dimension(120, 40));
    p.add(m_preview, BorderLayout.CENTER);
    getContentPane().add(p);

    p = new JPanel(new FlowLayout());
    JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
    JButton btOK = new JButton("OK");
    ActionListener lst = new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        m_option = JOptionPane.OK_OPTION;
        setVisible(false);
      }
    };
    btOK.addActionListener(lst);
    p1.add(btOK);

    JButton btCancel = new JButton("Cancel");
    lst = new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        m_option = JOptionPane.CANCEL_OPTION;
        setVisible(false);
      }
    };
    btCancel.addActionListener(lst);
    p1.add(btCancel);
    p.add(p1);
    getContentPane().add(p);

    pack();
    setResizable(false);

    ListSelectionListener lsel = new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updatePreview();
      }
    };
    m_lstFontName.addListSelectionListener(lsel);
    m_lstFontSize.addListSelectionListener(lsel);

    lst = new ActionListener() { 
      public void actionPerformed(ActionEvent e) {
        updatePreview();
      }
    };
    m_chkBold.addActionListener(lst);
    m_chkItalic.addActionListener(lst);
    m_cbColor.addActionListener(lst);
  }

  public void setAttributes(AttributeSet a) {
    m_attributes = new SimpleAttributeSet(a);
    String name = StyleConstants.getFontFamily(a);
    m_lstFontName.setSelected(name);
    int size = StyleConstants.getFontSize(a);
    m_lstFontSize.setSelectedInt(size);
    m_chkBold.setSelected(StyleConstants.isBold(a));
    m_chkItalic.setSelected(StyleConstants.isItalic(a));
    m_chkUnderline.setSelected(StyleConstants.isUnderline(a));
    m_chkStrikethrough.setSelected(
      StyleConstants.isStrikeThrough(a));
    m_chkSubscript.setSelected(StyleConstants.isSubscript(a));
    m_chkSuperscript.setSelected(StyleConstants.isSuperscript(a));
    m_cbColor.setSelectedItem(StyleConstants.getForeground(a));
    updatePreview();
  }

  public Font getFont() {
	  return fn;
  }
  
  public AttributeSet getAttributes() {
    if (m_attributes == null)
      return null;
    StyleConstants.setFontFamily(m_attributes, 
      m_lstFontName.getSelected());
    StyleConstants.setFontSize(m_attributes, 
      m_lstFontSize.getSelectedInt());
    StyleConstants.setBold(m_attributes, 
      m_chkBold.isSelected());
    StyleConstants.setItalic(m_attributes, 
      m_chkItalic.isSelected());
    StyleConstants.setUnderline(m_attributes, 
      m_chkUnderline.isSelected());
    StyleConstants.setStrikeThrough(m_attributes, 
      m_chkStrikethrough.isSelected());
    StyleConstants.setSubscript(m_attributes, 
      m_chkSubscript.isSelected());
    StyleConstants.setSuperscript(m_attributes, 
      m_chkSuperscript.isSelected());
    StyleConstants.setForeground(m_attributes, 
      (Color)m_cbColor.getSelectedItem());
    return m_attributes;
  }

  public int getOption() { return m_option; }

  protected void updatePreview() {
    String name = m_lstFontName.getSelected();
    int size = m_lstFontSize.getSelectedInt();
    if (size <= 0)
      return;
    int style = Font.PLAIN;
    if (m_chkBold.isSelected())
      style |= Font.BOLD;
    if (m_chkItalic.isSelected())
      style |= Font.ITALIC;

    // Bug Alert! This doesn't work if only style is changed.
    fn = new Font(name, style, size);
    m_preview.setFont(fn);

    Color c = (Color)m_cbColor.getSelectedItem();
    m_preview.setForeground(c);
    m_preview.repaint();
  }
}
