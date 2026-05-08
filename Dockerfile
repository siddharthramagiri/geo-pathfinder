# ===============================
# Stage 1: Build the application
# ===============================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app
# Cache dependencies
COPY pom.xml .
RUN mvn -q -e -B dependency:go-offline
# Copy source and build
COPY src ./src
RUN mvn -q -DskipTests package


# ===============================
# Stage 2: Pre-build the graph
# (heavy memory OK here — build time)
# ===============================
FROM eclipse-temurin:17-jre AS graph-builder
WORKDIR /
COPY --from=builder /app/target/*.jar app.jar
COPY data/telangana-latest.osm.pbf /data/telangana-latest.osm.pbf
RUN ls -lh /data/ && \
    java -Xms128m -Xmx900m -XX:+UseSerialGC \
    -DOSM_FILE_PATH=/data/telangana-latest.osm.pbf \
    -DGRAPH_CACHE_PATH=/graph-cache \
    -DDATASOURCE_URL=h2:mem:dummy \
    -DDATASOURCE_USERNAME=sa \
    -DDATASOURCE_PASSWORD= \
    -jar app.jar --import-graph-only


# ===============================
# Stage 3: Lean runtime image
# ===============================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
COPY --from=graph-builder /graph-cache /graph-cache

RUN ls -lh /graph-cache/ && echo "Graph cache OK"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
