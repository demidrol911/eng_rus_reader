package app;


import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.apache.log4j.Logger;

public class Main {

    public static void main(String[] args) {
        
        final Logger LOG = Logger.getLogger(Main.class);
                
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(new MetalLookAndFeel());
                    JFrame.setDefaultLookAndFeelDecorated(true);
                    JDialog.setDefaultLookAndFeelDecorated(true);
                    new LanguageFrame().setVisible(true);
                } catch (UnsupportedLookAndFeelException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        });   
    }
}
