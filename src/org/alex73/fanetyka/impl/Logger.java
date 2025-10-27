package org.alex73.fanetyka.impl;

import java.util.ArrayList;
import java.util.List;

public class Logger implements ILogging {
    List<String> attentions = new ArrayList<>();
    List<String> prepares = new ArrayList<>();
    List<String> changes = new ArrayList<>();

    @Override
    public void logAttention(String msg) {
        attentions.add(msg);
    }

    @Override
    public void logPrepare(String msg) {
        prepares.add(msg);
    }

    @Override
    public void logChange(String title, String details, String charsBefore, String charsAfter, String dumpBefore, String dumpAfter) {
        changes.add("<b>" + title + "</b>: [" + charsBefore + " -> " + charsAfter + "]. " + details + "; <span style='color: #663300'>" + dumpBefore + " => " + dumpAfter + "</span>");
    }

    public void logChange(String msg) {
        changes.add(msg);
    }

    public String dump() {
        StringBuilder out = new StringBuilder();

        if (!attentions.isEmpty()) {
            for (String a : attentions) {
                out.append("<b>" + a + "</b><br>\n");
            }
        }
        if (!prepares.isEmpty()) {
            out.append("<b>Падрыхтоўка:</b><br>\n");
            int pos = 0;
            for (String p : prepares) {
                pos++;
                out.append("<b>" + pos + ".</b> " + p + "<br>\n");
            }
        }
        if (!changes.isEmpty()) {
            out.append("<b>Фанетычныя змены:</b><br>\n");
            int pos = 0;
            for (String z : changes) {
                pos++;
                out.append("<b>" + pos + ".</b> " + z + "<br>\n");
            }
        }

        return out.toString();
    }
}
