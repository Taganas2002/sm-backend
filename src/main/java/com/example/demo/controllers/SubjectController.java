package com.example.demo.controllers;

import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.services.Interface.SubjectService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

  private final SubjectService service;
  public SubjectController(SubjectService service){ this.service = service; }

  @PostMapping public SubjectResponse create(@RequestBody @Valid SubjectUpsertRequest req){ return service.create(req); }
  @GetMapping  public Page<SubjectResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public SubjectResponse get(@PathVariable Long id){ return service.get(id); }
  @PutMapping("/{id}") public SubjectResponse update(@PathVariable Long id, @RequestBody @Valid SubjectUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
