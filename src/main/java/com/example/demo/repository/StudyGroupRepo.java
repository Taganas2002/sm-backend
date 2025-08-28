// src/main/java/com/example/demo/repository/StudyGroupRepo.java
package com.example.demo.repository;

import com.example.demo.dto.response.GroupOption;
import com.example.demo.models.StudyGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface StudyGroupRepo extends JpaRepository<StudyGroup, Long>, JpaSpecificationExecutor<StudyGroup> {

  @Query("""
     select new com.example.demo.dto.response.GroupOption(g.id, g.name)
     from StudyGroup g
     where (:ay is null or g.academicYear = :ay)
       and (:active is null or g.active = :active)
       and (:q is null or lower(g.name) like lower(concat('%', :q, '%')))
     order by g.name asc
  """)
  Page<GroupOption> lookupOptions(@Param("ay") String academicYear,
                                  @Param("active") Boolean active,
                                  @Param("q") String q,
                                  Pageable pageable);
}
