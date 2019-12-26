package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityRepository extends JpaRepository<Activity,Long> , JpaSpecificationExecutor<Activity> {
}
