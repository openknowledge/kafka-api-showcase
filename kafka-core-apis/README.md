# Kafka Core APIs Sample

Kafka provides five core APIs which enables clients to send, read or stream data and connect to or manage the Kafka broker.

1. The [Producer API](https://kafka.apache.org/documentation/#producerapi) allows applications to send streams of data to topics in the Kafka cluster.
2. The [Consumer API](https://kafka.apache.org/documentation/#consumerapi) allows applications to read streams of data from topics in the Kafka cluster.
3. The [Streams API](https://kafka.apache.org/documentation/#streamsapi) allows transforming streams of data from input topics to output topics.
4. The [Connect API](https://kafka.apache.org/documentation/#connectapi) allows implementing connectors that continually pull from some source system or application into Kafka or push from Kafka into some sink system or application.
5. The [Admin API](https://kafka.apache.org/documentation/#adminapi) allows managing and inspecting topics, brokers, and other Kafka objects.


The sample contains three modules using MP Reactive Messaging to communicate with the Kafka broker  
* `kafka-producer` containing a Kafka producer using the Producer API **(not yet implemented)**
* `kafka-consumer` containing a Kafka consumer using the Consumer API **(not yet implemented)**
* `kafka-streams` containing a Kafka Streams consumer using the Streams API

## Kafka Core APIs

### Kafka Streams

_will be added soon ..._