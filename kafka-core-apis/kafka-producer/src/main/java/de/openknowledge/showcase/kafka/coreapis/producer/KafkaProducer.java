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

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

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
