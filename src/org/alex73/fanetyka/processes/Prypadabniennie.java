package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Prypadabniennie {

    @ProcessCase(name = "Прыпадабненне зычных: ц'-т -> т-т", logCountBefore = 2, logCountAfter = 2)
    public boolean ct(Huk h1, Huk h2) {
        h1.bazavyHuk = BAZAVY_HUK.т;
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне свісцячых па мяккасці", logCountBefore = 2, logCountAfter = 2)
    public boolean eq2(Huk h1, Huk h2) {
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне: с'-ш -> ш:, с'-ж -> ж:, с'-з -> з:, з’+ш-> ш:, з’+ж-> ж:", logCountBefore = 2, logCountAfter = 1)
    public boolean ss(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        h1.miakki = h2.miakki;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне: с'+ц-> сц, с'ч -> сч", logCountBefore = 2, logCountAfter = 2)
    public boolean sc(Huk h1, Huk h2) {
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т-ш -> ч-ш пасля галоснага", logCountBefore = 2, logCountAfter = 2)
    public boolean pryTS(Huk huk1, Huk huk2) {
        huk2.bazavyHuk = BAZAVY_HUK.ч;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне: шумны пярэднеязычны змычны+шумны пярэднеязычны змычна-шчылінны", logCountBefore = 2, logCountAfter = 2)
    public boolean prySPZSPZS(Huk huk1, Huk huk2, Huk huk3) {
        huk2.bazavyHuk = huk3.bazavyHuk;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т-ш -> ч-ш, т+ч => ч на канцы слова", logCountBefore = 3, logCountAfter = 2)
    public boolean pryTSe(ProcessContext context) {
        context.vydalicPierad(1);
        return true;
    }

    @ProcessCase(name = "Прыпадабненне ґ+г => г:", logCountBefore = 2, logCountAfter = 2)
    public boolean gh(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = BAZAVY_HUK.г;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т+ц => ц:", logCountBefore = 2, logCountAfter = 2)
    public boolean tc(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне д+дж => дж:, д+дз => дз:", logCountBefore = 2, logCountAfter = 2)
    public boolean ddz(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне дз+д => д+д", logCountBefore = 2, logCountAfter = 2)
    public boolean dzd(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = BAZAVY_HUK.д;
        h1.miakki = 0;
        return true;
    }
}
