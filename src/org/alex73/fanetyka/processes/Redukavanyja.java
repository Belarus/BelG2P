package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Redukavanyja {
    @ProcessCase(name = "Устаўны 'а' перад цвёрдым 'р' пасля зычных акрамя р,j,ў", logCountBefore = 3, logCountAfter = 4)
    public boolean r(ProcessContext context) {
        Huk h = new Huk("", hasStressAfter(context) ? BAZAVY_HUK.ы : BAZAVY_HUK.а);
        h.redukavany = true;
        context.dadac(1, h);
        return true;
    }

    @ProcessCase(name = "Устаўны 'а' перад мяккім 'л'", logCountBefore = 3, logCountAfter = 4)
    public boolean l(ProcessContext context) {
        Huk h = new Huk("", hasStressAfter(context) ? BAZAVY_HUK.ы : BAZAVY_HUK.а);
        h.redukavany = true;
        context.dadac(1, h);
        return true;
    }

    /**
     * Ці ёсць націск да канца слова?
     * TODO калі няма націску - вызначыць дзе складоў больш
     */
    boolean hasStressAfter(ProcessContext context) {
        for (int i = context.currentPosition; i < context.huki.size(); i++) {
            Huk h = context.huki.get(i);
            if (h.stress) {
                return true;
            }
            if (h.padzielPasla != 0) {
                return false;
            }
        }
        return false;
    }
}
