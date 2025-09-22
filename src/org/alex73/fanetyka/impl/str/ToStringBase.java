package org.alex73.fanetyka.impl.str;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.utils.ReadResource;

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
                if (out.charAt(out.length() - 1) == ' ') {
                    // калі падваенне вакол прагала - прагала не мусіць быць наогул
                    out.setCharAt(out.length() - 1, getPadvojenyChar());
                } else {
                    out.append(getPadvojenyChar());
                }
            } else {
                out.append(hukStr);
            }
            prevHukStr = hukStr;
            if (getStressChar() != 0 && h.stress) {
                out.append(getStressChar());
            }
            if ((h.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0) {
                out.append(' ');
            }
        }

        // адкідаем прагал у канцы - нельга рабіць гэта пры канвертацыі слоў, бо тады не
        // будзе спрацоўваць праверка на канец слова
        return out.toString().trim();
    }

    /**
     * Чытае з рэсурса адпаведнасць гукаў.
     */
    static Map<Huk.POUNY_HUK, String> loadOutputMap(String resourceName) {
        Map<Huk.POUNY_HUK, String> map = new HashMap<>();
        Properties props = ReadResource.readProperties(ToStringBase.class, resourceName);
        props.stringPropertyNames().forEach(key -> {
            String value = props.getProperty(key);
            Huk.POUNY_HUK h;
            try {
                h = Huk.POUNY_HUK.valueOf(key);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Няма такога гуку '" + key + "' з " + resourceName);
            }
            map.put(h, value);
        });
        for (Huk.POUNY_HUK o : Huk.POUNY_HUK.values()) {
            if (!map.containsKey(o)) {
                throw new RuntimeException("Няма радку для гуку '" + o.name() + "' у " + resourceName);
            }
        }
        return map;
    }
}
