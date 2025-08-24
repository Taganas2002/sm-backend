package com.example.demo.services.Interface;

import com.example.demo.dto.request.GroupUpsertRequest;
import com.example.demo.dto.response.GroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StudyGroupService {
  GroupResponse create(GroupUpsertRequest req);
  GroupResponse update(Long id, GroupUpsertRequest req);
  GroupResponse get(Long id);
  Page<GroupResponse> list(String academicYear, Boolean active, Pageable pageable);
}
