package org.alex73.fanetyka.impl;

public interface ILogging {
    /**
     * Заўвагі да вынікаў.
     */
    void logAttention(String msg);

    /**
     * Падрыхтоўчыя змены.
     */
    void logPrepare(String msg);

    /**
     * Фанетычныя змены.
     * 
     * @param title      з'ява
     * @param details    падрабязнасці
     * @param changeFrom спалучэнне літар да змены
     * @param changeTo   спалучэнне літар пасля змены
     * @param dumpFrom   поўная фанетыка да змены
     * @param dumpTo     поўная фанетыка да змены
     */
    void logChange(String title, String details, String changeFrom, String changeTo, String dumpFrom, String dumpTo);

    /**
     * Іншыя фанетычныя змены падчас канвертавання.
     */
    void logChange(String msg);
}
