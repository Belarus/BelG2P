package org.alex73.fanetyka.impl.str;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.alex73.fanetyka.impl.Huk;
import org.junit.jupiter.api.Test;

public class ToStringIPA2TTSTest {
    @Test
    void stress() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.в);
        Huk h3 = new Huk("", Huk.BAZAVY_HUK.г);
        Huk h4 = new Huk("", Huk.BAZAVY_HUK.а);
        h4.stress = true;
        Huk h5 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("bʋɣaˈb", new ToStringIPA2TTS().toString(List.of(h1, h2, h3, h4, h5)));
    }

    @Test
    void doubleNoSpace() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("bː", new ToStringIPA2TTS().toString(List.of(h1, h2)));
    }

    @Test
    void doubleAroundSpace() {
        Huk h1 = new Huk("", Huk.BAZAVY_HUK.б);
        h1.padzielPasla = Huk.PADZIEL_SLOVY;
        Huk h2 = new Huk("", Huk.BAZAVY_HUK.б);
        assertEquals("b b", new ToStringIPA2TTS().toString(List.of(h1, h2)));
    }
}
