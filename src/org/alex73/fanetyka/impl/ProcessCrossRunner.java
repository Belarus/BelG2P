package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.alex73.fanetyka.config.CaseCross;
import org.alex73.fanetyka.config.IConfig;
import org.alex73.fanetyka.config.TsvCrossConfig;

public class ProcessCrossRunner implements IProcess {
    public final Class<?> processType;
    public final TsvCrossConfig config;

    public ProcessCrossRunner(Class<?> process, Map<String, byte[]> configs) throws Exception {
        this.processType = process;
        String processName = process.getSimpleName();
        config = new TsvCrossConfig(processName, new ByteArrayInputStream(configs.get(processName)));
    }

    @Override
    public IConfig getConfig() {
        return config;
    }

    @Override
    public String getProcessType() {
        return processType.getSimpleName();
    }

    public boolean isConfigExists() {
        return config != null;
    }

    /**
     * Для кожнай табліцы праходзіць па ўсіх гуках.
     */
    public void process(Fanetyka3 instance) throws Exception {
        CaseCross ca = config.cross;
        // ад канца да пачатка
        for (int pos = instance.huki.size() - 2; pos >= 0; pos--) {
            Huk h1 = instance.huki.get(pos);
            Huk h2 = instance.huki.get(pos + 1);
            if (!check(h1, h2)) {
                continue;
            }

            CaseCross.TypZmiahcennia zm = ca.values.get(h1.bazavyHuk).get(h2.bazavyHuk);
            if (zm.zmiahcajecca) {
                if (miazaSlou(h1, h2)) {
                    if (zm.pierakrocvajeMiezySlou) {
                        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                        instance.why.add("Мяккасць: адбылося змякчэнне " + h1.bazavyHuk);
                    }
                } else if (miazaUsiaredzinieSlova(h1, h2)) {
                    if (zm.pierakrocvajeMiezyUsizredzinieSlova) {
                        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                        instance.why.add("Мяккасць: адбылося змякчэнне " + h1.bazavyHuk);
                    }
                } else {
                    h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    instance.why.add("Мяккасць: адбылося змякчэнне " + h1.bazavyHuk);
                }
            }
        }
    }

    /**
     * Правяраем гукі на адпаведнасць агульным умовам.
     */
    public static boolean check(Huk h1, Huk h2) {
        // гук 2 мусіць быць мяккі
        if (h2.miakki == 0) {
            return false;
        }
        // гук 1 не мусіць быць ужо мяккі
        if (h1.miakki != 0) {
            return false;
        }
        return true;
    }

    public static boolean miazaUsiaredzinieSlova(Huk h1, Huk h2) {
        // ці ёсць мяжа ?
        switch (h1.padzielPasla) {
        case Huk.PADZIEL_KARANI:
        case Huk.PADZIEL_PRYSTAUKA:
            return true;
        }
        if (h1.apostrafPasla) {
            return true;
        }
        return false;
    }

    public static boolean miazaSlou(Huk h1, Huk h2) {
        // ці ёсць мяжа ?
        switch (h1.padzielPasla) {
        case Huk.PADZIEL_MINUS:
        case Huk.PADZIEL_SLOVY:
            return true;
        }
        return false;
    }
}
