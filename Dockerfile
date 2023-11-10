FROM openjdk:17-oracle
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /home/server.jar
ENTRYPOINT ["java","-jar","/home/server.jar"]