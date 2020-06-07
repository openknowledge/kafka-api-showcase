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

import org.apache.kafka.common.serialization.Deserializer;

import javax.json.bind.JsonbBuilder;
import java.util.Map;

/**
 * JSON deserializer for the DTO {@link CustomMessage}.
 */
public class CustomMessageDeserializer implements Deserializer<CustomMessage> {

  @Override
  public void configure(final Map<String, ?> map, final boolean b) {
  }

  @Override
  public CustomMessage deserialize(String topic, byte[] data) {
    if (data == null) {
      return null;
    }
    return JsonbBuilder.create().fromJson(new String(data), CustomMessage.class);
  }

  @Override
  public void close() {
  }
}
