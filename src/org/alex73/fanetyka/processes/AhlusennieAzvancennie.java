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

    @ProcessCase(name = "Аглушэнне", logCountBefore = 1, logCountAfter = 1)
    public boolean ahlusennie(Huk huk, Huk nastupny) {
        BAZAVY_HUK replaceTo = ahlusennie.get(huk.bazavyHuk);
        huk.bazavyHuk = replaceTo;
        return true;
    }

    @ProcessCase(name = "Азванчэнне ц->д", logCountBefore = 1, logCountAfter = 1)
    public boolean azvancennieC(Huk huk, Huk nastupny) {
        huk.bazavyHuk = BAZAVY_HUK.д;
        return true;
    }

    @ProcessCase(name = "Азванчэнне", logCountBefore = 1, logCountAfter = 1)
    public boolean azvancennie(Huk huk, Huk nastupny) {
        BAZAVY_HUK replaceTo = azvancennie.get(huk.bazavyHuk);
        huk.bazavyHuk = replaceTo;
        return true;
    }
}
