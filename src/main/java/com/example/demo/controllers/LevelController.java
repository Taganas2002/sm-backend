package com.example.demo.controllers;

import com.example.demo.services.Interface.LevelService;
import com.example.demo.dto.request.LevelUpsertRequest;
import com.example.demo.dto.response.LevelResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/levels")
public class LevelController {
  private final LevelService service;
  public LevelController(LevelService service){ this.service=service; }

  @GetMapping public Page<LevelResponse> list(Pageable p){ return service.list(p); }
  @GetMapping("/{id}") public LevelResponse get(@PathVariable Long id){ return service.get(id); }
  @PostMapping public LevelResponse create(@Valid @RequestBody LevelUpsertRequest req){ return service.create(req); }
  @PutMapping("/{id}") public LevelResponse update(@PathVariable Long id, @Valid @RequestBody LevelUpsertRequest req){ return service.update(id, req); }
  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
