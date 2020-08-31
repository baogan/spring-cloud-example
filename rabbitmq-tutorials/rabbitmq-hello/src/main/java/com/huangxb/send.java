package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class send {

    private final static String Queue_Name = "huangxb";

    public static void main(String[] argv) throws Exception{

      ConnectionFactory connectionFactory = new ConnectionFactory();

      connectionFactory.setHost("localhost");

      try(Connection connection = connectionFactory.newConnection();
          Channel channel = connection.createChannel()){

          channel.queueDeclare(Queue_Name,false,false,false,null);
          String message = "hello burt 666";
          channel.basicPublish("", Queue_Name, null, message.getBytes(StandardCharsets.UTF_8));

          System.out.println(" Sent '" +message+ "'");
      }
    }
}
