package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class UstaunyJ {

    @ProcessCase(name = "Злучнік 'і' пераходзіць у 'й'", logCountBefore = 3, logCountAfter = 3)
    public boolean zi2(Huk h1, Huk h2) {
        h2.bazavyHuk = Huk.BAZAVY_HUK.j;
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' напачатку слова пад націскам", logCountBefore = 1, logCountAfter = 2)
    public boolean pin(ProcessContext context, Huk huk) {
        if (huk.wordContext.asnounaja != null) {
            if ("^і".equals(huk.wordContext.asnounaja.v.getZmienyFanietyki())) {
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
        return true;
    }
    // TODO remove
//    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля апострафу", logCountBefore = 1, logCountAfter = 2)
//    public boolean pia(ProcessContext context) {
//        ustavicJ(context, 0);
//        return true;
//    }

    @ProcessCase(name = "Пераход 'і' ў 'ы' пасля цвёрдага зычнага і мяжы", logCountBefore = 2, logCountAfter = 2)
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

    @ProcessCase(name = "Дадаецца j перад галоснай на пачатку слова ці пасля падзелу", logCountBefore = 1, logCountAfter = 2)
    public boolean jpac(ProcessContext context) {
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Дадаецца j перад галоснай пасля апострафу", logCountBefore = 1, logCountAfter = 2)
    public boolean japo(ProcessContext context) {
        ustavicJ(context, 0);
        return true;
    }

    @ProcessCase(name = "Дадаецца j перад галоснай пасля галоснай ці 'ў т д р ж ш ч'", logCountBefore = 1, logCountAfter = 2)
    public boolean jhal(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля прыстаўкі на галосную, 'ў' ці 'j'", logCountBefore = 1, logCountAfter = 2)
    public boolean pryhauj(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j перад галоснай пасля прыстаўкі, якая канчаецца на 'й'", logCountBefore = 1, logCountAfter = 2)
    public boolean jpry(ProcessContext context) {
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Пераход 'і' ў 'йі' пасля мяккага зычнага", logCountBefore = 1, logCountAfter = 2)
    public boolean jmiaczy(ProcessContext context, Huk h1) {
        if (h1.bazavyHuk == Huk.BAZAVY_HUK.j) {
            return false; // не пасля 'й'
        }
        ustavicJ(context, 1);
        return true;
    }

    @ProcessCase(name = "Дадаецца j перад галоснай пасля мяккага", logCountBefore = 1, logCountAfter = 2)
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
        jot.miakki = Huk.MIAKKASC_PAZNACANAJA;
        context.huki.add(pos, jot);
    }
}
