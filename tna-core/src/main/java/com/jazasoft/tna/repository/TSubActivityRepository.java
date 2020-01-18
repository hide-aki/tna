package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.TSubActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TSubActivityRepository extends JpaRepository<TSubActivity,Long>, JpaSpecificationExecutor<TSubActivity> {
}
