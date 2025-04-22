package org.alex73.fanetyka.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Huk {
    public static final int PADZIEL_PRYSTAUKA = 1; // прыстаўка
    public static final int PADZIEL_PRYNAZOUNIK = 32; // прыназоўнікі без, не, з, праз
    public static final int PADZIEL_KARANI = 2;
    public static final int PADZIEL_SUFIX = 4;
    public static final int PADZIEL_SLOVY = 8;
    public static final int PADZIEL_MINUS = 16;
    public static final int MIAKKASC_ASIMILACYJNAJA = 1;
    public static final int MIAKKASC_PAZNACANAJA = 2;
    public static final int MASK_MIAKKASC_USIE = 3;

    public enum BAZAVY_HUK {
        а, б, в, β, г, ґ, д, э, дж, дз, ж, з, і, к, л, м, ɱ, н, о, п, р, с, т, у, ў, ф, х, ц, ч, ш, ы, j
    }

    public enum IPA {
        a, b, bʲ, v, vʲ, β, βʲ, ɣ, ɣʲ, g, gʲ, d, dʲ, ɛ, d͡ʐ, d͡z, d͡zʲ, ʐ, z, zʲ, i, k, kʲ, ɫ, lʲ, m, mʲ, ɱ, n, nʲ, ɔ, p,
        pʲ, r, s, sʲ, t, tʲ, u, u̯, f, fʲ, x, xʲ, t͡s, t͡sʲ, t͡ʂ, ʂ, ɨ, j
    }

    public List<IPA> sanornyja = Arrays.asList(IPA.r, IPA.ɫ, IPA.lʲ, IPA.m, IPA.mʲ, IPA.n, IPA.nʲ, IPA.v, IPA.vʲ, IPA.ɱ,
            IPA.u̯, IPA.j);

    public String zychodnyjaLitary;
    public BAZAVY_HUK bazavyHuk;
    public int miakki;
    public boolean padvojeny;
    public boolean halosnaja;
    public boolean apostrafPasla;
    public boolean stressIpa;
    public boolean stress; // TODO: націск у падваеннях і спрашчэннях
    /** Гэты гук павінен быць аддзелены ад наступнага, бо гэта апошні гук прыстаўкі. */
    public int padzielPasla;

    public boolean debug; // debug not applied processes

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
        if (miakki == MIAKKASC_PAZNACANAJA && bazavyHuk == BAZAVY_HUK.ы) {
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

    public boolean isZvonki() {
        if (miakki != 0) {
            return bazavyHuk == BAZAVY_HUK.б || bazavyHuk == BAZAVY_HUK.д || bazavyHuk == BAZAVY_HUK.дз || bazavyHuk == BAZAVY_HUK.з
                    || bazavyHuk == BAZAVY_HUK.ґ || bazavyHuk == BAZAVY_HUK.г;
        } else {
            return bazavyHuk == BAZAVY_HUK.б || bazavyHuk == BAZAVY_HUK.д || bazavyHuk == BAZAVY_HUK.дз || bazavyHuk == BAZAVY_HUK.з
                    || bazavyHuk == BAZAVY_HUK.ж || bazavyHuk == BAZAVY_HUK.дж || bazavyHuk == BAZAVY_HUK.г || bazavyHuk == BAZAVY_HUK.ґ;
        }
    }

    public boolean isHluchi() {
        if (miakki != 0) {
            return bazavyHuk == BAZAVY_HUK.п || bazavyHuk == BAZAVY_HUK.ц || bazavyHuk == BAZAVY_HUK.с || bazavyHuk == BAZAVY_HUK.к || bazavyHuk == BAZAVY_HUK.х
                    || bazavyHuk == BAZAVY_HUK.ф;
        } else {
            return bazavyHuk == BAZAVY_HUK.п || bazavyHuk == BAZAVY_HUK.т || bazavyHuk == BAZAVY_HUK.ц || bazavyHuk == BAZAVY_HUK.с || bazavyHuk == BAZAVY_HUK.ш
                    || bazavyHuk == BAZAVY_HUK.ч || bazavyHuk == BAZAVY_HUK.х || bazavyHuk == BAZAVY_HUK.к || bazavyHuk == BAZAVY_HUK.ф;
        }
    }

    public boolean isSypiacy() {
//        if (miakki != 0) {
//            // небываюць мяккімі
//            return false;
//        }
        return bazavyHuk == BAZAVY_HUK.ш || bazavyHuk == BAZAVY_HUK.ж || bazavyHuk == BAZAVY_HUK.дж || bazavyHuk == BAZAVY_HUK.ч;
    }

    public boolean isSvisciacy() {
        return bazavyHuk == BAZAVY_HUK.с || bazavyHuk == BAZAVY_HUK.з || bazavyHuk == BAZAVY_HUK.дз || bazavyHuk == BAZAVY_HUK.ц;
    }

    @Override
    public String toString() {
        return skolny.apply(this);
    }

    public static Function<Huk, IPA> ipa_enum = h -> {
        switch (h.bazavyHuk) {
        case а:
            return IPA.a;
        case б:
            return h.miakki == 0 ? IPA.b : IPA.bʲ;
        case в:
            return h.miakki == 0 ? IPA.v : IPA.vʲ;
        case β:
            return h.miakki == 0 ? IPA.β : IPA.βʲ;
        case г:
            return h.miakki == 0 ? IPA.ɣ : IPA.ɣʲ;
        case ґ:
            return h.miakki == 0 ? IPA.g : IPA.gʲ;
        case д:
            return h.miakki == 0 ? IPA.d : IPA.dʲ;
        case э:
            return IPA.ɛ;
        case дж:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.d͡ʐ;
        case дз:
            return h.miakki == 0 ? IPA.d͡z : IPA.d͡zʲ;
        case ж:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ʐ;
        case з:
            return h.miakki == 0 ? IPA.z : IPA.zʲ;
        case і:
            return IPA.i;
        case к:
            return h.miakki == 0 ? IPA.k : IPA.kʲ;
        case л:
            return h.miakki == 0 ? IPA.ɫ : IPA.lʲ;
        case м:
            return h.miakki == 0 ? IPA.m : IPA.mʲ;
        case ɱ:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɱ;
        case н:
            return h.miakki == 0 ? IPA.n : IPA.nʲ;
        case о:
            return IPA.ɔ;
        case п:
            return h.miakki == 0 ? IPA.p : IPA.pʲ;
        case р:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.r;
        case с:
            return h.miakki == 0 ? IPA.s : IPA.sʲ;
        case т:
            return h.miakki == 0 ? IPA.t : IPA.tʲ;
        case у:
            return IPA.u;
        case ў:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.u̯;
        case ф:
            return h.miakki == 0 ? IPA.f : IPA.fʲ;
        case х:
            return h.miakki == 0 ? IPA.x : IPA.xʲ;
        case ц:
            return h.miakki == 0 ? IPA.t͡s : IPA.t͡sʲ;
        case ч:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.t͡ʂ;
        case ш:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ʂ;
        case ы:
            if (h.miakki != 0) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ɨ;
        case j:
            if (h.miakki != Huk.MIAKKASC_PAZNACANAJA) {
                //TODO throw new RuntimeException("мусіць быць мяккі: " + h.bazavyHuk);
            }
            // ён заўсёды мяккі
            return IPA.j;
        }
        throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
    };

    public static Huk parseIpa(ParseIpaContext context) { // TODO check ё, ю, я
        String s = context.fan;
        Huk huk;
        if (s.startsWith("u̯")) {
            huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ў);
        } else if (s.startsWith("d͡ʐ")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.дж);
        } else if (s.startsWith("d͡zʲ")) {
            huk = new Huk(s.substring(0, 4), BAZAVY_HUK.дз);
            huk.miakki = MIAKKASC_PAZNACANAJA;
        } else if (s.startsWith("d͡z")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.дз);
        } else if (s.startsWith("t͡ʂ")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.ч);
        } else if (s.startsWith("t͡sʲ")) {
            huk = new Huk(s.substring(0, 4), BAZAVY_HUK.ц);
            huk.miakki = MIAKKASC_PAZNACANAJA;
        } else if (s.startsWith("t͡s")) {
            huk = new Huk(s.substring(0, 3), BAZAVY_HUK.ц);
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
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.а);
                huk.halosnaja = true;
                break;
            case 'b':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.б);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.б);
                }
                break;
            case 'v':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.в);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.в);
                }
                break;
            case 'β':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.β);
                break;
            case 'ɣ':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.г);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.г);
                }
                break;
            case 'g':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ґ);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ґ);
                }
                break;
            case 'd':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.д);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.д);
                }
                break;
            case 'ɛ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.э);
                huk.halosnaja = true;
                break;
            case 'ʐ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ж);
                break;
            case 'z':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.з);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.з);
                }
                break;
            case 'i':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.і);
                huk.halosnaja = true;
                break;
            case 'k':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.к);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.к);
                }
                break;
            case 'ɫ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.л);
                break;
            case 'l':
                if (c1 != 'ʲ') {
                    throw new RuntimeException("Няправільнае аднаўленне гуку: " + s);
                }
                huk = new Huk(s.substring(0, 2), BAZAVY_HUK.л);
                huk.miakki = MIAKKASC_PAZNACANAJA;
                break;
            case 'm':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.м);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.м);
                }
                break;
            case 'ɱ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ɱ);
                break;
            case 'n':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.н);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.н);
                }
                break;
            case 'ɔ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.о);
                huk.halosnaja = true;
                break;
            case 'p':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.п);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.п);
                }
                break;
            case 'r':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.р);
                break;
            case 's':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.с);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.с);
                }
                break;
            case 't':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.т);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.т);
                }
                break;
            case 'u':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.у);
                huk.halosnaja = true;
                break;
            case 'f':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ф);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ф);
                }
                break;
            case 'x':
                if (c1 == 'ʲ') {
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.х);
                    huk.miakki = MIAKKASC_PAZNACANAJA;
                } else {
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.х);
                }
                break;
            case 'ʂ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ш);
                break;
            case 'ɨ':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ы);
                huk.halosnaja = true;
                break;
            case 'j':
                huk = new Huk(s.substring(0, 1), BAZAVY_HUK.j);
                huk.miakki = Huk.MIAKKASC_PAZNACANAJA;
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

    public static class ParseIpaContext {
        public String fan;
        public boolean stress = false;

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
