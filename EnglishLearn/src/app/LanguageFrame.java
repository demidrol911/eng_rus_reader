package app;

import app.translate.Translator;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LanguageFrame extends JFrame implements ActionListener {

    public LanguageFrame() {
        super();
        
        JSplitPane splitPane = new JSplitPane();


        splitPane.setRightComponent(tabbedPane);
        splitPane.setLeftComponent(bookTextPane);
        splitPane.setDividerSize(2);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.9);
        splitPane.setDividerLocation(LanguageFrame.DEFAULT_SIZE.width-250);

        this.setTitle("English Learn");
        this.setIconImage(new ImageIcon(this.getClass().getResource("resources/img/icon.png")).getImage());
        this.setSize(DEFAULT_SIZE);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.setJMenuBar(createMenu());
        tabbedPane.add("Dictionary", dictPane);
        tabbedPane.add("Bookish words", bookishWordPanel);
        this.getContentPane().add(splitPane, BorderLayout.CENTER);
        
        bookTextPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                String word = bookTextPane.getSelectedWord();
                if(e.getClickCount() == 2 && !word.isEmpty()) {
                    tabbedPane.setSelectedIndex(1);
                    bookishWordPanel.setSelectedWord(word.toLowerCase(), true);
                }
            }
        });
        
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                LanguageFrame.translator.dismiss();
            }
        });

    }

    private JMenuBar createMenu() {
        JMenuBar menu_bar = new JMenuBar();
        for(int idx=0; idx<menuArray.length; idx++) {
            JMenu menu = new JMenu(menuArray[idx]);
            for (String[] menuItemString : menuItemArray[idx]) {
                if(menuItemString == null)
                    menu.addSeparator();
                else {
                    JMenuItem menuItem = new JMenuItem(menuItemString[0]);
                    menuItem.setActionCommand(menuItemString[1]);
                    menuItem.addActionListener(this);
                    if(menuItemString[2] != null)
                        menuItem.setIcon(new ImageIcon(this.getClass().getResource("resources/img/"+menuItemString[2])));
                    menu.add(menuItem);
                }
            }
            menu_bar.add(menu);
        }
        return menu_bar;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "open":
                int result = fileChooser.showOpenDialog(this);
                if(result == FileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    fileChooser.setCurrentDirectory(file.getParentFile());
                    TXTBook book = new TXTBook();
                    String text = book.getText(file);
                    bookTextPane.setBook(text);
                    this.setTitle("English Learn :: "+file.getPath());
                }   break;
            case "analyze":
                tabbedPane.setSelectedIndex(1);
                tabbedPane.updateUI();
                bookishWordPanel.setText(bookTextPane.getBook());
                break;
            case "open_clipboard":
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    bookTextPane.setBook((String)clipboard.getData(DataFlavor.stringFlavor));
                } catch (UnsupportedFlavorException | IOException ex) {
                    Logger.getLogger(LanguageFrame.class.getName()).log(Level.SEVERE, null, ex);
                }   break;
            case "close":
                dispose();
                System.exit(0);
        }
    }


    public static Translator translator = new Translator();

    private DictionaryPanel dictPane = new DictionaryPanel();
    private BookishWordPanel bookishWordPanel = new BookishWordPanel();
    private BookTextPane bookTextPane = new BookTextPane();
    private FileChooser fileChooser = new FileChooser();

    private JTabbedPane tabbedPane = new JTabbedPane();
    private String[] menuArray = {"File", "Dict", "Help"};
    private String[][][] menuItemArray = {{{"Open book",  "open", "open_file.png"},
                                           {"Open URL", "open_url", "url.png"}, 
                                           {"Open clipboard", "open_clipboard", "clipboard.png"},
                                           {"Analyze book", "analyze", "analyze.png"},
                                            null,
                                           {"Close", "close", "close_program.png"}},
                                          {null},
                                          {{"About", "about", "open_help.png"}}};

    public static Dimension DEFAULT_SIZE = new Dimension(720, 560);
}
