package org.alex73.fanetyka.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.alex73.fanetyka.config.Case.Example;
import org.alex73.fanetyka.impl.Huk;

public class TsvCrossConfig implements IConfig {
    private final String configName;
    private final List<String[]> lines = new ArrayList<>();
    private int lineIndex;
    public CaseCross cross;
    public List<Example> examples = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new TsvCrossConfig("Miakkasc");
    }

    public TsvCrossConfig(String name) throws Exception {
        this.configName = name;
        try (InputStream in = TsvConfig.class.getResourceAsStream("/" + name + ".tsv")) {
            load(in);
        }
    }

    public TsvCrossConfig(String name, InputStream in) throws Exception {
        this.configName = name;
        load(in);
    }

    @Override
    public List<Example> getExamples() {
        return examples;
    }

    private void load(InputStream in) throws Exception {
        try (BufferedReader rd = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String s;
            while ((s = rd.readLine()) != null) {
                lines.add(split(s));
            }
        }

        for (lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String[] line = lines.get(lineIndex);
            if (line.length == 0 || line[0].isBlank()) {
                continue;
            } else if ("Cross".equals(line[0])) {
                readCross();
            } else if ("Прыклады".equals(line[0])) {
                lineIndex++;
                parseExamples();
            } else {
                err(0, "Невядомы загаловак: " + line[0]);
            }
        }
    }

    private void readCross() throws Exception {
        List<Huk.BAZAVY_HUK> cols = new ArrayList<>();
        List<Huk.BAZAVY_HUK> rows = new ArrayList<>();

        String[] line = lines.get(lineIndex);
        // слупкі - да першага пустога ці да канца, пачынаючы з другога слупка
        for (int i = 1; i < line.length; i++) {
            String c = line[i].trim();
            if (c.isEmpty()) {
                break;
            }
            cols.add(Huk.BAZAVY_HUK.valueOf(c));
        }

        // радкі - да першага пустога ці да канца
        for (int i = lineIndex + 1; i < lines.size(); i++) {
            String c = lines.get(i)[0].trim();
            if (c.isEmpty()) {
                break;
            }
            rows.add(Huk.BAZAVY_HUK.valueOf(c));
        }

        cross = new CaseCross();
        for (int col = 0; col < cols.size(); col++) {
            for (int row = 0; row < rows.size(); row++) {
                Huk.BAZAVY_HUK h1 = rows.get(row);
                Huk.BAZAVY_HUK h2 = cols.get(col);
                String text = lines.get(lineIndex + 1 + row)[1 + col];
                CaseCross.TypZmiahcennia zm = new CaseCross.TypZmiahcennia();
                text = text.trim().replaceAll("\\?$", "");
                if (text.equals(h1.name() + h2.name())) {
                    zm.zmiahcajecca = true;
                } else if (text.startsWith(h1.name() + h2.name())) {
                    zm.zmiahcajecca = true;
                    switch (text.substring(2)) {
                    case "*":
                        zm.pierakrocvajeMiezyUsizredzinieSlova = true;
                        break;
                    case "+":
                        zm.pierakrocvajeMiezySlou = true;
                        break;
                    case "+*":
                    case "*+":
                        zm.pierakrocvajeMiezyUsizredzinieSlova = true;
                        zm.pierakrocvajeMiezySlou = true;
                        break;
                    }
                } else if (text.isBlank()) {
                    // TODO error
                    zm.nievyznacana = true;
                } else {
                    for (String w : text.split(",")) {
                        w = w.trim();
                        if (w.isEmpty()) {
                        } else if (w.matches("\\-[0-9]+")) {
                        } else if (w.equals("0")) {
                            // TODO не мусіць быць такіх спалучэнняў
                        } else if (w.equals("-")) {
                            // змягчэння не адбываецца
                        } else {
                            err(col + 1, row + 1, "няправільная пазнака: " + text);
                        }
                    }
                }
                cross.values.computeIfAbsent(h1, h -> new HashMap<>()).put(h2, zm);
            }
        }

        lineIndex += rows.size();
    }

    private void parseExamples() {
        for (; lineIndex < lines.size(); lineIndex++) {
            List<String> cells = new ArrayList<>();
            Case.Example ex = new Case.Example();
            ex.caseName = "";
            int col = 0;
            for (String c : lines.get(lineIndex)) {
                c = c.trim();
                if (!c.isEmpty()) {
                    if (cells.isEmpty()) {
                        ex.cell = new Cell(lineIndex, col);
                    }
                    cells.add(c);
                }
                col++;
            }
            if (cells.size() >= 2) {
                ex.word = cells.get(0);
                ex.expected = cells.get(1);
                examples.add(ex);
            }
        }
    }

    private static String[] split(String s) {
        int count = 1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\t') {
                count++;
            }
        }
        String[] r = new String[count];
        int p = 0;
        int num = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '\t') {
                r[num] = s.substring(p, i);
                num++;
                p = i + 1;
            }
        }
        r[num] = p < s.length() ? s.substring(p) : "";
        return r;
    }

    private void err(int column, String message) {
        err(column, 0, message);
    }

    // column and rows starts from 1
    private void err(int column, int lineOffset, String message) {
        throw new RuntimeException("Памылка ў канфігурацыі '%s#%s': %s".formatted(configName, new Cell(lineIndex + lineOffset, column), message));
    }
}
