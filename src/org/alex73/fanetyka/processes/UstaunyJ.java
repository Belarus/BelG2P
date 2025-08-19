package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class UstaunyJ {
//    @ProcessCase(name = "Злучнік 'і' пераходзіць у 'ы'", logCountBefore = 3, logCountAfter = 3)
//    public boolean zi1(Huk h1, Huk h2) {
//        h2.bazavyHuk = Huk.BAZAVY_HUK.ы;
//        h2.miakki = 0;
//        return true;
//    }

    @ProcessCase(name = "Злучнік 'і' пераходзіць у 'й'", logCountBefore = 3, logCountAfter = 3)
    public boolean zi2(Huk h1, Huk h2) {
        h2.bazavyHuk = Huk.BAZAVY_HUK.j;
        h2.halosnaja = false;
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' напачатку слова пад націскам", logCountBefore = 1, logCountAfter = 2)
    public boolean pin(ProcessContext context, Huk huk) {
        if (huk.fromDB != null) {
            if ("^і".equals(huk.fromDB.v.getZmienyFanietyki())) {
                // выключэнні(тут застаецца “і”): іба (нескл.), ібіс (заал.), івалга, ігрэк,
                // ікаць, ілька (заал.), Ільмень, інкі (гіст.), іпуцкі, Істрыя, іфні (нескл.),
                // ішыяс
                return false;
            }
        }
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'й' напачатку слова не пад націскам", logCountBefore = 2, logCountAfter = 2)
    public boolean pinn(Huk h1, Huk h2) {
        h2.bazavyHuk = Huk.BAZAVY_HUK.j;
        h2.halosnaja = false;
        return true;
    }

//    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля 'па-'", logCountBefore = 3, logCountAfter = 4)
//    public boolean pa(ProcessContext context) {
//        ustavicJ(context, 2);
//        return true;
//    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля апострафу", logCountBefore = 1, logCountAfter = 2)
    public boolean pia(ProcessContext context) {
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'ы' пасля мяжы і цвёрдага зычнага", logCountBefore = 2, logCountAfter = 2)
    public boolean mcz(Huk h1, Huk h2) {
        h2.bazavyHuk = Huk.BAZAVY_HUK.ы;
        h2.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'ы' пасля апострафа і мяжы", logCountBefore = 2, logCountAfter = 2)
    public boolean mcza(Huk h1, Huk h2) {
        h2.bazavyHuk = Huk.BAZAVY_HUK.ы;
        h2.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля галоснай і 'ў' у сярэдзіне слова", logCountBefore = 1, logCountAfter = 2)
    public boolean pss(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j на пачатку слова ці пасля падзелу", logCountBefore = 1, logCountAfter = 2)
    public boolean jpac(ProcessContext context) {
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Дадаецца j пасля апострафу", logCountBefore = 1, logCountAfter = 2)
    public boolean japo(ProcessContext context) {
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Дадаецца j пасля галоснай ці 'ў т д р ж ш ч'", logCountBefore = 1, logCountAfter = 2)
    public boolean jhal(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j пасля прыстаўкі на галосную, 'ў' ці 'j'", logCountBefore = 1, logCountAfter = 2)
    public boolean pryhauj(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j пасля прыстаўкі, якая канчаецца на 'й'", logCountBefore = 1, logCountAfter = 2)
    public boolean jpry(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j пасля мяккага зычнага перад 'і'", logCountBefore = 1, logCountAfter = 2)
    public boolean jmiaczy(ProcessContext context, Huk h1) {
        if (h1.bazavyHuk == Huk.BAZAVY_HUK.j) {
            return false; // не пасля 'й'
        }
        ustavicJ(context, 1);
        return true;
    }
    @ProcessCase(name = "Дадаецца j пасля мяккага", logCountBefore = 1, logCountAfter = 2)
    public boolean jmiac(ProcessContext context, Huk h1) {
        if (h1.bazavyHuk == Huk.BAZAVY_HUK.j) {
            return false; // не пасля 'й'
        }
        ustavicJ(context, 1);
        return true;
    }

    private void ustavicJ(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        Huk jot = new Huk("", BAZAVY_HUK.j);
        jot.setMiakkasc(true);
        jot.miakki = Huk.MIAKKASC_PAZNACANAJA;
        context.huki.add(pos, jot);
    }
}
