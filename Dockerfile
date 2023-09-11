FROM eclipse-temurin:17

WORKDIR /app

#COPY build/libs/*.jar /app.jar
COPY build/libs/simple_clinic.jar /app.jar
COPY src/main/resources/ /app/resources/
#kopiuje wszystko do kontenera
COPY . .


ENV RUNNING_IN_DOCKER=true

ENTRYPOINT ["java","-jar","/app.jar"]