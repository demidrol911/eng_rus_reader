package app.translate;

import com.sleepycat.bind.tuple.BooleanBinding;
import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Translator {

    public Translator() {
        envConfig.setAllowCreate(true);
        dbConfig.setAllowCreate(true);
        File envDir = new File("dict");
        if(!envDir.exists())
            envDir.mkdir();
        dictEnv = new Environment(envDir, envConfig);
        
        String islearnName = "word_islearn";
        islearnDatabase = dictEnv.openDatabase(null, islearnName, dbConfig);
        for(String nameDataBase: getDictList()) {
            if(!nameDataBase.equals(islearnName)) {
                Database dict = dictEnv.openDatabase(null, nameDataBase, dbConfig);
                dictList.put(nameDataBase, dict);
            }
        }
    }
    
    public void convertDict(File xml_dict) {
        try {
            String dict_title = xml_dict.getName();
            
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document dictDoc = dBuilder.parse(xml_dict);
            
            dictDoc.getDocumentElement().normalize();
            
            NodeList articleList = dictDoc.getElementsByTagName("ar");
            
            for (int index = 0; index < articleList.getLength(); index++) {
                Node article = articleList.item(index);
                if(article.getNodeType() == Node.ELEMENT_NODE) {
                    Element articleElement = (Element) article;
                    String word = articleElement.getElementsByTagName("k").item(0).getTextContent();
                    String translation = articleElement.getLastChild().getTextContent();
                    String transcription;
                    if(articleElement.getElementsByTagName("tr").item(0) != null) {
                        transcription = articleElement.getElementsByTagName("tr").item(0).getTextContent();
                        translation = "["+transcription+"] "+translation;
                    }
                    translation = translation.replaceAll(word.replaceAll("[*]", ""), "~");
                    translation = translation.replaceAll(" +", " ");
                    translation = translation.replaceAll("^\n+", "");
                    DictionaryEntry dictEntry = new DictionaryEntry(translation, dict_title);
                    WordEntry wordEntry = new WordEntry(word);
                    wordEntry.addDictEntry(dictEntry);
                    learnWord(wordEntry);
                    
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
    
    
    public Database selectDict(String dict_title) {
        if(!dictList.containsKey(dict_title)) {
            Database dict = dictEnv.openDatabase(null, dict_title, dbConfig);
            dictList.put(dict_title, dict);
        }
        return dictList.get(dict_title);
    }
    
    
    public void learnWord(WordEntry wordEntry) {
        String translation = wordEntry.getDictEntries().get(0).getTranslation();
        String dictName = wordEntry.getDictEntries().get(0).getDictName();
        DatabaseEntry wordkey = new DatabaseEntry();
        DatabaseEntry data = new DatabaseEntry();
        stringBinding.objectToEntry(wordEntry.getWord(), wordkey); 
        stringBinding.objectToEntry(translation, data);
        Database dict = selectDict(dictName);
        dict.put(null, wordkey, data);
    }
    
    
    public boolean isWordLearn(String word) {
        DatabaseEntry wordkey = new DatabaseEntry();
        stringBinding.objectToEntry(word, wordkey);
        DatabaseEntry learnData = new DatabaseEntry();
        return islearnDatabase.get(null, wordkey, learnData, LockMode.DEFAULT) == OperationStatus.SUCCESS;
    }
    
    public void setWordIsLearn(WordEntry wordEntry) {
        boolean learn = isWordLearn(wordEntry.getWord());
        DatabaseEntry wordkey = new DatabaseEntry();
        stringBinding.objectToEntry(wordEntry.getWord(), wordkey); 
        if(!learn && wordEntry.isLearn()) {
            DatabaseEntry learnData = new DatabaseEntry();
            booleanBinding.objectToEntry(true, learnData);
            islearnDatabase.put(null, wordkey, learnData);
        }
        else if (learn && !wordEntry.isLearn()) {
            islearnDatabase.removeSequence(null, wordkey);
        } 
    }    
    
    public WordEntry translateWord(String word) {
        WordEntry wordEntry = new WordEntry(word);
        wordEntry.setLearn(isWordLearn(word));
        wordEntry = searchDictEntry(wordEntry, word);
        if(wordEntry.getDictEntries().isEmpty() && word.endsWith("s"))
            wordEntry = searchDictEntry(wordEntry, word.substring(0, word.length()-1));
        return wordEntry;
    }
    
    
    public List<String> getDictList() {
        return dictEnv.getDatabaseNames();
    }
    
    
    private WordEntry searchDictEntry(WordEntry wordEntry, String word) {
        DatabaseEntry wordkey = new DatabaseEntry();
        stringBinding.objectToEntry(word, wordkey); 
        DatabaseEntry translateData = new DatabaseEntry();
        for(Database dict: dictList.values()) {
            if(dict.get(null, wordkey, translateData, LockMode.DEFAULT) 
                    == OperationStatus.SUCCESS) {
                DictionaryEntry dictEntry = new DictionaryEntry(
                        stringBinding.entryToObject(translateData),
                        dict.getDatabaseName());
                wordEntry.addDictEntry(dictEntry);
            }
        }
        return wordEntry;
    }
    
    
    public void dismiss() {
        for(Database dict: dictList.values()){
            dict.close();
        }
        islearnDatabase.close();
        if(dictEnv != null) {
            dictEnv.close();
        }
    }
    
    Logger LOG =  Logger.getLogger(Translator.class);
    
    private final EnvironmentConfig envConfig = new EnvironmentConfig();
    private final DatabaseConfig dbConfig = new DatabaseConfig();
    private Environment dictEnv = null;
    private final HashMap<String, Database> dictList = new HashMap<>();
    private Database islearnDatabase = null;
    private final StringBinding stringBinding = new StringBinding();
    private final BooleanBinding booleanBinding = new BooleanBinding();
    
    
    public static void main(String[] args) {
        Translator translator = new Translator();
        File xdxfDir = new File("xdxf");
        File[] dictList = xdxfDir.listFiles();
        for (File dictList1 : dictList) {
            translator.convertDict(dictList1);
        }
        translator.dismiss();
    }
    
}
