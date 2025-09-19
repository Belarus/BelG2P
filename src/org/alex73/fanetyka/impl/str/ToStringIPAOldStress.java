package org.alex73.fanetyka.impl.str;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.IPAUtils;

/**
 * Канвертавання гукаў у String для IPA з націскам пасля галоснай, а не як у
 * стандартным IPA - перад складам.
 */
public class ToStringIPAOldStress extends ToStringBase {
    @Override
    protected String huk2str(Huk h) {
        return IPAUtils.huk2ipa(h).name();
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
