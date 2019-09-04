package org.maple.rabbit.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-13 10:55
 * @description 消费者
 */
public class RabbitConsumer2 {

    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY_INFO = "info";
    private static final String IP_ADDRESS = "docker1";
    private static final int PORT = 5672;
    private static final String QUEUE_NAME = "queue_info";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };


        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection(addresses);
        // 创建信道
        final Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, true, false, false, null);


        channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,ROUTING_KEY_INFO);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("receive msg : " + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };

        channel.basicConsume(QUEUE_NAME,consumer);

    }


}
