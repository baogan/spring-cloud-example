package com.huangxb;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.nio.charset.StandardCharsets;

public class Work {

    private static final String Task_Queue_Name = "task_queue";

    public static void main(String[] argv) throws Exception{

      ConnectionFactory connectionFactory = new ConnectionFactory();
      connectionFactory.setHost("localhost");
      final Connection connection = connectionFactory.newConnection();
      final Channel channel = connection.createChannel();

      channel.queueDeclare(Task_Queue_Name, true, false, false, null);
      System.out.println(" [*] waiting for message. to exit press ctrl + c");

      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) ->{
        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);

        System.out.println(" [x] received '" +message+ "'");

        try{
          doWork(message);
        }finally {
          System.out.println(" [x] done");
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
      };
      channel.basicConsume(Task_Queue_Name, false, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task){
      for(char ch: task.toCharArray()){
        if(ch == '.'){
          try{
            Thread.sleep(1000);
          }catch (InterruptedException _ignored){
            Thread.currentThread().interrupt();
          }
        }
      }
    }

}
