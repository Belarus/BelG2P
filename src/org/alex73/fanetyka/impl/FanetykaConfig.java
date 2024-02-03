package org.alex73.fanetyka.impl;

import java.util.Map;

import org.alex73.fanetyka.processes.AhlusennieAzvancennie;
import org.alex73.fanetyka.processes.BilabijalnyV;
import org.alex73.fanetyka.processes.HubnaZubnyM;
import org.alex73.fanetyka.processes.Miakkasc;
import org.alex73.fanetyka.processes.PierachodFH;
import org.alex73.fanetyka.processes.PierachodI;
import org.alex73.fanetyka.processes.PierachodZG;
import org.alex73.fanetyka.processes.Prypadabniennie;
import org.alex73.fanetyka.processes.Sprascennie;
import org.alex73.fanetyka.processes.SypiacyjaSvisciacyja;
import org.alex73.fanetyka.processes.UstaunyA;
import org.alex73.grammardb.GrammarFinder;

public class FanetykaConfig {
    protected final GrammarFinder finder;

    protected final ProcessRunner processPierachodI;
    protected final ProcessCrossRunner processMiakkasc;
    protected final ProcessRunner processAhlusennieAzvancennie;
    protected final ProcessRunner processSprascennie;
    protected final ProcessRunner processPrypadabniennie;
    protected final ProcessRunner processSypiacyjaSvisciacyja;
    protected ProcessRunner processBilabijalnyV;
    protected ProcessRunner processHubnaZubnyM;
    protected ProcessRunner processUstaunyA;
    protected ProcessRunner processPierachodFH;
    protected ProcessRunner processPierachodZG;

    public FanetykaConfig(GrammarFinder finder, Map<String, byte[]> configs) throws Exception {
        this.finder = finder;
        this.processPierachodI = new ProcessRunner(PierachodI.class, configs);
        this.processMiakkasc = new ProcessCrossRunner(Miakkasc.class, configs);
        this.processAhlusennieAzvancennie = new ProcessRunner(AhlusennieAzvancennie.class, configs);
        this.processSprascennie = new ProcessRunner(Sprascennie.class, configs);
        this.processPrypadabniennie = new ProcessRunner(Prypadabniennie.class, configs);
        this.processSypiacyjaSvisciacyja = new ProcessRunner(SypiacyjaSvisciacyja.class, configs);
        this.processBilabijalnyV = new ProcessRunner(BilabijalnyV.class, configs);
        this.processHubnaZubnyM = new ProcessRunner(HubnaZubnyM.class, configs);
        this.processUstaunyA = new ProcessRunner(UstaunyA.class, configs);
        this.processPierachodFH = new ProcessRunner(PierachodFH.class, configs);
        this.processPierachodZG = new ProcessRunner(PierachodZG.class, configs);
    }
}
