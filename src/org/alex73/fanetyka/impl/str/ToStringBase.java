package org.alex73.fanetyka.impl.str;

import java.util.ArrayList;
import java.util.List;

import org.alex73.fanetyka.impl.Huk;

/**
 * Базавы клас для канвертавання гукаў у String для розных гукавых сістэм.
 */
public abstract class ToStringBase {
    abstract protected String huk2str(Huk huk);

    abstract protected char getIpaStressChar();

    abstract protected char getStressChar();

    abstract protected char getPadvojenyChar();

    public String toString(List<Huk> huki) {
        // папярэдняя канвертацыя
        List<HukChars> hs = new ArrayList<>(huki.stream().map(h -> new HukChars(h)).toList());

        // падваенне
        for (int i = 1; i < hs.size(); i++) {
            HukChars p = hs.get(i - 1);
            HukChars c = hs.get(i);
            if (p.zycny && c.zycny && p.str.equals(c.str)) {
                p.str += getPadvojenyChar();
                p.ipaStressBefore |= c.ipaStressBefore;
                p.stressAfter |= c.stressAfter;
                p.spaceAfter = c.spaceAfter;
                hs.remove(i);
                i--;
            }
        }

        // вывад у адзін радок
        hs.getLast().spaceAfter = false;
        StringBuilder out = new StringBuilder();
        for (HukChars h : hs) {
            if (h.ipaStressBefore && getIpaStressChar() != 0) {
                out.append(getIpaStressChar());
            }
            out.append(h.str);
            if (h.stressAfter && getStressChar() != 0) {
                out.append(getStressChar());
            }
            if (h.spaceAfter) {
                out.append(' ');
            }
        }
        return out.toString();
    }

    public class HukChars {
        boolean ipaStressBefore, stressAfter, spaceAfter;
        boolean zycny;
        String str;

        public HukChars(Huk huk) {
            str = huk2str(huk);
            ipaStressBefore = huk.stressIpa;
            stressAfter = huk.stress;
            spaceAfter = (huk.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0;
            zycny = !Huk.halosnyja.contains(huk.bazavyHuk);
        }
    }
}
