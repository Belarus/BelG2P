package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;

import org.alex73.fanetyka.impl.str.ToStringIPA;
import org.alex73.fanetyka.impl.str.ToStringSkolny;
import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;

public class FanetykaText {
    public String ipa = "";
    public String skola = "";
    public List<String> why = new ArrayList<>();

    public FanetykaText(GrammarFinder finder, String text) throws Exception {
        FanetykaConfig config = new FanetykaConfig(finder, null);
        String word = "";
        List<String> words = new ArrayList<>();
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
                    words.add(word);
                    word = "";
                } else if (c == ' ') {
                    ipa += c;
                    skola += c;
                }
                if (c != ' ') {
                    f.calcFanetyka(words);
                    words.clear();
                    ipa += f.toString(new ToStringIPA());
                    skola += f.toString(new ToStringSkolny());
                    why.addAll(f.logPhenomenon);
                    f = new Fanetyka3(config);
                    ipa += c;
                    skola += c;
                }
            }
        }
        if (!word.isEmpty()) {
            words.add(word);
        }
        f.calcFanetyka(words);
        ipa +=  f.toString(new ToStringIPA());
        skola += f.toString(new ToStringSkolny());
        why.addAll(f.logPhenomenon);
    }

    boolean isLetter(char c) {
        c = Character.toLowerCase(c);
        return ("ёйцукенгшўзх'фывапролджэячсмітьбюґ|-\u0301" + WordInitialConverter.usie_apostrafy + WordInitialConverter.usie_naciski).indexOf(c) >= 0;
    }

    public static void main(String[] a) throws Exception {
        GrammarDB2 db = GrammarDB2.empty();
        GrammarFinder finder = new GrammarFinder(db);

        String text ="лё";// new String(System.in.readAllBytes());
        FanetykaText t = new FanetykaText(finder, text);
        System.out.println(t.skola);
    }
}
