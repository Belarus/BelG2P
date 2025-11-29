package org.alex73.fanetyka.impl.str;

import java.util.Map;

import org.alex73.fanetyka.impl.Huk;

/**
 * Канвертавання гукаў у String для фармату арфаэпічнага слоўніка.
 */
public class ToStringArfaep extends ToStringBase {
    private static final Map<Huk.POUNY_HUK, String> ARFAEP_MAP = loadOutputMap("out_arfaep.txt");

    @Override
    protected char getStressChar() {
        return 0;
    }

    @Override
    protected char getPadvojenyChar() {
        return ':';
    }

    @Override
    public String huk2str(Huk huk) {
        return ARFAEP_MAP.get(huk.pouny());
    }
}
