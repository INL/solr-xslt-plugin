package org.ivdnt.solr;

import static java.util.Map.entry;

import java.util.LinkedHashMap;
import java.util.Map;

public class JewishAccentStripper1 implements AccentStripper {

    public static final Map<String, String> map = new LinkedHashMap<>();

    static {
        map.put("\u05D0\u05B7", "א"); // letter אַ als samengesteld unicode teken
        map.put("\uFB2E", "א");       // letter אַ als 1 unicode teken
        map.put("אָ", "א");
        map.put("בּ", "ב");
        map.put("בֿ", "ב");
        map.put("װ", "וו");
        map.put("וּ", "ו");
        map.put("ױ", "וי");
        map.put("יִ", "י");
        map.put("ײ", "יי");
        map.put("ײַ", "יי");
        map.put("כּ", "כ");
        map.put("כֿ", "כ");
        map.put("פּ", "פ");
        map.put("פֿ", "פ");
        map.put("שׁ", "ש");
        map.put("שׂ", "ש");
        map.put("תּ", "ת");
        map.put("־", "-");
    }

    public String strip(String s) {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return s;
    }
}
