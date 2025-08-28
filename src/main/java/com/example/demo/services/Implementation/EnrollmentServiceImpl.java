package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.EnrollmentMapper;
import com.example.demo.dto.request.EnrollmentCreateRequest;
import com.example.demo.dto.response.EnrollmentResponse;
import com.example.demo.dto.response.EnrollmentStatusSummaryResponse;
import com.example.demo.models.Enrollment;
import com.example.demo.models.Student;
import com.example.demo.models.StudyGroup;
import com.example.demo.models.enums.EnrollmentStatus;
import com.example.demo.repository.EnrollmentRepo;
import com.example.demo.services.Interface.EnrollmentService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.EnumSet;

import static org.springframework.http.HttpStatus.*;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

  private final EnrollmentRepo repo;
  private final EnrollmentMapper mapper;

  @PersistenceContext
  private EntityManager em;

  public EnrollmentServiceImpl(EnrollmentRepo repo, EnrollmentMapper mapper){
    this.repo = repo; this.mapper = mapper;
  }

  @Transactional
  @Override
  public EnrollmentResponse enroll(EnrollmentCreateRequest req) {
    Student student = mustFind(Student.class, req.getStudentId());
    StudyGroup group = mustFind(StudyGroup.class, req.getGroupId());
    if (!group.isActive()) throw new ResponseStatusException(BAD_REQUEST, "Group is inactive");

    // capacity rule (ACTIVE only)
    if (group.getCapacity()!=null) {
      long activeCount = repo.countByGroupIdAndStatus(group.getId(), EnrollmentStatus.ACTIVE);
      if (activeCount >= group.getCapacity())
        throw new ResponseStatusException(BAD_REQUEST, "Group capacity reached");
    }

    // unique (ACTIVE/SUSPENDED)
    if (repo.existsByStudentIdAndGroupIdAndStatusIn(
        student.getId(), group.getId(),
        EnumSet.of(EnrollmentStatus.ACTIVE, EnrollmentStatus.SUSPENDED)))
      throw new ResponseStatusException(CONFLICT, "Student already enrolled in this group");

    Enrollment e = new Enrollment();
    e.setStudent(student);
    e.setGroup(group);
    e.setEnrollmentDate(req.getEnrollmentDate() != null ? req.getEnrollmentDate() : LocalDate.now());
    e.setStatus(req.getStatus() != null ? req.getStatus() : EnrollmentStatus.ACTIVE);
    e.setNotes(req.getNotes());

    return mapper.toResponse(repo.save(e));
  }

  @Transactional(readOnly = true)
  @Override
  public EnrollmentResponse get(Long id) {
    return repo.findById(id).map(mapper::toResponse)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Enrollment not found"));
  }

  @Transactional(readOnly = true)
  @Override
  public Page<EnrollmentResponse> list(Long groupId, Long studentId, EnrollmentStatus status, Pageable pageable) {
    Specification<Enrollment> spec = baseSpec(groupId, studentId);
    if (status != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
    }
    return repo.findAll(spec, pageable).map(mapper::toResponse);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<EnrollmentResponse> listByStatuses(Long groupId, Long studentId, EnumSet<EnrollmentStatus> statuses, Pageable pageable) {
    Specification<Enrollment> spec = baseSpec(groupId, studentId);
    if (statuses != null && !statuses.isEmpty()) {
      spec = spec.and((root, q, cb) -> root.get("status").in(statuses));
    }
    return repo.findAll(spec, pageable).map(mapper::toResponse);
  }

  @Transactional(readOnly = true)
  @Override
  public EnrollmentStatusSummaryResponse summaryByGroup(Long groupId) {
    StudyGroup group = mustFind(StudyGroup.class, groupId);

    long active    = repo.countByGroupIdAndStatus(groupId, EnrollmentStatus.ACTIVE);
    long suspended = repo.countByGroupIdAndStatus(groupId, EnrollmentStatus.SUSPENDED);
    long dropped   = repo.countByGroupIdAndStatus(groupId, EnrollmentStatus.DROPPED);
    long completed = repo.countByGroupIdAndStatus(groupId, EnrollmentStatus.COMPLETED);
    long total     = active + suspended + dropped + completed;

    Integer capacity = group.getCapacity();
    Integer capacityLeft = capacity == null ? null : Math.max(0, capacity - (int)active);

    EnrollmentStatusSummaryResponse r = new EnrollmentStatusSummaryResponse();
    r.setGroupId(groupId);
    r.setCapacity(capacity);
    r.setActive(active);
    r.setSuspended(suspended);
    r.setDropped(dropped);
    r.setCompleted(completed);
    r.setTotal(total);
    r.setCapacityLeft(capacityLeft);
    return r;
  }

  @Transactional
  @Override
  public void updateStatus(Long id, EnrollmentStatus status, String notes) {
    Enrollment e = repo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Enrollment not found"));
    e.setStatus(status);
    if (notes != null && !notes.isBlank()) e.setNotes(notes);
    repo.save(e);
  }

  @Transactional
  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Enrollment not found");
    repo.deleteById(id);
  }

  private Specification<Enrollment> baseSpec(Long groupId, Long studentId) {
    Specification<Enrollment> spec = Specification.where(null);
    if (groupId != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("group").get("id"), groupId));
    }
    if (studentId != null) {
      spec = spec.and((root, q, cb) -> cb.equal(root.get("student").get("id"), studentId));
    }
    return spec;
  }

  private <T> T mustFind(Class<T> type, Long id){
    T x = em.find(type, id);
    if (x==null) throw new ResponseStatusException(NOT_FOUND, type.getSimpleName()+" not found: "+id);
    return x;
  }
}
