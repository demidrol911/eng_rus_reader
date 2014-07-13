package app;

import app.translate.WordEntry;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DictionaryPanel extends JPanel {

    public DictionaryPanel() {
        super(new GridBagLayout());
        voce.SpeechInterface.init("../../../lib", true, false, "", "");


        searchButton = new JButton(new ImageIcon(this.getClass().getResource("resources/img/start_search.png")));
        JButton speakButton = new JButton(new ImageIcon(this.getClass().getResource("resources/img/start_speech.png")));


        searchButton.setPreferredSize(new Dimension(30, 30));
        searchButton.setMaximumSize(new Dimension(30, 30));
        searchButton.setMinimumSize(new Dimension(30, 30));

        speakButton.setPreferredSize(new Dimension(30, 30));
        speakButton.setMaximumSize(new Dimension(30, 30));
        speakButton.setMinimumSize(new Dimension(30, 30));

        wordTextField.setMaximumSize(new Dimension(30, 30));
        wordTextField.setPreferredSize(new Dimension(30, 30));
        wordTextField.setMinimumSize(new Dimension(30, 30));

        liveSearchCheckBox.setText("Live search");
        liveSearchCheckBox.setSelected(false);


        this.add(wordTextField, new GBC(0, 0, 2, 1).
                setInsets(5, 5, 2, 0).
                setWeight(100, 0).
                setFill(GBC.HORIZONTAL));
        this.add(searchButton, new GBC(2, 0).setInsets(5, 0, 2, 0));
        this.add(speakButton, new GBC(3, 0).setInsets(5, 0, 2, 0));
        this.add(new JScrollPane(translationTextPane), new GBC(0, 1, 4, 1).
                setInsets(2, 0, 0, 0).
                setWeight(100, 100).
                setFill(GBC.BOTH));
        this.add(liveSearchCheckBox, new GBC(0, 2));


        wordTextField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if(liveSearchCheckBox.isSelected() || e.getKeyCode() == KeyEvent.VK_ENTER)
                    startSearch();
            }
        });

        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                startSearch();
            }
        });

        speakButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                voce.SpeechInterface.synthesize(wordTextField.getText());
            }
        });
    }


    private void startSearch() {
        Thread searchThread = new Thread(new Runnable() {

            @Override
            public void run() {
                searchButton.setEnabled(false);
                wordTextField.setEditable(false);
                String word = wordTextField.getText();
                if(word.length()>0) {
                    WordEntry wordEntry = LanguageFrame.translator.translateWord(word);
                    translationTextPane.setTranslate(wordEntry);
                }
                searchButton.setEnabled(true);
                wordTextField.setEditable(true);
            }
        });
        searchThread.start();
    }


    private JTextField wordTextField = new JTextField();
    private DictionaryTextPane translationTextPane = new DictionaryTextPane();
    private JCheckBox liveSearchCheckBox = new JCheckBox();
    private JButton searchButton = null;
}
