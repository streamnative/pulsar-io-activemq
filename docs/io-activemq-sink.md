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

