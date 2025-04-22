package org.alex73.fanetyka.impl;

import java.util.List;

public class ProcessContext {
    public List<Huk> huki;
    public int currentPosition;
    public String debugPrefix; // if !=null - show debug
    public List<String> debug;

    @Override
    public String toString() {
        return huki.subList(0, currentPosition) + " + " + huki.subList(currentPosition, huki.size());
    }
}
