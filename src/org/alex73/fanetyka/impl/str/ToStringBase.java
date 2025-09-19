package org.alex73.fanetyka.impl.str;

import java.util.List;

import org.alex73.fanetyka.impl.Huk;

/**
 * Базавы клас для канвертавання гукаў у String для розных гукавых сістэм.
 */
public abstract class ToStringBase {
    abstract protected String huk2str(Huk huk);

    abstract protected char getStressChar();

    abstract protected char getPadvojenyChar();

    public String toString(List<Huk> huki) {
        StringBuilder out = new StringBuilder();
        String prevHukStr = null;
        for (Huk h : huki) {
            String hukStr = huk2str(h);
            if (!Huk.halosnyja.contains(h.bazavyHuk) && hukStr.equals(prevHukStr)) {
                out.append(getPadvojenyChar());
            } else {
                out.append(hukStr);
            }
            prevHukStr = hukStr;
            if ((h.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0) {
                out.append(' ');
            }
        }
        return out.toString();
    }
}
