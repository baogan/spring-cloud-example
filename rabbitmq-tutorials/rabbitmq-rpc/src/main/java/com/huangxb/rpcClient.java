package com.huangxb;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class rpcClient implements AutoCloseable {

  private Connection connection;
  private Channel channel;
  private String requestQueueName = "rpc_queue";

  public static void main(String[] argv){
    try(rpcClient rpcClient = new rpcClient()){
      for(int i = 0; i < 32; i++){
        String i_str = Integer.toString(i);
        System.out.println(" [x] requesting fib(" +i_str+ ")");
        String response = rpcClient.call(i_str);
        System.out.println(" [.] got '" +response+ "'");
      }
    }catch(IOException | TimeoutException | InterruptedException e){
      e.printStackTrace();
    }
  }

  public rpcClient() throws IOException, TimeoutException{
    ConnectionFactory connectionFactory = new ConnectionFactory();
    connectionFactory.setHost("localhost");

    connection = connectionFactory.newConnection();
    channel = connection.createChannel();
  }

  public String call(String message) throws IOException, InterruptedException {
    final String corrId = UUID.randomUUID().toString();
    String replyQueueName = channel.queueDeclare().getQueue();

    AMQP.BasicProperties properties = new BasicProperties
        .Builder()
        .correlationId(corrId)
        .replyTo(replyQueueName)
        .build();

    channel.basicPublish("", requestQueueName, properties, message.getBytes(StandardCharsets.UTF_8));

    final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);

    String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
          if(delivery.getProperties().getCorrelationId().equals(corrId)){
            response.offer(new String(delivery.getBody(), StandardCharsets.UTF_8));
          }
        }, consumerTag -> { }
        );

    String result = response.take();
    channel.basicCancel(ctag);
    return result;
  }

  @Override
  public void close() throws IOException {
    connection.close();
  }
}
