package org.alex73.fanetyka.impl;

import java.util.List;
import java.util.function.Function;

public interface IFanetyka {
    void calcFanetyka(List<String> words) throws Exception;

    String toString(Function<Huk, String> map);
}
