package com.example.demo.dto.mapper;

import com.example.demo.dto.request.TeacherUpsertRequest;
import com.example.demo.dto.response.TeacherResponse;
import com.example.demo.models.Teacher;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "school", ignore = true) // set in service
  @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
  Teacher toEntity(TeacherUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "school", ignore = true) // handled in service
  @Mapping(target = "updatedAt", expression = "java(java.time.OffsetDateTime.now())")
  void updateEntityFromUpsert(TeacherUpsertRequest req, @MappingTarget Teacher entity);

  @Mapping(target = "schoolId", source = "school.id")
  TeacherResponse toResponse(Teacher entity);
}
