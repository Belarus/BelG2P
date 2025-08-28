package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.alex73.fanetyka.config.Case;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProcessRunnerTest {
    Case.HukCheck c;
    Huk h;

    @BeforeEach
    public void before() {
        c = new Case.HukCheck();
        h = new Huk("", BAZAVY_HUK.а);
    }

    void shouldPass() {
        assertNull(ProcessRunner.checkRules("", c, h));
    }

    void shouldNotPass() {
        assertNotNull(ProcessRunner.checkRules("", c, h));
    }

    void shouldBeError() {
        try {
            ProcessRunner.checkRules("", c, h);
            fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void testNacisk() throws Exception {
        c.nacisk = Case.MODE.ERROR;
        shouldPass();
        c.nacisk = Case.MODE.DONT_CARE;
        shouldPass();
        c.nacisk = Case.MODE.YES;
        shouldNotPass();
        c.nacisk = Case.MODE.NO;
        shouldPass();

        h.stress = true;

        c.nacisk = Case.MODE.ERROR;
        shouldBeError();
        c.nacisk = Case.MODE.DONT_CARE;
        shouldPass();
        c.nacisk = Case.MODE.YES;
        shouldPass();
        c.nacisk = Case.MODE.NO;
        shouldNotPass();
    }

    @Test
    public void testPadziely() throws Exception {
        shouldPass();

        c.pasziel.maskError = Huk.PADZIEL_SLOVY;
        shouldPass();
        c.pasziel.maskError = 0;

        c.pasziel.maskYes = Huk.PADZIEL_SLOVY;
        shouldNotPass();
        c.pasziel.maskYes = 0;

        c.pasziel.maskNo = Huk.PADZIEL_SLOVY;
        shouldPass();
        c.pasziel.maskNo = 0;

        h.padzielPasla = Huk.PADZIEL_KARANI;

        c.pasziel.maskError = Huk.PADZIEL_SLOVY;
        shouldPass();
        c.pasziel.maskError = 0;

        c.pasziel.maskYes = Huk.PADZIEL_SLOVY;
        shouldNotPass();
        c.pasziel.maskYes = 0;

        c.pasziel.maskNo = Huk.PADZIEL_SLOVY;
        shouldPass();
        c.pasziel.maskNo = 0;

        h.padzielPasla = Huk.PADZIEL_SLOVY;

        c.pasziel.maskError = Huk.PADZIEL_SLOVY;
        shouldBeError();
        c.pasziel.maskError = 0;

        c.pasziel.maskYes = Huk.PADZIEL_SLOVY;
        shouldPass();
        c.pasziel.maskYes = 0;

        c.pasziel.maskNo = Huk.PADZIEL_SLOVY;
        shouldNotPass();
        c.pasziel.maskNo = 0;

        h.padzielPasla = Huk.PADZIEL_APOSTRAF;

        c.pasziel.maskError = Huk.PADZIEL_APOSTRAF;
        shouldBeError();
        c.pasziel.maskError = 0;

        c.pasziel.maskYes = Huk.PADZIEL_APOSTRAF;
        shouldPass();
        c.pasziel.maskYes = 0;

        c.pasziel.maskNo = Huk.PADZIEL_APOSTRAF;
        shouldNotPass();
        c.pasziel.maskNo = 0;
    }
}
