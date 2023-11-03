package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class HubnaZubnyM {
    @ProcessCase("Губна-зубны 'м'")
    public String m(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ɱ;
        return "м -> ɱ";
    }
}
