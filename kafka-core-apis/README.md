# Kafka API Showcase - Kafka Core APIs

Kafka provides five core APIs which enables clients to send, read or stream data and connect to or manage the Kafka broker.

The showcase contains three modules using the Kafka core APIs to communicate with the Kafka broker  
* `kafka-producer` containing a Kafka producer using the Producer API
* `kafka-consumer` containing a Kafka consumer using the Consumer API
* `kafka-streams` containing a Kafka Streams consumer using the Streams API


**Notable Features:**

* Apache Kafka broker
* Integration of Kafka Producer API
* Integration of Kafka Consumer API
* Integration of Kafka Streams API


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
* the custom Java EE application `kafka-producer` which send messages to the Kafka topic
* the custom Java EE application `kafka-consumer` which consumes messages from the Kafka topic
* the custom Java EE application `kafka-streams` which streams messages from the Kafka topic

To start the containers you have to run `docker-compose`:

```shell script
$ docker-compose up
```

#### Step 3: Produce and consume messages

There are two ways to test the communication between the `kafka-producer` and the `kafka-consumer` or the `kafka-streams` application. 

1) The custom application `kafka-producer` contains a message generator that generates and sends a new message every two seconds. The 
receipt and successful processing of the message can be traced in the log output of the `kafka-consumer` and the `kafka-streams` 
application.

2) In addition to the message generator, the application provides a REST API that can be used to create and send your own messages. 

To send a custom message you have to send the following GET request:

```shell script
$ curl -X GET http://localhost:9080/kafka-producer/api/messages?msg=<custom message>
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

### Kafka Producer/Consumer/Streams API

To use the Kafka API you have to add the following dependencies to the `pom.xml`:

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>${version.kafka}</version>
</dependency>
```

#### Kafka Producer

To send messages to Apache Kafka there must be a KafkaProducer which is configured in a _ProducerFactory_. The KafkaProducer uses a 
`Properties` object to store the configuration. The key and value serializer are static, therefore those can be used as a string literal. 
The `clientID` and `bootstrapServers` can change, so they have to be read from environment variables.

**KafkaProducerFactory**
```java
/**
 * Kafka producer factory. Provides Kafka producer for the application.
 */
@ApplicationScoped
public class KafkaProducerFactory {

  @Inject
  @ConfigProperty(name = "KAFKA_HOST")
  private String bootstrapServers;

  @Inject
  @ConfigProperty(name = "KAFKA_CLIENT_ID")
  private String clientId;

  @Produces
  @ApplicationScoped
  public Producer<String, CustomMessage> createProducer() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "de.openknowledge.showcase.kafka.coreapis.producer.CustomMessageSerializer");

    return new KafkaProducer<>(properties);
  }
}
```

Now the KafkaProducer can be used to send messages.
 
[//]: # (TODO: Erklärung, wie die @Produces Methode den Producer erstellt)

**KafkaProducer**
```java
/**
 * Kafka producer that sends messages to a kafka topic.
 */
@ApplicationScoped
public class KafkaProducer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);

  @Inject
  private Producer<String, CustomMessage> producer;

  @Inject
  @ConfigProperty(name = "KAFKA_TOPIC")
  private String topic;

  public void send(final CustomMessage message) {
    LOG.info("Send message {}", message);
    producer.send(new ProducerRecord<>(topic, message));
  }
}
```

Now the KafkaProducer can be injected into the Resource. 

**MessageResource**
```java
/**
 * JAX-RS resource that produces messages which are send by a Kafka producer.
 */
@Path("messages")
public class MessageResource {

  private static final Logger LOG = LoggerFactory.getLogger(MessageResource.class);

  @Inject
  private KafkaProducer producer;

  @GET
  public Response sendMessage(@QueryParam("msg") String message) {
    LOG.info("Send custom message {}", message);

    CustomMessage customMessage = new CustomMessage(message);
    producer.send(customMessage);

    LOG.info("Message send");

    return Response.status(Response.Status.ACCEPTED).build();
  }
}
```

#### Kafka Consumer

The Kafka Consumer uses the same type of configuration as the Kafka Producer. Because there can be multiple instances of the consumer, 
there has to be a `groupID` which sums all consumer, so they dont receive the same message multiple times.

**KafkaConsumerFactory**
```java
/**
 * Kafka consumer factory. Provides Kafka consumer for the application.
 */
@ApplicationScoped
public class KafkaConsumerFactory {

  @Inject
  @ConfigProperty(name = "KAFKA_HOST")
  private String bootstrapServers;

  @Inject
  @ConfigProperty(name = "KAFKA_CLIENT_ID")
  private String clientId;

  @Inject
  @ConfigProperty(name = "KAFKA_GROUP_ID")
  private String groupId;

  @Produces
  public Consumer<String, CustomMessage> createConsumer() {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    properties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "de.openknowledge.de.openknowledge.showcase.kafka.coreapis.consumer.CustomMessageDeserializer");
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

    return new KafkaConsumer<>(properties);
  }
}
```

The Kafka Consumer API has no automatic way of receiving messages from a topic, therefore a class has to be provided, which polls the topic 
manually. That class uses a `ManagedScheduledExecutorService` which executes the polling method every second.

The `ManagedScheduledExecutorService` is configured in the `server.xml:`
```xml
<managedScheduledExecutorService id="pollingKafkaMessageExecutorService" jndiName="concurrent/pollingKafkaMessageExecutorService">
    <concurrencyPolicy max="1"/>
</managedScheduledExecutorService>
```

**KafkaSubscriber**
```java
/**
 * Abstract Kafka subscriber. Provides means for Kafka consumers.
 */
public abstract class KafkaSubscriber<V> {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriber.class);

  private static final int DURATION_MILLIS = 1000;

  private Consumer<String, V> consumer;

  private String topic;

  @Inject
  private Event<ConsumerRecord<String, V>> consumerEvent;

  @Inject
  private AdminClient adminClient;

  @Resource(lookup = "concurrent/pollingKafkaMessageExecutorService")
  private ManagedScheduledExecutorService executor;

  private final AtomicBoolean running = new AtomicBoolean(true);

  protected KafkaSubscriber() {
    super();
  }

  public KafkaSubscriber(Consumer<String, V> consumer, String topic) {
    this();
    this.consumer = Objects.requireNonNull(consumer, "consumer must not be null");
    this.topic = Objects.requireNonNull(topic, "topic must not be null");
  }

  public void onCdiInitialized(@Observes @Initialized(ApplicationScoped.class) Object param) {
    ensureTopicExists();
    this.executor.execute(() -> polling());
  }

  private void ensureTopicExists() {
    try {
      final boolean topicExists = adminClient.listTopics().names().get().stream().anyMatch(name -> topic.equals(name));
      if (!topicExists) {
        adminClient.createTopics(Collections.singletonList(new NewTopic(topic, 1, (short)1)));
        adminClient.close();
      }
    } catch (InterruptedException | ExecutionException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void polling() {
    this.consumer.subscribe(Collections.singletonList(topic));
    try {
      while (this.running.get()) {
        ConsumerRecords<String, V> records = this.consumer.poll(Duration.ofMillis(DURATION_MILLIS));
        for (ConsumerRecord record : records) {
          try {
            this.consumerEvent.fire(record);
          } catch (Exception e) {
            LOG.error("Firing event failed.", e);
          } finally {
            commitRecord(record);
          }
        }
      }
    } finally {
      this.consumer.close();
    }
  }

  private void commitRecord(final ConsumerRecord record) {
    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
    OffsetAndMetadata metadata = new OffsetAndMetadata(record.offset() + 1, "no metadata");
    this.consumer.commitSync(Collections.singletonMap(topicPartition, metadata));
  }

  public void onCdiDestroyed(@Observes @Destroyed(ApplicationScoped.class) Object param) {
    this.running.set(false);
  }
}
```

The consumer itself has to extend the KafkaSubscriber, which polls the topic for messages. The `onMessage` method is run every time a 
message is received.

**KafkaConsumer**
```java
/**
 * Kafka consumer that receives messages from a Kafka topic.
 */
@ApplicationScoped
public class KafkaConsumer extends KafkaSubscriber<CustomMessage> {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

  @Inject
  protected KafkaConsumer(final Consumer<String, CustomMessage> consumer, @ConfigProperty(name = "KAFKA_TOPIC") final String topic) {
    super(consumer, topic);
  }

  public void onMessage(@Observes final ConsumerRecord<String, CustomMessage> record) {
    LOG.info("Received message {}", record.value());
  }
}
```

#### Kafka Streams

The Kafka Streams API also uses the some `Properties` to be configured. Because Kafka Streams read and write messages from and to topics, 
it needs a `Serializer` and `Deserializer`.

Serdes uses a Serializer and Deserializer to convert bytes coming from Kafka and sends bytes to Kafka.

**CustomMessageSerdes**
```java
/**
 * Serdes for CustomMessages, uses {@link CustomMessageSerializer} Serializer and {@link CustomMessageDeserializer} Deserializer
 */
public class CustomMessageSerdes extends Serdes.WrapperSerde<CustomMessage> {
  
  public CustomMessageSerdes() {
    super(new CustomMessageSerializer(), new CustomMessageDeserializer());
  }
}
```

**KafkaStreamConsumer**
```java
/**
 * Kafka consumer that receives messages from a Kafka topic.
 */
@Singleton
@Startup
public class KafkaStreamConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamConsumer.class);

  @Inject
  @ConfigProperty(name = "KAFKA_HOST")
  private String server;

  @Inject
  @ConfigProperty(name = "KAFKA_APP_ID")
  private String applicationId;

  @Inject
  @ConfigProperty(name = "KAFKA_TOPIC")
  private String topic;

  @PostConstruct
  public void initialize() {
    LOG.info("Init Kafka Streams");

    Topology topology = buildTopology();
    Properties properties = getProperties();

    KafkaStreams streams = new KafkaStreams(topology, properties);

    streams.start();

    LOG.info("Start streaming");
  }

  private Topology buildTopology() {
    StreamsBuilder builder = new StreamsBuilder();

    builder.stream(topic).peek((s, customMessage) -> LOG.info("Received Message {}", customMessage));

    return builder.build();
  }

  private Properties getProperties() {
    Properties properties = new Properties();

    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, server);
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);

    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, CustomMessageSerdes.class);

    return properties;
  }
}
```

Kafka Streams uses a KStream with a similar API to Java 8 Streams, to manipulate messages coming from Kafka.

Here the messages are logged to _stdout_.
```java
 private Topology buildTopology() {
  StreamsBuilder builder = new StreamsBuilder();

  builder.stream(topic).peek((s, customMessage) -> LOG.info("Received Message {}", customMessage));

  return builder.build();
}
```
