package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class UstaunyA {
    @ProcessCase("Устаўны 'а' перад цвёрдым 'р' пасля зычных акрамя р,j,ў")
    public String r(ProcessContext context) {
        ustavicA(context, 1);
        return "а";
    }

    @ProcessCase("Устаўны 'а' перад мяккім 'л'")
    public String l(ProcessContext context) {
        ustavicA(context, 1);
        return "а";
    }

    private void ustavicA(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        Huk a = new Huk("", BAZAVY_HUK.а);
        a.halosnaja = true;
        context.huki.add(pos, a);
    }
}
