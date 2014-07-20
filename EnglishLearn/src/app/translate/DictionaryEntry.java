package app.translate;


public class DictionaryEntry {
    
    public DictionaryEntry(String translation, String dictName) {
        this.translation = translation;
        this.dictName = dictName;
    }

    public DictionaryEntry() {
        this("", "");
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
    
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictName() {
        return dictName;
    }
    
    private String translation = null;
    private String dictName = null;
}
