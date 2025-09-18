package org.alex73.fanetyka.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        WordInitialConverter wc = new WordInitialConverter(mockFinder, "тэст", null, logger);

        assertEquals(3, wc.huki.size());
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(0).bazavyHuk);
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(1).bazavyHuk);
        assertEquals(Huk.BAZAVY_HUK.т, wc.huki.get(2).bazavyHuk);
    }

    @Test
    void testStandardizesApostrophesAndStress() {
        String input = "не́’";
        WordInitialConverter wc = new WordInitialConverter(emptyFinder, input, null, logger);
        assertEquals("не́’".replace('́', GrammarDB2.pravilny_nacisk).replace('’', GrammarDB2.pravilny_apostraf), wc.word);
    }

    @Test
    void testPrynazouniki1() {
        WordInitialConverter next1 = new WordInitialConverter(emptyFinder, "яго", null, logger);
        WordInitialConverter wc = new WordInitialConverter(emptyFinder, "з", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testPrynazouniki2() {
        WordInitialConverter next1 = new WordInitialConverter(emptyFinder, "яго", null, logger);
        WordInitialConverter wc = new WordInitialConverter(emptyFinder, "праз", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) != 0);
    }

    @Test
    void testPrynazouniki3() {
        WordInitialConverter next1 = new WordInitialConverter(emptyFinder, "яго", null, logger);
        WordInitialConverter wc = new WordInitialConverter(emptyFinder, "ад", next1, logger);
        assertTrue((wc.huki.getLast().padzielPasla & Huk.PADZIEL_PRYNAZOUNIK) == 0);
    }

    @Test
    void testMiakkasc() {
        WordInitialConverter wc = new WordInitialConverter(emptyFinder, "ль", null, logger);
        assertEquals(1, wc.huki.size());
        assertEquals(Huk.BAZAVY_HUK.л, wc.huki.get(0).bazavyHuk);
        assertEquals(Huk.MIAKKASC_PAZNACANAJA, wc.huki.get(0).miakki);
    }

    @Test
    void testAutamatycnyjaNaciski() {
        assertEquals("яно+".replace('+', '\u0301'), new WordInitialConverter(emptyFinder, "яно", null, logger).word);
        assertEquals("ё+лкі".replace('+', '\u0301'), new WordInitialConverter(emptyFinder, "ёлкі", null, logger).word);
    }

    @Test
    void testZBazyUKarotki() {
        // калі няма ў базе
        WordInitialConverter wcn = new WordInitialConverter(emptyFinder, "ўяўны", null, logger);
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

        WordInitialConverter wcy = new WordInitialConverter(finder, "ўяўны", null, logger);
        assertEquals("ўя+ўны".replace('+', '\u0301'), wcy.word);
    }

    @Test
    void testNaciski() {
        // калі няма ў базе, але ёсць 'ё', 'о'
        WordInitialConverter wcn1 = new WordInitialConverter(emptyFinder, "вянок", null, logger);
        assertEquals("вяно+к".replace('+', '\u0301'), wcn1.word);

        WordInitialConverter wcn2 = new WordInitialConverter(emptyFinder, "ётавы", null, logger);
        assertEquals("ё+тавы".replace('+', '\u0301'), wcn2.word);
    }

    @Test
    void testAdnaskladovyNacisk() {
        // калі няма ў базе
        WordInitialConverter wcn1 = new WordInitialConverter(emptyFinder, "жах", null, logger);
        assertEquals("жа+х".replace('+', '\u0301'), wcn1.word);

        // калі ёсць у базе, але без націску
        Form f = new Form();
        f.setValue("жах");
        Variant v = new Variant();
        v.getForm().add(f);
        Paradigm p = new Paradigm();
        p.getVariant().add(v);

        GrammarDB2 db = GrammarDB2.empty();
        db.getAllParadigms().add(p);
        GrammarFinder finder = new GrammarFinder(db);
//check db
        WordInitialConverter wcy = new WordInitialConverter(finder, "жах", null, logger);
        assertEquals("жа+х".replace('+', '\u0301'), wcy.word);
    }

    private void checkDebug(List<WordInitialConverter> words, String... debugs) {
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
        List<WordInitialConverter> words = List.of(new WordInitialConverter(emptyFinder, "н(ов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-++-");
    }

    @Test
    public void debugStartOne() throws Exception {
        List<WordInitialConverter> words = List.of(new WordInitialConverter(emptyFinder, "(нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "+++-");
    }

    @Test
    public void debugEndOne() throws Exception {
        List<WordInitialConverter> words = List.of(new WordInitialConverter(emptyFinder, "н(овы)", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++");
    }

    @Test
    public void debugTwo() throws Exception {
        List<WordInitialConverter> words = List.of(new WordInitialConverter(emptyFinder, "н(овы", null, logger),
                new WordInitialConverter(emptyFinder, "нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++", "+++-");
    }

    @Test
    public void debugThree() throws Exception {
        List<WordInitialConverter> words = List.of(new WordInitialConverter(emptyFinder, "н(овы", null, logger),
                new WordInitialConverter(emptyFinder, "новы", null, logger), new WordInitialConverter(emptyFinder, "нов)ы", null, logger));
        Fanetyka3.applyDebug(words);
        checkDebug(words, "-+++", "++++", "+++-");
    }

    @Test
    public void empty() throws Exception {
        new WordInitialConverter(emptyFinder, "", null, logger);
    }
}
