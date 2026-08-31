package org.ivdnt.solr;

import static java.util.Map.entry;

import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class StripJewishAccentsFilter extends TokenFilter {

    private final CharTermAttribute termAtt = this.addAttribute(CharTermAttribute.class);

    public StripJewishAccentsFilter(TokenStream in) {
        super(in);
    }

    AccentStripper stripper = new JewishAccentStripper2();

    public boolean incrementToken() throws IOException {
        if (input.incrementToken()) {
            String t = new String(termAtt.buffer(), 0, termAtt.length());
            String n = stripper.strip(t);
            if (!n.equals(t))
                termAtt.copyBuffer(n.toCharArray(), 0, n.length());
            return true;
        }
        return false;
    }
}
