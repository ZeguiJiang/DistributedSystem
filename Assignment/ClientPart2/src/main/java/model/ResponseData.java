package model;

import java.util.Objects;

public class ResponseData {

  private long startTime;
  private String requestType = "POST";
  private long latency;
  private int responseCode;

  public ResponseData(long startTime, long latency, int responseCode) {
    this.startTime = startTime;
    this.latency = latency;
    this.responseCode = responseCode;
  }

  public long getStartTime() {
    return startTime;
  }

  public long getLatency() {
    return latency;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  public void setLatency(long latency) {
    this.latency = latency;
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  @Override
  public String toString() {
    return
        "" + startTime +
        ", " + requestType +
        ", " + latency +
        ", " + responseCode;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ResponseData that = (ResponseData) o;
    return startTime == that.startTime && latency == that.latency
        && responseCode == that.responseCode
        && Objects.equals(requestType, that.requestType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(startTime, requestType, latency, responseCode);
  }
}
