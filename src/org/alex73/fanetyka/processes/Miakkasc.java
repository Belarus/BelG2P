package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;

public class Miakkasc {
    //TODO насамрэч гэтыя метады непатэрбныя ?
    @ProcessCase("Змякчэнне перад мяккімі галоснымі")
    public String zha(Huk h1) {
        String before = h1.toString();
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return before + "->" + h1;
    }

    @ProcessCase("Змякчэнне перад мяккімі зычнымі")
    public String zzy(Huk h1) {
        String before = h1.toString();
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return before + "->" + h1;
    }

    @ProcessCase("Змякчэнне д,т перад мяккімі зычнымі")
    public String zdt(Huk h1) {
        String before = h1.toString();
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return before + "->" + h1;
    }

    @ProcessCase("Змякчэнне дз перад мяккімі зычнымі")
    public String zdz(Huk h1) {
        String before = h1.toString();
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return before + "->" + h1;
    }

    @ProcessCase("Змякчэнне перад мяккімі вф")
    public String zvf(Huk h1) {
        String before = h1.toString();
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        return before + "->" + h1;
    }

}
