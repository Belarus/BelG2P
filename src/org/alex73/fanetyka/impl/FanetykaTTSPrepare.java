package org.alex73.fanetyka.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alex73.korpus.base.GrammarDB2;
import org.alex73.korpus.base.GrammarFinder;

/**
 * Файл для канвертацыі тэкста ў фанетыку для сінтэза маўлення.
 */
public class FanetykaTTSPrepare {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("FanetykaTTSPrepare <infile> <outfile>");
            System.exit(1);
        }
        Set<Character> chars = new HashSet<>();
        List<String> lines = Files.readAllLines(Path.of(args[0]));
        GrammarDB2 db = GrammarDB2.initializeFromJar();
        //GrammarDB2 db = GrammarDB2.initializeFromDir("/home/alex/gits/GrammarDB");
        GrammarFinder finder = new GrammarFinder(db);
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);
            int pos = s.indexOf('|');
            if (pos < 0) {
                throw new Exception("Error in line: " + s);
            }
            String text = s.substring(pos + 1).replace('"', ' ').trim().replaceAll("^\\-", "");
            try {
                FanetykaText ft = new FanetykaText(finder, text);
                for (char c : ft.ipa.toCharArray()) {
                    chars.add(c);
                }
                lines.set(i, s.substring(0, pos + 1) + ft.ipa);
            } catch (Exception ex) {
                throw new Exception("Error in line: " + s, ex);
            }
        }
        Files.write(Path.of(args[1]), lines);
        System.out.println("Сімвалы, што выкарыстоўваюцца ў тэксце : " + String.join("", chars.stream().sorted().map(c -> Character.toString(c)).toList()));
    }
}
