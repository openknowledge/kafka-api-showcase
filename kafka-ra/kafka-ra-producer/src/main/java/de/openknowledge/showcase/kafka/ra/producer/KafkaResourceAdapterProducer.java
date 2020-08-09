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
package de.openknowledge.showcase.kafka.ra.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.JsonbBuilder;

import fish.payara.cloud.connectors.kafka.api.KafkaConnection;
import fish.payara.cloud.connectors.kafka.api.KafkaConnectionFactory;

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
