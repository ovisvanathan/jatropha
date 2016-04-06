/*
 * MessageDialog.java
 *
 * Created on January 21, 2007, 5:01 PM
 */

package com.exalto.UI.util;

/**
 *
 * @author  sys
 */
public class MessageDialog extends java.awt.Dialog {
    
    javax.swing.ImageIcon infoIcon;
    javax.swing.ImageIcon errorIcon;
    String msg;
    
    /** Creates new form MessageDialog */
    public MessageDialog(java.awt.Frame parent, boolean modal, String msgType, String msg) {
        super(parent, modal);
        initComponents();
        
        this.msg = msg;
        
        infoIcon = new javax.swing.ImageIcon(getClass().getResource("/resources/info.gif"));        
        errorIcon = new javax.swing.ImageIcon(getClass().getResource("/resources/error.gif"));        
        
        if(msgType == null)
            msgType = "";
        if(msgType.equals("") || msgType.equals("INFO"))
            this.jLabel1.setIcon(infoIcon);
        else if(msgType.equals("") || msgType.equals("INFO"))
            this.jLabel1.setIcon(errorIcon);
        
        this.jLabel1.setText(msg);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel1.setIcon(new javax.swing.ImageIcon(""));

        jButton1.setPreferredSize(new java.awt.Dimension(65, 23));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(49, 49, 49)
                .add(jLabel1)
                .addContainerGap(435, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(210, Short.MAX_VALUE)
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jButton2)
                .add(70, 70, 70))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 38, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton2))
                .add(29, 29, 29))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MessageDialog(new java.awt.Frame(), true, null, "").setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    
}
