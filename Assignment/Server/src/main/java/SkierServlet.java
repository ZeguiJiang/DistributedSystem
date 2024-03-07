import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.PrintWriter;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import model.LiftRideRecord;
import model.Message;
import com.google.gson.Gson;


@WebServlet(name = "SkierServlet", value = "/SkierServlet")
public class SkierServlet extends HttpServlet {


  private Gson gson = new Gson();
  private String rabbitMQName = "skiRideQueue";
  private String rabbitMQHost = "localhost";
//  ConnectionFactory factory = new ConnectionFactory();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {

    res.setContentType("text/plain");
    String urlPath = req.getPathInfo();

    // check we have a URL!
    if (urlPath == null || urlPath.isEmpty()) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
      res.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");
    // and now validate url path and return the response status code
    // (and maybe also some value if input is valid)

    if (!isUrlValid(urlParts)) {
      res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      res.setStatus(HttpServletResponse.SC_OK);
      // do any sophisticated processing with urlParts which contains all the url params
      // TODO: process url params in `urlParts`
      res.getWriter().write("It works!");
    }
  }




  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

//    factory.setHost(rabbitMQHost);

    PrintWriter printWriter = response.getWriter();
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    String urlPath = request.getPathInfo();

    // check we have a URL!
    if(urlPath == null || urlPath.isEmpty()){
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing parameters");
      return;
    }

    String[] urlParts = urlPath.split("/");

    // check url is valid
    if (!isUrlValid(urlParts)) {
      Message message = new Message("The request url is invalid");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      printWriter.write(response.getStatus() + gson.toJson(message));
      return;
    }

    try {
      StringBuilder stringBuilder = new StringBuilder();
      String str;
      while ((str = request.getReader().readLine()) != null) {
        stringBuilder.append(str);
      }
      LiftRideRecord liftRideRecord = gson.fromJson(stringBuilder.toString(), LiftRideRecord.class);

      if (!isPostValid(liftRideRecord)) {
        Message message = new Message("The request body is invalid");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.write(response.getStatus() + gson.toJson(message));
        return;
      }
//
//      try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()){
//        channel.queueDeclare(rabbitMQName, false, false, false, null);
//        channel.basicPublish("", rabbitMQName, null, liftRideRecord.toString().getBytes());
//        System.out.println(" Sent " + liftRideRecord);
//      } catch (Exception e) {
//        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//        printWriter.write(response.getStatus()  + "Fail to publish message to queue");
//      }

      response.setStatus(HttpServletResponse.SC_OK);
      printWriter.write(response.getStatus() + gson.toJson(stringBuilder));
    } catch ( Exception e) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      printWriter.write(response.getStatus()  + "The request body is invalid with exception");
    }
  }


  private boolean isUrlValid(String[] urlPath) {
    // validate the request url path according to the API spec
    // example valid path
    // "/skier/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}"

    int validUrlPathLength = 8;
    int validUrlPathResortNumberPosition = 1;
    int validUrlPathSeasonPosition = 2;
    int validUrlPathSeasonIDPosition = 3;
    int validUrlPathDaysPosition = 4;
    int validUrlPathDaysIDPosition = 5;
    int validUrlPathSkiersPosition = 6;
    int validUrlPathSkiersIDPosition = 7;

    String dayID = "1";
    String seasonID = "2024";
    int resortID_Max = 10;
    int resortID_Min = 1;
    int skierID_Max = 100000;
    int skierID_Min = 1;

    if (urlPath.length == validUrlPathLength) {
      return urlPath[validUrlPathSeasonPosition].equals("seasons")
          && urlPath[validUrlPathDaysPosition].equals("days")
          && urlPath[validUrlPathSkiersPosition].equals("skiers")
          && urlPath[validUrlPathResortNumberPosition]!= null
          && isNumeric(urlPath[validUrlPathResortNumberPosition])
          && urlPath[validUrlPathSeasonIDPosition]!= null
          && isNumeric(urlPath[validUrlPathSeasonIDPosition])
          && urlPath[validUrlPathDaysIDPosition]!= null
          && isNumeric(urlPath[validUrlPathDaysIDPosition])
          && urlPath[validUrlPathSkiersIDPosition]!= null
          && isNumeric(urlPath[validUrlPathSkiersIDPosition])
          && urlPath[validUrlPathDaysIDPosition].equals(dayID)
          && urlPath[validUrlPathSeasonIDPosition].equals(seasonID)
          && Integer.parseInt(urlPath[validUrlPathResortNumberPosition]) >= resortID_Min
          && Integer.parseInt(urlPath[validUrlPathResortNumberPosition]) <= resortID_Max
          && Integer.parseInt(urlPath[validUrlPathSkiersIDPosition]) >= skierID_Min
          && Integer.parseInt(urlPath[validUrlPathSkiersIDPosition]) <= skierID_Max;
    }
    return false;
  }

  private boolean isPostValid(LiftRideRecord liftRideRecord) {
    //  liftID - between 1 and 40
    //  time - between 1 and 360
    int time_Max = 360;
    int time_Min = 1;
    int liftID_Max = 40;
    int liftID_Min = 1;
    return liftRideRecord.getTime() >= time_Min
        && liftRideRecord.getTime() <= time_Max
        && liftRideRecord.getLiftID() >= liftID_Min
        && liftRideRecord.getLiftID() <= liftID_Max;
  }


  private  boolean isNumeric(String str) {
    // check number is Numeric or not
    try {
      Double.parseDouble(str);
      return true;
    } catch(NumberFormatException e){
      return false;
    }
  }
}



