package org.alex73.fanetyka.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alex73.fanetyka.impl.FanetykaTestSpisu.InOut;
import org.alex73.korpus.base.GrammarFinder;
import org.junit.Test;

/**
 * Паказвае тыя прыклады з спісу спраўджанага, для якіх у лог дадаецца "@".
 * Зручна для пошуку прыкладаў: для гэтага трэба ў з'яве дадаць запіс у лог
 * "@", і гэты клас пакажа той выпадак.
 */
public class PosukPrykladau {
    @Test
    public void test() throws Exception {
        GrammarFinder finder = FanetykaTestSpisu.readGrammarDB();
        List<String> in = Files.readAllLines(Path.of("test/org/alex73/fanetyka/impl/spraudzana.txt"));
        Set<String> excludes = new HashSet<>(Files.readAllLines(Path.of("test/org/alex73/fanetyka/impl/spraudzanaSkip.txt")));
        List<InOut> data = FanetykaTestSpisu.readSparaudzanaje(in, excludes);
        for (InOut d : data) {
            try {
                Fanetyka3 f = new Fanetyka3(finder, null);
                for (String w : d.word.split("\\s+")) {
                    f.addWord(w);
                }

                f.calcFanetyka();
                String tr = f.toString(Huk.ipaOldStress);
                if (f.why.contains("@")) {
                    if (d.expected.equals(tr)) {
                        System.out.println(d.word + "\t" + tr);
                    } else {
                        System.out.println(d.word + "\t" + tr + "\tЧАКАЕЦЦА " + d.expected);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Памылка ў слове '" + d.word + "':" + ex.getClass() + " - " + ex.getMessage());
                throw ex;
            }
        }
    }
}
