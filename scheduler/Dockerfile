FROM openjdk:8-jdk-alpine
ARG JAR_FILE=./build/libs/baljc-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","-Dspring.profiles.active=prod","/app.jar"]
