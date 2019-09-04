package org.maple.rabbit.direct;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-13 10:44
 * @description 生产者
 */
public class RabbitProducer {

    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY_WARNING = "warning";
    private static final String ROUTING_KEY_INFO = "info";
    private static final String ROUTING_KEY_ERROR = "error";
    private static final String IP_ADDRESS = "docker1";
    // 默认端口
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        /*
            只声明了   交换机和   发布消息到哪个路由键
         */

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);

        // 发送到 info
        String info = "this is info log <---------";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_INFO, MessageProperties.PERSISTENT_TEXT_PLAIN, info.getBytes());

        // 发送到 info
        String warning = "this is warning log <---------";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_WARNING, MessageProperties.PERSISTENT_TEXT_PLAIN, warning.getBytes());

        // 发送到 info
        String error = "this is error log <---------";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_ERROR, MessageProperties.PERSISTENT_TEXT_PLAIN, error.getBytes());


        channel.close();
        connection.close();
    }

}
