package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.ProcessContext;

public class PierachodZHA {
    @ProcessCase("Устаўное а")
    public String a(ProcessContext context) {
        Huk a = new Huk("", BAZAVY_HUK.а);
        a.halosnaja = true;
        context.huki.add(context.currentPosition + 1, a);
        return null;
    }
}
