package com.example.demo.controllers;

import com.example.demo.services.Interface.ClassroomService;
import com.example.demo.dto.request.ClassroomUpsertRequest;
import com.example.demo.dto.response.ClassroomResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {
  private final ClassroomService service;
  public ClassroomController(ClassroomService service){ this.service = service; }

  @GetMapping public Page<ClassroomResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public ClassroomResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public ClassroomResponse create(@Valid @RequestBody ClassroomUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public ClassroomResponse update(@PathVariable Long id, @Valid @RequestBody ClassroomUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
