package org.alex73.fanetyka.impl.str;

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
}
