## Stage 0: Build Solr plugin
#=============================
FROM maven:3.9-eclipse-temurin-11 AS builder
WORKDIR /app

# Copy source from stage 0
COPY . .

# Build plugin JAR
RUN --mount=type=cache,target=/root/.m2 mvn --no-transfer-progress package


## Stage 1: Solr
#=============================
FROM solr:8.11
LABEL maintainer="Jan Niestadt <jan.niestadt@ivdnt.org>"

# Download and place Saxon JAR
USER root
RUN curl -SL https://repo1.maven.org/maven2/net/sf/saxon/Saxon-HE/9.8.0-4/Saxon-HE-9.8.0-4.jar --output /opt/solr/dist/Saxon-HE-9.8.0-4.jar \
    && chown -R solr:solr /opt/solr/dist

# Place solr-apply-xslt plugin JAR built in previous stage
USER solr
COPY --from=builder /app/target/solr-apply-xslt.jar /opt/solr/contrib/apply-xslt/lib/
