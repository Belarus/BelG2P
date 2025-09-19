package org.alex73.fanetyka.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class ReadResource {
    /**
     * Чытае тэкставы файл, што знаходзіцца ў тым самым package, што і cls.
     * Прапускае пустыя радкі і часткі, што пачынаюцца з '#'.
     */
    public static Stream<String> readLines(Class<?> cls, String resourceName) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                lines.add(s);
            }
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
        return lines.stream().map(s -> s.replaceAll("#.*", "").trim()).filter(s -> !s.isEmpty());
    }

    /**
     * Чытае тэкставы файл у фармаце UTF-8 Java Properties, што знаходзіцца ў тым
     * самым package, што і cls.
     */
    public static Properties readProperties(Class<?> cls, String resourceName) {
        Properties props = new Properties();
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(cls.getResourceAsStream(resourceName), StandardCharsets.UTF_8))) {
            props.load(rd);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
        return props;
    }
}
