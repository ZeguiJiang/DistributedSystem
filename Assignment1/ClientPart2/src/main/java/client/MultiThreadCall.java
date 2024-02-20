package client;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import model.LiftRideRecord;
import model.ResponseData;

public class MultiThreadCall {

  public static void main(String[] args) throws InterruptedException {

//    final String ipAddress = "localhost:8080";
    final String ipAddress = "18.236.136.1:8080";
    final int numberOfThread = 100;
    ExecutorService executor = Executors.newFixedThreadPool(numberOfThread);
    BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue = new LinkedBlockingQueue<>();
    final int requestCount = 200000;
    final int requestCountPerThread = requestCount / numberOfThread;
    AtomicInteger requestSuccessCount = new AtomicInteger(0);
    AtomicInteger requestFailureCount = new AtomicInteger(0);
    List<ResponseData> responseDataList = Collections.synchronizedList(new ArrayList<>());




    long threadStartTime = System.currentTimeMillis();

    CountDownLatch producerLatch = new CountDownLatch(1);
    LiftRecordProducer liftRecordProducer = new LiftRecordProducer(liftRideRecordBlockingQueue, requestCount, producerLatch);
    Thread producerThread = new Thread(liftRecordProducer);
    producerThread.start();


    CountDownLatch consumerLatch = new CountDownLatch(1);
    for (int i = 0; i < numberOfThread; i++) {
      executor.submit( new ApiCaller(ipAddress, liftRideRecordBlockingQueue, requestCountPerThread, requestSuccessCount, requestFailureCount, consumerLatch,5, responseDataList));
    }
    consumerLatch.await();

    long threadEndTime = System.currentTimeMillis();
    long latency = threadEndTime-threadStartTime;

    executor.shutdown();
    executor.awaitTermination(1, TimeUnit.HOURS);

    String localPath = "responseData.csv";
    writeRawData(responseDataList, localPath);

    calculateStatistic(responseDataList);

    System.out.println("Summary:");
    System.out.println("Number of thread: "+ numberOfThread);
    System.out.println("Number of successful requests: "+ requestSuccessCount.get());
    System.out.println("Number of fail requests: "+ requestFailureCount.get());
    System.out.println("Total run time: " + latency);
    System.out.println("Response Time: "+((double)(latency)/(requestSuccessCount.get()+requestFailureCount.get())) + " ms/request");
    System.out.println("RPS: " +  (requestSuccessCount.get() + requestFailureCount.get() )  * 1000L / latency + " requests/second");
  }



  private static void writeRawData(List<ResponseData> responseDataList, String localPath) {

    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(localPath, true))) {
      for (ResponseData record : responseDataList) {
        bufferedWriter.write(record.toString());
        bufferedWriter.newLine(); // Ensure each record is on a new line.
      }
    } catch (IOException e) {
      System.err.println("Failed to write records to file: " + e.getMessage());
    }
  }

  private static void calculateStatistic(List<ResponseData> responseDataList) {

    List<Long> latencies = new ArrayList<>();
    double total = 0;
    for (ResponseData responseData: responseDataList) {
      latencies.add(responseData.getLatency());
      total += responseData.getLatency();
    }
    Collections.sort(latencies);

    double mean = total/ latencies.size();
    double median = latencies.get(latencies.size() / 2);
    long p99 = latencies.get((int) (latencies.size() * 0.99));
    long min = latencies.get(0);
    long max = latencies.get(latencies.size() - 1);
    System.out.println("Statistic Metrics");
    System.out.println("Mean Response Time: " + mean + " ms");
    System.out.println("Median Response Time: " + median + " ms");
    System.out.println("P99 Response Time: " + p99 + " ms");
    System.out.println("Min Response Time: " + min + " ms");
    System.out.println("Max Response Time: " + max + " ms");
  }

}
