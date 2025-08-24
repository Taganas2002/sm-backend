package com.example.demo.controllers;

import com.example.demo.services.Interface.StudentService;
import com.example.demo.dto.request.StudentUpsertRequest;
import com.example.demo.dto.response.StudentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {
  private final StudentService service;
  public StudentController(StudentService service){ this.service = service; }

  @GetMapping public Page<StudentResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public StudentResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public StudentResponse create(@Valid @RequestBody StudentUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }

  // Scan/lookup by card UID
  @GetMapping("/by-card/{cardUid}")
  public StudentResponse getByCard(@PathVariable String cardUid){ return service.findByCardUid(cardUid); }
}
