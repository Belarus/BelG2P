package org.alex73.fanetyka.utils;

import java.text.Collator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;
import org.alex73.grammardb.SetUtils;
import org.alex73.grammardb.StressUtils;
import org.alex73.grammardb.structures.Form;
import org.alex73.grammardb.structures.Paradigm;
import org.alex73.grammardb.structures.Variant;
import org.alex73.grammardb.tags.BelarusianTags;

public class HistarycnyK {

    public static void main(String[] args) throws Exception {
        BelarusianTags tags = new BelarusianTags();
        GrammarDB2 db = GrammarDB2.initializeFromDir("/home/alex/gits/GrammarDB/data");
        GrammarFinder finder = new GrammarFinder(db);

        Map<String, Set<String>> lemmasByForm = new HashMap<>();
        db.getAllParadigms().stream().forEach(p -> {
            for (Variant v : p.getVariant()) {
                if (v.getLemma().endsWith("ка") || v.getLemma().endsWith("ка" + GrammarDB2.pravilny_nacisk)) {
                    for (Form f : v.getForm()) {
                        String s = StressUtils.unstress(f.getValue()).toLowerCase();
                        if (s.matches(".+[шжджч][цдзсз][ыэ]")) {
                            String tag = SetUtils.tag(p, v, f);
                            char sk = tags.getValueOfGroup(tag, "Склон");
                            if (sk != 'D' && sk != 'L') {
                                System.out.println(sk + "   " + f.getValue());
                            }
                            Set<String> lemmas = lemmasByForm.computeIfAbsent(s, t -> new TreeSet<>());
                            for (Paradigm pf : finder.getParadigms(s)) {
                                if (hasForm(s, pf)) {
                                    lemmas.add(pf.getLemma());
                                }
                            }
                        }
                    }
                }
            }
        });
        lemmasByForm.entrySet().stream().filter(en -> en.getValue().size() > 1).map(en -> {
            for (String p : en.getValue()) {
                if (!p.endsWith("ка") && !p.endsWith("ка" + GrammarDB2.pravilny_nacisk)) {
                    return en.toString();
                }
            }
            return null;
        }).filter(s -> s != null).sorted(Collator.getInstance(Locale.of("be"))).forEach(System.out::println);
    }

    static boolean hasForm(String unstressedLowercaseForm, Paradigm p) {
        for (Variant v : p.getVariant()) {
            for (Form f : v.getForm()) {
                String s = StressUtils.unstress(f.getValue()).toLowerCase();
                if (s.equals(unstressedLowercaseForm)) {
                    return true;
                }
            }
        }
        return false;
    }
}
