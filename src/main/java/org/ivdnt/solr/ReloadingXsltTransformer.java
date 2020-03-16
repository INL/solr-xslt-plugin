package org.ivdnt.solr;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

public class ReloadingXsltTransformer {
    
    private File xsltFile;
    
    private Transformer transformer;

    private long xsltFileLastModified = 0;

    private TransformerFactory transformerFactory;

    public ReloadingXsltTransformer(File xsltFile) {
        this.xsltFile = xsltFile;
        transformerFactory = TransformerFactory.newInstance();
    }
    
    public synchronized Transformer get() throws TransformerConfigurationException {
        if (transformer == null || xsltFile.lastModified() > xsltFileLastModified) {
            transformer = transformerFactory.newTransformer(new StreamSource(xsltFile));
            xsltFileLastModified = xsltFile.lastModified();
        }
        return transformer;
    }

}
