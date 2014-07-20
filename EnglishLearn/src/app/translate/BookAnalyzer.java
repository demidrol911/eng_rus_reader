package app.translate;


import app.LanguageFrame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.JProgressBar;

public class BookAnalyzer {
    
    
    public Thread runAnalysis(final String text, final JProgressBar progressBar) {
        wordHash.clear();
        wordEntries.clear();
        Thread analysis = new Thread(new Runnable() {
            
            @Override
            public void run() {
                StringTokenizer tok = new StringTokenizer(text.replaceAll("[^a-zA-z-]", " "), " ");
                int countWord = tok.countTokens()-1;
                progressBar.setMaximum(countWord);
                int progress = 0;
                while(tok.hasMoreElements()) {
                    String word = tok.nextElement().toString().toLowerCase();
                    if(!wordHash.containsKey(word)){
                        wordHash.put(word, wordEntries.size());
                        wordEntries.add(LanguageFrame.translator.translateWord(word));
                    }
                    else {
                        getWordEntry(word).incFrequency();
                    }
                    progressBar.setString((int)((float)progress/countWord*100)+"%");
                    progressBar.setValue(progress);
                    progress++;
                }
                
            }
        });
        analysis.start();
        return analysis;
    }

    public int countWords() {
        return wordEntries.size();
    }
    
    public WordEntry getWordEntry(String word) {
        int index = getIndex(word);
        if(index == -1)
            return new WordEntry(word);
        else 
            return wordEntries.get(index);
    }
    
    public WordEntry getWordEntry(int index) {
        if(index>=0 && index<wordEntries.size())
            return wordEntries.get(index);
        else
            return new WordEntry("");
    }
    
    public int getIndex(String word) {
        if(wordHash.containsKey(word))
            return wordHash.get(word);
        else
            return -1;
    }

    private final HashMap<String, Integer> wordHash = new HashMap<>();
    private final ArrayList<WordEntry> wordEntries = new ArrayList<>(); 
}
