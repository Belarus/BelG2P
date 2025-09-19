package org.alex73.fanetyka.impl;

import java.util.Set;

import org.alex73.fanetyka.impl.str.ToStringSkolny;

/**
 * 
 * 
 * Класіфікацыя гукаў: -
 * 
 * - https://ebooks.grsu.by/gachko_phonetic/klasifikatsyya-zychnykh-guka.htm
 * 
 * - ФСБЛМ с. 61
 */
public class Huk {
    public static final int PADZIEL_PRYSTAUKA = 1; // прыстаўка
    public static final int PADZIEL_PRYNAZOUNIK = 16; // Прыназоўнікі без, не, з, праз. TODO Дадаецца разам з падзелам на словы ?
    public static final int PADZIEL_KARANI = 2;
    public static final int PADZIEL_SLOVY = 4;
    public static final int PADZIEL_ZLUCOK = 8;
    public static final int PADZIEL_APOSTRAF = 32;
    public static final int MIAKKASC_ASIMILACYJNAJA = 1;
    public static final int MIAKKASC_PAZNACANAJA = 2;
    public static final int MASK_MIAKKASC_USIE = 3;

    /**
     * Кожны гук мусіць быць: галосным, альбо санорным, альбо парным звонкім, альбо
     * глухім.
     */
    public enum BAZAVY_HUK {
        а, б, в, β, г, ґ, д, э, дж, дз, ж, з, і, к, л, м, ɱ, н, о, п, р, с, т, у, ў, ф, х, ц, ч, ш, ы, j
    }

    /**
     * Поўны спіс гукаў, якія выводзяцца ў выніковай транскрыпцыі. Яны не супадаюць
     * ані з якой сістэмай, а толькі выкарыстоўваюцца для ўнутранага прадстаўлення
     * каб сканвертаваць у патрэбную сістэму пры вывадзе звычайнай заменай.
     */
    public enum POUNY_HUK {
        а, ɐ, б, бь, в, вь, β, βь, г, гь, ґ, ґь, д, дь, э, дж, дз, дзь, ж, з, зь, і, к, кь, л, ль, м, мь, ɱ, н, нь, о, п, пь, р, с, сь, т, ть, у, ў, ф, фь, х,
        хь, ц, ць, ч, ш, ы, ə, j
    }

    public static final Set<BAZAVY_HUK> halosnyja = Set.of(BAZAVY_HUK.а, BAZAVY_HUK.э, BAZAVY_HUK.і, BAZAVY_HUK.о, BAZAVY_HUK.у, BAZAVY_HUK.ы);
    /**
     * Няпарныя звонкія.
     */
    public static final Set<BAZAVY_HUK> sanornyja = Set.of(BAZAVY_HUK.р, BAZAVY_HUK.л, BAZAVY_HUK.м, BAZAVY_HUK.н, BAZAVY_HUK.в, BAZAVY_HUK.β, BAZAVY_HUK.ɱ,
            BAZAVY_HUK.ў, BAZAVY_HUK.j);
    /**
     * Парныя звонкія.
     */
    public static final Set<BAZAVY_HUK> parnyja_zvonkija = Set.of(BAZAVY_HUK.б, BAZAVY_HUK.д, BAZAVY_HUK.дз, BAZAVY_HUK.з, BAZAVY_HUK.ж, BAZAVY_HUK.дж,
            BAZAVY_HUK.г, BAZAVY_HUK.ґ);
    /**
     * Усе глухія - парныя і няпарныя.
     */
    public static final Set<BAZAVY_HUK> hluchija = Set.of(BAZAVY_HUK.п, BAZAVY_HUK.т, BAZAVY_HUK.ц, BAZAVY_HUK.с, BAZAVY_HUK.ш, BAZAVY_HUK.ч, BAZAVY_HUK.х,
            BAZAVY_HUK.к, BAZAVY_HUK.ф);

    /**
     * Шыпячыя.
     */
    public static final Set<BAZAVY_HUK> sypiacyja = Set.of(BAZAVY_HUK.ш, BAZAVY_HUK.ж, BAZAVY_HUK.дж, BAZAVY_HUK.ч);

    /**
     * Свісцячыя.
     */
    public static final Set<BAZAVY_HUK> sviasciacyja = Set.of(BAZAVY_HUK.с, BAZAVY_HUK.з, BAZAVY_HUK.дз, BAZAVY_HUK.ц);

    public String zychodnyjaLitary;
    public BAZAVY_HUK bazavyHuk;
    public int miakki;
    public boolean stress;

    // толькі ўстаўны "а" ці "ы"
    public boolean redukavany;

    /**
     * Гэты гук аддзелены ад наступнага.
     */
    public int padzielPasla;
    public WordContext wordContext; // спасылка на граматычную базу

    public static boolean SKIP_ERRORS = false; // не спыняць працу пры памылках канвертавання
    public boolean debug; // debug not applied processes

    public Huk(String z, BAZAVY_HUK b) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
    }

    public Huk(String z, BAZAVY_HUK b, int miakki, int padzielPasla) {
        zychodnyjaLitary = z;
        bazavyHuk = b;
        this.miakki = miakki;
        this.padzielPasla = padzielPasla;
    }

    @Override
    public String toString() {
        return new ToStringSkolny().huk2str(this);
    }

    public POUNY_HUK pouny() {
        switch (bazavyHuk) {
        case а:
            return redukavany ? POUNY_HUK.ɐ : POUNY_HUK.а;
        case б:
            return miakki == 0 ? POUNY_HUK.б : POUNY_HUK.бь;
        case в:
            return miakki == 0 ? POUNY_HUK.в : POUNY_HUK.вь;
        case β:
            return miakki == 0 ? POUNY_HUK.β : POUNY_HUK.βь;
        case г:
            return miakki == 0 ? POUNY_HUK.г : POUNY_HUK.гь;
        case ґ:
            return miakki == 0 ? POUNY_HUK.ґ : POUNY_HUK.ґь;
        case д:
            return miakki == 0 ? POUNY_HUK.д : POUNY_HUK.дь;
        case э:
            return POUNY_HUK.э;
        case дж:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.дж;
        case дз:
            return miakki == 0 ? POUNY_HUK.дз : POUNY_HUK.дзь;
        case ж:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.ж;
        case з:
            return miakki == 0 ? POUNY_HUK.з : POUNY_HUK.зь;
        case і:
            return POUNY_HUK.і;
        case к:
            return miakki == 0 ? POUNY_HUK.к : POUNY_HUK.кь;
        case л:
            return miakki == 0 ? POUNY_HUK.л : POUNY_HUK.ль;
        case м:
            return miakki == 0 ? POUNY_HUK.м : POUNY_HUK.мь;
        case ɱ:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.ɱ;
        case н:
            return miakki == 0 ? POUNY_HUK.н : POUNY_HUK.нь;
        case о:
            return POUNY_HUK.о;
        case п:
            return miakki == 0 ? POUNY_HUK.п : POUNY_HUK.пь;
        case р:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.р;
        case с:
            return miakki == 0 ? POUNY_HUK.с : POUNY_HUK.сь;
        case т:
            return miakki == 0 ? POUNY_HUK.т : POUNY_HUK.ть;
        case у:
            return POUNY_HUK.у;
        case ў:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.ў;
        case ф:
            return miakki == 0 ? POUNY_HUK.ф : POUNY_HUK.фь;
        case х:
            return miakki == 0 ? POUNY_HUK.х : POUNY_HUK.хь;
        case ц:
            return miakki == 0 ? POUNY_HUK.ц : POUNY_HUK.ць;
        case ч:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.ч;
        case ш:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return POUNY_HUK.ш;
        case ы:
            if (miakki != 0 && !Huk.SKIP_ERRORS) {
                throw new RuntimeException("Небывае мяккі: " + bazavyHuk);
            }
            return redukavany ? POUNY_HUK.ə : POUNY_HUK.ы;
        case j:
            if (miakki != Huk.MIAKKASC_PAZNACANAJA) {
                throw new RuntimeException("мусіць быць мяккі: " + bazavyHuk);
            }
            // ён заўсёды мяккі
            return POUNY_HUK.j;
        }
        throw new RuntimeException("Невядомы базавы гук: " + bazavyHuk);
    }
}
