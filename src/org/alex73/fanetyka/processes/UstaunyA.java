package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class UstaunyA {
    @ProcessCase(name = "Устаўны 'а' перад цвёрдым 'р' пасля зычных акрамя р,j,ў", logCountBefore = 3, logCountAfter = 4)
    public boolean r(ProcessContext context) {
        context.dadac(1, new Huk("", BAZAVY_HUK.а));
        return true;
    }

    @ProcessCase(name = "Устаўны 'а' перад мяккім 'л'", logCountBefore = 3, logCountAfter = 4)
    public boolean l(ProcessContext context) {
        context.dadac(1, new Huk("", BAZAVY_HUK.а));
        return true;
    }
}
