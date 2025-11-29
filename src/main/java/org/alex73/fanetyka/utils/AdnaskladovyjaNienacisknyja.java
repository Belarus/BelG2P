package org.alex73.fanetyka.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.SetUtils;
import org.alex73.grammardb.StressUtils;
import org.alex73.grammardb.structures.Form;
import org.alex73.grammardb.structures.Variant;

/**
 * Гэты скрыпт выбірае з базы аднаскладовыя часціцы, прыназоўнікі, злучнікі -
 * націск на іх не ставіцца.
 */
public class AdnaskladovyjaNienacisknyja {

    public static void main(String[] args) throws Exception {
        GrammarDB2 db = GrammarDB2.initializeFromDir(args[0]);
        List<String> data = db.getAllParadigms().parallelStream().flatMap(p -> {
            List<String> result = new ArrayList<>();
            for (Variant v : p.getVariant()) {
                String tag = SetUtils.tag(p, v);
                if (tag.startsWith("E") || tag.startsWith("I") || tag.startsWith("C")) {
                    for (Form f : v.getForm()) {
                        if (StressUtils.syllCount(f.getValue()) == 1) {
                            result.add(tag.charAt(0) + "/" + StressUtils.unstress(f.getValue()));
                        }
                    }
                }
            }
            return result.stream();
        }).sorted(Collator.getInstance(Locale.of("be"))).toList();
        Files.write(Path.of("src/org/alex73/fanetyka/impl/nienacisknyja.txt"), data);
    }
}
