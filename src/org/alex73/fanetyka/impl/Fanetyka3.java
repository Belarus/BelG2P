package org.alex73.fanetyka.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.SetUtils;
import org.alex73.grammardb.StressUtils;
import org.alex73.grammardb.WordMorphology;
import org.alex73.grammardb.structures.Form;
import org.alex73.grammardb.structures.Paradigm;
import org.alex73.grammardb.structures.Variant;

/*
 * Праверыць - [й] перад галоснымі - гэта галосны ці змяніць на зычны
 * 
 * TODO зрабіць: н перад санорнымі не змякчаецца
 * 
 * TODO брск - бск (Акцябрскі)     стм - см (пластмаса)     мюзікл mʲuzʲikl
 * 
 * Спасылкі на кнігі:
 * Ф1989 - Фанетыка беларускай літаратурнай мовы / І.Р.Бурлыка,Л.Ц.Выгонная,Г.В.Лосік,А.І.Падлужны. - Мн. : Навука і тэхніка, 1989. - 335с.
 */
public class Fanetyka3 implements IFanetyka {
    protected final FanetykaConfig config;
    List<Huk> huki = new ArrayList<>();
    List<String> words = new ArrayList<>();
    public List<String> why = new ArrayList<>(); // як адбываюцца пераходы

    public Fanetyka3(FanetykaConfig config) throws Exception {
        this.config = config;
    }

    public void addWord(String w) {
        words.add(normalize(w));
    }

    public void calcFanetyka() throws Exception {
        for (int i = 0; i < words.size(); i++) {
            String w = words.get(i);
            w = narmalizacyjaSlova(w.toLowerCase());
            words.set(i, w);
        }

        for (int i = 0; i < words.size(); i++) {
            String w = words.get(i);

            boolean appendToNextWord = false;
            if (i < words.size() - 1) {
                // прыназоўнікі "без", "не" - мусяць прыляпляцца да слова, бо ёсць працэсы, якія
                // з імі адбываюцца: мяккасць, аглушэнне/азванчэнне
                // яканне робім адразу тут
                String wl = StressUtils.unstress(w.toLowerCase());
                // яканне адбываецца калі ў наступным слове націск прыпадае на першы склад,
                // альбо нават у трэцім слове: "ня ў лад", "ня з ім"
                switch (wl) {
                case "не":
                    appendToNextWord = true;
                    if (naciskNaNastupnySklad(i)) {
                        w = "ня";
                        why.add("'не' пераходзіць у 'ня' перад словам з націскам на першы склад");
                    }
                    break;
                case "без":
                    appendToNextWord = true;
                    if (naciskNaNastupnySklad(i)) {
                        w = "бяз";
                        why.add("'без' пераходзіць у 'бяз' перад словам з націскам на першы склад");
                    }
                    break;
                case "праз":
                case "з":
                    appendToNextWord = true;
                    break;
                }
            }

            if (!fanetykaBazy(w)) {
                stvarajemBazavyjaHuki(w);
            }

            if (appendToNextWord) {
                Huk aposni = huki.get(huki.size() - 1);
                if (aposni.padzielPasla != Huk.PADZIEL_SLOVY) {
                    throw new Exception("Няправільная канвертацыя без/не");
                }
                aposni.padzielPasla = Huk.PADZIEL_PRYNAZOUNIK;
            }
        }

        String prev = toString();
        int pass = 0;
        while (true) {
            startIteration();

            config.processPierachodI.process(this);
            config.processMiakkasc.process(this);
            config.processAhlusennieAzvancennie.process(this);
            config.processSprascennie.process(this);
            config.processPrypadabniennie.process(this);
            config.processSypiacyjaSvisciacyja.process(this);
            config.processBilabijalnyV.process(this);
            config.processHubnaZubnyM.process(this);
            config.processUstaunyA.process(this);
            config.processPierachodFH.process(this);
            String hnew = toString();
            if (hnew.equals(prev)) {
                // нічога не змянілася
                break;
            }
            prev = hnew;
            pass++;
            if (pass >= 100) {
                throw new RuntimeException("Зашмат крокаў канверсіі");
            }
        }
        setIpaStress();
    }

    private boolean naciskNaNastupnySklad(int currentWord) {
        for (int i = currentWord + 1; i < words.size(); i++) {
            if (StressUtils.syllCount(words.get(i)) > 0) { // не ўлічваем "ў", "з"
                // аднаскладовыя словы хутчэй за ўсё службовыя - націску няма
                return StressUtils.getStressFromStart(words.get(i)) == 0;
            }
        }
        return false;
    }

    protected void startIteration() {

    }

    public String toString() {
        return toString(Huk.ipa) + " / " + toString(Huk.skolny);
    }

    public String toString(Function<Huk, String> hukConverter) {
        StringBuilder out = new StringBuilder();
        for (Huk huk : huki) {
            out.append(hukConverter.apply(huk));
            if ((huk.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_PRYNAZOUNIK)) != 0) {
                out.append(' ');
            }
        }
        return out.toString().trim();
    }

    public void setIpaStress() {
        int hal = 0;
        for (int i = 0; i < huki.size(); i++) {
            Huk h = huki.get(i);
            if (h.stress) {
                setIpaStress(hal, i);
            }
            if (h.halosnaja && h.bazavyHuk != BAZAVY_HUK.j) {
                hal = i;
            }
            if ((h.padzielPasla & Huk.PADZIEL_SLOVY) != 0) {
                hal = i + 1;
            }
        }
    }

    /*
     * Націскі ў IPA: ставіцца перад складам.
     */
    protected void setIpaStress(int prevHalIndex, int halIndex) {
        // huki.get(halIndex).stress = false;
        StringBuilder s = new StringBuilder();
        for (int i = prevHalIndex; i <= halIndex; i++) {
            Huk h = huki.get(i);
            if (h.bazavyHuk == BAZAVY_HUK.ў || h.bazavyHuk == BAZAVY_HUK.j || h.bazavyHuk == BAZAVY_HUK.р) {
                s.append('J');
            } else if (h.halosnaja) {
                s.append('H');
            } else if (h.isSanorny()) {
                s.append('S');
            } else {
                s.append("Z");
            }
        }
        int pierad;
        switch (s.toString()) {
        case "H":
        case "JH":
        case "JSH":
        case "SH":
        case "SSH":
        case "SZZH":
        case "ZH":
        case "ZJH":
        case "ZSH":
        case "ZSJH":
        case "ZZH":
        case "ZZJH":
        case "ZZSH":
        case "ZZZH":
            pierad = 0;
            break;
        case "HH":
        case "HJH":
        case "HJJH":
        case "HSH":
        case "HZH":
        case "HZJH":
        case "HZSH":
        case "HZSSH":
        case "HZZH":
        case "HZZJH":
        case "HZZSH":
        case "HZZZH":
        case "HZZZJH":
        case "HZZZSH":
            pierad = 1;
            break;
        case "HJSH":
        case "HJSJH":
        case "HJSSH":
        case "HJZH":
        case "HJZJH":
        case "HJZSH":
        case "HJZZH":
        case "HJZZJH":
        case "HJZZSH":
        case "HSJH":
        case "HSSH":
        case "HSZH":
        case "HSZJH":
        case "HSZSH":
        case "HSZZH":
        case "HSZZJH":
        case "HSZZSH":
        case "HSZZZH":
        case "HZSJH":
        case "HJZZZH":
            pierad = 2;
            break;
        default:
            System.err.println("Незразумелая мяжа складаў у мадэлі '" + s + "' для слоў " + words + "/" + this);
            pierad = -1;
            break;
        }
        if (pierad >= 0) {
            huki.get(prevHalIndex + pierad).stressIpa = true;
        }
    }

    boolean fanetykaBazy(String word) {
        String fan = config.finder.getFan(word);
        if (fan == null) {
            return false;
        }
        why.add("Фанетыка з базы: " + fan);
        Huk.ParseIpaContext p = new Huk.ParseIpaContext(fan);
        while (p.fan.length() > 0) {
            Huk h = Huk.parseIpa(p);
            if (h != null) {
                huki.add(h);
            }
        }
        if (p.stress) {
            throw new RuntimeException("Няправільнае аднаўленне націску: " + word);
        }
        if (!huki.isEmpty()) {
            huki.get(huki.size() - 1).padzielPasla |= Huk.PADZIEL_SLOVY;
        }
        return true;
    }

    /**
     * Бярэм падзел слова на часткі з базы, ці толькі націскі і ґ, ці пазначаем
     * найбольш распаўсюджаныя прыстаўкі.
     */
    String narmalizacyjaSlova(String w) {
        if (w.contains("/") || w.contains("|")) {
            // ужо пазначана
            return w;
        }

        if (w.toLowerCase().equals("і") || w.toLowerCase().equals("у") || w.toLowerCase().equals("ў")) {
            // асобны выпадак - мусіць не быць націску
            return w;
        }

        w = w.replace('+', '\u0301').replace('´', '\u0301').replace('+', '\u0301');

        // 'ў' мусіць захоўвацца
        boolean piersyUkarotki = w.toLowerCase().startsWith("ў");

        Set<WordMorphology> foundForms = new TreeSet<>();
        // у базе няма марфалогіі - спрабуем выцягнуць націскі і ґ
        for (Paradigm p : config.finder.getParadigms(w)) {
            for (Variant v : p.getVariant()) {
                for (Form f : v.getForm()) {
                    if (f.getValue().isEmpty()) {
                        continue;
                    }
                    if (compareWord(w, f.getValue())) {
                        foundForms.add(new WordMorphology(p, v, f));
                    }
                }
            }
        }
        
        // спрабуем ўзяць форму дзе няма замены на г выбухны
        WordMorphology fromDB = foundForms.stream().filter(wm -> wm.v.getZmienyFanietyki() == null).findFirst().orElse(null);
        if (fromDB == null) {
            // калі такой формы няма, спрабуем ўзяць любую форму
            fromDB = foundForms.stream().findFirst().orElse(null);
        }

        if (fromDB == null) {
            // не знайшлі ў базе - прастаўляем націскі на о, ё
            int p = w.indexOf('о');
            if (p < 0) {
                p = w.indexOf('ё');
            }
            if (p >= 0) {
                w = w.substring(0, p + 1) + GrammarDB2.pravilny_nacisk + w.substring(p + 1);
                why.add("Аўтаматычна пазначаныя націскі: " + w);
            }
        } else {
            if (foundForms.stream().map(f -> f.getFanetykaApplied()).distinct().count() > 1) {
                // ці не ўсе аднолькавыя ?
                why.add("Больш за 1 варыянт для '" + w + "' з базы: " + foundForms);
            }

            w = fromDB.getFanetykaApplied(); // нават калі прыстаўкі не вызначаныя, могуць быць змены фанетыкі
            String tag = SetUtils.tag(fromDB.p, fromDB.v);
            if (StressUtils.syllCount(w) == 1 && (tag.startsWith("E") || tag.startsWith("I") || tag.startsWith("C"))) {
                // для аднаскладовых часціц, прыназоўнікаў, злучнікаў - націск не ставіцца
                w = StressUtils.unstress(w);
            }

            why.add("Націскі, пазначэнне ґ і прыставак з базы: " + w);
        }

        if (fromDB == null || !fromDB.isMorphologyDefined()) {
            // пазначаем найбольш распаўсюджаныя прыстаўкі, калі ў базе няма інфармацыі
            String wl = StressUtils.unstress(w.toLowerCase());
            for (Prystauka p : PRYSTAUKI) {
                if (wl.startsWith(p.beg)) {
                    int skipLength = p.result.replaceAll("[/\\|{}]", "").length();

                    if (p.result.endsWith("/")) {
                        // гэта прыстаўка - правяраем, ці магчымая яна ў гэтым слове
                        char nextLetter = wl.charAt(skipLength );
                        char nextLetter2 = wl.charAt(skipLength+1);
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

                    why.add("Мяркуем, што прыстаўка '" + p.result + "'");
                    w = StressUtils.setUsuallyStress(w);
                    int stress = StressUtils.getStressFromStart(w);
                    wl = p.result + wl.substring(skipLength);
                    w = StressUtils.setStressFromStart(wl, stress);
                    break;
                }
            }
        }

        if (piersyUkarotki && w.startsWith("у")) {
            w = "ў" + w.substring(1);
        } else if (piersyUkarotki && w.startsWith("У")) {
            w = "Ў" + w.substring(1);
        }

        return w;
    }

    /**
     * Канвэртуем літары ў базавыя гукі, ўлічваючы дж/дз як адзін гук і пазначаючы
     * мяккі знак як мяккасьць папярэдняга гуку.
     */
    void stvarajemBazavyjaHuki(String w) {
        Huk papiaredniHuk = null;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            Huk novyHuk = null;
            char next;
            try {
                next = w.charAt(i + 1);
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
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("е", BAZAVY_HUK.э);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'ё':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
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
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("і", BAZAVY_HUK.і);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'й':
                novyHuk = new Huk("й", BAZAVY_HUK.j);
                novyHuk.miakki=Huk.MIAKKASC_PAZNACANAJA;
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
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("ю", BAZAVY_HUK.у);
                novyHuk.halosnaja = true;
                novyHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                break;
            case 'я':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
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
                papiaredniHuk.padzielPasla = Huk.PADZIEL_PRYSTAUKA;
                break;
            case '|':
                papiaredniHuk.padzielPasla = Huk.PADZIEL_KARANI;
                break;
            case '{':
            case '}':
                // інтэрфікс - ніякіх падзелаў
                break;
            case '-':
                papiaredniHuk.padzielPasla = Huk.PADZIEL_MINUS;
                break;
            case GrammarDB2.pravilny_nacisk:
                if (papiaredniHuk == null || !papiaredniHuk.halosnaja || papiaredniHuk.padzielPasla == Huk.PADZIEL_MINUS
                        || papiaredniHuk.padzielPasla == Huk.PADZIEL_SLOVY) {
                    throw new RuntimeException("Няправільная пазнака націску - пасля " + papiaredniHuk);
                }
                papiaredniHuk.stress = true;
                break;
            default:
                throw new RuntimeException("Невядомая літара: " + c);
            }
            if (novyHuk != null) {
                huki.add(novyHuk);
                papiaredniHuk = novyHuk;
            }
        }
        if (!huki.isEmpty()) {
            huki.get(huki.size() - 1).padzielPasla |= Huk.PADZIEL_SLOVY;
        }
    }

    void dadacJotKaliPatrebny(Huk papiaredni, char current, char next) {
        boolean add = false;
//TODO а калі прыназоўнік ?
        boolean pacatakSlova = papiaredni == null || papiaredni.padzielPasla == Huk.PADZIEL_SLOVY;
        switch (current) {
        case 'і':
            // ФБЛМ стар. 145-146
            if (next == GrammarDB2.pravilny_nacisk) {
                // націскны
                if (pacatakSlova || papiaredni.halosnaja || papiaredni.apostrafPasla || papiaredni.miakki != 0 || papiaredni.zychodnyjaLitary.equals("ў")) {
                    // дадаем j
                    add = true;
                }
            } else {
                // ненаціскны
                if (!pacatakSlova) {
                    if (papiaredni.halosnaja || papiaredni.miakki != 0 || papiaredni.zychodnyjaLitary.equals("ў")) {
                        // дадаем j
                        add = true;
                    }
                }
            }
            break;
        case 'е':
        case 'ё':
        case 'ю':
        case 'я':
            if (pacatakSlova || papiaredni.halosnaja || papiaredni.apostrafPasla || papiaredni.miakki != 0 || papiaredni.zychodnyjaLitary.equals("ў")
                    || papiaredni.padzielPasla != 0 || "тдржшч".indexOf(papiaredni.zychodnyjaLitary) >= 0) {
                add = true;//TODO а калі прыназоўнік ?
            }
            break;
        }
        if (add) {
            Huk jot = new Huk("", BAZAVY_HUK.j);
            jot.setMiakkasc(true);
            jot.miakki = Huk.MIAKKASC_PAZNACANAJA;
            huki.add(jot);
        }
    }

    public static final String usie_naciski = GrammarDB2.pravilny_nacisk + "\u00B4";
    public static final String usie_apostrafy = GrammarDB2.pravilny_apostraf + "\'\u2019";

    static String normalize(String inputWord) {
        StringBuilder s = new StringBuilder();
        for (char c : inputWord.toCharArray()) {
            if (usie_apostrafy.indexOf(c) >= 0) {
                c = GrammarDB2.pravilny_apostraf;
            }
            if (usie_naciski.indexOf(c) >= 0) {
                c = GrammarDB2.pravilny_nacisk;
            }
            s.append(c);
        }
        return s.toString();
    }

    static boolean compareWord(String inputWord, String grammarDbWord) {
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
    }

    static final List<Prystauka> PRYSTAUKI = new ArrayList<>();

    static {
        Pattern RE = Pattern.compile("(.+)=(.*) #.+");

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(Fanetyka3.class.getResourceAsStream("prystauki.txt"), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                Matcher m = RE.matcher(s);
                if (!m.matches()) {
                    throw new Exception(s);
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
    }
}
