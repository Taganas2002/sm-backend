package com.example.demo.models;

import com.example.demo.models.enums.BillingModel;
import com.example.demo.models.enums.TeacherShareType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "class_groups") // << renamed from "groups"
public class StudyGroup {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "school_id")
  private School school;

  @Column(name = "academic_year", length = 20, nullable = false)
  private String academicYear;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "teacher_id", nullable = false)
  private Teacher teacher;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "subject_id", nullable = false)
  private Subject subject;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "level_id", nullable = false)
  private Level level;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "section_id", nullable = false)
  private Section section;

  @Column(name = "private_group", nullable = false)  private boolean privateGroup = false;
  @Column(name = "revision_group", nullable = false) private boolean revisionGroup = false;
  @Column(name = "active", nullable = false)         private boolean active = true;
  @Column(name = "capacity")                         private Integer capacity;

  @Enumerated(EnumType.STRING) @Column(name="billing_model", nullable = false)
  private BillingModel billingModel;
  @Column(name="sessions_per_month")   private Integer sessionsPerMonth;
  @Column(name="monthly_fee")          private BigDecimal monthlyFee;
  @Column(name="session_cost")         private BigDecimal sessionCost;
  @Column(name="hourly_cost")          private BigDecimal hourlyCost;
  @Column(name="session_duration_min") private Integer sessionDurationMin;

  @Enumerated(EnumType.STRING) @Column(name="teacher_share_type", nullable=false)
  private TeacherShareType teacherShareType = TeacherShareType.NONE;
  @Column(name="teacher_share_value") private BigDecimal teacherShareValue;

  @Column(name="allow_checkin_without_balance", nullable=false) private boolean allowCheckInWithoutBalance = true;
  @Column(name="require_first_lesson_attendance", nullable=false) private boolean requireFirstLessonAttendance = false;
  @Column(name="register_first_absence", nullable=false) private boolean registerFirstAbsence = false;
  @Column(name="last_lesson_reminder", nullable=false) private boolean lastLessonReminder = false;
  @Column(name="absence_stop_threshold") private Integer absenceStopThreshold;
  @Column(name="warn_duplicate_card", nullable=false) private boolean warnDuplicateCard = true;
  @Column(name="allow_multiple_checkins_per_day", nullable=false) private boolean allowMultipleCheckinsPerDay = false;
  
//Day-4 additions
 @Column(name="approval_required", nullable=false)  private boolean approvalRequired = true;
 @Column(name="auto_close_grace_min", nullable=false) private int autoCloseGraceMin = 10;

  @Column(name="start_date") private LocalDate startDate;
  @Lob private String notes;

  @Column(name="created_at", nullable=false) private OffsetDateTime createdAt = OffsetDateTime.now();
  @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt = OffsetDateTime.now();
  @PreUpdate void touch(){ this.updatedAt = OffsetDateTime.now(); }

  // getters/setters...
  public Long getId(){ return id; } public void setId(Long id){ this.id = id; }
  public School getSchool(){ return school; } public void setSchool(School school){ this.school = school; }
  public String getAcademicYear(){ return academicYear; } public void setAcademicYear(String academicYear){ this.academicYear = academicYear; }
  public Teacher getTeacher(){ return teacher; } public void setTeacher(Teacher teacher){ this.teacher = teacher; }
  public Subject getSubject(){ return subject; } public void setSubject(Subject subject){ this.subject = subject; }
  public Level getLevel(){ return level; } public void setLevel(Level level){ this.level = level; }
  public Section getSection(){ return section; } public void setSection(Section section){ this.section = section; }

  public boolean isPrivateGroup(){ return privateGroup; } public void setPrivateGroup(boolean v){ this.privateGroup = v; }
  public boolean isRevisionGroup(){ return revisionGroup; } public void setRevisionGroup(boolean v){ this.revisionGroup = v; }
  public boolean isActive(){ return active; } public void setActive(boolean v){ this.active = v; }
  public Integer getCapacity(){ return capacity; } public void setCapacity(Integer capacity){ this.capacity = capacity; }

  public BillingModel getBillingModel(){ return billingModel; } public void setBillingModel(BillingModel v){ this.billingModel = v; }
  public Integer getSessionsPerMonth(){ return sessionsPerMonth; } public void setSessionsPerMonth(Integer v){ this.sessionsPerMonth = v; }
  public BigDecimal getMonthlyFee(){ return monthlyFee; } public void setMonthlyFee(BigDecimal v){ this.monthlyFee = v; }
  public BigDecimal getSessionCost(){ return sessionCost; } public void setSessionCost(BigDecimal v){ this.sessionCost = v; }
  public BigDecimal getHourlyCost(){ return hourlyCost; } public void setHourlyCost(BigDecimal v){ this.hourlyCost = v; }
  public Integer getSessionDurationMin(){ return sessionDurationMin; } public void setSessionDurationMin(Integer v){ this.sessionDurationMin = v; }

  public TeacherShareType getTeacherShareType(){ return teacherShareType; } public void setTeacherShareType(TeacherShareType v){ this.teacherShareType = v; }
  public BigDecimal getTeacherShareValue(){ return teacherShareValue; } public void setTeacherShareValue(BigDecimal v){ this.teacherShareValue = v; }

  public boolean isAllowCheckInWithoutBalance(){ return allowCheckInWithoutBalance; }
  public void setAllowCheckInWithoutBalance(boolean v){ this.allowCheckInWithoutBalance = v; }
  public boolean isRequireFirstLessonAttendance(){ return requireFirstLessonAttendance; }
  public void setRequireFirstLessonAttendance(boolean v){ this.requireFirstLessonAttendance = v; }
  public boolean isRegisterFirstAbsence(){ return registerFirstAbsence; }
  public void setRegisterFirstAbsence(boolean v){ this.registerFirstAbsence = v; }
  public boolean isLastLessonReminder(){ return lastLessonReminder; }
  public void setLastLessonReminder(boolean v){ this.lastLessonReminder = v; }
  public Integer getAbsenceStopThreshold(){ return absenceStopThreshold; }
  public void setAbsenceStopThreshold(Integer v){ this.absenceStopThreshold = v; }
  public boolean isWarnDuplicateCard(){ return warnDuplicateCard; }
  public void setWarnDuplicateCard(boolean v){ this.warnDuplicateCard = v; }
  public boolean isAllowMultipleCheckinsPerDay(){ return allowMultipleCheckinsPerDay; }
  public void setAllowMultipleCheckinsPerDay(boolean v){ this.allowMultipleCheckinsPerDay = v; }
  
//--- day-4 additions (getters/setters) ---
public boolean isApprovalRequired() {
 return approvalRequired;
}
public void setApprovalRequired(boolean approvalRequired) {
 this.approvalRequired = approvalRequired;
}

public int getAutoCloseGraceMin() {
 return autoCloseGraceMin;
}
public void setAutoCloseGraceMin(int autoCloseGraceMin) {
 this.autoCloseGraceMin = autoCloseGraceMin;
}

//--- timestamps ---
public java.time.OffsetDateTime getCreatedAt() {
 return createdAt;
}
public void setCreatedAt(java.time.OffsetDateTime createdAt) {
 this.createdAt = createdAt;
}

public java.time.OffsetDateTime getUpdatedAt() {
 return updatedAt;
}
public void setUpdatedAt(java.time.OffsetDateTime updatedAt) {
 this.updatedAt = updatedAt;
}

//keep your @PreUpdate touch() and add @PrePersist so values are never null
@jakarta.persistence.PrePersist
void onCreate() {
 if (createdAt == null) createdAt = java.time.OffsetDateTime.now();
 if (updatedAt == null) updatedAt = createdAt;
}


  public LocalDate getStartDate(){ return startDate; } public void setStartDate(LocalDate v){ this.startDate = v; }
  public String getNotes(){ return notes; } public void setNotes(String v){ this.notes = v; }

  @Override public boolean equals(Object o){ return o instanceof StudyGroup g && id!=null && id.equals(g.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
