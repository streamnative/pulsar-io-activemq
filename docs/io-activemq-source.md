---
id: io-activemq-source
title: ActiveMQ source connector
sidebar_label: ActiveMQ source connector
---

The ActiveMQ source connector receives messages from ActiveMQ clusters 
and writes messages to Pulsar topics.

## Configuration 

The configuration of the ActiveMQ source connector has the following properties.

### Property

| Name | Type|Required | Default | Description 
|------|----------|----------|---------|-------------|
| `protocol` |String| true | "tcp" | The ActiveMQ protocol. |
| `host` | String| true | " " (empty string) | The ActiveMQ host. |
| `port` | int |true | 5672 | The ActiveMQ port. |
| `username` | String|false | " " (empty string) | The username used to authenticate to ActiveMQ. |
| `password` | String|false | " " (empty string) | The password used to authenticate to ActiveMQ. |
| `queueName` | String|false | " " (empty string) | The ActiveMQ queue name that messages should be read from or written to. |
| `topicName` | String|false | " " (empty string) | The ActiveMQ topic name that messages should be read from or written to. |

### Example

Before using the ActiveMQ source connector, you need to create a configuration file through one of the following methods.

* JSON 

    ```json
    {
        "tenant": "public",
        "namespace": "default",
        "name": "activemq-source",
        "topicName": "user-op-queue-topic",
        "archive": "connectors/pulsar-io-activemq-0.0.1.nar",
        "parallelism": 1,
        "configs": {
            "protocol": "tcp",
            "host": "localhost",
            "port": "61616",
            "username": "admin",
            "password": "admin",
            "queueName": "user-op-queue"
        }
    }
    ```

* YAML

    ```yaml
    tenant: "public"
    namespace: "default"
    name: "activemq-source"
    topicName: "user-op-queue-topic"
    archive: "connectors/pulsar-io-activemq-0.0.1.nar"
    parallelism: 1
    
    configs:
        protocol: "tcp"
        host: "localhost"
        port: "61616"
        username: "admin"
        password: "admin"
        queueName: "user-op-queue"
    ```

1. Prepare ActiveMQ Service
```
docker pull rmohr/activemq
docker run -p 61616:61616 -p 8161:8161 rmohr/activemq
```

2. Put the `pulsar-io-activemq-0.0.1.nar` in the pulsar connectors catalog
```
cp pulsar-io-activemq-0.0.1.nar $PULSAR_HOME/connectors/pulsar-io-activemq-0.0.1.nar
```

3. Start Pulsar standalone
```
$PULSAR_HOME/bin/pulsar standalone
```

4. LocalRun ActiveMQ source
```
$PULSAR_HOME/bin/pulsar-admin source localrun --source-config-file activemq-source-config.yaml
```

5. Consume pulsar message test
```
bin/pulsar-client consume -s "sub-products" public/default/user-op-queue-topic -n 0
```

6. Send ActiveMQ message

Use the test method `sendMessage` of the class `org.apache.pulsar.ecosystem.io.activemq.ActiveMQDemo`

```
@Test
private void sendMessage() throws JMSException {

    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

    @Cleanup
    Connection connection = connectionFactory.createConnection();
    connection.start();

    @Cleanup
    Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

    Destination destination = session.createQueue("user-op-queue");

    @Cleanup
    MessageProducer producer = session.createProducer(destination);
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

    for (int i = 0; i < 10; i++) {
        String msgContent = "Hello ActiveMQ - " + i;
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(msgContent);
        producer.send(message);
    }
}
```