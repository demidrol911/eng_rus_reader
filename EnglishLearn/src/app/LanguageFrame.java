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
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class LanguageFrame extends JFrame implements ActionListener {

    public LanguageFrame() {
        super();
        
        JSplitPane splitPane = new JSplitPane();


        splitPane.setRightComponent(tabbedPane);
        splitPane.setLeftComponent(new JScrollPane(bookTextPane));
        splitPane.setDividerSize(2);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.9);
        splitPane.setDividerLocation(LanguageFrame.DEFAULT_SIZE.width-250);

        bookTextPane.setEditable(false);
        bookTextPane.setBackground(new Color(0, 26, 66));
        Style base = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        Style book_style = bookTextPane.getStyledDocument().addStyle("book_style", base);
        StyleConstants.setLineSpacing(book_style, 0.5f);
        StyleConstants.setFontFamily(book_style, "verdana");
        StyleConstants.setFontSize(book_style, 12);
        StyleConstants.setForeground(book_style, new Color(220, 218, 47));
        bookTextPane.getStyledDocument().setLogicalStyle(0, book_style);

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
                String word = bookTextPane.getSelectedText();
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
                    bookTextPane.setText(text);
                    bookTextPane.setCaretPosition(0);
                    this.setTitle("English Learn :: "+file.getPath());
                }   break;
            case "analyze":
                tabbedPane.setSelectedIndex(1);
                tabbedPane.updateUI();
                bookishWordPanel.setText(bookTextPane.getText());
                break;
            case "open_clipboard":
                try {
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    bookTextPane.setText((String)clipboard.getData(DataFlavor.stringFlavor));
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
    private JTextPane bookTextPane = new JTextPane();
    private FileChooser fileChooser = new FileChooser();

    private JTabbedPane tabbedPane = new JTabbedPane();
    private String[] menuArray = {"File", "Help"};
    private String[][][] menuItemArray = {{{"Open book",  "open", "open_file.png"},
                                           {"Open URL", "open_url", "url.png"}, 
                                           {"Open clipboard", "open_clipboard", "clipboard.png"},
                                           {"Analyze book", "analyze", "analyze.png"},
                                            null,
                                           {"Close", "close", "close_program.png"}},
                                          {{"About", "about", "open_help.png"}}};

    public static Dimension DEFAULT_SIZE = new Dimension(720, 560);
}
