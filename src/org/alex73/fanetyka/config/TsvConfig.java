package org.alex73.fanetyka.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alex73.fanetyka.config.Case.HukCheck;
import org.alex73.fanetyka.impl.Huk;

public class TsvConfig {
    private final String configName;
    private final List<String[]> lines = new ArrayList<>();
    private int lineIndex;
    public final Map<String, Case> cases = new TreeMap<>();
    public final List<String> casesOrder = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        new TsvConfig("AhlusennieAzvancennie");

    }

    public TsvConfig(String name) throws Exception {
        this.configName = name;
        try (InputStream in = TsvConfig.class.getResourceAsStream("/" + name + ".tsv")) {
            load(in);
        }
    }

    public TsvConfig(String name, InputStream in) throws Exception {
        this.configName = name;
        load(in);
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
            } else if ("З'ява:".equals(line[0])) {
                readTask();
            } else {
                err(0, "Невядомы загаловак: " + line[0]);
            }
        }
    }

    private static final String[] TASK_HEADERS = new String[] { "Умовы:", "якія гукі", "апостраф", "падзел - прыстаўка і корань", "падзел - паміж каранямі",
            "падзел - кораня і суфікса", "падзел - мяжа слова", "падзел - праз злучок", "мяккасць - асіміляцыйная", "мяккасць - пазначаная", "падвоены",
            "націск" };

    private void readTask() throws Exception {
        Case t = new Case();

        // правяраем першы радок - назву з'явы
        String[] line = lines.get(lineIndex);
        if (!"З'ява:".equals(line[0].trim())) {
            err(0, "мусіць быць \"З'ява:\"");
        }
        if (line[1].isBlank()) {
            err(1, "мусіць быць назва з'явы");
        }
        t.name = line[1].trim();
        if (cases.put(t.name, t) != null) {
            err(1, "паўтараецца назва з'явы: " + t.name);
        }
        casesOrder.add(t.name);
        lineIndex++;
        // правяраем другі радок - паведамленне ў log
        line = lines.get(lineIndex);
        if (!"Log:".equals(line[0].trim())) {
            err(0, "мусіць быць \"Log:\"");
        }
        if (line[1].isBlank()) {
            err(1, "мусіць быць тэкст паведамлення");
        }
        t.logMessage = line[1].trim();
        lineIndex++;

        int columnAfter = parseTable(t);

        line = lines.get(lineIndex);
        for (int c = columnAfter; c < line.length; c++) {
            if (line[c].isBlank()) {
                continue;
            } else if ("Прыклады:".equals(line[c])) {
                parseExamples(t, c);
            } else {
                err(c, "Мусяць быць прыклады, але ў запісана нешта іншае: '%s'".formatted(line[c]));
            }
        }

        lineIndex += TASK_HEADERS.length;
    }

    private int parseTable(Case t) {
        // правяраем радкі табліцы
        for (int i = 0; i < TASK_HEADERS.length; i++) {
            if (!lines.get(lineIndex + i)[0].equals(TASK_HEADERS[i])) {
                err(0, i, "'%s', але чакаецца '%s'".formatted(lines.get(lineIndex + i)[0], TASK_HEADERS[i]));
            }
        }
        // ці ёсць пусты радок пасля табліцы ?
        if (lineIndex + TASK_HEADERS.length < lines.size()) {
            String[] lineAfter = lines.get(lineIndex + TASK_HEADERS.length);
            if (lineAfter.length > 0 && !lineAfter[0].isBlank()) {
                err(0, TASK_HEADERS.length + 1, "мусіць быць пусты радок");
            }
        }
        // чытаем ячэйкі
        int hukIndex = 1;
        Boolean byuHuk = null;
        HukCheck check = null;
        int c;
        for (c = 1; c < lines.get(lineIndex).length; c++) {
            String header = lines.get(lineIndex)[c];
            if (header.isBlank()) {
                // ці ўвесь слупок пусты ?
                for (int i = 0; i < TASK_HEADERS.length; i++) {
                    if (lines.get(lineIndex + i).length >= c && !lines.get(lineIndex + i)[c].isBlank()) {
                        err(c, i, "слупок пасля табліцы мусіць быць пусты");
                    }
                }
                c++;
                break;
            }
            if ("мяжа".equals(header)) {
                if (byuHuk == null) {
                    // мяжа перад першым гукам
                    t.borderCheckBefore = check = new HukCheck();
                } else if (!byuHuk.booleanValue()) {
                    // была мяжа
                    err(c, 0, "слупок мяжы пасля папярэдняга слупку мяжы");
                }
                check.apostraf = readMode(2, c);
                readMultiMode(3, c, check.pasziel, Huk.PADZIEL_PRYSTAUKA);
                readMultiMode(4, c, check.pasziel, Huk.PADZIEL_KARANI);
                readMultiMode(5, c, check.pasziel, Huk.PADZIEL_SUFIX);
                readMultiMode(6, c, check.pasziel, Huk.PADZIEL_SLOVY);
                readMultiMode(7, c, check.pasziel, Huk.PADZIEL_MINUS);
                if (check.pasziel.maskYes != 0 && check.pasziel.maskNo != 0) {
                    err(c, 3, "Няправільная камбінацыя пазнак падзелаў");
                }
                byuHuk = false;
            } else if (("гук " + hukIndex).equals(header) || ("гук " + hukIndex + "(неабавязковы)").equals(header)) {
                if (byuHuk != null && byuHuk.booleanValue()) {
                    // быў гук
                    err(c, 0, "слупок гуку пасля папярэдняга слупку гуку");
                }
                t.checks.add(check = new HukCheck());
                check.which = readHuki(1, c);
                readMultiMode(8, c, check.miakkasc, Huk.MIAKKASC_ASIMILACYJNAJA);
                readMultiMode(9, c, check.miakkasc, Huk.MIAKKASC_PAZNACANAJA);
                if (check.miakkasc.maskYes != 0 && check.miakkasc.maskNo != 0) {
                    err(c, 3, "Няправільная камбінацыя пазнак мяккасці");
                }
                check.padvojeny = readMode(10, c);
                check.nacisk = readMode(11, c);
                if (header.contains("неабавязковы")) {
                    check.optionalHuk = true;
                } else {
                    t.requiresHuks++;
                }
                byuHuk = true;
                hukIndex++;
            } else {
                err(c, "Няправільны загаловак - " + header);
            }
        }

        boolean wasOptional = false;
        for (HukCheck hc : t.checks) {
            if (hc.optionalHuk) {
                wasOptional = true;
            } else if (wasOptional) {
                err(0, "Неабавязковы гук пасля абавязковага");
            }
        }

        return c;
    }

    private void parseExamples(Case t, int column) {
        for (int i = lineIndex + 1; i < lines.size(); i++) {
            String[] line = lines.get(i);
            try {
                if (line[column].isBlank()) {
                    break;
                }
                if (line[column + 1].isBlank()) {
                    continue;
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
            }
            Case.Example ex = new Case.Example();
            ex.lineIndex = i;
            ex.column = (char) ('A' + column);
            ex.word = line[column].trim();
            if (line.length > column + 1) {
                ex.expected = line[column + 1].trim();
            }
            t.examples.add(ex);
        }
    }

    private String[] readHuki(int lineOffset, int column) {
        String value = lines.get(lineIndex + lineOffset)[column].trim();
        return value.trim().split("\\s+");
    }

    private Case.MODE readMode(int lineOffset, int column) {
        String value = lines.get(lineIndex + lineOffset)[column].trim();
        switch (value) {
        case "":
            return Case.MODE.DONT_CARE;
        case "так":
            return Case.MODE.YES;
        case "не":
            return Case.MODE.NO;
        case "-":
            return Case.MODE.ERROR;
        default:
            err(column, lineOffset, "направільная пазнака - " + value);
            return null;
        }
    }

    private void readMultiMode(int lineOffset, int column, Case.MultiMode multiMode, int mask) {
        String value = lines.get(lineIndex + lineOffset)[column].trim();
        switch (value) {
        case "":
            break;
        case "так":
            multiMode.maskYes |= mask;
            break;
        case "не":
            multiMode.maskNo |= mask;
            break;
        case "-":
            multiMode.maskError |= mask;
            break;
        default:
            err(column, lineOffset, "направільная пазнака - " + value);
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

    private void err(int column, int lineOffset, String message) {
        throw new RuntimeException("Памылка ў канфігурацыі '%s#%d%c': %s".formatted(configName, lineIndex + lineOffset, (char) ('A' + column), message));
    }
}
