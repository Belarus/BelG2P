package org.alex73.fanetyka.processes;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.grammardb.StressUtils;
import org.alex73.grammardb.WordMorphology;

public class SypiacyjaSvisciacyja {
    static final String sypiacyja2svisciacyja_str = "ш/с, ж/з, дж/дз, ч/ц";
    static final String svisciacyja2sypiacyja_str = "т/ч, с/ш, з/ж, дз/дж, ц/ч";
    static final Map<BAZAVY_HUK, BAZAVY_HUK> sypiacyja2svisciacyja = new TreeMap<>();
    static final Map<BAZAVY_HUK, BAZAVY_HUK> svisciacyja2sypiacyja = new TreeMap<>();
    static {
        for (String p : sypiacyja2svisciacyja_str.split(",")) {
            p = p.trim();
            String[] ps = p.split("/");
            if (ps.length != 2) {
                throw new IllegalArgumentException(sypiacyja2svisciacyja_str);
            }
            BAZAVY_HUK h1 = BAZAVY_HUK.valueOf(ps[0]);
            BAZAVY_HUK h2 = BAZAVY_HUK.valueOf(ps[1]);
            sypiacyja2svisciacyja.put(h1, h2);
        }
        for (String p : svisciacyja2sypiacyja_str.split(",")) {
            p = p.trim();
            String[] ps = p.split("/");
            if (ps.length != 2) {
                throw new IllegalArgumentException(svisciacyja2sypiacyja_str);
            }
            BAZAVY_HUK h1 = BAZAVY_HUK.valueOf(ps[0]);
            BAZAVY_HUK h2 = BAZAVY_HUK.valueOf(ps[1]);
            svisciacyja2sypiacyja.put(h1, h2);
        }
    }

    @ProcessCase(name = "Пераход шыпячых перад свісцячымі ва ўскосных склонах", logCountBefore = 3, logCountAfter = 3)
    public boolean sycdz(Huk huk, ProcessContext context) {
        // Толькі калі пачатковая форма заканчваецца на "-ка" // FAN-58
        Set<String> ka = new TreeSet<>();
        Set<String> nonKa = new TreeSet<>();
        for (WordMorphology mf : huk.wordContext.amonimy) {
            String f0 = StressUtils.unstress(mf.v.getLemma()).toLowerCase();
            if (f0.endsWith("ка")) {
                ka.add(f0);
            } else {
                nonKa.add(f0);
            }
        }
        if (!ka.isEmpty()) {
            if (!nonKa.isEmpty()) {
                String word = StressUtils.unstress(huk.wordContext.word);
                String last3 = word.length() >= 3 ? word.substring(word.length() - 3) : word;
                String msg = String.format("Заўвага: вымаўленне “-%s” у слове “%s”, калі пачатковая форма “%s”, а не “%s”", last3, word,
                        String.join(",", nonKa), String.join(",", ka));
                context.debug.addFirst(msg);
            }
            BAZAVY_HUK replaceTo = sypiacyja2svisciacyja.get(huk.bazavyHuk);
            huk.bazavyHuk = replaceTo;
            return true;
        }
        return false;
    }

    @ProcessCase(name = "Пераход свісцячых у шыпячыя", logCountBefore = 2, logCountAfter = 2)
    public boolean sv(Huk huk) {
        BAZAVY_HUK replaceTo = svisciacyja2sypiacyja.get(huk.bazavyHuk);
        huk.bazavyHuk = replaceTo;
        huk.miakki = 0;
        return true;
    }
}
