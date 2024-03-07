


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import com.google.gson.JsonObject;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;


public class Consumer {

  public static final Map<Integer, List<JsonObject>> record = new ConcurrentHashMap<>();

  public static void main(String[] argv) throws Exception {

    int numberOfThread = 1;
    int basicqos = 10;
    String QueueName = "";
    ConnectionFactory factory = new ConnectionFactory();
    Connection connection = factory.newConnection();


    ExecutorService multiThreadPool = Executors.newFixedThreadPool(numberOfThread);

    for (int num = 0; num < numberOfThread; num ++) {
      multiThreadPool.execute(new ConsumerThread(connection, queu, basicqos));
    }
  }


}
