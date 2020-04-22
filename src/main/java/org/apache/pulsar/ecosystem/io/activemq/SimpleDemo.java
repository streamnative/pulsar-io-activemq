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

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * simple demo.
 */
public class SimpleDemo {

    public static void main(String[] args) throws JMSException, InterruptedException {
        SimpleDemo simpleDemo = new SimpleDemo();
        simpleDemo.sendMessage();
        simpleDemo.receiveMessage();
    }

    private void sendMessage() throws JMSException {

        // Create a ConnectionFactory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Create a connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a session
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        // Create a destination (Topic or Queue)
        Destination destination = session.createQueue("user-op-queue");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        for (int i = 0; i < 10; i++) {
            String msgContent = "Hello ActiveMQ - " + i;
            ActiveMQTextMessage message = new ActiveMQTextMessage();
            message.setText(msgContent);

            // Tell the producer to send the message
            producer.send(message);
        }

        producer.close();
        session.close();
        connection.close();
    }

    private void receiveMessage() throws JMSException, InterruptedException {

        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Destination destination = session.createQueue("user-op-queue-pulsar");

        MessageConsumer consumer = session.createConsumer(destination);

        while (true) {
            ActiveMQTextMessage message = (ActiveMQTextMessage) consumer.receive();
            System.out.println("get message ----------------- ");
            System.out.println("receive: " + message.getText());

            message.acknowledge();
        }

//        consumer.close();
//        session.close();
//        connection.close();
    }

}
