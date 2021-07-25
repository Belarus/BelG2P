package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;

import org.alex73.korpus.base.GrammarMorphFinder;

public class FanetykaText {
    public String ipa = "";
    public String skola = "";
    public List<String> why = new ArrayList<>();

    public FanetykaText(GrammarMorphFinder finder, String text) {
        String word = "";
        Fanetyka3 f = new Fanetyka3(finder);
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
                    f = new Fanetyka3(finder);
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
        return "ёйцукенгшўзх'фывапролджэячсмітьбюґ|-´".indexOf(c) >= 0;
    }
}
