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
            startIteration();

            config.processPierachodZG.process(this);
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

    protected void startIteration() {

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

    // TODO дадаць націскі
    // TODO прыстаўкі перад еёюя - толькі калі ёсць апостраф
    static final String[] PRYSTAUKI = new String[] { "ад", "безад", "беспад", "вод", "звод", "наад", "навод", "напад", "над", "неад", "непад", "непрад",
            "павод", "панад", "папад", "падад", "пад", "перапад", "перад", "под", "прад", "прыад", "прыпад", "спад", "спрад", "за", "з",
            "супад", "най" /*
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
        if (w.contains("/") || w.contains("|")) {
            // ужо пазначана
            return w;
        }

        Set<String> foundForms = new TreeSet<>();
        PrystaukiApplied fromDB = null;
        // у базе няма марфалогіі - спрабуем выцягнуць націскі і ґ
        for (Paradigm p : config.finder.getParadigms(w)) {
            for (Variant v : p.getVariant()) {
                for (Form f : v.getForm()) {
                    if (f.getValue().isEmpty()) {
                        continue;
                    }
                    if (compareWord(w, f.getValue())) {
                        PrystaukiApplied a = applyPrystauki(f.getValue().toLowerCase(), v.getPrystauki());
                        if (foundForms.isEmpty()) {
                            fromDB = a;
                            why.add("Націскі, пазначэнне ґ і прыставак з базы: " + fromDB.word);
                        }
                        foundForms.add(a.word + (a.prystaukiVyznacanyja ? "" : " - прыстаўкі невызнячаныя"));
                    }
                }
            }
        }
        if (foundForms.isEmpty()) {
            // не знайшлі ў базе - прастаўляем націскі на о, ё
            int p = w.indexOf('о');
            if (p < 0) {
                p = w.indexOf('ё');
            }
            String cw;
            if (p >= 0) {
                cw = w.substring(0, p + 1) + GrammarDB2.pravilny_nacisk + w.substring(p + 1);
                why.add("Аўтаматычна пазначаныя націскі: " + w);
            } else {
                cw = w;
            }
            fromDB = new PrystaukiApplied(cw, false);
        } else if (foundForms.size() > 1) {
            why.add("Больш за 1 варыянт для '" + w + "' з базы: " + foundForms);// TODO check for all words
        }

        w = fromDB.word;

        if (!fromDB.prystaukiVyznacanyja) {
            // пазначаем найбольш распаўсюджаныя прыстаўкі
            String wl = w.toLowerCase();
            for (String p : PRYSTAUKI) {
                if (wl.length() > p.length() + 2 && wl.startsWith(p)) {
                    boolean prystauka = false;
                    char nextLetter = wl.charAt(p.length());
                    char nextLetter2 = wl.charAt(p.length() + 1);
                    if (p.endsWith("й")) {
                        prystauka = true;
                    } else if (nextLetter == GrammarDB2.pravilny_apostraf
                            && (nextLetter2 == 'е' || nextLetter2 == 'ё' || nextLetter2 == 'ю' || nextLetter2 == 'я')) {
                        // прыстаўкі перад еёюя - толькі калі ёсць апостраф, але калі прыстаўка не на -й
                        // і выпадае з гэтага раду, бо ёсць заінелы. але заезджаны
                        prystauka = true;
                    } else if (nextLetter == 'е' || nextLetter == 'ё' || nextLetter == 'ю' || nextLetter == 'я') {
                        prystauka = false;
                    } else {
                        prystauka = true;
                    }
                    if (prystauka) {
                        w = w.substring(0, p.length()) + '/' + w.substring(p.length());
                        why.add("Мяркуем, што прыстаўка '" + p + "'");
                    }
                }
            }
        }

        return w;
    }

    record PrystaukiApplied(String word, boolean prystaukiVyznacanyja) {
    }

    /**
     * Дадаем прыстаўкі ў слова
     */
    PrystaukiApplied applyPrystauki(String word, String prystauki) {
        if (prystauki == null) {
            return new PrystaukiApplied(word, false);
        } else {
            String pr = prystauki.replace("/", "");
            if (!word.startsWith(pr)) {
                why.add("Неадапаведнасць прыставак у базе"); // TODO check ?
                return new PrystaukiApplied(word, false);
            } else {
                return new PrystaukiApplied(prystauki + word.substring(pr.length()), true);
            }
        }
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
            //jot.halosnaja = true;
            jot.setMiakkasc(true);
            jot.miakki=Huk.MIAKKASC_PAZNACANAJA;
            huki.add(jot);
        } else if (papiaredni != null && "еёюя".indexOf(current) >= 0 && ("тдржшч".indexOf(papiaredni.zychodnyjaLitary) >= 0 || papiaredni.padzielPasla != 0)) {
            // звычайна сутык прыстаўкі і кораня
            Huk jot = new Huk("", BAZAVY_HUK.j);
            //jot.halosnaja = true;
            jot.setMiakkasc(true);
            jot.miakki=Huk.MIAKKASC_PAZNACANAJA;
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
}
