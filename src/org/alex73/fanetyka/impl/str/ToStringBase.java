package org.alex73.fanetyka.impl.str;

import java.util.List;

import org.alex73.fanetyka.impl.Huk;

/**
 * Базавы клас для канвертавання гукаў у String для розных гукавых сістэм.
 */
public abstract class ToStringBase {
    abstract protected String huk2str(Huk huk);

    abstract protected char getIpaStressChar();

    abstract protected char getStressChar();

    abstract protected char getPadvojenyChar();

    public String toString(List<Huk> huki) {
        StringBuilder out = new StringBuilder();
        Huk prevHuk = null;
        String prevHukStr = "";
        for (Huk huk : huki) {
            String hukStr = huk2str(huk);
            if (prevHuk != null && prevHuk.stressIpa && getIpaStressChar() != 0) {
                out.append(getIpaStressChar());
            }
            out.append(prevHukStr);
            if (prevHuk != null && prevHuk.stress && getStressChar() != 0) {
                out.append(getStressChar());
            }
            if (!Huk.halosnyja.contains(huk.bazavyHuk) && hukStr.equals(prevHukStr)) {
                hukStr = Character.toString(getPadvojenyChar());
            } else if (prevHuk != null && (prevHuk.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0) {
                out.append(' ');
            }
            prevHuk = huk;
            prevHukStr = hukStr;
        }
        out.append(prevHukStr);
        return out.toString();
    }
}
