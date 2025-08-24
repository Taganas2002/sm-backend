package com.example.demo.services.Interface;

import com.example.demo.dto.request.StudentUpsertRequest;
import com.example.demo.dto.response.StudentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudentService {
  StudentResponse create(StudentUpsertRequest req);
  StudentResponse update(Long id, StudentUpsertRequest req);
  StudentResponse get(Long id);
  Page<StudentResponse> list(Pageable pageable);
  void delete(Long id);
  StudentResponse findByCardUid(String cardUid);
}
