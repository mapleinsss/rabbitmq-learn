package org.maple.rabbit.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-14 8:58
 * @description 主题模式
 */
public class Producer {

    private static final String EXCHANGE_NAME = "exchange_topic";
    private static final String IP_ADDRESS = "docker1";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        String msg = "topic 主题模式消息~~~";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.basicPublish(EXCHANGE_NAME, "org.maple.dev", null, msg.getBytes());
        channel.close();
        connection.close();
    }
}
