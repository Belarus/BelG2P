package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class BilabijalnyV {
    @ProcessCase("Білабіяльны 'в'")
    public String b(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.β;
        return "в -> β";
    }
}
