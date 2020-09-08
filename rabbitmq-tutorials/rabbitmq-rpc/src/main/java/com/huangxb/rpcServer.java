package com.huangxb;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class rpcServer {

  private static final String rpcQueueName = "rpc_queue";

  private static int fib(int n){
    if(n == 0)
      return 0;
    if(n == 1)
      return 1;
    return fib(n-1) + fib(n - 2);
  }

  public static void main(String[] args) throws Exception{

    ConnectionFactory connectionFactory =  new ConnectionFactory();
    connectionFactory.setHost("localhost");

    try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){
      channel.queueDeclare(rpcQueueName, false, false, false, null);
      channel.queuePurge(rpcQueueName);

      channel.basicQos(1);

      System.out.println(" [x] awaiting rpc client request");

      Object monitor = new Object();

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        AMQP.BasicProperties replyProps = new BasicProperties
            .Builder()
            .correlationId(delivery.getProperties().getCorrelationId())
            .build();
        String response = "";

        try{
          String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
          int n = Integer.parseInt(message);
          System.out.println(" [.] fib(" +message+ ")");
          response += fib(n);

        }catch(RuntimeException e){
          System.out.println( "[.] " +e.toString());
        }finally {
          channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes(
              StandardCharsets.UTF_8));
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
          synchronized (monitor){
            monitor.notify();
          }
        }
      };

      channel.basicConsume(rpcQueueName, false, deliverCallback, (consumerTag -> { }));

      // wait and be prepared to consume the message from rpc client.
      while (true){
        synchronized (monitor){
          try{
            monitor.wait();
          }catch (InterruptedException e){
            e.printStackTrace();
          }
        }
      }
    }
  }
}
