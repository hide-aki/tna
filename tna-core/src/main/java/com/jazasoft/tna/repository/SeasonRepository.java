package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SeasonRepository extends JpaRepository<Season, Long>, JpaSpecificationExecutor<Season> {
}
