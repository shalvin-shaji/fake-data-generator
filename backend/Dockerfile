FROM maven:3.9-amazoncorretto-24 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

FROM amazoncorretto:24-alpine
WORKDIR /app
COPY --from=build /app/target/fake-data-generator-0.1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
