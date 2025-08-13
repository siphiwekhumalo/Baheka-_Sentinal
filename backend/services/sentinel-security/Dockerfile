FROM eclipse-temurin:17-jre-alpine
RUN apk update && apk upgrade && apk add --no-cache curl tzdata && rm -rf /var/cache/apk/*
RUN addgroup -g 1001 -S spring && adduser -S spring -u 1001 -G spring
WORKDIR /app
COPY target/*.jar app.jar
RUN mkdir -p /app/logs && chown -R spring:spring /app
USER spring
EXPOSE 8084
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8084/actuator/health || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC -XX:+UseStringDeduplication"
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
