package org.alex73.fanetyka.processes;

import java.util.Map;
import java.util.TreeMap;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

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

    @ProcessCase("Пераход шыпячых ц/дз у свісцячыя")
    public String sycdz(Huk huk) {
        BAZAVY_HUK replaceTo = sypiacyja2svisciacyja.get(huk.bazavyHuk);
        String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
        huk.bazavyHuk = replaceTo;
        return log;
    }

    @ProcessCase("Пераход шыпячых с/з у свісцячыя")
    public String sysz(Huk huk) {
        BAZAVY_HUK replaceTo = sypiacyja2svisciacyja.get(huk.bazavyHuk);
        String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
        huk.bazavyHuk = replaceTo;
        return log;
    }

    @ProcessCase("Пераход свісцячых у шыпячыя")
    public String sv(Huk huk) {
        BAZAVY_HUK replaceTo = svisciacyja2sypiacyja.get(huk.bazavyHuk);
        String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
        huk.bazavyHuk = replaceTo;
        huk.miakki = 0;
        return log;
    }
}
