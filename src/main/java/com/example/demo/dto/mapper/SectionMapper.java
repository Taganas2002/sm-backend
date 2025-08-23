package com.example.demo.dto.mapper;

import com.example.demo.models.Section;
import com.example.demo.dto.request.SectionUpsertRequest;
import com.example.demo.dto.response.SectionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SectionMapper {
  Section toEntity(SectionUpsertRequest req);
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUpsert(SectionUpsertRequest req, @MappingTarget Section entity);
  SectionResponse toResponse(Section entity);
}
