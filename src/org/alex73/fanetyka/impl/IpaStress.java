package org.alex73.fanetyka.impl;

import java.util.List;

import org.alex73.fanetyka.impl.Huk.BAZAVY_HUK;

/**
 * Выстаўляе націск па IPA-стандарце - перад складам.
 */
public class IpaStress {
    /**
     * Set stress using IPA standard - before syllable. It required to define syllables borders.
     */
    public static void setIpaStress(List<Huk> huki, Fanetyka3 parent) {
        int hal = 0;
        for (int i = 0; i < huki.size(); i++) {
            Huk h = huki.get(i);
            if (h.stress) {
                setIpaStress(huki, hal, i, parent);
            }
            if (h.halosnaja && h.bazavyHuk != BAZAVY_HUK.j) {
                hal = i;
            }
            if ((h.padzielPasla & Huk.PADZIEL_SLOVY) != 0 || (h.padzielPasla & Huk.PADZIEL_KARANI) != 0) {
                hal = i + 1;
            }
        }
    }

    /*
     * Націскі ў IPA: ставіцца перад складам.
     */
    private static void setIpaStress(List<Huk> huki, int prevHalIndex, int halIndex, Fanetyka3 parent) {
        // huki.get(halIndex).stress = false;
        StringBuilder s = new StringBuilder();
        for (int i = prevHalIndex; i <= halIndex; i++) {
            Huk h = huki.get(i);
            if (h.bazavyHuk == BAZAVY_HUK.ў || h.bazavyHuk == BAZAVY_HUK.j || h.bazavyHuk == BAZAVY_HUK.р) {
                s.append('J');
            } else if (h.halosnaja) {
                s.append('H');
            } else if (h.isSanorny()) {
                s.append('S');
            } else {
                s.append("Z");
            }
        }
        int pierad;
        switch (s.toString()) {
        case "H":
        case "JH":
        case "JSH":
        case "SH":
        case "SSH":
        case "SZZH":
        case "ZH":
        case "ZJH":
        case "ZSH":
        case "ZSJH":
        case "ZZH":
        case "ZZJH":
        case "ZZSH":
        case "ZZZH":
            pierad = 0;
            break;
        case "HH":
        case "HJH":
        case "HJJH":
        case "HSH":
        case "HZH":
        case "HZJH":
        case "HZSH":
        case "HZSSH":
        case "HZZH":
        case "HZZJH":
        case "HZZSH":
        case "HZZZH":
        case "HZZZJH":
        case "HZZZSH":
            pierad = 1;
            break;
        case "HJSH":
        case "HJSJH":
        case "HJSSH":
        case "HJZH":
        case "HJZJH":
        case "HJZSH":
        case "HJZZH":
        case "HJZZJH":
        case "HJZZSH":
        case "HSJH":
        case "HSSH":
        case "HSZH":
        case "HSZJH":
        case "HSZSH":
        case "HSZZH":
        case "HSZZJH":
        case "HSZZSH":
        case "HSZZZH":
        case "HZSJH":
        case "HJZZZH":
            pierad = 2;
            break;
        default:
            System.err.println("Незразумелая мяжа складаў у мадэлі '" + s + "' для слоў " + parent.inputWords + "/" + parent);
            pierad = -1;
            break;
        }
        if (pierad >= 0) {
            huki.get(prevHalIndex + pierad).stressIpa = true;
        }
    }
}
