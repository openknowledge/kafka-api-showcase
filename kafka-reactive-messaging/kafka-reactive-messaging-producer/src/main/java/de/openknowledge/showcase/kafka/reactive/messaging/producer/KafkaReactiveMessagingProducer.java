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
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

/**
 * Kafka producer that sends messages to a kafka topic.
 */
@ApplicationScoped
public class KafkaReactiveMessagingProducer {

  private static final Logger LOG = LoggerFactory.getLogger(KafkaReactiveMessagingProducer.class);

  private FlowableEmitter<CustomMessage> emitter;

  public void send(final CustomMessage message) {
    LOG.info("Send message {}", message);
    emitter.onNext(message);
  }

  @Outgoing("message")
  public Publisher<CustomMessage> process() {
    return Flowable.create(emitter -> this.emitter = emitter, BackpressureStrategy.BUFFER);
  }
}
