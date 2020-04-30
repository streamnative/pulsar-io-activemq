---
id: io-activemq-sink
title: ActiveMQ sink connector
sidebar_label: ActiveMQ sink connector
---

The ActiveMQ sink connector pulls messages from Pulsar topics 
and persist the messages to ActiveMQ queues.

## Configuration 

The configuration of the ActiveMQ sink connector has the following properties.

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
| `activeMessageType` | String|false |0 | The ActiveMQ message simple class name. |

### Example

Before using the ActiveMQ sink connector, you need to create a configuration file through one of the following methods.

* JSON 

    ```json
    {
        "tenant": "public",
        "namespace": "default",
        "name": "activemq-sink",
        "inputs": ["user-op-queue-topic"],
        "archive": "connectors/pulsar-io-activemq-0.0.1.nar",
        "parallelism": 1,
        "configs":
        {
            "protocol": "tcp",
            "host": "localhost",
            "port": "61616",
            "username": "admin",
            "password": "admin",
            "queueName": "user-op-queue-pulsar"
        }
    }
    ```

* YAML

    ```yaml
    tenant: "public"
    namespace: "default"
    name: "activemq-sink"
    inputs: 
      - "user-op-queue-topic"
    archive: "connectors/pulsar-io-activemq-0.0.1.nar"
    parallelism: 1
    
    configs:
        protocol: "tcp"
        host: "localhost"
        port: "61616"
        username: "admin"
        password: "admin"
        queueName: "user-op-queue-pulsar"
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
$PULSAR_HOME/bin/pulsar-admin sink localrun --sink-config-file activemq-sink-config.yaml
```

5. Send message to pulsar
```
$PULSAR_HOME/bin/pulsar-client produce public/default/user-op-queue-topic --messages hello -n 10
```

6. Consume ActiveMQ message

Use the test method `receiveMessage` of the class `org.apache.pulsar.ecosystem.io.activemq.ActiveMQDemo`

```
@Test
private void receiveMessage() throws JMSException, InterruptedException {

    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

    @Cleanup
    Connection connection = connectionFactory.createConnection();
    connection.start();

    @Cleanup
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    Destination destination = session.createQueue("user-op-queue-pulsar");

    @Cleanup
    MessageConsumer consumer = session.createConsumer(destination);

    consumer.setMessageListener(new MessageListener() {
        @Override
        public void onMessage(Message message) {
            if (message instanceof ActiveMQTextMessage) {
                try {
                    System.out.println("get message ----------------- ");
                    System.out.println("receive: " + ((ActiveMQTextMessage) message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    });
}
```