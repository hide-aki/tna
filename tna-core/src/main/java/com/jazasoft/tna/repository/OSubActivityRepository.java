package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.OSubActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OSubActivityRepository extends JpaRepository<OSubActivity,Long>, JpaSpecificationExecutor<OSubActivity> {

    Optional<OSubActivity> findOneByOActivityAndId(OActivity oActivity, Long id);
}
