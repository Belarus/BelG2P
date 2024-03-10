package org.alex73.fanetyka.impl;

import org.alex73.fanetyka.config.IConfig;

public interface IProcess {
    public IConfig getConfig();

    public String getProcessTypeName();
}
