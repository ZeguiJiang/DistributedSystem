package client1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import model.LiftRideRecord;

public class SingleThreadCall {

  public static void main(String[] args) throws InterruptedException {

//    final String ipAddress = "localhost:8080";
    final String ipAddress = "34.220.164.24:8080";

    BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue = new LinkedBlockingQueue<>();
    final Integer requestCount = 100;

    AtomicInteger requestSuccessCount = new AtomicInteger(0);
    AtomicInteger requestFailureCount = new AtomicInteger(0);

    long threadStartTime = System.currentTimeMillis();

    CountDownLatch producerLatch = new CountDownLatch(1);
    LiftRecordProducer liftRecordProducer = new LiftRecordProducer(liftRideRecordBlockingQueue, requestCount, producerLatch);
    Thread producerThread = new Thread(liftRecordProducer);
    producerThread.start();

    CountDownLatch consumerLatch = new CountDownLatch(1);
    ApiCaller singleThreadCall = new ApiCaller(ipAddress, liftRideRecordBlockingQueue, requestCount, requestSuccessCount, requestFailureCount, consumerLatch,5);
    Thread singleThread = new Thread(singleThreadCall);
    singleThread.start();
    consumerLatch.await();

    long threadEndTime = System.currentTimeMillis();
    long latency = threadEndTime-threadStartTime;

    System.out.println("Summary:");
    System.out.println("Number of thread:" + "1");
    System.out.println("Number of successful requests: "+ requestSuccessCount.get());
    System.out.println("Number of fail requests: "+ requestFailureCount.get());
    System.out.println("Total run time: " + latency);
    System.out.println("Response Time: "+((double)(latency)/(requestSuccessCount.get()+requestFailureCount.get())) + " ms/request");
    System.out.println("RPS:     " +  (requestSuccessCount.get() + requestFailureCount.get() )  * 1000L / latency + " requests/second");
  }
}
