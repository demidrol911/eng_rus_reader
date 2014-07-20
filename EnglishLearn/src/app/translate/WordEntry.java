package app.translate;

import java.util.ArrayList;


public class WordEntry {

    public WordEntry(String word, boolean learn) {
        this.word = word;
        this.frequency = 1;     
        this.learn = learn;
    }
    
    public WordEntry(String word) {
        this(word, false);
    }

    public String getWord() {
        return word;
    }

    public ArrayList<DictionaryEntry> getDictEntries() {
        return dictEntries;
    }
    
    public void incFrequency() {
        frequency++;
    }

    public int getFrequency() {
        return frequency;
    }
    
    public boolean isLearn() {
        return learn;
    }
    
    public void setLearn(boolean learn) {
        this.learn = learn;
    }

    @Override
    public String toString() {
        String newline = System.getProperty("line.separator");
        String printStr = "word: "+word;
        for(DictionaryEntry dictEntry: dictEntries) {
            printStr += newline+"; translation: "+dictEntry.getTranslation()+newline;
        }
        return printStr;
    }
    
    public void addDictEntry(DictionaryEntry dictEntry) {
        dictEntries.add(dictEntry);
    }
    
    
    private String word = null;
    private final ArrayList<DictionaryEntry> dictEntries = new ArrayList<>();
    private boolean learn = false;
    private int frequency = 0;
    
}
