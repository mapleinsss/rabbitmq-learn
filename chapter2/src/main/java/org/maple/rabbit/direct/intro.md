Direct 模型

生产者  ：向同一个交换机发送了三条 **路由键** 不同的消息


```text
消费者1 ：使用 queue_info_warning_error 队列，并且绑定了三个路由键
消费者2 ：使用 queue_info 队列，绑定了 info 路由键
消费者3 ：使用 queue_info_warning_error 队列，绑定了 info 路由键
消费者4 ：使用 queue_info_warning_error 队列，并且绑定了三个路由键
```
#### 只存在消费者1 和消费者2 的情况  不同队列：
```text
运行生产者后：

消费者1：
receive msg : this is info log <---------
receive msg : this is warning log <---------
receive msg : this is error log <---------
消费者2：
receive msg : this is info log <---------
```

#### 只存在消费者1 和消费者 3 的情况  同一队列：
```text
第一次
消费者1：
receive msg : this is info log <---------
receive msg : this is error log <---------
消费者3：
receive msg : this is warning log <---------
第二次
消费者1：
receive msg : this is warning log <---------
消费者3：
receive msg : this is info log <---------
receive msg : this is error log <---------

```

#### 只存在消费者1 和消费者 4 的情况  同一队列，绑定一样的路由键：
采用 轮询的模式发送
```text
第一次：

消费者1：
receive msg : this is info log <---------
receive msg : this is error log <---------
消费者4：
receive msg : this is warning log <---------

第二次：

消费者1：
receive msg : this is warning log <---------
消费者4：
receive msg : this is info log <---------
receive msg : this is error log <---------
```

结论：
1.从界面上看到队列 queque_info 只绑定了 info , 而 quque_info_warning_error 绑定了三个路由键<br>
(如果有一个消费者队列声明了3个路由键，之后再新写一个 main 线程，即使只声明了一个路由键，依然会收到3个路由键的消息)

2.只有消费者启动了，生产者发送消息才能送达，不接受以往消息

3.生产者只负责声明交换机，发布消息的时候需要指定路由键
```text
channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, null);
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY_INFO, MessageProperties.PERSISTENT_TEXT_PLAIN, info.getBytes());
```

消费者值只需要声明队列，将交换机、队列、路由键绑定后，就可以消费消息
```text
channel.queueDeclare(QUEUE_NAME, true, false, false, null);

channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_INFO);
channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_WARNING);
channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_ERROR);
```