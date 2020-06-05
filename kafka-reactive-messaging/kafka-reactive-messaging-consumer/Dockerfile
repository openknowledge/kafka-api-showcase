FROM openjdk:8-jre-alpine

ARG JAR_FILE
COPY target/${JAR_FILE} /opt/kafka-reactive-messaging-consumer.jar

EXPOSE 9080

ENTRYPOINT exec java -Djava.net.preferIPv4Stack=true -Djava.net.preferIPv4Addresses=true -jar /opt/kafka-reactive-messaging-consumer.jar