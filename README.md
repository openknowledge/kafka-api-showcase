# Kafka API Showcase

This is a showcase demonstrating three ways to connect and communicate with Apache Kafka. It contains Java Enterprise based message 
producer and consumer applications using 
[MicroProfile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging), Kafka Resource Adapter or Kafka
Core APIS like [Kafka Streams](https://kafka.apache.org/documentation/streams/) to send or receive messages from the Kafka broker. 


The [kafka-core-api](kafka-core-apis/README.md) showcase shows how to communicate with a Kafka broker by using the Kafka core APIs. 
Therefore a custom producer application, a custom consumer and a custom streams application are provided.

The [kafka-ra](kafka-ra/README.md) showcase shows how to communicate with a Kafka broker by using the Payara's Kafka Resource Adapter which
is part of the Payara Cloud Connectors. Therefore a custom producer application and a custom consumer application are provided.

The [kafka-reactive-messaging](kafka-reactive-messaging/README.md) showcase shows how to communicate with a Kafka broker by using 
MicroProfile Reactive Messaging. Therefore a custom producer application and a custom consumer application are provided.


## Kafka Core APIs

Kafka provides five core APIs which enables clients to send, read or stream data and connect to or manage the Kafka broker.

1. The [Producer API](https://kafka.apache.org/documentation/#producerapi) allows applications to send streams of data to topics in the 
Kafka cluster.
2. The [Consumer API](https://kafka.apache.org/documentation/#consumerapi) allows applications to read streams of data from topics in the 
Kafka cluster.
3. The [Streams API](https://kafka.apache.org/documentation/#streamsapi) allows transforming streams of data from input topics to output 
topics.
4. The [Connect API](https://kafka.apache.org/documentation/#connectapi) allows implementing connectors that continually pull from some 
source system or application into Kafka or push from Kafka into some sink system or application.
5. The [Admin API](https://kafka.apache.org/documentation/#adminapi) allows managing and inspecting topics, brokers, and other Kafka 
objects.


### Kafka Streams API

Kafka Streams is a library for building streaming applications, specifically applications that transform input Kafka topics into output 
Kafka topics (or calls to external services, or updates to databases, or whatever). It lets you do this with concise code in a way that is
distributed and fault-tolerant. Stream processing is a computer programming paradigm, equivalent to data-flow programming, event stream 
processing, and reactive programming, that allows some applications to more easily exploit a limited form of parallel processing.

The Streams API of Apache Kafka is available through a Java library and can be used to build highly scalable, elastic, fault-tolerant, 
distributed applications and microservices. First and foremost, the Kafka Streams API allows you to create real-time applications that 
power your core business. It is the easiest yet the most powerful technology to process data stored in Kafka. It builds upon important 
concepts for stream processing such as efficient management of application state, fast and efficient aggregations and joins, properly 
distinguishing between event-time and processing-time, and seamless handling of out-of-order data.

A unique feature of the Kafka Streams API is that the applications you build with it are normal Java applications. These applications can 
be packaged, deployed, and monitored like any other Java application – there is no need to install separate processing clusters or similar
special-purpose and expensive infrastructure!


## Kafka Resource Adapter (by Payara)

Payara provides a [resource adapter](https://github.com/payara/Cloud-Connectors/tree/master/Kafka) for Apache Kafka which enables using 
Message Driven Beans to consume Kafka records in a Java Enterprise application.



### Payara Cloud Connectors

Payara's Kafka resource adapter is part of the [Payara Cloud Connectors](https://github.com/payara/Cloud-Connectors). This is a project to 
provide JavaEE standards based connectivity to common Cloud infrastructure. Utilising JCA we provide connectivity to many different 
services provided by the leading cloud providers and open source technologies. Payara Cloud Connectors enable the creation of Cloud Native 
applications using JavaEE apis with the ability to build Event Sourcing and Message Driven architectures simply on public clouds.

#### JCA 

One of the benefits of using JCA adapters rather than crafting your own clients, using the standard apis for the messaging technologies, is
that the JCA adapters are fully integrated into your Java EE environment. That means your JavaEE application can use familiar JavaEE 
constructs such as Message Driven Beans and Connection Factories. Using MDBs to receive messages means that any threads are automatically 
provided via the Java EE application server which means they can take advantage of Container Transactions, Security, integration with EJBs,
CDI and the full range of Java EE components. Connection factories for outbound messaging also benefit from connection pooling and 
configuration via the administration console or via annotations in your code.

#### Resource Adapter

A [resource adapter](https://docs.oracle.com/cd/E13222_01/wls/docs92/resadapter/understanding.html) is a system library specific to an 
Enterprise Information System (EIS) and provides connectivity to an EIS; a resource adapter is analogous to a JDBC driver, which provides 
connectivity to a database management system. The interface between a resource adapter and the EIS is specific to the underlying EIS; it 
can be a native interface. The resource adapter plugs into an application server and provides seamless connectivity between the EIS, 
application server, and enterprise application.

Multiple resource adapters can plug in to an application server. This capability enables application components deployed on the application
server to access the underlying EISes. An application server and an EIS collaborate to keep all system-level mechanisms—transactions, 
security, and connection management—transparent to the application components. As a result, an application component provider can focus on 
the development of business and presentation logic for application components and need not get involved in the system-level issues related
to EIS integration. This leads to an easier and faster cycle for the development of scalable, secure, and transactional enterprise 
applications that require connectivity with multiple EISes.


## MicroProfile Reactive Messaging

[MicroProfile Reactive Messaging](https://microprofile.io/project/eclipse/microprofile-reactive-messaging) is a specification providing 
asynchronous messaging support based on Reactive Streams for MicroProfile. It's implementations (e.g. 
[Smallrye Reactive Messaging](https://smallrye.io/smallrye-reactive-messaging/) which is used by Quarkus) supports Apache Kafka, AMQP, 
MQTT, JMS, WebSocket and other messaging technologies. 

Reactive Messaging can handle messages generated from within the application but also interact with remote brokers. Reactive Messaging 
Connectors interacts with these remote brokers to retrieve messages and send messages using various protocols and technology. Each 
connector is dedicated to a specific technology. For example, a Kafka Connector is responsible to interact with Kafka, while a MQTT 
Connector is responsible for MQTT interactions.


### Rationale 

_Please note the following documentation is provided by the 
[MicroProfile Reactive Messaging Specification](https://download.eclipse.org/microprofile/microprofile-reactive-messaging-1.0/microprofile-reactive-messaging-spec.html#_microprofile_reactive_messaging)_   

State-of-the-art systems must be able to adapt themselves to emerging needs and requirements, such as market change and user expectations 
but also fluctuating load and inevitable failures. Leading-edge applications are capable of dynamic and adaptive capabilities aiming to 
provide responsive systems. While microservices aim to offer this agility, HTTP-based connecting tissue tends to fail to provide the 
required runtime adaptations, especially when facing failures.

Asynchronous communication allows temporal decoupling of services in a microservice based architecture. This temporal decoupling is 
necessary if communication is to be enabled to occur regardless of when the parties involved in the communication are running, whether 
they are loaded or overloaded, and whether they are successfully processing messages or failing.

In contrast, synchronous communication couples services together, binding their uptime, failure, and handling of the load to each other. In
a chain of synchronous interactions, the entire conversation can only be successful if all parties in the chain are responsive - if they 
are all running, processing messages successfully, and not overloaded. If just one party has a problem, all effectively exhibit the same 
problem. Therefore, systems of microservices relying on synchronous HTTP or relying on synchronous protocols tend to be fragile, and 
failures limit their availability. Indeed, in a microservice-based architecture, temporal coupling results in a fragile system, with 
resilience and scaling properties that are worse than a monolith, hence, it is essential for microservice based architectures to embrace 
asynchronous communication as much as possible.

The role of the MicroProfile Reactive Messaging specification is to deliver a way to build systems of microservices promoting both location
transparency and temporal decoupling, enforcing asynchronous communication between the different parts of the system.


#### Reactive Systems

[Reactive Systems](https://www.reactivemanifesto.org/) provide an architecture style to deliver responsive systems. By infusing 
asynchronous messaging passing at the core of the system, applications enforcing the reactive system’s characteristics are inherently 
resilient and become more elastic by scaling up and down the number of message consumers.

Microservices as part of reactive systems interact using messages. The location and temporal decoupling, promoted by this interaction 
mechanism, enable numerous benefits such as:

* Better failure handling as the temporal decoupling enables message brokers to resend or reroute messages in the case of remote service 
failures.
* Improved elasticity as under fluctuating load the system can decide to scale up and down some of the microservices.
* The ability to introduce new features more easily as components are more loosely coupled by receiving and publishing messages.

The MicroProfile Reactive Messaging specification aims to deliver applications embracing the characteristics of reactive systems.


#### On JMS and Message Driven Beans

Java EE offers JMS and Message Driven Beans for handling asynchronous communication; however, there are some problems with these 
specifications:

* Both are designed for a technology landscape where messaging was typically on the edge of the system to hand control of a transaction 
from one system to another; consequently, these technologies can appear heavyweight when used between microservices.
* It is assumed in their design that consistency is handled using distributed transactions. However, many message brokers, popular in 
microservice deployments, such as Apache Kafka, Amazon Kinesis and Azure Event Hubs, do not support XA transactions, rather, message 
acknowledgment is handled using offsets with at least once delivery guarantees.
* They do not have support for asynchronous IO; it is assumed that message processing is done on a single thread, however, many modern 
specifications are moving to asynchronous IO.

Hence a lighter weight, reactive solution to messaging is desirable for MicroProfile to ensure microservices written using MicroProfile 
are able to meet the demands required by the architecture.


#### Use cases

MicroProfile Reactive Messaging aims to provide a way to connect event-driven microservices. The key characteristics of the specification 
make it versatile and suitable for building different types of architecture and applications.

First, asynchronous interactions with different services and resources can be implemented using Reactive Messaging. Typically, asynchronous
database drivers can be used in conjunction with Reactive Messaging to read and write into a data store in a non-blocking and asynchronous
manner.

When building microservices, the CQRS and event-sourcing patterns provide an answer to the data sharing between microservices. Reactive 
Messaging can also be used as the foundation to CQRS and Event-Sourcing mechanism, as these patterns embrace message-passing as core 
communication pattern.

IOT applications, dealing with events from various devices, and data streaming applications can also be implemented using Reactive 
Messaging. The application receives events or messages, process them, transform them, and may forward them to another microservices. It 
allows for more fluid architecture for building data-centric applications.


### Concepts

The Reactive Messaging specification defines a development model for declaring CDI beans producing, consuming and processing messages. The
communication between these components uses Reactive Streams.

This specification relies on Eclipse MicroProfile Reactive Streams Operators and CDI.


#### Overall architecture

An application using Reactive Messaging is composed of CDI beans consuming, producing and processing messages.

These messages can be wholly internal to the application or can be sent and received via different message brokers.

![Overall Architecture](https://download.eclipse.org/microprofile/microprofile-reactive-messaging-1.0/images/overall.png)

Application’s beans contain methods annotated with `@Incoming` and `@Outgoing` annotations. A method with an `@Incoming` annotation 
consumes messages from a _channel_. A method with an `@Outgoing` annotation publishes messages to a _channel_. A method with both an 
`@Incoming` and an @Outgoing annotation is a message processor, it consumes messages from a _channel_, does some transformation to them, 
and publishes messages to another _channel_.


#### Channel

A _channel_ is a name indicating which source or destination of _messages_ is used. Channels are opaque Strings.

There are two types of channels:
* Internal channels are local to the application. They allows implementing multi-step processing where several beans from the same 
application form a chain of processing.
* Channels can be connected to remote brokers or various message transport layers such as Apache Kafka or to an AMQP broker. These channels
are managed by connectors.


#### Message

At the core of the Reactive Messaging specification is the concept of _message_. A message is an envelope wrapping a _payload_. A message 
is sent to a specific channel and, when received and processed successfully, acknowledged.

Reactive Messaging application components are addressable recipients which await the arrival of messages on a channel and react to them, 
otherwise lying dormant.

Messages are represented by the `org.eclipse.microprofile.reactive.messaging.Message` class. This interface is intentionally kept minimal.
The aim is that connectors will provide their own implementations with additional metadata that is relevant to that connector. For 
instance, a KafkaMessage` would provide access to the topic and partition.

The `org.eclipse.microprofile.reactive.messaging.Message#getPayload` method retrieves the wrapped payload. The 
`org.eclipse.microprofile.reactive.messaging.Message#ack` method acknowledges the message. Note that the ack method is asynchronous as 
acknowledgement is generally an asynchronous process.

Plain messages are created using:

* `org.eclipse.microprofile.reactive.messaging.Message#of(T)` - wraps the given payload, no acknowledgement
* `org.eclipse.microprofile.reactive.messaging.Message#of(T, java.util.function.Supplier<java.util.concurrent.CompletionStage<java.lang.Void>>)` - wraps the given payload and provides the acknowledgment logic


##### Message consumption with @Incoming

The `org.eclipse.microprofile.reactive.messaging.Incoming` annotation is used on a method from a CDI bean to indicate that the method 
consumes messages from the specified channel:

```
@Incoming("<channel-name>")                                            // (1)
public CompletionStage<Void> consume(Message<String> message) {        // (2)
  return message.ack();
}
```

1. my-channel is the channel
2. the method is called for every message sent to the my-channel channel

Reactive Messaging supports various forms of method signatures. This is detailed in the next section.

Remember that Reactive Messaging interactions are assembled from Reactive Streams. A method annotated with `@Incoming` is a Reactive 
Streams subscriber and so consumes messages that fit with the message signature and its annotations. Note that the handling of the Reactive
Streams protocol, such as subscriptions and back pressure, is managed by the Reactive Messaging implementation. The MicroProfile Reactive 
Streams specification used as a foundation for this version of Reactive Messaging is a single subscriber model where a stream `Publisher` 
is connected to a single Subscriber which controls back pressure. This implies that a Reactive Messaging channel should appear in a single
`@Incoming` annotation. The annotation of more than one `@Incoming` method to be associated with the same channel is not supported and will
cause an error during deployment.

From the user perspective, whether the incoming messages comes from co-located beans or a remote message broker is transparent. However, 
the user may decide to consume a specific subclass of `Message` (e.g. `KafkaMessage` in the following example) if the user is aware of 
this characteristic:

```
@Incoming("<channel-name>")
public CompletionStage<Void> consume(KafkaMessage<String> message) {    // (1)
  return message.ack();
}
```
Explicit consumption of a `KafkaMessage`


##### Message production with @Outgoing

The `org.eclipse.microprofile.reactive.messaging.Outgoing` annotation is used to annotate a method from a CDI bean to indicate that the 
method publishes messages to a specified channel:

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

A method annotated with `@Outgoing` is a Reactive Streams publisher and so publishes messages according to the requests it receives. 
The downstream `@Incoming` method or outgoing connector with a matching channel name will be linked to this publisher. Only a single method
can be annotated with `@Outgoing` for a particular channel name. Having the same channel name in more than one `@Outgoing` annotated method
is not supported and will result in an error during deployment.


##### Method consuming and producing

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

Having the same channel appear in the `@Outgoing` and `@Incoming` annotations of a processor is not supported and will result in an error 
during deployment.


#### Connectors

The application can receive and forward messages from various message brokers or transport layers. For instance, an application can be 
connected to a Kafka cluster, an AMQP broker or an MQTT server.

Reactive Messaging Connectors are extensions managing the communication with a specific transport technology. They are responsible for 
mapping a specific channel to remote sink or source of messages. This mapping is configured in the application configuration. Note that an
implementation may provide various ways to configure the mapping, but support for MicroProfile Config as a configuration source is 
mandatory.

Connector implementations are associated with a name corresponding to a messaging transport, such as Apache Kafka, Amazon Kinesis, RabbitMQ
or Apache ActiveMQ. For instance, an hypothetical Kafka connector could be associated with the following name: acme.kafka. This name is 
indicated using a qualifier on the connector implementation.

The user can associate a channel with this connector using the associated name:

```
mp.messaging.incoming.my-kafka-topic.connector=acme.kafka  (1)
```
1. the name associated with the connector.

The configuration format is detailed later in this document.

The Reactive Messaging implementation is responsible for finding the connector implementation associated with the given name in the user 
configuration. If the connector cannot be found, the deployment of the application must be failed.

The Reactive Messaging specification provides an SPI to implement connectors.


### Message stream operation

Message stream operation occurs according to the principles of reactive programming. The back pressure mechanism of reactive streams means
that a publisher will not send data to a subscriber unless there are outstanding subscriber requests. This implies that data flow along the
stream is enabled by the first request for data received by the publisher. For methods that are annotated with `@Incoming` and `@Outgoing`
this data flow control is handled automatically by the underlying system which will call the `@Incoming` and `@Outgoing` methods as
appropriate.

> Although `@Incoming` and `@Outgoing` methods remain callable from Java code, calling them directly will not affect the reactive streams
>they are associated with. For example, calling an `@Outgoing` annotated method from user code will not post a message on a message queue
>and calling an `@Incoming` method cannot be used to read a message. Enabling this would bypass the automatic back pressure mechanism that
>is one of the benefits of the specification. The `@Incoming` and `@Outgoing` method annotations are used to declaratively define the 
>stream which is then run by the implementation of MicroProfile Reactive Messaging without the user’s code needing to handle concerns such
>as subscriptions or flow control within the stream.


### Connectors

Connectors can retrieve messages from a remote broker (inbound) or send messages to a remove broker (outbound). A connector can, of course,
implement both directions.

Inbound connectors are responsible for:
1. Getting messages from the remote broker,
2. Creating a Reactive Messaging Message associated with the retrieved message. 
3. Potentially associating technical metadata with the message. This includes unmarshalling the payload.
4. Associating a acknowledgement callback to acknowledge the incoming message when the Reactive Messaging message is acknowledged.

Outbound connectors are responsible for:
1. Receiving Reactive Messaging Message and transform it into a structure understand by the remote broker. This includes marshalling the 
payload.
2. If the Message contains outbound metadata (metadata set during the processing to influence the outbound structure and routing), taking
them into account.
3. Sending the message to the remote broker.
4. Acknowledging the Reactive Messaging Message when the broker has accepted / acknowledged the message.

Applications needs to configure the connector used by expressing which channel is managed by which connector. Non-mapped channels are 
local / in-memory.

A connector is configured at `src/main/resources/META-INF/microprofile-config.properties` with a set of properties structured as follows:

```
mp.messaging.connector.[connector-name].[attribute]=[value]
```
```
mp.messaging.[incoming|outgoing].[channel-name].[attribute]=[value]
```

Each channel (both incoming and outgoing) is configured individually.

The `[incoming|outgoing]` segment indicates the direction.
* an incoming channel consumes data from a message broker or something producing data. It’s an inbound interaction. It can be connected to
a method annotated with an `@Incoming` using the same channel name.
* an outgoing consumes data from the application and forward it to a message broker or something consuming data. It’s an outbound 
interaction. It can be connected to a method annotated with an `@Outgoing` using the same channel name.

The `[channel-name]` is the name of the channel.

The `[connector-name]` is the name of the connector implementation (e.g. `liberty-kafka` or `smallrye-kafka`).

The `[attribute]=[value]` sets a specific connector attribute to the given value. Attributes depends on the sued connector. So, refer to
the connector documentation to check the supported attributes.

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
