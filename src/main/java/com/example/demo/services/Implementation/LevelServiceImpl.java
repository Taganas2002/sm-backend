package com.example.demo.services.Implementation;

import com.example.demo.dto.mapper.LevelMapper;
import com.example.demo.dto.request.LevelUpsertRequest;
import com.example.demo.dto.response.LevelResponse;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.Level;
import com.example.demo.repository.LevelRepo;
import com.example.demo.services.Interface.LevelService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LevelServiceImpl implements LevelService {
  private final LevelRepo repo; private final LevelMapper mapper;
  public LevelServiceImpl(LevelRepo repo, LevelMapper mapper){ this.repo=repo; this.mapper=mapper; }

  @Override public LevelResponse create(LevelUpsertRequest req){ return mapper.toResponse(repo.save(mapper.toEntity(req))); }
  @Override public LevelResponse update(Long id, LevelUpsertRequest req){
    Level e = repo.findById(id).orElseThrow(() -> new NotFoundException("Level not found: " + id));
    mapper.updateEntityFromUpsert(req, e); return mapper.toResponse(repo.save(e));
  }
  @Override public LevelResponse get(Long id){
    return mapper.toResponse(repo.findById(id).orElseThrow(() -> new NotFoundException("Level not found: " + id)));
  }
  @Override public Page<LevelResponse> list(Pageable p){ return repo.findAll(p).map(mapper::toResponse); }
  @Override public void delete(Long id){
    if(!repo.existsById(id)) throw new NotFoundException("Level not found: " + id);
    repo.deleteById(id);
  }
}
