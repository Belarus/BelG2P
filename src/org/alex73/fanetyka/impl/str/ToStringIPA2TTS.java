package org.alex73.fanetyka.impl.str;

import org.alex73.fanetyka.impl.Huk;

/**
 * Канвертавання гукаў у String для IPA, у варыянце для Text-to-Speech.
 * 
 * Адрозненні ад звычайнага IPA вываду:
 * 
 * - гук не пазначаецца як падвоены на мяжы слоў
 */
public class ToStringIPA2TTS extends ToStringBase {
    @Override
    protected String huk2str(Huk h) {
        return ToStringIPA.IPA_MAP.get(h.pouny());
    }

    @Override
    protected char getStressChar() {
        return ToStringIPA.IPA_STRESS_CHAR;
    }

    @Override
    protected boolean doubleAroundSpace() {
        return false;
    }

    @Override
    protected char getPadvojenyChar() {
        return 'ː';
    }
}
