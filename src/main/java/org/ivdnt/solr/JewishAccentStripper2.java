package org.ivdnt.solr;

import static java.util.Map.entry;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JewishAccentStripper2 implements AccentStripper {

    // Alle Unicode "combining marks" (klinker-/medeklinkerpunten e.d.)
    private static final Pattern COMBINING_MARKS = Pattern.compile("\\p{Mn}+");

    public String strip(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // 1. NFKD: ontleedt voorgecomponeerde tekens (bv. U+FB2E "אַ")
        //    in basisletter + los combinerend teken.
        String decomposed = Normalizer.normalize(input, Normalizer.Form.NFKD);

        // 2. Verwijder alle combinerende diakritische tekens.
        //    Dit dekt in één keer: patach-alef, komets-alef, dagesh-
        //    en rafe-varianten, shin-/sin-punt, hiriq-yod, etc. —
        //    ongeacht of de brontekst voorgecomponeerd of al ontleed was.
        String stripped = COMBINING_MARKS.matcher(decomposed).replaceAll("");

        // 3. Ligaturen -> twee losse letters
        stripped = stripped
                .replace("\u05F0", "\u05D5\u05D5")   // װ -> וו
                .replace("\u05F1", "\u05D5\u05D9")   // ױ -> וי
                .replace("\u05F2", "\u05D9\u05D9");  // ײ -> יי

        // 4. Maqaf -> ASCII hyphen
        stripped = stripped.replace('\u05BE', '-');

        return stripped;
    }
}
