package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Pierachody {
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

    @ProcessCase("Невымаўленне інтэрвакальнага [j] у прыстаўным галосным \"і\" перад спалучэннямі зычных [рлм]  з зычнымі")
    public String zai(ProcessContext context) {
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
        context.huki.remove(pos);
        return "й -> ";
    }

    @ProcessCase("Пераход 'ф'->'ў' перад 'г'")
    public String fg(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ў;
        return "ф -> ў";
    }
}
