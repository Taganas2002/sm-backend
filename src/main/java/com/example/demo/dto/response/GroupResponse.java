package com.example.demo.dto.response;

import com.example.demo.models.enums.BillingModel;
import com.example.demo.models.enums.TeacherShareType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GroupResponse {
  private Long id;
  private Long schoolId;
  private String academicYear;
  private Long teacherId;
  private Long subjectId;
  private Long levelId;
  private Long sectionId;

  private boolean privateGroup;
  private boolean revisionGroup;
  private boolean active;
  private Integer capacity;

  private BillingModel billingModel;
  private Integer sessionsPerMonth;
  private BigDecimal monthlyFee;
  private BigDecimal sessionCost;
  private BigDecimal hourlyCost;
  private Integer sessionDurationMin;

  private TeacherShareType teacherShareType;
  private BigDecimal teacherShareValue;

  private boolean allowCheckInWithoutBalance;
  private boolean requireFirstLessonAttendance;
  private boolean registerFirstAbsence;
  private boolean lastLessonReminder;
  private Integer absenceStopThreshold;
  private boolean warnDuplicateCard;
  private boolean allowMultipleCheckinsPerDay;

  private LocalDate startDate;
  private String notes;

  // getters/setters (all)
  public Long getId(){ return id; } public void setId(Long v){ this.id = v; }
  public Long getSchoolId(){ return schoolId; } public void setSchoolId(Long v){ this.schoolId = v; }
  public String getAcademicYear(){ return academicYear; } public void setAcademicYear(String v){ this.academicYear = v; }
  public Long getTeacherId(){ return teacherId; } public void setTeacherId(Long v){ this.teacherId = v; }
  public Long getSubjectId(){ return subjectId; } public void setSubjectId(Long v){ this.subjectId = v; }
  public Long getLevelId(){ return levelId; } public void setLevelId(Long v){ this.levelId = v; }
  public Long getSectionId(){ return sectionId; } public void setSectionId(Long v){ this.sectionId = v; }
  public boolean isPrivateGroup(){ return privateGroup; } public void setPrivateGroup(boolean v){ this.privateGroup = v; }
  public boolean isRevisionGroup(){ return revisionGroup; } public void setRevisionGroup(boolean v){ this.revisionGroup = v; }
  public boolean isActive(){ return active; } public void setActive(boolean v){ this.active = v; }
  public Integer getCapacity(){ return capacity; } public void setCapacity(Integer v){ this.capacity = v; }
  public BillingModel getBillingModel(){ return billingModel; } public void setBillingModel(BillingModel v){ this.billingModel = v; }
  public Integer getSessionsPerMonth(){ return sessionsPerMonth; } public void setSessionsPerMonth(Integer v){ this.sessionsPerMonth = v; }
  public BigDecimal getMonthlyFee(){ return monthlyFee; } public void setMonthlyFee(BigDecimal v){ this.monthlyFee = v; }
  public BigDecimal getSessionCost(){ return sessionCost; } public void setSessionCost(BigDecimal v){ this.sessionCost = v; }
  public BigDecimal getHourlyCost(){ return hourlyCost; } public void setHourlyCost(BigDecimal v){ this.hourlyCost = v; }
  public Integer getSessionDurationMin(){ return sessionDurationMin; } public void setSessionDurationMin(Integer v){ this.sessionDurationMin = v; }
  public TeacherShareType getTeacherShareType(){ return teacherShareType; } public void setTeacherShareType(TeacherShareType v){ this.teacherShareType = v; }
  public BigDecimal getTeacherShareValue(){ return teacherShareValue; } public void setTeacherShareValue(BigDecimal v){ this.teacherShareValue = v; }
  public boolean isAllowCheckInWithoutBalance(){ return allowCheckInWithoutBalance; } public void setAllowCheckInWithoutBalance(boolean v){ this.allowCheckInWithoutBalance = v; }
  public boolean isRequireFirstLessonAttendance(){ return requireFirstLessonAttendance; } public void setRequireFirstLessonAttendance(boolean v){ this.requireFirstLessonAttendance = v; }
  public boolean isRegisterFirstAbsence(){ return registerFirstAbsence; } public void setRegisterFirstAbsence(boolean v){ this.registerFirstAbsence = v; }
  public boolean isLastLessonReminder(){ return lastLessonReminder; } public void setLastLessonReminder(boolean v){ this.lastLessonReminder = v; }
  public Integer getAbsenceStopThreshold(){ return absenceStopThreshold; } public void setAbsenceStopThreshold(Integer v){ this.absenceStopThreshold = v; }
  public boolean isWarnDuplicateCard(){ return warnDuplicateCard; } public void setWarnDuplicateCard(boolean v){ this.warnDuplicateCard = v; }
  public boolean isAllowMultipleCheckinsPerDay(){ return allowMultipleCheckinsPerDay; } public void setAllowMultipleCheckinsPerDay(boolean v){ this.allowMultipleCheckinsPerDay = v; }
  public LocalDate getStartDate(){ return startDate; } public void setStartDate(LocalDate v){ this.startDate = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }
}
