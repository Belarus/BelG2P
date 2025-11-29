package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;

import org.alex73.grammardb.WordMorphology;

public class WordContext {
    public String word;
    // Усе знойдзеныя ў базе амонімы.
    public List<WordMorphology> amonimy = new ArrayList<>();
    public WordMorphology asnounaja;

}
