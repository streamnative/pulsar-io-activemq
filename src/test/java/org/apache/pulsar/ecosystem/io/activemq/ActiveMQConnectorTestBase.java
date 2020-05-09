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

import java.util.HashMap;
import java.util.Map;
import org.junit.Before;

/**
 * ActiveMQ connector test base.
 */
public class ActiveMQConnectorTestBase {

    protected final Map<String, Object> emptyConfig = new HashMap<>();
    protected final Map<String, Object> baseConfig = new HashMap<>();
    protected final Map<String, Object> queueConfig = new HashMap<>();
    protected final Map<String, Object> topicConfig = new HashMap<>();

    @Before
    public void setup() {
        baseConfig.put("protocol", "tcp");
        baseConfig.put("host", "localhost");
        baseConfig.put("port", "61616");
        baseConfig.put("username", "admin");
        baseConfig.put("password", "admin");

        queueConfig.putAll(baseConfig);
        queueConfig.put("queueName", "test-queue");

        topicConfig.putAll(baseConfig);
        topicConfig.put("topicName", "test-topic");
    }

}
