package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.ProcessContext;

public class Sprascennie {
    @ProcessCase(name = "Спрашчэнне: шумны глухі шчылінны + шумны глухі змычна-шчылінны + шумны глухі змычны", logCountBefore = 3, logCountAfter = 2)
    public boolean sck(ProcessContext context) {
        // толькі калі перад гэтымі гукамі не ідзе зычны
        if (context.currentPosition > 0) {
            Huk h = context.huki.get(context.currentPosition - 1);
            if (!Huk.halosnyja.contains(h.bazavyHuk)) {
                // перад с-ц-к стаіць зычны - не спрашчаецца
                return false;
            }
        }
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: шумны глухі шчылінны + шумны глухі змычны + шумны глухі змычна-шчылінны", logCountBefore = 3, logCountAfter = 2)
    public boolean stc(ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: шумны шчылінны зубны + шумны змычны зубны + санорны носавы", logCountBefore = 3, logCountAfter = 2)
    public boolean stn(ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: шумны шчылінны пярэднеязычны + шумны шчылінны пярэднеязычны + шумны змычны", logCountBefore = 3, logCountAfter = 2)
    public boolean sss(ProcessContext context) {
        context.vydalicPierad(1);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: зычны + заднеязычны + шумны глухі шчылінны + заднеязычны", logCountBefore = 4, logCountAfter = 3)
    public boolean sks(ProcessContext context, Huk h1) {
        context.vydalicPierad(2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: Шумны глухі шчылінны, пярэднеязычны апікальны, зубны + Шумны глухі змычны, пярэднеязычны апікальны, зубны + Шумны глухі шчылінны, пярэднеязычны апікальны, зубны", logCountBefore = 3, logCountAfter = 2)
    public boolean sts(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: Шумны звонкі шчылінны, пярэднеязычны апікальны, зубны + Шумны звонкі змычны, пярэднеязычны апікальны, зубны + Шумны глухі змычна-шчылінны, пярэднеязычны апікальны, пярэднепаднябенны", logCountBefore = 3, logCountAfter = 2)
    public boolean zdc(Huk h1, Huk h2, ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: шумны шчылінны + шумны змычна-шчылінны + мяжа + любы зычны", logCountBefore = 3, logCountAfter = 2)
    public boolean cch(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: н-т-ш-ч -> н-ш-ч", logCountBefore = 4, logCountAfter = 3)
    public boolean ntsc(Huk h1, Huk h2, ProcessContext context) {
        context.vydalicPierad(2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: н-т-с -> н-с", logCountBefore = 3, logCountAfter = 2)
    public boolean nts(Huk h1, Huk h2, ProcessContext context) {
        context.vydalicPierad(2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: т-с не на мяжы -> ц", logCountBefore = 2, logCountAfter = 1)
    public boolean ts(Huk huk1, Huk huk2, ProcessContext context) {
        if (huk1.bazavyHuk == Huk.BAZAVY_HUK.н) {
            // першы гук 'н' - іншамоўнае слова
            return false;
        }
        huk2.bazavyHuk = BAZAVY_HUK.ц;
        context.vydalicPasla(1);
        return true;
    }

    @ProcessCase(name = "Пераход: т-с на мяжы -> ц-с", logCountBefore = 2, logCountAfter = 2)
    public boolean ts_miaza(Huk huk1, Huk huk2, ProcessContext context) {
        if (huk1.bazavyHuk == Huk.BAZAVY_HUK.н) {
            // першы гук 'н' - іншамоўнае слова
            return false;
        }
        huk2.bazavyHuk = BAZAVY_HUK.ц;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: б-р-с -> б-с", logCountBefore = 3, logCountAfter = 2)
    public boolean brs(Huk h1, Huk h2, ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: м-л'-с -> м-с", logCountBefore = 3, logCountAfter = 2)
    public boolean mls(Huk h1, Huk h2, ProcessContext context) {
        context.vydalicPasla(0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: ц-ч -> ч", logCountBefore = 2, logCountAfter = 1)
    public boolean cc(Huk hal, Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = Huk.BAZAVY_HUK.ч;
        h1.miakki = 0;
        return true;
    }
}
