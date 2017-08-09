package org.alex73.korpus.voice;

import java.util.function.Function;

public class Huk {
    public static int PADZIEL_PRYSTAUKA = 1;
    public static int PADZIEL_KORANI = 2;
    public static int PADZIEL_SLOVY = 4;
    public static int PADZIEL_MINUS = 8;
    public static int MIAKKASC_ASIMILACYJNAJA = 1;
    public static int MIAKKASC_PAZNACANAJA = 2;
    public static int MASK_MIAKKASC_USIE=3;

    public String zychodnyjaLitary;
    public String bazavyHuk;
    public int miakki;
    public boolean padvojeny;
    public boolean halosnaja, miakkajaHalosnaja;
    public boolean apostrafPasla;
    /** Гэты гук павінен быць аддзелены ад наступнага, бо гэта апошні гук прыстаўкі. */
    public int padzielPasla;

    public Huk(String z, String b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }
    
    /**
     * Пазначае асіміляцыйную мяккасць ці цвёрдасць. Не павінен канфліктаваць з пазначанай.
     */
    public void setMiakkasc(boolean m) {
        if (miakki==MIAKKASC_PAZNACANAJA) {
            if (!m) {
                throw new RuntimeException("Спроба асіміляцыйнай цвёрдасці ў пазначаным мяккім");
            }
        } else {
            miakki = m?MIAKKASC_ASIMILACYJNAJA:0;
        }
    }

    @Override
    public String toString() {
        return ipa.apply(this);
    }

    public static Function<Huk, String> ipa = h -> {
        String o;
        switch (h.bazavyHuk) {
        case "a":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "b":
            o = h.miakki != 0 ? "bʲ" : "b";
            break;
        case "v":
            o = h.miakki != 0 ? "vʲ" : "v";
            break;
        case "β":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ɣ":
            o = h.miakki != 0 ? "ʝ" : "ɣ";
            break;
        case "g":
            o = h.miakki != 0 ? "ɟ" : "g";
            break;
        case "d":
            o = h.miakki != 0 ? "dʲ" : "d";
            break;
        case "ɛ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "d͡ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "d͡z":
            o = h.miakki != 0 ? "d͡zʲ" : "d͡z";
            break;
        case "ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "z":
            o = h.miakki != 0 ? "zʲ" : "z";
            break;
        case "i":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "k":
            o = h.miakki != 0 ? "c" : "k";
            break;
        case "l":
            o = h.miakki != 0 ? "ʎ" : "l";
            break;
        case "m":
            o = h.miakki != 0 ? "mʲ" : "m";
            break;
        case "ɱ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "n":
            o = h.miakki != 0 ? "ɲ" : "n";
            break;
        case "ɔ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "p":
            o = h.miakki != 0 ? "pʲ" : "p";
            break;
        case "r":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "s":
            o = h.miakki != 0 ? "sʲ" : "s";
            break;
        case "t":
            o = h.miakki != 0 ? "tʲ" : "t";
            break;
        case "u":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "u̯":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "f":
            o = h.miakki != 0 ? "fʲ" : "f";
            break;
        case "x":
            o = h.miakki != 0 ? "ç" : "x";
            break;
        case "t͡s":
            o = h.miakki != 0 ? "t͡sʲ" : "t͡s";
            break;
        case "t͡ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ɨ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "j":
            // ён заўсёды аднолькавы
            o = h.bazavyHuk;
            break;
        default:
            throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
        }
        if (h.padvojeny) {
            o += "ː";
        }
        return o;
    };

    public static Function<Huk, String> arfaep = h -> {
        String o;
        switch (h.bazavyHuk) {
        case "a":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "а";
            break;
        case "b":
            o = h.miakki != 0 ? "б'" : "б";
            break;
        case "v":
            o = h.miakki != 0 ? "в'" : "в";
            break;
        case "β":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "в";
            break;
        case "ɣ":
            o = h.miakki != 0 ? "γ'" : "γ";
            break;
        case "g":
            o = h.miakki != 0 ? "g'" : "g";
            break;
        case "d":
            o = h.miakki != 0 ? "д'" : "д";
            break;
        case "ɛ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "э";
            break;
        case "d͡ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ž";
            break;
        case "d͡z":
            o = h.miakki != 0 ? "z'" : "z";
            break;
        case "ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ж";
            break;
        case "z":
            o = h.miakki != 0 ? "з'" : "з";
            break;
        case "i":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "і";
            break;
        case "k":
            o = h.miakki != 0 ? "к'" : "к";
            break;
        case "l":
            o = h.miakki != 0 ? "л'" : "л";
            break;
        case "m":
            o = h.miakki != 0 ? "м'" : "м";
            break;
        case "ɱ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "м";
            break;
        case "n":
            o = h.miakki != 0 ? "н'" : "н";
            break;
        case "ɔ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "о";
            break;
        case "p":
            o = h.miakki != 0 ? "п'" : "п";
            break;
        case "r":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "р";
            break;
        case "s":
            o = h.miakki != 0 ? "с'" : "с";
            break;
        case "t":
            o = h.miakki != 0 ? "т'" : "т";
            break;
        case "u":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "у";
            break;
        case "u̯":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ў";
            break;
        case "f":
            o = h.miakki != 0 ? "ф'" : "ф";
            break;
        case "x":
            o = h.miakki != 0 ? "х'" : "х";
            break;
        case "t͡s":
            o = h.miakki != 0 ? "ц'" : "ц";
            break;
        case "t͡ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ч";
            break;
        case "ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ш";
            break;
        case "ɨ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ы";
            break;
        case "j":
            // ён заўсёды аднолькавы
            o = "й";
            break;
        default:
            throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public static Function<Huk, String> skolny = h -> {
        String o;
        switch (h.bazavyHuk) {
        case "a":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "а";
            break;
        case "b":
            o = h.miakki != 0 ? "б'" : "б";
            break;
        case "v":
            o = h.miakki != 0 ? "в'" : "в";
            break;
        case "β":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "в";
            break;
        case "ɣ":
            o = h.miakki != 0 ? "г'" : "г";
            break;
        case "g":
            o = h.miakki != 0 ? "ѓ'" : "ѓ";
            break;
        case "d":
            o = h.miakki != 0 ? "д'" : "д";
            break;
        case "ɛ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "э";
            break;
        case "d͡ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "дж";
            break;
        case "d͡z":
            o = h.miakki != 0 ? "дз'" : "дз";
            break;
        case "ʐ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ж";
            break;
        case "z":
            o = h.miakki != 0 ? "з'" : "з";
            break;
        case "i":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "і";
            break;
        case "k":
            o = h.miakki != 0 ? "к'" : "к";
            break;
        case "l":
            o = h.miakki != 0 ? "л'" : "л";
            break;
        case "m":
            o = h.miakki != 0 ? "м'" : "м";
            break;
        case "ɱ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "м";
            break;
        case "n":
            o = h.miakki != 0 ? "н'" : "н";
            break;
        case "ɔ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "о";
            break;
        case "p":
            o = h.miakki != 0 ? "п'" : "п";
            break;
        case "r":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "р";
            break;
        case "s":
            o = h.miakki != 0 ? "с'" : "с";
            break;
        case "t":
            o = h.miakki != 0 ? "т'" : "т";
            break;
        case "u":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "у";
            break;
        case "u̯":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ў";
            break;
        case "f":
            o = h.miakki != 0 ? "ф'" : "ф";
            break;
        case "x":
            o = h.miakki != 0 ? "х'" : "х";
            break;
        case "t͡s":
            o = h.miakki != 0 ? "ц'" : "ц";
            break;
        case "t͡ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ч";
            break;
        case "ʂ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ш";
            break;
        case "ɨ":
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ы";
            break;
        case "j":
            // ён заўсёды аднолькавы
            o = "й";
            break;
        default:
            throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public boolean is(String bazavy, Integer miakkasc, Boolean padvajennie, Integer padziel) {
        if (!bazavyHuk.equals(bazavy)) {
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
