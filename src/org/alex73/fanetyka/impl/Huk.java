package org.alex73.fanetyka.impl;

import java.util.Set;

import org.alex73.fanetyka.impl.str.ToStringSkolny;

/**
 * 
 * 
 * Класіфікацыя гукаў: -
 * 
 * - https://ebooks.grsu.by/gachko_phonetic/klasifikatsyya-zychnykh-guka.htm
 * 
 * - ФСБЛМ с. 61
 */
public class Huk {
    public static final int PADZIEL_PRYSTAUKA = 1; // прыстаўка
    public static final int PADZIEL_PRYNAZOUNIK = 16; // Прыназоўнікі без, не, з, праз. TODO Дадаецца разам з падзелам на словы ?
    public static final int PADZIEL_KARANI = 2;
    public static final int PADZIEL_SLOVY = 4;
    public static final int PADZIEL_ZLUCOK = 8;
    public static final int PADZIEL_APOSTRAF = 32;
    public static final int MIAKKASC_ASIMILACYJNAJA = 1;
    public static final int MIAKKASC_PAZNACANAJA = 2;
    public static final int MASK_MIAKKASC_USIE = 3;

    /**
     * Кожны гук мусіць быць: галосным, альбо санорным, альбо парным звонкім, альбо
     * глухім.
     */
    public enum BAZAVY_HUK {
        а, б, в, β, г, ґ, д, э, дж, дз, ж, з, і, к, л, м, ɱ, н, о, п, р, с, т, у, ў, ф, х, ц, ч, ш, ы, j
    }

    public static final Set<BAZAVY_HUK> halosnyja = Set.of(BAZAVY_HUK.а, BAZAVY_HUK.э, BAZAVY_HUK.і, BAZAVY_HUK.о, BAZAVY_HUK.у, BAZAVY_HUK.ы);
    /**
     * Няпарныя звонкія.
     */
    public static final Set<BAZAVY_HUK> sanornyja = Set.of(BAZAVY_HUK.р, BAZAVY_HUK.л, BAZAVY_HUK.м, BAZAVY_HUK.н, BAZAVY_HUK.в, BAZAVY_HUK.β, BAZAVY_HUK.ɱ,
            BAZAVY_HUK.ў, BAZAVY_HUK.j);
    /**
     * Парныя звонкія.
     */
    public static final Set<BAZAVY_HUK> parnyja_zvonkija = Set.of(BAZAVY_HUK.б, BAZAVY_HUK.д, BAZAVY_HUK.дз, BAZAVY_HUK.з, BAZAVY_HUK.ж, BAZAVY_HUK.дж,
            BAZAVY_HUK.г, BAZAVY_HUK.ґ);
    /**
     * Усе глухія - парныя і няпарныя.
     */
    public static final Set<BAZAVY_HUK> hluchija = Set.of(BAZAVY_HUK.п, BAZAVY_HUK.т, BAZAVY_HUK.ц, BAZAVY_HUK.с, BAZAVY_HUK.ш, BAZAVY_HUK.ч, BAZAVY_HUK.х,
            BAZAVY_HUK.к, BAZAVY_HUK.ф);

    /**
     * Шыпячыя.
     */
    public static final Set<BAZAVY_HUK> sypiacyja = Set.of(BAZAVY_HUK.ш, BAZAVY_HUK.ж, BAZAVY_HUK.дж, BAZAVY_HUK.ч);

    /**
     * Свісцячыя.
     */
    public static final Set<BAZAVY_HUK> sviasciacyja = Set.of(BAZAVY_HUK.с, BAZAVY_HUK.з, BAZAVY_HUK.дз, BAZAVY_HUK.ц);

    public String zychodnyjaLitary;
    public BAZAVY_HUK bazavyHuk;
    public int miakki;
    public boolean stressIpa;
    public boolean stress;

    // толькі ўстаўны "а" ці "ы"
    public boolean redukavany;

    /**
     * Гэты гук аддзелены ад наступнага.
     */
    public int padzielPasla;
    public WordContext wordContext; // спасылка на граматычную базу

    public static boolean SKIP_ERRORS = false; // не спыняць працу пры памылках канвертавання
    public boolean debug; // debug not applied processes

    public Huk(String z, BAZAVY_HUK b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }

    @Override
    public String toString() {
        return new ToStringSkolny().huk2str(this);
    }
}
