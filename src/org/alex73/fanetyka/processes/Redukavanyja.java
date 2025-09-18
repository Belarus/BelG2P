package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Redukavanyja {
    @ProcessCase(name = "Устаўны 'а' перад цвёрдым 'р' пасля зычных акрамя р,j,ў", logCountBefore = 3, logCountAfter = 4)
    public boolean r(ProcessContext context) {
        PLACE_TYPE place = inRoot(context);
        if (place == PLACE_TYPE.NONE) {
            return false;
        }
        Huk h = new Huk("", place == PLACE_TYPE.ROOT ? BAZAVY_HUK.ы : BAZAVY_HUK.а);
        h.redukavany = true;
        context.dadac(1, h);
        return true;
    }

    @ProcessCase(name = "Устаўны 'а' перад мяккім 'л'", logCountBefore = 3, logCountAfter = 4)
    public boolean l(ProcessContext context) {
        PLACE_TYPE place = inRoot(context);
        if (place == PLACE_TYPE.NONE) {
            return false;
        }
        Huk h = new Huk("", place == PLACE_TYPE.ROOT ? BAZAVY_HUK.ы : BAZAVY_HUK.а);
        h.redukavany = true;
        context.dadac(1, h);
        return true;
    }

    enum PLACE_TYPE {
        NONE, ROOT, SUFFIX
    };

    /**
     * Вызначаем - гук у суфіксе ці ў корні па колькасці складаў і націску да і
     * пасля знойдзенага гуку.
     */
    PLACE_TYPE inRoot(ProcessContext context) {
        int stressesBefore = 0;
        int stressesAfter = 0;
        int syllBefore = 0;
        int syllAfter = 0;
        for (int i = context.currentPosition - 1; i >= 0; i--) {
            Huk h = context.huki.get(i);
            if (h.padzielPasla != 0) {
                break;
            }
            if (Huk.halosnyja.contains(h.bazavyHuk)) {
                syllBefore++;
            }
            if (h.stress) {
                stressesBefore++;
            }
        }
        for (int i = context.currentPosition; i < context.huki.size(); i++) {
            Huk h = context.huki.get(i);
            if (Huk.halosnyja.contains(h.bazavyHuk)) {
                syllAfter++;
            }
            if (h.stress) {
                stressesAfter++;
            }
            if (h.padzielPasla != 0) {
                break;
            }
        }
        if (syllBefore == 0 && syllAfter == 0) {
            // няма галосных у слове - нічога не дадаем
            return PLACE_TYPE.NONE;
        } else if (syllAfter == 0) {
            return PLACE_TYPE.SUFFIX;
        } else if (stressesAfter > 0) {
            return PLACE_TYPE.ROOT;
        } else if (syllAfter == 1 && syllBefore > 1) {
            return PLACE_TYPE.SUFFIX;
        } else if (syllAfter == 1 && stressesBefore > 0) {
            return PLACE_TYPE.SUFFIX;
        } else {
            return PLACE_TYPE.ROOT;
        }
    }
}
