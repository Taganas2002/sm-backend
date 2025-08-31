// src/main/java/com/example/demo/dto/response/TeacherSummaryResponse.java
package com.example.demo.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class TeacherSummaryResponse {
  public Long teacherId;
  public String teacherName;
  public BigDecimal unpaidTotal;
  public List<GroupRow> groups;

  public static class GroupRow {
    public Long groupId;
    public String groupName;
    public String shareType; // PERCENT / FIXED_PER_SESSION / FIXED_PER_HOUR / NONE
    public String period;    // optional label if you filter by window
    public BigDecimal accrued;  // total share (window)
    public BigDecimal unpaid;   // UNPAID share (window)
    public BigDecimal paid;     // accrued - unpaid (window)
  }
}
