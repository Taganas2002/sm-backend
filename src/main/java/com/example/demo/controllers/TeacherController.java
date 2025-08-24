package com.example.demo.controllers;

import com.example.demo.services.Interface.TeacherService;
import com.example.demo.dto.request.TeacherUpsertRequest;
import com.example.demo.dto.response.TeacherResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {
  private final TeacherService service;
  public TeacherController(TeacherService service){ this.service = service; }

  @GetMapping public Page<TeacherResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public TeacherResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public TeacherResponse create(@Valid @RequestBody TeacherUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public TeacherResponse update(@PathVariable Long id, @Valid @RequestBody TeacherUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
