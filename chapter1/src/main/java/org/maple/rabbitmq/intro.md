# 安装
网上有大量的安装教程，这里就不记录了，采用的是 docker 的安装方式

```text
docker pull rabbitmq:management

docker run -d --hostname my-rabbit --name rabbit -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 -p 25672:25672 -p 61613:61613 -p 1883:1883 rabbitmq:management

http://${host}:15672/
```
