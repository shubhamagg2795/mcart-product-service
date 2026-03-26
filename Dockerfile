FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY app.jar app.jar
RUN addgroup -S mcart && adduser -S mcart -G mcart
USER mcart
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx150m", "-Xms64m", "-XX:+UseG1GC", "-XX:MaxMetaspaceSize=96m", "-jar", "app.jar"]