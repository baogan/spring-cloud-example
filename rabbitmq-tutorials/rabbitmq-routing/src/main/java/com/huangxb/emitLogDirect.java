package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class emitLogDirect {

  private static final String Exchange_Name = "direct_logs";

  public static void main(String[] args) throws Exception{

    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){

      channel.exchangeDeclare(Exchange_Name, "direct");

      String severity = getSeverity(args);
      String message = getMessage(args);

      channel.basicPublish(Exchange_Name, severity, null, message.getBytes(StandardCharsets.UTF_8));
      System.out.println(" [x] sent '" +severity+ "':'" +message+ "'");

    }
  }

  private static String getSeverity(String[] args){
    if(args.length < 1)
      return "info";
    return args[0];
  }

  private static String getMessage(String[] args){
    if(args.length < 2)
      return "hello .";
    return joinString(args, " ", 1);
  }

  private static String joinString(String[] args, String delimiter, int startIndex){
    if(args.length ==0 || args.length <= startIndex)
      return "";

    StringBuffer words = new StringBuffer(args[startIndex]);
    for(int i = startIndex + 1; i< args.length; i++)
      words.append(delimiter).append(args[i]);
    return words.toString();
  }
}
