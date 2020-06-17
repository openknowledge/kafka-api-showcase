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
package de.openknowledge.showcase.kafka.coreapis.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

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
