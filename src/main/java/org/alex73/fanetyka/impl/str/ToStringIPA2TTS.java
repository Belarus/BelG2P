package org.alex73.fanetyka.impl.str;

import java.util.Map;

import org.alex73.fanetyka.impl.Huk;

/**
 * Канвертавання гукаў у String для IPA, у варыянце для Text-to-Speech.
 * 
 * Адрозненні ад звычайнага IPA вываду:
 * 
 * - гук не пазначаецца як падвоены на мяжы слоў
 */
public class ToStringIPA2TTS extends ToStringBase {
    public static final Map<Huk.POUNY_HUK, String> TTS_IPA_MAP = loadOutputMap("out_ipa_tts.txt");

    @Override
    protected String huk2str(Huk h) {
        return TTS_IPA_MAP.get(h.pouny());
    }

    @Override
    protected char getStressCharBefore() {
        return ToStringIPA.IPA_STRESS_CHAR;
    }

    @Override
    protected char getStressCharAfter() {
        return 0;
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
