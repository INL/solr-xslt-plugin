package org.ivdnt.solr;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class StripJewishAccentsFilter extends TokenFilter {

    public static final Map<String, String> map = new LinkedHashMap<>(Map.ofEntries(
            entry("אַ", "א"),
            entry("אָ", "א"),
            entry("בּ", "ב"),
            entry("בֿ", "ב"),
            entry("װ", "וו"),
            entry("וּ", "ו"),
            entry("ױ", "וי"),
            entry("יִ", "י"),
            entry("ײ", "יי"),
            entry("ײַ", "יי"),
            entry("כּ", "כ"),
            entry("כֿ", "כ"),
            entry("פּ", "פ"),
            entry("פֿ", "פ"),
            entry("שׁ", "ש"),
            entry("שׂ", "ש"),
            entry("תּ", "ת"),
            entry("־", "-")
    ));

    public static String strip(String s) {
        for (Map.Entry<String, String> entry: map.entrySet()) {
            s = s.replace(entry.getKey(), entry.getValue());
        }
        return s;
    }

    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public StripJewishAccentsFilter(TokenStream in) {
        super(in);
    }

    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            String t = new String(termAtt.buffer(), 0, termAtt.length());
            String n = strip(t);
            if (!n.equals(t))
                termAtt.copyBuffer(n.toCharArray(), 0, n.length());
            return true;
        }
        return false;
    }
}
