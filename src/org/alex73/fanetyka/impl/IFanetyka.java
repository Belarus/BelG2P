package org.alex73.fanetyka.impl;

import java.util.function.Function;

public interface IFanetyka {
    void addWord(String w);

    void calcFanetyka() throws Exception;

    String toString(Function<Huk, String> map);
}
