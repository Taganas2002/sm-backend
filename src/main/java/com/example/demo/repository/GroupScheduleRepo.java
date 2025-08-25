package com.example.demo.repository;

import com.example.demo.models.GroupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;

public interface GroupScheduleRepo extends JpaRepository<GroupSchedule, Long> {
  List<GroupSchedule> findByGroupIdAndDayOfWeekAndActiveIsTrue(Long groupId, DayOfWeek dayOfWeek);
}
