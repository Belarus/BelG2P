package org.alex73.fanetyka.config;

import java.util.ArrayList;
import java.util.List;

public class Case {
    public String name;
    public String logMessage;
    public String details;
    public HukCheck borderCheckBefore; // праверкі для мяжы перад першым гукам
    public List<HukCheck> checks = new ArrayList<>(); // праверкі для кожнага гуку
    public int requiresHuks;
    public List<Example> examples = new ArrayList<>();

    public enum MODE {
        DONT_CARE, YES, NO, ERROR
    }

    public static class HukCheck {
        boolean optionalHuk;
        public String[] which;
        public MODE apostraf = MODE.DONT_CARE;
        public MultiMode pasziel = new MultiMode();
        public MultiMode miakkasc = new MultiMode();
        public MODE padvojeny = MODE.DONT_CARE;
        public MODE nacisk = MODE.DONT_CARE;
    }

    public static class MultiMode {
        public int maskYes, maskNo, maskError;
    }

    public static class Example {
        public String caseName;
        public Cell cell;
        public String word, expected;
    }
}
