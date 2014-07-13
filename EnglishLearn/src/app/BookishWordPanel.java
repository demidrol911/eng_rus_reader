package app;


import app.translate.BookAnalyzer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


public class BookishWordPanel extends JPanel {

    public BookishWordPanel() {
        super(new GridBagLayout());
       
        analyzer = new BookAnalyzer();
        
        learnComboBox.addItem("All");
        learnComboBox.addItem("Is learn");
        learnComboBox.addItem("Is not learn");
        learnComboBox.addItem("Is not found");
        learnComboBox.setRenderer(new ListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent component;
                switch(index) {
                    case 0: component = new JLabel(value.toString(), 
                            new ImageIcon(this.getClass().getResource("resources/img/joy_sorrow.png")), 
                            SwingConstants.LEFT);
                            break;
                    case 1: component = new JLabel(value.toString(), 
                            new ImageIcon(this.getClass().getResource("resources/img/joy.png")), 
                            SwingConstants.LEFT);
                            break;
                    case 2: component = new JLabel(value.toString(), 
                            new ImageIcon(this.getClass().getResource("resources/img/sorrow.png")), 
                            SwingConstants.LEFT);
                            break;
                    case 3: component = new JLabel(value.toString(), 
                            new ImageIcon(this.getClass().getResource("resources/img/sorrow.png")), 
                            SwingConstants.LEFT);
                            component.setBackground(new Color(220, 218, 47));
                            break;
                    default: component = new JLabel(value.toString());
                }
                component.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(list.getSelectionBackground()),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                component.setOpaque(true);
                if(isSelected)
                    component.setBackground(list.getSelectionBackground());
                return component;
            }
        });
                
        bookishWordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookishWordTable.setRowHeight(30);
        bookishWordTable.setIntercellSpacing(new Dimension(0, 1));
        bookishWordTable.setShowVerticalLines(false);
        bookishWordTable.setModel(new BookishWordTableModel(analyzer));
        bookishWordTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        TableColumnModel columnModel = bookishWordTable.getColumnModel();
        columnModel.getColumn(0).setMaxWidth(30);
        JCheckBox learnEditor = new JCheckBox();
        learnEditor.setIcon(new ImageIcon(this.getClass().getResource("resources/img/sorrow.png")));
        learnEditor.setSelectedIcon(new ImageIcon(this.getClass().getResource("resources/img/joy.png")));
        learnEditor.setOpaque(true);
        learnEditor.setBackground(bookishWordTable.getSelectionBackground());
        learnEditor.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultCellEditor learnCellEditor = new DefaultCellEditor(learnEditor);
        bookishWordTable.setDefaultEditor(Boolean.class, learnCellEditor);
        bookishWordTable.setDefaultRenderer(Boolean.class, new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                boolean is_not_found = analyzer.getWordEntry(row).getDictEntries().isEmpty();
                JCheckBox component = new JCheckBox(); 
                component.setSelected((boolean)value);
                component.setIcon(new ImageIcon(this.getClass().getResource("resources/img/sorrow.png")));
                component.setSelectedIcon(new ImageIcon(this.getClass().getResource("resources/img/joy.png")));
                component.setHorizontalAlignment(SwingConstants.CENTER);
                component.setBorder(BorderFactory.createEmptyBorder());
                component.setOpaque(true);
                if(is_not_found)
                    component.setBackground(new Color(220, 218, 47));
                if(isSelected)
                    component.setBackground(table.getSelectionBackground());
                return component;
            }
        });
        
        bookishWordTable.setDefaultRenderer(String.class, new TableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                boolean is_not_found = analyzer.getWordEntry(row).getDictEntries().isEmpty();
                JLabel component = new JLabel(value.toString());
                component.setHorizontalAlignment(SwingConstants.RIGHT);
                component.setFont(table.getFont());
                component.setBorder(BorderFactory.createEmptyBorder());
                component.setOpaque(true);
                if(is_not_found)
                    component.setBackground(new Color(220, 218, 47));
                if(isSelected)
                    component.setBackground(table.getSelectionBackground());
                return component;
            }
        });
        
        scrollWordTable.setViewportView(bookishWordTable);
        bookishWordTable.setTableHeader(new JTableHeader());
        
        scrollTextPane.setViewportView(translationTextPane);
        
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        
        add(learnComboBox, new GBC(0, 0, 2, 1).
                setWeight(100, 0).setFill(GBC.HORIZONTAL));
        add(scrollWordTable, new GBC(0, 1, 2, 1).
                setWeight(100, 100).setFill(GBC.BOTH));
        add(scrollTextPane, new GBC(0, 2, 2, 1).
                setWeight(100, 100).setFill(GBC.BOTH));
        add(statusLabel, new GBC(0, 3));
        add(progressBar, new GBC(1, 3).setWeight(100, 0).
                setFill(GBC.HORIZONTAL).setInsets(0, 5, 0, 0));     
        
        bookishWordTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                setSelectedWord(bookishWordTable.getSelectedRow(), false);
            }
        });
    }

    public void setText(String text) {
        final Thread analysisThread = analyzer.runAnalysis(text, progressBar);
        statusLabel.setText("Run analisys...");
        progressBar.setVisible(true);
        Thread managerThread = new Thread(new Runnable() {
            
            @Override
            public void run() {
                while(analysisThread.getState() != Thread.State.TERMINATED){}
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        BookishWordPanel.this.analyzer = analyzer;
                        progressBar.setVisible(false);
                        statusLabel.setText("Number words: "+BookishWordPanel.this.analyzer.countWords());
                        bookishWordTable.updateUI();
                        setSelectedWord(0, false);
                    }
                });
            }
        });
        managerThread.start();
        
    }
    
    private void setSelectedRow(int index, boolean select) {
        if(select && index != -1) {
            bookishWordTable.removeRowSelectionInterval(0, bookishWordTable.getRowCount()-1);
            bookishWordTable.addRowSelectionInterval(index, index);
            scrollWordTable.getViewport().
                setViewPosition(new Point(0, bookishWordTable.getRowHeight()*index));
        }
    }
    
    
    public void setSelectedWord(int index, boolean select) {
        if(analyzer != null) {
            translationTextPane.setTranslate(analyzer.getWordEntry(index));
            setSelectedRow(index, select);
        }
    }
    
    public void setSelectedWord(String word, boolean select) {
        if(analyzer != null) {
            int index = analyzer.getIndex(word);
            translationTextPane.setTranslate(analyzer.getWordEntry(word));
            setSelectedRow(index, select);
        }
    }

    private JComboBox learnComboBox = new JComboBox();
    private JTable bookishWordTable = new JTable();
    private DictionaryTextPane translationTextPane = new DictionaryTextPane();
    private JLabel statusLabel = new JLabel();
    private JProgressBar progressBar = new JProgressBar();
    private JScrollPane scrollWordTable = new JScrollPane();
    private JScrollPane scrollTextPane = new JScrollPane();
    private BookAnalyzer analyzer = null;
}
