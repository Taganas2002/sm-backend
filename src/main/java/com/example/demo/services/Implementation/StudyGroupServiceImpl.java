package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.StudyGroupMapper;
import com.example.demo.dto.request.GroupUpsertRequest;
import com.example.demo.dto.response.GroupResponse;
import com.example.demo.models.*;
import com.example.demo.models.enums.BillingModel;
import com.example.demo.repository.StudyGroupRepo;
import com.example.demo.repository.SubjectRepo;
import com.example.demo.services.Interface.StudyGroupService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
public class StudyGroupServiceImpl implements StudyGroupService {

  private final StudyGroupRepo repo;
  private final SubjectRepo subjectRepo;
  private final StudyGroupMapper mapper;

  @PersistenceContext private EntityManager em;

  public StudyGroupServiceImpl(StudyGroupRepo repo, SubjectRepo subjectRepo, StudyGroupMapper mapper) {
    this.repo = repo; this.subjectRepo = subjectRepo; this.mapper = mapper;
  }

  @Override
  public GroupResponse create(GroupUpsertRequest req) {
    validatePolicy(req);

    StudyGroup g = mapper.toEntity(req);

    if (req.getSchoolId()!=null) g.setSchool(load(School.class, req.getSchoolId()));
    g.setTeacher(load(Teacher.class, req.getTeacherId()));
    g.setSubject(subjectRepo.findById(req.getSubjectId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subject not found")));
    g.setLevel(load(Level.class, req.getLevelId()));
    g.setSection(load(Section.class, req.getSectionId()));

    return mapper.toResponse(repo.save(g));
  }

  @Override
  public GroupResponse update(Long id, GroupUpsertRequest req) {
    validatePolicy(req);
    StudyGroup g = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Group not found"));
    mapper.update(g, req);

    if (req.getSchoolId()!=null) g.setSchool(load(School.class, req.getSchoolId()));
    if (req.getTeacherId()!=null) g.setTeacher(load(Teacher.class, req.getTeacherId()));
    if (req.getSubjectId()!=null) g.setSubject(subjectRepo.findById(req.getSubjectId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subject not found")));
    if (req.getLevelId()!=null) g.setLevel(load(Level.class, req.getLevelId()));
    if (req.getSectionId()!=null) g.setSection(load(Section.class, req.getSectionId()));

    return mapper.toResponse(repo.save(g));
  }

  @Override
  public GroupResponse get(Long id) {
    return repo.findById(id).map(mapper::toResponse)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Group not found"));
  }

  @Override
  public Page<GroupResponse> list(String year, Boolean active, Pageable pageable) {
    Specification<StudyGroup> spec = Specification.where(null);
    if (year != null && !year.isBlank())
      spec = spec.and((root, q, cb) -> cb.equal(root.get("academicYear"), year));
    if (active != null)
      spec = spec.and((root, q, cb) -> cb.equal(root.get("active"), active));
    return repo.findAll(spec, pageable).map(mapper::toResponse);
  }

  private void validatePolicy(GroupUpsertRequest r){
    if (r.getBillingModel() == BillingModel.MONTHLY) {
      if (r.getMonthlyFee()==null && r.getSessionsPerMonth()==null)
        throw new ResponseStatusException(BAD_REQUEST, "MONTHLY requires monthlyFee or sessionsPerMonth");
    }
    if (r.getBillingModel() == BillingModel.PER_SESSION && r.getSessionCost()==null)
      throw new ResponseStatusException(BAD_REQUEST, "PER_SESSION requires sessionCost");
    if (r.getBillingModel() == BillingModel.PER_HOUR && (r.getHourlyCost()==null || r.getSessionDurationMin()==null))
      throw new ResponseStatusException(BAD_REQUEST, "PER_HOUR requires hourlyCost and sessionDurationMin");
  }

  private <T> T load(Class<T> clazz, Long id){
    T ref = em.find(clazz, id);
    if (ref == null) throw new ResponseStatusException(NOT_FOUND, clazz.getSimpleName()+" not found: "+id);
    return ref;
  }
}
