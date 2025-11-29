package org.alex73.fanetyka.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

import org.alex73.fanetyka.config.TsvConfig;
import org.alex73.fanetyka.processes.AhlusennieAzvancennie;
import org.alex73.fanetyka.processes.Miakkasc;
import org.alex73.fanetyka.processes.Pazicyjnyja;
import org.alex73.fanetyka.processes.Prypadabniennie;
import org.alex73.fanetyka.processes.Sprascennie;
import org.alex73.fanetyka.processes.SypiacyjaSvisciacyja;
import org.alex73.fanetyka.processes.Redukavanyja;
import org.alex73.fanetyka.processes.UstaunyJ;
import org.alex73.grammardb.GrammarFinder;

/**
 * Configuration class for Fanetyka processes. This class initializes and
 * manages various phonetic processes and their configurations.
 */
public class FanetykaConfig {
    protected final GrammarFinder finder; // GrammarFinder instance for managing grammar-related operations.

    protected final ProcessPrykladyRunner processPryklady;
    protected final ProcessCrossRunner processMiakkasc;
    protected final ProcessRunner processAhlusennieAzvancennie;
    protected final ProcessRunner processSprascennie;
    protected final ProcessRunner processPrypadabniennie;
    protected final ProcessRunner processSypiacyjaSvisciacyja;
    protected final ProcessRunner processPazicyjnyja;
    protected final ProcessRunner processRedukavanyja;
    protected final ProcessRunner processUstaunyJ;

    static final List<String> CONFIG_NAMES = List.of("Miakkasc", "AhlusennieAzvancennie", "Pazicyjnyja", "Pryklady", "Sprascennie", "Prypadabniennie",
            "SypiacyjaSvisciacyja", "Redukavanyja", "UstaunyJ");

    private final List<String> debugCases = new ArrayList<String>();

    public FanetykaConfig(GrammarFinder finder) throws Exception {
        this(finder, () -> loadBuiltinConfigs());
    }

    public static Map<String, byte[]> loadBuiltinConfigs() {
        Map<String, byte[]> configs = new TreeMap<>();
        for (String t : CONFIG_NAMES) {
            try (InputStream in = TsvConfig.class.getResourceAsStream("/" + t + ".tsv")) {
                configs.put(t, in.readAllBytes());
            } catch (IOException ex) {
                throw new RuntimeException("Can't read /" + t + ".tsv from jar");
            }
        }
        return configs;
    }

    public FanetykaConfig(GrammarFinder finder, Map<String, byte[]> configs) throws Exception {
        this(finder, () -> configs);
    }

    private FanetykaConfig(GrammarFinder finder, Supplier<Map<String, byte[]>> getConfigs) throws Exception {
        this.finder = finder;
        Map<String, byte[]> configs = getConfigs.get();
        this.processMiakkasc = new ProcessCrossRunner(Miakkasc.class, configs);
        this.processPryklady = new ProcessPrykladyRunner(configs);
        this.processAhlusennieAzvancennie = new ProcessRunner(AhlusennieAzvancennie.class, configs);
        this.processSprascennie = new ProcessRunner(Sprascennie.class, configs);
        this.processPrypadabniennie = new ProcessRunner(Prypadabniennie.class, configs);
        this.processSypiacyjaSvisciacyja = new ProcessRunner(SypiacyjaSvisciacyja.class, configs);
        this.processPazicyjnyja = new ProcessRunner(Pazicyjnyja.class, configs);
        this.processRedukavanyja = new ProcessRunner(Redukavanyja.class, configs);
        this.processUstaunyJ = new ProcessRunner(UstaunyJ.class, configs);

        debugCases.addAll(this.processMiakkasc.getDebugCases());
        debugCases.addAll(this.processPryklady.getDebugCases());
        debugCases.addAll(this.processAhlusennieAzvancennie.getDebugCases());
        debugCases.addAll(this.processSprascennie.getDebugCases());
        debugCases.addAll(this.processPrypadabniennie.getDebugCases());
        debugCases.addAll(this.processSypiacyjaSvisciacyja.getDebugCases());
        debugCases.addAll(this.processPazicyjnyja.getDebugCases());
        debugCases.addAll(this.processRedukavanyja.getDebugCases());
        debugCases.addAll(this.processUstaunyJ.getDebugCases());

        Collections.sort(debugCases, Collator.getInstance(Locale.of("be")));
    }

    public List<String> getDebugCases() {
        return debugCases;
    }
}
