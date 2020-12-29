package org.alex73.fanetyka.impl;

public class Fanetyka2 {
    static int pos;
    static String word;
    static StringBuilder out = new StringBuilder();

    /**
     * Спраўдзіць ці толькі малыя літары, апостраф і націск ў слове.
     */
    private static void checkWord() {
        for (int i = 0; i < word.length(); i++) {
            if (!isLitara(word.charAt(i))) {
                throw new RuntimeException("Не літара: " + word.charAt(i));
            }
        }
    }

    public static boolean isLitara(char c) {
        return "-ьйцкнгшўзхфвпрлджчсмтбёуеыаоэяію'’".indexOf(c) >= 0;
    }

    public static String fanetykaSlova(String w) {
        word = w.toLowerCase();
        checkWord();
        out.setLength(0);
        for (pos = 0; pos < word.length(); pos++) {
            char c = word.charAt(pos);
            switch (c) {
            case 'а':
                pravila("а -> а");
                out.append('a');
                break;
            case 'б':
                String litarab;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    if (dalejHluchi()) {
                        pravila("б -> pʲ бо далей ідзе глухі і мяккі знак ці мяккая галосная");
                        litarab = "pʲ";
                    } else {
                        pravila("б -> bʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        litarab = "bʲ";
                    }
                } else {
                    if (dalejHluchi()) {
                        pravila("б -> p, бо далей ідзе глухі і не мяккая");
                        litarab = "p";
                    } else {
                        pravila("б -> b, бо не мяккая");
                        litarab = "b";
                    }
                }
                dadacCiPadvoic(litarab);
                break;
            case 'в':
                String litarav;
                if (dalejMiakkasc()) {
                    pravila("в -> vʲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litarav = "vʲ";
                } else if (pos+1<word.length() && (word.charAt(pos+1)=='о'||word.charAt(pos+1)=='у')){
                    pravila("в -> β, бо наступная 'о' ці 'у'");
                    litarav = "β";
                } else {
                    pravila("в -> v, бо не мяккая");
                    litarav = "v";
                }
                dadacCiPadvoic(litarav);
                break;
            case 'г':
                String litarah;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    if (dalejHluchi()) {
                        pravila("г -> xʲ бо далей ідзе глухі і мяккі знак ці мяккая галосная");
                        litarah = "xʲ";
                    } else {
                        pravila("г -> ɣʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        litarah = "ɣʲ";
                    }
                } else {
                    if (dalejHluchi()) {
                        pravila("г -> x, бо далей ідзе глухі і не мяккая");
                        litarah = "x";
                    } else {
                        pravila("г -> ɣ, бо не мяккая");
                        litarah = "ɣ";
                    }
                }
                dadacCiPadvoic(litarah);
                break;
            case 'ґ':
                String litarag;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    if (dalejHluchi()) {
                        pravila("ґ -> kʲ бо далей ідзе глухі і мяккі знак ці мяккая галосная");
                        litarag = "kʲ";
                    } else {
                        pravila("ґ -> gʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        litarag = "gʲ";
                    }
                } else {
                    if (dalejHluchi()) {
                        pravila("ґ -> k, бо далей ідзе глухі і не мяккая");
                        litarag = "k";
                    } else {
                        pravila("ґ -> g, бо не мяккая");
                        litarag = "g";
                    }
                }
                dadacCiPadvoic(litarag);
                break;
            case 'д':
                // можа ддж/ддз ?
                if (now("ддж") || now("дьдж")) {
                    pos += now("ддж") ? 2 : 3;
                    if (!nastupnyZycny() && dalejMiakkasc()) {
                        pravila("ддж ->  d͡ʐʲː бо далей ідзе мяккі знак ці мяккая галосная");
                        out.append("d͡ʐʲː");
                    } else {
                        pravila("ддж -> d͡ʐː бо не мяккая");
                        out.append("d͡ʐː");
                    }
                } else if (now("ддз") || now("дьдз")) {
                    pos += now("ддз") ? 2 : 3;
                    if (dalejMiakkasc()) {
                        pravila("ддз ->  d͡zʲː бо далей ідзе мяккі знак ці мяккая галосная");
                        out.append("d͡zʲː");
                    } else {
                        pravila("ддз -> d͡zː бо не мяккая");
                        out.append("d͡zː");
                    }
                } else if (now("дж")) {// ці дж/дз ?
                    pos++;
                    if (!nastupnyZycny() && dalejMiakkasc()) {
                        pravila("дж ->  d͡ʐʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        out.append("d͡ʐʲ");
                    } else {
                        pravila("дж -> d͡ʐ бо не мяккая");
                        out.append("d͡ʐ");
                    }
                } else if (now("дз")) {
                    pos++;
                    if (dalejMiakkasc()) {
                        pravila("дз ->  d͡zʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        out.append("d͡zʲ");
                    } else {
                        pravila("дз -> d͡z бо не мяккая");
                        out.append("d͡z");
                    }
                } else { // проста д
                    String litarad;
                    if (!nastupnyZycny() && dalejMiakkasc()) {
                        if (dalejHluchi()) {
                            pravila("д ->  t͡sʲ бо далей ідзе глухі і мяккі знак ці мяккая галосная");
                            litarad = "t͡sʲ";// аналізаваць
                        } else {
                            pravila("д -> dʲ бо далей ідзе мяккі знак ці мяккая галосная");
                            litarad = "dʲ";// аналізаваць
                        }
                    } else {
                        if (dalejHluchi()) {
                            pravila("д -> t, бо далей ідзе глухі і не мяккая");
                            litarad = "t";
                        } else {
                            pravila("д -> d, бо не мяккая");
                            litarad = "d";
                        }
                    }
                    dadacCiPadvoic(litarad);
                }
                break;
            case 'е':
                if (napacatkuCiPaslaHalosnaj()) {
                    pravila("е -> jɛ на пачатку слова і пасьля галосных - дадаецца j");
                    out.append("jɛ");
                } else {
                    pravila("е -> ɛ пасьля зычных");
                    out.append("ɛ");
                }
                break;
            case 'ё':
                if (napacatkuCiPaslaHalosnaj()) {
                    pravila("ё -> jɔ на пачатку слова і пасьля галосных - дадаецца j");
                    out.append("jɔ");
                } else {
                    pravila("ё -> ɔ пасьля зычных");
                    out.append("ɔ");
                }
                break;
            case 'ж':
                String litarazh;
                if (dalejHluchi()) {
                    pravila("ж -> ʂ, бо заўсёды цьвёрды і далей ідзе глухі і не мяккая");
                    litarazh = "ʂ";
                } else {
                    pravila("ж -> ʐ, бо заўсёды цьвёрды");
                    litarazh = "ʐ";
                }
                dadacCiPadvoic(litarazh);
                break;
            case 'з':
                String litaraz;
                if (dalejMiakkasc()) {
                    if (dalejHluchi()) {
                        pravila("з -> sʲ бо далей ідзе глухі і мяккі знак ці мяккая галосная");
                        litaraz = "sʲ";
                    } else {
                        pravila("з -> zʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        litaraz = "zʲ";
                    }
                } else {
                    if (dalejHluchi()) {
                        pravila("з -> s, бо далей ідзе глухі і не мяккая");
                        litaraz = "s";
                    } else {
                        pravila("з -> z, бо не мяккая");
                        litaraz = "z";
                    }
                }
                dadacCiPadvoic(litaraz);
                break;
            case 'і':
                if (napacatkuCiPaslaHalosnaj()) {
                    pravila("і -> ji на пачатку слова і пасьля галосных - дадаецца j");
                    out.append("ji");
                } else {
                    pravila("і -> i пасьля зычных");
                    out.append("i");
                }
                break;
            case 'й':
                pravila("й -> j");
                out.append('j');
                break;
            case 'к':
                String litarak;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    pravila("к -> c бо далей ідзе мяккі знак ці мяккая галосная");
                    litarak = "c";
                } else {
                    pravila("к -> k, бо не мяккая");
                    litarak = "k";
                }
                dadacCiPadvoic(litarak);
                break;
            case 'л':
                String litaral;
                if (dalejMiakkasc()) {
                    pravila("л -> ʎ бо далей ідзе мяккі знак ці мяккая галосная");
                    litaral = "ʎ";
                } else {
                    pravila("л -> l, бо не мяккая");
                    litaral = "l";
                }
                dadacCiPadvoic(litaral);
                break;
            case 'м':
                String litaram;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    pravila("м -> mʲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litaram = "mʲ";
                } else {
                    pravila("м -> m, бо не мяккая");
                    litaram = "m";
                }
                dadacCiPadvoic(litaram);
                break;
            case 'н':
                String litaran;
                if (dalejMiakkasc()) {
                    pravila("н -> ɲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litaran = "ɲ";
                } else {
                    pravila("н -> n, бо не мяккая");
                    litaran = "n";
                }
                dadacCiPadvoic(litaran);
                break;
            case 'о':
                pravila("о -> ɔ");
                out.append('ɔ');
                break;
            case 'п':
                String litarap;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    if (dalejZvonki()) {
                        pravila("п -> p бо далей ідзе звонкі і мяккі знак ці мяккая галосная");
                        litarap = "bʲ";
                    } else {
                        pravila("п -> p бо далей ідзе мяккі знак ці мяккая галосная");
                        litarap = "pʲ";
                    }
                } else {
                    if (dalejZvonki()) {
                        pravila("п -> b, бо далей ідзе звонкі і не мяккая");
                        litarap = "b";
                    } else {
                        pravila("п -> p, бо не мяккая");
                        litarap = "p";
                    }
                }
                dadacCiPadvoic(litarap);
                break;
            case 'р':
                pravila("р -> r, бо заўсёды цьвёрды");
                dadacCiPadvoic("r");
                break;
            case 'с':
                String litaras;
                if (dalejMiakkasc()) {
                    if (dalejZvonki()) {
                        pravila("с -> zʲ бо далей ідзе звонкі і мяккі знак ці мяккая галосная");
                        litaras = "zʲ";
                    } else {
                        pravila("с -> sʲ бо далей ідзе мяккі знак ці мяккая галосная");
                        litaras = "sʲ";
                    }
                } else {
                    if (dalejZvonki()) {
                        pravila("с -> z, бо далей ідзе звонкі і не мяккая");
                        litaras = "z";
                    } else {
                        pravila("с -> s, бо не мяккая");
                        litaras = "s";
                    }
                }
                dadacCiPadvoic(litaras);
                break;
            case 'т':
                String litarat;
                if (dalejMiakkasc()) {
                    pravila("т -> t͡sʲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litarat = "t͡sʲ";
                } else {
                    pravila("т -> t, бо не мяккая");
                    litarat = "t";
                }
                dadacCiPadvoic(litarat);
                break;
            case 'у':
                pravila("у -> u");
                out.append('u');
                break;
            case 'ў':
                pravila("ў -> u̯");
                out.append("u̯");
                break;
            case 'ф':
                String litaraf;
                if (dalejMiakkasc()) {
                    pravila("ф -> fʲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litaraf = "fʲ";
                } else {
                    pravila("ф -> f, бо не мяккая");
                    litaraf = "f";
                }
                dadacCiPadvoic(litaraf);
                break;
            case 'х':
                String litarach;
                if (!nastupnyZycny() && dalejMiakkasc()) {
                    pravila("х -> ç бо далей ідзе мяккі знак ці мяккая галосная");
                    litarach = "ç";
                } else {
                    pravila("х -> x, бо не мяккая");
                    litarach = "x";
                }
                dadacCiPadvoic(litarach);
                break;
            case 'ц':
                String litarac;
                if (dalejMiakkasc()) {
                    pravila("ц -> t͡sʲ бо далей ідзе мяккі знак ці мяккая галосная");
                    litarac = "t͡sʲ";
                } else {
                    pravila("ц -> t͡s, бо не мяккая");
                    litarac = "t͡s";
                }
                dadacCiPadvoic(litarac);
                break;
            case 'ч':
                pravila("ч -> t͡ʂ");
                dadacCiPadvoic("t͡ʂ");
                break;
            case 'ш':
                pravila("ш -> ʂ бо заўсёды цьвёрды");
                dadacCiPadvoic("ʂ");
                break;
            case 'ы':
                pravila("ы -> ɨ");
                out.append('ɨ');
                break;
            case 'ь':
                pravila("мяккі знак не дае гуку");
                break;
            case 'э':
                pravila("э -> ɛ");
                out.append('ɛ');
                break;
            case 'ю':
                if (napacatkuCiPaslaHalosnaj()) {
                    pravila("ю -> ju на пачатку слова і пасьля галосных - дадаецца j");
                    out.append("ju");
                } else {
                    pravila("ю -> u пасьля зычных");
                    out.append("u");
                }
                break;
            case 'я':
                if (napacatkuCiPaslaHalosnaj()) {
                    pravila("я -> ja на пачатку слова і пасьля галосных - дадаецца j");
                    out.append("ja");
                } else {
                    pravila("я -> a пасьля зычных");
                    out.append("a");
                }
                break;
            case '\'':
                pravila("Апостраф уплывае на літары але не мае свайго гуку");
                break;
            default:
                pravila("Прапускаем '" + c + "'");
                break;
            }
        }
        return out.toString();
    }

    /**
     * Дадаць ці пазначыць падваеньне калі супадае з папярэднім.
     */
    static void dadacCiPadvoic(String zycnaja) {
        if (was(zycnaja)) {
            pravila("Падваеньне " + zycnaja);
            out.append("ː");
        } else if (zycnaja.length()==2 && zycnaja.charAt(1)=='ʲ' && out.length()>0 && zycnaja.charAt(0)==out.charAt(out.length()-1)) {
            pravila("Падваеньне " + zycnaja+" з ʲ");
            out.append("ʲː");
        } else {
            out.append(zycnaja);
        }
    }

    /**
     * Ці папярэдні гук такі ?
     */
    static boolean was(String s) {
        if (s.length() > out.length()) {
            return false;
        }
        return out.substring(out.length() - s.length()).equals(s);
    }

    static boolean now(String s) {
        if (word.length() - pos < s.length()) {
            return false;
        }
        return word.substring(pos, pos + s.length()).equals(s);
    }

    /**
     * Ці ёсьць мяккі знак ці мяккая галосная пасьля ? Выключэньні - галосныя да гкх.
     */
    static boolean dalejMiakkasc() {
        for (int i = pos + 1; i < word.length(); i++) {
            char c = word.charAt(i);
            if ("гґкх".indexOf(c) >= 0) {
                return false;
            } else if ("ьёеяію".indexOf(c) >= 0) {
                return true;
            } else if ("уыаоэ".indexOf(c) >= 0) {
                return false;
            }
        }
        return false;
    }

    /**
     * Калі наступная літара - галосная.
     */
    static boolean nastupnyZycny() {
        int i = now("ь") ? pos + 2 : pos + 1;
        if (word.length() <= i) {
            return false;
        }
        char c = word.charAt(i);
        return "йцкнгшўзхфвпрлджчсмтб".indexOf(c) >= 0;
    }

    static boolean dalejHluchi() {
        int i = now("ь") ? pos + 2 : pos + 1;
        if (word.length() <= i) {
            return false;
        }
        char c = word.charAt(i);
        return "пктчшсхц".indexOf(c) >= 0;
    }

    static boolean dalejZvonki() {
        int i = now("ь") ? pos + 2 : pos + 1;
        if (word.length() <= i) {
            return false;
        }
        char c = word.charAt(i);
        return "бгґджз".indexOf(c) >= 0;
    }

    /**
     * Ці напачатку слова альбо пасьля галоснай.
     */
    static boolean napacatkuCiPaslaHalosnaj() {
        if (pos == 0) {
            return true;
        } else if ("уыаоэёеяію".indexOf(word.charAt(pos - 1)) >= 0) {
            return true;
        }
        return false;
    }

    private static void pravila(String rule) {
        // System.out.println(rule);
    }
}
