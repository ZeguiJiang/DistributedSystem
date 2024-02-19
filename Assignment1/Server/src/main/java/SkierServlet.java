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

    if (!isUrlValid(urlParts)) {
      Message message = new Message("The request url is invalid");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      printWriter.write(response.getStatus() + gson.toJson(message));
    } else {
      try {
        StringBuilder stringBuilder = new StringBuilder();
        String str;
        while ((str = request.getReader().readLine()) != null) {
          stringBuilder.append(str);
        }
        LiftRideRecord liftRideRecord = gson.fromJson(stringBuilder.toString(), LiftRideRecord.class);

        if (!isPostValid(liftRideRecord, urlParts)) {
          Message message = new Message("The request body is invalid");
          response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          printWriter.write(response.getStatus() + gson.toJson(message));
          return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        printWriter.write(response.getStatus() + gson.toJson(stringBuilder));


      } catch ( Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        printWriter.write(response.getStatus()  + "The request body is invalid with exception");
      }
    }

  }

  private boolean isUrlValid(String[] urlPath) {
    // validate the request url path according to the API spec
    // example valid path
    // "/skier/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}"

    if (urlPath.length == 8) {
      return urlPath[2].equals("seasons") && urlPath[4].equals("days") && urlPath[6].equals(
          "skiers");
    }
    return false;
  }

  private boolean isPostValid(LiftRideRecord liftRideRecord, String[] urlPath) {
    // Following are the requirements
    // element must not be null

    //  skierID - between 1 and 100000
    //  resortID - between 1 and 10
    //  liftID - between 1 and 40
    //  seasonID - 2024
    //  dayID - 1
    //  time - between 1 and 360

    String dayID = "1";
    String seasonID = "2024";
    int time_Max = 360;
    int time_Min = 1;
    int liftID_Max = 40;
    int liftID_Min = 1;
    int resortID_Max = 10;
    int resortID_Min = 1;
    int skierID_Max = 100000;
    int skierID_Min = 1;
    return Integer.parseInt(urlPath[7]) >= skierID_Min
        && Integer.parseInt(urlPath[7]) <= skierID_Max
        && Integer.parseInt(urlPath[1]) >= resortID_Min
        && Integer.parseInt(urlPath[1]) <= resortID_Max
        && urlPath[5].equals(dayID)
        && urlPath[3].equals(seasonID)
        && liftRideRecord.getTime() >= time_Min
        && liftRideRecord.getTime() <= time_Max
        && liftRideRecord.getLiftID() >= liftID_Min
        && liftRideRecord.getLiftID() <= liftID_Max;
  }
}
