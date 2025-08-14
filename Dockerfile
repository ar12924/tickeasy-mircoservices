# syntax=docker/dockerfile:1.4

# Multi-stage build for all microservices in this mono-repo.
# Use build-arg SERVICE_CLASSIFIER to select which boot jar to run.

ARG MAVEN_IMAGE=maven:3.9.6-eclipse-temurin-11
ARG RUNTIME_IMAGE=eclipse-temurin:11-jre
ARG SERVICE_CLASSIFIER
ARG SKIP_GO_OFFLINE=true

FROM ${MAVEN_IMAGE} AS builder
ARG SERVICE_CLASSIFIER
ARG SKIP_GO_OFFLINE
WORKDIR /workspace
ENV MAVEN_OPTS="-Djava.net.preferIPv4Stack=true"

# 1) 帶入 settings.xml（若存在才複製，避免強制依賴）
COPY .mvn/settings.xml /opt/maven-settings.xml

# Cache deps
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    MSET=""; if [ -f /opt/maven-settings.xml ]; then MSET="-s /opt/maven-settings.xml"; fi; \
    if [ "${SKIP_GO_OFFLINE}" = "true" ]; then \
      echo "[maven] skip dependency:go-offline"; \
    else \
      mvn ${MSET} -B -q -T 1C -DskipTests \
        -Dmaven.wagon.http.retryHandler.count=5 \
        -Dmaven.wagon.httpconnectionManager.ttl=300 \
        -Dmaven.wagon.http.pool=true \
        -Dmaven.wagon.http.timeout=120000 \
        -Djava.net.preferIPv4Stack=true \
        dependency:go-offline; \
    fi

COPY src ./src
RUN --mount=type=cache,target=/root/.m2,sharing=locked \
    MSET=""; if [ -f /opt/maven-settings.xml ]; then MSET="-s /opt/maven-settings.xml"; fi; \
    mvn ${MSET} -B -q -T 1C -DskipTests \
      -Dmaven.wagon.http.retryHandler.count=5 \
      -Dmaven.wagon.httpconnectionManager.ttl=300 \
      -Dmaven.wagon.http.pool=true \
      -Dmaven.wagon.http.timeout=120000 \
      -Djava.net.preferIPv4Stack=true \
      package \
 && set -eux; \
    ARTIFACT=$(ls target/*-${SERVICE_CLASSIFIER}.jar 2>/dev/null || true); \
    if [ -z "$ARTIFACT" ]; then ARTIFACT=$(ls target/*-${SERVICE_CLASSIFIER}.war 2>/dev/null || true); fi; \
    if [ -z "$ARTIFACT" ]; then echo "No artifact found for classifier=${SERVICE_CLASSIFIER}" >&2; exit 1; fi; \
    mkdir -p /out && cp "$ARTIFACT" /out/app.jar

FROM ${RUNTIME_IMAGE} AS runtime

# Install curl for container healthchecks
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/*

ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /app

# Copy the singular artifact produced by builder (jar or war) as app.jar
COPY --from=builder /out/app.jar /app/app.jar

EXPOSE 8080 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]

