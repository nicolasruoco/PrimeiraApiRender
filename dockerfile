FROM maven:4.0.0-openjdk-17 as build
COPY . .
RUN mvn clean package -Dskiptests

FROM openjdk-17-jdk-slim
COPY --from-build /target/ApiData-0.0.1 -SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "jar", "app.jar"]
