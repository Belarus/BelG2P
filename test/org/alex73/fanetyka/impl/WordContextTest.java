package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Consumer;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;
import org.alex73.grammardb.structures.Form;
import org.alex73.grammardb.structures.Paradigm;
import org.alex73.grammardb.structures.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WordContextTest {

    Consumer<String> logger = (msg) -> System.out.println(msg);
    GrammarFinder emptyFinder;

    @BeforeEach
    void setUp() {
        emptyFinder = mock(GrammarFinder.class);
        when(emptyFinder.getParadigms(anyString())).thenReturn(new Paradigm[0]);
    }

    @Test
    void testFanetykaBazy() {
        GrammarFinder mockFinder = mock(GrammarFinder.class);
        when(mockFinder.getFan("тэст")).thenReturn("ttt");

        WordContext wc = new WordContext(mockFinder, "тэст", null, logger);

        assertEquals(3, wc.huki.size());
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(0).bazavyHuk);
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(1).bazavyHuk);
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(2).bazavyHuk);
    }

    @Test
    void testStandardizesApostrophesAndStress() {
        String input = "не́’";
        WordContext wc = new WordContext(emptyFinder, input, null, logger);
        assertEquals("не́’".replace('́', GrammarDB2.pravilny_nacisk).replace('’', GrammarDB2.pravilny_apostraf), wc.word);
    }

    @Test
    void testJakannieUPrynazounikach1() {
        WordContext next1a = new WordContext(emptyFinder, "ё´н", null, logger);
        WordContext wca = new WordContext(emptyFinder, "не", next1a, logger);
        assertEquals("ня", wca.word);
        assertTrue((wca.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testJakannieUPrynazounikach2() {
        WordContext next2b = new WordContext(emptyFinder, "і´м", null, logger);
        WordContext next1b = new WordContext(emptyFinder, "з", next2b, logger);
        WordContext wcb = new WordContext(emptyFinder, "не", next1b, logger);
        assertEquals("ня", wcb.word);
        assertTrue((wcb.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testJakannieUPrynazounikach3() {
        WordContext next2c = new WordContext(emptyFinder, "яго´", null, logger);
        WordContext next1c = new WordContext(emptyFinder, "з", next2c, logger);
        WordContext wcc = new WordContext(emptyFinder, "не", next1c, logger);
        assertEquals("не", wcc.word);
        assertTrue((wcc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testJakannieUPrynazounikach4() {
        WordContext next1d = new WordContext(emptyFinder, "яго´", null, logger);
        WordContext wcd = new WordContext(emptyFinder, "без", next1d, logger);
        assertEquals("без", wcd.word);
        assertTrue((wcd.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testJakannieUPrynazounikach5() {
        WordContext next1e = new WordContext(emptyFinder, "вё´скі", null, logger);
        WordContext wce = new WordContext(emptyFinder, "без", next1e, logger);
        assertEquals("бяз", wce.word);
        assertTrue((wce.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testPrynazouniki1() {
        WordContext next1 = new WordContext(emptyFinder, "яго", null, logger);
        WordContext wc = new WordContext(emptyFinder, "з", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testPrynazouniki2() {
        WordContext next1 = new WordContext(emptyFinder, "яго", null, logger);
        WordContext wc = new WordContext(emptyFinder, "праз", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testPrynazouniki3() {
        WordContext next1 = new WordContext(emptyFinder, "яго", null, logger);
        WordContext wc = new WordContext(emptyFinder, "ад", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) == 0);
    }

    @Test
    void testNaciskNaPiersySklad() {
        WordContext next2a = new WordContext(emptyFinder, "і´м", null, logger);
        WordContext next1a = new WordContext(emptyFinder, "з", next2a, logger);
        assertTrue(next1a.naciskNaPiersySklad());

        WordContext next2b = new WordContext(emptyFinder, "яго´", null, logger);
        WordContext next1b = new WordContext(emptyFinder, "з", next2b, logger);
        assertFalse(next1b.naciskNaPiersySklad());
    }

    @Test
    void testMiakkasc() {
        WordContext wc = new WordContext(emptyFinder, "ль", null, logger);
        assertEquals(1, wc.huki.size());
        assertEquals(Huk.BAZAVY_HUK.л, wc.huki.get(0).bazavyHuk);
        assertEquals(Huk.MIAKKASC_PAZNACANAJA, wc.huki.get(0).miakki);
    }

    @Test
    void testAutamatycnyjaNaciski() {
        assertEquals("яно+".replace('+', '\u0301'), new WordContext(emptyFinder, "яно", null, logger).word);
        assertEquals("ё+лкі".replace('+', '\u0301'), new WordContext(emptyFinder, "ёлкі", null, logger).word);
    }

    @Test
    void testZBazyUKarotki() {
        // калі няма ў базе
        WordContext wcn = new WordContext(emptyFinder, "ўяўны", null, logger);
        assertEquals("ўяўны", wcn.word);

        // калі ёсць у базе
        Form f = new Form();
        f.setValue("уя+ўны".replace('+', '\u0301'));
        Variant v = new Variant();
        v.getForm().add(f);
        Paradigm p = new Paradigm();
        p.getVariant().add(v);

        GrammarDB2 db = GrammarDB2.empty();
        db.getAllParadigms().add(p);
        GrammarFinder finder = new GrammarFinder(db);

        WordContext wcy = new WordContext(finder, "ўяўны", null, logger);
        assertEquals("ўя+ўны".replace('+', '\u0301'), wcy.word);
    }

    private void checkDebug(List<WordContext> words, String... debugs) {
        assertEquals(words.size(), debugs.length);
        for (int i = 0; i < words.size(); i++) {
            List<Huk> huki = words.get(i).huki;
            assertEquals(huki.size(), debugs[i].length());
            StringBuilder real = new StringBuilder();
            for (int j = 0; j < huki.size(); j++) {
                real.append(huki.get(j).debug ? '+' : '-');
            }
            assertEquals(debugs[i], real.toString());
        }
    }

    @Test
    public void debugInsideOne() throws Exception {
        List<WordContext> words = List.of(new WordContext(emptyFinder, "н(ов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-++-");
    }

    @Test
    public void debugStartOne() throws Exception {
        List<WordContext> words = List.of(new WordContext(emptyFinder, "(нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "+++-");
    }

    @Test
    public void debugEndOne() throws Exception {
        List<WordContext> words = List.of(new WordContext(emptyFinder, "н(овы)", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++");
    }

    @Test
    public void debugTwo() throws Exception {
        List<WordContext> words = List.of(new WordContext(emptyFinder, "н(овы", null, logger), new WordContext(emptyFinder, "нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++", "+++-");
    }

    @Test
    public void debugThree() throws Exception {
        List<WordContext> words = List.of(new WordContext(emptyFinder, "н(овы", null, logger), new WordContext(emptyFinder, "новы", null, logger),
                new WordContext(emptyFinder, "нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++", "++++", "+++-");
    }

    @Test
    public void empty() throws Exception {
        new WordContext(emptyFinder, "", null, logger);
    }
}
