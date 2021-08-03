package org.alex73.fanetyka.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Huk {
    public static int PADZIEL_PRYSTAUKA = 1;
    public static int PADZIEL_KORANI = 2;
    public static int PADZIEL_SLOVY = 4;
    public static int PADZIEL_MINUS = 8;
    public static int MIAKKASC_ASIMILACYJNAJA = 1;
    public static int MIAKKASC_PAZNACANAJA = 2;
    public static int MASK_MIAKKASC_USIE = 3;

    public enum BAZAVY_HUK {
        a, b, v, β, ɣ, g, d, ɛ, d͡ʐ, d͡z, ʐ, z, i, k, ɫ, m, ɱ, n, ɔ, p, r, s, t, u, u̯, f, x, t͡s, t͡ʂ, ʂ, ɨ, j
    }

    public enum IPA {
        a, b, bʲ, v, vʲ, β, ɣ, ɣʲ, g, gʲ, d, dʲ, ɛ, d͡ʐ, d͡z, d͡zʲ, ʐ, z, zʲ, i, k, kʲ, ɫ, lʲ, m, mʲ, ɱ, n, nʲ, ɔ, p,
        pʲ, r, s, sʲ, t, tʲ, u, u̯, f, fʲ, x, xʲ, t͡s, t͡sʲ, t͡ʂ, ʂ, ɨ, j
    }

    public List<IPA> sanornyja = Arrays.asList(IPA.r, IPA.ɫ, IPA.lʲ, IPA.m, IPA.mʲ, IPA.n, IPA.nʲ, IPA.v, IPA.vʲ, IPA.ɱ,
            IPA.u̯, IPA.j);

    public String zychodnyjaLitary;
    public BAZAVY_HUK bazavyHuk;
    public int miakki;
    public boolean padvojeny;
    public boolean halosnaja, miakkajaHalosnaja;
    public boolean apostrafPasla;
    public boolean stressIpa;
    public boolean stress; // TODO: націск у падваеннях і спрашчэннях
    /** Гэты гук павінен быць аддзелены ад наступнага, бо гэта апошні гук прыстаўкі. */
    public int padzielPasla;

    static final Map<IPA, String> ARFAEP_MAP = new HashMap<>();
    static final Map<IPA, String> SKOLNY_MAP = new HashMap<>();

    static {
        initMaps();
    }

    public Huk(String z, BAZAVY_HUK b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }

    /**
     * Пазначае асіміляцыйную мяккасць ці цвёрдасць. Не павінен канфліктаваць з пазначанай.
     */
    public void setMiakkasc(boolean m) {
        if (miakki == MIAKKASC_PAZNACANAJA && bazavyHuk == BAZAVY_HUK.ɨ) {
            if (!m) {
                throw new RuntimeException("Спроба асіміляцыйнай цвёрдасці ў пазначаным мяккім");
            }
        } else {
            miakki = m ? MIAKKASC_ASIMILACYJNAJA : 0;
        }
    }

    public boolean isSanorny() {
        IPA ipa = ipa_enum.apply(this);
        return sanornyja.contains(ipa);
    }

    @Override
    public String toString() {
        return skolny.apply(this);
    }

    public static Function<Huk, IPA> ipa_enum = h -> {
        switch (h.bazavyHuk) {
        case a:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.a;
        case b:
            return h.miakki == 0 ? IPA.b : IPA.bʲ;
        case v:
            return h.miakki == 0 ? IPA.v : IPA.vʲ;
        case β:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.β;
        case ɣ:
            return h.miakki == 0 ? IPA.ɣ : IPA.ɣʲ;
        case g:
            return h.miakki == 0 ? IPA.g : IPA.gʲ;
        case d:
            return h.miakki == 0 ? IPA.d : IPA.dʲ;
        case ɛ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɛ;
        case d͡ʐ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.d͡ʐ;
        case d͡z:
            return h.miakki == 0 ? IPA.d͡z : IPA.d͡zʲ;
        case ʐ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ʐ;
        case z:
            return h.miakki == 0 ? IPA.z : IPA.zʲ;
        case i:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.i;
        case k:
            return h.miakki == 0 ? IPA.k : IPA.kʲ;
        case ɫ:
            return h.miakki == 0 ? IPA.ɫ : IPA.lʲ;
        case m:
            return h.miakki == 0 ? IPA.m : IPA.mʲ;
        case ɱ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɱ;
        case n:
            return h.miakki == 0 ? IPA.n : IPA.nʲ;
        case ɔ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɔ;
        case p:
            return h.miakki == 0 ? IPA.p : IPA.pʲ;
        case r:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.r;
        case s:
            return h.miakki == 0 ? IPA.s : IPA.sʲ;
        case t:
            return h.miakki == 0 ? IPA.t : IPA.tʲ;
        case u:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.u;
        case u̯:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.u̯;
        case f:
            return h.miakki == 0 ? IPA.f : IPA.fʲ;
        case x:
            return h.miakki == 0 ? IPA.x : IPA.xʲ;
        case t͡s:
            return h.miakki == 0 ? IPA.t͡s : IPA.t͡sʲ;
        case t͡ʂ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.t͡ʂ;
        case ʂ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ʂ;
        case ɨ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɨ;
        case j:
            // ён заўсёды аднолькавы
            return IPA.j;
        }
        throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
    };

    static Huk parseIpa(ParseIpaContext context) { // TODO check ё, ю, я
        String s = context.fan;
        Huk huk;
        if (s.startsWith("u̯")) {
            huk = new Huk(s.substring(0, 2), BAZAVY_HUK.u̯);
        } else if (s.startsWith("d͡ʐ")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.d͡ʐ);
        } else if (s.startsWith("d͡zʲ")) {
            huk = new Huk(s.substring(0, 4), BAZAVY_HUK.d͡z);
            huk.miakki = MIAKKASC_PAZNACANAJA;
        } else if (s.startsWith("d͡z")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.d͡z);
        } else if (s.startsWith("t͡ʂ")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.t͡ʂ);
        } else if (s.startsWith("t͡sʲ")) {
            huk = new Huk(s.substring(0, 4), BAZAVY_HUK.t͡s);
            huk.miakki = MIAKKASC_PAZNACANAJA;
        } else if (s.startsWith("t͡s")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.t͡s);
        } else {
            char c1;
            try {
                c1 = s.charAt(1);
            } catch (StringIndexOutOfBoundsException ex) {
                c1 = 0;
            }
            switch (s.charAt(0)) {
            case 'ˈ':
                context.stress = true;
                context.fan = context.fan.substring(1);
                return null;
            case 'a':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.a);
                huk.halosnaja = true;
                break;
            case 'b':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.b);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.b);
                }
                break;
            case 'v':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.v);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.v);
                }
                break;
            case 'β':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.β);
                break;
            case 'ɣ':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ɣ);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɣ);
                }
                break;
            case 'g':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.g);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.g);
                }
                break;
            case 'd':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.d);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.d);
                }
                break;
            case 'ɛ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɛ);
                huk.halosnaja = true;
                huk.miakkajaHalosnaja = true;
                break;
            case 'ʐ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ʐ);
                break;
            case 'z':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.z);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.z);
                }
                break;
            case 'i':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.i);
                huk.halosnaja = true;
                huk.miakkajaHalosnaja = true;
                break;
            case 'k':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.k);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.k);
                }
                break;
            case 'ɫ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɫ);
                break;
            case 'l':
                if (c1 != 'ʲ') {
                    throw new RuntimeException("Няправільнае аднаўленне гуку: " + s);
                }
                huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ɫ);
                huk.miakki = MIAKKASC_PAZNACANAJA;
                break;
            case 'm':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.m);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.m);
                }
                break;
            case 'ɱ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɱ);
                break;
            case 'n':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.n);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.n);
                }
                break;
            case 'ɔ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɔ);
                huk.halosnaja = true;
                break;
            case 'p':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.p);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.p);
                }
                break;
            case 'r':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.r);
                break;
            case 's':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.s);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.s);
                }
                break;
            case 't':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.t);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.t);
                }
                break;
            case 'u':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.u);
                huk.halosnaja = true;
                break;
            case 'f':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.f);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.f);
                }
                break;
            case 'x':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.x);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.x);
                }
                break;
            case 'ʂ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ʂ);
                break;
            case 'ɨ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɨ);
                huk.halosnaja = true;
                break;
            case 'j':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.j);
                break;
            default:
                throw new RuntimeException("Невядомы гук: " + s);
            }
        }
        if (context.stress && huk.halosnaja) {
            huk.stress = true;
            context.stress = false;
        }
        String sipa = ipa.apply(huk);
        if (!s.startsWith(sipa) || !sipa.equals(huk.zychodnyjaLitary)) {
            throw new RuntimeException("Няправільнае аднаўленне гуку: " + ipa.apply(huk) + " => " + s);
        }
        context.fan = context.fan.substring(sipa.length());
        return huk;
    }

    static class ParseIpaContext {
        String fan;
        boolean stress = false;

        public ParseIpaContext(String fan) {
            this.fan = fan;
        }
    }

    static void initMaps() {
        SKOLNY_MAP.put(IPA.a, "а");
        SKOLNY_MAP.put(IPA.b, "б");
        SKOLNY_MAP.put(IPA.bʲ, "б'");
        SKOLNY_MAP.put(IPA.v, "в");
        SKOLNY_MAP.put(IPA.vʲ, "в'");
        SKOLNY_MAP.put(IPA.β, "в");
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
        String o = ipa_enum.apply(h).name().toLowerCase();
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
        String o = ipa_enum.apply(h).name().toLowerCase();
        if (h.stressIpa) {
            //o = 'ˈ' + o;
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
        String o = ARFAEP_MAP.get(ipa_enum.apply(h));
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
        String o = ARFAEP_MAP.get(ipa_enum.apply(h));
        if (h.stressIpa) {
            //o = 'ˈ' + o;
        }
        if (h.stress) {
            //o += '´';
        }
        if (h.padvojeny) {
            o += ":";
        }
        return o;
    };

    public static Function<Huk, String> skolny = h -> {
        String o = SKOLNY_MAP.get(ipa_enum.apply(h));
        if (h.stressIpa) {
            //o = 'ˈ' + o;
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
