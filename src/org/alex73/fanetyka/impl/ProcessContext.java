package org.alex73.fanetyka.impl;

import java.util.List;

public class ProcessContext {
    public List<Huk> huki;
    public int currentPosition;
    public String debugPrefix; // if !=null - show debug
    public List<String> debug;

    public String dump(int logCount) {
        StringBuilder o = new StringBuilder();
        for (int i = currentPosition; i < huki.size() && logCount > 0; i++, logCount--) {
            o.append(huki.get(i).toString());
        }
        return o.toString();
    }

    @Override
    public String toString() {
        return huki.subList(0, currentPosition) + " + " + huki.subList(currentPosition, huki.size());
    }

    public void vydalicPierad(int offsetFromCurrent) {
        int pos = currentPosition + offsetFromCurrent;
        huki.get(pos).zychodnyjaLitary = huki.get(pos - 1).zychodnyjaLitary + huki.get(pos).zychodnyjaLitary;
        huki.get(pos).debug = huki.get(pos - 1).debug || huki.get(pos).debug;
        huki.remove(pos - 1);
    }

    public void vydalicPasla(int offsetFromCurrent) {
        int pos = currentPosition + offsetFromCurrent;
        huki.get(pos).zychodnyjaLitary = huki.get(pos).zychodnyjaLitary + huki.get(pos + 1).zychodnyjaLitary;
        huki.get(pos).debug = huki.get(pos + 1).debug || huki.get(pos).debug;
        huki.get(pos).padzielPasla = huki.get(pos + 1).padzielPasla | huki.get(pos).padzielPasla;
        huki.remove(pos + 1);
    }

    public void dadac(int offsetFromCurrent, Huk huk) {
        int pos = currentPosition + offsetFromCurrent;
        huki.add(pos, huk);
    }
}
