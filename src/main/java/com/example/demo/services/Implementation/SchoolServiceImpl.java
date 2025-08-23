package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.SchoolMapper;
import com.example.demo.dto.request.SchoolUpsertRequest;
import com.example.demo.dto.response.SchoolResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.School;
import com.example.demo.repository.SchoolRepo;
import com.example.demo.services.Interface.SchoolService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SchoolServiceImpl implements SchoolService {
  private final SchoolRepo repo; private final SchoolMapper mapper;
  public SchoolServiceImpl(SchoolRepo repo, SchoolMapper mapper){ this.repo=repo; this.mapper=mapper; }

  @Override public SchoolResponse create(SchoolUpsertRequest req){ return mapper.toResponse(repo.save(mapper.toEntity(req))); }
  @Override public SchoolResponse update(Long id, SchoolUpsertRequest req){
    School e = repo.findById(id).orElseThrow(() -> new NotFoundException("School not found: " + id));
    mapper.updateEntityFromUpsert(req, e); return mapper.toResponse(repo.save(e));
  }
  @Override public SchoolResponse get(Long id){
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("School not found: " + id)));
  }
  @Override public Page<SchoolResponse> list(Pageable p){ return repo.findAll(p).map(mapper::toResponse); }
  @Override public void delete(Long id){
    if(!repo.existsById(id)) throw new NotFoundException("School not found: " + id);
    repo.deleteById(id);
  }
}
