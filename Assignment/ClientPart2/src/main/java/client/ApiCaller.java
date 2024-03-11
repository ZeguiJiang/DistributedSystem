package client;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import model.LiftRideRecord;
import model.ResponseData;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.commons.httpclient.HttpStatus.SC_BAD_REQUEST;
import static org.apache.commons.httpclient.HttpStatus.SC_NOT_FOUND;


public class ApiCaller implements Runnable {

  /*
  implement the logic for call API
   */

  private int retryCount;

  private String ipAddress;
  private Integer requestCount;
  private BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue;
  private AtomicInteger requestSuccessCount;
  private AtomicInteger requestFailureCount;
  private CountDownLatch startLatch;

  private List<ResponseData> responseDataList;

  /*
  Constructor
   */
  public ApiCaller(String ipAddress,
      BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue,
      Integer requestCount,
      AtomicInteger requestSuccessCount,
      AtomicInteger requestFailureCount,
      CountDownLatch startLatch,
      Integer retryCount,
      List<ResponseData> responseDataList) {
    this.requestCount = requestCount;
    this.ipAddress = ipAddress;
    this.liftRideRecordBlockingQueue = liftRideRecordBlockingQueue;
    this.requestSuccessCount = requestSuccessCount;
    this.requestFailureCount = requestFailureCount;
    this.startLatch = startLatch;
    this.retryCount = retryCount;
    this.responseDataList = responseDataList;
  }


  @Override
  public void run() {


    SkiersApi skiersApi = new SkiersApi();
    ApiClient apiClient = skiersApi.getApiClient();
//    apiClient.setBasePath("http://" + this.ipAddress + "/LIftServer_war_exploded/"); // "http://localhost:8080/Server_war_exploded/"
    apiClient.setBasePath("http://" + this.ipAddress + "/LIftServer_war/");
//    apiClient.setBasePath("http://" + this.ipAddress + "/Server_war/");
//    apiClient.setBasePath("http://" + this.ipAddress + "/Server_war_exploded/"); // "http://localhost:8080/Server_war_exploded/"


    int tmpSuccessCount = 0;
    int tmpRequestCount = 0;

    while (tmpRequestCount < requestCount) {

      try {
        LiftRideRecord record = liftRideRecordBlockingQueue.take();
        boolean checkSuccess = postApiCall(skiersApi, record);
        if ( checkSuccess ) {
          tmpSuccessCount += 1;
        }

      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      tmpRequestCount += 1;
    }
    this.requestSuccessCount.getAndAdd(tmpSuccessCount);
    this.requestFailureCount.getAndAdd(tmpRequestCount - tmpSuccessCount);
    this.startLatch.countDown();
  }


  private boolean postApiCall(SkiersApi skiersApi, LiftRideRecord liftRideRecord) {
    // post an api call with retry strategy

    int tryCount = 0;
    LiftRide liftRide = new LiftRide();
    liftRide.setTime(liftRideRecord.getTime());
    liftRide.setLiftID(liftRideRecord.getLiftID());
    long startTime = System.currentTimeMillis();

    while (tryCount < retryCount) {
      try {
        ApiResponse<Void> response = skiersApi.writeNewLiftRideWithHttpInfo(liftRide,
            liftRideRecord.getResortID(),
            liftRideRecord.getSeasonID(),
            liftRideRecord.getDayID(),
            liftRideRecord.getSkierID());

        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        responseDataList.add(new ResponseData(startTime, latency, response.getStatusCode()));
        if (response.getStatusCode() == HTTP_OK || response.getStatusCode() == HTTP_CREATED) {

          return true;
        } else if (response.getStatusCode() == SC_NOT_FOUND || response.getStatusCode() == SC_BAD_REQUEST) {
          tryCount += 1;
        }
      } catch (ApiException e) {
        tryCount += 1;
        e.printStackTrace();
        long endTime = System.currentTimeMillis();
        long latency = endTime - startTime;
        responseDataList.add(new ResponseData(startTime, latency, e.getCode()));
      }
    }
    return false;
  }
}

