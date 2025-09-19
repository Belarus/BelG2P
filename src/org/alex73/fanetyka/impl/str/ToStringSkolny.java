package org.alex73.fanetyka.impl.str;

import java.util.Map;

import org.alex73.fanetyka.impl.Huk;

/**
 * Канвертавання гукаў у String для школьнай натацыі.
 * 
 * Combining Double Inverted Breve - U+0361
 */
public class ToStringSkolny extends ToStringBase {
    private static final Map<Huk.POUNY_HUK, String> SKOLNY_MAP = loadOutputMap("out_skolny.txt");

    @Override
    protected char getStressChar() {
        return '\u0301';
    }

    @Override
    protected char getPadvojenyChar() {
        return ':';
    }

    @Override
    public String huk2str(Huk huk) {
        return SKOLNY_MAP.get(huk.pouny());
    }
}
