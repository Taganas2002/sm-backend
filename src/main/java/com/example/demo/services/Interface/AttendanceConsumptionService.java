package com.example.demo.services.Interface;

import com.example.demo.dto.response.RunningConsumptionDto;

public interface AttendanceConsumptionService {
  RunningConsumptionDto computeRunning(Long studentId, Long groupId);
}
