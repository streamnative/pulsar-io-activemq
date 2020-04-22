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

import java.util.Map;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.util.ByteSequence;
import org.apache.pulsar.functions.api.Record;
import org.apache.pulsar.io.core.Sink;
import org.apache.pulsar.io.core.SinkContext;

/**
 * A sink connector is used for move message from Pulsar to ActiveMQ.
 */
@Slf4j
public class ActiveMQSink implements Sink<byte[]> {

    private ActiveMQConfig activeMQConfig;

    private Connection connection;

    private Session session;

    private MessageProducer messageProducer;

    @Override
    public void open(Map<String, Object> map, SinkContext sinkContext) throws Exception {
        if (null != activeMQConfig) {
            throw new IllegalStateException("Connector is already open");
        }

        activeMQConfig = ActiveMQConfig.load(map);
        activeMQConfig.validate();

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeMQConfig.getBrokerUrl());

        if (StringUtils.isNotEmpty(activeMQConfig.getUsername())
                && StringUtils.isNotEmpty(activeMQConfig.getPassword())) {
            connection = connectionFactory.createConnection(activeMQConfig.getUsername(), activeMQConfig.getPassword());
        } else {
            connection = connectionFactory.createConnection();
        }
        connection.start();

        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Destination destination;
        if (StringUtils.isNotEmpty(activeMQConfig.getQueueName())) {
            destination = session.createQueue(activeMQConfig.getQueueName());
        } else if (StringUtils.isNotEmpty(activeMQConfig.getTopicName())) {
            destination = session.createTopic(activeMQConfig.getTopicName());
        } else {
            throw new Exception("destination is null.");
        }

        messageProducer = session.createProducer(destination);
    }

    @Override
    public void write(Record<byte[]> record) throws Exception {
        try {
            ActiveMQMessage activeMQMessage;
            if (activeMQConfig.getActiveMessageType().equals(ActiveMQTextMessage.class.getSimpleName())) {
                activeMQMessage = new ActiveMQTextMessage();
                ((ActiveMQTextMessage) activeMQMessage).setText(new String(record.getValue()));
            } else {
                activeMQMessage = new ActiveMQMessage();
                activeMQMessage.setContent(new ByteSequence(record.getValue()));
            }
            messageProducer.send(activeMQMessage);
            record.ack();
        } catch (Exception e) {
            log.error("failed send message to ActiveMQ.");
            record.fail();
        }
    }

    @Override
    public void close() throws Exception {
        if (messageProducer != null) {
            messageProducer.close();
        }
        if (session != null) {
            session.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
