package com.example.demo.dto.mapper;

import com.example.demo.models.School;
import com.example.demo.dto.request.SchoolUpsertRequest;
import com.example.demo.dto.response.SchoolResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SchoolMapper {
  School toEntity(SchoolUpsertRequest req);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(SchoolUpsertRequest req, @MappingTarget School entity);
  SchoolResponse toResponse(School entity);
}
