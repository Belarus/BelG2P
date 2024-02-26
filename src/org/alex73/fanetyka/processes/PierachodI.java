package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class PierachodI {
    @ProcessCase("Пераход і ў ы")
    public String iy(Huk papiaredni, Huk huk) {
        String r = huk.bazavyHuk.name() + " -> ы";
        huk.bazavyHuk = BAZAVY_HUK.ы;
        huk.miakki = 0;
        return r;
    }

    @ProcessCase("Пераход і ў ы (пасля с)")
    public String siy(Huk papiaredni, Huk huk) {
        String r = huk.bazavyHuk.name() + " -> ы";
        huk.bazavyHuk = BAZAVY_HUK.ы;
        huk.miakki = 0;
        return r;
    }

    @ProcessCase("Прыстаўка за/і(ненаціскны)[рлм] - j не вымаўляецца")
    public String zai(ProcessContext context) {
        context.huki.remove(2);
        return "й -> ";
    }
}
