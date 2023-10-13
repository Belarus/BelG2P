package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class PierachodZG {
    @ProcessCase("Пераход г -> ґ пасля з, дж, дз напачатку слова")
    public String b(Huk huk, Huk nastupny) {
        nastupny.bazavyHuk = BAZAVY_HUK.ґ;
        return "г -> ґ";
    }

    @ProcessCase("Пераход г -> ґ пасля з, дж, дз пры канцы слова")
    public String e(Huk hal, Huk huk, Huk nastupny) {
        nastupny.bazavyHuk = BAZAVY_HUK.ґ;
        return "г -> ґ";
    }

    @ProcessCase("Пераход г -> ґ пасля з, дж, дз паміж галоснымі")
    public String m(Huk hal, Huk huk, Huk nastupny) {
        nastupny.bazavyHuk = BAZAVY_HUK.ґ;
        return "г -> ґ";
    }
}
