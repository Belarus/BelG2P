package org.alex73.fanetyka.processes;

import java.util.Map;
import java.util.TreeMap;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class AhlusennieAzvancennie {

    static final String PARY = "б/п, д/т, дз/ц, з/с, ж/ш, дж/ч, г/х, ґ/к";
    static final Map<BAZAVY_HUK, BAZAVY_HUK> ahlusennie = new TreeMap<>();
    static final Map<BAZAVY_HUK, BAZAVY_HUK> azvancennie = new TreeMap<>();
    static {
        for (String p : PARY.split(",")) {
            p = p.trim();
            String[] ps = p.split("/");
            if (ps.length != 2) {
                throw new IllegalArgumentException(PARY);
            }
            BAZAVY_HUK h1 = BAZAVY_HUK.valueOf(ps[0]);
            BAZAVY_HUK h2 = BAZAVY_HUK.valueOf(ps[1]);
            ahlusennie.put(h1, h2);
            azvancennie.put(h2, h1);
        }
    }

    @ProcessCase("Аглушэнне")
    public String ahlusennie(Huk huk, Huk nastupny) {
        BAZAVY_HUK replaceTo = ahlusennie.get(huk.bazavyHuk);
        String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
        huk.bazavyHuk = replaceTo;
        return log;
    }

    @ProcessCase("Азванчэнне ц->д")
    public String azvancennieC(Huk huk, Huk nastupny) {
        huk.bazavyHuk = BAZAVY_HUK.д;
        return "ц->д";
    }

    @ProcessCase("Азванчэнне")
    public String azvancennie(Huk huk, Huk nastupny) {
        BAZAVY_HUK replaceTo = azvancennie.get(huk.bazavyHuk);
        String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
        huk.bazavyHuk = replaceTo;
        return log;
    }
}
