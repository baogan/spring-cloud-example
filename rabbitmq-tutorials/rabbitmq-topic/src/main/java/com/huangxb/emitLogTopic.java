package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.nio.charset.StandardCharsets;

public class emitLogTopic {

    private static final String Exchange_Name = "topic_logs";

  public static void main(String[] args) throws  Exception{
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    try(Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel()){

      channel.exchangeDeclare(Exchange_Name, "topic");
      String routingKey = getRouting(args);
      String message = getMessage(args);

      channel.basicPublish(Exchange_Name, routingKey, null, message.getBytes(StandardCharsets.UTF_8));

      System.out.println(" [x] sent '" +routingKey+ "':'" +message+ "'");
    }

  }

  private static  String getRouting(String[] args){
    if(args.length < 1)
      return "anonymous.info";
    return args[0];
  }

  private static String getMessage(String[] args){
    if(args.length < 2)
      return "hello duck";
    return joinString(args, " ", 1);
  }

  private static String joinString(String[] strings, String delimiter, int startIndex){
    if(strings.length == 0 || strings.length <= startIndex)
      return "";

    StringBuffer words = new StringBuffer(strings[startIndex]);
    for(int i = startIndex+1; i < strings.length; i++ ){
      words.append(delimiter).append(strings[i]);
    }

    return words.toString();
  }
}
