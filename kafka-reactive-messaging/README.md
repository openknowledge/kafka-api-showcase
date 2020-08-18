# Microprofile Reactive Messaging Sample

[Microprofile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging) is a specification providing asynchronous messaging support based on Reactive Streams for MicroProfile. It's implementations (e.g. [Smallrye Reactive Messaging](https://smallrye.io/smallrye-reactive-messaging/) which is used by Quarkus) supports Apache Kafka, AMQP, MQTT, JMS, WebSocket and other messaging technologies. 

Reactive Messaging can handle messages generated from within the application but also interact with remote brokers. Reactive Messaging Connectors interacts with these remote brokers to retrieve messages and send messages using various protocols and technology. Each connector is dedicated to a specific technology. For example, a Kafka Connector is responsible to interact with Kafka, while a MQTT Connector is responsible for MQTT interactions.

The sample contains two Maven modules using MP Reactive Messaging to communicate with the Kafka broker 
* `kafka-reactive-messaging-producer` containing a Kafka producer
* `kafka-reactive-messaging-consumer` containing a Kafka consumer

## Reactive Messaging

_Please note the following documentation is provided by the [MicroProfile Reactive Messaging Specification](https://download.eclipse.org/microprofile/microprofile-reactive-messaging-1.0/microprofile-reactive-messaging-spec.html#_microprofile_reactive_messaging)_   

State-of-the-art systems must be able to adapt themselves to emerging needs and requirements, such as market change and user expectations but also fluctuating load and inevitable failures. Leading-edge applications are capable of dynamic and adaptive capabilities aiming to provide responsive systems. While microservices aim to offer this agility, HTTP-based connecting tissue tends to fail to provide the required runtime adaptations, especially when facing failures.

Asynchronous communication allows temporal decoupling of services in a microservice based architecture. This temporal decoupling is necessary if communication is to be enabled to occur regardless of when the parties involved in the communication are running, whether they are loaded or overloaded, and whether they are successfully processing messages or failing.

In contrast, synchronous communication couples services together, binding their uptime, failure, and handling of the load to each other. In a chain of synchronous interactions, the entire conversation can only be successful if all parties in the chain are responsive - if they are all running, processing messages successfully, and not overloaded. If just one party has a problem, all effectively exhibit the same problem. Therefore, systems of microservices relying on synchronous HTTP or relying on synchronous protocols tend to be fragile, and failures limit their availability. Indeed, in a microservice-based architecture, temporal coupling results in a fragile system, with resilience and scaling properties that are worse than a monolith, hence, it is essential for microservice based architectures to embrace asynchronous communication as much as possible.

The role of the MicroProfile Reactive Messaging specification is to deliver a way to build systems of microservices promoting both location transparency and temporal decoupling, enforcing asynchronous communication between the different parts of the system.

### Rationale

#### Reactive Systems

[Reactive Systems](https://www.reactivemanifesto.org/) provide an architecture style to deliver responsive systems. By infusing asynchronous messaging passing at the core of the system, applications enforcing the reactive system’s characteristics are inherently resilient and become more elastic by scaling up and down the number of message consumers.

Microservices as part of reactive systems interact using messages. The location and temporal decoupling, promoted by this interaction mechanism, enable numerous benefits such as:

* Better failure handling as the temporal decoupling enables message brokers to resend or reroute messages in the case of remote service failures.
* Improved elasticity as under fluctuating load the system can decide to scale up and down some of the microservices.
* The ability to introduce new features more easily as components are more loosely coupled by receiving and publishing messages.

The MicroProfile Reactive Messaging specification aims to deliver applications embracing the characteristics of reactive systems.


#### On JMS and Message Driven Beans

Java EE offers JMS and Message Driven Beans for handling asynchronous communication; however, there are some problems with these specifications:

* Both are designed for a technology landscape where messaging was typically on the edge of the system to hand control of a transaction from one system to another; consequently, these technologies can appear heavyweight when used between microservices.
* It is assumed in their design that consistency is handled using distributed transactions. However, many message brokers, popular in microservice deployments, such as Apache Kafka, Amazon Kinesis and Azure Event Hubs, do not support XA transactions, rather, message acknowledgment is handled using offsets with at least once delivery guarantees.
* They do not have support for asynchronous IO; it is assumed that message processing is done on a single thread, however, many modern specifications are moving to asynchronous IO.

Hence a lighter weight, reactive solution to messaging is desirable for MicroProfile to ensure microservices written using MicroProfile are able to meet the demands required by the architecture.


#### Use cases

MicroProfile Reactive Messaging aims to provide a way to connect event-driven microservices. The key characteristics of the specification make it versatile and suitable for building different types of architecture and applications.

First, asynchronous interactions with different services and resources can be implemented using Reactive Messaging. Typically, asynchronous database drivers can be used in conjunction with Reactive Messaging to read and write into a data store in a non-blocking and asynchronous manner.

When building microservices, the CQRS and event-sourcing patterns provide an answer to the data sharing between microservices. Reactive Messaging can also be used as the foundation to CQRS and Event-Sourcing mechanism, as these patterns embrace message-passing as core communication pattern.

IOT applications, dealing with events from various devices, and data streaming applications can also be implemented using Reactive Messaging. The application receives events or messages, process them, transform them, and may forward them to another microservices. It allows for more fluid architecture for building data-centric applications.


### Architecture

The Reactive Messaging specification defines a development model for declaring CDI beans producing, consuming and processing messages. The communication between these components uses Reactive Streams.

This specification relies on Eclipse MicroProfile Reactive Streams Operators and CDI.


#### Overall architecture

An application using Reactive Messaging is composed of CDI beans consuming, producing and processing messages.

These messages can be wholly internal to the application or can be sent and received via different message brokers.

Application’s beans contain methods annotated with `@Incoming` and `@Outgoing` annotations. A method with an `@Incoming` annotation consumes messages from a _channel_. A method with an `@Outgoing` annotation publishes messages to a _channel_. A method with both an `@Incoming` and an @Outgoing annotation is a message processor, it consumes messages from a _channel_, does some transformation to them, and publishes messages to another _channel_.


#### Channel

A _channel_ is a name indicating which source or destination of _messages_ is used. Channels are opaque Strings.

There are two types of channels:
* Internal channels are local to the application. They allows implementing multi-step processing where several beans from the same application form a chain of processing.
* Channels can be connected to remote brokers or various message transport layers such as Apache Kafka or to an AMQP broker. These channels are managed by connectors.


#### Message

At the core of the Reactive Messaging specification is the concept of _message_. A message is an envelope wrapping a _payload_. A message is sent to a specific channel and, when received and processed successfully, acknowledged.

Reactive Messaging application components are addressable recipients which await the arrival of messages on a channel and react to them, otherwise lying dormant.

Messages are represented by the org.eclipse.microprofile.reactive.messaging.Message` class. This interface is intentionally kept minimal. The aim is that connectors will provide their own implementations with additional metadata that is relevant to that connector. For instance, a KafkaMessage` would provide access to the topic and partition.

The `org.eclipse.microprofile.reactive.messaging.Message#getPayload method retrieves the wrapped payload. The org.eclipse.microprofile.reactive.messaging.Message#ack` method acknowledges the message. Note that the ack method is asynchronous as acknowledgement is generally an asynchronous process.

Plain messages are created using:

* `org.eclipse.microprofile.reactive.messaging.Message#of(T)` - wraps the given payload, no acknowledgement
* `org.eclipse.microprofile.reactive.messaging.Message#of(T, java.util.function.Supplier<java.util.concurrent.CompletionStage<java.lang.Void>>)` - wraps the given payload and provides the acknowledgment logic


##### Message consumption with @Incoming

The `org.eclipse.microprofile.reactive.messaging.Incoming` annotation is used on a method from a CDI bean to indicate that the method consumes messages from the specified channel:

```
@Incoming("<channel-name>")                                            // (1)
public CompletionStage<Void> consume(Message<String> message) {        // (2)
  return message.ack();
}
```

1. my-channel is the channel
2. the method is called for every message sent to the my-channel channel

Reactive Messaging supports various forms of method signatures. This is detailed in the next section.

Remember that Reactive Messaging interactions are assembled from Reactive Streams. A method annotated with `@Incoming` is a Reactive Streams subscriber and so consumes messages that fit with the message signature and its annotations. Note that the handling of the Reactive Streams protocol, such as subscriptions and back pressure, is managed by the Reactive Messaging implementation. The MicroProfile Reactive Streams specification used as a foundation for this version of Reactive Messaging is a single subscriber model where a stream `Publisher` is connected to a single Subscriber which controls back pressure. This implies that a Reactive Messaging channel should appear in a single `@Incoming` annotation. The annotation of more than one `@Incoming` method to be associated with the same channel is not supported and will cause an error during deployment.

From the user perspective, whether the incoming messages comes from co-located beans or a remote message broker is transparent. However, the user may decide to consume a specific subclass of `Message` (e.g. `KafkaMessage` in the following example) if the user is aware of this characteristic:

```
@Incoming("<channel-name>")
public CompletionStage<Void> consume(KafkaMessage<String> message) {    // (1)
  return message.ack();
}
```
Explicit consumption of a `KafkaMessage`


##### Message production with @Outgoing

The `org.eclipse.microprofile.reactive.messaging.Outgoing` annotation is used to annotate a method from a CDI bean to indicate that the method publishes messages to a specified channel:

```
@Outgoing("<channel-name>")                                    // (1)
public Message<String> publish() {                             // (2)
  return Message.of("hello");                                  // (3)
}
```

1. `my-channel` is the targeted channel
2. the method is called for every consumer request
3. you can create a plain `org.eclipse.microprofile.reactive.messaging.Message` using `org.eclipse.microprofile.reactive.messaging.Message#of(T)`

Reactive Messaging supports various forms of method signatures. This is detailed in the next section.

A method annotated with `@Outgoing` is a Reactive Streams publisher and so publishes messages according to the requests it receives. The downstream `@Incoming` method or outgoing connector with a matching channel name will be linked to this publisher. Only a single method can be annotated with `@Outgoing` for a particular channel name. Having the same channel name in more than one `@Outgoing` annotated method is not supported and will result in an error during deployment.


###### Method consuming and producing

A method can combine the `@Incoming` and `@Outgoing` annotation and will then act as a Reactive Streams processor:

```
@Incoming("<incoming-channel-name>")                            // (1)
@Outgoing("<outgoing-channel-name>")                            // (2)
public Message<String> process(Message<String> message) {
  return Message.of(message.getPayload().toUpperCase());
}
```

1. The incoming channel
2. The outgoing channel

Having the same channel appear in the `@Outgoing` and `@Incoming` annotations of a processor is not supported and will result in an error during deployment.


#### Connectors

The application can receive and forward messages from various message brokers or transport layers. For instance, an application can be connected to a Kafka cluster, an AMQP broker or an MQTT server.

Reactive Messaging Connectors are extensions managing the communication with a specific transport technology. They are responsible for mapping a specific channel to remote sink or source of messages. This mapping is configured in the application configuration. Note that an implementation may provide various ways to configure the mapping, but support for MicroProfile Config as a configuration source is mandatory.

Connector implementations are associated with a name corresponding to a messaging transport, such as Apache Kafka, Amazon Kinesis, RabbitMQ or Apache ActiveMQ. For instance, an hypothetical Kafka connector could be associated with the following name: acme.kafka. This name is indicated using a qualifier on the connector implementation.

The user can associate a channel with this connector using the associated name:

```
mp.messaging.incoming.my-kafka-topic.connector=acme.kafka  (1)
```
1. the name associated with the connector.

The configuration format is detailed later in this document.

The Reactive Messaging implementation is responsible for finding the connector implementation associated with the given name in the user configuration. If the connector cannot be found, the deployment of the application must be failed.

The Reactive Messaging specification provides an SPI to implement connectors.


#### Message stream operation

Message stream operation occurs according to the principles of reactive programming. The back pressure mechanism of reactive streams means that a publisher will not send data to a subscriber unless there are outstanding subscriber requests. This implies that data flow along the stream is enabled by the first request for data received by the publisher. For methods that are annotated with `@Incoming` and `@Outgoing` this data flow control is handled automatically by the underlying system which will call the `@Incoming` and `@Outgoing` methods as appropriate.

> Although `@Incoming` and `@Outgoing` methods remain callable from Java code, calling them directly will not affect the reactive streams they are associated with. For example, calling an `@Outgoing` annotated method from user code will not post a message on a message queue and calling an `@Incoming` method cannot be used to read a message. Enabling this would bypass the automatic back pressure mechanism that is one of the benefits of the specification. The `@Incoming` and `@Outgoing` method annotations are used to declaratively define the stream which is then run by the implementation of MicroProfile Reactive Messaging without the user’s code needing to handle concerns such as subscriptions or flow control within the stream.


## Connectors

Connectors can retrieve messages from a remote broker (inbound) or send messages to a remove broker (outbound). A connector can, of course, implement both directions.

Inbound connectors are responsible for:
1. Getting messages from the remote broker,
2. Creating a Reactive Messaging Message associated with the retrieved message. 
3. Potentially associating technical metadata with the message. This includes unmarshalling the payload.
4. Associating a acknowledgement callback to acknowledge the incoming message when the Reactive Messaging message is acknowledged.

Outbound connectors are responsible for:
1. Receiving Reactive Messaging Message and transform it into a structure understand by the remote broker. This includes marshalling the payload.
2. If the Message contains outbound metadata (metadata set during the processing to influence the outbound structure and routing), taking them into account.
3. Sending the message to the remote broker.
4. Acknowledging the Reactive Messaging Message when the broker has accepted / acknowledged the message.

Applications needs to configure the connector used by expressing which channel is managed by which connector. Non-mapped channels are local / in-memory.

A connector is configured at `src/main/resources/META-INF/microprofile-config.properties` with a set of properties structured as follows:

```
mp.messaging.connector.[connector-name].[attribute]=[value]
```
```
mp.messaging.[incoming|outgoing].[channel-name].[attribute]=[value]
```

Each channel (both incoming and outgoing) is configured individually.

The `[incoming|outgoing]` segment indicates the direction.
* an incoming channel consumes data from a message broker or something producing data. It’s an inbound interaction. It can be connected to a method annotated with an `@Incoming` using the same channel name.
* an outgoing consumes data from the application and forward it to a message broker or something consuming data. It’s an outbound interaction. It can be connected to a method annotated with an `@Outgoing` using the same channel name.

The `[channel-name]` is the name of the channel.

The `[connector-name]` is the name of the connector implementation (e.g. `liberty-kafka` or `smallrye-kafka`).

The `[attribute]=[value]` sets a specific connector attribute to the given value. Attributes depends on the sued connector. So, refer to the connector documentation to check the supported attributes.

The `connector` attribute must be set for each mapped channel, and indicates the name of the connector responsible for the channel.

The `bootstrap.server` attribute can be set for each mapped channel individually or globally for all channels.

Here is an example of a channel using an Kafka connector for Open Liberty, consuming data from a Kafka broker.

```
mp.messaging.connector.liberty-kafka.bootstrap.servers=kafka:9092

mp.messaging.incoming.message.connector=liberty-kafka
mp.messaging.incoming.message.topic=custom-messages
mp.messaging.incoming.message.client.id=kafka-reactive-messaging-consumer
mp.messaging.incoming.message.group.id=kafka-reactive-messaging-consumer
mp.messaging.incoming.message.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.incoming.message.value.deserializer=de.openknowledge.showcase.kafka.reactive.messaging.consumer.CustomMessageDeserializer
```


## Kafka & MicroProfile Reactive Messaging 

The Kafka connector adds support for Kafka to Reactive Messaging. With it you can receive Kafka Records as well as write message into Kafka.

[Apache Kafka](https://kafka.apache.org/) is a popular distributed streaming platform which enables you:

* publishing and subscribing to streams of records, similar to a message queue or enterprise messaging system.
* Store streams of records in a fault-tolerant durable way.
* Process streams of records as they occur.

The Kafka cluster stores streams of records in categories called topics. Each record consists of a key, a value, and a timestamp.

For more details about Kafka, check the [documentation](https://kafka.apache.org/intro).


### Using the Kafka Connector

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

_HINT: The example shows the dependencies requirement for Open Liberty. Open Liberty provides a Kafka connector named `liberty-kafka`.  If you prefer using a different microservice framework (e.g. Quarkus) please check the documentation if special dependencies are provided._

In addition to that you have to activate the MicroProfile in your `server.xml`:

**server.xml**
```
<featureManager>
  ...
  <feature>mpReactiveMessaging-1.0</feature>
</featureManager>
```


### Sending And Receiving Kafka Records

The Kafka Connector sends and retrieves Kafka Records from Kafka Brokers and maps each of them to Reactive Messaging `Messages` and vice versa.

For the following example a Kafka broker, which is accessible at `kafka:9092` and a topic named `custom-messages` are used.


#### Sending Kafka Records

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

1. Configure the Kafka connector globally
2. Configure the outgoing channel connector
3. Configure the Kafka topic
4. Configure the client id of the producer
5. Configure a serializer (e.g. StringSerializer) to be used
6. Configure the custom serializer

To send messages to an outgoing channel a `org.reactivestreams.Publisher` can be used. Therefore a **non-argument** method annotated with the annotation `@Outgoing("<channel-name>")` has to be provided. 

While generating a continuous stream of messages with a `io.reactivex.rxjava3.core.Flowable`, sending a single message seams to become a bigger problem. You cannot pass an argument to a method providing a stream without any argument allowed. Even if combining imperative programming and reactive seams to be tricky, there are solutions available. RxJava provides a `io.reactivex.rxjava3.core.FlowableEmitter` which enables to add single messages to a reactive stream. 

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

Kafka Records are sent as byte arrays, which requires to serialize messages. Message serialization is handled by the underlying Kafka Client and has to be configured in the `microprofile-config.properties`.

* `mp.messaging.outgoing.[channel-name].key.serializer` to configure the key serializer (optional, default to String)
* `mp.messaging.outgoing.[channel-name].value.serializer` to configure the value serializer (mandatory)

To send as JSON, a custom serializer has to be provided.

```
mp.messaging.outgoing.message.key.serializer=org.apache.kafka.common.serialization.StringSerializer
mp.messaging.outgoing.message.value.serializer=de.openknowledge.showcase.kafka.reactive.messaging.producer.CustomMessageSerializer
```

**CustomMessage**
```
package de.openknowledge.showcase.kafka.reactive.messaging.producer;

/**
 * A DTO that represents a custom message.
 */
public class CustomMessage {

  private String text;

  private String sender;

  public CustomMessage() {
  }

  public CustomMessage(final String text) {
    this(text, "Reactive Messaging Producer");
  }

  private CustomMessage(final String text, final String sender) {
    this.text = text;
    this.sender = sender;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(final String sender) {
    this.sender = sender;
  }

  @Override
  public String toString() {
    return "CustomMessage{" + "text='" + text + '\'' + ", sender='" + sender + '\'' + '}';
  }
}
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


#### Receiving Kafka Records

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

1. Configure the Kafka connector globally
2. Configure the outgoing channel connector
3. Configure the Kafka topic
4. Configure the client id of the consumer
5. Configura the group id of 
6. Configure a serializer (e.g. StringSerializer) to be used
7. Configure the custom serializer

To receive messages from a incoming channel a reactive message consumer has to be implemented. Therefore a method annotated with the annotation `@Incoming("<channel-name>")` and the expected message as a parameter has to be provided.
   
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

Kafka Records are sent as byte arrays, which requires to deserialize messages. Message deserialization is handled by the underlying Kafka Client and has to be configured in the `microprofile-config.properties`.

* `mp.messaging.incoming.[channel-name].key.serializer` to configure the key serializer (optional, default to String)
* `mp.messaging.incoming.[channel-name].value.serializer` to configure the value serializer (mandatory)

To receive message in JSON, a custom deserializer has to be provided.

```
mp.messaging.incoming.message.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
mp.messaging.incoming.message.value.deserializer=de.openknowledge.showcase.kafka.reactive.messaging.consumer.CustomMessageDeserializer
```

**CustomMessage**
```
package de.openknowledge.showcase.kafka.reactive.messaging.consumer;

/**
 * A DTO that represents a custom message.
 */
public class CustomMessage {

  private String text;

  private String sender;

  public CustomMessage() {
  }

  public CustomMessage(final String text, final String sender) {
    this.text = text;
    this.sender = sender;
  }

  public String getText() {
    return text;
  }

  public void setText(final String text) {
    this.text = text;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(final String sender) {
    this.sender = sender;
  }

  @Override
  public String toString() {
    return "CustomMessage{" + "text='" + text + '\'' + ", sender='" + sender + '\'' + '}';
  }
}
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
