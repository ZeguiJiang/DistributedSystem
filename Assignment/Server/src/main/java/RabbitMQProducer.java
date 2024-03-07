import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import model.LiftRideRecord;


public class RabbitMQProducer {
  private String queue_name;
  private String host;
  public ConnectionFactory factory;

  public RabbitMQProducer (String queueName, String rabbitMQHost ){
    this.queue_name = queueName;
    this.host = rabbitMQHost;
  }

  public void init(){
    factory = new ConnectionFactory();
    factory.setHost(host);
  }


   public void publishLiftRecordToQueue(LiftRideRecord record) throws Exception {

    try (Connection connection = factory.newConnection();Channel channel = connection.createChannel()) {
      channel.queueDeclare(queue_name, false, false, false, null);
      channel.basicPublish("", queue_name, null, record.toString().getBytes());
      System.out.println(" Sent " + record);
    }
   }

//  public static void main(String[] argv) throws Exception {
//
////    ConnectionFactory factory = new ConnectionFactory();
////    factory.setHost("localhost");
//    try (Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel()) {
//
//      channel.queueDeclare(queue_name, false, false, false, null);
//      String message = "diuleilaomou";
//      channel.basicPublish("", queue_name, null, message.getBytes());
//      System.out.println(" [x] Sent '" + message + "'");
//
//    }
//  }
}