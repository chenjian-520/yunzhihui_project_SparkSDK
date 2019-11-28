package com.treasuremountain.exchangemsgsdk;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by gerryzhao on 10/29/2018.
 */
public class TMexchangeMsgSdk {

    private static Channel channel;
    private static Connection conn;

    public static void exchangeRevive(String exchangeName, int threadsCount, Consumer<String> exchangeCallback) throws IOException, TimeoutException {

        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadsCount);

        connect();

        channel.exchangeDeclare(exchangeName, "fanout", true);

        AMQP.Queue.DeclareOk queueOk = channel.queueDeclare();
        String queueName = queueOk.getQueue();

        channel.queueBind(queueName, exchangeName, "");

        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        String message = null;
                        try {
                            message = new String(body, "UTF-8");
                            channel.basicAck(envelope.getDeliveryTag(), false);
                            exchangeCallback.accept(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }

    private static void connect() throws IOException, TimeoutException {
        if (channel == null || conn == null) {
            ConnectionFactory factory = new ConnectionFactory();

            factory.setUsername("admin");
            factory.setPassword("admin");
            factory.setHost("TMexchangeMsg");
            factory.setPort(5672);
            factory.setVirtualHost("/");

            conn = factory.newConnection();

            channel = conn.createChannel();
        }
    }
}
