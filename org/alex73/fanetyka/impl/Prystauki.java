package org.alex73.fanetyka.impl;

import java.io.File;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class Prystauki {
    static final Pattern RE1 = Pattern.compile("(\\{[0-9]+[abа]?\\. ?\\} ?).+");
    static final Pattern RE2 = Pattern.compile("(\\[[0-9]+\\.\\] ).+");
    static final Pattern RE3 = Pattern.compile("([0-9]+a?\\. ).+");

    public static void main(String[] args) throws Exception {
        List<String> lines = FileUtils
                .readLines(new File("src-fanetyka/org/alex73/fanetyka/impl/slovautv_teza.txt"), "UTF-8");
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder s = new StringBuilder(lines.get(i));
            if (s.toString().trim().isEmpty()) {
                continue;
            }

            if (fix(RE1, s)) {
                lines.set(i, s.toString().trim());
            } else if (fix(RE2, s)) {
                lines.set(i, s.toString().trim());
            } else if (fix(RE3, s)) {
                lines.set(i, s.toString().trim());
            } else {
                System.err.println(s.toString());
            }
        }
        FileUtils.writeLines(new File("src-fanetyka/org/alex73/fanetyka/impl/slovautv_teza.txt"), lines);
        for (String line : lines) {
            String[] ws = line.split("\t");
            if (ws.length != 3) {
                continue;
            }
            process(ws[2]);
        }

        // prystauki.entrySet().stream().filter(p -> p.getValue() > 0).forEach(w ->
        // System.out.println(w));
    }

    static boolean fix(Pattern re, StringBuilder s) {
        Matcher m = re.matcher(s);
        if (m.matches()) {
            if (!s.toString().startsWith(m.group(1))) {
                throw new RuntimeException();
            }
            s.delete(0, m.group(1).length());
            return true;
        }
        return false;
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
