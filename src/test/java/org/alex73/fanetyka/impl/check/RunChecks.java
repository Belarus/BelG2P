package org.alex73.fanetyka.impl.check;

import org.alex73.fanetyka.impl.Fanetyka3;
import org.alex73.fanetyka.impl.FanetykaConfig;
import org.alex73.grammardb.GrammarDB2;
import org.alex73.grammardb.GrammarFinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Скрыпт для стварэння фанетыкі для ўсіх радкоў файла і праверкі рэгрэсій.
 * <p>
 * Выкарыстоўваецца для праверкі ўплыву пры зменах фанетычных правілаў:
 * <ul>
 *   <li>Чытае {@code expected.txt} з указанага каталога (фармат радкоў: {@code тэкст === чаканая_фанетыка}).</li>
 *   <li>Патрабуе пераменную асяроддзя {@code GRAMMAR_DB_DIR} для ініцыялізацыі граматычнай базы.</li>
 *   <li>Паралельна разлічвае фанетыку праз {@link Fanetyka3}.</li>
 *   <li>Запісвае поўны разлічаны вынік у {@code output.txt}.</li>
 *   <li>Пры разыходжанні паміж чаканым і фактычным значэннем запісвае адрозныя радкі
 *       ў {@code part-expected.txt} і {@code part-output.txt} для зручнага diff-параўнання.</li>
 * </ul>
 */
public class RunChecks {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: RunChecks <input_dir>");
            System.exit(1);
        }
        Path dir = Path.of(args[0]);
        try (BufferedReader in = Files.newBufferedReader(dir.resolve("expected.txt"), StandardCharsets.UTF_8);
             BufferedWriter out = Files.newBufferedWriter(dir.resolve("output.txt"), StandardCharsets.UTF_8);
             BufferedWriter outExpectedPart = Files.newBufferedWriter(dir.resolve("part-expected.txt"), StandardCharsets.UTF_8);
             BufferedWriter outOutputPart = Files.newBufferedWriter(dir.resolve("part-output.txt"), StandardCharsets.UTF_8);
             BufferedWriter outExpectedNoStressPart = Files.newBufferedWriter(dir.resolve("part-expected-nostress.txt"), StandardCharsets.UTF_8);
             BufferedWriter outOutputNoStressPart = Files.newBufferedWriter(dir.resolve("part-output-nostress.txt"), StandardCharsets.UTF_8);
        ) {

            // Ініцыялізацыя граматычнай базы праз зменную асяроддзя GRAMMAR_DB_DIR
            String GRAMMAR_DB_DIR = System.getenv("GRAMMAR_DB_DIR");
            if (GRAMMAR_DB_DIR == null) {
                throw new Exception("GRAMMAR_DB_DIR environment variable is not set");
            }
            GrammarDB2 db = GrammarDB2.initializeFromDir(GRAMMAR_DB_DIR);
            GrammarFinder finder = new GrammarFinder(db);
            FanetykaConfig config = new FanetykaConfig(finder);

            // Паралельны разлік фанетыкі і паслядоўны запіс вынікаў і разыходжанняў
            in.lines().parallel().map(line -> {
                try {
                    Record r = new Record(line);
                    String[] words = r.text.split("\\s+");
                    Fanetyka3 f = new Fanetyka3(config);
                    f.calcFanetyka(Arrays.asList(words));
                    r.output = f.toString();
                    return r;
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }).forEachOrdered(result -> {
                try {
                    out.write(result.text + "  ===  " + result.output);
                    out.newLine();
                    // Калі вынік адрозніваецца ад чаканага — фіксуем разыходжанне
                    if (!result.expected.equals(result.output)) {
                        System.err.println("Mismatch: " + result);
                        outExpectedPart.write(result.text + "  ===  " + result.expected);
                        outExpectedPart.newLine();
                        outOutputPart.write(result.text + "  ===  " + result.output);
                        outOutputPart.newLine();
                        String expectedNoStress = result.expected.replaceAll("[ˈ\u0301]", "");
                        String outputNoStress = result.output.replaceAll("[ˈ\u0301]", "");
                        if (!expectedNoStress.equals(outputNoStress)) {
                            outExpectedNoStressPart.write(result.text + "  ===  " + expectedNoStress);
                            outExpectedNoStressPart.newLine();
                            outOutputNoStressPart.write(result.text + "  ===  " + outputNoStress);
                            outOutputNoStressPart.newLine();
                        }
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    public static class Record {
        private final String text;
        private final String expected;
        private String output;

        public Record(String line) {
            String[] parts = line.split("=+");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid line format: " + line);
            }
            this.text = parts[0].trim();
            this.expected = parts[1].trim();
            this.output = null;
        }

        @Override
        public String toString() {
            return text + ": " + expected + " => " + output;
        }
    }
}
