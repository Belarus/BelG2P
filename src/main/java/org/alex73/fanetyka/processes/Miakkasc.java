package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;

public class Miakkasc {
    // TODO насамрэч гэтыя метады непатэрбныя ?
    @ProcessCase(name = "Змякчэнне перад мяккімі галоснымі", logCountBefore = 2, logCountAfter = 2)
    public boolean zha(Huk h1) {
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return true;
    }

    @ProcessCase(name = "Змякчэнне перад мяккімі зычнымі", logCountBefore = 2, logCountAfter = 2)
    public boolean zzy(Huk h1) {
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return true;
    }

    @ProcessCase(name = "Змякчэнне д,т перад мяккімі зычнымі", logCountBefore = 2, logCountAfter = 2)
    public boolean zdt(Huk h1) {
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return true;
    }

    @ProcessCase(name = "Змякчэнне дз перад мяккімі зычнымі", logCountBefore = 2, logCountAfter = 2)
    public boolean zdz(Huk h1) {
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return true;
    }

    @ProcessCase(name = "Змякчэнне перад мяккімі вф", logCountBefore = 2, logCountAfter = 2)
    public boolean zvf(Huk h1) {
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return true;
    }
}
