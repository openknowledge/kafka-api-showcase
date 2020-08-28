# Kafka API Showcase - MicroProfile Reactive Messaging

[MicroProfile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging) is a specification providing 
asynchronous messaging support based on Reactive Streams for MicroProfile. It's implementations (e.g. 
[Smallrye Reactive Messaging](https://smallrye.io/smallrye-reactive-messaging/) which is used by Quarkus) supports Apache Kafka, AMQP, 
MQTT, JMS, WebSocket and other messaging technologies. 

Reactive Messaging can handle messages generated from within the application but also interact with remote brokers. Reactive Messaging 
Connectors interacts with these remote brokers to retrieve messages and send messages using various protocols and technology. Each 
connector is dedicated to a specific technology. For example, a Kafka Connector is responsible to interact with Kafka, while a MQTT 
Connector is responsible for MQTT interactions.

The showcase contains two Maven modules using MP Reactive Messaging to communicate with the Kafka broker 
* `kafka-reactive-messaging-producer` containing a Kafka producer using MP Reactive Messaging
* `kafka-reactive-messaging-consumer` containing a Kafka consumer using MP Reactive Messaging


## How to run

#### Step 1: Create docker images 

Software requirements to run the sample are `maven`, `openjdk-8` (or any other JDK 8) and `docker`. 
When running the Maven lifecycle it will create the war packages and use the `liberty-maven-plugin` to create a runnable JARs (fat JAR) 
which contains the application and the Open Liberty application server. The fat JARs will be copied into a Docker images using Spotify's 
`dockerfile-maven-plugin` during the package phase.

Before running the application it needs to be compiled and packaged using `Maven`. It creates the runnable JARs and Docker images.

```shell script
$ mvn clean package
```

#### Step 2: Start docker images

After creating the docker images you can start the containers. The `docker-compose.yml` file defines the containers required to run the 
showcase.  

* the Apache Zookeeper application provided by Confluent Inc.
* the Apache Kafka broker provided by Confluent Inc.
* the custom Java EE application `kafka-reactive-messaging-producer` which send messages to the Kafka topic
* the custom Java EE application `kafka-reactive-messaging-consumer` which consumes messages from the Kafka topic

To start the containers you have to run `docker-compose`:

```shell script
$ docker-compose up
```

#### Step 3: Produce and consume messages

There are two ways to test the communication between the `kafka-reactive-messaging-producer` and the `kafka-reactive-messaging-consumer` 
application. 

1) The custom application `kafka-reactive-messaging-producer` contains a message generator that generates and sends a new message every 
two seconds. The receipt and successful processing of the message can be traced in the log output of the `kafka-reactive-messaging-consumer` 
application.

2) In addition to the message generator, the application provides a REST API that can be used to create and send your own messages. 

To send a custom message you have to send the following GET request:

```shell script
$ curl -X GET http://localhost:9082/kafka-reactive-messaging-producer/api/messages?msg=<custom message>
```


### Resolving issues

Sometimes it may happen that the containers did not stop as expected when trying to stop the pipeline early. This may
result in running containers although they should have been stopped and removed. To detect them you need to check
Docker:

```shell script
$ docker ps -a | grep <id of the container>
```

If there are containers remaining although the application has been stopped you can remove them:

```shell script
$ docker rm <ids of the containers>
```


## Features

### MicroProfile Reactive Messaging 

The Kafka connector adds support for Kafka to Reactive Messaging. With it you can receive Kafka Records as well as write message into Kafka. 
To use the Kafka Connector for MicroProfile Reactive Messaging you have to add the following dependencies to your `pom.xml`:

**pom.xml**
```
<dependency>
  <groupId>org.eclipse.microprofile.reactive.messaging</groupId>
  <artifactId>microprofile-reactive-messaging-api</artifactId>
  <version>${version.microprofile-reactive-messaging}</version>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>io.reactivex.rxjava3</groupId>
  <artifactId>rxjava</artifactId>
  <version>${version.rxjava}</version>
</dependency>
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <version>${version.kafka-client}</version>
</dependency>
```

_HINT: The example shows the dependencies requirement for Open Liberty. Open Liberty provides a Kafka connector named `liberty-kafka`.  If
you prefer using a different microservice framework (e.g. Quarkus) please check the documentation if special dependencies are provided._

In addition to that you have to activate the MicroProfile in your `server.xml`:

**server.xml**
```
<featureManager>
  ...
  <feature>mpReactiveMessaging-1.0</feature>
</featureManager>
```

#### Sending And Receiving Kafka Records

The Kafka Connector sends and retrieves Kafka Records from Kafka Brokers and maps each of them to Reactive Messaging `Messages` and vice
versa.

For the following example a Kafka broker, which is accessible at `kafka:9092` and a topic named `custom-messages` are used.


##### Sending Kafka Records

To send messages to the topic `custom-messages` an outgoing channel has to be configured in the `microprofile-config.properties`:

**microprofile-config.properties**
```
mp.messaging.connector.liberty-kafka.bootstrap.servers=kafka:9092                                                                  (1)

mp.messaging.outgoing.message.connector=liberty-kafka                                                                              (2)
mp.messaging.outgoing.message.topic=custom-messages                                                                                (3)
mp.messaging.outgoing.message.client.id=kafka-reactive-messaging-producer                                                          (4)
mp.messaging.outgoing.message.key.serializer=org.apache.kafka.common.serialization.StringSerializer                                (5)
mp.messaging.outgoing.message.value.serializer=de.openknowledge.showcase.kafka.reactive.messaging.producer.CustomMessageSerializer (6)
```

To send messages to an outgoing channel a `org.reactivestreams.Publisher` can be used. Therefore a **non-argument** method annotated with
the annotation `@Outgoing("<channel-name>")` has to be provided. 

While generating a continuous stream of messages with a `io.reactivex.rxjava3.core.Flowable`, sending a single message seams to become a
bigger problem. You cannot pass an argument to a method providing a stream without any argument allowed. Even if combining imperative
programming and reactive seams to be tricky, there are solutions available. RxJava provides a `io.reactivex.rxjava3.core.FlowableEmitter`
which enables to add single messages to a reactive stream. 

For further information about _reactive streams_ and _RxJava_ please check the corresponding [documentation](https://github.com/ReactiveX/RxJava).

**KafkaReactiveMessagingProducer (send a single message)**
```
package de.openknowledge.showcase.kafka.reactive.messaging.producer;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

/**
 * Kafka producer that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties.
 */
@ApplicationScoped
public class KafkaReactiveMessagingProducer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingProducer.class);

  private FlowableEmitter<CustomMessage> emitter;

  public void send(final CustomMessage message) {
    LOG.info("Send message {}", message);
    emitter.onNext(message);
  }

  @Outgoing("message") // has to be equal to outgoing channel-name in microprofile-config.properties
  public Publisher<CustomMessage> process() {
    return Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER);
  }
}
```

**KafkaReactiveMessagingGenerator (generate and send message every 2s)**
```
package de.openknowledge.showcase.kafka.reactive.messaging.producer;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Kafka message generator that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties.
 */
@ApplicationScoped
public class KafkaReactiveMessagingGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingGenerator.class);

  @Outgoing("message") // has to be equal to outgoing channel-name in microprofile-config.properties
  public Flowable<CustomMessage> generate() {
    return Flowable
        .interval(2, TimeUnit.SECONDS)
        .doOnEach(notification -> LOG.info("Send generated message {}", notification.getValue()))
        .map(tick -> new CustomMessage(String.format("Message %d", tick)));
  }
}
```

Kafka Records are sent as byte arrays, which requires to serialize messages. Message serialization is handled by the underlying Kafka
Client and has to be configured in the `microprofile-config.properties`.

* `mp.messaging.outgoing.[channel-name].key.serializer` to configure the key serializer (optional, default to String)
* `mp.messaging.outgoing.[channel-name].value.serializer` to configure the value serializer (mandatory)

To send as JSON, a custom serializer has to be provided.

```
mp.messaging.outgoing.message.key.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.message.value.serializer=de.openknowledge.showcase.kafka.reactive.messaging.producer.CustomMessageSerializer
```

**CustomMessageSerializer**
```
package de.openknowledge.showcase.kafka.reactive.messaging.producer;

import org.apache.kafka.common.serialization.Serializer;

import javax.json.bind.JsonbBuilder;

/**
 * JSON serializer for the DTO {@link CustomMessage}.
 */
public class CustomMessageSerializer implements Serializer<Object> {

  @Override
  public byte[] serialize(final String topic, final Object data) {
    return JsonbBuilder.create().toJson(data).getBytes();
  }
}
```  


##### Receiving Kafka Records

To retrieve messages from the topic `custom-messages`, a incoming channel has to be configured in the `microprofile-config.properties`:

**microprofile-config.properties**
```
mp.messaging.connector.liberty-kafka.bootstrap.servers=kafka:9092                                                                       (1)

mp.messaging.incoming.message.connector=liberty-kafka                                                                                   (2)
mp.messaging.incoming.message.topic=custom-messages                                                                                     (3)
mp.messaging.incoming.message.client.id=kafka-reactive-messaging-consumer                                                               (4)
mp.messaging.incoming.message.group.id=kafka-reactive-messaging-consumer                                                                (5)
mp.messaging.incoming.message.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer                                 (6)
mp.messaging.incoming.message.value.deserializer=de.openknowledge.showcase.kafka.reactive.messaging.consumer.CustomMessageDeserializer  (7)
```

To receive messages from a incoming channel a reactive message consumer has to be implemented. Therefore a method annotated with the
annotation `@Incoming("<channel-name>")` and the expected message as a parameter has to be provided.
   
**KafkaReactiveMessagingConsumer**
```
package de.openknowledge.showcase.kafka.reactive.messaging.consumer;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

/**
 * Kafka consumer that receives messages from a Kafka topic. The topic is configured in the microprofile-config.properties.
 */
@ApplicationScoped
public class KafkaReactiveMessagingConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingConsumer.class);

  @Incoming("message") // has to be equal to incoming channel-name in microprofile-config.properties
  public void onMessage(final CustomMessage message) {
    try {
      LOG.info("Received message {}", message);
    } catch (IllegalArgumentException e) {
      LOG.error(e.getMessage(), e);
    }
  }
}

```

Kafka Records are sent as byte arrays, which requires to deserialize messages. Message deserialization is handled by the underlying Kafka 
Client and has to be configured in the `microprofile-config.properties`.

* `mp.messaging.incoming.[channel-name].key.serializer` to configure the key serializer (optional, default to String)
* `mp.messaging.incoming.[channel-name].value.serializer` to configure the value serializer (mandatory)

To receive message in JSON, a custom deserializer has to be provided.

```
mp.messaging.incoming.message.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.incoming.message.value.deserializer=de.openknowledge.showcase.kafka.reactive.messaging.consumer.CustomMessageDeserializer
```

**CustomMessageSerializer**
```
package de.openknowledge.showcase.kafka.reactive.messaging.consumer;

import org.apache.kafka.common.serialization.Deserializer;

import javax.json.bind.JsonbBuilder;

/**
 * JSON deserializer for the DTO {@link CustomMessage}.
 */
public class CustomMessageDeserializer implements Deserializer<CustomMessage> {

  @Override
  public CustomMessage deserialize(String topic, byte[] data) {
    if (data == null) {
      return null;
    }
    return JsonbBuilder.create().fromJson(new String(data), CustomMessage.class);
  }
}
``` 
