/*
 * Copyright (C) open knowledge GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.openknowledge.showcase.kafka.coreapis.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Kafka consumer that receives messages from a Kafka topic. The topic is configured in the microprofile-config.properties.
 */
@Singleton
@Startup
public class KafkaStreamConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamConsumer.class);

  @Inject
  @ConfigProperty(name = "kafka-streams.bootstrap-servers")
  private String server;

  @Inject
  @ConfigProperty(name = "kafka-streams.application-id")
  private String applicationId;

  @Inject
  @ConfigProperty(name = "kafka-streams.topic")
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

    KStream<String, CustomMessage> stream = builder.stream(topic);
    stream.peek((s, customMessage) -> LOG.info("Received Message {}", customMessage));

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
