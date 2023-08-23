package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

import org.alex73.corpus.paradigm.Form;
import org.alex73.corpus.paradigm.Paradigm;
import org.alex73.corpus.paradigm.Variant;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.korpus.base.GrammarFinder;
import org.alex73.korpus.languages.belarusian.BelarusianWordNormalizer;

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
    private final GrammarFinder finder;
    List<Huk> huki = new ArrayList<>();
    List<String> words = new ArrayList<>();
    List<String> why = new ArrayList<>(); // як адбываюцца пераходы

    public Fanetyka3(GrammarFinder finder) {
        this.finder = finder;
    }

    public void addWord(String w) {
        words.add(normalize(w));
    }

    public void calcFanetyka() {
        for (int i = 0; i < words.size(); i++) {
            String w = words.get(i);
            if (i < words.size() - 1) {
                // "без,не -> бяз,ня" перад словамі з націскам на першы склад
                String wl = w.toLowerCase();
                switch (wl) {
                case "не":
                case "не" + BelarusianWordNormalizer.pravilny_nacisk:
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "ня";
                        why.add("'не' пераходзіць у 'ня' перад словам з націскам на першы склад");
                    }
                    break;
                case "без":
                case "без" + BelarusianWordNormalizer.pravilny_nacisk:
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "бяз";
                        why.add("'без' пераходзіць у 'бяз' перад словам з націскам на першы склад");
                    }
                    break;
                }
            }
            if (!fanetykaBazy(w)) {
                w = narmalizacyjaSlova(w.toLowerCase());
                stvarajemBazavyjaHuki(w);
            }
        }
        String prev = toString();
        int pass = 0;
        while (true) {
            pierachodZHA();
            pierachodI();
            paznacajemMiakkasc();
            ahlusennieIazvancennie();
            sprascennie();
            prypadabniennie();
            sypiacyjaSvisciacyja();
            pierachodTS();
            pierachodV();
            pierachodM();
            padvajennie();
            ustaunojeA();
            pierachodFH();
            pierachodZG();
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
            if (h.bazavyHuk == BAZAVY_HUK.u̯ || h.bazavyHuk == BAZAVY_HUK.j || h.bazavyHuk == BAZAVY_HUK.r) {
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

    public static String fanetykaSlova(Fanetyka3 r, String w) {
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
            if (nastupny.bazavyHuk == BAZAVY_HUK.r && nastupny.miakki == 0) {
                if (!huk.halosnaja && huk.bazavyHuk != BAZAVY_HUK.r && huk.bazavyHuk != BAZAVY_HUK.j && huk.bazavyHuk != BAZAVY_HUK.u̯) {
                    Huk a = new Huk("", BAZAVY_HUK.a);
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                    why.add("Дадаецца 'а'");
                }
            } else if (nastupny.bazavyHuk == BAZAVY_HUK.ɫ && nastupny.miakki != 0) {
                if (huk.bazavyHuk == BAZAVY_HUK.r || huk.bazavyHuk == BAZAVY_HUK.t͡ʂ || huk.bazavyHuk == BAZAVY_HUK.d͡ʐ || huk.bazavyHuk == BAZAVY_HUK.ʂ
                        || huk.bazavyHuk == BAZAVY_HUK.ʐ || huk.bazavyHuk == BAZAVY_HUK.b || huk.bazavyHuk == BAZAVY_HUK.p || huk.bazavyHuk == BAZAVY_HUK.m
                        || huk.bazavyHuk == BAZAVY_HUK.f || huk.bazavyHuk == BAZAVY_HUK.ɣ || huk.bazavyHuk == BAZAVY_HUK.k || huk.bazavyHuk == BAZAVY_HUK.x) {
                    Huk a = new Huk("", BAZAVY_HUK.a);
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
            if (huk.is(BAZAVY_HUK.f, null, false, 0) && nastupny.is(BAZAVY_HUK.ɣ, null, false, 0)) {
                huk.bazavyHuk = BAZAVY_HUK.u̯;
            }
        }
    }

    /**
     * зг, дзг, джг паміж галосных(ці напачатку слова) пераходзяць у зґ, дзґ, джґ
     */
    void pierachodZG() {
        for (int i = 0; i < huki.size() - 3; i++) {
            Huk h1 = i > 0 ? huki.get(i - 1) : null;
            Huk h2 = huki.get(i);
            Huk h3 = huki.get(i + 1);
            // Huk h4 = huki.get(i + 3);
            if (h1 == null || (h1.halosnaja && !h1.apostrafPasla)) {
                if (h2.padzielPasla == 0 && (h2.bazavyHuk == BAZAVY_HUK.z || h2.bazavyHuk == BAZAVY_HUK.d͡z || h2.bazavyHuk == BAZAVY_HUK.d͡ʐ)
                        && !h2.apostrafPasla && !h2.padvojeny) {
                    if (h3.bazavyHuk == BAZAVY_HUK.ɣ && !h3.apostrafPasla && !h3.padvojeny && (h3.padzielPasla == 0 || h3.padzielPasla == Huk.PADZIEL_KORANI)) {
                        h3.bazavyHuk = BAZAVY_HUK.g;
                        why.add("Пераход 'зг, дзг, джг' -> 'зґ, дзґ, джґ' паміж галосных і не на сутыку прыстаўкі і кораня");
                    }
                }
            }
        }
    }

    /**
     * zɣ -> zaɣ, zg -> zag
     */
    void pierachodZHA() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.is(BAZAVY_HUK.z, 0, false, 0) && (nastupny.is(BAZAVY_HUK.ɣ, 0, false, null) || nastupny.is(BAZAVY_HUK.g, 0, false, null))) {
                if (nastupny.padzielPasla == Huk.PADZIEL_SLOVY) {
                    why.add("Устаўное 'а': zɣ -> zaɣ, zg -> zag"); // TODO толькі для "мозг" ? ён па-за сістэмны
                    Huk a = new Huk("", BAZAVY_HUK.a);
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                }
            }
        }
    }

    /**
     * 't' і 's' пераходзіць у 'c', толькі калі няма падзелу
     */
    void pierachodTS() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.padzielPasla != 0) {
                continue;
            }
            if (huk.miakki == 0 && nastupny.miakki == 0) {
                if (huk.bazavyHuk == BAZAVY_HUK.t && nastupny.bazavyHuk == BAZAVY_HUK.s) {
                    why.add("'t' і 's' пераходзіць у 'c', толькі калі няма падзелу");
                    huk.bazavyHuk = BAZAVY_HUK.t͡s;
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
            if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t͡s, BAZAVY_HUK.k)) { // с-ц-к
                if (h1.miakki == 0 && h2.miakki == 0) {
                    why.add("Спрашчэнне: с-ц-к -> с-к");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t, BAZAVY_HUK.t͡ʂ)) { // с-т-ч
                if (h2.miakki == 0 && h1.miakki == 0 && h3.miakki == 0) {
                    // сярэдні выпадае
                    why.add("Спрашчэнне: с-т-ч -> с-ч, як 'даездчык'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t, BAZAVY_HUK.n)) { // с-т-н
                why.add("Спрашчэнне: с-т-н -> с-н");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.z, BAZAVY_HUK.d, BAZAVY_HUK.n)) { // з-д-н
                why.add("Спрашчэнне: з-д-н -> з-н");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
                h1.miakki = h3.miakki;
            } else if (areHuki(i, BAZAVY_HUK.r, BAZAVY_HUK.k, BAZAVY_HUK.s, BAZAVY_HUK.k)) { // р-к-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && !h4.padvojeny) {
                    why.add("Спрашчэнне: р-к-с-к -> р-c-к, як 'цюркскі'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.s, BAZAVY_HUK.k)) { // с-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && h1.padzielPasla == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: с-с-к -> c-к не на сутыку, як 'гагаузскі'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.s, BAZAVY_HUK.k)) { // с-с-к
                if (!h1.padvojeny && !h2.padvojeny && !h3.padvojeny && h1.padzielPasla != 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: с-с-к -> c:-к на сутыку, як 'бяссківічны'");
                    h1.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    h1.padvojeny = true;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.z, BAZAVY_HUK.d, BAZAVY_HUK.t͡ʂ)) { // з-д-ч
                if (!h1.padvojeny && h2.padvojeny && h3.padvojeny && h1.padzielPasla == 0 && h2.padzielPasla == 0 && h1.miakki == 0 && h2.miakki == 0
                        && h3.miakki == 0) {
                    why.add("Спрашчэнне: з-д-ч -> ш-ч: як 'аб’ездчык'");
                    h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
                    h2.bazavyHuk = BAZAVY_HUK.ʂ;
                    huki.remove(i);
                }
            } else if (areHuki(i, BAZAVY_HUK.ʂ, BAZAVY_HUK.s)) { // ш-с
                if (h1.miakki == 0 && h1.padzielPasla == 0) {
                    why.add("Спрашчэнне: ш-с -> с-с");
                    h1.bazavyHuk = BAZAVY_HUK.s;
                }
            } else if (areHuki(i, BAZAVY_HUK.t͡s, BAZAVY_HUK.t)) { // ц-т
                if (h1.miakki != 0 && (h1.padzielPasla & Huk.PADZIEL_PRYSTAUKA) != 0 && h2.miakki == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: ц'-т -> т'-т");
                    h1.bazavyHuk = BAZAVY_HUK.t;
                }
            } else if (areHuki(i, BAZAVY_HUK.t͡s, BAZAVY_HUK.t͡s)) { // ц-ц
                if (h1.miakki != 0 && (h1.padzielPasla & Huk.PADZIEL_PRYSTAUKA) != 0 && h2.miakki == 0 && h2.padzielPasla == 0) {
                    why.add("Спрашчэнне: ц'-ц -> т'-ц");
                    h1.bazavyHuk = BAZAVY_HUK.t;
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t͡s, BAZAVY_HUK.t) && h3 != null && isHluchi(h3) && !isSypiacy(h3)) { // с-ц_глухі-нешыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(глухі нешыпячы ў наступным слове) -> c', як 'дасць талацэ'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t͡s, BAZAVY_HUK.t) && h3 != null && isHluchi(h3) && isSypiacy(h3)) { // с-ц_глухі-шыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(глухі шыпячы ў наступным слове) -> ш, як 'дасць шырокі'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.ʂ;
                    h1.miakki = 0;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t͡s, BAZAVY_HUK.t) && h3 != null && isZvonki(h3) && !isSypiacy(h3)) { // с-ц_звонкі-нешыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(звонкі нешыпячы ў наступным слове) -> з', як 'дасць дачцэ'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.z;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.s, BAZAVY_HUK.t͡s, BAZAVY_HUK.t) && h3 != null && isZvonki(h3) && isSypiacy(h3)) { // с-ц_звонкі-шыпячы
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: с'-ц' +(звонкі шыпячы ў наступным слове) -> ж, як 'дасць жыцця'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    h1.bazavyHuk = Huk.BAZAVY_HUK.ʐ;
                    h1.miakki = 0;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.z, BAZAVY_HUK.d) && h3 != null && isZvonki(h3)) { // з-д // TODO небывае ? трэба прыклад
                if (h1.miakki != 0 && h1.padzielPasla == 0 && h2.miakki != 0 && !h2.padvojeny
                        && (h2.padzielPasla & (Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) != 0) {
                    why.add("Спрашчэнне: з'-д' +(звонкі ў наступным слове) -> з', як 'дасць заснуць'");
                    h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                    huki.remove(i + 1);
                }
            } else if (areHuki(i, BAZAVY_HUK.ʐ, BAZAVY_HUK.d͡ʐ)) { // ж-дж
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
            } else if (areHuki(i, BAZAVY_HUK.n, BAZAVY_HUK.t, BAZAVY_HUK.ʂ, BAZAVY_HUK.t͡ʂ)) { // н-т-ш-ч
                why.add("Спрашчэнне: н-т-ш-ч -> н-ш-ч");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.n, BAZAVY_HUK.d, BAZAVY_HUK.ʂ)) { // н-д-ш
                why.add("Спрашчэнне: н-д-ш -> н-ш");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.n, BAZAVY_HUK.t, BAZAVY_HUK.ʂ)) { // н-т-ш
                why.add("Спрашчэнне: н-т-ш -> н-ш");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.n, BAZAVY_HUK.t, BAZAVY_HUK.s)) { // н-т-с
                why.add("Спрашчэнне: н-т-с -> н-с, як 'бургундскі'");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.b, BAZAVY_HUK.r, BAZAVY_HUK.s)) { // б-р-с
                why.add("Спрашчэнне: б-р-с -> б-с, як 'акцябрскі'");
                h1.zychodnyjaLitary += h2.zychodnyjaLitary;
                huki.remove(i + 1);
            } else if (areHuki(i, BAZAVY_HUK.m, BAZAVY_HUK.ɫ, BAZAVY_HUK.s)) { // м-ль-с
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
     * 'v' перад 'о' і 'у' пераходзіць у 'β'
     */
    void pierachodV() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.miakki == 0 && huk.bazavyHuk == BAZAVY_HUK.v) {
                if (!nastupny.miakkajaHalosnaja && (nastupny.bazavyHuk == BAZAVY_HUK.ɔ || nastupny.bazavyHuk == BAZAVY_HUK.u)) {
                    why.add("'v' перад 'о' і 'у' пераходзіць у 'β'");
                    huk.bazavyHuk = BAZAVY_HUK.β;
                }
            }
        }
    }

    /**
     * 'm' перад 'f', 'v', 'β' пераходзіць у 'ɱ'
     */
    void pierachodM() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.miakki == 0 && huk.bazavyHuk == BAZAVY_HUK.m) {
                if (nastupny.bazavyHuk == BAZAVY_HUK.f || nastupny.bazavyHuk == BAZAVY_HUK.v || nastupny.bazavyHuk == BAZAVY_HUK.β) {
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
            if (huk.bazavyHuk == BAZAVY_HUK.i && (papiaredni.bazavyHuk == BAZAVY_HUK.d || papiaredni.bazavyHuk == BAZAVY_HUK.t
                    || papiaredni.bazavyHuk == BAZAVY_HUK.ʐ || papiaredni.bazavyHuk == BAZAVY_HUK.ʂ || papiaredni.bazavyHuk == BAZAVY_HUK.t͡ʂ
                    || papiaredni.bazavyHuk == BAZAVY_HUK.d͡ʐ || papiaredni.bazavyHuk == BAZAVY_HUK.r)) {
                why.add("'і' перад д т ж ш ч дж пераходзіць у ɨ");
                huk.bazavyHuk = BAZAVY_HUK.ɨ;
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
            if (huk.is(BAZAVY_HUK.d, 0, false, null) && nastupny.is(BAZAVY_HUK.t͡ʂ, 0, false, null)) {
                why.add("Прыпадабненне:");
                huk.bazavyHuk = BAZAVY_HUK.t;
            } else if (huk.is(BAZAVY_HUK.d, 0, false, null) && nastupny.is(BAZAVY_HUK.t͡s, 0, false, null)) {
                why.add("Прыпадабненне:");
                huk.bazavyHuk = BAZAVY_HUK.t;
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
                    why.add("Падваенне: " + huk);
                } else {
                    // як 'пражскі'
                    huk.padvojeny = false;
                    why.add("Спрашчэнне: " + huk);
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
            } else if (huk.bazavyHuk == BAZAVY_HUK.d͡ʐ && papiaredni.bazavyHuk == BAZAVY_HUK.d) {
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
            } else if (huk.bazavyHuk == BAZAVY_HUK.d͡z && papiaredni.bazavyHuk == BAZAVY_HUK.d) {
                why.add("Падваенне: д+дз => дз:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk == BAZAVY_HUK.t͡ʂ && papiaredni.bazavyHuk == BAZAVY_HUK.t) {
                why.add("Падваенне: т+ч => ч:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk == BAZAVY_HUK.t͡s && papiaredni.bazavyHuk == BAZAVY_HUK.t) {
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
            } else if (papiaredni.is(BAZAVY_HUK.g, 0, false, null) && huk.is(BAZAVY_HUK.ɣ, null, false, null)) {
                why.add("Падваенне: ґ+г => г:, як 'банк-гарант'");
                huk.bazavyHuk = BAZAVY_HUK.ɣ;
                huk.padvojeny = true;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            }
        }
    }

    boolean isSypiacy(Huk huk) {
        if (huk.miakki != 0) {
            return false;
        }
        return huk.bazavyHuk == BAZAVY_HUK.ʂ || huk.bazavyHuk == BAZAVY_HUK.ʐ || huk.bazavyHuk == BAZAVY_HUK.d͡ʐ || huk.bazavyHuk == BAZAVY_HUK.t͡ʂ;
    }

    boolean isSvisciacy(Huk huk) {
        if (huk.miakki != 0) {
            return false;
        }
        return huk.bazavyHuk == BAZAVY_HUK.s || huk.bazavyHuk == BAZAVY_HUK.z || huk.bazavyHuk == BAZAVY_HUK.d͡z || huk.bazavyHuk == BAZAVY_HUK.t͡s;
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
                case ʂ: // ш+свісцячы
                    why.add("Пераход у свісцячыя: ш->с");
                    huk.bazavyHuk = BAZAVY_HUK.s;
                    break;
                case ʐ:
                    why.add("Пераход у свісцячыя: ж->з");
                    huk.bazavyHuk = BAZAVY_HUK.z;
                    break;
                case d͡ʐ:
                    why.add("Пераход у свісцячыя: дж->дз");
                    huk.bazavyHuk = BAZAVY_HUK.d͡z;
                    break;
                case t͡ʂ:
                    why.add("Пераход у свісцячыя: ч->ц");
                    huk.bazavyHuk = BAZAVY_HUK.t͡s;
                    break;
                }
            } else if (isSypiacy(nastupny) /* && !huk.miakki */ && huk.padzielPasla == 0) {
                // пераход свісцячых у шыпячыя
                // пры пераходзе ў шыпячыя мяккасць знікае, нават калі была пазначаная:
                // бязьджаўковая
                switch (huk.bazavyHuk) {
                case t: // т+шыпячы
                    why.add("Пераход у шыпячыя: т->ч");
                    huk.bazavyHuk = BAZAVY_HUK.t͡ʂ;
                    huk.miakki = 0;
                    break;
                case s:
                    why.add("Пераход у шыпячыя: с->ш");
                    huk.bazavyHuk = BAZAVY_HUK.ʂ;
                    huk.miakki = 0;
                    break;
                case z:
                    // праблема: бязьджаўковая
                    why.add("Пераход у шыпячыя: з->ж");
                    huk.bazavyHuk = BAZAVY_HUK.ʐ;
                    huk.miakki = 0;
                    break;
                case d͡z:
                    // праблема: "сядзь жа"
                    /*
                     * на сутыку: шыпячых мяккіх у беларускай мове няма(усярэдзіне слоў), але каб
                     * зрабіць пераход "вось што" у "ш:", трэба апрацоўваць "сьш", таму праверка на
                     * мяккасць папярэдняга - неабавязковая
                     */
                    why.add("Пераход у шыпячыя: дз->дж");
                    huk.bazavyHuk = BAZAVY_HUK.d͡ʐ;
                    huk.miakki = 0;
                    break;
                case t͡s:
                    // праблема: "сядзь шалёны"
                    why.add("Пераход у шыпячыя: ц->ч");
                    huk.bazavyHuk = BAZAVY_HUK.t͡ʂ;
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
                case p:
                    why.add("Азванчэнне п->б");
                    huk.bazavyHuk = BAZAVY_HUK.b;
                    break;
                case t:
                    why.add("Азванчэнне т->д");
                    huk.bazavyHuk = BAZAVY_HUK.d;
                    break;
                case t͡s:
                    if (huk.padzielPasla != 0 && huk.miakki != 0 && nastupny.miakki == 0) {
                        // мяккі зычны перад цьвёрдым звонкім на сутыку
                        why.add("Азванчэнне ц->д");
                        huk.bazavyHuk = BAZAVY_HUK.d;
                    } else {
                        why.add("Азванчэнне ц->дз");
                        huk.bazavyHuk = BAZAVY_HUK.d͡z;
                    }
                    break;
                case s:
                    why.add("Азванчэнне с->з");
                    huk.bazavyHuk = BAZAVY_HUK.z;
                    break;
                case ʂ:
                    why.add("Азванчэнне ш->ж");
                    huk.bazavyHuk = BAZAVY_HUK.ʐ;
                    break;
                case t͡ʂ:
                    why.add("Азванчэнне ч->дж");
                    huk.bazavyHuk = BAZAVY_HUK.d͡ʐ;
                    break;
                case x:
                    why.add("Азванчэнне х->г");
                    huk.bazavyHuk = BAZAVY_HUK.ɣ;
                    break;
                case k:
                    why.add("Азванчэнне к->ґ");
                    huk.bazavyHuk = BAZAVY_HUK.g;
                    break;
                }
            } else if (nastupny == null || isHluchi(nastupny)) {
                // аглушэнне
                switch (huk.bazavyHuk) {
                case b:
                    why.add("Аглушэнне б->п");
                    huk.bazavyHuk = BAZAVY_HUK.p;
                    break;
                case d:
                    why.add("Аглушэнне д->т");
                    huk.bazavyHuk = BAZAVY_HUK.t;
                    break;
                case d͡z:
                    why.add("Аглушэнне дз->ц");
                    huk.bazavyHuk = BAZAVY_HUK.t͡s;
                    break;
                case z:
                    why.add("Аглушэнне з->с");
                    huk.bazavyHuk = BAZAVY_HUK.s;
                    break;
                case ʐ:
                    why.add("Аглушэнне ж->ш");
                    huk.bazavyHuk = BAZAVY_HUK.ʂ;
                    break;
                case d͡ʐ:
                    why.add("Аглушэнне дж->ч");
                    huk.bazavyHuk = BAZAVY_HUK.t͡ʂ;
                    break;
                case ɣ:
                    why.add("Аглушэнне г->х");
                    huk.bazavyHuk = BAZAVY_HUK.x;
                    break;
                case g:
                    why.add("Аглушэнне ґ->к");
                    huk.bazavyHuk = BAZAVY_HUK.k;
                    break;
                }
            }
        }
    }

    boolean isZvonki(Huk huk) {
        if (huk.miakki != 0) {
            return huk.bazavyHuk == BAZAVY_HUK.b || huk.bazavyHuk == BAZAVY_HUK.d || huk.bazavyHuk == BAZAVY_HUK.d͡z || huk.bazavyHuk == BAZAVY_HUK.z
                    || huk.bazavyHuk == BAZAVY_HUK.g || huk.bazavyHuk == BAZAVY_HUK.ɣ;
        } else {
            return huk.bazavyHuk == BAZAVY_HUK.b || huk.bazavyHuk == BAZAVY_HUK.d || huk.bazavyHuk == BAZAVY_HUK.d͡z || huk.bazavyHuk == BAZAVY_HUK.z
                    || huk.bazavyHuk == BAZAVY_HUK.ʐ || huk.bazavyHuk == BAZAVY_HUK.d͡ʐ || huk.bazavyHuk == BAZAVY_HUK.ɣ || huk.bazavyHuk == BAZAVY_HUK.g;
        }
    }

    boolean isHluchi(Huk huk) {
        if (huk.miakki != 0) {
            return huk.bazavyHuk == BAZAVY_HUK.p || huk.bazavyHuk == BAZAVY_HUK.t͡s || huk.bazavyHuk == BAZAVY_HUK.s || huk.bazavyHuk == BAZAVY_HUK.k
                    || huk.bazavyHuk == BAZAVY_HUK.x || huk.bazavyHuk == BAZAVY_HUK.f;
        } else {
            return huk.bazavyHuk == BAZAVY_HUK.p || huk.bazavyHuk == BAZAVY_HUK.t || huk.bazavyHuk == BAZAVY_HUK.t͡s || huk.bazavyHuk == BAZAVY_HUK.s
                    || huk.bazavyHuk == BAZAVY_HUK.ʂ || huk.bazavyHuk == BAZAVY_HUK.t͡ʂ || huk.bazavyHuk == BAZAVY_HUK.x || huk.bazavyHuk == BAZAVY_HUK.k
                    || huk.bazavyHuk == BAZAVY_HUK.f;
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
            if (huk.halosnaja) {
                // зьмягчаеццца перад мяккімі галоснымі
                miakkasc = huk.miakkajaHalosnaja;
            } else if (huk.apostrafPasla) {
                why.add("Перад апострафам '" + huk + "' не змякчаецца");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.r || huk.bazavyHuk == BAZAVY_HUK.t͡ʂ || huk.bazavyHuk == BAZAVY_HUK.d͡ʐ || huk.bazavyHuk == BAZAVY_HUK.ʂ
                    || huk.bazavyHuk == BAZAVY_HUK.ʐ) {
                // why.add("Не змякчаецца '" + huk + "' бо зацьвярдзелыя р ж ш дж ч ніколі не
                // бываюць мяккімі");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.u̯ || huk.bazavyHuk == BAZAVY_HUK.j) {
                // паўгалосны
                // why.add("Не змякчаецца '" + huk + "' бо ён паўгалосны");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk == BAZAVY_HUK.b || huk.bazavyHuk == BAZAVY_HUK.p || huk.bazavyHuk == BAZAVY_HUK.m || huk.bazavyHuk == BAZAVY_HUK.ɱ
                    || huk.bazavyHuk == BAZAVY_HUK.f || (huk.bazavyHuk == BAZAVY_HUK.ɫ && huk.miakki == 0)) {
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
            } else if (huk.bazavyHuk == BAZAVY_HUK.g || huk.bazavyHuk == BAZAVY_HUK.ɣ || huk.bazavyHuk == BAZAVY_HUK.k || huk.bazavyHuk == BAZAVY_HUK.x) {
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
                miakkasc = false; // TODO праверыць падваеньне гкх
            } else if (huk.bazavyHuk == BAZAVY_HUK.d) {
                if (nastupny != null && nastupny.miakki != 0
                        && (nastupny.bazavyHuk == BAZAVY_HUK.n || nastupny.bazavyHuk == BAZAVY_HUK.s || nastupny.bazavyHuk == BAZAVY_HUK.z)) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk == BAZAVY_HUK.t) {
                if (nastupny != null && nastupny.miakki != 0
                        && (nastupny.bazavyHuk == BAZAVY_HUK.n || nastupny.bazavyHuk == BAZAVY_HUK.s || nastupny.bazavyHuk == BAZAVY_HUK.z)) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk == BAZAVY_HUK.d͡z && nastupny != null && nastupny.bazavyHuk == BAZAVY_HUK.ɫ) {
                huk.setMiakkasc(false);
                miakkasc = false;
            } else if (nastupny != null && huk.is(BAZAVY_HUK.n, null, false, null)
                    && (nastupny.is(BAZAVY_HUK.v, Huk.MASK_MIAKKASC_USIE, false, null) || nastupny.is(BAZAVY_HUK.f, Huk.MASK_MIAKKASC_USIE, false, null))) {
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
        String fan = finder.getFan(word);
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
        if (w.indexOf(BelarusianWordNormalizer.pravilny_nacisk) >= 0 || w.contains("|")) {
            // ужо пазначана
            return w;
        }

        String dbMorph = finder.getMorph(w);
        if (dbMorph != null) {
            why.add("Марфемы з базы: " + dbMorph);
            return dbMorph;
        }

        Set<String> foundForms = new TreeSet<>();
        // у базе няма марфалогіі - спрабуем выцягнуць націскі і ґ
        f1: for (Paradigm p : finder.getParadigms(w)) {
            for (Variant v : p.getVariant()) {
                for (Form f : v.getForm()) {
                    if (f.getValue().isEmpty()) {
                        continue;
                    }
                    if (compareWord(w, f.getValue())) {
                        foundForms.add(f.getValue().toLowerCase());
                        w = f.getValue().replace('+', BelarusianWordNormalizer.pravilny_nacisk);
                        why.add("Націскі з базы: " + w);
                        break f1;
                    }
                }
            }
        }
        if (foundForms.size() == 1) {
            w = foundForms.iterator().next().replace('+', BelarusianWordNormalizer.pravilny_nacisk);
            why.add("Націскі і пазначэнне ґ з базы: " + w);
        } else {
            // націскі на о, ё
            int p = w.indexOf('о');
            if (p < 0) {
                p = w.indexOf('ё');
            }
            if (p >= 0) {
                w = w.substring(0, p+1) + BelarusianWordNormalizer.pravilny_nacisk + w.substring(p+1);
            }
        }

        // пазначаем найбольш распаўсюджаныя прыстаўкі
        for (String p : PRYSTAUKI) {
            if (w.length() > p.length() + 2 && w.startsWith(p)) {
                w = w.substring(0, p.length()) + '|' + w.substring(p.length());
                why.add("Мяркуем, што прыстаўка '" + p + "'");
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
                novyHuk = new Huk("а", BAZAVY_HUK.a);
                novyHuk.halosnaja = true;
                break;
            case 'б':
                novyHuk = new Huk("б", BAZAVY_HUK.b);
                break;
            case 'в':
                novyHuk = new Huk("в", BAZAVY_HUK.v);
                break;
            case 'г':
                novyHuk = new Huk("г", BAZAVY_HUK.ɣ);
                break;
            case 'ґ':
                novyHuk = new Huk("ґ", BAZAVY_HUK.g);
                break;
            case 'д':
                novyHuk = new Huk("д", BAZAVY_HUK.d);
                break;
            case 'е':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("е", BAZAVY_HUK.ɛ);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ё':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("ё", BAZAVY_HUK.ɔ);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ж':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary) && papiaredniHuk.padzielPasla == 0) {
                    // дж
                    papiaredniHuk.zychodnyjaLitary = "дж";
                    papiaredniHuk.bazavyHuk = BAZAVY_HUK.d͡ʐ;
                } else {
                    novyHuk = new Huk("ж", BAZAVY_HUK.ʐ);
                }
                break;
            case 'з':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary) && papiaredniHuk.padzielPasla == 0) {
                    // дз
                    papiaredniHuk.zychodnyjaLitary = "дз";
                    papiaredniHuk.bazavyHuk = BAZAVY_HUK.d͡z;
                } else {
                    novyHuk = new Huk("з", BAZAVY_HUK.z);
                }
                break;
            case 'і':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("і", BAZAVY_HUK.i);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'й':
                novyHuk = new Huk("й", BAZAVY_HUK.j);
                break;
            case 'к':
                novyHuk = new Huk("к", BAZAVY_HUK.k);
                break;
            case 'л':
                novyHuk = new Huk("л", BAZAVY_HUK.ɫ);
                break;
            case 'м':
                novyHuk = new Huk("м", BAZAVY_HUK.m);
                break;
            case 'н':
                novyHuk = new Huk("н", BAZAVY_HUK.n);
                break;
            case 'о':
                novyHuk = new Huk("о", BAZAVY_HUK.ɔ);
                novyHuk.halosnaja = true;
                break;
            case 'п':
                novyHuk = new Huk("п", BAZAVY_HUK.p);
                break;
            case 'р':
                novyHuk = new Huk("р", BAZAVY_HUK.r);
                break;
            case 'с':
                novyHuk = new Huk("с", BAZAVY_HUK.s);
                break;
            case 'т':
                novyHuk = new Huk("т", BAZAVY_HUK.t);
                break;
            case 'у':
                novyHuk = new Huk("у", BAZAVY_HUK.u);
                novyHuk.halosnaja = true;
                break;
            case 'ў':
                novyHuk = new Huk("ў", BAZAVY_HUK.u̯);
                break;
            case 'ф':
                novyHuk = new Huk("ф", BAZAVY_HUK.f);
                break;
            case 'х':
                novyHuk = new Huk("х", BAZAVY_HUK.x);
                break;
            case 'ц':
                novyHuk = new Huk("ц", BAZAVY_HUK.t͡s);
                break;
            case 'ч':
                novyHuk = new Huk("ч", BAZAVY_HUK.t͡ʂ);
                break;
            case 'ш':
                novyHuk = new Huk("ш", BAZAVY_HUK.ʂ);
                break;
            case 'ы':
                novyHuk = new Huk("ы", BAZAVY_HUK.ɨ);
                novyHuk.halosnaja = true;
                break;
            case 'ь':
                if (papiaredniHuk != null) {
                    papiaredniHuk.miakki = Huk.MIAKKASC_PAZNACANAJA;
                }
                break;
            case 'э':
                novyHuk = new Huk("э", BAZAVY_HUK.ɛ);
                novyHuk.halosnaja = true;
                break;
            case 'ю':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("ю", BAZAVY_HUK.u);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'я':
                dadacJotKaliPatrebny(papiaredniHuk, c, next);
                novyHuk = new Huk("я", BAZAVY_HUK.a);
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case BelarusianWordNormalizer.pravilny_apostraf:
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
            case BelarusianWordNormalizer.pravilny_nacisk:
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
                if (current == 'і' && next != BelarusianWordNormalizer.pravilny_nacisk) {
                    return;
                }
            } else {
                if (!papiaredni.halosnaja && !papiaredni.apostrafPasla && current == 'і' && next != BelarusianWordNormalizer.pravilny_nacisk) {
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
                    return c1 == BelarusianWordNormalizer.pravilny_nacisk;
                }
            }
        }
        return false;
    }

    static String normalize(String inputWord) {
        StringBuilder s = new StringBuilder();
        for (char c : inputWord.toCharArray()) {
            if (BelarusianWordNormalizer.usie_apostrafy.indexOf(c) >= 0) {
                c = BelarusianWordNormalizer.pravilny_apostraf;
            }
            if (BelarusianWordNormalizer.usie_naciski.indexOf(c) >= 0) {
                c = BelarusianWordNormalizer.pravilny_nacisk;
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
                cg = BelarusianWordNormalizer.pravilny_nacisk;
            }
            if (cg == 'ґ' && ci == 'г') {
                ci = 'ґ';
            }
            if (ci == BelarusianWordNormalizer.pravilny_nacisk && cg != BelarusianWordNormalizer.pravilny_nacisk) {
                ii++;
                continue;
            }
            if (ci != BelarusianWordNormalizer.pravilny_nacisk && cg == BelarusianWordNormalizer.pravilny_nacisk) {
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
