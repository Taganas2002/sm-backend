package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.SubjectMapper;
import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.models.Subject;
import com.example.demo.repository.SubjectRepo;
import com.example.demo.services.Interface.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;

@Service
public class SubjectServiceImpl implements SubjectService {

  private final SubjectRepo repo;
  private final SubjectMapper mapper;

  public SubjectServiceImpl(SubjectRepo repo, SubjectMapper mapper){
    this.repo = repo; this.mapper = mapper;
  }

  @Override public SubjectResponse create(SubjectUpsertRequest req){
    Subject s = mapper.toEntity(req);
    return mapper.toResponse(repo.save(s));
  }

  @Override public SubjectResponse update(Long id, SubjectUpsertRequest req){
    Subject s = repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subject not found"));
    mapper.update(s, req);
    return mapper.toResponse(repo.save(s));
  }

  @Override public SubjectResponse get(Long id){
    return repo.findById(id).map(mapper::toResponse)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subject not found"));
  }

  @Override public Page<SubjectResponse> list(Pageable pageable){
    return repo.findAll(pageable).map(mapper::toResponse);
  }

  @Override public void delete(Long id){
    if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Subject not found");
    repo.deleteById(id);
  }
}
