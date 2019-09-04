# header 模式

生产者<br>
创建完交换机，在发布消息时指定 Header
```text
channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

Map<String, Object> headers = new HashMap<>();
headers.put("latitude",  51.5252949);
headers.put("longitude", -0.0905493);

AMQP.BasicProperties basicProperties = new AMQP.BasicProperties().builder().headers(headers).build();
channel.basicPublish(EXCHANGE_NAME, "", basicProperties, msg.getBytes());
```

消费者
如果没指定 header 则可以接收<br>
如果指定了 header 必须与生产者 header 中的值一致，或者包含并多余生产者中的参数

header 类型的交换性能会很差，并且不实用，基本不用。