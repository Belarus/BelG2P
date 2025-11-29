package org.alex73.fanetyka.impl;

import java.util.Collection;

import org.alex73.fanetyka.config.IConfig;

public interface IProcess {
    public IConfig getConfig();

    public String getProcessTypeName();

    public Collection<String> getDebugCases();
}
