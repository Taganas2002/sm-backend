package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.ClassroomMapper;
import com.example.demo.dto.request.ClassroomUpsertRequest;
import com.example.demo.dto.response.ClassroomResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.Classroom;
import com.example.demo.repository.ClassroomRepo;
import com.example.demo.services.Interface.ClassroomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClassroomServiceImpl implements ClassroomService {

  private final ClassroomRepo repo;
  private final ClassroomMapper mapper;

  public ClassroomServiceImpl(ClassroomRepo repo, ClassroomMapper mapper) {
    this.repo = repo;
    this.mapper = mapper;
  }

  @Override
  public ClassroomResponse create(ClassroomUpsertRequest req) {
    Classroom e = mapper.toEntity(req);
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public ClassroomResponse update(Long id, ClassroomUpsertRequest req) {
    Classroom e = repo.findById(id)
        .orElseThrow(() -> new NotFoundException("Classroom not found: " + id));
    mapper.updateEntityFromUpsert(req, e);
    return mapper.toResponse(repo.save(e));
  }

  @Override
  public ClassroomResponse get(Long id) {
    return mapper.toResponse(
        repo.findById(id).orElseThrow(() -> new NotFoundException("Classroom not found: " + id))
    );
  }

  @Override
  public Page<ClassroomResponse> list(Pageable p) {
    return repo.findAll(p).map(mapper::toResponse);
  }

  @Override
  public void delete(Long id) {
    if (!repo.existsById(id)) throw new NotFoundException("Classroom not found: " + id);
    repo.deleteById(id);
  }
}
