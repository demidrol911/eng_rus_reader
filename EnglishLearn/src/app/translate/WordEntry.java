package app.translate;

import java.util.ArrayList;


public class WordEntry {

    public WordEntry(String word) {
        this.word = word;
        this.frequency = 1;          
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
    private ArrayList<DictionaryEntry> dictEntries = new ArrayList<>();
    private int frequency = 0;
    
}
