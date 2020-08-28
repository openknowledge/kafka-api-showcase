# Kafka API Showcase - Kafka Resource Adapter (by Payara)

Payara provides a [resource adapter](https://github.com/payara/Cloud-Connectors/tree/master/Kafka) for Apache Kafka which enables using 
Message Driven Beans to consume Kafka records in a Java Enterprise application.

The showcase contains two modules using Payara's Kafka resource adapter to communicate with the Kafka broker  
* `kafka-ra-producer` containing a Kafka producer using the Payara Kafka Resource Adapter
* `kafka-ra-consumer` containing a Kafka consumer using the Payara Kafka Resource Adapter


**Notable Features:**

* Apache Kafka broker
* Integration of Payara's Kafka Resource Adapter 

_HINT: Due to some configuration issues, the resource adapter is not working with Open Liberty at the moment. The showcase runs on the 
Wildfly Application Server instead._  


## How to run

#### Step 1: Create docker images 

Software requirements to run the sample are `maven`, `openjdk-8` (or any other JDK 8) and `docker`. When running the Maven lifecycle it 
will create the war packages which contains the application. The WAR files will be copied into Wildfly Docker images using Spotify's 
`dockerfile-maven-plugin` during the package phase.

Before running the application it needs to be compiled and packaged using `Maven`. It creates the WAR files and the Docker images.

```shell script
$ mvn clean package
```

#### Step 2: Start docker images

After creating the docker images you can start the containers. The `docker-compose.yml` file defines the containers required to run the 
showcase.  

* the Apache Zookeeper application provided by Confluent Inc.
* the Apache Kafka broker provided by Confluent Inc.
* the custom Java EE application `kafka-ra-producer` which send messages to the Kafka topic
* the custom Java EE application `kafka-ra-consumer` which consumes messages from the Kafka topic

To start the containers you have to run `docker-compose`:

```shell script
$ docker-compose up
```

#### Step 3: Produce and consume messages

There are two ways to test the communication between the `kafka-ra-producer` and the `kafka-ra-consumer` application. 

1) The custom application `kafka-ra-producer` contains a message generator that generates and sends a new message every two seconds. The 
receipt and successful processing of the message can be traced in the log output of the `kafka-ra-consumer` application.

2) In addition to the message generator, the application provides a REST API that can be used to create and send your own messages. 

To send a custom message you have to send the following GET request:

```shell script
$ curl -X GET http://localhost:9081/kafka-ra-producer/api/messages?msg=<custom message>
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

### Kafka Resource Adapter

The resource adapter for both the producer and the consumer needs to be installed in Wildfly. In the `standalone.xml` the resource adapter 
has to be configured like this: 

**standalone.xml**
```xml
<subsystem xmlns="urn:jboss:domain:resource-adapters:5.0">
    <resource-adapters>
        <resource-adapter id="kafka">
            <archive>kafka-rar-0.5.0.rar</archive>
            <transaction-support>XATransaction</transaction-support>
            <connection-definitions>
                <connection-definition class-name="fish.payara.cloud.connectors.kafka.outbound.KafkaManagedConnectionFactory"
                                       jndi-name="java:/KafkaConnectionFactory" pool-name="ConnectionFactory" enabled="true">
                    <config-property name="bootstrapServersConfig">${env.KAFKA_HOST}</config-property>
                    <config-property name="clientId">${env.KAFKA_CLIENT_ID}</config-property>
                    <xa-pool>
                        <min-pool-size>1</min-pool-size>
                        <max-pool-size>20</max-pool-size>
                        <prefill>false</prefill>
                        <is-same-rm-override>false</is-same-rm-override>
                    </xa-pool>
                </connection-definition>
            </connection-definitions>
        </resource-adapter>
    </resource-adapters>
</subsystem>
```

#### Kafka RA Producer

The `bootstrapServersConfig` and `clientID` are dynamic, so they are injected from environment variables

**KafkaResourceAdapterProducer**
```java
/**
 * Kafka producer that sends messages to a Kafka topic via the Payara Kafka resource adapter.
 */
@ApplicationScoped
public class KafkaResourceAdapterProducer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaResourceAdapterProducer.class);

  @Resource(lookup = "java:/KafkaConnectionFactory")
  private KafkaConnectionFactory factory;

  @Inject
  @ConfigProperty(name = "KAFKA_TOPIC")
  private String topic;

  public void send(final CustomMessage message) {
    try (KafkaConnection connection = factory.createConnection()) {
      connection.send(new ProducerRecord(topic, JsonbBuilder.create().toJson(message)));
    } catch (Exception e) {
      LOG.error(e.getMessage(), e);
    }
  }
}
```

To send a message with the resource adapter, the `KafkaConnectionFactory` is injected from the JNDI name of the resource adapter from the `standalone.xml` and a `KafkaConnection` is created from that ConnectionFactory.

```java
@Resource(lookup = "java:/KafkaConnectionFactory")
private KafkaConnectionFactory factory;
```

Messages can be send via the `KafkaConnection` created from the `KafkaConnectionFactory`
```java
public void send(final CustomMessage message) {
  try (KafkaConnection connection = factory.createConnection()) {
    connection.send(new ProducerRecord(topic, JsonbBuilder.create().toJson(message)));
  } catch (Exception e) {
    LOG.error(e.getMessage(), e);
  }
}
```

#### Kafka RA Consumer

The resource adapter uses the JMS API to communicate with Kafka. `Message Driven Bean` are used to receive messages. 

**KafkaResourceAdapterConsumer**
```java
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "clientId", propertyValue = "kafka-ra-consumer"),
    @ActivationConfigProperty(propertyName = "groupIdConfig", propertyValue = "kafka-ra-consumer"),
    @ActivationConfigProperty(propertyName = "topics", propertyValue = "messages-topic"),
    @ActivationConfigProperty(propertyName = "bootstrapServersConfig", propertyValue = "kafka:9092"),
    @ActivationConfigProperty(propertyName = "retryBackoff", propertyValue = "1000"),
    @ActivationConfigProperty(propertyName = "autoCommitInterval", propertyValue = "100"),
    @ActivationConfigProperty(propertyName = "keyDeserializer", propertyValue = "org.apache.kafka.common.serialization.StringDeserializer"),
    @ActivationConfigProperty(propertyName = "valueDeserializer", propertyValue = "org.apache.kafka.common.serialization.StringDeserializer"),
    @ActivationConfigProperty(propertyName = "pollInterval", propertyValue = "3000"),
    @ActivationConfigProperty(propertyName = "commitEachPoll", propertyValue = "true"),
    @ActivationConfigProperty(propertyName = "useSynchMode", propertyValue = "true")})
@ResourceAdapter(value = "kafka")
public class KafkaResourceAdapterConsumer implements KafkaListener {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaResourceAdapterConsumer.class);

  @OnRecord(topics = {"messages-topic"})
  public void onMessage(final ConsumerRecord record) {
    CustomMessage message = JsonbBuilder.create().fromJson(record.value().toString(), CustomMessage.class);
    LOG.info("Received message {}", message);
  }
}
```
