

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alex73.corpus.paradigm.Paradigm;
import org.alex73.korpus.base.BelarusianWordNormalizer;
import org.alex73.korpus.base.GrammarDB2;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class FanetykaAllWords {

    static List<String> lines = new ArrayList<>();
    static int fc = 0;

    public static void main(String[] args) throws Exception {
        GrammarDB2 db=   GrammarDB2.initializeFromDir("GrammarDB");
        for (Paradigm p : db.getAllParadigms()) {
            process(p.getLemma());
            // for (LiteForm f : p.forms) {
            // process(f.value);
            // }
        }
        System.out.println(fc + " / " + lines.size());
        FileUtils.writeLines(new File("/tmp/z.html"), "UTF-8", lines);
        System.out.println("done");
    }

    static void process(String word) {
        fc++;
        if (fc % 1000 == 0) {
            System.out.println(fc + " / " + lines.size());
        }
        if (StringUtils.isEmpty(word)) {
            return;
        }
        word = BelarusianWordNormalizer.normalize(word);
        String fanetyka = Fanetyka.fanetykaSlova(word);
        fanetyka = Fanetyka.mark(fanetyka, "<u><b>", "</b></u>");
        if (fanetyka != null) {
            add(word, fanetyka);
        }
    }

    static void add(String word, String fanetyka) {
        lines.add(word + " -> " + fanetyka + "<br/>");
    }
}
