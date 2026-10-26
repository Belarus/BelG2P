package org.alex73.fanetyka.impl.check;

import org.alex73.fanetyka.impl.Fanetyka3;
import org.alex73.fanetyka.impl.FanetykaConfig;
import org.alex73.fanetyka.impl.str.ToStringIPAOldStress;
import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FanetykaTestSpisu {
    private final GrammarFinder finder;
    private final List<String> in;
    private final Set<String> excludes;
    public final List<String> noErrors = new ArrayList<>();

    /**
     * Запуск тэста, які правярае адпаведнасць фанетыкі з спісам ужо спраўджанага.
     */
    public static void main(String[] args) throws Exception {
        String GRAMMAR_DB_DIR = System.getenv("GRAMMAR_DB_DIR");
        if (GRAMMAR_DB_DIR == null) {
            throw new Exception("GRAMMAR_DB_DIR environment variable is not set");
        }
        GrammarDB2 db = GrammarDB2.initializeFromDir(GRAMMAR_DB_DIR);
        GrammarFinder finder = new GrammarFinder(db);
        List<String> results = new FanetykaTestSpisu(finder).test(null, null);
        if (!results.isEmpty()) {
            for (String result : results) {
                System.out.println(result);
            }
        }
    }

    static class InOut {
        int lineIndex;
        String word, expected;
    }

    List<String> load(String classpath) throws Exception {
        List<String> result = new ArrayList<>();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(FanetykaTestSpisu.class.getResourceAsStream(classpath), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                result.add(s);
            }
        }
        return result;
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

            line.word = s.substring(0, p).trim().replaceFirst("^\\[?[0-9]+[абab]?\\.\\]?", "").trim()
                    .replace("%", GrammarDB2.pravilny_nacisk + "").replace("ё", "ё´").replace("´´", "´").replace("!", "").replace("?", "").replace("@", "");
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

    public FanetykaTestSpisu(GrammarFinder finder) throws Exception {
        this.finder = finder;
        in = load("spraudzana.txt");
        excludes = new HashSet<>(load("spraudzanaSkip.txt"));
    }

    public List<String> test(Map<String, byte[]> tables, String debugRuleName) throws Exception {
        FanetykaConfig config = tables != null ? new FanetykaConfig(finder, tables) : new FanetykaConfig(finder);

        List<InOut> data = readSparaudzanaje(in, excludes);

        return data.parallelStream().map(d -> {
            List<String> words = new ArrayList<>();
            try {
                Fanetyka3 f = new Fanetyka3(config);
                f.setDebugRuleName(debugRuleName);
                for (String w : d.word.split("\\s+")) {
                    words.add(w);
                }

                f.calcFanetyka(words);
                String tr = f.toString(new ToStringIPAOldStress());
                // адкідаем новыя combined у IPA
                tr = tr.replaceAll("[\u031E\u032A\u035F\u0331]", "");
                if (!d.expected.equals(tr) && !d.expected.replace("´", "").replace("\u0301", "").equals(tr.replace("´", "").replace("\u0301", ""))) {
                    if (debugRuleName != null && !f.debugRuleProcessed) {
                        return null;
                    }
                    return d.word + ": " + kir(tr) + ", але чакаецца " + kir(d.expected);
                    // if (d.expected.equals(tr.replace("´", ""))) {
                    // in.set(d.lineIndex, in.get(d.lineIndex).replace(d.expected, tr));
                    // }
                }
                if (debugRuleName != null && f.debugRuleProcessed) {
                    noErrors.add(words.toString());
                }
                return null;
            } catch (Exception ex) {
                return "Памылка ў слове '" + d.word + "':" + ex.getClass() + " - " + ex.getMessage();
            }
        }).filter(e -> e != null).toList();
    }

    static String kir(String w) {
        String m = w;// Fanetyka.mark(w, "<", ">");
        return m != null ? m : w;
    }
}
