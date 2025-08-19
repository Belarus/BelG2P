package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.ProcessContext;

public class Sprascennie {
    @ProcessCase(name = "Спрашчэнне: с-ц-к пераходзіць у с-к", logCountBefore = 3, logCountAfter = 2)
    public boolean sck(ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: с-т-ч пераходзіць у ш-ч", logCountBefore = 3, logCountAfter = 2)
    public boolean stc(ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: с-т-н -> с-н", logCountBefore = 3, logCountAfter = 2)
    public boolean stn(ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: з-д-н -> з-н", logCountBefore = 3, logCountAfter = 2)
    public boolean zdn(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        h1.miakki = h3.miakki;
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: р-к-с-к -> р-c-к", logCountBefore = 4, logCountAfter = 3)
    public boolean rksk(ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Прыпадабненне аднолькавых зычных", logCountBefore = 2, logCountAfter = 1)
    public boolean eq2(Huk h1, Huk h2, ProcessContext context) {
        if (h1.bazavyHuk != h2.bazavyHuk) {
            // неаднолькавыя
            return false;
        }
        if (h1.miakki != 0 && h2.miakki == 0) {
            // калі у першым ёсць мяккасць, а ў другім няма - не спрашчаецца
            return false;
        }
        vydalicPapiaredni(context, 1);
        h2.padvojeny = true;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: с/ш-с-к -> c-к не на стыку", logCountBefore = 3, logCountAfter = 2)
    public boolean sss(ProcessContext context) {
        vydalicPapiaredni(context, 1);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: х-с-к -> c-к не на стыку", logCountBefore = 4, logCountAfter = 3)
    public boolean sks(ProcessContext context, Huk h1) {
        if (h1.bazavyHuk == Huk.BAZAVY_HUK.j) {
            // калі перад х стаіць j - не спрашчаецца
            return false;
        }
        vydalicPapiaredni(context, 2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: с-т-с -> c:", logCountBefore = 3, logCountAfter = 1)
    public boolean sts(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        h1.padvojeny = true;
        h1.miakki = h3.miakki;
        vydalicNastupny(context, 0);
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: з-д-ч -> ш-ч", logCountBefore = 3, logCountAfter = 2)
    public boolean zdc(Huk h1, Huk h2, ProcessContext context) {
        h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
        h2.debug = h1.debug || h2.debug;
        h2.bazavyHuk = BAZAVY_HUK.ш;
        context.huki.remove(context.currentPosition);
        return true;
    }

    @ProcessCase(name = "Прыпадабненне: ш-с-> с-с", logCountBefore = 2, logCountAfter = 2)
    public boolean ss(Huk h1, Huk h2) {
        h1.bazavyHuk = BAZAVY_HUK.с;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне зычных: ц'-т -> т'-т", logCountBefore = 2, logCountAfter = 2)
    public boolean ct(Huk h1, Huk h2) {
        h1.bazavyHuk = BAZAVY_HUK.т;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: с'-т/ц' + (глухі ці санорны ў наступным слове) -> c'", logCountBefore = 3, logCountAfter = 2)
    public boolean cch(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        context.huki.get(context.currentPosition).padzielPasla = context.huki.get(context.currentPosition + 1).padzielPasla;
        vydalicNastupny(context, 0);
        return true;
//        if (h3.isSanorny()) {
//            // калі санорны - не спрашчаецца
//            return false;
//        }
//        if (h3.isSypiacy()) {
//            // шыпячы ў наступным слове
//            if (h3.isHluchi()) {
//                h1.bazavyHuk = Huk.BAZAVY_HUK.ш;
//                h1.miakki = 0;
//                vydalicNastupny(context, 0);
//            } else if (h3.isZvonki()) {
//                h1.bazavyHuk = Huk.BAZAVY_HUK.ж;
//                h1.miakki = 0;
//                vydalicNastupny(context, 0);
//            } else {
//                throw new RuntimeException();
//            }
//        } else {
//            // нешыпячы ў наступным слове
//            if (h3.isHluchi()) {
//                vydalicNastupny(context, 0);
//            } else if (h3.isZvonki()) {
//                h1.bazavyHuk = Huk.BAZAVY_HUK.з;
//                vydalicNastupny(context, 0);
//            } else {
//                throw new RuntimeException();
//            }
//        }
//        return true;
    }

    @ProcessCase(name = "Спрашчэнне: з'-д/дз' +(звонкі ці санорны ў наступным слове) -> з'", logCountBefore = 3, logCountAfter = 2)
    public boolean zdz(ProcessContext context) {
        context.huki.get(context.currentPosition).padzielPasla = context.huki.get(context.currentPosition + 1).padzielPasla;
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: ш-ч-(с ці ш) -> ш-(с ці ш)", logCountBefore = 3, logCountAfter = 2)
    public boolean scs(ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: ж-дж -> ж (толькі на канцы слова)", logCountBefore = 2, logCountAfter = 1)
    public boolean zdzz(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

//    @ProcessCase(name="Спрашчэнне: д-дж -> дж, д-дз -> дз (не пасля галоснай)")
//    public boolean ddz1(ProcessContext context) {
//        int pos = context.currentPosition + 1;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return true;
//    }
//
//    @ProcessCase(name="Спрашчэнне: д-дж -> дж, д-дз -> дз (не перад галоснай)")
//    public boolean ddz2(ProcessContext context) {
//        int pos = context.currentPosition;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return true;
//    }

//    @ProcessCase(name="Спрашчэнне: т-ч-> ч, т-ц -> ц (не пасля галоснай)")
//    public boolean tc1(ProcessContext context) {
//        int pos = context.currentPosition + 1;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return true;
//    }

//    @ProcessCase(name="Спрашчэнне: т-ч-> ч, т-ц -> ц(не перад галоснай)")
//    public boolean tc2(ProcessContext context) {
//        int pos = context.currentPosition;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.get(pos + 1).padvojeny = true;
//        context.huki.remove(pos);
//        return true;
//    }

    @ProcessCase(name = "Спрашчэнне падвойнага j", logCountBefore = 1, logCountAfter = 1)
    public boolean jj(ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
        context.huki.remove(pos);
        return true;
    }

//    @ProcessCase(name="Спрашчэнне: с-с-к -> c-к не на стыку")
//    public boolean ssk(Huk h1, Huk h2, ProcessContext context) {
//        vydalicNastupny(context, 0);
//        return true;
//    }

    @ProcessCase(name = "Спрашчэнне аднолькавых зычных (не пасля галоснай)", logCountBefore = 3, logCountAfter = 2)
    public boolean eq1(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        if (h2.bazavyHuk != h3.bazavyHuk) {
            if (context.debugPrefix != null) {
                context.debug.add(context.debugPrefix + " другі і трэці гукі не супадаюць");
            }
            return false;
        }
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
        context.huki.remove(pos);
        return true;
    }

    @ProcessCase(name = "Прыпадабненне свісцячых па мяккасці", logCountBefore = 2, logCountAfter = 2)
    public boolean eq2(Huk h1, Huk h2) {
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: н-т-ш-ч -> н-ш-ч", logCountBefore = 4, logCountAfter = 3)
    public boolean ntsc(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return true;
    }

//    @ProcessCase(name = "Спрашчэнне: н-д-ш -> н-ш, н-т-ш -> н-ш",logCountBefore = 3, logCountAfter = 2)
//    public boolean nds(Huk h1, Huk h2, ProcessContext context) {
//        vydalicPapiaredni(context, 2);
//        return true;
//    }

    @ProcessCase(name = "Спрашчэнне: н-т-с -> н-с", logCountBefore = 3, logCountAfter = 2)
    public boolean nts(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: т-с не на мяжы -> ц", logCountBefore = 2, logCountAfter = 1)
    public boolean ts(Huk huk1, Huk huk2, ProcessContext context) {
        if (huk1.bazavyHuk == Huk.BAZAVY_HUK.н) {
            // першы гук 'н' - іншамоўнае слова
            return false;
        }
        huk2.bazavyHuk = BAZAVY_HUK.ц;
        vydalicNastupny(context, 1);
        return true;
    }

    @ProcessCase(name = "Пераход: т-с на мяжы -> ц-с", logCountBefore = 2, logCountAfter = 2)
    public boolean ts_miaza(Huk huk1, Huk huk2, ProcessContext context) {
        if (huk1.bazavyHuk == Huk.BAZAVY_HUK.н) {
            // першы гук 'н' - іншамоўнае слова
            return false;
        }
        huk2.bazavyHuk = BAZAVY_HUK.ц;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: б-р-с -> б-с", logCountBefore = 3, logCountAfter = 2)
    public boolean brs(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: м-л'-с -> м-с", logCountBefore = 3, logCountAfter = 2)
    public boolean mls(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: ц-ч -> ч", logCountBefore = 2, logCountAfter = 1)
    public boolean cc(Huk hal, Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = Huk.BAZAVY_HUK.ч;
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Спрашчэнне: з'-с -> с:", logCountBefore = 2, logCountAfter = 1)
    public boolean zs(Huk h1, Huk h2, ProcessContext context) {
        throw new RuntimeException("Спрашчэнне: з'-с -> с:");
    }

    @ProcessCase(name = "Спрашчэнне: з'-з -> з", logCountBefore = 2, logCountAfter = 1)
    public boolean zz(Huk h1, Huk h2, ProcessContext context) {
        throw new RuntimeException("Спрашчэнне: з'-з -> з");
    }

    @ProcessCase(name = "Прыпадабненне: с'-ш -> ш:, с'-ж -> ж:, с'-з -> з:, з’+ш-> ш:, з’+ж-> ж:", logCountBefore = 2, logCountAfter = 1)
    public boolean ss(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 1);
        h2.padvojeny = true;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне: с'+ц-> сц, с'ч -> сч", logCountBefore = 2, logCountAfter = 2)
    public boolean sc(Huk h1, Huk h2) {
        h1.miakki = 0;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне дч -> тч, дц -> тц", logCountBefore = 2, logCountAfter = 2)
    public boolean pryDC(Huk huk1, Huk huk2) {
        huk1.bazavyHuk = BAZAVY_HUK.т;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т-ш -> ч-ш, т+ч => ч-ч пасля галоснага", logCountBefore = 2, logCountAfter = 2)
    public boolean pryTS(Huk huk1, Huk huk2) {
        huk2.bazavyHuk = BAZAVY_HUK.ч;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т-ш -> ч-ш, т+ч => ч на канцы слова", logCountBefore = 3, logCountAfter = 2)
    public boolean pryTSe(ProcessContext context) {
        vydalicPapiaredni(context, 1);
        return true;
    }

    @ProcessCase(name = "Прыпадабненне ґ+г => г:", logCountBefore = 2, logCountAfter = 2)
    public boolean gh(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = BAZAVY_HUK.г;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне т+ц => ц:", logCountBefore = 2, logCountAfter = 2)
    public boolean tc(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне д+дж => дж:, д+дз => дз:", logCountBefore = 2, logCountAfter = 2)
    public boolean ddz(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        return true;
    }

    @ProcessCase(name = "Прыпадабненне дз+д => д'+д", logCountBefore = 2, logCountAfter = 2)
    public boolean dzd(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = BAZAVY_HUK.д;
        return true;
    }

    /*
     * @ProcessCase(name="Падваенне аднолькавых зычных") public boolean eq(Huk h1,
     * Huk h2, Huk h3, ProcessContext context) { if (h2.bazavyHuk != h3.bazavyHuk) {
     * return null; } int pos = context.currentPosition + 1; context.huki.get(pos +
     * 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary +
     * context.huki.get(pos + 1).zychodnyjaLitary; context.huki.remove(pos);
     * h3.padvojeny = true; return true; }
     */

    private void vydalicPapiaredni(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        context.huki.get(pos).zychodnyjaLitary += context.huki.get(pos - 1).zychodnyjaLitary + context.huki.get(pos).zychodnyjaLitary;
        context.huki.get(pos).debug = context.huki.get(pos - 1).debug || context.huki.get(pos).debug;
        context.huki.remove(pos - 1);
    }

    private void vydalicNastupny(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        context.huki.get(pos).zychodnyjaLitary += context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos).debug = context.huki.get(pos + 1).debug || context.huki.get(pos).debug;
        context.huki.remove(pos + 1);
    }
}
