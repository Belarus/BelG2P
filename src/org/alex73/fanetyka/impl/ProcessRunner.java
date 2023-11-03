package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.alex73.fanetyka.config.Case;
import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.config.TsvConfig;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class ProcessRunner {
    protected final TsvConfig config;
    private final Object processor;
    private final Map<String, Method> methods = new TreeMap<>();

    public ProcessRunner(Class<?> process, Map<String, byte[]> configs) throws Exception {
        processor = process.getDeclaredConstructor().newInstance();
        String processName = process.getSimpleName();
        if (configs == null) {
            config = new TsvConfig(processName);
        } else if (configs.containsKey(processName)) {
            config = new TsvConfig(processName, new ByteArrayInputStream(configs.get(processName)));
        } else {
            config = null; // for debug only
            return;
        }

        for (Method m : process.getMethods()) {
            ProcessCase ca = m.getAnnotation(ProcessCase.class);
            if (ca != null) {
                if (methods.put(ca.value(), m) != null) {
                    throw new Exception("Duplicate cases in methods in class " + process.getSimpleName());
                }
            }
        }
        if (!config.cases.keySet().equals(methods.keySet())) {
            throw new Exception("Wrong list of cases in config and class " + process.getSimpleName() + ": \n    exist in class but not in configs : "
                    + setMinus(methods.keySet(), config.cases.keySet()) + "\n    exist in configs but not in class: "
                    + setMinus(config.cases.keySet(), methods.keySet()));
        }
    }

    private String setMinus(Set<String> s1, Set<String> s2) {
        Set<String> r = new TreeSet<>(s1);
        r.removeAll(s2);
        return String.join(",", r);
    }

    public boolean isConfigExists() {
        return config != null;
    }

    /**
     * Для кожнай табліцы праходзіць па ўсіх гуках.
     */
    public void process(Fanetyka3 instance) throws Exception {
        ProcessContext context = new ProcessContext();
        context.huki = instance.huki;
        for(String caseName: config.casesOrder) {
            Method m = methods.get(caseName);
            Case ca = config.cases.get(caseName);
            // ад канца да пачатка
            for (int pos = instance.huki.size() - ca.requiresHuks; pos >= 0; pos--) {
                context.currentPosition = pos;
                if (!check(ca, context)) {
                    continue;
                }
                // збіраем параметры для выкліка метада
                int hukParamsCount = 0;
                List<Object> parameters = new ArrayList<>();
                for (Parameter p : m.getParameters()) {
                    if (Huk.class.isAssignableFrom(p.getType())) {
                        if (hukParamsCount < 0) {
                            throw new Exception("Метад " + m + " ужо мае параметр List");
                        }
                        if (hukParamsCount > ca.checks.size()) {
                            throw new Exception("Метад " + m + " мае зашмат параметраў для гукаў");
                        } else if (hukParamsCount < context.huki.size() - context.currentPosition) {
                            parameters.add(context.huki.get(hukParamsCount + context.currentPosition));
                        } else {
                            parameters.add(null);
                        }
                        hukParamsCount++;
                    } else if (ProcessContext.class.isAssignableFrom(p.getType())) {
                        parameters.add(context);
                    } else {
                        throw new Exception("Метад " + m + " мае невядомы параметр " + p);
                    }
                }
                // спачатку выклікаем метад, і ён ужо можа выклікаць праверку па табліцы, пасля
                // таго як сам зробіць іншыя праверкі
                String before = instance.toString(Huk.ipa);
                String change = (String) m.invoke(processor, parameters.toArray());
                if (change != null) {
                    instance.why.add(ca.logMessage.replace("()", "(" + change + ")") + ": " + before + " -> " + instance.toString(Huk.ipa));
                }
            }
        }
    }

    /**
     * Правяраем гукі адпаведнасць умовам - пасля папярэдняй праверкі ў метадзе.
     */
    private boolean check(Case ca, ProcessContext context) {
        int before = Math.min(context.currentPosition + ca.checks.size(), context.huki.size());
        if (ca.borderCheckBefore != null) {
            // мяжа перад першым гукам табліцы
            Huk h;
            if (context.currentPosition > 0) {
                h = context.huki.get(context.currentPosition - 1);
            } else {
                // перад пачаткам слова
                h = new Huk(null, null);
                h.padzielPasla = Huk.PADZIEL_SLOVY;
            }
            if (!checkRules(ca.name, ca.borderCheckBefore, h)) {
                return false;
            }
        }
        for (int i = context.currentPosition; i < before; i++) {
            if (!checkHuk(ca.name, ca.checks.get(i - context.currentPosition), context.huki.get(i))) {
                return false;
            }
        }
        for (int i = context.currentPosition; i < before; i++) {
            if (!checkRules(ca.name, ca.checks.get(i - context.currentPosition), context.huki.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Правяраем адзін гук на адпаведнасць гукам у табліцы.
     */
    static boolean checkHuk(String zjava, Case.HukCheck c, Huk huk) {
        if (c.which == null) {
       //     return true; // табліца пачынаецца з мяжы слова, а не з гука
        }
        for (String h : c.which) {
            switch (h) {
            case "звонкі":
                if (huk.isZvonki()) {
                    return true;
                }
                break;
            case "глухі":
                if (huk.isHluchi()) {
                    return true;
                }
                break;
            case "галосны":
                if (huk.halosnaja) {
                    return true;
                }
                break;
            case "зычны":
                if (!huk.halosnaja) { // TODO
                    return true;
                }
                break;
            case "шыпячы":
                if (huk.isSypiacy()) {
                    return true;
                }
                break;
            case "свісцячы":
                if (huk.isSvisciacy()) {
                    return true;
                }
                break;
            default:
                BAZAVY_HUK expected = BAZAVY_HUK.valueOf(h);
                if (huk.bazavyHuk.equals(expected)) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * Правяраем адзін гук на адпаведнасць умовам.
     */
    static boolean checkRules(String zjava, Case.HukCheck c, Huk huk) {
        return checkValue(zjava, "апостраф", c.apostraf, huk.apostrafPasla) && checkValue(zjava, "падзел", c.pasziel, huk.padzielPasla)
                && checkValue(zjava, "мяккасць", c.miakkasc, huk.miakki) && checkValue(zjava, "падвоены", c.padvojeny, huk.padvojeny)
                && checkValue(zjava, "націск", c.nacisk, huk.stress);
    }

    static boolean checkValue(String zjava, String umova, Case.MODE mode, boolean value) {
        switch (mode) {
        case DONT_CARE:
            return true;
        case ERROR:
            if (value) {
                throw new RuntimeException("У з'яве '%s' не можа выконвацца ўмова '%s'".formatted(zjava, umova));
            }
            return true;
        case YES:
            return value == true;
        case NO:
            return value == false;
        default:
            throw new RuntimeException();
        }
    }

    static boolean checkValue(String zjava, String umova, Case.MultiMode mode, int value) {
        if ((mode.maskError & value) != 0) {
            throw new RuntimeException("У з'яве '%s' не можа выконвацца ўмова '%s' у значэнні %d".formatted(zjava, umova, value));
        }

        if (mode.maskYes != 0) {
            return (mode.maskYes & value) != 0;
        } else if (mode.maskNo != 0) {
            return (mode.maskNo & value) == 0;
        } else {
            return true;
        }
    }
}
