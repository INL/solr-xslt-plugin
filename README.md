# Solr XSLT plugin

A simple Solr plugin that will read one of the response fields, pass it through an XSLT, and include the resulting output in the response.

To enable this plugin for your core, in your `solrconfig.xml`, add this to the `<config>` section:

```xml
<!-- Load the solr-apply-xslt plugin -->
<lib dir="${solr.install.dir:/opt/solr}/dist/" regex="Saxon-*.jar" />
<lib dir="${solr.install.dir:/opt/solr}/contrib/apply-xslt/lib/" regex="solr-apply-xslt.*\.jar" />
```

Add the `apply-xslt` search component, and specify the XSLT file and the Solr field containing the input XML:

```xml
<!-- Our Apply XSLT SearchComponent -->
<searchComponent name="apply-xslt" class="org.ivdnt.solr.ApplyXsltComponent" >
    <!-- any parameters (apply what XSLT? to what response field?) go here -->
    <str name="xsltFile">./xslt/article.xslt</str>
    <str name="inputField">xml</str>
</searchComponent>
```

To run the plugin on your `/select` handler, add this to the `<requestHandler name="/select" ...>` element:

```xml
<!-- After all other components have run, apply XSLT to the configured XML field -->
<arr name="last-components">
<str>apply-xslt</str>
</arr>
```

The plugin will check for a parameter `applyXslt=true`. If found, it will look for the configured input field and run the XML throught the configured XSLT file.

## Docker

A `Dockerfile` is included which adds this to a Solr image. Build the image with this command:

    docker build -t instituutnederlandsetaal/solr-xslt:3 -f Dockerfile .

You can derive your own `Dockerfile` from this. Here's an example that adds a Solr configuration dir to the image and creates a core based on that configuration:

```Dockerfile
# Based on Solr + XSLT plugin image.
# Creates our core (using the config).
FROM instituutnederlandsetaal/solr-xslt:3

# Copy the configuration files for our core
COPY . /opt/solr/server/solr/configsets/woordcombinaties/conf

# Pre-create core (using the config copied above)
# as soon as the container is started.
CMD ["solr-precreate", "woordcombinaties", "/opt/solr/server/solr/configsets/woordcombinaties"]
```
