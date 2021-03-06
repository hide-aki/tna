package com.jazasoft.tna.repository;

import com.jazasoft.tna.entity.OActivity;
import com.jazasoft.tna.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OActivityRepository extends JpaRepository<OActivity,Long> , JpaSpecificationExecutor<OActivity>, RevisionRepository<OActivity, Long, Integer> {

    Optional<OActivity> findOneByOrderAndId(Order order, Long id);
}
