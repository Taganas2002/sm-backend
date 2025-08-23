package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.SectionMapper;
import com.example.demo.dto.request.SectionUpsertRequest;
import com.example.demo.dto.response.SectionResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.Section;
import com.example.demo.repository.SectionRepo;
import com.example.demo.services.Interface.SectionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SectionServiceImpl implements SectionService {
  private final SectionRepo repo; private final SectionMapper mapper;
  public SectionServiceImpl(SectionRepo repo, SectionMapper mapper){ this.repo=repo; this.mapper=mapper; }

  @Override public SectionResponse create(SectionUpsertRequest req){ return mapper.toResponse(repo.save(mapper.toEntity(req))); }
  @Override public SectionResponse update(Long id, SectionUpsertRequest req){
    Section e = repo.findById(id).orElseThrow(() -> new NotFoundException("Section not found: " + id));
    mapper.updateEntityFromUpsert(req, e); return mapper.toResponse(repo.save(e));
  }
  @Override public SectionResponse get(Long id){
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("Section not found: " + id)));
  }
  @Override public Page<SectionResponse> list(Pageable p){ return repo.findAll(p).map(mapper::toResponse); }
  @Override public void delete(Long id){
    if(!repo.existsById(id)) throw new NotFoundException("Section not found: " + id);
    repo.deleteById(id);
  }
}
