package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Pierachody {

    @ProcessCase(name = "Пераход 'ф'->'ў' перад звонкім пасля галоснага", logCountBefore = 3, logCountAfter = 3)
    public boolean fh(Huk huk1, Huk huk2) {
        huk2.bazavyHuk = BAZAVY_HUK.ў;
        return true;
    }

    @ProcessCase(name = "Пераход 'ф'->'в' перад звонкім пасля зычнага", logCountBefore = 3, logCountAfter = 3)
    public boolean fz(Huk huk1, Huk huk2) {
        huk2.bazavyHuk = BAZAVY_HUK.в;
        return true;
    }
}
