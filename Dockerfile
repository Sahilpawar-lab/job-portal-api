FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw && ./mvnw -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/job-portal-api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 10000
ENV PORT=10000

ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]
