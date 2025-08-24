package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.EnrollmentMapper;
import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.models.Enrollment;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.Student;
import com.example.demo.models.enums.EnrollmentStatus;
import com.example.demo.repository.EnrollmentRepo;
import com.example.demo.services.Interface.EnrollmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.EnumSet;

import static org.springframework.http.HttpStatus.*;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

  private final EnrollmentRepo repo;
  private final EnrollmentMapper mapper;
  @PersistenceContext private EntityManager em;

  public EnrollmentServiceImpl(EnrollmentRepo repo, EnrollmentMapper mapper){
    this.repo = repo; this.mapper = mapper;
  }

  @Override
  public EnrollmentResponse enroll(EnrollmentCreateRequest req) {
    Student student = mustFind(Student.class, req.getStudentId());
    StudyGroup group = mustFind(StudyGroup.class, req.getGroupId());
    if (!group.isActive()) throw new ResponseStatusException(BAD_REQUEST, "Group is inactive");

    // capacity rule
    if (group.getCapacity()!=null) {
      long count = repo.countByGroupIdAndStatus(group.getId(), EnrollmentStatus.ACTIVE);
      if (count >= group.getCapacity()) throw new ResponseStatusException(BAD_REQUEST, "Group capacity reached");
    }

    // unique (ACTIVE/SUSPENDED)
    if (repo.existsByStudentIdAndGroupIdAndStatusIn(student.getId(), group.getId(),
        EnumSet.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.SUSPENDED)))
      throw new ResponseStatusException(CONFLICT, "Student already enrolled in this group");

    Enrollment e = new Enrollment();
    e.setStudent(student);
    e.setGroup(group);
    e.setEnrollmentDate(req.getEnrollmentDate());
    if (req.getStatus()!=null) e.setStatus(req.getStatus());
    e.setNotes(req.getNotes());

    return mapper.toResponse(repo.save(e));
  }

  @Override
  public EnrollmentResponse get(Long id) {
    return repo.findById(id).map(mapper::toResponse)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Enrollment not found"));
  }

  @Override
  public Page<EnrollmentResponse> list(Long groupId, Long studentId, EnrollmentStatus status, Pageable pageable) {
    Specification<Enrollment> spec = Specification.where(null);
    if (groupId != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("group").get("id"), groupId));
    if (studentId != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("student").get("id"), studentId));
    if (status != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
    return repo.findAll(spec, pageable).map(mapper::toResponse);
  }

  @Override
  public void updateStatus(Long id, EnrollmentStatus status, String notes) {
    Enrollment e = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Enrollment not found"));
    e.setStatus(status);
    if (notes != null) e.setNotes(notes);
    repo.save(e);
  }

  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Enrollment not found");
    repo.deleteById(id);
  }

  private <T> T mustFind(Class<T> type, Long id){
    T x = em.find(type, id);
    if (x==null) throw new ResponseStatusException(NOT_FOUND, type.getSimpleName()+" not found: "+id);
    return x;
  }
}
