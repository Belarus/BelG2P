package org.alex73.korpus.voice;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.alex73.corpus.paradigm.Paradigm;
import org.alex73.korpus.base.GrammarDB2;
import org.apache.commons.io.FileUtils;

public class Prystauki {

    public static void main(String[] args) throws Exception {
        GrammarDB2 db = GrammarDB2.initializeFromDir("GrammarDB");

        List<String> o = new ArrayList<>();
        for (Paradigm p : db.getAllParadigms()) {
            if (p.getLemma().startsWith("ад")) {
                String m = p.getMeaning() != null ? " / " + p.getMeaning() : "";
                o.add(p.getLemma() + " / " + p.getPdgId() + m);
            }
        }
        Collections.sort(o, Collator.getInstance(new Locale("be")));
        FileUtils.writeLines(new File("/tmp/prystauki-ad.txt"), o);
    }
}
