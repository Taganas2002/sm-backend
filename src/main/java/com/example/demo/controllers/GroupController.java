package com.example.demo.controllers;

import com.example.demo.dto.request.GroupUpsertRequest;
import com.example.demo.dto.response.GroupResponse;
import com.example.demo.dto.response.GroupOption;
import com.example.demo.services.Interface.StudyGroupService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

  private final StudyGroupService service;

  public GroupController(StudyGroupService service){
    this.service = service;
  }

  // ---------- NEW: dropdown lookup ----------
  @GetMapping("/lookup")
  public List<GroupOption> lookup(@RequestParam(required = false) String academicYear,
                                  @RequestParam(required = false) Boolean active,
                                  @RequestParam(required = false) String q,
                                  @RequestParam(defaultValue = "50") int limit) {
    return service.lookup(academicYear, active, q, limit);
  }

  @PostMapping
  public GroupResponse create(@RequestBody @Valid GroupUpsertRequest req){
    return service.create(req);
  }

  @GetMapping
  public Page<GroupResponse> list(@RequestParam(required=false) String academicYear,
                                  @RequestParam(required=false) Boolean active,
                                  Pageable pageable){
    return service.list(academicYear, active, pageable);
  }

  @GetMapping("/{id}")
  public GroupResponse get(@PathVariable Long id){
    return service.get(id);
  }

  @PutMapping("/{id}")
  public GroupResponse update(@PathVariable Long id, @RequestBody @Valid GroupUpsertRequest req){
    return service.update(id, req);
  }
}
