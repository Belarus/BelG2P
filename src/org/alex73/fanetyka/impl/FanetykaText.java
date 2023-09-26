package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;

public class FanetykaText {
    public String ipa = "";
    public String skola = "";
    public List<String> why = new ArrayList<>();

    public FanetykaText(GrammarFinder finder, String text) throws Exception {
        FanetykaConfig config = new FanetykaConfig(finder, null);
        String word = "";
        Fanetyka3 f = new Fanetyka3(config);
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean letter = isLetter(c);
            if (c == '-' && i > 0 && !isLetter(text.charAt(i - 1))) {
                letter = false;
            }
            if (letter) {
                word += c;
            } else {
                if (!word.isEmpty()) {
                    f.addWord(word);
                    word = "";
                } else if (c == ' ') {
                    ipa += c;
                    skola += c;
                }
                if (c != ' ') {
                    f.calcFanetyka();
                    ipa += f.toString(Huk.ipa);
                    skola += f.toString(Huk.skolny);
                    why.addAll(f.why);
                    f = new Fanetyka3(config);
                    ipa += c;
                    skola += c;
                }
            }
        }
        if (!word.isEmpty()) {
            f.addWord(word);
        }
        f.calcFanetyka();
        ipa += f.toString(Huk.ipa);
        skola += f.toString(Huk.skolny);
        why.addAll(f.why);
    }

    boolean isLetter(char c) {
        c = Character.toLowerCase(c);
        return ("ёйцукенгшўзх'фывапролджэячсмітьбюґ|-\u0301" + Fanetyka3.usie_apostrafy + Fanetyka3.usie_naciski).indexOf(c) >= 0;
    }

    public static void main(String[] a) throws Exception {
        GrammarDB2 db = GrammarDB2.empty();
        GrammarFinder finder = new GrammarFinder(db);

        String text ="абвод";// new String(System.in.readAllBytes());
        FanetykaText t = new FanetykaText(finder, text);
        System.out.println(t.skola);
    }
}
