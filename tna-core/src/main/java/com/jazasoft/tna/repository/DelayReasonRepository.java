package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.DelayReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DelayReasonRepository extends JpaRepository<DelayReason,Long>, JpaSpecificationExecutor<DelayReason> {
}
