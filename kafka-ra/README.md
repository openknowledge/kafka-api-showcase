# Kafka Resource Adapter (by Payara)

Payara provides a [resource adapter](https://github.com/payara/Cloud-Connectors/tree/master/Kafka) for Apache Kafka which enables using Message Driven Beans to consume Kafka records in a Java Enterprise application.

The sample contains two modules using the Payara Kafka resource adapter to communicate with the Kafka broker  
* `kafka-ra-producer` containing a Kafka producer
* `kafka-ra-consumer` containing a Kafka consumer

## Kafka Resource Adapter

_HINT: Due to some configuration issues, the resource adapter is not working with Open Liberty at the moment. The sample runs on Wildfly Application Server instead._  

### Kafka RA Producer

#### Configuration

The resource adapter needs to be installed in Wildfly. In the `standalone.xml` the resource adapter has to be configured like this: 

**standalone.xml**
```xml
<subsystem xmlns="urn:jboss:domain:resource-adapters:5.0">
    <resource-adapters>
        <resource-adapter id="kafka">
            <archive>kafka-rar-0.5.0.rar</archive>
            <transaction-support>XATransaction</transaction-support>
            <connection-definitions>
                <connection-definition class-name="fish.payara.cloud.connectors.kafka.outbound.KafkaManagedConnectionFactory"
                                       jndi-name="java:/KafkaConnectionFactory"
                                       enabled="true"
                                       pool-name="ConnectionFactory">
                    <config-property name="bootstrapServersConfig">
                        ${env.KAFKA_HOST}
                    </config-property>
                    <config-property name="clientId">
                        ${env.KAFKA_CLIENT_ID}
                    </config-property>
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

#### Producer

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

### Kafka RA Consumer

#### Configuration

The consumer is configured the same way like the producer:

**standalone.xml**
```xml
<subsystem xmlns="urn:jboss:domain:resource-adapters:5.0">
    <resource-adapters>
        <resource-adapter id="kafka">
            <archive>kafka-rar-0.5.0.rar</archive>
            <transaction-support>XATransaction</transaction-support>
            <connection-definitions>
                <connection-definition class-name="fish.payara.cloud.connectors.kafka.outbound.KafkaManagedConnectionFactory"
                                       jndi-name="java:/KafkaConnectionFactory" 
                                       enabled="true" 
                                       pool-name="ConnectionFactory">
                    <config-property name="bootstrapServersConfig">
                        ${env.KAFKA_HOST}
                    </config-property>
                    <config-property name="clientId">
                        ${env.KAFKA_CLIENT_ID}
                    </config-property>
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

#### Consumer

The resource adapter uses the JMS API to communicate with Kafka.
`Message Driven Bean` are used to receive messages. 

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
