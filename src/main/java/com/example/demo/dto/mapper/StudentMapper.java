package com.example.demo.dto.mapper;

import com.example.demo.dto.request.StudentUpsertRequest;
import com.example.demo.dto.response.StudentResponse;
import com.example.demo.models.Student;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudentMapper {

  Student toEntity(StudentUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(StudentUpsertRequest req, @MappingTarget Student entity);

  // Tell MapStruct we'll set these manually
  @Mapping(target = "schoolId",  ignore = true)
  @Mapping(target = "levelId",   ignore = true)
  @Mapping(target = "sectionId", ignore = true)
  StudentResponse toResponse(Student entity);

  @AfterMapping
  default void fillIds(Student entity, @MappingTarget StudentResponse dto) {
    if (entity == null) return;
    if (entity.getSchool()  != null) dto.setSchoolId(entity.getSchool().getId());
    if (entity.getLevel()   != null) dto.setLevelId(entity.getLevel().getId());
    if (entity.getSection() != null) dto.setSectionId(entity.getSection().getId());
  }
}
