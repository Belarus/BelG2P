package org.alex73.fanetyka.processes;

import org.alex73.fanetyka.config.ProcessCase;
import org.alex73.fanetyka.impl.Huk;
import org.alex73.fanetyka.impl.ProcessContext;
import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

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

    @ProcessCase("Спрашчэнне: с-с-к -> c-к не на сутыку")
    public String ssk(Huk h1, Huk h2, ProcessContext context) {
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: с-с-к -> c:-к на сутыку")
    public String ssks(Huk h1, Huk h2, ProcessContext context) {
        h1.padvojeny = true;
        vydalicNastupny(context, 0);
        return "";
    }

    @ProcessCase("Спрашчэнне: з-д-ч -> ш-ч")
    public String zdc(Huk h1, Huk h2, ProcessContext context) {
        h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
        h2.bazavyHuk = BAZAVY_HUK.ш;
        context.huki.remove(context.currentPosition);
        return "";
    }

//    @ProcessCase("Спрашчэнне: ш-с-> с-с")
//    public String ss(Huk h1, Huk h2) {
//        h1.bazavyHuk = BAZAVY_HUK.с;
//        return "";
//    }

    @ProcessCase("Спрашчэнне: ц'-т -> т'-т, ц'-ц -> т'-ц")
    public String ct(Huk h1, Huk h2) {
        String r = h1.bazavyHuk.name() + "'-" + h2.bazavyHuk.name() + " -> т'-ц";
        h1.bazavyHuk = BAZAVY_HUK.т;
        return r;
    }

    @ProcessCase("Спрашчэнне: с'-ц' + зычны")
    public String cch(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        String r = "с'-ц'-" + h3.bazavyHuk.name() + " -> ";
        if ((h3.isHluchi() || h3.isSanorny()) && !h3.isSypiacy()) {
            // глухі нешыпячы ў наступным слове
            vydalicNastupny(context, 0);
            r += "с'ц'";
        } else if (h3.isHluchi() && h3.isSypiacy()) {
            // глухі шыпячы ў наступным слове
            h1.bazavyHuk = Huk.BAZAVY_HUK.ш;
            h1.miakki = 0;
            vydalicNastupny(context, 0);
            r += "с'ц'ш";
        } else if (h3.isZvonki() && !h3.isSypiacy()) {
            // звонкі нешыпячы ў наступным слове
            h1.bazavyHuk = Huk.BAZAVY_HUK.з;
            vydalicNastupny(context, 0);
            r += "с'ц'з";
        } else if (h3.isZvonki() && h3.isSypiacy()) {
            // звонкі шыпячы ў наступным слове
            h1.bazavyHuk = Huk.BAZAVY_HUK.ж;
            h1.miakki = 0;
            vydalicNastupny(context, 0);
            r += "с'ц'ж";
        } else {
            throw new RuntimeException();
        }
        return r;
    }

    @ProcessCase("Спрашчэнне: з'-д' +(звонкі ў наступным слове) -> з'")
    public String zdz(ProcessContext context) {
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

    @ProcessCase("Спрашчэнне: т-ч-> ч, т-ц -> ц (не пасля галоснай)")
    public String tc1(ProcessContext context) {
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        return "";
    }

    @ProcessCase("Спрашчэнне: т-ч-> ч, т-ц -> ц(не перад галоснай)")
    public String tc2(ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        return "";
    }

    @ProcessCase("Спрашчэнне падвойнага j у сярэдзіне слова")
    public String jj(ProcessContext context) {
        int pos = context.currentPosition;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        return "";
    }

    @ProcessCase("Спрашчэнне аднолькавых зычных (не пасля галоснай)")
    public String eq1(Huk h1, Huk h2, Huk h3, ProcessContext context) {
        if (h2.bazavyHuk != h3.bazavyHuk) {
            return null;
        }
        int pos = context.currentPosition + 1;
        context.huki.get(pos + 1).zychodnyjaLitary = context.huki.get(pos).zychodnyjaLitary + context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos);
        return "";
    }

    @ProcessCase("Спрашчэнне аднолькавых зычных")
    public String eq2(Huk h1, Huk h2, ProcessContext context) {
        if (h1.bazavyHuk != h2.bazavyHuk) {
            return null;
        }
        h2.zychodnyjaLitary = h1.zychodnyjaLitary + h2.zychodnyjaLitary;
        h2.padvojeny = true;
        context.huki.remove(context.currentPosition);
        return "";
    }

    @ProcessCase("Спрашчэнне свісцячых-шыпячых")
    public String eq2(Huk h1, Huk h2) {
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

    @ProcessCase("Спрашчэнне: с'-ш -> ш:, с'-ж -> ж:, с'-з -> з:, з’+ш-> ш:, з’+ж-> ж:")
    public String ss(Huk h1, Huk h2, ProcessContext context) {
        String r = h1.bazavyHuk + "'" + h2.bazavyHuk + " -> " + h2.bazavyHuk + ":";
        vydalicPapiaredni(context, 1);
        h2.padvojeny = true;
        return r;
    }

    @ProcessCase("Спрашчэнне: с'+ц-> сц, с'ч -> сч")
    public String sc(Huk h1, Huk h2) {
        String r = h1.bazavyHuk + "'" + h2.bazavyHuk + " -> " + h1.bazavyHuk + h2.bazavyHuk;
        h1.miakki = 0;
        return r;
    }

    private void vydalicPapiaredni(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        context.huki.get(pos).zychodnyjaLitary += context.huki.get(pos - 1).zychodnyjaLitary + context.huki.get(pos).zychodnyjaLitary;
        context.huki.remove(pos - 1);
    }

    private void vydalicNastupny(ProcessContext context, int offsetFromCurrent) {
        int pos = context.currentPosition + offsetFromCurrent;
        context.huki.get(pos).zychodnyjaLitary += context.huki.get(pos + 1).zychodnyjaLitary;
        context.huki.remove(pos + 1);
    }
}
