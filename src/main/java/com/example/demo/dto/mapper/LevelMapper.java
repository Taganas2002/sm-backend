package com.example.demo.dto.mapper;

import com.example.demo.models.Level;
import com.example.demo.dto.request.LevelUpsertRequest;
import com.example.demo.dto.response.LevelResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LevelMapper {
  Level toEntity(LevelUpsertRequest req);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(LevelUpsertRequest req, @MappingTarget Level entity);
  LevelResponse toResponse(Level entity);
}
