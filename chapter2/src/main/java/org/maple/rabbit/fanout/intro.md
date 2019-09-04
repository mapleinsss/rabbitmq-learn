# fanout 广播模式

它会把所有发送到该交换器的消息路由到所有与该交换器绑定的队列中。

消费者1 和 消费者2 绑定同一个队列，消费者三单独一个队列

生产者只用声明交换机然后发布消息
```text
channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
// fanout类型的exchange会把消息推到所有的queue中，所以不需要指定routingkey，指定了也没用！
channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
```

消费者可以自己声明队列，然后将队列绑定到交换机上,然后进行消费
```text
// 消费者自己开队列去拉取交换机中的值
channel.queueDeclare(QUEUE_NAME, true, false, false, null);
// 消费者要绑定交换机
channel.queueBind(QUEUE_NAME,EXCHANGE_NAME,"");
```

开启三个消费者：
一条消息进来，对于消费者1 和 消费者2，他们绑定的是同一个队列，所以采用轮询的方式发送到每个队列；
对于消费者1 和消费者3 他们绑定是不同的队列，所以都会收到广播消息