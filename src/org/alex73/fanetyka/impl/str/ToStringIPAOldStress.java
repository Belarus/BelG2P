package org.alex73.fanetyka.impl.str;

import org.alex73.fanetyka.impl.Huk;

/**
 * Канвертавання гукаў у String для IPA з націскам пасля галоснай, а не як у
 * стандартным IPA - перад складам.
 */
public class ToStringIPAOldStress extends ToStringBase {
    @Override
    protected String huk2str(Huk h) {
        return ToStringIPA.IPA_MAP.get(h.pouny());
    }

    @Override
    protected char getStressChar() {
        return '´';
    }

    @Override
    protected char getPadvojenyChar() {
        return 'ː';
    }
}
