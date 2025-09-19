package org.alex73.fanetyka.impl.str;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.IPAUtils;
import org.alex73.fanetyka.utils.ReadResource;

/**
 * Канвертавання гукаў у String для IPA.
 * 
 * У IPA націскі трэба падаваць перад складам, а не над галоснай. Але для гэтага
 * трэба вызначыць межы складаў. Шыблоны пазначэння націскаў -- у
 * ipa_stress.txt. Але ёсць цяжкія выпадкі - падваенне гукаў і межы паміж
 * прыстаўкай і каранямі. Мяжа пасля прыстаўкі не ўлічваецца, калі ў прыстаўцы
 * няма галоснай.
 */
public class ToStringIPA extends ToStringBase {
    private static final char IPA_STRESS_CHAR = 'ˈ';

    /**
     * Асобнае канвертаванне ў string з падзелам на склады.
     */
    public String toString(List<Huk> huki) {
        // папярэдняя канвертацыя
        List<HukChars> hs = new ArrayList<>(huki.stream().map(h -> new HukChars(h)).toList());

        // падваенне
        for (int i = 1; i < hs.size(); i++) {
            HukChars p = hs.get(i - 1);
            HukChars c = hs.get(i);
            if (p.zycny && c.zycny && p.str.equals(c.str)) {
                p.str += getPadvojenyChar();
                p.stress |= c.stress;
                p.spaceAfter = c.spaceAfter;
                // пераносім padzielPasla на папярэднні
                if (i > 1) {
                    hs.get(i - 2).padzielPasla |= p.padzielPasla;
                    p.padzielPasla = 0;
                }
                p.padzielPasla |= c.padzielPasla;
                hs.remove(i);
                i--;
            }
        }

        // для ўсіх націскаў шукаем пачатак складу і ставім там ipaStressBefore
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < hs.size(); i++) {
            HukChars h = hs.get(i);
            if (h.stress) {
                // шукаем пачатак складу
                int syllStart = getSyllStart(hs, i);
                pattern.setLength(0);
                for (int p = syllStart; p <= i; p++) {
                    pattern.append(hs.get(p).tp.name());
                }
                Integer stressPos = IPA_STRESSES.get(pattern.toString());
                if (stressPos == null) {
                    System.err.println("No IPA stress pattern for '" + pattern + "'");
                } else {
                    hs.get(syllStart + stressPos).ipaStressBefore = true;
                }
            }
        }

        StringBuilder out = new StringBuilder();
        for (HukChars h : hs) {
            if (h.ipaStressBefore) {
                out.append(IPA_STRESS_CHAR);
            }
            out.append(h.str);
            if (h.spaceAfter) {
                out.append(' ');
            }
        }
        return out.toString();
    }

    @Override
    public String huk2str(Huk h) {
        return IPAUtils.huk2ipa(h).name();
    }

    @Override
    protected char getStressChar() {
        return 0;
    }

    @Override
    protected char getPadvojenyChar() {
        return 'ː';
    }

    /**
     * Вызначае межы складу вакол патрэбнай галоснай.
     */
    protected int getSyllStart(List<HukChars> hs, int halIndex) {
        // шукаем пачатак складу
        int syllStart = 0;
        int syllStartAfterPadziel = -1;
        for (int i = halIndex - 1; i >= 0; i--) {
            HukChars t = hs.get(i);
            if (t.spaceAfter || (t.padzielPasla & (Huk.PADZIEL_KARANI | Huk.PADZIEL_ZLUCOK)) != 0) {
                syllStart = i + 1;
                break;
            }
            if (t.tp == TYPE.H) {
                if (syllStartAfterPadziel >= 0) {
                    syllStart = syllStartAfterPadziel;
                } else {
                    syllStart = i; // перад складам ідзе папярэдні галосны
                }
                break;
            }
            if ((t.padzielPasla & Huk.PADZIEL_PRYSTAUKA) != 0) {
                // тут, калі знойдзем галосны да прагалу
                syllStartAfterPadziel = i + 1;
            }
        }
        return syllStart;
    }

    enum TYPE {
        J, H, S, Z
    };

    public class HukChars {
        boolean ipaStressBefore, spaceAfter, stress;
        int padzielPasla;
        boolean zycny;
        String str;
        TYPE tp;

        public HukChars(Huk huk) {
            str = huk2str(huk);
            stress = huk.stress;
            spaceAfter = (huk.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0;
            padzielPasla = huk.padzielPasla;
            zycny = !Huk.halosnyja.contains(huk.bazavyHuk);

            if (huk.bazavyHuk == BAZAVY_HUK.ў || huk.bazavyHuk == BAZAVY_HUK.j || huk.bazavyHuk == BAZAVY_HUK.р) {
                tp = TYPE.J;
            } else if (Huk.halosnyja.contains(huk.bazavyHuk)) {
                tp = TYPE.H;
            } else if (Huk.sanornyja.contains(huk.bazavyHuk)) {
                tp = TYPE.S;
            } else {
                tp = TYPE.Z;
            }
        }
    }

    private static final Map<String, Integer> IPA_STRESSES = new HashMap<>();

    static {
        ReadResource.readLines(ToStringIPA.class, "ipa_stress.txt").forEach(s -> {
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
            if (IPA_STRESSES.put(s, p) != null) {
                throw new RuntimeException("Duplicate line '" + s + "' in the ipa_stress.txt");
            }
        });
    }
}
