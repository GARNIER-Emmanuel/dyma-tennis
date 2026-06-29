FROM maven:3.8.5-eclipse-temurin-17 AS build
COPY pom.xml .
COPY src/ src/
RUN mvn -f pom.xml -Pprod clean package

FROM eclipse-temurin:17-jre AS run
RUN useradd dyma
USER dyma
COPY --from=build /target/dyma-tennis.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar" ]
