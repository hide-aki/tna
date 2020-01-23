package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Timeline;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface TimelineRepository extends JpaRepository<Timeline,Long>, JpaSpecificationExecutor<Timeline> {

    @Override
    @EntityGraph("timeline.findOne")
    Optional<Timeline> findById(Long id);
}
