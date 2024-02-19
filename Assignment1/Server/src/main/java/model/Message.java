package model;

public class Message {

  // response message body

  private String message = "";

  public Message(String message) {
    this.message = message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public static void main(String[] args) {
    System.out.println("1".equals(1));
  }
}
