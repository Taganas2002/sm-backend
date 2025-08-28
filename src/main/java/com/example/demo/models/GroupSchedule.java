package com.example.demo.models;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

@Entity
@Table(name = "group_schedule",
       indexes = {
         @Index(name="idx_sched_group_day", columnList = "group_id, day_of_week"),
         @Index(name="idx_sched_room_day_start", columnList = "classroom_id, day_of_week, start_time")
       })
public class GroupSchedule {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="group_id", nullable=false)
  private StudyGroup group;

  @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name="classroom_id", nullable=false)
  private Classroom classroom;

  @Enumerated(EnumType.STRING)
  @Column(name="day_of_week", nullable=false, length=10)
  private DayOfWeek dayOfWeek;

  @Column(name="start_time", nullable=false)
  private LocalTime startTime;

  @Column(name="end_time", nullable=false)
  private LocalTime endTime;

  @Column(name="active", nullable=false)
  private boolean active = true;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public StudyGroup getGroup() { return group; }
  public void setGroup(StudyGroup group) { this.group = group; }

  public Classroom getClassroom() { return classroom; }
  public void setClassroom(Classroom classroom) { this.classroom = classroom; }

  public DayOfWeek getDayOfWeek() { return dayOfWeek; }
  public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
  public LocalTime getStartTime() { return startTime; }
  public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
  public LocalTime getEndTime() { return endTime; }
  public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  @Override public boolean equals(Object o){ return o instanceof GroupSchedule s && id!=null && id.equals(s.id); }
  @Override public int hashCode(){ return Objects.hashCode(id); }
}
