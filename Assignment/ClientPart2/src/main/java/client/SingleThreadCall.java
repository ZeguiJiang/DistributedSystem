package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import model.LiftRideRecord;
import model.ResponseData;

public class SingleThreadCall {

  public static void main(String[] args) throws InterruptedException {

//    final String ipAddress = "localhost:8080";
    final String ipAddress = "Server-LB-1567985433.us-west-2.elb.amazonaws.com";
    BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue = new LinkedBlockingQueue<>();
    final int requestCount = 1;
    AtomicInteger requestSuccessCount = new AtomicInteger(0);
    AtomicInteger requestFailureCount = new AtomicInteger(0);
    List<ResponseData> responseDataList = Collections.synchronizedList(new ArrayList<>());

    long threadStartTime = System.currentTimeMillis();

    CountDownLatch producerLatch = new CountDownLatch(1);
    LiftRecordProducer liftRecordProducer = new LiftRecordProducer(liftRideRecordBlockingQueue, requestCount, producerLatch);
    Thread producerThread = new Thread(liftRecordProducer);
    producerThread.start();

    CountDownLatch consumerLatch = new CountDownLatch(1);
    ApiCaller singleThreadCall = new ApiCaller(ipAddress, liftRideRecordBlockingQueue, requestCount, requestSuccessCount, requestFailureCount, consumerLatch,5, responseDataList);
    Thread singleThread = new Thread(singleThreadCall);
    singleThread.start();
    consumerLatch.await();

    long threadEndTime = System.currentTimeMillis();
    long latency = threadEndTime-threadStartTime;


    System.out.println("Summary:");
    System.out.println("Number of successful requests: "+ requestSuccessCount.get());
    System.out.println("Number of fail requests: "+ requestFailureCount.get());
    System.out.println("Total run time: " + latency);
    System.out.println("Response Time: "+((double)(latency)/(requestSuccessCount.get()+requestFailureCount.get())) + " ms/request");
    System.out.println("RPS:     " +  (requestSuccessCount.get() + requestFailureCount.get() )  * 1000L / latency + " requests/second");
  }

}
