package org.alex73.fanetyka.impl.ui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.alex73.fanetyka.impl.Fanetyka3;
import org.alex73.fanetyka.impl.Huk;

public class FanetykaUI {
    static MainWindow w;

    public static void main(String[] a) {
        w = new MainWindow();
        w.setBounds(100, 100, 800, 600);
        w.split.setDividerLocation(370);
        w.txtSource.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });
        w.setVisible(true);
    }

    static void update() {
        String[] words = w.txtSource.getText().split("\\n+");

        StringBuilder out = new StringBuilder();
        for (String line : words) {
            Fanetyka3 tr = new Fanetyka3();
            try {
                for(String w:line.split("\\s+")) {
                    tr.addWord(w);
                }
                tr.calcFanetyka();
                out.append(tr.toString(Huk.ipa));
            } catch (Exception ex) {
                out.append("Памылка ў '" + w + "': " + ex.getMessage());
                ex.printStackTrace();
            }
            out.append("\n");
        }
        w.txtTarget.setText(out.toString());
    }
}
