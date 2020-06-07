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
package de.openknowledge.showcase.kafka.ra.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jboss.ejb3.annotation.ResourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.json.bind.JsonbBuilder;

import fish.payara.cloud.connectors.kafka.api.KafkaListener;
import fish.payara.cloud.connectors.kafka.api.OnRecord;

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
