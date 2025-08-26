package org.alex73.fanetyka.impl.str;

import java.util.HashMap;
import java.util.Map;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.IPAUtils;
import org.alex73.fanetyka.impl.IPAUtils.IPA;

/**
 * Канвертавання гукаў у String для школьнай натацыі.
 */
public class ToStringSkolny extends ToStringBase {
    static final Map<IPA, String> SKOLNY_MAP = new HashMap<>();
    static {
        SKOLNY_MAP.put(IPA.a, "а");
        SKOLNY_MAP.put(IPA.b, "б");
        SKOLNY_MAP.put(IPA.bʲ, "б'");
        SKOLNY_MAP.put(IPA.v, "в");
        SKOLNY_MAP.put(IPA.vʲ, "в'");
        SKOLNY_MAP.put(IPA.β, "в");
        SKOLNY_MAP.put(IPA.βʲ, "в'");
        SKOLNY_MAP.put(IPA.ɣ, "г");
        SKOLNY_MAP.put(IPA.ɣʲ, "г'");
        SKOLNY_MAP.put(IPA.g, "ѓ");
        SKOLNY_MAP.put(IPA.gʲ, "ѓ'");
        SKOLNY_MAP.put(IPA.d, "д");
        SKOLNY_MAP.put(IPA.dʲ, "д'");
        SKOLNY_MAP.put(IPA.ɛ, "э");
        SKOLNY_MAP.put(IPA.d͡ʐ, "дж");
        SKOLNY_MAP.put(IPA.d͡z, "дз");
        SKOLNY_MAP.put(IPA.d͡zʲ, "дз'");
        SKOLNY_MAP.put(IPA.ʐ, "ж");
        SKOLNY_MAP.put(IPA.z, "з");
        SKOLNY_MAP.put(IPA.zʲ, "з'");
        SKOLNY_MAP.put(IPA.i, "і");
        SKOLNY_MAP.put(IPA.k, "к");
        SKOLNY_MAP.put(IPA.kʲ, "к'");
        SKOLNY_MAP.put(IPA.ɫ, "л");
        SKOLNY_MAP.put(IPA.lʲ, "л'");
        SKOLNY_MAP.put(IPA.m, "м");
        SKOLNY_MAP.put(IPA.mʲ, "м'");
        SKOLNY_MAP.put(IPA.ɱ, "м");
        SKOLNY_MAP.put(IPA.n, "н");
        SKOLNY_MAP.put(IPA.nʲ, "н'");
        SKOLNY_MAP.put(IPA.ɔ, "о");
        SKOLNY_MAP.put(IPA.p, "п");
        SKOLNY_MAP.put(IPA.pʲ, "п'");
        SKOLNY_MAP.put(IPA.r, "р");
        SKOLNY_MAP.put(IPA.s, "с");
        SKOLNY_MAP.put(IPA.sʲ, "с'");
        SKOLNY_MAP.put(IPA.t, "т");
        SKOLNY_MAP.put(IPA.tʲ, "т'");
        SKOLNY_MAP.put(IPA.u, "у");
        SKOLNY_MAP.put(IPA.u̯, "ў");
        SKOLNY_MAP.put(IPA.f, "ф");
        SKOLNY_MAP.put(IPA.fʲ, "ф'");
        SKOLNY_MAP.put(IPA.x, "х");
        SKOLNY_MAP.put(IPA.xʲ, "х'");
        SKOLNY_MAP.put(IPA.t͡s, "ц");
        SKOLNY_MAP.put(IPA.t͡sʲ, "ц'");
        SKOLNY_MAP.put(IPA.t͡ʂ, "ч");
        SKOLNY_MAP.put(IPA.ʂ, "ш");
        SKOLNY_MAP.put(IPA.ɨ, "ы");
        SKOLNY_MAP.put(IPA.j, "й");
    }

    @Override
    public String huk2str(Huk h) {
        return SKOLNY_MAP.get(IPAUtils.huk2ipa(h));
    }

    @Override
    protected char getIpaStressChar() {
        return 0;
    }

    @Override
    protected char getStressChar() {
        return '\u0301';
    }

    @Override
    protected char getPadvojenyChar() {
        return ':';
    }
}
