package com.example.demo.dto.mapper;

import com.example.demo.models.Classroom;
import com.example.demo.dto.request.ClassroomUpsertRequest;
import com.example.demo.dto.response.ClassroomResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClassroomMapper {
  Classroom toEntity(ClassroomUpsertRequest req);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(ClassroomUpsertRequest req, @MappingTarget Classroom entity);

  ClassroomResponse toResponse(Classroom entity);
}
