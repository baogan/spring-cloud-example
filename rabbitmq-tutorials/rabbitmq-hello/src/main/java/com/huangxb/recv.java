package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class recv {

    private final static String Queue_Name = "huangxb";

    public static void main(String[] argv) throws Exception{

      ConnectionFactory connectionFactory = new ConnectionFactory();
      connectionFactory.setHost("localhost");

      Connection connection = connectionFactory.newConnection();
      Channel channel = connection.createChannel();

      channel.queueDeclare(Queue_Name, false, false, false, null);
      System.out.println(" [*] waiting for message. To exit press ctrl + c");

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        System.out.println(" [x] received '" +message+ "'");

      };

      channel.basicConsume(Queue_Name, true, deliverCallback, consumerTag -> { });
    }
}
