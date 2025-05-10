package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class BilabijalnyV {
    @ProcessCase(name = "Білабіяльны 'в'", logCountBefore = 1, logCountAfter = 1)
    public boolean b(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.β;
        return true;
    }
}
