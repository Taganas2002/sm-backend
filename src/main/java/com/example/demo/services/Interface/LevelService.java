package com.example.demo.services.Interface;

import com.example.demo.dto.request.LevelUpsertRequest;
import com.example.demo.dto.response.LevelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LevelService {
  LevelResponse create(LevelUpsertRequest req);
  LevelResponse update(Long id, LevelUpsertRequest req);
  LevelResponse get(Long id);
  Page<LevelResponse> list(Pageable pageable);
  void delete(Long id);
}
