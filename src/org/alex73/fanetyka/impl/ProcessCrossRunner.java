package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alex73.fanetyka.config.CaseCross;
import org.alex73.fanetyka.config.IConfig;
import org.alex73.fanetyka.config.TsvCrossConfig;
import org.alex73.fanetyka.impl.str.ToStringIPA;

public class ProcessCrossRunner implements IProcess {
    public static final String DEBUG_NAME = "Мяккасць";

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
    public String getProcessTypeName() {
        return processType.getSimpleName();
    }

    @Override
    public Collection<String> getDebugCases() {
        return List.of(DEBUG_NAME);
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

            String debugHuki = null;
            if (h1.debug && h2.debug && DEBUG_NAME.equals(instance.debugRuleName)) {
                debugHuki = '[' + h1.toString() + h2.toString() + ']';
            }

            if (!check(h1, h2, instance.logPhenomenon, debugHuki)) {
                continue;
            }

            CaseCross.TypZmiahcennia zm = ca.values.get(h1.bazavyHuk).get(h2.bazavyHuk);
            if (zm.zmiahcajecca) {
                if (miazaSlou(h1, h2, instance.logPhenomenon, debugHuki)) {
                    if (zm.pierakrocvajeMiezySlou) {
                        setMiakkasc(instance, h1, instance.logPhenomenon);
                    }
                } else if (miazaUsiaredzinieSlova(h1, h2, instance.logPhenomenon, debugHuki)) {
                    if (zm.pierakrocvajeMiezyUsizredzinieSlova) {
                        setMiakkasc(instance, h1, instance.logPhenomenon);
                    }
                } else {
                    setMiakkasc(instance, h1, instance.logPhenomenon);
                }
            }
        }
    }

    private void setMiakkasc(Fanetyka3 instance, Huk h1, ILogging log) {
        String charBefore = null, wordBefore = null;
        if (log != null) {
            charBefore = h1.toString();
            wordBefore = instance.toString(new ToStringIPA());
        }
        h1.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
        if (log != null) {
            String charAfter = h1.toString();
            String wordAfter = instance.toString(new ToStringIPA());
            instance.logPhenomenon.logChange("Мяккасць", "асіміляцыйнае змякчэнне " + h1.bazavyHuk, charBefore, charAfter, wordBefore, wordAfter);
        }
    }

    /**
     * Правяраем гукі на адпаведнасць агульным умовам.
     */
    public static boolean check(Huk h1, Huk h2, ILogging log, String debugHuki) {
        // гук 2 мусіць быць мяккі
        if (h2.miakki == 0) {
            if (debugHuki != null && log != null) {
                log.logChange(debugHuki + " другі гук не мяккі");
            }
            return false;
        }
        // гук 1 не мусіць быць ужо мяккі
        if (h1.miakki != 0) {
            if (debugHuki != null && log != null) {
                log.logChange(debugHuki + " першы гук ужо мяккі");
            }
            return false;
        }
        return true;
    }

    public static boolean miazaUsiaredzinieSlova(Huk h1, Huk h2, ILogging log, String debugHuki) {
        // ці ёсць мяжа ?
        switch (h1.padzielPasla) {
        case Huk.PADZIEL_KARANI:// TODO зліць межы ?
        case Huk.PADZIEL_PRYSTAUKA:
        case Huk.PADZIEL_PRYNAZOUNIK:
        case Huk.PADZIEL_APOSTRAF:
            return true;
        }
        if (debugHuki != null && log != null) {
            log.logChange(debugHuki + " няма мяжы ўсярэдзіне слова паміж гукамі");
        }
        return false;
    }

    public static boolean miazaSlou(Huk h1, Huk h2, ILogging log, String debugHuki) {
        // ці ёсць мяжа ?
        switch (h1.padzielPasla) {
        case Huk.PADZIEL_ZLUCOK:
        case Huk.PADZIEL_SLOVY:
            return true;
        }
        if (debugHuki != null && log != null) {
            log.logChange(debugHuki + " няма мяжы слоў паміж гукамі");
        }
        return false;
    }
}
