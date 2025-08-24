package com.example.demo.dto.mapper;

import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.models.Subject;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
  Subject toEntity(SubjectUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void update(@MappingTarget Subject entity, SubjectUpsertRequest req);

  SubjectResponse toResponse(Subject entity);
}
