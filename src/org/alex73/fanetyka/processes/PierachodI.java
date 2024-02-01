package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class PierachodI {
    @ProcessCase("Пераход і ў ы")
    public String a(Huk papiaredni, Huk huk) {
        String r = huk.bazavyHuk.name() + " -> ы";
        huk.bazavyHuk = BAZAVY_HUK.ы;
        huk.miakkajaHalosnaja = false;
        huk.miakki = 0;
        return r;
    }
}
