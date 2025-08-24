package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.StudentMapper;
import com.example.demo.dto.request.StudentUpsertRequest;
import com.example.demo.dto.response.StudentResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.*;
import com.example.demo.repository.*;
import com.example.demo.services.Interface.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

  private final StudentRepo repo;
  private final SchoolRepo schoolRepo;
  private final LevelRepo levelRepo;
  private final SectionRepo sectionRepo;
  private final StudentMapper mapper;

  public StudentServiceImpl(StudentRepo repo, SchoolRepo schoolRepo, LevelRepo levelRepo,
                            SectionRepo sectionRepo, StudentMapper mapper) {
    this.repo = repo;
    this.schoolRepo = schoolRepo;
    this.levelRepo = levelRepo;
    this.sectionRepo = sectionRepo;
    this.mapper = mapper;
  }

  @Override
  public StudentResponse create(StudentUpsertRequest req) {
    Student e = mapper.toEntity(req);
    // relations
    if (req.getSchoolId() != null) {
      e.setSchool(schoolRepo.findById(req.getSchoolId())
          .orElseThrow(() -> new NotFoundException("School not found: " + req.getSchoolId())));
    }
    if (req.getLevelId() != null) {
      e.setLevel(levelRepo.findById(req.getLevelId())
          .orElseThrow(() -> new NotFoundException("Level not found: " + req.getLevelId())));
    }
    if (req.getSectionId() != null) {
      e.setSection(sectionRepo.findById(req.getSectionId())
          .orElseThrow(() -> new NotFoundException("Section not found: " + req.getSectionId())));
    }
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public StudentResponse update(Long id, StudentUpsertRequest req) {
    Student e = repo.findById(id).orElseThrow(() -> new NotFoundException("Student not found: " + id));
    mapper.updateEntityFromUpsert(req, e);
    if (req.getSchoolId() != null) {
      e.setSchool(schoolRepo.findById(req.getSchoolId())
          .orElseThrow(() -> new NotFoundException("School not found: " + req.getSchoolId())));
    }
    if (req.getLevelId() != null) {
      e.setLevel(levelRepo.findById(req.getLevelId())
          .orElseThrow(() -> new NotFoundException("Level not found: " + req.getLevelId())));
    }
    if (req.getSectionId() != null) {
      e.setSection(sectionRepo.findById(req.getSectionId())
          .orElseThrow(() -> new NotFoundException("Section not found: " + req.getSectionId())));
    }
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public StudentResponse get(Long id) {
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("Student not found: " + id)));
  }

  @Override
  public Page<StudentResponse> list(Pageable p) {
    return repo.findAll(p).map(mapper::toResponse);
  }

  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new NotFoundException("Student not found: " + id);
    repo.deleteById(id);
  }

  @Override
  public StudentResponse findByCardUid(String cardUid) {
    return mapper.toResponse(
        repo.findByCardUid(cardUid).orElseThrow(() -> new NotFoundException("Student not found by card: " + cardUid))
    );
  }
}
