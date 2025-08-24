package com.example.demo.services.Interface;

import com.example.demo.dto.request.TeacherUpsertRequest;
import com.example.demo.dto.response.TeacherResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TeacherService {
  TeacherResponse create(TeacherUpsertRequest req);
  TeacherResponse update(Long id, TeacherUpsertRequest req);
  TeacherResponse get(Long id);
  Page<TeacherResponse> list(Pageable pageable);
  void delete(Long id);
}
