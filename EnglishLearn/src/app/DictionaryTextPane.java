package app;


import app.translate.WordEntry;
import app.translate.DictionaryEntry;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DictionaryTextPane extends JTextPane {

    public DictionaryTextPane() {
        super();
        
        this.setEditable(false);
  
    }


    public void setTranslate(WordEntry wordEntry) {
        String newline = System.getProperty("line.separator");
        Style base = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);
        DefaultStyledDocument transDoc = new DefaultStyledDocument();
        Style word_style = transDoc.addStyle("word_style", base);
        StyleConstants.setBold(word_style, true);
        StyleConstants.setItalic(word_style, true);
        StyleConstants.setForeground(word_style, Color.BLUE);
        Style transcription_style = transDoc.addStyle("transcription_style", base);
        StyleConstants.setItalic(transcription_style, true);
        Style translation_style = transDoc.addStyle("translation_style", base);
        StyleConstants.setItalic(translation_style, true);
        StyleConstants.setBold(translation_style, true);
        StyleConstants.setUnderline(translation_style, true);
        try{
            String word = wordEntry.getWord();
            ArrayList<DictionaryEntry> dictEntries = wordEntry.getDictEntries();
            for(DictionaryEntry dictEntry: dictEntries) {
                String dictName = dictEntry.getDictName();
                String translation = dictEntry.getTranslation();
                transDoc.insertString(transDoc.getLength(), "Found in the ", transcription_style);
                transDoc.insertString(transDoc.getLength(), dictName+newline, word_style);
                if(translation.startsWith("~")) {
                    transDoc.insertString(transDoc.getLength(), word, translation_style);
                }
                StringTokenizer tok = new StringTokenizer(translation, "~");
                for(; tok.hasMoreTokens();) {
                    transDoc.insertString(transDoc.getLength(), tok.nextElement().toString(), null);
                    if(tok.hasMoreTokens())transDoc.insertString(transDoc.getLength(), word, translation_style);
                }
                transDoc.insertString(transDoc.getLength(), newline+newline, null);
            }
            if(dictEntries.isEmpty()) {
                transDoc.insertString(transDoc.getLength(), newline+"Nothing found", null);
            }
        }
        catch (BadLocationException ex) {
            Logger.getLogger(DictionaryTextPane.class.getName()).log(Level.SEVERE, null, ex);
        }   
        this.setDocument(transDoc);
        this.setCaretPosition(0);
    }
}
