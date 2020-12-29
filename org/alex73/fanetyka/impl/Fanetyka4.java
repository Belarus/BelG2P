package org.alex73.fanetyka.impl;

/**
 * Замяняем гукі паміж словамі.
 */
public class Fanetyka4 {
    public static String fixWords(String text) {
        text = text.replace("с ч", "шч"); // лёс часта
        text = text.replace("с ш", "ш:"); // час шукаць
        text = text.replace("с’ ш", "ш:"); // вось што
        text = text.replace("с’ ж", "ж:"); // летась жыхары
        text = text.replace("с’ з", "з:"); // Алесь Загорскі
        text = text.replace("с’ з’", "з’:"); // вось зіма
        text = text.replace("с’ ч", "шч"); // увес час
        text = text.replace("с’ с", "с:"); // увесь савецкі
        text = text.replace("с’ ц", "сц?"); // Антось цалкам
        text = text.replace("с з", "з:"); // клас захацеў
        text = text.replace("с з’", "з’:"); // рос зялёны
        text = text.replace("с с’", "с’:"); // нас сягоння
        text = text.replace("с ц’", "с’ц’"); // калгас цяля
        text = text.replace("з ш", "ш:"); // зараз шырокі
        text = text.replace("з ж", "ж:"); // праз жыццё
        text = text.replace("з ч", "шч"); // зараз чакае
        text = text.replace("з з’", "з’:"); // мароз зямлю
        text = text.replace("з с’", "с’:"); // мароз сёлета
        text = text.replace("з ц’", "с’ц’"); // газ цяпер
        text = text.replace("з’ ш", "ш:"); // Свіцязь шмат
        text = text.replace("з’ ж", "ж:"); // завязь жоўтых
        text = text.replace("з’ ч", "шч"); // гразь чарнее
        text = text.replace("з’ з", "з:"); // гразь зацягнула
        text = text.replace("з’ с", "с:"); // скрозь сад
        text = text.replace("з’ ц", "сц"); // дробязь цалкам
        text = text.replace("ц ч", "ч:"); // палац чыгуначнікаў
        text = text.replace("ц ж", "?"); // баец жорстка
        text = text.replace("ц ш", "чш"); // канец шляху
        text = text.replace("ц’ ж", "?"); // дасць жаданае
        text = text.replace("ц’ ч", "ч:"); // верыць чалавеку
        text = text.replace("ц’ ж", "дж?"); // даць жыццё
        text = text.replace("ц’ ш", "чш"); // хоць што
        text = text.replace("ц’ з", "дз?"); // здаць загадзя
        text = text.replace("с’ц’ з", "з:"); // дасць заснуць
        text = text.replace("ц' с", "цс"); // зрабіць сабе
        text = text.replace("ц’ с’", "(т)с'?"); // стаіць сёмы
        text = text.replace("с’ц’ ц", "с’ц"); // свядомасць цэлых
        text = text.replace("с’ц’ шч", "ш:ч"); // радасць шчырых
        text = text.replace("ц’ н", "(т)н"); // прызнаць новую
        text = text.replace("ц’ л", "(т)л"); // напісаць ліст
        text = text.replace("д ш", "чш"); // ад шашы
        text = text.replace("д ч", "ч:"); // ад часу
        text = text.replace("д ц’", "ц':"); // ад цёмнага
        text = text.replace("ч з", "d͡ʐз"); // Мікалаевіч завіхаецца
        text = text.replace("с’ц’ д", "з'д"); // чуласць да
        return text;
    }
}
