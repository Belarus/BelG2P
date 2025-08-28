package org.alex73.fanetyka.config;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.alex73.fanetyka.impl.Huk;

public class Case {
    public String name;
    public String logMessage;
    public String details;
    public HukCheck borderCheckBefore; // праверкі для мяжы перад першым гукам
    public List<HukCheck> checks = new ArrayList<>(); // праверкі для кожнага гуку
    public int requiresHuks;
    public List<Example> examples = new ArrayList<>();
    public String[][] table;

    public enum MODE {
        DONT_CARE, YES, NO, ERROR
    }

    public static class HukCheck {
        boolean optionalHuk;
        public String[] which;
        public Predicate<Huk>[] whichFunctions; // праверкі на гукі which
        public MultiMode pasziel = new MultiMode();
        public MultiMode miakkasc = new MultiMode();
        public MODE nacisk = MODE.DONT_CARE;
    }

    public static class MultiMode {
        public int maskYes, maskNo, maskError;
    }

    public static class Example {
        public String caseName;
        public Cell cell;
        public String word, expected;
        public Boolean ruleExecution; // should be true if the example should be changed by this rule
    }
}
