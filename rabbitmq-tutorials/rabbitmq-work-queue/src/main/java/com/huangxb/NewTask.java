package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.nio.charset.StandardCharsets;

public class NewTask {

  private static final String Task_Queue_Name = "task_queue";

  public static void main(String[] argv) throws Exception{

    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){
      channel.queueDeclare(Task_Queue_Name, true, false, false,null);

      String message = String.join(" ", argv);

      channel.basicPublish("", Task_Queue_Name, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(
          StandardCharsets.UTF_8));
      System.out.println(" [x] sent '" +message+ "'");
    }
  }

}
