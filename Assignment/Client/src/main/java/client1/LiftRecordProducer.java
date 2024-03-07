package client1;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import model.LiftRideRecord;
import java.util.concurrent.CountDownLatch;

public class LiftRecordProducer implements Runnable {

  private BlockingQueue<LiftRideRecord> liftRideRecordBlockingQueue;
  private final CountDownLatch countDownLatch;
  private final Random random = new Random();
  private final int numPosts;

  public LiftRecordProducer(BlockingQueue liftRideRecordBlockingQueue, int numPosts, CountDownLatch countDownLatch) {
    this.liftRideRecordBlockingQueue = liftRideRecordBlockingQueue;
    this.numPosts = numPosts;
    this.countDownLatch = countDownLatch;
  }
  @Override
  public void run() {

    for (int i = 0; i < numPosts; i++) {
      LiftRideRecord record = validRideGenerator();
      try {
        liftRideRecordBlockingQueue.put(record);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      countDownLatch.countDown();
    }
  }

  private LiftRideRecord validRideGenerator() {

    String dayID = "1";
    String seasonID = "2024";
    int time_Max = 360;
    int liftID_Max = 40;
    int resortID_Max = 10;
    int skierID_Max = 100000;

    return new LiftRideRecord(random.nextInt(skierID_Max) + 1,
        random.nextInt(resortID_Max) + 1,
        random.nextInt(liftID_Max) + 1,
        seasonID,
        dayID,
        random.nextInt(time_Max) + 1);
  }
}
