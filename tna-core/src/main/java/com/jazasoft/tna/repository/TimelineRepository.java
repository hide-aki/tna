package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TimelineRepository extends JpaRepository<Timeline,Long>, JpaSpecificationExecutor<Timeline> {
}
