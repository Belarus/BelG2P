package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Prypadabniennie {
    @ProcessCase("Прыпадабненне дч -> тч, дц -> тц")
    public String pry(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.т;
        return "";
    }
}
