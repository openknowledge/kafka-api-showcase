FROM openjdk:8-jre-alpine

ARG JAR_FILE=kafka-streams.jar
COPY target/${JAR_FILE} /opt/application.jar

ENV JAVA_OPTS="-Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true"

EXPOSE 9080
ENTRYPOINT exec java $JAVA_OPTS -jar /opt/application.jar