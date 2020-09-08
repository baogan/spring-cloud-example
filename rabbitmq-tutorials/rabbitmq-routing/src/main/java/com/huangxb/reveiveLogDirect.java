package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class reveiveLogDirect {

  private static final String Exchange_Name = "direct_logs";

  public static void main(String[] args) throws Exception{

    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    Connection connection = connectionFactory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(Exchange_Name, "direct");
    String queueName = channel.queueDeclare().getQueue();

    if(args.length < 1){
      System.err.println("usage: receiveLogDirect [info] [warning] [error]");
      System.exit(1);
    }

    for(String severity : args){
      channel.queueBind(queueName, Exchange_Name, severity);
    }

    System.out.println(" [*] waiting for message. to exit press ctrl + c");

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [x] received '" +delivery.getEnvelope().getRoutingKey()+ "':'" +message+ "'");
    };

    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
  }

}
