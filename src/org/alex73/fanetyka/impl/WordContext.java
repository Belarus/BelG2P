package org.alex73.fanetyka.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;
import org.alex73.grammardb.StressUtils;
import org.alex73.grammardb.WordMorphology;
import org.alex73.grammardb.structures.Form;
import org.alex73.grammardb.structures.Paradigm;
import org.alex73.grammardb.structures.Variant;

/**
 * Гэты клас адказвае за нармалізацыю слова і выцягванне дадатковай інфармацыі
 * пра слова(націскі, межы) з граматычнай базы.
 */
public class WordContext {
    public static final String usie_naciski = GrammarDB2.pravilny_nacisk + "\u00B4";
    public static final String usie_apostrafy = GrammarDB2.pravilny_apostraf + "\'\u2019";

    private final GrammarFinder finder;
    private final Consumer<String> logger;
    public String word;
    private final WordContext nextWord;
    private boolean appendToNextWord;
    protected List<Huk> huki;
    protected float debugPartBegin = 0, debugPartEnd = 0; // if debug, value will be from 0 to 1 - position in word
    private WordMorphology fromDB;

    public WordContext(GrammarFinder finder, String wordToProcess, WordContext nextWord, Consumer<String> logger) {
        this.finder = finder;
        this.logger = logger;
        this.word = wordToProcess;
        this.nextWord = nextWord;

        zamienaSimvalau();
        fanetykaBazy();
        if (huki == null) { // фанетыка не ўзялася з базы
            String wlower = word.toLowerCase();
            if (wlower.equals("ў") || NIENACISKNYJA.contains(wlower)) {
                // Простыя ненаціскныя словы - не бяром націск і прыстаўкі з базы.
                logger.accept("Слова '" + word + "' - у спісе ненаціскных, не бяром націск і прыстаўкі з базы");
            } else {
                // Звычайныя словы - глядзім базу.
                word = zBazy(word);
            }
            checkNextWord();
            stvarajemBazavyjaHuki();
        }
    }

    /**
     * This method fixes stress chars and apostrophes to correct char code. Also, it
     * retrieves debug positions.
     */
    private void zamienaSimvalau() {
        int debugBegin = -1, debugEnd = -1;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (c == '(') {
                debugBegin = s.length();
            } else if (c == ')') {
                debugEnd = s.length();
            } else {
                if (usie_apostrafy.indexOf(c) >= 0) {
                    c = GrammarDB2.pravilny_apostraf;
                }
                if (usie_naciski.indexOf(c) >= 0) {
                    c = GrammarDB2.pravilny_nacisk;
                }
                s.append(c);
            }
        }
        word = s.toString();
        if (word.length() >= 0) {
            if (debugBegin < 0 && debugEnd >= 0) {
                debugBegin = 0;
            }
            if (debugBegin >= 0 && debugEnd < 0) {
                debugEnd = word.length();
            }
            if (debugBegin >= 0) {
                debugPartBegin = debugBegin * 1.0f / word.length();
            } else {
                debugPartBegin = 0;
            }
            if (debugEnd >= 0) {
                debugPartEnd = debugEnd * 1.0f / word.length();
            } else {
                debugPartEnd = 0;
            }
        }
    }

    public boolean applyDebug(float from, float to) {
        int debugBegin = Math.round(from * huki.size());
        int debugEnd = Math.round(to * huki.size());
        for (int i = debugBegin; i < debugEnd; i++) {
            huki.get(i).debug = true;
        }
        return debugEnd == huki.size();
    }

    private void checkNextWord() {
        if (nextWord != null) {
            // прыназоўнікі "без", "не" - мусяць прыляпляцца да слова, бо ёсць працэсы, якія
            // з імі адбываюцца: мяккасць, аглушэнне/азванчэнне
            // яканне робім адразу тут
            String wl = StressUtils.unstress(word.toLowerCase());
            // яканне адбываецца калі ў наступным слове націск прыпадае на першы склад,
            // альбо нават у трэцім слове: "ня ў лад", "ня з ім"
            // Яканне адбываецца толькі ў некаторых часціцах і прыназоўніках, і не
            // адбываецца ў астатніх: "дзе вёска"(але "ня вёска"),
            // таму яканне не ўзнікае ад прыляпляння прыназоўнікаў да назоўнікаў.
            switch (wl) {
            case "не":
                appendToNextWord = true;
                if (nextWord.naciskNaPiersySklad()) {
                    word = "ня";
                    logger.accept("'не' пераходзіць у 'ня' перад словам з націскам на першы склад");
                }
                break;
            case "без":
                appendToNextWord = true;
                if (nextWord.naciskNaPiersySklad()) {
                    word = "бяз";
                    logger.accept("'без' пераходзіць у 'бяз' перад словам з націскам на першы склад");
                }
                break;
            case "праз":
            case "з":
                appendToNextWord = true;
                break;
            }
        }
    }

    /**
     * Канвэртуем літары ў базавыя гукі, ўлічваючы дж/дз як адзін гук і пазначаючы
     * мяккі знак як мяккасьць папярэдняга гуку.
     */
    void stvarajemBazavyjaHuki() {
        huki = new ArrayList<>();
        String wl = word.toLowerCase();
        Huk papiaredniHuk = null;
        for (int i = 0; i < wl.length(); i++) {
            char c = wl.charAt(i);
            Huk novyHuk = null;
            char next;
            try {
                next = wl.charAt(i + 1);
            } catch (StringIndexOutOfBoundsException ex) {
                next = 0;
            }
            switch (c) {
            case 'а':
                novyHuk = new Huk("а", BAZAVY_HUK.а);
                novyHuk.halosnaja = true;
                break;
            case 'б':
                novyHuk = new Huk("б", BAZAVY_HUK.б);
                break;
            case 'в':
                novyHuk = new Huk("в", BAZAVY_HUK.в);
                break;
            case 'г':
                novyHuk = new Huk("г", BAZAVY_HUK.г);
                break;
            case 'ґ':
                novyHuk = new Huk("ґ", BAZAVY_HUK.ґ);
                break;
            case 'д':
                novyHuk = new Huk("д", BAZAVY_HUK.д);
                break;
            case 'е':
                novyHuk = new Huk("е", BAZAVY_HUK.э);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'ё':
                novyHuk = new Huk("ё", BAZAVY_HUK.о);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'ж':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary) && papiaredniHuk.padzielPasla == 0) {
                    // дж
                    papiaredniHuk.zychodnyjaLitary = "дж";
                    papiaredniHuk.bazavyHuk = BAZAVY_HUK.дж;
                } else {
                    novyHuk = new Huk("ж", BAZAVY_HUK.ж);
                }
                break;
            case 'з':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary) && papiaredniHuk.padzielPasla == 0) {
                    // дз
                    papiaredniHuk.zychodnyjaLitary = "дз";
                    papiaredniHuk.bazavyHuk = BAZAVY_HUK.дз;
                } else {
                    novyHuk = new Huk("з", BAZAVY_HUK.з);
                }
                break;
            case 'і':
                novyHuk = new Huk("і", BAZAVY_HUK.і);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'й':
                novyHuk = new Huk("й", BAZAVY_HUK.j);
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'к':
                novyHuk = new Huk("к", BAZAVY_HUK.к);
                break;
            case 'л':
                novyHuk = new Huk("л", BAZAVY_HUK.л);
                break;
            case 'м':
                novyHuk = new Huk("м", BAZAVY_HUK.м);
                break;
            case 'н':
                novyHuk = new Huk("н", BAZAVY_HUK.н);
                break;
            case 'о':
                novyHuk = new Huk("о", BAZAVY_HUK.о);
                novyHuk.halosnaja = true;
                break;
            case 'п':
                novyHuk = new Huk("п", BAZAVY_HUK.п);
                break;
            case 'р':
                novyHuk = new Huk("р", BAZAVY_HUK.р);
                break;
            case 'с':
                novyHuk = new Huk("с", BAZAVY_HUK.с);
                break;
            case 'т':
                novyHuk = new Huk("т", BAZAVY_HUK.т);
                break;
            case 'у':
                novyHuk = new Huk("у", BAZAVY_HUK.у);
                novyHuk.halosnaja = true;
                break;
            case 'ў':
                novyHuk = new Huk("ў", BAZAVY_HUK.ў);
                break;
            case 'ф':
                novyHuk = new Huk("ф", BAZAVY_HUK.ф);
                break;
            case 'х':
                novyHuk = new Huk("х", BAZAVY_HUK.х);
                break;
            case 'ц':
                novyHuk = new Huk("ц", BAZAVY_HUK.ц);
                break;
            case 'ч':
                novyHuk = new Huk("ч", BAZAVY_HUK.ч);
                break;
            case 'ш':
                novyHuk = new Huk("ш", BAZAVY_HUK.ш);
                break;
            case 'ы':
                novyHuk = new Huk("ы", BAZAVY_HUK.ы);
                novyHuk.halosnaja = true;
                break;
            case 'ь':
                if (papiaredniHuk != null) {
                    papiaredniHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                }
                break;
            case 'э':
                novyHuk = new Huk("э", BAZAVY_HUK.э);
                novyHuk.halosnaja = true;
                break;
            case 'ю':
                novyHuk = new Huk("ю", BAZAVY_HUK.у);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'я':
                novyHuk = new Huk("я", BAZAVY_HUK.а);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case GrammarDB2.pravilny_apostraf:
                if (papiaredniHuk != null) {
                    papiaredniHuk.apostrafPasla = true;
                }
                break;
            case '/':
                // у базе так пазначаецца прыстаўка
                if (papiaredniHuk != null) {
                    papiaredniHuk.padzielPasla = Huk.PADZIEL_PRYSTAUKA;
                }
                break;
            case '|':
                // у базе так пазначаецца мяжа ў двухкаранёвых словах
                if (papiaredniHuk != null) {
                    papiaredniHuk.padzielPasla = Huk.PADZIEL_KARANI;
                }
                break;
            case '{':
            case '}':
                // Інтэрфікс - ніякіх падзелаў не робіцца, інтэрфікс у базе толькі на будучыню.
                break;
            case '-':
                // злучок
                if (papiaredniHuk != null) {
                    papiaredniHuk.padzielPasla = Huk.PADZIEL_ZLUCOK;
                }
                break;
            case GrammarDB2.pravilny_nacisk:
                if (papiaredniHuk == null || !papiaredniHuk.halosnaja || papiaredniHuk.padzielPasla == Huk.PADZIEL_ZLUCOK
                        || papiaredniHuk.padzielPasla == Huk.PADZIEL_SLOVY) {
                    if (!Huk.SKIP_ERRORS) {
                        throw new RuntimeException("Няправільная пазнака націску - пасля " + papiaredniHuk);
                    }
                }
                papiaredniHuk.stress = true;
                break;
            default:
                if (!Huk.SKIP_ERRORS) {
                    throw new RuntimeException("Невядомая літара: " + c);
                }
            }
            if (novyHuk != null) {
                novyHuk.fromDB = fromDB;
                huki.add(novyHuk);
                papiaredniHuk = novyHuk;
            }
        }
        if (!huki.isEmpty()) {
            huki.getLast().padzielPasla |= Huk.PADZIEL_SLOVY;
            if (nextWord != null) {
                if (appendToNextWord) {
                    huki.getLast().padzielPasla |= Huk.PADZIEL_PRYNAZOUNIK;
                }
            }
        }
        logger.accept("Базавыя гукі з '" + wl + "': " + Fanetyka3.toString(huki, Huk.ipa));
    }

    /**
     * Калі ў базе прапісана адмысловая фанетыка для слова - выкарыстоўваем яе.
     */
    void fanetykaBazy() {
        if (word.indexOf('ґ') > 0 || word.indexOf('Ґ') > 0) {
            // калі ў слове ёсць 'ґ' - шукаем фанетыку ўсё ж для 'г'
            word = word.toLowerCase().replace('ґ', 'г'); // TODO - толькі для пошуку фанетыкі і па базе. калі не знайшлі - захоўваем ґ
        }
        String fan = finder.getFan(word);
        if (fan == null) {
            return;
        }
        logger.accept("Адмысловая фанетыка з базы: " + fan);
        huki = IPAUtils.parseIpa(fan);
        huki.getLast().padzielPasla = Huk.PADZIEL_SLOVY;
    }

    /**
     * Ці прыпадае ў гэтым слове націск на першы склад ?
     */
    boolean naciskNaPiersySklad() {
        // на які склад націск ?
        for (Huk h : huki) {
            if (h.halosnaja) {
                return h.stress;
            }
        }
        // Слова без галосных - магчыма прыназоўнік.
        // Не ўлічваем "ў", "з" - бяром з наступнага слова, бо "не з ім" - мусіць
        // пераходзіць у "ня з ім".
        if (nextWord != null) {
            return nextWord.naciskNaPiersySklad();
        } else {
            // наступнага слова няма
            return false;
        }
    }

    /**
     * Узяць слова з базы, калі ёсць.
     */
    String zBazy(String w) {
        boolean pacatakUKarotki = word.toLowerCase().startsWith("ў");// слова пачынаецца на 'ў'

        // шукаем усе падобныя формы з базы
        List<WordMorphology> foundForms = new ArrayList<>();
        for (Paradigm p : finder.getParadigms(word)) {
            for (Variant v : p.getVariant()) {
                for (Form f : v.getForm()) {
                    if (f.getValue().isEmpty()) {
                        continue;
                    }
                    if (compareWord(word, f.getValue())) {
                        WordMorphology newWord = new WordMorphology(p, v, f);
                        if (foundForms.stream().noneMatch(wm -> wm.compareTo(newWord) == 0)) {
                            // калі такой формы яшчэ няма ў спісе - дадаем, але параўнанне - толькі па тэксту формы
                            foundForms.add(newWord);
                        }
                    }
                }
            }
        }

        // спрабуем ўзяць форму дзе няма замены на г выбухны
        fromDB = foundForms.stream().filter(wm -> wm.v.getZmienyFanietyki() == null).findFirst().orElse(null);
        if (fromDB == null) {
            // калі такой формы няма, спрабуем ўзяць любую форму
            fromDB = foundForms.stream().findFirst().orElse(null);
        }

        if (fromDB == null) {
            logger.accept("Слова не знойдзена ў базе: " + w);
            // не знайшлі ў базе - прастаўляем націскі на о, ё
            int p = w.indexOf('о');
            if (p < 0) {
                p = w.indexOf('ё');
            }
            if (p >= 0) {
                w = w.substring(0, p + 1) + GrammarDB2.pravilny_nacisk + w.substring(p + 1);
                logger.accept("Аўтаматычна пазначаныя націскі: " + w);
            }
        } else {
            if (foundForms.stream().map(f -> f.getFanetykaApplied()).distinct().count() > 1) {
                // ці не ўсе аднолькавыя ?
                logger.accept("Аманімія для '" + w + "' з базы: " + foundForms);
            }
            w = fromDB.getFanetykaApplied(); // нават калі прыстаўкі не вызначаныя, могуць быць змены фанетыкі
            logger.accept("Націскі, пазначэнне ґ і прыставак з базы " + fromDB.p.getPdgId() + fromDB.v.getId() + ": " + w);
        }

        if (fromDB == null || !fromDB.isMorphologyDefined()) {
            // пазначаем найбольш распаўсюджаныя прыстаўкі, калі ў базе няма інфармацыі
            String wUnstressed = StressUtils.unstress(w);
            String wUnstressedLowerCase = wUnstressed.toLowerCase();
            for (Prystauka p : PRYSTAUKI) {
                if (wUnstressedLowerCase.startsWith(p.beg) && wUnstressedLowerCase.length() > p.beg.length()) {
                    int skipLength = p.result.replaceAll("[/\\|{}]", "").length();

                    if (p.result.endsWith("/")) {
                        // гэта прыстаўка - правяраем, ці магчымая яна ў гэтым слове
                        char nextLetter = skipLength < wUnstressedLowerCase.length() ? wUnstressedLowerCase.charAt(skipLength) : '\0';
                        char nextLetter2 = skipLength + 1 < wUnstressedLowerCase.length() ? wUnstressedLowerCase.charAt(skipLength + 1) : '\0';
                        if (p.beg.endsWith("й")) {
                            // прыстаўка
                        } else if (nextLetter == GrammarDB2.pravilny_apostraf
                                && (nextLetter2 == 'е' || nextLetter2 == 'ё' || nextLetter2 == 'ю' || nextLetter2 == 'я')) {
                            // прыстаўкі перад еёюя - толькі калі ёсць апостраф, але калі прыстаўка не на -й
                            // і выпадае з гэтага раду, бо ёсць заінелы. але заезджаны
                            // прыстаўка
                        } else if (nextLetter == 'е' || nextLetter == 'ё' || nextLetter == 'ю' || nextLetter == 'я') {
                            continue;
                        }
                    }

                    if (p.result.isEmpty()) {
                        logger.accept("Мяркуем, што няма прыстаўкі ці першага кораня");
                    } else {
                        logger.accept("Мяркуем, што прыстаўка ці першы корань '" + p.result + "'");
                        w = StressUtils.setUsuallyStress(w);
                        boolean[] stresses = StressUtils.getStressMap(w);
                        wUnstressed = p.result + wUnstressed.substring(skipLength);
                        w = StressUtils.applyStressMap(wUnstressed, stresses);
                    }
                    break;
                }
            }
        }

        // аднаўляем 'ў' калі было
        if (pacatakUKarotki && w.startsWith("у")) {
            w = "ў" + w.substring(1); // малая літара
        } else if (pacatakUKarotki && w.startsWith("У")) {
            w = "Ў" + w.substring(1); // вялікая літара
        }

        return w;
    }

    /**
     * Параўноўвае слова з формай з базы каб вызначыць - ці яны супадаюць.
     */
    private boolean compareWord(String inputWord, String grammarDbWord) {
        int ii = 0, ig = 0;
        char ci, cg;
        while (true) {
            try {
                ci = inputWord.charAt(ii);
            } catch (StringIndexOutOfBoundsException ex) {
                ci = 0;
            }
            try {
                cg = grammarDbWord.charAt(ig);
            } catch (StringIndexOutOfBoundsException ex) {
                cg = 0;
            }
            if (ci == 0 && cg == 0) {
                return true;
            }
            if (Character.isLowerCase(ci) && Character.isUpperCase(cg)) {
                return false;
            }
            ci = Character.toLowerCase(ci);
            cg = Character.toLowerCase(cg);
            if (ii == 0 && ci == 'ў') {
                ci = 'у';
            }
            if (cg == '+') {
                cg = GrammarDB2.pravilny_nacisk;
            }
            if (cg == 'ґ' && ci == 'г') {
                ci = 'ґ';
            }
            if (ci == GrammarDB2.pravilny_nacisk && cg != GrammarDB2.pravilny_nacisk) {
                return false;
            }
            if (ci != GrammarDB2.pravilny_nacisk && cg == GrammarDB2.pravilny_nacisk) {
                ig++;
                continue;
            }
            if (ci != cg) {
                return false;
            }
            ii++;
            ig++;
        }
    }

    static class Prystauka implements Comparable<Prystauka> {
        String beg;
        String result;

        @Override
        public int compareTo(Prystauka o) {
            return o.beg.length() - beg.length();
        }

        @Override
        public String toString() {
            return beg + "=" + result;
        }
    }

    static final List<Prystauka> PRYSTAUKI = new ArrayList<>();
    static final Set<String> NIENACISKNYJA;

    static {
        Pattern RE_P = Pattern.compile("(.+)=(.*) #.+");
        Pattern RE_N = Pattern.compile("[A-Z]/(.+)");

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(Fanetyka3.class.getResourceAsStream("prystauki.txt"), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                Matcher m = RE_P.matcher(s);
                if (!m.matches()) {
                    throw new ExceptionInInitializerError(s);
                }
                Prystauka p = new Prystauka();
                p.beg = m.group(1).trim();
                p.result = m.group(2).trim();
                if (!p.result.equals("?")) {
                    PRYSTAUKI.add(p);
                }
            }
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
        Collections.sort(PRYSTAUKI);

        Set<String> nienacisknyja = new HashSet<>();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(Fanetyka3.class.getResourceAsStream("nienacisknyja.txt"), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                Matcher m = RE_N.matcher(s);
                if (!m.matches()) {
                    throw new ExceptionInInitializerError(s);
                }
                nienacisknyja.add(m.group(1));
            }
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
        NIENACISKNYJA = Collections.unmodifiableSet(nienacisknyja);
    }
}
