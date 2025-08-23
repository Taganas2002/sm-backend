package com.example.demo.controllers;

import com.example.demo.services.Interface.SectionService;
import com.example.demo.dto.request.SectionUpsertRequest;
import com.example.demo.dto.response.SectionResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sections")
public class SectionController {
  private final SectionService service;
  public SectionController(SectionService service){ this.service=service; }

  @GetMapping public Page<SectionResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public SectionResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public SectionResponse create(@Valid @RequestBody SectionUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public SectionResponse update(@PathVariable Long id, @Valid @RequestBody SectionUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
