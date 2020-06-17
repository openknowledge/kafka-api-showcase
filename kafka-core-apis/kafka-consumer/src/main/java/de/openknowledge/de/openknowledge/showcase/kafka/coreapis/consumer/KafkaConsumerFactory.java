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

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

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

  @Produces
  @ApplicationScoped
  public AdminClient createAdminClient() {
    Properties properties = new Properties();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    return AdminClient.create(properties);
  }
}
