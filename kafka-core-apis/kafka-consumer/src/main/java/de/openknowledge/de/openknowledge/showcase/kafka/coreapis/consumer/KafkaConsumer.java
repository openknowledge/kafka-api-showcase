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
package de.openknowledge.de.openknowledge.showcase.kafka.coreapis.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Kafka consumer that receives messages from a Kafka topic.
 */
@ApplicationScoped
public class KafkaConsumer extends KafkaSubscriber<CustomMessage> {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

  @Inject
  protected KafkaConsumer(final Consumer<String, CustomMessage> consumer, @ConfigProperty(name = "kafka.topic") final String topic) {
    super(consumer, topic);
  }

  public void onMessage(@Observes final ConsumerRecord<String, CustomMessage> record) {
    LOG.info("Received message {}", record.value());
  }
}
