package org.alex73.fanetyka.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    // static final String FN =
    // "src-fanetyka/org/alex73/fanetyka/impl/slovautvarennie.2.txt";
    static final String FN = "src-fanetyka/org/alex73/fanetyka/impl/slovautvarennie.2.txt";
    static final String FN3 = "src-fanetyka/org/alex73/fanetyka/impl/slovautvarennie.3.txt";
    static final String MAR = "src-fanetyka/org/alex73/fanetyka/impl/marfiemy.txt";
    static final String TEZA = "src-fanetyka/org/alex73/fanetyka/impl/slovautv_teza.txt";
    static final Pattern RE1 = Pattern.compile("(\\{[0-9]+[abа]?\\. ?\\} ?).+");
    static final Pattern RE2 = Pattern.compile("(\\[[0-9]+\\.\\] ).+");
    static final Pattern RE3 = Pattern.compile("([0-9]+a?\\. ).+");

    public static void main(String[] args) throws Exception {
        teza();
    }

    static void teza() throws Exception {
        Map<String, String> r = new TreeMap<>(P);
        for (String line : Files.readAllLines(Paths.get(TEZA))) {
            String[] ws = line.split("\t");
            ws[0] = ws[0].replaceAll("[\\-!']", "");
            String prev = r.put(ws[0], line);
            if (prev != null) {
                System.err.println("Duplicate: " + line + " /// " + prev);
            }
        }
    }

    static void k2() throws Exception {
        List<String> lines = FileUtils.readLines(new File(FN3), "UTF-8");
        for (String line : lines) {
            String[] ws = line.split("\t");
            if (ws[1].equals("???") && ws[2].replaceAll("\\([^\\)]+\\)", "").replaceAll("[^/]", "").length() >= 6) {
                System.out.println(line);
            }
        }
    }

    static void uzn() throws Exception {
        List<String> lines = FileUtils.readLines(new File(FN3), "UTF-8");
        for (int i = 0; i < lines.size(); i++) {
            String[] ws = lines.get(i).trim().split("\t");
            if (ws[0].equals("???")) {
                ws[0] = ws[2].replaceAll("\\([^\\)]+\\)", "").replace("-", "").replace("/", "");
            }
            lines.set(i, String.join("\t", ws));
        }
        FileUtils.writeLines(new File(FN3), lines);
    }

    static void merge() throws Exception {
        List<String> lines = FileUtils.readLines(new File(FN), "UTF-8");
        List<String> mar = FileUtils.readLines(new File(MAR), "UTF-8");
        nnn: for (int i = 0, m = 0; i < lines.size();) {
            String[] ws = lines.get(i).trim().split("\t");
            if (ws.length != 3) {
                throw new Exception(lines.get(i));
            }
            String mline = mar.get(m).trim().replace('%', '!');
            if (mline.equals(ws[2])) {
                i++;
                m++;
            } else {
                for (int inext = i; inext - i < 20; inext++) {
                    for (int mnext = m; mnext < m + 20; mnext++) {
                        String mlinenext = mar.get(mnext).trim().replace('%', '!');
                        String[] wsnext = lines.get(inext).trim().split("\t");
                        if (mlinenext.equals(wsnext[2])) {
                            i = inext;
                            for (int mfrom = m; mfrom < mnext; mfrom++) {
                                String mlinefrom = mar.get(mfrom).trim().replace('%', '!');
                                lines.add(i, "???\t???\t" + mlinefrom);
                                i++;
                            }
                            m = mnext;
                            continue nnn;
                        }
                    }
                }
                throw new Exception();
            }
        }
        FileUtils.writeLines(new File(FN), lines);

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
