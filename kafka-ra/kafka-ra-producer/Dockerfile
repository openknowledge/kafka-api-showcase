FROM jboss/wildfly:19.0.0.Final

ARG WAR_FILE
ARG KAFKA_RAR=kafka-rar-0.5.0.rar

COPY target/$WAR_FILE $JBOSS_HOME/standalone/deployments/$WAR_FILE

COPY wildfly/$KAFKA_RAR $JBOSS_HOME/standalone/deployments/$KAFKA_RAR
COPY wildfly/standalone.xml $JBOSS_HOME/standalone/configuration/standalone.xml
