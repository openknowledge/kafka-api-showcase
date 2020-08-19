# Kafka API Showcase

This is a showcase demonstrating different ways to connect and communicate with Apache Kafka. It contains sample Java Enterprise based message producer and consumer applications using [Microprofile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging), Kafka Resource Adapter or Kafka Core APIS like [Kafka Streams](https://kafka.apache.org/documentation/streams/) to send or receive messages from the Kafka broker. 

Software requirements to run the samples are `maven`, `openjdk-1.8` (or any other 1.8 JDK) and `docker`.

## How to run

Before running the application it needs to be compiled and packaged using Maven. It creates the required docker images and can be run together with Apache Kafka via `docker-compose`:

```shell script
$ mvn clean package
$ docker-compose up
```

When changing code you must re-run the package process and start docker-compose with the additional build parameter to
ensure that both the application and the Docker image is up-to-date:
```shell script
$ mvn clean package
$ docker-compose up --build
```

Wait for a message log similar to this:

> kafka-reactive-messaging-consumer_1  | [AUDIT   ] CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 35.800 seconds.

> kafka-reactive-messaging-producer_1  | [AUDIT   ] CWWKF0011I: The defaultServer server is ready to run a smarter planet. The defaultServer server started in 36.693 seconds.

If everything worked the producers start sending messages automatically every 2s. Additionally a custom message can be send by by calling on of the following URLs: 
* `http://localhost:9080/kafka-producer/api/messages?msg=<custom message>` 
* `http://localhost:9081/kafka-ra-producer/api/messages?msg=<custom message>` 
* `http://localhost:9082/kafka-reactive-messaging-producer/api/messages?msg=<custom message>`

**_HINT: Starting all samples requires many resources, that's why we recommend to select a subset of all samples. Therefor each sample module provides its own docker-compose file that starts Kafka, Zookeeper and the corresponding Docker container._**

## Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep <id of the container>
```

If there are containers remaining although the application has been stopped you can remove them:

````shell script
$ docker rm <ids of the containers>
````

## Samples

### Kafka Core APIs

Kafka provides five core APIs which enables clients to send, read or stream data and connect to or manage the Kafka broker.

The sample contains three modules  
* `kafka-producer` containing a Kafka producer using the Producer API
* `kafka-consumer` containing a Kafka consumer using the Consumer API
* `kafka-streams` containing a Kafka Streams consumer using the Streams API

For further descriptions concerning Kafka Core APIs and the sample read [here](kafka-core-apis/README.md).

### Kafka Resource Adapter (by Payara)

Payara provides a [resource adapter](https://github.com/payara/Cloud-Connectors/tree/master/Kafka) for Apache Kafka which enables using Message Driven Beans to consume Kafka records in a Java Enterprise application.

The sample contains two modules using the Payara Kafka resource adapter to communicate with the Kafka broker  
* `kafka-ra-producer` containing a Kafka producer
* `kafka-ra-consumer` containing a Kafka consumer

For further descriptions concerning the Payara resource adapter and the sample read [here](kafka-ra/README.md).

### Microprofile Reactive Messaging

[Microprofile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging) is a specification providing asynchronous messaging support based on Reactive Streams for MicroProfile. It's implementations (e.g. [Smallrye Reactive Messaging](https://smallrye.io/smallrye-reactive-messaging/) which is used by Quarkus) supports Apache Kafka, AMQP, MQTT, JMS, WebSocket and other messaging technologies. 

Reactive Messaging can handle messages generated from within the application but also interact with remote brokers. Reactive Messaging Connectors interacts with these remote brokers to retrieve messages and send messages using various protocols and technology. Each connector is dedicated to a specific technology. For example, a Kafka Connector is responsible to interact with Kafka, while a MQTT Connector is responsible for MQTT interactions.

The sample contains two modules using MP Reactive Messaging to communicate with the Kafka broker  
* `kafka-reactive-messaging-producer` containing a Kafka producer
* `kafka-reactive-messaging-consumer` containing a Kafka consumer

For further descriptions concerning Microprofile Reactive Messaging and the sample read [here](kafka-reactive-messaging/README.md).
