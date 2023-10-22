# jdk17 image start
FROM openjdk:17-jdk
# 인자 정리 -Jar
ARG JAR_FILE=./build/libs/bidpanda-0.0.1-SNAPSHOT.jar
# jar file copy
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]