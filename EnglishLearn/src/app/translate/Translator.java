package app.translate;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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
       
    }
    
    public void convertDict(File xml_dict) {
        try {
            String dict_title = xml_dict.getName();
            selectDict(dict_title);
            
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
                    String transcription = "";
                    if(articleElement.getElementsByTagName("tr").item(0) != null) {
                        transcription = articleElement.getElementsByTagName("tr").item(0).getTextContent();
                        translation = "["+transcription+"] "+translation;
                    }
                    translation = translation.replaceAll(word.replaceAll("[*]", ""), "~");
                    translation = translation.replaceAll(" +", " ");
                    translation = translation.replaceAll("^\n+", "");
                    DictionaryEntry dictEntry = new DictionaryEntry(translation, false);
                    dictEntry.setDictName(dict_title);
                    WordEntry wordEntry = new WordEntry(word);
                    wordEntry.addDictEntry(dictEntry);
                    learnWord(wordEntry);
                    
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            dict.close();
            dict = null;
        }
    }
    
    public void selectDict(String dict_title) {
        if(dict != null) {
            dict.close();
        }
        dict = dictEnv.openDatabase(null, dict_title, dbConfig);
    }
    
    
    public void learnWord(WordEntry wordEntry) {
        try {
            DatabaseEntry key = new DatabaseEntry(wordEntry.getWord().getBytes("UTF-8"));
            DatabaseEntry data = new DatabaseEntry();
            for(DictionaryEntry dictEntry: wordEntry.getDictEntries()) {
                selectDict(dictEntry.getDictName());
                dictTupleBinding.objectToEntry(dictEntry, data);
                dict.put(null, key, data);
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public WordEntry translateWord(String word) {
        WordEntry wordEntry = new WordEntry(word);
        wordEntry = searchDictEntry(wordEntry, word);
        if(wordEntry.getDictEntries().isEmpty() && word.endsWith("s")) {
            wordEntry = searchDictEntry(wordEntry, word.substring(0, word.length()-1));
        }
        return wordEntry;
    }
    
    public List<String> getDictList() {
        return dictEnv.getDatabaseNames();
    }
    
    private WordEntry searchDictEntry(WordEntry wordEntry, String word) {
        try {
            List<String> dictNames = getDictList();
            DatabaseEntry wordkey = new DatabaseEntry(word.getBytes("UTF-8"));
            DatabaseEntry translateData = new DatabaseEntry();
            for (String dictName : dictNames) {
                selectDict(dictName);
                if(dict.get(null, wordkey, translateData, LockMode.DEFAULT) 
                        == OperationStatus.SUCCESS) {
                    DictionaryEntry dictEntry = (DictionaryEntry)dictTupleBinding.entryToObject(translateData);
                    dictEntry.setDictName(dictName);
                    wordEntry.addDictEntry(dictEntry);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Translator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wordEntry;
    }
    
    
    public void dismiss() {
        if(dict != null) {
            dict.close();
        }
        if(dictEnv != null) {
            dictEnv.close();
        }
    }
    
    
    private final EnvironmentConfig envConfig = new EnvironmentConfig();
    private final DatabaseConfig dbConfig = new DatabaseConfig();
    private final DictionaryTupleBinding dictTupleBinding = new DictionaryTupleBinding();
    private Environment dictEnv = null;
    private Database dict = null;
    
    
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
