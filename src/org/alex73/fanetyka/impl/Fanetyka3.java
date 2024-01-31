package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.grammardb.GrammarDB2;
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
public class Fanetyka3 {
    private final FanetykaConfig config;
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
            if (i < words.size() - 1) {
                // "без,не -> бяз,ня" перад словамі з націскам на першы склад
                String wl = w.toLowerCase();
                switch (wl) {
                case "не":
                case "не" + GrammarDB2.pravilny_nacisk:
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "ня";
                        why.add("'не' пераходзіць у 'ня' перад словам з націскам на першы склад");
                    }
                    break;
                case "без":
                case "без" + GrammarDB2.pravilny_nacisk:
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "бяз";
                        why.add("'без' пераходзіць у 'бяз' перад словам з націскам на першы склад");
                    }
                    break;
                }
            }
            if (!fanetykaBazy(w)) {
                stvarajemBazavyjaHuki(w);
            }
        }
        String prev = toString();
        int pass = 0;
        while (true) {
            if (config.processPierachodZG != null && config.processPierachodZG.isConfigExists()) {
                config.processPierachodZG.process(this);
            } else {
                pierachodZG();
            }
            if (config.processPierachodI != null && config.processPierachodI.isConfigExists()) {
                config.processPierachodI.process(this);
            } else {
                pierachodI();
            }
            if (config.processPaznacajemMiakkasc != null && config.processPaznacajemMiakkasc.isConfigExists()) {
                config.processPaznacajemMiakkasc.process(this);
            } else {
                paznacajemMiakkasc();
            }
            if (config.processAhlusennieAzvancennie.isConfigExists()) {
                config.processAhlusennieAzvancennie.process(this);
            } else {
                ahlusennieIazvancennie();
            }
            if (config.processSprascennie != null && config.processSprascennie.isConfigExists()) {
                config.processSprascennie.process(this);
            } else {
                sprascennie();
                pierachodTS();
            }
            if (config.processPrypadabniennie != null && config.processPrypadabniennie.isConfigExists()) {
                config.processPrypadabniennie.process(this);
            } else {
                prypadabniennie();
                padvajennie();
            }
            if (config.processSypiacyjaSvisciacyja != null && config.processSypiacyjaSvisciacyja.isConfigExists()) {
                config.processSypiacyjaSvisciacyja.process(this);
            } else {
                sypiacyjaSvisciacyja();
            }
            if (config.processBilabijalnyV != null && config.processBilabijalnyV.isConfigExists()) {
                config.processBilabijalnyV.process(this);
            } else {
                pierachodV();
            }
            if (config.processHubnaZubnyM != null && config.processHubnaZubnyM.isConfigExists()) {
                config.processHubnaZubnyM.process(this);
            } else {
                pierachodM();
            }
            if (config.processUstaunyA != null && config.processUstaunyA.isConfigExists()) {
                config.processUstaunyA.process(this);
            } else {
                ustaunojeA();
            }
            if (config.processPierachodFH != null && config.processPierachodFH.isConfigExists()) {
                config.processPierachodFH.process(this);
            } else {
                pierachodFH();
            }
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

    public String toString() {
        return toString(Huk.ipa) + " / " + toString(Huk.skolny);
    }

    public String toString(Function<Huk, String> hukConverter) {
        StringBuilder out = new StringBuilder();
        for (Huk huk : huki) {
            out.append(hukConverter.apply(huk));
            if ((huk.padzielPasla & Huk.PADZIEL_SLOVY) != 0) {
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

    public static String fanetykaSlova(Fanetyka3 r, String w) throws Exception {
        r.addWord(w);
        r.calcFanetyka();
        return r.toString(Huk.ipa);
    }
    //
    // /**
    // * Часьцей за ўсё калі слова пачынаецца на адж-, гэта прыстаўка "ад"; адс-
    // прыстаўка "ад".
    // *
    // * TODO Трэба рабіць па напісаньні - пазначаць "падзеленасьць".
    // */
    // void vypraulenniePrystavak() {
    // int prystLen = getPrystaukaLen();
    // if (prystLen < 0) {
    // return;
    // }
    // if (huki.size() < prystLen + 3) {
    // return;
    // }
    // Huk hety = huki.get(prystLen);
    // Huk nastupny = huki.get(prystLen + 1);
    // if (hety.bazavyHuk==BAZAVY_HUK.d͡ʐ") && !hety.miakki) {
    // Huk d = new Huk("", "d");
    // huki.add(prystLen, d);
    // huki.get(prystLen + 1).bazavyHuk = BAZAVY_HUK.ʐ";
    // } else if (hety.bazavyHuk==BAZAVY_HUK.d͡z") && !hety.padvojeny &&
    // !nastupny.halosnaja) {
    // Huk d = new Huk("", "d");
    // d.miakki = false; // заўсёды цьвердае ў прыстаўцы
    // huki.add(prystLen, d);
    // huki.get(prystLen + 1).bazavyHuk = BAZAVY_HUK.z";
    // } else if (hety.bazavyHuk==BAZAVY_HUK.t͡s") && !hety.padvojeny) {
    // Huk t = new Huk("", "t");
    // huki.add(prystLen, t);
    // huki.get(prystLen + 1).bazavyHuk = BAZAVY_HUK.s";
    // }
    // }

    /**
     * Напрыканцы слова паміж некаторымі зычнымі й р/л' дадаецца а
     * 
     * ня толькі напрыканцы
     */
    void ustaunojeA() {
        for (int i = huki.size() - 2; i >= 0; i--) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            Huk dalej = i < huki.size() - 2 ? huki.get(i + 2) : null;
            if (dalej != null && dalej.halosnaja) {
                continue;
            }
            if (nastupny.bazavyHuk == BAZAVY_HUK.р && nastupny.miakki == 0) {
                if (!huk.halosnaja && huk.bazavyHuk != BAZAVY_HUK.р && huk.bazavyHuk != BAZAVY_HUK.j && huk.bazavyHuk != BAZAVY_HUK.ў) {
                    Huk a = new Huk("", BAZAVY_HUK.а);
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                    why.add("Дадаецца 'а'");
                }
            } else if (nastupny.bazavyHuk == BAZAVY_HUK.л && nastupny.miakki != 0) {
                if (huk.bazavyHuk == BAZAVY_HUK.р || huk.bazavyHuk == BAZAVY_HUK.ч || huk.bazavyHuk == BAZAVY_HUK.дж || huk.bazavyHuk == BAZAVY_HUK.ш
                        || huk.bazavyHuk == BAZAVY_HUK.ж || huk.bazavyHuk == BAZAVY_HUK.б || huk.bazavyHuk == BAZAVY_HUK.п || huk.bazavyHuk == BAZAVY_HUK.м
                        || huk.bazavyHuk == BAZAVY_HUK.ф || huk.bazavyHuk == BAZAVY_HUK.г || huk.bazavyHuk == BAZAVY_HUK.к || huk.bazavyHuk == BAZAVY_HUK.х) {
                    Huk a = new Huk("", BAZAVY_HUK.а);
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                    why.add("Дадаецца 'а'");
                }
            }
        }
    }

    // TODO перагледзець як g адрозніваецца ад ɣ
    /**
     * ф-г => ў-г, як у афганец (TODO таксама: прафбюро, сульфгідрыдны ???)
     */
    void pierachodFH() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.is(BAZAVY_HUK.ф, null, false, 0) && nastupny.is(BAZAVY_HUK.г, null, false, 0)) {
                huk.bazavyHuk = BAZAVY_HUK.ў;
            }
        }
    }

    /**
     * з+г, дз+г, дж+г паміж галосных(ці напачатку слова і напрыканцы слова) пераходзяць у з+ґ, дз+ґ, дж+ґ
     */
    void pierachodZG() {
        for (int i = 0; i < huki.size() - 3; i++) {
            Huk h1 = i > 0 ? huki.get(i - 1) : null;
            Huk h2 = huki.get(i);
            Huk h3 = huki.get(i + 1);
            // Huk h4 = huki.get(i + 3);
            if (h1 == null || (h1.halosnaja && !h1.apostrafPasla)) {
                if (h2.padzielPasla == 0 && (h2.bazavyHuk == BAZAVY_HUK.з || h2.bazavyHuk == BAZAVY_HUK.дз || h2.bazavyHuk == BAZAVY_HUK.дж)
                        && !h2.apostrafPasla && !h2.padvojeny) {
                    if (h3.bazavyHuk == BAZAVY_HUK.г && !h3.apostrafPasla && !h3.padvojeny && (h3.padzielPasla == 0 || h3.padzielPasla == Huk.PADZIEL_KARANI)) {
                        h3.bazavyHuk = BAZAVY_HUK.ґ;
                        why.add("Пераход 'зг, дзг, джг' -> 'зґ, дзґ, джґ' паміж галосных і не на сутыку прыстаўкі і кораня");
                    }
                }
            }
        }
    }

    /**
     * 't' і 's' пераходзіць у 'c', толькі калі няма падзелу - спрашчэнне. Можа на мяжы прыстаўкі і кораня не мусіць быць ?
     */
    void pierachodTS() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.padzielPasla != 0) {
                continue;
            }
            if (huk.miakki == 0 && nastupny.miakki == 0) {
                if (huk.bazavyHuk == BAZAVY_HUK.т && nastupny.bazavyHuk == BAZAVY_HUK.с) {
                    why.add("'t' і 's' пераходзіць у 'c', толькі калі няма падзелу");
                    huk.bazavyHuk = BAZAVY_HUK.ц;
                    huk.zychodnyjaLitary += nastupny.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            }
        }
    }

    /**
     * с-ц-к пераходзіць у с-к
     * 
     * с-т-ч пераходзіць у ш-ч
     *
     * с-т-н пераходзіць у с-н
     * 
     * ш-с пераходзіць у с-с
     * 
     * з-д-н пераходзіць у з-н
     * 
     * з-д-н' пераходзіць у з'-н'
     * 
     * з-д-ч пераходзіць у ш-ч
     * 
     * н-т-ш-ч пераходзіць у н-ш-ч
     * 
     * н-д-ш пераходзіць у н-ш TODO
     * 
     * с-с-к пераходзіць у c-к
     * 
     * ц'-т пераходзіць у т'-т
     * 
     * ц'-ц пераходзіць у т'-ц
     * 
     * ж-дж-зычны(толькі на канцы слова мяккія і цвёрдыя) - дз выпадае, як "дасць
     * жыцця"
     * 
     * ш-ч-зычны(толькі на канцы слова мяккія і цвёрдыя) - ч выпадае, як "дасць
     * шырокі"
     */
    void sprascennie() {
        for (int i = 0; i < huki.size(); i++) {
            Huk h1 = null, h2 = null, h3 = null, h4 = null;
            try {
                h1 = huki.get(i);
            } catch (IndexOutOfBoundsException ex) {
            }
            try {
                h2 = huki.get(i + 1);
            } catch (IndexOutOfBoundsException ex) {
            }
            try {
                h3 = huki.get(i + 2);
            } catch (IndexOutOfBoundsException ex) {
            }
            try {
                h4 = huki.get(i + 3);
            } catch (IndexOutOfBoundsException ex) {
            }
            if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.ц, BAZAVY_HUK.к)) { // с-ц-к
                if (h1.miakki == 0 && h2.miakki == 0) {
                    why.add("Спрашчэнне: с-ц-к -> с-к");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.т, BAZAVY_HUK.ч)) { // с-т-ч
                if (h2.miakki == 0 && h1.miakki == 0 && h3.miakki == 0) {
                    // сярэдні выпадае
                    why.add("Спрашчэнне: с-т-ч -> с-ч, як 'даездчык'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.т, BAZAVY_HUK.н)) { // с-т-н
                why.add("Спрашчэнне: с-т-н -> с-н");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.з, BAZAVY_HUK.д, BAZAVY_HUK.н)) { // з-д-н
                why.add("Спрашчэнне: з-д-н -> з-н");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
                h1.miakki = h3.miakki;
            } else if (areHuki(i, BAZAVY_HUK.р, BAZAVY_HUK.к, BAZAVY_HUK.с, BAZAVY_HUK.к)) { // р-к-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && !h4.padvojeny) {
                    why.add("Спрашчэнне: р-к-с-к -> р-c-к, як 'цюркскі'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.с, BAZAVY_HUK.к)) { // с-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && h1.padzielPasla == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: с-с-к -> c-к не на сутыку, як 'гагаузскі'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.с, BAZAVY_HUK.к)) { // с-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && h1.padzielPasla != 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: с-с-к -> c:-к на сутыку, як 'бяссківічны'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    h1.padvojeny = true;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.з, BAZAVY_HUK.д, BAZAVY_HUK.ч)) { // з-д-ч
                if (!h1.padvojeny && h2.padvojeny && h3.padvojeny && h1.padzielPasla == 0 && h2.padzielPasla == 0 && h1.miakki == 0 && h2.miakki == 0
                        && h3.miakki == 0) {
                    why.add("Спрашчэнне: з-д-ч -> ш-ч: як 'аб’ездчык'");
                    h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    h2.bazavyHuk = BAZAVY_HUK.ш;
                    huki.remove(i);
                }
            } else if (areHuki(i, BAZAVY_HUK.ш, BAZAVY_HUK.с)) { // ш-с
                if (h1.miakki == 0 && h1.padzielPasla == 0) {
                    why.add("Спрашчэнне: ш-с -> с-с");
                    h1.bazavyHuk = BAZAVY_HUK.с;
                }
            } else if (areHuki(i, BAZAVY_HUK.ц, BAZAVY_HUK.т)) { // ц-т
                if (h1.miakki != 0 && (h1.padzielPasla & Huk.PADZIEL_PRYSTAUKA) != 0 && h2.miakki == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: ц'-т -> т'-т");
                    h1.bazavyHuk = BAZAVY_HUK.т;
                }
            } else if (areHuki(i, BAZAVY_HUK.ц, BAZAVY_HUK.ц)) { // ц-ц
                if (h1.miakki != 0 && (h1.padzielPasla & Huk.PADZIEL_PRYSTAUKA) != 0 && h2.miakki == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: ц'-ц -> т'-ц");
                    h1.bazavyHuk = BAZAVY_HUK.т;
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.ц, BAZAVY_HUK.т) && h3 != null && isHluchi(h3) && !isSypiacy(h3)) { // с-ц_глухі-нешыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(глухі нешыпячы ў наступным слове) -> c', як 'дасць талацэ'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.ц, BAZAVY_HUK.т) && h3 != null && isHluchi(h3) && isSypiacy(h3)) { // с-ц_глухі-шыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(глухі шыпячы ў наступным слове) -> ш, як 'дасць шырокі'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.ш;
                    h1.miakki = 0;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.ц, BAZAVY_HUK.т) && h3 != null && isZvonki(h3) && !isSypiacy(h3)) { // с-ц_звонкі-нешыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(звонкі нешыпячы ў наступным слове) -> з', як 'дасць дачцэ'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.з;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.с, BAZAVY_HUK.ц, BAZAVY_HUK.т) && h3 != null && isZvonki(h3) && isSypiacy(h3)) { // с-ц_звонкі-шыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(звонкі шыпячы ў наступным слове) -> ж, як 'дасць жыцця'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.ж;
                    h1.miakki = 0;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.з, BAZAVY_HUK.д) && h3 != null && isZvonki(h3)) { // з-д // TODO небывае ? трэба прыклад
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && !h2.padvojeny
                        && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: з'-д' +(звонкі ў наступным слове) -> з', як 'дасць заснуць'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.ж, BAZAVY_HUK.дж)) { // ж-дж
                if (h1.padzielPasla == 0 && !h2.padvojeny && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: ж-дж -> ж (толькі на канцы слова)");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
                /*
                 * } else if (areHuki(i, BAZAVY_HUK.ʂ, BAZAVY_HUK.t͡ʂ)) { // ш-ч// TODO
                 * непераходзіць if (h1.padzielPasla == 0 && !h2.padvojeny && (h2.padzielPasla &
                 * (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                 * why.add("Спрашчэнне: ш-ч -> ш (толькі на канцы)"); h1.zychodnyjaLitary +=
                 * h2.zychodnyjaLitary; huki.remove(i+1); }
                 */
            } else if (areHuki(i, BAZAVY_HUK.н, BAZAVY_HUK.т, BAZAVY_HUK.ш, BAZAVY_HUK.ч)) { // н-т-ш-ч
                why.add("Спрашчэнне: н-т-ш-ч -> н-ш-ч");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.н, BAZAVY_HUK.д, BAZAVY_HUK.ш)) { // н-д-ш
                why.add("Спрашчэнне: н-д-ш -> н-ш");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.н, BAZAVY_HUK.т, BAZAVY_HUK.ш)) { // н-т-ш
                why.add("Спрашчэнне: н-т-ш -> н-ш");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.н, BAZAVY_HUK.т, BAZAVY_HUK.с)) { // н-т-с
                why.add("Спрашчэнне: н-т-с -> н-с, як 'бургундскі'");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.б, BAZAVY_HUK.р, BAZAVY_HUK.с)) { // б-р-с
                why.add("Спрашчэнне: б-р-с -> б-с, як 'акцябрскі'");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.м, BAZAVY_HUK.л, BAZAVY_HUK.с)) { // м-ль-с
                if (h2.miakki == Huk.MIAKKASC_PAZNACANAJA) {
                    why.add("Спрашчэнне: м-ль-с -> м-с, як 'бягомльскі'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            }
        }
    }

    boolean areHuki(int startPos, BAZAVY_HUK... expected) {
        if (startPos + expected.length > huki.size()) {
            return false;
        }

        for (int i = 0; i < expected.length; i++) {
            if (huki.get(startPos + i).bazavyHuk != expected[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 'v' перад 'о' і 'у' пераходзіць у 'β'. Білабіяльны 'в'.
     */
    void pierachodV() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.miakki == 0 && huk.bazavyHuk == BAZAVY_HUK.в) {
                if (!nastupny.miakkajaHalosnaja && (nastupny.bazavyHuk == BAZAVY_HUK.о || nastupny.bazavyHuk == BAZAVY_HUK.у)) {
                    why.add("'v' перад 'о' і 'у' пераходзіць у 'β'");
                    huk.bazavyHuk = BAZAVY_HUK.β;
                }
            }
        }
    }

    /**
     * 'm' перад 'f', 'v', 'β' пераходзіць у 'ɱ'. Губна-зубны 'м'.
     */
    void pierachodM() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.miakki == 0 && huk.bazavyHuk == BAZAVY_HUK.м) {
                if (nastupny.bazavyHuk == BAZAVY_HUK.ф || nastupny.bazavyHuk == BAZAVY_HUK.в || nastupny.bazavyHuk == BAZAVY_HUK.β) {
                    why.add("'m' перад 'f', 'v', 'β' пераходзіць у 'ɱ'");
                    huk.bazavyHuk = BAZAVY_HUK.ɱ;
                }
            }
        }
    }

    /**
     * 'і' перад д т ж ш ч дж пераходзіць у ɨ
     * 
     * TODO шматярусны
     */
    void pierachodI() {
        for (int i = 1; i < huki.size(); i++) {
            Huk papiaredni = huki.get(i - 1);
            Huk huk = huki.get(i);
            if (huk.bazavyHuk == BAZAVY_HUK.і && (papiaredni.bazavyHuk == BAZAVY_HUK.д || papiaredni.bazavyHuk == BAZAVY_HUK.т
                    || papiaredni.bazavyHuk == BAZAVY_HUK.ж || papiaredni.bazavyHuk == BAZAVY_HUK.ш || papiaredni.bazavyHuk == BAZAVY_HUK.ч
                    || papiaredni.bazavyHuk == BAZAVY_HUK.дж || papiaredni.bazavyHuk == BAZAVY_HUK.р)) {
                why.add("'і' перад д т ж ш ч дж пераходзіць у ɨ");
                huk.bazavyHuk = BAZAVY_HUK.ы;
                huk.miakkajaHalosnaja = false;
            }
        }
    }

    /**
     * Падобныя гукі TODO
     */
    void prypadabniennie() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.is(BAZAVY_HUK.д, 0, false, null) && nastupny.is(BAZAVY_HUK.ч, 0, false, null)) {
                why.add("Прыпадабненне:");
                huk.bazavyHuk = BAZAVY_HUK.т;
            } else if (huk.is(BAZAVY_HUK.д, 0, false, null) && nastupny.is(BAZAVY_HUK.ц, 0, false, null)) {
                why.add("Прыпадабненне:");
                huk.bazavyHuk = BAZAVY_HUK.т;
            }
        }
    }

    /**
     * Падваеньне аднолькавых гукаў што ідуць адзін за адным. Мяккасьць бярэцца з
     * апошняга.
     * 
     * Гэта працуе толькі паміж галоснымі. Калі ёсьць побач зычны - ніякага
     * падваеньня не адбываецца. Застаецца толькі адзін гук. // TODO перагледзіць
     * усе падваенне - ці не паміж галоснымі
     */
    void padvajennie() {
        for (int i = huki.size() - 1; i > 0; i--) {
            Huk papiaredni = huki.get(i - 1);
            Huk huk = huki.get(i);
            boolean halosnyPierad = i == 1 || huki.get(i - 2).halosnaja;
            boolean halosnyPasla = i == huki.size() - 1 || huki.get(i + 1).halosnaja;
            if (huk.bazavyHuk.equals(papiaredni.bazavyHuk) && !huk.halosnaja) {
                if (halosnyPasla && halosnyPierad) {
                    huk.padvojeny = true;
                    why.add("Падваенне: " + huk); // на старонку "Падваенне"
                } else {
                    // як 'пражскі'
                    huk.padvojeny = false;
                    why.add("Спрашчэнне: " + huk); // на старонку "Спрашчэнне"
                }
                /*
                 * if (halosnyPierad &&
                 * (huk.bazavyHuk==BAZAVY_HUK.s") || huk.bazavyHuk==BAZAVY_HUK.ʂ") ||
                 * huk.bazavyHuk==BAZAVY_HUK.z") || huk.bazavyHuk==BAZAVY_HUK.p"))) { //
                 * cc+зычны, шш+зычны, зз+зычны, пп+зычны - звычайна ў прыстаўках huk.padvojeny
                 * = true; }
                 */
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
                // 1. дасць сёння c'+c'=>с': 2. дасць зялёнаму з'+з'=>з': 3. дасць табе
                // с'+т=>с'+т 4. дасць дом
                // з'+д=>з'+д
                // 5. дасць швагру с'+ш=>ш: 6. дасць жабе з'+ж=>ж: 7. дасць чалавеку с'+ч=>ш+ч
                // 8. дасць джону
                // з'+дж=>ж+жд
            } else if (huk.bazavyHuk == BAZAVY_HUK.дж && papiaredni.bazavyHuk == BAZAVY_HUK.д) {
                // прыпадабненне
                if (halosnyPasla && halosnyPierad) {
                    why.add("Падваенне: д+дж => дж:");
                    huk.padvojeny = true;
                } else {
                    why.add("Спрашчэнне: д+дж => дж");
                    huk.padvojeny = false;
                }
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk == BAZAVY_HUK.дз && papiaredni.bazavyHuk == BAZAVY_HUK.д) {
                // прыпадабненне
                why.add("Падваенне: д+дз => дз:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk == BAZAVY_HUK.ч && papiaredni.bazavyHuk == BAZAVY_HUK.т) {
                // прыпадабненне
                why.add("Падваенне: т+ч => ч:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk == BAZAVY_HUK.ц && papiaredni.bazavyHuk == BAZAVY_HUK.т) {
                // прыпадабненне
                if (halosnyPasla && halosnyPierad) {
                    why.add("Падваенне: т+ц => ц:");
                    huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                } else {
                    // як хвасцовы
                    why.add("Спрашчэнне: т+ц => ц");
                    huk.padvojeny = false;
                }
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (papiaredni.is(BAZAVY_HUK.ґ, 0, false, null) && huk.is(BAZAVY_HUK.г, null, false, null)) {
                // прыпадабненне
                why.add("Падваенне: ґ+г => г:, як 'банк-гарант'");
                huk.bazavyHuk = BAZAVY_HUK.г;
                huk.padvojeny = true;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            }
        }
    }

    @Deprecated
    boolean isSypiacy(Huk huk) {
        if (huk.miakki != 0) {
            return false;
        }
        return huk.bazavyHuk == BAZAVY_HUK.ш || huk.bazavyHuk == BAZAVY_HUK.ж || huk.bazavyHuk == BAZAVY_HUK.дж || huk.bazavyHuk == BAZAVY_HUK.ч;
    }

    @Deprecated
    boolean isSvisciacy(Huk huk) {
        if (huk.miakki != 0) {
            return false;
        }
        return huk.bazavyHuk == BAZAVY_HUK.с || huk.bazavyHuk == BAZAVY_HUK.з || huk.bazavyHuk == BAZAVY_HUK.дз || huk.bazavyHuk == BAZAVY_HUK.ц;
    }

    /**
     * 
     * 
     * свісцячыя - шыпячыя :
     * 
     * s - ʂ z - ʐ d͡z - d͡ʐ t͡s - t͡ʂ
     * 
     */
    void sypiacyjaSvisciacyja() {
        for (int i = huki.size() - 2; i >= 0; i--) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (isSvisciacy(nastupny) && huk.miakki == 0 && huk.padzielPasla == 0) {
                // пераход шыпячых у свісьцячыя
                switch (huk.bazavyHuk) {
                case ш: // ш+свісцячы
                    why.add("Пераход у свісцячыя: ш->с");
                    huk.bazavyHuk = BAZAVY_HUK.с;
                    break;
                case ж:
                    why.add("Пераход у свісцячыя: ж->з");
                    huk.bazavyHuk = BAZAVY_HUK.з;
                    break;
                case дж:
                    why.add("Пераход у свісцячыя: дж->дз");
                    huk.bazavyHuk = BAZAVY_HUK.дз;
                    break;
                case ч:
                    why.add("Пераход у свісцячыя: ч->ц");
                    huk.bazavyHuk = BAZAVY_HUK.ц;
                    break;
                }
            } else if (isSypiacy(nastupny) /* && !huk.miakki */ && huk.padzielPasla == 0) {
                // пераход свісцячых у шыпячыя
                // пры пераходзе ў шыпячыя мяккасць знікае, нават калі была пазначаная:
                // бязьджаўковая
                switch (huk.bazavyHuk) {
                case т: // т+шыпячы
                    why.add("Пераход у шыпячыя: т->ч");
                    huk.bazavyHuk = BAZAVY_HUK.ч;
                    huk.miakki = 0;
                    break;
                case с:
                    why.add("Пераход у шыпячыя: с->ш");
                    huk.bazavyHuk = BAZAVY_HUK.ш;
                    huk.miakki = 0;
                    break;
                case з:
                    // праблема: бязьджаўковая
                    why.add("Пераход у шыпячыя: з->ж");
                    huk.bazavyHuk = BAZAVY_HUK.ж;
                    huk.miakki = 0;
                    break;
                case дз:
                    // праблема: "сядзь жа"
                    /*
                     * на сутыку: шыпячых мяккіх у беларускай мове няма(усярэдзіне слоў), але каб
                     * зрабіць пераход "вось што" у "ш:", трэба апрацоўваць "сьш", таму праверка на
                     * мяккасць папярэдняга - неабавязковая
                     */
                    why.add("Пераход у шыпячыя: дз->дж");
                    huk.bazavyHuk = BAZAVY_HUK.дж;
                    huk.miakki = 0;
                    break;
                case ц:
                    // праблема: "сядзь шалёны"
                    why.add("Пераход у шыпячыя: ц->ч");
                    huk.bazavyHuk = BAZAVY_HUK.ч;
                    huk.miakki = 0;
                    break;
                }
            }
        }
    }

    /**
     * Аглушэнне і азванчэнне гукаў перад глухімі - распаўсюджваецца на некалькі
     * папярэдніх гукаў. Таксама звонкі пераходзіць у глухі напрыканцы слова.
     */
    void ahlusennieIazvancennie() {
        for (int i = huki.size() - 1; i >= 0; i--) {
            Huk nastupny = i < huki.size() - 1 ? huki.get(i + 1) : null;
            Huk huk = huki.get(i);
            if (nastupny != null && isZvonki(nastupny)) {
                // азванчэнне
                switch (huk.bazavyHuk) {
                case п:
                    why.add("Азванчэнне п->б");
                    huk.bazavyHuk = BAZAVY_HUK.б;
                    break;
                case т:
                    why.add("Азванчэнне т->д");
                    huk.bazavyHuk = BAZAVY_HUK.д;
                    break;
                case ц:
                    if (huk.padzielPasla != 0 && huk.miakki != 0 && nastupny.miakki == 0) {
                        // мяккі зычны перад цьвёрдым звонкім на сутыку
                        why.add("Азванчэнне ц->д");
                        huk.bazavyHuk = BAZAVY_HUK.д;
                    } else {
                        why.add("Азванчэнне ц->дз");
                        huk.bazavyHuk = BAZAVY_HUK.дз;
                    }
                    break;
                case с:
                    why.add("Азванчэнне с->з");
                    huk.bazavyHuk = BAZAVY_HUK.з;
                    break;
                case ш:
                    why.add("Азванчэнне ш->ж");
                    huk.bazavyHuk = BAZAVY_HUK.ж;
                    break;
                case ч:
                    why.add("Азванчэнне ч->дж");
                    huk.bazavyHuk = BAZAVY_HUK.дж;
                    break;
                case х:
                    why.add("Азванчэнне х->г");
                    huk.bazavyHuk = BAZAVY_HUK.г;
                    break;
                case к:
                    why.add("Азванчэнне к->ґ");
                    huk.bazavyHuk = BAZAVY_HUK.ґ;
                    break;
                }
            } else if (nastupny == null || isHluchi(nastupny)) {
                // аглушэнне
                switch (huk.bazavyHuk) {
                case б:
                    why.add("Аглушэнне б->п");
                    huk.bazavyHuk = BAZAVY_HUK.п;
                    break;
                case д:
                    why.add("Аглушэнне д->т");
                    huk.bazavyHuk = BAZAVY_HUK.т;
                    break;
                case дз:
                    why.add("Аглушэнне дз->ц");
                    huk.bazavyHuk = BAZAVY_HUK.ц;
                    break;
                case з:
                    why.add("Аглушэнне з->с");
                    huk.bazavyHuk = BAZAVY_HUK.с;
                    break;
                case ж:
                    why.add("Аглушэнне ж->ш");
                    huk.bazavyHuk = BAZAVY_HUK.ш;
                    break;
                case дж:
                    why.add("Аглушэнне дж->ч");
                    huk.bazavyHuk = BAZAVY_HUK.ч;
                    break;
                case г:
                    why.add("Аглушэнне г->х");
                    huk.bazavyHuk = BAZAVY_HUK.х;
                    break;
                case ґ:
                    why.add("Аглушэнне ґ->к");
                    huk.bazavyHuk = BAZAVY_HUK.к;
                    break;
                }
            }
        }
    }

    @Deprecated
    boolean isZvonki(Huk huk) {
        if (huk.miakki != 0) {
            return huk.bazavyHuk == BAZAVY_HUK.б || huk.bazavyHuk == BAZAVY_HUK.д || huk.bazavyHuk == BAZAVY_HUK.дз || huk.bazavyHuk == BAZAVY_HUK.з
                    || huk.bazavyHuk == BAZAVY_HUK.ґ || huk.bazavyHuk == BAZAVY_HUK.г;
        } else {
            return huk.bazavyHuk == BAZAVY_HUK.б || huk.bazavyHuk == BAZAVY_HUK.д || huk.bazavyHuk == BAZAVY_HUK.дз || huk.bazavyHuk == BAZAVY_HUK.з
                    || huk.bazavyHuk == BAZAVY_HUK.ж || huk.bazavyHuk == BAZAVY_HUK.дж || huk.bazavyHuk == BAZAVY_HUK.г || huk.bazavyHuk == BAZAVY_HUK.ґ;
        }
    }

    @Deprecated
    boolean isHluchi(Huk huk) {
        if (huk.miakki != 0) {
            return huk.bazavyHuk == BAZAVY_HUK.п || huk.bazavyHuk == BAZAVY_HUK.ц || huk.bazavyHuk == BAZAVY_HUK.с || huk.bazavyHuk == BAZAVY_HUK.к
                    || huk.bazavyHuk == BAZAVY_HUK.х || huk.bazavyHuk == BAZAVY_HUK.ф;
        } else {
            return huk.bazavyHuk == BAZAVY_HUK.п || huk.bazavyHuk == BAZAVY_HUK.т || huk.bazavyHuk == BAZAVY_HUK.ц || huk.bazavyHuk == BAZAVY_HUK.с
                    || huk.bazavyHuk == BAZAVY_HUK.ш || huk.bazavyHuk == BAZAVY_HUK.ч || huk.bazavyHuk == BAZAVY_HUK.х || huk.bazavyHuk == BAZAVY_HUK.к
                    || huk.bazavyHuk == BAZAVY_HUK.ф;
        }
    }

    /**
     * Пазначаем мяккасьць гукаў перад мяккімі галоснымі і перад 'ь'.
     */
    void paznacajemMiakkasc() {
        boolean miakkasc = false;
        for (int i = huki.size() - 1; i >= 0; i--) {
            Huk huk = huki.get(i);
            Huk nastupny = i < huki.size() - 1 ? huki.get(i + 1) : null;
            if (!huk.halosnaja && huk.padzielPasla != 0) {
                // miakkasc = huk.miakki != 0;
                // continue;
            }
            if (huk.padzielPasla == Huk.PADZIEL_SLOVY || huk.padzielPasla == Huk.PADZIEL_MINUS || huk.padzielPasla == Huk.PADZIEL_PRYSTAUKA) {
                // мяккасць не перакрочвае мяжу слова: "лёс Ігара", "лёс вядомы"
                // але калі яна дадатковы элемент іншых працэсаў - то пераходзіць: "лёс сёння"
                //miakkasc = false;
            }
            if (huk.halosnaja) {
                // зьмягчаеццца перад мяккімі галоснымі
                miakkasc = huk.miakkajaHalosnaja;
            } else if (huk.apostrafPasla) {
                //why.add("Перад апострафам '" + huk + "' не змякчаецца");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.р || huk.bazavyHuk == BAZAVY_HUK.ч || huk.bazavyHuk == BAZAVY_HUK.дж || huk.bazavyHuk == BAZAVY_HUK.ш
                    || huk.bazavyHuk == BAZAVY_HUK.ж) {
                // why.add("Не змякчаецца '" + huk + "' бо зацьвярдзелыя р ж ш дж ч ніколі не
                // бываюць мяккімі");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.ў || huk.bazavyHuk == BAZAVY_HUK.j) {
                // паўгалосны
                // why.add("Не змякчаецца '" + huk + "' бо ён паўгалосны");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.б || huk.bazavyHuk == BAZAVY_HUK.п || huk.bazavyHuk == BAZAVY_HUK.м || huk.bazavyHuk == BAZAVY_HUK.ɱ
                    || huk.bazavyHuk == BAZAVY_HUK.ф || (huk.bazavyHuk == BAZAVY_HUK.л && huk.miakki == 0)) {
                // бпмфл не зьмягчаецца перад наступным мяккім зычным
                if (nastupny != null) {
                    if (huk.bazavyHuk.equals(nastupny.bazavyHuk)) {
                        // акрамя выпадкаў калі далей ідзе такі самы зычны(бпмфл)
                    } else if (nastupny.halosnaja) {
                        miakkasc = nastupny.miakkajaHalosnaja;
                    } else {
                        // бпмф не зьмягчаецца перад наступным мяккім зычным, але не перад галоснай
                        miakkasc = false;
                    }
                }
                huk.setMiakkasc(miakkasc);
            } else if (huk.bazavyHuk == BAZAVY_HUK.ґ || huk.bazavyHuk == BAZAVY_HUK.г || huk.bazavyHuk == BAZAVY_HUK.к || huk.bazavyHuk == BAZAVY_HUK.х) {
                // яны самі зьмягчаюцца толькі перад галоснымі(але не перад зычнымі), і не даюць
                // зьмягчацца гукам
                // перад імі: аб'едкі, вянгляр
                if (nastupny != null) {
                    if (huk.bazavyHuk.equals(nastupny.bazavyHuk)) {
                        // акрамя выпадкаў калі далей ідзе такі самы зычны(бпмгкх)
                    } else if (nastupny.halosnaja) {
                        huk.setMiakkasc(nastupny.miakkajaHalosnaja);
                    }
                }
                // не даюць зьмягчацца гукам перад імі
                miakkasc = false; // TODO праверыць падваеньне гкх // не мусіць перакрочваць мяжу слова ?
            } else if (huk.bazavyHuk == BAZAVY_HUK.д) {
                if (nastupny != null && nastupny.miakki != 0
                        && (nastupny.bazavyHuk == BAZAVY_HUK.н || nastupny.bazavyHuk == BAZAVY_HUK.с || nastupny.bazavyHuk == BAZAVY_HUK.з)) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk == BAZAVY_HUK.т) {
                if (nastupny != null && nastupny.miakki != 0
                        && (nastupny.bazavyHuk == BAZAVY_HUK.н || nastupny.bazavyHuk == BAZAVY_HUK.с || nastupny.bazavyHuk == BAZAVY_HUK.з)) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk == BAZAVY_HUK.дз && nastupny != null && nastupny.bazavyHuk == BAZAVY_HUK.л) {
                huk.setMiakkasc(false);
                miakkasc = false;
            } else if (nastupny != null && huk.is(BAZAVY_HUK.н, null, false, null)
                    && (nastupny.is(BAZAVY_HUK.в, Huk.MASK_MIAKKASC_USIE, false, null) || nastupny.is(BAZAVY_HUK.ф, Huk.MASK_MIAKKASC_USIE, false, null))) {
                // Перад губнымі [в’], [ф’] у двухчленных спалучэннях часцей сустракаецца цвёрды
                // н
                if (huk.miakki != Huk.MIAKKASC_PAZNACANAJA) {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                } else {
                    miakkasc = true;
                }
            } else if (huk.miakki != 0) {
                // зьмягчаеццца перад мяккімі зычнымі(калі быў 'ь')
                miakkasc = true;
            } else {
                // цьвёрды зычны - зьмягчаецца ў залежнасьці ад таго што ідзе далей
                huk.setMiakkasc(miakkasc);
            }
        }
    }

    // TODO дадаць націскі
    // TODO прыстаўкі перад еёюя - толькі калі ёсць апостраф
    static final String[] PRYSTAUKI = new String[] { "ад", "безад", "беспад", "вод", "звод", "наад", "навод", "напад", "над", "неад", "непад", "непрад",
            "павод", "панад", "папад", "падад", "пад", "перапад", "перад", "под", "прад", "прыад", "прыпад", "спад", "спрад", "за", "з",
            "супад"/*
                    * ,
                    * 
                    * "абяс", "ас", "абес", "адс", "бес", "бяс", "вус", "выс", "дас", "дыс",
                    * "зрас", "зас", "нарас", "нас", "небес", "небяс", "церас", "ус", "наўс",
                    * "не-рас", "не-с", "не-ўс", "пера-рас", "пера-с", "ня-с", "ня-ўс", "па-па-с",
                    * "па-рас", "прас", "пра-с", "рас", "рас-с", "па-ўс", "пры-с", "рос", "с",
                    * "са-с", "у-рас", "у-рос", "па-с"
                    */ , "між", "звыш", "контр", "гіпер", "супер", "экс", "обер", "супраць", "абез", "без", "беc", "бяз", "бяс", "вуз" };

    static {
        Arrays.sort(PRYSTAUKI, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
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
        if (w.indexOf(GrammarDB2.pravilny_nacisk) >= 0 || w.contains("|")) {
            // ужо пазначана
            return w;
        }

        String dbMorph = config.finder.getMorph(w);
        if (dbMorph != null) {
            why.add("Марфемы з базы: " + dbMorph);
            return dbMorph;
        }

        Set<String> foundForms = new TreeSet<>();
        // у базе няма марфалогіі - спрабуем выцягнуць націскі і ґ
        f1: for (Paradigm p : config.finder.getParadigms(w)) {
            for (Variant v : p.getVariant()) {
                for (Form f : v.getForm()) {
                    if (f.getValue().isEmpty()) {
                        continue;
                    }
                    if (compareWord(w, f.getValue())) {
                        foundForms.add(f.getValue().toLowerCase());
                        String neww = f.getValue().replace('+', GrammarDB2.pravilny_nacisk);
                        if (!w.equals(neww)) {
                            w = neww;
                            why.add("Націскі з базы: " + w);
                        }
                        break f1;
                    }
                }
            }
        }
        if (foundForms.size() == 1) {
            String neww = foundForms.iterator().next().replace('+', GrammarDB2.pravilny_nacisk);
            if (!w.equals(neww)) {
                w = neww;
                why.add("Націскі і пазначэнне ґ з базы: " + w);
            }
        } else {
            // націскі на о, ё
            int p = w.indexOf('о');
            if (p < 0) {
                p = w.indexOf('ё');
            }
            if (p >= 0) {
                w = w.substring(0, p + 1) + GrammarDB2.pravilny_nacisk + w.substring(p + 1);
            }
        }

        // пазначаем найбольш распаўсюджаныя прыстаўкі
        String wl = w.toLowerCase();
        for (String p : PRYSTAUKI) {
            if (wl.length() > p.length() + 2 && wl.startsWith(p)) {
                boolean prystauka = false;
                char nextLetter = wl.charAt(p.length());
                char nextLetter2 = wl.charAt(p.length() + 1);
                if (nextLetter == GrammarDB2.pravilny_apostraf && (nextLetter2 == 'е' || nextLetter2 == 'ё' || nextLetter2 == 'ю' || nextLetter2 == 'я')) {
                    // прыстаўкі перад еёюя - толькі калі ёсць апостраф
                    prystauka = true;
                } else if (nextLetter == 'е' || nextLetter == 'ё' || nextLetter == 'ю' || nextLetter == 'я') {
                    prystauka = false;
                } else {
                    prystauka = true;
                }
                if (prystauka) {
                    w = w.substring(0, p.length()) + '|' + w.substring(p.length());
                    why.add("Мяркуем, што прыстаўка '" + p + "'");
                }
            }
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
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ё':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("ё", BAZAVY_HUK.о);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
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
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'й':
                novyHuk = new Huk("й", BAZAVY_HUK.j);
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
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'я':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("я", BAZAVY_HUK.а);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case GrammarDB2.pravilny_apostraf:
                if (papiaredniHuk != null) {
                    papiaredniHuk.apostrafPasla = true;
                }
                break;
            case '|':
                papiaredniHuk.padzielPasla = Huk.PADZIEL_PRYSTAUKA;
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
        if ((papiaredni == null || papiaredni.halosnaja || papiaredni.apostrafPasla || papiaredni.miakki != 0 || papiaredni.zychodnyjaLitary.equals("ў"))) {
            if (papiaredni != null && papiaredni.padzielPasla != 0/* && !papiaredni.apostrafPasla */) {
                // return;
            }
            // перад ненаціскным 'і' - непатрэбна
            if (papiaredni == null) {
                if (current == 'і' && next != GrammarDB2.pravilny_nacisk) {
                    return;
                }
            } else {
                if (!papiaredni.halosnaja && !papiaredni.apostrafPasla && current == 'і' && next != GrammarDB2.pravilny_nacisk) {
                    return;
                }
            }
            // першая літара ці пасьля пералічаных
            Huk jot = new Huk("", BAZAVY_HUK.j);
            jot.miakkajaHalosnaja = true;
            jot.halosnaja = true;
            jot.setMiakkasc(true);
            huki.add(jot);
        } else if (papiaredni != null && "еёюя".indexOf(current) >= 0 && ("тдржшч".indexOf(papiaredni.zychodnyjaLitary) >= 0 || papiaredni.padzielPasla != 0)) {
            // звычайна сутык прыстаўкі і кораня
            Huk jot = new Huk("", BAZAVY_HUK.j);
            jot.miakkajaHalosnaja = true;
            jot.halosnaja = true;
            jot.setMiakkasc(true);
            huki.add(jot);
        }
    }

    boolean firstSkladNacisk(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = Character.toLowerCase(word.charAt(i));
            if ("ёуеыаоэяію".indexOf(c) >= 0) {
                if (i < word.length() - 1) {
                    char c1 = word.charAt(i + 1);
                    return c1 == GrammarDB2.pravilny_nacisk;
                }
            }
        }
        return false;
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
                ii++;
                continue;
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
}
