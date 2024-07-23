package org.ivdnt.solr;

import static java.util.Map.entry;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.TokenFilterFactory;

public class StripJewishAccentsFilterFactory extends TokenFilterFactory {

    public StripJewishAccentsFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new StripJewishAccentsFilter(tokenStream);
    }
}
