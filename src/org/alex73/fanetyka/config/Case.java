package org.alex73.fanetyka.config;

import java.util.ArrayList;
import java.util.List;

public class Case {
    public String name;
    public String logMessage;
    public List<HukCheck> checks = new ArrayList<>();
    public int requiresHuks;
    public List<Example> examples = new ArrayList<>();

    public enum MODE {
        DONT_CARE, YES, NO, ERROR
    }

    public static class HukCheck {
        boolean optionalHuk;
        String which;
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
        public int lineIndex;
        public char column;
        public String word, expected;
    }
}
