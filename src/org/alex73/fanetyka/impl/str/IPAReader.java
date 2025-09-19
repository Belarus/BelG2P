package org.alex73.fanetyka.impl.str;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

/**
 * Чытае IPA і пераўтварае ва ўнутранае прадстаўленне.
 */
public class IPAReader {
    static private List<IpaHuk> ipa2huk = new ArrayList<>();

    private static Huk p2h(String letters, Huk.POUNY_HUK p) {
        switch (p) {
        case а:
            return new Huk(letters, BAZAVY_HUK.а);
        case ɐ:
            Huk ha = new Huk(letters, BAZAVY_HUK.а);
            ha.redukavany = true;
            return ha;
        case б:
            return new Huk(letters, BAZAVY_HUK.б);
        case бь:
            return new Huk(letters, BAZAVY_HUK.б, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case в:
            return new Huk(letters, BAZAVY_HUK.в);
        case вь:
            return new Huk(letters, BAZAVY_HUK.в, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case β:
            return new Huk(letters, BAZAVY_HUK.β);
        case βь:
            return new Huk(letters, BAZAVY_HUK.β, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case г:
            return new Huk(letters, BAZAVY_HUK.г);
        case гь:
            return new Huk(letters, BAZAVY_HUK.г, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case ґ:
            return new Huk(letters, BAZAVY_HUK.ґ);
        case ґь:
            return new Huk(letters, BAZAVY_HUK.ґ, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case д:
            return new Huk(letters, BAZAVY_HUK.д);
        case дь:
            return new Huk(letters, BAZAVY_HUK.д, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case э:
            return new Huk(letters, BAZAVY_HUK.э);
        case дж:
            return new Huk(letters, BAZAVY_HUK.дж);
        case дз:
            return new Huk(letters, BAZAVY_HUK.дз);
        case дзь:
            return new Huk(letters, BAZAVY_HUK.дз, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case ж:
            return new Huk(letters, BAZAVY_HUK.ж);
        case з:
            return new Huk(letters, BAZAVY_HUK.з);
        case зь:
            return new Huk(letters, BAZAVY_HUK.з, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case і:
            return new Huk(letters, BAZAVY_HUK.і);
        case j:
            return new Huk(letters, BAZAVY_HUK.j, Huk.MIAKKASC_PAZNACANAJA, 0);
        case к:
            return new Huk(letters, BAZAVY_HUK.к);
        case кь:
            return new Huk(letters, BAZAVY_HUK.к, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case л:
            return new Huk(letters, BAZAVY_HUK.л);
        case ль:
            return new Huk(letters, BAZAVY_HUK.л, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case м:
            return new Huk(letters, BAZAVY_HUK.м);
        case мь:
            return new Huk(letters, BAZAVY_HUK.м, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case ɱ:
            return new Huk(letters, BAZAVY_HUK.ɱ);
        case н:
            return new Huk(letters, BAZAVY_HUK.н);
        case нь:
            return new Huk(letters, BAZAVY_HUK.н, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case о:
            return new Huk(letters, BAZAVY_HUK.о);
        case п:
            return new Huk(letters, BAZAVY_HUK.п);
        case пь:
            return new Huk(letters, BAZAVY_HUK.п, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case р:
            return new Huk(letters, BAZAVY_HUK.р);
        case с:
            return new Huk(letters, BAZAVY_HUK.с);
        case сь:
            return new Huk(letters, BAZAVY_HUK.с, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case т:
            return new Huk(letters, BAZAVY_HUK.т);
        case ть:
            return new Huk(letters, BAZAVY_HUK.т, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case у:
            return new Huk(letters, BAZAVY_HUK.у);
        case ў:
            return new Huk(letters, BAZAVY_HUK.ў);
        case ф:
            return new Huk(letters, BAZAVY_HUK.ф);
        case фь:
            return new Huk(letters, BAZAVY_HUK.ф, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case х:
            return new Huk(letters, BAZAVY_HUK.х);
        case хь:
            return new Huk(letters, BAZAVY_HUK.х, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case ц:
            return new Huk(letters, BAZAVY_HUK.ц);
        case ць:
            return new Huk(letters, BAZAVY_HUK.ц, Huk.MIAKKASC_ASIMILACYJNAJA, 0);
        case ч:
            return new Huk(letters, BAZAVY_HUK.ч);
        case ш:
            return new Huk(letters, BAZAVY_HUK.ш);
        case ы:
            return new Huk(letters, BAZAVY_HUK.ы);
        case ə:
            Huk h = new Huk(letters, BAZAVY_HUK.ы);
            h.redukavany = true;
            return h;
        default:
            throw new RuntimeException("Невядомы гук: " + p.name());
        }
    }

    public static List<Huk> parseIpa(String orig) { // TODO check ё, ю, я
        String s = orig;
        boolean ipaStress = false;
        List<Huk> huki = new ArrayList<>();
        Huk prevHuk = null;
        while (!s.isEmpty()) {
            if (s.startsWith(" ")) { // націск перад складам
                if (prevHuk != null) {
                    prevHuk.padzielPasla |= Huk.PADZIEL_SLOVY;
                }
                s = s.substring(1);
                continue;
            } else if (s.startsWith("ˈ")) { // націск перад складам
                ipaStress = true;
                s = s.substring(1);
                continue;
            } else if (s.startsWith("ː")) { // падваенне
                if (prevHuk == null) {
                    throw new RuntimeException("Падваенне гуку без папярэдняга гуку: " + orig);
                }
                Huk h = new Huk(prevHuk.zychodnyjaLitary, prevHuk.bazavyHuk, prevHuk.miakki, prevHuk.padzielPasla);
                h.redukavany = prevHuk.redukavany;
                h.stress = prevHuk.stress;
                huki.add(h);
                s = s.substring(1);
                continue;
            }

            // шукаем з якога IPA гуку пачынаецца радок
            String ff = s;
            IpaHuk line = ipa2huk.stream().filter(ih -> ff.startsWith(ih.ipa)).findFirst()
                    .orElseThrow(() -> new RuntimeException("Няправільнае аднаўленне гуку: " + orig));

            Huk huk = p2h(line.ipa, line.pouny);
            s = s.substring(line.ipa.length());
            huki.add(huk);

            if (ipaStress && Huk.halosnyja.contains(huk.bazavyHuk)) {
                huk.stress = true;
                ipaStress = false;
            }
            prevHuk = huk;
        }
        if (ipaStress) {
            throw new RuntimeException("Няправільнае аднаўленне націску: " + orig);
        }
        return huki;
    }

    record IpaHuk(String ipa, Huk.POUNY_HUK pouny) {
    }

    /**
     * Чытаем IPA_MAP і робім інверсны спіс - мапінг з IPA ў фанетыку.
     */
    static {
        ToStringIPA.IPA_MAP.forEach((k, v) -> {
            ipa2huk.add(new IpaHuk(v, k));
        });
        Collections.sort(ipa2huk, (a, b) -> Integer.compare(b.ipa.length(), a.ipa.length()));
    }
}
