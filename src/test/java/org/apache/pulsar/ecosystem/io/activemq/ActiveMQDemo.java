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

import java.util.concurrent.CountDownLatch;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import lombok.Cleanup;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * Simple ActiveMQ Demo, used for testing validate.
 */
public class ActiveMQDemo {

    public void sendMessage() throws JMSException {

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
            System.out.println("send message " + i);
        }
    }

    public void receiveMessage() throws JMSException, InterruptedException {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        @Cleanup
        Connection connection = connectionFactory.createConnection();
        connection.start();

        @Cleanup
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("user-op-queue-pulsar");

        @Cleanup
        MessageConsumer consumer = session.createConsumer(destination);

        CountDownLatch countDownLatch = new CountDownLatch(10);
        consumer.setMessageListener(message -> {
            if (message instanceof ActiveMQTextMessage) {
                try {
                    System.out.println("get message ----------------- ");
                    System.out.println("receive: " + ((ActiveMQTextMessage) message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

}
