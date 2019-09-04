package org.maple.rabbit.header;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-14 10:11
 * @description TODO
 */
public class Producer {

    private static final String EXCHANGE_NAME = "exchange_header";
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
        String msg = "header 模式消息~~~";
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

        Map<String, Object> headers = new HashMap<>();
        headers.put("latitude",  51.5252949);
        headers.put("longitude", -0.0905493);

        AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().headers(headers).build();
        channel.basicPublish(EXCHANGE_NAME, "", basicProperties, msg.getBytes());
        channel.close();
        connection.close();
    }

}
