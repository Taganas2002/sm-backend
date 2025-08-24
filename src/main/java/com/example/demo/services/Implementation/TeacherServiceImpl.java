package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.TeacherMapper;
import com.example.demo.dto.request.TeacherUpsertRequest;
import com.example.demo.dto.response.TeacherResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.School;
import com.example.demo.models.Teacher;
import com.example.demo.repository.SchoolRepo;
import com.example.demo.repository.TeacherRepo;
import com.example.demo.services.Interface.TeacherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TeacherServiceImpl implements TeacherService {

  private final TeacherRepo repo;
  private final SchoolRepo schoolRepo;
  private final TeacherMapper mapper;

  public TeacherServiceImpl(TeacherRepo repo, SchoolRepo schoolRepo, TeacherMapper mapper) {
    this.repo = repo;
    this.schoolRepo = schoolRepo;
    this.mapper = mapper;
  }

  @Override
  public TeacherResponse create(TeacherUpsertRequest req) {
    Teacher e = mapper.toEntity(req);
    if (req.getSchoolId() != null) {
      School s = schoolRepo.findById(req.getSchoolId())
          .orElseThrow(() -> new NotFoundException("School not found: " + req.getSchoolId()));
      e.setSchool(s);
    }
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public TeacherResponse update(Long id, TeacherUpsertRequest req) {
    Teacher e = repo.findById(id).orElseThrow(() -> new NotFoundException("Teacher not found: " + id));
    mapper.updateEntityFromUpsert(req, e);
    if (req.getSchoolId() != null) {
      School s = schoolRepo.findById(req.getSchoolId())
          .orElseThrow(() -> new NotFoundException("School not found: " + req.getSchoolId()));
      e.setSchool(s);
    }
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public TeacherResponse get(Long id) {
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("Teacher not found: " + id)));
  }

  @Override
  public Page<TeacherResponse> list(Pageable p) {
    return repo.findAll(p).map(mapper::toResponse);
  }

  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new NotFoundException("Teacher not found: " + id);
    repo.deleteById(id);
  }
}
