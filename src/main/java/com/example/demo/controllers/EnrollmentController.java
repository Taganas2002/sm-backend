package com.example.demo.controllers;

import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.models.enums.EnrollmentStatus;
import com.example.demo.services.Interface.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

  private final EnrollmentService service;
  public EnrollmentController(EnrollmentService service){ this.service = service; }

  @PostMapping public EnrollmentResponse enroll(@RequestBody @Valid EnrollmentCreateRequest req){ return service.enroll(req); }
  @GetMapping  public Page<EnrollmentResponse> list(@RequestParam(required=false) Long groupId,
                                                   @RequestParam(required=false) Long studentId,
                                                   @RequestParam(required=false) EnrollmentStatus status,
                                                   Pageable pageable){
    return service.list(groupId, studentId, status, pageable);
  }
  @GetMapping("/{id}") public EnrollmentResponse get(@PathVariable Long id){ return service.get(id); }

  @PatchMapping("/{id}/status")
  public void updateStatus(@PathVariable Long id,
                           @RequestParam EnrollmentStatus status,
                           @RequestParam(required=false) String notes){
    service.updateStatus(id, status, notes);
  }

  @DeleteMapping("/{id}") public void delete(@PathVariable Long id){ service.delete(id); }
}
