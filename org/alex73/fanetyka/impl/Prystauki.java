package org.alex73.fanetyka.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;

public class Prystauki {

    static Set<String> prystauki = new TreeSet<>();

    public static void main(String[] args) throws Exception {
        List<String> lines = FileUtils.readLines(new File("/home/alex/gits/GrammarDB/slovautvarennie.2.csv"),
                "UTF-8");
        for (String line : lines) {
            String[] ws = line.split("\t");
            if (ws.length != 3) {
                continue;
            }
            process(ws[2]);
        }
        List<String> out = new ArrayList<>(prystauki);
        Collections.sort(out, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                StringBuilder s1=new StringBuilder(o1.replace("-", "").replace("'", ""));
                StringBuilder s2=new StringBuilder(o2.replace("-", "").replace("'", ""));
                return s1.reverse().toString().compareTo(s2.reverse().toString());
            }
        });

        out.forEach(w -> System.out.println(w));
        System.out.println(prystauki.size());
    }

    static void process(String w) {
        w = w.replace("!", "").replace("[", "");
        int pos = w.indexOf("//");
        if (pos < 0) {
            return;
        }
        w = w.substring(0, pos).replaceAll("\\-+$", "");
        if (w.isEmpty()) {
            return;
        }
        if ("уўеыаоэёяію".indexOf(w.charAt(w.length() - 1)) >= 0) {
            return;
        }
        prystauki.add(w);
    }

}
