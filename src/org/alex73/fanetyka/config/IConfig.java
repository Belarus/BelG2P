package org.alex73.fanetyka.config;

import java.util.List;

public interface IConfig {
    List<Case.Example> getExamples();

    boolean reportRuleExecutionFail();
}
