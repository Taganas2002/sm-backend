package com.example.demo.services.Interface;

import com.example.demo.dto.request.SchoolUpsertRequest;
import com.example.demo.dto.response.SchoolResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchoolService {
  SchoolResponse create(SchoolUpsertRequest req);
  SchoolResponse update(Long id, SchoolUpsertRequest req);
  SchoolResponse get(Long id);
  Page<SchoolResponse> list(Pageable pageable);
  void delete(Long id);
}
