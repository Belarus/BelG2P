package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class HubnaZubnyM {
    @ProcessCase(name = "Губна-зубны 'м'", logCountBefore = 1, logCountAfter = 1)
    public boolean m(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ɱ;
        return true;
    }
}
