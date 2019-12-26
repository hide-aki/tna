package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TeamRepository extends JpaRepository<Team,Long>, JpaSpecificationExecutor<Team> {
}
