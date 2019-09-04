package org.maple.rabbitmq;

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
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "docker1";
    // 默认端口
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 创建连接
        Connection connection = factory.newConnection();
        // 创建信道
        Channel channel = connection.createChannel();
        // 创建一个type="direct" 、持久化的、非自动删除的交换器  boolean durable, boolean autoDelete
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
        // 创建一个持久化、非排他的、非自动删除的队列  boolean durable, boolean exclusive, boolean autoDelete
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 将交换器与队列通过路由键绑定
        /**
         * 此处的第三个参数应该是 BindKey
         *  当 BindKey 和 RoutingKey 相同时，才会推到这个队列
         *  该示例是 direct 模式 ，故 两者相同
         */
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        // 发送一条持久化消息
        String message = "Hello World !";
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
        // 关闭资源
        channel.close();
        connection.close();
    }

}
