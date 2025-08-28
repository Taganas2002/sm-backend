package com.example.demo.dto.request;

import com.example.demo.models.enums.BillingModel;
import com.example.demo.models.enums.TeacherShareType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GroupUpsertRequest {
  private Long schoolId; // optional
  
  @NotBlank private String name;
  @NotBlank private String academicYear;
  @NotNull  private Long teacherId;
  @NotNull  private Long subjectId;
  @NotNull  private Long levelId;
  @NotNull  private Long sectionId;

  // NEW: classroom for this group (optional)
  private Long classroomId;

  private Boolean privateGroup;
  private Boolean revisionGroup;
  private Boolean active;
  private Integer capacity;

  @NotNull private BillingModel billingModel;
  private Integer sessionsPerMonth;
  private BigDecimal monthlyFee;
  private BigDecimal sessionCost;
  private BigDecimal hourlyCost;
  private Integer sessionDurationMin;

  private TeacherShareType teacherShareType;
  private BigDecimal teacherShareValue;

  private Boolean allowCheckInWithoutBalance;
  private Boolean requireFirstLessonAttendance;
  private Boolean registerFirstAbsence;
  private Boolean lastLessonReminder;
  private Integer absenceStopThreshold;
  private Boolean warnDuplicateCard;
  private Boolean allowMultipleCheckinsPerDay;

  private LocalDate startDate;
  private String notes;

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  // getters/setters (all)
  public Long getSchoolId(){ return schoolId; } public void setSchoolId(Long v){ this.schoolId = v; }
  public String getAcademicYear(){ return academicYear; } public void setAcademicYear(String v){ this.academicYear = v; }
  public Long getTeacherId(){ return teacherId; } public void setTeacherId(Long v){ this.teacherId = v; }
  public Long getSubjectId(){ return subjectId; } public void setSubjectId(Long v){ this.subjectId = v; }
  public Long getLevelId(){ return levelId; } public void setLevelId(Long v){ this.levelId = v; }
  public Long getSectionId(){ return sectionId; } public void setSectionId(Long v){ this.sectionId = v; }

  public Long getClassroomId() { return classroomId; }
  public void setClassroomId(Long classroomId) { this.classroomId = classroomId; }

  public Boolean getPrivateGroup(){ return privateGroup; } public void setPrivateGroup(Boolean v){ this.privateGroup = v; }
  public Boolean getRevisionGroup(){ return revisionGroup; } public void setRevisionGroup(Boolean v){ this.revisionGroup = v; }
  public Boolean getActive(){ return active; } public void setActive(Boolean v){ this.active = v; }
  public Integer getCapacity(){ return capacity; } public void setCapacity(Integer v){ this.capacity = v; }
  public BillingModel getBillingModel(){ return billingModel; } public void setBillingModel(BillingModel v){ this.billingModel = v; }
  public Integer getSessionsPerMonth(){ return sessionsPerMonth; } public void setSessionsPerMonth(Integer v){ this.sessionsPerMonth = v; }
  public BigDecimal getMonthlyFee(){ return monthlyFee; } public void setMonthlyFee(BigDecimal v){ this.monthlyFee = v; }
  public BigDecimal getSessionCost(){ return sessionCost; } public void setSessionCost(BigDecimal v){ this.sessionCost = v; }
  public BigDecimal getHourlyCost(){ return hourlyCost; } public void setHourlyCost(BigDecimal v){ this.hourlyCost = v; }
  public Integer getSessionDurationMin(){ return sessionDurationMin; } public void setSessionDurationMin(Integer v){ this.sessionDurationMin = v; }

  public TeacherShareType getTeacherShareType(){ return teacherShareType; } public void setTeacherShareType(TeacherShareType v){ this.teacherShareType = v; }
  public BigDecimal getTeacherShareValue(){ return teacherShareValue; } public void setTeacherShareValue(BigDecimal v){ this.teacherShareValue = v; }

  public Boolean getAllowCheckInWithoutBalance(){ return allowCheckInWithoutBalance; } public void setAllowCheckInWithoutBalance(Boolean v){ this.allowCheckInWithoutBalance = v; }
  public Boolean getRequireFirstLessonAttendance(){ return requireFirstLessonAttendance; } public void setRequireFirstLessonAttendance(Boolean v){ this.requireFirstLessonAttendance = v; }
  public Boolean getRegisterFirstAbsence(){ return registerFirstAbsence; } public void setRegisterFirstAbsence(Boolean v){ this.registerFirstAbsence = v; }
  public Boolean getLastLessonReminder(){ return lastLessonReminder; } public void setLastLessonReminder(Boolean v){ this.lastLessonReminder = v; }
  public Integer getAbsenceStopThreshold(){ return absenceStopThreshold; } public void setAbsenceStopThreshold(Integer v){ this.absenceStopThreshold = v; }
  public Boolean getWarnDuplicateCard(){ return warnDuplicateCard; } public void setWarnDuplicateCard(Boolean v){ this.warnDuplicateCard = v; }
  public Boolean getAllowMultipleCheckinsPerDay(){ return allowMultipleCheckinsPerDay; } public void setAllowMultipleCheckinsPerDay(Boolean v){ this.allowMultipleCheckinsPerDay = v; }

  public LocalDate getStartDate(){ return startDate; } public void setStartDate(LocalDate v){ this.startDate = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
