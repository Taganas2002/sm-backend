package com.example.demo.services.Interface;

import com.example.demo.dto.request.ClassroomUpsertRequest;
import com.example.demo.dto.response.ClassroomResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClassroomService {
  ClassroomResponse create(ClassroomUpsertRequest req);
  ClassroomResponse update(Long id, ClassroomUpsertRequest req);
  ClassroomResponse get(Long id);
  Page<ClassroomResponse> list(Pageable pageable);
  void delete(Long id);
}
