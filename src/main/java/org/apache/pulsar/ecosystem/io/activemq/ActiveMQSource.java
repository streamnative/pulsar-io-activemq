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

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Map;
import java.util.Optional;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.pulsar.functions.api.Record;
import org.apache.pulsar.io.core.PushSource;
import org.apache.pulsar.io.core.SourceContext;

/**
 * A source connector is used to move message from ActiveMQ to Pulsar.
 */
@Slf4j
public class ActiveMQSource extends PushSource<byte[]> {

    @Getter
    private ActiveMQConnectorConfig config;

    private Connection connection;

    private Session session;

    private MessageConsumer messageConsumer;

    @Override
    public void open(Map<String, Object> map, SourceContext sourceContext) throws Exception {
        if (null != config) {
            throw new IllegalStateException("Connector is already open");
        }

        config = ActiveMQConnectorConfig.load(map, null, sourceContext);
        config.validate();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(config.getBrokerUrl());

        if (StringUtils.isNotEmpty(config.getUsername())
                && StringUtils.isNotEmpty(config.getPassword())) {
            connection = connectionFactory.createConnection(config.getUsername(), config.getPassword());
        } else {
            connection = connectionFactory.createConnection();
        }
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Destination destination;
        if (StringUtils.isNotEmpty(config.getQueueName())) {
            destination = session.createQueue(config.getQueueName());
        } else if (StringUtils.isNotEmpty(config.getTopicName())) {
            destination = session.createTopic(config.getTopicName());
        } else {
            throw new Exception("destination is null.");
        }

        messageConsumer = session.createConsumer(destination);

        messageConsumer.setMessageListener(new MessageListenerImpl(this));
    }

    private static class MessageListenerImpl implements MessageListener {

        private ActiveMQSource activeMQSource;

        MessageListenerImpl(ActiveMQSource activeMQSource) {
            this.activeMQSource = activeMQSource;
        }

        @Override
        public void onMessage(Message message) {
            try {
                if (message instanceof ActiveMQTextMessage) {
                    activeMQSource.consume(new ActiveMQRecord(Optional.empty(),
                            ((ActiveMQTextMessage) message).getText().getBytes(UTF_8)));
                    message.acknowledge();
                } else if (message instanceof ActiveMQMessage) {
                    activeMQSource.consume(new ActiveMQRecord(Optional.empty(),
                            ((ActiveMQMessage) message).getContent().getData()));
                }
            } catch (Exception e) {
                log.error("failed to read ActiveMQ message.");
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (messageConsumer != null) {
            messageConsumer.close();
        }
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @Data
    private static class ActiveMQRecord implements Record<byte[]> {
        private final Optional<String> key;
        private final byte[] value;
    }

}
