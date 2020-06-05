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
package de.openknowledge.showcase.kafka.reactive.messaging.producer;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;

import io.reactivex.rxjava3.core.Flowable;

/**
 * Kafka message generator that sends messages to a kafka topic. The topic is configured in the microprofile-config.properties.
 */
@ApplicationScoped
public class KafkaReactiveMessagingGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingGenerator.class);

  @Outgoing("message")
  public Flowable<CustomMessage> generate() {
    return Flowable
        .interval(2, TimeUnit.SECONDS)
        .doOnEach(notification -> LOG.info("Send generated message {}", notification.getValue()))
        .map(tick -> new CustomMessage(String.format("Message %d - %s", tick, LocalDateTime.now())));
  }
}
