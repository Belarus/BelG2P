package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class PierachodFH {
    @ProcessCase("Пераход 'ф'->'ў' перад 'г' у няправільных запазычаннях")
    public String fg(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ў;
        return "ф -> ў";
    }
}
