package org.alex73.fanetyka.utils;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.StressUtils;

public class SpalucenniZycnuch {
    static final int LENGTH = 4;

    public static void main(String[] args) throws Exception {
        GrammarDB2 db = GrammarDB2.initializeFromDir("/home/alex/gits/GrammarDB/data");

        db.getAllParadigms().parallelStream().flatMap(p -> p.getVariant().stream()).flatMap(v -> v.getForm().stream()).map(f -> {
            String s = StressUtils.unstress(f.getValue()).toLowerCase();
            int found = 0;
            for (int i = 0; i < s.length() - LENGTH; i++) {
                if (zycny(s.charAt(i))) {
                    found++;
                } else {
                    found = 0;
                }
                if (found == LENGTH) {
                    return s.substring(i - LENGTH + 1, i+1);
                }
            }
            return null;
        }).filter(s -> s != null).sequential().distinct().sorted().forEach(System.out::println);
    }

    static boolean zycny(char c) {
        return "йцкнгшўзхфвпрлджчсмтб".indexOf(c) >= 0;
    }
}
