package com.example.demo.controllers;

import com.example.demo.services.Interface.SchoolService;

import com.example.demo.dto.request.SchoolUpsertRequest;
import com.example.demo.dto.response.SchoolResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/schools")
public class SchoolController {
  private final SchoolService service;
  public SchoolController(SchoolService service){ this.service = service; }

  @GetMapping public Page<SchoolResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public SchoolResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public SchoolResponse create(@Valid @RequestBody SchoolUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public SchoolResponse update(@PathVariable Long id, @Valid @RequestBody SchoolUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
