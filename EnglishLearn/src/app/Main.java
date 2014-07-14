package app;


import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                try {
                    NimRODLookAndFeel lookAndFeel = new NimRODLookAndFeel();
                    NimRODTheme theme = new NimRODTheme("snow.theme");
                    NimRODLookAndFeel.setCurrentTheme(theme);
                    UIManager.setLookAndFeel(lookAndFeel);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                JDialog.setDefaultLookAndFeelDecorated(true);
                new LanguageFrame().setVisible(true);
            }
        });   
    }
}
