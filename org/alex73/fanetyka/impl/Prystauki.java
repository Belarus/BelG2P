package org.alex73.fanetyka.impl;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

public class Prystauki {

    public static void main(String[] args) throws Exception {
        List<String> lines = FileUtils
                .readLines(new File("src-fanetyka/org/alex73/fanetyka/impl/slovautvarennie.2.txt"), "UTF-8");
        for (String line : lines) {
            String[] ws = line.split("\t");
            if (ws.length != 3) {
                continue;
            }
            process(ws[2]);
        }

        prystauki.entrySet().stream().filter(p->p.getValue()>0).forEach(w -> System.out.println(w));
    }

    static void process(String w) {
        int p = w.indexOf("//");
        if (p <= 0)
            return;

        String pry = w.substring(0, p);
        Integer c = prystauki.get(pry);
        prystauki.put(pry, c == null ? 1 : c.intValue() + 1);
    }

    static Comparator<String> P = new Comparator<String>() {
        Collator BE = Collator.getInstance(new Locale("be"));

        @Override
        public int compare(String o1, String o2) {
            int c = BE.compare(o1.replaceAll("[\\-!']", ""), o2.replaceAll("[\\-!']", ""));
            if (c == 0) {
                c = BE.compare(o1, o2);
            }
            return c;
        }
    };
    static Map<String, Integer> prystauki = new TreeMap<>(P);
}
