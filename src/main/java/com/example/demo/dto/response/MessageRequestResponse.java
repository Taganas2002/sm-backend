package com.example.demo.dto.response;

public class MessageRequestResponse {
  private String message;

  public MessageRequestResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
