package org.alex73.fanetyka.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class FanetykaTestSpisu {
    public static void main(String[] args) throws Exception {
        List<String> in = FileUtils.readLines(new File("src/org/alex73/korpus/voice/spraudzana.txt"));
        Set<String> f0 = new HashSet<>(
                FileUtils.readLines(new File("src/org/alex73/korpus/voice/spraudzanaSkip.txt")));

        int count=0;
        for (int i = 0; i < in.size(); i++) {
            int p=in.get(i).indexOf("=>");
            if (p<0) {
                throw new Exception("Wrong line: "+in.get(i));
            }
            String word = in.get(i).substring(0, p).toLowerCase().trim().replaceFirst("^\\[?[0-9]+[абab]?\\.\\]?", "").trim()
                    .replace("%", "").replace("!", "").replace("?", "").replace("@", "");
            if (f0.contains(word)) {
                continue;
            }
            
            String expected = in.get(i).substring(p+2).trim().replaceFirst("^[0-9]+\\.", "").replaceFirst("^\\[", "").replaceFirst("\\]$", "").trim()
                    .replace("ˈ", "");
            //String tr = Fanetyka2.fanetykaSlova(word);
            try {
                Fanetyka3 f=new Fanetyka3();
                for(String w:word.split("\\s+")) {
                  f.addWord(w);
                }

                f.calcFanetyka();
            String tr=f.toString(Huk.ipa);
            if (!expected.equals(tr)) {
                System.out.println(word + ": " + kir(tr) + " замест " + kir(expected));
                count++;
            }
            }catch (Exception ex) {
                System.err.println("Памылка ў слове '"+word+"':"+ex.getMessage());
                throw ex;
            }
        }
        System.out.println(count);
    }

    static String kir(String w) {
        String m = w;//Fanetyka.mark(w, "<", ">");
        return m != null ? m : w;
    }
}
