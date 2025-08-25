package com.example.demo.config;

import com.example.demo.services.Interface.AttendanceService;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AttendanceAutoCloseJob {
  private static final Logger log = LoggerFactory.getLogger(AttendanceAutoCloseJob.class);
  private final AttendanceService attendanceService;

  public AttendanceAutoCloseJob(AttendanceService attendanceService){
    this.attendanceService = attendanceService;
  }

  // every 60 seconds, close expired sessions (end time + grace)
  @Scheduled(fixedDelay = 60_000L)
  public void run() {
    int changed = attendanceService.autoCloseExpiredSessions();
    if (changed > 0) log.info("Auto-closed {} attendance sessions", changed);
  }
}
