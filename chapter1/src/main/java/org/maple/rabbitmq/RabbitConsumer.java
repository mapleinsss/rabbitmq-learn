package org.maple.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-13 10:55
 * @description 消费者
 */
public class RabbitConsumer {

    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "docker1";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        // 可以填写多个地址
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername("admin");
        factory.setPassword("admin");
        // 这里的连接方式与生产者的略有不同， 注意辨别区别
        Connection connection = factory.newConnection(addresses);
        // 创建信道
        final Channel channel = connection.createChannel();
        // 设置客户端最多未接受 ack 的消息个数
        channel.basicQos(64);
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
        // 可以将接受消息的线程 和 处理消息的线程 分为两个线程，进一步解耦
        channel.basicConsume(QUEUE_NAME, consumer);
        // 等回调函数执行完后，关闭资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }


}
