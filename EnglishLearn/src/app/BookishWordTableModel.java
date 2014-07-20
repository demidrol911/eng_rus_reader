package app;


import app.translate.BookAnalyzer;
import app.translate.WordEntry;
import javax.swing.table.AbstractTableModel;

public class BookishWordTableModel extends AbstractTableModel {

    public BookishWordTableModel(BookAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public int getRowCount() {
        return analyzer.countWords();
    }

    
    @Override
    public int getColumnCount() {
        return 3;
    }

    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WordEntry wordEntry = analyzer.getWordEntry(rowIndex);
        switch (columnIndex) {
            case 0: if(wordEntry.getDictEntries().isEmpty()) return false;
                    else return wordEntry.isLearn();
            case 1: return wordEntry.getWord();
            case 2: return wordEntry.getFrequency();
            default: return null;
        }
    }

    
    @Override
    public String getColumnName(int columnIndex) {
        return columnName[columnIndex];
    }

    
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if(columnIndex == 0) return Boolean.class;
        else return String.class;
    }

    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0){
           WordEntry wordEntry = analyzer.getWordEntry(rowIndex);
           wordEntry.setLearn((boolean)aValue);
           LanguageFrame.translator.setWordIsLearn(wordEntry);
        }
    }
    

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 0;
    }

    
    private BookAnalyzer analyzer = null;
    private final String[] columnName = {"", "Word", "Frequency"};
}
