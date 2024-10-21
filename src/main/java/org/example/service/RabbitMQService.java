package org.example.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.example.Main;
import org.example.model.Ticket;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class RabbitMQService {

    private static final String QUEUE_NAME = "tickets_queue";
    private static final String HOST = "localhost";
    private final ConnectionFactory factory;
    private final ObjectMapper objectMapper;
    private Connection connection;
    private Channel channel;
    private long recievedCount = 0;

    public RabbitMQService() {
        this.factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        this.objectMapper = new ObjectMapper();
        try {
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
            this.channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }

    public void sendObjectToQueue(Object obj) throws Exception {
        String jsonMessage = objectMapper.writeValueAsString(obj);
        channel.basicPublish("", QUEUE_NAME, null, jsonMessage.getBytes());
    }


    //Be patvirtinimo
    public <T> void continuousReceiveAndProcess(Class<T> clazz) throws Exception {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonMessage = new String(delivery.getBody(), "UTF-8");
                System.out.println(recievedCount +" Gautas JSON: " + jsonMessage);
                recievedCount++;
                try {
                    T obj = objectMapper.readValue(jsonMessage, clazz);
                    System.out.println("Gautas: "+obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

            System.out.println("Laukiama");
            while (true) {
                Thread.sleep(1000);
            }
        }
    }

    //Su patvirtinimu ir re-enqueue
    public void receiveAndProcessOneMessageAtATime(String queueName, int[] luckyNumbers) throws Exception {

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(queueName, false, false, false, null);

            //channel.basicQos(1);

            AMQP.Queue.DeclareOk declareOk = channel.queueDeclarePassive(queueName);
            AtomicInteger messageCount = new AtomicInteger(declareOk.getMessageCount());

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String jsonMessage = new String(delivery.getBody(), "UTF-8");
                //System.out.println(recievedCount+" Gautas JSON: " + jsonMessage);
                recievedCount++;
                try {

                    Ticket ticket = objectMapper.readValue(jsonMessage, Ticket.class);

                    int matchingNumbers = Main.countMatchingNumbers(luckyNumbers, ticket.getNumbers());
                    double prize = Main.calculatePrize(matchingNumbers);
                    System.out.println(Thread.currentThread().getName() + "- Ticket UUID: " + ticket.getUuidCode());
                    System.out.println("Ticket numbers: " + Arrays.toString(ticket.getNumbers()));
                    System.out.println("Matching numbers: " + matchingNumbers);
                    System.out.println("Prize: " + (prize > 0 ? prize + " EUR" : "No prize") + "\n");

                    //Patvirtinimas jog žinutė apdorota sėkmingai
                    //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    messageCount.getAndDecrement();

                } catch (Exception e) {
                    e.printStackTrace();

                    //Praneša, jog nepavyko apdoroti žinutės ir žinutė vėl grąžinama į eilę
                    //channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };

            channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

            System.out.println("Laukiama");
            while (true) {
                if (messageCount.get() == 0) {
                    break;
                }
                //Thread.sleep(10);
            }

        }
    }

    // clode channel and connection
    public void close() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
