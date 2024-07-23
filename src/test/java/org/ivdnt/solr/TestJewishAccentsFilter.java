package org.ivdnt.solr;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;

/** Test some character filters.  */
public class TestJewishAccentsFilter {

    static String stripJewishAccents2(String s) {

        // Replace some characters that are not in the Unicode normalization
        s = s.replace("כּ", "כ");
        s = s.replace("וּ", "ו");
        s = s.replace("יִ", "ו");
        s = s.replace("תּ", "ת");

        s = Normalizer.normalize(s, Normalizer.Form.NFKD);

        // Return the string with all combining diacritical marks removed
        return s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    void test(String expected, String input) {
        Assert.assertEquals(input + "(using search/replace)", expected, StripJewishAccentsFilter.strip(input));
        //Assert.assertEquals(input + "(using normalization)", expected, stripJewishAccents2(input));
    }

    public static void main(String[] args) throws IOException {

        TokenStream tokenStream = new TokenStreamFromList(StripJewishAccentsFilter.map.keySet());
        StripJewishAccentsFilter filter = new StripJewishAccentsFilter(tokenStream);
        CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);

        for (Map.Entry<String, String> entry: StripJewishAccentsFilter.map.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();

            filter.incrementToken();
            String result = termAtt.toString();
            boolean correct = result.equals(v);

            System.out.println("Character " +
                    k + " (len " + k.length() + ") became " +
                    result + " (len " + result.length() + ")" +
                    (correct ? "" : " (incorrect, should be " +
                    v + " (len " + v.length() + ")"));
        }
    }

    @Test
    public void testStripJewishAccents() {
        for (Map.Entry<String, String> entry : StripJewishAccentsFilter.map.entrySet()) {
            test(entry.getValue(), entry.getKey());
        }
    }

    @Test
    public void testStripJewishAccentsWholeString() {
        String input    = "אַאָבּבֿװוּױיִײײַכּכֿפּפֿשׁשׂתּ־";
        String expected = "אאבבווווייייייככפפששת-";
        test(expected, input);
    }

}
