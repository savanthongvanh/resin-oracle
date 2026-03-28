FROM gradle:7.6.4-jdk8 AS build
WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY src src
COPY postman postman

RUN ./gradlew --no-daemon clean war copyRuntimeLibs

FROM eclipse-temurin:8-jre-jammy
WORKDIR /opt/resin

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl ca-certificates \
    && rm -rf /var/lib/apt/lists/* \
    && curl -kfSL https://caucho.com/download/resin-4.0.67.tar.gz -o /tmp/resin.tar.gz \
    && tar -xzf /tmp/resin.tar.gz -C /opt \
    && mv /opt/resin-4.0.67 /opt/resin-dist \
    && cp -R /opt/resin-dist/. /opt/resin/ \
    && rm -f /tmp/resin.tar.gz

COPY docker/resin/resin.xml.template /opt/resin/conf/resin.xml.template
COPY docker/resin/docker-entrypoint.sh /opt/resin/bin/docker-entrypoint.sh
COPY --from=build /workspace/build/libs/ROOT.war /opt/resin/webapps/ROOT.war
COPY --from=build /workspace/build/runtime-libs/ /opt/resin/lib/

RUN chmod +x /opt/resin/bin/docker-entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/opt/resin/bin/docker-entrypoint.sh"]
