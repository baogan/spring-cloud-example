package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class reveiveLog {

  private static final String Exchange_Name = "logs";

  public static void main(String[] args) throws Exception{

    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");
    Connection connection = connectionFactory.newConnection();
    Channel channel = connection.createChannel();

    channel.exchangeDeclare(Exchange_Name, "fanout");
    String queueName = channel.queueDeclare().getQueue();
    channel.queueBind(queueName, Exchange_Name, "");

    System.out.println(" [*] waiting for message. to exit press ctrl + c");

    DeliverCallback deliverCallback =(consumerTag, delivery) -> {
      String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [x] received '" +message+ "'");
    };

    channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});

  }

}
