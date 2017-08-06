package org.alex73.korpus.voice;

import java.util.function.Function;

public class Huk {
    public static int PADZIEL_PRYSTAUKA = 1;
    public static int PADZIEL_KORANI = 2;
    public static int PADZIEL_SLOVY = 4;

    public String zychodnyjaLitary;
    public String bazavyHuk;
    public boolean miakki;
    public boolean padvojeny;
    public boolean halosnaja, miakkajaHalosnaja;
    public boolean apostrafPasla;
    /** Гэты гук павінен быць аддзелены ад наступнага, бо гэта апошні гук прыстаўкі. */
    public int padzielPasla;

    public Huk(String z, String b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }

    @Override
    public String toString() {
        return ipa.apply(this);
    }

    public static Function<Huk,String> ipa= h-> {
        String o;
        switch (h.bazavyHuk) {
        case "a":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "b":
            o = h.miakki ? "bʲ" : "b";
            break;
        case "v":
            o = h.miakki ? "vʲ" : "v";
            break;
        case "β":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ɣ":
            o = h.miakki ? "ʝ" : "ɣ";
            break;
        case "g":
            o = h.miakki ? "ɟ" : "g";
            break;
        case "d":
            o = h.miakki ? "dʲ" : "d";
            break;
        case "ɛ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "d͡ʐ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "d͡z":
            o = h.miakki ? "d͡zʲ" : "d͡z";
            break;
        case "ʐ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "z":
            o = h.miakki ? "zʲ" : "z";
            break;
        case "i":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "k":
            o = h.miakki ? "c" : "k";
            break;
        case "l":
            o = h.miakki ? "ʎ" : "l";
            break;
        case "m":
            o = h.miakki ? "mʲ" : "m";
            break;
        case "ɱ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "n":
            o = h.miakki ? "ɲ" : "n";
            break;
        case "ɔ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "p":
            o = h.miakki ? "pʲ" : "p";
            break;
        case "r":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "s":
            o = h.miakki ? "sʲ" : "s";
            break;
        case "t":
            o = h.miakki ? "tʲ" : "t";
            break;
        case "u":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "u̯":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "f":
            o = h.miakki ? "fʲ" : "f";
            break;
        case "x":
            o = h.miakki ? "ç" : "x";
            break;
        case "t͡s":
            o = h.miakki ? "t͡sʲ" : "t͡s";
            break;
        case "t͡ʂ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ʂ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = h.bazavyHuk;
            break;
        case "ɨ":
            if (h.miakki) {
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

    public static Function<Huk,String> arfaep= h-> {
        String o;
        switch (h.bazavyHuk) {
        case "a":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "а";
            break;
        case "b":
            o = h.miakki ? "б'" : "б";
            break;
        case "v":
            o = h.miakki ? "в'" : "в";
            break;
        case "β":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "в";
            break;
        case "ɣ":
            o = h.miakki ? "γ'" : "γ";
            break;
        case "g":
            o = h.miakki ? "g'" : "g";
            break;
        case "d":
            o = h.miakki ? "д'" : "д";
            break;
        case "ɛ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "э";
            break;
        case "d͡ʐ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ž";
            break;
        case "d͡z":
            o = h.miakki ? "z'" : "z";
            break;
        case "ʐ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ж";
            break;
        case "z":
            o = h.miakki ? "з'" : "з";
            break;
        case "i":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "і";
            break;
        case "k":
            o = h.miakki ? "к'" : "к";
            break;
        case "l":
            o = h.miakki ? "л'" : "л";
            break;
        case "m":
            o = h.miakki ? "м'" : "м";
            break;
        case "ɱ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "м";
            break;
        case "n":
            o = h.miakki ? "н'" : "н";
            break;
        case "ɔ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "о";
            break;
        case "p":
            o = h.miakki ? "п'" : "п";
            break;
        case "r":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "р";
            break;
        case "s":
            o = h.miakki ? "с'" : "с";
            break;
        case "t":
            o = h.miakki ? "т'" : "т";
            break;
        case "u":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "у";
            break;
        case "u̯":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ў";
            break;
        case "f":
            o = h.miakki ? "ф'" : "ф";
            break;
        case "x":
            o = h.miakki ? "х'" : "х";
            break;
        case "t͡s":
            o = h.miakki ? "ц'" : "ц";
            break;
        case "t͡ʂ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ч";
            break;
        case "ʂ":
            if (h.miakki) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            o = "ш";
            break;
        case "ɨ":
            if (h.miakki) {
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

    public boolean is(String bazavy, Boolean miakkasc, Boolean padvajennie, Integer padziel) {
        if (!bazavyHuk.equals(bazavy)) {
            return false;
        }
        if (miakkasc != null && miakkasc.booleanValue() != miakki) {
            return false;
        }
        if (padvajennie != null && padvajennie.booleanValue() != padvojeny) {
            return false;
        }
        if (padziel != null) {
            int pp=padziel.intValue();
            if (pp==0) {
                return padzielPasla==0;
            }
            return (pp & padzielPasla) != 0;
        }
        return true;
    }
}
