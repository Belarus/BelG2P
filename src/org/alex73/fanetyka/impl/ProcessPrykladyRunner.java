package org.alex73.fanetyka.impl;

import java.io.ByteArrayInputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.alex73.fanetyka.config.IConfig;
import org.alex73.fanetyka.config.TsvPrykladyConfig;

public class ProcessPrykladyRunner implements IProcess {
    private final String NAME = "Pryklady";

    public final TsvPrykladyConfig config;

    public ProcessPrykladyRunner(Map<String, byte[]> configs) throws Exception {
        config = new TsvPrykladyConfig(NAME, new ByteArrayInputStream(configs.get(NAME)));
    }

    @Override
    public String getProcessTypeName() {
        return NAME;
    }

    @Override
    public IConfig getConfig() {
        return config;
    }

    @Override
    public Collection<String> getDebugCases() {
        return List.of();
    }
}
