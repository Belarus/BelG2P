package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.alex73.fanetyka.config.Case;
import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.config.ProcessConfig;
import org.alex73.fanetyka.config.TsvConfig;

public class ProcessRunner {
    protected final TsvConfig config;
    private final Object processor;
    private final List<Method> methods = new ArrayList<>();

    public ProcessRunner(Class<?> process, Map<String, byte[]> configs) throws Exception {
        ProcessConfig pc = process.getAnnotation(ProcessConfig.class);
        config = configs == null ? new TsvConfig(pc.value()) : new TsvConfig(pc.value(), new ByteArrayInputStream(configs.get(pc.value())));
        processor = process.getDeclaredConstructor().newInstance();

        Set<String> usedCases = new TreeSet<>();
        for (Method m : process.getMethods()) {
            ProcessCase ca = m.getAnnotation(ProcessCase.class);
            if (ca != null) {
                methods.add(m);
                usedCases.add(ca.value());
            }
        }
        if (!config.cases.keySet().equals(usedCases)) {
            throw new Exception("Wrong list of cases in config and class " + process.getSimpleName() + ": \n    class : "
                    + String.join(",", config.cases.keySet()) + "\n    config:" + String.join(",", usedCases));
        }
    }

    public void process(List<Huk> huki, List<String> log) throws Exception {
        for (Method m : methods) {
            Case ca = config.cases.get(m.getAnnotation(ProcessCase.class).value());
            // ад канца да пачатка
            for (int pos = huki.size() - ca.requiresHuks; pos >= 0; pos--) {
                List<Huk> part = huki.subList(pos, Math.min(pos + ca.checks.size(), huki.size()));
                Supplier<Boolean> check = () -> check(ca, part);
                // збіраем параметры для выкліка метада
                int hukParamsCount = 0;
                List<Object> parameters = new ArrayList<>();
                for (Parameter p : m.getParameters()) {
                    if (List.class.isAssignableFrom(p.getType())) {
                        if (hukParamsCount > 0) {
                            throw new Exception("Метад " + m + " ужо мае параметры Huk");
                        }
                        parameters.add(part);
                    } else if (Huk.class.isAssignableFrom(p.getType())) {
                        if (hukParamsCount < 0) {
                            throw new Exception("Метад " + m + " ужо мае параметр List");
                        }
                        if (hukParamsCount > ca.checks.size()) {
                            throw new Exception("Метад " + m + " мае зашмат параметраў для гукаў");
                        } else if (hukParamsCount < part.size()) {
                            parameters.add(part.get(hukParamsCount));
                        } else {
                            parameters.add(null);
                        }
                        hukParamsCount++;
                    } else if (Supplier.class.isAssignableFrom(p.getType())) {
                        parameters.add(check);
                    } else {
                        throw new Exception("Метад " + m + " мае невядомы параметр " + p);
                    }
                }
                // спачатку выклікаем метад, і ён ужо можа выклікаць праверку па табліцы, пасля
                // таго як сам зробіць іншыя праверкі
                String change = (String) m.invoke(processor, parameters.toArray());
                if (change != null) {
                    log.add(ca.logMessage.replace("()", "(" + change + ")"));
                }
            }
        }
    }

    /**
     * Правяраем гукі адпаведнасць умовам - пасля папярэдняй праверкі ў метадзе.
     */
    private boolean check(Case ca, List<Huk> part) {
        for (int i = 0; i < part.size(); i++) {
            if (!check(ca.name, ca.checks.get(i), part.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Правяраем адзін гук на адпаведнасць умовам.
     */
    static boolean check(String zjava, Case.HukCheck c, Huk huk) {
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
