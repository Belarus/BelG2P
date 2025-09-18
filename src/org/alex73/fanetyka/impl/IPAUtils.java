package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.str.ToStringIPA;

/**
 * Handles stress placement using the IPA standard, placing stress before the
 * syllable.
 */
public class IPAUtils {

    public static final Map<String, Integer> IPA_STRESSES;

    public enum IPA {
        a, ɐ, b, bʲ, v, vʲ, β, βʲ, ɣ, ɣʲ, g, gʲ, d, dʲ, ɛ, d͡ʐ, d͡z, d͡zʲ, ʐ, z, zʲ, i, k, kʲ, ɫ, lʲ, m, mʲ, ɱ, n, nʲ, ɔ, p, pʲ, r, s, sʲ, t, tʲ, u, u̯, f, fʲ,
        x, xʲ, t͡s, t͡sʲ, t͡ʂ, ʂ, ɨ, ə, j
    }

    public static IPA huk2ipa(Huk h) {
        switch (h.bazavyHuk) {
        case а:
            return h.redukavany ? IPA.ɐ : IPA.a;
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
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.d͡ʐ;
        case дз:
            return h.miakki == 0 ? IPA.d͡z : IPA.d͡zʲ;
        case ж:
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
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
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
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
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
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
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
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
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.t͡ʂ;
        case ш:
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return IPA.ʂ;
        case ы:
            if (h.miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + h.bazavyHuk);
            }
            return h.redukavany ? IPA.ə : IPA.ɨ;
        case j:
            if (h.miakki != Huk.MIAKKASC_PAZNACANAJA) {
                throw new RuntimeException("мусіць быць мяккі: " + h.bazavyHuk);
            }
            // ён заўсёды мяккі
            return IPA.j;
        }
        throw new RuntimeException("Невядомы базавы гук: " + h.bazavyHuk);
    }

    public static class ParseIpaContext {
        public String fan;
        public boolean stress = false;

        public ParseIpaContext(String fan) {
            this.fan = fan;
        }
    }

    public static List<Huk> parseIpa(String fan) { // TODO check ё, ю, я
        ParseIpaContext context = new ParseIpaContext(fan);
        List<Huk> huki = new ArrayList<>();
        while (!context.fan.isEmpty()) {
            String s = context.fan;
            Huk huk;
            if (s.startsWith("u̯")) {
                huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ў);
            } else if (s.startsWith("d͡ʐ")) {
                huk = new Huk(s.substring(0, 3), BAZAVY_HUK.дж);
            } else if (s.startsWith("d͡zʲ")) {
                huk = new Huk(s.substring(0, 4), BAZAVY_HUK.дз);
                huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
            } else if (s.startsWith("d͡z")) {
                huk = new Huk(s.substring(0, 3), BAZAVY_HUK.дз);
            } else if (s.startsWith("t͡ʂ")) {
                huk = new Huk(s.substring(0, 3), BAZAVY_HUK.ч);
            } else if (s.startsWith("t͡sʲ")) {
                huk = new Huk(s.substring(0, 4), BAZAVY_HUK.ц);
                huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
            } else if (s.startsWith("t͡s")) {
                huk = new Huk(s.substring(0, 3), BAZAVY_HUK.ц);
            } else {
                char c1 = s.length() > 1 ? s.charAt(1) : 0;
                switch (s.charAt(0)) {
                case 'ˈ':
                    context.stress = true;
                    context.fan = context.fan.substring(1);
                    continue;
                case 'a':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.а);
                    break;
                case 'ɐ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.а);
                    huk.redukavany = true;
                    break;
                case 'b':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.б);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.б);
                    }
                    break;
                case 'v':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.в);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
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
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.г);
                    }
                    break;
                case 'g':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ґ);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ґ);
                    }
                    break;
                case 'd':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.д);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.д);
                    }
                    break;
                case 'ɛ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.э);
                    break;
                case 'ʐ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ж);
                    break;
                case 'z':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.з);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.з);
                    }
                    break;
                case 'i':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.і);
                    break;
                case 'k':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.к);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.к);
                    }
                    break;
                case 'ɫ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.л);
                    break;
                case 'l':
                    if (c1 != 'ʲ' && !Huk.SKIP_ERRORS) {
                        throw new RuntimeException("Няправільнае аднаўленне гуку: " + s);
                    }
                    huk = new Huk(s.substring(0, 2), BAZAVY_HUK.л);
                    huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    break;
                case 'm':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.м);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
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
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.н);
                    }
                    break;
                case 'ɔ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.о);
                    break;
                case 'p':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.п);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
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
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.с);
                    }
                    break;
                case 't':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.т);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.т);
                    }
                    break;
                case 'u':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.у);
                    break;
                case 'f':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.ф);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ф);
                    }
                    break;
                case 'x':
                    if (c1 == 'ʲ') {
                        huk = new Huk(s.substring(0, 2), BAZAVY_HUK.х);
                        huk.miakki = Huk.MIAKKASC_ASIMILACYJNAJA;
                    } else {
                        huk = new Huk(s.substring(0, 1), BAZAVY_HUK.х);
                    }
                    break;
                case 'ʂ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ш);
                    break;
                case 'ɨ':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ы);
                    break;
                case 'ə':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.ы);
                    huk.redukavany = true;
                    break;
                case 'j':
                    huk = new Huk(s.substring(0, 1), BAZAVY_HUK.j);
                    huk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                    break;
                default:
                    throw new RuntimeException("Невядомы гук: " + s);
                }
            }
            huki.add(huk);
            if (context.stress && Huk.halosnyja.contains(huk.bazavyHuk)) {
                huk.stress = true;
                context.stress = false;
            }
            String sipa = new ToStringIPA().huk2str(huk);
            context.fan = context.fan.substring(sipa.length());

            char cafter = context.fan.length() > 0 ? context.fan.charAt(0) : 0;
            if (cafter == 'ː') {
                huki.add(huk);
                huk.zychodnyjaLitary = huk.zychodnyjaLitary + "ː";
                context.fan = context.fan.substring(1);
                sipa += cafter;
            }

            if (!s.startsWith(sipa) || !sipa.equals(huk.zychodnyjaLitary)) {
                throw new RuntimeException("Няправільнае аднаўленне гуку: " + new ToStringIPA().huk2str(huk) + " => " + s);
            }

            char cspace = context.fan.length() > 0 ? context.fan.charAt(0) : 0;
            if (cspace == ' ') {
                huk.padzielPasla |= Huk.PADZIEL_SLOVY;
                context.fan = context.fan.substring(1);
                sipa += cspace;
            }
        }
        if (context.stress) {
            throw new RuntimeException("Няправільнае аднаўленне націску: " + fan);
        }
        return huki;
    }

    /**
     * Set stress using IPA standard - before syllable. It required to define
     * syllables borders.
     */
    public static void setIpaStress(List<Huk> huki, Fanetyka3 parent) {
        int hal = 0;
        for (int i = 0; i < huki.size(); i++) {
            Huk h = huki.get(i);
            if (h.stress) {
                setIpaStress(huki, hal, i, parent);
            }
            if (Huk.halosnyja.contains(h.bazavyHuk)) {
                hal = i;
            }
            if ((h.padzielPasla & Huk.PADZIEL_SLOVY) != 0 || (h.padzielPasla & Huk.PADZIEL_KARANI) != 0) {
                hal = i + 1;
            }
        }
    }

    /*
     * Націскі ў IPA: ставіцца перад складам.
     */
    private static void setIpaStress(List<Huk> huki, int prevHalIndex, int halIndex, Fanetyka3 parent) {
        // huki.get(halIndex).stress = false;
        StringBuilder s = new StringBuilder();
        for (int i = prevHalIndex; i <= halIndex; i++) {
            Huk h = huki.get(i);
            if (h.bazavyHuk == BAZAVY_HUK.ў || h.bazavyHuk == BAZAVY_HUK.j || h.bazavyHuk == BAZAVY_HUK.р) {
                s.append('J');
            } else if (Huk.halosnyja.contains(h.bazavyHuk)) {
                s.append('H');
            } else if (Huk.sanornyja.contains(h.bazavyHuk)) {
                s.append('S');
            } else {
                s.append("Z");
            }
        }
        int pierad;
        switch (s.toString()) {
        case "H":
        case "JH":
        case "JSH":
        case "SH":
        case "SSH":
        case "SZZH":
        case "ZH":
        case "ZJH":
        case "ZSH":
        case "ZSJH":
        case "ZZH":
        case "ZZJH":
        case "ZZSH":
        case "ZZZH":
            pierad = 0;
            break;
        case "HH":
        case "HJH":
        case "HJJH":
        case "HSH":
        case "HZH":
        case "HZJH":
        case "HZSH":
        case "HZSSH":
        case "HZZH":
        case "HZZJH":
        case "HZZSH":
        case "HZZZH":
        case "HZZZJH":
        case "HZZZSH":
            pierad = 1;
            break;
        case "HJSH":
        case "HJSJH":
        case "HJSSH":
        case "HJZH":
        case "HJZJH":
        case "HJZSH":
        case "HJZZH":
        case "HJZZJH":
        case "HJZZSH":
        case "HSJH":
        case "HSSH":
        case "HSZH":
        case "HSZJH":
        case "HSZSH":
        case "HSZZH":
        case "HSZZJH":
        case "HSZZSH":
        case "HSZZZH":
        case "HZSJH":
        case "HJZZZH":
            pierad = 2;
            break;
        default:
            System.err.println("Незразумелая мяжа складаў у мадэлі '" + s + "' для слоў " + parent.inputWords + "/" + parent);
            pierad = -1;
            break;
        }
        if (pierad >= 0) {
            huki.get(prevHalIndex + pierad).stressIpa = true;
        }
    }

    static {
        Map<String, Integer> ipaStresses = new HashMap<>();
        WordInitialConverter.readResourceLines("ipa_stress.txt").forEach(s -> {
            if (s.isBlank()) {
                return;
            }
            int p = s.indexOf('ˈ');
            if (p < 0) {
                throw new RuntimeException("No stress in '" + s + "' in the ipa_stress.txt");
            }
            s = s.substring(0, p) + s.substring(p + 1);
            if (!s.matches("[HZJS]+")) {
                throw new RuntimeException("Wrong line '" + s + "' in the ipa_stress.txt");
            }
            if (ipaStresses.put(s, p) != null) {
                throw new RuntimeException("Duplicate line '" + s + "' in the ipa_stress.txt");
            }
        });
        IPA_STRESSES = Collections.unmodifiableMap(ipaStresses);
    }
}
