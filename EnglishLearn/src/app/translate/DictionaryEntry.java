package app.translate;


public class DictionaryEntry {
    
    public DictionaryEntry(String translation, boolean learn) {
        this.translation = translation;
        this.learn = learn;
    }

    public DictionaryEntry() {
        this("", false);
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
    
    public void setLearn(boolean learn) {
        this.learn = learn;
    }

    public boolean isLearn() {
        return learn;
    }
    
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictName() {
        return dictName;
    }
    
    
    private String translation = null;
    private boolean learn = false;
    private String dictName = null;
}
