package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

public class Prypadabniennie {
    @ProcessCase("Прыпадабненне дч -> тч, дц -> тц")
    public String pry(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.т;
        return "";
    }

    @ProcessCase("Падваенне ґ+г => г:")
    public String gh(Huk h1, Huk h2, ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        h2.padvojeny = true;
        return "";
    }

    @ProcessCase("Прыпадабненне т+ц => ц:, т+ч => ч:")
    public String tc(Huk h1, Huk h2, ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        h2.padvojeny = true;
        return "";
    }

    @ProcessCase("Прыпадабненне д+дж => дж:, д+дз => дз:")
    public String ddz(Huk h1, Huk h2, ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        h2.padvojeny = true;
        return "";
    }
/*
    @ProcessCase("Падваенне аднолькавых зычных")
    public String eq(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        if (h2.bazavyHuk != h3.bazavyHuk) {
            return null;
        }
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        h3.padvojeny = true;
        return "";
    }*/
}
