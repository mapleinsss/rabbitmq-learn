package org.maple.rabbit.pull;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Mapleins
 * @date 2019-08-14 17:28
 * @description TODO
 */
public class Consumer {

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
        // 可以将接受消息的线程 和 处理消息的线程 分为两个线程，进一步解耦
        GetResponse response = channel.basicGet(QUEUE_NAME, false);
        System.out.println(new String(response.getBody()));
        channel.basicAck(response.getEnvelope().getDeliveryTag(),false);
        // 等回调函数执行完后，关闭资源
        TimeUnit.SECONDS.sleep(5);
        channel.close();
        connection.close();
    }

}
