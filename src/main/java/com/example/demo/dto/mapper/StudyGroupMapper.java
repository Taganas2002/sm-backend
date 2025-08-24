package com.example.demo.dto.mapper;

import com.example.demo.dto.request.GroupUpsertRequest;
import com.example.demo.dto.response.GroupResponse;
import com.example.demo.models.StudyGroup;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StudyGroupMapper {
  StudyGroup toEntity(GroupUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void update(@MappingTarget StudyGroup entity, GroupUpsertRequest req);

  @Mapping(target="schoolId", ignore = true)
  @Mapping(target="teacherId", ignore = true)
  @Mapping(target="subjectId", ignore = true)
  @Mapping(target="levelId", ignore = true)
  @Mapping(target="sectionId", ignore = true)
  GroupResponse toResponse(StudyGroup entity);

  @AfterMapping
  default void fillIds(StudyGroup entity, @MappingTarget GroupResponse dto) {
    if (entity == null) return;
    if (entity.getSchool()  != null) dto.setSchoolId(entity.getSchool().getId());
    if (entity.getTeacher() != null) dto.setTeacherId(entity.getTeacher().getId());
    if (entity.getSubject() != null) dto.setSubjectId(entity.getSubject().getId());
    if (entity.getLevel()   != null) dto.setLevelId(entity.getLevel().getId());
    if (entity.getSection() != null) dto.setSectionId(entity.getSection().getId());
  }
}
