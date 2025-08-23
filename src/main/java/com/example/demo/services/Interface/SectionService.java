package com.example.demo.services.Interface;

import com.example.demo.dto.request.SectionUpsertRequest;
import com.example.demo.dto.response.SectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SectionService {
  SectionResponse create(SectionUpsertRequest req);
  SectionResponse update(Long id, SectionUpsertRequest req);
  SectionResponse get(Long id);
  Page<SectionResponse> list(Pageable pageable);
  void delete(Long id);
}
