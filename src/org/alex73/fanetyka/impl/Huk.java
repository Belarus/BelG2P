package org.alex73.fanetyka.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.alex73.fanetyka.impl.IPAUtils.IPA;

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
    public static final int MIAKKASC_ASIMILACYJNAJA = 1;
    public static final int MIAKKASC_PAZNACANAJA = 2;
    public static final int MASK_MIAKKASC_USIE = 3;

    /**
     * Кожны гук мусіць быць: галосным, альбо санорным, альбо парным звонкім, альбо глухім.
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

    public static final Set<BAZAVY_HUK> sypiacyja = Set.of(BAZAVY_HUK.ш, BAZAVY_HUK.ж, BAZAVY_HUK.дж, BAZAVY_HUK.ч);

    public static final Set<BAZAVY_HUK> sviasciacyja = Set.of(BAZAVY_HUK.с, BAZAVY_HUK.з, BAZAVY_HUK.дз, BAZAVY_HUK.ц);

    public String zychodnyjaLitary;
    public BAZAVY_HUK bazavyHuk;
    public int miakki;
    public boolean padvojeny;
    public boolean apostrafPasla;
    public boolean stressIpa;
    public boolean stress; // TODO: націск у падваеннях і спрашчэннях
    /**
     * Гэты гук павінен быць аддзелены ад наступнага, бо гэта апошні гук прыстаўкі.
     */
    public int padzielPasla;
    public WordContext wordContext; // спасылка на граматычную базу

    public static boolean SKIP_ERRORS = false; // не спыняць працу пры памылках канвертавання
    public boolean debug; // debug not applied processes

    static final Map<IPA, String> ARFAEP_MAP = new HashMap<>();
    static final Map<IPA, String> SKOLNY_MAP = new HashMap<>();

    public Huk(String z, BAZAVY_HUK b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }

    /**
     * Пазначае асіміляцыйную мяккасць ці цвёрдасць. Не павінен канфліктаваць з
     * пазначанай.
     */
    public void setMiakkasc(boolean m) {
        if (miakki == MIAKKASC_PAZNACANAJA && bazavyHuk == BAZAVY_HUK.ы) {
            if (!m) {
                if (!SKIP_ERRORS) {
                    throw new RuntimeException("Спроба асіміляцыйнай цвёрдасці ў пазначаным мяккім");
                }
            }
        } else {
            miakki = m ? MIAKKASC_ASIMILACYJNAJA : 0;
        }
    }

    @Override
    public String toString() {
        return skolny.apply(this);
    }

    static {
        SKOLNY_MAP.put(IPA.a, "а");
        SKOLNY_MAP.put(IPA.b, "б");
        SKOLNY_MAP.put(IPA.bʲ, "б'");
        SKOLNY_MAP.put(IPA.v, "в");
        SKOLNY_MAP.put(IPA.vʲ, "в'");
        SKOLNY_MAP.put(IPA.β, "в");
        SKOLNY_MAP.put(IPA.βʲ, "в'");
        SKOLNY_MAP.put(IPA.ɣ, "г");
        SKOLNY_MAP.put(IPA.ɣʲ, "г'");
        SKOLNY_MAP.put(IPA.g, "ѓ");
        SKOLNY_MAP.put(IPA.gʲ, "ѓ'");
        SKOLNY_MAP.put(IPA.d, "д");
        SKOLNY_MAP.put(IPA.dʲ, "д'");
        SKOLNY_MAP.put(IPA.ɛ, "э");
        SKOLNY_MAP.put(IPA.d͡ʐ, "дж");
        SKOLNY_MAP.put(IPA.d͡z, "дз");
        SKOLNY_MAP.put(IPA.d͡zʲ, "дз'");
        SKOLNY_MAP.put(IPA.ʐ, "ж");
        SKOLNY_MAP.put(IPA.z, "з");
        SKOLNY_MAP.put(IPA.zʲ, "з'");
        SKOLNY_MAP.put(IPA.i, "і");
        SKOLNY_MAP.put(IPA.k, "к");
        SKOLNY_MAP.put(IPA.kʲ, "к'");
        SKOLNY_MAP.put(IPA.ɫ, "л");
        SKOLNY_MAP.put(IPA.lʲ, "л'");
        SKOLNY_MAP.put(IPA.m, "м");
        SKOLNY_MAP.put(IPA.mʲ, "м'");
        SKOLNY_MAP.put(IPA.ɱ, "м");
        SKOLNY_MAP.put(IPA.n, "н");
        SKOLNY_MAP.put(IPA.nʲ, "н'");
        SKOLNY_MAP.put(IPA.ɔ, "о");
        SKOLNY_MAP.put(IPA.p, "п");
        SKOLNY_MAP.put(IPA.pʲ, "п'");
        SKOLNY_MAP.put(IPA.r, "р");
        SKOLNY_MAP.put(IPA.s, "с");
        SKOLNY_MAP.put(IPA.sʲ, "с'");
        SKOLNY_MAP.put(IPA.t, "т");
        SKOLNY_MAP.put(IPA.tʲ, "т'");
        SKOLNY_MAP.put(IPA.u, "у");
        SKOLNY_MAP.put(IPA.u̯, "ў");
        SKOLNY_MAP.put(IPA.f, "ф");
        SKOLNY_MAP.put(IPA.fʲ, "ф'");
        SKOLNY_MAP.put(IPA.x, "х");
        SKOLNY_MAP.put(IPA.xʲ, "х'");
        SKOLNY_MAP.put(IPA.t͡s, "ц");
        SKOLNY_MAP.put(IPA.t͡sʲ, "ц'");
        SKOLNY_MAP.put(IPA.t͡ʂ, "ч");
        SKOLNY_MAP.put(IPA.ʂ, "ш");
        SKOLNY_MAP.put(IPA.ɨ, "ы");
        SKOLNY_MAP.put(IPA.j, "й");

        ARFAEP_MAP.putAll(SKOLNY_MAP);
        ARFAEP_MAP.put(IPA.ɣ, "γ");
        ARFAEP_MAP.put(IPA.ɣʲ, "γ'");
        ARFAEP_MAP.put(IPA.g, "g");
        ARFAEP_MAP.put(IPA.gʲ, "g'");
        ARFAEP_MAP.put(IPA.d͡ʐ, "ž");
        ARFAEP_MAP.put(IPA.d͡z, "z");
        ARFAEP_MAP.put(IPA.d͡zʲ, "z'");
    }

    public static Function<Huk, String> ipa = h -> {
        String o = IPAUtils.ipa_enum.apply(h).name().toLowerCase();
        if (h.stressIpa) {
            o = 'ˈ' + o;
        }
        if (h.stress) {
            // o += '´';
        }
        if (h.padvojeny) {
            o += "ː";
        }
        return o;
    };

    public static Function<Huk, String> ipaOldStress = h -> {
        String o = IPAUtils.ipa_enum.apply(h).name().toLowerCase();
        if (h.stressIpa) {
            // o = 'ˈ' + o;
        }
        if (h.stress) {
            o += '´';
        }
        if (h.padvojeny) {
            o += "ː";
        }
        return o;
    };

    public static Function<Huk, String> arfaep = h -> {
        String o = ARFAEP_MAP.get(IPAUtils.ipa_enum.apply(h));
        if (h.stressIpa) {
            o = 'ˈ' + o;
        }
        if (h.stress) {
            o += '´';
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public static Function<Huk, String> arfaepNoStress = h -> {
        String o = ARFAEP_MAP.get(IPAUtils.ipa_enum.apply(h));
        if (h.stressIpa) {
            // o = 'ˈ' + o;
        }
        if (h.stress) {
            // o += '´';
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public static Function<Huk, String> skolny = h -> {
        String o = SKOLNY_MAP.get(IPAUtils.ipa_enum.apply(h));
        if (h.stressIpa) {
            // o = 'ˈ' + o;
        }
        if (h.stress) {
            o += '\u0301';
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public boolean is(BAZAVY_HUK bazavy, Integer miakkasc, Boolean padvajennie, Integer padziel) {
        if (bazavyHuk != bazavy) {
            return false;
        }
        if (!isInt(miakkasc, miakki)) {
            return false;
        }
        if (padvajennie != null && padvajennie.booleanValue() != padvojeny) {
            return false;
        }
        if (!isInt(padziel, padzielPasla)) {
            return false;
        }
        return true;
    }

    private boolean isInt(Integer expected, int value) {
        if (expected == null) {
            return true;
        }
        int e = expected.intValue();
        if (e == 0) {
            return value == 0;
        }
        return (e & value) != 0;
    }
}
