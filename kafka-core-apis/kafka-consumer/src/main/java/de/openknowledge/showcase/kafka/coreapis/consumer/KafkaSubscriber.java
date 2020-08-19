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
package de.openknowledge.showcase.kafka.coreapis.consumer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

/**
 * Abstract Kafka subscriber. Provides means for Kafka consumers.
 */
public abstract class KafkaSubscriber<V> {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaSubscriber.class);

  private static final int DURATION_MILLIS = 1000;

  private Consumer<String, V> consumer;

  private String topic;

  @Inject
  private Event<ConsumerRecord<String, V>> consumerEvent;

  @Inject
  private AdminClient adminClient;

  @Resource(lookup = "concurrent/pollingKafkaMessageExecutorService")
  private ManagedScheduledExecutorService executor;

  private final AtomicBoolean running = new AtomicBoolean(true);

  protected KafkaSubscriber() {
    super();
  }

  public KafkaSubscriber(Consumer<String, V> consumer, String topic) {
    this();
    this.consumer = Objects.requireNonNull(consumer, "consumer must not be null");
    this.topic = Objects.requireNonNull(topic, "topic must not be null");
  }

  public void onCdiInitialized(@Observes @Initialized(ApplicationScoped.class) Object param) {
    ensureTopicExists();
    this.executor.execute(() -> polling());
  }

  private void ensureTopicExists() {
    try {
      final boolean topicExists = adminClient.listTopics().names().get().stream().anyMatch(name -> topic.equals(name));
      if (!topicExists) {
        adminClient.createTopics(Collections.singletonList(new NewTopic(topic, 1, (short)1)));
        adminClient.close();
      }
    } catch (InterruptedException | ExecutionException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private void polling() {
    this.consumer.subscribe(Collections.singletonList(topic));
    try {
      while (this.running.get()) {
        ConsumerRecords<String, V> records = this.consumer.poll(Duration.ofMillis(DURATION_MILLIS));
        for (ConsumerRecord record : records) {
          try {
            this.consumerEvent.fire(record);
          } catch (Exception e) {
            LOG.error("Firing event failed.", e);
          } finally {
            commitRecord(record);
          }
        }
      }
    } finally {
      this.consumer.close();
    }
  }

  private void commitRecord(final ConsumerRecord record) {
    TopicPartition topicPartition = new TopicPartition(record.topic(), record.partition());
    OffsetAndMetadata metadata = new OffsetAndMetadata(record.offset() + 1, "no metadata");
    this.consumer.commitSync(Collections.singletonMap(topicPartition, metadata));
  }

  public void onCdiDestroyed(@Observes @Destroyed(ApplicationScoped.class) Object param) {
    this.running.set(false);
  }
}
