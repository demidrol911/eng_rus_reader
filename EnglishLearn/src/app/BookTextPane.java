package app;

import java.awt.Color;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;


public class BookTextPane extends JPanel {

    public BookTextPane() {
        super(new GridBagLayout());
        
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
        
        JButton pageDownButton = new JButton("<");
        JButton pageUpButton = new JButton(">");
        JTextField pageTextField= new JTextField("1/20");
        
        add(new JScrollPane(bookTextPane), new GBC(0, 0, 3, 1).setWeight(100, 100).setFill(GBC.BOTH));
        add(pageDownButton, new GBC(0, 1).setWeight(100, 0).setAnchor(GBC.EAST));
        add(pageTextField, new GBC(1, 1).setAnchor(GBC.CENTER));
        add(pageUpButton, new GBC(2, 1).setWeight(100, 0).setAnchor(GBC.WEST));
    }
    
    public void setBook(String text) {
        bookTextPane.setText(text);
        bookTextPane.setCaretPosition(0);
    }
    
    public String getBook() {
        return bookTextPane.getText();
    }
    
    public String getSelectedWord() {
        return bookTextPane.getSelectedText();
    }
    
    private JTextPane bookTextPane = new JTextPane();
}