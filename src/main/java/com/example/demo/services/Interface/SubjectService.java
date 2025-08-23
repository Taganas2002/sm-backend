package com.example.demo.services.Interface;

import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubjectService {
  SubjectResponse create(SubjectUpsertRequest req);
  SubjectResponse update(Long id, SubjectUpsertRequest req);
  SubjectResponse get(Long id);
  Page<SubjectResponse> list(Pageable pageable);
  void delete(Long id);
}
