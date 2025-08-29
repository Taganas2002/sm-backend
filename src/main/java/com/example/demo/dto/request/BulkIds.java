// src/main/java/com/example/demo/dto/request/BulkIds.java
package com.example.demo.dto.request;

import java.util.List;

public class BulkIds {
  private List<Long> studentIds;
  public List<Long> getStudentIds() { return studentIds; }
  public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
