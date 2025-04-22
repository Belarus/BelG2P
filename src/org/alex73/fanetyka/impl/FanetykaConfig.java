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
import org.alex73.fanetyka.processes.BilabijalnyV;
import org.alex73.fanetyka.processes.HubnaZubnyM;
import org.alex73.fanetyka.processes.Miakkasc;
import org.alex73.fanetyka.processes.PierachodFH;
import org.alex73.fanetyka.processes.PierachodI;
import org.alex73.fanetyka.processes.Prypadabniennie;
import org.alex73.fanetyka.processes.Sprascennie;
import org.alex73.fanetyka.processes.SypiacyjaSvisciacyja;
import org.alex73.fanetyka.processes.UstaunyA;
import org.alex73.grammardb.GrammarFinder;

public class FanetykaConfig {
    protected final GrammarFinder finder;

    protected final ProcessPrykladyRunner processPryklady;
    protected final ProcessCrossRunner processMiakkasc;
    protected final ProcessRunner processPierachodI;
    protected final ProcessRunner processAhlusennieAzvancennie;
    protected final ProcessRunner processSprascennie;
    protected final ProcessRunner processPrypadabniennie;
    protected final ProcessRunner processSypiacyjaSvisciacyja;
    protected final ProcessRunner processBilabijalnyV;
    protected final ProcessRunner processHubnaZubnyM;
    protected final ProcessRunner processUstaunyA;
    protected final ProcessRunner processPierachodFH;

    private final List<String> debugCases = new ArrayList<String>();

    public FanetykaConfig(GrammarFinder finder) throws Exception {
        this(finder, () -> {
            Map<String, byte[]> configs = new TreeMap<>();
            for (String t : List.of("Miakkasc", "AhlusennieAzvancennie", "BilabijalnyV", "HubnaZubnyM", "Prypadabniennie", "PierachodFH", "PierachodI",
                    "Pryklady", "Sprascennie", "SypiacyjaSvisciacyja", "UstaunyA")) {
                try (InputStream in = TsvConfig.class.getResourceAsStream("/" + t + ".tsv")) {
                    configs.put(t, in.readAllBytes());
                } catch (IOException ex) {
                    throw new RuntimeException("Can't read /" + t + ".tsv from jar");
                }
            }
            return configs;
        });
    }

    public FanetykaConfig(GrammarFinder finder, Map<String, byte[]> configs) throws Exception {
        this(finder, () -> configs);
    }

    private FanetykaConfig(GrammarFinder finder, Supplier<Map<String, byte[]>> getConfigs) throws Exception {
        this.finder = finder;
        Map<String, byte[]> configs = getConfigs.get();
        this.processPierachodI = new ProcessRunner(PierachodI.class, configs);
        this.processMiakkasc = new ProcessCrossRunner(Miakkasc.class, configs);
        this.processPryklady = new ProcessPrykladyRunner(configs);
        this.processAhlusennieAzvancennie = new ProcessRunner(AhlusennieAzvancennie.class, configs);
        this.processSprascennie = new ProcessRunner(Sprascennie.class, configs);
        this.processPrypadabniennie = new ProcessRunner(Prypadabniennie.class, configs);
        this.processSypiacyjaSvisciacyja = new ProcessRunner(SypiacyjaSvisciacyja.class, configs);
        this.processBilabijalnyV = new ProcessRunner(BilabijalnyV.class, configs);
        this.processHubnaZubnyM = new ProcessRunner(HubnaZubnyM.class, configs);
        this.processUstaunyA = new ProcessRunner(UstaunyA.class, configs);
        this.processPierachodFH = new ProcessRunner(PierachodFH.class, configs);

        debugCases.addAll(this.processPierachodI.getDebugCases());
        debugCases.addAll(this.processMiakkasc.getDebugCases());
        debugCases.addAll(this.processPryklady.getDebugCases());
        debugCases.addAll(this.processAhlusennieAzvancennie.getDebugCases());
        debugCases.addAll(this.processSprascennie.getDebugCases());
        debugCases.addAll(this.processPrypadabniennie.getDebugCases());
        debugCases.addAll(this.processSypiacyjaSvisciacyja.getDebugCases());
        debugCases.addAll(this.processBilabijalnyV.getDebugCases());
        debugCases.addAll(this.processHubnaZubnyM.getDebugCases());
        debugCases.addAll(this.processUstaunyA.getDebugCases());
        debugCases.addAll(this.processPierachodFH.getDebugCases());

        Collections.sort(debugCases, Collator.getInstance(Locale.of("be")));
    }

    public List<String> getDebugCases() {
        return debugCases;
    }
}
