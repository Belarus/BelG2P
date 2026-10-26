package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class FanetykaConfigTest {
    @Test
    public void testProcessCasesMatchTsvConfigs() {
        assertDoesNotThrow(() -> new FanetykaConfig(null));
    }
}
