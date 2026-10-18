# Customer Order Tracking System - container image
# Build the WAR first:  mvn clean package -DskipTests
FROM eclipse-temurin:17-jre

# curl is used by the health check
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# run the application as a non-root user
RUN useradd --system --uid 1001 appuser
WORKDIR /app
COPY target/order-tracker.war /app/app.war
USER appuser

# the executable Spring Boot WAR listens on 8080 inside the container
EXPOSE 8080
ENV APP_ENV=docker

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fs http://localhost:8080/actuator/health | grep -q UP || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.war"]
