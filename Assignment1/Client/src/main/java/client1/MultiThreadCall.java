package client1;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import model.LiftRideRecord;

public class MultiThreadCall {

  public static void main(String[] args) throws InterruptedException {

    final String ipAddress = "localhost:8080";
    final int numberOfThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);
    BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue = new LinkedBlockingQueue<>();
    final int requestCount = 200000;
    final int requestCountPerThread = requestCount / numberOfThread;
    AtomicInteger requestSuccessCount = new AtomicInteger(0);
    AtomicInteger requestFailureCount = new AtomicInteger(0);

    long threadStartTime = System.currentTimeMillis();

    CountDownLatch producerLatch = new CountDownLatch(1);
    LiftRecordProducer liftRecordProducer = new LiftRecordProducer(liftRideRecordBlockingQueue, requestCount, producerLatch);
    Thread producerThread = new Thread(liftRecordProducer);
    producerThread.start();


    CountDownLatch consumerLatch = new CountDownLatch(1);
    for (int i = 0; i < numberOfThread; i++) {
      executor.submit( new ApiCaller(ipAddress, liftRideRecordBlockingQueue, requestCountPerThread, requestSuccessCount, requestFailureCount, consumerLatch,5));
    }
    consumerLatch.await();

    long threadEndTime = System.currentTimeMillis();
    long latency = threadEndTime-threadStartTime;

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.HOURS);

    System.out.println("Summary:");
    System.out.println("Number of thread: "+ numberOfThread);
    System.out.println("Number of successful requests: "+ requestSuccessCount.get());
    System.out.println("Number of fail requests: "+ requestFailureCount.get());
    System.out.println("Total run time: " + latency);
    System.out.println("Response Time: "+((double)(latency)/(requestSuccessCount.get()+requestFailureCount.get())) + " ms/request");
    System.out.println("RPS: " +  (requestSuccessCount.get() + requestFailureCount.get() )  * 1000L / latency + " requests/second");
  }
}
