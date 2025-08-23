package com.example.demo.dto.mapper;

import com.example.demo.models.Subject;
import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubjectMapper {
  Subject toEntity(SubjectUpsertRequest req);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(SubjectUpsertRequest req, @MappingTarget Subject entity);
  SubjectResponse toResponse(Subject entity);
}
