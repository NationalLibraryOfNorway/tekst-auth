FROM harbor.nb.no/library/eclipse-temurin:21-jdk-alpine
COPY target/tekst-auth.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java","-jar","/app.jar"]
