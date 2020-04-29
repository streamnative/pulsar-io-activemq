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
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Assert;
import org.junit.Test;

/**
 * connector config test.
 */
public class ConnectorConfigTest extends ActiveMQConnectorTestBase {

    @Test
    public void loadBasicConfigTest() throws IOException {
        ActiveMQConnectorConfig activeMQConnectorConfig = ActiveMQConnectorConfig.load(queueConfig);
        activeMQConnectorConfig.validate();

        Assert.assertEquals("tcp", activeMQConnectorConfig.getProtocol());
        Assert.assertEquals("localhost", activeMQConnectorConfig.getHost());
        Assert.assertEquals("61616", activeMQConnectorConfig.getPort());
        Assert.assertEquals("tcp://localhost:61616", activeMQConnectorConfig.getBrokerUrl());
        Assert.assertEquals("admin", activeMQConnectorConfig.getUsername());
        Assert.assertEquals("admin", activeMQConnectorConfig.getPassword());
    }

    @Test
    public void loadQueueConfigTest() throws IOException {
        ActiveMQConnectorConfig activeMQConnectorConfig = ActiveMQConnectorConfig.load(queueConfig);
        activeMQConnectorConfig.validate();

        Assert.assertEquals("test-queue", activeMQConnectorConfig.getQueueName());
        Assert.assertNull(activeMQConnectorConfig.getTopicName());
    }

    @Test
    public void loadTopicConfigTest() throws IOException {
        ActiveMQConnectorConfig activeMQConnectorConfig = ActiveMQConnectorConfig.load(topicConfig);
        activeMQConnectorConfig.validate();

        Assert.assertEquals("test-topic", activeMQConnectorConfig.getTopicName());
        Assert.assertNull(activeMQConnectorConfig.getQueueName());
    }

    @Test
    public void loadMessageTypeConfig() throws IOException {
        ActiveMQConnectorConfig activeMQConnectorConfig = ActiveMQConnectorConfig.load(topicConfig);
        activeMQConnectorConfig.validate();

        Assert.assertEquals(ActiveMQTextMessage.class.getSimpleName(), activeMQConnectorConfig.getActiveMessageType());

        topicConfig.put("activeMessageType", ActiveMQBytesMessage.class.getSimpleName());
        activeMQConnectorConfig = ActiveMQConnectorConfig.load(topicConfig);
        activeMQConnectorConfig.validate();

        Assert.assertEquals(ActiveMQBytesMessage.class.getSimpleName(), activeMQConnectorConfig.getActiveMessageType());

    }

}
