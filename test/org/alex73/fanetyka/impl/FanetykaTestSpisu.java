package org.alex73.fanetyka.impl;

import static org.junit.Assert.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alex73.korpus.base.GrammarDB2;
import org.alex73.korpus.base.GrammarFinder;
import org.junit.Test;

public class FanetykaTestSpisu {
    static GrammarFinder readGrammarDB() throws Exception {
        GrammarDB2 db = GrammarDB2.initializeFromDir("/data/gits/GrammarDB");
        return new GrammarFinder(db);
    }

    static class InOut {
        int lineIndex;
        String word, expected;
    }

    static List<InOut> readSparaudzanaje(List<String> in, Set<String> excludes) throws Exception {
        List<InOut> result = new ArrayList<>();

        for (int i = 0; i < in.size(); i++) {
            String s = in.get(i);
            int p = s.indexOf("=>");
            if (p < 0) {
                throw new Exception("Wrong line: " + s);
            }
            InOut line = new InOut();
            line.lineIndex = i;

            line.word = s.substring(0, p).toLowerCase().trim().replaceFirst("^\\[?[0-9]+[абab]?\\.\\]?", "").trim().replace("%", "´").replace("ё", "ё´")
                    .replace("´´", "´").replace("!", "").replace("?", "").replace("@", "");
            if (excludes.contains(line.word)) {
                continue;
            }

            line.expected = s.substring(p + 2).trim().replaceFirst("^[0-9]+\\.", "").replaceFirst("^\\[", "").replaceFirst("\\]$", "").trim();
            if (!line.word.contains("´")) {
                line.expected = line.expected.replace("ˈ", "");
            }

            result.add(line);
        }
        return result;
    }

    @Test
    public void test() throws Exception {
        GrammarFinder finder = readGrammarDB();
        List<String> in = Files.readAllLines(Path.of("test/org/alex73/fanetyka/impl/spraudzana.txt"));
        Set<String> excludes = new HashSet<>(Files.readAllLines(Path.of("test/org/alex73/fanetyka/impl/spraudzanaSkip.txt")));
        List<InOut> data = readSparaudzanaje(in, excludes);

        int errors = 0;
        for (InOut d : data) {
            try {
                Fanetyka3 f = new Fanetyka3(finder, null);
                for (String w : d.word.split("\\s+")) {
                    f.addWord(w);
                }

                f.calcFanetyka();
                String tr = f.toString(Huk.ipaOldStress);
                if (!d.expected.equals(tr)) {
                    System.out.println(d.word + ": " + kir(tr) + " замест " + kir(d.expected));
                    errors++;
                    if (d.expected.equals(tr.replace("´", ""))) {
                        in.set(d.lineIndex, in.get(d.lineIndex).replace(d.expected, tr));
                    }
                }
            } catch (Exception ex) {
                System.err.println("Памылка ў слове '" + d.word + "':" + ex.getClass() + " - " + ex.getMessage());
                throw ex;
            }
        }
        Files.write(Path.of("test/org/alex73/fanetyka/impl/spraudzana.txt"), in);
        assertEquals(0, errors);
    }

    static String kir(String w) {
        String m = w;// Fanetyka.mark(w, "<", ">");
        return m != null ? m : w;
    }
}
