package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.OActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OActivityRepository extends JpaRepository<OActivity,Long> , JpaSpecificationExecutor<OActivity> {
}
