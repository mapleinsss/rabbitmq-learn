package org.maple.rabbit.header;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-14 9:04
 * @description TODO
 */
public class Consumer2 {


    private static final String IP_ADDRESS = "docker1";
    private static final int PORT = 5672;
    private static final String EXCHANGE_NAME = "exchange_header";
    private static final String QUEUE_NAME = "queue_header_2";

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
        // 消费者自己开队列去拉取交换机中的值
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);

        Map<String, Object> headers = new HashMap<>();
        headers.put("x-match", "any");
        // 消费者要绑定交换机
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "", headers);
        channel.basicConsume(QUEUE_NAME, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("consumer2 receive header msg : " + new String(body));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });


    }
}
