# Use a very small and efficient JRE image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy only the built Spring Boot JAR
COPY target/pathfinder-0.0.1-SNAPSHOT.jar app.jar

# Copy the prebuilt GraphHopper cache (built locally once)
#COPY customGraph.ser /app/customGraph.ser
COPY data/telangana-latest.osm.pbf /app/data/
COPY graph-cache/ /app/graph-cache/

# (Optional) Copy other small static files if your app needs them
# COPY src/main/resources/static /app/static

# Set environment variables
ENV SERVER_PORT=8080
ENV SERVER_ADDRESS=0.0.0.0

# Limit JVM memory so it fits Render's 512 MB cap
#ENV JAVA_OPTS="-Xmx300m -Xms200m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV JAVA_OPTS="-Xmx480m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=90.0"

# Optimize startup & disable unused Spring features
ENV SPRING_MAIN_LAZY_INITIALIZATION=true
ENV SPRING_JMX_ENABLED=false
ENV SPRING_MAIN_BANNER_MODE=off

# Expose port
EXPOSE 8080

# Clean up unnecessary files to shrink image
RUN rm -rf /var/cache/apk/* && \
    find /app/graph-cache -type f -name "*.tmp" -delete

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
