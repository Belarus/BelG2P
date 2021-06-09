package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/*
 * Праверыць - [й] перад галоснымі - гэта галосны ці змяніць на зычны
 * 
 * TODO зрабіць: н перад санорнымі не змякчаецца
 * 
 * TODO брск - бск (Акцябрскі)     стм - см (пластмаса)     мюзікл mʲuzʲikl
 */
public class Fanetyka3 {
    List<Huk> huki = new ArrayList<>();
    List<String> words = new ArrayList<>();
    List<String> why = new ArrayList<>();

    public void addWord(String w) {
        words.add(w);
    }

    public void calcFanetyka() {
        for (int i = 0; i < words.size(); i++) {
            String w = words.get(i);
            if (i < words.size() - 1) {
                // "без,не -> бяз,ня" перад словамі з націскам на першы склад
                String wl = w.toLowerCase();
                switch (wl) {
                case "не":
                case "не´":
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "ня";
                    }
                    break;
                case "без":
                case "без´":
                    if (firstSkladNacisk(words.get(i + 1))) {
                        w = "бяз";
                    }
                    break;
                }
            }
            stvarajemBazavyjaHuji(w.toLowerCase());
        }
        String prev = toString();
        int pass = 0;
        while (true) {
            pierachodZHA();
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
            pierachodFH();
            pierachodZG();
            String hnew = toString();
            if (hnew.equals(prev)) {
                break;
            }
            prev = hnew;
            pass++;
            if (pass >= 100) {
                throw new RuntimeException("Too many passes");
            }
        }
        setIpaStress();
    }

    public String toString() {
        return toString(Huk.ipa);
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
            if (h.halosnaja && !h.bazavyHuk.equals("j")) {
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
        //huki.get(halIndex).stress = false;
        StringBuilder s = new StringBuilder();
        for (int i = prevHalIndex; i <= halIndex; i++) {
            Huk h = huki.get(i);
            if (h.bazavyHuk.equals("u̯") || h.bazavyHuk.equals("j") || h.bazavyHuk.equals("r")) {
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
            pierad=0;
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
            pierad=1;
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
            pierad=2;
            break;
        default:
            System.err.println("Незразумелая мяжа складаў у мадэлі '" + s + "' для слоў " + words + "/" + this);
            pierad=-1;
            break;
        }
        if (pierad >= 0) {
            huki.get(prevHalIndex + pierad).stressIpa = true;
        }
    }

    public static String fanetykaSlova(String w) {
        Fanetyka3 r = new Fanetyka3();
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
    // if (hety.bazavyHuk.equals("d͡ʐ") && !hety.miakki) {
    // Huk d = new Huk("", "d");
    // huki.add(prystLen, d);
    // huki.get(prystLen + 1).bazavyHuk = "ʐ";
    // } else if (hety.bazavyHuk.equals("d͡z") && !hety.padvojeny &&
    // !nastupny.halosnaja) {
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
            if (nastupny.bazavyHuk.equals("r") && nastupny.miakki == 0) {
                if (!huk.halosnaja && !huk.bazavyHuk.equals("r") && !huk.bazavyHuk.equals("j")
                        && !huk.bazavyHuk.equals("u̯")) {
                    Huk a = new Huk("", "a");
                    a.halosnaja = true;
                    huki.add(i + 1, a);
                }
            } else if (nastupny.bazavyHuk.equals("ɫ") && nastupny.miakki != 0) {
                if (huk.bazavyHuk.equals("r") || huk.bazavyHuk.equals("t͡ʂ") || huk.bazavyHuk.equals("d͡ʐ")
                        || huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("b")
                        || huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("m") || huk.bazavyHuk.equals("f")
                        || huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("k") || huk.bazavyHuk.equals("x")) {
                    Huk a = new Huk("", "a");
                    a.halosnaja = true;
                    huki.add(i + 1, a);
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
            if (huk.is("f", null, false, 0) && nastupny.is("ɣ", null, false, 0)) {
                huk.bazavyHuk = "u̯";
            }
        }
    }

    /**
     * зг паміж галосных пераходзіць у зґ
     * TODO праверыць на сутыку прыстаўкі
     */
    void pierachodZG() {
       /* for (int i = 0; i < huki.size() - 3; i++) {
            Huk h1 = huki.get(i);
            Huk h2 = huki.get(i + 1);
            Huk h3 = huki.get(i + 2);
            Huk h4 = huki.get(i + 3);
            if (h1.halosnaja && h4.halosnaja && !h1.apostrafPasla && h1.padzielPasla < Huk.PADZIEL_SLOVY
                    && h2.bazavyHuk.equals("з") && h3.bazavyHuk.equals("γ") && !h2.apostrafPasla && !h3.apostrafPasla
                    && !h2.padvojeny && !h3.padvojeny) {
                h3.bazavyHuk = "g";
            }
        }*/
    }

    /**
     * zɣ -> zaɣ, zg -> zag
     */
    void pierachodZHA() {
        for (int i = 0; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            if (huk.is("z", 0, false, 0) && (nastupny.is("ɣ", 0, false, null) || nastupny.is("g", 0, false, null))) {
                if (nastupny.padzielPasla == Huk.PADZIEL_SLOVY) {
                    why.add("Устаўное 'а': zɣ -> zaɣ, zg -> zag");
                    Huk a = new Huk("", "a");
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
                if (huk.bazavyHuk.equals("t") && nastupny.bazavyHuk.equals("s")) {
                    why.add("'t' і 's' пераходзіць у 'c', толькі калі няма падзелу");
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
     * с'-ц'(толькі на канцы слова) пераходзіць у c', як "дасць талацэ" з'-д'(толькі
     * на канцы слова) пераходзіць у з', як "дасць заснуць" ж-дж-зычны(толькі на
     * канцы слова мяккія і цвёрдыя) - дз выпадае, як "дасць жыцця" ш-ч-зычны(толькі
     * на канцы слова мяккія і цвёрдыя) - ч выпадае, як "дасць шырокі"
     */
    void sprascennie() {
        for (int i = 1; i < huki.size() - 1; i++) {
            Huk huk = huki.get(i);
            Huk nastupny = huki.get(i + 1);
            Huk papiaredni = huki.get(i - 1);
            if (huk.miakki == 0 && papiaredni.miakki == 0 && papiaredni.bazavyHuk.equals("s")
                    && huk.bazavyHuk.equals("t͡s") && nastupny.bazavyHuk.equals("k")) {
                why.add("Спрашчэнне: сярэдні выпадае");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (huk.miakki == 0 && papiaredni.miakki == 0 && nastupny.miakki == 0
                    && papiaredni.bazavyHuk.equals("s") && huk.bazavyHuk.equals("t")
                    && nastupny.bazavyHuk.equals("t͡ʂ")) {
                // сярэдні выпадае
                why.add("Спрашчэнне: сярэдні выпадае, с->ш");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
                papiaredni.bazavyHuk = "ʂ";
            } else if (papiaredni.bazavyHuk.equals("s") && huk.bazavyHuk.equals("t")
                    && nastupny.bazavyHuk.equals("n")) {
                why.add("Спрашчэнне: сярэдні выпадае");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.bazavyHuk.equals("z") && huk.bazavyHuk.equals("d")
                    && nastupny.bazavyHuk.equals("n")) {
                why.add("Спрашчэнне: сярэдні выпадае");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
                papiaredni.miakki = nastupny.miakki;
            } else if (papiaredni.is("s", null, false, 0) && huk.is("s", null, false, 0)
                    && nastupny.is("k", null, false, null)) {
                why.add("Спрашчэнне: с-с-к пераходзіць у c-к: першы выпадае, але не на сутыку, як 'бяссківічны'");
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                huki.remove(i - 1);
            } else if (huk.miakki == 0 && huk.padzielPasla == 0 && huk.bazavyHuk.equals("ʂ")
                    && nastupny.bazavyHuk.equals("s")) {
                why.add("Спрашчэнне: ш->с");
                huk.bazavyHuk = "s";
            } else if (huk.is("t͡s", Huk.MASK_MIAKKASC_USIE, null, Huk.PADZIEL_PRYSTAUKA)
                    && nastupny.is("t", 0, null, 0)) {
                why.add("Спрашчэнне: ц'-т -> т'-т");
                huk.bazavyHuk = "t";
            } else if (huk.is("t͡s", Huk.MASK_MIAKKASC_USIE, null, Huk.PADZIEL_PRYSTAUKA)
                    && nastupny.is("t͡s", 0, null, 0)) {
                why.add("Спрашчэнне: ц'-ц -> т'-ц");
                huk.bazavyHuk = "t";
            } else if (papiaredni.is("s", Huk.MASK_MIAKKASC_USIE, null, 0)
                    && huk.is("t͡s", Huk.MASK_MIAKKASC_USIE, false, Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) {
                why.add("Спрашчэнне: с'-ц'(толькі на канцы слова) пераходзіць у c', як 'дасць талацэ'");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("z", Huk.MASK_MIAKKASC_USIE, null, 0)
                    && huk.is("d", Huk.MASK_MIAKKASC_USIE, false, Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) {
                why.add("Спрашчэнне: з'-д'(толькі на канцы слова) пераходзіць у з', як 'дасць заснуць'");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("ʐ", null, null, 0)
                    && huk.is("d͡ʐ", null, false, Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) {
                why.add("Спрашчэнне: ж-дж-зычны(толькі на канцы слова мяккія і цвёрдыя) - дз выпадае, як 'дасць жыцця'");
                papiaredni.zychodnyjaLitary += huk.zychodnyjaLitary;
                huki.remove(i);
            } else if (papiaredni.is("ʂ", null, null, 0)
                    && huk.is("t͡ʂ", null, false, Huk.PADZIEL_SLOVY | Huk.PADZIEL_MINUS)) {
                why.add("Спрашчэнне: ш-ч-зычны(толькі на канцы слова мяккія і цвёрдыя) - ч выпадае, як 'дасць шырокі'");
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
            if (huk.miakki == 0 && huk.bazavyHuk.equals("v")) {
                if (!nastupny.miakkajaHalosnaja && (nastupny.bazavyHuk.equals("ɔ") || nastupny.bazavyHuk.equals("u"))) {
                    why.add("'v' перад 'о' і 'у' пераходзіць у 'β'");
                    huk.bazavyHuk = "β";
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
            if (huk.miakki == 0 && huk.bazavyHuk.equals("m")) {
                if (nastupny.bazavyHuk.equals("f") || nastupny.bazavyHuk.equals("v")
                        || nastupny.bazavyHuk.equals("β")) {
                    why.add("'m' перад 'f', 'v', 'β' пераходзіць у 'ɱ'");
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
            if (huk.bazavyHuk.equals("i") && (papiaredni.bazavyHuk.equals("d") || papiaredni.bazavyHuk.equals("t")
                    || papiaredni.bazavyHuk.equals("ʐ") || papiaredni.bazavyHuk.equals("ʂ")
                    || papiaredni.bazavyHuk.equals("t͡ʂ") || papiaredni.bazavyHuk.equals("d͡ʐ")
                    || papiaredni.bazavyHuk.equals("r"))) {
                why.add("'і' перад д т ж ш ч дж пераходзіць у ɨ");
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
            if (huk.is("d", 0, false, null) && nastupny.is("t͡ʂ", 0, false, null)) {
                why.add("Падваенне:");
                huk.bazavyHuk = "t";
            } else if (huk.is("d", 0, false, null) && nastupny.is("t͡s", 0, false, null)) {
                why.add("Падваенне:");
                huk.bazavyHuk = "t";
            }
        }
    }

    /**
     * Падваеньне аднолькавых гукаў што ідуць адзін за адным. Мяккасьць бярэцца з
     * апошняга.
     * 
     * Гэта працуе толькі паміж галоснымі. Калі ёсьць побач зычны - ніякага
     * падваеньня не адбываецца. Застаецца толькі адзін гук.
     */
    void padvajennie() {
        for (int i = huki.size() - 1; i > 0; i--) {
            Huk papiaredni = huki.get(i - 1);
            Huk huk = huki.get(i);
            boolean halosnyPierad = i == 1 || huki.get(i - 2).halosnaja;
            boolean halosnyPasla = i == huki.size() - 1 || huki.get(i + 1).halosnaja;
            if (huk.bazavyHuk.equals(papiaredni.bazavyHuk) && !huk.halosnaja) {
                why.add("Падваенне:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                /*
                 * if (halosnyPierad && (huk.bazavyHuk.equals("s") || huk.bazavyHuk.equals("ʂ")
                 * || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("p"))) { // cc+зычны,
                 * шш+зычны, зз+зычны, пп+зычны - звычайна ў прыстаўках huk.padvojeny = true; }
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
            } else if (huk.bazavyHuk.equals("d͡ʐ") && papiaredni.bazavyHuk.equals("d")) {
                why.add("Падваенне: д+дж => дж:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("d͡z") && papiaredni.bazavyHuk.equals("d")) {
                why.add("Падваенне: ддз => дз:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("t͡ʂ") && papiaredni.bazavyHuk.equals("t")) {
                why.add("Падваенне: т+ч => ч:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
                huki.remove(i - 1);
                huk.zychodnyjaLitary = papiaredni.zychodnyjaLitary + huk.zychodnyjaLitary;
                i--;
            } else if (huk.bazavyHuk.equals("t͡s") && papiaredni.bazavyHuk.equals("t")) {
                why.add("Падваенне: т+ш => ш:");
                huk.padvojeny = true;// halosnyPasla && halosnyPierad;
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
        return huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("d͡ʐ")
                || huk.bazavyHuk.equals("t͡ʂ");
    }

    boolean isSvisciacy(Huk huk) {
        if (huk.miakki != 0) {
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
            if (isSvisciacy(nastupny) && huk.miakki == 0 && huk.padzielPasla == 0) {
                // пераходзіць у сьвісьцячы
                switch (huk.bazavyHuk) {
                case "ʂ":
                    why.add("Пераход у свісцячыя");
                    huk.bazavyHuk = "s";
                    break;
                case "ʐ":
                    why.add("Пераход у свісцячыя");
                    huk.bazavyHuk = "z";
                    break;
                case "d͡ʐ":
                    why.add("Пераход у свісцячыя");
                    huk.bazavyHuk = "d͡z";
                    break;
                case "t͡ʂ":
                    why.add("Пераход у свісцячыя");
                    huk.bazavyHuk = "t͡s";
                    break;
                }
            } else if (isSypiacy(nastupny) /* && !huk.miakki */) {
                /*
                 * на сутыку: шыпячых мяккіх у беларускай мове няма(усярэдзіне слоў), але каб
                 * зрабіць пераход "вось што" у "ш:", трэба апрацоўваць "сьш", таму праверка на
                 * мяккасць папярэдняга - неабавязковая
                 */
                // пераходзіць у шыпячы
                if (huk.miakki == Huk.MIAKKASC_PAZNACANAJA) {
                    throw new RuntimeException("Ня можа пераходзіць пазначаная мяккасць");
                }
                switch (huk.bazavyHuk) {
                case "t":
                    why.add("Пераход у шыпячыя");
                    huk.bazavyHuk = "t͡ʂ";
                    huk.miakki = 0;
                    break;
                case "s":
                    why.add("Пераход у шыпячыя");
                    huk.bazavyHuk = "ʂ";
                    huk.miakki = 0;
                    break;
                case "z":
                    // праблема: бязьджаўковая
                    why.add("Пераход у шыпячыя");
                    huk.bazavyHuk = "ʐ";
                    huk.miakki = 0;
                    break;
                case "d͡z":
                    // праблема: "сядзь жа"
                    why.add("Пераход у шыпячыя");
                    huk.bazavyHuk = "d͡ʐ";
                    huk.miakki = 0;
                    break;
                case "t͡s":
                    // праблема: "сядзь шалёны"
                    why.add("Пераход у шыпячыя");
                    huk.bazavyHuk = "t͡ʂ";
                    huk.miakki = 0;
                    break;
                }
            }
        }
    }

    /**
     * Аглушэньне й азванчэннье гукаў перад глухімі - распаўсюджваецца на некалькі
     * папярэдніх гукаў. Таксама звонкі пераходзіць у глухі напрыканцы слова.
     */
    void ahlusennieIazvancennie() {
        for (int i = huki.size() - 1; i >= 0; i--) {
            Huk nastupny = i < huki.size() - 1 ? huki.get(i + 1) : null;
            Huk huk = huki.get(i);
            if (nastupny != null && isZvonki(nastupny)) {
                // азванчэньне
                switch (huk.bazavyHuk) {
                case "p":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "b";
                    break;
                case "t":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "d";
                    break;
                case "t͡s":
                    why.add("Азванчэнне");
                    if (huk.padzielPasla != 0 && huk.miakki != 0 && nastupny.miakki == 0) {
                        // мяккі зычны перад цьвёрдым звонкім на сутыку
                        huk.bazavyHuk = "d";
                    } else {
                        huk.bazavyHuk = "d͡z";
                    }
                    break;
                case "s":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "z";
                    break;
                case "ʂ":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "ʐ";
                    break;
                case "t͡ʂ":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "d͡ʐ";
                    break;
                case "x":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "ɣ";
                    break;
                case "k":
                    why.add("Азванчэнне");
                    huk.bazavyHuk = "g";
                    break;
                }
            } else if (nastupny == null || isHluchi(nastupny)) {
                // аглушэньне
                switch (huk.bazavyHuk) {
                case "b":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "p";
                    break;
                case "d":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "t";
                    break;
                case "d͡z":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "t͡s";
                    break;
                case "z":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "s";
                    break;
                case "ʐ":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "ʂ";
                    break;
                case "d͡ʐ":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "t͡ʂ";
                    break;
                case "ɣ":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "x";
                    break;
                case "g":
                    why.add("Аглушэнне");
                    huk.bazavyHuk = "k";
                    break;
                }
            }
        }
    }

    boolean isZvonki(Huk huk) {
        if (huk.miakki != 0) {
            return huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("d") || huk.bazavyHuk.equals("d͡z")
                    || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("g") || huk.bazavyHuk.equals("ɣ");
        } else {
            return huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("d") || huk.bazavyHuk.equals("d͡z")
                    || huk.bazavyHuk.equals("z") || huk.bazavyHuk.equals("ʐ") || huk.bazavyHuk.equals("d͡ʐ")
                    || huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("g");
        }
    }

    boolean isHluchi(Huk huk) {
        if (huk.miakki != 0) {
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
            if (!huk.halosnaja && huk.padzielPasla != 0) {
                miakkasc = huk.miakki != 0;
                continue;
            }
            if (huk.halosnaja) {
                // зьмягчаеццца перад мяккімі галоснымі
                why.add("Мяккасць: перад галоснымі");
                miakkasc = huk.miakkajaHalosnaja;
            } else if (huk.apostrafPasla) {
                why.add("Мяккасць: перад апострафам");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk.equals("r") || huk.bazavyHuk.equals("t͡ʂ") || huk.bazavyHuk.equals("d͡ʐ")
                    || huk.bazavyHuk.equals("ʂ") || huk.bazavyHuk.equals("ʐ")) {
                why.add("Мяккасць: ніколі не бываюць мяккімі - зацьвярдзелыя р ж ш дж ч");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk.equals("u̯") || huk.bazavyHuk.equals("j")) {
                // паўгалосны
                why.add("Мяккасць: паўгалосны");
                miakkasc = false;
                huk.setMiakkasc(false);
            } else if (huk.bazavyHuk.equals("b") || huk.bazavyHuk.equals("p") || huk.bazavyHuk.equals("m")
                    || huk.bazavyHuk.equals("ɱ") || huk.bazavyHuk.equals("f")
                    || (huk.bazavyHuk.equals("ɫ") && huk.miakki == 0)) {
                // бпмфл не зьмягчаецца перад наступным мяккім зычным
                why.add("Мяккасць: ");
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
            } else if (huk.bazavyHuk.equals("g") || huk.bazavyHuk.equals("ɣ") || huk.bazavyHuk.equals("k")
                    || huk.bazavyHuk.equals("x")) {
                // яны самі зьмягчаюцца толькі перад галоснымі(але не перад зычнымі), і не даюць
                // зьмягчацца гукам
                // перад імі: аб'едкі, вянгляр
                why.add("Мяккасць: ");
                if (nastupny != null) {
                    if (huk.bazavyHuk.equals(nastupny.bazavyHuk)) {
                        // акрамя выпадкаў калі далей ідзе такі самы зычны(бпмгкх)
                    } else if (nastupny.halosnaja) {
                        huk.setMiakkasc(nastupny.miakkajaHalosnaja);
                    }
                }
                // не даюць зьмягчацца гукам перад імі
                miakkasc = false; // TODO праверыць падваеньне гкх
            } else if (huk.bazavyHuk.equals("d")) {
                why.add("Мяккасць: ");
                if (nastupny != null && nastupny.miakki != 0 && (nastupny.bazavyHuk.equals("n")
                        || nastupny.bazavyHuk.equals("s") || nastupny.bazavyHuk.equals("z"))) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk.equals("t")) {
                why.add("Мяккасць: ");
                if (nastupny != null && nastupny.miakki != 0 && (nastupny.bazavyHuk.equals("n")
                        || nastupny.bazavyHuk.equals("s") || nastupny.bazavyHuk.equals("z"))) {
                    huk.setMiakkasc(miakkasc);
                } else {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                }
            } else if (huk.bazavyHuk.equals("d͡z") && nastupny != null && nastupny.bazavyHuk.equals("ɫ")) {
                why.add("Мяккасць: ");
                huk.setMiakkasc(false);
                miakkasc = false;
            } else if (nastupny != null && huk.is("n", null, false, null)
                    && (nastupny.is("v", Huk.MASK_MIAKKASC_USIE, false, null)
                            || nastupny.is("f", Huk.MASK_MIAKKASC_USIE, false, null))) {
                why.add("Мяккасць: ");
                // Перад губнымі [в’], [ф’] у двухчленных спалучэннях часцей сустракаецца цвёрды
                // н
                if (huk.miakki != Huk.MIAKKASC_PAZNACANAJA) {
                    huk.setMiakkasc(false);
                    miakkasc = false;
                } else {
                    miakkasc = true;
                }
            } else if (huk.miakki != 0) {
                why.add("Мяккасць: ");
                // зьмягчаеццца перад мяккімі зычнымі(калі быў 'ь')
                miakkasc = true;
            } else {
                why.add("Мяккасць: ");
                // цьвёрды зычны - зьмягчаецца ў залежнасьці ад таго што ідзе далей
                huk.setMiakkasc(miakkasc);
            }
        }
    }

    static final String[] PRYSTAUKI = new String[] { "zzzад", "безад", "беспад", "вод", "звод", "наад", "навод",
            "напад", "над", "неад", "непад", "непрад", "павод", "панад", "папад", "падад", "пад", "перапад", "перад",
            "под", "прад", "прыад", "прыпад", "спад", "спрад",
            "супад"/*
                    * ,
                    * 
                    * "абяс", "ас", "абес", "адс", "бес", "бяс", "вус", "выс", "дас", "дыс",
                    * "зрас", "зас", "нарас", "нас", "небес", "небяс", "церас", "ус", "наўс",
                    * "не-рас", "не-с", "не-ўс", "пера-рас", "пера-с", "ня-с", "ня-ўс", "па-па-с",
                    * "па-рас", "прас", "пра-с", "рас", "рас-с", "па-ўс", "пры-с", "рос", "с",
                    * "са-с", "у-рас", "у-рос", "па-с"
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
     * Канвэртуем літары ў базавыя гукі, ўлічваючы дж/дз як адзін гук і пазначаючы
     * мяккі знак як мяккасьць папярэдняга гуку.
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
                dadacJotKaliPatrebny(papiaredniHuk, c);
                novyHuk = new Huk("е", "ɛ");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ё':
                dadacJotKaliPatrebny(papiaredniHuk, c);
                novyHuk = new Huk("ё", "ɔ");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'ж':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary)
                        && papiaredniHuk.padzielPasla == 0) {
                    // дж
                    papiaredniHuk.zychodnyjaLitary = "дж";
                    papiaredniHuk.bazavyHuk = "d͡ʐ";
                } else {
                    novyHuk = new Huk("ж", "ʐ");
                }
                break;
            case 'з':
                if (papiaredniHuk != null && "д".equals(papiaredniHuk.zychodnyjaLitary)
                        && papiaredniHuk.padzielPasla == 0) {
                    // дз
                    papiaredniHuk.zychodnyjaLitary = "дз";
                    papiaredniHuk.bazavyHuk = "d͡z";
                } else {
                    novyHuk = new Huk("з", "z");
                }
                break;
            case 'і':
                dadacJotKaliPatrebny(papiaredniHuk, c);
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
                novyHuk = new Huk("л", "ɫ");
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
                    papiaredniHuk.setMiakkasc(true);
                }
                break;
            case 'э':
                novyHuk = new Huk("э", "ɛ");
                novyHuk.halosnaja = true;
                break;
            case 'ю':
                dadacJotKaliPatrebny(papiaredniHuk, c);
                novyHuk = new Huk("ю", "u");
                novyHuk.halosnaja = true;
                novyHuk.miakkajaHalosnaja = true;
                break;
            case 'я':
                dadacJotKaliPatrebny(papiaredniHuk, c);
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
                papiaredniHuk.padzielPasla = Huk.PADZIEL_MINUS;
                break;
            case '´':
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

    void dadacJotKaliPatrebny(Huk papiaredni, char current) {
        if ((papiaredni == null || papiaredni.halosnaja || papiaredni.apostrafPasla || papiaredni.miakki != 0
                || papiaredni.zychodnyjaLitary.equals("ў"))) {
            if (papiaredni != null && papiaredni.padzielPasla != 0) {
                return;
            }
            // першая літара ці пасьля пералічаных
            Huk jot = new Huk("", "j");
            jot.miakkajaHalosnaja = true;
            jot.halosnaja = true;
            jot.setMiakkasc(true);
            huki.add(jot);
        } else if (papiaredni != null && "еёюя".indexOf(current) >= 0
                && ("тдржшч".indexOf(papiaredni.zychodnyjaLitary) >= 0 || papiaredni.padzielPasla != 0)) {
            // звычайна сутык прыстаўкі і кораня
            Huk jot = new Huk("", "j");
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
                    return c1 == '´';
                }
            }
        }
        return false;
    }

    public static void main(String[] words) {
        for (String w : words) {
            System.out.println(w + " -> " + fanetykaSlova(w));
        }
    }
}
