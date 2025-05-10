package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.junit.jupiter.api.Test;

class IPAUtilsTest {

    @Test
    void parseIpaHandlesValidInput() {
        List<Huk> result = IPAUtils.parseIpa("d͡ʐa");
        assertEquals(2, result.size());
        assertEquals(BAZAVY_HUK.дж, result.get(0).bazavyHuk);
        assertEquals(BAZAVY_HUK.а, result.get(1).bazavyHuk);
    }

    @Test
    void parseIpaThrowsOnInvalidStress() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            IPAUtils.parseIpa("ˈ");
        });
        assertTrue(exception.getMessage().contains("Няправільнае аднаўленне націску"));
    }

    @Test
    void parseIpaHandlesSoftConsonants() {
        List<Huk> result = IPAUtils.parseIpa("dʲa");
        assertEquals(2, result.size());
        assertEquals(BAZAVY_HUK.д, result.get(0).bazavyHuk);
        assertEquals(Huk.MIAKKASC_PAZNACANAJA, result.get(0).miakki);
        assertEquals(BAZAVY_HUK.а, result.get(1).bazavyHuk);
    }

    @Test
    void parseIpaHandlesLong() {
        List<Huk> result = IPAUtils.parseIpa("dː");
        assertEquals(1, result.size());
        assertEquals(BAZAVY_HUK.д, result.get(0).bazavyHuk);
        assertTrue(result.get(0).padvojeny);
    }

    @Test
    void parseIpaHandlesEmptyInput() {
        List<Huk> result = IPAUtils.parseIpa("");
        assertTrue(result.isEmpty());
    }

    @Test
    void setIpaStressAppliesStressCorrectly() {
        List<Huk> huki = IPAUtils.parseIpa("ˈa");
        IPAUtils.setIpaStress(huki, null);
        assertTrue(huki.get(0).stressIpa);
    }

    @Test
    void setIpaStressHandlesMultipleSyllables() {
        List<Huk> huki = IPAUtils.parseIpa("ˈaba");
        IPAUtils.setIpaStress(huki, null);
        assertTrue(huki.get(0).stressIpa);
        assertFalse(huki.get(1).stressIpa);
        assertFalse(huki.get(2).stressIpa);
    }

    @Test
    void ipaEnumHandlesKnownHuk() {
        Huk huk = new Huk("a", BAZAVY_HUK.а);
        IPAUtils.IPA result = IPAUtils.ipa_enum.apply(huk);
        assertEquals(IPAUtils.IPA.a, result);
    }
}
