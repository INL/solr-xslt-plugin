package org.ivdnt.solr;

import java.util.Iterator;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.BytesRef;

/**
 * Takes an {@code Iterable<String>} and iterates through it as a TokenStream.
 *
 * The Strings are taken as terms, and the position increment is always 1.
 */
public class TokenStreamFromList extends TokenStream {

    /** Iterator over the terms */
    protected final Iterator<String> iterator;

    /**
     * Term text of the current token
     */
    protected final CharTermAttribute termAttr;

    public TokenStreamFromList(Iterable<String> tokens) {
        clearAttributes();
        termAttr = addAttribute(CharTermAttribute.class);
        iterator = tokens.iterator();
    }

    @Override
    final public boolean incrementToken() {
        // Capture token contents
        if (iterator.hasNext()) {
            String word = iterator.next();
            termAttr.copyBuffer(word.toCharArray(), 0, word.length());
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((iterator == null) ? 0 : iterator.hashCode());
        result = prime * result + ((termAttr == null) ? 0 : termAttr.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        TokenStreamFromList other = (TokenStreamFromList) obj;
        if (iterator == null) {
            if (other.iterator != null)
                return false;
        } else if (!iterator.equals(other.iterator))
            return false;
        if (termAttr == null) {
            if (other.termAttr != null)
                return false;
        } else if (!termAttr.equals(other.termAttr))
            return false;
        return true;
    }

}
