package org.alex73.fanetyka.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.StressUtils;

public class Amohrafy {
    private static final Map<String, String> naciski = new HashMap<>();
    static {
        try (BufferedReader rd = new BufferedReader(
                new InputStreamReader(Amohrafy.class.getResourceAsStream("amohrafy_naciski.txt"), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                s = s.replaceAll("#.*", "").trim().replace('+', GrammarDB2.pravilny_nacisk);
                if (!s.isEmpty()) {
                    naciski.put(StressUtils.unstress(s), s);
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String resolveAmohraf(String word) {
        String r = naciski.get(word.toLowerCase());
        return r != null ? r : word;
    }
}
