package org.alex73.fanetyka.processes;

import java.util.Map;
import java.util.function.Supplier;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.config.ProcessConfig;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

@ProcessConfig("/ahlusennieIazvancennie.tsv")
public class AhlusennieAzvancennie {

    static final Map<BAZAVY_HUK, BAZAVY_HUK> ahlusennie = Map.of(BAZAVY_HUK.б, BAZAVY_HUK.п, BAZAVY_HUK.д, BAZAVY_HUK.т, BAZAVY_HUK.дз, BAZAVY_HUK.ц,
            BAZAVY_HUK.з, BAZAVY_HUK.с, BAZAVY_HUK.ж, BAZAVY_HUK.ш, BAZAVY_HUK.дж, BAZAVY_HUK.ч, BAZAVY_HUK.г, BAZAVY_HUK.х, BAZAVY_HUK.ґ, BAZAVY_HUK.к);

    @ProcessCase("Аглушэнне")
    public String ahlusennie(Huk huk, Huk nastupny, Supplier<Boolean> checkByTable) {
        BAZAVY_HUK replaceTo = ahlusennie.get(huk.bazavyHuk);
        if (replaceTo != null && (nastupny == null || nastupny.isHluchi())) {
            if (checkByTable.get()) {
                String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
                huk.bazavyHuk = replaceTo;
                return log;
            }
        }
        return null;
    }

    @ProcessCase("Азванчэнне ц->д")
    public String azvancennieC(Huk huk, Huk nastupny, Supplier<Boolean> checkByTable) {
        if (huk.bazavyHuk == BAZAVY_HUK.ц && nastupny.isZvonki() && checkByTable.get()) {
            if (checkByTable.get()) {
                huk.bazavyHuk = BAZAVY_HUK.д;
                return "ц->д";
            }
        }
        return null;
    }

    static final Map<BAZAVY_HUK, BAZAVY_HUK> azvancennie = Map.of(BAZAVY_HUK.п, BAZAVY_HUK.б, BAZAVY_HUK.т, BAZAVY_HUK.д, BAZAVY_HUK.ц, BAZAVY_HUK.дз,
            BAZAVY_HUK.с, BAZAVY_HUK.з, BAZAVY_HUK.ш, BAZAVY_HUK.ж, BAZAVY_HUK.ч, BAZAVY_HUK.дж, BAZAVY_HUK.х, BAZAVY_HUK.г, BAZAVY_HUK.к, BAZAVY_HUK.ґ);

    @ProcessCase("Азванчэнне")
    public String azvancennie(Huk huk, Huk nastupny, Supplier<Boolean> checkByTable) {
        BAZAVY_HUK replaceTo = azvancennie.get(huk.bazavyHuk);
        if (replaceTo != null && nastupny.isZvonki()) {
            if (checkByTable.get()) {
                String log = huk.bazavyHuk.name() + "->" + replaceTo.name();
                huk.bazavyHuk = replaceTo;
                return log;
            }
        }
        return null;
    }
}
