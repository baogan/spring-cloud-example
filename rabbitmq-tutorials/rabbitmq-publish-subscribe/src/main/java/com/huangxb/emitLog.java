package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class emitLog {

  private static final String Exchange_Name = "logs";

  public static void main(String[] argv) throws Exception {

    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){

      channel.exchangeDeclare(Exchange_Name, "fanout");
      String message = argv.length < 1 ? "info: hello world " : String.join(" ", argv);

      channel.basicPublish(Exchange_Name,"", null, message.getBytes(StandardCharsets.UTF_8));

      System.out.println(" [x] sent '" +message+ "'");
    }
  }

}
