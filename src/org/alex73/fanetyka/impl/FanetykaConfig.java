package org.alex73.fanetyka.impl;

import java.util.Map;

import org.alex73.fanetyka.processes.AhlusennieAzvancennie;
import org.alex73.fanetyka.processes.PierachodI;
import org.alex73.fanetyka.processes.PierachodZG;
import org.alex73.fanetyka.processes.Prypadabniennie;
import org.alex73.fanetyka.processes.Sprascennie;
import org.alex73.fanetyka.processes.SypiacyjaSvisciacyja;
import org.alex73.grammardb.GrammarFinder;

public class FanetykaConfig {
    protected final GrammarFinder finder;

    protected final ProcessRunner processPierachodI;
    protected ProcessRunner processPaznacajemMiakkasc;
    protected final ProcessRunner processAhlusennieAzvancennie;
    protected final ProcessRunner processSprascennie;
    protected final ProcessRunner processPrypadabniennie;
    protected final ProcessRunner processSypiacyjaSvisciacyja;
    protected ProcessRunner processPierachodTS;
    protected ProcessRunner processPierachodV;
    protected ProcessRunner processPierachodM;
    protected ProcessRunner processPadvajennie;
    protected ProcessRunner processUstaunojeA;
    protected ProcessRunner processPierachodFH;
    protected ProcessRunner processPierachodZG;

    public FanetykaConfig(GrammarFinder finder, Map<String, byte[]> configs) throws Exception {
        this.finder = finder;
        this.processPierachodI = new ProcessRunner(PierachodI.class, configs);
        // this.processPaznacajemMiakkasc = new ProcessRunner(PaznacajemMiakkasc.class,
        // configs);
        this.processAhlusennieAzvancennie = new ProcessRunner(AhlusennieAzvancennie.class, configs);
        this.processSprascennie = new ProcessRunner(Sprascennie.class, configs);
        this.processPrypadabniennie = new ProcessRunner(Prypadabniennie.class, configs);
        this.processSypiacyjaSvisciacyja = new ProcessRunner(SypiacyjaSvisciacyja.class, configs);
        // this.processPierachodTS = new ProcessRunner(PierachodTS.class, configs);
        // this.processPierachodV = new ProcessRunner(PierachodV.class, configs);
        // this.processPierachodM = new ProcessRunner(PierachodM.class, configs);
        // this.processPadvajennie = new ProcessRunner(Padvajennie.class, configs);
        // this.processUstaunojeA = new ProcessRunner(UstaunojeA.class, configs);
        // this.processPierachodFH = new ProcessRunner(PierachodFH.class, configs);
        this.processPierachodZG = new ProcessRunner(PierachodZG.class, configs);
    }
}
