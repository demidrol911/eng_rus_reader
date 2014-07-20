package app;


import java.io.*;
import org.apache.log4j.Logger;

public class TXTBook implements Book {

    @Override
    public String getText(File file) {
        String text = "";
        String newline = System.getProperty("line.separator");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = null;
            while (null != (line = reader.readLine())) {
                text += line + newline;
            }
        }   catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return text;
    }
    
    Logger LOG = Logger.getLogger(TXTBook.class);
}
