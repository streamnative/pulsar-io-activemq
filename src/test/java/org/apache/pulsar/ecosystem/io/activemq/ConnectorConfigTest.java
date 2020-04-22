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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * connector config test.
 */
public class ConnectorConfigTest {

    public Map<String, Object> getBasicConfig() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("protocol", "tcp");
        configMap.put("host", "localhost");
        configMap.put("port", "61616");
        configMap.put("username", "admin");
        configMap.put("password", "admin");
        return configMap;
    }

    @Test
    public void loadBasicConfigTest() throws IOException {
        Map<String, Object> configMap = getBasicConfig();
        configMap.put("queueName", "user-op-queue");

        ActiveMQConfig activeMQConfig = ActiveMQConfig.load(configMap);
        activeMQConfig.validate();

        Assert.assertEquals("tcp", activeMQConfig.getProtocol());
        Assert.assertEquals("localhost", activeMQConfig.getHost());
        Assert.assertEquals("61616", activeMQConfig.getPort());
        Assert.assertEquals("tcp://localhost:61616", activeMQConfig.getBrokerUrl());
        Assert.assertEquals("admin", activeMQConfig.getUsername());
        Assert.assertEquals("admin", activeMQConfig.getPassword());
    }

    @Test
    public void loadQueueConfigTest() throws IOException {
        Map<String, Object> configMap = getBasicConfig();
        configMap.put("queueName", "user-op-queue");

        ActiveMQConfig activeMQConfig = ActiveMQConfig.load(configMap);
        activeMQConfig.validate();

        Assert.assertEquals("user-op-queue", activeMQConfig.getQueueName());
        Assert.assertNull(activeMQConfig.getTopicName());
    }

    @Test
    public void loadTopicConfigTest() throws IOException {
        Map<String, Object> configMap = getBasicConfig();
        configMap.put("topicName", "user-op-topic");

        ActiveMQConfig activeMQConfig = ActiveMQConfig.load(configMap);
        activeMQConfig.validate();

        Assert.assertEquals("user-op-topic", activeMQConfig.getTopicName());
        Assert.assertNull(activeMQConfig.getQueueName());
    }

    @Test
    public void loadMessageTypeConfig() throws IOException {
        Map<String, Object> configMap = getBasicConfig();
        configMap.put("topicName", "user-op-topic");

        ActiveMQConfig activeMQConfig = ActiveMQConfig.load(configMap);
        activeMQConfig.validate();

        Assert.assertEquals(ActiveMQTextMessage.class.getSimpleName(), activeMQConfig.getActiveMessageType());

        configMap.put("activeMessageType", ActiveMQBytesMessage.class.getSimpleName());
        activeMQConfig = ActiveMQConfig.load(configMap);
        activeMQConfig.validate();

        Assert.assertEquals(ActiveMQBytesMessage.class.getSimpleName(), activeMQConfig.getActiveMessageType());

    }

}
