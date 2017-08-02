package org.alex73.korpus.voice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Fanetyka3 {
    List<Huk> huki = new ArrayList<>();
    List<String> words=new ArrayList<>();
    
    public void addWord(String w) {
        words.add(w);
    }

    public String getFanetyka() {
        for(String w:words) {
           stvarajemBazavyjaHuji(w.toLowerCase());
        }
        String h;
        while (true) {
            h = toString();
            pierachodI();
            paznacajemMiakkasc();
            ahlusennieIazvancennie();
            prypadabniennie();
            sypiacyjaSvisciacyja();
            pierachodTS();
            sprascennie();
            pierachodV();
            pierachodM();
            padvajennie();
            ustaunojeA();
            if (h.equals(toString())) {
                break;
            }
        }
        return h;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (Huk huk : huki) {
            out.append(huk.toString());
            if ((huk.padzielPasla & Huk.PADZIEL_SLOVY) != 0) {
                out.append(' ');
            }
        }
        return out.toString().trim();
    }

    public static String fanetykaSlova(String w) {
        Fanetyka3 r=new Fanetyka3();
        r.addWord(w);
        return r.getFanetyka();
    }
    //
    // /**
    // * Часьцей за ўсё калі слова пачынаецца на адж-, гэта прыстаўка "ад"; адс- прыстаўка "ад".
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
    // if (hety.bazavyHuk.equals("d͡ʐ") && !hety.miakki) {
    // Huk d = new Huk("", "d");
    // huki.add(prystLen, d);
    // huki.get(prystLen + 1).bazavyHuk = "ʐ";
    // } else if (hety.bazavyHuk.equals("d͡z") && !hety.padvojeny && !nastupny.halosnaja) {
    // Huk d = new Huk("", "d");
    // d.miakki = false; // заўсёды цьвердае ў прыстаўцы
    // huki.add(prystLen, d);
    // huki.get(prystLen + 1).bazavyHuk = "z";
    // } else if (hety.bazavyHuk.equals("t͡s") && !hety.padvojeny) {
    // Huk t = new Huk("", "t");
    // huki.add(prystLen, t);
    // huki.get(prystLen + 1).bazavyHuk = "s";
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
            if (nastupny.bazavyHuk.equals("r") && !nastupny.miakki) {
                if (!huk.halosnaja && !huk.equals("r") && !huk.equals("j")) {
                    Huk a = new Huk("", "a");
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                }
            } else if (nastupny.bazavyHuk.equals("l") && nastupny.miakki) {
                if (huk.bazavyHuk.equals("r") || huk.bazavyHuk.equals("t͡ʂ") || huk.bazavyHuk.equals("d͡ʐ")
                        || huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("b")
                        || huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("m") || huk.bazavyHuk.equals("f")
                        || huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("k")
                        || huk.bazavyHuk.equals("x")) {
                    Huk a = new Huk("", "a");
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                }
            }
        }
    }

    /**
     * 't' і 's' пераходзіць у 'c'
     */
    void pierachodTS() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.padzielPasla != 0) {
                continue;
            }
            if (!huk.miakki && !nastupny.miakki) {
                if (huk.bazavyHuk.equals("t") && nastupny.bazavyHuk.equals("s")) {
                    huk.bazavyHuk = "t͡s";
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
     * с-с-к пераходзіць у c-к
     * 
     * ц'-т пераходзіць у т'-т
     * 
     * ц'-ц пераходзіць у т'-ц
     * 
     * с'-ц'(толькі на канцы слова) пераходзіць у c', як "дасць талацэ"
     * з'-д'(толькі на канцы слова) пераходзіць у з', як "дасць заснуць"
     * ж-дж-зычны(толькі на канцы слова мяккія і цвёрдыя) - дз выпадае, як "дасць жыцця"
     * ш-ч-зычны(толькі на канцы слова мяккія і цвёрдыя) - ч выпадае, як "дасць шырокі"
     */
    void sprascennie() {
        for (int i = 1; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            Huk papiaredni = huki.get(i - 1);
            if (!huk.miakki && !papiaredni.miakki && papiaredni.bazavyHuk.equals("s")
                    && huk.bazavyHuk.equals("t͡s") && nastupny.bazavyHuk.equals("k")) {
                // сярэдні выпадае
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (!huk.miakki && !papiaredni.miakki && !nastupny.miakki
                    && papiaredni.bazavyHuk.equals("s") && huk.bazavyHuk.equals("t")
                    && nastupny.bazavyHuk.equals("t͡ʂ")) {
                // сярэдні выпадае
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
                // с->ш
                papiaredni.bazavyHuk = "ʂ";
            } else if (papiaredni.bazavyHuk.equals("s") && huk.bazavyHuk.equals("t")
                    && nastupny.bazavyHuk.equals("n")) {
                // сярэдні выпадае
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.bazavyHuk.equals("z") && huk.bazavyHuk.equals("d")
                    && nastupny.bazavyHuk.equals("n")) {
                // сярэдні выпадае
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
                papiaredni.miakki = nastupny.miakki;
            } else if (papiaredni.is("s", null, false, 0) && huk.is("s", null, false, 0) && nastupny.is("k", null, false, null)) {
                // с-с-к пераходзіць у c-к: першы выпадае, але не на сутыку: бяссківічны
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                huki.remove(i - 1);
            } else if (!huk.miakki && huk.padzielPasla==0 && huk.bazavyHuk.equals("ʂ")
                    && nastupny.bazavyHuk.equals("s")) {
                // ш->с
                huk.bazavyHuk = "s";
            } else if (huk.is("t͡s", true, null, Huk.PADZIEL_PRYSTAUKA) && nastupny.is("t", false, null, 0)) {
                // ц'-т -> т'-т
                huk.bazavyHuk = "t";
            } else if (huk.is("t͡s", true, null, Huk.PADZIEL_PRYSTAUKA)&& nastupny.is("t͡s", false, null, 0)) {
                // ц'-ц -> т'-ц
                huk.bazavyHuk = "t";
            } else if (papiaredni.is("s", true, null, 0) && huk.is("t͡s", true, false, Huk.PADZIEL_SLOVY)) {
                // с'-ц'(толькі на канцы слова) пераходзіць у c', як "дасць талацэ"
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("z", true, null, 0) && huk.is("d", true, false, Huk.PADZIEL_SLOVY)) {
                // з'-д'(толькі на канцы слова) пераходзіць у з', як "дасць заснуць"
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("ʐ", null, null, 0) && huk.is("d͡ʐ", null, false, Huk.PADZIEL_SLOVY)) {
                // ж-дж-зычны(толькі на канцы слова мяккія і цвёрдыя) - дз выпадае, як "дасць жыцця"
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("ʂ", null, null, 0) && huk.is("t͡ʂ", null, false, Huk.PADZIEL_SLOVY)) {
                // ш-ч-зычны(толькі на канцы слова мяккія і цвёрдыя) - ч выпадае, як "дасць шырокі"
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            }
        }
    }

    /**
     * 'v' перад 'о' і 'у' пераходзіць у 'β'
     */
    void pierachodV() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (!huk.miakki && huk.bazavyHuk.equals("v")) {
                if (!nastupny.miakkajaHalosnaja
                        && (nastupny.bazavyHuk.equals("ɔ") || nastupny.bazavyHuk.equals("u"))) {
                    huk.bazavyHuk = "β";
                }
            }
        }
    }

    /**
     * 'm' перад цьвёрдымі 'f', 'v', 'β' пераходзіць у 'ɱ' TODO
     */
    void pierachodM() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (!huk.miakki && huk.bazavyHuk.equals("m")) {
                if (!nastupny.miakki && (nastupny.bazavyHuk.equals("f") || nastupny.bazavyHuk.equals("v")
                        || nastupny.bazavyHuk.equals("β"))) {
                    huk.bazavyHuk = "ɱ";
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
            if (huk.bazavyHuk.equals("i") && (papiaredni.bazavyHuk.equals("d")
                    || papiaredni.bazavyHuk.equals("t") || papiaredni.bazavyHuk.equals("ʐ")
                    || papiaredni.bazavyHuk.equals("ʂ") || papiaredni.bazavyHuk.equals("t͡ʂ")
                    || papiaredni.bazavyHuk.equals("d͡ʐ") || papiaredni.bazavyHuk.equals("r"))) {
                huk.bazavyHuk = "ɨ";
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
            if (huk.is("d", false, false, null) && nastupny.is("t͡ʂ", false, false, null)) {
                huk.bazavyHuk = "t";
            } else if (huk.is("d", false, false, null) && nastupny.is("t͡s", false, false, null)) {
                huk.bazavyHuk = "t";
            }
        }
    }

    /**
     * Падваеньне аднолькавых гукаў што ідуць адзін за адным. Мяккасьць бярэцца з апошняга.
     * 
     * Гэта працуе толькі паміж галоснымі. Калі ёсьць побач зычны - ніякага падваеньня не адбываецца.
     * Застаецца толькі адзін гук.
     */
    void padvajennie() {
        for (int i = huki.size() - 1; i > 0; i--) {
            Huk papiaredni = huki.get(i - 1);
            Huk huk = huki.get(i);
            boolean halosnyPierad = i == 1 || huki.get(i - 2).halosnaja;
            boolean halosnyPasla = i == huki.size() - 1 || huki.get(i + 1).halosnaja;
            if (huk.bazavyHuk.equals(papiaredni.bazavyHuk) && !huk.halosnaja) {
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                /*if (halosnyPierad && (huk.bazavyHuk.equals("s") || huk.bazavyHuk.equals("ʂ")
                        || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("p"))) {
                    // cc+зычны, шш+зычны, зз+зычны, пп+зычны - звычайна ў прыстаўках
                    huk.padvojeny = true;
                }*/
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
                // 1. дасць сёння c'+c'=>с': 2. дасць зялёнаму з'+з'=>з': 3. дасць табе с'+т=>с'+т 4. дасць дом з'+д=>з'+д
                // 5. дасць швагру с'+ш=>ш: 6. дасць жабе з'+ж=>ж: 7. дасць чалавеку с'+ч=>ш+ч 8. дасць джону з'+дж=>ж+жд
            } else if (huk.bazavyHuk.equals("d͡ʐ") && papiaredni.bazavyHuk.equals("d")) {
                // д+дж => дж:
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("d͡z") && papiaredni.bazavyHuk.equals("d")) {
                // ддз => дз:
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("t͡ʂ") && papiaredni.bazavyHuk.equals("t")) {
                // т+ч => ч:
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("t͡s") && papiaredni.bazavyHuk.equals("t")) {
                // т+ш => ш:
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            }
        }
    }

    boolean isSypiacy(Huk huk) {
        if (huk.miakki) {
            return false;
        }
        return huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("d͡ʐ")
                || huk.bazavyHuk.equals("t͡ʂ");
    }

    boolean isSvisciacy(Huk huk) {
        if (huk.miakki) {
            return false;
        }
        return huk.bazavyHuk.equals("s") || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("d͡z")
                || huk.bazavyHuk.equals("t͡s");
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
            if (isSvisciacy(nastupny) && !huk.miakki && huk.padzielPasla == 0) {
                // пераходзіць у сьвісьцячы
                switch (huk.bazavyHuk) {
                case "ʂ":
                    huk.bazavyHuk = "s";
                    break;
                case "ʐ":
                    huk.bazavyHuk = "z";
                    break;
                case "d͡ʐ":
                    huk.bazavyHuk = "d͡z";
                    break;
                case "t͡ʂ":
                    huk.bazavyHuk = "t͡s";
                    break;
                }
            } else if (isSypiacy(nastupny) /*&& !huk.miakki*/) {
                /* на сутыку: шыпячых мяккіх у беларускай мове няма(усярэдзіне слоў), але каб зрабіць пераход "вось што" у "ш:", 
                 * трэба апрацоўваць "сьш", таму праверка на мяккасць папярэдняга - неабавязковая */
                // пераходзіць у шыпячы
                switch (huk.bazavyHuk) {
                case "s":
                    huk.bazavyHuk = "ʂ";
                    huk.miakki = false;
                    break;
                case "z":
                    // праблема: бязьджаўковая
                    huk.bazavyHuk = "ʐ";
                    huk.miakki = false;
                    break;
                case "d͡z":
                    // праблема: "сядзь жа"
                    huk.bazavyHuk = "d͡ʐ";
                    huk.miakki = false;
                    break;
                case "t͡s":
                    // праблема: "сядзь шалёны"
                    huk.bazavyHuk = "t͡ʂ";
                    huk.miakki = false;
                    break;
                }
            }
        }
    }

    /**
     * Аглушэньне й азванчэннье гукаў перад глухімі - распаўсюджваецца на некалькі папярэдніх гукаў. Таксама
     * звонкі пераходзіць у глухі напрыканцы слова.
     */
    void ahlusennieIazvancennie() {
        for (int i = huki.size() - 1; i >= 0; i--) {
            Huk nastupny = i < huki.size() - 1 ? huki.get(i + 1) : null;
            Huk huk = huki.get(i);
            if (nastupny != null && isZvonki(nastupny)) {
                // азванчэньне
                switch (huk.bazavyHuk) {
                case "p":
                    huk.bazavyHuk = "b";
                    break;
                case "t":
                    huk.bazavyHuk = "d";
                    break;
                case "t͡s":
                    if (huk.padzielPasla!=0 && huk.miakki && !nastupny.miakki) {
                        // мяккі зычны перад цьвёрдым звонкім на сутыку
                        huk.bazavyHuk = "d";
                    } else {
                        huk.bazavyHuk = "d͡z";
                    }
                    break;
                case "s":
                    huk.bazavyHuk = "z";
                    break;
                case "ʂ":
                    huk.bazavyHuk = "ʐ";
                    break;
                case "t͡ʂ":
                    huk.bazavyHuk = "d͡ʐ";
                    break;
                case "x":
                    huk.bazavyHuk = "ɣ";
                    break;
                case "k":
                    huk.bazavyHuk = "g";
                    break;
                }
            } else if (nastupny == null || isHluchi(nastupny)) {
                // аглушэньне
                switch (huk.bazavyHuk) {
                case "b":
                    huk.bazavyHuk = "p";
                    break;
                case "d":
                    huk.bazavyHuk = "t";
                    break;
                case "d͡z":
                    huk.bazavyHuk = "t͡s";
                    break;
                case "z":
                    huk.bazavyHuk = "s";
                    break;
                case "ʐ":
                    huk.bazavyHuk = "ʂ";
                    break;
                case "d͡ʐ":
                    huk.bazavyHuk = "t͡ʂ";
                    break;
                case "ɣ":
                    huk.bazavyHuk = "x";
                    break;
                case "g":
                    huk.bazavyHuk = "k";
                    break;
                }
            }
        }
    }

    boolean isZvonki(Huk huk) {
        if (huk.miakki) {
            return huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("d") || huk.bazavyHuk.equals("d͡z")
                    || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("g") || huk.bazavyHuk.equals("ɣ");
        } else {
            return huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("d") || huk.bazavyHuk.equals("d͡z")
                    || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("d͡ʐ")
                    || huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("g");
        }
    }

    boolean isHluchi(Huk huk) {
        if (huk.miakki) {
            return huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("t͡s") || huk.bazavyHuk.equals("s")
                    || huk.bazavyHuk.equals("k") || huk.bazavyHuk.equals("x") || huk.bazavyHuk.equals("f");
        } else {
            return huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("t") || huk.bazavyHuk.equals("t͡s")
                    || huk.bazavyHuk.equals("s") || huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("t͡ʂ")
                    || huk.bazavyHuk.equals("x") || huk.bazavyHuk.equals("k") || huk.bazavyHuk.equals("f");
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
            if (!huk.halosnaja && huk.padzielPasla!=0) {
                miakkasc = huk.miakki;
                continue;
            }
            if (huk.halosnaja) {
                // зьмягчаеццца перад мяккімі галоснымі
                miakkasc = huk.miakkajaHalosnaja;
            } else if (huk.apostrafPasla) {
                miakkasc = false;
                huk.miakki = false;
            } else if (huk.bazavyHuk.equals("r") || huk.bazavyHuk.equals("t͡ʂ") || huk.bazavyHuk.equals("d͡ʐ")
                    || huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ")) {
                // ніколі не бываюць мяккімі - зацьвярдзелыя р ж ш дж ч
                miakkasc = false;
                huk.miakki = false;
            } else if (huk.bazavyHuk.equals("u̯") || huk.bazavyHuk.equals("j")) {
                // паўгалосны
                miakkasc = false;
                huk.miakki = false;
            } else if (huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("m")
                    || huk.bazavyHuk.equals("f") || (huk.bazavyHuk.equals("l") && !huk.miakki)) {
                // бпмфл не зьмягчаецца перад наступным мяккім зычным
                if (nastupny != null) {
                    if (huk.bazavyHuk.equals(nastupny.bazavyHuk)) {
                        // акрамя выпадкаў калі далей ідзе такі самы зычны(бпмф)
                    } else if (nastupny.halosnaja) {
                        miakkasc = nastupny.miakkajaHalosnaja;
                    } else {
                        // бпмф не зьмягчаецца перад наступным мяккім зычным, але не перад галоснай
                        miakkasc = false;
                    }
                }
                huk.miakki = miakkasc;
            } else if (huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("k") || huk.bazavyHuk.equals("x")) {
                // яны самі зьмягчаюцца толькі перад галоснымі(але не перад зычнымі), і не даюць зьмягчацца гукам
                // перад імі: аб'едкі, вянгляр
                if (nastupny != null) {
                    if (huk.bazavyHuk.equals(nastupny.bazavyHuk)) {
                        // акрамя выпадкаў калі далей ідзе такі самы зычны(бпмгкх)
                    } else if (nastupny.halosnaja) {
                        huk.miakki = nastupny.miakkajaHalosnaja;
                    }
                }
                // не даюць зьмягчацца гукам перад імі
                miakkasc = false; // TODO праверыць падваеньне гкх
            } else if (huk.bazavyHuk.equals("d")) {
                if (nastupny != null && nastupny.miakki && (nastupny.bazavyHuk.equals("n")
                        || nastupny.bazavyHuk.equals("s") || nastupny.bazavyHuk.equals("z"))) {
                    huk.miakki = miakkasc;
                } else {
                    huk.miakki = false;
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk.equals("t")) {
                if (nastupny != null && nastupny.miakki && (nastupny.bazavyHuk.equals("n")
                        || nastupny.bazavyHuk.equals("s") || nastupny.bazavyHuk.equals("z"))) {
                    huk.miakki = miakkasc;
                } else {
                    huk.miakki = false;
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk.equals("d͡z") && nastupny != null && nastupny.bazavyHuk.equals("l")) {
                huk.miakki = false;
                miakkasc = false;
            } else if (nastupny != null && huk.is("n", true, false, null) && (nastupny.is("v", true, false, null) || nastupny.is("f", true, false, null))) {
                // Перад губнымі [в’], [ф’] у двухчленных спалучэннях часцей сустракаецца цвёрды н
                huk.miakki = false;
                miakkasc = false;
            } else if (huk.miakki) {
                // зьмягчаеццца перад мяккімі зычнымі(калі быў 'ь')
                miakkasc = true;
            } else {
                // цьвёрды зычны - зьмягчаецца ў залежнасьці ад таго што ідзе далей
                huk.miakki = miakkasc;
            }
        }
    }

    static final String[] PRYSTAUKI = new String[] { "zzzад", "безад", "беспад", "вод", "звод", "наад", "навод",
            "напад", "над", "неад", "непад", "непрад", "павод", "панад", "папад", "падад", "пад", "перапад",
            "перад", "под", "прад", "прыад", "прыпад", "спад", "спрад",
            "супад"/*
                    * ,
                    * 
                    * "абяс", "ас", "абес", "адс", "бес", "бяс", "вус", "выс", "дас", "дыс", "зрас", "зас",
                    * "нарас", "нас", "небес", "небяс", "церас", "ус", "наўс", "не-рас", "не-с", "не-ўс",
                    * "пера-рас", "пера-с", "ня-с", "ня-ўс", "па-па-с", "па-рас", "прас", "пра-с", "рас",
                    * "рас-с", "па-ўс", "пры-с", "рос", "с", "са-с", "у-рас", "у-рос", "па-с"
                    */ , "між", "звыш", "контр", "гіпер", "супер", "экс", "обер", "супраць" };

    static {
        Arrays.sort(PRYSTAUKI, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o2.length() - o1.length();
            }
        });
    }

    /**
     * Канвэртуем літары ў базавыя гукі, ўлічваючы дж/дз як адзин гук і пазначаючы мяккі знак як мяккасьць
     * папярэдняга гуку.
     */
    void stvarajemBazavyjaHuji(String w) {
        // пазначаем прыстаўкі
        for (String p : PRYSTAUKI) {
            if (w.length() > p.length() + 2 && w.startsWith(p)) {
                w = w.substring(0, p.length()) + '|' + w.substring(p.length());
            }
        }

        Huk papiaredniHuk = null;
        for (int i = 0; i < w.length(); i++) {
            char c = w.charAt(i);
            Huk novyHuk = null;
            switch (c) {
            case 'а':
                novyHuk = new Huk("а", "a");
                novyHuk.halosnaja = true;
                break;
            case 'б':
                novyHuk = new Huk("б", "b");
                break;
            case 'в':
                novyHuk = new Huk("в", "v");
                break;
            case 'г':
                novyHuk = new Huk("г", "ɣ");
                break;
            case 'ґ':
                novyHuk = new Huk("ґ", "g");
                break;
            case 'д':
                novyHuk = new Huk("д", "d");
                break;
            case 'е':
                dadacJotKaliPatrebny(w, i);
                novyHuk = new Huk("е", "ɛ");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ё':
                dadacJotKaliPatrebny(w, i);
                novyHuk = new Huk("ё", "ɔ");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ж':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary)
                        && papiaredniHuk.padzielPasla==0) {
                    // дж
                    papiaredniHuk.zychodnyjaLitary = "дж";
                    papiaredniHuk.bazavyHuk = "d͡ʐ";
                } else {
                    novyHuk = new Huk("ж", "ʐ");
                }
                break;
            case 'з':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary)
                        && papiaredniHuk.padzielPasla==0) {
                    // дз
                    papiaredniHuk.zychodnyjaLitary = "дз";
                    papiaredniHuk.bazavyHuk = "d͡z";
                } else {
                    novyHuk = new Huk("з", "z");
                }
                break;
            case 'і':
                dadacJotKaliPatrebny(w, i);
                novyHuk = new Huk("і", "i");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'й':
                novyHuk = new Huk("й", "j");
                break;
            case 'к':
                novyHuk = new Huk("к", "k");
                break;
            case 'л':
                novyHuk = new Huk("л", "l");
                break;
            case 'м':
                novyHuk = new Huk("м", "m");
                break;
            case 'н':
                novyHuk = new Huk("н", "n");
                break;
            case 'о':
                novyHuk = new Huk("о", "ɔ");
                novyHuk.halosnaja = true;
                break;
            case 'п':
                novyHuk = new Huk("п", "p");
                break;
            case 'р':
                novyHuk = new Huk("р", "r");
                break;
            case 'с':
                novyHuk = new Huk("с", "s");
                break;
            case 'т':
                novyHuk = new Huk("т", "t");
                break;
            case 'у':
                novyHuk = new Huk("у", "u");
                novyHuk.halosnaja = true;
                break;
            case 'ў':
                novyHuk = new Huk("ў", "u̯");
                break;
            case 'ф':
                novyHuk = new Huk("ф", "f");
                break;
            case 'х':
                novyHuk = new Huk("х", "x");
                break;
            case 'ц':
                novyHuk = new Huk("ц", "t͡s");
                break;
            case 'ч':
                novyHuk = new Huk("ч", "t͡ʂ");
                break;
            case 'ш':
                novyHuk = new Huk("ш", "ʂ");
                break;
            case 'ы':
                novyHuk = new Huk("ы", "ɨ");
                novyHuk.halosnaja = true;
                break;
            case 'ь':
                if (papiaredniHuk != null) {
                    papiaredniHuk.miakki = true;
                }
                break;
            case 'э':
                novyHuk = new Huk("э", "ɛ");
                novyHuk.halosnaja = true;
                break;
            case 'ю':
                dadacJotKaliPatrebny(w, i);
                novyHuk = new Huk("ю", "u");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'я':
                dadacJotKaliPatrebny(w, i);
                novyHuk = new Huk("я", "a");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case '\'':
            case '’':
                if (papiaredniHuk != null) {
                    papiaredniHuk.apostrafPasla = true;
                }
                break;
            case '|':
                papiaredniHuk.padzielPasla = Huk.PADZIEL_PRYSTAUKA;
                break;
            case '-':
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
            huki.get(huki.size()-1).padzielPasla |= Huk.PADZIEL_SLOVY;
        }
    }

    void dadacJotKaliPatrebny(String w, int i) {
        if (i == 0 || "'’ьёуўеыаоэяію".indexOf(w.charAt(i - 1)) >= 0) {
            // першая літара ці пасьля пералічаных
            Huk jot = new Huk("", "j");
            jot.miakkajaHalosnaja = true;
            jot.halosnaja = true;
            jot.miakki = true;
            huki.add(jot);
        } else if (i > 0 && "еёюя".indexOf(w.charAt(i)) >= 0 && "тдржшч|".indexOf(w.charAt(i - 1)) >= 0) {
            // звычайна сутык прыстаўкі і кораня
            Huk jot = new Huk("", "j");
            jot.miakkajaHalosnaja = true;
            jot.halosnaja = true;
            jot.miakki = true;
            huki.add(jot);
        }
    }
}
