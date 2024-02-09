package org.alex73.fanetyka.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
    public void testApostraf() throws Exception {
        c.apostraf = Case.MODE.ERROR;
        shouldPass();
        c.apostraf = Case.MODE.DONT_CARE;
        shouldPass();
        c.apostraf = Case.MODE.YES;
        shouldNotPass();
        c.apostraf = Case.MODE.NO;
        shouldPass();

        h.apostrafPasla = true;

        c.apostraf = Case.MODE.ERROR;
        shouldBeError();
        c.apostraf = Case.MODE.DONT_CARE;
        shouldPass();
        c.apostraf = Case.MODE.YES;
        shouldPass();
        c.apostraf = Case.MODE.NO;
        shouldNotPass();
    }

    @Test
    public void testPadvojeny() throws Exception {
        c.padvojeny = Case.MODE.ERROR;
        shouldPass();
        c.padvojeny = Case.MODE.DONT_CARE;
        shouldPass();
        c.padvojeny = Case.MODE.YES;
        shouldNotPass();
        c.padvojeny = Case.MODE.NO;
        shouldPass();

        h.padvojeny = true;

        c.padvojeny = Case.MODE.ERROR;
        shouldBeError();
        c.padvojeny = Case.MODE.DONT_CARE;
        shouldPass();
        c.padvojeny = Case.MODE.YES;
        shouldPass();
        c.padvojeny = Case.MODE.NO;
        shouldNotPass();
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
    }
}
