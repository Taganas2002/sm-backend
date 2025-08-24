package com.example.demo.dto.mapper;

import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.models.Enrollment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
  @Mapping(target="studentId", ignore = true)
  @Mapping(target="groupId", ignore = true)
  EnrollmentResponse toResponse(Enrollment e);

  @AfterMapping
  default void fillIds(Enrollment e, @MappingTarget EnrollmentResponse dto){
    if (e.getStudent()!=null) dto.setStudentId(e.getStudent().getId());
    if (e.getGroup()!=null)   dto.setGroupId(e.getGroup().getId());
  }
}
