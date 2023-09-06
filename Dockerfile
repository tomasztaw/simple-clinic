FROM eclipse-temurin:17

COPY build/libs/*.jar /app.jar
COPY src/main/resources/ /app/resources/
#kopiuje wszystko do kontenera
COPY . .

WORKDIR /app

ENV RUNNING_IN_DOCKER=true

ENTRYPOINT ["java","-jar","/app.jar"]