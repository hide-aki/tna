package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.OSubActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OSubActivityRepository extends JpaRepository<OSubActivity,Long>, JpaSpecificationExecutor<OSubActivity> {
}
