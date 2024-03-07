package model;

public class LiftRideRecord {
  private int skierID;
  private int resortID;
  private int liftID;
  private String seasonID;
  private String dayID;
  private int time;

  public LiftRideRecord(int skierID, int resortID,  int liftID, String seasonID, String dayID, int time) {
    this.skierID = skierID;
    this.resortID = resortID;
    this.liftID = liftID;
    this.seasonID = seasonID;
    this.dayID = dayID;
    this.time = time;
  }

  public void setDayID(String dayID) {
    this.dayID = dayID;
  }

  public void setLiftID(int liftID) {
    this.liftID = liftID;
  }

  public void setResortID(int resortID) {
    this.resortID = resortID;
  }

  public void setSeasonID(String seasonID) {
    this.seasonID = seasonID;
  }

  public void setSkierID(int skierID) {
    this.skierID = skierID;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public int getLiftID() {
    return liftID;
  }

  public int getResortID() {
    return resortID;
  }


  public int getSkierID() {
    return skierID;
  }

  public int getTime() {
    return time;
  }

  public String getDayID() {
    return dayID;
  }

  public String getSeasonID() {
    return seasonID;
  }

  @Override
  public String toString() {
    return "LiftRideRecord{" +
        "skierID=" + skierID +
        ", resortID=" + resortID +
        ", liftID=" + liftID +
        ", seasonID='" + seasonID + '\'' +
        ", dayID='" + dayID + '\'' +
        ", time=" + time +
        '}';
  }
}
