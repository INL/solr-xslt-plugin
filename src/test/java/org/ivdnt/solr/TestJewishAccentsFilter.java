package org.ivdnt.solr;

import static java.util.Map.entry;

import java.io.IOException;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.Assert;
import org.junit.Test;

/** Test some character filters.  */
public class TestJewishAccentsFilter {

    AccentStripper stripper = new JewishAccentStripper2();

    void test(String expected, String input) {
        Assert.assertEquals(input + "(using search/replace)", expected, stripper.strip(input));
    }

    public static void main(String[] args) throws IOException {

        TokenStream tokenStream = new TokenStreamFromList(JewishAccentStripper1.map.keySet());
        StripJewishAccentsFilter filter = new StripJewishAccentsFilter(tokenStream);
        CharTermAttribute termAtt = filter.getAttribute(CharTermAttribute.class);

        for (Map.Entry<String, String> entry: JewishAccentStripper1.map.entrySet()) {
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
        for (Map.Entry<String, String> entry : JewishAccentStripper1.map.entrySet()) {
            test(entry.getValue(), entry.getKey());
        }
    }

    @Test
    public void testStripJewishAccentsWholeString() {
        String input    = "אַאָבּבֿװוּױיִײײַכּכֿפּפֿשׁשׂתּ־";
        String expected = "אאבבווווייייייככפפששת-";
        test(expected, input);
    }

    @Test
    public void inputsNormalizeToTheSameString() {
        String r1 = stripper.strip("האט");
        String r2 = stripper.strip("האָט");
        String r3 = stripper.strip("האַט");
        Assert.assertEquals(r1, r2);
        Assert.assertEquals(r1, r3);
    }


}
