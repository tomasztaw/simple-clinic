FROM eclipse-temurin:17

#COPY build/libs/*.jar app.jar
COPY build/libs/*.jar /app.jar
COPY src/main/resources/desc /app/desc

WORKDIR /app

ENV RUNNING_IN_DOCKER=true

ENTRYPOINT ["java","-jar","/app.jar"]