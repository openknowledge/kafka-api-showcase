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
package de.openknowledge.showcase.kafka.jms.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Kafka message generator that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties.
 */
@Startup
@Singleton
public class MessageGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(MessageGenerator.class);

  private static AtomicLong messageCounter = new AtomicLong(0);

  @Inject
  private KafkaResourceAdapterProducer producer;

  @Schedule(second = "*/2", minute = "*", hour = "*", persistent = false)
  public void generate() {
    long count = messageCounter.getAndIncrement();
    LOG.info("Send message {}", count);
    producer.send(new CustomMessage(String.format("Message %d - %s", count, LocalDateTime.now())));
  }
}
