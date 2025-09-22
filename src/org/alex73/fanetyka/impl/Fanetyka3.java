package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.str.ToStringBase;
import org.alex73.fanetyka.impl.str.ToStringIPA;
import org.alex73.fanetyka.impl.str.ToStringSkolny;

/**
 * Converter.
 * 
 * Class is not thread-safe.
 */
public class Fanetyka3 {
    protected final FanetykaConfig config;

    protected List<String> inputWords;

    protected final List<Huk> huki = new ArrayList<>();

    protected String debugRuleName;
    public boolean debugRuleProcessed;
    public List<String> logPhenomenon = new ArrayList<>(); // log of phonetic phenomenon

    /**
     * Create phonetic converted instance using config.
     * 
     * @param config        - config with conversion tables
     * @param debugRuleName - which phonetic rule to debug. Some letters should be
     *                      marked by () for debugging
     */
    public Fanetyka3(FanetykaConfig config) throws Exception {
        this.config = config;
    }

    /**
     * Debug some cases.
     */
    public void setDebugRuleName(String debugRuleName) {
        this.debugRuleName = debugRuleName;
    }

    /**
     * Conversion of words into a phonetic representation.
     */
    public void calcFanetyka(List<String> inputWords) throws Exception {
        if (this.inputWords != null) {
            throw new Exception("You need to create new converter instance to process new words");
        }
        this.inputWords = inputWords;
        prepareWordsForProcessing();

        String prev = toString();
        int pass = 1;
        while (true) {
            startIteration();

            config.processUstaunyJ.process(this);
            config.processMiakkasc.process(this);
            config.processSprascennie.process(this);
            config.processPrypadabniennie.process(this);
            config.processAhlusennieAzvancennie.process(this);
            config.processSypiacyjaSvisciacyja.process(this);
            config.processRedukavanyja.process(this);
            config.processPazicyjnyja.process(this);
            String hnew = toString();
            if (hnew.equals(prev)) {
                // nothing was changed during last iteration - conversion finished
                break;
            }
            prev = hnew;
            pass++;
            if (pass >= 100) {
                // too many iterations - probably issue with config
                throw new RuntimeException("Зашмат крокаў канверсіі");
            }
        }
    }

    /**
     * Рыхтуем словы для працэсінга. Для кожнага слова знаходзім адпаведнае слова ў
     * базе, і робім змены:
     *
     * - З базы бяром: націскі, змены фанетыкі, дакладную фанетыку, пазначэнне
     * межаў.
     * 
     * - Пазначаем найбольш распаўсюджаныя прыстаўкі, калі слова няма ў базе.
     * 
     * - Захоўваем пазначаныя карыстальнікам межы і націскі.
     * 
     * - Пазначаем межы для некаторых прыназоўнікаў адмыслова, каб яны разглядаліся
     * як частка слова.
     * 
     * - Пазначаем месца дужак для debug.
     */
    protected void prepareWordsForProcessing() throws Exception {
        List<WordInitialConverter> words = new ArrayList<>();
        WordInitialConverter nextWord = null;
        for (int i = inputWords.size() - 1; i >= 0; i--) {
            // апрацоўваем словы, ад канца да пачатку,
            // бо патрэбна спасылка на наступнае слова
            WordInitialConverter w = new WordInitialConverter(config.finder, inputWords.get(i), nextWord, log -> logPhenomenon.add(log));
            words.add(w);
            nextWord = w;
        }
        words = words.reversed();

        applyDebug(words);

        // збіраем гукі з усіх слоў
        for (WordInitialConverter w : words) {
            huki.addAll(w.huki);
        }

        // яканне
        jakannie();
    }

    static final Set<String> JAKANNIE_WORDS = Set.of("не", "без");

    /**
     * Яканне ў прыназоўніках "без" і "не". Робіць яканне ў выразах "не ў хаце".
     */
    void jakannie() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk h = huki.get(i);
            if ("е".equals(h.zychodnyjaLitary) && !h.stress) { // ненаціскны 'е'
                if (!JAKANNIE_WORDS.contains(h.wordContext.word.toLowerCase())) {
                    continue; // не прыназоўнік "не" ці "без"
                }
                boolean change = false;
                // шукаем наступны галосны
                for (int j = i + 1; j < huki.size() - 1; j++) {
                    if (Huk.halosnyja.contains(huki.get(j).bazavyHuk)) {
                        // знайшлі наступны галосны
                        if (huki.get(j).stress) {
                            // націск на наступным галосным
                            change = true;
                        }
                        break;
                    }
                }
                if (change) {
                    h.bazavyHuk = BAZAVY_HUK.а;
                    logPhenomenon.add("Яканне ў '" + h.wordContext.word + "' перад словам з націскам на першы склад");
                }
            }
        }
    }

    static void applyDebug(List<WordInitialConverter> words) {
        // пазначаем debug для гукаў
        boolean inDebug = false;
        for (WordInitialConverter w : words) {
            boolean wordHasOwnDebug = w.debugPartEnd > 0;
            if (wordHasOwnDebug) {
                if (w.applyDebug(w.debugPartBegin, w.debugPartEnd)) {
                    inDebug = true;
                }
            }
            if (inDebug && !wordHasOwnDebug) {
                w.applyDebug(0, 1);
            }
        }
    }

    /**
     * Start next iteration. Required for some overrides.
     */
    protected void startIteration() {
    }

    public String toString() {
        return new ToStringIPA().toString(huki) + " / " + new ToStringSkolny().toString(huki);
    }

    /**
     * Convert sounds to specific text representation.
     */
    public String toString(ToStringBase hukConverter) {
        return hukConverter.toString(huki);
    }
}
