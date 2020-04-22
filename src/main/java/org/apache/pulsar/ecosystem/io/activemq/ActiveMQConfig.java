/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pulsar.ecosystem.io.activemq;

//import avro.shaded.com.google.common.base.Preconditions;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * ActiveMQ config.
 */
@Data
public class ActiveMQConfig implements Serializable {

    private String protocol;

    private String host;

    private String port;

    private String username;

    private String password;

    private String queueName;

    private String topicName;

    private String activeMessageType = ActiveMQTextMessage.class.getSimpleName();

    public static ActiveMQConfig load(Map<String, Object> map) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new ObjectMapper().writeValueAsString(map), ActiveMQConfig.class);
    }

    public void validate() {
//        Preconditions.checkNotNull(protocol, "protocol property not set.");
//        Preconditions.checkNotNull(host, "host property not set.");
//        Preconditions.checkNotNull(port, "port property not set.");
//
//        Preconditions.checkArgument((queueName != null && queueName.length() > 0) ||
//                        (topicName != null && topicName.length() > 0),
//                "queueName and topicName all not set.");
    }

    public String getBrokerUrl() {
        return protocol + "://" + host + ":" + port;
    }

}
