package app;


import java.io.*;

public class TXTBook implements Book {

    @Override
    public String getText(File file) {
        String text = "";
        try {
            String newline = System.getProperty("line.separator");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while (null != (line = reader.readLine()))
                text += line + newline;
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return text;
    }
}
