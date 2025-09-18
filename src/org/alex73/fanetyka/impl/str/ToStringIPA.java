package org.alex73.fanetyka.impl.str;

import java.util.List;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.IPAUtils;

/**
 * Канвертавання гукаў у String для IPA.
 */
public class ToStringIPA extends ToStringBase {
    @Override
    public String huk2str(Huk h) {
        return IPAUtils.huk2ipa(h).name();
    }

    @Override
    protected char getIpaStressChar() {
        return 'ˈ';
    }

    @Override
    protected char getStressChar() {
        return 0;
    }

    @Override
    protected char getPadvojenyChar() {
        return 'ː';
    }

    @Override
    protected void applyIPAstresses(List<HukChars> hs) {
        int hal = 0;
        for (int i = 0; i < hs.size(); i++) {
            HukChars h = hs.get(i);
            if (h.stressAfter) {
                applyIPAstresses(hs, hal, i);
            }
            if (h.tp == ToStringIPA.TYPE.H) {
                hal = i;
            }
            if (h.padzielSkladau) {
                hal = i + 1;
            }
        }
    }

    private static void applyIPAstresses(List<HukChars> huki, int prevHalIndex, int halIndex) {
        // huki.get(halIndex).stress = false;
        StringBuilder s = new StringBuilder();
        for (int i = prevHalIndex; i <= halIndex; i++) {
            HukChars h = huki.get(i);
            s.append(h.tp.name());
        }
        Integer pierad = IPAUtils.IPA_STRESSES.get(s.toString());
        if (pierad == null) {
            System.err.println("Незразумелая мяжа складаў у мадэлі '" + s + "' для " + huki);
        } else {
            huki.get(prevHalIndex + pierad).ipaStressBefore = true;
        }
    }
}
