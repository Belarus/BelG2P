package org.alex73.fanetyka.impl.str;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.str.ToStringIPA.HukChars;
import org.junit.jupiter.api.Test;

public class ToStringIPATest {

    @Test
    void syllBordersFromStartText() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(0, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromStartWord() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorder() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderNoHal() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, Huk.PADZIEL_SLOVY)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, Huk.PADZIEL_PRYSTAUKA)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderRoots() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, Huk.PADZIEL_KARANI)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(2, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromBorderPryn() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, Huk.PADZIEL_PRYNAZOUNIK)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }

    @Test
    void syllBordersFromHal() {
        ToStringIPA c = new ToStringIPA();
        List<HukChars> hs = new ArrayList<>();
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.к, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.з, 0)));
        hs.add(c.new HukChars(new Huk("", Huk.BAZAVY_HUK.а, 0)));
        assertEquals(1, c.getSyllStart(hs, hs.size() - 1));
    }
}
