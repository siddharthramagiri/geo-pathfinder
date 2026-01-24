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
# Stage 2: Run the application
# ===============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar
COPY --from=builder /app/target/*.jar app.jar

# Copy OSM data into container
COPY data/telangana-latest.osm.pbf /data/telangana.osm.pbf

# GraphHopper cache (Render persistent disk)
VOLUME ["/graph-cache"]

# JVM tuning (Render-friendly)
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
