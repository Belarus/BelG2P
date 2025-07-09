package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Pierachody {
//    @ProcessCase(name = "Пераход і ў ы", logCountBefore = 2, logCountAfter = 2)
//    public boolean iy(Huk papiaredni, Huk huk) {
//        huk.bazavyHuk = BAZAVY_HUK.ы;
//        huk.miakki = 0;
//        return true;
//    }
//
//    @ProcessCase(name = "Пераход і ў ы (пасля с)", logCountBefore = 2, logCountAfter = 2)
//    public boolean siy(Huk papiaredni, Huk huk) {
//        huk.bazavyHuk = BAZAVY_HUK.ы;
//        huk.miakki = 0;
//        return true;
//    }
//
//    @ProcessCase(name = "Невымаўленне інтэрвакальнага [j] у прыстаўным галосным \"і\" перад спалучэннямі зычных [рлм]  з зычнымі", logCountBefore = 4, logCountAfter = 3)
//    public boolean zai(ProcessContext context) {
//        int pos = context.currentPosition + 1;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
//        context.huki.remove(pos);
//        return true;
//    }

    @ProcessCase(name = "Пераход 'ф'->'ў' перад 'г'", logCountBefore = 2, logCountAfter = 2)
    public boolean fg(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ў;
        return true;
    }
}
