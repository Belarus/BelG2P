package org.alex73.fanetyka.config;

import java.util.HashMap;
import java.util.Map;

import org.alex73.fanetyka.impl.Huk;

public class CaseCross {

    public Map<Huk.BAZAVY_HUK, Map<Huk.BAZAVY_HUK, TypZmiahcennia>> values = new HashMap<>();

    public String[][] table;

    public static class TypZmiahcennia {
        public boolean nievyznacana;
        public boolean zmiahcajecca;
        public boolean pierakrocvajeMiezyUsizredzinieSlova;
        public boolean pierakrocvajeMiezySlou;
    }
}
