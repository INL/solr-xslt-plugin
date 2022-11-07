package org.ivdnt.solr;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.core.CoreContainer;
import org.apache.solr.core.SolrCore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestEmbeddedSolr {

    private static CoreContainer container;

    private static SolrCore core;

    /*
     * PREPARE AND TEAR DOWN FOR TESTS
     */
    @BeforeClass
    public static void prepareClass() throws Exception
    {
        // create the coreContainer from conf dir in test resources
        URI uri = TestEmbeddedSolr.class.getResource("/solrDir").toURI();
        Path path = Path.of(uri);

        // Note that the following property could be set through JVM level arguments too
        CoreContainer container = CoreContainer.createAndLoad(path, Path.of("solr.xml"));
        //EmbeddedSolrServer server = new EmbeddedSolrServer(container, "");

        core = container.getCore("test");
    }

    @AfterClass
    public static void cleanUpClass()
    {
        if (core != null)
            core.close();
        if (container != null)
            container.shutdown();
    }

    @Ignore
    @Test
    public void testStuff() throws SolrServerException, IOException {
        EmbeddedSolrServer server = new EmbeddedSolrServer(container, "test");
        ModifiableSolrParams solrParams = new ModifiableSolrParams();
        solrParams.add(CommonParams.Q, "*:*");

        QueryResponse queryResponse = server.query(solrParams);
        for (SolrDocument document : queryResponse.getResults()) {
            System.out.println(document);
        }
    }

}
