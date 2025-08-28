package com.example.demo.services.Implementation;

import com.example.demo.dto.response.RunningConsumptionDto;
import com.example.demo.models.StudyGroup;
import com.example.demo.repository.StudentAttendanceRepo;
import com.example.demo.repository.StudyGroupRepo;
import com.example.demo.services.Interface.AttendanceConsumptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AttendanceConsumptionServiceImpl implements AttendanceConsumptionService {

  private final StudentAttendanceRepo studentAttendanceRepo;
  private final StudyGroupRepo studyGroupRepo;

  public AttendanceConsumptionServiceImpl(StudentAttendanceRepo studentAttendanceRepo,
                                          StudyGroupRepo studyGroupRepo) {
    this.studentAttendanceRepo = studentAttendanceRepo;
    this.studyGroupRepo = studyGroupRepo;
  }

  @Override
  @Transactional(readOnly = true)
  public RunningConsumptionDto computeRunning(Long studentId, Long groupId) {
    StudyGroup group = studyGroupRepo.findById(groupId)
        .orElseThrow(() -> new IllegalArgumentException("Group not found"));

    int attended = (int) studentAttendanceRepo.countByStudentIdAndSessionGroupId(studentId, groupId);
    int quota = group.getSessionsPerMonth() == null ? 0 : group.getSessionsPerMonth();

    int cyclesCompleted = 0;
    int currentCycleAttended = attended;
    int remaining = 0;
    boolean needsPayment = false;
    String ratio;

    if (quota > 0) {
      cyclesCompleted = attended / quota;
      currentCycleAttended = attended % quota;
      remaining = (attended == 0) ? quota : (quota - (attended % quota == 0 ? quota : currentCycleAttended));
      needsPayment = (attended > 0) && (attended % quota == 0);
      ratio = currentCycleAttended + "/" + quota;
    } else {
      ratio = String.valueOf(attended);
    }

    RunningConsumptionDto dto = new RunningConsumptionDto();
    dto.setStudentId(studentId);
    dto.setGroupId(groupId);
    dto.setAttended(attended);
    dto.setQuota(quota);
    dto.setRatio(ratio);
    dto.setCyclesCompleted(cyclesCompleted);
    dto.setCurrentCycleAttended(currentCycleAttended);
    dto.setRemainingInCurrentCycle(remaining);
    dto.setNeedsPayment(needsPayment);
    return dto;
  }
}
