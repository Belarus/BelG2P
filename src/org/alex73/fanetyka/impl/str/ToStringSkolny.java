package org.alex73.fanetyka.impl.str;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.IPAUtils;
import org.alex73.fanetyka.impl.IPAUtils.IPA;

/**
 * Канвертавання гукаў у String для школьнай натацыі.
 * 
 * Combining Double Inverted Breve - U+0361
 */
public class ToStringSkolny extends ToStringBase {
    @Override
    public String huk2str(Huk h) {
        IPA ipa = IPAUtils.huk2ipa(h);
        return switch (ipa) {
        case IPA.a -> "а";
        case IPA.b -> "б";
        case IPA.bʲ -> "б'";
        case IPA.v -> "в";
        case IPA.vʲ -> "в'";
        case IPA.β -> "в";
        case IPA.βʲ -> "в'";
        case IPA.ɣ -> "г";
        case IPA.ɣʲ -> "г'";
        case IPA.g -> "ґ";
        case IPA.gʲ -> "ґ'";
        case IPA.d -> "д";
        case IPA.dʲ -> "д'";
        case IPA.ɛ -> "э";
        case IPA.d͡ʐ -> "д\u0361ж";
        case IPA.d͡z -> "д\u0361з";
        case IPA.d͡zʲ -> "д\u0361з'";
        case IPA.ʐ -> "ж";
        case IPA.z -> "з";
        case IPA.zʲ -> "з'";
        case IPA.i -> "і";
        case IPA.k -> "к";
        case IPA.kʲ -> "к'";
        case IPA.ɫ -> "л";
        case IPA.lʲ -> "л'";
        case IPA.m -> "м";
        case IPA.mʲ -> "м'";
        case IPA.ɱ -> "м";
        case IPA.n -> "н";
        case IPA.nʲ -> "н'";
        case IPA.ɔ -> "о";
        case IPA.p -> "п";
        case IPA.pʲ -> "п'";
        case IPA.r -> "р";
        case IPA.s -> "с";
        case IPA.sʲ -> "с'";
        case IPA.t -> "т";
        case IPA.tʲ -> "т'";
        case IPA.u -> "у";
        case IPA.u̯ -> "ў";
        case IPA.f -> "ф";
        case IPA.fʲ -> "ф'";
        case IPA.x -> "х";
        case IPA.xʲ -> "х'";
        case IPA.t͡s -> "ц";
        case IPA.t͡sʲ -> "ц'";
        case IPA.t͡ʂ -> "ч";
        case IPA.ʂ -> "ш";
        case IPA.ɨ -> "ы";
        case IPA.j -> "й";
        case IPA.ɐ -> "ӑ";// "\u2009\u2DF6";// 2DF6
        case IPA.ə -> "ъ";// "\u2009\uA679"; // A679
        };
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
