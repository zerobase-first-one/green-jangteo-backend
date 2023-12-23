FROM openjdk:11-jre-slim

ONBUILD RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY ./*.jar app.jar

EXPOSE 8443

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

CMD ["java", "-jar", "app.jar"]

###

FROM docker.elastic.co/elasticsearch/elasticsearch:7.15.1
RUN elasticsearch-plugin install analysis-nori https://github.com/skyer9/elasticsearch-jaso-analyzer/releases/download/v7.15.1/jaso-analyzer-plugin-7.15.1-plugin.zip
