package org.alex73.korpus.voice;

public class FanetykaText {
    public String ipa = "";
    public String skola = "";

    public FanetykaText(String text) {
        String word = "";
        Fanetyka3 f = new Fanetyka3();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            boolean letter = isLetter(c);
            if (c == '-' && i > 0 && !isLetter(text.charAt(i - 1))) {
                letter = false;
            }
            if (letter) {
                word += c;
            } else {
                if (!word.isEmpty()) {
                    f.addWord(word);
                    word = "";
                }
                if (c != ' ') {
                    f.calcFanetyka();
                    ipa += f.toString(Huk.ipa);
                    skola += f.toString(Huk.skolny);
                    f = new Fanetyka3();
                    ipa += c;
                    skola += c;
                }
            }
        }
        if (!word.isEmpty()) {
            f.addWord(word);
        }
        f.calcFanetyka();
        ipa += f.toString(Huk.ipa);
        skola += f.toString(Huk.skolny);
    }

    boolean isLetter(char c) {
        c = Character.toLowerCase(c);
        return "ёйцукенгшўзх'фывапролджэячсмітьбюґ|-".indexOf(c) >= 0;
    }

    public static void main(String[] a) {
        FanetykaText f = new FanetykaText("мозґ");
        System.out.println(f.ipa);
        System.out.println(f.skola);
    }
}
