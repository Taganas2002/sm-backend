package com.example.demo.repository;

import com.example.demo.models.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface GroupScheduleRepo extends JpaRepository<GroupSchedule, Long> {

  List<GroupSchedule> findByGroupIdAndDayOfWeekAndActiveIsTrue(Long groupId, DayOfWeek dayOfWeek);
  List<GroupSchedule> findByGroupIdAndActiveIsTrue(Long groupId);
  List<GroupSchedule> findByGroupId(Long groupId);

  List<GroupSchedule> findByClassroomId(Long classroomId);

  

  // group-specific, only active, with refs
  @Query("""
     select s from GroupSchedule s
     join fetch s.group g
     left join fetch s.classroom c
     where g.id = :groupId and s.active = true
  """)
  List<GroupSchedule> findActiveByGroupIdWithRefs(@Param("groupId") Long groupId);

  // all active schedules across active groups, with refs (for week view)
  @Query("""
     select s from GroupSchedule s
     join fetch s.group g
     left join fetch s.classroom c
     where s.active = true and g.active = true
  """)
  List<GroupSchedule> findAllActiveWithRefs();

  // room conflict checker (unchanged logic but explicit)
  @Query("""
     select s from GroupSchedule s
     join s.classroom c
     join s.group g
     where c.id = :classroomId
       and s.dayOfWeek = :day
       and s.active = true
       and (s.startTime < :end and s.endTime > :start)
       and (:excludeId is null or s.id <> :excludeId)
  """)
  List<GroupSchedule> findConflicts(
      @Param("classroomId") Long classroomId,
      @Param("day") DayOfWeek day,
      @Param("start") LocalTime start,
      @Param("end") LocalTime end,
      @Param("excludeId") Long excludeId
  );
}
