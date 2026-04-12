package org.alex73.fanetyka.impl.str;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.str.ToStringIPA.HukChars;
import org.junit.jupiter.api.Test;

public class ToStringIPATest {
    @Test
    void letters() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.в);
        Huk h3 = new Huk("", Huk.BAZAVY_HUK.г);
        Huk h4 = new Huk("", Huk.BAZAVY_HUK.а);
        Huk h5 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("bʋɣab", new ToStringIPA().toString(List.of(h1, h2, h3, h4, h5)));
    }

    @Test
    void doubleNoSpace() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("bː", new ToStringIPA().toString(List.of(h1, h2)));
    }

    @Test
    void doubleAroundSpace() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        h1.padzielPasla = Huk.PADZIEL_SLOVY;
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("bː", new ToStringIPA().toString(List.of(h1, h2)));
    }

    @Test
    void syllBordersFromStartText() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(0, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromStartWord() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorder() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderNoHal() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0, Huk.PADZIEL_PRYSTAUKA)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderRoots() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0, Huk.PADZIEL_KARANI)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(2, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderPryn() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0, Huk.PADZIEL_PRYNAZOUNIK)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromHal() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }
}
