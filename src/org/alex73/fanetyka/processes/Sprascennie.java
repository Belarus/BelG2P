package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;
import org.alex73.fanetyka.impl.ProcessContext;

public class Sprascennie {
    @ProcessCase("Спрашчэнне: с-ц-к пераходзіць у с-к")
    public String sck(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: с-т-ч пераходзіць у ш-ч")
    public String stc(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: с-т-н -> с-н")
    public String stn(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return "";
    }

    @ProcessCase("Спрашчэнне: з-д-н -> з-н")
    public String zdn(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        h1.miakki = h3.miakki;
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: р-к-с-к -> р-c-к")
    public String rksk(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Прыпадабненне аднолькавых зычных")
    public String eq2(Huk h1, Huk h2, ProcessContext context) {
        if (h1.bazavyHuk != h2.bazavyHuk) {
            return null;
        }
        vydalicPapiaredni(context, 1);
        h2.padvojeny = true;
        return "";
    }

    @ProcessCase("Спрашчэнне: с/ш/х-с-к -> c-к не на стыку")
    public String ssks(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 1);
        return "";
    }

    @ProcessCase("Спрашчэнне: с-т-с -> c:")
    public String sts(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        h1.padvojeny = true;
        h1.miakki = h3.miakki;
        vydalicNastupny(context, 0);
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: з-д-ч -> ш-ч")
    public String zdc(Huk h1, Huk h2, ProcessContext context) {
        h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
        h2.debug = h1.debug || h2.debug;
        h2.bazavyHuk = BAZAVY_HUK.ш;
        context.huki.remove(context.currentPosition);
        return "";
    }

    @ProcessCase("Прыпадабненне: ш-с-> с-с")
    public String ss(Huk h1, Huk h2) {
        h1.bazavyHuk = BAZAVY_HUK.с;
        return "";
    }

    @ProcessCase("Прыпадабненне зычных: ц'-т -> т'-т")
    public String ct(Huk h1, Huk h2) {
        String r = h1.bazavyHuk.name() + "'-" + h2.bazavyHuk.name() + " -> т'-ц";
        h1.bazavyHuk = BAZAVY_HUK.т;
        return r;
    }

    @ProcessCase("Спрашчэнне: с'-ц' + зычны")
    public String cch(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        String r = "с'-ц'-" + h3.bazavyHuk.name() + " -> ";
        if (h3.isSanorny()) {
            // калі санорны - не спрашчаецца
            return null;
        }
        if (h3.isSypiacy()) {
            // шыпячы ў наступным слове
            if (h3.isHluchi()) {
                h1.bazavyHuk = Huk.BAZAVY_HUK.ш;
                h1.miakki = 0;
                vydalicNastupny(context, 0);
                r += "с'ц'ш";
            } else if (h3.isZvonki()) {
                h1.bazavyHuk = Huk.BAZAVY_HUK.ж;
                h1.miakki = 0;
                vydalicNastupny(context, 0);
                r += "с'ц'ж";
            } else {
                throw new RuntimeException();
            }
        } else {
            // нешыпячы ў наступным слове
            if (h3.isHluchi()) {
                vydalicNastupny(context, 0);
                r += "с'ц'";
            } else if (h3.isZvonki()) {
                h1.bazavyHuk = Huk.BAZAVY_HUK.з;
                vydalicNastupny(context, 0);
                r += "с'ц'з";
            } else {
                throw new RuntimeException();
            }
        }
        return r;
    }

    @ProcessCase("Спрашчэнне: з'-д' +(звонкі ці санорны ў наступным слове) -> з'")
    public String zdz(ProcessContext context) {
        context.huki.get(context.currentPosition).padzielPasla = context.huki.get(context.currentPosition + 1).padzielPasla;
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: ш-ч-с -> ш-с")
    public String scs(ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: ж-дж -> ж (толькі на канцы слова)")
    public String zdzz(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

//    @ProcessCase("Спрашчэнне: д-дж -> дж, д-дз -> дз (не пасля галоснай)")
//    public String ddz1(ProcessContext context) {
//        int pos = context.currentPosition + 1;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return "";
//    }
//
//    @ProcessCase("Спрашчэнне: д-дж -> дж, д-дз -> дз (не перад галоснай)")
//    public String ddz2(ProcessContext context) {
//        int pos = context.currentPosition;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return "";
//    }

//    @ProcessCase("Спрашчэнне: т-ч-> ч, т-ц -> ц (не пасля галоснай)")
//    public String tc1(ProcessContext context) {
//        int pos = context.currentPosition + 1;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.remove(pos);
//        return "";
//    }

//    @ProcessCase("Спрашчэнне: т-ч-> ч, т-ц -> ц(не перад галоснай)")
//    public String tc2(ProcessContext context) {
//        int pos = context.currentPosition;
//        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
//        context.huki.get(pos + 1).padvojeny = true;
//        context.huki.remove(pos);
//        return "";
//    }

    @ProcessCase("Спрашчэнне падвойнага j")
    public String jj(ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
        context.huki.remove(pos);
        return "";
    }

//    @ProcessCase("Спрашчэнне: с-с-к -> c-к не на стыку")
//    public String ssk(Huk h1, Huk h2, ProcessContext context) {
//        vydalicNastupny(context, 0);
//        return "";
//    }

    @ProcessCase("Спрашчэнне аднолькавых зычных (не пасля галоснай)")
    public String eq1(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        if (h2.bazavyHuk != h3.bazavyHuk) {
            if (context.debugPrefix!=null) {
                context.debug.add(context.debugPrefix + " другі і трэці гукі не супадаюць");
            }
            return null;
        }
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.get(pos + 1).debug = context.huki.get(pos).debug || context.huki.get(pos + 1).debug;
        context.huki.remove(pos);
        return "";
    }

    @ProcessCase("Прыпадабненне свісцячых-шыпячых")
    public String eq2(Huk h1, Huk h2) {
        if (h2.isSanorny()) {
            return null;
        }
        h1.miakki = 0;
        return "";
    }

    @ProcessCase("Спрашчэнне: н-т-ш-ч -> н-ш-ч")
    public String ntsc(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return "";
    }

    @ProcessCase("Спрашчэнне: н-д-ш -> н-ш, н-т-ш -> н-ш")
    public String nds(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return "";
    }

    @ProcessCase("Спрашчэнне: н-т-с -> н-с")
    public String nts(Huk h1, Huk h2, ProcessContext context) {
        vydalicPapiaredni(context, 2);
        return "";
    }

    @ProcessCase("Спрашчэнне: т-с -> ц")
    public String ts(Huk huk, ProcessContext context) {
        huk.bazavyHuk = BAZAVY_HUK.ц;
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Пераход: т-с на мяжы -> ц-с")
    public String ts_miaza(Huk huk, ProcessContext context) {
        huk.bazavyHuk = BAZAVY_HUK.ц;
        return "";
    }

    @ProcessCase("Спрашчэнне: б-р-с -> б-с")
    public String brs(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: м-л'-с -> м-с")
    public String mls(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: ц-ч -> ч")
    public String cc(Huk hal, Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = Huk.BAZAVY_HUK.ч;
        h1.miakki = 0;
        return "ц->ч";
    }

    @ProcessCase("Спрашчэнне: з'-с -> с:")
    public String zs(Huk h1, Huk h2, ProcessContext context) {
        throw new RuntimeException("Спрашчэнне: з'-с -> с:");
    }

    @ProcessCase("Спрашчэнне: з'-з -> з")
    public String zz(Huk h1, Huk h2, ProcessContext context) {
        throw new RuntimeException("Спрашчэнне: з'-з -> з");
    }

    @ProcessCase("Прыпадабненне: с'-ш -> ш:, с'-ж -> ж:, с'-з -> з:, з’+ш-> ш:, з’+ж-> ж:")
    public String ss(Huk h1, Huk h2, ProcessContext context) {
        String r = h1.bazavyHuk + "'" + h2.bazavyHuk + " -> " + h2.bazavyHuk + ":";
        vydalicPapiaredni(context, 1);
        h2.padvojeny = true;
        return r;
    }

    @ProcessCase("Прыпадабненне: с'+ц-> сц, с'ч -> сч")
    public String sc(Huk h1, Huk h2) {
        String r = h1.bazavyHuk + "'" + h2.bazavyHuk + " -> " + h1.bazavyHuk + h2.bazavyHuk;
        h1.miakki = 0;
        return r;
    }

    @ProcessCase("Прыпадабненне дч -> тч, дц -> тц")
    public String pryDC(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.т;
        return "";
    }

    @ProcessCase("Прыпадабненне т-ш -> ч-ш, т+ч => ч-ч")
    public String pryTS(Huk huk) {
        huk.bazavyHuk = BAZAVY_HUK.ч;
        return "";
    }

    @ProcessCase("Прыпадабненне ґ+г => г:")
    public String gh(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = BAZAVY_HUK.г;
        return "";
    }

    @ProcessCase("Прыпадабненне т+ц => ц:")
    public String tc(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
        return "";
    }

    @ProcessCase("Прыпадабненне д+дж => дж:, д+дз => дз:")
    public String ddz(Huk h1, Huk h2, ProcessContext context) {
        h1.bazavyHuk = h2.bazavyHuk;
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
