package com.example.demo.dto.mapper;

import com.example.demo.models.Teacher;
import com.example.demo.dto.request.TeacherUpsertRequest;
import com.example.demo.dto.response.TeacherResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeacherMapper {
  Teacher toEntity(TeacherUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(TeacherUpsertRequest req, @MappingTarget Teacher entity);

  @Mapping(target = "schoolId", source = "school.id")
  TeacherResponse toResponse(Teacher entity);
}
