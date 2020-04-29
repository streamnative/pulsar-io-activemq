package org.apache.pulsar.ecosystem.io.activemq;

import org.junit.Before;

import java.util.HashMap;
import java.util.Map;

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
