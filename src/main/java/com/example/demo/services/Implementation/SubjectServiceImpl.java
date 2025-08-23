package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.SubjectMapper;
import com.example.demo.dto.request.SubjectUpsertRequest;
import com.example.demo.dto.response.SubjectResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.Subject;
import com.example.demo.repository.SubjectRepo;
import com.example.demo.services.Interface.SubjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl implements SubjectService {
  private final SubjectRepo repo; private final SubjectMapper mapper;
  public SubjectServiceImpl(SubjectRepo repo, SubjectMapper mapper){ this.repo=repo; this.mapper=mapper; }

  @Override public SubjectResponse create(SubjectUpsertRequest req){ return mapper.toResponse(repo.save(mapper.toEntity(req))); }
  @Override public SubjectResponse update(Long id, SubjectUpsertRequest req){
    Subject e = repo.findById(id).orElseThrow(() -> new NotFoundException("Subject not found: " + id));
    mapper.updateEntityFromUpsert(req, e); return mapper.toResponse(repo.save(e));
  }
  @Override public SubjectResponse get(Long id){
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("Subject not found: " + id)));
  }
  @Override public Page<SubjectResponse> list(Pageable p){ return repo.findAll(p).map(mapper::toResponse); }
  @Override public void delete(Long id){
    if(!repo.existsById(id)) throw new NotFoundException("Subject not found: " + id);
    repo.deleteById(id);
  }
}
