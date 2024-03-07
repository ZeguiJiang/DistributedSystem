import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ConsumerThread implements Runnable {
  Gson gson = new Gson();
  private Connection connection;
  private String queueName;
  private int basicQos;

  public ConsumerThread(Connection connection, String queueName, int basicQos) {

    this.connection = connection;
    this.queueName = queueName;
    this.basicQos = basicQos;
  }

  @Override
  public void run() {

    try {
      Channel channel = connection.createChannel();
      channel.queueDeclare(queueName, false, false, false, null);
      channel.basicQos(basicQos);


      DeliverCallback deliverCallback = (consumerTag, delivery) -> {

        String message = new String(delivery.getBody(), "UTF-8");
        try {
          JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
          Integer skierID = jsonObject.get("skierID").getAsInt();

          if ( Consumer.record.containsKey(skierID) ) {
            Consumer.record.get(skierID).add(jsonObject);
          } else {
            List<JsonObject> jsonObjects = Collections.synchronizedList(new ArrayList<>());
            jsonObjects.add(jsonObject);
            Consumer.record.put(skierID, jsonObjects);
          }
          System.out.println("Successful consume object: " + jsonObject + ", Thread Id is: " + Thread.currentThread().getId());
        } catch ( Exception e) {
          String error_message = String.format("Fail to consume Object %s", message);
          System.out.println(error_message + e);
        }
      };

      channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
