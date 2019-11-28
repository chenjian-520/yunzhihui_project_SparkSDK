package com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq;

import com.rabbitmq.client.*;
import com.treasuremountain.tmcommon.thirdpartyservice.rabbitmq.message.QueryCallbackMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Created by gerryzhao on 10/20/2018.
 */
public class TMRabbitMqOperator {

    private final static Logger log = LoggerFactory.getLogger(TMRabbitMqOperator.class);

    private static int threadCount = 17;
    private static Channel channel;
    private static Connection conn;

    private static ExecutorService cachedThreadPool;

    public static void connect(String host, String userName, String password, int port, int threadCount) throws IOException, TimeoutException {

        cachedThreadPool = Executors.newFixedThreadPool(threadCount);

        ConnectionFactory factory = new ConnectionFactory();

        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(port);
        factory.setVirtualHost("/");

        conn = factory.newConnection();

        channel = conn.createChannel();
    }

    public static void queueDeclare(String queryName, boolean durable, boolean exclusive, boolean autoDelete,
                                    Map<String, Object> arguments) throws IOException {
        channel.queueDeclare(queryName, durable, exclusive, autoDelete, arguments);
    }

    public static void publishQuery(String queryName, String msg) throws IOException {
        channel.queueDeclare(queryName, true, false, false, null);
        channel.basicPublish("", queryName,
                MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
    }

    /**
     * 创建交换机
     * ref.tian
     * **/
    public static void exchangeDeclare(String exchangeName) throws IOException {
        channel.exchangeDeclare(exchangeName, "fanout", true);
    }

    public static void publishExchange(String exchangeName, String msg) throws IOException {
//        channel.exchangeDeclare(exchangeName, "fanout", true);//delete by ref.tian
        channel.basicPublish(exchangeName, "",
                null, msg.getBytes());
    }

    public static void queryRevive(String queryName, Consumer<QueryCallbackMsg> queryCallback) throws IOException {

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
                            QueryCallbackMsg callbackMsg = new QueryCallbackMsg(message, channel, envelope.getDeliveryTag());
                            queryCallback.accept(callbackMsg);
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }
                });
            }
        };

        channel.basicConsume(queryName, false, consumer);
    }

    public static void exchangeRevive(String exchangeName, Consumer<String> exchangeCallback) throws IOException {

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
                            exchangeCallback.accept(message);
                            channel.basicAck(envelope.getDeliveryTag(), false);
                        } catch (Exception e) {
                            log.error(e.toString());
                        }
                    }
                });
            }
        };

        channel.basicConsume(queueName, false, consumer);
    }

    /**
     * 关闭通道和连接
     * ref.tian
     **/
    public static void closeConnetion() throws IOException, TimeoutException {
        if (channel.isOpen()) {
            channel.close();
        }

        if (conn.isOpen()) {
            conn.close();
        }
    }
}
