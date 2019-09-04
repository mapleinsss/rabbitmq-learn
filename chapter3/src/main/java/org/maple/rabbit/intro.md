# 客户端开发
## 3.1 连接 RabbitMQ
#### 创建 connection
```text
1. 第一种方式
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(IP_ADDRESS);
        factory.setPort(PORT);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setVirtualHost(VIRTUALHOST);
        Connection connection = factory.newConnection();

2. 第二种方式
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUrl("amqp://USERNAME:PASSWORD@IP_ADDRESS:PORT/VIRTUALHOST");
        Connection connection = factory.newConnection();
```

#### 创建 channel
```text
Channel channel = connection.createChannel();
```

#### 检测 connection 和 channel 是否开启
不建议使用，可能会产生竞争，如果未开启，程序也会抛出异常，只需要捕获异常就行了。
```text
public boolean isOpen(){
    synchronized(this.monitor){
        return this.shutdownCause == null;
    }
}
```

## 3.2 交换机和队列

### 3.2.1 exchangeDeclare 详解
```text
public DeclareOk exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments) throws IOException {
    return this.exchangeDeclare(exchange, type.getType(), durable, autoDelete, internal, arguments);
}
参数说明：
exchange:交换器名称
type:交换机类型 fanout、direct、topic、header
durable:是否持久化，持久化可以将交换器存盘，服务器重启不会丢失相关信息
autoDelete:是否自动删除。前提是有交换机或者队列与这个交换机绑定，删除的意思是：将交换机或者队列与这个交换机解绑
internal:是否内置，如果是内置的，客户端程序无法直接发送消息到这个交换器中，只有通过交换器路由到交换器这种方式。
argument:其他一些机构化参数
```

```text
void exchangeDeclareNoWait(String exchange,
                           String type,
                           boolean durable,
                           boolean autoDelete,
                           boolean internal,
                           Map<String, Object> arguments) throws IOException;
直接创建，不等服务器返回，不建议使用
```

```text
Exchange.DeclareOk exchangeDeclarePassive(String name) throws IOException;
确定交换机是否存在
```

```text
Exchange.DeleteOk exchangeDelete(String exchange, boolean ifUnused) throws IOException;
Exchange.DeleteOk exchangeDelete(String exchange) throws IOException;
void exchangeDeleteNoWait(String exchange, boolean ifUnused) throws IOException;
删除交换机,ifUnused 为 true 只有在交换器没有使用情况下才删除
```

### 3.2.2 queueDeclare 详解
```text
Queue.DeclareOk queueDeclare() throws IOException;    
Queue.DeclareOk queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete,
                              Map<String, Object> arguments) throws IOException;
不带参数的方法默认创建一个命名类似 amq.gen--LhQzlgv3GhDOv8PIDabOXA 的（匿名队列），排他的，自动删除的，非持久化的队列。
参数说明：
queue:队列名称
durable:是否持久化
exclusive:设置是否排他。为true 则设置队列为排他的。如果一个队列被声明为排
          他队列，该队列仅对首次声明它的连接可见，并在连接断开时自动删除。这里需要注意
          三点:排他队列是基于连接( Connection) 可见的，同一个连接的不同信道(Channel)
          是可以同时访问同一连接创建的排他队列; "首次"是指如果一个连接己经声明了一个
          排他队列，其他连接是不允许建立同名的排他队列的，这个与普通队列不同:即使该队
          列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除，这种队列
          适用于一个客户端同时发送和读取消息的应用场景。
autoDelete:设置是否自动删除。为true 则设置队列为自动删除。自动删除的前提是:
           至少有一个消费者连接到这个队列，之后所有与这个队列连接的消费者都断开时，才会
           自动删除。不能把这个参数错误地理解为: "当连接到此队列的所有客户端断开时，这
           个队列自动删除"，因为生产者客户端创建这个队列，或者没有消费者客户端与这个队
           列连接时，都不会自动删除这个队列。(意思就是该队列必须连接过消费者后才能删除）
```

```text
重点注意：
    生产者和消费者都能够使用queueDeclare 来声明一个队列，但是如果消费者在同一个
信道上订阅了另一个队列，就无法再声明队列了。必须先取消订阅，然后将信道直为"传输"模式，之后才能声明队列。
```

```text
void queueDeclareNoWait(String queue, boolean durable, boolean exclusive, boolean autoDelete,
                        Map<String, Object> arguments) throws IOException;
Queue.DeclareOk queueDeclarePassive(String queue) throws IOException;
```

```text
删除的方法
Queue.DeleteOk queueDelete(String queue) throws IOException;
Queue.DeleteOk queueDelete(String queue, boolean ifUnused, boolean ifEmpty) throws IOException;
void queueDeleteNoWait(String queue, boolean ifUnused, boolean ifEmpty) throws IOException;
ifUnused 只有队列没使用的时候删除，ifEmpty 队列为空才能删除

Queue.PurgeOk queuePurge(String queue) throws IOException;
清空队列中的内容，不删除队列本身
```

### 3.2.3 queueBind 方法详解
```text
// 绑定
Queue.BindOk queueBind(String queue, String exchange, String routingKey) throws IOException;
Queue.BindOk queueBind(String queue, String exchange, String routingKey, Map<String, Object> arguments) throws IOException;
// 解绑
Queue.UnbindOk queueUnbind(String queue, String exchange, String routingKey) throws IOException;
Queue.UnbindOk queueUnbind(String queue, String exchange, String routingKey, Map<String, Object> arguments) throws IOException;
```

### 3.2.4 exchangeBind 方法详解
不仅可以队列和交换机绑定，还可以将交换机和交换机绑定
```text
Exchange.BindOk exchangeBind(String destination, String source, String routingKey) throws IOException;
Exchange.BindOk exchangeBind(String destination, String source, String routingKey, Map<String, Object> arguments) throws IOException;
void exchangeBindNoWait(String destination, String source, String routingKey, Map<String, Object> arguments) throws IOException;
消息由 source 交换机转发到 destination 交换机，某种程度上 destination 交换机可以看做一个队列
```

### 3.2.5 何时创建
交换机的使用并不正真耗费服务器资源，而队列会影响 QPS
RabbitMQ 官方建议，生产者和消费者都应该尝试创建(声明)队列。
但是如果业务本身架构在设计之初已经充分预估队列的使用情况，完全可以在业务上线之前在服务器创建好队列。

## 发送消息
```text
// 简单的发送
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

// 自定义属性
channel.basicPublish(EXCHANGE_NAME,ROUTING_KEY,
        new AMQP.BasicProperties().builder().contentType("text/plain").deliveryMode(2).priority(1).userId("hidden").build(),
        message.getBytes());
设置投递模式为 2 ，即持久化到服务器，优先级为1

// 还可以添加 header
Map<String, Object> headers = new HashMap<>();
headers.put(" loca1tion", "here ");
headers.put(" time ", " today");
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
        new AMQP.BasicProperties().builder().headers(headers).build(),
        message.getBytes());
        
// 还可以设置过期时间
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY,
        new AMQP.BasicProperties().builder().expiration("60000").build(),
        message.getBytes());
```

```text
void basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body) throws IOException;
void basicPublish(String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body)
        throws IOException;
void basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
        throws IOException;
        
props : 消息的基本属性集，其包含14 个属性成员，分别有contentType 、
contentEncoding 、headers ( Map<String,Object>) 、deliveryMode 、priority 、
correlationld 、replyTo 、expiration 、messageld、timestamp 、type 、userld 、
appld、clusterld。其中常用的几种都在上面的示例中进行了演示。

mandatory和 immediate 第四节介绍
```

## 3.4 消费消息
消费消息有两种：
```text
Push: Basic.Consume
Pull: Basic.Get

Push
推模式中一般使用实现 Consumer 接口 或者继承 DefaultConsumer 来实现
consumerTag 来区分不同的订阅
boolean autoAck = false;
channel.basicQos(64);
channel.basicConsume(queueName, autoAck, "myConsumerTag",new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                // (process the message components here . .. )
                channel.basicAck(deliveryTag, false);
            }
        });
        
String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, Consumer callback) throws IOException;
consumerTag:消费者标签，用来区分多个消费者
noLocal:设置为 true 则不能将同一个 Connection 中生产者发送的消息 传递给这个连接中的消费者。

重写其他方法见代码 
handleConsumeOk -> handleDelivery -> handleShutdownSignal



Pull
// 可以将接受消息的线程 和 处理消息的线程 分为两个线程，进一步解耦
GetResponse response = channel.basicGet(QUEUE_NAME, false);
System.out.println(new String(response.getBody()));
channel.basicAck(response.getEnvelope().getDeliveryTag(),false);


要点： 
Basic.Consume 会将信道设置为接收模式，知道取消订阅为止。

```











































