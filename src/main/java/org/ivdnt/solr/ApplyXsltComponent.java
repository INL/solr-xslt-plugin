package org.ivdnt.solr;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.lucene.document.Document;
import org.apache.solr.cloud.ZkController;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.search.DocIterator;
import org.apache.solr.search.DocList;
import org.apache.solr.search.SolrIndexSearcher;
import org.apache.solr.util.plugin.SolrCoreAware;

public class ApplyXsltComponent extends SearchComponent implements SolrCoreAware {

    public static final String COMPONENT_NAME = "apply-xslt";
    
//    private BasicTransformerFactory factory;

    private String xsltFilePath;
    
//    private File xsltFile;
    
    private ReloadingXsltTransformer xslt;

    private String inputField;
    
    public ApplyXsltComponent() {
//        factory = new net.sf.saxon.BasicTransformerFactory();
    }
    
    @Override
    public void inform(SolrCore core) {
        File xsltFile = findFile(core, xsltFilePath);
        xslt = new ReloadingXsltTransformer(xsltFile);
    }
    
    /**
     * (Re)Loads elevation configuration.
     *
     * @param core The core holding this component.
     * @return The number of elevation rules parsed.
     */
    protected File findFile(SolrCore core, String path) {
        ZkController zkController = core.getCoreContainer().getZkController();
        if (zkController != null) {
          throw new UnsupportedOperationException("Zookeeper not yet supported");
        }
        return new File(core.getResourceLoader().getConfigDir(), path);
    }

    @Override
    public void init(@SuppressWarnings("rawtypes") NamedList args) {
      SolrParams initArgs = args.toSolrParams();
      System.out.println("Parameters: " + initArgs);
      if (initArgs.get("xsltFile") == null || initArgs.get("inputField") == null) {
          throw new RuntimeException("ApplyXsltComponent needs xsltFile and inputField parameters!");
      }
      xsltFilePath = initArgs.get("xsltFile");
      inputField = initArgs.get("inputField");
    }
    
    @Override
    public void prepare(ResponseBuilder rb) throws IOException {
        rb.setNeedDocList(true);
    }

    @Override
    public synchronized void process(ResponseBuilder rb) throws IOException {
        String parXslt = rb.req.getParams().get("applyXslt");
        if (parXslt != null && Boolean.parseBoolean(parXslt)) {
            DocList results = null;
            if (rb.getResults() != null) {
                results = rb.getResults().docList;
            }
            //SimpleOrderedMap<String> data = new SimpleOrderedMap<>();
            List<String> data = new ArrayList<>();
            DocIterator it = results.iterator();
            SolrIndexSearcher searcher = rb.req.getSearcher();
            
            try {
                Transformer transformer = xslt.get(); //factory.newTransformer(new StreamSource(new FileInputStream(xsltFile)));
                while (it.hasNext()) {
                    // Get the XML from the response
                    Document doc = searcher.doc(it.next());
                    String xml = doc.get(inputField); // TODO: make field configurable
                    if (xml != null) {
                        // Transform the XML using the XSLT
                        Source source = new StreamSource(new StringReader(xml));
                        StringWriter html = new StringWriter();
                        Result result = new StreamResult(html);
                        transformer.reset();
                        transformer.transform(source, result);

                        // Add to our output section in the same order as the results
                        data.add(html.toString());
                    }
                }
                rb.rsp.add("apply-xslt", data);
                
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /////////////////////////////////////////////
    ///  SolrInfoBean
    ////////////////////////////////////////////

    @Override
    public String getDescription() {
        return "Debug Information";
    }

    @Override
    public Category getCategory() {
        return Category.OTHER;
    }

}
