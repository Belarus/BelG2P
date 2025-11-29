package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.str.IPAReader;
import org.alex73.fanetyka.impl.str.ToStringIPA;
import org.junit.jupiter.api.Test;

public class IPAUtilsTest {

    @Test
    void parseIpaHandlesValidInput() {
        List<Huk> result = IPAReader.parseIpa("d͡ʐa");
        assertEquals(2, result.size());
        assertEquals(BAZAVY_HUK.дж, result.get(0).bazavyHuk);
        assertEquals(BAZAVY_HUK.а, result.get(1).bazavyHuk);
    }

    @Test
    void parseIpaThrowsOnInvalidStress() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            IPAReader.parseIpa("ˈ");
        });
        assertTrue(exception.getMessage().contains("Няправільнае аднаўленне націску"));
    }

    @Test
    void parseIpaHandlesSoftConsonants() {
        List<Huk> result = IPAReader.parseIpa("d\u032Aʲa");
        assertEquals(2, result.size());
        assertEquals(BAZAVY_HUK.д, result.get(0).bazavyHuk);
        assertEquals(Huk.MIAKKASC_ASIMILACYJNAJA, result.get(0).miakki);
        assertEquals(BAZAVY_HUK.а, result.get(1).bazavyHuk);
    }

    @Test
    void parseIpaHandlesLong() {
        List<Huk> result = IPAReader.parseIpa("d\u032Aː");
        assertEquals(2, result.size());
        assertEquals(BAZAVY_HUK.д, result.get(0).bazavyHuk);
        assertEquals(BAZAVY_HUK.д, result.get(1).bazavyHuk);
    }

    @Test
    void parseIpaHandlesEmptyInput() {
        List<Huk> result = IPAReader.parseIpa("");
        assertTrue(result.isEmpty());
    }

    @Test
    void ipaEnumHandlesKnownHuk() {
        Huk huk = new Huk("a", BAZAVY_HUK.а);
        String result = new ToStringIPA().huk2str(huk);
        assertEquals("a", result);
    }
}
