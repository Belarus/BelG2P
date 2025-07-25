package org.alex73.fanetyka.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.alex73.fanetyka.config.Case.Example;

public class TsvPrykladyConfig implements IConfig {
    private final String configName;
    private final List<String[]> lines = new ArrayList<>();
    private int lineIndex;
    public CaseCross cross;
    public List<Example> examples = new ArrayList<>();

    public TsvPrykladyConfig(String name, InputStream in) throws Exception {
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
            } else if ("Прыклады".equals(line[0])) {
                lineIndex++;
                parseExamples();
            } else {
                err(0, "Невядомы загаловак: " + line[0]);
            }
        }
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
            switch (cells.size()) {
            default:
                ex.expected = cells.get(1);
            case 1:
                ex.word = cells.get(0);
                examples.add(ex);
                break;
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