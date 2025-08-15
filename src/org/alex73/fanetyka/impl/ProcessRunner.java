package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.alex73.fanetyka.config.Case;
import org.alex73.fanetyka.config.IConfig;
import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.config.TsvConfig;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class ProcessRunner implements IProcess {
    public final Class<?> processType;
    protected final TsvConfig config;
    private final Object processor;
    private final Map<String, Method> methods = new TreeMap<>();

    public ProcessRunner(Class<?> process, Map<String, byte[]> configs) throws Exception {
        this.processType = process;
        processor = process.getDeclaredConstructor().newInstance();
        String processName = process.getSimpleName();
        config = new TsvConfig(processName, new ByteArrayInputStream(configs.get(processName)));

        for (Method m : process.getMethods()) {
            ProcessCase ca = m.getAnnotation(ProcessCase.class);
            if (ca != null) {
                if (methods.put(ca.name(), m) != null) {
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
        return config.cases.keySet();
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
        context.debug = instance.logPhenomenon;
        // from end to beginning
        for (int pos = instance.huki.size(); pos >= 0; pos--) {
            for (String caseName : config.casesOrder) {
                Method m = methods.get(caseName);
                Case ca = config.cases.get(caseName);
                context.currentPosition = pos;

                // ціхапае гукаў на з'яву ?
                if (context.currentPosition + ca.requiresHuks > context.huki.size()) {
                    // не хапае гукаў для праверкі
                    continue;
                }

                // detect if debug required
                context.debugPrefix = null;
                if (caseName.equals(instance.debugRuleName)) {
                    context.debugPrefix = "[";
                    for (int i = context.currentPosition; i < context.currentPosition + ca.checks.size() && i < context.huki.size(); i++) {
                        context.debugPrefix += context.huki.get(i).toString();
                        if (!context.huki.get(i).debug) {
                            // do not debug if some huk is outside of debug mark
                            context.debugPrefix = null;
                            break;
                        }
                    }
                    if (context.debugPrefix != null) {
                        context.debugPrefix += ']';
                    }
                }
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
                ProcessCase ann = m.getAnnotation(ProcessCase.class);
                String before = context.dump(ann.logCountBefore());
                String wordBefore = instance.toString(Huk.ipa);
                boolean changed = (Boolean) m.invoke(processor, parameters.toArray());
                if (changed) {
                    String after = context.dump(ann.logCountAfter());
                    String wordAfter = instance.toString(Huk.ipa);
                    instance.logPhenomenon.add(ca.name + ": [" + before + " -> " + after + "]. " + ca.logMessage + "; " + wordBefore + "=>" + wordAfter);
                    if (caseName.equals(instance.debugRuleName)) {
                        instance.debugRuleProcessed = true; // ці выканалася гэтае правіла
                    }
                }
            }
        }
    }

    /**
     * Check sounds for table before method execution.
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
            String err = checkRules(ca.name, ca.borderCheckBefore, h);
            if (err != null) {
                if (context.debugPrefix != null) {
                    context.debug.add(context.debugPrefix + " не выканалася ўмова для гука напачатку: " + err);
                }
                return false;
            }
        }
        for (int i = context.currentPosition; i < before; i++) {
            if (!checkHuk(ca.name, ca.checks.get(i - context.currentPosition), context.huki.get(i))) {
                if (context.debugPrefix != null) {
                    context.debug.add(context.debugPrefix + " гук не супадае ў пазіцыі +" + (i - context.currentPosition) + ": '" + context.huki.get(i) + "' замест '"
                            + String.join(" ", ca.checks.get(i - context.currentPosition).which) + "'");
                }
                return false;
            }
        }
        for (int i = context.currentPosition; i < before; i++) {
            String err = checkRules(ca.name, ca.checks.get(i - context.currentPosition), context.huki.get(i));
            if (err != null) {
                if (context.debugPrefix != null) {
                    context.debug.add(context.debugPrefix + " не выканалася ўмова для гука ў пазіцыі +" + (i - context.currentPosition) + ": " + err);
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Вяртае Predicate, які правярае гук на адпаведнасць гукам у табліцы.
     */
    public static Predicate<Huk> checkHukFactory(String which) {
        switch (which) {
        case "любы":
            return h -> true;
        case "звонкі":
            return huk -> huk.isZvonki();
        case "глухі":
            return huk -> huk.isHluchi();
        case "галосны":
            return huk -> huk.halosnaja;
        case "зычны":
            return huk -> !huk.halosnaja;
        case "шыпячы":
            return huk -> huk.isSypiacy();
        case "свісцячы":
            return huk -> huk.isSvisciacy();
        case "санорны":
            return huk -> huk.isSanorny();
        default:
            BAZAVY_HUK expected = BAZAVY_HUK.valueOf(which);
            return huk -> huk.bazavyHuk == expected;
        }
    }

    /**
     * Правяраем адзін гук на адпаведнасць гукам у табліцы.
     */
    static boolean checkHuk(String zjava, Case.HukCheck c, Huk huk) {
        for (Predicate<Huk> p : c.whichFunctions) {
            if (p.test(huk)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Правяраем адзін гук на адпаведнасць умовам. Вяртае назву ўмовы, якая не
     * выканалася, альбо null, калі ўсё добра.
     */
    static String checkRules(String zjava, Case.HukCheck c, Huk huk) {
        if (!checkValue(zjava, "апостраф", c.apostraf, huk.apostrafPasla)) {
            return "апостраф";
        }
        if (!checkValue(zjava, "падзел", c.pasziel, huk.padzielPasla)) {
            return "падзел " + huk.padzielPasla;
        }
        if (!checkValue(zjava, "мяккасць", c.miakkasc, huk.miakki)) {
            return "мяккасць " + huk.miakki;
        }
        if (!checkValue(zjava, "падвоены", c.padvojeny, huk.padvojeny)) {
            return "падвоены";
        }
        if (!checkValue(zjava, "націск", c.nacisk, huk.stress)) {
            return "націск";
        }
        return null;
    }

    static boolean checkValue(String zjava, String umova, Case.MODE mode, boolean value) {
        switch (mode) {
        case DONT_CARE:
            return true;
        case ERROR:
            if (value && !Huk.SKIP_ERRORS) {
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
        if ((mode.maskError & value) != 0  && !Huk.SKIP_ERRORS) {
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
