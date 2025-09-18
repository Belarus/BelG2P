package org.alex73.fanetyka.impl.str;

import java.util.HashMap;
import java.util.Map;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.IPAUtils;
import org.alex73.fanetyka.impl.IPAUtils.IPA;

/**
 * Канвертавання гукаў у String для фармату арфаэпічнага слоўніка.
 */
public class ToStringArfaep extends ToStringBase {
    static final Map<IPA, String> ARFAEP_MAP = new HashMap<>();
    static {
        ARFAEP_MAP.putAll(ToStringSkolny.SKOLNY_MAP);
        ARFAEP_MAP.put(IPA.ɣ, "γ");
        ARFAEP_MAP.put(IPA.ɣʲ, "γ'");
        ARFAEP_MAP.put(IPA.g, "g");
        ARFAEP_MAP.put(IPA.gʲ, "g'");
        ARFAEP_MAP.put(IPA.d͡ʐ, "ž");
        ARFAEP_MAP.put(IPA.d͡z, "z");
        ARFAEP_MAP.put(IPA.d͡zʲ, "z'");
        ARFAEP_MAP.put(IPA.ɐ, "а");
        ARFAEP_MAP.put(IPA.ə, "ы'");
    }

    @Override
    protected String huk2str(Huk h) {
        return ARFAEP_MAP.get(IPAUtils.huk2ipa(h));
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
        return ':';
    }
}
