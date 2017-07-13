package org.alex73.korpus.voice;

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
        String o;
        switch (bazavyHuk) {
        case "a":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "b":
            o = miakki ? "bʲ" : "b";
            break;
        case "v":
            o = miakki ? "vʲ" : "v";
            break;
        case "β":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "ɣ":
            o = miakki ? "ʝ" : "ɣ";
            break;
        case "g":
            o = miakki ? "ɟ" : "g";
            break;
        case "d":
            o = miakki ? "dʲ" : "d";
            break;
        case "ɛ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "d͡ʐ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "d͡z":
            o = miakki ? "d͡zʲ" : "d͡z";
            break;
        case "ʐ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "z":
            o = miakki ? "zʲ" : "z";
            break;
        case "i":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "k":
            o = miakki ? "c" : "k";
            break;
        case "l":
            o = miakki ? "ʎ" : "l";
            break;
        case "m":
            o = miakki ? "mʲ" : "m";
            break;
        case "ɱ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "n":
            o = miakki ? "ɲ" : "n";
            break;
        case "ɔ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "p":
            o = miakki ? "pʲ" : "p";
            break;
        case "r":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "s":
            o = miakki ? "sʲ" : "s";
            break;
        case "t":
            o = miakki ? "tʲ" : "t";
            break;
        case "u":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "u̯":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "f":
            o = miakki ? "fʲ" : "f";
            break;
        case "x":
            o = miakki ? "ç" : "x";
            break;
        case "t͡s":
            o = miakki ? "t͡sʲ" : "t͡s";
            break;
        case "t͡ʂ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "ʂ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "ɨ":
            if (miakki) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            o = bazavyHuk;
            break;
        case "j":
            // ён заўсёды аднолькавы
            o = bazavyHuk;
            break;
        default:
            throw new RuntimeException("Невядомы базавы гук: " + bazavyHuk);
        }
        if (padvojeny) {
            o += "ː";
        }
        return o;
    }

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
        if (padziel != null && (padziel.intValue() & padzielPasla) == 0) {
            return false;
        }
        return true;
    }
}
